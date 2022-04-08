package me.prisonranksx.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import me.prisonranksx.utils.XSound;
import me.prisonranksx.utils.XUUID;

@SuppressWarnings("deprecation")
public class RebirthLegacy {

	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	private PRXAPI prxAPI;

	public RebirthLegacy() {
		this.prxAPI = main.prxAPI;
	}

	public void rebirth(final Player player) {
		String name = player.getName();
		if(PRXAPI.TASKED_PLAYERS.contains(name)) {
			if(prxAPI.g("commandspam") == null || prxAPI.g("commandspam").isEmpty()) {
				return;
			}
			player.sendMessage(prxAPI.g("commandspam"));
			return;
		}
		PRXAPI.TASKED_PLAYERS.add(name);
		RebirthUpdateEvent e = new RebirthUpdateEvent(player, RebirthUpdateCause.REBIRTHUP);
		Bukkit.getPluginManager().callEvent(e);
		if(e.isCancelled()) {
			PRXAPI.TASKED_PLAYERS.remove(name);
			return;
		}
		Player p = player;
		UUID u = XUUID.tryNameConvert(p.getName());
		String rebirth = prxAPI.getPlayerNextRebirth(u);
		if(!p.hasPermission(main.rebirthCommand.getPermission()) && !p.hasPermission("*")) {
			if(prxAPI.g("nopermission") == null || prxAPI.g("nopermission").isEmpty()) {
				return;
			}
			p.sendMessage(prxAPI.g("nopermission"));
			PRXAPI.TASKED_PLAYERS.remove(name);
			return;
		}
		if(rebirth.equalsIgnoreCase("LASTREBIRTH")) {
			if(prxAPI.h("lastrebirth") == null || prxAPI.h("lastrebirth").isEmpty()) {
				return;
			}
			for(String line : prxAPI.h("lastrebirth")) {
				p.sendMessage(prxAPI.c(line));
			}
			PRXAPI.TASKED_PLAYERS.remove(name);
			return;
		}
		if(!prxAPI.isLastRank(p)) {
			if(prxAPI.g("norebirth") != null && !prxAPI.g("norebirth").isEmpty()) {
				p.sendMessage(prxAPI.cp(prxAPI.g("norebirth"), p));
			}
			PRXAPI.TASKED_PLAYERS.remove(name);
			return;
		}
		if(prxAPI.getPlayerNextRebirthCost(u) > prxAPI.getPlayerMoney(p.getName())) {
			if(prxAPI.h("rebirth-notenoughmoney") == null || prxAPI.h("rebirth-notenoughmoney").isEmpty()) {
				return;
			}
			for(String line : prxAPI.h("rebirth-notenoughmoney")) {
				p.sendMessage(prxAPI.c(line)
						.replace("%nextrebirth%", prxAPI.getPlayerNextRebirth(u)).replace("%nextrebirth_display%", prxAPI.getPlayerNextRebirthDisplayNoFallback(u))
						.replace("%nextrebirth_cost%", prxAPI.s(prxAPI.getPlayerNextRebirthCost(u))).replace("%nextrebirth_cost_formatted%", prxAPI.getPlayerNextRebirthCostFormatted(u)));
			}
			PRXAPI.TASKED_PLAYERS.remove(name);
			return;
		}
		int requiredPrestiges = prxAPI.getRequiredPrestiges(rebirth);
		if(requiredPrestiges > 0) {
			if(requiredPrestiges > prxAPI.getPlayerPrestiges(u)) {
				// ouh
				int left = requiredPrestiges - prxAPI.getPlayerPrestiges(u);
				p.sendMessage(prxAPI.g("rebirth-failed").replace("%prestiges_amount_left%", String.valueOf(left))
						.replace("%prestiges_amount%", String.valueOf(requiredPrestiges)));
				PRXAPI.TASKED_PLAYERS.remove(name);
				return;
			}
		} else {
			if(!prxAPI.hasNextPrestige(u)) {
				p.sendMessage(prxAPI.g("rebirth-failed").replace("%prestiges_amount_left%", "")
						.replace("%prestiges_amount%", ""));
				PRXAPI.TASKED_PLAYERS.remove(name);
				return;
			}
		}
		String rebirthMsg = prxAPI.g("rebirth");
		if(rebirthMsg != null) {
			if(!rebirthMsg.isEmpty()) {
				if(main.globalStorage.getBooleanData("Options.send-rebirthmsg")) {
					p.sendMessage(prxAPI.cp(rebirthMsg
							.replace("%nextrebirth%", prxAPI.getPlayerNextRebirth(u))
							.replace("%nextrebirth_display%", prxAPI.getPlayerNextRebirthDisplayNoFallback(u)), p));
				}
			}
		}
		List<String> addPermissionList = main.rebirthStorage.getAddPermissionList(rebirth);
		if(addPermissionList != null) {
			if(!addPermissionList.isEmpty()) {
				for(String permission : addPermissionList) {
					main.perm.addPermission(name, permission
							.replace("%player%", name)
							.replace("%nextrebirth%", prxAPI.getPlayerNextRebirth(u))
							.replace("%nextrebirth_display%", prxAPI.getPlayerNextRebirthDisplayNoFallback(u)));
				}
			}
		}
		List<String> delPermissionList = main.rebirthStorage.getDelPermissionList(rebirth);
		if(delPermissionList != null) {
			if(!delPermissionList.isEmpty()) {
				for(String permission : delPermissionList) {
					main.perm.delPermission(name, permission
							.replace("%player%", name)
							.replace("%nextrebirth%", prxAPI.getPlayerNextRebirth(u))
							.replace("%nextrebirth_display%", prxAPI.getPlayerNextRebirthDisplayNoFallback(u)));
				}
			}
		}
		List<String> nextRebirthCommands = main.rebirthStorage.getRebirthCommands(rebirth);
		if(nextRebirthCommands != null) {
			if(!nextRebirthCommands.isEmpty()) {
				List<String> newRebirthCommands = new ArrayList<>();
				for(String command : nextRebirthCommands) {
					newRebirthCommands.add(command.replace("%nextrebirth%", prxAPI.getPlayerNextRebirth(u))
							.replace("%nextrebirth_display%", prxAPI.getPlayerNextRebirthDisplayNoFallback(u))
							.replace("%nextrebirth_cost%", prxAPI.s(prxAPI.getPlayerNextRebirthCost(u))));
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
					newActionbarText.add(line.replace("%nextrebirth%", prxAPI.getPlayerNextRebirth(u))
							.replace("%nextrebirth_display%", prxAPI.getPlayerNextRebirthDisplayNoFallbackR(u)));
				}
				int actionbarInterval = main.rebirthStorage.getActionbarInterval(rebirth);
				main.animateActionbar(p, actionbarInterval, newActionbarText);
			}
		}
		List<String> broadcastMessages = main.rebirthStorage.getBroadcast(rebirth);
		if(broadcastMessages != null) {
			if(!broadcastMessages.isEmpty()) {
				for(String messageLine : broadcastMessages) {
					Bukkit.broadcastMessage(prxAPI.cp(messageLine
							.replace("%player%", name)
							.replace("%nextrebirth%", prxAPI.getPlayerNextRebirth(u))
							.replace("%nextrebirth_display%", prxAPI.getPlayerNextRebirthDisplayNoFallback(u)), p));
				}
			}
		}
		List<String> messages = main.rebirthStorage.getMsg(rebirth);
		if(messages != null) {
			if(!messages.isEmpty()) {
				for(String messageLine : messages) {
					p.sendMessage(prxAPI.cp(messageLine
							.replace("%nextrebirth%", prxAPI.getPlayerNextRebirth(u))
							.replace("%nextrebirth_display%", prxAPI.getPlayerNextRebirthDisplayNoFallback(u)), p));
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
					String pCMD = prxAPI.cp(cmd.replace("%player%", name).replace("%nextrebirth%", prxAPI.getPlayerNextRebirth(u)), p);
					replacedCommands.add(pCMD);
				}
				main.executeCommands(p, replacedCommands);
			}
		}
		String nextRebirthSoundName = main.globalStorage.getStringData("Options.rebirthsound-name");
		if(!nextRebirthSoundName.isEmpty() && nextRebirthSoundName.length() > 1) {
			float nextRebirthSoundPitch = (float)main.globalStorage.getDoubleData("Options.rebirthsound-pitch");
			float nextRebirthSoundVolume = (float)main.globalStorage.getDoubleData("Options.rebirthsound-volume");
			p.playSound(p.getLocation(), XSound.matchSound(nextRebirthSoundName), nextRebirthSoundVolume, nextRebirthSoundPitch);
		}
		boolean nextRebirthHologramIsEnable = main.globalStorage.getBooleanData("Holograms.rebirth.enable");
		if(nextRebirthHologramIsEnable && main.hasHologramsPlugin) {
			int nextRebirthHologramRemoveTime = main.globalStorage.getIntegerData("Holograms.rebirth.remove-time");
			int nextRebirthHologramHeight = main.globalStorage.getIntegerData("Holograms.rebirth.height");
			List<String> nextRebirthHologramFormat = main.globalStorage.getStringListData("Holograms.rebirth.format");
			spawnHologram(nextRebirthHologramFormat, nextRebirthHologramRemoveTime, nextRebirthHologramHeight, p);
		}
		main.sendRebirthFirework(p);
		main.econ.withdrawPlayer(name, prxAPI.getPlayerNextRebirthCost(u));
		if(main.globalStorage.getBooleanData("RebirthOptions.ResetMoney")) {
			main.econ.withdrawPlayer(name, prxAPI.getPlayerMoney(name));
		}
		if(main.globalStorage.getBooleanData("RebirthOptions.ResetRank")) {
			RankUpdateEvent xrue = new RankUpdateEvent(p, RankUpdateCause.RANKSET_BYREBIRTH, main.globalStorage.getStringData("defaultrank"));
			Bukkit.getScheduler().runTask(main, () -> {
				Bukkit.getPluginManager().callEvent(xrue);
				if(xrue.isCancelled()) {
					return;
				}
			});
			main.playerStorage.setPlayerRank(u, main.globalStorage.getStringData("defaultrank"));
		}
		if(main.globalStorage.getBooleanData("RebirthOptions.ResetPrestige")) {
			PrestigeUpdateEvent xpue = new PrestigeUpdateEvent(p, PrestigeUpdateCause.SETPRESTIGE_BY_REBIRTH);
			Bukkit.getScheduler().runTask(main, () -> {
				Bukkit.getPluginManager().callEvent(xpue);
				if(xpue.isCancelled()) {
					return;
				}
			});
			main.playerStorage.setPlayerPrestige(u, prxAPI.getFirstPrestige());
		}
		List<String> rebirthCommands = main.globalStorage.getStringListData("RebirthOptions.rebirth-cmds");
		if(!rebirthCommands.isEmpty()) {
			rebirthCommands.forEach(cmd -> {
				if(cmd.startsWith("[rankpermissions]")) {
					prxAPI.allRankAddPermissions.forEach(permission -> {
						main.perm.delPermission(name, permission);
					});
				} else if (cmd.startsWith("[prestigepermissions]")) {
					prxAPI.allPrestigeAddPermissions.forEach(permission -> {
						main.perm.delPermission(name, permission);
					});
				} else if (cmd.startsWith("[rebirthpermissions]")) {
					prxAPI.allRebirthAddPermissions.forEach(permission -> {
						main.perm.delPermission(name, permission);
					});
				} else {
					main.executeCommand(p, cmd);
				}
			});
		}
		main.playerStorage.setPlayerRebirth(u, rebirth);
		PRXAPI.TASKED_PLAYERS.remove(name);
	}

	public void spawnHologram(final List<String> format, final int removeTime, final int height, final Player player) {
		Player p = player;
		String name = p.getName();
		UUID u = XUUID.tryNameConvert(name);
		Hologram hologram = HologramsAPI.createHologram(main, p.getLocation().add(0, height, 0));
		hologram.setAllowPlaceholders(true);
		for(String line : format) {
			String updatedLine = main.getString(line.replace("%player%", name)
					.replace("%player_display%", p.getDisplayName())
					.replace("%nextrebirth%", prxAPI.getPlayerNextRebirth(u))
					.replace("%nextrebirth_display%", main.getString(prxAPI.getPlayerNextRebirthDisplay(u)))
					, name);
			hologram.appendTextLine(updatedLine);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable () {public void run() {
			hologram.delete();
		}}, 20L * removeTime);
	}
}
