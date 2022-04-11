package me.prisonranksx.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.samdev.actionutil.ActionUtil;
import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.data.RankRandomCommands;
import me.prisonranksx.events.AsyncRankupMaxEvent;
import me.prisonranksx.events.RankUpdateCause;
import me.prisonranksx.events.RankUpdateEvent;
import me.prisonranksx.utils.AccessibleString;
import me.prisonranksx.utils.OnlinePlayers;

public class RankupMax {

	public final Map<String, String> rankupMaxMap;
	public final Set<String> rankupMaxProcess;
	public final Map<String, Integer> rankupMaxStreak;
	public final Map<String, String> rankupFromMap;
	public final Map<String, List<String>> rankupMaxPassedRanks;
	public final Map<String, Double> rankupMaxCost;
	public final Map<String, Boolean> canPrestigeMap;
	public final List<String> lastRankMessage;
	public final List<String> notEnoughMoneyMessage;
	public final boolean isBroadcastLastRankOnly;
	public final boolean isMsgLastRankOnly;
	public final boolean isRankupMsgLastRankOnly;
	public final boolean isPerRankPermission;
	public final boolean isResetRank;
	public final String rankupMaxMsg;
	public boolean isRankupMaxMsg;
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	private PRXAPI prxAPI;

	public RankupMax() {
		this.prxAPI = main.prxAPI;
		this.rankupMaxMap = new HashMap<>();
		this.rankupMaxProcess = new HashSet<>();
		this.rankupMaxStreak = new HashMap<>();
		this.rankupFromMap = new HashMap<>();
		this.rankupMaxPassedRanks = new HashMap<>();
		this.rankupMaxCost = new HashMap<>();
		this.canPrestigeMap = new HashMap<>();
		this.rankupMaxMsg = main.prxAPI.c(main.messagesStorage.getStringMessage("rankupmax"));
		this.isRankupMaxMsg = main.globalStorage.getBooleanData("Options.send-rankupmaxmsg");
		this.lastRankMessage = main.prxAPI.cl(main.messagesStorage.getStringListMessage("lastrank"));
		this.notEnoughMoneyMessage = main.prxAPI.cl(main.messagesStorage.getStringListMessage("notenoughmoney"));
		this.isBroadcastLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-broadcastlastrankonly");
		this.isMsgLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-msglastrankonly");
		this.isRankupMsgLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-rankupmsglastrankonly");
		this.isPerRankPermission = main.globalStorage.getBooleanData("Options.per-rank-permission");
		this.isResetRank = main.globalStorage.getBooleanData("PrestigeOptions.ResetRank");
	}

	@SuppressWarnings("unused")
	public void rankupMax(final Player player) {
		Player p = player;
		String name = p.getName();
		String rankupFrom = null;
		if(rankupMaxProcess.contains(name)) {
			p.sendMessage(prxAPI.g("rankupmax-is-on"));
			return;
		}
		double allCost = 0;
		rankupMaxCost.put(name, allCost);
		rankupMaxStreak.put(name, 0);
		rankupMaxProcess.add(name);
		//clear old data
		//player checking values
		RankPath rp1 = prxAPI.getPlayerRankPath(p);
		String currentRank = rp1.getRankName();
		String currentPath = rp1.getPathName();
		rankupFrom = currentRank;
		rankupFromMap.put(name, rankupFrom);
		String nextRank = prxAPI.getPlayerNextRank(p);
		double playerBalance = main.econ.getBalance(p);
		Map<String, String> stringRequirements = prxAPI.getRankStringRequirements(rp1);
		Map<String, Double> numberRequirements = prxAPI.getRankNumberRequirements(rp1);
		List<String> customRequirementMessage = prxAPI.getRankCustomRequirementMessage(rp1);
		RankUpdateEvent e = new RankUpdateEvent(p, RankUpdateCause.RANKUPMAX, nextRank);
		main.scheduler.runTask(main, () -> Bukkit.getPluginManager().callEvent(e));
		if(e.isCancelled()) {
			rankupMaxProcess.remove(name);
			return;
		}
		// other values
		boolean canPrestige = prxAPI.canPrestige(p);
		boolean isLastRank = false;
		// if the player is at the latest rank
		if(nextRank == null && !canPrestigeMap.containsKey(name) && !canPrestige) {
			main.sendListMessage(p, lastRankMessage);
			rankupMaxProcess.remove(name);
			isLastRank = true;
			return;
		}
		List<String> ranksConfigList = main.rankStorage.getPathRanksMap().get(currentPath);
		// if he had a nextrank
		// player values
		String nextRankDisplay = !canPrestigeMap.containsKey(name) && !canPrestige && !isLastRank ? main.getString(prxAPI.getPlayerRankupDisplay(p), name) : "null";
		double nextRankCost = !canPrestigeMap.containsKey(name) && !canPrestige && !isLastRank ? prxAPI.getPlayerRankupCostWithIncreaseDirect(p) : 0.0;
		String nextRankCostInString = String.valueOf(nextRankCost);
		String nextRankCostFormatted = prxAPI.formatBalance(nextRankCost);
		//other values [2]
		List<String> allRanksCommands = new ArrayList<>();
		List<String> rankups = new ArrayList<>();
		String rankupMessage = !canPrestigeMap.containsKey(name) && !canPrestige && !isLastRank ? main.getString(main.messagesStorage.getStringMessage("rankup"), name) : "null";
		String rankupNoPermissionMessage = !canPrestigeMap.containsKey(name) && !canPrestige && !isLastRank ? main.getString(main.messagesStorage.getStringMessage("rankup-nopermission"), name).replace("%nextrank%", nextRank).replace("%rankup%", nextRank).replace("%rankup_display%", nextRankDisplay) : "null";
		//if the rank cost is higher than player's balance
		if(nextRankCost > playerBalance && !canPrestigeMap.containsKey(name) && !canPrestige && !isLastRank ) {
			for (String msg : notEnoughMoneyMessage) {
				p.sendMessage(main.getString(msg, name).replace("%player%", name).replace("%rankup_cost%", nextRankCostInString).replace("%rankup%", nextRank).replace("%rankup_display%", nextRankDisplay).replace("%rankup_cost_formatted%", nextRankCostFormatted));
			}
			rankupMaxProcess.remove(name);
			return;
		}
		//if per rank permission option is enabled and the player dosen't has the required permission
		if(isPerRankPermission && !p.hasPermission(main.rankupCommand.getPermission() + "." + nextRank) && !canPrestigeMap.containsKey(name) && !canPrestige && !isLastRank ) {
			p.sendMessage(rankupNoPermissionMessage);
			rankupMaxProcess.remove(name);
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
			rankupMaxProcess.remove(name);
			return;
		}
		canPrestigeMap.remove(name);
		//add new player data
		rankupMaxMap.put(name, prxAPI.getPlayerRank(p));
		//@@@@@@@@@@@@@@@@@@@@
		//==============
		//~~~~~~
		// loop section
		//~~~~~~
		//==============
		//@@@@@@@@@@@@@@@@@@@@
		main.newSharedChain("maxprocess#" + name).abortIf(!player.isOnline()).async(() -> {

			for(String rankSection : ranksConfigList) {
				//loopValues
				String loopCurrentRank = null;
				String loopCurrentRankDisplay = null;
				String loopNextRank = null;
				double loopNextRankCost = 0;
				String loopNextRankCostInString = null;
				String loopNextRankCostFormatted = null;
				String loopNextRankDisplay = null;
				String loopRankupMsg = null;
				List<String> loopNextRankCommands = new ArrayList<>();
				List<String> loopNextRankBroadcast = new ArrayList<>();
				List<String> loopNextRankMsg = new ArrayList<>();
				List<String> loopNextRankActions = new ArrayList<>();
				double loopPlayerBalance = main.econ.getBalance(p);
				//temporarily save player data in a map
				String mapRank = rankupMaxMap.get(name);
				RankPath rp = new RankPath(mapRank, main.prxAPI.getDefaultPath());
				loopCurrentRank = mapRank;
				//RankPath rp = main.playerStorage.getPlayerRankPath(p);
				loopCurrentRankDisplay = main.rankStorage.getDisplayName(rp);
				loopNextRank = main.rankStorage.getRankupName(rp);
				AccessibleString accessibleNextRank = new AccessibleString(loopNextRank);
				//if there is no rank next then stop the loop
				if(loopNextRank.equalsIgnoreCase("lastrank")) {
					if(main.getGlobalStorage().getBooleanData("Options.rankupmax-with-prestige")) {
						if(main.prxAPI.canPrestige(p, true)) {
							main.debug("can prestige: true");
							canPrestigeMap.put(name, true);
							break;
						} else {
							main.debug("can prestige: false");
							main.sendListMessage(p, lastRankMessage);
							canPrestigeMap.remove(name);
							rankupMaxProcess.remove(name);
							break;
						}
					} else {
						main.debug("can prestige: ignored");
						main.sendListMessage(p, lastRankMessage);
						canPrestigeMap.remove(name);
						rankupMaxProcess.remove(name);
						break;
					}
				}
				//if not then continue and check for the cost
				loopNextRankCost = prxAPI.getIncreasedRankupCostX(main.playerStorage.getPlayerRebirth(p) ,main.playerStorage.getPlayerPrestige(p), main.rankStorage.getRankupCost(rp));

				rankupMaxCost.put(name, rankupMaxCost.get(name) + loopNextRankCost);
				//update values
				loopNextRankCostInString = String.valueOf(loopNextRankCost);
				loopNextRankCostFormatted = prxAPI.formatBalance(loopNextRankCost);
				loopNextRankDisplay = main.getString(main.rankStorage.getRankupDisplayName(rp), name);
				String loopRankupNoPermissionMessage = main.getString(main.messagesStorage.getStringMessage("rankup-nopermission"), p.getName()).replace("%nextrank%", loopNextRank).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay);
				Map<String, String> stringRequirements1 = prxAPI.getRankStringRequirements(rp1);
				Map<String, Double> numberRequirements1 = prxAPI.getRankNumberRequirements(rp1);
				List<String> customRequirementMessage1 = prxAPI.getRankCustomRequirementMessage(rp1);
				//check if the next rank cost is higher than player's balance
				if(loopNextRankCost > loopPlayerBalance) {
					for (String msg : notEnoughMoneyMessage) {
						p.sendMessage(main.getString(msg, name).replace("%player%", name).replace("%rankup_cost%", loopNextRankCostInString).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rankup_cost_formatted%", loopNextRankCostFormatted));
					}
					rankupMaxProcess.remove(name);
					break;
				}
				//
				if(isPerRankPermission && !p.hasPermission(main.rankupCommand.getPermission() + "." + nextRank)) {
					p.sendMessage(loopRankupNoPermissionMessage);
					rankupMaxProcess.remove(name);
					break;
				}
				boolean failedRequirements1 = false;
				if(stringRequirements1 != null) {
					for(Entry<String, String> entry : stringRequirements1.entrySet()) {
						String placeholder = prxAPI.cp(entry.getKey(), p);
						String value = prxAPI.cp(entry.getValue(), p);
						if(!placeholder.equalsIgnoreCase(value)) {
							failedRequirements1 = true;
						}
					}
				}
				if(numberRequirements1 != null) {
					for(Entry<String, Double> entry : numberRequirements1.entrySet()) {
						String placeholder = prxAPI.cp(entry.getKey(), p);
						double value = entry.getValue();
						if(Double.valueOf(placeholder) < value) {
							failedRequirements1 = true;
						}
					}
				}
				if(failedRequirements1) {
					if(customRequirementMessage1 != null) {
						customRequirementMessage1.forEach(message -> {
							p.sendMessage(prxAPI.cp(message, p));
						});
					}
					PRXAPI.TASKED_PLAYERS.remove(name);
					rankupMaxProcess.remove(name);
					break;
				}
				//after check actions
				loopRankupMsg = main.getString(main.messagesStorage.getStringMessage("rankup"), name).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rankup_cost%", loopNextRankCostInString).replace("%rankup_cost_formatted%", loopNextRankCostFormatted);
				if(!isRankupMsgLastRankOnly) {
					if(!loopRankupMsg.isEmpty())
						p.sendMessage(loopRankupMsg);
				}
				loopNextRankCommands = main.rankStorage.getRankupCommands(rp);
				if(loopNextRankCommands != null && !loopNextRankCommands.isEmpty()) {
					main.scheduler.runTask(main, () -> {
						if(main.isRankupMaxWarpFilter) {
							main.executeCachedCommandsWithWarpFilter(p, rp);
						} else {
							main.executeCachedCommands(p, rp);
						}
					});
				}
				CompletableFuture<Void> permissionFuture = null;
				if(main.rankStorage.getAddPermissionList(rp) != null && !main.rankStorage.getAddPermissionList(rp).isEmpty()) {
					permissionFuture = CompletableFuture.runAsync(() -> {
						for(String addpermission : main.rankStorage.getAddPermissionList(rp)) {
							main.perm.addPermissionAsync(p, addpermission.replace("%player%", name).replace("%rankup%", accessibleNextRank.getString()).replace("%rank%", mapRank));
						}
					});
				}
				if(main.rankStorage.getDelPermissionList(rp) != null && !main.rankStorage.getDelPermissionList(rp).isEmpty()) {
					if(permissionFuture != null) {
						CompletableFuture<Void> continuePermissionFuture = permissionFuture.thenRunAsync(() -> {
							try {
								Thread.sleep(50);
								for(String delpermission : main.rankStorage.getDelPermissionList(rp)) {
									main.perm.delPermissionAsync(p, delpermission.replace("%player%", name).replace("%rankup%", accessibleNextRank.getString()).replace("%rank%", mapRank));
								}
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}	
						});
						continuePermissionFuture.join();
					} else {
						for(String delpermission : main.rankStorage.getDelPermissionList(rp)) {
							main.perm.delPermissionAsync(p, delpermission.replace("%player%", name).replace("%rankup%", loopNextRank).replace("%rank%", loopCurrentRank));
						}
					}
				}
				loopNextRankBroadcast = main.rankStorage.getBroadcast(rp);
				if(loopNextRankBroadcast != null && !isBroadcastLastRankOnly) {
					OnlinePlayers.getPlayers().forEach(ap -> {
						if(main.isInDisabledWorld(ap)) return;
						for(String broadcast :  main.rankStorage.getBroadcast(rp)) {
							ap.sendMessage(main.getString(broadcast, name).replace("%player%", name).replace("%rankup%", main.rankStorage.getRankupName(rp)).replace("%rankup_display%", main.rankStorage.getRankupDisplayName(rp)).replace("%rankupdisplay%", main.rankStorage.getRankupDisplayName(rp)));   
						}
					});
				}
				loopNextRankMsg = main.rankStorage.getMsg(rp);
				if(loopNextRankMsg != null && !isMsgLastRankOnly) {
					for(String msg : loopNextRankMsg) {
						p.sendMessage(main.getString(msg, name).replace("%player%", name).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay));   
					}
				}
				loopNextRankActions = main.rankStorage.getActions(rp);
				if(main.isActionUtil && loopNextRankActions != null && !loopNextRankActions.isEmpty()) {
					ActionUtil.executeActions(p, loopNextRankActions);
				}
				Map<String, Double> chances = new HashMap<String, Double>();
				RankRandomCommands rrc = main.rankStorage.getRandomCommandsManager(rp);
				if(rrc != null && rrc.getRandomCommandsMap() != null) {
					for(String section : rrc.getRandomCommandsMap().keySet()) {
						double chance = rrc.getChance(section);
						chances.put(section, chance);
					}
					String randomSection = prxAPI.numberAPI.getChanceFromWeightedMap(chances);
					if(rrc.getCommands(randomSection) != null) {
						List<String> commands = rrc.getCommands(randomSection);
						List<String> replacedCommands = new ArrayList<>();
						for(String cmd : commands) {
							String pCMD = prxAPI.cp(cmd.replace("%player%", name).replace("%rankup%", loopNextRank), p);
							main.scheduler.runTask(main, () -> {
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), pCMD);
							});
						}
					}
				}
				//rankup things
				main.econ.withdrawPlayer(p, loopNextRankCost);
				rankupMaxStreak.put(name, (rankupMaxStreak.get(name)+1));
				rankups.add(loopNextRank);
				rankupMaxMap.put(name, loopNextRank);
			}
			//end of loop
			//save player data
			String mapRank = rankupMaxMap.get(name);
			RankPath rp = new RankPath(mapRank, main.prxAPI.getDefaultPath());
			List<String> endNextRankActionbarMessage = new ArrayList<>();
			Integer endNextRankActionbarInterval = null;
			List<String> endNextRankBroadcast = new ArrayList<>();
			List<String> endNextRankMsg = new ArrayList<>();
			String endRankupMessage = main.getString(main.messagesStorage.getStringMessage("rankup"), name);
			boolean endIsBroadcastLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-broadcastlastrankonly");
			boolean endIsMsgLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-msglastrankonly");
			boolean endIsRankupMsgLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-rankupmsglastrankonly");
			if(endIsBroadcastLastRankOnly) {
				List<String> broadcastList = main.rankStorage.getBroadcast(rp);
				if(broadcastList != null && !broadcastList.isEmpty()) {
					OnlinePlayers.getPlayers().forEach(ap -> {
						if(!main.isInDisabledWorld(ap)) {
							for(String broadcast :  broadcastList) {
								ap.sendMessage(main.getString(broadcast, name).replace("%player%", name).replace("%rankup%", rp.getRankName()).replace("%rankup_display%", main.rankStorage.getDisplayName(rp)).replace("%rankupdisplay%", main.rankStorage.getDisplayName(rp)));   
							}
						}
					});
				}
			}
			if(endIsMsgLastRankOnly) {
				for(String msg :  endNextRankMsg) {
					p.sendMessage(main.getString(msg, name).replace("%player%", name).replace("%rankup_display%", main.getString(main.rankStorage.getDisplayName(rp), name)));  
				}
			}
			if(endIsRankupMsgLastRankOnly && !isRankupMaxMsg) {
				if(!rankupMessage.isEmpty())
				p.sendMessage(rankupMessage.replace("%rankup%", mapRank).replace("%rankup_display%", main.getString(main.rankStorage.getDisplayName(rp), name)));
			}

			endNextRankActionbarMessage = main.rankStorage.getActionbarMessages(rp);
			endNextRankActionbarInterval = main.rankStorage.getActionbarInterval(rp);
			if(isRankupMaxMsg && !rankupFromMap.get(name).equals(mapRank)) {
				if(!rankupMaxMsg.isEmpty())
				p.sendMessage(main.getString(rankupMaxMsg, name).replace("%rank%", rankupFromMap.get(name))
						.replace("%rank_display%", prxAPI.c(prxAPI.getRankDisplay(new RankPath(rankupFromMap.get(name), rp.getPathName()))))
						.replace("%rankup%", mapRank)
						.replace("%rankup_display%", prxAPI.c(prxAPI.getRankDisplay(new RankPath(mapRank, rp.getPathName()))))
						.replace("%cost%", String.valueOf(rankupMaxCost.get(name)))
						);
			}
			prxAPI.setPlayerRank(p, mapRank);
			main.animateActionbar(p, endNextRankActionbarInterval, endNextRankActionbarMessage);
			rankupMaxPassedRanks.put(name, rankups);
			AsyncRankupMaxEvent x = new AsyncRankupMaxEvent(p, rankupFromMap.get(name), mapRank, rankupMaxStreak.get(name), rankupMaxPassedRanks.get(name), false);
			main.getServer().getPluginManager().callEvent(x);
			Bukkit.getScheduler().runTaskLater(main, () -> {	
				if(main.isRankupMaxWarpFilter) {
					if(main.rankStorage.getPlayerCommands().containsKey(mapRank)) {
						main.rankStorage.getPlayerCommands().get(mapRank).forEach(line -> {
							if(line.contains("warp")) {
								main.executeCommand(p, line);
							}
						});
					}
				}
				rankupMaxMap.remove(name);
				rankupMaxProcess.remove(name);
				rankupMaxPassedRanks.remove(name);
				rankupFromMap.remove(name);
				if(main.getPrestigeMax().isProcessing(name))
					main.getPrestigeMax().removeProcessingPlayer(name);
				if(canPrestigeMap.containsKey(name)) {
					main.prestigeAPI.prestige2(p, true);
					rankupMax(p);
				}
			}, 1);
		}).execute();
	}

	@SuppressWarnings("unused")
	public void rankupMax(final Player player, final String rankLimit) {
		Player p = player;
		String name = p.getName();
		String rankupFrom = null;
		RankPath rp1 = prxAPI.getPlayerRankPath(p);
		String limit = main.manager.matchRank(rankLimit, rp1.getPathName());
		if(!prxAPI.rankExists(limit, rp1.getPathName())) {
			return;
		}
		if(rankupMaxProcess.contains(name)) {
			p.sendMessage(prxAPI.g("rankupmax-is-on"));
			return;
		}

		double allCost = 0;
		rankupMaxCost.put(name, allCost);
		rankupMaxStreak.put(name, 0);
		rankupMaxProcess.add(name);
		//clear old data
		//player checking values

		String currentRank = prxAPI.getPlayerRank(p);
		rankupFrom = currentRank;
		rankupFromMap.put(name, rankupFrom);
		String nextRank = prxAPI.getPlayerNextRank(p);
		double playerBalance = main.econ.getBalance(p);
		RankUpdateEvent e = new RankUpdateEvent(p, RankUpdateCause.RANKUPMAX, nextRank);
		main.scheduler.runTask(main, () -> Bukkit.getPluginManager().callEvent(e));
		if(e.isCancelled()) {
			rankupMaxProcess.remove(name);
			return;
		}
		// other values
		List<String> ranksConfigList = prxAPI.getRanksCollection(rp1.getPathName());
		// if the player is at the latest rank
		if(nextRank == null) {
			main.sendListMessage(p, lastRankMessage);
			rankupMaxProcess.remove(name);
			return;
		}
		// if he had a nextrank
		// player values
		String nextRankDisplay = main.getString(prxAPI.getPlayerRankupDisplay(p), name);
		Double nextRankCost = prxAPI.getPlayerRankupCostWithIncreaseDirect(p);
		String nextRankCostInString = String.valueOf(nextRankCost);
		String nextRankCostFormatted = prxAPI.formatBalance(nextRankCost);
		Map<String, String> stringRequirements = prxAPI.getRankStringRequirements(rp1);
		Map<String, Double> numberRequirements = prxAPI.getRankNumberRequirements(rp1);
		List<String> customRequirementMessage = prxAPI.getRankCustomRequirementMessage(rp1);
		//other values [2]
		List<String> allRanksCommands = new ArrayList<>();
		List<String> rankups = new ArrayList<>();
		String rankupMessage = main.getString(main.messagesStorage.getStringMessage("rankup"), name);
		String rankupNoPermissionMessage = main.getString(main.messagesStorage.getStringMessage("rankup-nopermission"), name).replace("%nextrank%", nextRank).replace("%rankup%", nextRank).replace("%rankup_display%", nextRankDisplay);

		//if the rank cost is higher than player's balance
		if(nextRankCost > playerBalance) {
			for (String msg : notEnoughMoneyMessage) {
				p.sendMessage(main.getString(msg, name).replace("%player%", name).replace("%rankup_cost%", nextRankCostInString).replace("%rankup%", nextRank).replace("%rankup_display%", nextRankDisplay).replace("%rankup_cost_formatted%", nextRankCostFormatted));
			}
			rankupMaxProcess.remove(name);
			return;
		}
		//if per rank permission option is enabled and the player dosen't has the required permission
		if(isPerRankPermission && !p.hasPermission(main.rankupCommand.getPermission() + "." + nextRank)) {
			p.sendMessage(rankupNoPermissionMessage);
			rankupMaxProcess.remove(name);
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
			rankupMaxProcess.remove(name);
			return;
		}
		//add new player data
		rankupMaxMap.put(name, prxAPI.getPlayerRank(p));
		//@@@@@@@@@@@@@@@@@@@@
		//==============
		//~~~~~~
		// loop section
		//~~~~~~
		//==============
		//@@@@@@@@@@@@@@@@@@@@
		main.getServer().getScheduler().runTaskAsynchronously(main, () -> {
			for(String rankSection : ranksConfigList) {
				//loopValues
				String loopCurrentRank = null;
				String loopCurrentRankDisplay = null;
				String loopNextRank = null;
				Double loopNextRankCost = null;
				String loopNextRankCostInString = null;
				String loopNextRankCostFormatted = null;
				String loopNextRankDisplay = null;
				String loopRankupMsg = null;
				List<String> loopNextRankCommands = new ArrayList<>();
				List<String> loopNextRankBroadcast = new ArrayList<>();
				List<String> loopNextRankMsg = new ArrayList<>();
				List<String> loopNextRankActions = new ArrayList<>();
				Double loopPlayerBalance = main.econ.getBalance(p);
				//temporarily save player data in a map
				String mapRank = rankupMaxMap.get(name);
				RankPath rp = new RankPath(mapRank, main.prxAPI.getDefaultPath());
				loopCurrentRank = mapRank;
				//RankPath rp = main.playerStorage.getPlayerRankPath(p);
				loopCurrentRankDisplay = main.rankStorage.getDisplayName(rp);
				loopNextRank = main.rankStorage.getRankupName(rp);

				//if there is no rank next then stop the loop
				if(loopNextRank.equalsIgnoreCase("lastrank")) {
					main.sendListMessage(p, lastRankMessage);
					rankupMaxProcess.remove(name);
					break;
				}
				//if not then continue and check for the cost
				loopNextRankCost = prxAPI.getIncreasedRankupCostX(main.playerStorage.getPlayerRebirth(p) ,main.playerStorage.getPlayerPrestige(p), main.rankStorage.getRankupCost(rp));
				rankupMaxCost.put(name, rankupMaxCost.get(name) + loopNextRankCost);
				//update values
				loopNextRankCostInString = String.valueOf(loopNextRankCost);
				loopNextRankCostFormatted = prxAPI.formatBalance(loopNextRankCost);
				loopNextRankDisplay = main.getString(main.rankStorage.getRankupDisplayName(rp), name);
				String loopRankupNoPermissionMessage = main.getString(main.messagesStorage.getStringMessage("rankup-nopermission"), p.getName()).replace("%nextrank%", loopNextRank).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay);
				Map<String, String> stringRequirements1 = prxAPI.getRankStringRequirements(rp1);
				Map<String, Double> numberRequirements1 = prxAPI.getRankNumberRequirements(rp1);
				List<String> customRequirementMessage1 = prxAPI.getRankCustomRequirementMessage(rp1);
				//check if the next rank cost is higher than player's balance
				if(loopNextRankCost > loopPlayerBalance) {
					for (String msg : notEnoughMoneyMessage) {
						p.sendMessage(main.getString(msg, name).replace("%player%", name).replace("%rankup_cost%", loopNextRankCostInString).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rankup_cost_formatted%", loopNextRankCostFormatted));
					}
					rankupMaxProcess.remove(name);
					break;
				}
				//
				if(isPerRankPermission && !p.hasPermission(main.rankupCommand.getPermission() + "." + nextRank)) {
					p.sendMessage(loopRankupNoPermissionMessage);
					rankupMaxProcess.remove(name);
					break;
				}
				boolean failedRequirements1 = false;
				if(stringRequirements1 != null) {
					for(Entry<String, String> entry : stringRequirements1.entrySet()) {
						String placeholder = prxAPI.cp(entry.getKey(), p);
						String value = prxAPI.cp(entry.getValue(), p);
						if(!placeholder.equalsIgnoreCase(value)) {
							failedRequirements1 = true;
						}
					}
				}
				if(numberRequirements1 != null) {
					for(Entry<String, Double> entry : numberRequirements1.entrySet()) {
						String placeholder = prxAPI.cp(entry.getKey(), p);
						double value = entry.getValue();
						if(Double.valueOf(placeholder) < value) {
							failedRequirements1 = true;
						}
					}
				}
				if(failedRequirements1) {
					if(customRequirementMessage1 != null) {
						customRequirementMessage1.forEach(message -> {
							p.sendMessage(prxAPI.cp(message, p));
						});
					}
					PRXAPI.TASKED_PLAYERS.remove(name);
					rankupMaxProcess.remove(name);
					break;
				}
				//after check actions
				loopRankupMsg = main.getString(main.messagesStorage.getStringMessage("rankup"), name).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rankup_cost%", loopNextRankCostInString).replace("%rankup_cost_formatted%", loopNextRankCostFormatted);
				if(!isRankupMsgLastRankOnly) {
					p.sendMessage(loopRankupMsg);
				}
				loopNextRankCommands = main.rankStorage.getRankupCommands(rp);
				if(loopNextRankCommands != null && !loopNextRankCommands.isEmpty()) {
					Bukkit.getScheduler().runTask(main, () -> {
						if(main.isRankupMaxWarpFilter) {
							main.executeCachedCommandsWithWarpFilter(p, rp);
						} else {
							main.executeCachedCommands(p, rp);
						}
					});
				}

				if(main.rankStorage.getAddPermissionList(rp) != null && !main.rankStorage.getAddPermissionList(rp).isEmpty()) {
					for(String addpermission : main.rankStorage.getAddPermissionList(rp)) {
						main.perm.addPermission(p, addpermission.replace("%player%", name).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rank%", loopCurrentRank), false);
					}
				}
				if(main.rankStorage.getDelPermissionList(rp) != null && !main.rankStorage.getDelPermissionList(rp).isEmpty()) {
					for(String delpermission : main.rankStorage.getDelPermissionList(rp)) {
						main.perm.delPermission(p, delpermission.replace("%player%", name).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rank%", loopCurrentRank), false);
					}
				}
				loopNextRankBroadcast = main.rankStorage.getBroadcast(rp);
				if(loopNextRankBroadcast != null && !isBroadcastLastRankOnly) {
					OnlinePlayers.getPlayers().forEach(ap -> {
						if(main.isInDisabledWorld(ap)) return;
						for(String broadcast :  main.rankStorage.getBroadcast(rp)) {
							ap.sendMessage(main.getString(broadcast, name).replace("%player%", name).replace("%rankup%", main.rankStorage.getRankupName(rp)).replace("%rankup_display%", main.rankStorage.getRankupDisplayName(rp)).replace("%rankupdisplay%", main.rankStorage.getRankupDisplayName(rp)));   
						}
					});
				}
				loopNextRankMsg = main.rankStorage.getMsg(rp);
				if(loopNextRankMsg != null && !isMsgLastRankOnly) {
					for(String msg : loopNextRankMsg) {
						p.sendMessage(main.getString(msg, name).replace("%player%", name).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay));   
					}
				}
				loopNextRankActions = main.rankStorage.getActions(rp);
				if(main.isActionUtil && loopNextRankActions != null && !loopNextRankActions.isEmpty()) {
					ActionUtil.executeActions(p, loopNextRankActions);
				}
				Map<String, Double> chances = new HashMap<String, Double>();
				RankRandomCommands rrc = main.rankStorage.getRandomCommandsManager(rp);
				if(rrc != null && rrc.getRandomCommandsMap() != null) {
					for(String section : rrc.getRandomCommandsMap().keySet()) {
						Double chance = rrc.getChance(section);
						chances.put(section, chance);
					}
					String randomSection = prxAPI.numberAPI.getChanceFromWeightedMap(chances);
					if(rrc.getCommands(randomSection) != null) {
						List<String> commands = rrc.getCommands(randomSection);
						List<String> replacedCommands = new ArrayList<>();
						for(String cmd : commands) {
							String pCMD = prxAPI.cp(cmd.replace("%player%", name).replace("%rankup%", prxAPI.getPlayerNextRank(p)), p);
							Bukkit.getScheduler().runTask(main, () -> {
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), pCMD);
							});
						}
					}
				}
				//rankup things
				main.debug(loopNextRankCost);
				main.econ.withdrawPlayer(p, loopNextRankCost);
				rankupMaxStreak.put(name, (rankupMaxStreak.get(name)+1));
				rankups.add(loopNextRank);
				rankupMaxMap.put(name, loopNextRank);
				if(loopNextRank.equalsIgnoreCase(limit)) {
					rankupMaxProcess.remove(name);
					break;
				}
			}
			//end of loop
			//save player data
			String mapRank = rankupMaxMap.get(name);
			RankPath rp = new RankPath(mapRank, main.prxAPI.getDefaultPath());
			List<String> endNextRankActionbarMessage = new ArrayList<>();
			Integer endNextRankActionbarInterval = null;
			List<String> endNextRankBroadcast = new ArrayList<>();
			List<String> endNextRankMsg = new ArrayList<>();
			String endRankupMessage = main.getString(main.messagesStorage.getStringMessage("rankup"), name);
			boolean endIsBroadcastLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-broadcastlastrankonly");
			boolean endIsMsgLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-msglastrankonly");
			boolean endIsRankupMsgLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-rankupmsglastrankonly");
			if(endIsBroadcastLastRankOnly) {
				for(String broadcast :  endNextRankBroadcast) {
					Bukkit.broadcastMessage(main.getString(broadcast, name).replace("%player%", name).replace("%rankup_display%", main.getString(main.rankStorage.getDisplayName(rp), name)));
				}
			}
			if(endIsMsgLastRankOnly) {
				for(String msg :  endNextRankMsg) {
					p.sendMessage(main.getString(msg, name).replace("%player%", name).replace("%rankup_display%", main.getString(main.rankStorage.getDisplayName(rp), name)));  
				}
			}
			if(endIsRankupMsgLastRankOnly && !isRankupMaxMsg) {
				p.sendMessage(rankupMessage.replace("%rankup%", mapRank).replace("%rankup_display%", main.getString(main.rankStorage.getDisplayName(rp), name)));
			}

			endNextRankActionbarMessage = main.rankStorage.getActionbarMessages(rp);
			endNextRankActionbarInterval = main.rankStorage.getActionbarInterval(rp);
			if(isRankupMaxMsg) {
				p.sendMessage(main.getString(rankupMaxMsg, name).replace("%rank%", rankupFromMap.get(name))
						.replace("%rank_display%", prxAPI.c(prxAPI.getRankDisplay(new RankPath(rankupFromMap.get(name), rp.getPathName()))))
						.replace("%rankup%", mapRank)
						.replace("%rankup_display%", prxAPI.c(prxAPI.getRankDisplay(new RankPath(mapRank, rp.getPathName()))))
						.replace("%cost%", String.valueOf(rankupMaxCost.get(name)))
						);
			}
			prxAPI.setPlayerRank(p, mapRank);
			main.animateActionbar(p, endNextRankActionbarInterval, endNextRankActionbarMessage);
			rankupMaxPassedRanks.put(name, rankups);
			AsyncRankupMaxEvent x = new AsyncRankupMaxEvent(p, rankupFromMap.get(name), mapRank, rankupMaxStreak.get(name), rankupMaxPassedRanks.get(name), true);
			main.getServer().getPluginManager().callEvent(x);
			Bukkit.getScheduler().runTaskLater(main, () -> {
				if(main.isRankupMaxWarpFilter) {
					if(main.rankStorage.getPlayerCommands().containsKey(mapRank)) {
						main.rankStorage.getPlayerCommands().get(mapRank).forEach(line -> {
							if(line.contains("warp")) {
								main.executeCommand(p, line);
							}
						});
					}
				}
				prxAPI.celeberate(p);
				rankupMaxMap.remove(name);
				rankupMaxProcess.remove(name);
				rankupMaxPassedRanks.remove(name);
				rankupFromMap.remove(name);
			}, 1);
		});
	}

}
