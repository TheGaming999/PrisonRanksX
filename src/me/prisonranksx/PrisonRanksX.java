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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerKickEvent;
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
import me.prisonranksx.commands.PrestigesCommand;
import me.prisonranksx.commands.RanksCommand;
import me.prisonranksx.commands.RankupCommand;
import me.prisonranksx.commands.RankupMaxCommand;
import me.prisonranksx.commands.RebirthCommand;
import me.prisonranksx.commands.RebirthsCommand;
import me.prisonranksx.commands.TopPrestigesCommand;
import me.prisonranksx.commands.TopRebirthsCommand;
import me.prisonranksx.data.GlobalDataStorage;
import me.prisonranksx.data.MessagesDataStorage;
import me.prisonranksx.data.PlayerDataStorage;
import me.prisonranksx.data.PrestigeDataStorage;
import me.prisonranksx.data.RankDataStorage;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.data.RebirthDataStorage;
import me.prisonranksx.data.XUser;
import me.prisonranksx.error.ErrorInspector;
import me.prisonranksx.events.RankUpdateCause;
import me.prisonranksx.events.XAutoRankupEvent;
import me.prisonranksx.events.XPrestigeUpdateEvent;
import me.prisonranksx.events.XRankUpdateEvent;
import me.prisonranksx.events.XRankupMaxEvent;
import me.prisonranksx.events.XRebirthUpdateEvent;
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
import me.prisonranksx.reflections.ActionbarProgress;
import me.prisonranksx.reflections.ExpbarProgress;
import me.prisonranksx.utils.TempOpProtection;
import me.prisonranksx.utils.XUUID;
import me.prisonranksx.utils.CommandLoader;
import me.prisonranksx.utils.ConfigManager;
import me.prisonranksx.utils.ConfigUpdater;
import me.prisonranksx.utils.MySqlUtils;

import com.google.common.io.Files;

import cloutteam.samjakob.gui.types.PaginatedGUI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

@SuppressWarnings("deprecation")
public class PrisonRanksX extends JavaPlugin implements Listener{
	public boolean isMvdw;
	public PermissionManager perm;
	public PlayerDataStorage playerStorage;
	public RankDataStorage rankStorage;
	public PrestigeDataStorage prestigeStorage;
	public GlobalDataStorage globalStorage;
	public RebirthDataStorage rebirthStorage;
	public MessagesDataStorage messagesStorage;
	public ConfigManager configManager;
	public TempOpProtection top;
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
	public GuiListManager guiManager;
	public boolean isActionUtil;
	public boolean debug = false;
	public boolean terminateMode = false;
	public RankupCommand rankupCommand;
	public PrestigeCommand prestigeCommand;
	public RankupMaxCommand rankupMaxCommand;
	public RanksCommand ranksCommand;
	public RebirthCommand rebirthCommand;
	public PrestigesCommand prestigesCommand;
	public RebirthsCommand rebirthsCommand;
	public PRXCommand prxCommand;
	public AutoRankupCommand autoRankupCommand;
	public AutoPrestigeCommand autoPrestigeCommand;
	public ForceRankupCommand forceRankupCommand;
	public TopPrestigesCommand topPrestigesCommand;
	public TopRebirthsCommand topRebirthsCommand;
	public CustomItemsManager cim;
	public CustomRankItems cri;
	public CustomPrestigeItems cpi;
	public CustomRebirthItems crri;
	public MVdWPapiHook mvdw;
	public PapiHook papi;
	public PRXAPI prxAPI;
	public String vaultPlugin;
	public boolean isVaultGroups;
	public LuckPerms luckperms;
	public GMHook groupManager;
	public List<String> ignoredSections;
	public boolean isBefore1_7;
	public boolean isRankEnabled;
	public boolean isPrestigeEnabled;
	public boolean isRebirthEnabled;
	public boolean forceSave;
	public CommandLoader commandLoader;
	public LeaderboardManager lbm;
	public ActionbarProgress abprogress;
	public ExpbarProgress ebprogress;
	public boolean isABProgress;
	public boolean isRankupMaxWarpFilter;
	public Set<UUID> actionbarInUse; 
	BukkitTask ar = null;
	public ErrorInspector errorInspector;
	//MySQL
	private boolean isMySql, useSSL, autoReconnect;
    public Connection connection;
    public String host, database, username, password, table;
    public int port;
	private Statement statement;
	//vault
	private Permission perms;
	//
	public Integer plus(Integer integer) {
		return integer + 1;
	}

	public File originalYml = new File("plugins/PrisonRanksX/config.yml");

	
	public FileConfiguration rankDataConfig;
	public FileConfiguration prestigeDataConfig;
	
	public boolean ishooked = false;
	public boolean isholo = false;
	public boolean UUID;
	//..how to get percentage..
	//..yourmoney / rankcost * 100..
    public Map<Player, Integer> actionbar_animation = new HashMap<>();
    public Map<Player, BukkitTask> actionbar_task = new HashMap<>();
    BukkitTask actionbarTask;
    //
public void setupLuckPerms() {
	RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
	if (provider != null) {
	    luckperms = provider.getProvider();
	    
	}
}

	 public Economy econ = null;
	public boolean isEBProgress;
	public boolean isSaveOnLeave;
	public boolean checkVault;
	//...
	 
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
	//...
	    private boolean setupPermissions() {
	        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
	        perms = rsp.getProvider();
	        return perms != null;
	    }
	    public Permission getPermissions() {
	        return perms;
	    }
	    
	    /**
	     * data update every 15 minutes.
	     */
	    public void startAsyncUpdateTask() {
	    	Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
	    		long timeBefore = System.currentTimeMillis();
	    		if(getGlobalStorage().getBooleanData("Options.save-notification")) {
	    		Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §eSaving data...");
	    		}
	    		getPlayerStorage().savePlayersData();
	    		long timeNow = System.currentTimeMillis() - timeBefore;
	    		if(getGlobalStorage().getBooleanData("Options.save-notification")) {
	    		Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §aData saved §7& §etook §6(§e" + toSeconds(timeNow) + "§6)§e.");
	    		}
	    	}, 18000, 18000);
	    }
	    
	    public String toSeconds(final long time) {
	    	if(time > 1000) {
	    		return String.valueOf(time / 1000) + " s";
	    	} else {
	    		return String.valueOf(time) + " ms";
	    	}
	    }
	   
	public void onEnable() {
		  
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
	 			ignoredSections = new ArrayList<>();
	 			ignoredSections.add("Ranklist-gui.current-format.custom");
	 			ignoredSections.add("Ranklist-gui.current-format.custom");
	 			ignoredSections.add("Ranklist-gui.completed-format.custom");
	 			ignoredSections.add("Ranklist-gui.other-format.custom");
	 			ignoredSections.add("Prestigelist-gui.current-format.custom");
	 			ignoredSections.add("Prestigelist-gui.completed-format.custom");
	 			ignoredSections.add("Prestigelist-gui.other-format.custom");
	 			ignoredSections.add("Rebirthlist-gui.current-format.custom");
	 			ignoredSections.add("Rebirthlist-gui.completed-format.custom");
	 			ignoredSections.add("Rebirthlist-gui.other-format.custom");
					ConfigUpdater.update(this, "config.yml", new File(this.getDataFolder() + "/config.yml"), ignoredSections);
				} catch (IOException e) {
					// TODO Auto-generated catch block
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
			  globalStorage = new GlobalDataStorage(this);
			  playerStorage = new PlayerDataStorage(this);
			  rankStorage = new RankDataStorage(this);
			  prestigeStorage = new PrestigeDataStorage(this);
			  rebirthStorage = new RebirthDataStorage(this);
			  messagesStorage = new MessagesDataStorage(this);
			  configManager.loadConfigs();
			  
				  if(configManager.commandsConfig.getBoolean("commands.rankup.enable")) {
				  rankupCommand = new RankupCommand("rankup");
				  commandLoader.registerCommand("rankup", rankupCommand);
				  }
				  if(configManager.commandsConfig.getBoolean("commands.prestige.enable")) {
				  prestigeCommand = new PrestigeCommand("prestige");
				  commandLoader.registerCommand("prestige", prestigeCommand);
				  }
				  if(configManager.commandsConfig.getBoolean("commands.rankupmax.enable")) {
				  rankupMaxCommand = new RankupMaxCommand("rankupmax");
				  commandLoader.registerCommand("rankupmax", rankupMaxCommand);
				  }
				  if(configManager.commandsConfig.getBoolean("commands.ranks.enable")) {
				  ranksCommand = new RanksCommand("ranks");
				  commandLoader.registerCommand("ranks", ranksCommand);
				  }
				  if(configManager.commandsConfig.getBoolean("commands.rebirth.enable")) {
				  rebirthCommand = new RebirthCommand("rebirth");
				  commandLoader.registerCommand("rebirth", rebirthCommand);
				  }
				  if(configManager.commandsConfig.getBoolean("commands.prestiges.enable")) {
				  prestigesCommand = new PrestigesCommand("prestiges");
				  commandLoader.registerCommand("prestiges", prestigesCommand);
				  }
				  if(configManager.commandsConfig.getBoolean("commands.rebirths.enable")) {
				  rebirthsCommand = new RebirthsCommand("rebirths");
				  commandLoader.registerCommand("rebirths", rebirthsCommand);
				  }
				  if(configManager.commandsConfig.getBoolean("commands.prx.enable")) {
				  prxCommand = new PRXCommand("prx");
				  commandLoader.registerCommand("prx", prxCommand);
				  }
				  if(configManager.commandsConfig.getBoolean("commands.autorankup.enable")) {
				  autoRankupCommand = new AutoRankupCommand("autorankup");
				  commandLoader.registerCommand("autorankup", autoRankupCommand);
				  }
				  if(configManager.commandsConfig.getBoolean("commands.autoprestige.enable")) {
					  autoPrestigeCommand = new AutoPrestigeCommand("autoprestige");
					  commandLoader.registerCommand("autoprestige", autoPrestigeCommand);
				  }
				  if(configManager.commandsConfig.getBoolean("commands.forcerankup.enable")) {
				  forceRankupCommand = new ForceRankupCommand("forcerankup");
				  commandLoader.registerCommand("forcerankup", forceRankupCommand);
				  }

				  
		       rankDataConfig = configManager.rankDataConfig;
		       prestigeDataConfig = configManager.prestigeDataConfig;
			  globalStorage.loadGlobalData();
			  rankStorage.loadRanksData();
			  prestigeStorage.loadPrestigesData();
			  rebirthStorage.loadRebirthsData();
			  messagesStorage.loadMessages();

			  setupMySQL();
			  forceSave = globalStorage.getBooleanData("Options.forcesave");
			  isRankupMaxWarpFilter = globalStorage.getBooleanData("Options.rankupmax-warp-filter");
			  checkVault = globalStorage.getBooleanData("Options.rankup-vault-groups-check");
			  //playerStorage.loadPlayersData();
		       try {
				ConfigUpdater.update(this, "messages.yml", new File(this.getDataFolder() + "/messages.yml"), new ArrayList<String>());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  isVaultGroups = globalStorage.getBooleanData("Options.rankup-vault-groups");
			  if(isVaultGroups) {
				  this.vaultPlugin = globalStorage.getStringData("Options.rankup-vault-groups-plugin");
			  }
			  // API Setup {
			  prxAPI = new PRXAPI();
			  prxAPI.setup();
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
			  lbm = new LeaderboardManager(this);
			  // }
		  }
		  if((Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))) {
			  Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §eLoading PlaceholderAPI Placeholders...");
			  papi = new PapiHook(this);
			  papi.register();
			  Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §aPlaceholderAPI Hooked.");
			  ishooked = true;
		  } else {
			  Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §2Started without PlaceholderAPI.");
			  ishooked = false;
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
		  } else if (Bukkit.getPluginManager().isPluginEnabled("GroupManager") && isVaultGroups) {
			  groupManager = new GMHook(this);
		  }
			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §aEnabled.");


				  guiManager = new GuiListManager(this);
				  guiManager.setupConstantItems();
				  abprogress = new ActionbarProgress(this);
				  ebprogress = new ExpbarProgress(this);
				  isEBProgress = globalStorage.getBooleanData("Options.expbar-progress");
				  isABProgress = globalStorage.getBooleanData("Options.actionbar-progress");
				  isSaveOnLeave = globalStorage.getBooleanData("Options.save-on-leave");
				  actionbarInUse = new HashSet<UUID>();
				  if(!isBefore1_7) {
				  errorInspector = new ErrorInspector(this);
				  errorInspector.inspect();
				  }
				  playerStorage.loadPlayersData();
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
	       host = globalStorage.getStringData("MySQL.host");
	        port = globalStorage.getIntegerData("MySQL.port");
	        database = globalStorage.getStringData("MySQL.database");
	        username = globalStorage.getStringData("MySQL.username");
	        password = globalStorage.getStringData("MySQL.password");   
	        table = globalStorage.getStringData("MySQL.table");
	        isMySql = globalStorage.getBooleanData("MySQL.enable");
	        useSSL = globalStorage.getBooleanData("MySQL.useSSL");
	        autoReconnect = globalStorage.getBooleanData("MySQL.autoReconnect");
	        isRankEnabled = globalStorage.getBooleanData("Options.rank-enabled");
	        isPrestigeEnabled = globalStorage.getBooleanData("Options.prestige-enabled");
	        isRebirthEnabled = globalStorage.getBooleanData("Options.rebirth-enabled");
        if(isMySql) {
        try {    
            openConnection();
            statement = connection.createStatement();  
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + database + "." + table + " (uuid varchar(255), name varchar(255), rank varchar(255), prestige varchar(255), rebirth varchar(255), path varchar(255));");
         
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
        try {
        	statement.executeUpdate("ALTER TABLE " + database + "." + table + " ADD rebirth varchar(255);");
        	
         } catch (SQLException err) {
        	// COLUMN ALREADY EXISTS
        
         }
        try {
			statement.executeUpdate("ALTER TABLE " + database + "." + table + " ADD path varchar(255);");
		} catch (SQLException e) {
			// COLUMN ALREADY EXISTS
			
		}
        }
	}
	
	public boolean isForceSave() {
		return forceSave;
	}
	
    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }
        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            //Class.forName("com.mysql.jdbc.Driver");
            Properties prop = new Properties();
            prop.setProperty("user", username);
            prop.setProperty("password", password);
            prop.setProperty("useSSL", String.valueOf(useSSL));
            prop.setProperty("autoReconnect", String.valueOf(autoReconnect));
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, prop);
        }
    }
    
    public void closeConnection() {
    	try {
			if(connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cCouldn't close database connection.");
			e.printStackTrace();
		}
    }
    
    public void updateMySqlData(Player player, String rank, String prestige, String rebirth, String path) {
    	Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
		try {
			Player p = player;
			String name = p.getName();
			String u = XUser.getXUser(p).getUUID().toString();
			String rankName = rank == null ? prxAPI.getDefaultRank() : rank;
			String prestigeName = prestige == null ? "none" : prestige;
			String rebirthName = rebirth == null ? "none" : rebirth;
			String pathName = path == null ? prxAPI.getDefaultPath() : path;
			Statement statement = connection.createStatement();
			MySqlUtils util = new MySqlUtils(statement, database + "." + table);
			ResultSet result = statement.executeQuery("SELECT * FROM " + database + "." + table + " WHERE uuid = '" + u + "'");
			if(result.next()) {
				util.set(u, "rank", rankName);
				util.set(u, "path", pathName);
				util.set(u, "prestige", prestigeName);
				util.set(u, "rebirth", rebirthName);
				util.set(u, "name", name); 
			} else {
				statement.executeUpdate("INSERT INTO " + database + "." + table +" (uuid, name, rank, prestige, rebirth, path) VALUES ('" + u + "', '" + name + "', '" + rankName + "', '" + prestigeName + "', '" + rebirthName + "', '" + pathName + "');");
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data update failed.");
			e1.printStackTrace();
			getLogger().info("ERROR Updating Player SQL Data");
		}
    	});
    }
    
    public void updateMySqlData(Player player) {
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
			Statement statement = connection.createStatement();
			MySqlUtils util = new MySqlUtils(statement, database + "." + table);
			ResultSet result = statement.executeQuery("SELECT * FROM " + database + "." + table + " WHERE uuid = '" + u + "'");
			if(result.next()) {
				util.set(u, "rank", rankName);
				util.set(u, "path", pathName);
				util.set(u, "prestige", prestigeName);
				util.set(u, "rebirth", rebirthName);
				util.set(u, "name", name);       
			} else {
				statement.executeUpdate("INSERT INTO " + database + "." + table +" (uuid, name, rank, prestige, rebirth, path) VALUES ('" + u + "', '" + name + "', '" + rankName + "', '" + prestigeName + "', '" + rebirthName + "', '" + pathName + "');");
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
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
    public void updateMySqlData(UUID uuid) {
    	Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
		try {
			UUID uu = XUser.getXUser(uuid).getUUID();
			String name = Bukkit.getOfflinePlayer(uuid).getName();
			String u = uu.toString();
			String rankName = prxAPI.getPlayerRank(uu) == null ? prxAPI.getDefaultRank() : prxAPI.getPlayerRank(uu);
			String prestigeName = prxAPI.getPlayerPrestige(uu) == null ? "none" : prxAPI.getPlayerPrestige(uu);
			String rebirthName = prxAPI.getPlayerRebirth(uu) == null ? "none" : prxAPI.getPlayerRebirth(uu);
			String pathName = prxAPI.getPlayerRankPath(uu).getPathName() == null ? prxAPI.getDefaultPath() : prxAPI.getPlayerRankPath(uu).getPathName();
			Statement statement = connection.createStatement();
			MySqlUtils util = new MySqlUtils(statement, database + "." + table);
			ResultSet result = statement.executeQuery("SELECT * FROM " + database + "." + table + " WHERE uuid = '" + u + "'");
			if(result.next()) {
				util.set(u, "rank", rankName);
				util.set(u, "path", pathName);
				util.set(u, "prestige", prestigeName);
				util.set(u, "rebirth", rebirthName);
				util.set(u, "name", name);       
			} else {
				statement.executeUpdate("INSERT INTO " + database + "." + table +" (uuid, name, rank, prestige, rebirth, path) VALUES ('" + u + "', '" + name + "', '" + rankName + "', '" + prestigeName + "', '" + rebirthName + "', '" + pathName + "');");
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
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
    public void updateMySqlData(UUID uuid, String name) {
    	Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
		try {
			UUID uu = XUser.getXUser(uuid).getUUID();
			String u = uu.toString();
			String rankName = prxAPI.getPlayerRank(uu) == null ? prxAPI.getDefaultRank() : prxAPI.getPlayerRank(uu);
			String prestigeName = prxAPI.getPlayerPrestige(uu) == null ? "none" : prxAPI.getPlayerPrestige(uu);
			String rebirthName = prxAPI.getPlayerRebirth(uu) == null ? "none" : prxAPI.getPlayerRebirth(uu);
			String pathName = prxAPI.getPlayerRankPath(uu).getPathName() == null ? prxAPI.getDefaultPath() : prxAPI.getPlayerRankPath(uu).getPathName();
			Statement statement = connection.createStatement();
			MySqlUtils util = new MySqlUtils(statement, database + "." + table);
			ResultSet result = statement.executeQuery("SELECT * FROM " + database + "." + table + " WHERE uuid = '" + u + "'");
			if(result.next()) {
				util.set(u, "rank", rankName);
				util.set(u, "path", pathName);
				util.set(u, "prestige", prestigeName);
				util.set(u, "rebirth", rebirthName);
				util.set(u, "name", name);       
			} else {
				statement.executeUpdate("INSERT INTO " + database + "." + table +" (uuid, name, rank, prestige, rebirth, path) VALUES ('" + u + "', '" + name + "', '" + rankName + "', '" + prestigeName + "', '" + rebirthName + "', '" + pathName + "');");
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data update failed.");
			e1.printStackTrace();
			getLogger().info("ERROR Updating Player SQL Data");
		}
    	});
    }
	//
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
	
	public void debug(Object message) {
		if(debug) {
			if(message instanceof Integer) {
				int msg = (int)message;
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&9[DEBUG] " + String.valueOf(msg)));
			} else if (message instanceof Double) {
				double msg = (double)message;
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&9[DEBUG] " + String.valueOf(msg)));
			} else if (message instanceof List) {
				List<String> msg = (ArrayList)message;
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&9[DEBUG] " + msg.toString()));
			} else if (message instanceof Set) {
				Set<String> msg = (HashSet)message;
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&9[DEBUG] " + msg.toString()));
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
		if(!isBefore1_7) {
		user = new XUser(e.getUniqueId());
		} else {
			user = new XUser(XUUID.tryNameConvert(e.getName()));
		}
	    UUID playerUUID = user.getUUID();
	    if(!getPlayerStorage().isRegistered(playerUUID)) {
	    getPlayerStorage().loadPlayerData(playerUUID);
		 if(isMySql()) {
			this.updateMySqlData(playerUUID, e.getName());
		 }
	    }
	    if(isVaultGroups) {
	    	if(this.vaultPlugin.equalsIgnoreCase("luckperms")) {
	    		User lpUser = luckperms.getUserManager().getUser(playerUUID);
	    		if(!lpUser.getPrimaryGroup().equalsIgnoreCase(prxAPI.getPlayerRank(playerUUID))) {
	    			prxAPI.setPlayerRank(playerUUID, lpUser.getPrimaryGroup());
	    		}
	    	}
	    }
		if((getPlayerStorage().isRegistered(playerUUID))) {
			 return;
		}
			if((!getPlayerStorage().isRegistered(playerUUID))) {
			 getPlayerStorage().register(playerUUID);
			 RankPath rankPath = new RankPath(getGlobalStorage().getStringData("defaultrank"), getGlobalStorage().getStringData("defaultpath"));
			 getPlayerStorage().setPlayerRankPath(playerUUID, rankPath);
		     getPlayerStorage().setPlayerRank(playerUUID, new RankPath(getGlobalStorage().getStringData("defaultrank"), getGlobalStorage().getStringData("defaultpath")));
            return;
			}
		});
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if(isBefore1_7) {
			return;
		}
		Player p = e.getPlayer();
		Bukkit.getScheduler().runTaskLater(this, () -> {
		if(isVaultGroups) {
			if(vaultPlugin.equalsIgnoreCase("GroupManager")) {
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
		}, 60);
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
		if(isSaveOnLeave) {
		if(isMySql()) {
			this.updateMySqlData(p);
		} else {
			getPlayerStorage().savePlayerData(p.getUniqueId());
		}
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
		if(isMySql()) {
			this.updateMySqlData(p);
		} else {
			getPlayerStorage().savePlayerData(p);
		}
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
		if(actionbar == null) {
			return;
		}
         if(actionbar.size() == 0) {
        	 return;
         }
         Player p = player;
         actionbarInUse.add(p.getUniqueId());
         List<String> actionBar = actionbar;
		if(actionBar.size() == 1) {
			Actionbar.sendActionBar(p, getString(actionBar.get(0), p.getName()).replace("%rankup%", getString(prxAPI.getPlayerRank(p), p.getName())).replace("%rankup_display%", getString(prxAPI.getPlayerRankDisplay(p), p.getName())));
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
		        	Integer lines = actionBar.size();
		        	if(actionbar_animation.get(p) == lines) {
		        		Bukkit.getScheduler().runTaskLater(prxAPI.getPluginMainClass(), () -> {
		        		actionbarInUse.remove(p.getUniqueId());
		        		}, 20);
		        		cancel();
		        		return;
		        	}
		        	String currentLine = actionBar.get(actionbar_animation.get(p).intValue());
		        	
		        	Actionbar.sendActionBar(p, getString(currentLine, p.getName()).replace("%rankup%", getString(playerStorage.getPlayerRank(p), p.getName())).replace("%rankup_display%", getString(prxAPI.getPlayerRankDisplay(p), p.getName())));
					actionbar_animation.put(p, plus(actionbar_animation.get(p)));
        	 }
         }.runTaskTimer(this, 1L, interval);
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
	
	@EventHandler
	  public void onPlayerChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		List<String> disabledWorlds = globalStorage.getStringListData("worlds");
		String playerWorld = p.getWorld().getName();
		if(disabledWorlds.contains(playerWorld)) {
			return;
		}
		String eventFormat = e.getFormat();
		String formatUEdit = globalStorage.getStringData("Options.force-display-order")
				.replace("#", "");
		if(prxAPI.getPlayerRankDisplay(p) == null && isRankEnabled) {
			prxAPI.setPlayerRank(p, prxAPI.getDefaultRank());
			p.sendMessage(prxAPI.c("&cInvalid rank, your rank has been set to ") + prxAPI.getDefaultRank());
			e.setCancelled(true);
			return;
		}
		RankPath playerRankPath = null;
		if(isRankEnabled) {
			 playerRankPath = playerStorage.getPlayerRankPath(p);
		}
		String playerRank = playerRankPath == null ? "" : this.getStringWithoutPAPI(rankStorage.getDisplayName(playerRankPath) + "&r");
		String playerPrestige = playerStorage.getPlayerPrestige(p) != null  && isPrestigeEnabled ? 
				this.getStringWithoutPAPI(prestigeStorage.getDisplayName(playerStorage.getPlayerPrestige(p)) + "&r") + " ": getStringWithoutPAPI(globalStorage.getStringData("Options.no-prestige-display"));
		String playerRebirth = playerStorage.getPlayerRebirth(p) != null && isRebirthEnabled ? 
				this.getStringWithoutPAPI(rebirthStorage.getDisplayName(playerStorage.getPlayerRebirth(p)) + "&r") + " ": getStringWithoutPAPI(globalStorage.getStringData("Options.no-rebirth-display"));
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
	  
	  


	
	//Life Easier Methods
	//usage: sendListMessage(p.getName(), getConfig().getStringList("listed-message"));
	public void sendListMessage(Player p,List<String> list) {
		for(String loopedstring : list) {
			p.sendMessage(getString(ChatColor.translateAlternateColorCodes('&', loopedstring), p.getName()));
		
		}
	}
	public void sendListMessage(CommandSender s,List<String> list) {
		for(String loopedstring : list) {
			s.sendMessage(getString(ChatColor.translateAlternateColorCodes('&', loopedstring), s.getName()).replace("%player%", s.getName()));
		
		}
	}
	public void sendListMessage(String playername,List<String> list) {
		for(String loopedstring : list) {
			Bukkit.getPlayer(playername).sendMessage(getString(ChatColor.translateAlternateColorCodes('&', loopedstring), playername));
		
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
		if(rankStorage.getOpCommands().containsKey(rankpath)) {
		for(String string : rankStorage.getOpCommands().get(rankpath)) {
 		   if(!p.isOp()) {
 		   top.addCommand(string);
 		   top.setTempOp(p, true);
 		   p.setOp(true);
 	       }
 		   Bukkit.dispatchCommand(p, string.replace("%player%", name));
 		   if(p.isOp() && top.isTempOp(p)) {
 		   p.setOp(false);
 		   top.delCommand(string);
 		   top.setTempOp(p, false);
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
		if(rankStorage.getOpCommands().containsKey(rankpath)) {
		for(String string : rankStorage.getOpCommands().get(rankpath)) {
 		   if(!p.isOp()) {
 		   top.addCommand(string);
 		   top.setTempOp(p, true);
 		   p.setOp(true);
 	       }
 		   Bukkit.dispatchCommand(p, string.replace("%player%", name));
 		   if(p.isOp() && top.isTempOp(p)) {
 		   p.setOp(false);
 		   top.delCommand(string);
 		   top.setTempOp(p, false);
 		   }
		}
		}
	}
	
	public void executeCommandsSafely(Player player, List<String> stringList) {
			Player p = player;
           for(String command : stringList) {
        	   if(command.startsWith("[console]")) {
        		   Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.substring(10).replace("%player%", p.getName()));
        	   } else if (command.startsWith("[op]")) {
        		   String opCommand = command.substring(5).replace("%player%", p.getName());
        		   if(!p.isOp()) {
        		   top.addCommand(opCommand);
        		   top.setTempOp(p, true);
        		   p.setOp(true);
        	       }
        		   Bukkit.dispatchCommand(p, opCommand);
        		   if(p.isOp() && top.isTempOp(p)) {
        		   p.setOp(false);
        		   top.delCommand(opCommand);
        		   top.setTempOp(p, false);
        		   }
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
        	   } else if (command.startsWith("[op]")) {
        		   String opCommand = command.substring(5).replace("%player%", p.getName());
        		   if(!p.isOp()) {
        		   top.addCommand(opCommand);
        		   top.setTempOp(p, true);
        		   p.setOp(true);
        	       }
        		   Bukkit.dispatchCommand(p, opCommand);
        		   if(p.isOp() && top.isTempOp(p)) {
        		   p.setOp(false);
        		   top.delCommand(opCommand);
        		   top.setTempOp(p, false);
        		   }
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
    	   if(command.startsWith("[console]")) {
    		   Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.substring(10).replace("%player%", p.getName()));
    	   } else if (command.startsWith("[op]")) {
    		   String opCommand = command.substring(5).replace("%player%", p.getName());
    		   if(!p.isOp()) {
    		   top.addCommand(opCommand);
    		   top.setTempOp(p, true);
    		   p.setOp(true);
    	       }
    		   Bukkit.dispatchCommand(p, opCommand);
    		   if(p.isOp() && top.isTempOp(p)) {
    		   p.setOp(false);
    		   top.delCommand(opCommand);
    		   top.setTempOp(p, false);
    		   }
    	   } else if (command.startsWith("[player]")) {
    		   Bukkit.dispatchCommand(p, command.substring(9).replace("%player%", p.getName()));
    	   } else {
    		   Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", p.getName()));
    	   }
	}
	
	public String v(String string) {
		String Z = globalStorage.getStringData("Moneyformatter.zillion");
		return Z + string;
	}
	public String formatBalance(double y)
    {
		String k = globalStorage.getStringData("MoneyFormatter.thousand");
		String M = globalStorage.getStringData("MoneyFormatter.million");
		String B = globalStorage.getStringData("MoneyFormatter.billion");
		String T = globalStorage.getStringData("MoneyFormatter.trillion");
		String q = globalStorage.getStringData("MoneyFormatter.quadrillion");
		String Q = globalStorage.getStringData("MoneyFormatter.quintillion");
		String s = globalStorage.getStringData("MoneyFormatter.sextillion");
		String S = globalStorage.getStringData("MoneyFormatter.septillion");
		String O = globalStorage.getStringData("MoneyFormatter.octillion");
		String N = globalStorage.getStringData("MoneyFormatter.nonillion");
		String d = globalStorage.getStringData("MoneyFormatter.decillion");
		String U = globalStorage.getStringData("MoneyFormatter.undecillion");
		String D = globalStorage.getStringData("MoneyFormatter.Duodecillion");
		String Z = globalStorage.getStringData("MoneyFormatter.zillion");
		// ## k, ##, ###
        String[] abbrivations ={"",k,M,B,T,q,Q,s,S,O,N,d,U,D,Z, Z + "II",Z + "III",v("IV"),v("V"),v("VI"),v("VII"), v("VIII"), v("IX"), v("X")
        		, "Z11", "Z12", "Z13", "Z14", "Z15", "Z16", "Z17" , "Z18" , "Z19", "Z20", "Z21", "Z22", "Z23", "Z24", "Z25", "Z26"
        		, "Z27", "Z28", "Z29", "Z30", "~", "~!", "~?", "~@", "#", "^", "&", "*", "-", "+", "+2", "+3", "+4", "+5", "+6"
        };
        DecimalFormat abb = new DecimalFormat("0.##");
        if(y > 999) {
        double x = y / Math.pow(10,Math.floor(Math.log10(y) / 3) * 3);
        return abb.format(x) + abbrivations[((int) Math.floor(Math.log10(y) / 3))];   
        }
        return String.valueOf(y);
    }
	public String getArgs(String[] args, int num){ //You can use a method if you want
	    StringBuilder sb = new StringBuilder(); //We make a String Builder
	    for(int i = num; i < args.length; i++) { //We get all the arguments with a for loop
	        sb.append(args[i]).append(" "); //We add the argument and the space
	    }
	    return sb.toString().trim(); //We return the message
	}

	public String getString(String configstring, String player) {
		if(ishooked) {
			String configholdedstring;
 if(Bukkit.getPlayer(player) == null) {
	 configholdedstring = configstring;
 } else {
	 configholdedstring = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(player), configstring);
	
 }
			
			return configholdedstring.replace("&", "§")
					
					.replace("[>>]", "»")
					.replace("[<<]", "«")
					.replace("[coolarrow]", "➤")
					.replace("[<3]", "�?�")
					.replace("[shadowarrow]", "➢")
					.replace("[shadowarrow_2]", "➣")
					.replace("[shadowarrow_down]", "⧨")
					.replace("[shadowsquare]", "�?�")
					.replace("[nuke]", "☢")
					.replace("[+]", "✚")
					.replace("[correct]", "✔")
					.replace("[incorrect]", "✖")
					.replace("[bowarrow]", "➸")
					.replace("[squaredot]", "◼")
					.replace("[square]", "■")
					.replace("[happyface]", "☺")
					.replace("[|]", "⎟");
		}
		return configstring.replace("&", "§")
				
		.replace("[>>]", "»")
		.replace("[<<]", "«")
		.replace("[coolarrow]", "➤")
		.replace("[<3]", "�?�")
		.replace("[shadowarrow]", "➢")
		.replace("[shadowarrow_2]", "➣")
		.replace("[shadowarrow_down]", "⧨")
		.replace("[shadowsquare]", "�?�")
		.replace("[nuke]", "☢")
		.replace("[+]", "✚")
		.replace("[correct]", "✔")
		.replace("[incorrect]", "✖")
		.replace("[bowarrow]", "➸")
		.replace("[squaredot]", "◼")
		.replace("[square]", "■")
		.replace("[happyface]", "☺")
		.replace("[|]", "⎟");
		
	}
	public String getStringWithoutPAPI(String configstring) {
		return configstring.replace("&", "§")
		.replace("[>>]", "»")
		.replace("[<<]", "«")
		.replace("[coolarrow]", "➤")
		.replace("[<3]", "�?�")
		.replace("[shadowarrow]", "➢")
		.replace("[shadowarrow_2]", "➣")
		.replace("[shadowarrow_down]", "⧨")
		.replace("[shadowsquare]", "�?�")
		.replace("[nuke]", "☢")
		.replace("[+]", "✚")
		.replace("[correct]", "✔")
		.replace("[incorrect]", "✖")
		.replace("[bowarrow]", "➸")
		.replace("[squaredot]", "◼")
		.replace("[square]", "■")
		.replace("[happyface]", "☺")
		.replace("[|]", "⎟");
	}
	public List<String> getStringList(List<String> configstring, String player) {
		if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") == true) {
			String configholdedstring = "PAPIHOLDER";
			List<String> withplaceholders = new ArrayList<String>();
			for(String cfg : configstring) {
				configholdedstring = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(player), cfg);
				withplaceholders.add(configholdedstring.replace("&", "§")
						
						.replace("[>>]", "»")
						.replace("[<<]", "«")
						.replace("[coolarrow]", "➤")
						.replace("[<3]", "�?�")
						.replace("[shadowarrow]", "➢")
						.replace("[shadowarrow_2]", "➣")
						.replace("[shadowarrow_down]", "⧨")
						.replace("[shadowsquare]", "�?�")
						.replace("[nuke]", "☢")
						.replace("[+]", "✚")
						.replace("[correct]", "✔")
						.replace("[incorrect]", "✖")
						.replace("[bowarrow]", "➸")
						.replace("[squaredot]", "◼")
						.replace("[square]", "■")
						.replace("[happyface]", "☺")
						.replace("[|]", "⎟"));
			}
			
    return withplaceholders;
		}
		List<String> withoutplaceholders = new ArrayList<String>();
		for(String cfg : configstring) {
			withoutplaceholders.add(cfg.replace("&", "§")
					
					.replace("[>>]", "»")
					.replace("[<<]", "«")
					.replace("[coolarrow]", "➤")
					.replace("[<3]", "�?�")
					.replace("[shadowarrow]", "➢")
					.replace("[shadowarrow_2]", "➣")
					.replace("[shadowarrow_down]", "⧨")
					.replace("[shadowsquare]", "�?�")
					.replace("[nuke]", "☢")
					.replace("[+]", "✚")
					.replace("[correct]", "✔")
					.replace("[incorrect]", "✖")
					.replace("[bowarrow]", "➸")
					.replace("[squaredot]", "◼")
					.replace("[square]", "■")
					.replace("[happyface]", "☺")
					.replace("[|]", "⎟"));
		}
		return withoutplaceholders;
				
		
	}
	public List<String> getStringListAll(List<String> configstring) {
		if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") == true) {
			String configholdedstring = "PAPIHOLDER";
			List<String> withplaceholders = new ArrayList<String>();
			for(String cfg : configstring) {
				for(Player pla : Bukkit.getOnlinePlayers()) {
				configholdedstring = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(pla.getName()), cfg);
				}
				withplaceholders.add(configholdedstring.replace("&", "§")
						
						.replace("[>>]", "»")
						.replace("[<<]", "«")
						.replace("[coolarrow]", "➤")
						.replace("[<3]", "�?�")
						.replace("[shadowarrow]", "➢")
						.replace("[shadowarrow_2]", "➣")
						.replace("[shadowarrow_down]", "⧨")
						.replace("[shadowsquare]", "�?�")
						.replace("[nuke]", "☢")
						.replace("[+]", "✚")
						.replace("[correct]", "✔")
						.replace("[incorrect]", "✖")
						.replace("[bowarrow]", "➸")
						.replace("[squaredot]", "◼")
						.replace("[square]", "■")
						.replace("[happyface]", "☺")
						.replace("[|]", "⎟"));
			}
			
    return withplaceholders;
		}
		List<String> withoutplaceholders = new ArrayList<String>();
		for(String cfg : configstring) {
			withoutplaceholders.add(cfg.replace("&", "§")
					
					.replace("[>>]", "»")
					.replace("[<<]", "«")
					.replace("[coolarrow]", "➤")
					.replace("[<3]", "�?�")
					.replace("[shadowarrow]", "➢")
					.replace("[shadowarrow_2]", "➣")
					.replace("[shadowarrow_down]", "⧨")
					.replace("[shadowsquare]", "�?�")
					.replace("[nuke]", "☢")
					.replace("[+]", "✚")
					.replace("[correct]", "✔")
					.replace("[incorrect]", "✖")
					.replace("[bowarrow]", "➸")
					.replace("[squaredot]", "◼")
					.replace("[square]", "■")
					.replace("[happyface]", "☺")
					.replace("[|]", "⎟"));
		}
		return withoutplaceholders;
				
		
	}
		@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
		public void onPerformCommand(PlayerCommandPreprocessEvent e) {
			Player p = e.getPlayer();
			String message = e.getMessage();
			if(message.startsWith("/")) {
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
		public void onRankup(XRankUpdateEvent e) {
			if(isBefore1_7) {
				return;
			}
			if(e.getCause() == RankUpdateCause.RANKUPMAX) {
				return;
			}
			Player p = e.getPlayer();
			if(isForceSave()) {
				Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
				playerStorage.savePlayerData(p);
				configManager.saveRankDataConfig();
				configManager.savePrestigeDataConfig();
				configManager.saveRebirthDataConfig();
				}, 20);
			}
			UUID uuid = p.getUniqueId();
			String rank = prxAPI.getPlayerRank(p);
			String path = prxAPI.getPlayerRankPath(p).getPathName();
			String prestige = prxAPI.getPlayerPrestige(p);
			String rebirth = prxAPI.getPlayerRebirth(p);
			if(isVaultGroups) { 
			String nextRank = e.getRankup();
			if(vaultPlugin.equalsIgnoreCase("Vault")) {
			   perms.playerAddGroup(p, nextRank);
			} else if (vaultPlugin.equalsIgnoreCase("LuckPerms")) {
				User lpUser = luckperms.getUserManager().getUser(uuid);
				lpUser.setPrimaryGroup(nextRank);
				luckperms.getUserManager().saveUser(lpUser);
			} else if (vaultPlugin.equalsIgnoreCase("GroupManager")) {
				groupManager.setGroup(p, nextRank);
			} else if (vaultPlugin.equalsIgnoreCase("PermissionsEX")) {
				PermissionUser user = PermissionsEx.getUser(p);
				user.addGroup(nextRank);
			} else {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), globalStorage.getStringData("Options.rankup-vault-groups-plugin").replace("%player%", p.getName()).replace("%rank%", nextRank));
			}
			}
			if(isMySql) {
				//updateMySqlData(p, rank, prestige, rebirth, path);
			}
		}
		
		@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
		public void onAutoRankup(XAutoRankupEvent e) {
			if(isBefore1_7) {
				return;
			}
			Player p = e.getPlayer();
			if(isForceSave()) {
				Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
				playerStorage.savePlayerData(p);
				configManager.saveRankDataConfig();
				configManager.savePrestigeDataConfig();
				configManager.saveRebirthDataConfig();
				}, 20);
			}
			UUID uuid = p.getUniqueId();
			String rank = prxAPI.getPlayerRank(p);
			String path = prxAPI.getPlayerRankPath(p).getPathName();
			String prestige = prxAPI.getPlayerPrestige(p);
			String rebirth = prxAPI.getPlayerRebirth(p);
			if(isVaultGroups) { 
			String nextRank = e.getRankupTo();
			if(vaultPlugin.equalsIgnoreCase("Vault")) {
			   perms.playerAddGroup(p, nextRank);
			} else if (vaultPlugin.equalsIgnoreCase("LuckPerms")) {
				User lpUser = luckperms.getUserManager().getUser(uuid);
				lpUser.setPrimaryGroup(nextRank);
				luckperms.getUserManager().saveUser(lpUser);
			} else if (vaultPlugin.equalsIgnoreCase("GroupManager")) {
				groupManager.setGroup(p, nextRank);
			} else if (vaultPlugin.equalsIgnoreCase("PermissionsEX")) {
				PermissionUser user = PermissionsEx.getUser(p);
				user.addGroup(nextRank);
			} else {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), globalStorage.getStringData("Options.rankup-vault-groups-plugin").replace("%player%", p.getName()).replace("%rank%", nextRank));
			}
			}
			if(isMySql) {
				//updateMySqlData(p, rank, prestige, rebirth, path);
			}
		}
		
		@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
		public void onRankupMax(XRankupMaxEvent e) {
			if(isBefore1_7) {
				return;
			}
			Player p = e.getPlayer();
			if(isForceSave()) {
				Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
				playerStorage.savePlayerData(p);
				configManager.saveRankDataConfig();
				configManager.savePrestigeDataConfig();
				configManager.saveRebirthDataConfig();
				}, 20);
			}
			UUID uuid = p.getUniqueId();
			String rank = prxAPI.getPlayerRank(p);
			String path = prxAPI.getPlayerRankPath(p).getPathName();
			String prestige = prxAPI.getPlayerPrestige(p);
			String rebirth = prxAPI.getPlayerRebirth(p);
			if(isVaultGroups) { 
			String nextRank = e.getFinalRankup();
			if(vaultPlugin.equalsIgnoreCase("Vault")) {
			   perms.playerAddGroup(p, nextRank);
			} else if (vaultPlugin.equalsIgnoreCase("LuckPerms")) {
				User lpUser = luckperms.getUserManager().getUser(uuid);
				lpUser.setPrimaryGroup(nextRank);
				luckperms.getUserManager().saveUser(lpUser);
			} else if (vaultPlugin.equalsIgnoreCase("GroupManager")) {
				groupManager.setGroup(p, nextRank);
			} else if (vaultPlugin.equalsIgnoreCase("PermissionsEX")) {
				PermissionUser user = PermissionsEx.getUser(p);
				user.addGroup(nextRank);
			} else {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), globalStorage.getStringData("Options.rankup-vault-groups-plugin").replace("%player%", p.getName()).replace("%rank%", nextRank));
			}
			}
			if(isMySql) {
				//updateMySqlData(p, rank, prestige, rebirth, path);
			}
		}
		
		@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
		public void onPrestige(XPrestigeUpdateEvent e) {
			if(isBefore1_7) {
				return;
			}
			Player p = e.getPlayer();
			if(isForceSave()) {
				Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
				playerStorage.savePlayerData(p);
				configManager.saveRankDataConfig();
				configManager.savePrestigeDataConfig();
				configManager.saveRebirthDataConfig();
				}, 20);
			}
			String rank = prxAPI.getPlayerRank(p);
			String path = prxAPI.getPlayerRankPath(p).getPathName();
			String prestige = prxAPI.getPlayerPrestige(p);
			String rebirth = prxAPI.getPlayerRebirth(p);
			if(isMySql) {
				//updateMySqlData(p, rank, prestige, rebirth, path);
			}
		}
		
		@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
		public void onRebirth(XRebirthUpdateEvent e) {
			if(isBefore1_7) {
				return;
			}
			Player p = e.getPlayer();
			if(isForceSave()) {
				Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
				playerStorage.savePlayerData(p);
				configManager.saveRankDataConfig();
				configManager.savePrestigeDataConfig();
				configManager.saveRebirthDataConfig();
				}, 20);
			}
			String rank = prxAPI.getPlayerRank(p);
			String path = prxAPI.getPlayerRankPath(p).getPathName();
			String prestige = prxAPI.getPlayerPrestige(p);
			String rebirth = prxAPI.getPlayerRebirth(p);
			if(isMySql) {
				//updateMySqlData(p, rank, prestige, rebirth, path);
			}
		}
		
		
}
