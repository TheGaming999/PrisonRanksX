package me.prisonranksx.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.RankPath;

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
	
	/**
	 * start searching for errors.
	 */
	public void inspect() {
		errors.clear();
		Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
		try {
		Set<String> rankList = main.configManager.ranksConfig.getConfigurationSection("Ranks." + main.prxAPI.getDefaultPath()).getKeys(false);
        String[] arrayList = rankList.toArray(new String[0]);
        String lastRank = arrayList[rankList.size()-1];
        if(!lastRank.equals(main.prxAPI.getLastRank())) {
        	main.getLogger().warning("Last rank on ranks.yml doesn't match lastrank on config.yml, type '/prx errors' for more info.");
        	errors.add("&4(0x0)Error: &clast rank on ranks.yml doesn't match lastrank on config.yml"
        			+ " &cthis may result into an unexpected behavior");
        	errors.add("&e(0x0)Solution: goto config.yml at the very bottom change lastrank to the one in ranks.yml remember: it's (CASE SENSITIVE)");
        }
        if(!main.rankStorage.getRankupName(new RankPath(lastRank, main.prxAPI.getDefaultPath())).equalsIgnoreCase("LASTRANK")) {
        	main.getLogger().warning("Last rank in ranks.yml 'nextrank:' field value is not LASTRANK, type '/prx errors' for more info.");
        	errors.add("&4(0x1)Error: &cthe rank at the very bottom of ranks.yml next rank is not assigned to the value: LASTRANK");
        	errors.add("&e(0x2)Solution: goto ranks.yml the lastrank in the config change nextrank to this &7nextrank: LASTRANK");
        }
        main.playerStorage.getPlayerData().keySet().forEach(player -> {
        	if(main.prxAPI.getRankDisplay(main.playerStorage.getPlayerData().get(player).getRankPath()) == null) {
        		main.getLogger().warning("Player rank data doesn't exist in ranks.yml, type '/prx errors' for more info.");
        		errors.add("&4(0x2)Error: &c" + Bukkit.getOfflinePlayer(UUID.fromString(player)).getName() + " has a rank that doesn't exist in ranks.yml"
        				+ " with UUID:" + player);
        		errors.add("&e(0x2)Solution: delete rankdata.yml while the server is offline OR edit player data inside rankdata.yml manually while the server is offline.");
        	}
        });
       if(main.isMySql()) {
    	   return;
       }
       main.configManager.rankDataConfig.getConfigurationSection("players").getKeys(false).forEach(player -> {
    	   String rank = main.configManager.rankDataConfig.getString("players." + player + ".rank");
    	   String path = main.configManager.rankDataConfig.getString("players." + player + ".path");
    	   if(main.rankStorage.getRankupName(new RankPath(rank, path)) == null) {
    		   main.getLogger().warning("Player rank doesn't have a rankup name. Repairing...");
    		   main.configManager.rankDataConfig.set("players." + player + ".rank", main.prxAPI.getDefaultRank());
    		   rankSave = true;
    		   main.getLogger().warning("Please restart your server to avoid future problems.");
    	   }
       });
       main.configManager.prestigeDataConfig.getConfigurationSection("players").getKeys(false).forEach(player -> {
    	   String prestige = main.configManager.prestigeDataConfig.getString("players." + player);
    	   if(main.prestigeStorage.getNextPrestigeName(prestige) == null) {
    		   main.getLogger().warning("Player prestige doesn't have a next prestige name. Repairing...");
    		   main.configManager.prestigeDataConfig.set("players." + player, main.prxAPI.getFirstPrestige());
    		   prestigeSave = true;
    		   main.getLogger().warning("Please restart your server to avoid future problems.");
    	   }
       });
       main.configManager.rebirthDataConfig.getConfigurationSection("players").getKeys(false).forEach(player -> {
    	   String rebirth = main.configManager.rebirthDataConfig.getString("players." + player);
    	   if(main.rebirthStorage.getNextRebirthName(rebirth) == null) {
    		   main.getLogger().warning("Player rebirth doesn't have a next rebirth name. Repairing...");
    		   main.configManager.rebirthDataConfig.set("players." + player, main.prxAPI.getFirstRebirth());
    		   rebirthSave = true;
    		   main.getLogger().warning("Please restart your server to avoid future problems.");
    	   }
       });
       if(rankSave) {
    	   rankSave = false;
    	   main.configManager.saveRankDataConfig();
       }
       if(prestigeSave) {
    	   prestigeSave = false;
    	   main.configManager.savePrestigeDataConfig();
       }
       if(rebirthSave) {
    	   rebirthSave = false;
    	   main.configManager.saveRebirthDataConfig();
       }
		} catch (Exception err) {
			
		}
		});
	}
	
}
