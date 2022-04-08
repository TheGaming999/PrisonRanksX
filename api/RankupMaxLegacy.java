package me.prisonranksx.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.samdev.actionutil.ActionUtil;
import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.data.RankRandomCommands;
import me.prisonranksx.events.RankUpdateCause;
import me.prisonranksx.events.RankUpdateEvent;
import me.prisonranksx.events.AsyncRankupMaxEvent;
import me.prisonranksx.utils.XUUID;

public class RankupMaxLegacy {

	
	public final Map<Player, String> rankupMaxMap;
	public final Map<Player, List<String>> rankupMaxCommandsMap;
	public final Map<Player, String> rankupMaxRecentRankupMap;
    public final Set<Player> rankupMaxProcess;
    public final Map<Player, Integer> rankupMaxStreak;
    public final Map<Player, String> rankupFromMap;
    public final Map<Player, List<String>> rankupMaxPassedRanks;
    public final Map<Player, Double> rankupMaxCost;
    public final Map<Player, Boolean> canPrestigeMap;
    public final String rankupMaxMsg;
    public boolean isRankupMaxMsg;
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	private PRXAPI prxAPI;
	
	public RankupMaxLegacy() {
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
		this.rankupMaxMsg = main.messagesStorage.getStringMessage("rankupmax");
		this.isRankupMaxMsg = main.globalStorage.getBooleanData("Options.send-rankupmaxmsg");
	}
	
	@SuppressWarnings({ "unused", "deprecation" })
	public void rankupMax(final Player player) {
        Player p = player;
        String name = p.getName();
        UUID u = XUUID.tryNameConvert(name);
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
        RankPath rp1 = prxAPI.getPlayerRankPath(u);
        String currentRank = prxAPI.getPlayerRank(u);
        rankupFrom = currentRank;
        rankupFromMap.put(p, rankupFrom);
        String nextRank = prxAPI.getPlayerNextRank(u);
        Double playerBalance = main.prxAPI.getPlayerMoneyOnline(p);
        RankUpdateEvent e = new RankUpdateEvent(p, RankUpdateCause.RANKUPMAX, nextRank);
        Bukkit.getPluginManager().callEvent(e);
        if(e.isCancelled()) {
        	rankupMaxProcess.remove(p);
        	return;
        }
        // other values
        List<String> ranksConfigList = prxAPI.getRanksCollection(rp1.getPathName());
        List<String> lastRankMessage = main.messagesStorage.getStringListMessage("lastrank");
        List<String> notEnoughMoneyMessage = main.messagesStorage.getStringListMessage("notenoughmoney");
        // if the player is at the latest rank
        if(nextRank == null && !canPrestigeMap.containsKey(p)) {
        	main.sendListMessage(p, lastRankMessage);
        	rankupMaxProcess.remove(p);
        	return;
        }
        // if he had a nextrank
        // player values
        String nextRankDisplay = main.getString(prxAPI.getPlayerRankupDisplay(u), p.getName());
	    Double nextRankCost = prxAPI.getPlayerRankupCostWithIncreaseDirect(u);
	    String nextRankCostInString = String.valueOf(nextRankCost);
        String nextRankCostFormatted = prxAPI.formatBalance(nextRankCost);
        //other values [2]
    	List<String> allRanksCommands = new ArrayList<>();
    	List<String> rankups = new ArrayList<>();
    	String rankupMessage = main.getString(main.messagesStorage.getStringMessage("rankup"), p.getName());
    	boolean isBroadcastLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-broadcastlastrankonly");
    	boolean isMsgLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-msglastrankonly");
    	boolean isRankupMsgLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-rankupmsglastrankonly");
    	boolean isPerRankPermission = main.globalStorage.getBooleanData("Options.per-rank-permission");
    	String rankupNoPermissionMessage = main.getString(main.messagesStorage.getStringMessage("rankup-nopermission"), p.getName()).replace("%nextrank%", nextRank).replace("%rankup%", nextRank).replace("%rankup_display%", nextRankDisplay);
        canPrestigeMap.remove(p);
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
        //add new player data
 	   rankupMaxMap.put(p, prxAPI.getPlayerRank(u));
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
        	Double loopPlayerBalance = main.prxAPI.getPlayerMoneyOnline(p);
        	//temporarily save player data in a map
        	   RankPath rp = new RankPath(rankupMaxMap.get(p), main.prxAPI.getDefaultPath());
        	   loopCurrentRank = rankupMaxMap.get(p);
        	   //RankPath rp = main.playerStorage.getPlayerRankPath(p);
        	   loopCurrentRankDisplay = main.rankStorage.getDisplayName(rp);
        	   loopNextRank = main.rankStorage.getRankupName(rp);
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
              	loopNextRankCost = prxAPI.getIncreasedRankupCostX(main.playerStorage.getPlayerRebirth(u) ,main.playerStorage.getPlayerPrestige(u), main.rankStorage.getRankupCost(rp));

               rankupMaxCost.put(p, rankupMaxCost.get(p) + loopNextRankCost);
               //update values
               loopNextRankCostInString = String.valueOf(loopNextRankCost);
               loopNextRankCostFormatted = prxAPI.formatBalance(loopNextRankCost);
               loopNextRankDisplay = main.getString(main.rankStorage.getRankupDisplayName(rp), p.getName());
               String loopRankupNoPermissionMessage = main.getString(main.messagesStorage.getStringMessage("rankup-nopermission"), p.getName()).replace("%nextrank%", loopNextRank).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay);
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
  	        	main.perm.addPermission(name, addpermission.replace("%player%", p.getName()).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rank%", loopCurrentRank));
  	          }
  	        }
  	        if(main.rankStorage.getDelPermissionList(rp) != null && !main.rankStorage.getDelPermissionList(rp).isEmpty()) {
  	        	for(String delpermission : main.rankStorage.getDelPermissionList(rp)) {
  	        		main.perm.delPermission(name, delpermission.replace("%player%", p.getName()).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rank%", loopCurrentRank));
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
   				String pCMD = prxAPI.cp(cmd.replace("%player%", p.getName()).replace("%rankup%", prxAPI.getPlayerNextRank(u)), p);
   				Bukkit.getScheduler().runTask(main, () -> {
   				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), pCMD);
   				});
   			}
   			}
   			}
               //rankup things
               main.econ.withdrawPlayer(name, loopNextRankCost);
               rankupMaxStreak.put(p, (rankupMaxStreak.get(p)+1));
               rankups.add(loopNextRank);
               rankupMaxMap.put(p, loopNextRank);
        }
        //end of loop
        //save player data
        RankPath rp = new RankPath(rankupMaxMap.get(p), main.prxAPI.getDefaultPath());
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
        	p.sendMessage(rankupMessage.replace("%rankup%", rankupMaxMap.get(p)).replace("%rankup_display%", main.getString(main.rankStorage.getDisplayName(rp), p.getName())));
        }
        
        endNextRankActionbarMessage = main.rankStorage.getActionbarMessages(rp);
        endNextRankActionbarInterval = main.rankStorage.getActionbarInterval(rp);
        if(isRankupMaxMsg) {
        	p.sendMessage(main.getString(rankupMaxMsg, p.getName()).replace("%rank%", rankupFromMap.get(p))
        			.replace("%rank_display%", prxAPI.c(prxAPI.getRankDisplay(new RankPath(rankupFromMap.get(p), rp.getPathName()))))
        			.replace("%rankup%", rankupMaxMap.get(p))
        			.replace("%rankup_display%", prxAPI.c(prxAPI.getRankDisplay(new RankPath(rankupMaxMap.get(p), rp.getPathName()))))
        			.replace("%cost%", String.valueOf(rankupMaxCost.get(p)))
        			);
        }
		prxAPI.setPlayerRank(p, rankupMaxMap.get(p));
        main.animateActionbar(p, endNextRankActionbarInterval, endNextRankActionbarMessage);
        rankupMaxPassedRanks.put(p, rankups);
		AsyncRankupMaxEvent x = new AsyncRankupMaxEvent(p, rankupFromMap.get(p), rankupMaxMap.get(p), rankupMaxStreak.get(p), rankupMaxPassedRanks.get(p), false);
		main.getServer().getPluginManager().callEvent(x);
		Bukkit.getScheduler().runTaskLater(main, () -> {
        rankupMaxMap.remove(p);
        rankupMaxProcess.remove(p);
        rankupMaxPassedRanks.remove(p);
        rankupFromMap.remove(p);
        if(canPrestigeMap.containsKey(p)) {
        	main.prestigeLegacy.prestige2(p, true);
            rankupMax(p);
            return;
        }
        if(main.isRankupMaxWarpFilter) {
          if(main.rankStorage.getPlayerCommands().containsKey(rankupMaxMap.get(p))) {
        	main.rankStorage.getPlayerCommands().get(rankupMaxMap.get(p)).forEach(line -> {
        		if(line.contains("warp")) {
        			main.executeCommand(p, line);
        		}
        	});
          }
        }
		}, 1);
 	  });
	}
	
	@SuppressWarnings({ "unused", "deprecation" })
	public void rankupMax(final Player player, final String rankLimit) {
        Player p = player;
        String name = p.getName();
        UUID u = XUUID.tryNameConvert(name);
        String rankupFrom = null;
        RankPath rp1 = prxAPI.getPlayerRankPath(u);
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
        
        String currentRank = prxAPI.getPlayerRank(u);
        rankupFrom = currentRank;
        rankupFromMap.put(p, rankupFrom);
        String nextRank = prxAPI.getPlayerNextRank(u);
        Double playerBalance = main.prxAPI.getPlayerMoneyOnline(p);
        RankUpdateEvent e = new RankUpdateEvent(p, RankUpdateCause.RANKUPMAX, nextRank);
        Bukkit.getPluginManager().callEvent(e);
        if(e.isCancelled()) {
        	rankupMaxProcess.remove(p);
        	return;
        }
        // other values
        List<String> ranksConfigList = prxAPI.getRanksCollection(rp1.getPathName());
        List<String> lastRankMessage = main.messagesStorage.getStringListMessage("lastrank");
        List<String> notEnoughMoneyMessage = main.messagesStorage.getStringListMessage("notenoughmoney");
        // if the player is at the latest rank
        if(nextRank == null) {
        	main.sendListMessage(p, lastRankMessage);
        	rankupMaxProcess.remove(p);
        	return;
        }
        // if he had a nextrank
        // player values
        String nextRankDisplay = main.getString(prxAPI.getPlayerRankupDisplay(u), p.getName());
	    Double nextRankCost = prxAPI.getPlayerRankupCostWithIncreaseDirect(u);
	    String nextRankCostInString = String.valueOf(nextRankCost);
        String nextRankCostFormatted = prxAPI.formatBalance(nextRankCost);
        //other values [2]
    	List<String> allRanksCommands = new ArrayList<>();
    	List<String> rankups = new ArrayList<>();
    	String rankupMessage = main.getString(main.messagesStorage.getStringMessage("rankup"), p.getName());
    	boolean isBroadcastLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-broadcastlastrankonly");
    	boolean isMsgLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-msglastrankonly");
    	boolean isRankupMsgLastRankOnly = main.globalStorage.getBooleanData("Options.rankupmax-rankupmsglastrankonly");
    	boolean isPerRankPermission = main.globalStorage.getBooleanData("Options.per-rank-permission");
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
        //add new player data
 	   rankupMaxMap.put(p, prxAPI.getPlayerRank(u));
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
        	Double loopPlayerBalance = main.prxAPI.getPlayerMoneyOnline(p);
        	//temporarily save player data in a map
        	   RankPath rp = new RankPath(rankupMaxMap.get(p), main.prxAPI.getDefaultPath());
        	   loopCurrentRank = rankupMaxMap.get(p);
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
              	loopNextRankCost = prxAPI.getIncreasedRankupCostX(main.playerStorage.getPlayerRebirth(u) ,main.playerStorage.getPlayerPrestige(u), main.rankStorage.getRankupCost(rp));
               rankupMaxCost.put(p, rankupMaxCost.get(p) + loopNextRankCost);
               //update values
               loopNextRankCostInString = String.valueOf(loopNextRankCost);
               loopNextRankCostFormatted = prxAPI.formatBalance(loopNextRankCost);
               loopNextRankDisplay = main.getString(main.rankStorage.getRankupDisplayName(rp), p.getName());
               String loopRankupNoPermissionMessage = main.getString(main.messagesStorage.getStringMessage("rankup-nopermission"), p.getName()).replace("%nextrank%", loopNextRank).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay);
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
  	        	main.perm.addPermission(name, addpermission.replace("%player%", p.getName()).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rank%", loopCurrentRank));
  	          }
  	        }
  	        if(main.rankStorage.getDelPermissionList(rp) != null && !main.rankStorage.getDelPermissionList(rp).isEmpty()) {
  	        	for(String delpermission : main.rankStorage.getDelPermissionList(rp)) {
  	        		main.perm.delPermission(name, delpermission.replace("%player%", p.getName()).replace("%rankup%", loopNextRank).replace("%rankup_display%", loopNextRankDisplay).replace("%rank%", loopCurrentRank));
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
   				String pCMD = prxAPI.cp(cmd.replace("%player%", p.getName()).replace("%rankup%", prxAPI.getPlayerNextRank(u)), p);
   				Bukkit.getScheduler().runTask(main, () -> {
   				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), pCMD);
   				});
   			}
   			}
   			}
               //rankup things
   			   main.debug(loopNextRankCost);
               main.econ.withdrawPlayer(name, loopNextRankCost);
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
        RankPath rp = new RankPath(rankupMaxMap.get(p), main.prxAPI.getDefaultPath());
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
        	p.sendMessage(rankupMessage.replace("%rankup%", rankupMaxMap.get(p)).replace("%rankup_display%", main.getString(main.rankStorage.getDisplayName(rp), p.getName())));
        }
        
        endNextRankActionbarMessage = main.rankStorage.getActionbarMessages(rp);
        endNextRankActionbarInterval = main.rankStorage.getActionbarInterval(rp);
        if(isRankupMaxMsg) {
        	p.sendMessage(main.getString(rankupMaxMsg, p.getName()).replace("%rank%", rankupFromMap.get(p))
        			.replace("%rank_display%", prxAPI.c(prxAPI.getRankDisplay(new RankPath(rankupFromMap.get(p), rp.getPathName()))))
        			.replace("%rankup%", rankupMaxMap.get(p))
        			.replace("%rankup_display%", prxAPI.c(prxAPI.getRankDisplay(new RankPath(rankupMaxMap.get(p), rp.getPathName()))))
        			.replace("%cost%", String.valueOf(rankupMaxCost.get(p)))
        			);
        }
		prxAPI.setPlayerRank(p, rankupMaxMap.get(p));
        main.animateActionbar(p, endNextRankActionbarInterval, endNextRankActionbarMessage);
        rankupMaxPassedRanks.put(p, rankups);
	    AsyncRankupMaxEvent x = new AsyncRankupMaxEvent(p, rankupFromMap.get(p), rankupMaxMap.get(p), rankupMaxStreak.get(p), rankupMaxPassedRanks.get(p), true);
		main.getServer().getPluginManager().callEvent(x);
	    Bukkit.getScheduler().runTaskLater(main, () -> {
        rankupMaxMap.remove(p);
        rankupMaxProcess.remove(p);
        rankupMaxPassedRanks.remove(p);
        rankupFromMap.remove(p);
		}, 1);
 	  });
	}
	
}
