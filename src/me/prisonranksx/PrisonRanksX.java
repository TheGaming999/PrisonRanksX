package me.prisonranksx;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import me.prisonranksx.api.PRXAPI;
import me.prisonranksx.api.PRXManager;
import me.prisonranksx.api.Prestige;
import me.prisonranksx.api.PrestigeLegacy;
import me.prisonranksx.api.IPrestigeMax;
import me.prisonranksx.api.PrestigeMaxLegacy;
import me.prisonranksx.api.PrestigeMax;
import me.prisonranksx.api.Prestiges;
import me.prisonranksx.api.Ranks;
import me.prisonranksx.api.Rankup;
import me.prisonranksx.api.RankupLegacy;
import me.prisonranksx.api.RankupMaxLegacy;
import me.prisonranksx.api.Rebirth;
import me.prisonranksx.api.RebirthLegacy;
import me.prisonranksx.api.Rebirths;
import me.prisonranksx.commands.AutoPrestigeCommand;
import me.prisonranksx.commands.AutoRankupCommand;
import me.prisonranksx.commands.ForceRankupCommand;
import me.prisonranksx.commands.PRXCommand;
import me.prisonranksx.commands.PrestigeCommand;
import me.prisonranksx.commands.PrestigeMaxCommand;
import me.prisonranksx.commands.PrestigesCommand;
import me.prisonranksx.commands.RanksCommand;
import me.prisonranksx.commands.RankupCommand;
import me.prisonranksx.commands.RankupMaxCommand;
import me.prisonranksx.commands.RebirthCommand;
import me.prisonranksx.commands.RebirthsCommand;
import me.prisonranksx.commands.TopPrestigesCommand;
import me.prisonranksx.commands.TopRebirthsCommand;
import me.prisonranksx.data.FireworkManager;
import me.prisonranksx.data.GlobalDataStorage;
import me.prisonranksx.data.GlobalDataStorage1_16;
import me.prisonranksx.data.GlobalDataStorage1_8;
import me.prisonranksx.data.IPrestigeDataStorage;
import me.prisonranksx.data.InfinitePrestigeSettings;
import me.prisonranksx.data.MessagesDataStorage;
import me.prisonranksx.data.MySQLDataUpdater;
import me.prisonranksx.data.PlayerDataStorage;
import me.prisonranksx.data.PrestigeDataStorage;
import me.prisonranksx.data.PrestigeDataStorageInfinite;
import me.prisonranksx.data.RankDataStorage;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.data.RebirthDataStorage;
import me.prisonranksx.error.ErrorInspector;
import me.prisonranksx.gui.CustomItemsManager;
import me.prisonranksx.gui.CustomPrestigeItems;
import me.prisonranksx.gui.CustomRankItems;
import me.prisonranksx.gui.CustomRebirthItems;
import me.prisonranksx.gui.GuiListManager;
import me.prisonranksx.hooks.DHHologramManager;
import me.prisonranksx.hooks.GMHook;
import me.prisonranksx.hooks.HDHologramManager;
import me.prisonranksx.hooks.HologramManager;
import me.prisonranksx.hooks.MVdWPapiHook;
import me.prisonranksx.hooks.PapiHook;
import me.prisonranksx.leaderboard.LeaderboardManager;
import me.prisonranksx.listeners.IPlayerChatListener;
import me.prisonranksx.listeners.IPlayerLoginListener;
import me.prisonranksx.listeners.IPlayerQuitListener;
import me.prisonranksx.listeners.InventoryListener;
import me.prisonranksx.listeners.PlayerChatListener;
import me.prisonranksx.listeners.PlayerChatListenerForceDisplay;
import me.prisonranksx.listeners.PlayerLoginListener;
import me.prisonranksx.listeners.PlayerLoginListenerLegacy;
import me.prisonranksx.listeners.PlayerQuitListener;
import me.prisonranksx.listeners.PlayerQuitListenerLegacy;
import me.prisonranksx.listeners.PrisonRanksXListener;
import me.prisonranksx.permissions.CommandVaultDataUpdater;
import me.prisonranksx.permissions.GMVaultDataUpdater;
import me.prisonranksx.permissions.IVaultDataUpdater;
import me.prisonranksx.permissions.LPVaultDataUpdater;
import me.prisonranksx.permissions.PEXVaultDataUpdater;
import me.prisonranksx.permissions.PermissionManager;
import me.prisonranksx.permissions.VaultDataUpdater;
import me.prisonranksx.reflections.Actionbar;
import me.prisonranksx.reflections.ActionbarLegacy;
import me.prisonranksx.reflections.ActionbarProgress;
import me.prisonranksx.reflections.ExpbarProgress;
import me.prisonranksx.utils.HolidayUtils.Holiday;
import me.prisonranksx.utils.ChatColorReplacer;
import me.prisonranksx.utils.ChatColorReplacer1_16;
import me.prisonranksx.utils.ChatColorReplacer1_8;
import me.prisonranksx.utils.CollectionUtils;
import me.prisonranksx.utils.CommandLoader;
import me.prisonranksx.utils.ConfigManager;
import me.prisonranksx.utils.ConfigUpdater;
import me.prisonranksx.utils.EZLuckPerms;
import me.prisonranksx.utils.EventPriorityManager;
import me.prisonranksx.utils.HolidayUtils;
import me.prisonranksx.utils.MessageCenterizer;
import me.prisonranksx.utils.OnlinePlayers;
import me.prisonranksx.utils.PlaceholderReplacer;
import me.prisonranksx.utils.PlaceholderReplacerDefault;
import me.prisonranksx.utils.PlaceholderReplacerPAPI;
import me.prisonranksx.utils.XUUID;

import com.google.common.collect.Sets;

import cloutteam.samjakob.gui.types.PaginatedGUI;
import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PrisonRanksX extends JavaPlugin {

	// GENERAL BOOLEAN FIELDS
	public boolean hasMVdWPAPI, isApiLoaded, isActionUtil, debug, terminateMode,
	isBefore1_7, isRankEnabled, isPrestigeEnabled, isRebirthEnabled, forceSave,
	isABProgress, isRankupMaxWarpFilter, isVaultGroups, hasHologramsPlugin, hasPAPI, isEBProgress,
	isSaveOnLeave, checkVault, saveNotification, allowEasterEggs, isEnabledInsteadOfDisabled,
	isInfinitePrestige, rankForceDisplay, prestigeForceDisplay, rebirthForceDisplay, formatChat, isModernVersion;
	// ======================
	// MYSQL FIELDS
	private boolean isMySql, useSSL, autoReconnect, useCursorFetch;
	private int port;
	private Connection connection;
	private String host, database, username, password, table;
	private Statement statement;
	private MySQLDataUpdater sqlDataUpdater;
	// ======================
	// INTERFACE FIELDS
	public GlobalDataStorage globalStorage;
	public Actionbar actionBar;
	public IPrestigeMax prestigeMax;
	public PlaceholderReplacer placeholderReplacer;
	public ChatColorReplacer chatColorReplacer;
	// ======================
	// DATA STORAGE FIELDS
	public PlayerDataStorage playerStorage;
	public RankDataStorage rankStorage;
	public IPrestigeDataStorage prestigeStorage;
	public RebirthDataStorage rebirthStorage;
	public MessagesDataStorage messagesStorage;
	// ======================
	// PERMISSION FIELDS
	private Permission perms;
	public PermissionManager perm;
	public String vaultPlugin;
	public IVaultDataUpdater vaultDataUpdater;
	public GMHook groupManager;
	// ======================
	// API FIELDS
	public PRXAPI prxAPI;
	public Rankup rankupAPI;
	public RankupLegacy rankupLegacy;
	public Prestige prestigeAPI;
	public PrestigeLegacy prestigeLegacy;
	public me.prisonranksx.api.RankupMax rankupMaxAPI;
	public me.prisonranksx.api.RankupMaxLegacy rankupMaxLegacy;
	public Ranks ranksAPI;
	public Prestiges prestigesAPI;
	public Rebirth rebirthAPI;
	public RebirthLegacy rebirthLegacy;
	public Rebirths rebirthsAPI;
	public PRXManager manager;
	// ======================
	// COMMAND FIELDS
	public PRXCommand prxCommand;
	public RankupCommand rankupCommand;
	public PrestigeCommand prestigeCommand;
	public RankupMaxCommand rankupMaxCommand;
	public RanksCommand ranksCommand;
	public RebirthCommand rebirthCommand;
	public PrestigesCommand prestigesCommand;
	public RebirthsCommand rebirthsCommand;	
	public AutoRankupCommand autoRankupCommand;
	public AutoPrestigeCommand autoPrestigeCommand;
	public ForceRankupCommand forceRankupCommand;
	public TopPrestigesCommand topPrestigesCommand;
	public TopRebirthsCommand topRebirthsCommand;
	public PrestigeMaxCommand prestigeMaxCommand;
	// ======================
	// GUI FIELDS
	private GuiListManager guiManager;
	private CustomItemsManager cim;
	private CustomRankItems cri;
	private CustomPrestigeItems cpi;
	private CustomRebirthItems crri;
	// ======================
	// UTIL FIELDS
	private HolidayUtils holidayUtils;
	private CommandLoader commandLoader;
	private ConfigManager configManager;
	private FireworkManager fireworkManager;
	// ======================
	// HOOK FIELDS
	public MVdWPapiHook mvdw;
	public PapiHook papi;
	public Economy econ;
	public HologramManager hologramManager;
	// ======================
	// BALANCE FORMAT FIELDS
	private String k, M, B, T, q, Q, s, S, O, N, d, U, D, Z;
	private String[] abbreviations;
	private final DecimalFormat abb = new DecimalFormat("0.##");
	// ======================
	// OTHER FIELDS
	public BukkitScheduler scheduler;
	public ConsoleCommandSender console;
	public PluginManager pluginManager;
	public LeaderboardManager lbm;
	public ActionbarProgress abprogress;
	public ExpbarProgress ebprogress;
	public ErrorInspector errorInspector;
	public InfinitePrestigeSettings infinitePrestigeSettings;
	private static PrisonRanksX instance;
	public int autoSaveTime;
	private BukkitTask actionbarTask;
	public Set<UUID> actionbarInUse; 
	private final List<String> ignoredSections = Arrays.asList("Ranklist-gui.current-format.custom",
			"Ranklist-gui.completed-format.custom", "Ranklist-gui.other-format.custom",
			"Prestigelist-gui.current-format.custom", "Prestigelist-gui.completed-format.custom",
			"Prestigelist-gui.other-format.custom", "Rebirthlist-gui.current-format.custom",
			"Rebirthlist-gui.completed-format.custom", "Rebirthlist-gui.other-format.custom");
	public Map<Player, Integer> actionbarAnimationHolder;
	public Map<Player, BukkitTask> actionbarTaskHolder;
	public Set<String> disabledWorlds;
	private final List<String> oldVersions = Arrays.asList("1.15", "1.14", "1.13", "1.12", "1.11", "1.10", "1.9", "1.8", "1.7", "1.6", "1.5", "1.4");
	private final List<String> ancientVersions = Arrays.asList("1.6", "1.5", "1.4");
	public final static String PREFIX = "§e[§9PrisonRanks§cX§e]";
	// ======================
	// LISTENERS
	public IPlayerLoginListener playerLoginListener;
	public IPlayerChatListener playerChatListener;
	public IPlayerQuitListener playerQuitListener;
	public PrisonRanksXListener prisonRanksXListener;
	public InventoryListener inventoryListener;
	// ======================
	private TaskChainFactory taskChainFactory;

	public <T> TaskChain<T> newChain() {
		return taskChainFactory.newChain();
	}

	public <T> TaskChain<T> newSharedChain(String name) {
		return taskChainFactory.newSharedChain(name);
	}

	public Actionbar getActionbar() {
		return this.actionBar;
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) return false;
		econ = rsp.getProvider();
		return econ != null;
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	/**
	 * 
	 * @return Vault Permission Service.
	 */
	public Permission getPermissions() {
		return perms;
	}

	/**
	 * Starts a repeating task in which data gets updated.
	 */
	public void startAsyncUpdateTask() {
		scheduler.runTaskTimerAsynchronously
		(this, () -> performDataSave(), autoSaveTime, autoSaveTime);
	}  

	/**
	 * Performs a full player disk/MySQL data save.
	 */
	public void performDataSave() {
		long timeBefore = System.currentTimeMillis();
		if(saveNotification)
			cout(PREFIX + " §eSaving data...");
		getPlayerStorage().savePlayersData();
		long timeNow = System.currentTimeMillis() - timeBefore;
		if(saveNotification)
			cout(PREFIX + " §aData saved §7& §eit took §6(§e" + toSeconds(timeNow) + "§6)§e.");
	}

	/**
	 * Performs a full player disk/MySQL data save asynchronously.
	 */
	public void performDataSaveAsynchronously() {
		scheduler.runTaskAsynchronously(this, () -> {
			long timeBefore = System.currentTimeMillis();
			if(saveNotification)
				cout(PREFIX + " §eSaving data...");
			getPlayerStorage().savePlayersData();
			long timeNow = System.currentTimeMillis() - timeBefore;
			if(saveNotification)
				cout(PREFIX + " §aData saved §7& §eit took §6(§e" + toSeconds(timeNow) + "§6)§e.");
		});
	}

	/**
	 * Performs a full player MySQL data save asynchronously.
	 */
	public void performMySQLAsyncDataSave() {
		AtomicLong timeBefore = new AtomicLong(0);
		AtomicLong timeNow = new AtomicLong(0);
		CompletableFuture<Void> saveFuture = CompletableFuture.runAsync(() -> {
			timeBefore.set(System.currentTimeMillis());
			if(saveNotification)
				cout(PREFIX + " §eSaving data...");
			getPlayerStorage().saveLargePlayersData().join();
		});
		CompletableFuture<Void> finishFuture = saveFuture.thenRunAsync(() -> {
			timeNow.set(System.currentTimeMillis() - timeBefore.get());
		});
		finishFuture.join();
		if(saveNotification)
			cout(PREFIX + " §aData saved §7& §eit took §6(§e" + toSeconds(timeNow.get()) + "§6)§e.");
	}

	/**
	 * Performs an asynchronous disk/MySQL data save for a single player.
	 * @param u Player Unique ID
	 * @param name Player Name
	 */
	public void saveDataAsynchronously(UUID u, String name) {
		TaskChain <?> saveDataChain = taskChainFactory.newSharedChain("dataSave");
		saveDataChain.async(() -> {
			getPlayerStorage().savePlayerData(u);
			if(!isMySql()) {
				getConfigManager().saveRankDataConfig();
				getConfigManager().savePrestigeDataConfig();
				getConfigManager().saveRebirthDataConfig(); 
			} else {
				this.updateMySqlData(u);
			}
		})
		.execute();
	}

	/**
	 * Performs a disk/MySQL data save for a single player in the current thread.
	 * @param u Player Unique ID
	 * @param name Player Name
	 */
	public void saveData(UUID u) {
		TaskChain <?> saveDataChain = taskChainFactory.newSharedChain("dataSave");
		saveDataChain.current(() -> {
			getPlayerStorage().savePlayerData(u);
			getConfigManager().saveRankDataConfig();
			getConfigManager().savePrestigeDataConfig();
			getConfigManager().saveRebirthDataConfig(); 
		})
		.execute();
	}

	/**
	 * 
	 * @param time in millseconds
	 * @return converted time to seconds when applicable
	 */
	public String toSeconds(final long time) {
		if(time > 1000) {
			return String.valueOf((double)time / 1000.0) + " s";
		} else {
			return String.valueOf(time) + " ms";
		}
	}

	/**
	 * @return PrestigeMax class in which you can perform a prestigemax process for a certain player.
	 */
	public IPrestigeMax getPrestigeMax() {
		return this.prestigeMax;
	}

	/**
	 * Initiate data for balance formatting.
	 */
	public void setupBalanceFormat() {
		this.k = getGlobalStorage().getStringData("MoneyFormatter.thousand");
		this.M = getGlobalStorage().getStringData("MoneyFormatter.million");
		this.B = getGlobalStorage().getStringData("MoneyFormatter.billion");
		this.T = getGlobalStorage().getStringData("MoneyFormatter.trillion");
		this.q = getGlobalStorage().getStringData("MoneyFormatter.quadrillion");
		this.Q = getGlobalStorage().getStringData("MoneyFormatter.quintillion");
		this.s = getGlobalStorage().getStringData("MoneyFormatter.sextillion");
		this.S = getGlobalStorage().getStringData("MoneyFormatter.septillion");
		this.O = getGlobalStorage().getStringData("MoneyFormatter.octillion");
		this.N = getGlobalStorage().getStringData("MoneyFormatter.nonillion");
		this.d = getGlobalStorage().getStringData("MoneyFormatter.decillion");
		this.U = getGlobalStorage().getStringData("MoneyFormatter.undecillion");
		this.D = getGlobalStorage().getStringData("MoneyFormatter.Duodecillion");
		this.Z = getGlobalStorage().getStringData("MoneyFormatter.zillion");
		String[] abbreviations = {"",k,M,B,T,q,Q,s,S,O,N,d,U,D,Z, Z + "II", Z + "III", Z + "IV",
				Z + "V", Z + "VI", Z + "VII", Z + "VIII", Z + "IX", Z + "X", Z + "11", Z + "12",
				Z + "13", Z + "14", Z + "15", Z + "16", Z + "17" , Z + "18" , Z + "19", Z + "20",
				Z + "21", Z + "22", Z + "23", Z + "24", Z + "25", Z + "26", Z + "27", Z + "28",
				Z + "29", Z + "30", "~", "~!", "~?", "~@", "#", "^", "&", "*", "-", "+", "+2", "+3",
				"+4", "+5", "+6", "ALOT!"
		};
		this.abbreviations = abbreviations;
	}

	public void onEnable() {
		instance = this;
		scheduler = Bukkit.getScheduler();
		console = Bukkit.getConsoleSender();
		pluginManager = Bukkit.getPluginManager();
		taskChainFactory = BukkitTaskChainFactory.create(this);
		holidayUtils = new HolidayUtils();
		actionbarAnimationHolder = new WeakHashMap<>();
		actionbarTaskHolder = new WeakHashMap<>();
		String version = Bukkit.getVersion();
		commandLoader = new CommandLoader();
		if(CollectionUtils.containsFromList(version, ancientVersions)) isBefore1_7 = true;
		if(!CollectionUtils.containsFromList(version, oldVersions)) isModernVersion = true;
		saveDefaultConfig();
		try {
			ConfigUpdater.update(this, "config.yml", new File(this.getDataFolder(), "config.yml"), ignoredSections);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(!hasPlugin("Vault")) {
			cout(PREFIX + " §cUnable to find vault !");
			cout(PREFIX + " §cFailed to start, disabling....");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		} else {
			setupEconomy();
			setupPermissions();
			perm = new PermissionManager(this);
			configManager = new ConfigManager(this);
			globalStorage = isModernVersion ? new GlobalDataStorage1_16(this) : new GlobalDataStorage1_8(this);
			rankStorage = new RankDataStorage(this);
			prestigeStorage = new PrestigeDataStorage(this);
			rebirthStorage = new RebirthDataStorage(this);
			messagesStorage = new MessagesDataStorage(this);
			getConfigManager().loadConfigs();
			fireworkManager = new FireworkManager(this);
			
			if(hasPlugin("PlaceholderAPI")) {
				cout(PREFIX + " §eLoading PlaceholderAPI placeholders...");
				papi = new PapiHook(this);
				hasPAPI = true;
				placeholderReplacer = new PlaceholderReplacerPAPI();
			} else {
				hasPAPI = false;
				placeholderReplacer = new PlaceholderReplacerDefault();
			}
			if(hasPlugin("DecentHolograms")) {
				cout(PREFIX + " §aDecentHolograms hooked.");
				hologramManager = new DHHologramManager(this);
				hasHologramsPlugin = true;
			} else if (hasPlugin("HolographicDisplays")) {
				cout(PREFIX + " §aHolographicDisplays hooked.");
				hologramManager = new HDHologramManager(this);
				hasHologramsPlugin = true;
			} else {
				cout(PREFIX + " §2Started without a Hologram plugin.");
			}
			if(hasPlugin("MVdWPlaceholderAPI")) {
				cout(PREFIX + " §eLoading MVdWPlaceholderAPI placeholders...");
				mvdw = new MVdWPapiHook(this);
				mvdw.registerPlaceholders();
				hasMVdWPAPI = true;
				cout(PREFIX + " §aMVdWPlaceholderAPI hooked.");
			} else {
				hasMVdWPAPI = false;
				cout(PREFIX + " §2Started without MVdWPlaceholderAPI.");
			}
			if(hasPlugin("ActionUtil")) {
				isActionUtil = true;
				cout(PREFIX + " §aActionUtil detected.");
			} else {
				cout(PREFIX + " §2Started without ActionUtil.");
			}

			if(isVaultGroups) this.vaultPlugin = getGlobalStorage().getStringData("Options.rankup-vault-groups-plugin");

			if(isVaultGroups && vaultPlugin != null) {
				if(hasPlugin("LuckPerms") && vaultPlugin.equalsIgnoreCase("LuckPerms")) {
					EZLuckPerms.getLuckPerms();
					vaultDataUpdater = new LPVaultDataUpdater(this);
				} else if (hasPlugin("GroupManager") && vaultPlugin.equalsIgnoreCase("GroupManager")) {
					groupManager = new GMHook(this);
					vaultDataUpdater = new GMVaultDataUpdater(this);
				} else if (hasPlugin("PermissionsEX") && vaultPlugin.equalsIgnoreCase("PermissionsEX")) {
					PermissionsEx.getPermissionManager();
					vaultDataUpdater = new PEXVaultDataUpdater(this);
				} else if (vaultPlugin.equalsIgnoreCase("Vault")) {
					vaultDataUpdater = new VaultDataUpdater(this);
				} else {
					vaultDataUpdater = new CommandVaultDataUpdater(this);
				}
			}
			chatColorReplacer = isModernVersion ? new ChatColorReplacer1_16(this) : new ChatColorReplacer1_8(this);
			if(hasPAPI) {
				papi.register();
				cout(PREFIX + " §aPlaceholderAPI hooked.");
			} else {
				cout(PREFIX + " §2Started without PlaceholderAPI.");
			}
			if(!isBefore1_7) this.actionBar = new ActionbarLegacy();
			getGlobalStorage().loadGlobalData();
			isInfinitePrestige = getGlobalStorage().getBooleanData("Options.infinite-prestige");
			getRankDataStorage().loadRanksData();
			if(isInfinitePrestige) {
				infinitePrestigeSettings = new InfinitePrestigeSettings(this);
				infinitePrestigeSettings.load();
				prestigeStorage = new PrestigeDataStorageInfinite(this);
				cout(PREFIX + " §7Infinite Prestige: §aON");
			} 
			isMySql = getGlobalStorage().getBooleanData("MySQL.enable");
			isVaultGroups = getGlobalStorage().getBooleanData("Options.rankup-vault-groups");
			isRankEnabled = getGlobalStorage().getBooleanData("Options.rank-enabled");
			isPrestigeEnabled = getGlobalStorage().getBooleanData("Options.prestige-enabled");
			isRebirthEnabled = getGlobalStorage().getBooleanData("Options.rebirth-enabled");
			getPrestigeDataStorage().loadPrestigesData();
			getRebirthDataStorage().loadRebirthsData();
			getMessagesStorage().loadMessages(); 
			playerStorage = new PlayerDataStorage(this);
			prxAPI = new PRXAPI();
			prxAPI.setup();
			setupMySQL();
			lbm = new LeaderboardManager(this);	
			if(hasPAPI) papi.refreshValues(this);
			if(holidayUtils.getHoliday() == Holiday.CHRISTMAS_EVE) {
				cout(PREFIX + " §2Merry §4Christmas§f!");
			} else if (holidayUtils.getHoliday() == Holiday.HALLOWEEN_DAY) {
				cout(PREFIX + " §6Happy Halloween§7!");
			}
			if(getConfigManager().commandsConfig.getBoolean("commands.rankup.enable", true)) {
				rankupCommand = new RankupCommand("rankup");
				commandLoader.registerCommand("rankup", rankupCommand);
			}
			if(getConfigManager().commandsConfig.getBoolean("commands.prestige.enable", true)) {
				prestigeCommand = new PrestigeCommand("prestige");
				commandLoader.registerCommand("prestige", prestigeCommand);
			}
			if(getConfigManager().commandsConfig.getBoolean("commands.rankupmax.enable", true)) {
				rankupMaxCommand = new RankupMaxCommand("rankupmax");
				commandLoader.registerCommand("rankupmax", rankupMaxCommand);
			}
			if(getConfigManager().commandsConfig.getBoolean("commands.ranks.enable", true)) {
				ranksCommand = new RanksCommand("ranks");
				commandLoader.registerCommand("ranks", ranksCommand);
			}
			if(getConfigManager().commandsConfig.getBoolean("commands.rebirth.enable", true)) {
				rebirthCommand = new RebirthCommand("rebirth");
				commandLoader.registerCommand("rebirth", rebirthCommand);
			}
			if(getConfigManager().commandsConfig.getBoolean("commands.prestiges.enable", true)) {
				prestigesCommand = new PrestigesCommand("prestiges");
				commandLoader.registerCommand("prestiges", prestigesCommand);
			}
			if(getConfigManager().commandsConfig.getBoolean("commands.rebirths.enable", true)) {
				rebirthsCommand = new RebirthsCommand("rebirths");
				commandLoader.registerCommand("rebirths", rebirthsCommand);
			}
			if(getConfigManager().commandsConfig.getBoolean("commands.prx.enable", true)) {
				prxCommand = new PRXCommand("prx");
				commandLoader.registerCommand("prx", prxCommand);
			}
			if(getConfigManager().commandsConfig.getBoolean("commands.autorankup.enable", true)) {
				autoRankupCommand = new AutoRankupCommand("autorankup");
				commandLoader.registerCommand("autorankup", autoRankupCommand);
			}
			if(getConfigManager().commandsConfig.getBoolean("commands.autoprestige.enable", true)) {
				autoPrestigeCommand = new AutoPrestigeCommand("autoprestige");
				commandLoader.registerCommand("autoprestige", autoPrestigeCommand);
			}
			if(getConfigManager().commandsConfig.getBoolean("commands.forcerankup.enable", true)) {
				forceRankupCommand = new ForceRankupCommand("forcerankup");
				commandLoader.registerCommand("forcerankup", forceRankupCommand);
			}
			if(getConfigManager().commandsConfig.getBoolean("commands.prestigemax.enable", true)) {
				prestigeMaxCommand = new PrestigeMaxCommand("prestigemax");
				commandLoader.registerCommand("prestigemax", prestigeMaxCommand);
			}	  

			setupBalanceFormat();

			autoSaveTime = getGlobalStorage().getIntegerData("Options.autosave-time");
			forceSave = getGlobalStorage().getBooleanData("Options.forcesave");
			isRankupMaxWarpFilter = getGlobalStorage().getBooleanData("Options.rankupmax-warp-filter");
			checkVault = getGlobalStorage().getBooleanData("Options.rankup-vault-groups-check");
			allowEasterEggs = getGlobalStorage().getBooleanData("Options.allow-easter-eggs");
			disabledWorlds = Sets.newHashSet(getGlobalStorage().getStringListData("worlds"));

			try {
				ConfigUpdater.update(this, "messages.yml", new File(this.getDataFolder() + "/messages.yml"), new ArrayList<String>());
			} catch (IOException e) {
				e.printStackTrace();
			}

			if(getConfigManager().commandsConfig.getBoolean("commands.topprestiges.enable")) {
				topPrestigesCommand = new TopPrestigesCommand("topprestiges");
				commandLoader.registerCommand("topprestiges", topPrestigesCommand);
				topPrestigesCommand.load();
			}
			if(getConfigManager().commandsConfig.getBoolean("commands.toprebirths.enable")) {
				topRebirthsCommand = new TopRebirthsCommand("toprebirths");
				commandLoader.registerCommand("toprebirths", topRebirthsCommand);
				topRebirthsCommand.load();
			}
			if(!isBefore1_7) {
				rankupAPI = new Rankup();
				prestigeAPI = new Prestige();
				rebirthAPI = new Rebirth();
				rankupMaxAPI = new me.prisonranksx.api.RankupMax();
			} else {
				cout(PREFIX + " §eDetected an ancient version!");
				cout(PREFIX + " §aSwitching to legacy classes.");
				rankupLegacy = new RankupLegacy();
				prestigeLegacy = new PrestigeLegacy();
				rebirthLegacy = new RebirthLegacy();
				rankupMaxLegacy = new RankupMaxLegacy();
			}
			ranksAPI = new Ranks();
			ranksAPI.load();
			prestigesAPI = new Prestiges();
			prestigesAPI.load();
			rebirthsAPI = new Rebirths();
			rebirthsAPI.load();
			manager = new PRXManager(this);
			prxAPI.loadProgressBars();
			prxAPI.loadPermissions();
			PaginatedGUI.prepare(this);
			cim = new CustomItemsManager();
			cri = new CustomRankItems(this);
			cpi = new CustomPrestigeItems(this);
			crri = new CustomRebirthItems(this);
			cri.setup();
			cpi.setup();
			crri.setup();
			prestigeMax = isBefore1_7 ? new PrestigeMaxLegacy(this) : new PrestigeMax(this);
		}
		guiManager = new GuiListManager(this);
		if(!isBefore1_7) {
			guiManager.setupConstantItems();
			abprogress = new ActionbarProgress(this);
		}
		ebprogress = new ExpbarProgress(this);
		isEBProgress = getGlobalStorage().getBooleanData("Options.expbar-progress");
		isABProgress = isBefore1_7 ? false : getGlobalStorage().getBooleanData("Options.actionbar-progress");
		isSaveOnLeave = getGlobalStorage().getBooleanData("Options.save-on-leave");
		saveNotification = getGlobalStorage().getBooleanData("Options.save-notification");
		isEnabledInsteadOfDisabled = getGlobalStorage().getBooleanData("Options.enabled-worlds-instead-of-disabled");
		actionbarInUse = new HashSet<>();
		errorInspector = new ErrorInspector(this);
		errorInspector.inspect();
		errorInspector.validateRanks(console);
		errorInspector.validatePrestiges(console);
		if(getGlobalStorage().getBooleanData("Options.autosave")) startAsyncUpdateTask();	
		registerListeners();
		instance = this;
		if(!OnlinePlayers.isEmpty()) {
			OnlinePlayers.getPlayers().forEach(player -> {
				this.playerLoginListener.registerUserData(XUUID.getUUID(player), player.getName());
				if(!isABProgress)
					return;
				abprogress.enable(player);
			});
		}
		cout(PREFIX + " §aEnabled.");
	}

	/**
	 * Registers plugin listeners in the other classes.
	 */
	public void registerListeners() {
		playerLoginListener = isBefore1_7 ? new PlayerLoginListenerLegacy(this) : new PlayerLoginListener(this);
		playerQuitListener = isBefore1_7 ? new PlayerQuitListenerLegacy(this) : new PlayerQuitListener(this);
		prisonRanksXListener = new PrisonRanksXListener(this);
		inventoryListener = new InventoryListener(this);
		rankForceDisplay = getGlobalStorage().getBooleanData("Options.force-rank-display");
		prestigeForceDisplay = getGlobalStorage().getBooleanData("Options.force-prestige-display");
		rebirthForceDisplay = getGlobalStorage().getBooleanData("Options.force-rebirth-display");
		formatChat = getGlobalStorage().getBooleanData("Options.format-chat");
		boolean messWithChat = false;
		if(rankForceDisplay || prestigeForceDisplay || rebirthForceDisplay) {
			playerChatListener = new PlayerChatListenerForceDisplay(this);
			messWithChat = true;
		} else {
			if(formatChat) {
				playerChatListener = new PlayerChatListener(this);
				messWithChat = true;
			}
		}
		pluginManager.registerEvents(playerLoginListener, this);
		String loginPriority = getGlobalStorage().getStringData("Options.login-event-handling-priority");
		if(!loginPriority.equals("NORMAL") && loginPriority != null)
		EventPriorityManager.setPriorities(playerLoginListener, getGlobalStorage().getStringData("Options.login-event-handling-priority"));
		pluginManager.registerEvents(playerQuitListener, this);
		pluginManager.registerEvents(prisonRanksXListener, this);
		pluginManager.registerEvents(inventoryListener, this);
		if(!messWithChat) return;
		pluginManager.registerEvents(playerChatListener, this);
		String chatPriority = getGlobalStorage().getStringData("Options.chat-event-handling-priority");
		if(!chatPriority.equals("NORMAL") && chatPriority != null)
		EventPriorityManager.setPriorities(playerChatListener, getGlobalStorage().getStringData("Options.chat-event-handling-priority"));
	}

	/**
	 * Unregister listeners from the other classes.
	 */
	public void unregisterListeners() {
		if(playerChatListener != null)
		playerChatListener.unregister();
		if(playerLoginListener != null)
		playerLoginListener.unregister();
		if(playerQuitListener != null)
		playerQuitListener.unregister();
		if(prisonRanksXListener != null)
		prisonRanksXListener.unregister();
		if(inventoryListener != null)
		inventoryListener.unregister();
	}

	/**
	 * 
	 * @return Whether MySQL data storage is being used or not.
	 */
	public boolean isMySql() {
		return this.isMySql;
	}

	/**
	 * 
	 * @return A MySQL statement that gets recreated once its closed.
	 */
	public Statement getMySqlStatement() {
		try {
			if(this.statement == null || this.statement.isClosed()) {
				try {
					openConnection();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				statement = getConnection().createStatement(); 
				return statement;
			}
		} catch (SQLException e) {
			System.out.println("Statement is either closed or null.");
			e.printStackTrace();
		}
		return this.statement;
	}

	/**
	 * Initiate MySQL data.
	 */
	public void setupMySQL() {
		host = String.valueOf(getGlobalStorage().getStringData("MySQL.host"));
		port = getGlobalStorage().getIntegerData("MySQL.port");
		database = getGlobalStorage().getStringData("MySQL.database");
		username = getGlobalStorage().getStringData("MySQL.username");
		password = getGlobalStorage().getStringData("MySQL.password");   
		table = getGlobalStorage().getStringData("MySQL.table"); 
		useSSL = getGlobalStorage().getBooleanData("MySQL.useSSL");
		autoReconnect = getGlobalStorage().getBooleanData("MySQL.autoReconnect");
		useCursorFetch = getGlobalStorage().getBooleanData("MySQL.useCursorFetch");
		if(isMySql) {
			try {    
				openConnection();
				statement = getConnection().createStatement();  
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + getDatabase() + "." + table + " (`uuid` varchar(255), `name` varchar(255), `rank` varchar(255), `prestige` varchar(255), `rebirth` varchar(255), `path` varchar(255), `rankscore` int(5), `prestigescore` int(10), `rebirthscore` int(10), `stagescore` int(24));");
				this.sqlDataUpdater = new MySQLDataUpdater(this);
				try {
					System.out.println("Checking for database update...");
					getMySqlStatement().executeUpdate("ALTER TABLE " + getDatabase() + "." + getTable() + " ADD `rankscore` int(5) AFTER `path`, ADD `prestigescore` int(10) AFTER `rankscore`, ADD `rebirthscore` int(10) AFTER `prestigescore`, ADD `stagescore` int(24) AFTER `rebirthscore`;");
					System.out.println("Database update successful.");
				} catch (SQLException e) {
					System.out.println("Database is up to date.");
				}
				cout(PREFIX + " §aSuccessfully connected to the database.");
			} catch (ClassNotFoundException e) {
				cout(PREFIX + " §cDatabase class couldn't be found.");
				e.printStackTrace();
				getLogger().info("MySQL connection failed.");
			} catch (SQLException e) {
				cout(PREFIX + " §cSQL error occurred.");
				e.printStackTrace();
				getLogger().info("MySQL SQL error occurred.");
			}
		}

	}

	/**
	 * 
	 * @return Whether force-save option is being used or not.
	 */
	public boolean isForceSave() {
		return forceSave;
	}

	/**
	 * Opens a new MySQL connection.
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public void openConnection() throws SQLException, ClassNotFoundException {
		if (getConnection() != null && !getConnection().isClosed()) {
			return;
		}
		synchronized (this) {
			if (getConnection() != null && !getConnection().isClosed()) {
				return;
			}
			Class.forName("com.mysql.jdbc.Driver");
			Properties prop = new Properties();
			prop.setProperty("user", username);
			prop.setProperty("password", password);
			prop.setProperty("useSSL", String.valueOf(useSSL));
			prop.setProperty("autoReconnect", String.valueOf(autoReconnect));
			if(useCursorFetch) prop.setProperty("useCursorFetch", String.valueOf(useCursorFetch));
			setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.getDatabase() + "?characterEncoding=utf8", prop));
		}
	}

	/**
	 * Closes the MySQL connection.
	 */
	public void closeConnection() {
		try {
			if(getConnection() != null && !getConnection().isClosed()) {
				getConnection().close();
			}
		} catch (SQLException e) {
			cout(PREFIX + " §cCouldn't close database connection.");
			e.printStackTrace();
		}
	}

	public void updateMySqlData(final Player player, final String rank, final String prestige, final String rebirth, final String path) {
		sqlDataUpdater.updateMySqlData(player, rank, prestige, rebirth, path);
	}

	public void updateMySqlData(final Player player) {
		sqlDataUpdater.updateMySqlData(player);
	}

	public void updateMySqlData(final UUID uuid) {
		sqlDataUpdater.updateMySqlData(uuid);
	}

	public void updateMySqlData(final UUID uuid, final String name) {
		sqlDataUpdater.updateMySqlData(uuid, name);
	}

	public void onDisable() {
		if(terminateMode) {
			closeConnection();
			cout(PREFIX + " §4Plugin terminated.");
			return;
		}
		cout(PREFIX + " §eSaving data...");
		getPlayerStorage().savePlayersData(true);
		getConfigManager().saveRankDataConfig();
		getConfigManager().savePrestigeDataConfig();
		getConfigManager().saveRebirthDataConfig();
		unregisterListeners();
		getPlayerStorage().getPlayerData().clear();
		getRankDataStorage().getEntireData().clear();
		getPrestigeDataStorage().getPrestigeData().clear();
		getRebirthDataStorage().getRebirthData().clear();
		getGlobalStorage().getDoubleMap().clear();
		getGlobalStorage().getStringMap().clear();
		getGlobalStorage().getBooleanMap().clear();
		getGlobalStorage().getStringListMap().clear();
		getGlobalStorage().getIntegerMap().clear();
		getGlobalStorage().getStringSetMap().clear();
		getGlobalStorage().getGlobalMap().clear();
		isActionUtil = false;
		hasPAPI = false;
		hasHologramsPlugin = false;
		if(isABProgress) abprogress.clear(true);
		closeConnection();
		cout(PREFIX + " §aData saved.");
		cout(PREFIX + " §cDisabled.");
	}

	public void debug(String message) {
		if(debug) Bukkit.broadcastMessage(getString("&9[DEBUG] " + message));
	}

	public void debugPreEnable(String message) {
		if(debug) System.out.println("[DEBUG] " + message);		
	}

	@SuppressWarnings({ "rawtypes" })
	public void debug(Object message) {
		if(debug) {
			String prefix = "&9[&7DEBUG&9] &6";
			if(message instanceof Integer) {
				int msg = (int)message;
				Bukkit.broadcastMessage(getString(prefix + String.valueOf(msg)));
			} else if (message instanceof Double) {
				double msg = (double)message;
				Bukkit.broadcastMessage(getString(prefix + String.valueOf(msg)));
			} else if (message instanceof UUID) {
				UUID uuid = (UUID)message;
				Bukkit.broadcastMessage(getString(prefix + uuid.toString()));
			} else if (message instanceof List) {
				List msg = (List)message;
				Bukkit.broadcastMessage(getString(prefix + msg.toString()));
			} else if (message instanceof Set) {
				Set msg = (Set)message;
				Bukkit.broadcastMessage(getString(prefix + msg.toString()));
			} else if (message instanceof Deque) {
				Deque msg = (Deque)message;
				Bukkit.broadcastMessage(getString(prefix + msg.toString()));
			} else if (message instanceof Collection) {
				Collection msg = (Collection)message;
				Bukkit.broadcastMessage(getString(prefix + msg.toString()));
			} else if (message instanceof Map) {
				Map map = (Map)message;
				Bukkit.broadcastMessage(getString(prefix + map.entrySet().toString()));
			} else if (message instanceof RankPath) {
				RankPath msg = (RankPath)message;
				Bukkit.broadcastMessage(getString(prefix + msg.get()));
			} else if (message instanceof Number) {
				Number msg = (Number)message;
				Bukkit.broadcastMessage(getString(prefix + String.valueOf(msg)));
			} else {
				Bukkit.broadcastMessage(getString(prefix + message.toString()));
			}
		}
	}


	// one line methods
	public PlayerDataStorage getPlayerStorage() { return this.playerStorage; }
	public IPrestigeDataStorage getPrestigeDataStorage() { return this.prestigeStorage; }
	public RankDataStorage getRankDataStorage() { return this.rankStorage; }
	public RebirthDataStorage getRebirthDataStorage() { return this.rebirthStorage; }
	public GlobalDataStorage getGlobalStorage() { return this.globalStorage; }
	public void cout(String string) { console.sendMessage(string); }
	public boolean hasPlugin(String pluginName) { return pluginManager.isPluginEnabled(pluginName); }
	public boolean hasActionbarOn(UUID uuid) { return actionbarInUse.contains(uuid); }
	public Color getColor(String temp) { return fireworkManager.getColor(temp); }
	public void sendRebirthFirework(Player p) { fireworkManager.sendRebirthFirework(p); }
	public void sendPrestigeFirework(Player p) { fireworkManager.sendPrestigeFirework(p); }
	public void sendRankFirework(Player p) { fireworkManager.sendRankFirework(p); }

	/**
	 * 
	 * @param player the player that will receive the action bar messages
	 * @param interval action bar animation in ticks // 20 ticks = 1 second
	 * @param actionbarLines action bar messages to be sent and animated
	 */
	public void animateActionbar(Player player, Integer interval, List<String> actionbarLines) {
		if(isBefore1_7 || actionbarLines == null) return;
		if(actionbarLines.size() == 0) return;
		Player p = player;
		String name = p.getName();
		UUID uuid = p.getUniqueId();
		actionbarInUse.add(uuid);
		int linesAmount = actionbarLines.size();
		if(linesAmount == 1) {
			getActionbar().sendActionBar(p, getString(actionbarLines.get(0), name).replace("%rankup%", prxAPI.getPlayerRank(p)).replace("%rankup_display%", getString(prxAPI.getPlayerRankDisplay(p), name)));
			return;
		}
		BukkitTask savedTask = actionbarTaskHolder.get(p);
		if(savedTask != null) savedTask.cancel();
		// skip older action bar
		actionbarTaskHolder.put(p, null);
		actionbarTask = actionbarTaskHolder.get(p);
		// put new one
		actionbarAnimationHolder.put(p, 0);
		actionbarTask = new BukkitRunnable() {
			public void run() {
				if(actionbarTaskHolder.containsKey(p)) actionbarTaskHolder.put(p, actionbarTask);
				boolean animationEnded = actionbarAnimationHolder.get(p) >= linesAmount;
				if(animationEnded) {
					scheduler.runTaskLaterAsynchronously(instance, () -> actionbarInUse.remove(uuid), 20);
					cancel();
					return;
				}
				String currentLine = actionbarLines.get(actionbarAnimationHolder.get(p));
				getActionbar().sendActionBar(p, getString(currentLine, name).replace("%rankup%", getPlayerStorage().getPlayerRank(p)).replace("%rankup_display%", getString(prxAPI.getPlayerRankDisplay(p), name)));
				if(!animationEnded) actionbarAnimationHolder.put(p, actionbarAnimationHolder.get(p)+1);
			}
		}.runTaskTimerAsynchronously(this, 1L, interval);
	}

	public void sendListMessage(Player p, List<String> stringLines) {
		stringLines.forEach(messageLine -> p.sendMessage(getString(messageLine, p.getName())));
	}

	public void sendListMessage(CommandSender s, List<String> stringLines) {
		stringLines.forEach(messageLine -> s.sendMessage(getString(messageLine, s.getName()).replace("%player%", s.getName())));
	}

	public void sendListMessage(String playerName, List<String> stringLines) {
		sendListMessage(Bukkit.getPlayer(playerName), stringLines);
	}

	public void executeCachedCommandsWithWarpFilter(Player player, RankPath rank) {
		String rankpath = rank.get();
		Player p = player;
		String name = p.getName();
		if(getRankDataStorage().getConsoleCommands().containsKey(rankpath)) {
			getRankDataStorage().getConsoleCommands().get(rankpath).forEach(commandLine ->
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getString(commandLine.replace("%player%", name), player)));	
		}
		if(getRankDataStorage().getPlayerCommands().containsKey(rankpath)) {
			getRankDataStorage().getPlayerCommands().get(rankpath).stream()
			.filter(commandLine ->
			!commandLine.contains("warp"))
			.forEach(commandLine -> 
			Bukkit.dispatchCommand(p, commandLine.replace("%player%", name)));
		}
	}

	public void executeCachedCommands(Player player, RankPath rank) {
		String rankpath = rank.get();
		Player p = player;
		String name = p.getName();
		if(getRankDataStorage().getConsoleCommands().containsKey(rankpath)) {
			for(String string : getRankDataStorage().getConsoleCommands().get(rankpath)) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getString(string.replace("%player%", name), player));
			}
		}
		if(getRankDataStorage().getPlayerCommands().containsKey(rankpath)) {
			for(String string : getRankDataStorage().getPlayerCommands().get(rankpath)) {
				Bukkit.dispatchCommand(p, getString(string.replace("%player%", name), player));
			}
		}
	}

	public void executeCommandsSafely(Player player, List<String> stringList) {
		Player p = player;
		for(String command : stringList) {
			if(command.startsWith("[console]")) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getString(command.substring(10).replace("%player%", p.getName()), player));
			} else if (command.startsWith("[player]")) {
				Bukkit.dispatchCommand(p, command.substring(9).replace("%player%", p.getName()));
			} else {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", p.getName()));
			}
		}
	}

	public void executeCommands(Player player, final List<String> commandsList) {
		if(commandsList.isEmpty()) return;
		newSharedChain("command").current(() -> {
			scheduler.runTaskLater(this, () ->{
				Player p = player;
				for(String command : commandsList) {
					if(command.startsWith("[console]")) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getString(command.substring(10).replace("%player%", p.getName()), player));
					} else if (command.startsWith("[player]")) {
						Bukkit.dispatchCommand(p, command.substring(9).replace("%player%", p.getName()));
					} else {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", p.getName()));
					}
				}
			}, 1);
		}).execute();
	}

	public void executeCommand(Player player, String command) {
		Player p = player;
		scheduler.runTask(this, () -> {
			if(command.startsWith("[console]")) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getString(command.substring(10).replace("%player%", p.getName()), player));
			} else if (command.startsWith("[player]")) {
				Bukkit.dispatchCommand(p, command.substring(9).replace("%player%", p.getName()));
			} else {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", p.getName()));
			}
		});
	}

	public String formatBalance(double y)
	{
		if(y > 999) {
			double x = y / Math.pow(10, Math.floor(Math.log10(y) / 3) * 3);
			return abb.format(x) + abbreviations[((int) Math.floor(Math.log10(y) / 3))];   
		}
		return String.valueOf(y);
	}

	public String getArgs(String[] args, int num) { 
		StringBuilder sb = new StringBuilder();
		for(int i = num; i < args.length; i++) {
			sb.append(args[i]).append(" ");
		}
		return sb.toString().trim();
	}

	/**
	 * 
	 * @param text to centerize
	 * @return sets the message to the center if it starts with [center] prefix
	 */
	public String center(String text) {
		if(text.startsWith("[center]")) return MessageCenterizer.centerMessage(text.substring(8));
		return text;
	}

	/**
	 * 
	 * @param text to process and format
	 * @param playerName player name whom the placeholders should be parsed for
	 * @return Formatted colored string with symbols and optionally, PlaceholderAPI placeholders.
	 */
	public String getString(String text, String playerName) {
		return center(getChatColorReplacer().parsePlaceholders(text, playerName));
	}

	/**
	 * 
	 * @param text to process and format
	 * @param player the player whom the placeholders should be parsed for
	 * @return Formatted colored string with symbols and optionally, PlaceholderAPI placeholders.
	 */
	public String getString(String text, Player player) {
		return center(getChatColorReplacer().parsePlaceholders(text, player));
	}

	/**
	 * 
	 * @param text to process and format
	 * @return Formatted colored string with symbols.
	 */
	public String getString(String text) {
		return center(getChatColorReplacer().parsePlaceholders(text));
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public String getTable() {
		return this.table;
	}

	public HolidayUtils getHolidayUtils() {
		return this.holidayUtils;
	}

	public ConfigManager getConfigManager() {
		return this.configManager;
	}

	public CustomRankItems getCustomRankItems() {
		return cri;
	}

	public void setCustomRankItems(CustomRankItems cri) {
		this.cri = cri;
	}

	public CustomPrestigeItems getCustomPrestigeItems() {
		return cpi;
	}

	public void setCustmPrestigeItems(CustomPrestigeItems cpi) {
		this.cpi = cpi;
	}

	public CustomRebirthItems getCustomRebirthItems() {
		return crri;
	}

	public void setCustomRebirthItems(CustomRebirthItems crri) {
		this.crri = crri;
	}

	public GuiListManager getGuiManager() {
		return guiManager;
	}

	public void setGuiManager(GuiListManager guiManager) {
		this.guiManager = guiManager;
	}

	public CustomItemsManager getCustomItemsManager() {
		return this.cim;
	}

	public PlaceholderReplacer getPlaceholderReplacer() {
		return this.placeholderReplacer;
	}

	public ChatColorReplacer getChatColorReplacer() {
		return this.chatColorReplacer;
	}

	public TaskChainFactory getTaskChainFactory() {
		return this.taskChainFactory;
	}

	public boolean isInDisabledWorld(Player p) {
		String worldName = p.getWorld().getName();
		return disabledWorlds.contains(worldName) != isEnabledInsteadOfDisabled;
	}

	public boolean isInDisabledWorld(CommandSender sender) {
		if(!(sender instanceof Player)) {
			return false;
		}
		Player p = (Player)sender;
		return disabledWorlds.contains(p.getWorld().getName()) != isEnabledInsteadOfDisabled;
	}

	public static PrisonRanksX getInstance() {
		return instance;
	}

	public static void setInstance(PrisonRanksX instance) {
		PrisonRanksX.instance = instance;
	}

	public FireworkManager getFireworkManager() {
		return this.fireworkManager;
	}

	public MessagesDataStorage getMessagesStorage() {
		return this.messagesStorage;
	}

}
