package me.prisonranksx.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.utils.OnlinePlayers;

public class ErrorInspector {

	private PrisonRanksX main;
	private List<String> errors;
    boolean rankSave = false;
    boolean prestigeSave = false;
    boolean rebirthSave = false;
    
	public ErrorInspector(PrisonRanksX main) {this.main = main; this.errors = new ArrayList<>();}
	
	/**
	 * 
	 * @return found errors in the format (line-1: error, line-2: solution)
	 */
	public List<String> getErrors() {
		return this.errors;
	}
	
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	
	public void validateRanks() {
		List<String> defaultPathRanks = main.rankStorage.getPathRanksMap().get("default");
		String actualFirstRank = defaultPathRanks.get(0);
		String actualLastRank = defaultPathRanks.get(defaultPathRanks.size()-1);
		if(!main.prxAPI.getDefaultRank().equals(actualFirstRank)) {
			main.getGlobalStorage().getStringMap().put("defaultrank", actualFirstRank);
			main.getGlobalStorage().getStringMap().put("lastrank", actualLastRank);
			main.getConfig().set("defaultrank", actualFirstRank);
			main.getConfig().set("lastrank", actualLastRank);
			main.getConfigManager().saveMainConfig();
			FileConfiguration rankDataConfig = main.getConfigManager().rankDataConfig;
		    Set<String> registeredPlayers = rankDataConfig.getConfigurationSection("players").getKeys(false);
		    registeredPlayers.forEach(playerUUID -> {
		    	if(!main.prxAPI.rankExists(rankDataConfig.getString("players." + playerUUID + ".rank"))) {
		    		rankDataConfig.set("players." + playerUUID + ".rank", actualFirstRank);
		    		OnlinePlayers.getPlayers().forEach(player -> {
		    			main.prxAPI.setPlayerRank(player, actualFirstRank);
		    		});
		    	}
		    });
		    main.getConfigManager().saveRankDataConfig();
		    main.performDataSave();
			main.manager.reload();
		}	
	}
	
	public void validateRanks(CommandSender sender) {
		List<String> defaultPathRanks = main.rankStorage.getPathRanksMap().get("default");
		String actualFirstRank = defaultPathRanks.get(0);
		String actualLastRank = defaultPathRanks.get(defaultPathRanks.size()-1);
		AtomicBoolean hasFoundErrors = new AtomicBoolean(false);
		boolean saveMainConfig = false;
		if(!main.prxAPI.getDefaultRank().equals(actualFirstRank)) {
			sender.sendMessage(main.prxAPI.c("&4Error &c<!> config.yml default rank doesn't match ranks.yml first rank &7[|] &frepairing..."));
			main.getGlobalStorage().getStringMap().put("defaultrank", actualFirstRank);
			main.getConfig().set("defaultrank", actualFirstRank);
			saveMainConfig = true;
			FileConfiguration rankDataConfig = main.getConfigManager().rankDataConfig;
		    Set<String> registeredPlayers = rankDataConfig.getConfigurationSection("players").getKeys(false);
		    registeredPlayers.forEach(playerUUID -> {
		    	if(!main.prxAPI.rankExists(rankDataConfig.getString("players." + playerUUID + ".rank"))) {
		    		sender.sendMessage(main.prxAPI.c("&4Error &c<!> " + playerUUID + "'s rank is invalid &7[|] &frepairing..."));
		    		rankDataConfig.set("players." + playerUUID + ".rank", actualFirstRank);
		    		OnlinePlayers.getPlayers().forEach(player -> {
		    			main.prxAPI.setPlayerRank(player, actualFirstRank);
		    		});
		    		hasFoundErrors.set(true);
		    	}
		    });
		    hasFoundErrors.set(true);
		    
		}	
		if(!main.prxAPI.getLastRank().equals(actualLastRank)) {
			sender.sendMessage(main.prxAPI.c("&4Error &c<!> config.yml last rank doesn't match ranks.yml last rank &7[|] &frepairing..."));
			main.getGlobalStorage().getStringMap().put("lastrank", actualLastRank);
			main.getConfig().set("lastrank", actualLastRank);
			saveMainConfig = true;
		}
		if(saveMainConfig) main.getConfigManager().saveMainConfig();
		List<String> ranksCollection = main.rankStorage.getRanksCollection(main.prxAPI.getDefaultPath());
		int size = ranksCollection.size();
		int i = -1;
		String actualLastRankFromCollection = ranksCollection.get(size-1);
		if(!main.rankStorage.getRankupName(RankPath.getRankPath(actualLastRankFromCollection, main.prxAPI.getDefaultPath())).equalsIgnoreCase("LASTRANK")) {
			sender.sendMessage(main.prxAPI.c("&6Warning &e<!> ranks.yml rank: " + actualLastRankFromCollection + " is the lastrank, but its nextrank is not set to 'LASTRANK'."));
		}
		for(String rank : ranksCollection) {
			i++;
			String setNextRank = main.rankStorage.getRankupName(RankPath.getRankPath(rank, main.prxAPI.getDefaultPath()));
			String actualNextRank = "LASTRANK";
			if(i+1 < size)
			actualNextRank = ranksCollection.get(i+1);
			if(!setNextRank.equals(actualNextRank) && !actualNextRank.equals("LASTRANK")) {
				sender.sendMessage(main.prxAPI.c("&6Warning &e<!> " + rank + " nextrank doesn't match the rank underneath it."));
			}
			if(!main.prxAPI.rankPathExists(RankPath.getRankPath(setNextRank, main.prxAPI.getDefaultPath())) && !actualNextRank.equals("LASTRANK")) {
				sender.sendMessage(main.prxAPI.c("&6Warning &e<!> " + rank + " nextrank '" + setNextRank + "' is invalid."));
			}
		}
		if(hasFoundErrors.get()) {
			main.getConfigManager().saveRankDataConfig();
			main.performDataSaveAsynchronously();
			main.manager.reload();
		}
	}
	
	public void validatePrestiges(CommandSender sender) {
		List<String> prestigesCollection = main.prxAPI.getPrestigesCollection();
		boolean isInfinitePrestige = main.isInfinitePrestige;
		String actualFirstPrestige = isInfinitePrestige ? "1" : prestigesCollection.get(0);
		String actualLastPrestige = isInfinitePrestige ? String.valueOf(main.infinitePrestigeSettings.getFinalPrestige()) : prestigesCollection.get(prestigesCollection.size()-1);
		AtomicBoolean hasFoundErrors = new AtomicBoolean(false);
		boolean saveMainConfig = false;
		if(!main.prxAPI.getFirstPrestige().equals(actualFirstPrestige)) {
			sender.sendMessage(main.prxAPI.c("&4Error &c<!> config.yml first prestige doesn't match prestiges.yml first prestige &7[|] &frepairing..."));
			main.getGlobalStorage().getStringMap().put("firstprestige", actualFirstPrestige);
			main.getConfig().set("firstprestige", actualFirstPrestige);
			saveMainConfig = true;
			FileConfiguration prestigeDataConfig = main.getConfigManager().prestigeDataConfig;
		    Set<String> registeredPlayers = prestigeDataConfig.getConfigurationSection("players").getKeys(false);
		    registeredPlayers.forEach(playerUUID -> {
		    	if(!main.prxAPI.prestigeExistsAny(prestigeDataConfig.getString("players." + playerUUID))) {
		    		sender.sendMessage(main.prxAPI.c("&4Error &c<!> " + playerUUID + "'s prestige is invalid &7[|] &frepairing..."));
		    		prestigeDataConfig.set("players." + playerUUID, actualFirstPrestige);
		    		OnlinePlayers.getPlayers().forEach(player -> {
		    			main.prxAPI.setPlayerPrestige(player, actualFirstPrestige);
		    		});
		    		hasFoundErrors.set(true);
		    	}
		    });
		    hasFoundErrors.set(true);
		    
		}	
		if(!main.prxAPI.getLastPrestige().equals(actualLastPrestige)) {
			sender.sendMessage(main.prxAPI.c("&4Error &c<!> config.yml last prestige doesn't match prestiges.yml last prestige &7[|] &frepairing..."));
			main.getGlobalStorage().getStringMap().put("lastprestige", actualLastPrestige);
			main.getConfig().set("lastprestige", actualLastPrestige);
			saveMainConfig = true;
		}
		if(saveMainConfig) main.getConfigManager().saveMainConfig();
		if(hasFoundErrors.get()) {
			main.getConfigManager().saveRankDataConfig();
			main.performDataSaveAsynchronously();
			main.manager.reload();
		}
	}
	
    public void validatePlayerRank(Player p) {
    	if(main.prxAPI.rankExists(main.prxAPI.getPlayerRank(p))) {
    		return;
    	}
    	main.prxAPI.setPlayerRank(p, main.prxAPI.getDefaultRank());
    }
	
	/**
	 * ! start searching for errors asynchronously.
	 */
	public void inspect() {
		if(main.isBefore1_7) return;
		errors.clear();
		Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
		try {
		Set<String> rankList = main.getConfigManager().ranksConfig.getConfigurationSection("Ranks." + main.prxAPI.getDefaultPath()).getKeys(false);
        String[] arrayList = rankList.toArray(new String[0]);
        String firstRank = arrayList[0];
        String lastRank = arrayList[rankList.size()-1];
        if(!lastRank.equals(main.prxAPI.getLastRank(main.prxAPI.getDefaultPath()))) {
        	main.getLogger().warning("Last rank on ranks.yml doesn't match lastrank on config.yml, type '/prx errors' for more info.");
        	errors.add("&4(0x0)Error: &clast rank on ranks.yml doesn't match lastrank on config.yml"
        			+ " &cthis may result into an unexpected behavior");
        	errors.add("&e(0x0)Solution: goto config.yml at the very bottom change lastrank to the one in ranks.yml remember: it's (CASE SENSITIVE)");
        }
        if(!main.rankStorage.getRankupName(new RankPath(lastRank, main.prxAPI.getDefaultPath())).equalsIgnoreCase("LASTRANK")) {
        	main.getLogger().warning("Last rank in ranks.yml 'nextrank:' field value is not LASTRANK, type '/prx errors' for more info.");
        	errors.add("&4(0x1)Error: &cthe rank at the very bottom of ranks.yml next rank is not assigned to the value: LASTRANK");
        	errors.add("&e(0x1)Solution: goto ranks.yml the lastrank in the config change nextrank to this &7nextrank: LASTRANK");
        }
        main.playerStorage.getPlayerData().keySet().forEach(player -> {
        	if(main.prxAPI.getRankDisplay(main.playerStorage.getPlayerData().get(player).getRankPath()) == null) {
        		main.getLogger().warning("Player rank data doesn't exist in ranks.yml, type '/prx errors' for more info.");
        		errors.add("&4(0x2)Error: &c" + Bukkit.getOfflinePlayer(UUID.fromString(player)).getName() + " has a rank that doesn't exist in ranks.yml"
        				+ " with UUID:" + player);
        		errors.add("&e(0x2)Solution: delete rankdata.yml while the server is offline OR edit player data inside rankdata.yml manually while the server is offline.");
        	}
        });
        main.playerStorage.getPlayerData().keySet().forEach(player -> {
        	if(!main.prxAPI.rankPathExists(main.playerStorage.getPlayerData().get(player).getRankPath())) {
        		main.getLogger().warning("Player rank data is null, type '/prx errors' for more info.");
        		errors.add("&4(0x3)Error: &c" + Bukkit.getOfflinePlayer(UUID.fromString(player)).getName() + " has a rank-with-path that doesn't exist in ranks.yml"
        				+ " with UUID:" + player);
        		errors.add("&e(0x3)Solution: delete rankdata.yml while the server is offline OR edit player data inside rankdata.yml manually while the server is offline.");
        	}
        	if(main.playerStorage.getPlayerData().get(player).getRankPath() == null) {
        		main.getLogger().warning("Player rank path is null, type '/prx errors' for more info.");
        		errors.add("&4(0x8)Error: &c" + Bukkit.getOfflinePlayer(UUID.fromString(player)).getName() + " has a rank-with-path that has invalid or null values"
        				+ " with UUID:" + player);
        		errors.add("&e(0x8)Solution: Make sure your server is offline and check on your rankdata.yml for player with a missing rank data");
        	}
        	if(main.playerStorage.getPlayerData().get(player).getRankPath() != null) {
        		if(main.playerStorage.getPlayerData().get(player).getRankPath().getPathName() == null) {
        			main.getLogger().warning("Player path name is null, type '/prx errors' for more info.");
        			errors.add("&4(0x9)Error: &c" + Bukkit.getOfflinePlayer(UUID.fromString(player)).getName() + " has a rank path name that has invalid or null values"
            				+ " with UUID:" + player);
            		errors.add("&e(0x9)Solution: Make sure your server is offline and check on your rankdata.yml for player with a missing path name");
        		}
        		if(main.playerStorage.getPlayerData().get(player).getRankPath().getPathName() == null) {
        			main.getLogger().warning("Player rank name is null, type '/prx errors' for more info.");
        			errors.add("&4(0x10)Error: &c" + Bukkit.getOfflinePlayer(UUID.fromString(player)).getName() + " has a rank name that has invalid or null values"
            				+ " with UUID:" + player);
            		errors.add("&e(0x10)Solution: Make sure your server is offline and check on your rankdata.yml for player with a missing rank name");
        		}
        	}
        });
        if(!firstRank.equals(main.prxAPI.getDefaultRank())) {
        	main.getLogger().warning("first rank on ranks.yml doesn't match defaultrank on config.yml, type '/prx errors' for more info.");
        	errors.add("&4(0x4)Error: &cfirst rank on ranks.yml doesn't match defaultrank on config.yml"
        			+ " &cthis may result into an unexpected behavior");
        	errors.add("&e(0x4)Solution: goto config.yml at the very bottom change defaultrank to the one in ranks.yml while the server is &loffline&e remember: it's (CASE SENSITIVE)");
        }
        rankList.forEach(rank -> {
        	if(main.getConfigManager().ranksConfig.isString("Ranks." + main.prxAPI.getDefaultPath() + "." + rank + ".executecmds")) {
        		main.getLogger().warning("Rank " + rank + " executecmds uses a wrong format! please change to the list format instead, type '/prx errors' for more info.");
        		errors.add("&4(0x5)Error: " + rank + " uses the string format '' instead of the list format - string");
        		errors.add("&e(0x5)Solution: change &nexecutecmds: 'example'&e to the following format:");
        		errors.add("&eexecutecmds:");
        		errors.add("&e- 'example'");
        	}
        	if(main.getConfigManager().ranksConfig.isString("Ranks." + main.prxAPI.getDefaultPath() + "." + rank + ".broadcast")) {
        		main.getLogger().warning("Rank " + rank + " broadcast uses a wrong format! please change to the list format instead, type '/prx errors' for more info.");
        		errors.add("&4(0x6)Error: " + rank + " uses the string format '' instead of the list format - string");
        		errors.add("&e(0x6)Solution: change &nbroadcast: 'example'&e to the following format:");
        		errors.add("&ebroadcast:");
        		errors.add("&e- 'example'");
        	}
        	if(main.getConfigManager().ranksConfig.isString("Ranks." + main.prxAPI.getDefaultPath() + "." + rank + ".msg")) {
        		main.getLogger().warning("Rank " + rank + " msg uses a wrong format! please change to the list format instead, type '/prx errors' for more info.");
        		errors.add("&4(0x7)Error: " + rank + " uses the string format '' instead of the list format - string");
        		errors.add("&e(0x7)Solution: change &nmsg: 'example'&e to the following format:");
        		errors.add("&emsg:");
        		errors.add("&e- 'example'");
        	}
        });
        main.playerStorage.getPlayerData().entrySet().forEach(entry -> {
        	if(main.prxAPI.getRankDisplay(entry.getValue().getRankPath()) == null) {
        		main.getLogger().warning("Detected invalid old data for: " + entry.getValue().getName() + ", fixing...");
        		main.prxAPI.setPlayerRankPath(entry.getValue().getUUID(), new RankPath(main.prxAPI.getDefaultRank(), main.prxAPI.getDefaultPath()));
        	}
        });
       if(main.isMySql()) {
    	   return;
       }
       main.getConfigManager().rankDataConfig.getConfigurationSection("players").getKeys(false).forEach(player -> {
    	   String rank = main.getConfigManager().rankDataConfig.getString("players." + player + ".rank");
    	   String path = main.getConfigManager().rankDataConfig.getString("players." + player + ".path");
    	   if(main.rankStorage.getRankupName(new RankPath(rank, path)) == null) {
    		   main.getLogger().warning("Player rank doesn't have a rankup name. Repairing...");
    		   main.getConfigManager().rankDataConfig.set("players." + player + ".rank", main.prxAPI.getDefaultRank());
    		   rankSave = true;
    		   main.getLogger().warning("Please restart your server to avoid future problems.");
    	   }
       });
       main.getConfigManager().prestigeDataConfig.getConfigurationSection("players").getKeys(false).forEach(player -> {
    	   String prestige = main.getConfigManager().prestigeDataConfig.getString("players." + player);
    	   if(main.prestigeStorage.getNextPrestigeName(prestige) == null) {
    		   main.getLogger().warning("Player prestige doesn't have a next prestige name. Repairing...");
    		   main.getConfigManager().prestigeDataConfig.set("players." + player, main.prxAPI.getFirstPrestige());
    		   prestigeSave = true;
    		   main.getLogger().warning("Please restart your server to avoid future problems.");
    	   }
       });
       main.getConfigManager().rebirthDataConfig.getConfigurationSection("players").getKeys(false).forEach(player -> {
    	   String rebirth = main.getConfigManager().rebirthDataConfig.getString("players." + player);
    	   if(main.rebirthStorage.getNextRebirthName(rebirth) == null) {
    		   main.getLogger().warning("Player rebirth doesn't have a next rebirth name. Repairing...");
    		   main.getConfigManager().rebirthDataConfig.set("players." + player, main.prxAPI.getFirstRebirth());
    		   rebirthSave = true;
    		   main.getLogger().warning("Please restart your server to avoid future problems.");
    	   }
       });
       if(rankSave) {
    	   rankSave = false;
    	   main.getConfigManager().saveRankDataConfig();
       }
       if(prestigeSave) {
    	   prestigeSave = false;
    	   main.getConfigManager().savePrestigeDataConfig();
       }
       if(rebirthSave) {
    	   rebirthSave = false;
    	   main.getConfigManager().saveRebirthDataConfig();
       }
		} catch (Exception err) {
			
		}
		});
	}
	
}
