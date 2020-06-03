package me.prisonranksx.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import io.samdev.actionutil.ActionUtil;
import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.data.RankRandomCommands;
import me.prisonranksx.events.RankUpdateCause;
import me.prisonranksx.events.XAutoRankupEvent;
import me.prisonranksx.events.XRankUpdateEvent;
import me.prisonranksx.utils.CompatibleSound.Sounds;

public class Rankup {

	private boolean isAutoRankupTaskEnabled;
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	private PRXAPI prxAPI;
	private int autoRankupDelay;
	private Set<Player> taskedPlayers;
	
	public Rankup() {
		this.prxAPI = main.prxAPI;
		this.autoRankupDelay = prxAPI.numberAPI.limitInverse(main.globalStorage.getIntegerData("Options.autorankup-delay"), 0);
        this.taskedPlayers = new HashSet<>();
	}
	
	private void startAutoRankupTask() {
		if(isAutoRankupTaskEnabled) {
			return;
		}
		isAutoRankupTaskEnabled = true;
		Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
			for(Player player : prxAPI.autoRankupPlayers) {
				this.rankup(player, true);
			}
		}, autoRankupDelay, autoRankupDelay);
	}
	
	public void autoRankup(Player player) {
		Player p = player;
		if(prxAPI.isAutoRankupEnabled(p)) {
			prxAPI.autoRankupPlayers.remove(p);
			if(prxAPI.g("autorankup-disabled") != null && !prxAPI.g("autorankup-disabled").isEmpty()) {
				p.sendMessage(prxAPI.g("autorankup-disabled"));
			}
		} else {
			prxAPI.autoRankupPlayers.add(p);
			startAutoRankupTask();
			if(prxAPI.g("autorankup-enabled") != null && !prxAPI.g("autorankup-enabled").isEmpty()) {
				p.sendMessage(prxAPI.g("autorankup-enabled"));
			}
		}
	}
	
	public void autoRankup(Player player, boolean enable) {
		Player p = player;
		if(prxAPI.isAutoRankupEnabled(p)) {
			if(!enable) {
			prxAPI.autoRankupPlayers.remove(p);
			if(prxAPI.g("autorankup-disabled") != null && !prxAPI.g("autorankup-disabled").isEmpty()) {
				p.sendMessage(prxAPI.g("autorankup-disabled"));
			}
			} else {
				if(prxAPI.g("autorankup-enabled") != null && !prxAPI.g("autorankup-enabled").isEmpty()) {
					p.sendMessage(prxAPI.g("autorankup-enabled"));
				}
			}
		} else {
			if(!enable) {
				if(prxAPI.g("autorankup-disabled") != null && !prxAPI.g("autorankup-disabled").isEmpty()) {
					p.sendMessage(prxAPI.g("autorankup-disabled"));
				}
				return;
			}
			prxAPI.autoRankupPlayers.add(p);
			startAutoRankupTask();
			if(prxAPI.g("autorankup-enabled") != null && !prxAPI.g("autorankup-enabled").isEmpty()) {
				p.sendMessage(prxAPI.g("autorankup-enabled"));
			}
		}
	}
	
	    public void forceRankup(Player player, CommandSender sender) {
	    	if(prxAPI.taskedPlayers.contains(player)) {
				if(prxAPI.g("commandspam") == null || prxAPI.g("commandspam").isEmpty()) {
					return;
				}
				player.sendMessage(prxAPI.g("commandspam"));
				return;
			}
			prxAPI.taskedPlayers.add(player);
			if(player == null) {
				return;
			}
			XRankUpdateEvent e = new XRankUpdateEvent(player, RankUpdateCause.FORCE_RANKUP);
			e.setRankup(prxAPI.getPlayerNextRank(player));
			main.getServer().getPluginManager().callEvent(e);
			if(e.isCancelled()) {
				return;
			}
			Player p = player;
			RankPath rp = prxAPI.getPlayerRankPath(p);
			if(!p.hasPermission(main.rankupCommand.getPermission()) && !p.hasPermission("*")) {
				if(prxAPI.g("nopermission") == null || prxAPI.g("nopermission").isEmpty()) {
					prxAPI.taskedPlayers.remove(p);
					return;
				}
				p.sendMessage(prxAPI.g("nopermission"));
				prxAPI.taskedPlayers.remove(p);
				return;
			}
			if(main.globalStorage.getBooleanData("Options.per-rank-permission")) {
				if(!p.hasPermission(main.rankupCommand.getPermission() + "." + prxAPI.getPlayerNextRank(p)) && !p.hasPermission("*")) {
					if(prxAPI.g("rankup-nopermission") == null || prxAPI.g("rankup-nopermission").isEmpty()) {
						sender.sendMessage(prxAPI.g("forcerankup-nopermission").replace("%player%", p.getName()));
						prxAPI.taskedPlayers.remove(p);
						return;
					}
					sender.sendMessage(prxAPI.g("forcerankup-nopermission").replace("%player%", p.getName()));
					p.sendMessage(prxAPI.g("rankup-nopermission")
							.replace("%rankup%", prxAPI.getPlayerNextRank(p))
							.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(p)));
					prxAPI.taskedPlayers.remove(p);
					return;
				}
			}
			if(main.rankStorage.getRankupName(rp).equalsIgnoreCase("LASTRANK")) {
				sender.sendMessage(prxAPI.g("forcerankup-lastrank").replace("%player%", p.getName()));
				for(String line : prxAPI.h("lastrank")) {
					p.sendMessage(prxAPI.c(line));
				}
				prxAPI.taskedPlayers.remove(p);
				return;
			}
			String rankupMsg = prxAPI.g("rankup");
			if(rankupMsg != null) {
				if(!rankupMsg.isEmpty()) {
					if(main.globalStorage.getBooleanData("Options.send-rankupmsg")) {
					p.sendMessage(prxAPI.cp(rankupMsg
							.replace("%rankup%", prxAPI.getPlayerNextRank(p))
							.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(p)), p));
					}
				}
			}
			sender.sendMessage(prxAPI.g("forcerankup-msg").replace("%player%", p.getName()).replace("%rankup%", prxAPI.getPlayerNextRank(p))
					.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(p)));
			List<String> addPermissionList = main.rankStorage.getAddPermissionList(rp);
			if(addPermissionList != null) {
				if(!addPermissionList.isEmpty()) {
					for(String permission : addPermissionList) {
					main.perm.addPermission(p, permission
							.replace("%player%", p.getName())
							.replace("%rankup%", prxAPI.getPlayerNextRank(p))
							.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(p)));
					}
				}
			}
			List<String> delPermissionList = main.rankStorage.getDelPermissionList(rp);
			if(delPermissionList != null) {
				if(!delPermissionList.isEmpty()) {
					for(String permission : delPermissionList) {
						main.perm.delPermission(p, permission
								.replace("%player%", p.getName())
								.replace("%rankup%", prxAPI.getPlayerNextRank(p))
								.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(p)));
					}
				}
			}
			List<String> rankupCommands = main.rankStorage.getRankupCommands(rp);
			if(rankupCommands != null) {
				if(!rankupCommands.isEmpty()) {
					List<String> newRankupCommands = new ArrayList<>();
					for(String command : rankupCommands) {
						newRankupCommands.add(command.replace("%rankup%", prxAPI.getPlayerNextRank(p))
								.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(p))
								.replace("%rankup_cost%", prxAPI.s(prxAPI.getPlayerRankupCostWithIncrease(p))));
					}
					main.executeCommands(p, newRankupCommands);
				}
			}
			List<String> actions = main.rankStorage.getActions(rp);
			if(actions != null) {
				if(!actions.isEmpty() && main.isActionUtil) {
					ActionUtil.executeActions(p, actions);
				}
			}
			List<String> actionbarText = main.rankStorage.getActionbarMessages(rp);
			if(actionbarText != null) {
				if(!actionbarText.isEmpty()) {
					List<String> newActionbarText = new LinkedList<>();
		            for(String line : actionbarText) {
		            	newActionbarText.add(line.replace("%rankup%", prxAPI.getPlayerNextRank(p))
		            			.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(p)));
		            }
				     int actionbarInterval = main.rankStorage.getActionbarInterval(rp);
				     main.animateActionbar(p, actionbarInterval, actionbarText);
				}
			}
			List<String> broadcastMessages = main.rankStorage.getBroadcast(rp);
			if(broadcastMessages != null) {
				if(!broadcastMessages.isEmpty()) {
					for(String messageLine : broadcastMessages) {
						Bukkit.broadcastMessage(prxAPI.cp(messageLine
								.replace("%player%", p.getName())
								.replace("%rankup%", prxAPI.getPlayerNextRank(p))
								.replace("%rankup_display%", prxAPI.getPlayerRankupDisplay(p)), p));
					}
				}
			}
			List<String> messages = main.rankStorage.getMsg(rp);
			if(messages != null) {
				if(!messages.isEmpty()) {
					for(String messageLine : messages) {
						p.sendMessage(prxAPI.cp(messageLine
								.replace("%rankup%", prxAPI.getPlayerNextRank(p))
								.replace("%rankup_display%", prxAPI.getPlayerRankupDisplay(p)), p));
					}
				}
			}
			Map<String, Double> chances = new HashMap<String, Double>();
			RankRandomCommands rrc = main.rankStorage.getRandomCommandsManager(rp);
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
				String pCMD = prxAPI.cp(cmd.replace("%player%", p.getName()).replace("%rankup%", prxAPI.getPlayerNextRank(p)), p);
				replacedCommands.add(pCMD);
			}
			main.executeCommands(p, replacedCommands);
			}
			}
			String rankupSoundName = main.globalStorage.getStringData("Options.rankupsound-name");
			if(!rankupSoundName.isEmpty() && rankupSoundName.length() > 1) {
				float rankupSoundPitch = (float)main.globalStorage.getDoubleData("Options.rankupsound-pitch");
			    float rankupSoundVolume = (float)main.globalStorage.getDoubleData("Options.rankupsound-volume");
				p.playSound(p.getLocation(), Sounds.valueOf(rankupSoundName).bukkitSound(), rankupSoundVolume, rankupSoundPitch);
			}
			boolean rankupHologramIsEnable = main.globalStorage.getBooleanData("Holograms.rankup.enable");
			if(rankupHologramIsEnable && main.isholo) {
				int rankupHologramRemoveTime = main.globalStorage.getIntegerData("Holograms.rankup.remove-time");
				int rankupHologramHeight = main.globalStorage.getIntegerData("Holograms.rankup.height");
				List<String> rankupHologramFormat = main.globalStorage.getStringListData("Holograms.rankup.format");
				spawnHologram(rankupHologramFormat, rankupHologramRemoveTime, rankupHologramHeight, p);
			}
			main.sendRankFirework(p);
			e.setRankup(main.rankStorage.getRankupName(rp));
			Bukkit.getScheduler().runTaskLater(main, () -> {
			main.playerStorage.setPlayerRank(p, main.rankStorage.getRankupName(rp));
			prxAPI.taskedPlayers.remove(p);
			}, 1);
	    }
	
		public void rankup(Player player) {
			Bukkit.getScheduler().runTask(main, () -> {
			if(prxAPI.taskedPlayers.contains(player)) {
				if(prxAPI.g("commandspam") == null || prxAPI.g("commandspam").isEmpty()) {
					return;
				}
				player.sendMessage(prxAPI.g("commandspam"));
				return;
			}
			prxAPI.taskedPlayers.add(player);
			if(player == null) {
				return;
			}
			Player p = player;
			XRankUpdateEvent e = new XRankUpdateEvent(p, RankUpdateCause.NORMAL_RANKUP, prxAPI.getPlayerNextRank(p));
            

			
			RankPath rp = prxAPI.getPlayerRankPath(p);
			// time to cache.
			String currentRank = rp.getRankName();
			String nextRank = prxAPI.getPlayerNextRank(p);
			e.setRankup(nextRank);
			main.getServer().getPluginManager().callEvent(e);
			if(e.isCancelled()) {
				return;
			}
			if(!p.hasPermission(main.rankupCommand.getPermission()) && !p.hasPermission("*")) {
				if(prxAPI.g("nopermission") != null && !prxAPI.g("nopermission").isEmpty()) {
				p.sendMessage(prxAPI.g("nopermission"));
				}
				prxAPI.taskedPlayers.remove(p);
				return;
			}
			if(main.rankStorage.getRankupName(rp).equalsIgnoreCase("LASTRANK")) {
				if(prxAPI.h("lastrank") != null && !prxAPI.h("lastrank").isEmpty()) {
				for(String line : prxAPI.h("lastrank")) {
					p.sendMessage(prxAPI.c(line));
				}
				}
				prxAPI.taskedPlayers.remove(p);
				return;
			}
			String nextRankDisplay = prxAPI.getPlayerRankupDisplayR(p);
			double rankupCostWithIncrease = prxAPI.getPlayerRankupCostWithIncreaseDirect(p);
			if(main.globalStorage.getBooleanData("Options.per-rank-permission")) {
				if(!p.hasPermission(main.rankupCommand.getPermission() + "." + nextRank) && !p.hasPermission("*")) {
					if(prxAPI.g("rankup-nopermission") != null && !prxAPI.g("rankup-nopermission").isEmpty()) {
					p.sendMessage(prxAPI.g("rankup-nopermission")
							.replace("%rankup%", nextRank)
							.replace("%rankup_display%", nextRankDisplay)
							.replace("%rank%", currentRank));
					}
					prxAPI.taskedPlayers.remove(p);
					return;
				}
			}

			if(rankupCostWithIncrease > prxAPI.getPlayerMoney(p)) {
				if(prxAPI.h("notenoughmoney") == null || prxAPI.h("notenoughmoney").isEmpty()) {
					return;
				}
				for(String line : prxAPI.h("notenoughmoney")) {
					p.sendMessage(prxAPI.c(line)
							.replace("%rankup%", nextRank).replace("%rankup_display%", nextRankDisplay).replace("%rank%", currentRank)
							.replace("%rankup_cost%", prxAPI.s(rankupCostWithIncrease)).replace("%rankup_cost_formatted%", prxAPI.formatBalance(rankupCostWithIncrease)));
				}
				prxAPI.taskedPlayers.remove(p);
				return;
			}
			String rankupMsg = prxAPI.g("rankup");
			if(rankupMsg != null) {
				if(!rankupMsg.isEmpty()) {
					if(main.globalStorage.getBooleanData("Options.send-rankupmsg")) {
					p.sendMessage(prxAPI.cp(rankupMsg
							.replace("%rankup%", nextRank)
							.replace("%rank%", currentRank)
							.replace("%rankup_display%", nextRankDisplay), p));
					}
				}
			}
			List<String> addPermissionList = main.rankStorage.getAddPermissionList(rp);
			if(addPermissionList != null) {
				if(!addPermissionList.isEmpty()) {
					for(String permission : addPermissionList) {
					main.perm.addPermission(p, permission
							.replace("%player%", p.getName())
							.replace("%rankup%", nextRank)
							.replace("%rank%", currentRank)
							.replace("%rankup_display%", nextRankDisplay));
					}
				}
			}
			List<String> delPermissionList = main.rankStorage.getDelPermissionList(rp);
			if(delPermissionList != null) {
				if(!delPermissionList.isEmpty()) {
					for(String permission : delPermissionList) {
						main.perm.delPermission(p, permission
								.replace("%player%", p.getName())
								.replace("%rankup%", nextRank)
								.replace("%rank%", currentRank)
								.replace("%rankup_display%", nextRankDisplay));
					}
				}
			}
			List<String> rankupCommands = main.rankStorage.getRankupCommands(rp);
			if(rankupCommands != null) {
				if(!rankupCommands.isEmpty()) {
					List<String> newRankupCommands = new ArrayList<>();
					for(String command : rankupCommands) {
						newRankupCommands.add(command.replace("%rankup%", nextRank)
								.replace("%rankup_display%", nextRankDisplay)
								.replace("%rank%", currentRank)
								.replace("%rankup_cost%", prxAPI.s(rankupCostWithIncrease)));
					}
					main.executeCommands(p, newRankupCommands);
				}
			}
			List<String> actions = main.rankStorage.getActions(rp);
			if(actions != null) {
				if(!actions.isEmpty() && main.isActionUtil) {
					ActionUtil.executeActions(p, actions);
				}
			}
			List<String> actionbarText = main.rankStorage.getActionbarMessages(rp);
			if(actionbarText != null) {
				if(!actionbarText.isEmpty()) {
					List<String> newActionbarText = new LinkedList<>();
		            for(String line : actionbarText) {
		            	newActionbarText.add(line.replace("%rankup%", nextRank)
		            			.replace("%rank%", currentRank)
		            			.replace("%rankup_display%", nextRankDisplay));
		            }
				     int actionbarInterval = main.rankStorage.getActionbarInterval(rp);
				     main.animateActionbar(p, actionbarInterval, actionbarText);
				}
			}
			List<String> broadcastMessages = main.rankStorage.getBroadcast(rp);
			if(broadcastMessages != null) {
				if(!broadcastMessages.isEmpty()) {
					for(String messageLine : broadcastMessages) {
						Bukkit.broadcastMessage(prxAPI.cp(messageLine
								.replace("%player%", p.getName())
								.replace("%rankup%", nextRank)
								.replace("%rank%", currentRank)
								.replace("%rankup_display%", nextRankDisplay), p));
					}
				}
			}
			List<String> messages = main.rankStorage.getMsg(rp);
			if(messages != null) {
				if(!messages.isEmpty()) {
					for(String messageLine : messages) {
						p.sendMessage(prxAPI.cp(messageLine
								.replace("%rankup%", nextRank)
								.replace("%rank%", currentRank)
								.replace("%rankup_display%", nextRankDisplay), p));
					}
				}
			}
			Map<String, Double> chances = new HashMap<String, Double>();
			RankRandomCommands rrc = main.rankStorage.getRandomCommandsManager(rp);
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
				String pCMD = prxAPI.cp(cmd.replace("%player%", p.getName())
						.replace("%rank%", currentRank)
						.replace("%rankup%", nextRank), p);
				replacedCommands.add(pCMD);
			}
			main.executeCommands(p, replacedCommands);
			}
			}
			String rankupSoundName = main.globalStorage.getStringData("Options.rankupsound-name");
			if(!rankupSoundName.isEmpty() && rankupSoundName.length() > 1) {
				float rankupSoundPitch = (float)main.globalStorage.getDoubleData("Options.rankupsound-pitch");
			    float rankupSoundVolume = (float)main.globalStorage.getDoubleData("Options.rankupsound-volume");
				p.playSound(p.getLocation(), Sounds.valueOf(rankupSoundName).bukkitSound(), rankupSoundVolume, rankupSoundPitch);
			}
			boolean rankupHologramIsEnable = main.globalStorage.getBooleanData("Holograms.rankup.enable");
			if(rankupHologramIsEnable && main.isholo) {
				int rankupHologramRemoveTime = main.globalStorage.getIntegerData("Holograms.rankup.remove-time");
				int rankupHologramHeight = main.globalStorage.getIntegerData("Holograms.rankup.height");
				List<String> rankupHologramFormat = main.globalStorage.getStringListData("Holograms.rankup.format");
				spawnHologram(rankupHologramFormat, rankupHologramRemoveTime, rankupHologramHeight, p);
			}
			main.sendRankFirework(p);
			main.econ.withdrawPlayer(p, rankupCostWithIncrease);
			e.setRankup(main.rankStorage.getRankupName(rp));
			Bukkit.getScheduler().runTaskLater(main, () -> {
			main.playerStorage.setPlayerRank(p, main.rankStorage.getRankupName(rp));
			prxAPI.taskedPlayers.remove(p);
			
			}, 1);
			});
		}
	
		public void spawnHologram(List<String> format, int removeTime, int height, Player player) {
			Player p = player;
			Hologram hologram = HologramsAPI.createHologram(main, p.getLocation().add(0, height, 0));
			hologram.setAllowPlaceholders(true);
			for(String line : format) {
				String updatedLine = main.getString(line.replace("%player%", p.getName())
						.replace("%player_display%", p.getDisplayName())
						.replace("%nextrank%", prxAPI.getPlayerNextRank(p))
						.replace("%nextrank_display%", main.getStringWithoutPAPI(prxAPI.getPlayerRankupDisplay(p)))
						, p.getName());
				hologram.appendTextLine(updatedLine);
			}
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable () {public void run() {
            	hologram.delete();
            }}, 20L * removeTime);
		}
		
		public void spawnHologramAsync(List<String> format, int removeTime, int height, Player player) {
			Bukkit.getScheduler().runTask(main, () -> {
			Player p = player;
			Hologram hologram = HologramsAPI.createHologram(main, p.getLocation().add(0, height, 0));
			hologram.setAllowPlaceholders(true);
			for(String line : format) {
				String updatedLine = main.getString(line.replace("%player%", p.getName())
						.replace("%player_display%", p.getDisplayName())
						.replace("%nextrank%", prxAPI.getPlayerNextRank(p))
						.replace("%nextrank_display%", main.getStringWithoutPAPI(prxAPI.getPlayerRankupDisplay(p)))
						, p.getName());
				hologram.appendTextLine(updatedLine);
			}
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable () {public void run() {
            	hologram.delete();
            }}, 20L * removeTime);
			});
		}
		
		public void rankup(Player player, boolean silent) {
			Player p = player;
			if(getTaskedPlayers().contains(p)) {
				return;
			}
			getTaskedPlayers().add(p);
			RankPath rp = prxAPI.getPlayerRankPath(p);
			String currentRank = rp.getRankName();
			String nextRank = prxAPI.getPlayerNextRank(p);
			
			
			XAutoRankupEvent e = new XAutoRankupEvent(p, nextRank, rp.getRankName());
			Bukkit.getScheduler().runTask(main, () -> {
			main.getServer().getPluginManager().callEvent(e);
			});
			if(e.isCancelled()) {
				getTaskedPlayers().remove(p);
				return;
			}
			if(!p.hasPermission(main.rankupCommand.getPermission()) && !p.hasPermission("*")) {
                getTaskedPlayers().remove(p);
				return;
			}
			if(main.globalStorage.getBooleanData("Options.per-rank-permission")) {
				if(!p.hasPermission(main.rankupCommand.getPermission() + "." + nextRank) && !p.hasPermission("*")) {
					getTaskedPlayers().remove(p);
					return;
				}
			}
			if(main.rankStorage.getRankupName(rp).equalsIgnoreCase("LASTRANK")) {
				getTaskedPlayers().remove(p);
				return;
			}
			String nextRankDisplay = prxAPI.getPlayerRankupDisplayR(p);
			double rankupCostWithIncrease = prxAPI.getPlayerRankupCostWithIncreaseDirect(p);
			if(rankupCostWithIncrease > prxAPI.getPlayerMoney(p)) {
				getTaskedPlayers().remove(p);
				return;
			}
			String rankupMsg = prxAPI.g("rankup");
			if(rankupMsg != null) {
				if(!rankupMsg.isEmpty()) {
					if(main.globalStorage.getBooleanData("Options.send-rankupmsg")) {
					p.sendMessage(prxAPI.cp(rankupMsg
							.replace("%rankup%", nextRank)
							.replace("%rank%", currentRank)
							.replace("%rankup_display%", nextRankDisplay), p));
					}
				}
			}
			List<String> addPermissionList = main.rankStorage.getAddPermissionList(rp);
			if(addPermissionList != null) {
				if(!addPermissionList.isEmpty()) {
					for(String permission : addPermissionList) {
					main.perm.addPermission(p, permission
							.replace("%player%", p.getName())
							.replace("%rankup%", nextRank)
							.replace("%rank%", currentRank)
							.replace("%rankup_display%", nextRankDisplay));
					}
				}
			}
			List<String> delPermissionList = main.rankStorage.getDelPermissionList(rp);
			if(delPermissionList != null) {
				if(!delPermissionList.isEmpty()) {
					for(String permission : delPermissionList) {
						main.perm.delPermission(p, permission
								.replace("%player%", p.getName())
								.replace("%rankup%", nextRank)
								.replace("%rank%", currentRank)
								.replace("%rankup_display%", nextRankDisplay));
					}
				}
			}
			List<String> rankupCommands = main.rankStorage.getRankupCommands(rp);
			if(rankupCommands != null) {
				if(!rankupCommands.isEmpty()) {
					List<String> newRankupCommands = new ArrayList<>();
					for(String command : rankupCommands) {
						newRankupCommands.add(command.replace("%rankup%", nextRank)
								.replace("%rankup_display%", nextRankDisplay)
								.replace("%rank%", currentRank)
								.replace("%rankup_cost%", prxAPI.s(rankupCostWithIncrease)));
					}
					main.executeCommands(p, newRankupCommands);
				}
			}
			List<String> actions = main.rankStorage.getActions(rp);
			if(actions != null) {
				if(!actions.isEmpty() && main.isActionUtil) {
					ActionUtil.executeActions(p, actions);
				}
			}
			List<String> actionbarText = main.rankStorage.getActionbarMessages(rp);
			if(actionbarText != null) {
				if(!actionbarText.isEmpty()) {
					List<String> newActionbarText = new LinkedList<>();
		            for(String line : actionbarText) {
		            	newActionbarText.add(line.replace("%rankup%", nextRank)
		            			.replace("%rank%", currentRank)
		            			.replace("%rankup_display%", nextRankDisplay));
		            }
				     int actionbarInterval = main.rankStorage.getActionbarInterval(rp);
				     main.animateActionbar(p, actionbarInterval, actionbarText);
				}
			}
			List<String> broadcastMessages = main.rankStorage.getBroadcast(rp);
			if(broadcastMessages != null) {
				if(!broadcastMessages.isEmpty()) {
					for(String messageLine : broadcastMessages) {
						Bukkit.broadcastMessage(prxAPI.cp(messageLine
								.replace("%player%", p.getName())
								.replace("%rankup%", nextRank)
								.replace("%rank%", currentRank)
								.replace("%rankup_display%", nextRankDisplay), p));
					}
				}
			}
			List<String> messages = main.rankStorage.getMsg(rp);
			if(messages != null) {
				if(!messages.isEmpty()) {
					for(String messageLine : messages) {
						p.sendMessage(prxAPI.cp(messageLine
								.replace("%rankup%", nextRank)
								.replace("%rank%", currentRank)
								.replace("%rankup_display%", nextRankDisplay), p));
					}
				}
			}
			Map<String, Double> chances = new HashMap<String, Double>();
			RankRandomCommands rrc = main.rankStorage.getRandomCommandsManager(rp);
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
				String pCMD = prxAPI.cp(cmd.replace("%player%", p.getName())
						.replace("%rank%", currentRank).replace("%rankup%", nextRank), p);
				replacedCommands.add(pCMD);
			}
			main.executeCommands(p, replacedCommands);
			}
			}
			String rankupSoundName = main.globalStorage.getStringData("Options.rankupsound-name");
			if(!rankupSoundName.isEmpty() && rankupSoundName.length() > 1) {
				float rankupSoundPitch = (float)main.globalStorage.getDoubleData("Options.rankupsound-pitch");
			    float rankupSoundVolume = (float)main.globalStorage.getDoubleData("Options.rankupsound-volume");
				p.playSound(p.getLocation(), Sounds.valueOf(rankupSoundName).bukkitSound(), rankupSoundVolume, rankupSoundPitch);
			}
			boolean rankupHologramIsEnable = main.globalStorage.getBooleanData("Holograms.rankup.enable");
			if(rankupHologramIsEnable && main.isholo) {
				int rankupHologramRemoveTime = main.globalStorage.getIntegerData("Holograms.rankup.remove-time");
				int rankupHologramHeight = main.globalStorage.getIntegerData("Holograms.rankup.height");
				List<String> rankupHologramFormat = main.globalStorage.getStringListData("Holograms.rankup.format");
				spawnHologramAsync(rankupHologramFormat, rankupHologramRemoveTime, rankupHologramHeight, p);
			}
			main.sendRankFirework(p);
			main.econ.withdrawPlayer(p, rankupCostWithIncrease);
			Bukkit.getScheduler().runTaskLater(main, () -> {
			main.playerStorage.setPlayerRank(p, main.rankStorage.getRankupName(rp));
			getTaskedPlayers().remove(p);
			}, 1);

		}

		public Set<Player> getTaskedPlayers() {
			return taskedPlayers;
		}

		public void setTaskedPlayers(Set<Player> taskedPlayers) {
			this.taskedPlayers = taskedPlayers;
		}
	
}
