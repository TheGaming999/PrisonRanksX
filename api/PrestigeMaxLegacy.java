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

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.google.common.util.concurrent.AtomicDouble;

import io.samdev.actionutil.ActionUtil;
import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.IPrestigeDataHandler;
import me.prisonranksx.data.PrestigeRandomCommands;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.events.PrePrestigeMaxEvent;
import me.prisonranksx.events.AsyncPrestigeMaxEvent;
import me.prisonranksx.utils.AccessibleBukkitTask;
import me.prisonranksx.utils.AccessibleString;
import me.prisonranksx.utils.XUUID;

public class PrestigeMaxLegacy implements IPrestigeMax {

	private PrisonRanksX plugin;
	private PRXAPI api;
	private Set<String> processingPlayers;
	private final List<String> lastPrestigeMessage;
	private final List<String> notEnoughMoneyMessage;
	private final String noPrestigeMessage;
	
	public PrestigeMaxLegacy(PrisonRanksX plugin) {
		this.plugin = plugin;
		this.api = this.plugin.prxAPI;
		this.processingPlayers = new HashSet<>();
		this.lastPrestigeMessage = this.api.cl(this.api.h("lastprestige"));
		this.notEnoughMoneyMessage = this.api.cl(this.api.h("prestige-notenoughmoney"));
		this.noPrestigeMessage = this.api.g("noprestige");
	}
	
	@Override
	public void execute(Player player) {
		Player p = player;
		String name = p.getName();
		UUID uuid = XUUID.tryNameConvert(name);
		if(isProcessing(name)) {
			p.sendMessage(getAPI().cp("prestigemax-is-on", p));
			return;
		}
		RankPath rankPath = getAPI().getPlayerRankPath(uuid);
		if(!getAPI().isLastRank(rankPath) && !getAPI().hasAllowPrestige(rankPath)) {
			p.sendMessage(noPrestigeMessage);
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
		double playerBalance = getAPI().getPlayerMoney(name);
		if(nextPrestigeName.equals("LASTPRESTIGE")) {
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
        		p.sendMessage(plugin.getString(messageLine, name)
        		.replace("%player%", name)
        		.replace("%nextprestige%", nextPrestigeName)
        		.replace("%nextprestige_display%", nextPrestigeDisplay)
        		.replace("%nextprestige_cost%", String.valueOf(nextPrestigeCost))
        		.replace("%nextprestige_cost_formatted%", nextPrestigeCostFormatted));
        	});
        	return;
        }
        if(!checkRequirements(stringRequirements, numberRequirements, customRequirementMessage, p, false))
        	return;
        int currentPrestigeIndex = prestigesCollection.indexOf(prestigeName);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
        	for(int i = currentPrestigeIndex ; i < prestigesCollection.size(); i++) {
        		String loopPrestigeName = prestigesCollection.get(i);
        		double loopBalance = getAPI().getPlayerMoney(name);
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
        				getAPI().getPermissionManager().addPermission(name, permission
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
        				getAPI().getPermissionManager().delPermission(name, permission
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
       			getAPI().getEconomy().withdrawPlayer(name, loopNextPrestigeCost);
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
        		plugin.getServer().getScheduler().runTask(plugin, () -> {
        		AsyncPrestigeMaxEvent event = new AsyncPrestigeMaxEvent(p, prestigeFrom.getString(), finalPrestigeName, prestigeTimes.get(), takenBalance.get());
        		Bukkit.getPluginManager().callEvent(event);
        		});
        	}
        	getProcessingPlayers().remove(name);
        });
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
			getAPI().prestigeLegacy(p);
			prestigeName = getAPI().getPlayerPrestige(uuid);
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
		double playerBalance = getAPI().getPlayerMoney(name);
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
        		double loopBalance = getAPI().getPlayerMoney(name);
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
        				getAPI().getPermissionManager().addPermission(name, permission
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
        				getAPI().getPermissionManager().delPermission(name, permission
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
       			getAPI().getEconomy().withdrawPlayer(name, loopNextPrestigeCost);
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
        		plugin.getServer().getScheduler().runTask(plugin, () -> {
        		AsyncPrestigeMaxEvent event = new AsyncPrestigeMaxEvent(p, prestigeFrom.getString(), finalPrestigeName, prestigeTimes.get(), takenBalance.get());
        		Bukkit.getPluginManager().callEvent(event);
        		});
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

	@Override
	public void executeOnAsyncQueue(Player player) {
		
	}

	@Override
	public void executeOnAsyncMultiThreadedQueue(Player player) {

	}

	@Override
	public void executeOnSyncMultiThreadedQueue(Player player) {

	}

	@Override
	public void executeFinal(AccessibleBukkitTask accessibleBukkitTask, Player player, String name,
			AccessibleString finalPrestige, AccessibleString prestigeFrom, AtomicInteger prestigeTimes,
			AtomicDouble takenBalance) {
		
	}

}
