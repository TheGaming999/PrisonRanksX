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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
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
import me.prisonranksx.events.RankUpdateCause;
import me.prisonranksx.events.AsyncAutoPrestigeEvent;
import me.prisonranksx.events.AsyncAutoRankupEvent;
import me.prisonranksx.events.AsyncPrestigeMaxEvent;
import me.prisonranksx.events.AsyncRankupMaxEvent;
import me.prisonranksx.events.PrestigeUpdateEvent;
import me.prisonranksx.events.RankUpdateEvent;
import me.prisonranksx.events.RebirthUpdateEvent;
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
import me.prisonranksx.listeners.PlayerChatListener;
import me.prisonranksx.listeners.PlayerChatListenerForceDisplay;
import me.prisonranksx.listeners.PlayerLoginListener;
import me.prisonranksx.listeners.PlayerLoginListenerLegacy;
import me.prisonranksx.permissions.PermissionManager;
import me.prisonranksx.reflections.Actionbar;
import me.prisonranksx.reflections.ActionbarLegacy;
import me.prisonranksx.reflections.ActionbarProgress;
import me.prisonranksx.reflections.ExpbarProgress;
import me.prisonranksx.utils.XUUID;
import me.prisonranksx.utils.HolidayUtils.Holiday;
import me.prisonranksx.utils.AtomicObject;
import me.prisonranksx.utils.ChatColorReplacer;
import me.prisonranksx.utils.ChatColorReplacer1_16;
import me.prisonranksx.utils.ChatColorReplacer1_8;
import me.prisonranksx.utils.CollectionUtils;
import me.prisonranksx.utils.CommandLoader;
import me.prisonranksx.utils.ConfigManager;
import me.prisonranksx.utils.ConfigUpdater;
import me.prisonranksx.utils.HolidayUtils;
import me.prisonranksx.utils.LuckPermsUtils;
import me.prisonranksx.utils.MessageCenterizer;
import me.prisonranksx.utils.OnlinePlayers;
import me.prisonranksx.utils.PlaceholderReplacer;
import me.prisonranksx.utils.PlaceholderReplacerDefault;
import me.prisonranksx.utils.PlaceholderReplacerPAPI;

import com.google.common.collect.Sets;

import cloutteam.samjakob.gui.types.PaginatedGUI;
import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PrisonRanksX extends JavaPlugin implements Listener {
	
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
	private String host, database, username, password;
	private String table;
	private Statement statement;
	private MySQLDataUpdater sqlDataUpdater;
	// ======================
	// INTERFACE FIELDS
	public GlobalDataStorage globalStorage;
	public LuckPerms luckperms;
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
	public LuckPermsUtils lpUtils;
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
	private String k;
	private String M;
	private String B;
	private String T;
	private String q;
	private String Q;
	private String s;
	private String S;
	private String O;
	private String N;
	private String d;
	private String U;
	private String D;
	private String Z;
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
    
    public void setupLuckPerms() {
    	RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
    	if (provider != null) {
    		luckperms = provider.getProvider();
    	}
    }
	 
	private boolean setupEconomy() {
	     if (getServer().getPluginManager().getPlugin("Vault") == null) {
	          return false;
	     }
	     RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
	     if (rsp == null) {
	          return false;
	     }
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
	    	(this, () -> simulateAsyncAutoDataSave(), autoSaveTime, autoSaveTime);
	}  
	
	/**
	 * Performs a full player disk/MySQL data save.
	 */
	public void simulateAsyncAutoDataSave() {
		long timeBefore = System.currentTimeMillis();
    	if(saveNotification)
    		cout(PREFIX + " §eSaving data...");
    	getPlayerStorage().savePlayersData();
    	long timeNow = System.currentTimeMillis() - timeBefore;
    	if(saveNotification)
    		cout(PREFIX + " §aData saved §7& §eit took §6(§e" + toSeconds(timeNow) + "§6)§e.");
	}
	
	/**
	 * Performs an asynchronous disk/MySQL data save for a single player.
	 * @param u Player Unique ID
	 * @param name Player Name
	 */
    public void saveDataAsynchronously(UUID u, String name) {
	    TaskChain <?> tc = taskChainFactory.newSharedChain("dataSave");
		tc.delay(30)
		.async(() -> {
			playerStorage.savePlayerData(u);
			if(!isMySql()) {
				configManager.saveRankDataConfig();
				configManager.savePrestigeDataConfig();
				configManager.saveRebirthDataConfig(); 
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
	    TaskChain <?> tc = taskChainFactory.newSharedChain("dataSave");
		tc.delay(30)
		.current(() -> {
			playerStorage.savePlayerData(u);
			configManager.saveRankDataConfig();
			configManager.savePrestigeDataConfig();
			configManager.saveRebirthDataConfig(); 
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
		this.k = globalStorage.getStringData("MoneyFormatter.thousand");
		this.M = globalStorage.getStringData("MoneyFormatter.million");
		this.B = globalStorage.getStringData("MoneyFormatter.billion");
		this.T = globalStorage.getStringData("MoneyFormatter.trillion");
		this.q = globalStorage.getStringData("MoneyFormatter.quadrillion");
		this.Q = globalStorage.getStringData("MoneyFormatter.quintillion");
		this.s = globalStorage.getStringData("MoneyFormatter.sextillion");
		this.S = globalStorage.getStringData("MoneyFormatter.septillion");
		this.O = globalStorage.getStringData("MoneyFormatter.octillion");
		this.N = globalStorage.getStringData("MoneyFormatter.nonillion");
		this.d = globalStorage.getStringData("MoneyFormatter.decillion");
		this.U = globalStorage.getStringData("MoneyFormatter.undecillion");
		this.D = globalStorage.getStringData("MoneyFormatter.Duodecillion");
		this.Z = globalStorage.getStringData("MoneyFormatter.zillion");
	    String[] abbreviations = {"",k,M,B,T,q,Q,s,S,O,N,d,U,D,Z, v("II"), v("III"), v("IV"), v("V"), v("VI"), v("VII"), v("VIII"), v("IX"), v("X")
	    		, v("11"), v("12"), v("13"), v("14"), v("15"), v("16"), v("17") , v("18") , v("19"), v("20"), v("21"), v("22"), v("23"), v("24"), v("25"), v("26")
	    		, v("27"), v("28"), v("29"), v("30"), "~", "~!", "~?", "~@", "#", "^", "&", "*", "-", "+", "+2", "+3", "+4", "+5", "+6", "ALOT!"
	    };
	    this.abbreviations = abbreviations;
	}
	
	public void onEnable() {
		instance = this;
		scheduler = Bukkit.getScheduler();
		console = Bukkit.getConsoleSender();
		pluginManager = Bukkit.getPluginManager();
		taskChainFactory = BukkitTaskChainFactory.create(this);
		holidayUtils = new HolidayUtils(this);
		holidayUtils.setup();
		actionbarAnimationHolder = new HashMap<>();
		actionbarTaskHolder = new HashMap<>();
        String version = Bukkit.getVersion();
        commandLoader = new CommandLoader();
    	if(CollectionUtils.containsFromList(version, ancientVersions)) isBefore1_7 = true;
    	if(!CollectionUtils.containsFromList(version, oldVersions)) isModernVersion = true;
		Bukkit.getPluginManager().registerEvents(this, this);
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
	 	try {
			ConfigUpdater.update(this, "config.yml", new File(this.getDataFolder() + "/config.yml"), ignoredSections);
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
			configManager.loadConfigs();
			fireworkManager = new FireworkManager(this);
			chatColorReplacer = isModernVersion ? new ChatColorReplacer1_16(this) : new ChatColorReplacer1_8(this);
		if(!isBefore1_7) this.actionBar = new ActionbarLegacy();
	    globalStorage.loadGlobalData();
	    isInfinitePrestige = globalStorage.getBooleanData("Options.infinite-prestige");
		rankStorage.loadRanksData();
		if(isInfinitePrestige) {
			infinitePrestigeSettings = new InfinitePrestigeSettings(this);
			infinitePrestigeSettings.load();
			prestigeStorage = new PrestigeDataStorageInfinite(this);
			cout(PREFIX + " §7Infinite Prestige: §aON");
		} 
		isMySql = globalStorage.getBooleanData("MySQL.enable");
		isVaultGroups = globalStorage.getBooleanData("Options.rankup-vault-groups");
		isRankEnabled = globalStorage.getBooleanData("Options.rank-enabled");
	    isPrestigeEnabled = globalStorage.getBooleanData("Options.prestige-enabled");
	    isRebirthEnabled = globalStorage.getBooleanData("Options.rebirth-enabled");
		prestigeStorage.loadPrestigesData();
		rebirthStorage.loadRebirthsData();
		messagesStorage.loadMessages(); 
		playerStorage = new PlayerDataStorage(this);
		prxAPI = new PRXAPI();
		prxAPI.setup();
		setupMySQL();
		lbm = new LeaderboardManager(this);	
	    if(hasPlugin("PlaceholderAPI")) {
			cout(PREFIX + " §eLoading PlaceholderAPI placeholders...");
			papi = new PapiHook(this);
			papi.register();
			cout(PREFIX + " §aPlaceholderAPI hooked.");
			hasPAPI = true;
			placeholderReplacer = new PlaceholderReplacerPAPI();
		} else {
			cout(PREFIX + " §2Started without PlaceholderAPI.");
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
		if(hasPlugin("LuckPerms") && isVaultGroups) {
			setupLuckPerms();
            lpUtils = new LuckPermsUtils(luckperms);
		} else if (hasPlugin("GroupManager") && isVaultGroups) {
			groupManager = new GMHook(this);
		}
	    
	    if(holidayUtils.getHoliday() == Holiday.CHRISTMAS) {
	    	cout(PREFIX + " §2Merry §4Christmas§f!");
	    } else if (holidayUtils.getHoliday() == Holiday.HALLOWEEN) {
	    	cout(PREFIX + " §6Happy Halloween§7!");
	    }
		if(configManager.commandsConfig.getBoolean("commands.rankup.enable", true)) {
		    rankupCommand = new RankupCommand("rankup");
		    commandLoader.registerCommand("rankup", rankupCommand);
		}
		if(configManager.commandsConfig.getBoolean("commands.prestige.enable", true)) {
		    prestigeCommand = new PrestigeCommand("prestige");
		    commandLoader.registerCommand("prestige", prestigeCommand);
		}
		if(configManager.commandsConfig.getBoolean("commands.rankupmax.enable", true)) {
		    rankupMaxCommand = new RankupMaxCommand("rankupmax");
		    commandLoader.registerCommand("rankupmax", rankupMaxCommand);
		}
		if(configManager.commandsConfig.getBoolean("commands.ranks.enable", true)) {
		    ranksCommand = new RanksCommand("ranks");
		    commandLoader.registerCommand("ranks", ranksCommand);
		}
		if(configManager.commandsConfig.getBoolean("commands.rebirth.enable", true)) {
		    rebirthCommand = new RebirthCommand("rebirth");
		    commandLoader.registerCommand("rebirth", rebirthCommand);
		}
		if(configManager.commandsConfig.getBoolean("commands.prestiges.enable", true)) {
		    prestigesCommand = new PrestigesCommand("prestiges");
	        commandLoader.registerCommand("prestiges", prestigesCommand);
		}
		if(configManager.commandsConfig.getBoolean("commands.rebirths.enable", true)) {
		    rebirthsCommand = new RebirthsCommand("rebirths");
		    commandLoader.registerCommand("rebirths", rebirthsCommand);
		}
		if(configManager.commandsConfig.getBoolean("commands.prx.enable", true)) {
		    prxCommand = new PRXCommand("prx");
		    commandLoader.registerCommand("prx", prxCommand);
		}
		if(configManager.commandsConfig.getBoolean("commands.autorankup.enable", true)) {
		    autoRankupCommand = new AutoRankupCommand("autorankup");
		    commandLoader.registerCommand("autorankup", autoRankupCommand);
		}
		if(configManager.commandsConfig.getBoolean("commands.autoprestige.enable", true)) {
		    autoPrestigeCommand = new AutoPrestigeCommand("autoprestige");
		    commandLoader.registerCommand("autoprestige", autoPrestigeCommand);
		}
		if(configManager.commandsConfig.getBoolean("commands.forcerankup.enable", true)) {
			forceRankupCommand = new ForceRankupCommand("forcerankup");
		    commandLoader.registerCommand("forcerankup", forceRankupCommand);
		}
		if(configManager.commandsConfig.getBoolean("commands.prestigemax.enable", true)) {
			prestigeMaxCommand = new PrestigeMaxCommand("prestigemax");
			commandLoader.registerCommand("prestigemax", prestigeMaxCommand);
		}	  
 
		setupBalanceFormat();
		
		autoSaveTime = globalStorage.getIntegerData("Options.autosave-time");
		forceSave = globalStorage.getBooleanData("Options.forcesave");
	    isRankupMaxWarpFilter = globalStorage.getBooleanData("Options.rankupmax-warp-filter");
		checkVault = globalStorage.getBooleanData("Options.rankup-vault-groups-check");
		allowEasterEggs = globalStorage.getBooleanData("Options.allow-easter-eggs");
		disabledWorlds = Sets.newHashSet(globalStorage.getStringListData("worlds"));
		try {
			ConfigUpdater.update(this, "messages.yml", new File(this.getDataFolder() + "/messages.yml"), new ArrayList<String>());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(isVaultGroups) this.vaultPlugin = globalStorage.getStringData("Options.rankup-vault-groups-plugin");

		if(configManager.commandsConfig.getBoolean("commands.topprestiges.enable")) {
			topPrestigesCommand = new TopPrestigesCommand("topprestiges");
			commandLoader.registerCommand("topprestiges", topPrestigesCommand);
			topPrestigesCommand.load();
		}
		if(configManager.commandsConfig.getBoolean("commands.toprebirths.enable")) {
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
			manager = new PRXManager();
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
		isEBProgress = globalStorage.getBooleanData("Options.expbar-progress");
		if(!isBefore1_7) {
			isABProgress = globalStorage.getBooleanData("Options.actionbar-progress");
		} else {
			isABProgress = false;
		}
		isSaveOnLeave = globalStorage.getBooleanData("Options.save-on-leave");
		saveNotification = globalStorage.getBooleanData("Options.save-notification");
		isEnabledInsteadOfDisabled = globalStorage.getBooleanData("Options.enabled-worlds-instead-of-disabled");
		actionbarInUse = new HashSet<>();
		errorInspector = new ErrorInspector(this);
		errorInspector.inspect();
		errorInspector.validateRanks(console);
		errorInspector.validatePrestiges(console);
		if(getGlobalStorage().getBooleanData("Options.autosave")) {
			startAsyncUpdateTask();
		}
		registerListeners();
		cout(PREFIX + " §aEnabled.");
	}
	/**
	 * Registers plugin listeners in the other classes.
	 */
	public void registerListeners() {
		playerLoginListener = isBefore1_7 ? new PlayerLoginListenerLegacy(this) : new PlayerLoginListener(this);
		rankForceDisplay = globalStorage.getBooleanData("Options.force-rank-display");
		prestigeForceDisplay = globalStorage.getBooleanData("Options.force-prestige-display");
		rebirthForceDisplay = globalStorage.getBooleanData("Options.force-rebirth-display");
		formatChat = globalStorage.getBooleanData("Options.format-chat");
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
		if(messWithChat) pluginManager.registerEvents(playerChatListener, this);
		pluginManager.registerEvents(playerLoginListener, this);
	}
	
	/**
	 * Unregister listeners from the other classes.
	 */
	public void unregisterListeners() {
		AsyncPlayerChatEvent.getHandlerList().unregister(playerChatListener);
	    AsyncPlayerPreLoginEvent.getHandlerList().unregister(playerLoginListener);
	    PlayerJoinEvent.getHandlerList().unregister(playerLoginListener);
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
					// TODO Auto-generated catch block
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
		host = String.valueOf(globalStorage.getStringData("MySQL.host"));
	    port = globalStorage.getIntegerData("MySQL.port");
	    database = globalStorage.getStringData("MySQL.database");
	    username = globalStorage.getStringData("MySQL.username");
	    password = globalStorage.getStringData("MySQL.password");   
	    table = globalStorage.getStringData("MySQL.table"); 
	    useSSL = globalStorage.getBooleanData("MySQL.useSSL");
	    autoReconnect = globalStorage.getBooleanData("MySQL.autoReconnect");
	    useCursorFetch = globalStorage.getBooleanData("MySQL.useCursorFetch");
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
            if(useCursorFetch) {
            prop.setProperty("useCursorFetch", String.valueOf(useCursorFetch));
            }
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
		playerStorage.savePlayersData();
		configManager.saveRankDataConfig();
		configManager.savePrestigeDataConfig();
		configManager.saveRebirthDataConfig();
		prxAPI = null;
		mvdw = null;
		perm = null;
		econ = null;
		papi = null;
		playerStorage.getPlayerData().clear();
		rankStorage.getEntireData().clear();
		prestigeStorage.getPrestigeData().clear();
		rebirthStorage.getRebirthData().clear();
		globalStorage.getDoubleMap().clear();
		globalStorage.getStringMap().clear();
		globalStorage.getBooleanMap().clear();
		globalStorage.getStringListMap().clear();
		globalStorage.getIntegerMap().clear();
		globalStorage.getStringSetMap().clear();
		globalStorage.getGlobalMap().clear();
		messagesStorage.stringData.clear();
		messagesStorage.stringListData.clear();
		playerStorage = null;
		rankStorage = null;
		prestigeStorage = null;
		globalStorage = null;
		messagesStorage = null;
		isActionUtil = false;
		hasPAPI = false;
		hasHologramsPlugin = false;
		scheduler.cancelTasks(this);
		closeConnection();
		instance = null;
		cout(PREFIX + " §aData saved.");
		cout(PREFIX + " §cDisabled.");
	}
	
	public void debug(String message) {
		if(debug) Bukkit.broadcastMessage(getString("&9[DEBUG] " + message));
	}
	
	public void debugPreEnable(String message) {
		if(debug) System.out.println("[DEBUG] " + message);		
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if(inv == null) return;
		InventoryHolder holder = inv.getHolder();
		if(holder == null) return;
		if(holder instanceof PaginatedGUI) {
			e.setResult(org.bukkit.event.Event.Result.DENY);
			e.setCancelled(true);
			debug("Yes it's a prisonranksx inventory");
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void debug(Object message) {
		if(debug) {
			if(message instanceof Integer) {
				int msg = (int)message;
				Bukkit.broadcastMessage(getString("&9[DEBUG] " + String.valueOf(msg)));
			} else if (message instanceof Double) {
				double msg = (double)message;
				Bukkit.broadcastMessage(getString("&9[DEBUG] " + String.valueOf(msg)));
			} else if (message instanceof UUID) {
				UUID uuid = (UUID)message;
				Bukkit.broadcastMessage(getString("&9[DEBUG] " + uuid.toString()));
			} else if (message instanceof List) {
				List<String> msg = (List)message;
				Bukkit.broadcastMessage(getString("&9[DEBUG] " + msg.toString()));
			} else if (message instanceof Set) {
				Set<String> msg = (HashSet<String>)message;
				Bukkit.broadcastMessage(getString("&9[DEBUG] " + msg.toString()));
			} else if (message instanceof RankPath) {
				RankPath msg = (RankPath)message;
				Bukkit.broadcastMessage(getString("&9[DEBUG] " + msg.get()));
			} else if (message instanceof Number) {
				Number msg = (Number)message;
				Bukkit.broadcastMessage(getString("&9[DEBUG] " + String.valueOf(msg)));
			} else {
				Bukkit.broadcastMessage(getString("&9[DEBUG] " + message.toString()));
			}
		}
	}
	
	public PlayerDataStorage getPlayerStorage() { return this.playerStorage; }
	public GlobalDataStorage getGlobalStorage() { return this.globalStorage; }
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		String name = p.getName();
		PRXAPI.AUTO_RANKUP_PLAYERS.remove(name);
		PRXAPI.AUTO_PRESTIGE_PLAYERS.remove(name);
		PRXAPI.TASKED_PLAYERS.remove(name);
		if(!isBefore1_7) {
			this.rankupMaxAPI.rankupMaxProcess.remove(name);
		} else {
			this.rankupMaxLegacy.rankupMaxProcess.remove(p);
		}
		if(isSaveOnLeave) {
			UUID uuid = null;
			AtomicObject atomicUUID = new AtomicObject();
			if(!isBefore1_7) {
				uuid = p.getUniqueId();
			} else {
				uuid = XUUID.tryNameConvert(name);
			}
			atomicUUID.set(uuid);
			taskChainFactory.newSharedChain("dataSave").async(() -> {
		if(isMySql()) {
			// this.updateMySqlData(p);
			this.updateMySqlData((UUID)atomicUUID.get(), name);
		} else {
			UUID uuidNew = (UUID)atomicUUID.get();
			getPlayerStorage().savePlayerData(uuidNew);
			getConfigManager().saveRankDataConfig();
			getConfigManager().savePrestigeDataConfig();
			getConfigManager().saveRebirthDataConfig();
			getPlayerStorage().unload(uuidNew);
		}
			}).execute();
		}
		if(isEBProgress)
			this.ebprogress.disable(p);
		if(!isABProgress)
			return;
		this.abprogress.disable(p);
	}
	
	public void cout(String string) { console.sendMessage(string); }
	
	public boolean hasPlugin(String pluginName) { return pluginManager.isPluginEnabled(pluginName); }
	
	public boolean hasActionbarOn(UUID uuid) { return actionbarInUse.contains(uuid); }
	/**
	 * 
	 * @param player the player that will receive the action bar message
	 * @param interval action bar animation in ticks // 20 ticks = 1 second
	 * @param actionbar action bar messages to be sent
	 */
	public void animateActionbar(Player player, Integer interval, List<String> actionbarLines) {
		if(isBefore1_7 || actionbarLines == null)
			return;
        if(actionbarLines.size() == 0)
        	return;
        Player p = player;
        String name = p.getName();
        UUID uuid = p.getUniqueId();
        actionbarInUse.add(uuid);
        int linesAmount = actionbarLines.size();
		if(linesAmount == 1) {
			getActionbar().sendActionBar(p, getString(actionbarLines.get(0), name).replace("%rankup%", getString(prxAPI.getPlayerRank(p), name)).replace("%rankup_display%", getString(prxAPI.getPlayerRankDisplay(p), name)));
			return;
		}
		BukkitTask savedTask = actionbarTaskHolder.get(p);
        if (savedTask != null)
        	savedTask.cancel();
        // skip older action bar
		actionbarTaskHolder.put(p, null);
        actionbarTask = actionbarTaskHolder.get(p);
        // put new one
		actionbarAnimationHolder.put(p, 0);
        actionbarTask = new BukkitRunnable() {
        	 public void run() {
        		 if(actionbarTaskHolder.containsKey(p)) {
        			 actionbarTaskHolder.put(p, actionbarTask);
        		 }
		         boolean animationEnded = actionbarAnimationHolder.get(p) >= linesAmount;
		         if(animationEnded) {
		        	 scheduler.runTaskLaterAsynchronously(instance, () -> actionbarInUse.remove(uuid), 20);
		        	 cancel();
		        	 return;
		         }
		         String currentLine = actionbarLines.get(actionbarAnimationHolder.get(p));
		         getActionbar().sendActionBar(p, getString(currentLine, name).replace("%rankup%", getString(playerStorage.getPlayerRank(p), name)).replace("%rankup_display%", getString(prxAPI.getPlayerRankDisplay(p), name)));
				 if(!animationEnded)
					 actionbarAnimationHolder.put(p, actionbarAnimationHolder.get(p)+1);
        	 }
         }.runTaskTimerAsynchronously(this, 1L, interval);
	}
	
	public Color getColor(String temp) {
		return fireworkManager.getColor(temp);
	}

	public void sendRebirthFirework(Player p) {
		fireworkManager.sendRebirthFirework(p);
	}

	public void sendPrestigeFirework(Player p) {
		fireworkManager.sendPrestigeFirework(p);
    }
	
	public void sendRankFirework(Player p) {
		fireworkManager.sendRankFirework(p);
	}
	
	public void sendListMessage(Player p, List<String> list) {
		for(String messageLine : list) {
			p.sendMessage(getString(messageLine, p.getName()));
		}
	}
	public void sendListMessage(CommandSender s, List<String> list) {
		for(String messageLine : list) {
			s.sendMessage(getString(messageLine, s.getName()).replace("%player%", s.getName()));
		}
	}
	public void sendListMessage(String playerName, List<String> list) {
		for(String messageLine : list) {
			Bukkit.getPlayer(playerName).sendMessage(getString(messageLine, playerName));
		}
	}

	public void executeCachedCommandsWithWarpFilter(Player player, RankPath rank) {
		String rankpath = rank.get();
		Player p = player;
		String name = p.getName();
		if(rankStorage.getConsoleCommands().containsKey(rankpath)) {
			for(String string : rankStorage.getConsoleCommands().get(rankpath)) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), string.replace("%player%", name));
			}
		}
		if(rankStorage.getPlayerCommands().containsKey(rankpath)) {
			for(String string : rankStorage.getPlayerCommands().get(rankpath)) {
				if(!string.contains("warp")) {
					Bukkit.dispatchCommand(p, string.replace("%player%", name));
				}
			}
		}
	}
	
	public void executeCachedCommands(Player player, RankPath rank) {
		String rankpath = rank.get();
		Player p = player;
		String name = p.getName();
		if(rankStorage.getConsoleCommands().containsKey(rankpath)) {
			for(String string : rankStorage.getConsoleCommands().get(rankpath)) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), string.replace("%player%", name));
			}
		}
		if(rankStorage.getPlayerCommands().containsKey(rankpath)) {
			for(String string : rankStorage.getPlayerCommands().get(rankpath)) {
				Bukkit.dispatchCommand(p, string.replace("%player%", name));
			}
		}
	}
	
	public void executeCommandsSafely(Player player, List<String> stringList) {
		Player p = player;
		for(String command : stringList) {
			if(command.startsWith("[console]")) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.substring(10).replace("%player%", p.getName()));
			} else if (command.startsWith("[player]")) {
        		Bukkit.dispatchCommand(p, command.substring(9).replace("%player%", p.getName()));
        	} else {
        		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", p.getName()));
        	}
		}
	}

	public void executeCommands(Player player, List<String> stringList) {
		if(stringList.isEmpty())
			return;
		newSharedChain("command").current(() -> {
		List<String> commandsList = stringList;
		scheduler.runTaskLater(this, () ->{
			Player p = player;
			for(String command : commandsList) {
				if(command.startsWith("[console]")) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.substring(10).replace("%player%", p.getName()));
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
    		   Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.substring(10).replace("%player%", p.getName()));
    	   } else if (command.startsWith("[player]")) {
    		   Bukkit.dispatchCommand(p, command.substring(9).replace("%player%", p.getName()));
    	   } else {
    		   Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", p.getName()));
    	   }
		});
	}
	
	private String v(String string) {
		return Z + string;
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

	public String center(String text) {
		if(text.startsWith("[center]")) return MessageCenterizer.centerMessage(text.substring(8));
		return text;
	}
	/**
	 * 
	 * @param text to be parsed
	 * @param playerName player name whom the placeholders should be parsed for
	 * @return <i>Colored string with symbols & placeholders aswell if PAPI is present.
	 */
	public String getString(String text, String playerName) {
        return center(getChatColorReplacer().parsePlaceholders(text, playerName));
	}
	
	/**
	 * 
	 * @param text to be parsed
	 * @param player the player whom the placeholders should be parsed for
	 * @return <i>Colored string with symbols & placeholders aswell if PAPI is present.
	 */
	public String getString(String text, Player player) {
        return center(getChatColorReplacer().parsePlaceholders(text, player));
	}
	
	/**
	 * 
	 * @param text
	 * @return <i>Colored String with symbols.
	 */
	public String getString(String text) {
		return center(getChatColorReplacer().parsePlaceholders(text));
	}
	
	@Deprecated
	public List<String> getStringList(List<String> stringList, String playerName) {
		List<String> newList = new ArrayList<>();
		stringList.forEach(line -> {
			newList.add(center(getChatColorReplacer().parsePlaceholders(line, playerName)));
		});
		return newList;
	}
	
	@Deprecated
	public List<List<String>> getStringListAll(List<String> stringList) {
		List<List<String>> newLists = new ArrayList<>();
		OnlinePlayers.getPlayers().forEach(p -> {
			List<String> newList = new ArrayList<>();
			stringList.forEach(line -> {
				newList.add(getChatColorReplacer().parsePlaceholders(line));
			});
			newLists.add(newList);
		});
		return newLists;
	}	
		
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onRankup(RankUpdateEvent e) {
		if(isBefore1_7) return;
		if(e.getCause() == RankUpdateCause.RANKUPMAX) return;
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		String name = p.getName();
		if(isForceSave()) saveDataAsynchronously(uuid, name);
		String currentRank = prxAPI.getPlayerRank(p);
		String nextRank = e.getRankup();
		updateVaultData(p, uuid, name, currentRank, nextRank);
	}
		
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onAutoRankup(AsyncAutoRankupEvent e) {
		if(isBefore1_7) return;
		Player p = e.getPlayer();
		String name = p.getName();
		if(isForceSave()) saveDataAsynchronously(p.getUniqueId(), name);
		UUID uuid = p.getUniqueId();
		String currentRank = e.getRankupFrom();
		String nextRank = e.getRankupTo();
		updateVaultData(p, uuid, name, currentRank, nextRank);
	}
		
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onRankupMax(AsyncRankupMaxEvent e) {
		if(isBefore1_7) return;
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		String name = p.getName();
		if(isForceSave()) saveDataAsynchronously(uuid, name);	
		String currentRank = e.getRankupFrom();
		String nextRank = e.getFinalRankup();
		updateVaultData(p, uuid, name, currentRank, nextRank);
	}
		
	public void updateVaultData(Player p, UUID uuid, String name, String currentRank, String nextRank) {
		if(isVaultGroups) { 
			if(vaultPlugin.equalsIgnoreCase("Vault")) {
				newSharedChain("vault").async(() -> {
					if(perms.playerInGroup(p, currentRank)) perms.playerRemoveGroup(p, currentRank);
					perms.playerAddGroup(p, nextRank);
				}).execute();
			} else if (vaultPlugin.equalsIgnoreCase("LuckPerms")) {
				newSharedChain("luckperms").async(() -> lpUtils.setGroup(uuid, nextRank, true)).execute();
			} else if (vaultPlugin.equalsIgnoreCase("GroupManager")) {
				newSharedChain("groupmanager").async(() -> groupManager.setGroup(p, nextRank)).execute();
			} else if (vaultPlugin.equalsIgnoreCase("PermissionsEX")) {
				newSharedChain("permissionsex").sync(() -> {
					PermissionUser user = PermissionsEx.getUser(p);
					if(user.inGroup(currentRank)) user.removeGroup(currentRank);
					user.addGroup(nextRank);
				}).execute();
			} else {
				scheduler.runTask(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), globalStorage.getStringData("Options.rankup-vault-groups-plugin").replace("%player%", name).replace("%rank%", nextRank)));
			}
		}
	}
	
	
		
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPrestige(PrestigeUpdateEvent e) {
		if(isBefore1_7) return;
		Player p = e.getPlayer();
		if(isForceSave()) saveDataAsynchronously(p.getUniqueId(), p.getName());
	}
		
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onRebirth(RebirthUpdateEvent e) {
		if(isBefore1_7) return;
		Player p = e.getPlayer();
		if(isForceSave()) saveDataAsynchronously(p.getUniqueId(), p.getName());
	}
		
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPrestigeMax(AsyncPrestigeMaxEvent e) {
		if(isBefore1_7) return;
		Player p = e.getPlayer();
		if(isForceSave()) saveDataAsynchronously(p.getUniqueId(), p.getName());
	}
		
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onAutoPrestige(AsyncAutoPrestigeEvent e) {
		Player p = e.getPlayer();
		if(isForceSave()) saveDataAsynchronously(p.getUniqueId(), p.getName());
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
		
}
