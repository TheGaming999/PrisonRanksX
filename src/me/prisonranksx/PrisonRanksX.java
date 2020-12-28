package me.prisonranksx;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.clip.placeholderapi.PlaceholderAPI;
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
import me.prisonranksx.data.GlobalDataStorage;
import me.prisonranksx.data.GlobalDataStorage1_16;
import me.prisonranksx.data.GlobalDataStorage1_8;
import me.prisonranksx.data.IPrestigeDataStorage;
import me.prisonranksx.data.MessagesDataStorage;
import me.prisonranksx.data.PlayerDataStorage;
import me.prisonranksx.data.PrestigeDataStorage;
import me.prisonranksx.data.RankDataStorage;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.data.RebirthDataStorage;
import me.prisonranksx.data.XUser;
import me.prisonranksx.error.ErrorInspector;
import me.prisonranksx.events.RankUpdateCause;
import me.prisonranksx.events.AsyncAutoPrestigeEvent;
import me.prisonranksx.events.AsyncAutoRankupEvent;
import me.prisonranksx.events.AsyncPrestigeMaxEvent;
import me.prisonranksx.events.AsyncRankRegisterEvent;
import me.prisonranksx.events.AsyncRankupMaxEvent;
import me.prisonranksx.events.PrestigeUpdateEvent;
import me.prisonranksx.events.RankUpdateEvent;
import me.prisonranksx.events.RebirthUpdateEvent;
import me.prisonranksx.gui.CustomItemsManager;
import me.prisonranksx.gui.CustomPrestigeItems;
import me.prisonranksx.gui.CustomRankItems;
import me.prisonranksx.gui.CustomRebirthItems;
import me.prisonranksx.gui.GuiListManager;
import me.prisonranksx.hooks.GMHook;
import me.prisonranksx.hooks.MVdWPapiHook;
import me.prisonranksx.hooks.PapiHook;
import me.prisonranksx.leaderboard.LeaderboardManager;
import me.prisonranksx.permissions.PermissionManager;
import me.prisonranksx.reflections.Actionbar;
import me.prisonranksx.reflections.Actionbar1_16;
import me.prisonranksx.reflections.ActionbarLegacy;
import me.prisonranksx.reflections.ActionbarProgress;
import me.prisonranksx.reflections.ExpbarProgress;
import me.prisonranksx.utils.TempOpProtection;
import me.prisonranksx.utils.XUUID;
import me.prisonranksx.utils.HolidayUtils.Holiday;
import me.prisonranksx.utils.ChatColorReplacer;
import me.prisonranksx.utils.ChatColorReplacer1_16;
import me.prisonranksx.utils.ChatColorReplacer1_8;
import me.prisonranksx.utils.CommandLoader;
import me.prisonranksx.utils.ConfigManager;
import me.prisonranksx.utils.ConfigUpdater;
import me.prisonranksx.utils.HolidayUtils;
import me.prisonranksx.utils.LuckPermsUtils;
import me.prisonranksx.utils.MySqlUtils;
import me.prisonranksx.utils.OnlinePlayers;
import me.prisonranksx.utils.PlaceholderReplacer;
import me.prisonranksx.utils.PlaceholderReplacerDefault;
import me.prisonranksx.utils.PlaceholderReplacerPAPI;

import com.google.common.io.Files;

import cloutteam.samjakob.gui.types.PaginatedGUI;
import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

@SuppressWarnings("deprecation")
public class PrisonRanksX extends JavaPlugin implements Listener {
	
	// GENERAL BOOLEAN FIELDS
	public boolean isMvdw, isApiLoaded, isActionUtil, debug, terminateMode,
	isBefore1_7, isRankEnabled, isPrestigeEnabled, isRebirthEnabled, forceSave,
    isABProgress, isRankupMaxWarpFilter, isVaultGroups, isholo, ishooked, isEBProgress,
    isSaveOnLeave, checkVault, saveNotification, allowEasterEggs, isEnabledInsteadOfDisabled;
	// ======================
	// MYSQL FIELDS
	private boolean isMySql, useSSL, autoReconnect, useCursorFetch;
    private int port;
	private Connection connection;
	private String host, database, username, password;
	private String table;
	private Statement statement;
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
	private TempOpProtection top;
	private CommandLoader commandLoader;
	private ConfigManager configManager;
	// ======================
	// HOOK FIELDS
	public MVdWPapiHook mvdw;
	public PapiHook papi;
	public Economy econ;
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
	public LeaderboardManager lbm;
	public ActionbarProgress abprogress;
	public ExpbarProgress ebprogress;
	public ErrorInspector errorInspector;
	public int autoSaveTime;
    private BukkitTask actionbarTask;
	public Set<UUID> actionbarInUse; 
	private final List<String> ignoredSections = Arrays.asList("Ranklist-gui.current-format.custom",
			"Ranklist-gui.completed-format.custom", "Ranklist-gui.other-format.custom",
			"Prestigelist-gui.current-format.custom", "Prestigelist-gui.completed-format.custom",
			"Prestigelist-gui.other-format.custom", "Rebirthlist-gui.current-format.custom",
			"Rebirthlist-gui.completed-format.custom", "Rebirthlist-gui.other-format.custom");
	public Map<Player, Integer> actionbar_animation;
    public Map<Player, BukkitTask> actionbar_task;
    public List<String> disabledWorlds;
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
	
	public Permission getPermissions() {
	    return perms;
	}
	    
	public void startAsyncUpdateTask() {
	    Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
	    	long timeBefore = System.currentTimeMillis();
	    	if(saveNotification) {
	    	Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §eSaving data...");
	    	}
	    	getPlayerStorage().savePlayersData();
	    	long timeNow = System.currentTimeMillis() - timeBefore;
	    	if(saveNotification) {
	    	Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §aData saved §7& §etook §6(§e" + toSeconds(timeNow) + "§6)§e.");
	    	}
	    }, autoSaveTime, autoSaveTime);
	}  
	    
	public void simulateAsyncAutoDataSave() {
		long timeBefore = System.currentTimeMillis();
    	if(saveNotification) {
    	Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §eSaving data...");
    	}
    	getPlayerStorage().savePlayersData();
    	long timeNow = System.currentTimeMillis() - timeBefore;
    	if(saveNotification) {
    	Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §aData saved §7& §etook §6(§e" + toSeconds(timeNow) + "§6)§e.");
    	}
	}
	
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
    
	public String toSeconds(final long time) {
	    if(time > 1000) {
	    	return String.valueOf(time / 1000) + " s";
	    } else {
	    	return String.valueOf(time) + " ms";
	    }
	}
	   
	public IPrestigeMax getPrestigeMax() {
	    return this.prestigeMax;
	}
	    
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
		taskChainFactory = BukkitTaskChainFactory.create(this);
		holidayUtils = new HolidayUtils(this);
		holidayUtils.setup();
		actionbar_animation = new HashMap<>();
		actionbar_task = new HashMap<>();
        String version = Bukkit.getVersion();
        commandLoader = new CommandLoader();
    	if(version.contains("1.5") || version.contains("1.6") || version.contains("1.4") || version.contains("1.3") || version.contains("1.2") || version.endsWith("1.1)") || version.contains("1.0")) {
    		isBefore1_7 = true;
    	}
        top = new TempOpProtection();
		Bukkit.getPluginManager().registerEvents(this, this);
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
	 	try {
			ConfigUpdater.update(this, "config.yml", new File(this.getDataFolder() + "/config.yml"), ignoredSections);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if((!Bukkit.getPluginManager().isPluginEnabled("Vault"))) {
          Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cUnable to find vault !");
		  Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cFailed to start, disabling....");
		  Bukkit.getPluginManager().disablePlugin(this);
		  return;
		} else {
		setupEconomy();
		setupPermissions();

		perm = new PermissionManager(this);
		configManager = new ConfigManager(this);
		if(Bukkit.getVersion().contains("1.16")) {
	         globalStorage = new GlobalDataStorage1_16(this);
		} else {
			 globalStorage = new GlobalDataStorage1_8(this);
		}	
		rankStorage = new RankDataStorage(this);
		prestigeStorage = new PrestigeDataStorage(this);
		rebirthStorage = new RebirthDataStorage(this);
		messagesStorage = new MessagesDataStorage(this);
	    configManager.loadConfigs();
		if(!isBefore1_7) {
		  if(Bukkit.getVersion().contains("1.16")) {
			  this.actionBar = new Actionbar1_16();
			  this.chatColorReplacer = new ChatColorReplacer1_16(this);
		  } else {
			  this.actionBar = new ActionbarLegacy();
			  this.chatColorReplacer = new ChatColorReplacer1_8(this);
		  }		  
		}
	    globalStorage.loadGlobalData();
		rankStorage.loadRanksData();
		prestigeStorage.loadPrestigesData();
		rebirthStorage.loadRebirthsData();
		messagesStorage.loadMessages(); 
		playerStorage = new PlayerDataStorage(this);
		prxAPI = new PRXAPI();
		prxAPI.setup();
		lbm = new LeaderboardManager(this);
	    if((Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))) {
			  Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §eLoading PlaceholderAPI Placeholders...");
			  papi = new PapiHook(this);
			  papi.register();
			  Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §aPlaceholderAPI Hooked.");
			  ishooked = true;
			  placeholderReplacer = new PlaceholderReplacerPAPI();
		} else {
			  Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §2Started without PlaceholderAPI.");
			  ishooked = false;
			  placeholderReplacer = new PlaceholderReplacerDefault();
		}
		if((Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays") == true)) {
			  Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §aHolographicDisplays Hooked.");
			  isholo = true;
		} else {
			  Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §2Started without HolographicDisplays.");
			  isholo = false;
		}
		if((Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI") == true)) {
			  Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §eLoading MVdWPlaceholderAPI Placeholders...");
			  mvdw = new MVdWPapiHook(this);
			  mvdw.registerPlaceholders();
			  isMvdw = true;
			  Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §aMVdWPlaceholderAPI Hooked.");
		} else {
			  isMvdw = false;
			  Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §2Started without MVdWPlaceholderAPI.");
		}
		if(Bukkit.getPluginManager().isPluginEnabled("ActionUtil") == true) {
			  isActionUtil = true;
			  Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §aActionUtil Detected.");
		} else {
			  Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §2Started without ActionUtil.");
		}
		if(Bukkit.getPluginManager().isPluginEnabled("LuckPerms") && isVaultGroups) {
            setupLuckPerms();
            lpUtils = new LuckPermsUtils(luckperms);
		} else if (Bukkit.getPluginManager().isPluginEnabled("GroupManager") && isVaultGroups) {
			  groupManager = new GMHook(this);
		}
	    Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §aEnabled.");
      if(holidayUtils.getHoliday() == Holiday.CHRISTMAS) {
          Bukkit.getConsoleSender().sendMessage("§c[§aPrisonRanksX§c] §2Merry §4Christmas§f!");
      } else if (holidayUtils.getHoliday() == Holiday.HALLOWEEN) {
          Bukkit.getConsoleSender().sendMessage("§8[§6PrisonRanksX§8] §6Happy Halloween§7!");
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
		setupMySQL();
		autoSaveTime = globalStorage.getIntegerData("Options.autosave-time");
		forceSave = globalStorage.getBooleanData("Options.forcesave");
	    isRankupMaxWarpFilter = globalStorage.getBooleanData("Options.rankupmax-warp-filter");
		checkVault = globalStorage.getBooleanData("Options.rankup-vault-groups-check");
		allowEasterEggs = globalStorage.getBooleanData("Options.allow-easter-eggs");
		disabledWorlds = globalStorage.getStringListData("worlds");
		try {
			ConfigUpdater.update(this, "messages.yml", new File(this.getDataFolder() + "/messages.yml"), new ArrayList<String>());
		} catch (IOException e) {
			e.printStackTrace();
		}
		isVaultGroups = globalStorage.getBooleanData("Options.rankup-vault-groups");
		if(isVaultGroups) {
			this.vaultPlugin = globalStorage.getStringData("Options.rankup-vault-groups-plugin");
		}
	    // API Setup {

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
			// }
		}
		
		guiManager = new GuiListManager(this);
		if(!this.isBefore1_7) {
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
		if(!isBefore1_7) {
			errorInspector = new ErrorInspector(this);
			errorInspector.inspect();
		}
			//playerStorage.loadPlayersData();
		if(getGlobalStorage().getBooleanData("Options.autosave")) {
			startAsyncUpdateTask();
		}

	}
	
	public boolean isMySql() {
		return this.isMySql;
	}
	
	public Statement getMySqlStatement() {
		return this.statement;
	}
	
	public void setupMySQL() {
	       host = String.valueOf(globalStorage.getStringData("MySQL.host"));
	       port = globalStorage.getIntegerData("MySQL.port");
	       setDatabase(globalStorage.getStringData("MySQL.database"));
	       username = globalStorage.getStringData("MySQL.username");
	       password = globalStorage.getStringData("MySQL.password");   
	       table = globalStorage.getStringData("MySQL.table");
	       isMySql = globalStorage.getBooleanData("MySQL.enable");
	       useSSL = globalStorage.getBooleanData("MySQL.useSSL");
	       autoReconnect = globalStorage.getBooleanData("MySQL.autoReconnect");
	       useCursorFetch = globalStorage.getBooleanData("MySQL.useCursorFetch");
	       isRankEnabled = globalStorage.getBooleanData("Options.rank-enabled");
	       isPrestigeEnabled = globalStorage.getBooleanData("Options.prestige-enabled");
	       isRebirthEnabled = globalStorage.getBooleanData("Options.rebirth-enabled");
        if(isMySql) {
        try {    
            openConnection();
            statement = getConnection().createStatement();  
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + getDatabase() + "." + table + " (`uuid` varchar(255), `name` varchar(255), `rank` varchar(255), `prestige` varchar(255), `rebirth` varchar(255), `path` varchar(255));");
         
            Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §aSuccessfully connected to the database.");
        } catch (ClassNotFoundException e) {
        	Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cdatabase class couldn't be found.");
            e.printStackTrace();
            getLogger().info("MySql Connection failed.");
        } catch (SQLException e) {
        	Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL error occurred.");
            e.printStackTrace();
            getLogger().info("MySql SQL Error occurred.");
        }
        }
 
	}
	
	public boolean isForceSave() {
		return forceSave;
	}
	
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
    
    public void closeConnection() {
    	try {
			if(getConnection() != null && !getConnection().isClosed()) {
				getConnection().close();
			}
		} catch (SQLException e) {
			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cCouldn't close database connection.");
			e.printStackTrace();
		}
    }
    
    public void updateMySqlData(final Player player, final String rank, final String prestige, final String rebirth, final String path) {
    	Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
		try {
			Player p = player;
			String name = p.getName();
			String u = XUser.getXUser(p).getUUID().toString();
			String rankName = rank == null ? prxAPI.getDefaultRank() : rank;
			String prestigeName = prestige == null ? "none" : prestige;
			String rebirthName = rebirth == null ? "none" : rebirth;
			String pathName = path == null ? prxAPI.getDefaultPath() : path;
			Statement statement = getConnection().createStatement();
			MySqlUtils util = new MySqlUtils(statement, getDatabase() + "." + table);
			ResultSet result = statement.executeQuery("SELECT * FROM " + getDatabase() + "." + table + " WHERE uuid = '" + u + "'");
			if(result.next()) {
				util.set(u, "rank", rankName);
				util.set(u, "path", pathName);
				util.set(u, "prestige", prestigeName);
				util.set(u, "rebirth", rebirthName);
				util.set(u, "name", name); 
				util.executeThenClose();
			} else {
				statement.executeUpdate("INSERT INTO " + getDatabase() + "." + table +" (`uuid`, `name`, `rank`, `prestige`, `rebirth`, `path`) VALUES ('" + u + "', '" + name + "', '" + rankName + "', '" + prestigeName + "', '" + rebirthName + "', '" + pathName + "');");
				statement.close();
			}
		} catch (SQLException e1) {
			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data update failed.");
			e1.printStackTrace();
			getLogger().info("ERROR Updating Player SQL Data");
		}
    	});
    }
    
    public void updateMySqlData(final Player player) {
    	Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
		try {
			Player p = player;
			String name = p.getName();
			UUID uu = XUser.getXUser(p).getUUID();
			String u = uu.toString();
			String rankName = prxAPI.getPlayerRank(uu) == null ? prxAPI.getDefaultRank() : prxAPI.getPlayerRank(uu);
			String prestigeName = prxAPI.getPlayerPrestige(uu) == null ? "none" : prxAPI.getPlayerPrestige(uu);
			String rebirthName = prxAPI.getPlayerRebirth(uu) == null ? "none" : prxAPI.getPlayerRebirth(uu);
			String pathName = prxAPI.getPlayerRankPath(uu).getPathName() == null ? prxAPI.getDefaultPath() : prxAPI.getPlayerRankPath(uu).getPathName();
			Statement statement = getConnection().createStatement();
			MySqlUtils util = new MySqlUtils(statement, getDatabase() + "." + table);
			ResultSet result = statement.executeQuery("SELECT * FROM " + getDatabase() + "." + table + " WHERE uuid = '" + u + "'");
			if(result.next()) {
				util.set(u, "rank", rankName);
				util.set(u, "path", pathName);
				util.set(u, "prestige", prestigeName);
				util.set(u, "rebirth", rebirthName);
				util.set(u, "name", name);  
				util.executeThenClose();
			} else {
				statement.executeUpdate("INSERT INTO " + getDatabase() + "." + table +" (`uuid`, `name`, `rank`, `prestige`, `rebirth`, `path`) VALUES ('" + u + "', '" + name + "', '" + rankName + "', '" + prestigeName + "', '" + rebirthName + "', '" + pathName + "');");
				statement.close();
			}
		} catch (SQLException e1) {
			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data update failed.");
			e1.printStackTrace();
			getLogger().info("ERROR Updating Player SQL Data");
		}
    	});
    }
    
    /**
     * 1.7+ only, use updateMySqlData(UUID uuid, String name) for 1.6 and earlier
     * @param uuid
     */
    public void updateMySqlData(final UUID uuid) {
    	Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
		try {
			UUID uu = XUser.getXUser(uuid).getUUID();
			String name = prxAPI.getPlayerNameFromUUID(uu);
			String u = uu.toString();
			String rankName = prxAPI.getPlayerRank(uu) == null ? prxAPI.getDefaultRank() : prxAPI.getPlayerRank(uu);
			String prestigeName = prxAPI.getPlayerPrestige(uu) == null ? "none" : prxAPI.getPlayerPrestige(uu);
			String rebirthName = prxAPI.getPlayerRebirth(uu) == null ? "none" : prxAPI.getPlayerRebirth(uu);
			String pathName = prxAPI.getPlayerRankPath(uu).getPathName() == null ? prxAPI.getDefaultPath() : prxAPI.getPlayerRankPath(uu).getPathName();
			Statement statement = getConnection().createStatement();
			MySqlUtils util = new MySqlUtils(statement, getDatabase() + "." + table);
			ResultSet result = statement.executeQuery("SELECT * FROM " + getDatabase() + "." + table + " WHERE uuid = '" + u + "'");
			if(result.next()) {
				util.set(u, "rank", rankName);
				util.set(u, "path", pathName);
				util.set(u, "prestige", prestigeName);
				util.set(u, "rebirth", rebirthName);
				util.set(u, "name", name);    
				util.executeThenClose();
			} else {
				statement.executeUpdate("INSERT INTO " + getDatabase() + "." + table +" (`uuid`, `name`, `rank`, `prestige`, `rebirth`, `path`) VALUES ('" + u + "', '" + name + "', '" + rankName + "', '" + prestigeName + "', '" + rebirthName + "', '" + pathName + "');");
				statement.close();
			}
		} catch (SQLException e1) {
			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data update failed.");
			e1.printStackTrace();
			getLogger().info("ERROR Updating Player SQL Data");
		}
    	});
    }
    
    /**
     * 1.0 - 1.15 mc versions
     * @param uuid
     */
    public void updateMySqlData(final UUID uuid, final String name) {
    	Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
		try {
			UUID uu = XUser.getXUser(uuid).getUUID();
			String u = uu.toString();
			String rankName = prxAPI.getPlayerRank(uu) == null ? prxAPI.getDefaultRank() : prxAPI.getPlayerRank(uu);
			String prestigeName = prxAPI.getPlayerPrestige(uu) == null ? "none" : prxAPI.getPlayerPrestige(uu);
			String rebirthName = prxAPI.getPlayerRebirth(uu) == null ? "none" : prxAPI.getPlayerRebirth(uu);
			String pathName = prxAPI.getPlayerRankPath(uu).getPathName() == null ? prxAPI.getDefaultPath() : prxAPI.getPlayerRankPath(uu).getPathName();
			Statement statement = getConnection().createStatement();
			MySqlUtils util = new MySqlUtils(statement, getDatabase() + "." + table);
			ResultSet result = statement.executeQuery("SELECT * FROM " + getDatabase() + "." + table + " WHERE uuid = '" + u + "'");
			if(result.next()) {
				util.set(u, "rank", rankName);
				util.set(u, "path", pathName);
				util.set(u, "prestige", prestigeName);
				util.set(u, "rebirth", rebirthName);
				util.set(u, "name", name);   
				util.executeThenClose();
			} else {
				statement.executeUpdate("INSERT INTO " + getDatabase() + "." + table +" (`uuid`, `name`, `rank`, `prestige`, `rebirth`, `path`) VALUES ('" + u + "', '" + name + "', '" + rankName + "', '" + prestigeName + "', '" + rebirthName + "', '" + pathName + "');");
				statement.close();
			}
		} catch (SQLException e1) {
			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data update failed.");
			e1.printStackTrace();
			getLogger().info("ERROR Updating Player SQL Data");
		}
    	});
    }
	public void onDisable() {
		if(terminateMode) {
			closeConnection();
			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §4Plugin terminated.");
			return;
		}
		Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §eSaving data...");
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
		ishooked = false;
		isholo = false;
		Bukkit.getScheduler().cancelTasks(this);
		closeConnection();
		Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §aData saved.");
		Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cDisabled.");
	}
	
	public void debug(String message) {
		if(debug) {
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&9[DEBUG] " + message));
		}
	}
	
	public void debugPreEnable(String message) {
		if(debug) {
		System.out.println("[DEBUG] " + message);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e) {
		Inventory inv = e.getClickedInventory();
		if(inv == null) return;
		InventoryHolder holder = inv.getHolder();
		if(holder == null) return;
		if(holder instanceof PaginatedGUI) {
			e.setResult(org.bukkit.event.Event.Result.DENY);
			e.setCancelled(true);
			debug("Yes it's a prisonranksx inventory");
		}
	}
	
	public void debug(Object message) {
		if(debug) {
			if(message instanceof Integer) {
				int msg = (int)message;
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&9[DEBUG] " + String.valueOf(msg)));
			} else if (message instanceof Double) {
				double msg = (double)message;
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&9[DEBUG] " + String.valueOf(msg)));
			} else if (message instanceof List) {
				List<String> msg = (List)message;
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&9[DEBUG] " + msg.toString()));
			} else if (message instanceof Set) {
				Set<String> msg = (HashSet<String>)message;
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&9[DEBUG] " + msg.toString()));
			} else if (message instanceof RankPath) {
				RankPath msg = (RankPath)message;
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&9[DEBUG] " + msg.get()));
			} else if (message instanceof Number) {
				Number msg = (Number)message;
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&9[DEBUG] " + String.valueOf(msg)));
			} else {
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&9[DEBUG] " + message.toString()));
			}
		}
	}

	public ConfigurationSection renameSection(String oldName, String newName) {
		Map<String, Object> values = getConfig().getConfigurationSection(oldName).getValues(true);
		getConfig().set(oldName, null);
		ConfigurationSection newSection = getConfig().createSection(newName, values);
		return newSection;
	}
	
	/**
	 * converts old config files (before v2.5) to (v2.5 configs) when it's true
	 * @return true if you have an old config
	 */
	public boolean convertConfigs() {
		if(getConfig().getString("Ranklist-text.rank-current-format") == null) {
			// yes old config
			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cold config detected! converting files...");
			try {
				Files.copy(new File(this.getDataFolder() + "/config.yml"), new File(this.getDataFolder() + "/old_config.yml"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FileConfiguration oldConfig = YamlConfiguration.loadConfiguration(new File(this.getDataFolder() + "/old_config.yml"));
			// convert <>
			List<String> oldWorlds = oldConfig.getStringList("worlds");
			getConfig().set("worlds", oldWorlds);
			boolean prestigeEnabled = oldConfig.getBoolean("Options.prestige-enabled");
			getConfig().set("Options.prestige-enabled", prestigeEnabled);
			boolean forceDisplay = oldConfig.getBoolean("Options.forcedisplay");
			getConfig().set("Options.force-rank-display", forceDisplay);
			boolean forcePrestigeDisplay = oldConfig.getBoolean("Options.forceprestigedisplay");
			getConfig().set("Options.force-prestige-display", forcePrestigeDisplay);
			boolean allWorldsBroadcast = oldConfig.getBoolean("Options.allworlds-broadcast");
			getConfig().set("Options.allworlds-broadcast", allWorldsBroadcast);
			boolean sendRankupMsg = oldConfig.getBoolean("Options.send-rankupmsg");
			getConfig().set("Options.send-rankupmsg", sendRankupMsg);
			boolean guiRankList = oldConfig.getBoolean("Options.GUI-RANKLIST");
			getConfig().set("Options.GUI-RANKLIST", guiRankList);
			boolean guiPrestigeList = oldConfig.getBoolean("Options.GUI-PRESTIGELIST");
			getConfig().set("Options.GUI-PRESTIGELIST", guiPrestigeList);
			String prestigeSoundName = oldConfig.getString("Options.prestigesound-name");
			getConfig().set("Options.prestigesound-name", prestigeSoundName);
			double prestigeSoundVolume = oldConfig.getDouble("Options.prestigesound-volume");
			getConfig().set("Options.prestigesound-volume", prestigeSoundVolume);
			double prestigeSoundPitch = oldConfig.getDouble("Options.prestigesound-pitch");
			getConfig().set("Options.prestigesound-pitch", prestigeSoundPitch);
			String rankupSoundName = oldConfig.getString("Options.rankupsound-name");
			getConfig().set("Options.rankupsound-name", rankupSoundName);
			double rankupSoundVolume = oldConfig.getDouble("Options.rankupsound-volume");
			getConfig().set("Options.rankupsound-volume", rankupSoundVolume);
			double rankupSoundPitch = oldConfig.getDouble("Options.rankupsound-pitch");
			getConfig().set("Options.rankupsound-pitch", rankupSoundPitch);
			boolean perRankPermission = oldConfig.getBoolean("Options.per-rank-permission");
			getConfig().set("Options.per-rank-permission", perRankPermission);
			boolean rankupMaxBroadcastLastRankOnly = oldConfig.getBoolean("Options.rankupmax-broadcastlastrankonly");
			getConfig().set("Options.rankupmax-broadcastlastrankonly", rankupMaxBroadcastLastRankOnly);
			boolean rankupMaxMsgLastRankOnly = oldConfig.getBoolean("Options.rankupmax-msglastrankonly");
			getConfig().set("Options.rankupmax-msglastrankonly", rankupMaxMsgLastRankOnly);
			boolean rankupMaxRankupMsgLastRankOnly = oldConfig.getBoolean("Options.rankupmax-rankupmsglastrankonly");
			getConfig().set("Options.rankupmax-rankupmsglastrankonly", rankupMaxRankupMsgLastRankOnly);
			boolean rankupVaultGroups = oldConfig.getBoolean("Options.rankup-vault-groups");
			getConfig().set("Options.rankup-vault-groups", rankupVaultGroups);
			String rankupVaultGroupsPlugin = oldConfig.getString("Options.rankup-vault-groups-plugin");
			getConfig().set("Options.rankup-vault-groups-plugin", rankupVaultGroupsPlugin);
			boolean autoRankup = oldConfig.getBoolean("Options.autorankup");
			getConfig().set("Options.autorankup", autoRankup);
			boolean MySqlEnable = oldConfig.getBoolean("MySQL.enable");
			getConfig().set("MySQL.enable", MySqlEnable);
			String MySqlHost = oldConfig.getString("MySQL.host");
			getConfig().set("MySQL.host", MySqlHost);
			int MySqlPort = oldConfig.getInt("MySQL.port");
			getConfig().set("MySQL.port", MySqlPort);
			String MySqlDatabase = oldConfig.getString("MySQL.database");
			getConfig().set("MySQL.database", MySqlDatabase);
			String MySqlTable = oldConfig.getString("MySQL.table");
			getConfig().set("MySQL.table", MySqlTable);
			String MySqlUsername = oldConfig.getString("MySQL.username");
			getConfig().set("MySQL.username", MySqlUsername);
			String MySqlPassword = oldConfig.getString("MySQL.password");
			getConfig().set("MySQL.password", MySqlPassword);
			
			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §aconversion success.");
			return true;
		} else {
			// not old config
			return false;
		}
	}
	
	public PlayerDataStorage getPlayerStorage() {
		return this.playerStorage;
	}
	
	public GlobalDataStorage getGlobalStorage() {
		return this.globalStorage;
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onAsyncLogin(AsyncPlayerPreLoginEvent e) {
		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
		if(!isRankEnabled) {
		  return;
		}
		XUser user;
		String name = e.getName();
		if(!isBefore1_7) {
		user = new XUser(e.getUniqueId());
		} else {
			user = new XUser(XUUID.tryNameConvert(name));
		}
	    UUID playerUUID = user.getUUID();
	    RankPath defaultRankPath = RankPath.getRankPath(prxAPI.getDefaultRank(), prxAPI.getDefaultPath());
	    AsyncRankRegisterEvent event = new AsyncRankRegisterEvent(playerUUID, name, defaultRankPath);
	    if(!getPlayerStorage().hasData(playerUUID) && !getPlayerStorage().isRegistered(playerUUID)) {
	    	Bukkit.getPluginManager().callEvent(event);
	    	if(event.isCancelled()) {
	    		return;
	    	}
	        getPlayerStorage().register(playerUUID, name, true);
	        prxAPI.setPlayerRankPath(playerUUID, defaultRankPath);
		    if(isMySql()) {
			    this.updateMySqlData(playerUUID, name);
		    }
	    } else {
	    	getPlayerStorage().loadPlayerData(playerUUID, name);
	    }
		});
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if(isBefore1_7) {
			return;
		}
     	Player p = e.getPlayer();
     	UUID playerUUID = p.getUniqueId();
     	String name = p.getName();
		prxAPI.autoRankupPlayers.remove(name);
		prxAPI.autoPrestigePlayers.remove(name);
		Bukkit.getScheduler().runTaskLater(this, () -> {
		if(isVaultGroups && checkVault) {
			if(this.vaultPlugin.equalsIgnoreCase("LuckPerms")) {
				taskChainFactory.newSharedChain("luckperms").async(() -> {
	    		User lpUser = lpUtils.getUser(playerUUID);
	    		if(!lpUser.getPrimaryGroup().equalsIgnoreCase(prxAPI.getPlayerRank(playerUUID))) {
	    			prxAPI.setPlayerRank(playerUUID, lpUser.getPrimaryGroup());
	    		}
				}).execute();
	    	}
			else if(vaultPlugin.equalsIgnoreCase("GroupManager")) {
				String group = groupManager.getGroup(p);
				if(!group.equalsIgnoreCase(prxAPI.getPlayerRank(p))) {
					prxAPI.setPlayerRank(p, group);
				}
			} else if (vaultPlugin.equalsIgnoreCase("PermissionsEX")) {
				String group = PermissionsEx.getUser(p).getGroups()[0].getName();
				if(!group.equalsIgnoreCase(prxAPI.getPlayerRank(p))) {
					prxAPI.setPlayerRank(p, group);
				}
			} else if (vaultPlugin.equalsIgnoreCase("Vault")) {
				String group = perms.getPrimaryGroup(p);
				if(!group.equalsIgnoreCase(prxAPI.getPlayerRank(p))) {
					prxAPI.setPlayerRank(p, group);
				}
			}
		}
		}, 5);
		if(isEBProgress) {
			Bukkit.getScheduler().runTaskLater(this, () -> {
				this.ebprogress.enable(p);
			}, 120);
		}
		if(!isABProgress) {
			return;
		}
		Bukkit.getScheduler().runTaskLater(this, () -> {
			this.abprogress.enable(p);
		}, 120);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		String name = p.getName();
		this.prxAPI.autoRankupPlayers.remove(name);
		this.prxAPI.autoPrestigePlayers.remove(name);
		this.prxAPI.taskedPlayers.remove(name);
		this.rankupMaxAPI.rankupMaxProcess.remove(p);
		if(isSaveOnLeave) {
			taskChainFactory.newSharedChain("saveOnLeave").async(() -> {
		if(isMySql()) {
			this.updateMySqlData(p);
		} else {
			
			UUID uuid = p.getUniqueId();
			
			getPlayerStorage().savePlayerData(uuid);
			getConfigManager().saveRankDataConfig();
			getConfigManager().savePrestigeDataConfig();
			getConfigManager().saveRebirthDataConfig();
			getPlayerStorage().unload(uuid);
			
		}
			}).execute();
		}
		if(isEBProgress) {
			this.ebprogress.disable(p);
		}
		if(!isABProgress) {
			return;
		}
		this.abprogress.disable(p);
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onKick(PlayerKickEvent e) {
		Player p = e.getPlayer();
		if(isSaveOnLeave) {
			taskChainFactory.newSharedChain("saveOnLeave").async(() -> {
		if(isMySql()) {
			this.updateMySqlData(p);
		} else {
			
			UUID uuid = p.getUniqueId();
			
			getPlayerStorage().savePlayerData(uuid);
			getConfigManager().saveRankDataConfig();
			getConfigManager().savePrestigeDataConfig();
			getConfigManager().saveRebirthDataConfig();
			getPlayerStorage().unload(uuid);
			
		}
			}).execute();
		}
		if(isEBProgress) {
			this.ebprogress.disable(p);
		}
		if(!isABProgress) {
			return;
		}
		this.abprogress.disable(p);
	}
	
	
	public boolean hasActionbarOn(UUID uuid) {
		return actionbarInUse.contains(uuid);
	}
	/**
	 * 
	 * @param player the player that will receive the action bar message
	 * @param interval action bar animation in ticks // 20 ticks = 1 second
	 * @param actionbar action bar messages to be sent
	 */
	public void animateActionbar(Player player, Integer interval, List<String> actionbar) {
		if(isBefore1_7 || actionbar == null) {
			return;
		}
         if(actionbar.size() == 0) {
        	 return;
         }
         Player p = player;
         actionbarInUse.add(p.getUniqueId());
         List<String> actionBar = actionbar;
		if(actionBar.size() == 1) {
			getActionbar().sendActionBar(p, getString(actionBar.get(0), p.getName()).replace("%rankup%", getString(prxAPI.getPlayerRank(p), p.getName())).replace("%rankup_display%", getString(prxAPI.getPlayerRankDisplay(p), p.getName())));
			return;
		}
        if (actionbar_task.get(p) != null) {
            actionbar_task.get(p).cancel();
        }
		//this hash map made to skip the recent task (if you rankup so fast) and place a new task with the new actionbar animation
		actionbar_task.put(p, null);
        actionbarTask = actionbar_task.get(p);
        //
		actionbar_animation.put(p, 0);
        actionbarTask = new BukkitRunnable() {
        	 public void run() {
        		 if(actionbar_task.containsKey(p)) {
        		 actionbar_task.put(p, actionbarTask);
        		 }
		        	int lines = actionBar.size();
		        	if(actionbar_animation.get(p) == lines) {
		        		Bukkit.getScheduler().runTaskLater(prxAPI.getPluginMainClass(), () -> {
		        		actionbarInUse.remove(p.getUniqueId());
		        		}, 20);
		        		cancel();
		        		return;
		        	}
		        	String currentLine = actionBar.get(actionbar_animation.get(p).intValue());
		        	
		        	getActionbar().sendActionBar(p, getString(currentLine, p.getName()).replace("%rankup%", getString(playerStorage.getPlayerRank(p), p.getName())).replace("%rankup_display%", getString(prxAPI.getPlayerRankDisplay(p), p.getName())));
					actionbar_animation.put(p, actionbar_animation.get(p)+1);
        	 }
         }.runTaskTimerAsynchronously(this, 1L, interval);
	}
	 public Color getColor(String paramString) {
		 String temp = paramString;
		 if (temp.equalsIgnoreCase("AQUA")) return Color.AQUA;
		 if (temp.equalsIgnoreCase("BLACK")) return Color.BLACK;
		 if (temp.equalsIgnoreCase("BLUE") || temp.equalsIgnoreCase("DARKBLUE") || temp.equalsIgnoreCase("DARK_BLUE")) return Color.BLUE;
		 if (temp.equalsIgnoreCase("FUCHSIA") || temp.equalsIgnoreCase("PINK")) return Color.FUCHSIA;
		 if (temp.equalsIgnoreCase("GRAY") || temp.equalsIgnoreCase("GREY")) return Color.GRAY;
		 if (temp.equalsIgnoreCase("GREEN") || temp.equalsIgnoreCase("DARKGREEN") || temp.equalsIgnoreCase("DARK_GREEN")) return Color.GREEN;
		 if (temp.equalsIgnoreCase("LIME") || temp.equalsIgnoreCase("LIGHTGREEN") || temp.equalsIgnoreCase("LIGHT_GREEN")) return Color.LIME;
		 if (temp.equalsIgnoreCase("MAROON")) return Color.MAROON;
		 if (temp.equalsIgnoreCase("NAVY")) return Color.NAVY;
		 if (temp.equalsIgnoreCase("OLIVE")) return Color.OLIVE;
		 if (temp.equalsIgnoreCase("ORANGE"))return Color.ORANGE;
		 if (temp.equalsIgnoreCase("PURPLE") || temp.equalsIgnoreCase("DARK_PURPLE") || temp.equalsIgnoreCase("DARKPURPLE")) return Color.PURPLE;
		 if (temp.equalsIgnoreCase("RED") || temp.equalsIgnoreCase("DARKRED") || temp.equalsIgnoreCase("DARK_RED")) return Color.RED;
		 if (temp.equalsIgnoreCase("SILVER") || temp.equalsIgnoreCase("LIGHTGRAY") || temp.equalsIgnoreCase("LIGHT_GRAY") || temp.equalsIgnoreCase("LIGHTGREY") || temp.equalsIgnoreCase("LIGHT_GREY")) return Color.SILVER;
		 if (temp.equalsIgnoreCase("TEAL")) return Color.TEAL;
		 if (temp.equalsIgnoreCase("WHITE")) return Color.WHITE;
		 if (temp.equalsIgnoreCase("YELLOW")) return Color.YELLOW;
		 // CUSTOM COLOR SECTION From RapidTables.
		 if (temp.equalsIgnoreCase("LIGHT_PURPLE") || temp.equalsIgnoreCase("LIGHTPURPLE") || temp.equalsIgnoreCase("LIGHT PURPLE")) return Color.fromRGB(255, 86, 255);
		 if (temp.equalsIgnoreCase("GOLD")) return Color.fromRGB(255,215,0);
		 if (temp.equalsIgnoreCase("CYAN")) return Color.fromRGB(16, 130, 148);
		 if (temp.equalsIgnoreCase("BROWN")) return Color.fromRGB(139,69,19);
		 if (temp.equalsIgnoreCase("LIGHT_YELLOW") || temp.equalsIgnoreCase("LIGHT YELLOW") || temp.equalsIgnoreCase("LIGHTYELLOW")) return Color.fromRGB(255, 255, 154);
		 if (temp.equalsIgnoreCase("SKYBLUE") || temp.equalsIgnoreCase("SKY_BLUE") || temp.equalsIgnoreCase("SKY BLUE") || temp.equalsIgnoreCase("BLUE_SKY") || temp.equalsIgnoreCase("BLUE SKY")) return Color.fromRGB(11, 182, 255);
		 if (temp.equalsIgnoreCase("TURQUOISE") || temp.equalsIgnoreCase("BLUEGREEN") || temp.equalsIgnoreCase("BLUE_GREEN") || temp.equalsIgnoreCase("BLUE GREEN"))  return Color.fromRGB(11, 255, 198);
		 if (temp.equalsIgnoreCase("LIGHT_RED") || temp.equalsIgnoreCase("LIGHTRED") || temp.equalsIgnoreCase("LIGHT RED")) return Color.fromRGB(255, 51, 51);
		 if (temp.equalsIgnoreCase("LIGHT_BLUE") || temp.equalsIgnoreCase("LIGHT BLUE") || temp.equalsIgnoreCase("LIGHTBLUE")) return Color.fromRGB(118, 118, 239);
		 return Color.WHITE;
		 }
		@SuppressWarnings("unused")
		public void sendRebirthFirework(Player p) {
			Bukkit.getScheduler().runTask(this, () -> {
		      String nextRebirth = prxAPI.getPlayerNextRebirth(p);
		      boolean sendFirework = rebirthStorage.isSendFirework(nextRebirth);
		      if(!sendFirework) {
		      	  return;
		      }
			  Firework fz = (Firework) p.getPlayer().getWorld().spawnEntity(p.getPlayer().getLocation(), EntityType.FIREWORK);
	    	  Map<String, Object> fbuilder = rebirthStorage.getFireworkBuilder(nextRebirth);
	          boolean fbuilder_flicker = (boolean)fbuilder.get("flicker");
	          boolean fbuilder_trail = (boolean)fbuilder.get("trail");
	          List<String> fbuilder_effect = (ArrayList<String>)fbuilder.get("effect");
	          List<String> fbuilder_color = (ArrayList<String>)fbuilder.get("color");
	          List<String> fbuilder_fade = (ArrayList<String>)fbuilder.get("fade");
	          List<Color> fireworkColors = new ArrayList<>();
	          List<Color> fireworkFade = new ArrayList<>();
	          for(String singleColor : fbuilder_color) {
	        	  fireworkColors.add(getColor(singleColor));
	          }
	          for(String singleFade : fbuilder_fade) {
	        	  fireworkFade.add(getColor(singleFade));
	          }
	          Integer fbuilder_power = (Integer)fbuilder.get("power");
	          for(String eff : fbuilder_effect) {
	    	  	   String effecto = eff;
		  	  FireworkMeta fm = fz.getFireworkMeta();
	          fm.addEffect(FireworkEffect.builder()
	            .flicker(fbuilder_flicker)
	            .trail(fbuilder_trail)
	            .with(FireworkEffect.Type.valueOf(effecto.replace("SPARKLE", "BURST").replace("STARS", "STAR")))
	            .withColor(fireworkColors)
	            .withFade(fireworkFade)
	            .build());
	          fm.setPower(fbuilder_power);
	          fz.setFireworkMeta(fm);   
	          }
			});
	    }
	@SuppressWarnings("unused")
	public void sendPrestigeFirework(Player p) {
		Bukkit.getScheduler().runTask(this, () -> {
	      String nextPrestige = prxAPI.getPlayerNextPrestige(p);
	      boolean sendFirework = prestigeStorage.isSendFirework(nextPrestige);
	      if(!sendFirework) {
	      	  return;
	      }
		  Firework fz = (Firework) p.getPlayer().getWorld().spawnEntity(p.getPlayer().getLocation(), EntityType.FIREWORK);
    	  Map<String, Object> fbuilder = prestigeStorage.getFireworkBuilder(nextPrestige);
          boolean fbuilder_flicker = (boolean)fbuilder.get("flicker");
          boolean fbuilder_trail = (boolean)fbuilder.get("trail");
          List<String> fbuilder_effect = (ArrayList<String>)fbuilder.get("effect");
          List<String> fbuilder_color = (ArrayList<String>)fbuilder.get("color");
          List<String> fbuilder_fade = (ArrayList<String>)fbuilder.get("fade");
          List<Color> fireworkColors = new ArrayList<>();
          List<Color> fireworkFade = new ArrayList<>();
          for(String singleColor : fbuilder_color) {
        	  fireworkColors.add(getColor(singleColor));
          }
          for(String singleFade : fbuilder_fade) {
        	  fireworkFade.add(getColor(singleFade));
          }
          Integer fbuilder_power = (Integer)fbuilder.get("power");
          for(String eff : fbuilder_effect) {
    	  	   String effecto = eff;
	  	  FireworkMeta fm = fz.getFireworkMeta();
          fm.addEffect(FireworkEffect.builder()
            .flicker(fbuilder_flicker)
            .trail(fbuilder_trail)
            .with(FireworkEffect.Type.valueOf(effecto.replace("SPARKLE", "BURST").replace("STARS", "STAR")))
            .withColor(fireworkColors)
            .withFade(fireworkFade)
            .build());
          fm.setPower(fbuilder_power);
          fz.setFireworkMeta(fm);   
          }
		});
    }
	@SuppressWarnings("unused")
	public void sendRankFirework(Player p) {
		Bukkit.getScheduler().runTask(this, () -> {
			RankPath currentRankPath = prxAPI.getPlayerRankPath(p);
	        boolean sendFirework = rankStorage.isSendFirework(currentRankPath);
	        if(!sendFirework) {
	    	    return;
	        }
		    Firework fz = (Firework) p.getPlayer().getWorld().spawnEntity(p.getPlayer().getLocation(), EntityType.FIREWORK);
    	    Map<String, Object> fbuilder = rankStorage.getFireworkBuilder(currentRankPath);
            boolean fbuilder_flicker = (boolean)fbuilder.get("flicker");
            boolean fbuilder_trail = (boolean)fbuilder.get("trail");
            List<String> fbuilder_effect = (ArrayList<String>)fbuilder.get("effect");
            List<String> fbuilder_color = (ArrayList<String>)fbuilder.get("color");
            List<String> fbuilder_fade = (ArrayList<String>)fbuilder.get("fade");
            List<Color> fireworkColors = new ArrayList<>();
            List<Color> fireworkFade = new ArrayList<>();
            for(String singleColor : fbuilder_color) {
  	             fireworkColors.add(getColor(singleColor));
            }
            for(String singleFade : fbuilder_fade) {
  	             fireworkFade.add(getColor(singleFade));
            }
            int fbuilder_power = (Integer)fbuilder.get("power");
            for(String eff : fbuilder_effect) {
	  	         String effecto = eff;
	             FireworkMeta fm = fz.getFireworkMeta();
                 fm.addEffect(FireworkEffect.builder()
                 .flicker(fbuilder_flicker)
                 .trail(fbuilder_trail)
                 .with(FireworkEffect.Type.valueOf(effecto.replace("SPARKLE", "BURST").replace("STARS", "STAR")))
                 .withColor(fireworkColors)
                 .withFade(fireworkFade)
                 .build());
                 fm.setPower(fbuilder_power);
                 fz.setFireworkMeta(fm);   
            }
		});
	}
	
	@EventHandler
	  public void onPlayerChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		String playerWorld = p.getWorld().getName();
		if(disabledWorlds.contains(playerWorld)) {
			return;
		}
		String eventFormat = e.getFormat();
		String formatUEdit = globalStorage.getStringData("Options.force-display-order")
				.replace("#", "");
		if(isRankEnabled && !rankStorage.getEntireData().containsKey(prxAPI.getPlayerRankPath(p).get())) {
			prxAPI.setPlayerRankPath(p, new RankPath(prxAPI.getDefaultRank(), prxAPI.getDefaultPath()));
		}
		RankPath playerRankPath = null;
		if(isRankEnabled) {
			 playerRankPath = playerStorage.getPlayerRankPath(p);
		}
		String playerRank = playerRankPath == null ? "" : this.getString(rankStorage.getDisplayName(playerRankPath) + "&r");
		String playerPrestige = playerStorage.getPlayerPrestige(p) != null  && isPrestigeEnabled ? 
				this.getString(prestigeStorage.getDisplayName(playerStorage.getPlayerPrestige(p)) + "&r") + " ": getString(globalStorage.getStringData("Options.no-prestige-display"));
		String playerRebirth = playerStorage.getPlayerRebirth(p) != null && isRebirthEnabled ? 
				this.getString(rebirthStorage.getDisplayName(playerStorage.getPlayerRebirth(p)) + "&r") + " ": getString(globalStorage.getStringData("Options.no-rebirth-display"));
		boolean rankForceDisplay = globalStorage.getBooleanData("Options.force-rank-display");
		boolean prestigeForceDisplay = globalStorage.getBooleanData("Options.force-prestige-display");
		boolean rebirthForceDisplay = globalStorage.getBooleanData("Options.force-rebirth-display");
		boolean isThereForceDisplay = false;
		if(rankForceDisplay || prestigeForceDisplay || rebirthForceDisplay) {
			isThereForceDisplay = true;
		}
		// FORCE DISPLAY {
		if(isThereForceDisplay) {
			// set stuff {
		String rankName;
		rankName = rankForceDisplay ? playerRank : "";
		String prestigeName;
		prestigeName = prestigeForceDisplay ? playerPrestige : "";
		String rebirthName;
		rebirthName = rebirthForceDisplay ? playerRebirth : "";
		   // }
			formatUEdit = formatUEdit.replace("{rank}", rankName)
					.replace("{prestige}", prestigeName)
					.replace("{rebirth}", rebirthName);
			e.setFormat(formatUEdit + " " + eventFormat.replace("{rank}", playerRank)
	        .replace("{prestige}", playerPrestige)
	        .replace("{rebirth}", playerRebirth));
			return;
		}
		// }
		// OTHER CHAT FORMAT {
        e.setFormat(eventFormat.replace("{rank}", playerRank)
        .replace("{prestige}", playerPrestige)
        .replace("{rebirth}", playerRebirth)
        .replace("#rank#", playerRank)
        .replace("#prestige#", playerPrestige)
        .replace("#rebirth#", playerRebirth));
		// }
	}
	  
	  


	
	public void sendListMessage(Player p, List<String> list) {
		for(String loopedstring : list) {
			p.sendMessage(getString(ChatColor.translateAlternateColorCodes('&', loopedstring), p.getName()));
		}
	}
	public void sendListMessage(CommandSender s, List<String> list) {
		for(String loopedstring : list) {
			s.sendMessage(getString(ChatColor.translateAlternateColorCodes('&', loopedstring), s.getName()).replace("%player%", s.getName()));
		}
	}
	public void sendListMessage(String playerName, List<String> list) {
		for(String loopedstring : list) {
			Bukkit.getPlayer(playerName).sendMessage(getString(ChatColor.translateAlternateColorCodes('&', loopedstring), playerName));
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
		if(stringList.isEmpty()) {
			return;
		}
		List<String> commandsList = stringList;
		Bukkit.getScheduler().runTaskLater(this, () ->{
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
	}

	public void executeCommand(Player player, String command) {
		Player p = player;
		Bukkit.getScheduler().runTask(this, () -> {
    	   if(command.startsWith("[console]")) {
    		   Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.substring(10).replace("%player%", p.getName()));
    	   } else if (command.startsWith("[player]")) {
    		   Bukkit.dispatchCommand(p, command.substring(9).replace("%player%", p.getName()));
    	   } else {
    		   Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", p.getName()));
    	   }
		});
	}
	
	public String v(String string) {
		return Z + string;
	}
	
	public String formatBalance(double y)
    {
        if(y > 999) {
        double x = y / Math.pow(10,Math.floor(Math.log10(y) / 3) * 3);
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
	 * @param text
	 * @param playerName
	 * @return <i>Colored String with Symbols & Placeholders if PAPI is present.
	 */
	public String getString(String text, String playerName) {
        return getChatColorReplacer().parsePlaceholders(text, playerName);
	}
	
	/**
	 * 
	 * @param text
	 * @param player
	 * @return <i>Colored String with Symbols & Placeholders if PAPI is present.
	 */
	public String getString(String text, Player player) {
        return getChatColorReplacer().parsePlaceholders(text, player);
	}
	
	/**
	 * 
	 * @param text
	 * @return <i>Colored String with symbols.
	 */
	public String getString(String text) {
		return getChatColorReplacer().parsePlaceholders(text);
	}
	
	@Deprecated
	public List<String> getStringList(List<String> stringList, String playerName) {
		List<String> newList = new ArrayList<>();
		stringList.forEach(line -> {
			newList.add(getChatColorReplacer().parsePlaceholders(line, playerName));
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
		public void onPerformCommand(PlayerCommandPreprocessEvent e) {
			Player p = e.getPlayer();
			String message = e.getMessage();
			if(message.charAt(0) == '/') {
			  if(top.isTempOp(p)) {
				  if(!top.isAllowed(message)) {
					  e.setCancelled(true);
					  top.setTempOp(p, false);
					  p.setOp(false);
					  return;
				  }
			  }
			}
		}
		
		
		@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
		public void onRankup(RankUpdateEvent e) {
			if(isBefore1_7) {
				return;
			}
			if(e.getCause() == RankUpdateCause.RANKUPMAX) {
				return;
			}
			Player p = e.getPlayer();
			UUID u = p.getUniqueId();
			String name = p.getName();
			if(isForceSave()) {
				saveDataAsynchronously(u, name);
			}
			String currentRank = prxAPI.getPlayerRank(u);
			if(isVaultGroups) { 
			String nextRank = e.getRankup();
			if(vaultPlugin.equalsIgnoreCase("Vault")) {
				   taskChainFactory.newSharedChain("vault").async(() -> {
				   if (perms.playerInGroup(p, currentRank)) {
					   perms.playerRemoveGroup(p, currentRank);
				   }
				   perms.playerAddGroup(p, nextRank);
				   });
				} else if (vaultPlugin.equalsIgnoreCase("LuckPerms")) {
					taskChainFactory.newSharedChain("luckperms").async(() -> {
						lpUtils.setGroup(u, nextRank, true);
					});
				} else if (vaultPlugin.equalsIgnoreCase("GroupManager")) {
					taskChainFactory.newSharedChain("groupmanager").async(() -> {
					groupManager.setGroup(p, nextRank);
					});
				} else if (vaultPlugin.equalsIgnoreCase("PermissionsEX")) {
					taskChainFactory.newSharedChain("permissionsex").sync(() -> {
					PermissionUser user = PermissionsEx.getUser(p);
					if(user.inGroup(currentRank)) {
					user.removeGroup(currentRank);
					}
					user.addGroup(nextRank);
					});
				} else {
					Bukkit.getScheduler().runTask(this, () -> {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), globalStorage.getStringData("Options.rankup-vault-groups-plugin").replace("%player%", name).replace("%rank%", nextRank));
					});
				}
			}
		}
		
		@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
		public void onAutoRankup(AsyncAutoRankupEvent e) {
			if(isBefore1_7) {
				return;
			}
			Player p = e.getPlayer();
			String name = p.getName();
			if(isForceSave()) {
				saveDataAsynchronously(p.getUniqueId(), name);
			}
			UUID uuid = p.getUniqueId();
			String currentRank = e.getRankupFrom();
			if(isVaultGroups) { 
			String nextRank = e.getRankupTo();
			if(vaultPlugin.equalsIgnoreCase("Vault")) {
				   taskChainFactory.newSharedChain("vault").async(() -> {
				   if (perms.playerInGroup(p, currentRank)) {
					   perms.playerRemoveGroup(p, currentRank);
				   }
				   perms.playerAddGroup(p, nextRank);
				   });
				} else if (vaultPlugin.equalsIgnoreCase("LuckPerms")) {
					taskChainFactory.newSharedChain("luckperms").async(() -> {
						lpUtils.setGroup(uuid, nextRank, true);
					});
				} else if (vaultPlugin.equalsIgnoreCase("GroupManager")) {
					taskChainFactory.newSharedChain("groupmanager").async(() -> {
					groupManager.setGroup(p, nextRank);
					});
				} else if (vaultPlugin.equalsIgnoreCase("PermissionsEX")) {
					taskChainFactory.newSharedChain("permissionsex").sync(() -> {
					PermissionUser user = PermissionsEx.getUser(p);
					if(user.inGroup(currentRank)) {
					user.removeGroup(currentRank);
					}
					user.addGroup(nextRank);
					});
				} else {
					Bukkit.getScheduler().runTask(this, () -> {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), globalStorage.getStringData("Options.rankup-vault-groups-plugin").replace("%player%", name).replace("%rank%", nextRank));
					});
				}
			}
		}
		
		@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
		public void onRankupMax(AsyncRankupMaxEvent e) {
			if(isBefore1_7) {
				return;
			}
			Player p = e.getPlayer();
			UUID uuid = p.getUniqueId();
			String name = p.getName();
			if(isForceSave()) {
				saveDataAsynchronously(uuid, name);
			}
			String currentRank = e.getRankupFrom();
			if(isVaultGroups) { 
			String nextRank = e.getFinalRankup();
			if(vaultPlugin.equalsIgnoreCase("Vault")) {
			   taskChainFactory.newSharedChain("vault").async(() -> {
			   if (perms.playerInGroup(p, currentRank)) {
				   perms.playerRemoveGroup(p, currentRank);
			   }
			   perms.playerAddGroup(p, nextRank);
			   });
			} else if (vaultPlugin.equalsIgnoreCase("LuckPerms")) {
				taskChainFactory.newSharedChain("luckperms").async(() -> {
					lpUtils.setGroup(uuid, nextRank, true);
				});
			} else if (vaultPlugin.equalsIgnoreCase("GroupManager")) {
				taskChainFactory.newSharedChain("groupmanager").async(() -> {
				groupManager.setGroup(p, nextRank);
				});
			} else if (vaultPlugin.equalsIgnoreCase("PermissionsEX")) {
				taskChainFactory.newSharedChain("permissionsex").sync(() -> {
				PermissionUser user = PermissionsEx.getUser(p);
				if(user.inGroup(currentRank)) {
				user.removeGroup(currentRank);
				}
				user.addGroup(nextRank);
				});
			} else {
				Bukkit.getScheduler().runTask(this, () -> {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), globalStorage.getStringData("Options.rankup-vault-groups-plugin").replace("%player%", name).replace("%rank%", nextRank));
				});
			}
			}
		}
		
		@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
		public void onPrestige(PrestigeUpdateEvent e) {
			if(isBefore1_7) {
				return;
			}
			Player p = e.getPlayer();
			if(isForceSave()) {
				saveDataAsynchronously(p.getUniqueId(), p.getName());
			}
			/*
			 String rank = prxAPI.getPlayerRank(p);
			 String path = prxAPI.getPlayerRankPath(p).getPathName();
			 String prestige = prxAPI.getPlayerPrestige(p);
			 String rebirth = prxAPI.getPlayerRebirth(p);
			 if(isMySql) {
			    updateMySqlData(p, rank, prestige, rebirth, path);
			 }
			*/
		}
		
		@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
		public void onRebirth(RebirthUpdateEvent e) {
			if(isBefore1_7) {
				return;
			}
			Player p = e.getPlayer();
			if(isForceSave()) {
				saveDataAsynchronously(p.getUniqueId(), p.getName());
			}
		}
		
		@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
		public void onPrestigeMax(AsyncPrestigeMaxEvent e) {
			if(isBefore1_7) {
				return;
			}
			Player p = e.getPlayer();
			if(isForceSave()) {
				saveDataAsynchronously(p.getUniqueId(), p.getName());
			}
		}
		
		@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
		public void onAutoPrestige(AsyncAutoPrestigeEvent e) {
			Player p = e.getPlayer();
			if(isForceSave()) {
				saveDataAsynchronously(p.getUniqueId(), p.getName());
			}
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
}
