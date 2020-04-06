package me.prisonranksx.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import me.prisonranksx.utils.XUUID;
import me.prisonranksx.utils.CompatibleSound.Sounds;

/**
 *  for versions before 1.7
 * @author TheGaming999 | Al3rb | SandwichaKSA
 *
 */
public class RankupLegacy {
	
	private boolean isAutoRankupTaskEnabled;
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	private PRXAPI prxAPI;
	private int autoRankupDelay;
	
	public RankupLegacy() {
		this.prxAPI = main.prxAPI;
		this.autoRankupDelay = main.globalStorage.getIntegerData("Options.autorankup-delay");
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
			
			if(e.isCancelled()) {
				return;
			}
			Player p = player;
			UUID u = XUUID.tryNameConvert(p.getName());
			RankPath rp = prxAPI.getPlayerRankPath(u);
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
				if(!p.hasPermission(main.rankupCommand.getPermission() + "." + prxAPI.getPlayerNextRank(u)) && !p.hasPermission("*")) {
					if(prxAPI.g("rankup-nopermission") == null || prxAPI.g("rankup-nopermission").isEmpty()) {
						sender.sendMessage(prxAPI.g("forcerankup-nopermission").replace("%player%", p.getName()));
						prxAPI.taskedPlayers.remove(p);
						return;
					}
					sender.sendMessage(prxAPI.g("forcerankup-nopermission").replace("%player%", p.getName()));
					p.sendMessage(prxAPI.g("rankup-nopermission")
							.replace("%rankup%", prxAPI.getPlayerNextRank(u))
							.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(u)));
					prxAPI.taskedPlayers.remove(p);
					return;
				}
			}
			if(main.rankStorage.getRankupName(rp).equalsIgnoreCase("LASTRANK")) {
				if(prxAPI.h("lastrank") == null || prxAPI.h("lastrank").isEmpty()) {
					return;
				}
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
							.replace("%rankup%", prxAPI.getPlayerNextRank(u))
							.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(u)), p));
					}
				}
			}
			sender.sendMessage(prxAPI.g("forcerankup-msg").replace("%player%", p.getName()).replace("%rankup%", prxAPI.getPlayerNextRank(u))
					.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(u)));
			List<String> addPermissionList = main.rankStorage.getAddPermissionList(rp);
			if(addPermissionList != null) {
				if(!addPermissionList.isEmpty()) {
					for(String permission : addPermissionList) {
					main.perm.addPermission(p, permission
							.replace("%player%", p.getName())
							.replace("%rankup%", prxAPI.getPlayerNextRank(u))
							.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(u)));
					}
				}
			}
			List<String> delPermissionList = main.rankStorage.getDelPermissionList(rp);
			if(delPermissionList != null) {
				if(!delPermissionList.isEmpty()) {
					for(String permission : delPermissionList) {
						main.perm.delPermission(p, permission
								.replace("%player%", p.getName())
								.replace("%rankup%", prxAPI.getPlayerNextRank(u))
								.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(u)));
					}
				}
			}
			List<String> rankupCommands = main.rankStorage.getRankupCommands(rp);
			if(rankupCommands != null) {
				if(!rankupCommands.isEmpty()) {
					List<String> newRankupCommands = new ArrayList<>();
					for(String command : rankupCommands) {
						newRankupCommands.add(command.replace("%rankup%", prxAPI.getPlayerNextRank(u))
								.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(u))
								.replace("%rankup_cost%", prxAPI.s(prxAPI.getPlayerRankupCostWithIncreaseDirect(u))));
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
		            	newActionbarText.add(line.replace("%rankup%", prxAPI.getPlayerNextRank(u))
		            			.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(u)));
		            }
				     int actionbarInterval = main.rankStorage.getActionbarInterval(rp);
				     main.animateActionbar(p, actionbarInterval, actionbarText);
				}
			}
			List<String> broadcastMessages = main.rankStorage.getBroadcast(rp);
			if(broadcastMessages != null) {
				if(!broadcastMessages.isEmpty()) {
					for(String messageLine : broadcastMessages) {
						p.sendMessage(prxAPI.cp(messageLine
								.replace("%player%", p.getName())
								.replace("%rankup%", prxAPI.getPlayerNextRank(u))
								.replace("%rankup_display%", prxAPI.getPlayerRankupDisplay(u)), p));
					}
				}
			}
			List<String> messages = main.rankStorage.getMsg(rp);
			if(messages != null) {
				if(!messages.isEmpty()) {
					for(String messageLine : messages) {
						p.sendMessage(prxAPI.cp(messageLine
								.replace("%rankup%", prxAPI.getPlayerNextRank(u))
								.replace("%rankup_display%", prxAPI.getPlayerRankupDisplay(u)), p));
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
				String pCMD = prxAPI.cp(cmd.replace("%player%", p.getName()).replace("%rankup%", prxAPI.getPlayerNextRank(u)), p);
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
			main.playerStorage.setPlayerRank(p, main.rankStorage.getRankupName(rp));
			prxAPI.taskedPlayers.remove(p);
			main.getServer().getPluginManager().callEvent(e);
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
			UUID u = XUUID.tryNameConvert(p.getName());
			XRankUpdateEvent e = new XRankUpdateEvent(p, RankUpdateCause.NORMAL_RANKUP, prxAPI.getPlayerNextRank(u));

			if(e.isCancelled()) {
				return;
			}
			
			RankPath rp = prxAPI.getPlayerRankPath(u);
			if(!p.hasPermission(main.rankupCommand.getPermission()) && !p.hasPermission("*")) {
				if(prxAPI.g("nopermission") == null || prxAPI.g("nopermission").isEmpty()) {
					return;
				}
				p.sendMessage(prxAPI.g("nopermission"));
				prxAPI.taskedPlayers.remove(player);
				return;
			}
			if(main.globalStorage.getBooleanData("Options.per-rank-permission")) {
				if(!p.hasPermission(main.rankupCommand.getPermission() + "." + prxAPI.getPlayerNextRank(u)) && !p.hasPermission("*")) {
					if(prxAPI.g("rankup-nopermission") == null || prxAPI.g("rankup-nopermission").isEmpty()) {
						return;
					}
					p.sendMessage(prxAPI.g("rankup-nopermission")
							.replace("%rankup%", prxAPI.getPlayerNextRank(u))
							.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(u)));
					prxAPI.taskedPlayers.remove(player);
					return;
				}
			}
			if(main.rankStorage.getRankupName(rp).equalsIgnoreCase("LASTRANK")) {
				if(prxAPI.h("lastrank") == null || prxAPI.h("lastrank").isEmpty()) {
					return;
				}
				for(String line : prxAPI.h("lastrank")) {
					p.sendMessage(prxAPI.c(line));
				}
				prxAPI.taskedPlayers.remove(player);
				return;
			}
			if(prxAPI.getPlayerRankupCostWithIncreaseDirect(u) > prxAPI.getPlayerMoney(p.getName())) {
				if(prxAPI.h("notenoughmoney") == null || prxAPI.h("notenoughmoney").isEmpty()) {
					return;
				}
				for(String line : prxAPI.h("notenoughmoney")) {
					p.sendMessage(prxAPI.c(line)
							.replace("%rankup%", prxAPI.getPlayerNextRank(u)).replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(u))
							.replace("%rankup_cost%", prxAPI.s(prxAPI.getPlayerRankupCostWithIncreaseDirect(u))).replace("%rankup_cost_formatted%", prxAPI.formatBalance(prxAPI.getPlayerRankupCostWithIncreaseDirect(u))));
				}
				prxAPI.taskedPlayers.remove(player);
				return;
			}
			String rankupMsg = prxAPI.g("rankup");
			if(rankupMsg != null) {
				if(!rankupMsg.isEmpty()) {
					if(main.globalStorage.getBooleanData("Options.send-rankupmsg")) {
					p.sendMessage(prxAPI.cp(rankupMsg
							.replace("%rankup%", prxAPI.getPlayerNextRank(u))
							.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(u)), p));
					}
				}
			}
			List<String> addPermissionList = main.rankStorage.getAddPermissionList(rp);
			if(addPermissionList != null) {
				if(!addPermissionList.isEmpty()) {
					for(String permission : addPermissionList) {
					main.perm.addPermission(player, permission
							.replace("%player%", p.getName())
							.replace("%rankup%", prxAPI.getPlayerNextRank(u))
							.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(u)));
					}
				}
			}
			List<String> delPermissionList = main.rankStorage.getDelPermissionList(rp);
			if(delPermissionList != null) {
				if(!delPermissionList.isEmpty()) {
					for(String permission : delPermissionList) {
						main.perm.delPermission(p, permission
								.replace("%player%", p.getName())
								.replace("%rankup%", prxAPI.getPlayerNextRank(u))
								.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(u)));
					}
				}
			}
			List<String> rankupCommands = main.rankStorage.getRankupCommands(rp);
			if(rankupCommands != null) {
				if(!rankupCommands.isEmpty()) {
					List<String> newRankupCommands = new ArrayList<>();
					for(String command : rankupCommands) {
						newRankupCommands.add(command.replace("%rankup%", prxAPI.getPlayerNextRank(u))
								.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(u))
								.replace("%rankup_cost%", prxAPI.s(prxAPI.getPlayerRankupCostWithIncreaseDirect(u))));
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
		            	newActionbarText.add(line.replace("%rankup%", prxAPI.getPlayerNextRank(u))
		            			.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(u)));
		            }
				     int actionbarInterval = main.rankStorage.getActionbarInterval(rp);
				     main.animateActionbar(p, actionbarInterval, actionbarText);
				}
			}
			List<String> broadcastMessages = main.rankStorage.getBroadcast(rp);
			if(broadcastMessages != null) {
				if(!broadcastMessages.isEmpty()) {
					for(String messageLine : broadcastMessages) {
						p.sendMessage(prxAPI.cp(messageLine
								.replace("%player%", p.getName())
								.replace("%rankup%", prxAPI.getPlayerNextRank(u))
								.replace("%rankup_display%", prxAPI.getPlayerRankupDisplay(u)), p));
					}
				}
			}
			List<String> messages = main.rankStorage.getMsg(rp);
			if(messages != null) {
				if(!messages.isEmpty()) {
					for(String messageLine : messages) {
						p.sendMessage(prxAPI.cp(messageLine
								.replace("%rankup%", prxAPI.getPlayerNextRank(u))
								.replace("%rankup_display%", prxAPI.getPlayerRankupDisplay(u)), p));
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
				String pCMD = prxAPI.cp(cmd.replace("%player%", p.getName()).replace("%rankup%", prxAPI.getPlayerNextRank(u)), p);
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
			main.econ.withdrawPlayer(p, prxAPI.getPlayerRankupCostWithIncreaseDirect(u));
			e.setRankup(main.rankStorage.getRankupName(rp));
			main.playerStorage.setPlayerRank(p, main.rankStorage.getRankupName(rp));
			prxAPI.taskedPlayers.remove(p);
			main.getServer().getPluginManager().callEvent(e);
			});
		}
	
		public void spawnHologram(List<String> format, int removeTime, int height, Player player) {
			Player p = player;
			UUID u = XUUID.tryNameConvert(p.getName());
			Hologram hologram = HologramsAPI.createHologram(main, p.getLocation().add(0, height, 0));
			hologram.setAllowPlaceholders(true);
			for(String line : format) {
				String updatedLine = main.getString(line.replace("%player%", p.getName())
						.replace("%player_display%", p.getDisplayName())
						.replace("%nextrank%", prxAPI.getPlayerNextRank(u))
						.replace("%nextrank_display%", main.getStringWithoutPAPI(prxAPI.getPlayerRankupDisplay(u)))
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
			UUID u = XUUID.tryNameConvert(p.getName());
			Hologram hologram = HologramsAPI.createHologram(main, p.getLocation().add(0, height, 0));
			hologram.setAllowPlaceholders(true);
			for(String line : format) {
				String updatedLine = main.getString(line.replace("%player%", p.getName())
						.replace("%player_display%", p.getDisplayName())
						.replace("%nextrank%", prxAPI.getPlayerNextRank(u))
						.replace("%nextrank_display%", main.getStringWithoutPAPI(prxAPI.getPlayerRankupDisplay(u)))
						, p.getName());
				hologram.appendTextLine(updatedLine);
			}
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable () {public void run() {
            	hologram.delete();
            }}, 20L * removeTime);
			});
		}
		
		public void rankup(Player player, boolean silent) {
			if(prxAPI.taskedPlayers.contains(player)) {
				if(prxAPI.g("commandspam") == null || prxAPI.g("commandspam").isEmpty()) {
					return;
				}
				return;
			}
			prxAPI.taskedPlayers.add(player);
			if(player == null) {
				return;
			}

			Player p = player;
			UUID u = XUUID.tryNameConvert(p.getName());
			RankPath rp = prxAPI.getPlayerRankPath(u);
			XAutoRankupEvent e = new XAutoRankupEvent(p, main.prxAPI.getPlayerNextRank(p), rp.getRankName());
			if(e.isCancelled()) {
				return;
			}
			if(!p.hasPermission(main.rankupCommand.getPermission()) && !p.hasPermission("*")) {
				if(prxAPI.g("nopermission") == null || prxAPI.g("nopermission").isEmpty()) {
					return;
				}
				prxAPI.taskedPlayers.remove(p);
				return;
			}
			if(main.globalStorage.getBooleanData("Options.per-rank-permission")) {
				if(!p.hasPermission(main.rankupCommand.getPermission() + "." + prxAPI.getPlayerNextRank(u)) && !p.hasPermission("*")) {
					if(prxAPI.g("rankup-nopermission") == null || prxAPI.g("rankup-nopermission").isEmpty()) {
						return;
					}
					prxAPI.taskedPlayers.remove(p);
					return;
				}
			}
			if(main.rankStorage.getRankupName(rp).equalsIgnoreCase("LASTRANK")) {
				if(prxAPI.h("lastrank") == null || prxAPI.h("lastrank").isEmpty()) {
					return;
				}
				prxAPI.taskedPlayers.remove(p);
				return;
			}
			if(prxAPI.getPlayerRankupCostWithIncreaseDirect(u) > prxAPI.getPlayerMoney(p.getName())) {
				if(prxAPI.h("notenoughmoney") == null || prxAPI.h("notenoughmoney").isEmpty()) {
					return;
				}
				prxAPI.taskedPlayers.remove(p);
				return;
			}
			String rankupMsg = prxAPI.g("rankup");
			if(rankupMsg != null) {
				if(!rankupMsg.isEmpty()) {
					if(main.globalStorage.getBooleanData("Options.send-rankupmsg")) {
					p.sendMessage(prxAPI.cp(rankupMsg
							.replace("%rankup%", prxAPI.getPlayerNextRank(u))
							.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(u)), p));
					}
				}
			}
			List<String> addPermissionList = main.rankStorage.getAddPermissionList(rp);
			if(addPermissionList != null) {
				if(!addPermissionList.isEmpty()) {
					for(String permission : addPermissionList) {
					main.perm.addPermission(p, permission
							.replace("%player%", p.getName())
							.replace("%rankup%", prxAPI.getPlayerNextRank(u))
							.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(u)));
					}
				}
			}
			List<String> delPermissionList = main.rankStorage.getDelPermissionList(rp);
			if(delPermissionList != null) {
				if(!delPermissionList.isEmpty()) {
					for(String permission : delPermissionList) {
						main.perm.delPermission(p, permission
								.replace("%player%", p.getName())
								.replace("%rankup%", prxAPI.getPlayerNextRank(u))
								.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(u)));
					}
				}
			}
			List<String> rankupCommands = main.rankStorage.getRankupCommands(rp);
			if(rankupCommands != null) {
				if(!rankupCommands.isEmpty()) {
					List<String> newRankupCommands = new ArrayList<>();
					for(String command : rankupCommands) {
						newRankupCommands.add(command.replace("%rankup%", prxAPI.getPlayerNextRank(u))
								.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(u))
								.replace("%rankup_cost%", prxAPI.s(prxAPI.getPlayerRankupCostWithIncreaseDirect(u))));
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
		            	newActionbarText.add(line.replace("%rankup%", prxAPI.getPlayerNextRank(u))
		            			.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(u)));
		            }
				     int actionbarInterval = main.rankStorage.getActionbarInterval(rp);
				     main.animateActionbar(p, actionbarInterval, actionbarText);
				}
			}
			List<String> broadcastMessages = main.rankStorage.getBroadcast(rp);
			if(broadcastMessages != null) {
				if(!broadcastMessages.isEmpty()) {
					for(String messageLine : broadcastMessages) {
						p.sendMessage(prxAPI.cp(messageLine
								.replace("%player%", p.getName())
								.replace("%rankup%", prxAPI.getPlayerNextRank(u))
								.replace("%rankup_display%", prxAPI.getPlayerRankupDisplay(u)), p));
					}
				}
			}
			List<String> messages = main.rankStorage.getMsg(rp);
			if(messages != null) {
				if(!messages.isEmpty()) {
					for(String messageLine : messages) {
						p.sendMessage(prxAPI.cp(messageLine
								.replace("%rankup%", prxAPI.getPlayerNextRank(u))
								.replace("%rankup_display%", prxAPI.getPlayerRankupDisplay(u)), p));
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
				String pCMD = prxAPI.cp(cmd.replace("%player%", p.getName()).replace("%rankup%", prxAPI.getPlayerNextRank(u)), p);
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
			Bukkit.getScheduler().runTaskLater(main, () -> {
			main.sendRankFirework(p);
			main.econ.withdrawPlayer(p, prxAPI.getPlayerRankupCostWithIncreaseDirect(u));
			main.playerStorage.setPlayerRank(p, main.rankStorage.getRankupName(rp));
			prxAPI.taskedPlayers.remove(p);
			}, 1);
			Bukkit.getScheduler().runTask(main, () -> {
			main.getServer().getPluginManager().callEvent(e);
			});
		}
}
