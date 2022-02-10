package me.prisonranksx.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.samdev.actionutil.ActionUtil;
import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.data.RankRandomCommands;
import me.prisonranksx.events.RankUpdateCause;
import me.prisonranksx.events.AsyncAutoRankupEvent;
import me.prisonranksx.events.RankUpdateEvent;
import me.prisonranksx.hooks.IHologram;
import me.prisonranksx.utils.OnlinePlayers;
import me.prisonranksx.utils.XSound;

public class Rankup {

	private boolean isAutoRankupTaskEnabled;
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	private PRXAPI prxAPI;
	private int autoRankupDelay;
	private Set<String> taskedPlayers;

	public Rankup() {
		this.prxAPI = main.prxAPI;
		this.autoRankupDelay = main.globalStorage.getIntegerData("Options.autorankup-delay");
		this.taskedPlayers = new HashSet<>();
	}

	private void startAutoRankupTask() {
		if(isAutoRankupTaskEnabled) {
			return;
		}
		isAutoRankupTaskEnabled = true;
		Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
			for(String playerName : PRXAPI.AUTO_RANKUP_PLAYERS) {
				this.rankup(Bukkit.getPlayer(playerName), true);
			}
		}, autoRankupDelay, autoRankupDelay);
	}

	public boolean autoRankup(Player player) {
		Player p = player;
		String name = p.getName();
		if(prxAPI.isAutoRankupEnabled(p)) {
			PRXAPI.AUTO_RANKUP_PLAYERS.remove(name);
			if(prxAPI.g("autorankup-disabled") != null && !prxAPI.g("autorankup-disabled").isEmpty()) {
				p.sendMessage(prxAPI.g("autorankup-disabled"));
			}
			return false;
		} else {
			PRXAPI.AUTO_RANKUP_PLAYERS.add(name);
			startAutoRankupTask();
			if(prxAPI.g("autorankup-enabled") != null && !prxAPI.g("autorankup-enabled").isEmpty()) {
				p.sendMessage(prxAPI.g("autorankup-enabled"));
			}
			return true;
		}
	}

	public void autoRankup(Player player, boolean enable) {
		Player p = player;
		String name = p.getName();
		if(prxAPI.isAutoRankupEnabled(p)) {
			if(!enable) {
				PRXAPI.AUTO_RANKUP_PLAYERS.remove(name);
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
				PRXAPI.AUTO_RANKUP_PLAYERS.remove(name);
				if(prxAPI.g("autorankup-disabled") != null && !prxAPI.g("autorankup-disabled").isEmpty()) {
					p.sendMessage(prxAPI.g("autorankup-disabled"));
				}
				return;
			}
			PRXAPI.AUTO_RANKUP_PLAYERS.add(name);
			startAutoRankupTask();
			if(prxAPI.g("autorankup-enabled") != null && !prxAPI.g("autorankup-enabled").isEmpty()) {
				p.sendMessage(prxAPI.g("autorankup-enabled"));
			}
		}
	}

	public void forceRankup(final Player player, final CommandSender sender) {
		if(player == null) {
			sender.sendMessage(prxAPI.c("&cPlayer is null"));
			return;
		}
		String name = player.getName();
		if(PRXAPI.TASKED_PLAYERS.contains(name)) {
			if(prxAPI.g("commandspam") != null && !prxAPI.g("commandspam").isEmpty()) {	
				sender.sendMessage(prxAPI.g("commandspam"));
			}
			return;
		}
		PRXAPI.TASKED_PLAYERS.add(name);

		Player p = player;
		RankPath rp = prxAPI.getPlayerRankPath(p);
		String currentRank = rp.getRankName();
		if(!p.hasPermission(main.rankupCommand.getPermission()) && !p.hasPermission("*")) {
			if(prxAPI.g("nopermission") == null || prxAPI.g("nopermission").isEmpty()) {
				PRXAPI.TASKED_PLAYERS.remove(name);
				return;
			}
			p.sendMessage(prxAPI.g("nopermission"));
			PRXAPI.TASKED_PLAYERS.remove(name);
			return;
		}
		if(main.globalStorage.getBooleanData("Options.per-rank-permission")) {
			if(!p.hasPermission(main.rankupCommand.getPermission() + "." + prxAPI.getPlayerNextRank(p)) && !p.hasPermission("*")) {
				if(prxAPI.g("rankup-nopermission") == null || prxAPI.g("rankup-nopermission").isEmpty()) {
					if(sender != null) {
						sender.sendMessage(prxAPI.g("forcerankup-nopermission").replace("%player%", p.getName()));
						PRXAPI.TASKED_PLAYERS.remove(name);
						return;
					}
				}
				if(sender != null) {
					sender.sendMessage(prxAPI.g("forcerankup-nopermission").replace("%player%", p.getName()));
					p.sendMessage(prxAPI.g("rankup-nopermission")
							.replace("%rankup%", prxAPI.getPlayerNextRank(p))
							.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(p)));
					PRXAPI.TASKED_PLAYERS.remove(name);
					return;
				}
			}
		}
		if(main.rankStorage.getRankupName(rp).equalsIgnoreCase("LASTRANK")) {
			if(sender != null) {
				sender.sendMessage(prxAPI.g("forcerankup-lastrank").replace("%player%", name));
				for(String line : prxAPI.h("lastrank")) {
					p.sendMessage(prxAPI.c(line));
				}
				PRXAPI.TASKED_PLAYERS.remove(name);
				return;
			}
		}
		RankUpdateEvent e = new RankUpdateEvent(player, RankUpdateCause.FORCE_RANKUP);
		e.setRankup(prxAPI.getPlayerNextRank(player));
		main.getServer().getPluginManager().callEvent(e);
		if(e.isCancelled()) {
			PRXAPI.TASKED_PLAYERS.remove(name);
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
		if(sender != null)
			sender.sendMessage(prxAPI.g("forcerankup-msg").replace("%player%", name).replace("%rankup%", prxAPI.getPlayerNextRank(p))
					.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(p)));
		List<String> addPermissionList = main.rankStorage.getAddPermissionList(rp);
		if(addPermissionList != null) {
			if(!addPermissionList.isEmpty()) {
				for(String permission : addPermissionList) {
					main.perm.addPermission(p, permission
							.replace("%player%", name)
							.replace("%rankup%", prxAPI.getPlayerNextRank(p))
							.replace("%rank%", currentRank)
							.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(p)));
				}
			}
		}
		List<String> delPermissionList = main.rankStorage.getDelPermissionList(rp);
		if(delPermissionList != null) {
			if(!delPermissionList.isEmpty()) {
				for(String permission : delPermissionList) {
					main.perm.delPermission(p, permission
							.replace("%player%", name)
							.replace("%rankup%", prxAPI.getPlayerNextRank(p))
							.replace("%rank%", currentRank)
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
							.replace("%rank%", currentRank)
							.replace("%rankup_cost%", prxAPI.s(prxAPI.getPlayerRankupCostWithIncreaseDirect(p))));
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
					newActionbarText.add(line.replace("%rank%", currentRank).replace("%rankup%", prxAPI.getPlayerNextRank(p))
							.replace("%rankup_display%", prxAPI.getPlayerRankupDisplayR(p)));
				}
				int actionbarInterval = main.rankStorage.getActionbarInterval(rp);
				main.animateActionbar(p, actionbarInterval, actionbarText);
			}
		}
		List<String> broadcastMessages = main.rankStorage.getBroadcast(rp);
		if(broadcastMessages != null) {
			if(!broadcastMessages.isEmpty()) {
				OnlinePlayers.getPlayers().forEach(ap -> {
					if(main.isInDisabledWorld(ap)) return;
					for(String messageLine : broadcastMessages) {
						ap.sendMessage(prxAPI.cp(messageLine
								.replace("%player%", name)
								.replace("%rankup%", prxAPI.getPlayerNextRank(p))
								.replace("%rank%", prxAPI.getPlayerRank(p))
								.replace("%rankup_display%", prxAPI.getPlayerRankupDisplay(p)), p));
					}
				});
			}
		}
		List<String> messages = main.rankStorage.getMsg(rp);
		if(messages != null) {
			if(!messages.isEmpty()) {
				for(String messageLine : messages) {
					p.sendMessage(prxAPI.cp(messageLine
							.replace("%rankup%", prxAPI.getPlayerNextRank(p))
							.replace("%rank%", currentRank)
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
					String pCMD = prxAPI.cp(cmd.replace("%player%", p.getName()).replace("%rank%", currentRank).replace("%rankup%", prxAPI.getPlayerNextRank(p)), p);
					replacedCommands.add(pCMD);
				}
				main.executeCommands(p, replacedCommands);
			}
		}
		String rankupSoundName = main.globalStorage.getStringData("Options.rankupsound-name");
		if(!rankupSoundName.isEmpty() && rankupSoundName.length() > 1) {
			float rankupSoundPitch = (float)main.globalStorage.getDoubleData("Options.rankupsound-pitch");
			float rankupSoundVolume = (float)main.globalStorage.getDoubleData("Options.rankupsound-volume");
			p.playSound(p.getLocation(), XSound.matchSound(rankupSoundName), rankupSoundVolume, rankupSoundPitch);
		}
		boolean rankupHologramIsEnable = main.globalStorage.getBooleanData("Holograms.rankup.enable");
		if(rankupHologramIsEnable && main.hasHologramsPlugin) {
			int rankupHologramRemoveTime = main.globalStorage.getIntegerData("Holograms.rankup.remove-time");
			int rankupHologramHeight = main.globalStorage.getIntegerData("Holograms.rankup.height");
			List<String> rankupHologramFormat = main.globalStorage.getStringListData("Holograms.rankup.format");
			try {
				spawnHologram(rankupHologramFormat, rankupHologramRemoveTime, rankupHologramHeight, p);
			} catch (IllegalArgumentException | InterruptedException | ExecutionException e1) {
				e1.printStackTrace();
			}
		}
		main.sendRankFirework(p);
		e.setRankup(main.rankStorage.getRankupName(rp));
		Bukkit.getScheduler().runTaskLater(main, () -> {
			main.playerStorage.setPlayerRank(p, main.rankStorage.getRankupName(rp));
			PRXAPI.TASKED_PLAYERS.remove(name);
		}, 1);
	}

	public void rankup(final Player player) {
		String name = player.getName();
		Bukkit.getScheduler().runTask(main, () -> {
			if(PRXAPI.TASKED_PLAYERS.contains(name)) {
				if(prxAPI.g("commandspam") != null && !prxAPI.g("commandspam").isEmpty()) {
					player.sendMessage(prxAPI.g("commandspam"));
				}
				return;
			}
			PRXAPI.TASKED_PLAYERS.add(name);
			Player p = player;
			RankUpdateEvent e = new RankUpdateEvent(p, RankUpdateCause.NORMAL_RANKUP, prxAPI.getPlayerNextRank(p));
			RankPath rp = prxAPI.getPlayerRankPath(p);
			String currentRank = rp.getRankName();
			String nextRank = prxAPI.getPlayerNextRank(p);
			e.setRankup(nextRank);

			if(!p.hasPermission(main.rankupCommand.getPermission()) && !p.hasPermission("*")) {
				if(prxAPI.g("nopermission") != null && !prxAPI.g("nopermission").isEmpty()) {
					p.sendMessage(prxAPI.g("nopermission"));
				}
				PRXAPI.TASKED_PLAYERS.remove(name);
				return;
			}
			if(main.rankStorage.getRankupName(rp).equalsIgnoreCase("LASTRANK")) {
				if(prxAPI.h("lastrank") != null && !prxAPI.h("lastrank").isEmpty()) {
					for(String line : prxAPI.h("lastrank")) {
						p.sendMessage(prxAPI.c(line));
					}
				}
				PRXAPI.TASKED_PLAYERS.remove(name);
				return;
			}

			String nextRankDisplay = prxAPI.getPlayerRankupDisplayR(p);
			if(!main.prxAPI.rankExists(nextRank)) {
				if(!p.isOp()) {
					p.sendMessage(prxAPI.c("&cRankup failed! Please inform the administrators."));
				} else {
					p.sendMessage(prxAPI.c("&cRankup failed! Please take a look at the console for error logs. Try running /prx scan."));
				}
				return;
			}
			double rankupCostWithIncrease = prxAPI.getPlayerRankupCostWithIncreaseDirect(p);
			Map<String, String> stringRequirements = prxAPI.getRankStringRequirements(rp);
			Map<String, Double> numberRequirements = prxAPI.getRankNumberRequirements(rp);
			List<String> customRequirementMessage = prxAPI.getRankCustomRequirementMessage(rp);

			if(main.globalStorage.getBooleanData("Options.per-rank-permission")) {
				if(!p.hasPermission(main.rankupCommand.getPermission() + "." + nextRank) && !p.hasPermission("*")) {
					if(prxAPI.g("rankup-nopermission") != null && !prxAPI.g("rankup-nopermission").isEmpty()) {
						p.sendMessage(prxAPI.g("rankup-nopermission")
								.replace("%rankup%", nextRank)
								.replace("%rankup_display%", nextRankDisplay)
								.replace("%rank%", currentRank));
					}
					PRXAPI.TASKED_PLAYERS.remove(name);
					return;
				}
			}

			if(rankupCostWithIncrease > prxAPI.getPlayerMoney(p)) {
				if(prxAPI.h("notenoughmoney") != null && !prxAPI.h("notenoughmoney").isEmpty()) {	
					for(String line : prxAPI.h("notenoughmoney")) {
						p.sendMessage(prxAPI.c(line)
								.replace("%rankup%", nextRank).replace("%rankup_display%", nextRankDisplay).replace("%rank%", currentRank)
								.replace("%rankup_cost%", prxAPI.s(rankupCostWithIncrease)).replace("%rankup_cost_formatted%", prxAPI.formatBalance(rankupCostWithIncrease)));
					}
				}
				PRXAPI.TASKED_PLAYERS.remove(name);
				return;
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
				PRXAPI.TASKED_PLAYERS.remove(name);
				return;
			}
			main.getServer().getPluginManager().callEvent(e);
			if(e.isCancelled()) {
				PRXAPI.TASKED_PLAYERS.remove(name);
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
					OnlinePlayers.getPlayers().forEach(ap -> {
						if(main.isInDisabledWorld(ap)) return;
						for(String messageLine : broadcastMessages) {
							ap.sendMessage(prxAPI.cp(messageLine
									.replace("%player%", name)
									.replace("%rankup%", nextRank)
									.replace("%rank%", currentRank)
									.replace("%rankup_display%", nextRankDisplay), p));
						}
					});
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
			Map<String, Double> chances = new HashMap<>();
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
						String pCMD = prxAPI.cp(cmd.replace("%player%", name)
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
				p.playSound(p.getLocation(), XSound.matchSound(rankupSoundName), rankupSoundVolume, rankupSoundPitch);
			}
			boolean rankupHologramIsEnable = main.globalStorage.getBooleanData("Holograms.rankup.enable");
			if(rankupHologramIsEnable && main.hasHologramsPlugin) {
				int rankupHologramRemoveTime = main.globalStorage.getIntegerData("Holograms.rankup.remove-time");
				int rankupHologramHeight = main.globalStorage.getIntegerData("Holograms.rankup.height");
				List<String> rankupHologramFormat = main.globalStorage.getStringListData("Holograms.rankup.format");
				try {
					spawnHologram(rankupHologramFormat, rankupHologramRemoveTime, rankupHologramHeight, p);
				} catch (IllegalArgumentException | InterruptedException | ExecutionException e1) {
					e1.printStackTrace();
				}
			}
			main.sendRankFirework(p);
			main.econ.withdrawPlayer(p, rankupCostWithIncrease);
			e.setRankup(main.rankStorage.getRankupName(rp));
			Bukkit.getScheduler().runTaskLater(main, () -> {
				main.playerStorage.setPlayerRank(p, main.rankStorage.getRankupName(rp));
				PRXAPI.TASKED_PLAYERS.remove(name);
			}, 1);
		});
	}

	public void spawnHologram(List<String> format, int removeTime, int height, Player player) throws IllegalArgumentException, InterruptedException, ExecutionException {
		Player p = player;
		String name = p.getName();
		String nextRank = prxAPI.getPlayerNextRank(p);
		IHologram hologram = main.hologramManager.createHologram("prx_" + nextRank + name, p.getLocation().add(0, height, 0), false);
		for(String line : format) {
			String updatedLine = main.getString(line.replace("%player%", name)
					.replace("%player_display%", p.getDisplayName())
					.replace("%nextrank%", nextRank)
					.replace("%nextrank_display%", main.getString(prxAPI.getPlayerRankupDisplay(p)))
					, name);
			hologram.addLine(updatedLine, false);
		}
		hologram.delete(removeTime);
	}

	public void spawnHologramAsync(List<String> format, int removeTime, int height, Player player) throws IllegalArgumentException, InterruptedException, ExecutionException {
		Player p = player;
		String name = p.getName();
		String nextRank = prxAPI.getPlayerNextRank(p);
		IHologram hologram = main.hologramManager.createHologram("prx_" + nextRank + name, p.getLocation().add(0, height, 0), true);
		for(String line : format) {
			String updatedLine = main.getString(line.replace("%player%", name)
					.replace("%player_display%", p.getDisplayName())
					.replace("%nextrank%", nextRank)
					.replace("%nextrank_display%", main.getString(prxAPI.getPlayerRankupDisplay(p)))
					, name);
			hologram.addLine(updatedLine, true);
		}
		hologram.delete(removeTime);
	}

	public void rankup(Player player, boolean silent) {
		Player p = player;
		String name = p.getName();
		if(getTaskedPlayers().contains(name)) {
			return;
		}
		getTaskedPlayers().add(name);
		RankPath rp = prxAPI.getPlayerRankPath(p);
		String currentRank = rp.getRankName();
		String nextRank = prxAPI.getPlayerNextRank(p);



		//if(!p.hasPermission(main.rankupCommand.getPermission()) && !p.hasPermission("*")) {
		// getTaskedPlayers().remove(p);
		//return;
		//}
		if(main.globalStorage.getBooleanData("Options.per-rank-permission")) {
			if(!p.hasPermission(main.rankupCommand.getPermission() + "." + nextRank) && !p.hasPermission("*")) {
				getTaskedPlayers().remove(name);
				return;
			}
		}
		if(main.rankStorage.getRankupName(rp).equalsIgnoreCase("LASTRANK")) {
			getTaskedPlayers().remove(name);
			return;
		}
		String nextRankDisplay = prxAPI.getPlayerRankupDisplayR(p);
		double rankupCostWithIncrease = prxAPI.getPlayerRankupCostWithIncreaseDirect(p);
		Map<String, String> stringRequirements = prxAPI.getRankStringRequirements(rp);
		Map<String, Double> numberRequirements = prxAPI.getRankNumberRequirements(rp);
		if(rankupCostWithIncrease > prxAPI.getPlayerMoney(p)) {
			getTaskedPlayers().remove(name);
			return;
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
			getTaskedPlayers().remove(name);
			return;
		}
		AsyncAutoRankupEvent ev = new AsyncAutoRankupEvent(p, nextRank, rp.getRankName());
		main.getServer().getPluginManager().callEvent(ev);
		if(ev.isCancelled()) {
			getTaskedPlayers().remove(name);
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
							.replace("%player%", name)
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
							.replace("%player%", name)
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
				OnlinePlayers.getPlayers().forEach(ap -> {
					if(main.isInDisabledWorld(ap)) return;
					for(String messageLine : broadcastMessages) {
						ap.sendMessage(prxAPI.cp(messageLine
								.replace("%player%", name)
								.replace("%rankup%", nextRank)
								.replace("%rank%", currentRank)
								.replace("%rankup_display%", nextRankDisplay), p));
					}
				});
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
		Map<String, Double> chances = new HashMap<>();
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
					String pCMD = prxAPI.cp(cmd.replace("%player%", name)
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
			p.playSound(p.getLocation(), XSound.matchSound(rankupSoundName), rankupSoundVolume, rankupSoundPitch);
		}
		boolean rankupHologramIsEnable = main.globalStorage.getBooleanData("Holograms.rankup.enable");
		if(rankupHologramIsEnable && main.hasHologramsPlugin) {
			int rankupHologramRemoveTime = main.globalStorage.getIntegerData("Holograms.rankup.remove-time");
			int rankupHologramHeight = main.globalStorage.getIntegerData("Holograms.rankup.height");
			List<String> rankupHologramFormat = main.globalStorage.getStringListData("Holograms.rankup.format");
			try {
				spawnHologramAsync(rankupHologramFormat, rankupHologramRemoveTime, rankupHologramHeight, p);
			} catch (IllegalArgumentException | InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		main.sendRankFirework(p);
		main.econ.withdrawPlayer(p, rankupCostWithIncrease);
		Bukkit.getScheduler().runTaskLater(main, () -> {
			main.playerStorage.setPlayerRank(p, nextRank);
			getTaskedPlayers().remove(name);
		}, 1);

	}

	public Set<String> getTaskedPlayers() {
		return taskedPlayers;
	}

	public void setTaskedPlayers(Set<String> taskedPlayers) {
		this.taskedPlayers = taskedPlayers;
	}

}
