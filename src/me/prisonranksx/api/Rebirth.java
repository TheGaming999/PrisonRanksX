package me.prisonranksx.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import io.samdev.actionutil.ActionUtil;
import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.RebirthRandomCommands;
import me.prisonranksx.events.RebirthUpdateEvent;
import me.prisonranksx.events.PrestigeUpdateCause;
import me.prisonranksx.events.RankUpdateCause;
import me.prisonranksx.events.RebirthUpdateCause;
import me.prisonranksx.events.PrestigeUpdateEvent;
import me.prisonranksx.events.RankUpdateEvent;
import me.prisonranksx.utils.OnlinePlayers;
import me.prisonranksx.utils.CompatibleSound.Sounds;

public class Rebirth {
	
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	private PRXAPI prxAPI;
	public Rebirth() {
		this.prxAPI = main.prxAPI;
	}
	
	public void rebirth(final Player player) {
		String name = player.getName();
		if(prxAPI.taskedPlayers.contains(name)) {
			if(prxAPI.g("commandspam") == null || prxAPI.g("commandspam").isEmpty()) {
				return;
			}
			player.sendMessage(prxAPI.g("commandspam"));
			return;
		}
		prxAPI.taskedPlayers.add(name);

		Player p = player;
		String rebirth = prxAPI.getPlayerNextRebirth(p);
		if(!p.hasPermission(main.rebirthCommand.getPermission()) && !p.hasPermission("*")) {
			if(prxAPI.g("nopermission") == null || prxAPI.g("nopermission").isEmpty()) {
				return;
			}
			p.sendMessage(prxAPI.g("nopermission"));
			prxAPI.taskedPlayers.remove(name);
			return;
		}
		if(rebirth.equalsIgnoreCase("LASTREBIRTH")) {
			if(prxAPI.h("lastrebirth") != null && !prxAPI.h("lastrebirth").isEmpty()) {
			  for(String line : prxAPI.h("lastrebirth")) {
				  p.sendMessage(prxAPI.c(line));
			  }
			}
			prxAPI.taskedPlayers.remove(name);
			return;
		}
		if(!prxAPI.isLastRank(p)) {
			if(prxAPI.g("norebirth") != null && !prxAPI.g("norebirth").isEmpty()) {
				p.sendMessage(prxAPI.cp(prxAPI.g("norebirth"), p));
			}
			prxAPI.taskedPlayers.remove(name);
			return;
		}
		if(prxAPI.getPlayerNextRebirthCost(p) > prxAPI.getPlayerMoney(p)) {
			if(prxAPI.h("rebirth-notenoughmoney") != null && !prxAPI.h("rebirth-notenoughmoney").isEmpty()) {	
			for(String line : prxAPI.h("rebirth-notenoughmoney")) {
				p.sendMessage(prxAPI.c(line)
						.replace("%nextrebirth%", prxAPI.getPlayerNextRebirth(p)).replace("%nextrebirth_display%", prxAPI.getPlayerNextRebirthDisplayNoFallback(p))
						.replace("%nextrebirth_cost%", prxAPI.s(prxAPI.getPlayerNextRebirthCost(p))).replace("%nextrebirth_cost_formatted%", prxAPI.getPlayerNextRebirthCostFormatted(p)));
			}
			}
			prxAPI.taskedPlayers.remove(name);
			return;
		}
		int requiredPrestiges = prxAPI.getRequiredPrestiges(rebirth);
		if(requiredPrestiges > 0) {
		if(requiredPrestiges > prxAPI.getPlayerPrestiges(p)) {
			// ouh
			int rebirthNumber = prxAPI.getPlayerRebirthNumber(p) <= 0 ? 1 : prxAPI.getPlayerRebirthNumber(p);
			int left = (requiredPrestiges - prxAPI.getPlayerPrestiges(p)) / rebirthNumber;
			p.sendMessage(prxAPI.g("rebirth-failed").replace("%prestiges_amount_left%", String.valueOf(left))
					.replace("%prestiges_amount%", String.valueOf(requiredPrestiges)));
			prxAPI.taskedPlayers.remove(name);
			return;
		}
		} else {
			if(!prxAPI.hasNextPrestige(p)) {
				int rebirthNumber = prxAPI.getPlayerRebirthNumber(p) <= 0 ? 1 : prxAPI.getPlayerRebirthNumber(p);
				int left = (prxAPI.getPrestigesCollection().size() - prxAPI.getPlayerPrestiges(p) / rebirthNumber);
				p.sendMessage(prxAPI.g("rebirth-failed").replace("%prestiges_amount_left%", String.valueOf(left))
						.replace("%prestiges_amount%", prxAPI.s(prxAPI.getPrestigesCollection().size())));
				prxAPI.taskedPlayers.remove(name);
				return;
			}
		}
		Map<String, String> stringRequirements = prxAPI.getRebirthStringRequirements(rebirth);
		Map<String, Double> numberRequirements = prxAPI.getRebirthNumberRequirements(rebirth);
		List<String> customRequirementMessage = prxAPI.getRebirthCustomRequirementMessage(rebirth);
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
		RebirthUpdateEvent e = new RebirthUpdateEvent(player, RebirthUpdateCause.REBIRTHUP);
		main.getServer().getPluginManager().callEvent(e);
		if(e.isCancelled()) {
			prxAPI.taskedPlayers.remove(name);
			return;
		}
		String rebirthMsg = prxAPI.g("rebirth");
		if(rebirthMsg != null) {
			if(!rebirthMsg.isEmpty()) {
				if(main.globalStorage.getBooleanData("Options.send-rebirthmsg")) {
				p.sendMessage(prxAPI.cp(rebirthMsg
						.replace("%nextrebirth%", prxAPI.getPlayerNextRebirth(p))
						.replace("%nextrebirth_display%", prxAPI.getPlayerNextRebirthDisplayNoFallback(p)), p));
				}
			}
		}
		List<String> addPermissionList = main.rebirthStorage.getAddPermissionList(rebirth);
		if(addPermissionList != null) {
			if(!addPermissionList.isEmpty()) {
				for(String permission : addPermissionList) {
				main.perm.addPermission(player, permission
						.replace("%player%", name)
						.replace("%nextrebirth%", prxAPI.getPlayerNextRebirth(p))
						.replace("%nextrebirth_display%", prxAPI.getPlayerNextRebirthDisplayNoFallback(p)));
				}
			}
		}
		List<String> delPermissionList = main.rebirthStorage.getDelPermissionList(rebirth);
		if(delPermissionList != null) {
			if(!delPermissionList.isEmpty()) {
				for(String permission : delPermissionList) {
					main.perm.delPermission(p, permission
							.replace("%player%", name)
							.replace("%nextrebirth%", prxAPI.getPlayerNextRebirth(p))
							.replace("%nextrebirth_display%", prxAPI.getPlayerNextRebirthDisplayNoFallback(p)));
				}
			}
		}
		List<String> nextRebirthCommands = main.rebirthStorage.getRebirthCommands(rebirth);
		if(nextRebirthCommands != null) {
			if(!nextRebirthCommands.isEmpty()) {
				List<String> newRebirthCommands = new ArrayList<>();
				for(String command : nextRebirthCommands) {
					newRebirthCommands.add(command.replace("%nextrebirth%", prxAPI.getPlayerNextRebirth(p))
							.replace("%nextrebirth_display%", prxAPI.getPlayerNextRebirthDisplayNoFallback(p))
							.replace("%nextrebirth_cost%", prxAPI.s(prxAPI.getPlayerNextRebirthCost(p))));
				}
				main.executeCommands(p, newRebirthCommands);
			}
		}
		List<String> actions = main.rebirthStorage.getActions(rebirth);
		if(actions != null) {
			if(!actions.isEmpty() && main.isActionUtil) {
				ActionUtil.executeActions(p, actions);
			}
		}
		List<String> actionbarText = main.rebirthStorage.getActionbarMessages(rebirth);
		if(actionbarText != null) {
			if(!actionbarText.isEmpty()) {
				List<String> newActionbarText = new LinkedList<>();
	            for(String line : actionbarText) {
	            	newActionbarText.add(line.replace("%nextrebirth%", prxAPI.getPlayerNextRebirth(p))
	            			.replace("%nextrebirth_display%", prxAPI.getPlayerNextRebirthDisplayNoFallbackR(p)));
	            }
			     int actionbarInterval = main.rebirthStorage.getActionbarInterval(rebirth);
			     main.animateActionbar(p, actionbarInterval, newActionbarText);
			}
		}
		List<String> broadcastMessages = main.rebirthStorage.getBroadcast(rebirth);
		if(broadcastMessages != null) {
			if(!broadcastMessages.isEmpty()) {
				OnlinePlayers.getPlayers().forEach(ap -> {
					if(main.isInDisabledWorld(ap)) return;
				for(String messageLine : broadcastMessages) {
					ap.sendMessage(prxAPI.cp(messageLine
							.replace("%player%", name)
							.replace("%nextrebirth%", prxAPI.getPlayerNextRebirth(p))
							.replace("%nextrebirth_display%", prxAPI.getPlayerNextRebirthDisplay(p)), p));
				}
				});
			}
		}
		List<String> messages = main.rebirthStorage.getMsg(rebirth);
		if(messages != null) {
			if(!messages.isEmpty()) {
				for(String messageLine : messages) {
					p.sendMessage(prxAPI.cp(messageLine
							.replace("%nextrebirth%", prxAPI.getPlayerNextRebirth(p))
							.replace("%nextrebirth_display%", prxAPI.getPlayerNextRebirthDisplayNoFallback(p)), p));
				}
			}
		}
		Map<String, Double> chances = new HashMap<String, Double>();
		RebirthRandomCommands rrc = main.rebirthStorage.getRandomCommandsManager(rebirth);
		if(rrc != null && rrc.getRandomCommandsMap() != null && !rrc.getRandomCommandsMap().isEmpty()) {
		for(String section : rrc.getRandomCommandsMap().keySet()) {
			Double chance = rrc.getChance(section);
			chances.put(section, chance);
		}
		String randomSection = prxAPI.numberAPI.getChanceFromWeightedMap(chances);
		if(rrc.getCommands(randomSection) != null) {
		List<String> commands = rrc.getCommands(randomSection);
		List<String> replacedCommands = new ArrayList<>();
		  for(String cmd : commands) {
			String pCMD = prxAPI.cp(cmd.replace("%player%", name).replace("%nextrebirth%", prxAPI.getPlayerNextRebirth(p)), p);
			replacedCommands.add(pCMD);
		  }
		main.executeCommands(p, replacedCommands);
		}
		}
		String nextRebirthSoundName = main.globalStorage.getStringData("Options.rebirthsound-name");
		if(!nextRebirthSoundName.isEmpty() && nextRebirthSoundName.length() > 1) {
			float nextRebirthSoundPitch = (float)main.globalStorage.getDoubleData("Options.rebirthsound-pitch");
		    float nextRebirthSoundVolume = (float)main.globalStorage.getDoubleData("Options.rebirthsound-volume");
			p.playSound(p.getLocation(), Sounds.valueOf(nextRebirthSoundName).bukkitSound(), nextRebirthSoundVolume, nextRebirthSoundPitch);
		}
		boolean nextRebirthHologramIsEnable = main.globalStorage.getBooleanData("Holograms.rebirth.enable");
		if(nextRebirthHologramIsEnable && main.isholo) {
			int nextRebirthHologramRemoveTime = main.globalStorage.getIntegerData("Holograms.rebirth.remove-time");
			int nextRebirthHologramHeight = main.globalStorage.getIntegerData("Holograms.rebirth.height");
			List<String> nextRebirthHologramFormat = main.globalStorage.getStringListData("Holograms.rebirth.format");
			spawnHologram(nextRebirthHologramFormat, nextRebirthHologramRemoveTime, nextRebirthHologramHeight, p);
		}
		main.sendRebirthFirework(p);
		main.econ.withdrawPlayer(p, prxAPI.getPlayerNextRebirthCost(p));
		if(main.globalStorage.getBooleanData("RebirthOptions.ResetMoney")) {
			main.econ.withdrawPlayer(p, prxAPI.getPlayerMoney(p));
		}
		if(main.globalStorage.getBooleanData("RebirthOptions.ResetRank")) {
			RankUpdateEvent xrue = new RankUpdateEvent(p, RankUpdateCause.RANKSET_BYREBIRTH, main.globalStorage.getStringData("defaultrank"));
			Bukkit.getScheduler().runTask(main, () -> {
				Bukkit.getPluginManager().callEvent(xrue);
			if(xrue.isCancelled()) {
				return;
			}
			});
			main.playerStorage.setPlayerRank(p, main.globalStorage.getStringData("defaultrank"));
		}
		if(main.globalStorage.getBooleanData("RebirthOptions.ResetPrestige")) {
			PrestigeUpdateEvent xpue = new PrestigeUpdateEvent(p, PrestigeUpdateCause.SETPRESTIGE_BY_REBIRTH);
			Bukkit.getScheduler().runTask(main, () -> {
				Bukkit.getPluginManager().callEvent(xpue);
			if(xpue.isCancelled()) {
				return;
			}
			});
			main.playerStorage.setPlayerPrestige(p, prxAPI.getFirstPrestige());
		}
		List<String> rebirthCommands = main.globalStorage.getStringListData("RebirthOptions.rebirth-cmds");
		if(!rebirthCommands.isEmpty()) {
           rebirthCommands.forEach(cmd -> {
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
		main.playerStorage.setPlayerRebirth(p, rebirth);
		prxAPI.taskedPlayers.remove(name);
		
		}, 1);
	}
	
	public void spawnHologram(List<String> format, int removeTime, int height, Player player) {
		Player p = player;
		String name = p.getName();
		Hologram hologram = HologramsAPI.createHologram(main, p.getLocation().add(0, height, 0));
		hologram.setAllowPlaceholders(true);
		for(String line : format) {
			String updatedLine = main.getString(line.replace("%player%", name)
					.replace("%player_display%", p.getDisplayName())
					.replace("%nextrebirth%", prxAPI.getPlayerNextRebirth(p))
					.replace("%nextrebirth_display%", main.getString(prxAPI.getPlayerNextRebirthDisplay(p)))
					, name);
			hologram.appendTextLine(updatedLine);
		}
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable () {public void run() {
        	hologram.delete();
        }}, 20L * removeTime);
	}
}
