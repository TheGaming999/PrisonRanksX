package me.prisonranksx.api;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import io.samdev.actionutil.ActionUtil;
import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.LevelType;
import me.prisonranksx.data.PercentageState;
import me.prisonranksx.data.PrestigeRandomCommands;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.data.RankRandomCommands;
import me.prisonranksx.events.RankupAction;
import me.prisonranksx.events.XPrestigeEvent;
import me.prisonranksx.events.XRankupEvent;
import me.prisonranksx.utils.MCProgressBar;
import me.prisonranksx.utils.NumberAPI;
import me.prisonranksx.utils.ProgressBarBuilder;

public class PRXAPI {
	public NumberAPI numberAPI;
	public MCProgressBar rankupProgressBar;
	public MCProgressBar rankupProgressBarExtended;
	public MCProgressBar globalProgressBar_rank;
	public MCProgressBar globalProgressBarExtended_rank;
	public MCProgressBar globalProgressBar_prestige;
	public MCProgressBar globalProgressBarExtended_prestige;
	public MCProgressBar globalProgressBar_rebirth;
	public MCProgressBar globalProgressBarExtended_rebirth;
	public FileConfiguration rankDataConfig;
	public FileConfiguration prestigeDataConfig;
	public FileConfiguration rebirthDataConfig;
	public FileConfiguration customConfig;
	public FileConfiguration originalConfig;
	public FileConfiguration ranksConfig;
	public FileConfiguration prestigesConfig;
	public FileConfiguration rebirthsConfig;
	public FileConfiguration commandsConfig;
	public FileConfiguration messagesConfig;
    public PrisonRanksX main = null;
	public  List<Player> autoRankupPlayers;
	public List<Player> taskedPlayers;
	public Set<String> allRankAddPermissions, allRankDelPermissions, allPrestigeAddPermissions
	, allPrestigeDelPermissions, allRebirthAddPermissions, allRebirthDelPermissions;
	
	public void loadMain() {
		if(main == null) {
		  try {
		      main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
		  } catch (java.lang.ClassCastException err) {
			  Bukkit.getLogger().info("Main class is already casted");
		  }
		}
	}
	public PRXAPI() {}
	
	public void setup() {
        loadMain();
        rankupProgressBar = new MCProgressBar();
        rankupProgressBarExtended = new MCProgressBar();
        globalProgressBar_rank = new MCProgressBar();
        globalProgressBarExtended_rank = new MCProgressBar();
        globalProgressBar_prestige = new MCProgressBar();
        globalProgressBarExtended_prestige = new MCProgressBar();
        globalProgressBar_rebirth = new MCProgressBar();
        globalProgressBarExtended_rebirth = new MCProgressBar();
        messagesConfig = main.configManager.messagesConfig;
		rankDataConfig = main.configManager.rankDataConfig;
		prestigeDataConfig = main.configManager.prestigeDataConfig;
		customConfig = main.configManager.rankDataConfig;
		originalConfig = main.getConfig();
		ranksConfig = main.configManager.ranksConfig;
		prestigesConfig = main.configManager.prestigesConfig;
		rebirthDataConfig = main.configManager.rebirthDataConfig;
		rebirthsConfig = main.configManager.rebirthsConfig;
		commandsConfig = main.configManager.commandsConfig;
		numberAPI = new NumberAPI();
		autoRankupPlayers = new ArrayList<>();
		taskedPlayers = new ArrayList<>();
		allRankAddPermissions = new LinkedHashSet<String>();
		allRankDelPermissions = new LinkedHashSet<String>();
		allPrestigeAddPermissions = new LinkedHashSet<String>();
		allPrestigeDelPermissions = new LinkedHashSet<String>();
		allRebirthAddPermissions = new LinkedHashSet<String>();
		allRebirthDelPermissions = new LinkedHashSet<String>();
	}
	
	public void loadProgressBars() {
		// old progress bars
		rankupProgressBar.setMaxValue(10);
		rankupProgressBar.setStyle(c(main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-style")));
		rankupProgressBar.setCompletedPrefix(main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-filled"));
		rankupProgressBar.setLeftPrefix(main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-needed"));
		rankupProgressBar.build();
		rankupProgressBar.setValue(0);
		rankupProgressBarExtended.setMaxValue(20);
		rankupProgressBarExtended.setStyle(c(main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-style")));
		rankupProgressBarExtended.setCompletedPrefix(main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-filled"));
		rankupProgressBarExtended.setLeftPrefix(main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-needed"));
		rankupProgressBarExtended.build();
		rankupProgressBarExtended.setValue(0);
		// new progress bars
		// non-extended progress bars setup
		globalProgressBar_rank.setStyle(c(main.globalStorage.getStringData("PlaceholderAPI.next-progress-style.rankup")));
		globalProgressBar_prestige.setStyle(c(main.globalStorage.getStringData("PlaceholderAPI.next-progress-style.prestige")));
		globalProgressBar_rebirth.setStyle(c(main.globalStorage.getStringData("PlaceholderAPI.next-progress-style.rebirth")));
		globalProgressBar_rank.setCompletedPrefix(main.globalStorage.getStringData("PlaceholderAPI.next-progress-filled.rankup"));
		globalProgressBar_prestige.setCompletedPrefix(main.globalStorage.getStringData("PlaceholderAPI.next-progress-filled.prestige"));
		globalProgressBar_rebirth.setCompletedPrefix(main.globalStorage.getStringData("PlaceholderAPI.next-progress-filled.rebirth"));
		globalProgressBar_rank.setLeftPrefix(main.globalStorage.getStringData("PlaceholderAPI.next-progress-needed.rankup"));
		globalProgressBar_prestige.setLeftPrefix(main.globalStorage.getStringData("PlaceholderAPI.next-progress-needed.prestige"));
		globalProgressBar_rebirth.setLeftPrefix(main.globalStorage.getStringData("PlaceholderAPI.next-progress-needed.rebirth"));
		globalProgressBar_rank.setMaxValue(10);
		globalProgressBar_prestige.setMaxValue(10);
		globalProgressBar_rebirth.setMaxValue(10);
		globalProgressBar_rank.build();
		globalProgressBar_prestige.build();
		globalProgressBar_rebirth.build();
		globalProgressBar_rank.setValue(0);
		globalProgressBar_prestige.setValue(0);
		globalProgressBar_rebirth.setValue(0);
		// extended progress bars setup
		globalProgressBarExtended_rank.setStyle(c(main.globalStorage.getStringData("PlaceholderAPI.next-progress-style.rankup")));
		globalProgressBarExtended_prestige.setStyle(c(main.globalStorage.getStringData("PlaceholderAPI.next-progress-style.prestige")));
		globalProgressBarExtended_rebirth.setStyle(c(main.globalStorage.getStringData("PlaceholderAPI.next-progress-style.rebirth")));
		globalProgressBarExtended_rank.setCompletedPrefix(main.globalStorage.getStringData("PlaceholderAPI.next-progress-filled.rankup"));
		globalProgressBarExtended_prestige.setCompletedPrefix(main.globalStorage.getStringData("PlaceholderAPI.next-progress-filled.prestige"));
		globalProgressBarExtended_rebirth.setCompletedPrefix(main.globalStorage.getStringData("PlaceholderAPI.next-progress-filled.rebirth"));
		globalProgressBarExtended_rank.setLeftPrefix(main.globalStorage.getStringData("PlaceholderAPI.next-progress-needed.rankup"));
		globalProgressBarExtended_prestige.setLeftPrefix(main.globalStorage.getStringData("PlaceholderAPI.next-progress-needed.prestige"));
		globalProgressBarExtended_rebirth.setLeftPrefix(main.globalStorage.getStringData("PlaceholderAPI.next-progress-needed.rebirth"));
		globalProgressBarExtended_rank.setMaxValue(20);
		globalProgressBarExtended_prestige.setMaxValue(20);
		globalProgressBarExtended_rebirth.setMaxValue(20);
		globalProgressBarExtended_rank.build();
		globalProgressBarExtended_prestige.build();
		globalProgressBarExtended_rebirth.build();
		globalProgressBarExtended_rank.setValue(0);
		globalProgressBarExtended_prestige.setValue(0);
		globalProgressBarExtended_rebirth.setValue(0);
	}
	
	public void loadPermissions() {
		// permissions cache {
		main.rankStorage.getEntireData().values().forEach(val -> {
			if(val.getAddPermissionList() != null) {
			val.getAddPermissionList().forEach(addPerm -> {
				allRankAddPermissions.add(addPerm);
			});
			}
			if(val.getDelPermissionList() != null) {
			val.getDelPermissionList().forEach(delPerm -> {
				allRankDelPermissions.add(delPerm);
			});
			}
		});
		main.prestigeStorage.getPrestigeData().values().forEach(val -> {
			if(val.getAddPermissionList() != null) {
			val.getAddPermissionList().forEach(addPerm -> {
				allPrestigeAddPermissions.add(addPerm);
			});
			}
			if(val.getDelPermissionList() != null) {
			val.getDelPermissionList().forEach(delPerm -> {
				allPrestigeDelPermissions.add(delPerm);
			});
			}
		});
		main.rebirthStorage.getRebirthData().values().forEach(val -> {
			if(val.getAddPermissionList() != null) {
			val.getAddPermissionList().forEach(addPerm -> {
				allRebirthAddPermissions.add(addPerm);
			});
			}
			if(val.getDelPermissionList() != null) {
			val.getDelPermissionList().forEach(delPerm -> {
				allRebirthDelPermissions.add(delPerm);
			});
			}
		});
		// }
	}
	
	public void saveConfigs() {
		main.configManager.saveConfigs();
	}
	
	@Deprecated
	 public  void saveCustomYml(FileConfiguration ymlConfig, File ymlFile) {
		    try {
		      ymlConfig.save(ymlFile);
		   } catch (IOException e) {
		      e.printStackTrace();
		   }
	}
	public String formatBalance(double y)
    {
     return main.formatBalance(y);
    }
	
	@Deprecated
	public boolean uuidOption() {
		return main.globalStorage.getBooleanData("Options.USE-UUID");
	}
	public PrisonRanksX getPluginMainClass() {
		return main;
	}
	
	public boolean rebirthExists(String rebirth) {
		return main.rebirthStorage.getRebirthData().get(rebirth) != null;
	}
	
	public boolean prestigeExists(String prestige) {
		return main.prestigeStorage.getPrestigeData().get(prestige) != null;
	}
	
	public boolean rankExists(String rank) {
		return main.rankStorage.getEntireData().get(rank + "#~#default") != null;
	}
	
	public boolean rankExists(String rank, String path) {
		return main.rankStorage.getEntireData().get(rank + "#~#" + path) != null;
	}
	
	public boolean rankPathExists(RankPath rankPath) {
		return main.rankStorage.getEntireData().get(rankPath.get()) != null;
	}
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return String player rank name
	    */
	public String getPlayerRank(OfflinePlayer offlinePlayer) {
		return main.playerStorage.getPlayerRank(offlinePlayer);
	}
	
	/**
	 * 
	 * @param offlinePlayer
	 * @return RankPath player rank path which gives you access to both the rank name and the path name
	 */
	public RankPath getPlayerRankPath(OfflinePlayer offlinePlayer) {
		return main.playerStorage.getPlayerRankPath(offlinePlayer);
	}
	
	/**
	 * 
	 * @param rankPath
	 * @return double rank cost
	 */
	public double getRankCostMethodII(RankPath rankPath) {
		return main.rankStorage.getCost(rankPath);
	}
	
	public double getRebirthCost(String rebirthName) {
		return main.rebirthStorage.getCost(rebirthName);
	}
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return String player rank display name/prefix
	    *  If the player is not registered in the config file then he will be automatically registered using this method
	    */
	public String getPlayerRankDisplay(OfflinePlayer offlinePlayer) {
		if(main.rankStorage.getDisplayName(getPlayerRankPath(offlinePlayer)) == null) {
			setPlayerRank(offlinePlayer, main.globalStorage.getStringData("defaultrank"));
		}
		return String.valueOf(main.rankStorage.getDisplayName(getPlayerRankPath(offlinePlayer)));
	}
	
	public String getPlayerRebirthDisplay(OfflinePlayer offlinePlayer) {
		return String.valueOf(main.rebirthStorage.getDisplayName(getPlayerRebirth(offlinePlayer)));
	}
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return double player current rank's cost
	    */
	public double getPlayerRankCost(OfflinePlayer offlinePlayer) {
		return (main.rankStorage.getCost(getPlayerRankPath(offlinePlayer)));
	}
	
	public double getPlayerRebirthCost(OfflinePlayer offlinePlayer) {
		return (main.rebirthStorage.getCost(getPlayerRebirth(offlinePlayer)));
	}
	/**
	 * 
	 * @param player
	 * @return boolean also checks if Options.autorankup is true or false so this one time check
	 */
	public boolean isAutoRankupEnabled(Player player) {
		if(main.globalStorage.getBooleanData("Options.autorankup") == false) {
			return false;
		}
			if(autoRankupPlayers.contains(player)) {
				return true;
			} else {
				return false;
			}
	}
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return String player current rank's cost formatted (1k,10m,1.5b,etc..).
	    */
	public String getPlayerRankCostFormatted(OfflinePlayer offlinePlayer) {
		return formatBalance(main.rankStorage.getCost(getPlayerRankPath(offlinePlayer)));
	}
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return String player rank up name | returns null if the player is at the latest rank
	    */
	public String getPlayerNextRank(OfflinePlayer offlinePlayer) {
		String nextRank = main.rankStorage.getRankupName(getPlayerRankPath(offlinePlayer));
		if(nextRank.equalsIgnoreCase("lastrank")) {
			return null;
		} else {
			return nextRank;
		}
	}
	
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return String player rank up name | doesn't return null if the player is at the latest rank
	    */
	public String getPlayerNextRankN(OfflinePlayer offlinePlayer) {
		String nextRank = main.rankStorage.getRankupName(getPlayerRankPath(offlinePlayer));
		return nextRank;
	}
	
	public String getPlayerRebirth(OfflinePlayer offlinePlayer) {
		String rebirthName = main.playerStorage.getPlayerRebirth(offlinePlayer);
		return rebirthName;
	}
	
    public String getRankupProgressStyle() {
    	String s = getPluginMainClass().getStringWithoutPAPI(main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-style"));
		return s;
    }
    public String getRankupProgressFilled() {
    	String f = getPluginMainClass().getStringWithoutPAPI(main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-filled"));
    	return f;
    }
    public String getRankupProgressNeeded() {
    	String n = getPluginMainClass().getStringWithoutPAPI(main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-needed"));
    	return n;
    }
    
    /**
     * @return PercentageState which contains the level type and the percentage depending on your state (max=100).
     */
    public PercentageState getPlayerNextPercentage(OfflinePlayer offlinePlayer) {
    	Player p = (Player)offlinePlayer;
    	PercentageState ps = new PercentageState();
    	if(!this.isLastRank(p)) { // is not last rank
       		double percent = (getPlayerMoney(p) / getPlayerRankupCostWithIncreaseDirect(p)) * 100;
       		if(percent >= 100) {
       			ps.setPercentage("100");
       			ps.setLevelType(LevelType.RANK);
       			return ps;
       		}
       		String intConverted = numberAPI.toFakeInteger(percent);
       		ps.setPercentage(intConverted);
       		ps.setLevelType(LevelType.RANK);
       		return ps;
    	} else { // is last rank
    		if(this.hasNextPrestige(p)) {
    			double percent = (getPlayerMoney(p) / getPlayerNextPrestigeCost(p)) * 100;
           		if(percent >= 100) {
           			ps.setPercentage("100");
           			ps.setLevelType(LevelType.PRESTIGE);
           			return ps;
           		}
    			String intConverted = numberAPI.toFakeInteger(percent);
    			ps.setPercentage(intConverted);
    			ps.setLevelType(LevelType.PRESTIGE);
    			return ps;
    		} else {
    			if(this.hasNextRebirth(p)) {
        			double percent = (getPlayerMoney(p) / getPlayerNextRebirthCost(p)) * 100;
               		if(percent >= 100) {
               			ps.setPercentage("100");
               			ps.setLevelType(LevelType.REBIRTH);
               			return ps;
               		}
        			String intConverted = numberAPI.toFakeInteger(percent);
        			ps.setPercentage(intConverted);
        			ps.setLevelType(LevelType.REBIRTH);
        			return ps;
    			} else {
    				ps.setPercentage("100");
    				ps.setLevelType(LevelType.UNKNOWN);
    			    return ps;
    			}
    		}
    	}
    }
    
    /**
     * @return integer next rank,prestige or rebirth percentage depending on your state (can go beyond 100).
     */
    public String getPlayerNextPercentageNoLimit(OfflinePlayer offlinePlayer) {
    	Player p = (Player)offlinePlayer;
    	if(!this.isLastRank(p)) { // is not last rank
       		double percent = (getPlayerMoney(p) / getPlayerRankupCostWithIncreaseDirect(p)) * 100;
       		String intConverted = numberAPI.toFakeInteger(percent);
       		return intConverted;
    	} else { // is last rank
    		if(this.hasNextPrestige(p)) {
    			double percent = (getPlayerMoney(p) / getPlayerNextPrestigeCost(p)) * 100;
    			String intConverted = numberAPI.toFakeInteger(percent);
    			return intConverted;
    		} else {
    			if(this.hasNextRebirth(p)) {
        			double percent = (getPlayerMoney(p) / getPlayerNextRebirthCost(p)) * 100;
        			String intConverted = numberAPI.toFakeInteger(percent);
        			return intConverted;
    			} else {
    			    return "100";
    			}
    		}
    	}
    }
    
    /**
     * @return double next rank,prestige or rebirth percentage depending on your state (max=100.0).
     */
    public String getPlayerNextPercentageDecimal(OfflinePlayer offlinePlayer) {
    	Player p = (Player)offlinePlayer;
    	if(!this.isLastRank(p)) { // is not last rank
       		double percent = (getPlayerMoney(p) / getPlayerRankupCostWithIncreaseDirect(p)) * 100;
       		if(percent >= 100) {
       			return "100.0";
       		}
       		String intConverted = String.valueOf(numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2));
       		return intConverted;
    	} else { // is last rank
    		if(this.hasNextPrestige(p)) {
    			double percent = (getPlayerMoney(p) / getPlayerNextPrestigeCost(p)) * 100;
           		if(percent >= 100) {
           			return "100.0";
           		}
    			String intConverted = String.valueOf(numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2));
    			return intConverted;
    		} else {
    			if(this.hasNextRebirth(p)) {
        			double percent = (getPlayerMoney(p) / getPlayerNextRebirthCost(p)) * 100;
               		if(percent >= 100) {
               			return "100.0";
               		}
        			String intConverted = String.valueOf(numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2));
        			return intConverted;
    			} else {
    			    return "100.0";
    			}
    		}
    	}
    }
    
    /**
     * @return double next rank,prestige or rebirth percentage depending on your state (can go beyond 100.0).
     */
    public String getPlayerNextPercentageDecimalNoLimit(OfflinePlayer offlinePlayer) {
    	Player p = (Player)offlinePlayer;
    	if(!this.isLastRank(p)) { // is not last rank
       		double percent = (getPlayerMoney(p) / getPlayerRankupCostWithIncreaseDirect(p)) * 100;
       		String intConverted = String.valueOf(numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2));
       		return intConverted;
    	} else { // is last rank
    		if(this.hasNextPrestige(p)) {
    			double percent = (getPlayerMoney(p) / getPlayerNextPrestigeCost(p)) * 100;
    			String intConverted = String.valueOf(numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2));
    			return intConverted;
    		} else {
    			if(this.hasNextRebirth(p)) {
        			double percent = (getPlayerMoney(p) / getPlayerNextRebirthCost(p)) * 100;
        			String intConverted = String.valueOf(numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2));
        			return intConverted;
    			} else {
    			    return "100.0";
    			}
    		}
    	}
    }
    
    public String getPlayerNextProgress(OfflinePlayer offlinePlayer) {
    	OfflinePlayer p = offlinePlayer;
    	if(getPlayerNextPercentage(p).getLevelType() == LevelType.RANK ) {
    		if(main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-isrankup-enabled")
    			&& getPlayerNextPercentage(p).getPercentage().equals("100")) {
    		return main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-isrankup");
    		} else {
    			int converted = Integer.valueOf(getPlayerNextPercentage(p).getPercentage()) / 10;
    			globalProgressBar_rank.setValue(converted);
    			return globalProgressBar_rank.getProgressBar();
    		}
    	}
    	if(getPlayerNextPercentage(p).getLevelType() == LevelType.PRESTIGE) {
    		if(main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-isprestige-enabled")
        			&& getPlayerNextPercentage(p).getPercentage().equals("100")) {
        		return main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-isprestige");
        		} else {
        			int converted = Integer.valueOf(getPlayerNextPercentage(p).getPercentage()) / 10;
        			globalProgressBar_prestige.setValue(converted);
        			return globalProgressBar_prestige.getProgressBar();
        		}
    	}
    	if(getPlayerNextPercentage(p).getLevelType() == LevelType.REBIRTH) {
    		if(main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-isrebirth-enabled")
        			&& getPlayerNextPercentage(p).getPercentage().equals("100")) {
        		return main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-isrebirth");
        		} else {
        			int converted = Integer.valueOf(getPlayerNextPercentage(p).getPercentage()) / 10;
        			globalProgressBar_rebirth.setValue(converted);
        			return globalProgressBar_rebirth.getProgressBar();
        		}
    	}
    	if(getPlayerNextPercentage(p).getLevelType() == LevelType.UNKNOWN && main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-islast-enabled")
    			&& getPlayerNextPercentage(p).getPercentage().equals("100")) {
    		return main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-islast");
    	}
		return globalProgressBar_rank.getPlainProgressBar();
    }
    
    public String getPlayerNextProgressExtended(OfflinePlayer offlinePlayer) {
    	OfflinePlayer p = offlinePlayer;
    	if(getPlayerNextPercentage(p).getLevelType() == LevelType.RANK ) {
    		if(main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-isrankup-enabled")
    			&& getPlayerNextPercentage(p).getPercentage().equals("100")) {
    		return main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-isrankup");
    		} else {
    			int converted = Integer.valueOf(getPlayerNextPercentage(p).getPercentage()) / 5;
    			globalProgressBarExtended_rank.setValue(converted);
    			return globalProgressBarExtended_rank.getProgressBar();
    		}
    	}
    	if(getPlayerNextPercentage(p).getLevelType() == LevelType.PRESTIGE) {
    		if(main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-isprestige-enabled")
        			&& getPlayerNextPercentage(p).getPercentage().equals("100")) {
        		return main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-isprestige");
        		} else {
        			int converted = Integer.valueOf(getPlayerNextPercentage(p).getPercentage()) / 5;
        			globalProgressBarExtended_prestige.setValue(converted);
        			return globalProgressBarExtended_prestige.getProgressBar();
        		}
    	}
    	if(getPlayerNextPercentage(p).getLevelType() == LevelType.REBIRTH) {
    		if(main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-isrebirth-enabled")
        			&& getPlayerNextPercentage(p).getPercentage().equals("100")) {
        		return main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-isrebirth");
        		} else {
        			int converted = Integer.valueOf(getPlayerNextPercentage(p).getPercentage()) / 5;
        			globalProgressBarExtended_rebirth.setValue(converted);
        			return globalProgressBarExtended_rebirth.getProgressBar();
        		}
    	}
    	if(getPlayerNextPercentage(p).getLevelType() == LevelType.UNKNOWN && main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-islast-enabled")
    			&& getPlayerNextPercentage(p).getPercentage().equals("100")) {
    		return main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-islast");
    	}
		return globalProgressBar_rank.getPlainProgressBar();
    }
    
    public String getPlayerRankupProgressBar(OfflinePlayer offlinePlayer) {
    	OfflinePlayer p = offlinePlayer;
    	if(isLastRank(p)) {
    		return main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-lastrank");
    	}
    	int convertedPercentage = Integer.valueOf(getPlayerRankupPercentageDirect(p)) / 10;
    	if(convertedPercentage >= 10 && main.globalStorage.getBooleanData("PlaceholderAPI.rankup-progress-full-enabled")) {
    		return main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-full");
    	}
    	rankupProgressBar.setValue(convertedPercentage);
    	return rankupProgressBar.getProgressBar();
    }
    
    public String getPlayerRankupProgressBarExtended(OfflinePlayer offlinePlayer) {
    	OfflinePlayer p = offlinePlayer;
    	if(isLastRank(p)) {
    		return main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-lastrank");
    	}
    	int convertedPercentage = Integer.valueOf(getPlayerRankupPercentageDirect(p)) / 5;
    	if(convertedPercentage >= 5 && main.globalStorage.getBooleanData("PlaceholderAPI.rankup-progress-full-enabled")) {
    		return main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-full");
    	}
    	rankupProgressBarExtended.setValue(convertedPercentage);
    	return rankupProgressBarExtended.getProgressBar();
    }
    
    /**
     * 
     * @param offlinePlayer
     * @return String player rank up progress bar
     */
    @Deprecated
    public String getPlayerRankupProgress(OfflinePlayer offlinePlayer) {
    	if(getRankupProgressStyle() == null && getRankupProgressFilled() == null && getRankupProgressNeeded() == null) {
    		return " ";
    	}
		String s = getRankupProgressStyle();
		String f = getRankupProgressFilled();
		String n = getRankupProgressNeeded();
		Integer numb = Integer.valueOf(getPlayerRankupPercentageDirect(offlinePlayer));
		if(numberAPI.isBetween(numb, 0, 9)) {
			return n + s + s + s + s + s + s + s + s + s + s;
		}
		else if (numberAPI.isBetween(numb, 10, 19)) {
			return f + s + n + s + s + s + s + s + s + s + s + s;
		}
		else if (numberAPI.isBetween(numb, 20, 29)) {
			return f + s + s + n + s + s + s + s + s + s + s + s;
		}
		else if (numberAPI.isBetween(numb, 30, 39)) {
			return f + s + s + s + n + s + s + s + s + s + s + s;
		}
		else if (numberAPI.isBetween(numb, 40, 49)) {
			return f + s + s + s + s + n + s + s + s + s + s + s;
		}
		else if (numberAPI.isBetween(numb, 50, 59)) {
			return f + s + s + s + s + s + n + s + s + s + s + s;
		}
		else if (numberAPI.isBetween(numb, 60, 69)) {
			return f + s + s + s + s + s + s + n + s + s + s + s;
		}
		else if (numberAPI.isBetween(numb, 70, 79)) {
			return f + s + s + s + s + s + s + s + n + s + s + s;
		}
		else if (numberAPI.isBetween(numb, 80, 89)) {
			return f + s + s + s + s + s + s + s + s + n + s + s;
		}
		else if (numberAPI.isBetween(numb, 90, 99)) {
			return f + s + s + s + s + s + s + s + s + s + n + s;
		}
		else if (numberAPI.isBetween(numb, 100, 100)) {
			if((main.globalStorage.getBooleanData("PlaceholderAPI.rankup-progress-full-enabled"))) {
				String full = main.getString(main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-full"), offlinePlayer.getName());
				return full;
			} else {
			return f + s + s + s + s + s + s + s + s + s + s;
			}
		}
    	return "n/a";
    }
    /**
     * 
     * @param offlinePlayer
     * @return String player rank up progress bar with 20 character instead of 10
     */
    @Deprecated
	public  String getPlayerRankupProgressDoubled(OfflinePlayer offlinePlayer) {
		String s = getRankupProgressStyle();
		String f = getRankupProgressFilled();
		String n = getRankupProgressNeeded();
		Integer numb = Integer.valueOf(getPlayerRankupPercentageDirect(offlinePlayer));
		if(numberAPI.isBetween(numb, 0, 4)) {
			return n + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s;
		}
		else if(numberAPI.isBetween(numb, 5, 9)) {
			return f + s + n + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s;
		}
		else if(numberAPI.isBetween(numb, 10, 14)) {
		    return f + s + s + n + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s;
	    }
		else if(numberAPI.isBetween(numb, 15, 19)) {
		    return f + s + s + s + n + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s;
	    }
		else if(numberAPI.isBetween(numb, 20, 24)) {
		    return f + s + s + s + s + n + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s;
	    }
		else if(numberAPI.isBetween(numb, 25, 29)) {
			return f + s + s + s + s + s + n + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s;
	    }
		else if(numberAPI.isBetween(numb, 30, 34)) {
			return f + s + s + s + s + s + s + n + s + s + s + s + s + s + s + s + s + s + s + s + s + s;
	    }
		else if(numberAPI.isBetween(numb, 35, 39)) {
			return f + s + s + s + s + s + s + s + n + s + s + s + s + s + s + s + s + s + s + s + s + s;
	    }
		else if(numberAPI.isBetween(numb, 40, 44)) {
			return f + s + s + s + s + s + s + s + s + n + s + s + s + s + s + s + s + s + s + s + s + s;
	    }
		else if(numberAPI.isBetween(numb, 45, 49)) {
			return f + s + s + s + s + s + s + s + s + s + n + s + s + s + s + s + s + s + s + s + s + s;
	    }
		else if(numberAPI.isBetween(numb, 50, 54)) {
			return f + s + s + s + s + s + s + s + s + s + s + n + s + s + s + s + s + s + s + s + s + s;
	    }
		else if(numberAPI.isBetween(numb, 55, 59)) {
			return f + s + s + s + s + s + s + s + s + s + s + s + n + s + s + s + s + s + s + s + s + s;
	    }
		else if(numberAPI.isBetween(numb, 60, 64)) {
			return f + s + s + s + s + s + s + s + s + s + s + s + s + n + s + s + s + s + s + s + s + s;
	    }
		else if(numberAPI.isBetween(numb, 65, 69)) {
			return f + s + s + s + s + s + s + s + s + s + s + s + s + s + n + s + s + s + s + s + s + s;
	    }
		else if(numberAPI.isBetween(numb, 70, 74)) {
			return f + s + s + s + s + s + s + s + s + s + s + s + s + s + s + n + s + s + s + s + s + s;
	    }
		else if(numberAPI.isBetween(numb, 75, 79)) {
			return f + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + n + s + s + s + s + s;
	    }
		else if(numberAPI.isBetween(numb, 80, 84)) {
			return f + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + n + s + s + s + s;
	    }
		else if(numberAPI.isBetween(numb, 85, 89)) {
			return f + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + n + s + s + s;
	    }
		else if(numberAPI.isBetween(numb, 90, 94)) {
			return f + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + n + s + s;
	    }
		else if(numberAPI.isBetween(numb, 95, 99)) {
			return f + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + n + s;
	    } else if(numberAPI.isBetween(numb, 100, 100)) {
			if((main.globalStorage.getBooleanData("PlaceholderAPI.rankup-progress-full-enabled") == true)) {
				String full = getPluginMainClass().getString(main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-full"), offlinePlayer.getName());
				return full;
			} else {
			return f + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s + s;
			}
	    }
		return s;
	}
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return double player vault economy balance 
	    */
	public double getPlayerMoney(OfflinePlayer offlinePlayer) {
		return main.econ.getBalance(offlinePlayer);
	}
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return double player rank up cost with prestige increase applied
	    */
	public Double getPlayerRankupCostWithIncrease(OfflinePlayer offlinePlayer) {
              if(hasPrestiged(offlinePlayer)) {
            	 String nextRank = main.rankStorage.getRankupName(getPlayerRankPath(offlinePlayer));
            	  getIncreasedRankupCost(main.playerStorage.getPlayerPrestige(offlinePlayer), nextRank);
              }
		return 0.0;
	}
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param string
	    *  @return Colored string with symbols
	    */
	public String c(String string) {
		return ChatColor.translateAlternateColorCodes('&', main.getStringWithoutPAPI(string));
	}
	
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param text
	    *  @param player
	    *  @return PlaceholderAPI parsed string with symbols
	    */
	public String cp(String string, OfflinePlayer player) {
		return main.getString(string, player.getName());
	}
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return String player rank number in ranks list
	    *  @deprecated will read the number from the disk which slows down the server a bit | use getRankNumber(String pathName, String rankName); instead
	    */
	@Deprecated
	public String getPlayerRankNumber(OfflinePlayer offlinePlayer) {
		ArrayList<String> xo = new ArrayList<String>(rankDataConfig.getConfigurationSection("Ranks").getKeys(false));
		String ranknumber = String.valueOf(xo.indexOf(getPlayerRank(offlinePlayer)) + 1);
		return ranknumber;
	}
	
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param pathName the rank path
	    *  @return Set<String> a collection of the ranks
	    */
	public List<String> getRanksCollection(String pathName) {
		return main.rankStorage.getRanksCollection(pathName);
	}
	
	public List<String> getPrestigesCollection() {
		return main.prestigeStorage.getPrestigesCollection();
	}
	
	public List<String> getRebirthsCollection() {
		return main.rebirthStorage.getRebirthsCollection();
	}
	
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param rankName
	    *  @return String rank number in ranks list
	    *  @deprecated will read the number from the disk which slows down the server a bit | use getRankNumber(String pathName, String rankName); instead
	    */
	@Deprecated
	public String getRankNumber(String rankName) {
		ArrayList<String> xo = new ArrayList<String>(rankDataConfig.getConfigurationSection("Ranks").getKeys(false));
		String rankNumber = String.valueOf(xo.indexOf(rankName) + 1);
		return rankNumber;
	}
	
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param pathName rank's path name in rankdata.yml
	    *  @param rankName rank name ofc
	    *  @return String rank number in ranks list
	    */
	public String getRankNumber(String pathName, String rankName) {
		List<String> collection = getRanksCollection(pathName);
		String rankNumber = String.valueOf(collection.indexOf(rankName) + 1);
		return rankNumber;
	}
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return player prestige number in prestiges list
	    *  @deprecated dead.
	    */
	@Deprecated
	public String getPlayerPrestigeNumber(OfflinePlayer offlinePlayer) {
		ArrayList<String> xo = new ArrayList<String>(getConfig().getConfigurationSection("Prestiges").getKeys(false));
		String prestigeNumber = String.valueOf(xo.indexOf(getPlayerPrestige(offlinePlayer)) + 1);
		return prestigeNumber;
		
	}
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return prestige number in prestiges list
	    *  @deprecated dead.
	    */
	@Deprecated
	public String getPrestigeNumber(String prestigeName) {
		ArrayList<String> xo = new ArrayList<String>(getConfig().getConfigurationSection("Prestiges").getKeys(false));
		String prestigeNumber = String.valueOf(xo.indexOf(prestigeName) + 1);
		return prestigeNumber;
	}
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return List<String> a list of rank up commands | should not be directly executed because of the prefixes {[console] [op] [player]}
	    */ 
	public List<String> getPlayerRankupCommands(OfflinePlayer offlinePlayer) {
		List<String> nextRankCommands = main.rankStorage.getRankupCommands(getPlayerRankPath(offlinePlayer));
		return nextRankCommands;
	}
	
	public List<String> getPlayerNextRebirthCommands(OfflinePlayer offlinePlayer) {
		List<String> nextRebirthCommands = main.rebirthStorage.getRebirthCommands(getPlayerRebirth(offlinePlayer));
		return nextRebirthCommands;
	}
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param rankName
	    *  @return String rank display name
	    *  @deprecated gets the rank display name from a rank path loop that detects the rank name which slows down the process | use getRankDisplay(RankPath rankPath); instead
	    */
	@Deprecated
	public  String getRankDisplay(String rankName) {
		return main.rankStorage.getDisplayName(main.rankStorage.getRankPath(rankName));
	}
	
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param rankPath taken from rankStorage and playerStorage
	    *  @return String rank display name
	    */
	public String getRankDisplay(RankPath rankPath) {
		return main.rankStorage.getDisplayName(rankPath);
	}
	
	public String getRebirthDisplay(String rebirthName) {
		return main.rebirthStorage.getDisplayName(rebirthName);
	}
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param rankName
	    *  @return Double rank cost
	    *  @deprecated gets the rank cost name from a rank path loop that detects the rank name which slows down the process
	    */
	@Deprecated
	public Double getRankCost(String rankName) {
		return main.rankStorage.getCost(main.rankStorage.getRankPath(rankName));
	}
	
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param rankPath taken from rankStorage and playerStorage
	    *  @return double rank cost
	    */
	public double getRankCost(RankPath rankPath) {
		return main.rankStorage.getCost(rankPath);
	}
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param rankName
	    *  @return String rank cost formatted
	    *  @deprecated bad for a server with +100 players, slows it down | use getRankCostFormatted(RankPath rankPath); instead
	    */
	@Deprecated
	public String getRankCostFormatted(String rankName) {
		return formatBalance(main.rankStorage.getCost(main.rankStorage.getRankPath(rankName)));
	}
	
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param rankPath taken from rankStorage and playerStorage
	    *  @return String rank cost formatted
	    */
	public String getRankCostFormatted(RankPath rankPath) {
		return formatBalance(main.rankStorage.getCost(rankPath));
	}
	
	public String getRebirthCostFormatted(String rebirthName) {
		return formatBalance(main.rebirthStorage.getCost(rebirthName));
	}
	
	@Deprecated
	public String getRankup(String rankName) {
		return main.rankStorage.getRankupName(main.rankStorage.getRankPath(rankName));
	}
	
	public String getRankup(RankPath rankPath) {
		return main.rankStorage.getRankupName(rankPath);
	}
	
	public String getNextRebirth(String rebirthName) {
		return main.rebirthStorage.getNextRebirthName(rebirthName);
	}
	@Deprecated
	public boolean hasAllowPrestige(String rankName) {
		if(main.rankStorage.isAllowPrestige(main.rankStorage.getRankPath(rankName)) == true) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasAllowPrestige(RankPath rankPath) {
		if(main.rankStorage.isAllowPrestige(rankPath)) {
			return true;
		} else {
			return false;
		}
	}
	
	@Deprecated
	public boolean hasNotAllowPrestige(String rankName) {
		if(main.rankStorage.isAllowPrestige(main.rankStorage.getRankPath(rankName)) == true) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean hasNotAllowPrestige(RankPath rankPath) {
		if(main.rankStorage.isAllowPrestige(rankPath) == true) {
			return false;
		} else {
			return true;
		}
	}
	
	public  FileConfiguration getConfig() {
		return originalConfig;
	}
	
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer OfflinePlayer
	    *  @return String player prestige / returns null if hasPrestiged returns false or getPlayerPrestigeDisplay is null
	    */
	public String getPlayerPrestige(OfflinePlayer offlinePlayer) {
		//if(!hasPrestiged(offlinePlayer)) {
		//	return null;
		//}
		//if(getPlayerPrestigeDisplay(offlinePlayer) == null) {
			//return null;
		//}
		return main.playerStorage.getPlayerPrestige(offlinePlayer);
	}

	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer OfflinePlayer
	    *  @return Double player prestige cost / returns null if hasPrestiged returns false
	    */
	public Double getPlayerPrestigeCost(OfflinePlayer offlinePlayer) {
		if(!hasPrestiged(offlinePlayer)) {
			return null;
		}
		return main.prestigeStorage.getCost(getPlayerPrestige(offlinePlayer));
	}
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer OfflinePlayer
	    *  @return String player prestige cost converted to string / returns null if hasPrestiged returns false
	    */
	public String getPlayerPrestigeCostInString(OfflinePlayer offlinePlayer) {
		if(!hasPrestiged(offlinePlayer)) {
			return null;
		}
		return String.valueOf(main.prestigeStorage.getCost(getPlayerPrestige(offlinePlayer)));
	}
	
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer OfflinePlayer
	    *  @return String player prestige's cost formatted with formatBalance
	    */
	public String getPlayerPrestigeCostFormatted(OfflinePlayer offlinePlayer) {
		if(!hasPrestiged(offlinePlayer)) {
			return null;
		}
		return formatBalance(main.prestigeStorage.getCost(getPlayerPrestige(offlinePlayer)));
	}
	
	   /**
	    * PrisonRanksX API
	    * 
	    *  @return String curreny symbol from the storage (default: $)
	    */
	public String getPlaceholderAPICurrencySymbol() {
		return main.globalStorage.getStringData("PlaceholderAPI.currency-symbol");
	}
	
	   /**
	    * PrisonRanksX API
	    * 
	    *  @return boolean whether the curreny symbol should show up behind or after the placeholder
	    */
	public boolean isCurrencySymbolBehind() {
		return main.globalStorage.getBooleanData("PlaceholderAPI.currency-symbol-behind");
	}
	   /**
	    * PrisonRanksX API
	    * 
	    *  @return String percent sign from the storage (default: %)
	    */
	public String getPercentSign() {
		return main.globalStorage.getStringData("PlaceholderAPI.percent-sign");
	}
	
	   /**
	    * PrisonRanksX API
	    * 
	    *  @return boolean whether the percent sign should show up behind or after the placeholder
	    */
	public boolean isPercentSignBehind() {
		return main.globalStorage.getBooleanData("PlaceholderAPI.percent-sign-behind");
	}
	
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return boolean returns true if hasPrestiged returned false / returns false if next prestige display name is null
	    */
	public boolean hasNextPrestige(OfflinePlayer offlinePlayer) {
		if(!hasPrestiged(offlinePlayer)) {
			return true;
		}
		String currentPrestige = main.playerStorage.getPlayerPrestige(offlinePlayer);
	    String nextPrestige = main.prestigeStorage.getNextPrestigeName(currentPrestige);
	    if(nextPrestige.equalsIgnoreCase("LASTPRESTIGE")) {
	    	return false;
	    }
	    return true;
	}
	
	public boolean hasRebirthed(OfflinePlayer offlinePlayer) {
		return main.playerStorage.getPlayerRebirth(offlinePlayer) != null;
	}
	
	public boolean hasNextRebirth(OfflinePlayer offlinePlayer) {
		if(!hasRebirthed(offlinePlayer)) {
			return true;
		}
		String currentRebirth = main.playerStorage.getPlayerRebirth(offlinePlayer);
		String nextRebirth = main.rebirthStorage.getNextRebirthName(currentRebirth);
		return !nextRebirth.equalsIgnoreCase("LASTREBIRTH");
	}
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return String returns first prestige name if hasPrestiged returned false
	    */
	public String getPlayerNextPrestige(OfflinePlayer offlinePlayer) {
		if(!hasPrestiged(offlinePlayer)) {
			return main.globalStorage.getStringData("firstprestige");
		}
		String currentPrestige = main.playerStorage.getPlayerPrestige(offlinePlayer);
	    String nextPrestige = main.prestigeStorage.getNextPrestigeName(currentPrestige);
		return nextPrestige;
	}
	
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return String returns first rebirth name if hasRebirthed returned false
	    */
	public String getPlayerNextRebirth(OfflinePlayer offlinePlayer) {
		if(!hasRebirthed(offlinePlayer)) {
			return main.globalStorage.getStringData("firstrebirth");
		}
		String currentRebirth = main.playerStorage.getPlayerRebirth(offlinePlayer);
		String nextRebirth = main.rebirthStorage.getNextRebirthName(currentRebirth);
		return nextRebirth;
	}
	
	   /**
	    * PrisonRanksX API
	    * 
	    *  @return String the first prestige from the storage
	    */
	public String getFirstPrestige() {
		return main.globalStorage.getStringData("firstprestige");
	}
	
	public String getFirstRebirth() {
		return main.globalStorage.getStringData("firstrebirth");
	}
	
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return returns placeholderapi fallback from the storage if hasPrestiged or hasNextPrestige returned false
	    */
	public String getPlayerNextPrestigeDisplay(OfflinePlayer offlinePlayer) {
		if(!hasPrestiged(offlinePlayer)) {
			return main.globalStorage.getStringData("PlaceholderAPI.nextprestige-notprestiged");
		}
		if(!hasNextPrestige(offlinePlayer)) {
			return c(main.globalStorage.getStringData("PlaceholderAPI.prestige-lastprestige"));
		}

	    String nextPrestigeDisplay = main.prestigeStorage.getNextPrestigeDisplayName(getPlayerPrestige(offlinePlayer));
		return nextPrestigeDisplay;
	}
	
	public String getPlayerNextRebirthDisplay(OfflinePlayer offlinePlayer) {
		if(!hasRebirthed(offlinePlayer)) {
			return main.globalStorage.getStringData("PlaceholderAPI.nextrebirth-notrebirthed");
		}
		if(!hasNextRebirth(offlinePlayer)) {
			return c(main.globalStorage.getStringData("PlaceholderAPI.rebirth-lastrebirth"));
		}
		String nextRebirthDisplay = main.rebirthStorage.getNextRebirthDisplayName(getPlayerRebirth(offlinePlayer));
		return nextRebirthDisplay;
	}
	
	public String getPlayerNextPrestigeDisplayNoFallback(OfflinePlayer offlinePlayer) {
		if(!hasPrestiged(offlinePlayer)) {
			return main.prestigeStorage.getDisplayName(getFirstPrestige());
		}
		if(!hasNextPrestige(offlinePlayer)) {
			return null;
		}
		return main.prestigeStorage.getNextPrestigeDisplayName(getPlayerPrestige(offlinePlayer));
	}
	
	public String getPlayerNextRebirthDisplayNoFallback(OfflinePlayer offlinePlayer) {
		if(!hasRebirthed(offlinePlayer)) {
			return main.rebirthStorage.getDisplayName(getFirstRebirth());
		}
		if(!hasNextRebirth(offlinePlayer)) {
			return null;
		}
		return main.rebirthStorage.getNextRebirthDisplayName(getPlayerRebirth(offlinePlayer));
	}
	
	public String getPlayerNextPrestigeDisplayNoFallbackR(OfflinePlayer offlinePlayer) {
		if(!hasPrestiged(offlinePlayer)) {
			return main.prestigeStorage.getDisplayName(getFirstPrestige());
		}
		if(!hasNextPrestige(offlinePlayer)) {
			return null;
		}
		return c(main.prestigeStorage.getNextPrestigeDisplayName(getPlayerPrestige(offlinePlayer)));
	}
	
	public String getPlayerNextRebirthDisplayNoFallbackR(OfflinePlayer offlinePlayer) {
		if(!hasRebirthed(offlinePlayer)) {
			return main.rebirthStorage.getDisplayName(getFirstRebirth());
		}
		if(!hasNextRebirth(offlinePlayer)) {
			return null;
		}
		return c(main.rebirthStorage.getNextRebirthDisplayName(getPlayerRebirth(offlinePlayer)));
	}
	
	public double getPlayerNextPrestigeCost(OfflinePlayer offlinePlayer) {
		if(!hasPrestiged(offlinePlayer)) {
			return main.prestigeStorage.getCost(getFirstPrestige());
		}
		main.debug("Your prestige: " + getPlayerPrestige(offlinePlayer));
		main.debug("Your next prestige: " + getPlayerNextPrestige(offlinePlayer));
	    double nextPrestigeCost = main.prestigeStorage.getNextPrestigeCost(getPlayerPrestige(offlinePlayer));
		return nextPrestigeCost;
	}
	
	public double getPlayerNextRebirthCost(OfflinePlayer offlinePlayer) {
		if(!hasRebirthed(offlinePlayer)) {
			return main.rebirthStorage.getCost(getFirstRebirth());
		}
		double nextRebirthCost = main.rebirthStorage.getNextRebirthCost(getPlayerRebirth(offlinePlayer));
		return nextRebirthCost;
	}
	
	public  String getPlayerNextPrestigeCostFormatted(OfflinePlayer offlinePlayer) {
		return getPluginMainClass().formatBalance(getPlayerNextPrestigeCost(offlinePlayer));
	}
	
	public String getPlayerNextRebirthCostFormatted(OfflinePlayer offlinePlayer) {
		return getPluginMainClass().formatBalance(getPlayerNextRebirthCost(offlinePlayer));
	}
	
	public  String getPlayerNextPrestigeCostInString(OfflinePlayer offlinePlayer) {
		if(!hasPrestiged(offlinePlayer)) {
			return String.valueOf(main.prestigeStorage.getCost(getFirstPrestige()));
		}
	    Double nextPrestigeCost = main.prestigeStorage.getNextPrestigeCost(getPlayerPrestige(offlinePlayer));
		return String.valueOf(nextPrestigeCost);
	}
	public  boolean hasPrestigeFirework(OfflinePlayer offlinePlayer) {
		if(main.prestigeStorage.isSendFirework(getPlayerPrestige(offlinePlayer))) {
			return true;
		}
		return false;
	}

	public  String getPlayerPrestigeDisplay(OfflinePlayer offlinePlayer) {
		return main.prestigeStorage.getDisplayName(getPlayerPrestige(offlinePlayer));
	}

	public  String getPlayerPrestigeDisplayR(OfflinePlayer offlinePlayer) {
		return c(main.prestigeStorage.getDisplayName(getPlayerPrestige(offlinePlayer)));
	}
	
	public boolean hasPrestiged(OfflinePlayer offlinePlayer) {
		if(main.playerStorage.getPlayerPrestige(offlinePlayer) == null) {
			return false;
		}
		return true;
	}


	public  void setPlayerRank(OfflinePlayer offlinePlayer, String rankName) {
        main.playerStorage.setPlayerRank(offlinePlayer, rankName);
	}
	
	public  void setPlayerPrestige(OfflinePlayer offlinePlayer, String prestigeName) {
       main.playerStorage.setPlayerPrestige(offlinePlayer, prestigeName);
	}
	
	public void setPlayerRebirth(OfflinePlayer offlinePlayer, String rebirthName) {
		main.playerStorage.setPlayerRebirth(offlinePlayer, rebirthName);
	}
	
	public void setPlayerPath(OfflinePlayer offlinePlayer, String pathName) {
		main.playerStorage.setPlayerPath(offlinePlayer, pathName);
	}
	
	public void setPlayerRankPath(OfflinePlayer offlinePlayer, RankPath rankPath) {
		main.playerStorage.setPlayerRank(offlinePlayer, rankPath);
	}
	
	public Double getRankupCostIncreasePercentage(String prestigeName) {
		if(main.globalStorage.getDoubleData("PrestigeOptions.rankup_cost_increase_percentage") > 0) {
			return main.globalStorage.getDoubleData("PrestigeOptions.rankup_cost_increase_percentage") * Integer.valueOf(getPrestigeNumber(prestigeName));
		}
		if(main.prestigeStorage.getRankupCostIncreasePercentage(prestigeName) <= 0) {
			return 0.0;
		} else {
			return main.prestigeStorage.getRankupCostIncreasePercentage(prestigeName);
		}
	}
	@Deprecated
	public Double getIncreasedRankupCost(String prestigeName, String rankName) {
		Double eff = getRankCostMethodII(main.rankStorage.getRankPath(rankName));
		Double inc = eff / 100;
		Double afterinc = null;
		afterinc = inc * getRankupCostIncreasePercentage(prestigeName);
		return afterinc;
	}
	
	public double getIncreasedRankupCost(String prestigeName, RankPath rankPath) {
		double eff = getRankCostMethodII(rankPath);
		double inc = eff / 100;
		double afterinc;
		afterinc = inc * getRankupCostIncreasePercentage(prestigeName);
		return afterinc;
	}
	
	@Deprecated
	public Double getIncreasedRankupCostNB(String prestigeName, String rankName) {
		if(prestigeName == null || prestigeName.equalsIgnoreCase("null")) {
			return 0.0;
		}
		Double eff = getRankCostMethodII(main.rankStorage.getRankPath(rankName));
		Double inc = eff / 100;
		Double afterinc = null;
		afterinc = inc * getRankupCostIncreasePercentage(prestigeName);
		return afterinc;
	}
	
	public double getIncreasedRankupCostNB(String prestigeName, RankPath rankPath) {
		double eff = getRankCostMethodII(rankPath);
		if(prestigeName == null || prestigeName.equalsIgnoreCase("null")) {
			return eff;
		}
		double inc = eff / 100;
		double afterinc;
		afterinc = inc * getRankupCostIncreasePercentage(prestigeName);
		return afterinc;
	}
	
	public double getIncreasedRankupCost(String prestigeName, Double rankCost) {
		if(prestigeName == null || prestigeName.equalsIgnoreCase("null")) {
			return rankCost;
		}
		double inc = rankCost / 100;
		Double afterinc = inc * getRankupCostIncreasePercentage(prestigeName);
		if(afterinc.isNaN()) {
			return 0.0;
		}
		if(afterinc <= 0.0) {
			return 0.0;
		}
		return afterinc;
	}
	
	public boolean isLastRank(OfflinePlayer offlinePlayer) {
		if(getPlayerRank(offlinePlayer).equalsIgnoreCase(main.globalStorage.getStringData("lastrank"))) {
			return true;
		}
		return false;
	}
	public void resetPlayerRank(OfflinePlayer offlinePlayer) {
		main.playerStorage.setPlayerRank(offlinePlayer, main.globalStorage.getStringData("defaultrank"));
	}

	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return Double made for placeholderapi | returns 0.0 if getPlayerNextRank is null
	    */
	public Double getPlayerRankupCostWithIncreaseDirect(OfflinePlayer offlinePlayer) {
	    if(getPlayerNextRank(offlinePlayer) == null) {
	    	return 0.0;
	    }
	    RankPath rp = RankPath.getRankPath(main.playerStorage.getPlayerRank(offlinePlayer) + "#~#" + main.playerStorage.getPlayerPath(offlinePlayer));
		Double nextRankCost = main.rankStorage.getRankupCost(rp) + getIncreasedRankupCostNB(getPlayerPrestige(offlinePlayer), rp);	
		return Double.valueOf(nextRankCost);
	}
	
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return String made for placeholderapi | returns "100" if percentage is above 100
	    */
	public String getPlayerRankupPercentageDirect(OfflinePlayer offlinePlayer) {
		Double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / getPlayerRankupCostWithIncreaseDirect(offlinePlayer) * 100;
		String convertedValue = numberAPI.toFakeInteger(Double.valueOf(numberAPI.deleteScientificNotationA(percent)));
		if(Double.valueOf(convertedValue) > 100) {
			return "100";
		}
		return String.valueOf(convertedValue);
	}

	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return String made for placeholderapi | returns rankup percentage without a limit so it can be above 100
	    */
	public String getPlayerRankupPercentageNoLimitDirect(OfflinePlayer offlinePlayer) {
		Double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / getPlayerRankupCostWithIncreaseDirect(offlinePlayer) * 100;
		String percentRemovedSN = numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2);
		String convertedValue = numberAPI.toFakeInteger(Double.valueOf(percentRemovedSN));
		return String.valueOf(convertedValue);
	}

	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return String made for placeholderapi | returns rankup percentage with 2 decimal numbers / returns "100.0" if percentage is above 100
	    */
	public String getPlayerRankupPercentageDecimalDirect(OfflinePlayer offlinePlayer) {
		Double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / getPlayerRankupCostWithIncreaseDirect(offlinePlayer) * 100;
		String percentRemovedSN = numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2);
		Double convertedValue = Double.valueOf(percentRemovedSN);
		if(convertedValue > 100) {
			return "100.0";
		}
		return String.valueOf(percentRemovedSN);
    }
    
	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return String made for placeholderapi | returns rankup percentage with 2 decimal numbers without a limit so it can be above 100%
	    */
	public String getPlayerRankupPercentageDecimalNoLimitDirect(OfflinePlayer offlinePlayer) {
		Double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / getPlayerRankupCostWithIncreaseDirect(offlinePlayer) * 100;
		String percentRemovedSN = numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2);
		return String.valueOf(percentRemovedSN);
    }

	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return String player rankup display name
	    */
	public String getPlayerRankupDisplay(OfflinePlayer offlinePlayer) {
        return main.rankStorage.getRankupDisplayName(getPlayerRankPath(offlinePlayer));
	}

	   /**
	    * PrisonRanksX API
	    * 
	    *  @param offlinePlayer
	    *  @return String colored player rankup display name
	    */
	public String getPlayerRankupDisplayR(OfflinePlayer offlinePlayer) {
        return c(main.rankStorage.getRankupDisplayName(getPlayerRankPath(offlinePlayer)));
	}
	
	/**
	 * 
	 * @param configMessage
	 * @return returns a colored string config message
	 */
	public String g(String configMessage) {
		return c(main.messagesStorage.getStringMessage(configMessage));
	}
	/**
	 * 
	 * @param configMessage
	 * @return returns a string list config message
	 */
	public List<String> h(String configMessage) {
		return main.messagesStorage.getStringListMessage(configMessage);
	}
   String s(Object o) {
	   return String.valueOf(o);
   }
	
   /**
    * Execute a rankup to a player
    * @param player
    */
	public void rankup(Player player) {
       main.rankupAPI.rankup(player);
	}
	
   /**
    * Execute a prestige to a player
	* @param player
    */
	public void prestige(Player player) {
       main.prestigeAPI.prestige(player);
	}
	
	/**
	 * Execute a rankupmax to a player
	 * @param player
	 */
	public void rankupMax(Player player) {
		main.rankupMaxAPI.rankupMax(player);
	}
	
	public void rebirth(Player player) {
		main.rebirthAPI.rebirth(player);
	}
	/**
	 * 
	 * @param player
	 * @return -0 if player leaderboard position is less than 10
	 */
	public Integer getPlayerLeaderboardPosition(OfflinePlayer player) {
		return 0;
	}
	
	/**
	 * 
	 * @param intValue
	 * @return null if player leaderboard position is less than 10
	 */
    public OfflinePlayer getLeaderboardPosition(Integer intValue) {
    	return null;
    }
}
