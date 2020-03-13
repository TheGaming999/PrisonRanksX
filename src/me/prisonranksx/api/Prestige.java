package me.prisonranksx.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import io.samdev.actionutil.ActionUtil;
import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.PrestigeRandomCommands;
import me.prisonranksx.events.XPrestigeEvent;
import me.prisonranksx.utils.CompatibleSound.Sounds;

public class Prestige {
	
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	private PRXAPI prxAPI;
	public Prestige() {
		this.prxAPI = main.prxAPI;
	}
	
	public void prestige(Player player) {
		if(prxAPI.taskedPlayers.contains(player)) {
			if(prxAPI.g("commandspam") != null && !prxAPI.g("commandspam").isEmpty()) {
			player.sendMessage(prxAPI.g("commandspam"));
			}
			return;
		}
		prxAPI.taskedPlayers.add(player);
		if(player == null) {
			return;
		}
		XPrestigeEvent e = new XPrestigeEvent(player, "PRESTIGEUP");
		
		if(e.isCancelled()) {
			return;
		}
		Player p = player;
		String prestige = prxAPI.getPlayerNextPrestige(p);
		if(!p.hasPermission(main.prestigeCommand.getPermission()) && !p.hasPermission("*")) {
			if(prxAPI.g("nopermission") == null || prxAPI.g("nopermission").isEmpty()) {
				return;
			}
			p.sendMessage(prxAPI.g("nopermission"));
			prxAPI.taskedPlayers.remove(p);
			return;
		}
		if(prestige.equalsIgnoreCase("LASTPRESTIGE")) {
			if(prxAPI.h("lastprestige") != null && !prxAPI.h("lastprestige").isEmpty()) {
			for(String line : prxAPI.h("lastprestige")) {
				p.sendMessage(prxAPI.c(line));
			}
			}
			prxAPI.taskedPlayers.remove(p);
			return;
		}
		if(!prxAPI.isLastRank(p) && !main.rankStorage.isAllowPrestige(prxAPI.getPlayerRankPath(p))) {
			if(prxAPI.g("noprestige") != null && !prxAPI.g("noprestige").isEmpty()) {
				p.sendMessage(prxAPI.g("noprestige"));
			}
			prxAPI.taskedPlayers.remove(p);
			return;
		}
		if(prxAPI.getPlayerNextPrestigeCost(p) > prxAPI.getPlayerMoney(p)) {
			if(prxAPI.h("prestige-notenoughmoney") == null || prxAPI.h("prestige-notenoughmoney").isEmpty()) {
				return;
			}
			for(String line : prxAPI.h("prestige-notenoughmoney")) {
				p.sendMessage(prxAPI.c(line)
						.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p)).replace("%nextprestige_display%", prxAPI.getPlayerNextPrestigeDisplayNoFallback(p))
						.replace("%nextprestige_cost%", prxAPI.s(prxAPI.getPlayerNextPrestigeCost(p))).replace("%nextprestige_cost_formatted%", prxAPI.getPlayerNextPrestigeCostFormatted(p)));
			}
			prxAPI.taskedPlayers.remove(p);
			return;
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
		List<String> addPermissionList = main.prestigeStorage.getAddPermissionList(prestige);
		if(addPermissionList != null) {
			if(!addPermissionList.isEmpty()) {
				for(String permission : addPermissionList) {
				main.perm.addPermission(player, permission
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
							.replace("%nextprestige_cost%", prxAPI.s(prxAPI.getPlayerNextPrestigeCost(p))));
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
					p.sendMessage(prxAPI.cp(messageLine
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
		main.econ.withdrawPlayer(p, prxAPI.getPlayerNextPrestigeCost(p));
		if(main.globalStorage.getBooleanData("PrestigeOptions.ResetMoney")) {
			main.econ.withdrawPlayer(p, prxAPI.getPlayerMoney(p));
		}
		if(main.globalStorage.getBooleanData("PrestigeOptions.ResetRank")) {
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
		main.playerStorage.setPlayerPrestige(p, prestige);
		prxAPI.taskedPlayers.remove(player);
		main.getServer().getPluginManager().callEvent(e);
	}
	
	public void spawnHologram(List<String> format, int removeTime, int height, Player player) {
		Hologram hologram = HologramsAPI.createHologram(main, player.getLocation().add(0, height, 0));
		hologram.setAllowPlaceholders(true);
		for(String line : format) {
			String updatedLine = main.getString(line.replace("%player%", player.getName())
					.replace("%player_display%", player.getDisplayName())
					.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(player))
					.replace("%nextprestige_display%", main.getStringWithoutPAPI(prxAPI.getPlayerNextPrestigeDisplay(player)))
					, player.getName());
			hologram.appendTextLine(updatedLine);
		}
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable () {public void run() {
        	hologram.delete();
        }}, 20L * removeTime);
	}
}
