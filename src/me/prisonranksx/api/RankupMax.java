package me.prisonranksx.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import io.samdev.actionutil.ActionUtil;
import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.data.RankRandomCommands;
import me.prisonranksx.events.AsyncRankupMaxEvent;
import me.prisonranksx.events.RankUpdateCause;
import me.prisonranksx.events.RankUpdateEvent;
import me.prisonranksx.utils.CollectionUtils;
public class RankupMax {

	
	public final Map<OfflinePlayer, String> rankupMaxMap;
	public final Map<OfflinePlayer, List<String>> rankupMaxCommandsMap;
	public final Map<OfflinePlayer, String> rankupMaxRecentRankupMap;
    public final Set<OfflinePlayer> rankupMaxProcess;
    public final Map<OfflinePlayer, Integer> rankupMaxStreak;
    public final Map<OfflinePlayer, String> rankupFromMap;
    public final Map<OfflinePlayer, List<String>> rankupMaxPassedRanks;
    public final Map<OfflinePlayer, Double> rankupMaxCost;
    public final Map<OfflinePlayer, Boolean> canPrestigeMap;
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
		this.rankupMaxCommandsMap = new HashMap<>();
		this.rankupMaxRecentRankupMap = new HashMap<>();
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
        if(rankupMaxProcess.contains(p)) {
        	p.sendMessage(prxAPI.g("rankupmax-is-on"));
        	return;
        }
        double allCost = 0;
        rankupMaxCost.put(p, allCost);
        rankupMaxStreak.put(p, 0);
        rankupMaxProcess.add(p);
        //clear old data
        //player checking values
        RankPath rp1 = prxAPI.getPlayerRankPath(p);
        String currentRank = rp1.getRankName();
        String currentPath = rp1.getPathName();
        rankupFrom = currentRank;
        rankupFromMap.put(p, rankupFrom);
        String nextRank = prxAPI.getPlayerNextRank(p);
        Double playerBalance = main.econ.getBalance(p);
        Map<String, String> stringRequirements = prxAPI.getRankStringRequirements(rp1);
		Map<String, Double> numberRequirements = prxAPI.getRankNumberRequirements(rp1);
		List<String> customRequirementMessage = prxAPI.getRankCustomRequirementMessage(rp1);
        RankUpdateEvent e = new RankUpdateEvent(p, RankUpdateCause.RANKUPMAX, nextRank);
        Bukkit.getPluginManager().callEvent(e);
        if(e.isCancelled()) {
        	rankupMaxProcess.remove(p);
        	return;
        }
        // other values
        List<String> ranksConfigList = prxAPI.getRanksCollection(currentPath);
        int currentIndex = ranksConfigList.indexOf(currentRank);
        int size = ranksConfigList.size();
        boolean canPrestige = prxAPI.canPrestige(p);
        // if the player is at the latest rank
        if(nextRank == null && !canPrestigeMap.containsKey(p) && !canPrestige) {
        	main.sendListMessage(p, lastRankMessage);
        	rankupMaxProcess.remove(p);
        	return;
        }
        // if he had a nextrank
        // player values
        String nextRankDisplay = !canPrestigeMap.containsKey(p) && !canPrestige ? main.getString(prxAPI.getPlayerRankupDisplay(p), name) : "null";
	    double nextRankCost = !canPrestigeMap.containsKey(p) && !canPrestige ? prxAPI.getPlayerRankupCostWithIncreaseDirect(p) : 0.0;
	    String nextRankCostInString = String.valueOf(nextRankCost);
        String nextRankCostFormatted = prxAPI.formatBalance(nextRankCost);
        //other values [2]
    	List<String> allRanksCommands = CollectionUtils.EMPTY_STRING_LIST;
    	List<String> rankups = CollectionUtils.EMPTY_STRING_LIST;
    	String rankupMessage = !canPrestigeMap.containsKey(p) && !canPrestige ? main.getString(main.messagesStorage.getStringMessage("rankup"), name) : "null";
    	String rankupNoPermissionMessage = !canPrestigeMap.containsKey(p) && !canPrestige ? main.getString(main.messagesStorage.getStringMessage("rankup-nopermission"), name).replace("%nextrank%", nextRank).replace("%rankup%", nextRank).replace("%rankup_display%", nextRankDisplay) : "null";
        //if the rank cost is higher than player's balance
        if(nextRankCost > playerBalance && !canPrestigeMap.containsKey(p) && !canPrestige) {
            for (String msg : notEnoughMoneyMessage) {
                p.sendMessage(main.getString(msg, name).replace("%player%", name).replace("%rankup_cost%", nextRankCostInString).replace("%rankup%", nextRank).replace("%rankup_display%", nextRankDisplay).replace("%rankup_cost_formatted%", nextRankCostFormatted));
            }
            rankupMaxProcess.remove(p);
        	return;
        }
        //if per rank permission option is enabled and the player dosen't has the required permission
        if(isPerRankPermission && !p.hasPermission(main.rankupCommand.getPermission() + "." + nextRank) && !canPrestigeMap.containsKey(p) && !canPrestige) {
        	p.sendMessage(rankupNoPermissionMessage);
        	rankupMaxProcess.remove(p);
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
			prxAPI.taskedPlayers.remove(name);
			rankupMaxProcess.remove(p);
			return;
		}
        canPrestigeMap.remove(p);
        String lastRank = prxAPI.getPathsCount() > 1 ? prxAPI.getLastRank(currentPath) : prxAPI.getLastRank();
        //add new player data
 	   rankupMaxMap.put(p, prxAPI.getPlayerRank(p));
        //@@@@@@@@@@@@@@@@@@@@
        //==============
        //~~~~~~
        // loop section
        //~~~~~~
        //==============
        //@@@@@@@@@@@@@@@@@@@@
 	  main.getServer().getScheduler().runTaskAsynchronously(main, () -> {
        for(int i = currentIndex; i < size; i++) {
        	//loopValues
        	String loopCurrentRank = null;
        	String loopCurrentRankDisplay = null;
        	String loopNextRank = null;
        	Double loopNextRankCost = null;
        	String loopNextRankCostInString = null;
        	String loopNextRankCostFormatted = null;
        	String loopNextRankDisplay = null;
        	String loopRankupMsg = null;
        	List<String> loopNextRankCommands = CollectionUtils.EMPTY_STRING_LIST;
        	List<String> loopNextRankBroadcast = CollectionUtils.EMPTY_STRING_LIST;
        	List<String> loopNextRankMsg = CollectionUtils.EMPTY_STRING_LIST;
        	List<String> loopNextRankActions = CollectionUtils.EMPTY_STRING_LIST;
        	double loopPlayerBalance = main.econ.getBalance(p);
        	//temporarily save player data in a map
        	String mapRank = rankupMaxMap.get(p);
        	   RankPath rp = new RankPath(mapRank, main.prxAPI.getDefaultPath());
        	   loopCurrentRank = mapRank;
        	   //RankPath rp = main.playerStorage.getPlayerRankPath(p);
        	   loopCurrentRankDisplay = main.rankStorage.getDisplayName(rp);
        	   loopNextRank = main.rankStorage.getRankupName(rp);
        	   if(loopNextRank.equals(lastRank)) {
        		   break;
        	   }
        	   //if there is no rank next then stop the loop
        	   if(loopNextRank.equalsIgnoreCase("lastrank")) {
        		   if(main.getGlobalStorage().getBooleanData("Options.rankupmax-with-prestige")) {
        			   if(main.prxAPI.canPrestige(p, true)) {
        				   main.debug("can prestige: true");
        				   canPrestigeMap.put(p, true);
        				   break;
        			   } else {
        				   main.debug("can prestige: false");
                		   main.sendListMessage(p, lastRankMessage);
                		   canPrestigeMap.remove(p);
                		   rankupMaxProcess.remove(p);
                		   break;
        			   }
        		   } else {
        		   main.debug("can prestige: ignored");
        		   main.sendListMessage(p, lastRankMessage);
        		   canPrestigeMap.remove(p);
        		   rankupMaxProcess.remove(p);
        		   break;
        		   }
        	   }
        	   //if not then continue and check for the cost
              	loopNextRankCost = prxAPI.getIncreasedRankupCostX(main.playerStorage.getPlayerRebirth(p) ,main.playerStorage.getPlayerPrestige(p), main.rankStorage.getRankupCost(rp));

               rankupMaxCost.put(p, rankupMaxCost.get(p) + loopNextRankCost);
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
                   rankupMaxProcess.remove(p);
            	   break;
               }
               //
               if(isPerRankPermission && !p.hasPermission(main.rankupCommand.getPermission() + "." + nextRank)) {
               	p.sendMessage(loopRankupNoPermissionMessage);
               	rankupMaxProcess.remove(p);
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
   				prxAPI.taskedPlayers.remove(name);
   				rankupMaxProcess.remove(p);
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
  	        	main.perm.addPermission(p, addpermission.replace("%player%", name).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rank%", loopCurrentRank));
  	          }
  	        }
  	        if(main.rankStorage.getDelPermissionList(rp) != null && !main.rankStorage.getDelPermissionList(rp).isEmpty()) {
  	        	for(String delpermission : main.rankStorage.getDelPermissionList(rp)) {
  	        		main.perm.delPermission(p, delpermission.replace("%player%", name).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rank%", loopCurrentRank));
  	        	}
  	        }
               loopNextRankBroadcast = main.rankStorage.getBroadcast(rp);
               if(loopNextRankBroadcast != null && !isBroadcastLastRankOnly) {
                 for(String broadcast :  loopNextRankBroadcast) {
            	     Bukkit.broadcastMessage(main.getString(broadcast, name).replace("%player%", name).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rankupdisplay%", loopNextRankDisplay));   
                 }
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
   				String pCMD = prxAPI.cp(cmd.replace("%player%", name).replace("%rankup%", loopNextRank), p);
   				Bukkit.getScheduler().runTask(main, () -> {
   				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), pCMD);
   				});
   			}
   			}
   			}
               //rankup things
               main.econ.withdrawPlayer(p, loopNextRankCost);
               rankupMaxStreak.put(p, (rankupMaxStreak.get(p)+1));
               rankups.add(loopNextRank);
               rankupMaxMap.put(p, loopNextRank);
        }
        //end of loop
        //save player data
        String mapRank = rankupMaxMap.get(p);
        RankPath rp = new RankPath(mapRank, main.prxAPI.getDefaultPath());
    	List<String> endNextRankActionbarMessage = CollectionUtils.EMPTY_STRING_LIST;
    	int endNextRankActionbarInterval = 0;
    	List<String> endNextRankBroadcast = CollectionUtils.EMPTY_STRING_LIST;
    	List<String> endNextRankMsg = CollectionUtils.EMPTY_STRING_LIST;
    	String endRankupMessage = main.getString(main.messagesStorage.getStringMessage("rankup"), name);
    	boolean endIsBroadcastLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-broadcastlastrankonly");
    	boolean endIsMsgLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-msglastrankonly");
    	boolean endIsRankupMsgLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-rankupmsglastrankonly");
        if(endIsBroadcastLastRankOnly) {
            for(String broadcast : endNextRankBroadcast) {
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
        if(isRankupMaxMsg && !rankupFromMap.get(p).equals(mapRank)) {
        	p.sendMessage(main.getString(rankupMaxMsg, p.getName()).replace("%rank%", rankupFromMap.get(p))
        			.replace("%rank_display%", prxAPI.c(prxAPI.getRankDisplay(new RankPath(rankupFromMap.get(p), rp.getPathName()))))
        			.replace("%rankup%", mapRank)
        			.replace("%rankup_display%", prxAPI.c(prxAPI.getRankDisplay(new RankPath(mapRank, rp.getPathName()))))
        			.replace("%cost%", String.valueOf(rankupMaxCost.get(p)))
        			);
        }
		prxAPI.setPlayerRank(p, mapRank);
        main.animateActionbar(p, endNextRankActionbarInterval, endNextRankActionbarMessage);
        rankupMaxPassedRanks.put(p, rankups);
		AsyncRankupMaxEvent x = new AsyncRankupMaxEvent(p, rankupFromMap.get(p), mapRank, rankupMaxStreak.get(p), rankupMaxPassedRanks.get(p));
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
        rankupMaxMap.remove(p);
        rankupMaxProcess.remove(p);
        rankupMaxPassedRanks.remove(p);
        rankupFromMap.remove(p);
        if(canPrestigeMap.containsKey(p)) {
        	main.prestigeAPI.prestige2(p, true);
            rankupMax(p);
        }
		}, 1);
 	  });
	}
	
	@SuppressWarnings("unused")
	public void rankupMax(final Player player, final String rankLimit) {
        Player p = player;
        String rankupFrom = null;
        RankPath rp1 = prxAPI.getPlayerRankPath(p);
        String limit = main.manager.matchRank(rankLimit, rp1.getPathName());
        if(!prxAPI.rankExists(limit, rp1.getPathName())) {
        	return;
        }
        if(rankupMaxProcess.contains(p)) {
        	p.sendMessage(prxAPI.g("rankupmax-is-on"));
        	return;
        }
        double allCost = 0;
        rankupMaxCost.put(p, allCost);
        rankupMaxStreak.put(p, 0);
        rankupMaxProcess.add(p);
        //clear old data
        //player checking values
        
        String currentRank = prxAPI.getPlayerRank(p);
        rankupFrom = currentRank;
        rankupFromMap.put(p, rankupFrom);
        String nextRank = prxAPI.getPlayerNextRank(p);
        Double playerBalance = main.econ.getBalance(p);
        RankUpdateEvent e = new RankUpdateEvent(p, RankUpdateCause.RANKUPMAX, nextRank);
        Bukkit.getPluginManager().callEvent(e);
        if(e.isCancelled()) {
        	rankupMaxProcess.remove(p);
        	return;
        }
        // other values
        List<String> ranksConfigList = prxAPI.getRanksCollection(rp1.getPathName());
        // if the player is at the latest rank
        if(nextRank == null) {
        	main.sendListMessage(p, lastRankMessage);
        	rankupMaxProcess.remove(p);
        	return;
        }
        // if he had a nextrank
        // player values
        String nextRankDisplay = main.getString(prxAPI.getPlayerRankupDisplay(p), p.getName());
	    Double nextRankCost = prxAPI.getPlayerRankupCostWithIncreaseDirect(p);
	    String nextRankCostInString = String.valueOf(nextRankCost);
        String nextRankCostFormatted = prxAPI.formatBalance(nextRankCost);
        Map<String, String> stringRequirements = prxAPI.getRankStringRequirements(rp1);
		Map<String, Double> numberRequirements = prxAPI.getRankNumberRequirements(rp1);
		List<String> customRequirementMessage = prxAPI.getRankCustomRequirementMessage(rp1);
        //other values [2]
    	List<String> allRanksCommands = new ArrayList<>();
    	List<String> rankups = new ArrayList<>();
    	String rankupMessage = main.getString(main.messagesStorage.getStringMessage("rankup"), p.getName());
    	String rankupNoPermissionMessage = main.getString(main.messagesStorage.getStringMessage("rankup-nopermission"), p.getName()).replace("%nextrank%", nextRank).replace("%rankup%", nextRank).replace("%rankup_display%", nextRankDisplay);
    	
        //if the rank cost is higher than player's balance
        if(nextRankCost > playerBalance) {
            for (String msg : notEnoughMoneyMessage) {
                p.sendMessage(main.getString(msg, p.getName()).replace("%player%", p.getName()).replace("%rankup_cost%", nextRankCostInString).replace("%rankup%", nextRank).replace("%rankup_display%", nextRankDisplay).replace("%rankup_cost_formatted%", nextRankCostFormatted));
            }
            rankupMaxProcess.remove(p);
        	return;
        }
        //if per rank permission option is enabled and the player dosen't has the required permission
        if(isPerRankPermission && !p.hasPermission(main.rankupCommand.getPermission() + "." + nextRank)) {
        	p.sendMessage(rankupNoPermissionMessage);
        	rankupMaxProcess.remove(p);
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
			prxAPI.taskedPlayers.remove(p.getName());
			rankupMaxProcess.remove(p);
			return;
		}
        //add new player data
 	   rankupMaxMap.put(p, prxAPI.getPlayerRank(p));
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
        	String mapRank = rankupMaxMap.get(p);
        	   RankPath rp = new RankPath(mapRank, main.prxAPI.getDefaultPath());
        	   loopCurrentRank = mapRank;
        	   //RankPath rp = main.playerStorage.getPlayerRankPath(p);
        	   loopCurrentRankDisplay = main.rankStorage.getDisplayName(rp);
        	   loopNextRank = main.rankStorage.getRankupName(rp);

        	   //if there is no rank next then stop the loop
        	   if(loopNextRank.equalsIgnoreCase("lastrank")) {
        		   main.sendListMessage(p, lastRankMessage);
        		   rankupMaxProcess.remove(p);
        		   break;
        	   }
        	   //if not then continue and check for the cost
              	loopNextRankCost = prxAPI.getIncreasedRankupCostX(main.playerStorage.getPlayerRebirth(p) ,main.playerStorage.getPlayerPrestige(p), main.rankStorage.getRankupCost(rp));
               rankupMaxCost.put(p, rankupMaxCost.get(p) + loopNextRankCost);
               //update values
               loopNextRankCostInString = String.valueOf(loopNextRankCost);
               loopNextRankCostFormatted = prxAPI.formatBalance(loopNextRankCost);
               loopNextRankDisplay = main.getString(main.rankStorage.getRankupDisplayName(rp), p.getName());
               String loopRankupNoPermissionMessage = main.getString(main.messagesStorage.getStringMessage("rankup-nopermission"), p.getName()).replace("%nextrank%", loopNextRank).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay);
               Map<String, String> stringRequirements1 = prxAPI.getRankStringRequirements(rp1);
       		   Map<String, Double> numberRequirements1 = prxAPI.getRankNumberRequirements(rp1);
       		   List<String> customRequirementMessage1 = prxAPI.getRankCustomRequirementMessage(rp1);
        	   //check if the next rank cost is higher than player's balance
               if(loopNextRankCost > loopPlayerBalance) {
                   for (String msg : notEnoughMoneyMessage) {
                       p.sendMessage(main.getString(msg, p.getName()).replace("%player%", p.getName()).replace("%rankup_cost%", loopNextRankCostInString).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rankup_cost_formatted%", loopNextRankCostFormatted));
                   }
                   rankupMaxProcess.remove(p);
            	   break;
               }
               //
               if(isPerRankPermission && !p.hasPermission(main.rankupCommand.getPermission() + "." + nextRank)) {
               	p.sendMessage(loopRankupNoPermissionMessage);
               	rankupMaxProcess.remove(p);
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
      				prxAPI.taskedPlayers.remove(p.getName());
      				rankupMaxProcess.remove(p);
      				break;
      			}
               //after check actions
               loopRankupMsg = main.getString(main.messagesStorage.getStringMessage("rankup"), p.getName()).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rankup_cost%", loopNextRankCostInString).replace("%rankup_cost_formatted%", loopNextRankCostFormatted);
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
  	        	main.perm.addPermission(p, addpermission.replace("%player%", p.getName()).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rank%", loopCurrentRank));
  	          }
  	        }
  	        if(main.rankStorage.getDelPermissionList(rp) != null && !main.rankStorage.getDelPermissionList(rp).isEmpty()) {
  	        	for(String delpermission : main.rankStorage.getDelPermissionList(rp)) {
  	        		main.perm.delPermission(p, delpermission.replace("%player%", p.getName()).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rank%", loopCurrentRank));
  	        	}
  	        }
               loopNextRankBroadcast = main.rankStorage.getBroadcast(rp);
               if(loopNextRankBroadcast != null && !isBroadcastLastRankOnly) {
                 for(String broadcast :  loopNextRankBroadcast) {
            	     Bukkit.broadcastMessage(main.getString(broadcast, p.getName()).replace("%player%", p.getName()).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rankupdisplay%", loopNextRankDisplay));   
                 }
               }
               loopNextRankMsg = main.rankStorage.getMsg(rp);
               if(loopNextRankMsg != null && !isMsgLastRankOnly) {
                 for(String msg : loopNextRankMsg) {
            	     p.sendMessage(main.getString(msg, p.getName()).replace("%player%", p.getName()).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay));   
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
   				String pCMD = prxAPI.cp(cmd.replace("%player%", p.getName()).replace("%rankup%", prxAPI.getPlayerNextRank(p)), p);
   				Bukkit.getScheduler().runTask(main, () -> {
   				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), pCMD);
   				});
   			}
   			}
   			}
               //rankup things
   			   main.debug(loopNextRankCost);
               main.econ.withdrawPlayer(p, loopNextRankCost);
               rankupMaxStreak.put(p, (rankupMaxStreak.get(p)+1));
               rankups.add(loopNextRank);
               rankupMaxMap.put(p, loopNextRank);
        	   if(loopNextRank.equalsIgnoreCase(limit)) {
       		   rankupMaxProcess.remove(p);
       		   break;
       	       }
        }
        //end of loop
        //save player data
        String mapRank = rankupMaxMap.get(p);
        RankPath rp = new RankPath(mapRank, main.prxAPI.getDefaultPath());
    	List<String> endNextRankActionbarMessage = new ArrayList<>();
    	Integer endNextRankActionbarInterval = null;
    	List<String> endNextRankBroadcast = new ArrayList<>();
    	List<String> endNextRankMsg = new ArrayList<>();
    	String endRankupMessage = main.getString(main.messagesStorage.getStringMessage("rankup"), p.getName());
    	boolean endIsBroadcastLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-broadcastlastrankonly");
    	boolean endIsMsgLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-msglastrankonly");
    	boolean endIsRankupMsgLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-rankupmsglastrankonly");
        if(endIsBroadcastLastRankOnly) {
            for(String broadcast :  endNextRankBroadcast) {
       	     Bukkit.broadcastMessage(main.getString(broadcast, p.getName()).replace("%player%", p.getName()).replace("%rankup_display%", main.getString(main.rankStorage.getDisplayName(rp), p.getName())));
            }
        }
        if(endIsMsgLastRankOnly) {
            for(String msg :  endNextRankMsg) {
          	     p.sendMessage(main.getString(msg, p.getName()).replace("%player%", p.getName()).replace("%rankup_display%", main.getString(main.rankStorage.getDisplayName(rp), p.getName())));  
             }
        }
        if(endIsRankupMsgLastRankOnly && !isRankupMaxMsg) {
        	p.sendMessage(rankupMessage.replace("%rankup%", mapRank).replace("%rankup_display%", main.getString(main.rankStorage.getDisplayName(rp), p.getName())));
        }
        
        endNextRankActionbarMessage = main.rankStorage.getActionbarMessages(rp);
        endNextRankActionbarInterval = main.rankStorage.getActionbarInterval(rp);
        if(isRankupMaxMsg) {
        	p.sendMessage(main.getString(rankupMaxMsg, p.getName()).replace("%rank%", rankupFromMap.get(p))
        			.replace("%rank_display%", prxAPI.c(prxAPI.getRankDisplay(new RankPath(rankupFromMap.get(p), rp.getPathName()))))
        			.replace("%rankup%", mapRank)
        			.replace("%rankup_display%", prxAPI.c(prxAPI.getRankDisplay(new RankPath(mapRank, rp.getPathName()))))
        			.replace("%cost%", String.valueOf(rankupMaxCost.get(p)))
        			);
        }
		prxAPI.setPlayerRank(p, mapRank);
        main.animateActionbar(p, endNextRankActionbarInterval, endNextRankActionbarMessage);
        rankupMaxPassedRanks.put(p, rankups);
		AsyncRankupMaxEvent x = new AsyncRankupMaxEvent(p, rankupFromMap.get(p), mapRank, rankupMaxStreak.get(p), rankupMaxPassedRanks.get(p));
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
        rankupMaxMap.remove(p);
        rankupMaxProcess.remove(p);
        rankupMaxPassedRanks.remove(p);
        rankupFromMap.remove(p);
		}, 1);
 	  });
	}
	
}
