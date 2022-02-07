package me.prisonranksx.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.util.concurrent.AtomicDouble;

import co.aikar.taskchain.TaskChain;
import io.samdev.actionutil.ActionUtil;
import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.IPrestigeDataHandler;
import me.prisonranksx.data.InfinitePrestigeSettings;
import me.prisonranksx.data.PrestigeRandomCommands;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.events.AsyncPrestigeMaxEvent;
import me.prisonranksx.events.PrePrestigeMaxEvent;
import me.prisonranksx.events.RankUpdateCause;
import me.prisonranksx.events.RankUpdateEvent;
import me.prisonranksx.utils.AccessibleBukkitTask;
import me.prisonranksx.utils.AccessibleString;
import me.prisonranksx.utils.CompatibleSound.Sounds;
import me.prisonranksx.utils.TimeCounter;

public class PrestigeMax implements IPrestigeMax {

	private PrisonRanksX plugin;
	private PRXAPI api;
	private Set<String> processingPlayers;
	private final List<String> lastPrestigeMessage;
	private final List<String> notEnoughMoneyMessage;
	private String finalInfinitePrestige;
	private final String noPrestigeMessage;
	private final ConcurrentHashMultiset<String> multiThreadSet;
	public int prestigesPerTick = 5;
	public int threadTimer = 1;
	private final Map<String, Map<String, Double>> chancesCache;
	
	public PrestigeMax(PrisonRanksX plugin) {
		this.plugin = plugin;
		this.api = this.plugin.prxAPI;
		this.processingPlayers = new HashSet<>();
		this.lastPrestigeMessage = getAPI().cl(getAPI().h("lastprestige"));
		this.notEnoughMoneyMessage = getAPI().cl(getAPI().h("prestige-notenoughmoney"));
		this.noPrestigeMessage = getAPI().g("noprestige");
		this.multiThreadSet = ConcurrentHashMultiset.create();
		this.chancesCache = new HashMap<>();
		this.prestigesPerTick = 5;
		this.threadTimer = 1;
		this.finalInfinitePrestige = this.plugin.isInfinitePrestige ? String.valueOf(plugin.infinitePrestigeSettings.getFinalPrestige()) : "P2";
	}
	
	@Override
	public void execute(Player player) {
		Player p = player;
		String name = p.getName();
		UUID uuid = p.getUniqueId();
		if(isProcessing(name)) {
			p.sendMessage(getAPI().g("prestigemax-is-on"));
			return;
		}
		RankPath rankPath = getAPI().getPlayerRankPath(uuid);
		if(!getAPI().isLastRank(rankPath) && !getAPI().hasAllowPrestige(rankPath)) {
			if(getAPI().canRankup(p)) {
				getAPI().rankupMax(p);
			} else {
				//p.sendMessage(noPrestigeMessage);
			}
			return;
		}
		AtomicDouble takenBalance = new AtomicDouble(0.0);
		AtomicInteger prestigeTimes = new AtomicInteger(0);
		String prestigeName = getAPI().getPlayerPrestige(uuid);
		if(prestigeName == null) {
			if(getAPI().canPrestige(p)) {
			getAPI().getPrestigeAPI().prestige(p);
			prestigeName = getAPI().getFirstPrestige();
			} else {
				getAPI().getPrestigeAPI().prestige(p);
				getProcessingPlayers().remove(name);
				return;
			}
		}
		IPrestigeDataHandler prestige = getAPI().getPrestige(prestigeName);
		PrePrestigeMaxEvent e = new PrePrestigeMaxEvent(p, prestige);
		Bukkit.getPluginManager().callEvent(e);
		if(e.isCancelled()) {
			getProcessingPlayers().remove(name);
			return;
		}
		AccessibleString prestigeFrom = new AccessibleString(prestigeName);
		AccessibleString finalPrestige = new AccessibleString();
		String nextPrestigeName = prestige.getNextPrestigeName();
		double playerBalance = getAPI().getPlayerMoney(p);
		if(nextPrestigeName.equals("LASTPRESTIGE")) {
			getProcessingPlayers().remove(name);
			lastPrestigeMessage.forEach(p::sendMessage);
			return;
		}
        IPrestigeDataHandler nextPrestige = getAPI().getPrestige(nextPrestigeName);
		List<String> prestigesCollection = getAPI().getPrestigeStorage().getNativeLinkedPrestigesCollection();
		Map<String, String> stringRequirements = nextPrestige.getStringRequirements();
		Map<String, Double> numberRequirements = nextPrestige.getNumberRequirements();
		List<String> customRequirementMessage = nextPrestige.getCustomRequirementMessage();
		String rebirthName = getAPI().getPlayerRebirth(uuid);
		String nextPrestigeDisplay = nextPrestige.getDisplayName();
		double nextPrestigeCost = getAPI().getIncreasedPrestigeCost(rebirthName, nextPrestigeName);
		String nextPrestigeCostFormatted = getAPI().formatBalance(nextPrestigeCost);
		String prestigeMessage = plugin.getString(getAPI().g("prestige"), name);
        if(nextPrestigeCost > playerBalance) {
        	notEnoughMoneyMessage.forEach(messageLine -> {
        	    messageLine = plugin.getString(messageLine, name)
        		.replace("%player%", name)
        		.replace("%nextprestige%", nextPrestigeName)
        		.replace("%nextprestige_display%", nextPrestigeDisplay)
        		.replace("%nextprestige_cost%", String.valueOf(nextPrestigeCost))
        		.replace("%nextprestige_cost_formatted%", nextPrestigeCostFormatted);
        	    p.sendMessage(messageLine);
        	});
        	return;
        }
        if(!checkRequirements(stringRequirements, numberRequirements, customRequirementMessage, p, false))
        	return;
        getProcessingPlayers().add(name);
        int currentPrestigeIndex = prestigesCollection.indexOf(prestigeName);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
        	for(int i = currentPrestigeIndex ; i < prestigesCollection.size(); i++) {
        		RankPath loopRankPath = getAPI().getPlayerRankPath(uuid);
           		if(!getAPI().isLastRank(loopRankPath) && !getAPI().hasAllowPrestige(loopRankPath)) {
        			if(getAPI().canRankup(p)) {
        				getAPI().rankupMax(p);
        			}
        			return;
        		}
        		String loopPrestigeName = prestigesCollection.get(i);
        		double loopBalance = getAPI().getPlayerMoney(p);
        		
        		IPrestigeDataHandler loopPrestige = getAPI().getPrestige(loopPrestigeName);
        		String loopNextPrestigeName = loopPrestige.getNextPrestigeName();
        		if(loopNextPrestigeName.equals("LASTPRESTIGE")) {
        			lastPrestigeMessage.forEach(p::sendMessage);
        			break;
        		}
        		IPrestigeDataHandler loopNextPrestige = getAPI().getPrestige(loopNextPrestigeName);
        		double loopNextPrestigeCost = getAPI().getIncreasedPrestigeCost(rebirthName, loopNextPrestigeName);
        		takenBalance.addAndGet(loopNextPrestigeCost);
        		String loopNextPrestigeCostFormatted = getAPI().formatBalance(loopNextPrestigeCost);
        		Map<String, String> loopStringRequirements = loopNextPrestige.getStringRequirements();
        		Map<String, Double> loopNumberRequirements = loopNextPrestige.getNumberRequirements();
        		List<String> loopCustomRequirementMessage = loopNextPrestige.getCustomRequirementMessage();
        		String loopNextPrestigeDisplay = getAPI().c(loopNextPrestige.getDisplayName());
        		if(loopNextPrestigeCost > loopBalance) {
                	notEnoughMoneyMessage.forEach(messageLine -> {
                		p.sendMessage(plugin.getString(messageLine, name)
                		.replace("%player%", name)
                		.replace("%nextprestige%", loopNextPrestigeName)
                		.replace("%nextprestige_display%", loopNextPrestigeDisplay)
                		.replace("%nextprestige_cost%", String.valueOf(loopNextPrestigeCost))
                		.replace("%nextprestige_cost_formatted%", loopNextPrestigeCostFormatted));
                	});
                	break;
        		}
        		if(!checkRequirements(loopStringRequirements, loopNumberRequirements, loopCustomRequirementMessage, p, false))
        			break;
        		p.sendMessage(prestigeMessage
        				.replace("%nextprestige%", loopNextPrestigeName)
        				.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        				);
        		List<String> loopNextPrestigeCommands = loopNextPrestige.getPrestigeCommands();
        		if(loopNextPrestigeCommands != null && !loopNextPrestigeCommands.isEmpty()) {
        			plugin.getServer().getScheduler().runTask(plugin, () -> {
        				plugin.executeCommands(p, loopNextPrestigeCommands);
        			});
        		}
        		List<String> loopNextPrestigeAddPermissionList = loopNextPrestige.getAddPermissionList();
        		if(loopNextPrestigeAddPermissionList != null && !loopNextPrestigeAddPermissionList.isEmpty()) {
        			loopNextPrestigeAddPermissionList.forEach(permission -> {
        				getAPI().getPermissionManager().addPermission(p, permission
        						.replace("%player%", name)
        						.replace("%prestige%", loopPrestigeName)
        						.replace("%nextprestige%", loopNextPrestigeName)
        						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        						);
        			});
        		}
        		List<String> loopNextPrestigeDelPermissionList = loopNextPrestige.getDelPermissionList();
        		if(loopNextPrestigeDelPermissionList != null && !loopNextPrestigeDelPermissionList.isEmpty()) {
        			loopNextPrestigeDelPermissionList.forEach(permission -> {
        				getAPI().getPermissionManager().delPermission(p, permission
        						.replace("%player%", name)
        						.replace("%prestige%", loopPrestigeName)
        						.replace("%nextprestige%", loopNextPrestigeName)
        						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        						);
        			});
        		}
        		List<String> loopNextPrestigeBroadcast = loopNextPrestige.getBroadcast();
        		if(loopNextPrestigeBroadcast != null && !loopNextPrestigeBroadcast.isEmpty()) {
        			loopNextPrestigeBroadcast.forEach(broadcastMessage -> {
        				Bukkit.broadcastMessage(plugin.getString(broadcastMessage, name)
        				.replace("%player%", name)
						.replace("%prestige%", loopPrestigeName)
						.replace("%nextprestige%", loopNextPrestigeName)
						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
						);
        			});
        		}
        		List<String> loopNextPrestigeMessage = loopNextPrestige.getMsg();
        		if(loopNextPrestigeMessage != null && !loopNextPrestigeMessage.isEmpty()) {
        			loopNextPrestigeMessage.forEach(messageLine -> {
        				p.sendMessage(plugin.getString(messageLine, name)
        						.replace("%player%", name)
        						.replace("%prestige%", loopPrestigeName)
        						.replace("%nextprestige%", loopNextPrestigeName)
        						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        						);
        			});
        		}
        		List<String> loopNextPrestigeActions = loopNextPrestige.getActions();
        		if(getAPI().hasActionUtilEnabled() && loopNextPrestigeActions != null && !loopNextPrestigeActions.isEmpty()) {
        			ActionUtil.executeActions(p, loopNextPrestigeActions);
        		}
        		Map<String, Double> chances = new HashMap<>();
        		PrestigeRandomCommands loopPrestigeRandomCommands = loopNextPrestige.getRandomCommandsManager();
       			if(loopPrestigeRandomCommands != null && loopPrestigeRandomCommands.getRandomCommandsMap() != null) {
       			  for(String section : loopPrestigeRandomCommands.getRandomCommandsMap().keySet()) {
       				  Double chance = loopPrestigeRandomCommands.getChance(section);
       				  chances.put(section, chance);
       			  }
       			  String randomSection = getAPI().getNumberAPI().getChanceFromWeightedMap(chances);
       			  if(loopPrestigeRandomCommands.getCommands(randomSection) != null) {
       			    List<String> commands = loopPrestigeRandomCommands.getCommands(randomSection);
       			    ConsoleCommandSender ccs = Bukkit.getConsoleSender();
       			    for(String singleCommand : commands) {
       				    String replacedCommand = getAPI().cp(singleCommand.replace("%player%", name).replace("%nextprestige%", loopNextPrestigeName), p);
       				    plugin.getServer().getScheduler().runTask(plugin, () -> {
       				      plugin.getServer().dispatchCommand(ccs, replacedCommand);
       				    });
       			    }
       			  }
       			}
       			getAPI().getEconomy().withdrawPlayer(p, loopNextPrestigeCost);
       			finalPrestige.setString(loopNextPrestigeName);
       			getAPI().setPlayerPrestige(uuid, loopNextPrestigeName);
       			prestigeTimes.addAndGet(1);
        	}
        	if(!AccessibleString.isNullOrEmpty(finalPrestige)) {
        		IPrestigeDataHandler finalData = getAPI().getPrestige(finalPrestige.getString());
        		List<String> actionbarMessages = finalData.getActionbarMessages();
        		int actionbarInterval = finalData.getActionbarInterval();
        		String finalPrestigeName = finalData.getName();
        		String finalPrestigeDisplay = getAPI().c(finalData.getDisplayName());
        		if(actionbarMessages != null && !actionbarMessages.isEmpty()) {
        			List<String> replaced = new ArrayList<>();
        			actionbarMessages.forEach(messageLine -> {
        				replaced.add(messageLine.replace("%nextprestige%", finalPrestigeName).replace("%nextprestige_display%", finalPrestigeDisplay));
        			});
        			plugin.animateActionbar(p, actionbarInterval, replaced);
        		}
        		AsyncPrestigeMaxEvent event = new AsyncPrestigeMaxEvent(p, prestigeFrom.getString(), finalPrestigeName, prestigeTimes.get(), takenBalance.get());
        		Bukkit.getPluginManager().callEvent(event);
        	}
        	getProcessingPlayers().remove(name);
        });
 	}

	@Override
	public void executeInfinite(Player player) {
		Player p = player;
		String name = p.getName();
		UUID uuid = p.getUniqueId();
		if(isProcessing(name)) {
			p.sendMessage(getAPI().g("prestigemax-is-on"));
			return;
		}	
		RankPath rankPath = getAPI().getPlayerRankPath(uuid);
		plugin.newSharedChain("maxprocess#" + name).async(() -> {
			if(!getAPI().isLastRank(rankPath) && !getAPI().hasAllowPrestige(rankPath)) {
				getAPI().rankupMax(p);
			}
		}).execute();
		plugin.newSharedChain("maxprocess#" + name).abortIf(!player.isOnline()).async(() -> {
		AtomicDouble takenBalance = new AtomicDouble(0.0);
		AtomicInteger prestigeTimes = new AtomicInteger(0);
		String prestigeName = getAPI().getPlayerPrestige(uuid);
		if(prestigeName == null) {
			if(getAPI().canPrestige(p)) {
				getAPI().getPrestigeAPI().prestige(p);
				prestigeName = getAPI().getFirstPrestige();
			} else {
				getAPI().getPrestigeAPI().prestige(p);
				getProcessingPlayers().remove(name);
				return;
			}
		}
		IPrestigeDataHandler prestige = getAPI().getPrestige(prestigeName);
		PrePrestigeMaxEvent e = new PrePrestigeMaxEvent(p, prestige);
		plugin.scheduler.runTask(plugin, () -> Bukkit.getPluginManager().callEvent(e));
		if(e.isCancelled()) {
			getProcessingPlayers().remove(name);
			return;
		}
		AccessibleString prestigeFrom = new AccessibleString(prestigeName);
		AccessibleString finalPrestige = new AccessibleString();
		String nextPrestigeName = prestige.getNextPrestigeName();
		double playerBalance = getAPI().getPlayerMoney(p);
		final String finalInfinitePrestige = String.valueOf(plugin.infinitePrestigeSettings.getFinalPrestige()+1);
		if(nextPrestigeName.equals(finalInfinitePrestige)) {
			getProcessingPlayers().remove(name);
			lastPrestigeMessage.forEach(p::sendMessage);
			return;
		}
        IPrestigeDataHandler nextPrestige = getAPI().getPrestige(nextPrestigeName);
		String rebirthName = getAPI().getPlayerRebirth(uuid);
		String nextPrestigeDisplay = nextPrestige.getDisplayName();
		double nextPrestigeCost = getAPI().getIncreasedPrestigeCost(rebirthName, nextPrestigeName);
		String nextPrestigeCostFormatted = getAPI().formatBalance(nextPrestigeCost);
		String prestigeMessage = plugin.getString(getAPI().g("prestige"), name);
        if(nextPrestigeCost > playerBalance) {
        	notEnoughMoneyMessage.forEach(messageLine -> {
        	    messageLine = plugin.getString(messageLine, name)
        		.replace("%player%", name)
        		.replace("%nextprestige%", nextPrestigeName)
        		.replace("%nextprestige_display%", nextPrestigeDisplay)
        		.replace("%nextprestige_cost%", String.valueOf(nextPrestigeCost))
        		.replace("%nextprestige_cost_formatted%", nextPrestigeCostFormatted);
        	    p.sendMessage(messageLine);
        	});
        	return;
        }
        getProcessingPlayers().add(name);
        int currentPrestigeIndex = Integer.valueOf(prestigeName);
        int size = (int)plugin.infinitePrestigeSettings.getFinalPrestige();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
        	for(int i = currentPrestigeIndex ; i < size; i++) {
        		RankPath loopRankPath = getAPI().getPlayerRankPath(uuid);
           		if(!getAPI().isLastRank(loopRankPath) && !getAPI().hasAllowPrestige(loopRankPath)) {
        			if(getAPI().canRankup(p)) {
        				getAPI().rankupMax(p);
        			}
        			return;
        		}
           		if(!plugin.getPlayerStorage().getPlayerData().containsKey(uuid.toString())) {
        			getProcessingPlayers().remove(name);
        			break;
        		}
        		String loopPrestigeName = String.valueOf(i);
        		double loopBalance = getAPI().getPlayerMoney(p);
        		
        		IPrestigeDataHandler loopPrestige = getAPI().getPrestige(loopPrestigeName);
        		String loopNextPrestigeName = loopPrestige.getNextPrestigeName();
        		if(loopNextPrestigeName.equals(finalInfinitePrestige)) {
        			lastPrestigeMessage.forEach(p::sendMessage);
        			break;
        		}
        		IPrestigeDataHandler loopNextPrestige = getAPI().getPrestige(loopNextPrestigeName);
        		double loopNextPrestigeCost = getAPI().getIncreasedPrestigeCost(rebirthName, loopNextPrestigeName);
        		takenBalance.addAndGet(loopNextPrestigeCost);
        		String loopNextPrestigeCostFormatted = getAPI().formatBalance(loopNextPrestigeCost);
        		String loopNextPrestigeDisplay = getAPI().c(loopNextPrestige.getDisplayName());
        		if(loopNextPrestigeCost > loopBalance) {
                	notEnoughMoneyMessage.forEach(messageLine -> {
                		p.sendMessage(plugin.getString(messageLine, name)
                		.replace("%player%", name)
                		.replace("%nextprestige%", loopNextPrestigeName)
                		.replace("%nextprestige_display%", loopNextPrestigeDisplay)
                		.replace("%nextprestige_cost%", String.valueOf(loopNextPrestigeCost))
                		.replace("%nextprestige_cost_formatted%", loopNextPrestigeCostFormatted));
                	});
                	break;
        		}
        		p.sendMessage(prestigeMessage
        				.replace("%nextprestige%", loopNextPrestigeName)
        				.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        				);
        		List<String> loopNextPrestigeCommands = loopNextPrestige.getPrestigeCommands();
        		if(loopNextPrestigeCommands != null && !loopNextPrestigeCommands.isEmpty()) {
        			plugin.getServer().getScheduler().runTask(plugin, () -> {
        				plugin.executeCommands(p, loopNextPrestigeCommands);
        			});
        		}
        		List<String> loopNextPrestigeBroadcast = loopNextPrestige.getBroadcast();
        		if(loopNextPrestigeBroadcast != null && !loopNextPrestigeBroadcast.isEmpty()) {
        			loopNextPrestigeBroadcast.forEach(broadcastMessage -> {
        				Bukkit.broadcastMessage(plugin.getString(broadcastMessage, name)
        				.replace("%player%", name)
						.replace("%prestige%", loopPrestigeName)
						.replace("%nextprestige%", loopNextPrestigeName)
						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
						);
        			});
        		}
        		List<String> loopNextPrestigeMessage = loopNextPrestige.getMsg();
        		if(loopNextPrestigeMessage != null && !loopNextPrestigeMessage.isEmpty()) {
        			loopNextPrestigeMessage.forEach(messageLine -> {
        				p.sendMessage(plugin.getString(messageLine, name)
        						.replace("%player%", name)
        						.replace("%prestige%", loopPrestigeName)
        						.replace("%nextprestige%", loopNextPrestigeName)
        						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        						);
        			});
        		}
        		Map<Long, InfinitePrestigeSettings> cps = plugin.infinitePrestigeSettings.getContinuousPrestigeSettings();
    			if(!cps.isEmpty()) {
    				if(!loopNextPrestigeName.equals("0") && !loopNextPrestigeName.equals("1")) {
    					for(Entry<Long, InfinitePrestigeSettings> each : cps.entrySet()) {
    						
    						if(!getAPI().getNumberAPI().hasUsableDecimals((double)Long.valueOf(loopNextPrestigeName) / (double)each.getKey())) {
    							List<String> cbroadcast = each.getValue().getBroadcast();
    							if(cbroadcast != null && !cbroadcast.isEmpty()) {
    								cbroadcast.forEach(broadcastMessage -> {
    				    				Bukkit.broadcastMessage(plugin.getString(broadcastMessage.replace("{number}", loopNextPrestigeName), name)
    				    				.replace("%player%", name)
    									.replace("%prestige%", loopPrestigeName)
    									.replace("%nextprestige%", loopNextPrestigeName)
    									.replace("%nextprestige_display%", loopNextPrestigeDisplay)
    									);
    				    			});
    							}
    							List<String> ccommands = each.getValue().getCommands();
    							if(ccommands != null && !ccommands.isEmpty()) {
    								plugin.scheduler.runTask(plugin, () -> {
    									ccommands.forEach(cmd -> {
    										plugin.executeCommand(p, plugin.getString(cmd
    												.replace("{number}", loopNextPrestigeName)));
    									});
    								});
    							}
    						}
    					}
    				}
    			}
       			getAPI().getEconomy().withdrawPlayer(p, loopNextPrestigeCost);
       			finalPrestige.setString(loopNextPrestigeName);
       			getAPI().setPlayerPrestige(uuid, loopNextPrestigeName);
       			prestigeTimes.addAndGet(1);
       			if(plugin.globalStorage.getBooleanData("PrestigeOptions.ResetRank")) {
       				RankUpdateEvent e1 = new RankUpdateEvent(p, RankUpdateCause.RANKSET_BYPRESTIGE, plugin.globalStorage.getStringData("defaultrank"));
       				plugin.scheduler.runTask(plugin, () -> Bukkit.getPluginManager().callEvent(e1));
       				if(e1.isCancelled()) {
       					getAPI().taskedPlayers.remove(name);
       				} else {
       					plugin.playerStorage.setPlayerRank(p, plugin.globalStorage.getStringData("defaultrank"));
       				}
       				List<String> prestigeCommands = plugin.globalStorage.getStringListData("PrestigeOptions.prestige-cmds");
       				if(!prestigeCommands.isEmpty()) {
       		           prestigeCommands.forEach(cmd -> {
       		        	   if(cmd.startsWith("[rankpermissions]")) {
       		        		   getAPI().allRankAddPermissions.forEach(permission -> {
       		        		   plugin.perm.delPermissionAsync(p, permission);
       		        		   });
       		        	   } else if (cmd.startsWith("[prestigepermissions]")) {
       		        		   getAPI().allPrestigeAddPermissions.forEach(permission -> {
       		        			   plugin.perm.delPermissionAsync(p, permission);
       		        		   });
       		        	   } else if (cmd.startsWith("[rebirthpermissions]")) {
       		        		   getAPI().allRebirthAddPermissions.forEach(permission -> {
       		        			   plugin.perm.delPermissionAsync(p, permission);
       		        		   });
       		        	   } else {
       		        		   plugin.executeCommand(p, cmd);
       		        	   }
       		           });
       				}
       			}
        	}
        	if(!AccessibleString.isNullOrEmpty(finalPrestige)) {
        		IPrestigeDataHandler finalData = getAPI().getPrestige(finalPrestige.getString());
        		List<String> actionbarMessages = finalData.getActionbarMessages();
        		int actionbarInterval = finalData.getActionbarInterval();
        		String finalPrestigeName = finalData.getName();
        		String finalPrestigeDisplay = getAPI().c(finalData.getDisplayName());
        		if(actionbarMessages != null && !actionbarMessages.isEmpty()) {
        			List<String> replaced = new ArrayList<>();
        			actionbarMessages.forEach(messageLine -> {
        				replaced.add(messageLine.replace("%nextprestige%", finalPrestigeName).replace("%nextprestige_display%", finalPrestigeDisplay));
        			});
        			plugin.animateActionbar(p, actionbarInterval, replaced);
        		}
        		AsyncPrestigeMaxEvent event = new AsyncPrestigeMaxEvent(p, prestigeFrom.getString(), finalPrestigeName, prestigeTimes.get(), takenBalance.get());
        		Bukkit.getPluginManager().callEvent(event);
        	}
        	getProcessingPlayers().remove(name);
        });
		}).execute();
 	}
	
	@Override
	public void execute(Player player, boolean silent) {
		if(!silent) execute(player);
		Player p = player;
		String name = p.getName();
		UUID uuid = p.getUniqueId();
		if(isProcessing(name)) {
			p.sendMessage(getAPI().cp("prestigemax-is-on", p));
			return;
		}
		RankPath rankPath = getAPI().getPlayerRankPath(uuid);
		if(!getAPI().isLastRank(rankPath) && !getAPI().hasAllowPrestige(rankPath)) {
			return;
		}
		AtomicDouble takenBalance = new AtomicDouble(0.0);
		AtomicInteger prestigeTimes = new AtomicInteger(0);
		String prestigeName = getAPI().getPlayerPrestige(uuid);
		if(prestigeName == null) {
			getAPI().getPrestigeAPI().prestige(p);
			prestigeName = getAPI().getPlayerPrestige(uuid);
		}
		IPrestigeDataHandler prestige = getAPI().getPrestige(prestigeName);
		PrePrestigeMaxEvent e = new PrePrestigeMaxEvent(p, prestige);
		plugin.scheduler.runTask(plugin, () -> Bukkit.getPluginManager().callEvent(e));
		if(e.isCancelled()) {
			getProcessingPlayers().remove(name);
			return;
		}
		AccessibleString prestigeFrom = new AccessibleString(prestigeName);
		AccessibleString finalPrestige = new AccessibleString();
		String nextPrestigeName = prestige.getNextPrestigeName();
		double playerBalance = getAPI().getPlayerMoney(p);
		if(nextPrestigeName.equals("LASTPRESTIGE")) {
			return;
		}
        IPrestigeDataHandler nextPrestige = getAPI().getPrestige(nextPrestigeName);
		List<String> prestigesCollection = getAPI().getPrestigeStorage().getNativeLinkedPrestigesCollection();
		Map<String, String> stringRequirements = nextPrestige.getStringRequirements();
		Map<String, Double> numberRequirements = nextPrestige.getNumberRequirements();
		List<String> customRequirementMessage = nextPrestige.getCustomRequirementMessage();
		String rebirthName = getAPI().getPlayerRebirth(uuid);
		double nextPrestigeCost = getAPI().getIncreasedPrestigeCost(rebirthName, nextPrestigeName);
		String prestigeMessage = plugin.getString(getAPI().g("prestige"), name);
        if(nextPrestigeCost > playerBalance) {
        	return;
        }
        if(!checkRequirements(stringRequirements, numberRequirements, customRequirementMessage, p, true))
        	return;
        int currentPrestigeIndex = prestigesCollection.indexOf(prestigeName);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
        	for(int i = currentPrestigeIndex ; i < prestigesCollection.size(); i++) {
        		String loopPrestigeName = prestigesCollection.get(i);
        		double loopBalance = getAPI().getPlayerMoney(p);
        		IPrestigeDataHandler loopPrestige = getAPI().getPrestige(loopPrestigeName);
        		String loopNextPrestigeName = loopPrestige.getNextPrestigeName();
        		if(loopNextPrestigeName.equals("LASTPRESTIGE")) {
        			break;
        		}
        		IPrestigeDataHandler loopNextPrestige = getAPI().getPrestige(loopNextPrestigeName);
        		double loopNextPrestigeCost = getAPI().getIncreasedPrestigeCost(rebirthName, loopNextPrestigeName);
        		takenBalance.addAndGet(loopNextPrestigeCost);
        		Map<String, String> loopStringRequirements = loopNextPrestige.getStringRequirements();
        		Map<String, Double> loopNumberRequirements = loopNextPrestige.getNumberRequirements();
        		List<String> loopCustomRequirementMessage = loopNextPrestige.getCustomRequirementMessage();
        		String loopNextPrestigeDisplay = getAPI().c(loopNextPrestige.getDisplayName());
        		if(loopNextPrestigeCost > loopBalance) {
                	break;
        		}
        		if(!checkRequirements(loopStringRequirements, loopNumberRequirements, loopCustomRequirementMessage, p, true))
        			break;
        		p.sendMessage(prestigeMessage
        				.replace("%nextprestige%", loopNextPrestigeName)
        				.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        				);
        		List<String> loopNextPrestigeCommands = loopNextPrestige.getPrestigeCommands();
        		if(loopNextPrestigeCommands != null && !loopNextPrestigeCommands.isEmpty()) {
        			plugin.getServer().getScheduler().runTask(plugin, () -> {
        				plugin.executeCommands(p, loopNextPrestigeCommands);
        			});
        		}
        		List<String> loopNextPrestigeAddPermissionList = loopNextPrestige.getAddPermissionList();
        		if(loopNextPrestigeAddPermissionList != null && !loopNextPrestigeAddPermissionList.isEmpty()) {
        			loopNextPrestigeAddPermissionList.forEach(permission -> {
        				getAPI().getPermissionManager().addPermission(p, permission
        						.replace("%player%", name)
        						.replace("%prestige%", loopPrestigeName)
        						.replace("%nextprestige%", loopNextPrestigeName)
        						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        						);
        			});
        		}
        		List<String> loopNextPrestigeDelPermissionList = loopNextPrestige.getDelPermissionList();
        		if(loopNextPrestigeDelPermissionList != null && !loopNextPrestigeDelPermissionList.isEmpty()) {
        			loopNextPrestigeDelPermissionList.forEach(permission -> {
        				getAPI().getPermissionManager().delPermission(p, permission
        						.replace("%player%", name)
        						.replace("%prestige%", loopPrestigeName)
        						.replace("%nextprestige%", loopNextPrestigeName)
        						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        						);
        			});
        		}
        		List<String> loopNextPrestigeBroadcast = loopNextPrestige.getBroadcast();
        		if(loopNextPrestigeBroadcast != null && !loopNextPrestigeBroadcast.isEmpty()) {
        			loopNextPrestigeBroadcast.forEach(broadcastMessage -> {
        				Bukkit.broadcastMessage(plugin.getString(broadcastMessage, name)
        				.replace("%player%", name)
						.replace("%prestige%", loopPrestigeName)
						.replace("%nextprestige%", loopNextPrestigeName)
						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
						);
        			});
        		}
        		List<String> loopNextPrestigeMessage = loopNextPrestige.getMsg();
        		if(loopNextPrestigeMessage != null && !loopNextPrestigeMessage.isEmpty()) {
        			loopNextPrestigeMessage.forEach(messageLine -> {
        				p.sendMessage(plugin.getString(messageLine, name)
        						.replace("%player%", name)
        						.replace("%prestige%", loopPrestigeName)
        						.replace("%nextprestige%", loopNextPrestigeName)
        						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        						);
        			});
        		}
        		List<String> loopNextPrestigeActions = loopNextPrestige.getActions();
        		if(getAPI().hasActionUtilEnabled() && loopNextPrestigeActions != null && !loopNextPrestigeActions.isEmpty()) {
        			ActionUtil.executeActions(p, loopNextPrestigeActions);
        		}
        		Map<String, Double> chances = new HashMap<>();
        		PrestigeRandomCommands loopPrestigeRandomCommands = loopNextPrestige.getRandomCommandsManager();
       			if(loopPrestigeRandomCommands != null && loopPrestigeRandomCommands.getRandomCommandsMap() != null) {
       			  for(String section : loopPrestigeRandomCommands.getRandomCommandsMap().keySet()) {
       				  Double chance = loopPrestigeRandomCommands.getChance(section);
       				  chances.put(section, chance);
       			  }
       			  String randomSection = getAPI().getNumberAPI().getChanceFromWeightedMap(chances);
       			  if(loopPrestigeRandomCommands.getCommands(randomSection) != null) {
       			    List<String> commands = loopPrestigeRandomCommands.getCommands(randomSection);
       			    ConsoleCommandSender ccs = Bukkit.getConsoleSender();
       			    for(String singleCommand : commands) {
       				    String replacedCommand = getAPI().cp(singleCommand.replace("%player%", name).replace("%nextprestige%", loopNextPrestigeName), p);
       				    plugin.getServer().getScheduler().runTask(plugin, () -> {
       				      plugin.getServer().dispatchCommand(ccs, replacedCommand);
       				    });
       			    }
       			  }
       			}
       			getAPI().getEconomy().withdrawPlayer(p, loopNextPrestigeCost);
       			finalPrestige.setString(loopNextPrestigeName);
       			getAPI().setPlayerPrestige(uuid, loopNextPrestigeName);
       			prestigeTimes.addAndGet(1);
        	}
        	if(!AccessibleString.isNullOrEmpty(finalPrestige)) {
        		IPrestigeDataHandler finalData = getAPI().getPrestige(finalPrestige.getString());
        		List<String> actionbarMessages = finalData.getActionbarMessages();
        		int actionbarInterval = finalData.getActionbarInterval();
        		String finalPrestigeName = finalData.getName();
        		String finalPrestigeDisplay = getAPI().c(finalData.getDisplayName());
        		if(actionbarMessages != null && !actionbarMessages.isEmpty()) {
        			List<String> replaced = new ArrayList<>();
        			actionbarMessages.forEach(messageLine -> {
        				replaced.add(messageLine.replace("%nextprestige%", finalPrestigeName).replace("%nextprestige_display%", finalPrestigeDisplay));
        			});
        			plugin.animateActionbar(p, actionbarInterval, replaced);
        		}
        		getAPI().celeberate(p);
        		AsyncPrestigeMaxEvent event = new AsyncPrestigeMaxEvent(p, prestigeFrom.getString(), finalPrestigeName, prestigeTimes.get(), takenBalance.get());
        		Bukkit.getPluginManager().callEvent(event);
        	}
        	getProcessingPlayers().remove(name);
        });
	}

	@Override
	public Set<String> getProcessingPlayers() {
		return this.processingPlayers;
	}

	@Override
	public boolean isProcessing(String name) {
		return this.processingPlayers.contains(name);
	}

	@Override
	public PRXAPI getAPI() {
		return this.api;
	}

	/**
	 * 
	 * @param stringRequirementsMap
	 * @param numberRequirementsMap
	 * @param message
	 * @param player
	 * @return true if met
	 */
	public boolean checkRequirements(Map<String, String> stringRequirementsMap, Map<String, Double> numberRequirementsMap, List<String> message, Player player, boolean silent) {
		boolean failedRequirements = false;
		Map<String, String> stringRequirements = stringRequirementsMap;
		Map<String, Double> numberRequirements = numberRequirementsMap;
		Player p = player;
		if(stringRequirements != null) {
			for(Entry<String, String> entry : stringRequirements.entrySet()) {
				String placeholder = getAPI().cp(entry.getKey(), p);
				String value = getAPI().cp(entry.getValue(), p);
				if(!placeholder.equalsIgnoreCase(value)) {
					failedRequirements = true;
				}
			}
		}
		if(numberRequirements != null) {
			for(Entry<String, Double> entry : numberRequirements.entrySet()) {
				String placeholder = getAPI().cp(entry.getKey(), p);
				double value = entry.getValue();
				if(Double.valueOf(placeholder) < value) {
					failedRequirements = true;
				}
			}
		}
		List<String> customRequirementMessage = message;
		if(failedRequirements) {
			if(!silent) {
			if(customRequirementMessage != null) {
				customRequirementMessage.forEach(messageLine -> {
					p.sendMessage(getAPI().cp(messageLine, p));
				});
			}
			}
			getProcessingPlayers().remove(p.getName());
		}
		return !failedRequirements;
	}
	
	public void executeOnAsyncQueue(Player player, boolean infinite) {
		Player p = player;
		String name = p.getName();
		UUID uuid = p.getUniqueId();
		if(isProcessing(name)) {
			p.sendMessage(plugin.getString(getAPI().g("prestigemax-is-on"), name));
			return;
		}
		RankPath rankPath = getAPI().getPlayerRankPath(uuid);
		if(!getAPI().isLastRank(rankPath) && !getAPI().hasAllowPrestige(rankPath)) {
			if(getAPI().canRankup(p)) {
				getAPI().rankupMax(p);
			} else {
				//p.sendMessage(noPrestigeMessage);
			}
			return;
		}
		AtomicDouble takenBalance = new AtomicDouble(0.0);
		AtomicInteger prestigeTimes = new AtomicInteger(2);
		String prestigeName = getAPI().getPlayerPrestige(uuid);
		if(prestigeName == null) {
			if(getAPI().canPrestige(p)) {
			getAPI().getPrestigeAPI().prestige(p);
			prestigeName = getAPI().getFirstPrestige();
			} else {
				getAPI().getPrestigeAPI().prestige(p);
				getProcessingPlayers().remove(name);
				return;
			}
		}
		IPrestigeDataHandler prestige = getAPI().getPrestige(prestigeName);
		PrePrestigeMaxEvent e = new PrePrestigeMaxEvent(p, prestige);
		plugin.scheduler.runTask(plugin, () -> Bukkit.getPluginManager().callEvent(e));
		if(e.isCancelled()) {
			getProcessingPlayers().remove(name);
			return;
		}
		AccessibleString prestigeFrom = new AccessibleString(prestigeName);
		AccessibleString finalPrestige = new AccessibleString();
		String nextPrestigeName = prestige.getNextPrestigeName();
		double playerBalance = getAPI().getPlayerMoney(p);
		if(nextPrestigeName.equals(String.valueOf(plugin.infinitePrestigeSettings.getFinalPrestige()+1))) {
			getProcessingPlayers().remove(name);
			lastPrestigeMessage.forEach(p::sendMessage);
			return;
		}
        IPrestigeDataHandler nextPrestige = getAPI().getPrestige(nextPrestigeName);
		String rebirthName = getAPI().getPlayerRebirth(uuid);
		String nextPrestigeDisplay = nextPrestige.getDisplayName();
		double nextPrestigeCost = getAPI().getIncreasedPrestigeCost(rebirthName, nextPrestigeName);
		String nextPrestigeCostFormatted = getAPI().formatBalance(nextPrestigeCost);
		String prestigeMessage = plugin.getString(getAPI().g("prestige"), name);
        if(nextPrestigeCost > playerBalance) {
        	notEnoughMoneyMessage.forEach(messageLine -> {
        	    messageLine = plugin.getString(messageLine, name)
        		.replace("%player%", name)
        		.replace("%nextprestige%", nextPrestigeName)
        		.replace("%nextprestige_display%", nextPrestigeDisplay)
        		.replace("%nextprestige_cost%", String.valueOf(nextPrestigeCost))
        		.replace("%nextprestige_cost_formatted%", nextPrestigeCostFormatted);
        	    p.sendMessage(messageLine);
        	});
        	return;
        }
        getProcessingPlayers().add(name);
        int currentPrestigeIndex = Integer.valueOf(prestigeName);
        AtomicInteger increment = new AtomicInteger(currentPrestigeIndex-1);
        int size = (int)plugin.infinitePrestigeSettings.getFinalPrestige();
        AccessibleBukkitTask accessibleTask = new AccessibleBukkitTask();
        accessibleTask.set(plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
        	/*if(!plugin.getPlayerStorage().getPlayerData().containsKey(uuid.toString())) {
    			getProcessingPlayers().remove(name);
    			accessibleTask.cancel();
    		}*/
        	plugin.getTaskChainFactory().newSharedChain("prestigemax").async(() -> {
        		TimeCounter counter = new TimeCounter();
        		counter.begin();
        		if(!plugin.getPlayerStorage().getPlayerData().containsKey(uuid.toString())) {
        			getProcessingPlayers().remove(name);
        			accessibleTask.cancel();
        		}
        	    plugin.debug("Is player online check: " + counter.tryEndingAsSecondsFormattedAndRestart());
        	    
        	    int i = increment.get();
        		if(i >= size) {
        			if(!AccessibleString.isNullOrEmpty(finalPrestige)) {
                		IPrestigeDataHandler finalData = getAPI().getPrestige(finalPrestige.getString());
                		List<String> actionbarMessages = finalData.getActionbarMessages();
                		int actionbarInterval = finalData.getActionbarInterval();
                		String finalPrestigeName = finalData.getName();
                		String finalPrestigeDisplay = getAPI().c(finalData.getDisplayName());
                		if(actionbarMessages != null && !actionbarMessages.isEmpty()) {
                			List<String> replaced = new ArrayList<>();
                			actionbarMessages.forEach(messageLine -> {
                				replaced.add(messageLine.replace("%nextprestige%", finalPrestigeName).replace("%nextprestige_display%", finalPrestigeDisplay));
                			});
                			plugin.animateActionbar(p, actionbarInterval, replaced);
                		}
                		AsyncPrestigeMaxEvent event = new AsyncPrestigeMaxEvent(p, prestigeFrom.getString(), finalPrestigeName, prestigeTimes.get(), takenBalance.get());
                		Bukkit.getPluginManager().callEvent(event);
                	}
                	getProcessingPlayers().remove(name);
        			accessibleTask.cancel();
        		}
        		plugin.debug("Has surpassed prestiges size check: " + counter.tryEndingAsSecondsFormattedAndRestart());
        		increment.incrementAndGet();
        		i = increment.get();
        		RankPath loopRankPath = getAPI().getPlayerRankPath(uuid);
           		if(!getAPI().isLastRank(loopRankPath) && !getAPI().hasAllowPrestige(loopRankPath)) {
        			if(getAPI().canRankup(p)) {
        				getAPI().rankupMax(p);
        			}
        			return;
        		}
           		plugin.debug("Is last rank check: " + counter.tryEndingAsSecondsFormattedAndRestart());
        		String loopPrestigeName = String.valueOf(i);
        		double loopBalance = getAPI().getPlayerMoney(p);
        		
        		IPrestigeDataHandler loopPrestige = getAPI().getPrestige(loopPrestigeName);
        		String loopNextPrestigeName = loopPrestige.getNextPrestigeName();
        		if(loopNextPrestigeName.equals(finalInfinitePrestige)) {
        			lastPrestigeMessage.forEach(p::sendMessage);
        			//accessibleTask.cancel();
        			executeFinal(accessibleTask, player, name, finalPrestige, prestigeFrom, prestigeTimes, takenBalance);
        		}
        		plugin.debug("Is final prestige check: " + counter.tryEndingAsSecondsFormattedAndRestart());
        		IPrestigeDataHandler loopNextPrestige = getAPI().getPrestige(loopNextPrestigeName);
        		double loopNextPrestigeCost = getAPI().getIncreasedPrestigeCost(rebirthName, loopNextPrestigeName);
        		takenBalance.addAndGet(loopNextPrestigeCost);
        		String loopNextPrestigeCostFormatted = getAPI().formatBalance(loopNextPrestigeCost);
        		String loopNextPrestigeDisplay = getAPI().c(loopNextPrestige.getDisplayName());
        		if(loopNextPrestigeCost > loopBalance) {
                	notEnoughMoneyMessage.forEach(messageLine -> {
                		p.sendMessage(plugin.getString(messageLine, name)
                		.replace("%player%", name)
                		.replace("%nextprestige%", loopNextPrestigeName)
                		.replace("%nextprestige_display%", loopNextPrestigeDisplay)
                		.replace("%nextprestige_cost%", String.valueOf(loopNextPrestigeCost))
                		.replace("%nextprestige_cost_formatted%", loopNextPrestigeCostFormatted));
                	});
                	//accessibleTask.cancel();
                	executeFinal(accessibleTask, player, name, finalPrestige, prestigeFrom, prestigeTimes, takenBalance);
        		}
        		plugin.debug("Has enough balance check: " + counter.tryEndingAsSecondsFormattedAndRestart());
        		p.sendMessage(prestigeMessage
        				.replace("%nextprestige%", loopNextPrestigeName)
        				.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        				);
        		if(plugin.isInfinitePrestige) {
        			List<String> commands = plugin.infinitePrestigeSettings.getCommands();
        			if(commands != null && !commands.isEmpty()) {
        				plugin.getServer().getScheduler().runTask(plugin, () -> {
        					commands.forEach(cmd -> {
        						plugin.executeCommand(p, plugin.getString(cmd
        								.replace("{number}", loopPrestigeName)));
        					});
        				});
        			}
        			List<String> broadcast = plugin.infinitePrestigeSettings.getBroadcast();
        			if(broadcast != null && !broadcast.isEmpty()) {
        				broadcast.forEach(broadcastMessage -> {
            				Bukkit.broadcastMessage(plugin.getString(broadcastMessage.replace("{number}", loopPrestigeName), name)
            				.replace("%player%", name)
    						.replace("%prestige%", loopPrestigeName)
    						.replace("%nextprestige%", loopNextPrestigeName)
    						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
    						);
            			});
        			}
        		}
        		plugin.debug("Is infinite prestige check: " + counter.tryEndingAsSecondsFormattedAndRestart());
        		List<String> loopNextPrestigeCommands = loopNextPrestige.getPrestigeCommands();
        		if(loopNextPrestigeCommands != null && !loopNextPrestigeCommands.isEmpty()) {
        			plugin.getServer().getScheduler().runTask(plugin, () -> {
        				plugin.executeCommands(p, loopNextPrestigeCommands);
        			});
        		}
        		plugin.debug("Has commands check: " + counter.tryEndingAsSecondsFormattedAndRestart());
        		/*
        		List<String> loopNextPrestigeAddPermissionList = loopNextPrestige.getAddPermissionList();
        		if(loopNextPrestigeAddPermissionList != null && !loopNextPrestigeAddPermissionList.isEmpty()) {
        			loopNextPrestigeAddPermissionList.forEach(permission -> {
        				getAPI().getPermissionManager().addPermission(p, permission
        						.replace("%player%", name)
        						.replace("%prestige%", loopPrestigeName)
        						.replace("%nextprestige%", loopNextPrestigeName)
        						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        						);
        			});
        		}
        		
        		List<String> loopNextPrestigeDelPermissionList = loopNextPrestige.getDelPermissionList();
        		if(loopNextPrestigeDelPermissionList != null && !loopNextPrestigeDelPermissionList.isEmpty()) {
        			loopNextPrestigeDelPermissionList.forEach(permission -> {
        				getAPI().getPermissionManager().delPermission(p, permission
        						.replace("%player%", name)
        						.replace("%prestige%", loopPrestigeName)
        						.replace("%nextprestige%", loopNextPrestigeName)
        						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        						);
        			});
        		}
        		*/
        		List<String> loopNextPrestigeBroadcast = loopNextPrestige.getBroadcast();
        		if(loopNextPrestigeBroadcast != null && !loopNextPrestigeBroadcast.isEmpty()) {
        			loopNextPrestigeBroadcast.forEach(broadcastMessage -> {
        				Bukkit.broadcastMessage(plugin.getString(broadcastMessage, name)
        				.replace("%player%", name)
						.replace("%prestige%", loopPrestigeName)
						.replace("%nextprestige%", loopNextPrestigeName)
						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
						);
        			});
        		}
        		plugin.debug("Has broadcast check: " + counter.tryEndingAsSecondsFormattedAndRestart());
        		List<String> loopNextPrestigeMessage = loopNextPrestige.getMsg();
        		if(loopNextPrestigeMessage != null && !loopNextPrestigeMessage.isEmpty()) {
        			loopNextPrestigeMessage.forEach(messageLine -> {
        				p.sendMessage(plugin.getString(messageLine, name)
        						.replace("%player%", name)
        						.replace("%prestige%", loopPrestigeName)
        						.replace("%nextprestige%", loopNextPrestigeName)
        						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        						);
        			});
        		}
        		plugin.debug("Has message check: " + counter.tryEndingAsSecondsFormattedAndRestart());
        		/*
        		List<String> loopNextPrestigeActions = loopNextPrestige.getActions();
        		if(getAPI().hasActionUtilEnabled() && loopNextPrestigeActions != null && !loopNextPrestigeActions.isEmpty()) {
        			ActionUtil.executeActions(p, loopNextPrestigeActions);
        		}
        		Map<String, Double> chances = new HashMap<>();
        		PrestigeRandomCommands loopPrestigeRandomCommands = loopNextPrestige.getRandomCommandsManager();
       			if(loopPrestigeRandomCommands != null && loopPrestigeRandomCommands.getRandomCommandsMap() != null) {
       			  for(String section : loopPrestigeRandomCommands.getRandomCommandsMap().keySet()) {
       				  Double chance = loopPrestigeRandomCommands.getChance(section);
       				  chances.put(section, chance);
       			  }
       			  String randomSection = getAPI().getNumberAPI().getChanceFromWeightedMap(chances);
       			  if(loopPrestigeRandomCommands.getCommands(randomSection) != null) {
       			    List<String> commands = loopPrestigeRandomCommands.getCommands(randomSection);
       			    ConsoleCommandSender ccs = Bukkit.getConsoleSender();
       			    for(String singleCommand : commands) {
       				    String replacedCommand = getAPI().cp(singleCommand.replace("%player%", name).replace("%nextprestige%", loopNextPrestigeName), p);
       				    plugin.getServer().getScheduler().runTask(plugin, () -> {
       				      plugin.getServer().dispatchCommand(ccs, replacedCommand);
       				    });
       			    }
       			  }
       			}
       			*/
       			getAPI().getEconomy().withdrawPlayer(p, loopNextPrestigeCost);
       			plugin.debug("Money deduction: " + counter.tryEndingAsSecondsFormattedAndRestart());
       			finalPrestige.setString(loopNextPrestigeName);
       			getAPI().setPlayerPrestige(uuid, loopNextPrestigeName);
       			plugin.debug("Player prestige update: " + counter.tryEndingAsSecondsFormattedAndRestart());
       			prestigeTimes.addAndGet(1);
       			plugin.debug("Prestige strike update: " + counter.tryEndingAsSecondsFormatted());
        	}).execute();
        }, 0, 1));
 	}
	
	public void executeOnAsyncMultiThreadedQueue(Player player) {
		Player p = player;
		String name = p.getName();
		UUID uuid = p.getUniqueId();
		if(multiThreadSet.contains(name)) {
    		return;
    	}
		if(isProcessing(name)) {
			p.sendMessage(getAPI().g("prestigemax-is-on"));
			return;
		}
		RankPath rankPath = getAPI().getPlayerRankPath(uuid);
		if(!getAPI().isLastRank(rankPath) && !getAPI().hasAllowPrestige(rankPath)) {
			if(getAPI().canRankup(p)) {
				getAPI().rankupMax(p);
			} else {
				//p.sendMessage(noPrestigeMessage);
			}
			return;
		}
		AtomicDouble takenBalance = new AtomicDouble(0.0);
		AtomicInteger prestigeTimes = new AtomicInteger(0);
		String prestigeName = getAPI().getPlayerPrestige(uuid);
		if(prestigeName == null) {
			if(getAPI().canPrestige(p)) {
			getAPI().getPrestigeAPI().prestige(p);
			prestigeName = getAPI().getFirstPrestige();
			} else {
				getAPI().getPrestigeAPI().prestige(p);
				getProcessingPlayers().remove(name);
				return;
			}
		}
		IPrestigeDataHandler prestige = getAPI().getPrestige(prestigeName);
		PrePrestigeMaxEvent e = new PrePrestigeMaxEvent(p, prestige);
		plugin.scheduler.runTask(plugin, () -> Bukkit.getPluginManager().callEvent(e));
		if(e.isCancelled()) {
			getProcessingPlayers().remove(name);
			return;
		}
		AccessibleString prestigeFrom = new AccessibleString(prestigeName);
		AccessibleString finalPrestige = new AccessibleString();
		String nextPrestigeName = prestige.getNextPrestigeName();
		double playerBalance = getAPI().getPlayerMoney(p);
		if(nextPrestigeName.equals("LASTPRESTIGE")) {
			getProcessingPlayers().remove(name);
			lastPrestigeMessage.forEach(p::sendMessage);
			return;
		}
        IPrestigeDataHandler nextPrestige = getAPI().getPrestige(nextPrestigeName);
		List<String> prestigesCollection = getAPI().getPrestigeStorage().getNativeLinkedPrestigesCollection();
		Map<String, String> stringRequirements = nextPrestige.getStringRequirements();
		Map<String, Double> numberRequirements = nextPrestige.getNumberRequirements();
		List<String> customRequirementMessage = nextPrestige.getCustomRequirementMessage();
		String rebirthName = getAPI().getPlayerRebirth(uuid);
		String nextPrestigeDisplay = nextPrestige.getDisplayName();
		double nextPrestigeCost = getAPI().getIncreasedPrestigeCost(rebirthName, nextPrestigeName);
		String nextPrestigeCostFormatted = getAPI().formatBalance(nextPrestigeCost);
		String prestigeMessage = plugin.getString(getAPI().g("prestige"), name);
        if(nextPrestigeCost > playerBalance) {
        	notEnoughMoneyMessage.forEach(messageLine -> {
        	    messageLine = plugin.getString(messageLine, name)
        		.replace("%player%", name)
        		.replace("%nextprestige%", nextPrestigeName)
        		.replace("%nextprestige_display%", nextPrestigeDisplay)
        		.replace("%nextprestige_cost%", String.valueOf(nextPrestigeCost))
        		.replace("%nextprestige_cost_formatted%", nextPrestigeCostFormatted);
        	    p.sendMessage(messageLine);
        	});
        	return;
        }
        if(!checkRequirements(stringRequirements, numberRequirements, customRequirementMessage, p, false))
        	return;
        getProcessingPlayers().add(name);
        chancesCache.put(name, new HashMap<>());
        int currentPrestigeIndex = prestigesCollection.indexOf(prestigeName);
        AtomicLong increment = this.plugin.isInfinitePrestige ? new AtomicLong(Long.valueOf(prestigeName)) : new AtomicLong(currentPrestigeIndex-1);
        int size = prestigesCollection.size();    
        prestigesPerTick = this.plugin.isInfinitePrestige ? 10 : prestigesPerTick;
        AccessibleBukkitTask accessibleTask = new AccessibleBukkitTask();
        accessibleTask.set(plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
        	if(multiThreadSet.contains(name)) {
        		return;
        	}
        	multiThreadSet.add(name);
        	for(int prestigeAmount = 0; prestigeAmount < prestigesPerTick+1; prestigeAmount++) {
        		if(plugin.isInfinitePrestige) {
        			if(increment.get() >= plugin.infinitePrestigeSettings.getFinalPrestige()) {
        				executeFinal(accessibleTask, player, name, finalPrestige, prestigeFrom, prestigeTimes, takenBalance);
        				break;
        			}
        		}
        		if(!plugin.getPlayerStorage().getPlayerData().containsKey(uuid.toString())) {
        			getProcessingPlayers().remove(name);
        			multiThreadSet.remove(name);
        			accessibleTask.cancel();
        			break;
        		}
        		increment.incrementAndGet();
        		long i = increment.get();
        		plugin.debug("Increment: " + i);
        		RankPath loopRankPath = getAPI().getPlayerRankPath(uuid);
           		if(!getAPI().isLastRank(loopRankPath) && !getAPI().hasAllowPrestige(loopRankPath)) {
        			if(getAPI().canRankup(p)) {
        				getAPI().rankupMax(p);
        			}
        			return;
        		}
           		if(!plugin.isInfinitePrestige) {
           		if(i >= size) {
           			executeFinal(accessibleTask, player, name, finalPrestige, prestigeFrom, prestigeTimes, takenBalance);
        			break;
        		}
           		} else {
           			if(i >= plugin.infinitePrestigeSettings.getFinalPrestige()) {
           				executeFinal(accessibleTask, player, name, finalPrestige, prestigeFrom, prestigeTimes, takenBalance);
            			break;
           			}
           		}
        		AccessibleString loopPrestigeNameHolder = new AccessibleString();
        		if(plugin.isInfinitePrestige) {
        			loopPrestigeNameHolder.setString(String.valueOf(increment.get()));
        		} else {			
        			loopPrestigeNameHolder.setString(prestigesCollection.get(Integer.valueOf(String.valueOf(i))));
        		}
        		String loopPrestigeName = loopPrestigeNameHolder.getString();
        		double loopBalance = getAPI().getPlayerMoney(p);
        		
        		IPrestigeDataHandler loopPrestige = getAPI().getPrestige(loopPrestigeName);
        		String loopNextPrestigeName = loopPrestige.getNextPrestigeName();
        		if(loopNextPrestigeName.equals("LASTPRESTIGE")) {
        			lastPrestigeMessage.forEach(p::sendMessage);
        			executeFinal(accessibleTask, player, name, finalPrestige, prestigeFrom, prestigeTimes, takenBalance);
        			break;
        		}
        		IPrestigeDataHandler loopNextPrestige = getAPI().getPrestige(loopNextPrestigeName);
        		double loopNextPrestigeCost = getAPI().getIncreasedPrestigeCost(rebirthName, loopNextPrestigeName);
        		takenBalance.addAndGet(loopNextPrestigeCost);
        		String loopNextPrestigeCostFormatted = getAPI().formatBalance(loopNextPrestigeCost);
        		Map<String, String> loopStringRequirements = loopNextPrestige.getStringRequirements();
        		Map<String, Double> loopNumberRequirements = loopNextPrestige.getNumberRequirements();
        		List<String> loopCustomRequirementMessage = loopNextPrestige.getCustomRequirementMessage();
        		String loopNextPrestigeDisplay = getAPI().c(loopNextPrestige.getDisplayName());
        		if(loopNextPrestigeCost > loopBalance) {
                	notEnoughMoneyMessage.forEach(messageLine -> {
                		p.sendMessage(plugin.getString(messageLine, name)
                		.replace("%player%", name)
                		.replace("%nextprestige%", loopNextPrestigeName)
                		.replace("%nextprestige_display%", loopNextPrestigeDisplay)
                		.replace("%nextprestige_cost%", String.valueOf(loopNextPrestigeCost))
                		.replace("%nextprestige_cost_formatted%", loopNextPrestigeCostFormatted));
                	});
                	executeFinal(accessibleTask, player, name, finalPrestige, prestigeFrom, prestigeTimes, takenBalance);
                	break;
        		}
        		if(!checkRequirements(loopStringRequirements, loopNumberRequirements, loopCustomRequirementMessage, p, false)) {
        			executeFinal(accessibleTask, player, name, finalPrestige, prestigeFrom, prestigeTimes, takenBalance);
        			break;
        		}
        		p.sendMessage(prestigeMessage
        				.replace("%nextprestige%", loopNextPrestigeName)
        				.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        				);
        		if(plugin.isInfinitePrestige) {
        			List<String> commands = plugin.infinitePrestigeSettings.getCommands();
        			if(commands != null && !commands.isEmpty()) {
        				plugin.getServer().getScheduler().runTask(plugin, () -> {
        					commands.forEach(cmd -> {
        						plugin.executeCommand(p, plugin.getString(cmd
        								.replace("{number}", loopPrestigeName)));
        					});
        				});
        			}
        			List<String> broadcast = plugin.infinitePrestigeSettings.getBroadcast();
        			if(broadcast != null && !broadcast.isEmpty()) {
        				broadcast.forEach(broadcastMessage -> {
            				Bukkit.broadcastMessage(plugin.getString(broadcastMessage.replace("{number}", loopPrestigeName), name)
            				.replace("%player%", name)
    						.replace("%prestige%", loopPrestigeName)
    						.replace("%nextprestige%", loopNextPrestigeName)
    						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
    						);
            			});
        			}
        		}
        		List<String> loopNextPrestigeCommands = loopNextPrestige.getPrestigeCommands();
        		if(loopNextPrestigeCommands != null && !loopNextPrestigeCommands.isEmpty()) {
        			plugin.getServer().getScheduler().runTask(plugin, () -> {
        				plugin.executeCommands(p, loopNextPrestigeCommands);
        			});
        		}
        		List<String> loopNextPrestigeAddPermissionList = loopNextPrestige.getAddPermissionList();
        		if(loopNextPrestigeAddPermissionList != null && !loopNextPrestigeAddPermissionList.isEmpty()) {
        			loopNextPrestigeAddPermissionList.forEach(permission -> {
        				getAPI().getPermissionManager().addPermission(p, permission
        						.replace("%player%", name)
        						.replace("%prestige%", loopPrestigeName)
        						.replace("%nextprestige%", loopNextPrestigeName)
        						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        						);
        			});
        		}
        		List<String> loopNextPrestigeDelPermissionList = loopNextPrestige.getDelPermissionList();
        		if(loopNextPrestigeDelPermissionList != null && !loopNextPrestigeDelPermissionList.isEmpty()) {
        			loopNextPrestigeDelPermissionList.forEach(permission -> {
        				getAPI().getPermissionManager().delPermission(p, permission
        						.replace("%player%", name)
        						.replace("%prestige%", loopPrestigeName)
        						.replace("%nextprestige%", loopNextPrestigeName)
        						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        						);
        			});
        		}
        		List<String> loopNextPrestigeBroadcast = loopNextPrestige.getBroadcast();
        		if(loopNextPrestigeBroadcast != null && !loopNextPrestigeBroadcast.isEmpty()) {
        			loopNextPrestigeBroadcast.forEach(broadcastMessage -> {
        				Bukkit.broadcastMessage(plugin.getString(broadcastMessage, name)
        				.replace("%player%", name)
						.replace("%prestige%", loopPrestigeName)
						.replace("%nextprestige%", loopNextPrestigeName)
						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
						);
        			});
        		}
        		List<String> loopNextPrestigeMessage = loopNextPrestige.getMsg();
        		if(loopNextPrestigeMessage != null && !loopNextPrestigeMessage.isEmpty()) {
        			loopNextPrestigeMessage.forEach(messageLine -> {
        				p.sendMessage(plugin.getString(messageLine, name)
        						.replace("%player%", name)
        						.replace("%prestige%", loopPrestigeName)
        						.replace("%nextprestige%", loopNextPrestigeName)
        						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        						);
        			});
        		}
        		List<String> loopNextPrestigeActions = loopNextPrestige.getActions();
        		if(getAPI().hasActionUtilEnabled() && loopNextPrestigeActions != null && !loopNextPrestigeActions.isEmpty()) {
        			ActionUtil.executeActions(p, loopNextPrestigeActions);
        		}
        		
        		PrestigeRandomCommands loopPrestigeRandomCommands = loopNextPrestige.getRandomCommandsManager();
       			if(loopPrestigeRandomCommands != null && loopPrestigeRandomCommands.getRandomCommandsMap() != null) {
       				Map<String, Double> chances = chancesCache.get(name);
            		chances.clear();
       			  for(String section : loopPrestigeRandomCommands.getRandomCommandsMap().keySet()) {
       				  Double chance = loopPrestigeRandomCommands.getChance(section);
       				  chances.put(section, chance);
       			  }
       			  String randomSection = getAPI().getNumberAPI().getChanceFromWeightedMap(chances);
       			  if(loopPrestigeRandomCommands.getCommands(randomSection) != null) {
       			    List<String> commands = loopPrestigeRandomCommands.getCommands(randomSection);
       			    ConsoleCommandSender ccs = Bukkit.getConsoleSender();
       			    for(String singleCommand : commands) {
       				    String replacedCommand = getAPI().cp(singleCommand.replace("%player%", name).replace("%nextprestige%", loopNextPrestigeName), p);
       				    plugin.getServer().getScheduler().runTask(plugin, () -> {
       				      plugin.getServer().dispatchCommand(ccs, replacedCommand);
       				    });
       			    }
       			  }
       			}
       			getAPI().getEconomy().withdrawPlayer(p, loopNextPrestigeCost);
       			finalPrestige.setString(loopNextPrestigeName);
       			getAPI().setPlayerPrestige(uuid, loopNextPrestigeName);
       			prestigeTimes.addAndGet(1);
       			if(plugin.getGlobalStorage().getBooleanData("PrestigeOptions.ResetRank")) {
       				Bukkit.getScheduler().runTask(plugin, () -> {
       				RankUpdateEvent e1 = new RankUpdateEvent(p, RankUpdateCause.RANKSET_BYPRESTIGE);
       				if(e1.isCancelled()) {
       					getProcessingPlayers().remove(name);
       					return;
       				}
       				plugin.getPlayerStorage().setPlayerRank(p, plugin.getGlobalStorage().getStringData("defaultrank"));
       				Bukkit.getPluginManager().callEvent(e1);
       				});
       			}
       			List<String> prestigeCommands = plugin.getGlobalStorage().getStringListData("PrestigeOptions.prestige-cmds");
       			if(!prestigeCommands.isEmpty()) {
       	           prestigeCommands.forEach(cmd -> {
       	        	   if(cmd.startsWith("[rankpermissions]")) {
       	        		   getAPI().allRankAddPermissions.forEach(permission -> {
       	        		   plugin.perm.delPermission(p, permission);
       	        		   });
       	        	   } else if (cmd.startsWith("[prestigepermissions]")) {
       	        		   getAPI().allPrestigeAddPermissions.forEach(permission -> {
       	        			   plugin.perm.delPermission(p, permission);
       	        		   });
       	        	   } else if (cmd.startsWith("[rebirthpermissions]")) {
       	        		   getAPI().allRebirthAddPermissions.forEach(permission -> {
       	        			   plugin.perm.delPermission(p, permission);
       	        		   });
       	        	   } else {
       	        		   plugin.executeCommand(p, cmd);
       	        	   }
       	           });
       			}
        	}
                multiThreadSet.remove(name);
        }, 0, 1));
	}

	/**
	 * @deprecated useless just use executeOnAsyncMultiThreadedQueue(..)
	 */
	@Override
	public void executeOnSyncMultiThreadedQueue(Player player) {
		Player p = player;
		String name = p.getName();
		UUID uuid = p.getUniqueId();
		if(isProcessing(name)) {
			p.sendMessage(getAPI().cp("prestigemax-is-on", p));
			return;
		}
		RankPath rankPath = getAPI().getPlayerRankPath(uuid);
		if(!getAPI().isLastRank(rankPath) && !getAPI().hasAllowPrestige(rankPath)) {
			if(getAPI().canRankup(p)) {
				getAPI().rankupMax(p);
			} else {
				//p.sendMessage(noPrestigeMessage);
			}
			return;
		}
		AtomicDouble takenBalance = new AtomicDouble(0.0);
		AtomicInteger prestigeTimes = new AtomicInteger(0);
		String prestigeName = getAPI().getPlayerPrestige(uuid);
		if(prestigeName == null) {
			if(getAPI().canPrestige(p)) {
			getAPI().getPrestigeAPI().prestige(p);
			prestigeName = getAPI().getFirstPrestige();
			} else {
				getAPI().getPrestigeAPI().prestige(p);
				getProcessingPlayers().remove(name);
				return;
			}
		}
		IPrestigeDataHandler prestige = getAPI().getPrestige(prestigeName);
		PrePrestigeMaxEvent e = new PrePrestigeMaxEvent(p, prestige);
		plugin.scheduler.runTask(plugin, () -> Bukkit.getPluginManager().callEvent(e));
		if(e.isCancelled()) {
			getProcessingPlayers().remove(name);
			return;
		}
		AccessibleString prestigeFrom = new AccessibleString(prestigeName);
		AccessibleString finalPrestige = new AccessibleString();
		String nextPrestigeName = prestige.getNextPrestigeName();
		double playerBalance = getAPI().getPlayerMoney(p);
		if(nextPrestigeName.equals("LASTPRESTIGE")) {
			getProcessingPlayers().remove(name);
			lastPrestigeMessage.forEach(p::sendMessage);
			return;
		}
        IPrestigeDataHandler nextPrestige = getAPI().getPrestige(nextPrestigeName);
		List<String> prestigesCollection = getAPI().getPrestigeStorage().getNativeLinkedPrestigesCollection();
		Map<String, String> stringRequirements = nextPrestige.getStringRequirements();
		Map<String, Double> numberRequirements = nextPrestige.getNumberRequirements();
		List<String> customRequirementMessage = nextPrestige.getCustomRequirementMessage();
		String rebirthName = getAPI().getPlayerRebirth(uuid);
		String nextPrestigeDisplay = nextPrestige.getDisplayName();
		double nextPrestigeCost = getAPI().getIncreasedPrestigeCost(rebirthName, nextPrestigeName);
		String nextPrestigeCostFormatted = getAPI().formatBalance(nextPrestigeCost);
		String prestigeMessage = plugin.getString(getAPI().g("prestige"), name);
        if(nextPrestigeCost > playerBalance) {
        	notEnoughMoneyMessage.forEach(messageLine -> {
        	    messageLine = plugin.getString(messageLine, name)
        		.replace("%player%", name)
        		.replace("%nextprestige%", nextPrestigeName)
        		.replace("%nextprestige_display%", nextPrestigeDisplay)
        		.replace("%nextprestige_cost%", String.valueOf(nextPrestigeCost))
        		.replace("%nextprestige_cost_formatted%", nextPrestigeCostFormatted);
        	    p.sendMessage(messageLine);
        	});
        	return;
        }
        if(!checkRequirements(stringRequirements, numberRequirements, customRequirementMessage, p, false))
        	return;
        getProcessingPlayers().add(name);
        int currentPrestigeIndex = prestigesCollection.indexOf(prestigeName);
        AtomicInteger increment = new AtomicInteger(currentPrestigeIndex-1);
        int size = prestigesCollection.size();
        AccessibleBukkitTask accessibleTask = new AccessibleBukkitTask();
        accessibleTask.set(plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
        	if(multiThreadSet.contains(name)) {
        		return;
        	}
        	multiThreadSet.add(name);
        	    int i = increment.get();
        		if(i >= size) {
        			if(!AccessibleString.isNullOrEmpty(finalPrestige)) {
                		IPrestigeDataHandler finalData = getAPI().getPrestige(finalPrestige.getString());
                		List<String> actionbarMessages = finalData.getActionbarMessages();
                		int actionbarInterval = finalData.getActionbarInterval();
                		String finalPrestigeName = finalData.getName();
                		String finalPrestigeDisplay = getAPI().c(finalData.getDisplayName());
                		if(actionbarMessages != null && !actionbarMessages.isEmpty()) {
                			List<String> replaced = new ArrayList<>();
                			actionbarMessages.forEach(messageLine -> {
                				replaced.add(messageLine.replace("%nextprestige%", finalPrestigeName).replace("%nextprestige_display%", finalPrestigeDisplay));
                			});
                			plugin.animateActionbar(p, actionbarInterval, replaced);
                		}
                		AsyncPrestigeMaxEvent event = new AsyncPrestigeMaxEvent(p, prestigeFrom.getString(), finalPrestigeName, prestigeTimes.get(), takenBalance.get());
                		Bukkit.getPluginManager().callEvent(event);
                	}
                	getProcessingPlayers().remove(name);
        			accessibleTask.cancel();
        			multiThreadSet.remove(name);
        		}
        		increment.incrementAndGet();
        		i = increment.get();
        		RankPath loopRankPath = getAPI().getPlayerRankPath(uuid);
           		if(!getAPI().isLastRank(loopRankPath) && !getAPI().hasAllowPrestige(loopRankPath)) {
        			if(getAPI().canRankup(p)) {
        				getAPI().rankupMax(p);
        			}
        			return;
        		}
        		String loopPrestigeName = prestigesCollection.get(i);
        		double loopBalance = getAPI().getPlayerMoney(p);
        		
        		IPrestigeDataHandler loopPrestige = getAPI().getPrestige(loopPrestigeName);
        		String loopNextPrestigeName = loopPrestige.getNextPrestigeName();
        		if(loopNextPrestigeName.equals("LASTPRESTIGE")) {
        			lastPrestigeMessage.forEach(p::sendMessage);
        			accessibleTask.cancel();
        			multiThreadSet.remove(name);
        		}
        		IPrestigeDataHandler loopNextPrestige = getAPI().getPrestige(loopNextPrestigeName);
        		double loopNextPrestigeCost = getAPI().getIncreasedPrestigeCost(rebirthName, loopNextPrestigeName);
        		takenBalance.addAndGet(loopNextPrestigeCost);
        		String loopNextPrestigeCostFormatted = getAPI().formatBalance(loopNextPrestigeCost);
        		Map<String, String> loopStringRequirements = loopNextPrestige.getStringRequirements();
        		Map<String, Double> loopNumberRequirements = loopNextPrestige.getNumberRequirements();
        		List<String> loopCustomRequirementMessage = loopNextPrestige.getCustomRequirementMessage();
        		String loopNextPrestigeDisplay = getAPI().c(loopNextPrestige.getDisplayName());
        		if(loopNextPrestigeCost > loopBalance) {
                	notEnoughMoneyMessage.forEach(messageLine -> {
                		p.sendMessage(plugin.getString(messageLine, name)
                		.replace("%player%", name)
                		.replace("%nextprestige%", loopNextPrestigeName)
                		.replace("%nextprestige_display%", loopNextPrestigeDisplay)
                		.replace("%nextprestige_cost%", String.valueOf(loopNextPrestigeCost))
                		.replace("%nextprestige_cost_formatted%", loopNextPrestigeCostFormatted));
                	});
                	accessibleTask.cancel();
                	multiThreadSet.remove(name);
        		}
        		if(!checkRequirements(loopStringRequirements, loopNumberRequirements, loopCustomRequirementMessage, p, false)) {
        			accessibleTask.cancel();
        			multiThreadSet.remove(name);
        		}
        		p.sendMessage(prestigeMessage
        				.replace("%nextprestige%", loopNextPrestigeName)
        				.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        				);
        		List<String> loopNextPrestigeCommands = loopNextPrestige.getPrestigeCommands();
        		if(loopNextPrestigeCommands != null && !loopNextPrestigeCommands.isEmpty()) {
        			plugin.getServer().getScheduler().runTask(plugin, () -> {
        				plugin.executeCommands(p, loopNextPrestigeCommands);
        			});
        		}
        		List<String> loopNextPrestigeAddPermissionList = loopNextPrestige.getAddPermissionList();
        		if(loopNextPrestigeAddPermissionList != null && !loopNextPrestigeAddPermissionList.isEmpty()) {
        			loopNextPrestigeAddPermissionList.forEach(permission -> {
        				getAPI().getPermissionManager().addPermission(p, permission
        						.replace("%player%", name)
        						.replace("%prestige%", loopPrestigeName)
        						.replace("%nextprestige%", loopNextPrestigeName)
        						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        						);
        			});
        		}
        		List<String> loopNextPrestigeDelPermissionList = loopNextPrestige.getDelPermissionList();
        		if(loopNextPrestigeDelPermissionList != null && !loopNextPrestigeDelPermissionList.isEmpty()) {
        			loopNextPrestigeDelPermissionList.forEach(permission -> {
        				getAPI().getPermissionManager().delPermission(p, permission
        						.replace("%player%", name)
        						.replace("%prestige%", loopPrestigeName)
        						.replace("%nextprestige%", loopNextPrestigeName)
        						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        						);
        			});
        		}
        		List<String> loopNextPrestigeBroadcast = loopNextPrestige.getBroadcast();
        		if(loopNextPrestigeBroadcast != null && !loopNextPrestigeBroadcast.isEmpty()) {
        			loopNextPrestigeBroadcast.forEach(broadcastMessage -> {
        				Bukkit.broadcastMessage(plugin.getString(broadcastMessage, name)
        				.replace("%player%", name)
						.replace("%prestige%", loopPrestigeName)
						.replace("%nextprestige%", loopNextPrestigeName)
						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
						);
        			});
        		}
        		List<String> loopNextPrestigeMessage = loopNextPrestige.getMsg();
        		if(loopNextPrestigeMessage != null && !loopNextPrestigeMessage.isEmpty()) {
        			loopNextPrestigeMessage.forEach(messageLine -> {
        				p.sendMessage(plugin.getString(messageLine, name)
        						.replace("%player%", name)
        						.replace("%prestige%", loopPrestigeName)
        						.replace("%nextprestige%", loopNextPrestigeName)
        						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        						);
        			});
        		}
        		List<String> loopNextPrestigeActions = loopNextPrestige.getActions();
        		if(getAPI().hasActionUtilEnabled() && loopNextPrestigeActions != null && !loopNextPrestigeActions.isEmpty()) {
        			ActionUtil.executeActions(p, loopNextPrestigeActions);
        		}
        		Map<String, Double> chances = new HashMap<>();
        		PrestigeRandomCommands loopPrestigeRandomCommands = loopNextPrestige.getRandomCommandsManager();
       			if(loopPrestigeRandomCommands != null && loopPrestigeRandomCommands.getRandomCommandsMap() != null) {
       			  for(String section : loopPrestigeRandomCommands.getRandomCommandsMap().keySet()) {
       				  Double chance = loopPrestigeRandomCommands.getChance(section);
       				  chances.put(section, chance);
       			  }
       			  String randomSection = getAPI().getNumberAPI().getChanceFromWeightedMap(chances);
       			  if(loopPrestigeRandomCommands.getCommands(randomSection) != null) {
       			    List<String> commands = loopPrestigeRandomCommands.getCommands(randomSection);
       			    ConsoleCommandSender ccs = Bukkit.getConsoleSender();
       			    for(String singleCommand : commands) {
       				    String replacedCommand = getAPI().cp(singleCommand.replace("%player%", name).replace("%nextprestige%", loopNextPrestigeName), p);
       				    plugin.getServer().getScheduler().runTask(plugin, () -> {
       				      plugin.getServer().dispatchCommand(ccs, replacedCommand);
       				    });
       			    }
       			  }
       			}
       			getAPI().getEconomy().withdrawPlayer(p, loopNextPrestigeCost);
       			finalPrestige.setString(loopNextPrestigeName);
       			getAPI().setPlayerPrestige(uuid, loopNextPrestigeName);
       			prestigeTimes.addAndGet(1);
                multiThreadSet.remove(name);
        }, 0, 1));
	}

	@Override
	public void executeOnAsyncQueue(Player player) {
		Player p = player;
		String name = p.getName();
		UUID uuid = p.getUniqueId();
		if(isProcessing(name)) {
			p.sendMessage(getAPI().cp("prestigemax-is-on", p));
			return;
		}
		RankPath rankPath = getAPI().getPlayerRankPath(uuid);
		if(!getAPI().isLastRank(rankPath) && !getAPI().hasAllowPrestige(rankPath)) {
			if(getAPI().canRankup(p)) {
				getAPI().rankupMax(p);
			} else {
				//p.sendMessage(noPrestigeMessage);
			}
			return;
		}
		AtomicDouble takenBalance = new AtomicDouble(0.0);
		AtomicInteger prestigeTimes = new AtomicInteger(0);
		String prestigeName = getAPI().getPlayerPrestige(uuid);
		if(prestigeName == null) {
			if(getAPI().canPrestige(p)) {
			getAPI().getPrestigeAPI().prestige(p);
			prestigeName = getAPI().getFirstPrestige();
			} else {
				getAPI().getPrestigeAPI().prestige(p);
				getProcessingPlayers().remove(name);
				return;
			}
		}
		IPrestigeDataHandler prestige = getAPI().getPrestige(prestigeName);
		PrePrestigeMaxEvent e = new PrePrestigeMaxEvent(p, prestige);
		plugin.scheduler.runTask(plugin, () -> Bukkit.getPluginManager().callEvent(e));
		if(e.isCancelled()) {
			getProcessingPlayers().remove(name);
			return;
		}
		AccessibleString prestigeFrom = new AccessibleString(prestigeName);
		AccessibleString finalPrestige = new AccessibleString();
		String nextPrestigeName = prestige.getNextPrestigeName();
		double playerBalance = getAPI().getPlayerMoney(p);
		if(nextPrestigeName.equals("LASTPRESTIGE")) {
			getProcessingPlayers().remove(name);
			lastPrestigeMessage.forEach(p::sendMessage);
			return;
		}
        IPrestigeDataHandler nextPrestige = getAPI().getPrestige(nextPrestigeName);
		List<String> prestigesCollection = getAPI().getPrestigeStorage().getNativeLinkedPrestigesCollection();
		Map<String, String> stringRequirements = nextPrestige.getStringRequirements();
		Map<String, Double> numberRequirements = nextPrestige.getNumberRequirements();
		List<String> customRequirementMessage = nextPrestige.getCustomRequirementMessage();
		String rebirthName = getAPI().getPlayerRebirth(uuid);
		String nextPrestigeDisplay = nextPrestige.getDisplayName();
		double nextPrestigeCost = getAPI().getIncreasedPrestigeCost(rebirthName, nextPrestigeName);
		String nextPrestigeCostFormatted = getAPI().formatBalance(nextPrestigeCost);
		String prestigeMessage = plugin.getString(getAPI().g("prestige"), name);
        if(nextPrestigeCost > playerBalance) {
        	notEnoughMoneyMessage.forEach(messageLine -> {
        	    messageLine = plugin.getString(messageLine, name)
        		.replace("%player%", name)
        		.replace("%nextprestige%", nextPrestigeName)
        		.replace("%nextprestige_display%", nextPrestigeDisplay)
        		.replace("%nextprestige_cost%", String.valueOf(nextPrestigeCost))
        		.replace("%nextprestige_cost_formatted%", nextPrestigeCostFormatted);
        	    p.sendMessage(messageLine);
        	});
        	return;
        }
        if(!checkRequirements(stringRequirements, numberRequirements, customRequirementMessage, p, false))
        	return;
        getProcessingPlayers().add(name);
        int currentPrestigeIndex = prestigesCollection.indexOf(prestigeName);
        AtomicInteger increment = new AtomicInteger(currentPrestigeIndex-1);
        int size = prestigesCollection.size();
        AccessibleBukkitTask accessibleTask = new AccessibleBukkitTask();
        accessibleTask.set(plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
        	plugin.getTaskChainFactory().newSharedChain("prestigemax").async(() -> {
        		if(!plugin.getPlayerStorage().getPlayerData().containsKey(uuid.toString())) {
        			getProcessingPlayers().remove(name);
        			accessibleTask.cancel();
        		}
        	    int i = increment.get();
        		if(i >= size) {
        			if(!AccessibleString.isNullOrEmpty(finalPrestige)) {
                		IPrestigeDataHandler finalData = getAPI().getPrestige(finalPrestige.getString());
                		List<String> actionbarMessages = finalData.getActionbarMessages();
                		int actionbarInterval = finalData.getActionbarInterval();
                		String finalPrestigeName = finalData.getName();
                		String finalPrestigeDisplay = getAPI().c(finalData.getDisplayName());
                		if(actionbarMessages != null && !actionbarMessages.isEmpty()) {
                			List<String> replaced = new ArrayList<>();
                			actionbarMessages.forEach(messageLine -> {
                				replaced.add(messageLine.replace("%nextprestige%", finalPrestigeName).replace("%nextprestige_display%", finalPrestigeDisplay));
                			});
                			plugin.animateActionbar(p, actionbarInterval, replaced);
                		}
                		AsyncPrestigeMaxEvent event = new AsyncPrestigeMaxEvent(p, prestigeFrom.getString(), finalPrestigeName, prestigeTimes.get(), takenBalance.get());
                		Bukkit.getPluginManager().callEvent(event);
                	}
                	getProcessingPlayers().remove(name);
        			accessibleTask.cancel();
        		}
        		increment.incrementAndGet();
        		i = increment.get();
        		RankPath loopRankPath = getAPI().getPlayerRankPath(uuid);
           		if(!getAPI().isLastRank(loopRankPath) && !getAPI().hasAllowPrestige(loopRankPath)) {
        			if(getAPI().canRankup(p)) {
        				getAPI().rankupMax(p);
        			}
        			return;
        		}
        		String loopPrestigeName = prestigesCollection.get(i);
        		double loopBalance = getAPI().getPlayerMoney(p);
        		
        		IPrestigeDataHandler loopPrestige = getAPI().getPrestige(loopPrestigeName);
        		String loopNextPrestigeName = loopPrestige.getNextPrestigeName();
        		if(loopNextPrestigeName.equals("LASTPRESTIGE")) {
        			lastPrestigeMessage.forEach(p::sendMessage);
        			accessibleTask.cancel();
        		}
        		IPrestigeDataHandler loopNextPrestige = getAPI().getPrestige(loopNextPrestigeName);
        		double loopNextPrestigeCost = getAPI().getIncreasedPrestigeCost(rebirthName, loopNextPrestigeName);
        		takenBalance.addAndGet(loopNextPrestigeCost);
        		String loopNextPrestigeCostFormatted = getAPI().formatBalance(loopNextPrestigeCost);
        		Map<String, String> loopStringRequirements = loopNextPrestige.getStringRequirements();
        		Map<String, Double> loopNumberRequirements = loopNextPrestige.getNumberRequirements();
        		List<String> loopCustomRequirementMessage = loopNextPrestige.getCustomRequirementMessage();
        		String loopNextPrestigeDisplay = getAPI().c(loopNextPrestige.getDisplayName());
        		if(loopNextPrestigeCost > loopBalance) {
                	notEnoughMoneyMessage.forEach(messageLine -> {
                		p.sendMessage(plugin.getString(messageLine, name)
                		.replace("%player%", name)
                		.replace("%nextprestige%", loopNextPrestigeName)
                		.replace("%nextprestige_display%", loopNextPrestigeDisplay)
                		.replace("%nextprestige_cost%", String.valueOf(loopNextPrestigeCost))
                		.replace("%nextprestige_cost_formatted%", loopNextPrestigeCostFormatted));
                	});
                	accessibleTask.cancel();
        		}
        		if(!checkRequirements(loopStringRequirements, loopNumberRequirements, loopCustomRequirementMessage, p, false))
        			accessibleTask.cancel();
        		p.sendMessage(prestigeMessage
        				.replace("%nextprestige%", loopNextPrestigeName)
        				.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        				);
        		List<String> loopNextPrestigeCommands = loopNextPrestige.getPrestigeCommands();
        		if(loopNextPrestigeCommands != null && !loopNextPrestigeCommands.isEmpty()) {
        			plugin.getServer().getScheduler().runTask(plugin, () -> {
        				plugin.executeCommands(p, loopNextPrestigeCommands);
        			});
        		}
        		List<String> loopNextPrestigeAddPermissionList = loopNextPrestige.getAddPermissionList();
        		if(loopNextPrestigeAddPermissionList != null && !loopNextPrestigeAddPermissionList.isEmpty()) {
        			loopNextPrestigeAddPermissionList.forEach(permission -> {
        				getAPI().getPermissionManager().addPermission(p, permission
        						.replace("%player%", name)
        						.replace("%prestige%", loopPrestigeName)
        						.replace("%nextprestige%", loopNextPrestigeName)
        						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        						);
        			});
        		}
        		List<String> loopNextPrestigeDelPermissionList = loopNextPrestige.getDelPermissionList();
        		if(loopNextPrestigeDelPermissionList != null && !loopNextPrestigeDelPermissionList.isEmpty()) {
        			loopNextPrestigeDelPermissionList.forEach(permission -> {
        				getAPI().getPermissionManager().delPermission(p, permission
        						.replace("%player%", name)
        						.replace("%prestige%", loopPrestigeName)
        						.replace("%nextprestige%", loopNextPrestigeName)
        						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        						);
        			});
        		}
        		List<String> loopNextPrestigeBroadcast = loopNextPrestige.getBroadcast();
        		if(loopNextPrestigeBroadcast != null && !loopNextPrestigeBroadcast.isEmpty()) {
        			loopNextPrestigeBroadcast.forEach(broadcastMessage -> {
        				Bukkit.broadcastMessage(plugin.getString(broadcastMessage, name)
        				.replace("%player%", name)
						.replace("%prestige%", loopPrestigeName)
						.replace("%nextprestige%", loopNextPrestigeName)
						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
						);
        			});
        		}
        		List<String> loopNextPrestigeMessage = loopNextPrestige.getMsg();
        		if(loopNextPrestigeMessage != null && !loopNextPrestigeMessage.isEmpty()) {
        			loopNextPrestigeMessage.forEach(messageLine -> {
        				p.sendMessage(plugin.getString(messageLine, name)
        						.replace("%player%", name)
        						.replace("%prestige%", loopPrestigeName)
        						.replace("%nextprestige%", loopNextPrestigeName)
        						.replace("%nextprestige_display%", loopNextPrestigeDisplay)
        						);
        			});
        		}
        		List<String> loopNextPrestigeActions = loopNextPrestige.getActions();
        		if(getAPI().hasActionUtilEnabled() && loopNextPrestigeActions != null && !loopNextPrestigeActions.isEmpty()) {
        			ActionUtil.executeActions(p, loopNextPrestigeActions);
        		}
        		Map<String, Double> chances = new HashMap<>();
        		PrestigeRandomCommands loopPrestigeRandomCommands = loopNextPrestige.getRandomCommandsManager();
       			if(loopPrestigeRandomCommands != null && loopPrestigeRandomCommands.getRandomCommandsMap() != null) {
       			  for(String section : loopPrestigeRandomCommands.getRandomCommandsMap().keySet()) {
       				  Double chance = loopPrestigeRandomCommands.getChance(section);
       				  chances.put(section, chance);
       			  }
       			  String randomSection = getAPI().getNumberAPI().getChanceFromWeightedMap(chances);
       			  if(loopPrestigeRandomCommands.getCommands(randomSection) != null) {
       			    List<String> commands = loopPrestigeRandomCommands.getCommands(randomSection);
       			    ConsoleCommandSender ccs = Bukkit.getConsoleSender();
       			    for(String singleCommand : commands) {
       				    String replacedCommand = getAPI().cp(singleCommand.replace("%player%", name).replace("%nextprestige%", loopNextPrestigeName), p);
       				    plugin.getServer().getScheduler().runTask(plugin, () -> {
       				      plugin.getServer().dispatchCommand(ccs, replacedCommand);
       				    });
       			    }
       			  }
       			}
       			getAPI().getEconomy().withdrawPlayer(p, loopNextPrestigeCost);
       			finalPrestige.setString(loopNextPrestigeName);
       			getAPI().setPlayerPrestige(uuid, loopNextPrestigeName);
       			prestigeTimes.addAndGet(1);
 
        	}).execute();
        }, 0, 1));
 	}
	
	@Override
	public void executeFinal(AccessibleBukkitTask accessibleBukkitTask, Player player, String name,
			AccessibleString finalPrestige, AccessibleString prestigeFrom, AtomicInteger prestigeTimes,
			AtomicDouble takenBalance) {
		Player p = player;
		if(!AccessibleString.isNullOrEmpty(finalPrestige)) {
    		IPrestigeDataHandler finalData = getAPI().getPrestige(finalPrestige.getString());
    		List<String> actionbarMessages = finalData.getActionbarMessages();
    		int actionbarInterval = finalData.getActionbarInterval();
    		String finalPrestigeName = finalData.getName();
    		String finalPrestigeDisplay = getAPI().c(finalData.getDisplayName());
    		if(actionbarMessages != null && !actionbarMessages.isEmpty()) {
    			List<String> replaced = new ArrayList<>();
    			actionbarMessages.forEach(messageLine -> {
    				replaced.add(messageLine.replace("%nextprestige%", finalPrestigeName).replace("%nextprestige_display%", finalPrestigeDisplay));
    			});
    			plugin.animateActionbar(p, actionbarInterval, replaced);
    		}
    		String nextPrestigeSoundName = plugin.getGlobalStorage().getStringData("Options.prestigesound-name");
    		if(!nextPrestigeSoundName.isEmpty() && nextPrestigeSoundName.length() > 1) {
    			float nextPrestigeSoundPitch = (float)plugin.getGlobalStorage().getDoubleData("Options.prestigesound-pitch");
    		    float nextPrestigeSoundVolume = (float)plugin.getGlobalStorage().getDoubleData("Options.prestigesound-volume");
    			p.playSound(p.getLocation(), Sounds.valueOf(nextPrestigeSoundName).bukkitSound(), nextPrestigeSoundVolume, nextPrestigeSoundPitch);
    		}
    		boolean nextPrestigeHologramIsEnable = plugin.getGlobalStorage().getBooleanData("Holograms.prestige.enable");
    		if(plugin.hasHolographicDisplays && nextPrestigeHologramIsEnable) {
    			int nextPrestigeHologramRemoveTime = plugin.getGlobalStorage().getIntegerData("Holograms.prestige.remove-time");
    			int nextPrestigeHologramHeight = plugin.getGlobalStorage().getIntegerData("Holograms.prestige.height");
    			List<String> nextPrestigeHologramFormat = plugin.getGlobalStorage().getStringListData("Holograms.prestige.format");
    			getAPI().getPrestigeAPI().spawnHologram(nextPrestigeHologramFormat, nextPrestigeHologramRemoveTime, nextPrestigeHologramHeight, p, finalPrestigeName);
    		}
    		AsyncPrestigeMaxEvent event = new AsyncPrestigeMaxEvent(p, prestigeFrom.getString(), finalPrestigeName, prestigeTimes.get(), takenBalance.get());
    		Bukkit.getPluginManager().callEvent(event);
    	}
    	getProcessingPlayers().remove(name);
		accessibleBukkitTask.cancel();
		multiThreadSet.remove(name);	
	}

	@Override
	public void removeProcessingPlayer(String name) {
		getProcessingPlayers().remove(name);
	}
	
}
