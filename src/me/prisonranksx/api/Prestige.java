package me.prisonranksx.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import io.samdev.actionutil.ActionUtil;
import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.PrestigeRandomCommands;
import me.prisonranksx.events.AsyncAutoPrestigeEvent;
import me.prisonranksx.events.PrestigeUpdateCause;
import me.prisonranksx.events.RankUpdateCause;
import me.prisonranksx.events.PrestigeUpdateEvent;
import me.prisonranksx.events.RankUpdateEvent;
import me.prisonranksx.utils.CompatibleSound.Sounds;

public class Prestige {
	
	private boolean isAutoPrestigeTaskEnabled;
	private int autoPrestigeDelay;
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	private PRXAPI prxAPI;
	private Set<String> taskedPlayers;
	
	public Prestige() {
		this.prxAPI = main.prxAPI;
		this.autoPrestigeDelay = prxAPI.numberAPI.limitInverse(main.globalStorage.getIntegerData("Options.autoprestige-delay"), 0);
		this.taskedPlayers = new HashSet<>();
	}
	
	private void startAutoPrestigeTask() {
		if(isAutoPrestigeTaskEnabled) {
			return;
		}
		isAutoPrestigeTaskEnabled = true;
		Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
			for(String playerName : prxAPI.autoPrestigePlayers) {
				this.prestige(Bukkit.getPlayer(playerName), true);
			}
		}, autoPrestigeDelay, autoPrestigeDelay);
		
	}
	
	public void autoPrestige(Player player) {
		Player p = player;
		String name = p.getName();
		if(prxAPI.isAutoPrestigeEnabled(p)) {
			prxAPI.autoPrestigePlayers.remove(name);
			if(prxAPI.g("autoprestige-disabled") != null && !prxAPI.g("autoprestige-disabled").isEmpty()) {
				p.sendMessage(prxAPI.g("autoprestige-disabled"));
			}
		} else {
			prxAPI.autoPrestigePlayers.add(name);
			startAutoPrestigeTask();
			if(prxAPI.g("autoprestige-enabled") != null && !prxAPI.g("autoprestige-enabled").isEmpty()) {
				p.sendMessage(prxAPI.g("autoprestige-enabled"));
			}
		}
	}
	
	
	public void autoPrestige(Player player, boolean enable) {
		Player p = player;
		String name = p.getName();
		if(prxAPI.isAutoPrestigeEnabled(p)) {
			if(!enable) {
			prxAPI.autoPrestigePlayers.remove(name);
			if(prxAPI.g("autoprestige-disabled") != null && !prxAPI.g("autoprestige-disabled").isEmpty()) {
				p.sendMessage(prxAPI.g("autoprestige-disabled"));
			}
			} else {
				if(prxAPI.g("autoprestige-enabled") != null && !prxAPI.g("autoprestige-enabled").isEmpty()) {
					p.sendMessage(prxAPI.g("autoprestige-enabled"));
				}
			}
		} else {
			if(!enable) {
				if(prxAPI.g("autoprestige-disabled") != null && !prxAPI.g("autoprestige-disabled").isEmpty()) {
					p.sendMessage(prxAPI.g("autoprestige-disabled"));
				}
				return;
			}
			prxAPI.autoPrestigePlayers.add(name);
			startAutoPrestigeTask();
			if(prxAPI.g("autoprestige-enabled") != null && !prxAPI.g("autoprestige-enabled").isEmpty()) {
				p.sendMessage(prxAPI.g("autoprestige-enabled"));
			}
		}
	}
	
	public boolean prestige(final Player player) {
		String name = player.getName();
		if(prxAPI.taskedPlayers.contains(name)) {
			if(prxAPI.g("commandspam") != null && !prxAPI.g("commandspam").isEmpty()) {
			player.sendMessage(prxAPI.g("commandspam"));
			}
			return false;
		}	
		prxAPI.taskedPlayers.add(name);

		Player p = player;
		String prestige = prxAPI.getPlayerNextPrestige(p);
		if(!p.hasPermission(main.prestigeCommand.getPermission()) && !p.hasPermission("*")) {
			if(prxAPI.g("nopermission") != null && !prxAPI.g("nopermission").isEmpty()) {	
			p.sendMessage(prxAPI.g("nopermission"));
			}
			prxAPI.taskedPlayers.remove(name);
			return false;
		}
		if(prestige == null) {
			if(prxAPI.h("lastprestige") != null && !prxAPI.h("lastprestige").isEmpty()) {
			for(String line : prxAPI.h("lastprestige")) {
				p.sendMessage(prxAPI.c(line));
			}
			}
			prxAPI.taskedPlayers.remove(name);
			return false;
		}

       
		if(!prxAPI.isLastRank(p) && !main.rankStorage.isAllowPrestige(prxAPI.getPlayerRankPath(p))) {
			if(prxAPI.g("noprestige") != null && !prxAPI.g("noprestige").isEmpty()) {
				p.sendMessage(prxAPI.g("noprestige"));
			}
			prxAPI.taskedPlayers.remove(name);
			return false;
		}
		Map<String, String> stringRequirements = prxAPI.getPrestigeStringRequirements(prestige);
		Map<String, Double> numberRequirements = prxAPI.getPrestigeNumberRequirements(prestige);
		List<String> customRequirementMessage = prxAPI.getPrestigeCustomRequirementMessage(prestige);
		if(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p) > prxAPI.getPlayerMoney(p)) {
			if(prxAPI.h("prestige-notenoughmoney") != null && !prxAPI.h("prestige-notenoughmoney").isEmpty()) {
			for(String line : prxAPI.h("prestige-notenoughmoney")) {
				p.sendMessage(prxAPI.c(line)
						.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p)).replace("%nextprestige_display%", prxAPI.getPlayerNextPrestigeDisplayNoFallback(p))
						.replace("%nextprestige_cost%", prxAPI.s(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p))).replace("%nextprestige_cost_formatted%", prxAPI.formatBalance(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p))));
			}
			}
			prxAPI.taskedPlayers.remove(name);
			return false;
		}
		boolean failedRequirements = false;
		if(stringRequirements != null) {
			for(Entry<String, String> entry : stringRequirements.entrySet()) {
				String placeholder = prxAPI.cp(entry.getKey(), p);
				String value = prxAPI.cp(entry.getValue(), p);
				if(!placeholder.equalsIgnoreCase(value)) {
					failedRequirements = true;
				}
			}
		}
		if(numberRequirements != null) {
			for(Entry<String, Double> entry : numberRequirements.entrySet()) {
				String placeholder = prxAPI.cp(entry.getKey(), p);
				double value = entry.getValue();
				if(Double.valueOf(placeholder) < value) {
					failedRequirements = true;
				}
			}
		}
		if(failedRequirements) {
			if(customRequirementMessage != null) {
				customRequirementMessage.forEach(message -> {
					p.sendMessage(prxAPI.cp(message, p));
				});
			}
			prxAPI.taskedPlayers.remove(name);
			return false;
		}
		PrestigeUpdateEvent e = new PrestigeUpdateEvent(player, PrestigeUpdateCause.PRESTIGEUP);
		main.getServer().getPluginManager().callEvent(e);
		if(e.isCancelled()) {
			prxAPI.taskedPlayers.remove(name);
			return false;
		}
		String prestigeMsg = prxAPI.g("prestige");
		if(prestigeMsg != null) {
			if(!prestigeMsg.isEmpty()) {
				if(main.globalStorage.getBooleanData("Options.send-prestigemsg")) {
				p.sendMessage(prxAPI.cp(prestigeMsg
						.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p))
						.replace("%nextprestige_display%", prxAPI.getPlayerNextPrestigeDisplayNoFallback(p)), p));
				}
			}
		}
		prxAPI.celeberate(p);
		List<String> addPermissionList = main.prestigeStorage.getAddPermissionList(prestige);
		if(addPermissionList != null) {
			if(!addPermissionList.isEmpty()) {
				for(String permission : addPermissionList) {
				main.perm.addPermission(p, permission
						.replace("%player%", p.getName())
						.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p))
						.replace("%nextprestige_display%", prxAPI.getPlayerNextPrestigeDisplayNoFallback(p)));
				}
			}
		}
		List<String> delPermissionList = main.prestigeStorage.getDelPermissionList(prestige);
		if(delPermissionList != null) {
			if(!delPermissionList.isEmpty()) {
				for(String permission : delPermissionList) {
					main.perm.delPermission(p, permission
							.replace("%player%", p.getName())
							.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p))
							.replace("%nextprestige_display%", prxAPI.getPlayerNextPrestigeDisplayNoFallback(p)));
				}
			}
		}
		List<String> nextPrestigeCommands = main.prestigeStorage.getPrestigeCommands(prestige);
		if(nextPrestigeCommands != null) {
			if(!nextPrestigeCommands.isEmpty()) {
				List<String> newPrestigeCommands = new ArrayList<>();
				for(String command : nextPrestigeCommands) {
					newPrestigeCommands.add(command.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p))
							.replace("%nextprestige_display%", prxAPI.getPlayerNextPrestigeDisplayNoFallback(p))
							.replace("%nextprestige_cost%", prxAPI.s(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p))));
				}
				main.executeCommands(p, newPrestigeCommands);
			}
		}
		List<String> actions = main.prestigeStorage.getActions(prestige);
		if(actions != null) {
			if(!actions.isEmpty() && main.isActionUtil) {
				ActionUtil.executeActions(p, actions);
			}
		}
		List<String> actionbarText = main.prestigeStorage.getActionbarMessages(prestige);
		if(actionbarText != null) {
			if(!actionbarText.isEmpty()) {
				List<String> newActionbarText = new LinkedList<>();
	            for(String line : actionbarText) {
	            	newActionbarText.add(line.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p))
	            			.replace("%nextprestige_display%", prxAPI.getPlayerNextPrestigeDisplayNoFallbackR(p)));
	            }
			     int actionbarInterval = main.prestigeStorage.getActionbarInterval(prestige);
			     main.animateActionbar(p, actionbarInterval, newActionbarText);
			}
		}
		List<String> broadcastMessages = main.prestigeStorage.getBroadcast(prestige);
		if(broadcastMessages != null) {
			if(!broadcastMessages.isEmpty()) {
				for(String messageLine : broadcastMessages) {
					Bukkit.broadcastMessage(prxAPI.cp(messageLine
							.replace("%player%", p.getName())
							.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p))
							.replace("%nextprestige_display%", prxAPI.getPlayerNextPrestigeDisplayNoFallback(p)), p));
				}
			}
		}
		List<String> messages = main.prestigeStorage.getMsg(prestige);
		if(messages != null) {
			if(!messages.isEmpty()) {
				for(String messageLine : messages) {
					p.sendMessage(prxAPI.cp(messageLine
							.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p))
							.replace("%nextprestige_display%", prxAPI.getPlayerNextPrestigeDisplayNoFallback(p)), p));
				}
			}
		}
		Map<String, Double> chances = new HashMap<String, Double>();
		PrestigeRandomCommands prc = main.prestigeStorage.getRandomCommandsManager(prestige);
		if(prc != null && prc.getRandomCommandsMap() != null) {
		for(String section : prc.getRandomCommandsMap().keySet()) {
			Double chance = prc.getChance(section);
			chances.put(section, chance);
		}
		
		String randomSection = prxAPI.numberAPI.getChanceFromWeightedMap(chances);
		if(prc.getCommands(randomSection) != null) {
		List<String> commands = prc.getCommands(randomSection);
		List<String> replacedCommands = new ArrayList<>();
		  for(String cmd : commands) {
			String pCMD = prxAPI.cp(cmd.replace("%player%", p.getName()).replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p)), p);
			replacedCommands.add(pCMD);
		  }
		main.executeCommands(p, replacedCommands);
		}
		}
		String nextPrestigeSoundName = main.globalStorage.getStringData("Options.prestigesound-name");
		if(!nextPrestigeSoundName.isEmpty() && nextPrestigeSoundName.length() > 1) {
			float nextPrestigeSoundPitch = (float)main.globalStorage.getDoubleData("Options.prestigesound-pitch");
		    float nextPrestigeSoundVolume = (float)main.globalStorage.getDoubleData("Options.prestigesound-volume");
			p.playSound(p.getLocation(), Sounds.valueOf(nextPrestigeSoundName).bukkitSound(), nextPrestigeSoundVolume, nextPrestigeSoundPitch);
		}
		boolean nextPrestigeHologramIsEnable = main.globalStorage.getBooleanData("Holograms.prestige.enable");
		if(nextPrestigeHologramIsEnable && main.isholo) {
			int nextPrestigeHologramRemoveTime = main.globalStorage.getIntegerData("Holograms.prestige.remove-time");
			int nextPrestigeHologramHeight = main.globalStorage.getIntegerData("Holograms.prestige.height");
			List<String> nextPrestigeHologramFormat = main.globalStorage.getStringListData("Holograms.prestige.format");
			spawnHologram(nextPrestigeHologramFormat, nextPrestigeHologramRemoveTime, nextPrestigeHologramHeight, p);
		}
		main.sendPrestigeFirework(p);
		main.econ.withdrawPlayer(p, prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p));
		if(main.globalStorage.getBooleanData("PrestigeOptions.ResetMoney")) {
			main.econ.withdrawPlayer(p, prxAPI.getPlayerMoney(p));
		}
		if(main.globalStorage.getBooleanData("PrestigeOptions.ResetRank")) {
			RankUpdateEvent e1 = new RankUpdateEvent(p, RankUpdateCause.RANKSET_BYPRESTIGE, main.globalStorage.getStringData("defaultrank"));
			Bukkit.getPluginManager().callEvent(e1);
			if(e1.isCancelled()) {
				prxAPI.taskedPlayers.remove(name);
			} else {
			main.playerStorage.setPlayerRank(p, main.globalStorage.getStringData("defaultrank"));
			}
		}
		List<String> prestigeCommands = main.globalStorage.getStringListData("PrestigeOptions.prestige-cmds");
		if(!prestigeCommands.isEmpty()) {
           prestigeCommands.forEach(cmd -> {
        	   if(cmd.startsWith("[rankpermissions]")) {
        		   prxAPI.allRankAddPermissions.forEach(permission -> {
        		   main.perm.delPermission(p, permission);
        		   });
        	   } else if (cmd.startsWith("[prestigepermissions]")) {
        		   prxAPI.allPrestigeAddPermissions.forEach(permission -> {
        			   main.perm.delPermission(p, permission);
        		   });
        	   } else if (cmd.startsWith("[rebirthpermissions]")) {
        		   prxAPI.allRebirthAddPermissions.forEach(permission -> {
        			   main.perm.delPermission(p, permission);
        		   });
        	   } else {
        		   main.executeCommand(p, cmd);
        	   }
           });
		}
		Bukkit.getScheduler().runTaskLater(main, () -> {
		main.playerStorage.setPlayerPrestige(p, prestige);
		prxAPI.taskedPlayers.remove(name);
		
		}, 1);
		return true;
	}
	
	public void prestige2(final Player player, final boolean ignoreLastRank) {
		String name = player.getName();
		if(prxAPI.taskedPlayers.contains(name)) {
			return;
		}
		prxAPI.taskedPlayers.add(name);

		Player p = player;
		String prestige = prxAPI.getPlayerNextPrestige(p);
		if(!p.hasPermission(main.prestigeCommand.getPermission()) && !p.hasPermission("*")) {
			if(prxAPI.g("nopermission") != null && !prxAPI.g("nopermission").isEmpty()) {	
			p.sendMessage(prxAPI.g("nopermission"));
			}
			prxAPI.taskedPlayers.remove(name);
			return;
		}
		if(prestige == null) {
			if(prxAPI.h("lastprestige") != null && !prxAPI.h("lastprestige").isEmpty()) {
			for(String line : prxAPI.h("lastprestige")) {
				p.sendMessage(prxAPI.c(line));
			}
			}
			prxAPI.taskedPlayers.remove(name);
			return;
		}
		if(!ignoreLastRank) {
		if(!prxAPI.isLastRank(p) && !main.rankStorage.isAllowPrestige(prxAPI.getPlayerRankPath(p))) {
			if(prxAPI.g("noprestige") != null && !prxAPI.g("noprestige").isEmpty()) {
				p.sendMessage(prxAPI.g("noprestige"));
			}
			prxAPI.taskedPlayers.remove(name);
			return;
		}
		}
		double nextPrestigeCost = prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p);
		String nextPrestigeDisplay = prxAPI.getPlayerNextPrestigeDisplayNoFallbackR(p);
		if(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p) > prxAPI.getPlayerMoney(p)) {
			if(prxAPI.h("prestige-notenoughmoney") != null && !prxAPI.h("prestige-notenoughmoney").isEmpty()) {
			for(String line : prxAPI.h("prestige-notenoughmoney")) {
				p.sendMessage(prxAPI.c(line)
						.replace("%nextprestige%", prestige).replace("%nextprestige_display%", nextPrestigeDisplay)
						.replace("%nextprestige_cost%", prxAPI.s(nextPrestigeCost)).replace("%nextprestige_cost_formatted%", prxAPI.formatBalance(nextPrestigeCost)));
			}
			}
			prxAPI.taskedPlayers.remove(name);
			return;
		}
		Map<String, String> stringRequirements = prxAPI.getPrestigeStringRequirements(prestige);
		Map<String, Double> numberRequirements = prxAPI.getPrestigeNumberRequirements(prestige);
		List<String> customRequirementMessage = prxAPI.getPrestigeCustomRequirementMessage(prestige);
		boolean failedRequirements = false;
		if(stringRequirements != null) {
			for(Entry<String, String> entry : stringRequirements.entrySet()) {
				String placeholder = prxAPI.cp(entry.getKey(), p);
				String value = prxAPI.cp(entry.getValue(), p);
				if(!placeholder.equalsIgnoreCase(value)) {
					failedRequirements = true;
				}
			}
		}
		if(numberRequirements != null) {
			for(Entry<String, Double> entry : numberRequirements.entrySet()) {
				String placeholder = prxAPI.cp(entry.getKey(), p);
				double value = entry.getValue();
				if(Double.valueOf(placeholder) < value) {
					failedRequirements = true;
				}
			}
		}
		if(failedRequirements) {
			if(customRequirementMessage != null) {
				customRequirementMessage.forEach(message -> {
					p.sendMessage(prxAPI.cp(message, p));
				});
			}
			prxAPI.taskedPlayers.remove(name);
			return;
		}
		PrestigeUpdateEvent e = new PrestigeUpdateEvent(player, PrestigeUpdateCause.PRESTIGE_BY_RANKUPMAX);
		main.getServer().getPluginManager().callEvent(e);
		if(e.isCancelled()) {
			prxAPI.taskedPlayers.remove(name);
			return;
		}
		String prestigeMsg = prxAPI.g("prestige");
		if(prestigeMsg != null) {
			if(!prestigeMsg.isEmpty()) {
				if(main.globalStorage.getBooleanData("Options.send-prestigemsg")) {
				p.sendMessage(prxAPI.cp(prestigeMsg
						.replace("%nextprestige%", prestige)
						.replace("%nextprestige_display%", nextPrestigeDisplay), p));
				}
			}
		}
		List<String> addPermissionList = main.prestigeStorage.getAddPermissionList(prestige);
		if(addPermissionList != null) {
			if(!addPermissionList.isEmpty()) {
				for(String permission : addPermissionList) {
				main.perm.addPermission(p, permission
						.replace("%player%", p.getName())
						.replace("%nextprestige%", prestige)
						.replace("%nextprestige_display%", nextPrestigeDisplay));
				}
			}
		}
		List<String> delPermissionList = main.prestigeStorage.getDelPermissionList(prestige);
		if(delPermissionList != null) {
			if(!delPermissionList.isEmpty()) {
				for(String permission : delPermissionList) {
					main.perm.delPermission(p, permission
							.replace("%player%", p.getName())
							.replace("%nextprestige%", prestige)
							.replace("%nextprestige_display%", nextPrestigeDisplay));
				}
			}
		}
		List<String> nextPrestigeCommands = main.prestigeStorage.getPrestigeCommands(prestige);
		if(nextPrestigeCommands != null) {
			if(!nextPrestigeCommands.isEmpty()) {
				List<String> newPrestigeCommands = new ArrayList<>();
				for(String command : nextPrestigeCommands) {
					newPrestigeCommands.add(command.replace("%nextprestige%", prestige)
							.replace("%nextprestige_display%", nextPrestigeDisplay)
							.replace("%nextprestige_cost%", prxAPI.s(nextPrestigeCost)));
				}
				main.executeCommands(p, newPrestigeCommands);
			}
		}
		List<String> actions = main.prestigeStorage.getActions(prestige);
		if(actions != null) {
			if(!actions.isEmpty() && main.isActionUtil) {
				ActionUtil.executeActions(p, actions);
			}
		}
		List<String> actionbarText = main.prestigeStorage.getActionbarMessages(prestige);
		if(actionbarText != null) {
			if(!actionbarText.isEmpty()) {
				List<String> newActionbarText = new LinkedList<>();
	            for(String line : actionbarText) {
	            	newActionbarText.add(line.replace("%nextprestige%", prestige)
	            			.replace("%nextprestige_display%", nextPrestigeDisplay));
	            }
			     int actionbarInterval = main.prestigeStorage.getActionbarInterval(prestige);
			     main.animateActionbar(p, actionbarInterval, newActionbarText);
			}
		}
		List<String> broadcastMessages = main.prestigeStorage.getBroadcast(prestige);
		if(broadcastMessages != null) {
			if(!broadcastMessages.isEmpty()) {
				for(String messageLine : broadcastMessages) {
					Bukkit.broadcastMessage(prxAPI.cp(messageLine
							.replace("%player%", p.getName())
							.replace("%nextprestige%", prestige)
							.replace("%nextprestige_display%", nextPrestigeDisplay), p));
				}
			}
		}
		List<String> messages = main.prestigeStorage.getMsg(prestige);
		if(messages != null) {
			if(!messages.isEmpty()) {
				for(String messageLine : messages) {
					p.sendMessage(prxAPI.cp(messageLine
							.replace("%nextprestige%", prestige)
							.replace("%nextprestige_display%", nextPrestigeDisplay), p));
				}
			}
		}
		Map<String, Double> chances = new HashMap<String, Double>();
		PrestigeRandomCommands prc = main.prestigeStorage.getRandomCommandsManager(prestige);
		if(prc != null && prc.getRandomCommandsMap() != null) {
		for(String section : prc.getRandomCommandsMap().keySet()) {
			Double chance = prc.getChance(section);
			chances.put(section, chance);
		}
		
		String randomSection = prxAPI.numberAPI.getChanceFromWeightedMap(chances);
		if(prc.getCommands(randomSection) != null) {
		List<String> commands = prc.getCommands(randomSection);
		List<String> replacedCommands = new ArrayList<>();
		  for(String cmd : commands) {
			String pCMD = prxAPI.cp(cmd.replace("%player%", p.getName()).replace("%nextprestige%", prestige), p);
			replacedCommands.add(pCMD);
		  }
		main.executeCommands(p, replacedCommands);
		}
		}
		String nextPrestigeSoundName = main.globalStorage.getStringData("Options.prestigesound-name");
		if(!nextPrestigeSoundName.isEmpty() && nextPrestigeSoundName.length() > 1) {
			float nextPrestigeSoundPitch = (float)main.globalStorage.getDoubleData("Options.prestigesound-pitch");
		    float nextPrestigeSoundVolume = (float)main.globalStorage.getDoubleData("Options.prestigesound-volume");
			p.playSound(p.getLocation(), Sounds.valueOf(nextPrestigeSoundName).bukkitSound(), nextPrestigeSoundVolume, nextPrestigeSoundPitch);
		}
		boolean nextPrestigeHologramIsEnable = main.globalStorage.getBooleanData("Holograms.prestige.enable");
		if(nextPrestigeHologramIsEnable && main.isholo) {
			int nextPrestigeHologramRemoveTime = main.globalStorage.getIntegerData("Holograms.prestige.remove-time");
			int nextPrestigeHologramHeight = main.globalStorage.getIntegerData("Holograms.prestige.height");
			List<String> nextPrestigeHologramFormat = main.globalStorage.getStringListData("Holograms.prestige.format");
			spawnHologram(nextPrestigeHologramFormat, nextPrestigeHologramRemoveTime, nextPrestigeHologramHeight, p);
		}
		main.sendPrestigeFirework(p);
		main.econ.withdrawPlayer(p, nextPrestigeCost);
		if(main.globalStorage.getBooleanData("PrestigeOptions.ResetMoney")) {
			main.econ.withdrawPlayer(p, prxAPI.getPlayerMoney(p));
		}
		if(main.globalStorage.getBooleanData("PrestigeOptions.ResetRank")) {
			RankUpdateEvent e1 = new RankUpdateEvent(p, RankUpdateCause.RANKSET_BYPRESTIGE, main.globalStorage.getStringData("defaultrank"));
			Bukkit.getPluginManager().callEvent(e1);
			if(e1.isCancelled()) {
				prxAPI.taskedPlayers.remove(name);
				return;
			}
			main.playerStorage.setPlayerRank(p, main.globalStorage.getStringData("defaultrank"));
		}
		List<String> prestigeCommands = main.globalStorage.getStringListData("PrestigeOptions.prestige-cmds");
		if(!prestigeCommands.isEmpty()) {
           prestigeCommands.forEach(cmd -> {
        	   if(cmd.startsWith("[rankpermissions]")) {
        		   prxAPI.allRankAddPermissions.forEach(permission -> {
        		   main.perm.delPermission(p, permission);
        		   });
        	   } else if (cmd.startsWith("[prestigepermissions]")) {
        		   prxAPI.allPrestigeAddPermissions.forEach(permission -> {
        			   main.perm.delPermission(p, permission);
        		   });
        	   } else if (cmd.startsWith("[rebirthpermissions]")) {
        		   prxAPI.allRebirthAddPermissions.forEach(permission -> {
        			   main.perm.delPermission(p, permission);
        		   });
        	   } else {
        		   main.executeCommand(p, cmd);
        	   }
           });
		}
		Bukkit.getScheduler().runTaskLater(main, () -> {
		main.playerStorage.setPlayerPrestige(p, prestige);
		prxAPI.taskedPlayers.remove(name);
		
		}, 1);
	}
	
	/**
	 * 
	 * @param player player that will be promoted to the next prestige
	 * @param silent don't send messages when the prestige fails
	 * @return true for a success prestige, false otherwise. 
	 * <p><i>This method is thread-safe i.e Can be called from an Async Task
	 */
	public boolean prestige(final Player player, final boolean silent) {
		if(!silent) {Bukkit.getScheduler().runTask(main, () -> prestige(player));
		return false;}
		Player p = player;
		String name = p.getName();
       if(getTaskedPlayers().contains(name)) {
    	   return false;
       }
       getTaskedPlayers().add(name);
		String currentPrestige = prxAPI.getPlayerPrestige(p);
		String prestige = prxAPI.getPlayerNextPrestige(p);
		if(prestige == null) {
			getTaskedPlayers().remove(name);
			return false;
		}
		if(!prxAPI.isLastRank(p) && !main.rankStorage.isAllowPrestige(prxAPI.getPlayerRankPath(p))) {
			getTaskedPlayers().remove(name);
			return false;
		}
		double prestigeCost = prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p);
		if(prestigeCost > prxAPI.getPlayerMoney(p)) {
			getTaskedPlayers().remove(name);
			return false;
		}
		Map<String, String> stringRequirements = prxAPI.getPrestigeStringRequirements(prestige);
		Map<String, Double> numberRequirements = prxAPI.getPrestigeNumberRequirements(prestige);
		List<String> customRequirementMessage = prxAPI.getPrestigeCustomRequirementMessage(prestige);
		boolean failedRequirements = false;
		if(stringRequirements != null) {
			for(Entry<String, String> entry : stringRequirements.entrySet()) {
				String placeholder = prxAPI.cp(entry.getKey(), p);
				String value = prxAPI.cp(entry.getValue(), p);
				if(!placeholder.equalsIgnoreCase(value)) {
					failedRequirements = true;
				}
			}
		}
		if(numberRequirements != null) {
			for(Entry<String, Double> entry : numberRequirements.entrySet()) {
				String placeholder = prxAPI.cp(entry.getKey(), p);
				double value = entry.getValue();
				if(Double.valueOf(placeholder) < value) {
					failedRequirements = true;
				}
			}
		}
		if(failedRequirements) {
			if(customRequirementMessage != null) {
				customRequirementMessage.forEach(message -> {
					p.sendMessage(prxAPI.cp(message, p));
				});
			}
			getTaskedPlayers().remove(name);
			return false;
		}
		AsyncAutoPrestigeEvent event = new AsyncAutoPrestigeEvent(p, currentPrestige, prestige);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) {
			getTaskedPlayers().remove(name);
			return false;
		}
		String prestigeDisplay = prxAPI.getPlayerNextPrestigeDisplayNoFallback(p);
		String prestigeMsg = prxAPI.g("prestige");
		if(prestigeMsg != null) {
			if(!prestigeMsg.isEmpty()) {
				if(main.globalStorage.getBooleanData("Options.send-prestigemsg")) {
				p.sendMessage(prxAPI.cp(prestigeMsg
						.replace("%nextprestige%", prestige)
						.replace("%nextprestige_display%", prestigeDisplay), p));
				}
			}
		}
		List<String> addPermissionList = main.prestigeStorage.getAddPermissionList(prestige);
		if(addPermissionList != null) {
			if(!addPermissionList.isEmpty()) {
				if(main.isVaultGroups && main.vaultPlugin.equalsIgnoreCase("permissionsex")) {
					Bukkit.getScheduler().runTask(main, () -> {
					for(String permission : addPermissionList) {
						main.perm.addPermission(p, permission
								.replace("%player%", name)
								.replace("%nextprestige%", prestige)
								.replace("%nextprestige_display%", prestigeDisplay));
						}
					});
				} else {
				for(String permission : addPermissionList) {
				main.perm.addPermission(p, permission
						.replace("%player%", name)
						.replace("%nextprestige%", prestige)
						.replace("%nextprestige_display%", prestigeDisplay));
				}
				}
			}
		}
		List<String> delPermissionList = main.prestigeStorage.getDelPermissionList(prestige);
		if(delPermissionList != null) {
			if(!delPermissionList.isEmpty()) {
				if(main.isVaultGroups && main.vaultPlugin.equalsIgnoreCase("permissionsex")) {
					Bukkit.getScheduler().runTask(main, () -> {
					for(String permission : delPermissionList) {
						main.perm.delPermission(p, permission
								.replace("%player%", name)
								.replace("%nextprestige%", prestige)
								.replace("%nextprestige_display%", prestigeDisplay));
						}
					});
					
				} else {
				for(String permission : delPermissionList) {
					main.perm.delPermission(p, permission
							.replace("%player%", name)
							.replace("%nextprestige%", prestige)
							.replace("%nextprestige_display%", prestigeDisplay));
				}
				}
			}
		}
		List<String> nextPrestigeCommands = main.prestigeStorage.getPrestigeCommands(prestige);
		if(nextPrestigeCommands != null) {
			if(!nextPrestigeCommands.isEmpty()) {
				List<String> newPrestigeCommands = new ArrayList<>();
				for(String command : nextPrestigeCommands) {
					newPrestigeCommands.add(command.replace("%nextprestige%", prestige)
							.replace("%nextprestige_display%", prestigeDisplay)
							.replace("%nextprestige_cost%", prxAPI.s(prestigeCost)));
				}
				main.executeCommands(p, newPrestigeCommands);
			}
		}
		List<String> actions = main.prestigeStorage.getActions(prestige);
		if(actions != null) {
			if(!actions.isEmpty() && main.isActionUtil) {
				ActionUtil.executeActions(p, actions);
			}
		}
		List<String> actionbarText = main.prestigeStorage.getActionbarMessages(prestige);
		if(actionbarText != null) {
			if(!actionbarText.isEmpty()) {
				List<String> newActionbarText = new LinkedList<>();
	            for(String line : actionbarText) {
	            	newActionbarText.add(line.replace("%nextprestige%", prestige)
	            			.replace("%nextprestige_display%", prestigeDisplay));
	            }
			     int actionbarInterval = main.prestigeStorage.getActionbarInterval(prestige);
			     main.animateActionbar(p, actionbarInterval, newActionbarText);
			}
		}
		List<String> broadcastMessages = main.prestigeStorage.getBroadcast(prestige);
		if(broadcastMessages != null) {
			if(!broadcastMessages.isEmpty()) {
				for(String messageLine : broadcastMessages) {
					Bukkit.broadcastMessage(prxAPI.cp(messageLine
							.replace("%player%", name)
							.replace("%nextprestige%", prestige)
							.replace("%nextprestige_display%", prestigeDisplay), p));
				}
			}
		}
		List<String> messages = main.prestigeStorage.getMsg(prestige);
		if(messages != null) {
			if(!messages.isEmpty()) {
				for(String messageLine : messages) {
					p.sendMessage(prxAPI.cp(messageLine
							.replace("%nextprestige%", prestige)
							.replace("%nextprestige_display%", prestigeDisplay), p));
				}
			}
		}
		Map<String, Double> chances = new HashMap<String, Double>();
		PrestigeRandomCommands prc = main.prestigeStorage.getRandomCommandsManager(prestige);
		if(prc != null && prc.getRandomCommandsMap() != null) {
		for(String section : prc.getRandomCommandsMap().keySet()) {
			Double chance = prc.getChance(section);
			chances.put(section, chance);
		}
		
		String randomSection = prxAPI.numberAPI.getChanceFromWeightedMap(chances);
		if(prc.getCommands(randomSection) != null) {
		List<String> commands = prc.getCommands(randomSection);
		List<String> replacedCommands = new ArrayList<>();
		  for(String cmd : commands) {
			String pCMD = prxAPI.cp(cmd.replace("%player%", p.getName()).replace("%nextprestige%", prestige).replace("%nextprestige_display%", prestigeDisplay), p);
			replacedCommands.add(pCMD);
		  }
		main.executeCommands(p, replacedCommands);
		}
		}
		String nextPrestigeSoundName = main.globalStorage.getStringData("Options.prestigesound-name");
		if(!nextPrestigeSoundName.isEmpty() && nextPrestigeSoundName.length() > 1) {
			float nextPrestigeSoundPitch = (float)main.globalStorage.getDoubleData("Options.prestigesound-pitch");
		    float nextPrestigeSoundVolume = (float)main.globalStorage.getDoubleData("Options.prestigesound-volume");
			p.playSound(p.getLocation(), Sounds.valueOf(nextPrestigeSoundName).bukkitSound(), nextPrestigeSoundVolume, nextPrestigeSoundPitch);
		}
		boolean nextPrestigeHologramIsEnable = main.globalStorage.getBooleanData("Holograms.prestige.enable");
		if(nextPrestigeHologramIsEnable && main.isholo) {
			int nextPrestigeHologramRemoveTime = main.globalStorage.getIntegerData("Holograms.prestige.remove-time");
			int nextPrestigeHologramHeight = main.globalStorage.getIntegerData("Holograms.prestige.height");
			List<String> nextPrestigeHologramFormat = main.globalStorage.getStringListData("Holograms.prestige.format");
			spawnHologram(nextPrestigeHologramFormat, nextPrestigeHologramRemoveTime, nextPrestigeHologramHeight, p);
		}
		main.sendPrestigeFirework(p);
		main.econ.withdrawPlayer(p, prestigeCost);
		if(main.globalStorage.getBooleanData("PrestigeOptions.ResetMoney")) {
			main.econ.withdrawPlayer(p, prxAPI.getPlayerMoney(p));
		}
		if(main.globalStorage.getBooleanData("PrestigeOptions.ResetRank")) {
			Bukkit.getScheduler().runTask(main, () -> {
			RankUpdateEvent e1 = new RankUpdateEvent(p, RankUpdateCause.RANKSET_BYPRESTIGE);
			if(e1.isCancelled()) {
				getTaskedPlayers().remove(name);
				return;
			}
			main.playerStorage.setPlayerRank(p, main.globalStorage.getStringData("defaultrank"));
			Bukkit.getPluginManager().callEvent(e1);
			});
		}
		List<String> prestigeCommands = main.globalStorage.getStringListData("PrestigeOptions.prestige-cmds");
		if(!prestigeCommands.isEmpty()) {
           prestigeCommands.forEach(cmd -> {
        	   if(cmd.startsWith("[rankpermissions]")) {
        		   prxAPI.allRankAddPermissions.forEach(permission -> {
        		   main.perm.delPermission(p, permission);
        		   });
        	   } else if (cmd.startsWith("[prestigepermissions]")) {
        		   prxAPI.allPrestigeAddPermissions.forEach(permission -> {
        			   main.perm.delPermission(p, permission);
        		   });
        	   } else if (cmd.startsWith("[rebirthpermissions]")) {
        		   prxAPI.allRebirthAddPermissions.forEach(permission -> {
        			   main.perm.delPermission(p, permission);
        		   });
        	   } else {
        		   main.executeCommand(p, cmd);
        	   }
           });
		}
		Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> {
		main.playerStorage.setPlayerPrestige(p, prestige);
		getTaskedPlayers().remove(name);
		}, 1);
        return true;
	}
	
	/**
	 * 
	 * @param format hologram lines
	 * @param removeTime time until hologram decay
	 * @param height y level above player
	 * @param player player to spawn the hologram above
	 * <p><i>this method is thread-safe i.e can be called from an Async Task.
	 */
	public void spawnHologram(List<String> format, int removeTime, int height, Player player) {
		String name = player.getName();
		Bukkit.getScheduler().runTask(main, () -> {
		Hologram hologram = HologramsAPI.createHologram(main, player.getLocation().add(0, height, 0));
		hologram.setAllowPlaceholders(true);
		for(String line : format) {
			String updatedLine = main.getString(line.replace("%player%", name)
					.replace("%player_display%", player.getDisplayName())
					.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(player))
					.replace("%nextprestige_display%", main.getString(prxAPI.getPlayerNextPrestigeDisplay(player)))
					, player);
			hologram.appendTextLine(updatedLine);
		}
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable () {public void run() {
        	hologram.delete();
        }}, 20L * removeTime);
		});
	}

	/**
	 * 
	 * @param format hologram lines
	 * @param removeTime time until hologram decay
	 * @param height y level above player
	 * @param player player to spawn the hologram above
	 * <p><i>this method is thread-safe i.e can be called from an Async Task.
	 */
	public void spawnHologram(List<String> format, int removeTime, int height, Player player, String prestige) {
		String name = player.getName();
		Bukkit.getScheduler().runTask(main, () -> {
		Hologram hologram = HologramsAPI.createHologram(main, player.getLocation().add(0, height, 0));
		hologram.setAllowPlaceholders(true);
		for(String line : format) {
			String updatedLine = main.getString(line.replace("%player%", name)
					.replace("%player_display%", player.getDisplayName())
					.replace("%nextprestige%", prestige)
					.replace("%nextprestige_display%", main.getString(prxAPI.getPrestigeDisplay(prestige)))
					, player);
			hologram.appendTextLine(updatedLine);
		}
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable () {public void run() {
        	hologram.delete();
        }}, 20L * removeTime);
		});
	}
	
	public Set<String> getTaskedPlayers() {
		return taskedPlayers;
	}

	public void setTaskedPlayers(Set<String> taskedPlayers) {
		this.taskedPlayers = taskedPlayers;
	}
}
