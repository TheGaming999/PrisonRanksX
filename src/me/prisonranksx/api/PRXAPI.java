package me.prisonranksx.api;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import fr.mrmicky.fastparticle.FastParticle;
import fr.mrmicky.fastparticle.ParticleType;
import me.clip.placeholderapi.PlaceholderAPI;
import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.IPrestigeDataHandler;
import me.prisonranksx.data.IPrestigeDataStorage;
import me.prisonranksx.data.LevelType;
import me.prisonranksx.data.PercentageState;
import me.prisonranksx.data.PrestigeDataHandler;
import me.prisonranksx.data.PrestigeDataStorage;
import me.prisonranksx.data.RankDataHandler;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.data.RebirthDataHandler;
import me.prisonranksx.permissions.PermissionManager;
import me.prisonranksx.utils.MCProgressBar;
import me.prisonranksx.utils.NumberAPI;
import me.prisonranksx.utils.XMaterial;
import me.prisonranksx.utils.HolidayUtils.Holiday;
import net.milkbowl.vault.economy.Economy;

public class PRXAPI {
	public NumberAPI numberAPI;
	private MCProgressBar rankupProgressBar;
	private MCProgressBar rankupProgressBarExtended;
	private MCProgressBar globalProgressBar_rank;
	private MCProgressBar globalProgressBarExtended_rank;
	private MCProgressBar globalProgressBar_prestige;
	private MCProgressBar globalProgressBarExtended_prestige;
	private MCProgressBar globalProgressBar_rebirth;
	private MCProgressBar globalProgressBarExtended_rebirth;
	@SuppressWarnings("unused")
	private FileConfiguration rankDataConfig, prestigeDataConfig, rebirthDataConfig, customConfig
	, originalConfig, ranksConfig, prestigesConfig, rebirthsConfig, commandsConfig, messagesConfig;
	public PrisonRanksX main = null;
	public final static Set<String> AUTO_RANKUP_PLAYERS = new HashSet<>();
	public final static Set<String> AUTO_PRESTIGE_PLAYERS = new HashSet<>();
	public final static Set<String> AUTO_REBIRTH_PLAYERS = new HashSet<>();
	public final static Set<String> TASKED_PLAYERS = new HashSet<>();
	public Set<String> allRankAddPermissions, allRankDelPermissions, allPrestigeAddPermissions
	, allPrestigeDelPermissions, allRebirthAddPermissions, allRebirthDelPermissions;
	private String increaseType;
	private String rebirthIncreaseType;
	private String prestigeIncreaseExpression;
	private String rebirthIncreaseExpression;
	private boolean isNextProgressFullRankupEnabled;
	private String nextProgressFullRankup;
	private boolean isNextProgressFullPrestigeEnabled;
	private String nextProgressFullPrestige;
	private boolean isNextProgressFullRebirthEnabled;
	private String nextProgressFullRebirth;
	private boolean isNextProgressFullLastEnabled;
	private String nextProgressFullLast;

	public void forceLoadMain() {
		main = null;
		try {
			main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
		} catch (ClassCastException err) {
			Bukkit.getLogger().info("Couldn't update main field.");
		}
	}

	public void loadMain() {
		if(main == null) {
			try {
				main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
			} catch (java.lang.ClassCastException err) {
				Bukkit.getLogger().info("Main class is already casted");
			}
		}
	}
	public PRXAPI(boolean forceLoad) {
		forceLoadMain();
		increaseType = main.globalStorage.getStringData("PrestigeOptions.cost_increase_type");
		rebirthIncreaseType = main.globalStorage.getStringData("RebirthOptions.cost_increase_type");
		allRankAddPermissions = new LinkedHashSet<>();
		allRankDelPermissions = new LinkedHashSet<>();
		allPrestigeAddPermissions = new LinkedHashSet<>();
		allPrestigeDelPermissions = new LinkedHashSet<>();
		allRebirthAddPermissions = new LinkedHashSet<>();
		allRebirthDelPermissions = new LinkedHashSet<>();
		isNextProgressFullRankupEnabled = main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-isrankup-enabled");
		nextProgressFullRankup = main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-isrankup");
		isNextProgressFullPrestigeEnabled = main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-isprestige-enabled");
		nextProgressFullPrestige = main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-isprestige");
		isNextProgressFullRebirthEnabled = main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-isrebirth-enabled");
		nextProgressFullRebirth = main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-isrebirth");
		isNextProgressFullLastEnabled = main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-islast-enabled");
		nextProgressFullLast = main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-islast");
		TASKED_PLAYERS.clear();
		rankupProgressBar = new MCProgressBar();
		rankupProgressBarExtended = new MCProgressBar();
		globalProgressBar_rank = new MCProgressBar();
		globalProgressBarExtended_rank = new MCProgressBar();
		globalProgressBar_prestige = new MCProgressBar();
		globalProgressBarExtended_prestige = new MCProgressBar();
		globalProgressBar_rebirth = new MCProgressBar();
		globalProgressBarExtended_rebirth = new MCProgressBar();
		numberAPI = new NumberAPI();
		prestigeIncreaseExpression = main.globalStorage.getStringData("PrestigeOptions.cost_increase_expression");
		rebirthIncreaseExpression = main.globalStorage.getStringData("RebirthOptions.cost_increase_expression");
	}

	public PRXAPI() {
		loadMain();
		if(!main.isApiLoaded) {
			increaseType = main.globalStorage.getStringData("PrestigeOptions.cost_increase_type");
			rebirthIncreaseType = main.globalStorage.getStringData("RebirthOptions.cost_increase_type");
			allRankAddPermissions = new LinkedHashSet<String>();
			allRankDelPermissions = new LinkedHashSet<String>();
			allPrestigeAddPermissions = new LinkedHashSet<String>();
			allPrestigeDelPermissions = new LinkedHashSet<String>();
			allRebirthAddPermissions = new LinkedHashSet<String>();
			allRebirthDelPermissions = new LinkedHashSet<String>();
			isNextProgressFullRankupEnabled = main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-isrankup-enabled");
			nextProgressFullRankup = main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-isrankup");
			isNextProgressFullPrestigeEnabled = main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-isprestige-enabled");
			nextProgressFullPrestige = main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-isprestige");
			isNextProgressFullRebirthEnabled = main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-isrebirth-enabled");
			nextProgressFullRebirth = main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-isrebirth");
			isNextProgressFullLastEnabled = main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-islast-enabled");
			nextProgressFullLast = main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-islast");
			TASKED_PLAYERS.clear();
			rankupProgressBar = new MCProgressBar();
			rankupProgressBarExtended = new MCProgressBar();
			globalProgressBar_rank = new MCProgressBar();
			globalProgressBarExtended_rank = new MCProgressBar();
			globalProgressBar_prestige = new MCProgressBar();
			globalProgressBarExtended_prestige = new MCProgressBar();
			globalProgressBar_rebirth = new MCProgressBar();
			globalProgressBarExtended_rebirth = new MCProgressBar();
			numberAPI = new NumberAPI();
			prestigeIncreaseExpression = main.globalStorage.getStringData("PrestigeOptions.cost_increase_expression");
			rebirthIncreaseExpression = main.globalStorage.getStringData("RebirthOptions.cost_increase_expression");
			main.debug("prestigeIncreaseExpression: " + prestigeIncreaseExpression);
			main.isApiLoaded = true;
		}
	}

	public void setup() {loadMain();}

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
					allRankAddPermissions.add(addPerm.replace("%rank%", val.getName()).replace("%rankup%", val.getRankupName()));
				});
			}
			if(val.getDelPermissionList() != null) {
				val.getDelPermissionList().forEach(delPerm -> {
					allRankDelPermissions.add(delPerm.replace("%rank%", val.getName()).replace("%rankup%", val.getRankupName()));
				});
			}
		});
		main.prestigeStorage.getPrestigeData().values().forEach(val -> {
			if(val.getAddPermissionList() != null) {
				val.getAddPermissionList().forEach(addPerm -> {
					allPrestigeAddPermissions.add(addPerm.replace("%prestige%", val.getName()).replace("%nextprestige%", val.getNextPrestigeName()));
				});
			}
			if(val.getDelPermissionList() != null) {
				val.getDelPermissionList().forEach(delPerm -> {
					allPrestigeDelPermissions.add(delPerm.replace("%prestige%", val.getName()).replace("%nextprestige%", val.getNextPrestigeName()));
				});
			}
		});
		main.rebirthStorage.getRebirthData().values().forEach(val -> {
			if(val.getAddPermissionList() != null) {
				val.getAddPermissionList().forEach(addPerm -> {
					allRebirthAddPermissions.add(addPerm.replace("%rebirth%", val.getName()).replace("%nextrebirth%", val.getNextRebirthName()));
				});
			}
			if(val.getDelPermissionList() != null) {
				val.getDelPermissionList().forEach(delPerm -> {
					allRebirthDelPermissions.add(delPerm.replace("%rebirth%", val.getName()).replace("%nextrebirth%", val.getNextRebirthName()));
				});
			}
		});
		// }
	}

	public void saveConfigs() {
		main.getConfigManager().saveConfigs();
	}

	public PermissionManager getPermissionManager() {
		return main.perm;
	}

	public boolean hasActionUtilEnabled() {
		return main.isActionUtil;
	}

	public boolean isLegacy() {
		return main.isBefore1_7;
	}

	public NumberAPI getNumberAPI() {
		return this.numberAPI;
	}

	public Economy getEconomy() {
		return main.econ;
	}

	public IPrestigeMax getPrestigeMax() {
		return main.getPrestigeMax();
	}

	public IPrestigeDataStorage getPrestigeStorage() {
		return main.prestigeStorage;
	}

	/**
	 * 
	 * @return amount of registered rank paths (1 by default)
	 */
	public int getPathsCount() {
		return main.rankStorage.getPathsAmount();
	}

	@Deprecated
	public void saveCustomYml(FileConfiguration ymlConfig, File ymlFile) {
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


	/**
	 *
	 * @return always true
	 */
	@Deprecated
	public boolean uuidOption() {
		return true;
	}

	public PrisonRanksX getPluginMainClass() {
		return main;
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param rebirth
	 * @return true if the rebirth is registered on the server, false otherwise.
	 */
	public boolean rebirthExists(String rebirth) {
		return main.rebirthStorage.getRebirthData().get(rebirth) != null;
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param prestige
	 * @return true if the prestige is registered on the server, false otherwise.
	 */
	public boolean prestigeExists(String prestige) {
		return main.prestigeStorage.getPrestigeData().get(prestige) != null;
	}

	public boolean prestigeInfiniteExists(String prestige) {
		long pre = Long.valueOf(prestige);
		if(pre > main.infinitePrestigeSettings.getFinalPrestige() || pre < 0) {
			return false;
		}
		return true;
	}

	public boolean prestigeExistsAny(String prestige) {
		if(main.isInfinitePrestige) {
			return main.prestigeStorage.getPrestigeData().get(prestige) != null;
		} else {
			long pre = Long.valueOf(prestige);
			if(pre > main.infinitePrestigeSettings.getFinalPrestige() || pre < 0) {
				return false;
			}
			return true;
		}
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param rank
	 * @return true if the rank is registered on the server within the default path, false otherwise.
	 */
	public boolean rankExists(String rank) {
		return main.rankStorage.getEntireData().get(rank + "#~#default") != null;
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param rank
	 * @param path
	 * @return true if the rank and the path are registered on the server, false otherwise.
	 */
	public boolean rankExists(String rank, String path) {
		return main.rankStorage.getEntireData().get(rank + "#~#" + path) != null;
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param rank
	 * @param allPaths
	 * @return true if the rank is registered on one of the available paths on the server, false otherwise.
	 */
	public boolean rankExists(String rank, boolean allPaths) {
		boolean ye = false;
		for(String path : main.rankStorage.getPaths()) {
			if(main.rankStorage.getEntireData().containsKey(rank + "#~#" + path)) {
				ye = true;
			}
		}
		return ye;
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param rankPath
	 * @return true if the rankPath is registered on the server, false otherwise.
	 */
	public boolean rankPathExists(RankPath rankPath) {
		return main.rankStorage.getEntireData().get(rankPath.get()) != null;
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param path
	 * @return true if the path is registered on the server, false otherwise.
	 */
	public boolean pathExists(String path) {
		return main.rankStorage.getPaths().contains(path);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @return Default path name
	 */
	public String getDefaultPath() {
		return main.globalStorage.getStringData("defaultpath");
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @return Default rank name
	 */
	public String getDefaultRank() {
		return main.globalStorage.getStringData("defaultrank");
	}

	/**
	 * @deprecated use getLastRank(String pathName);
	 * @return last rank that has been set in config.yml
	 */
	@Deprecated
	public String getLastRank() {
		return main.globalStorage.getStringData("lastrank");
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param pathName
	 * @return last rank that has been detected when the rank data got setup.
	 */
	public String getLastRank(String pathName) {
		return main.rankStorage.getLastRank(pathName);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @param offlinePlayer
	 *  @return String player rank name
	 */
	public String getPlayerRank(OfflinePlayer offlinePlayer) {
		return main.playerStorage.getPlayerRank(offlinePlayer);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid player unique id
	 * @return player rank from a rank path
	 */
	public String getPlayerRank(UUID uuid) {
		return main.playerStorage.getPlayerRank(uuid);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid player uuid, fake uuid or real uuid are ok.
	 * @return player name from storage
	 */
	public String getPlayerNameFromUUID(UUID uuid) {
		return main.playerStorage.getPlayerData().get(uuid.toString()).getName();
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid .toStringed() player uuid, fake uuid or real uuid are ok.
	 * @return player name from storage
	 */
	public String getPlayerNameFromUUID(String uuid) {
		return main.playerStorage.getPlayerData().get(uuid).getName();
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param offlinePlayer
	 * @return RankPath player rank path which gives you access to both the rank name and the path name
	 */
	public RankPath getPlayerRankPath(OfflinePlayer offlinePlayer) {
		return main.playerStorage.getPlayerRankPath(offlinePlayer);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid player unique id
	 * @return RankPath which contains: getRankName(), getPathName().
	 * can return an offline player rankpath.
	 */
	public RankPath getPlayerRankPath(UUID uuid) {
		return main.playerStorage.getPlayerRankPath(uuid);
	}

	/**
	 * 
	 * @param rankPath
	 * @return double rank cost
	 * @deprecated use getRankCost(RankPath rankPath)
	 */
	public double getRankCostMethodII(RankPath rankPath) {
		return main.rankStorage.getCost(rankPath);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param prestigeName
	 * @return prestige cost from the storage
	 */
	public double getPrestigeCost(String prestigeName) {
		return main.prestigeStorage.getCost(prestigeName);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param rebirthName
	 * @return rebirth cost from the storage
	 */
	public double getRebirthCost(String rebirthName) {
		return main.rebirthStorage.getCost(rebirthName);
	}
	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @param offlinePlayer
	 *  @return String player rank display name/prefix
	 */
	public String getPlayerRankDisplay(OfflinePlayer offlinePlayer) {
		return String.valueOf(main.rankStorage.getDisplayName(getPlayerRankPath(offlinePlayer)));
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid player unique id
	 * @return display name of player rank
	 */
	public String getPlayerRankDisplay(UUID uuid) {
		return String.valueOf(main.rankStorage.getDisplayName(getPlayerRankPath(uuid)));
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param offlinePlayer
	 * @return display name of player rebirth
	 */
	@Nullable
	public String getPlayerRebirthDisplay(OfflinePlayer offlinePlayer) {
		return String.valueOf(main.rebirthStorage.getDisplayName(getPlayerRebirth(offlinePlayer)));
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid player unique id
	 * @return display name of player rebirth
	 */
	@Nullable
	public String getPlayerRebirthDisplay(UUID uuid) {
		return String.valueOf(main.rebirthStorage.getDisplayName(getPlayerRebirth(uuid)));
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @param offlinePlayer
	 *  @return double player current rank's cost
	 */
	public double getPlayerRankCost(OfflinePlayer offlinePlayer) {
		return (main.rankStorage.getCost(getPlayerRankPath(offlinePlayer)));
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid player unique id
	 * @return rank cost of player rank
	 */
	public double getPlayerRankCost(UUID uuid) {
		return (main.rankStorage.getCost(getPlayerRankPath(uuid)));
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param offlinePlayer
	 * @return current player rebirth's cost (not next)
	 */
	public double getPlayerRebirthCost(OfflinePlayer offlinePlayer) {
		return (main.rebirthStorage.getCost(getPlayerRebirth(offlinePlayer)));
	}

	/**
	 * 
	 * @param uuid player unique id
	 * @return cost of player current rebirth
	 */
	public double getPlayerRebirthCost(UUID uuid) {
		return (main.rebirthStorage.getCost(getPlayerRebirth(uuid)));
	}
	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param player
	 * @return boolean also checks if "Options.autorankup" is true or false so this is one time check
	 */
	public boolean isAutoRankupEnabled(Player player) {
		if(main.globalStorage.getBooleanData("Options.autorankup") == false) {
			return false;
		}
		return AUTO_RANKUP_PLAYERS.contains(player.getName());
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param player
	 * @return if player has autoprestige enabled
	 */
	public boolean isAutoPrestigeEnabled(Player player) {
		return AUTO_PRESTIGE_PLAYERS.contains(player.getName());
	}
	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 *  @param offlinePlayer
	 *  @return String player current rank's cost formatted (1k,10m,1.5b,etc..).
	 */
	public String getPlayerRankCostFormatted(OfflinePlayer offlinePlayer) {
		return formatBalance(main.rankStorage.getCost(getPlayerRankPath(offlinePlayer)));
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid player unique id
	 * @return Formatted cost of player current rank
	 */
	public String getPlayerRankCostFormatted(UUID uuid) {
		return formatBalance(main.rankStorage.getCost(getPlayerRankPath(uuid)));
	}
	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
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
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid player unique id
	 * @return player next rank name | returns null if player current rank next rank is set to LASTRANK
	 */
	public String getPlayerNextRank(UUID uuid) {
		String nextRank = main.rankStorage.getRankupName(getPlayerRankPath(uuid));
		if(nextRank.equalsIgnoreCase("lastrank")) {
			return null;
		} else {
			return nextRank;
		}
	}
	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @param offlinePlayer
	 *  @return String player rank up name | doesn't return null if the player is at the latest rank, will return "LASTRANK" instead
	 */
	public String getPlayerNextRankN(OfflinePlayer offlinePlayer) {
		String nextRank = main.rankStorage.getRankupName(getPlayerRankPath(offlinePlayer));
		return nextRank;
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @param uuid
	 *  @return String player rank up name | doesn't return null if the player is at the latest rank, will return "LASTRANK" instead
	 */
	public String getPlayerNextRankN(UUID uuid) {
		String nextRank = main.rankStorage.getRankupName(getPlayerRankPath(uuid));
		return nextRank;
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param offlinePlayer
	 * @return player's current rebirth can be null
	 */
	public String getPlayerRebirth(OfflinePlayer offlinePlayer) {
		String rebirthName = main.playerStorage.getPlayerRebirth(offlinePlayer);
		return rebirthName;
	}

	/**
	 * 
	 * @param uuid player unique id
	 * @return player current rebirth name
	 */
	public String getPlayerRebirth(UUID uuid) {
		String rebirthName = main.playerStorage.getPlayerRebirth(uuid);
		return rebirthName;
	}

	@Deprecated
	public String getRankupProgressStyle() {
		String s = getPluginMainClass().getString(main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-style"));
		return s;
	}

	@Deprecated
	public String getRankupProgressFilled() {
		String f = getPluginMainClass().getString(main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-filled"));
		return f;
	}

	@Deprecated
	public String getRankupProgressNeeded() {
		String n = getPluginMainClass().getString(main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-needed"));
		return n;
	}

	public PRXManager getPrisonRanksXManager() {
		return main.manager;
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param rankPath
	 * @return get access to rank settings
	 */
	public RankDataHandler getRank(RankPath rankPath) {
		return main.rankStorage.getDataHandler(rankPath.get());
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param name
	 * @return get access to prestige settings
	 */
	public IPrestigeDataHandler getPrestige(String name) {
		return main.prestigeStorage.getHandler(name);
	}

	/**
	 * 
	 * @param name
	 * @return get access to rebirth settings
	 */
	public RebirthDataHandler getRebirth(String name) {
		return main.rebirthStorage.getDataHandler(name);
	}

	/**
	 * <p><i>this method is not thread-safe
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
				double percent = (getPlayerMoney(p) / getPlayerNextPrestigeCostWithIncreaseDirect(p)) * 100;
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

	public PercentageState getPlayerNextPercentageOnline(final UUID uuid, final String name) {
		UUID p = uuid;
		PercentageState ps = new PercentageState();
		if(!this.isLastRank(p)) { // is not last rank
			double percent = (getPlayerMoney(name) / getPlayerRankupCostWithIncreaseDirect(p)) * 100;
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
				double percent = (getPlayerMoney(name) / getPlayerNextPrestigeCostWithIncreaseDirect(p)) * 100;
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
					double percent = (getPlayerMoney(name) / getPlayerNextRebirthCost(p)) * 100;
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
	 * <p><i>this method is not thread-safe
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
				double percent = (getPlayerMoney(p) / getPlayerNextPrestigeCostWithIncreaseDirect(p)) * 100;
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


	public String getPlayerNextPercentageNoLimitOnline(final UUID uuid, final String name) {
		UUID p = uuid;
		if(!this.isLastRank(p)) { // is not last rank
			double percent = (getPlayerMoney(name) / getPlayerRankupCostWithIncreaseDirect(p)) * 100;
			String intConverted = numberAPI.toFakeInteger(percent);
			return intConverted;
		} else { // is last rank
			if(this.hasNextPrestige(p)) {
				double percent = (getPlayerMoney(name) / getPlayerNextPrestigeCostWithIncreaseDirect(p)) * 100;
				String intConverted = numberAPI.toFakeInteger(percent);
				return intConverted;
			} else {
				if(this.hasNextRebirth(p)) {
					double percent = (getPlayerMoney(name) / getPlayerNextRebirthCost(p)) * 100;
					String intConverted = numberAPI.toFakeInteger(percent);
					return intConverted;
				} else {
					return "100";
				}
			}
		}
	}
	/**
	 * <p><i>this method is not thread-safe
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
				double percent = (getPlayerMoney(p) / getPlayerNextPrestigeCostWithIncreaseDirect(p)) * 100;
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

	public String getPlayerNextPercentageDecimalOnline(UUID uuid, final String name) {
		UUID p = uuid;
		if(!this.isLastRank(p)) { // is not last rank
			double percent = (getPlayerMoney(name) / getPlayerRankupCostWithIncreaseDirect(p)) * 100;
			if(percent >= 100) {
				return "100.0";
			}
			String intConverted = String.valueOf(numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2));
			return intConverted;
		} else { // is last rank
			if(this.hasNextPrestige(p)) {
				double percent = (getPlayerMoney(name) / getPlayerNextPrestigeCostWithIncreaseDirect(p)) * 100;
				if(percent >= 100) {
					return "100.0";
				}
				String intConverted = String.valueOf(numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2));
				return intConverted;
			} else {
				if(this.hasNextRebirth(p)) {
					double percent = (getPlayerMoney(name) / getPlayerNextRebirthCost(p)) * 100;
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
	 * <p><i>this method is not thread-safe
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

	public String getPlayerNextPercentageDecimalNoLimitOnline(UUID uuid, final String name) {
		UUID p = uuid;
		if(!this.isLastRank(p)) { // is not last rank
			double percent = (getPlayerMoney(name) / getPlayerRankupCostWithIncreaseDirect(p)) * 100;
			String intConverted = String.valueOf(numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2));
			return intConverted;
		} else { // is last rank
			if(this.hasNextPrestige(p)) {
				double percent = (getPlayerMoney(name) / getPlayerNextPrestigeCost(p)) * 100;
				String intConverted = String.valueOf(numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2));
				return intConverted;
			} else {
				if(this.hasNextRebirth(p)) {
					double percent = (getPlayerMoney(name) / getPlayerNextRebirthCost(p)) * 100;
					String intConverted = String.valueOf(numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2));
					return intConverted;
				} else {
					return "100.0";
				}
			}
		}
	}
	/**
	 * <p><i>this method is not thread-safe
	 * @param offlinePlayer
	 * @return next stage progress bar | stage => {rank,prestige,rebirth}
	 */
	public String getPlayerNextProgress(OfflinePlayer offlinePlayer) {
		OfflinePlayer p = offlinePlayer;
		PercentageState state = getPlayerNextPercentage(p);
		LevelType levelType = state.getLevelType();
		String percentage = state.getPercentage();
		boolean is100 = percentage.equals("100");
		if(levelType == LevelType.RANK) {
			if(isNextProgressFullRankupEnabled && is100) {
				return nextProgressFullRankup;
			} else {
				int converted = Integer.valueOf(percentage) / 10;
				globalProgressBar_rank.setValue(converted);
				return globalProgressBar_rank.getProgressBar();
			}
		}
		else if(levelType == LevelType.PRESTIGE) {
			if(isNextProgressFullPrestigeEnabled && is100) {
				return nextProgressFullPrestige;
			} else {
				int converted = Integer.valueOf(percentage) / 10;
				globalProgressBar_prestige.setValue(converted);
				return globalProgressBar_prestige.getProgressBar();
			}
		}
		else if(levelType == LevelType.REBIRTH) {
			if(isNextProgressFullRebirthEnabled && is100) {
				return nextProgressFullRebirth;
			} else {
				int converted = Integer.valueOf(percentage) / 10;
				globalProgressBar_rebirth.setValue(converted);
				return globalProgressBar_rebirth.getProgressBar();
			}
		}
		else if(levelType == LevelType.UNKNOWN && isNextProgressFullLastEnabled && is100) {
			return nextProgressFullLast;
		}
		return globalProgressBar_rank.getPlainProgressBar();
	}

	public String getPlayerNextProgress(UUID uuid, final String name) {
		UUID p = uuid;
		if(getPlayerNextPercentageOnline(p, name).getLevelType() == LevelType.RANK ) {
			if(main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-isrankup-enabled")
					&& getPlayerNextPercentageOnline(p, name).getPercentage().equals("100")) {
				return main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-isrankup");
			} else {
				int converted = Integer.valueOf(getPlayerNextPercentageOnline(p, name).getPercentage()) / 10;
				globalProgressBar_rank.setValue(converted);
				return globalProgressBar_rank.getProgressBar();
			}
		}
		if(getPlayerNextPercentageOnline(p, name).getLevelType() == LevelType.PRESTIGE) {
			if(main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-isprestige-enabled")
					&& getPlayerNextPercentageOnline(p, name).getPercentage().equals("100")) {
				return main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-isprestige");
			} else {
				int converted = Integer.valueOf(getPlayerNextPercentageOnline(p, name).getPercentage()) / 10;
				globalProgressBar_prestige.setValue(converted);
				return globalProgressBar_prestige.getProgressBar();
			}
		}
		if(getPlayerNextPercentageOnline(p, name).getLevelType() == LevelType.REBIRTH) {
			if(main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-isrebirth-enabled")
					&& getPlayerNextPercentageOnline(p, name).getPercentage().equals("100")) {
				return main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-isrebirth");
			} else {
				int converted = Integer.valueOf(getPlayerNextPercentageOnline(p, name).getPercentage()) / 10;
				globalProgressBar_rebirth.setValue(converted);
				return globalProgressBar_rebirth.getProgressBar();
			}
		}
		if(getPlayerNextPercentageOnline(p, name).getLevelType() == LevelType.UNKNOWN && main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-islast-enabled")
				&& getPlayerNextPercentageOnline(p, name).getPercentage().equals("100")) {
			return main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-islast");
		}
		return globalProgressBar_rank.getPlainProgressBar();
	}

	/**
	 * <p><i>this method is not thread-safe
	 * @param offlinePlayer
	 * @return next stage progress bar extended to 20 chars
	 */
	public String getPlayerNextProgressExtended(OfflinePlayer offlinePlayer) {
		OfflinePlayer p = offlinePlayer;
		PercentageState state = getPlayerNextPercentage(p);
		LevelType levelType = state.getLevelType();
		String percentage = state.getPercentage();
		boolean is100 = percentage.equals("100");
		if(levelType == LevelType.RANK) {
			if(isNextProgressFullRankupEnabled && is100) {
				return nextProgressFullRankup;
			} else {
				int converted = Integer.valueOf(percentage) / 5;
				globalProgressBarExtended_rank.setValue(converted);
				return globalProgressBarExtended_rank.getProgressBar();
			}
		}
		else if(levelType == LevelType.PRESTIGE) {
			if(isNextProgressFullPrestigeEnabled && is100) {
				return nextProgressFullPrestige;
			} else {
				int converted = Integer.valueOf(percentage) / 5;
				globalProgressBarExtended_prestige.setValue(converted);
				return globalProgressBarExtended_prestige.getProgressBar();
			}
		}
		else if(levelType == LevelType.REBIRTH) {
			if(isNextProgressFullRebirthEnabled && is100) {
				return nextProgressFullRebirth;
			} else {
				int converted = Integer.valueOf(percentage) / 5;
				globalProgressBarExtended_rebirth.setValue(converted);
				return globalProgressBarExtended_rebirth.getProgressBar();
			}
		}
		else if(levelType == LevelType.UNKNOWN && isNextProgressFullLastEnabled && is100) {
			return nextProgressFullLast;
		}
		return globalProgressBar_rank.getPlainProgressBar();
	}

	public String getPlayerNextProgressExtended(UUID uuid, final String name) {
		UUID p = uuid;
		if(getPlayerNextPercentageOnline(p, name).getLevelType() == LevelType.RANK ) {
			if(main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-isrankup-enabled")
					&& getPlayerNextPercentageOnline(p, name).getPercentage().equals("100")) {
				return main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-isrankup");
			} else {
				int converted = Integer.valueOf(getPlayerNextPercentageOnline(p, name).getPercentage()) / 5;
				globalProgressBarExtended_rank.setValue(converted);
				return globalProgressBarExtended_rank.getProgressBar();
			}
		}
		if(getPlayerNextPercentageOnline(p, name).getLevelType() == LevelType.PRESTIGE) {
			if(main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-isprestige-enabled")
					&& getPlayerNextPercentageOnline(p, name).getPercentage().equals("100")) {
				return main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-isprestige");
			} else {
				int converted = Integer.valueOf(getPlayerNextPercentageOnline(p, name).getPercentage()) / 5;
				globalProgressBarExtended_prestige.setValue(converted);
				return globalProgressBarExtended_prestige.getProgressBar();
			}
		}
		if(getPlayerNextPercentageOnline(p, name).getLevelType() == LevelType.REBIRTH) {
			if(main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-isrebirth-enabled")
					&& getPlayerNextPercentageOnline(p, name).getPercentage().equals("100")) {
				return main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-isrebirth");
			} else {
				int converted = Integer.valueOf(getPlayerNextPercentageOnline(p, name).getPercentage()) / 5;
				globalProgressBarExtended_rebirth.setValue(converted);
				return globalProgressBarExtended_rebirth.getProgressBar();
			}
		}
		if(getPlayerNextPercentageOnline(p, name).getLevelType() == LevelType.UNKNOWN && main.globalStorage.getBooleanData("PlaceholderAPI.next-progress-full-islast-enabled")
				&& getPlayerNextPercentageOnline(p, name).getPercentage().equals("100")) {
			return main.globalStorage.getStringData("PlaceholderAPI.next-progress-full-islast");
		}
		return globalProgressBar_rank.getPlainProgressBar();
	}

	/**
	 * <p><i>this method is not thread-safe
	 * @param offlinePlayer
	 * @return player next rank progress bar
	 */
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

	/**
	 * <p><i>this method is not thread-safe
	 * @param offlinePlayer
	 * @return player certain rank progress bar
	 */
	public String getPlayerRankProgressBar(OfflinePlayer offlinePlayer, RankPath rankPath) {
		OfflinePlayer p = offlinePlayer;
		int convertedPercentage = Integer.valueOf(getPlayerRankPercentage(p, rankPath)) / 10;
		if(convertedPercentage >= 10 && main.globalStorage.getBooleanData("PlaceholderAPI.rankup-progress-full-enabled")) {
			return main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-full");
		}
		rankupProgressBar.setValue(convertedPercentage);
		return rankupProgressBar.getProgressBar();
	}

	/**
	 * <p><i>this method is not thread-safe
	 * @param uuid player unique id
	 * @param name player name
	 * @return player next rank progress bar
	 */
	public String getPlayerRankupProgressBar(UUID uuid, final String name) {
		UUID p = uuid;
		if(isLastRank(p)) {
			return main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-lastrank");
		}
		int convertedPercentage = Integer.valueOf(getPlayerRankupPercentageDirectOnline(p, name)) / 10;
		if(convertedPercentage >= 10 && main.globalStorage.getBooleanData("PlaceholderAPI.rankup-progress-full-enabled")) {
			return main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-full");
		}
		rankupProgressBar.setValue(convertedPercentage);
		return rankupProgressBar.getProgressBar();
	}

	/**
	 * <p><i>this method is not thread-safe
	 * @param offlinePlayer
	 * @return player next rank progress bar with more characters
	 */
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
	 * <p><i>this method is not thread-safe
	 * @param offlinePlayer
	 * @return player certain rank progress bar with more characters
	 */
	public String getPlayerRankProgressBarExtended(OfflinePlayer offlinePlayer, RankPath rankPath) {
		OfflinePlayer p = offlinePlayer;
		int convertedPercentage = Integer.valueOf(getPlayerRankPercentage(p, rankPath)) / 5;
		if(convertedPercentage >= 5 && main.globalStorage.getBooleanData("PlaceholderAPI.rankup-progress-full-enabled")) {
			return main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-full");
		}
		rankupProgressBarExtended.setValue(convertedPercentage);
		return rankupProgressBarExtended.getProgressBar();
	}

	/**
	 * <p><i>this method is not thread-safe
	 * @param uuid player unique id
	 * @param name player name
	 * @return player next rank progress bar with more characters
	 */
	public String getPlayerRankupProgressBarExtended(UUID uuid, String name) {
		UUID p = uuid;
		if(isLastRank(p)) {
			return main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-lastrank");
		}
		int convertedPercentage = Integer.valueOf(getPlayerRankupPercentageDirectOnline(p, name)) / 5;
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
	 * 
	 * 
	 *  @param offlinePlayer
	 *  @return double player vault economy balance 
	 */
	public double getPlayerMoney(OfflinePlayer offlinePlayer) {
		return main.econ.getBalance(offlinePlayer);
	}

	@SuppressWarnings("deprecation")
	public double getPlayerMoney(String name) {
		return main.econ.getBalance(name);
	}

	@SuppressWarnings("deprecation")
	public double getPlayerMoneyOnline(Player p) {
		return main.econ.getBalance(p.getName());
	}
	/**
	 * PrisonRanksX API
	 * 
	 *  @param offlinePlayer
	 *  @return double player rank up cost with prestige increase applied | returns 0.0 if not prestiged
	 */
	@Deprecated
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
		return main.getString(string);
	}

	/**
	 * PrisonRanksX API
	 * 
	 * @param stringList
	 * @return Colored string list with symbols
	 */
	public List<String> cl(List<String> stringList) {
		List<String> newList = new ArrayList<>();
		stringList.forEach(line -> {
			newList.add(c(line));
		});
		return newList;
	}

	/**
	 * PrisonRanksX API
	 * 
	 *  @param string
	 *  @param player
	 *  @return PlaceholderAPI parsed string with symbols
	 */
	public String cp(String string, OfflinePlayer player) {
		return main.getString(string, player.getName());
	}

	public String cp(String string, Player player) {
		return main.getString(string, player.getName());
	}
	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @param offlinePlayer
	 *  @return int player rank number in ranks list
	 */
	public int getPlayerRankNumber(OfflinePlayer offlinePlayer) {
		return Integer.valueOf(getRankNumber(getPlayerRankPath(offlinePlayer).getPathName(), getPlayerRank(offlinePlayer)));
	}

	@Nonnull
	public int getPlayerRankNumber(UUID uuid) {
		if(uuid == null) return 0;
		RankPath rp = getPlayerRankPath(uuid);
		if(rp == null) return 0;
		String pathName = rp.getPathName();
		String rankName = rp.getRankName();
		if(pathName == null || rankName == null) return 0;
		return Integer.valueOf(getRankNumber(pathName, rankName));
	}

	/**
	 * 
	 * @param number (index-1) (so it should start from 1)
	 * @return can return null when (number - 1) is less than 0 or when (number - 1) is higher than or equal
	 * to prestigesCollection size
	 */
	@Nullable
	public String getPrestigeNameFromNumber(final int number) {
		if((number - 1) < 0) {
			return null;
		}
		if((number - 1) >= getPrestigeSize()) {
			return null;
		}
		return main.isInfinitePrestige ? String.valueOf(number) : getPrestigesCollection().get(number - 1);
	}

	/**
	 * 
	 * @param number (index-1) (so it should start from 1)
	 * @return can return null when (number - 1) is less than 0 or when (number - 1) is higher than or equal
	 * to rebirthsCollection size
	 */
	@Nullable
	public String getRebirthNameFromNumber(final int number) {
		if((number - 1) < 0) {
			return null;
		}
		if((number - 1) >= getRebirthsCollection().size()) {
			return null;
		}
		return getRebirthsCollection().get(number - 1);
	}

	/**
	 * PrisonRanksX API
	 * 
	 *  @param pathName the rank path (default path: "default")
	 *  @return List<String> a collection of the available ranks
	 */
	public List<String> getRanksCollection(String pathName) {
		return main.rankStorage.getRanksCollection(pathName);
	}

	/**
	 * @deprecated use getRanksCollection(String pathName);
	 * @return A set collection of ranks
	 */
	@Deprecated
	public List<String> getRanksCollection() {
		return main.rankStorage.getRanksCollection("default");
	}

	/**
	 * 
	 * @return a list of  available prestige names
	 */
	public synchronized List<String> getPrestigesCollection() {
		return main.prestigeStorage.getPrestigesCollection();
	}

	/**
	 * 
	 * @return a list of available rebirth names
	 */
	public List<String> getRebirthsCollection() {
		return main.rebirthStorage.getRebirthsCollection();
	}

	/**
	 * PrisonRanksX API
	 * 
	 *  @param rankName
	 *  @return String rank number in ranks list
	 *  @deprecated use getRankNumber(String pathName, String rankName); instead
	 */
	@Deprecated
	public String getRankNumber(String rankName) {
		ArrayList<String> xo = new ArrayList<String>(rankDataConfig.getConfigurationSection("Ranks.default").getKeys(false));
		String rankNumber = String.valueOf(xo.indexOf(rankName) + 1);
		return rankNumber;
	}

	/**
	 * PrisonRanksX API
	 * 
	 *  @param pathName rank's path name in rankdata.yml
	 *  @param rankName rank name
	 *  @return String rank number in ranks list
	 */
	public String getRankNumber(String pathName, String rankName) {
		List<String> collection = main.rankStorage.pathRanks.get(pathName);
		String rankNumber = String.valueOf(collection.indexOf(rankName) + 1);
		return rankNumber;
	}

	public int getRankNumberX(String pathName, String rankName) {
		List<String> collection = main.rankStorage.pathRanks.get(pathName);
		return collection.indexOf(rankName) + 1;
	}
	/**
	 * PrisonRanksX API
	 * 
	 *  @param prestigeName
	 *  @return prestige number in prestiges list ((index + 1))
	 */
	public synchronized String getPrestigeNumber(String prestigeName) {
		String prestigeNumber = "0";
		if(!main.isInfinitePrestige) {
			List<String> collection = getPrestigesCollection();
			prestigeNumber = String.valueOf(collection.indexOf(prestigeName) + 1);
		} else {
			// because infinite prestige names are always numbers, no need to do anything.
			prestigeNumber = prestigeName;
		}
		return prestigeNumber;
	}

	public synchronized int getPrestigeNumberX(String prestigeName) {
		return main.isInfinitePrestige ? Integer.valueOf(prestigeName) : getPrestigesCollection().indexOf(prestigeName) + 1;
	}

	public long getPrestigeSize() {
		return main.isInfinitePrestige ? main.infinitePrestigeSettings.getFinalPrestige() : getPrestigesCollection().size();
	}
	/**
	 * 
	 * @param player
	 * @return <i>Player prestige number starting from <b>1</b> for the first prestige, otherwise
	 * return <b>0</b> when player doesn't have a prestige
	 */
	@Nonnull
	public int getPlayerPrestigeNumber(final OfflinePlayer player) {
		if(!hasPrestiged(player)) {
			return 0;
		}
		return Integer.valueOf(getPrestigeNumber(getPlayerPrestige(player)));
	}

	/**
	 * 
	 * @param uuid
	 * @return <i>Player prestige number starting from <b>1</b> for the first prestige, otherwise
	 * return <b>0</b> when player doesn't have a prestige
	 */
	@Nonnull
	public int getPlayerPrestigeNumber(final UUID uuid) {
		if(!hasPrestiged(uuid)) {
			return 0;
		}
		return Integer.valueOf(getPrestigeNumber(getPlayerPrestige(uuid)));
	}


	public String getRebirthNumber(String rebirthName) {
		List<String> collection = getRebirthsCollection();
		String rebirthNumber = String.valueOf(collection.indexOf(rebirthName) + 1);
		return rebirthNumber;
	}

	/**
	 * 
	 * @param rebirthName
	 * @return 0, if rebirthName is null. otherwise: rebirth index + 1
	 */
	public int getRebirthNumberX(@Nullable final String rebirthName) {
		if(rebirthName == null) {
			return 0;
		}
		List<String> collection = getRebirthsCollection();
		int rebirthNumber = collection.indexOf(rebirthName) + 1;
		return rebirthNumber;
	}

	public int getPlayerRebirthNumber(OfflinePlayer player) {
		if(!hasRebirthed(player)) {
			return 0;
		}
		return Integer.valueOf(getRebirthNumber(getPlayerRebirth(player)));
	}

	public int getPlayerRebirthNumber(UUID uuid) {
		if(!hasRebirthed(uuid)) {
			return 0;
		}
		return Integer.valueOf(getRebirthNumber(getPlayerRebirth(uuid)));
	}

	/**
	 * PrisonRanksX API
	 * 
	 *  @param offlinePlayer
	 *  @return List<String> a list of rank up commands | should not be directly executed because of the prefixes {[console] [op] [player]}
	 *  use getPluginMainClass().executeCommands(...); to execute them
	 */ 
	public List<String> getPlayerRankupCommands(OfflinePlayer offlinePlayer) {
		List<String> nextRankCommands = main.rankStorage.getRankupCommands(getPlayerRankPath(offlinePlayer));
		return nextRankCommands;
	}

	public List<String> getPlayerRankupCommands(UUID uuid) {
		List<String> nextRankCommands = main.rankStorage.getRankupCommands(getPlayerRankPath(uuid));
		return nextRankCommands;
	}

	public List<String> getPlayerNextRebirthCommands(OfflinePlayer offlinePlayer) {
		List<String> nextRebirthCommands = main.rebirthStorage.getRebirthCommands(getPlayerRebirth(offlinePlayer));
		return nextRebirthCommands;
	}

	public List<String> getPlayerNextRebirthCommands(UUID uuid) {
		List<String> nextRebirthCommands = main.rebirthStorage.getRebirthCommands(getPlayerRebirth(uuid));
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
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param prestigeName
	 * @return display name of the specified prestige name
	 */
	public String getPrestigeDisplay(String prestigeName) {
		return main.prestigeStorage.getDisplayName(prestigeName);
	}


	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @param rankPath taken from rankStorage and playerStorage
	 *  @return String rank display name
	 */
	public String getRankDisplay(RankPath rankPath) {
		return main.rankStorage.getDisplayName(rankPath);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param rebirthName
	 * @return rebirth display name
	 */
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
	 * <p><i>this method is thread-safe (can be called from an Async Task).
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
	 *  @deprecated finds the correct rank path using a loop
	 */
	@Deprecated
	public String getRankCostFormatted(String rankName) {
		return formatBalance(main.rankStorage.getCost(main.rankStorage.getRankPath(rankName)));
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @param rankPath taken from rankStorage and playerStorage
	 *  @return String rank cost formatted
	 */
	public String getRankCostFormatted(RankPath rankPath) {
		return formatBalance(main.rankStorage.getCost(rankPath));
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param rebirthName
	 * @return rebirth cost formatted (10M, 1.5B, etc..)
	 */
	public String getRebirthCostFormatted(String rebirthName) {
		return formatBalance(main.rebirthStorage.getCost(rebirthName));
	}

	@Deprecated
	public String getRankup(String rankName) {
		return main.rankStorage.getRankupName(main.rankStorage.getRankPath(rankName));
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param rankPath
	 * @return rankPath next rank name
	 */
	public String getRankup(RankPath rankPath) {
		return main.rankStorage.getRankupName(rankPath);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param rebirthName
	 * @return rebirth next rebirth name
	 */
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

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param rankPath
	 * @return if rank has allow prestige option set to true
	 */
	public boolean hasAllowPrestige(RankPath rankPath) {
		return main.rankStorage.isAllowPrestige(rankPath);
	}

	@Deprecated
	public boolean hasNotAllowPrestige(String rankName) {
		if(main.rankStorage.isAllowPrestige(main.rankStorage.getRankPath(rankName)) == true) {
			return false;
		} else {
			return true;
		}
	}

	@Deprecated
	public boolean hasNotAllowPrestige(RankPath rankPath) {
		return main.rankStorage.isAllowPrestige(rankPath) == true;
	}

	/**
	 * 
	 * @return PrisonRanksX main config ((config.yml))
	 */
	public FileConfiguration getConfig() {
		return originalConfig;
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
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
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @param uuid player unique ID
	 *  @return String player prestige / returns null if hasPrestiged returns false or getPlayerPrestigeDisplay is null
	 */
	public String getPlayerPrestige(UUID uuid) {
		return main.playerStorage.getPlayerPrestige(uuid);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
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
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid player unique id
	 * @return player current prestige cost
	 */
	@Nullable
	public Double getPlayerPrestigeCost(UUID uuid) {
		if(!hasPrestiged(uuid)) {
			return null;
		}
		return main.prestigeStorage.getCost(getPlayerPrestige(uuid));
	}
	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
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

	public String getPlayerPrestigeCostInString(UUID uuid) {
		if(!hasPrestiged(uuid)) {
			return null;
		}
		return String.valueOf(main.prestigeStorage.getCost(getPlayerPrestige(uuid)));
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @param offlinePlayer OfflinePlayer
	 *  @return String player prestige's cost formatted with formatBalance / returns null if he doesn't have prestige
	 */
	public String getPlayerPrestigeCostFormatted(OfflinePlayer offlinePlayer) {
		if(!hasPrestiged(offlinePlayer)) {
			return null;
		}
		return formatBalance(main.prestigeStorage.getCost(getPlayerPrestige(offlinePlayer)));
	}

	@Nullable
	public String getPlayerPrestigeCostFormatted(UUID uuid) {
		if(!hasPrestiged(uuid)) {
			return null;
		}
		return formatBalance(main.prestigeStorage.getCost(getPlayerPrestige(uuid)));
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @return String curreny symbol from the storage (default: $)
	 */
	public String getPlaceholderAPICurrencySymbol() {
		return main.globalStorage.getStringData("PlaceholderAPI.currency-symbol");
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @return boolean whether the curreny symbol should show up behind or after the placeholder
	 */
	public boolean isCurrencySymbolBehind() {
		return main.globalStorage.getBooleanData("PlaceholderAPI.currency-symbol-behind");
	}
	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @return String percent sign from the storage (default: %)
	 */
	public String getPercentSign() {
		return main.globalStorage.getStringData("PlaceholderAPI.percent-sign");
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @return boolean whether the percent sign should show up behind or after the placeholder
	 */
	public boolean isPercentSignBehind() {
		return main.globalStorage.getBooleanData("PlaceholderAPI.percent-sign-behind");
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @param offlinePlayer
	 *  @return boolean returns true if hasPrestiged returned false / returns false if next prestige is last prestige
	 */
	public boolean hasNextPrestige(OfflinePlayer offlinePlayer) {
		if(!hasPrestiged(offlinePlayer)) {
			return true;
		}
		String currentPrestige = main.playerStorage.getPlayerPrestige(offlinePlayer);
		String nextPrestige = main.prestigeStorage.getNextPrestigeName(currentPrestige);
		if(main.isInfinitePrestige) {
			if(currentPrestige.equalsIgnoreCase(String.valueOf(main.infinitePrestigeSettings.getFinalPrestige())))
				return false;
		}
		if(nextPrestige.equalsIgnoreCase("LASTPRESTIGE")) {
			return false;
		}
		return true;
	}

	public boolean hasNextPrestige(UUID uuid) {
		if(!hasPrestiged(uuid)) {
			return true;
		}
		String currentPrestige = main.playerStorage.getPlayerPrestige(uuid);
		String nextPrestige = main.prestigeStorage.getNextPrestigeName(currentPrestige);
		if(nextPrestige.equalsIgnoreCase("LASTPRESTIGE")) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param offlinePlayer
	 * @return true if player has a rebirth
	 */
	public boolean hasRebirthed(OfflinePlayer offlinePlayer) {
		return main.playerStorage.getPlayerRebirth(offlinePlayer) != null;
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid
	 * @return true if player has a rebirth
	 */
	public boolean hasRebirthed(UUID uuid) {
		return main.playerStorage.getPlayerRebirth(uuid) != null;
	}

	public boolean hasNextRebirth(OfflinePlayer offlinePlayer) {
		if(!hasRebirthed(offlinePlayer)) {
			return true;
		}
		String currentRebirth = main.playerStorage.getPlayerRebirth(offlinePlayer);
		String nextRebirth = main.rebirthStorage.getNextRebirthName(currentRebirth);
		return !nextRebirth.equalsIgnoreCase("LASTREBIRTH");
	}

	public boolean hasNextRebirth(UUID uuid) {
		if(!hasRebirthed(uuid)) {
			return true;
		}
		String currentRebirth = main.playerStorage.getPlayerRebirth(uuid);
		String nextRebirth = main.rebirthStorage.getNextRebirthName(currentRebirth);
		return !nextRebirth.equalsIgnoreCase("LASTREBIRTH");
	}
	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @param offlinePlayer
	 *  @return String returns first prestige name if hasPrestiged returned false, returns null if he is at the latest prestige
	 */
	@Nullable
	public String getPlayerNextPrestige(OfflinePlayer offlinePlayer) {
		if(!hasPrestiged(offlinePlayer)) {
			return main.globalStorage.getStringData("firstprestige");
		}
		String currentPrestige = main.playerStorage.getPlayerPrestige(offlinePlayer);
		String nextPrestige = main.prestigeStorage.getNextPrestigeName(currentPrestige);
		if(main.isInfinitePrestige) {
			if(currentPrestige.equalsIgnoreCase(String.valueOf(main.infinitePrestigeSettings.getFinalPrestige())))
				return null;
		}
		if(nextPrestige.equalsIgnoreCase("LASTPRESTIGE")) {
			return null;
		}
		return nextPrestige;
	}

	@Nonnull
	public String getPlayerNextPrestige(UUID uuid) {
		if(!hasPrestiged(uuid)) {
			return main.globalStorage.getStringData("firstprestige");
		}
		String currentPrestige = main.playerStorage.getPlayerPrestige(uuid);
		String nextPrestige = main.prestigeStorage.getNextPrestigeName(currentPrestige);
		return nextPrestige;
	}
	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @param offlinePlayer
	 *  @return String returns first rebirth name if hasRebirthed returned false
	 */
	@Nonnull
	public String getPlayerNextRebirth(OfflinePlayer offlinePlayer) {
		if(!hasRebirthed(offlinePlayer)) {
			return main.globalStorage.getStringData("firstrebirth");
		}
		String currentRebirth = main.playerStorage.getPlayerRebirth(offlinePlayer);
		String nextRebirth = main.rebirthStorage.getNextRebirthName(currentRebirth);
		return nextRebirth;
	}

	@Nonnull
	public String getPlayerNextRebirth(UUID uuid) {
		if(!hasRebirthed(uuid)) {
			return main.globalStorage.getStringData("firstrebirth");
		}
		String currentRebirth = main.playerStorage.getPlayerRebirth(uuid);
		String nextRebirth = main.rebirthStorage.getNextRebirthName(currentRebirth);
		return nextRebirth;
	}
	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @return String the first prestige
	 */
	public String getFirstPrestige() {
		return main.globalStorage.getStringData("firstprestige");
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @return first rebirth name from the storage
	 */
	public String getFirstRebirth() {
		return main.globalStorage.getStringData("firstrebirth");
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
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
	
	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @param offlinePlayer
	 *  @return returns placeholderapi fallback from the storage if hasPrestiged or hasNextPrestige returned false
	 */
	public String getPlayerNextPrestigeDisplayR(OfflinePlayer offlinePlayer) {
		if(!hasNextPrestige(offlinePlayer)) {
			return c(main.globalStorage.getStringData("PlaceholderAPI.prestige-lastprestige"));
		}
		if(!hasPrestiged(offlinePlayer)) {
			return c(getPrestigeDisplay(getFirstPrestige()));
		}
		String nextPrestigeDisplay = main.prestigeStorage.getNextPrestigeDisplayName(getPlayerPrestige(offlinePlayer));
		return nextPrestigeDisplay;
	}

	public String getPlayerNextPrestigeDisplay(UUID uuid) {
		if(!hasPrestiged(uuid)) {
			return main.globalStorage.getStringData("PlaceholderAPI.nextprestige-notprestiged");
		}
		if(!hasNextPrestige(uuid)) {
			return c(main.globalStorage.getStringData("PlaceholderAPI.prestige-lastprestige"));
		}

		String nextPrestigeDisplay = main.prestigeStorage.getNextPrestigeDisplayName(getPlayerPrestige(uuid));
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

	public String getPlayerNextRebirthDisplay(UUID uuid) {
		if(!hasRebirthed(uuid)) {
			return main.globalStorage.getStringData("PlaceholderAPI.nextrebirth-notrebirthed");
		}
		if(!hasNextRebirth(uuid)) {
			return c(main.globalStorage.getStringData("PlaceholderAPI.rebirth-lastrebirth"));
		}
		String nextRebirthDisplay = main.rebirthStorage.getNextRebirthDisplayName(getPlayerRebirth(uuid));
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

	public String getPlayerNextPrestigeDisplayNoFallback(UUID uuid) {
		if(!hasPrestiged(uuid)) {
			return main.prestigeStorage.getDisplayName(getFirstPrestige());
		}
		if(!hasNextPrestige(uuid)) {
			return null;
		}
		return main.prestigeStorage.getNextPrestigeDisplayName(getPlayerPrestige(uuid));
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

	public String getPlayerNextRebirthDisplayNoFallback(UUID uuid) {
		if(!hasRebirthed(uuid)) {
			return main.rebirthStorage.getDisplayName(getFirstRebirth());
		}
		if(!hasNextRebirth(uuid)) {
			return null;
		}
		return main.rebirthStorage.getNextRebirthDisplayName(getPlayerRebirth(uuid));
	}

	public String getPlayerNextPrestigeDisplayNoFallbackR(OfflinePlayer offlinePlayer) {
		if(!hasPrestiged(offlinePlayer)) {
			return c(main.prestigeStorage.getDisplayName(getFirstPrestige()));
		}
		if(!hasNextPrestige(offlinePlayer)) {
			return null;
		}
		return c(main.prestigeStorage.getNextPrestigeDisplayName(getPlayerPrestige(offlinePlayer)));
	}

	public String getPlayerNextPrestigeDisplayNoFallbackR(UUID uuid) {
		if(!hasPrestiged(uuid)) {
			return c(main.prestigeStorage.getDisplayName(getFirstPrestige()));
		}
		if(!hasNextPrestige(uuid)) {
			return null;
		}
		return c(main.prestigeStorage.getNextPrestigeDisplayName(getPlayerPrestige(uuid)));
	}

	public String getPlayerNextRebirthDisplayNoFallbackR(OfflinePlayer offlinePlayer) {
		if(!hasRebirthed(offlinePlayer)) {
			return c(main.rebirthStorage.getDisplayName(getFirstRebirth()));
		}
		if(!hasNextRebirth(offlinePlayer)) {
			return null;
		}
		return c(main.rebirthStorage.getNextRebirthDisplayName(getPlayerRebirth(offlinePlayer)));
	}

	public String getPlayerNextRebirthDisplayNoFallbackR(UUID uuid) {
		if(!hasRebirthed(uuid)) {
			return main.rebirthStorage.getDisplayName(getFirstRebirth());
		}
		if(!hasNextRebirth(uuid)) {
			return null;
		}
		return c(main.rebirthStorage.getNextRebirthDisplayName(getPlayerRebirth(uuid)));
	}

	public double getPlayerNextPrestigeCost(OfflinePlayer offlinePlayer) {
		if(!hasPrestiged(offlinePlayer)) {
			return main.prestigeStorage.getCost(getFirstPrestige());
		}
		double nextPrestigeCost = main.prestigeStorage.getNextPrestigeCost(getPlayerPrestige(offlinePlayer));
		return nextPrestigeCost;
	}

	public double getPlayerNextPrestigeCost(UUID uuid) {
		if(!hasPrestiged(uuid)) {
			return main.prestigeStorage.getCost(getFirstPrestige());
		}
		double nextPrestigeCost = main.prestigeStorage.getNextPrestigeCost(getPlayerPrestige(uuid));
		return nextPrestigeCost;
	}

	public double getPlayerNextRebirthCost(OfflinePlayer offlinePlayer) {
		if(!hasRebirthed(offlinePlayer)) {
			return main.rebirthStorage.getCost(getFirstRebirth());
		}
		double nextRebirthCost = main.rebirthStorage.getNextRebirthCost(getPlayerRebirth(offlinePlayer));
		return nextRebirthCost;
	}

	public double getPlayerNextRebirthCost(UUID uuid) {
		if(!hasRebirthed(uuid)) {
			return main.rebirthStorage.getCost(getFirstRebirth());
		}
		double nextRebirthCost = main.rebirthStorage.getNextRebirthCost(getPlayerRebirth(uuid));
		return nextRebirthCost;
	}

	public String getPlayerNextPrestigeCostFormatted(OfflinePlayer offlinePlayer) {
		return getPluginMainClass().formatBalance(getPlayerNextPrestigeCost(offlinePlayer));
	}

	public String getPlayerNextPrestigeCostFormatted(UUID uuid) {
		return getPluginMainClass().formatBalance(getPlayerNextPrestigeCost(uuid));
	}

	public String getPlayerNextRebirthCostFormatted(OfflinePlayer offlinePlayer) {
		return getPluginMainClass().formatBalance(getPlayerNextRebirthCost(offlinePlayer));
	}

	public String getPlayerNextRebirthCostFormatted(UUID uuid) {
		return getPluginMainClass().formatBalance(getPlayerNextRebirthCost(uuid));
	}

	public  String getPlayerNextPrestigeCostInString(OfflinePlayer offlinePlayer) {
		if(!hasPrestiged(offlinePlayer)) {
			return String.valueOf(main.prestigeStorage.getCost(getFirstPrestige()));
		}
		Double nextPrestigeCost = main.prestigeStorage.getNextPrestigeCost(getPlayerPrestige(offlinePlayer));
		return String.valueOf(nextPrestigeCost);
	}

	public  String getPlayerNextPrestigeCostInString(UUID uuid) {
		if(!hasPrestiged(uuid)) {
			return String.valueOf(main.prestigeStorage.getCost(getFirstPrestige()));
		}
		Double nextPrestigeCost = main.prestigeStorage.getNextPrestigeCost(getPlayerPrestige(uuid));
		return String.valueOf(nextPrestigeCost);
	}

	public boolean hasPrestigeFirework(OfflinePlayer offlinePlayer) {
		if(main.prestigeStorage.isSendFirework(getPlayerPrestige(offlinePlayer))) {
			return true;
		}
		return false;
	}

	public  boolean hasPrestigeFirework(UUID uuid) {
		if(main.prestigeStorage.isSendFirework(getPlayerPrestige(uuid))) {
			return true;
		}
		return false;
	}

	public String getPlayerPrestigeDisplay(OfflinePlayer offlinePlayer) {
		return main.prestigeStorage.getDisplayName(getPlayerPrestige(offlinePlayer));
	}

	public String getPlayerPrestigeDisplay(UUID uuid) {
		return main.prestigeStorage.getDisplayName(getPlayerPrestige(uuid));
	}

	public String getPlayerPrestigeDisplayR(OfflinePlayer offlinePlayer) {
		return c(main.prestigeStorage.getDisplayName(getPlayerPrestige(offlinePlayer)));
	}

	public String getPlayerPrestigeDisplayR(UUID uuid) {
		return c(main.prestigeStorage.getDisplayName(getPlayerPrestige(uuid)));
	}

	public boolean hasPrestiged(OfflinePlayer offlinePlayer) {
		if(main.playerStorage.getPlayerPrestige(offlinePlayer) == null) {
			return false;
		}
		return true;
	}

	public boolean hasPrestiged(UUID uuid) {
		if(main.playerStorage.getPlayerPrestige(uuid) == null) {
			return false;
		}
		return true;
	}


	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param offlinePlayer
	 * @param rankName
	 */
	public void setPlayerRank(OfflinePlayer offlinePlayer, String rankName) {
		main.playerStorage.setPlayerRank(offlinePlayer, rankName);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid
	 * @param rankName
	 */
	public void setPlayerRank(UUID uuid, String rankName) {
		main.playerStorage.setPlayerRank(uuid, rankName);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid
	 * @param rank
	 */
	public void setPlayerRank(UUID uuid, RankDataHandler rank) {
		main.playerStorage.setPlayerRank(uuid, rank.getName());
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param offlinePlayer
	 * @param prestigeName
	 */
	public void setPlayerPrestige(OfflinePlayer offlinePlayer, String prestigeName) {
		main.playerStorage.setPlayerPrestige(offlinePlayer, prestigeName);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid
	 * @param prestigeName
	 */
	public void setPlayerPrestige(UUID uuid, String prestigeName) {
		main.playerStorage.setPlayerPrestige(uuid, prestigeName);
	}

	public void deletePlayerPrestige(UUID uuid) {
		main.playerStorage.setPlayerPrestige(uuid, null);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid
	 * @param prestige
	 */
	public void setPlayerPrestige(UUID uuid, PrestigeDataHandler prestige) {
		main.playerStorage.setPlayerPrestige(uuid, prestige.getName());
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param offlinePlayer
	 * @param rebirthName
	 */
	public void setPlayerRebirth(OfflinePlayer offlinePlayer, String rebirthName) {
		main.playerStorage.setPlayerRebirth(offlinePlayer, rebirthName);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid
	 * @param rebirthName
	 */
	public void setPlayerRebirth(UUID uuid, String rebirthName) {
		main.playerStorage.setPlayerRebirth(uuid, rebirthName);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid
	 * @param rebirth
	 */
	public void setPlayerRebirth(UUID uuid, RebirthDataHandler rebirth) {
		main.playerStorage.setPlayerRebirth(uuid, rebirth.getName());
	}

	public void deletePlayerRebirth(UUID uuid) {
		main.playerStorage.setPlayerRebirth(uuid, null);
	}
	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param offlinePlayer
	 * @param pathName
	 */
	public void setPlayerPath(OfflinePlayer offlinePlayer, String pathName) {
		main.playerStorage.setPlayerPath(offlinePlayer, pathName);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid
	 * @param pathName
	 */
	public void setPlayerPath(UUID uuid, String pathName) {
		main.playerStorage.setPlayerPath(uuid, pathName);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param offlinePlayer
	 * @param rankPath
	 */
	public void setPlayerRankPath(OfflinePlayer offlinePlayer, RankPath rankPath) {
		main.playerStorage.setPlayerRank(offlinePlayer, rankPath);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid
	 * @param rankPath
	 */
	public void setPlayerRankPath(UUID uuid, RankPath rankPath) {
		main.playerStorage.setPlayerRankPath(uuid, rankPath);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param prestigeName
	 * @return
	 */
	public double getRankupCostIncreasePercentage(String prestigeName) {
		int prestigeNumber = Integer.valueOf(getPrestigeNumber(prestigeName));
		double rankupCostIncrease = main.globalStorage.getDoubleData("PrestigeOptions.rankup_cost_increase_percentage");
		if(rankupCostIncrease > 0) {
			if(increaseType.equalsIgnoreCase("DEFAULT")) {
				return rankupCostIncrease * prestigeNumber;
			} else if (increaseType.equalsIgnoreCase("POWER")) {
				return Math.pow(rankupCostIncrease, prestigeNumber + 1);
			} else if (increaseType.equalsIgnoreCase("EXTRA")) {
				return (rankupCostIncrease * prestigeNumber) * 2;
			} else if (increaseType.equalsIgnoreCase("CUSTOM")) {
				return (main.prxAPI.numberAPI.calculate(prestigeIncreaseExpression
						.replace("{cost_increase}", String.valueOf(rankupCostIncrease))
						.replace("{prestigenumber}", String.valueOf(prestigeNumber))
						.replace("{rankcost}", String.valueOf("1"))
						));
			}
			return rankupCostIncrease * prestigeNumber;
		}
		if(main.prestigeStorage.getRankupCostIncreasePercentage(prestigeName) <= 0) {
			return 0.0;
		} else {
			return main.prestigeStorage.getRankupCostIncreasePercentage(prestigeName);
		}
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param rebirthName
	 * @return
	 */
	public double getPrestigeCostIncreasePercentage(final String rebirthName) {
		int rebirthNumber = Integer.valueOf(getRebirthNumber(rebirthName));
		double prestigeCostIncrease = main.globalStorage.getDoubleData("RebirthOptions.prestige_cost_increase_percentage");
		if(prestigeCostIncrease > 0) {
			if(rebirthIncreaseType.equalsIgnoreCase("DEFAULT")) {
				return prestigeCostIncrease * rebirthNumber;
			} else if (rebirthIncreaseType.equalsIgnoreCase("POWER")) {
				return Math.pow(prestigeCostIncrease, rebirthNumber);
			} else if (rebirthIncreaseType.equalsIgnoreCase("EXTRA")) {
				return (prestigeCostIncrease * rebirthNumber) * 2;
			}
			return prestigeCostIncrease * rebirthNumber;
		}
		if(main.rebirthStorage.getPrestigeCostIncreasePercentage(rebirthName) <= 0) {
			return 0.0;
		} else {
			return main.rebirthStorage.getPrestigeCostIncreasePercentage(rebirthName);
		}
	}

	@Deprecated
	public Double getIncreasedRankupCost(String prestigeName, String rankName) {
		Double eff = getRankCost(main.rankStorage.getRankPath(rankName));
		Double inc = eff / 100;
		Double afterinc = null;
		afterinc = inc * getRankupCostIncreasePercentage(prestigeName);
		return afterinc;
	}

	public double getIncreasedRankupCost(final String prestigeName, final RankPath rankPath) {
		double eff = getRankCost(rankPath);
		if(prestigeName == null || prestigeName.equalsIgnoreCase("null")) {
			return eff;
		}
		double inc = eff / 100;
		double afterinc;
		afterinc = inc * getRankupCostIncreasePercentage(prestigeName);
		return afterinc;
	}

	public double getIncreasedPrestigeCostDummy(final String rebirthName, final String prestigeName) {
		double eff = getPrestigeCost(prestigeName);
		if(rebirthName == null || rebirthName.equalsIgnoreCase("none")) {
			return eff;
		}
		double inc = eff / 100;
		double afterinc;
		afterinc = inc * getPrestigeCostIncreasePercentage(rebirthName);
		return afterinc;
	}

	@Deprecated
	public Double getIncreasedRankupCostNB(String prestigeName, String rankName) {
		if(prestigeName == null || prestigeName.equalsIgnoreCase("null")) {
			return 0.0;
		}
		Double eff = getRankCost(main.rankStorage.getRankPath(rankName));
		Double inc = eff / 100;
		Double afterinc = null;
		afterinc = inc * getRankupCostIncreasePercentage(prestigeName);
		return afterinc;
	}

	/**
	 * 
	 * @param prestigeName
	 * @param rankPath
	 * @return rankup cost with prestige increase applied | returns only rankup cost if no prestige
	 */
	public double getIncreasedRankupCostNB(final String prestigeName, final RankPath rankPath) {
		double eff = getRankCost(rankPath);
		if(prestigeName == null || prestigeName.equalsIgnoreCase("null")) {
			return eff;
		}
		double inc = eff / 100;
		double afterinc;
		afterinc = inc * getRankupCostIncreasePercentage(prestigeName);
		return afterinc;
	}

	public double getIncreasedPrestigeCostNB(final String rebirthName, final String prestigeName) {
		double eff = getPrestigeCost(prestigeName);
		if(rebirthName == null || rebirthName.equalsIgnoreCase("null")) {
			return eff;
		}
		double inc = eff / 100;
		double afterinc;
		afterinc = inc * getPrestigeCostIncreasePercentage(rebirthName);
		return afterinc;
	}

	/**
	 * Recommended Method
	 * @param prestigeName
	 * @param rankCost
	 * @return rankup cost with prestige increase applied | returns only rankup cost if no prestige
	 */
	public double getIncreasedRankupCost(final String prestigeName, final Double rankCost) {
		if(prestigeName == null || prestigeName.equalsIgnoreCase("null") || getRankupCostIncreasePercentage(prestigeName) <= 0) {
			return rankCost;
		}
		Double afterinc = main.prxAPI.numberAPI.calculate(prestigeIncreaseExpression
				.replace("{cost_increase}", String.valueOf(getRankupCostIncreasePercentage(prestigeName)))
				.replace("{prestigenumber}", String.valueOf(getPrestigeNumber(prestigeName)))
				.replace("{rankcost}", String.valueOf(main.prxAPI.numberAPI.deleteScientificNotationA(rankCost)))
				.replace("{ranknumber}", String.valueOf(1))
				);
		if(afterinc.isNaN()) {
			return 0.0;
		}
		if(afterinc <= 0.0) {
			return 0.0;
		}
		return afterinc;
	}

	public double getIncreasedRankupCostX(final String prestigeName, final RankPath rankPath) {
		if(prestigeName == null || prestigeName.equalsIgnoreCase("null") || getRankupCostIncreasePercentage(prestigeName) <= 0) {
			return getRankCost(rankPath);
		}
		Double afterinc = main.prxAPI.numberAPI.calculate(prestigeIncreaseExpression
				.replace("{cost_increase}", String.valueOf(getRankupCostIncreasePercentage(prestigeName)))
				.replace("{prestigenumber}", String.valueOf(getPrestigeNumber(prestigeName)))
				.replace("{rankcost}", String.valueOf(main.prxAPI.numberAPI.deleteScientificNotationA(getRankCost(rankPath))))
				.replace("{ranknumber}", String.valueOf(1))
				);
		if(afterinc.isNaN()) {
			return 0.0;
		}
		if(afterinc <= 0.0) {
			return 0.0;
		}
		return afterinc;
	}

	public double getIncreasedRankupCostX(final String rebirthName, final String prestigeName, final RankPath rankPath) {
		if(prestigeName == null || prestigeName.equalsIgnoreCase("null") || getRankupCostIncreasePercentage(prestigeName) <= 0) {
			return getRankCost(rankPath);
		}
		Double afterinc = main.prxAPI.numberAPI.calculate(prestigeIncreaseExpression
				.replace("{cost_increase}", String.valueOf(getRankupCostIncreasePercentage(prestigeName)))
				.replace("{prestigenumber}", String.valueOf(getPrestigeNumber(prestigeName)))
				.replace("{rebirthnumber}", String.valueOf(getRebirthNumberX(rebirthName)))
				.replace("{rankcost}", String.valueOf(main.prxAPI.numberAPI.deleteScientificNotationA(getRankCost(rankPath))))
				.replace("{ranknumber}", String.valueOf(1))
				);
		if(afterinc.isNaN()) {
			return 0.0;
		}
		if(afterinc <= 0.0) {
			return 0.0;
		}
		return afterinc;
	}

	public double getIncreasedRankupCostX(final String rebirthName, final String prestigeName, final Double rankCost) {
		if(prestigeName == null || prestigeName.equalsIgnoreCase("null") || getRankupCostIncreasePercentage(prestigeName) <= 0) {
			return rankCost;
		}
		Double afterinc = main.prxAPI.numberAPI.calculate(prestigeIncreaseExpression
				.replace("{cost_increase}", String.valueOf(getRankupCostIncreasePercentage(prestigeName)))
				.replace("{prestigenumber}", String.valueOf(getPrestigeNumber(prestigeName)))
				.replace("{rebirthnumber}", String.valueOf(getRebirthNumberX(rebirthName)))
				.replace("{rankcost}", String.valueOf(main.prxAPI.numberAPI.deleteScientificNotationA(rankCost)))
				.replace("{ranknumber}", String.valueOf(1))
				);
		if(afterinc.isNaN()) {
			return 0.0;
		}
		if(afterinc <= 0.0) {
			return 0.0;
		}
		return afterinc;
	}

	/**
	 * Recommended Method
	 * @param rebirthName
	 * @param prestigeCost
	 * @return prestige cost with rebirth increase applied | returns only prestige cost if no rebirth
	 */
	public double getIncreasedPrestigeCost(final String rebirthName, final double prestigeCost) {
		if(rebirthName == null || rebirthName.equalsIgnoreCase("null") || getPrestigeCostIncreasePercentage(rebirthName) <= 0) {
			return prestigeCost;
		}
		Double afterinc = main.prxAPI.numberAPI.calculate(rebirthIncreaseExpression
				.replace("{cost_increase}", String.valueOf(getPrestigeCostIncreasePercentage(rebirthName)))
				.replace("{rebirthnumber}", String.valueOf(getRebirthNumber(rebirthName)))
				.replace("{prestigecost}", String.valueOf(main.prxAPI.numberAPI.deleteScientificNotationA(prestigeCost)))
				.replace("{ranknumber}", String.valueOf(1))
				);
		if(afterinc.isNaN()) {
			return 0.0;
		}
		if(afterinc <= 0.0) {
			return 0.0;
		}
		return afterinc;
	}

	/**
	 * Recommended Method
	 * @param rebirthName
	 * @param prestigeCost
	 * @return prestige cost with rebirth increase applied | returns only prestige cost if no rebirth
	 */
	public double getIncreasedPrestigeCost(final String rebirthName, final String prestige) {
		double prestigeCost = this.getPrestigeCost(prestige);
		if(rebirthName == null || rebirthName.equalsIgnoreCase("null") || getPrestigeCostIncreasePercentage(rebirthName) <= 0) {
			return prestigeCost;
		}
		Double afterinc = main.prxAPI.numberAPI.calculate(rebirthIncreaseExpression
				.replace("{cost_increase}", String.valueOf(getPrestigeCostIncreasePercentage(rebirthName)))
				.replace("{rebirthnumber}", String.valueOf(getRebirthNumber(rebirthName)))
				.replace("{prestigecost}", String.valueOf(main.prxAPI.numberAPI.deleteScientificNotationA(prestigeCost)))
				.replace("{ranknumber}", String.valueOf(1))
				);
		if(afterinc.isNaN()) {
			return 0.0;
		}
		if(afterinc <= 0.0) {
			return 0.0;
		}
		return afterinc;
	}

	public boolean hasLastRank(OfflinePlayer offlinePlayer) {
		return main.rankStorage.isSetToLastRank(getPlayerRank(offlinePlayer));
	}

	public boolean isLastRank(OfflinePlayer offlinePlayer) {
		return main.rankStorage.isLastRank(getPlayerRankPath(offlinePlayer));
	}

	public boolean isLastRank(UUID uuid) {
		return main.rankStorage.isLastRank(getPlayerRankPath(uuid));
	}

	public boolean isLastRank(RankPath rankPath) {
		return main.rankStorage.isLastRank(rankPath);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param offlinePlayer
	 */
	public void resetPlayerRank(OfflinePlayer offlinePlayer) {
		main.playerStorage.setPlayerRank(offlinePlayer, main.globalStorage.getStringData("defaultrank"));
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param offlinePlayer
	 * @param pathCheck
	 */
	public void resetPlayerRank(OfflinePlayer offlinePlayer, boolean pathCheck) {
		List<String> pathRanks = main.rankStorage.getPathRanksMap().get(this.getPlayerRankPath(offlinePlayer).getPathName());
		main.playerStorage.setPlayerRank(offlinePlayer, pathRanks.get(pathRanks.size() - 1));
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid
	 */
	public void resetPlayerRank(UUID uuid) {
		List<String> pathRanks = main.rankStorage.getPathRanksMap().get(this.getPlayerRankPath(uuid).getPathName());
		main.playerStorage.setPlayerRank(uuid, pathRanks.get(pathRanks.size() - 1));
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @param offlinePlayer
	 *  @return Double made for placeholderapi | returns 0.0 if getPlayerNextRank is null
	 */
	public Double getPlayerRankupCostWithIncreaseDirect(final OfflinePlayer offlinePlayer) {
		if(getPlayerNextRank(offlinePlayer) == null) {
			return 0.0;
		}
		RankPath rp = RankPath.getRankPath(getPlayerNextRank(offlinePlayer), main.playerStorage.getPlayerPath(offlinePlayer));
		Double nextRankCost = getIncreasedRankupCostX(getPlayerRebirth(offlinePlayer), getPlayerPrestige(offlinePlayer), getRankCost(rp));	
		return Double.valueOf(nextRankCost);
	}

	public Double getPlayerRankCostWithIncreaseDirect(final OfflinePlayer offlinePlayer, final RankPath rankPath) {
		if(rankPath == null) {
			return 0.0;
		}
		RankPath rp = rankPath;
		Double newRankCost = getIncreasedRankupCostX(getPlayerRebirth(offlinePlayer), getPlayerPrestige(offlinePlayer), getRankCost(rp));	
		return Double.valueOf(newRankCost);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param player
	 * @return rank and/or prestige and/or rebirth display name if player has any>>>>>>>>>>>>>>>>>>>>>>
	 *  [\rebirth/] [\prestige/] <\rank/> || [] = optional, <> = must be returned
	 */
	public String getStageDisplay(final Player player, final String spaceChar) {
		if(this.hasRebirthed(player)) {
			if(this.hasPrestiged(player)) {
				return this.getPlayerRebirthDisplay(player) + spaceChar + this.getPlayerPrestigeDisplay(player)
				+ spaceChar + this.getPlayerRankDisplay(player);
			} else {
				return this.getPlayerRebirthDisplay(player) + spaceChar + this.getPlayerRankDisplay(player);
			}
		} else {
			if(this.hasPrestiged(player)) {
				return this.getPlayerPrestigeDisplay(player) + spaceChar + this.getPlayerRankDisplay(player);
			} else {
				return this.getPlayerRankDisplay(player);
			}
		}
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param player
	 * @param spaceChar
	 * @return
	 */
	public String getPrestigeAndRebirthDisplay(final UUID player, final String spaceChar) {
		if(!main.getPlayerStorage().isRegistered(player)) return "";
		if(this.hasRebirthed(player)) {
			if(this.hasPrestiged(player)) {
				return this.getPlayerPrestigeDisplay(player) + spaceChar + this.getPlayerRebirthDisplay(player);
			} else {
				return this.getPlayerRebirthDisplay(player);
			}
		} else {
			if(this.hasPrestiged(player)) {
				return this.getPlayerPrestigeDisplay(player);
			} else {
				return this.getPlayerRankDisplay(player);
			}
		}
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid
	 * @param spaceChar
	 * @return
	 */
	public String getStageDisplay(final UUID uuid, final String spaceChar) {
		if(this.hasRebirthed(uuid)) {
			if(this.hasPrestiged(uuid)) {
				return this.getPlayerRebirthDisplay(uuid) + spaceChar + this.getPlayerPrestigeDisplay(uuid)
				+ spaceChar + this.getPlayerRankDisplay(uuid);
			} else {
				return this.getPlayerRebirthDisplay(uuid) + spaceChar + this.getPlayerRankDisplay(uuid);
			}
		} else {
			if(this.hasPrestiged(uuid)) {
				return this.getPlayerPrestigeDisplay(uuid) + spaceChar + this.getPlayerRankDisplay(uuid);
			} else {
				return this.getPlayerRankDisplay(uuid);
			}
		}
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid
	 * @param spaceChar
	 * @return
	 */
	public String getStageName(final UUID uuid, final String spaceChar) {
		if(this.hasRebirthed(uuid)) {
			if(this.hasPrestiged(uuid)) {
				return this.getPlayerRebirth(uuid) + spaceChar + this.getPlayerPrestige(uuid)
				+ spaceChar + this.getPlayerRank(uuid);
			} else {
				return this.getPlayerRebirth(uuid) + spaceChar + this.getPlayerRank(uuid);
			}
		} else {
			if(this.hasPrestiged(uuid)) {
				return this.getPlayerPrestige(uuid) + spaceChar + this.getPlayerRank(uuid);
			} else {
				return this.getPlayerRank(uuid);
			}
		}
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param player
	 * @return rank and/or prestige and/or rebirth display name if player has any>>>>>>>>>>>>>>>>>>>>>>
	 *  [\rebirth/] [\prestige/] <\rank/> || [] = optional, <> = must be returned
	 */
	public String getStageDisplay(final Player player, final String spaceChar, boolean trimFormat) {
		String formatReset = c("&r");
		if(this.hasRebirthed(player)) {
			if(this.hasPrestiged(player)) {
				return this.getPlayerRebirthDisplay(player) + formatReset + spaceChar + this.getPlayerPrestigeDisplay(player)
				+ formatReset +spaceChar + this.getPlayerRankDisplay(player);
			} else {
				return this.getPlayerRebirthDisplay(player) + formatReset + spaceChar + this.getPlayerRankDisplay(player);
			}
		} else {
			if(this.hasPrestiged(player)) {
				return this.getPlayerPrestigeDisplay(player) + formatReset + spaceChar + this.getPlayerRankDisplay(player);
			} else {
				return this.getPlayerRankDisplay(player);
			}
		}
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid
	 * @param spaceChar
	 * @param trimFormat
	 * @return
	 */
	public String getStageDisplay(final UUID uuid, final String spaceChar, boolean trimFormat) {
		String formatReset = c("&r");
		if(this.hasRebirthed(uuid)) {
			if(this.hasPrestiged(uuid)) {
				return this.getPlayerRebirthDisplay(uuid) + formatReset + spaceChar + this.getPlayerPrestigeDisplay(uuid)
				+ formatReset +spaceChar + this.getPlayerRankDisplay(uuid);
			} else {
				return this.getPlayerRebirthDisplay(uuid) + formatReset + spaceChar + this.getPlayerRankDisplay(uuid);
			}
		} else {
			if(this.hasPrestiged(uuid)) {
				return this.getPlayerPrestigeDisplay(uuid) + formatReset + spaceChar + this.getPlayerRankDisplay(uuid);
			} else {
				return this.getPlayerRankDisplay(uuid);
			}
		}
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param rank, path, prestige, rebirth
	 * @param spaceChar
	 * @param trimFormat
	 * @return
	 */
	public String organizeStageDisplay(final String rank, final String path, final String prestige, final String rebirth, final String spaceChar, boolean trimFormat) {
		String formatReset = c("&r");
		if(rebirth != null) {
			if(prestige != null) {
				return this.getRebirthDisplay(rebirth) + formatReset + spaceChar + this.getPrestigeDisplay(prestige)
				+ formatReset +spaceChar + this.getRankDisplay(RankPath.getRankPath(rank, path));
			} else {
				return this.getRebirthDisplay(rebirth) + formatReset + spaceChar + this.getRankDisplay(RankPath.getRankPath(rank, path));
			}
		} else {
			if(prestige != null) {
				return this.getPrestigeDisplay(prestige) + formatReset + spaceChar + this.getRankDisplay(RankPath.getRankPath(rank, path));
			} else {
				return this.getRankDisplay(RankPath.getRankPath(rank, path));
			}
		}
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param rankPath
	 * @return
	 */
	public Map<String, String> getRankStringRequirements(RankPath rankPath) {
		return main.rankStorage.getDataHandler(rankPath.get()).getStringRequirements();
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param rankPath
	 * @return
	 */
	public Map<String, Double> getRankNumberRequirements(RankPath rankPath) {
		return main.rankStorage.getDataHandler(rankPath.get()).getNumberRequirements();
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param rankPath
	 * @return
	 */
	public List<String> getRankCustomRequirementMessage(RankPath rankPath) {
		return main.rankStorage.getDataHandler(rankPath.get()).getCustomRequirementMessage();
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param prestigeName
	 * @return
	 */
	public Map<String, String> getPrestigeStringRequirements(String prestigeName) {
		return main.prestigeStorage.getHandler(prestigeName).getStringRequirements();
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param prestigeName
	 * @return
	 */
	public Map<String, Double> getPrestigeNumberRequirements(String prestigeName) {
		return main.prestigeStorage.getHandler(prestigeName).getNumberRequirements();
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param prestigeName
	 * @return
	 */
	public List<String> getPrestigeCustomRequirementMessage(String prestigeName) {
		return main.prestigeStorage.getHandler(prestigeName).getCustomRequirementMessage();
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param rebirthName
	 * @return
	 */
	public Map<String, String> getRebirthStringRequirements(String rebirthName) {
		return main.rebirthStorage.getDataHandler(rebirthName).getStringRequirements();
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param rebirthName
	 * @return
	 */
	public Map<String, Double> getRebirthNumberRequirements(String rebirthName) {
		return main.rebirthStorage.getDataHandler(rebirthName).getNumberRequirements();
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param rebirthName
	 * @return
	 */
	public List<String> getRebirthCustomRequirementMessage(String rebirthName) {
		return main.rebirthStorage.getDataHandler(rebirthName).getCustomRequirementMessage();
	}

	/**
	 * 
	 * @param offlinePlayer
	 * @return 0.0 if something goes wrong
	 */
	@Nonnull
	public Double getPlayerNextPrestigeCostWithIncreaseDirect(final OfflinePlayer offlinePlayer) {
		if(getPlayerNextPrestige(offlinePlayer) == null) {
			return 0.0;
		}
		String prestigeName = getPlayerNextPrestige(offlinePlayer);
		if(prestigeName.equalsIgnoreCase("LASTPRESTIGE")) {
			return 0.0;
		}
		Double nextPrestigeCost = getIncreasedPrestigeCost(getPlayerRebirth(offlinePlayer), getPrestigeCost(prestigeName));
		return Double.valueOf(nextPrestigeCost);
	}

	public double getPlayerRankupCostWithIncreaseDirect(final UUID uuid) {
		if(getPlayerNextRank(uuid) == null) {
			return 0.0;
		}
		RankPath rp = RankPath.getRankPath(getPlayerNextRank(uuid), main.playerStorage.getPlayerPath(uuid));
		Double nextRankCost = getIncreasedRankupCost(getPlayerPrestige(uuid), getRankCost(rp));	
		return Double.valueOf(nextRankCost);
	}

	public double getPlayerNextPrestigeCostWithIncreaseDirect(final UUID uuid) {
		if(getPlayerNextPrestige(uuid) == null) {
			return 0.0;
		}
		String prestigeName = getPlayerNextPrestige(uuid);
		Double nextPrestigeCost = getIncreasedPrestigeCost(getPlayerRebirth(uuid), getPrestigeCost(prestigeName));
		return Double.valueOf(nextPrestigeCost);
	}

	/**
	 * 
	 * @param offlinePlayer
	 * @return player prestiges amount with rebirth number in count.
	 * example: player. prestige=10 rebirth=2 will return 30
	 */
	public int getPlayerPrestiges(final OfflinePlayer offlinePlayer) {
		int rebirthNumber = getPlayerRebirthNumber(offlinePlayer) == 0 ? 0 : getPlayerRebirthNumber(offlinePlayer) * (int)getPrestigeSize();
		if(!hasPrestiged(offlinePlayer)) {
			return 0;
		}
		if(!hasRebirthed(offlinePlayer)) {
			if(!hasPrestiged(offlinePlayer)) {
				return 0;
			}
			return getPlayerPrestigeNumber(offlinePlayer) + rebirthNumber;
		}
		return getPlayerPrestigeNumber(offlinePlayer) + rebirthNumber;
	}


	/**
	 * 
	 * @param uuid
	 * @return player prestiges amount with rebirth number in count.
	 * example: player. prestige=10 rebirth=2 will return 30
	 */
	public int getPlayerPrestiges(final UUID uuid) {
		int rebirthNumber = getPlayerRebirthNumber(uuid) == 0 ? 0 : getPlayerRebirthNumber(uuid) * (int)getPrestigeSize();
		if(!hasPrestiged(uuid)) {
			return 0;
		}
		if(!hasRebirthed(uuid)) {
			if(!hasPrestiged(uuid)) {
				return 0;
			}
			return getPlayerPrestigeNumber(uuid) + rebirthNumber;
		}
		return getPlayerPrestigeNumber(uuid) + rebirthNumber;
	}

	/**
	 * 
	 * @param uuid
	 * @return player power (made for leaderboards)
	 */
	public int getPlayerPromotionsAmount(final UUID uuid) {
		if(uuid == null) return 0;
		RankPath rp = getPlayerRankPath(uuid);
		if(rp == null) return 0;
		if(hasRebirthed(uuid)) {
			return (((getPlayerRebirthNumber(uuid)+1) * ((int)getPrestigeSize()+1))+getPlayerPrestigeNumber(uuid)+1) * (((getPlayerRebirthNumber(uuid)+1) * (getRanksCollection(rp.getPathName()).size()+1)) + getPlayerRankNumber(uuid)+1);
		} else {
			if(hasPrestiged(uuid)) {
				int rankSize = rp.getPathName() == null ? 1 : getRanksCollection(rp.getPathName()).size();
				return (getPlayerPrestigeNumber(uuid) * (rankSize+1)) + (getPlayerRankNumber(uuid)+1);
			} else {
				return getPlayerRankNumber(uuid)+1;
			}
		}
	}

	/**
	 * @return a score counting all the stages (made for leaderboard)
	 */
	public int getPower(final String rank, @Nullable final String path, @Nullable final String prestige, @Nullable final String rebirth) {
		if(rebirth != null) {
			if(prestige != null) {
				return (((this.getRebirthNumberX(rebirth)+1) * ((int)getPrestigeSize()+1)+Integer.valueOf(this.getPrestigeNumber(prestige)+1)) * ((this.getRebirthNumberX(rebirth)+1) * (getRanksCollection(path).size()+1) + this.getRankNumberX(path, rank)+1));
			} else {
				return (((this.getRebirthNumberX(rebirth)+1) * ((int)getPrestigeSize()+1)+1) * ((this.getRebirthNumberX(rebirth)+1) * (getRanksCollection(path).size()+1) + this.getRankNumberX(path, rank)+1));
			}
		} else {
			if(prestige != null) {
				int rankSize = path == null ? 1 : getRanksCollection(path).size();
				return (Integer.valueOf(getPrestigeNumber(prestige)+1) * (rankSize+1)) + Integer.valueOf(getRankNumber(path, rank)+1);
			} else {
				return Integer.valueOf(getRankNumber(path, rank)+1);
			}
		}
	}

	/**
	 * Alternative to getPower(...)
	 * @return a score counting all the stages (made for leaderboard)
	 */
	public int getPromotionsAmount(final String rank, @Nullable final String path, @Nullable final String prestige, @Nullable final String rebirth) {
		if(rebirth != null) {
			if(prestige != null) {
				return (((this.getRebirthNumberX(rebirth)+1) * ((int)getPrestigeSize()+1)+Integer.valueOf(this.getPrestigeNumber(prestige)+1)) * ((this.getRebirthNumberX(rebirth)+1) * (getRanksCollection(path).size()+1) + this.getRankNumberX(path, rank)+1));
			} else {
				return (((this.getRebirthNumberX(rebirth)+1) * ((int)getPrestigeSize()+1)+1) * ((this.getRebirthNumberX(rebirth)+1) * (getRanksCollection(path).size()+1) + this.getRankNumberX(path, rank)+1));
			}
		} else {
			if(prestige != null) {
				int rankSize = path == null ? 1 : getRanksCollection(path).size();
				return (Integer.valueOf(getPrestigeNumber(prestige)+1) * (rankSize+1)) + Integer.valueOf(getRankNumber(path, rank)+1);
			} else {
				return Integer.valueOf(getRankNumber(path, rank)+1);
			}
		}
	}

	/**
	 * 
	 * @param rebirthName
	 * @return rebirth's required prestiges amount for a successful rebirth.
	 */
	public int getRequiredPrestiges(final String rebirthName) {
		return main.rebirthStorage.getRequiredPrestiges(rebirthName);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param offlinePlayer
	 * @return
	 */
	public RankPath getPlayerNextRankPath(final OfflinePlayer offlinePlayer) {
		RankPath rp = getPlayerRankPath(offlinePlayer);
		RankDataHandler rdh = getRank(rp);
		RankPath nextRP = RankPath.getRankPath(rdh.getRankupName(), rp.getPathName());
		return nextRP;
	}

	/**
	 * getRankupPercentage
	 *  @param offlinePlayer
	 *  @return String made for placeholderapi | returns "100" if percentage is above 100
	 */
	public String getPlayerRankupPercentageDirect(final OfflinePlayer offlinePlayer) {
		if(this.getRankNumberRequirements(getPlayerRankPath(offlinePlayer)) == null) {
			Double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / numberAPI.limitInverse(getPlayerRankupCostWithIncreaseDirect(offlinePlayer), 1) * 100;
			String convertedValue = numberAPI.toFakeInteger(Double.valueOf(numberAPI.deleteScientificNotationA(percent)));
			if(Double.valueOf(convertedValue) > 100) {
				return "100";
			}
			return String.valueOf(convertedValue);
		} else {
			int splittingNumber = 1;
			double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / numberAPI.limitInverse(getPlayerRankupCostWithIncreaseDirect(offlinePlayer), 1) * 100;
			double addPercent = 0D;
			for(Entry<String, Double> entry : this.getRankNumberRequirements(getPlayerRankPath(offlinePlayer)).entrySet()) {
				splittingNumber++;
				addPercent += (Double.valueOf(PlaceholderAPI.setPlaceholders(offlinePlayer, entry.getKey())) / entry.getValue()) * 100;
			}
			double finalPercent = (numberAPI.limit(percent, 100) + numberAPI.limit(addPercent, 100)) / splittingNumber;
			if(finalPercent >= 100) {
				return "100";
			}
			return numberAPI.toFakeInteger(finalPercent);
		}
	}

	public String getPlayerRankPercentage(final OfflinePlayer offlinePlayer, final RankPath rankPath) {
		if(this.getRankNumberRequirements(rankPath) == null) {
			Double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / numberAPI.limitInverse(getPlayerRankCostWithIncreaseDirect(offlinePlayer, rankPath), 1) * 100;
			String convertedValue = numberAPI.toFakeInteger(Double.valueOf(numberAPI.deleteScientificNotationA(percent)));
			if(Double.valueOf(convertedValue) > 100) {
				return "100";
			}
			return String.valueOf(convertedValue);
		} else {
			int splittingNumber = 1;
			double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / numberAPI.limitInverse(getPlayerRankCostWithIncreaseDirect(offlinePlayer, rankPath), 1) * 100;
			double addPercent = 0D;
			for(Entry<String, Double> entry : this.getRankNumberRequirements(rankPath).entrySet()) {
				splittingNumber++;
				addPercent += (Double.valueOf(PlaceholderAPI.setPlaceholders(offlinePlayer, entry.getKey())) / entry.getValue()) * 100;
			}
			double finalPercent = (numberAPI.limit(percent, 100) + numberAPI.limit(addPercent, 100)) / splittingNumber;
			if(finalPercent >= 100) {
				return "100";
			}
			return numberAPI.toFakeInteger(finalPercent);
		}
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param player
	 * @param ignoreLastRank
	 * @return
	 */
	public boolean canPrestige(Player player, boolean ignoreLastRank) {
		PRXAPI prxAPI = this;
		Player p = player;
		String prestige = prxAPI.getPlayerNextPrestige(p);
		if(!p.hasPermission(main.prestigeCommand.getPermission()) && !p.hasPermission("*")) {
			main.debug("(canPrestige) doesn't have permission");
			return false;
		}
		if(prestige == null) {
			main.debug("(canPrestige) next prestige is null");
			return false;
		}
		if(!ignoreLastRank) {
			main.debug("(canPrestige) is not at last rank and doesn't have allow prestige");
			return false;
		}
		if(!main.isBefore1_7) {
			if(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p) > prxAPI.getPlayerMoney(p)) {
				main.debug("(canPrestige) next prestige cost is more than player money");
				return false;
			}
		} else {
			if(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p) > prxAPI.getPlayerMoney(p.getName())) {
				main.debug("(canPrestige) next prestige cost is more than player money");
				return false;
			}
		}
		main.debug("(canPrestige) passed all checks. player can prestige!");
		return true;
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param offlinePlayer
	 * @return true if the player has the permission and the money to prestige unless he is at the latest prestige.
	 */
	public boolean canPrestige(Player player) {
		PRXAPI prxAPI = this;
		Player p = player;
		String prestige = prxAPI.getPlayerNextPrestige(p);
		if(!p.hasPermission(main.prestigeCommand.getPermission()) && !p.hasPermission("*")) {
			main.debug("(canPrestige) doesn't have permission");
			return false;
		}
		if(prestige == null) {
			main.debug("(canPrestige) next prestige is null");
			return false;
		}
		if(!prxAPI.isLastRank(p) && !main.rankStorage.isAllowPrestige(prxAPI.getPlayerRankPath(p))) {
			main.debug("(canPrestige) is not at last rank and doesn't have allow prestige");
			return false;
		}
		if(!main.isBefore1_7) {
			if(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p) > prxAPI.getPlayerMoney(p)) {
				main.debug("(canPrestige) next prestige cost is more than player money");
				return false;
			}
		} else {
			if(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p) > prxAPI.getPlayerMoney(p.getName())) {
				main.debug("(canPrestige) next prestige cost is more than player money");
				return false;
			}
		}
		main.debug("(canPrestige) passed all checks. player can prestige!");
		return true;
	}

	public boolean canPrestige(OfflinePlayer player) {
		PRXAPI prxAPI = this;
		OfflinePlayer p = player;
		String prestige = prxAPI.getPlayerNextPrestige(p);
		if(prestige == null) {
			main.debug("(canPrestige) next prestige is null");
			return false;
		}
		if(!prxAPI.isLastRank(p) && !main.rankStorage.isAllowPrestige(prxAPI.getPlayerRankPath(p))) {
			main.debug("(canPrestige) is not at last rank and doesn't have allow prestige");
			return false;
		}
		if(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p) > prxAPI.getPlayerMoney(p)) {
			main.debug("(canPrestige) next prestige cost is more than player money");
			return false;
		}
		main.debug("(canPrestige) passed all checks. player can prestige!");
		return true;
	}

	public boolean canRankup(Player player) {
		PRXAPI prxAPI = this;
		Player p = player;
		String nextRank = prxAPI.getPlayerNextRank(player);
		if(!p.hasPermission(main.rankupCommand.getPermission()) && !p.hasPermission("*")) {
			return false;
		}
		if(nextRank == null) {
			return false;
		}
		if(!main.isBefore1_7) {
			if(prxAPI.getPlayerRankupCostWithIncreaseDirect(p) > prxAPI.getPlayerMoney(p)) {
				return false;
			}
		} else {
			if(prxAPI.getPlayerRankupCostWithIncreaseDirect(p) > prxAPI.getPlayerMoney(p.getName())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param player
	 * @return
	 */
	public boolean canRebirth(Player player) {
		PRXAPI prxAPI = this;
		Player p = player;
		String rebirth = prxAPI.getPlayerNextRebirth(p);
		if(!p.hasPermission(main.rebirthCommand.getPermission()) && !p.hasPermission("*")) {
			return false;
		}
		if(rebirth.equalsIgnoreCase("LASTREBIRTH")) {
			return false;
		}
		if(!main.isBefore1_7) {
			if(prxAPI.getPlayerNextRebirthCost(p) > prxAPI.getPlayerMoney(p)) {
				return false;
			}
		} else {
			if(prxAPI.getPlayerNextRebirthCost(p) > prxAPI.getPlayerMoney(p.getName())) {
				return false;
			}
		}
		int requiredPrestiges = prxAPI.getRequiredPrestiges(rebirth);
		if(requiredPrestiges != 0) {
			if(requiredPrestiges > prxAPI.getPlayerPrestiges(p)) {
				return false;
			}
		}
		return true;
	}

	public boolean canRebirth(OfflinePlayer player) {
		PRXAPI prxAPI = this;
		OfflinePlayer p = player;
		String rebirth = prxAPI.getPlayerNextRebirth(p);
		if(rebirth.equalsIgnoreCase("LASTREBIRTH")) {
			return false;
		}
		if(prxAPI.getPlayerNextRebirthCost(p) > prxAPI.getPlayerMoney(p)) {
			return false;
		}
		int requiredPrestiges = prxAPI.getRequiredPrestiges(rebirth);
		if(requiredPrestiges != 0) {
			if(requiredPrestiges > prxAPI.getPlayerPrestiges(p)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param offlinePlayer
	 * @return will return 100 if anything goes wrong.
	 */
	public String getPlayerRankupPercentageSafe(final OfflinePlayer offlinePlayer) {
		if(this.getRankNumberRequirements(getPlayerRankPath(offlinePlayer)) == null) {
			if(offlinePlayer == null) {
				return "100";
			}
			if(getPlayerNextRank(offlinePlayer) == null) {
				return "100";
			}
			Double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / numberAPI.limitInverse(getPlayerRankupCostWithIncreaseDirect(offlinePlayer), 1) * 100;
			String convertedValue = numberAPI.toFakeInteger(Double.valueOf(numberAPI.deleteScientificNotationA(percent)));
			if(Double.valueOf(convertedValue) > 100) {
				return "100";
			}
			return String.valueOf(convertedValue);
		} else {
			int splittingNumber = 1;
			double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / numberAPI.limitInverse(getPlayerRankupCostWithIncreaseDirect(offlinePlayer), 1) * 100;
			double addPercent = 0D;
			for(Entry<String, Double> entry : this.getRankNumberRequirements(getPlayerRankPath(offlinePlayer)).entrySet()) {
				splittingNumber++;
				addPercent += (Double.valueOf(PlaceholderAPI.setPlaceholders(offlinePlayer, entry.getKey())) / entry.getValue()) * 100;
			}
			double finalPercent = (numberAPI.limit(percent, 100) + numberAPI.limit(addPercent, 100)) / splittingNumber;
			if(finalPercent >= 100) {
				return "100";
			}
			return numberAPI.toFakeInteger(finalPercent);
		}
	}

	public String getPlayerNextPrestigePercentageDirect(final OfflinePlayer offlinePlayer) {
		if(this.getPrestigeNumberRequirements(getPlayerPrestige(offlinePlayer)) == null) {
			Double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / getPlayerNextPrestigeCostWithIncreaseDirect(offlinePlayer) * 100;
			String convertedValue = numberAPI.toFakeInteger(Double.valueOf(numberAPI.deleteScientificNotationA(percent)));
			if(Double.valueOf(convertedValue) > 100) {
				return "100";
			}
			return String.valueOf(convertedValue);
		} else {
			int splittingNumber = 1;
			double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / getPlayerNextPrestigeCostWithIncreaseDirect(offlinePlayer) * 100;
			double addPercent = 0D;
			for(Entry<String, Double> entry : this.getPrestigeNumberRequirements(getPlayerPrestige(offlinePlayer)).entrySet()) {
				splittingNumber++;
				addPercent += (Double.valueOf(PlaceholderAPI.setPlaceholders(offlinePlayer, entry.getKey())) / entry.getValue()) * 100;
			}
			double finalPercent = (numberAPI.limit(percent, 100) + numberAPI.limit(addPercent, 100)) / splittingNumber;
			if(finalPercent >= 100) {
				return "100";
			}
			return numberAPI.toFakeInteger(finalPercent);
		}
	}

	public String getPlayerNextRebirthPercentageDirect(final OfflinePlayer offlinePlayer) {
		if(this.getRebirthNumberRequirements(getPlayerRebirth(offlinePlayer)) == null) {
			Double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / getPlayerNextRebirthCost(offlinePlayer) * 100;
			String convertedValue = numberAPI.toFakeInteger(Double.valueOf(numberAPI.deleteScientificNotationA(percent)));
			if(Double.valueOf(convertedValue) > 100) {
				return "100";
			}
			return String.valueOf(convertedValue);
		} else {
			int splittingNumber = 1;
			double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / getPlayerNextRebirthCost(offlinePlayer) * 100;
			double addPercent = 0D;
			for(Entry<String, Double> entry : this.getRebirthNumberRequirements(getPlayerRebirth(offlinePlayer)).entrySet()) {
				splittingNumber++;
				addPercent += (Double.valueOf(PlaceholderAPI.setPlaceholders(offlinePlayer, entry.getKey())) / entry.getValue()) * 100;
			}
			double finalPercent = (numberAPI.limit(percent, 100) + numberAPI.limit(addPercent, 100)) / splittingNumber;
			if(finalPercent >= 100) {
				return "100";
			}
			return numberAPI.toFakeInteger(finalPercent);
		}
	}

	@SuppressWarnings("deprecation")
	public String getPlayerRankupPercentageDirectOnline(UUID uuid, String name) {
		Double percent = getPluginMainClass().econ.getBalance(name) / getPlayerRankupCostWithIncreaseDirect(uuid) * 100;
		String convertedValue = numberAPI.toFakeInteger(Double.valueOf(numberAPI.deleteScientificNotationA(percent)));
		if(Double.valueOf(convertedValue) > 100) {
			return "100";
		}
		return String.valueOf(convertedValue);
	}

	/**
	 * 
	 *  @param offlinePlayer
	 *  @return String made for placeholderapi | returns rankup percentage without a limit so it can be above 100
	 */
	public String getPlayerRankupPercentageNoLimitDirect(final OfflinePlayer offlinePlayer) {
		if(this.getRankNumberRequirements(getPlayerRankPath(offlinePlayer)) == null) {
			Double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / getPlayerRankupCostWithIncreaseDirect(offlinePlayer) * 100;
			String percentRemovedSN = numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2);
			String convertedValue = numberAPI.toFakeInteger(Double.valueOf(percentRemovedSN));
			return String.valueOf(convertedValue);
		} else {
			int splittingNumber = 1;
			double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / getPlayerRankupCostWithIncreaseDirect(offlinePlayer) * 100;
			double addPercent = 0D;
			for(Entry<String, Double> entry : this.getRankNumberRequirements(getPlayerRankPath(offlinePlayer)).entrySet()) {
				splittingNumber++;
				addPercent += (Double.valueOf(PlaceholderAPI.setPlaceholders(offlinePlayer, entry.getKey())) / entry.getValue()) * 100;
			}
			double finalPercent = (numberAPI.limit(percent, 100) + numberAPI.limit(addPercent, 100)) / splittingNumber;
			return numberAPI.toFakeInteger(finalPercent);
		}
	}

	public String getPlayerRankPercentageNoLimitDirect(final OfflinePlayer offlinePlayer, final RankPath rankPath) {
		if(this.getRankNumberRequirements(rankPath) == null) {
			Double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / getPlayerRankCostWithIncreaseDirect(offlinePlayer, rankPath) * 100;
			String percentRemovedSN = numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2);
			String convertedValue = numberAPI.toFakeInteger(Double.valueOf(percentRemovedSN));
			return String.valueOf(convertedValue);
		} else {
			int splittingNumber = 1;
			double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / getPlayerRankCostWithIncreaseDirect(offlinePlayer, rankPath) * 100;
			double addPercent = 0D;
			for(Entry<String, Double> entry : this.getRankNumberRequirements(rankPath).entrySet()) {
				splittingNumber++;
				addPercent += (Double.valueOf(PlaceholderAPI.setPlaceholders(offlinePlayer, entry.getKey())) / entry.getValue()) * 100;
			}
			double finalPercent = (numberAPI.limit(percent, 100) + numberAPI.limit(addPercent, 100)) / splittingNumber;
			return numberAPI.toFakeInteger(finalPercent);
		}
	}

	@SuppressWarnings("deprecation")
	public String getPlayerRankupPercentageNoLimitDirectOnline(UUID uuid, String name) {
		Double percent = getPluginMainClass().econ.getBalance(name) / getPlayerRankupCostWithIncreaseDirect(uuid) * 100;
		String percentRemovedSN = numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2);
		String convertedValue = numberAPI.toFakeInteger(Double.valueOf(percentRemovedSN));
		return String.valueOf(convertedValue);
	}

	/**
	 * 
	 *  @param offlinePlayer
	 *  @return String made for placeholderapi | returns rankup percentage with 2 decimal numbers / returns "100.0" if percentage is above 100
	 */
	public String getPlayerRankupPercentageDecimalDirect(final OfflinePlayer offlinePlayer) {
		if(this.getRankNumberRequirements(getPlayerRankPath(offlinePlayer)) == null) {
			Double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / getPlayerRankupCostWithIncreaseDirect(offlinePlayer) * 100;
			String percentRemovedSN = numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2);
			Double convertedValue = Double.valueOf(percentRemovedSN);
			if(convertedValue > 100) {
				return "100.0";
			}
			return String.valueOf(percentRemovedSN);
		} else {
			int splittingNumber = 1;
			double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / getPlayerRankupCostWithIncreaseDirect(offlinePlayer) * 100;
			double addPercent = 0D;
			for(Entry<String, Double> entry : this.getRankNumberRequirements(getPlayerRankPath(offlinePlayer)).entrySet()) {
				splittingNumber++;
				addPercent += (Double.valueOf(PlaceholderAPI.setPlaceholders(offlinePlayer, entry.getKey())) / entry.getValue()) * 100;
			}
			double finalPercent = (numberAPI.limit(percent, 100) + numberAPI.limit(addPercent, 100)) / splittingNumber;
			if(finalPercent >= 100) {
				return "100.0";
			}
			return dec(finalPercent);
		}
	}

	public String getPlayerRankPercentageDecimalDirect(final OfflinePlayer offlinePlayer, final RankPath rankPath) {
		if(this.getRankNumberRequirements(rankPath) == null) {
			Double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / getPlayerRankCostWithIncreaseDirect(offlinePlayer, rankPath) * 100;
			String percentRemovedSN = numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2);
			Double convertedValue = Double.valueOf(percentRemovedSN);
			if(convertedValue > 100) {
				return "100.0";
			}
			return String.valueOf(percentRemovedSN);
		} else {
			int splittingNumber = 1;
			double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / getPlayerRankCostWithIncreaseDirect(offlinePlayer, rankPath) * 100;
			double addPercent = 0D;
			for(Entry<String, Double> entry : this.getRankNumberRequirements(rankPath).entrySet()) {
				splittingNumber++;
				addPercent += (Double.valueOf(PlaceholderAPI.setPlaceholders(offlinePlayer, entry.getKey())) / entry.getValue()) * 100;
			}
			double finalPercent = (numberAPI.limit(percent, 100) + numberAPI.limit(addPercent, 100)) / splittingNumber;
			if(finalPercent >= 100) {
				return "100.0";
			}
			return dec(finalPercent);
		}
	}

	public String dec(double doubleParam) {
		return s(numberAPI.decimalize(numberAPI.deleteScientificNotationA(doubleParam), 2));
	}

	@SuppressWarnings("deprecation")
	public String getPlayerRankupPercentageDecimalDirectOnline(UUID uuid, String name) {
		Double percent = getPluginMainClass().econ.getBalance(name) / getPlayerRankupCostWithIncreaseDirect(uuid) * 100;
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
	public String getPlayerRankupPercentageDecimalNoLimitDirect(final OfflinePlayer offlinePlayer) {
		if(this.getRankNumberRequirements(getPlayerRankPath(offlinePlayer)) == null) {
			Double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / getPlayerRankupCostWithIncreaseDirect(offlinePlayer) * 100;
			String percentRemovedSN = numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2);
			return String.valueOf(percentRemovedSN);
		} else {
			int splittingNumber = 1;
			double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / getPlayerRankupCostWithIncreaseDirect(offlinePlayer) * 100;
			double addPercent = 0D;
			for(Entry<String, Double> entry : this.getRankNumberRequirements(getPlayerRankPath(offlinePlayer)).entrySet()) {
				splittingNumber++;
				addPercent += (Double.valueOf(PlaceholderAPI.setPlaceholders(offlinePlayer, entry.getKey())) / entry.getValue()) * 100;
			}
			double finalPercent = (numberAPI.limit(percent, 100) + numberAPI.limit(addPercent, 100)) / splittingNumber;
			return dec(finalPercent);
		}
	}

	public String getPlayerRankPercentageDecimalNoLimitDirect(final OfflinePlayer offlinePlayer, final RankPath rankPath) {
		if(this.getRankNumberRequirements(rankPath) == null) {
			Double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / getPlayerRankCostWithIncreaseDirect(offlinePlayer, rankPath) * 100;
			String percentRemovedSN = numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2);
			return String.valueOf(percentRemovedSN);
		} else {
			int splittingNumber = 1;
			double percent = getPluginMainClass().econ.getBalance(offlinePlayer) / getPlayerRankCostWithIncreaseDirect(offlinePlayer, rankPath) * 100;
			double addPercent = 0D;
			for(Entry<String, Double> entry : this.getRankNumberRequirements(rankPath).entrySet()) {
				splittingNumber++;
				addPercent += (Double.valueOf(PlaceholderAPI.setPlaceholders(offlinePlayer, entry.getKey())) / entry.getValue()) * 100;
			}
			double finalPercent = (numberAPI.limit(percent, 100) + numberAPI.limit(addPercent, 100)) / splittingNumber;
			return dec(finalPercent);
		}
	}

	@SuppressWarnings("deprecation")
	public String getPlayerRankupPercentageDecimalNoLimitDirectOnline(UUID uuid, String name) {
		Double percent = getPluginMainClass().econ.getBalance(name) / getPlayerRankupCostWithIncreaseDirect(uuid) * 100;
		String percentRemovedSN = numberAPI.decimalize(numberAPI.deleteScientificNotationA(percent), 2);
		return String.valueOf(percentRemovedSN);
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @param offlinePlayer
	 *  @return String player rankup display name
	 */
	public String getPlayerRankupDisplay(OfflinePlayer offlinePlayer) {
		return main.rankStorage.getRankupDisplayName(getPlayerRankPath(offlinePlayer));
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid player unique id
	 * @return next rank display name/prefix
	 */
	public String getPlayerRankupDisplay(UUID uuid) {
		return main.rankStorage.getRankupDisplayName(getPlayerRankPath(uuid));
	}
	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * 
	 *  @param offlinePlayer
	 *  @return String colored player rankup display name
	 */
	public String getPlayerRankupDisplayR(OfflinePlayer offlinePlayer) {
		return c(main.rankStorage.getRankupDisplayName(getPlayerRankPath(offlinePlayer)));
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param uuid player unique id
	 * @return colored player next rank display name/prefix
	 */
	public String getPlayerRankupDisplayR(UUID uuid) {
		return c(main.rankStorage.getRankupDisplayName(getPlayerRankPath(uuid)));
	}

	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param configMessage
	 * @return returns a colored cached string config message from the storage
	 */
	public String g(String configMessage) {
		return c(main.messagesStorage.getStringMessage(configMessage));
	}
	/**
	 * <p><i>this method is thread-safe (can be called from an Async Task).
	 * @param configMessage
	 * @return returns a cached string list config message
	 */
	public List<String> h(String configMessage) {
		return main.messagesStorage.getStringListMessage(configMessage);
	}
	String s(Object o) {
		return String.valueOf(o);
	}

	public String getLastPrestige() {
		return main.getGlobalStorage().getStringData("lastprestige");
	}

	public String getLastRebirth() {
		return main.getGlobalStorage().getStringData("lastrebirth");
	}

	/**
	 * Execute a rankup to a player
	 * @param player
	 */
	public void rankup(Player player) {
		main.rankupAPI.rankup(player);
	}

	/**
	 * Execute a rankup to a player for versions below 1.7
	 * @param player
	 */
	public void rankupLegacy(Player player) {
		main.rankupLegacy.rankup(player);
	}
	/**
	 * Execute a prestige to a player
	 * @param player
	 */
	public void prestige(Player player) {
		main.prestigeAPI.prestige(player);
	}

	public Rankup getRankupAPI() {
		return main.rankupAPI;
	}

	public Prestige getPrestigeAPI() {
		return main.prestigeAPI;
	}

	public Rebirth getRebirthAPI() {
		return main.rebirthAPI;
	}

	@SuppressWarnings("deprecation")
	private void sendTempBlockChange(final Player player, final Location location, final Material material, final byte data) {
		Block block = player.getWorld().getBlockAt(location);
		player.sendBlockChange(location, material, data);
		Bukkit.getScheduler().runTaskLater(main, () -> {
			player.sendBlockChange(location, block.getType(), block.getState().getRawData());
		}, 30);
	}

	/**
	 * 
	 * @param player
	 */
	public void celeberate(Player player) {
		if(!main.allowEasterEggs) return;
		Player p = player;
		World world = p.getWorld();
		Bukkit.getScheduler().runTask(main, () -> {
			if(main.getHolidayUtils().getHoliday() == Holiday.HALLOWEEN) {
				Entity ent = world.spawnEntity(p.getLocation().add(0, 1, 0), EntityType.BAT);
				FastParticle.spawnParticle(world, ParticleType.FLAME, p.getLocation().add(0, 1, 0), 5);
				FastParticle.spawnParticle(world, ParticleType.DRIP_LAVA, p.getLocation().add(0, 1, 0), 5);
				Bukkit.getScheduler().runTaskLater(main, () -> {
					ent.remove();
				}, 25);
			} else if (main.getHolidayUtils().getHoliday() == Holiday.CHRISTMAS) {
				FastParticle.spawnParticle(world, ParticleType.SNOWBALL, p.getLocation().add(0, 1, 0), 3);
				FastParticle.spawnParticle(world, ParticleType.FIREWORKS_SPARK, p.getLocation().add(1, 1, 0), 3);
				FastParticle.spawnParticle(world, ParticleType.SNOWBALL, p.getLocation().add(0, 1, 1), 3);
				FastParticle.spawnParticle(world, ParticleType.FIREWORKS_SPARK, p.getLocation().add(0, 0, 0), 3);
				sendTempBlockChange(p, p.getLocation(), XMaterial.SNOW.parseMaterial(), (byte)0);
			} else if (main.getHolidayUtils().getHoliday() == Holiday.VALENTINE) {
				FastParticle.spawnParticle(world, ParticleType.HEART, p.getLocation().add(0, 1, 0), 3);
				FastParticle.spawnParticle(world, ParticleType.HEART, p.getLocation().add(1, 1, 0), 3);
				FastParticle.spawnParticle(world, ParticleType.HEART, p.getLocation().add(0, 1, 1), 3);
				FastParticle.spawnParticle(world, ParticleType.HEART, p.getLocation().add(0, 0, 0), 3);
			}
		});
	}

	/**
	 * 
	 * @param player
	 * @param force ignore allow-easter-eggs option
	 */
	public void celeberate(Player player, boolean force) {
		Player p = player;
		World world = p.getWorld();
		Bukkit.getScheduler().runTask(main, () -> {
			if(main.getHolidayUtils().getHoliday() == Holiday.HALLOWEEN) {
				Entity ent = world.spawnEntity(p.getLocation().add(0, 1, 0), EntityType.BAT);
				FastParticle.spawnParticle(world, ParticleType.FLAME, p.getLocation().add(0, 1, 0), 5);
				FastParticle.spawnParticle(world, ParticleType.DRIP_LAVA, p.getLocation().add(0, 1, 0), 5);
				Bukkit.getScheduler().runTaskLater(main, () -> {
					ent.remove();
				}, 25);
			} else if (main.getHolidayUtils().getHoliday() == Holiday.CHRISTMAS) {
				FastParticle.spawnParticle(world, ParticleType.SNOWBALL, p.getLocation().add(0, 1, 0), 3);
				FastParticle.spawnParticle(world, ParticleType.FIREWORKS_SPARK, p.getLocation().add(1, 1, 0), 3);
				FastParticle.spawnParticle(world, ParticleType.SNOWBALL, p.getLocation().add(0, 1, 1), 3);
				FastParticle.spawnParticle(world, ParticleType.FIREWORKS_SPARK, p.getLocation().add(0, 0, 0), 3);
				sendTempBlockChange(p, p.getLocation(), XMaterial.SNOW.parseMaterial(), (byte)0);
			} else if (main.getHolidayUtils().getHoliday() == Holiday.VALENTINE) {
				FastParticle.spawnParticle(world, ParticleType.HEART, p.getLocation().add(0, 1, 0), 3);
				FastParticle.spawnParticle(world, ParticleType.HEART, p.getLocation().add(1, 1, 0), 3);
				FastParticle.spawnParticle(world, ParticleType.HEART, p.getLocation().add(0, 1, 1), 3);
				FastParticle.spawnParticle(world, ParticleType.HEART, p.getLocation().add(0, 0, 0), 3);
			}
		});
	}
	/**
	 * Execute a prestige to a player for versions below 1.7
	 * @param player
	 */
	public void prestigeLegacy(Player player) {
		main.prestigeLegacy.prestige(player);
	}
	/**
	 * Execute a rankupmax to a player
	 * @param player
	 */
	public void rankupMax(Player player) {
		main.rankupMaxAPI.rankupMax(player);
	}
	/**
	 * Execute a rankupmax to a player for versions below 1.7
	 * @param player
	 */
	public void rankupMaxLegacy(Player player) {
		main.rankupMaxLegacy.rankupMax(player);
	}

	/**
	 * Execute a rankupmax rank jump to a specific rank for a player
	 * @param player
	 */
	public void rankupMaxLimit(Player player, String rank) {
		main.rankupMaxAPI.rankupMax(player, rank);
	}

	public void rankupMaxLimitLegacy(Player player, String rank) {
		main.rankupMaxLegacy.rankupMax(player, rank);
	}

	public void rebirth(Player player) {
		main.rebirthAPI.rebirth(player);
	}

	public void rebirthLegacy(Player player) {
		main.rebirthLegacy.rebirth(player);
	}

	/**
	 * <p><i>this method is thread-safe and multi-threaded (can be called from an Async Task).
	 * <p><b>NOTE: the entire leaderboard system is MULTI-THREADED</b>
	 * @param player
	 * @return leaderboard position starting from 1
	 */
	public Integer getPlayerPrestigeLeaderboardPosition(OfflinePlayer player) {
		return main.lbm.getPlayerPrestigePosition(player);
	}

	/**
	 * <p><i>this method is thread-safe and multi-threaded (can be called from an Async Task).
	 * <p><b>NOTE: the entire leaderboard system is MULTI-THREADED</b>
	 * @param uuid
	 * @return player leaderboard position
	 */
	public Integer getPlayerPrestigeLeaderboardPosition(UUID uuid) {
		return main.lbm.getPlayerPrestigePosition(Bukkit.getOfflinePlayer(uuid));
	}

	/**
	 * <p><i>this method is thread-safe and multi-threaded (can be called from an Async Task).
	 * <p><b>NOTE: the entire leaderboard system is MULTI-THREADED</b>
	 * @param intValue
	 * @return player from leaderboard position
	 */
	public OfflinePlayer getPrestigeLeaderboardPosition(Integer intValue) {
		return Bukkit.getOfflinePlayer(main.lbm.getPlayerFromPositionPrestige(intValue).getKey());
	}

	/**
	 * <p><i>this method is thread-safe and multi-threaded (can be called from an Async Task).
	 * <p><b>NOTE: the entire leaderboard system is MULTI-THREADED</b>
	 * @param intValue
	 * @return player uuid from leaderboard position
	 */
	public UUID getPrestigeLeaderboardPositionUUID(Integer intValue) {
		return main.lbm.getPlayerFromPositionPrestige(intValue).getKey();
	}
}
