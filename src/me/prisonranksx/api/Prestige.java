package me.prisonranksx.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.samdev.actionutil.ActionUtil;
import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.InfinitePrestigeSettings;
import me.prisonranksx.data.PrestigeRandomCommands;
import me.prisonranksx.events.AsyncAutoPrestigeEvent;
import me.prisonranksx.events.PrestigeUpdateCause;
import me.prisonranksx.events.RankUpdateCause;
import me.prisonranksx.events.PrestigeUpdateEvent;
import me.prisonranksx.events.RankUpdateEvent;
import me.prisonranksx.hooks.IHologram;
import me.prisonranksx.hooks.PapiHook;
import me.prisonranksx.utils.OnlinePlayers;
import me.prisonranksx.utils.XSound;

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
		main.scheduler.runTaskTimerAsynchronously(main, () -> {
			for(String playerName : PRXAPI.AUTO_PRESTIGE_PLAYERS) {
				this.prestige(Bukkit.getPlayer(playerName), true);
			}
		}, autoPrestigeDelay, autoPrestigeDelay);

	}

	public void autoPrestige(Player player) {
		Player p = player;
		String name = p.getName();
		if(prxAPI.isAutoPrestigeEnabled(p)) {
			PRXAPI.AUTO_PRESTIGE_PLAYERS.remove(name);
			if(prxAPI.g("autoprestige-disabled") != null && !prxAPI.g("autoprestige-disabled").isEmpty()) {
				p.sendMessage(prxAPI.g("autoprestige-disabled"));
			}
		} else {
			PRXAPI.AUTO_PRESTIGE_PLAYERS.add(name);
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
				PRXAPI.AUTO_PRESTIGE_PLAYERS.remove(name);
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
				PRXAPI.AUTO_PRESTIGE_PLAYERS.remove(name);
				if(prxAPI.g("autoprestige-disabled") != null && !prxAPI.g("autoprestige-disabled").isEmpty()) {
					p.sendMessage(prxAPI.g("autoprestige-disabled"));
				}
				return;
			}
			PRXAPI.AUTO_PRESTIGE_PLAYERS.add(name);
			startAutoPrestigeTask();
			if(prxAPI.g("autoprestige-enabled") != null && !prxAPI.g("autoprestige-enabled").isEmpty()) {
				p.sendMessage(prxAPI.g("autoprestige-enabled"));
			}
		}
	}

	public boolean prestige(final Player player) {
		String name = player.getName();

		if(PRXAPI.TASKED_PLAYERS.contains(name)) {
			if(prxAPI.g("commandspam") != null && !prxAPI.g("commandspam").isEmpty()) {
				player.sendMessage(prxAPI.g("commandspam"));
			}
			return false;
		}	
		PRXAPI.TASKED_PLAYERS.add(name);

		Player p = player;

		String previousPrestige = prxAPI.getPlayerPrestige(p);
		int previousNumber = prxAPI.getPrestigeNumberX(previousPrestige);
		String prestige = prxAPI.getPlayerNextPrestige(p);
		int prestigeNumber = prxAPI.getPrestigeNumberX(prestige);

		if(!p.hasPermission(main.prestigeCommand.getPermission()) && !p.hasPermission("*")) {
			if(prxAPI.g("nopermission") != null && !prxAPI.g("nopermission").isEmpty()) {	
				p.sendMessage(prxAPI.g("nopermission"));
			}
			PRXAPI.TASKED_PLAYERS.remove(name);
			return false;
		}
		if(prestige == null) {
			if(prxAPI.h("lastprestige") != null && !prxAPI.h("lastprestige").isEmpty()) {
				for(String line : prxAPI.h("lastprestige")) {
					p.sendMessage(prxAPI.c(line));
				}
			}
			PRXAPI.TASKED_PLAYERS.remove(name);
			return false;
		}


		if(!prxAPI.isLastRank(p) && !main.rankStorage.isAllowPrestige(prxAPI.getPlayerRankPath(p))) {
			if(prxAPI.g("noprestige") != null && !prxAPI.g("noprestige").isEmpty()) {
				p.sendMessage(prxAPI.g("noprestige"));
			}
			PRXAPI.TASKED_PLAYERS.remove(name);
			return false;
		}
		Map<String, String> stringRequirements = prxAPI.getPrestigeStringRequirements(prestige);
		Map<String, Double> numberRequirements = prxAPI.getPrestigeNumberRequirements(prestige);
		List<String> customRequirementMessage = prxAPI.getPrestigeCustomRequirementMessage(prestige);
		if(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p) > prxAPI.getPlayerMoney(p)) {
			if(prxAPI.h("prestige-notenoughmoney") != null && !prxAPI.h("prestige-notenoughmoney").isEmpty()) {
				for(String line : prxAPI.h("prestige-notenoughmoney")) {
					p.sendMessage(prxAPI.c(line)
							.replace("%nextprestige%", prestige)
							.replace("%nextprestige_display%", prxAPI.getPlayerNextPrestigeDisplayNoFallback(p))
							.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
							.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
							.replace("%nextprestige_cost%", prxAPI.s(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p)))
							.replace("%nextprestige_cost_formatted%", prxAPI.formatBalance(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p))));
				}
			}
			PRXAPI.TASKED_PLAYERS.remove(name);
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
			PRXAPI.TASKED_PLAYERS.remove(name);
			return false;
		}
		PrestigeUpdateEvent e = new PrestigeUpdateEvent(player, PrestigeUpdateCause.PRESTIGEUP);
		main.scheduler.runTask(main, () -> main.getServer().getPluginManager().callEvent(e));
		if(e.isCancelled()) {
			PRXAPI.TASKED_PLAYERS.remove(name);
			return false;
		}
		main.newSharedChain("maxprocess#" + name).async(() -> {
			String prestigeMsg = prxAPI.g("prestige");
			if(prestigeMsg != null) {
				if(!prestigeMsg.isEmpty()) {
					if(main.globalStorage.getBooleanData("Options.send-prestigemsg")) {
						p.sendMessage(prxAPI.cp(prestigeMsg
								.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p))
								.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
								.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
								.replace("%nextprestige_display%", prxAPI.getPlayerNextPrestigeDisplayNoFallback(p)), p));
					}
				}
			}
			prxAPI.celeberate(p);
			List<String> addPermissionList = main.prestigeStorage.getAddPermissionList(prestige);
			if(addPermissionList != null) {
				if(!addPermissionList.isEmpty()) {
					for(String permission : addPermissionList) {
						main.perm.addPermissionAsync(p, permission
								.replace("%player%", p.getName())
								.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p))
								.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
								.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
								.replace("%nextprestige_display%", prxAPI.getPlayerNextPrestigeDisplayNoFallback(p)));
					}
				}
			}
			List<String> delPermissionList = main.prestigeStorage.getDelPermissionList(prestige);
			if(delPermissionList != null) {
				if(!delPermissionList.isEmpty()) {
					for(String permission : delPermissionList) {
						main.perm.delPermissionAsync(p, permission
								.replace("%player%", p.getName())
								.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p))
								.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
								.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
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
								.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
								.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
								.replace("%nextprestige_display%", prxAPI.getPlayerNextPrestigeDisplayNoFallback(p))
								.replace("%nextprestige_cost%", prxAPI.s(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p))));
					}
					main.executeCommands(p, newPrestigeCommands);
				}
			}
			if(main.isInfinitePrestige) {
				List<String> commands = main.infinitePrestigeSettings.getCommands();
				if(commands != null && !commands.isEmpty()) {
					main.getServer().getScheduler().runTask(main, () -> {
						commands.forEach(cmd -> {
							main.executeCommand(p, main.getString(cmd
									.replace("{number}", prxAPI.getPlayerNextPrestige(p))));
						});
					});
				}
				List<String> broadcast = main.infinitePrestigeSettings.getBroadcast();
				if(broadcast != null && !broadcast.isEmpty()) {
					broadcast.forEach(broadcastMessage -> {
						Bukkit.broadcastMessage(main.getString(broadcastMessage.replace("{number}", prxAPI.getPlayerNextPrestige(p)), name)
								.replace("%player%", name)
								.replace("%prestige%", prestige)
								.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
								.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
								.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p))
								.replace("%nextprestige_display%", prxAPI.getPlayerNextPrestigeDisplay(p))
								);
					});
				}
				Map<Long, InfinitePrestigeSettings> cps = main.infinitePrestigeSettings.getContinuousPrestigeSettings();
				if(!cps.isEmpty()) {
					String nextPrestige = prxAPI.getPlayerNextPrestige(p);
					if(!nextPrestige.equals("0") && !nextPrestige.equals("1")) {
						for(Entry<Long, InfinitePrestigeSettings> each : cps.entrySet()) {

							if(!prxAPI.getNumberAPI().hasUsableDecimals((double)Long.valueOf(nextPrestige) / (double)each.getKey())) {
								List<String> cbroadcast = each.getValue().getBroadcast();
								if(cbroadcast != null && !cbroadcast.isEmpty()) {
									cbroadcast.forEach(broadcastMessage -> {
										Bukkit.broadcastMessage(main.getString(broadcastMessage.replace("{number}", prxAPI.getPlayerNextPrestige(p)), name)
												.replace("%player%", name)
												.replace("%prestige%", prestige)
												.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
												.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
												.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p))
												.replace("%nextprestige_display%", prxAPI.getPlayerNextPrestigeDisplay(p))
												);
									});
								}
								List<String> continuousMessage = each.getValue().getMsg();
								if(continuousMessage != null && !continuousMessage.isEmpty()) {
									continuousMessage.forEach(message -> {
										p.sendMessage(main.getString(message.replace("{number}", prxAPI.getPlayerNextPrestige(p)), name)
												.replace("%player%", name)
												.replace("%prestige%", prestige)
												.replace("%prestige_usformat%", PapiHook.nf.format(Double.valueOf(previousNumber)))
												.replace("%nextprestige_usformat%", PapiHook.nf.format(Double.valueOf(prestigeNumber)))
												.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p))
												.replace("%nextprestige_display%", prxAPI.getPlayerNextPrestige(p))
												);
									});
								}
								List<String> ccommands = each.getValue().getCommands();
								if(ccommands != null && !ccommands.isEmpty()) {
									main.getServer().getScheduler().runTask(main, () -> {
										ccommands.forEach(cmd -> {
											main.executeCommand(p, main.getString(cmd
													.replace("{number}", prxAPI.getPlayerNextPrestige(p))));
										});
									});
								}
							}
						}
					}
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
								.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
								.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
								.replace("%nextprestige_display%", prxAPI.getPlayerNextPrestigeDisplayNoFallbackR(p)));
					}
					int actionbarInterval = main.prestigeStorage.getActionbarInterval(prestige);
					main.animateActionbar(p, actionbarInterval, newActionbarText);
				}
			}
			List<String> broadcastMessages = main.prestigeStorage.getBroadcast(prestige);
			if(broadcastMessages != null) {
				if(!broadcastMessages.isEmpty()) {
					OnlinePlayers.getPlayers().forEach(ap -> {
						if(main.isInDisabledWorld(ap)) return;
						for(String messageLine : broadcastMessages) {
							ap.sendMessage(prxAPI.cp(messageLine
									.replace("%player%", name)
									.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p))
									.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
									.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
									.replace("%nextprestige_display%", prxAPI.getPlayerNextPrestigeDisplay(p)), p));
						}
					});
				}
			}
			List<String> messages = main.prestigeStorage.getMsg(prestige);
			if(messages != null) {
				if(!messages.isEmpty()) {
					for(String messageLine : messages) {
						p.sendMessage(prxAPI.cp(messageLine
								.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p))
								.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
								.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
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
				p.playSound(p.getLocation(), XSound.matchSound(nextPrestigeSoundName), nextPrestigeSoundVolume, nextPrestigeSoundPitch);
			}
			boolean nextPrestigeHologramIsEnable = main.globalStorage.getBooleanData("Holograms.prestige.enable");
			if(nextPrestigeHologramIsEnable && main.hasHologramsPlugin) {
				int nextPrestigeHologramRemoveTime = main.globalStorage.getIntegerData("Holograms.prestige.remove-time");
				int nextPrestigeHologramHeight = main.globalStorage.getIntegerData("Holograms.prestige.height");
				List<String> nextPrestigeHologramFormat = main.globalStorage.getStringListData("Holograms.prestige.format");
				try {
					spawnHologram(nextPrestigeHologramFormat, nextPrestigeHologramRemoveTime, nextPrestigeHologramHeight, p);
				} catch (InterruptedException | ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			main.sendPrestigeFirework(p);
			main.econ.withdrawPlayer(p, prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p));
			if(main.globalStorage.getBooleanData("PrestigeOptions.ResetMoney")) {
				main.econ.withdrawPlayer(p, prxAPI.getPlayerMoney(p));
			}
			if(main.globalStorage.getBooleanData("PrestigeOptions.ResetRank")) {
				RankUpdateEvent e1 = new RankUpdateEvent(p, RankUpdateCause.RANKSET_BYPRESTIGE, main.globalStorage.getStringData("defaultrank"));
				main.scheduler.runTask(main, () -> Bukkit.getPluginManager().callEvent(e1));
				if(e1.isCancelled()) {
					PRXAPI.TASKED_PLAYERS.remove(name);
				} else {
					main.playerStorage.setPlayerRank(p, main.globalStorage.getStringData("defaultrank"));
				}
			}
			List<String> prestigeCommands = main.globalStorage.getStringListData("PrestigeOptions.prestige-cmds");
			if(!prestigeCommands.isEmpty()) {
				prestigeCommands.forEach(cmd -> {
					if(cmd.startsWith("[rankpermissions]")) {
						main.perm.delPermissionAsync(p, prxAPI.allRankAddPermissions);
					} else if (cmd.startsWith("[prestigepermissions]")) {
						main.perm.delPermissionAsync(p, prxAPI.allPrestigeAddPermissions);
					} else if (cmd.startsWith("[rebirthpermissions]")) {
						main.perm.delPermissionAsync(p, prxAPI.allRebirthAddPermissions);
					} else {
						main.executeCommand(p, cmd);
					}
				});
			}
		}).execute();
		main.newSharedChain("maxprocess#" + name).sync(() -> {
			Bukkit.getScheduler().runTaskLater(main, () -> {
				main.debug(prestige);
				main.playerStorage.setPlayerPrestige(p, prestige);
				PRXAPI.TASKED_PLAYERS.remove(name);
			}, 1);
		}).execute();
		return true;
	}

	public void prestige2(final Player player, final boolean ignoreLastRank) {
		String name = player.getName();
		if(PRXAPI.TASKED_PLAYERS.contains(name)) {
			return;
		}
		PRXAPI.TASKED_PLAYERS.add(name);

		Player p = player;
		String previousPrestige = prxAPI.getPlayerPrestige(p);
		int previousNumber = prxAPI.getPrestigeNumberX(previousPrestige);
		String prestige = prxAPI.getPlayerNextPrestige(p);
		int prestigeNumber = prxAPI.getPrestigeNumberX(prestige);

		if(!p.hasPermission(main.prestigeCommand.getPermission()) && !p.hasPermission("*")) {
			if(prxAPI.g("nopermission") != null && !prxAPI.g("nopermission").isEmpty()) {	
				p.sendMessage(prxAPI.g("nopermission"));
			}
			PRXAPI.TASKED_PLAYERS.remove(name);
			return;
		}
		if(prestige == null) {
			if(prxAPI.h("lastprestige") != null && !prxAPI.h("lastprestige").isEmpty()) {
				for(String line : prxAPI.h("lastprestige")) {
					p.sendMessage(prxAPI.c(line));
				}
			}
			PRXAPI.TASKED_PLAYERS.remove(name);
			return;
		}
		if(!ignoreLastRank) {
			if(!prxAPI.isLastRank(p) && !main.rankStorage.isAllowPrestige(prxAPI.getPlayerRankPath(p))) {
				if(prxAPI.g("noprestige") != null && !prxAPI.g("noprestige").isEmpty()) {
					p.sendMessage(prxAPI.g("noprestige"));
				}
				PRXAPI.TASKED_PLAYERS.remove(name);
				return;
			}
		}
		double nextPrestigeCost = prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p);
		String nextPrestigeDisplay = prxAPI.getPlayerNextPrestigeDisplayNoFallbackR(p);
		if(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p) > prxAPI.getPlayerMoney(p)) {
			if(prxAPI.h("prestige-notenoughmoney") != null && !prxAPI.h("prestige-notenoughmoney").isEmpty()) {
				for(String line : prxAPI.h("prestige-notenoughmoney")) {
					p.sendMessage(prxAPI.c(line)
							.replace("%nextprestige%", prestige)
							.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
							.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
							.replace("%nextprestige_display%", nextPrestigeDisplay)
							.replace("%nextprestige_cost%", prxAPI.s(nextPrestigeCost)).replace("%nextprestige_cost_formatted%", prxAPI.formatBalance(nextPrestigeCost)));
				}
			}
			PRXAPI.TASKED_PLAYERS.remove(name);
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
			PRXAPI.TASKED_PLAYERS.remove(name);
			return;
		}
		PrestigeUpdateEvent e = new PrestigeUpdateEvent(player, PrestigeUpdateCause.PRESTIGE_BY_RANKUPMAX);
		main.scheduler.runTask(main, () -> main.getServer().getPluginManager().callEvent(e));
		if(e.isCancelled()) {
			PRXAPI.TASKED_PLAYERS.remove(name);
			return;
		}
		String prestigeMsg = prxAPI.g("prestige");
		if(prestigeMsg != null) {
			if(!prestigeMsg.isEmpty()) {
				if(main.globalStorage.getBooleanData("Options.send-prestigemsg")) {
					p.sendMessage(prxAPI.cp(prestigeMsg
							.replace("%nextprestige%", prestige)
							.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
							.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
							.replace("%nextprestige_display%", nextPrestigeDisplay), p));
				}
			}
		}
		List<String> addPermissionList = main.prestigeStorage.getAddPermissionList(prestige);
		if(addPermissionList != null) {
			if(!addPermissionList.isEmpty()) {
				for(String permission : addPermissionList) {
					main.perm.addPermissionAsync(p, permission
							.replace("%player%", p.getName())
							.replace("%nextprestige%", prestige)
							.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
							.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
							.replace("%nextprestige_display%", nextPrestigeDisplay));
				}
			}
		}
		List<String> delPermissionList = main.prestigeStorage.getDelPermissionList(prestige);
		if(delPermissionList != null) {
			if(!delPermissionList.isEmpty()) {
				for(String permission : delPermissionList) {
					main.perm.delPermissionAsync(p, permission
							.replace("%player%", p.getName())
							.replace("%nextprestige%", prestige)
							.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
							.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
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
							.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
							.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
							.replace("%nextprestige_cost%", prxAPI.s(nextPrestigeCost)));
				}
				main.executeCommands(p, newPrestigeCommands);
			}
		}
		if(main.isInfinitePrestige) {
			List<String> commands = main.infinitePrestigeSettings.getCommands();
			if(commands != null && !commands.isEmpty()) {
				main.getServer().getScheduler().runTask(main, () -> {
					commands.forEach(cmd -> {
						main.executeCommand(p, main.getString(cmd
								.replace("{number}", prxAPI.getPlayerNextPrestige(p))));
					});
				});
			}
			List<String> broadcast = main.infinitePrestigeSettings.getBroadcast();
			if(broadcast != null && !broadcast.isEmpty()) {
				broadcast.forEach(broadcastMessage -> {
					Bukkit.broadcastMessage(main.getString(broadcastMessage.replace("{number}", prxAPI.getPlayerNextPrestige(p)), name)
							.replace("%player%", name)
							.replace("%prestige%", prestige)
							.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
							.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
							.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p))
							.replace("%nextprestige_display%", prxAPI.getPlayerNextPrestigeDisplay(p))
							);
				});
			}
			Map<Long, InfinitePrestigeSettings> cps = main.infinitePrestigeSettings.getContinuousPrestigeSettings();
			if(!cps.isEmpty()) {
				String nextPrestige = prxAPI.getPlayerNextPrestige(p);
				if(!nextPrestige.equals("0") && !nextPrestige.equals("1")) {
					for(Entry<Long, InfinitePrestigeSettings> each : cps.entrySet()) {

						if(!prxAPI.getNumberAPI().hasUsableDecimals((double)Long.valueOf(nextPrestige) / (double)each.getKey())) {
							List<String> cbroadcast = each.getValue().getBroadcast();
							if(cbroadcast != null && !cbroadcast.isEmpty()) {
								cbroadcast.forEach(broadcastMessage -> {
									Bukkit.broadcastMessage(main.getString(broadcastMessage.replace("{number}", prxAPI.getPlayerNextPrestige(p)), name)
											.replace("%player%", name)
											.replace("%prestige%", prestige)
											.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
											.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
											.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p))
											.replace("%nextprestige_display%", prxAPI.getPlayerNextPrestigeDisplay(p))
											);
								});
							}
							List<String> continuousMessage = each.getValue().getMsg();
							if(continuousMessage != null && !continuousMessage.isEmpty()) {
								continuousMessage.forEach(message -> {
									p.sendMessage(main.getString(message.replace("{number}", prxAPI.getPlayerNextPrestige(p)), name)
											.replace("%player%", name)
											.replace("%prestige%", prestige)
											.replace("%prestige_usformat%", PapiHook.nf.format(Double.valueOf(previousNumber)))
											.replace("%nextprestige_usformat%", PapiHook.nf.format(Double.valueOf(prestigeNumber)))
											.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p))
											.replace("%nextprestige_display%", prxAPI.getPlayerNextPrestige(p))
											);
								});
							}
							List<String> ccommands = each.getValue().getCommands();
							if(ccommands != null && !ccommands.isEmpty()) {
								main.getServer().getScheduler().runTask(main, () -> {
									ccommands.forEach(cmd -> {
										main.executeCommand(p, main.getString(cmd
												.replace("{number}", prxAPI.getPlayerNextPrestige(p))));
									});
								});
							}
						}
					}
				}
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
							.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
							.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
							.replace("%nextprestige_display%", nextPrestigeDisplay));
				}
				int actionbarInterval = main.prestigeStorage.getActionbarInterval(prestige);
				main.animateActionbar(p, actionbarInterval, newActionbarText);
			}
		}
		List<String> broadcastMessages = main.prestigeStorage.getBroadcast(prestige);
		if(broadcastMessages != null) {
			if(!broadcastMessages.isEmpty()) {
				OnlinePlayers.getPlayers().forEach(ap -> {
					if(main.isInDisabledWorld(ap)) return;
					for(String messageLine : broadcastMessages) {
						ap.sendMessage(prxAPI.cp(messageLine
								.replace("%player%", name)
								.replace("%nextprestige%", prestige)
								.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
								.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
								.replace("%nextprestige_display%", nextPrestigeDisplay), p));
					}
				});
			}
		}
		List<String> messages = main.prestigeStorage.getMsg(prestige);
		if(messages != null) {
			if(!messages.isEmpty()) {
				for(String messageLine : messages) {
					p.sendMessage(prxAPI.cp(messageLine
							.replace("%nextprestige%", prestige)
							.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
							.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
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
			p.playSound(p.getLocation(), XSound.matchSound(nextPrestigeSoundName), nextPrestigeSoundVolume, nextPrestigeSoundPitch);
		}
		boolean nextPrestigeHologramIsEnable = main.globalStorage.getBooleanData("Holograms.prestige.enable");
		if(nextPrestigeHologramIsEnable && main.hasHologramsPlugin) {
			int nextPrestigeHologramRemoveTime = main.globalStorage.getIntegerData("Holograms.prestige.remove-time");
			int nextPrestigeHologramHeight = main.globalStorage.getIntegerData("Holograms.prestige.height");
			List<String> nextPrestigeHologramFormat = main.globalStorage.getStringListData("Holograms.prestige.format");
			try {
				spawnHologram(nextPrestigeHologramFormat, nextPrestigeHologramRemoveTime, nextPrestigeHologramHeight, p);
			} catch (InterruptedException | ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		main.sendPrestigeFirework(p);
		main.econ.withdrawPlayer(p, nextPrestigeCost);
		if(main.globalStorage.getBooleanData("PrestigeOptions.ResetMoney")) {
			main.econ.withdrawPlayer(p, prxAPI.getPlayerMoney(p));
		}
		if(main.globalStorage.getBooleanData("PrestigeOptions.ResetRank")) {
			RankUpdateEvent e1 = new RankUpdateEvent(p, RankUpdateCause.RANKSET_BYPRESTIGE, main.globalStorage.getStringData("defaultrank"));
			main.scheduler.runTask(main, () -> Bukkit.getPluginManager().callEvent(e1));
			if(e1.isCancelled()) {
				PRXAPI.TASKED_PLAYERS.remove(name);
				return;
			}
			main.playerStorage.setPlayerRank(p, main.globalStorage.getStringData("defaultrank"));
		}
		List<String> prestigeCommands = main.globalStorage.getStringListData("PrestigeOptions.prestige-cmds");
		if(!prestigeCommands.isEmpty()) {
			prestigeCommands.forEach(cmd -> {
				if(cmd.startsWith("[rankpermissions]")) {
					main.perm.delPermissionAsync(p, prxAPI.allRankAddPermissions);
				} else if (cmd.startsWith("[prestigepermissions]")) {
					main.perm.delPermissionAsync(p, prxAPI.allPrestigeAddPermissions);
				} else if (cmd.startsWith("[rebirthpermissions]")) {
					main.perm.delPermissionAsync(p, prxAPI.allRebirthAddPermissions);
				} else {
					main.executeCommand(p, cmd);
				}
			});
		}
		Bukkit.getScheduler().runTaskLater(main, () -> {
			main.playerStorage.setPlayerPrestige(p, prestige);
			PRXAPI.TASKED_PLAYERS.remove(name);

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
		int previousNumber = prxAPI.getPrestigeNumberX(currentPrestige);
		String prestige = prxAPI.getPlayerNextPrestige(p);
		int prestigeNumber = prxAPI.getPrestigeNumberX(prestige);
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
		AsyncAutoPrestigeEvent event = new AsyncAutoPrestigeEvent(p, prestige, currentPrestige);
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
							.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
							.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
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
									.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
									.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
									.replace("%nextprestige_display%", prestigeDisplay));
						}
					});
				} else {
					for(String permission : addPermissionList) {
						main.perm.addPermissionAsync(p, permission
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
						main.perm.delPermissionAsync(p, permission
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
		if(main.isInfinitePrestige) {
			List<String> commands = main.infinitePrestigeSettings.getCommands();
			if(commands != null && !commands.isEmpty()) {
				main.getServer().getScheduler().runTask(main, () -> {
					commands.forEach(cmd -> {
						main.executeCommand(p, main.getString(cmd
								.replace("{number}", prestige)));
					});
				});
			}
			List<String> broadcast = main.infinitePrestigeSettings.getBroadcast();
			if(broadcast != null && !broadcast.isEmpty()) {
				broadcast.forEach(broadcastMessage -> {
					Bukkit.broadcastMessage(main.getString(broadcastMessage.replace("{number}", prestige), name)
							.replace("%player%", name)
							.replace("%prestige%", currentPrestige)
							.replace("%nextprestige%", prestige)
							.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
							.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
							.replace("%nextprestige_display%", prestigeDisplay)
							);
				});
			}
			Map<Long, InfinitePrestigeSettings> cps = main.infinitePrestigeSettings.getContinuousPrestigeSettings();
			if(!cps.isEmpty()) {
				String nextPrestige = prxAPI.getPlayerNextPrestige(p);
				if(!nextPrestige.equals("0") && !nextPrestige.equals("1")) {
					for(Entry<Long, InfinitePrestigeSettings> each : cps.entrySet()) {

						if(!prxAPI.getNumberAPI().hasUsableDecimals((double)Long.valueOf(nextPrestige) / (double)each.getKey())) {
							List<String> cbroadcast = each.getValue().getBroadcast();
							if(cbroadcast != null && !cbroadcast.isEmpty()) {
								cbroadcast.forEach(broadcastMessage -> {
									Bukkit.broadcastMessage(main.getString(broadcastMessage.replace("{number}", prxAPI.getPlayerNextPrestige(p)), name)
											.replace("%player%", name)
											.replace("%prestige%", prestige)
											.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
											.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
											.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p))
											.replace("%nextprestige_display%", prxAPI.getPlayerNextPrestigeDisplay(p))
											);
								});
							}
							List<String> continuousMessage = each.getValue().getMsg();
							if(continuousMessage != null && !continuousMessage.isEmpty()) {
								continuousMessage.forEach(message -> {
									p.sendMessage(main.getString(message.replace("{number}", prxAPI.getPlayerNextPrestige(p)), name)
											.replace("%player%", name)
											.replace("%prestige%", prestige)
											.replace("%prestige_usformat%", PapiHook.nf.format(Double.valueOf(previousNumber)))
											.replace("%nextprestige_usformat%", PapiHook.nf.format(Double.valueOf(prestigeNumber)))
											.replace("%nextprestige%", prxAPI.getPlayerNextPrestige(p))
											.replace("%nextprestige_display%", prxAPI.getPlayerNextPrestige(p))
											);
								});
							}
							List<String> ccommands = each.getValue().getCommands();
							if(ccommands != null && !ccommands.isEmpty()) {
								main.getServer().getScheduler().runTask(main, () -> {
									ccommands.forEach(cmd -> {
										main.executeCommand(p, main.getString(cmd
												.replace("{number}", prxAPI.getPlayerNextPrestige(p))));
									});
								});
							}
						}
					}
				}
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
							.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
							.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
							.replace("%nextprestige_display%", prestigeDisplay));
				}
				int actionbarInterval = main.prestigeStorage.getActionbarInterval(prestige);
				main.animateActionbar(p, actionbarInterval, newActionbarText);
			}
		}
		List<String> broadcastMessages = main.prestigeStorage.getBroadcast(prestige);
		if(broadcastMessages != null) {
			if(!broadcastMessages.isEmpty()) {
				OnlinePlayers.getPlayers().forEach(ap -> {
					if(main.isInDisabledWorld(ap)) return;
					for(String messageLine : broadcastMessages) {
						ap.sendMessage(prxAPI.cp(messageLine
								.replace("%player%", name)
								.replace("%nextprestige%", prestige)
								.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
								.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
								.replace("%nextprestige_display%", prestigeDisplay), p));
					}
				});
			}
		}
		List<String> messages = main.prestigeStorage.getMsg(prestige);
		if(messages != null) {
			if(!messages.isEmpty()) {
				for(String messageLine : messages) {
					p.sendMessage(prxAPI.cp(messageLine
							.replace("%nextprestige%", prestige)
							.replace("%prestige_usformat%", PapiHook.nf.format(previousNumber))
							.replace("%nextprestige_usformat%", PapiHook.nf.format(prestigeNumber))
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
			p.playSound(p.getLocation(), XSound.matchSound(nextPrestigeSoundName), nextPrestigeSoundVolume, nextPrestigeSoundPitch);
		}
		boolean nextPrestigeHologramIsEnable = main.globalStorage.getBooleanData("Holograms.prestige.enable");
		if(nextPrestigeHologramIsEnable && main.hasHologramsPlugin) {
			int nextPrestigeHologramRemoveTime = main.globalStorage.getIntegerData("Holograms.prestige.remove-time");
			int nextPrestigeHologramHeight = main.globalStorage.getIntegerData("Holograms.prestige.height");
			List<String> nextPrestigeHologramFormat = main.globalStorage.getStringListData("Holograms.prestige.format");
			try {
				spawnHologram(nextPrestigeHologramFormat, nextPrestigeHologramRemoveTime, nextPrestigeHologramHeight, p);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		main.sendPrestigeFirework(p);
		main.econ.withdrawPlayer(p, prestigeCost);
		if(main.globalStorage.getBooleanData("PrestigeOptions.ResetMoney")) {
			main.econ.withdrawPlayer(p, prxAPI.getPlayerMoney(p));
		}
		if(main.globalStorage.getBooleanData("PrestigeOptions.ResetRank")) {
			Bukkit.getScheduler().runTask(main, () -> {
				RankUpdateEvent e1 = new RankUpdateEvent(p, RankUpdateCause.RANKSET_BYPRESTIGE);
				main.scheduler.runTask(main, () -> Bukkit.getPluginManager().callEvent(e1));
				if(e1.isCancelled()) {
					getTaskedPlayers().remove(name);
					return;
				}
				main.playerStorage.setPlayerRank(p, main.globalStorage.getStringData("defaultrank"));
			});
		}
		List<String> prestigeCommands = main.globalStorage.getStringListData("PrestigeOptions.prestige-cmds");
		if(!prestigeCommands.isEmpty()) {
			prestigeCommands.forEach(cmd -> {
				if(cmd.startsWith("[rankpermissions]")) {
					main.perm.delPermissionAsync(p, prxAPI.allRankAddPermissions);
				} else if (cmd.startsWith("[prestigepermissions]")) {
					main.perm.delPermissionAsync(p, prxAPI.allPrestigeAddPermissions);
				} else if (cmd.startsWith("[rebirthpermissions]")) {
					main.perm.delPermissionAsync(p, prxAPI.allRebirthAddPermissions);
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
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public void spawnHologram(List<String> format, int removeTime, int height, Player player) throws InterruptedException, ExecutionException {
		String name = player.getName();
		String nextPrestige = prxAPI.getPlayerNextPrestige(player);
		IHologram hologram = main.hologramManager.createHologram("prx_" + nextPrestige + name + prxAPI.numberAPI.getRandomInteger(0, 999999), player.getLocation().add(0, height, 0), true);
		List<String> updatedFormat = new ArrayList<>();
		for(String line : format) {
			String updatedLine = main.getString(line.replace("%player%", name)
					.replace("%player_display%", player.getDisplayName())
					.replace("%nextprestige%", nextPrestige)
					.replace("%nextprestige_display%", main.getString(prxAPI.getPlayerNextPrestigeDisplayR(player)))
					, name);
			updatedFormat.add(updatedLine);
		}
		hologram.addLine(updatedFormat, true);
		hologram.delete(removeTime);
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
		IHologram hologram = main.hologramManager.createHologram("prx_" + prestige + name + prxAPI.numberAPI.getRandomInteger(0, 999999), player.getLocation().add(0, height, 0), true);
		List<String> updatedFormat = new ArrayList<>();
		for(String line : format) {
			String updatedLine = main.getString(line.replace("%player%", name)
					.replace("%player_display%", player.getDisplayName())
					.replace("%nextprestige%", prestige)
					.replace("%nextprestige_display%", main.getString(prxAPI.getPrestigeDisplay(prestige)))
					, name);
			updatedFormat.add(updatedLine);
		}
		hologram.addLine(updatedFormat, true);
		hologram.delete(removeTime);
	}

	public Set<String> getTaskedPlayers() {
		return taskedPlayers;
	}

	public void setTaskedPlayers(Set<String> taskedPlayers) {
		this.taskedPlayers = taskedPlayers;
	}
}
