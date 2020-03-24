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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import me.prisonranksx.api.PRXAPI;
import me.prisonranksx.api.PRXManager;
import me.prisonranksx.api.Prestige;
import me.prisonranksx.api.Prestiges;
import me.prisonranksx.api.Ranks;
import me.prisonranksx.api.Rankup;
import me.prisonranksx.api.Rebirth;
import me.prisonranksx.api.Rebirths;
import me.prisonranksx.commands.AutoRankupCommand;
import me.prisonranksx.commands.ForceRankupCommand;
import me.prisonranksx.commands.PRXCommand;
import me.prisonranksx.commands.PrestigeCommand;
import me.prisonranksx.commands.PrestigesCommand;
import me.prisonranksx.commands.PrxTabComplete;
import me.prisonranksx.commands.RanksCommand;
import me.prisonranksx.commands.RankupCommand;
import me.prisonranksx.commands.RankupMaxCommand;
import me.prisonranksx.commands.RebirthCommand;
import me.prisonranksx.commands.RebirthsCommand;
import me.prisonranksx.data.GlobalDataStorage;
import me.prisonranksx.data.MessagesDataStorage;
import me.prisonranksx.data.PlayerDataHandler;
import me.prisonranksx.data.PlayerDataStorage;
import me.prisonranksx.data.PrestigeDataStorage;
import me.prisonranksx.data.RankDataHandler;
import me.prisonranksx.data.RankDataStorage;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.data.RebirthDataStorage;
import me.prisonranksx.data.XUser;
import me.prisonranksx.events.XAutoRankupEvent;
import me.prisonranksx.events.XPrestigeEvent;
import me.prisonranksx.events.XRankUpdateEvent;
import me.prisonranksx.events.XRankupMaxEvent;
import me.prisonranksx.events.XRebirthEvent;
import me.prisonranksx.gui.CustomItemsManager;
import me.prisonranksx.gui.CustomPrestigeItems;
import me.prisonranksx.gui.CustomRankItems;
import me.prisonranksx.gui.CustomRebirthItems;
import me.prisonranksx.gui.GuiListManager;
import me.prisonranksx.hooks.GMHook;
import me.prisonranksx.hooks.MVdWPapiHook;
import me.prisonranksx.hooks.PapiHook;
import me.prisonranksx.permissions.PermissionManager;
import me.prisonranksx.reflections.Actionbar;
import me.prisonranksx.utils.ListUtils;
import me.prisonranksx.utils.NumberAPI;
import me.prisonranksx.utils.OnlinePlayers;
import me.prisonranksx.utils.TempOpProtection;
import me.prisonranksx.utils.XMaterial;
import me.prisonranksx.utils.XUUID;
import me.prisonranksx.utils.CompatibleSound.Sounds;
import me.prisonranksx.utils.CommandLoader;
import me.prisonranksx.utils.ConfigManager;
import me.prisonranksx.utils.ConfigUpdater;

import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.io.Files;

import cloutteam.samjakob.gui.ItemBuilder;
import cloutteam.samjakob.gui.buttons.GUIButton;
import cloutteam.samjakob.gui.types.PaginatedGUI;
import io.samdev.actionutil.ActionUtil;
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
	public Prestige prestigeAPI;
	public me.prisonranksx.api.RankupMax rankupMaxAPI;
	public Ranks ranksAPI;
	public Prestiges prestigesAPI;
	public Rebirth rebirthAPI;
	public Rebirths rebirthsAPI;
	public PRXManager manager;
	public GuiListManager guiManager;
	public boolean isActionUtil;
	public boolean debug = false;
	public RankupCommand rankupCommand;
	public PrestigeCommand prestigeCommand;
	public RankupMaxCommand rankupMaxCommand;
	public RanksCommand ranksCommand;
	public RebirthCommand rebirthCommand;
	public PrestigesCommand prestigesCommand;
	public RebirthsCommand rebirthsCommand;
	public PRXCommand prxCommand;
	public AutoRankupCommand autoRankupCommand;
	public ForceRankupCommand forceRankupCommand;
	public CustomItemsManager cim;
	public CustomRankItems cri;
	public CustomPrestigeItems cpi;
	public CustomRebirthItems crri;
	public MVdWPapiHook mvdw;
	public PapiHook papi;
	public PRXAPI prxAPI;
	public String vaultPlugin;
	public boolean isVaultGroups;
	public LuckPermsApi luckperms;
	public GMHook groupManager;
	public List<String> ignoredSections;
	public boolean isBefore1_7;
	public boolean isRankEnabled;
	public boolean isPrestigeEnabled;
	public boolean isRebirthEnabled;
	BukkitTask ar = null;
	//MySQL
	private boolean isMySql, useSSL, autoReconnect;
    private Connection connection;
    private String host, database, username, password, table;
    private int port;
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

	 public Economy econ = null;
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
	    		Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §eSaving data...");
	    		playerStorage.savePlayersData();
	    		rankStorage.saveRanksData();
	    		prestigeStorage.savePrestigesData();
	    		rebirthStorage.saveRebirthsData();
	    		long timeNow = System.currentTimeMillis() - timeBefore;
	    		Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §aData saved§7, §etook §6(§e" + String.valueOf(timeNow) + " ms§6)§e.");
	    	}, 18000, 18000);
	    }
	    
	public void onEnable() {
        String version = Bukkit.getVersion();
    	if(version.contains("1.5") || version.contains("1.6") || version.contains("1.4") || version.contains("1.3") || version.contains("1.2") || version.endsWith("1.1 )") || version.contains("1.0")) {
    		isBefore1_7 = true;
    	}
        top = new TempOpProtection();
		Bukkit.getPluginManager().registerEvents(this, this);
		  getConfig().options().copyDefaults(true);
		  saveDefaultConfig();
	 	    try {
					ConfigUpdater.update(this, "config.yml", new File(this.getDataFolder() + "/config.yml"), new ArrayList<>());
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
		       rankDataConfig = configManager.rankDataConfig;
		       prestigeDataConfig = configManager.prestigeDataConfig;
		       try {
				ConfigUpdater.update(this, "messages.yml", new File(this.getDataFolder() + "/messages.yml"), new ArrayList<String>());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  globalStorage.loadGlobalData();
			  rankStorage.loadRanksData();
			  prestigeStorage.loadPrestigesData();
			  rebirthStorage.loadRebirthsData();
			  messagesStorage.loadMessages();
			  playerStorage.loadPlayersData();
			  isVaultGroups = globalStorage.getBooleanData("Options.rankup-vault-groups");
			  if(isVaultGroups) {
				  this.vaultPlugin = globalStorage.getStringData("Options.rankup-vault-groups-plugin");
			  }
			  // Command registering {
			  if(configManager.commandsConfig.getBoolean("commands.rankup.enable")) {
			  rankupCommand = new RankupCommand("rankup");
			  CommandLoader.registerCommand("rankup", rankupCommand);
			  }
			  if(configManager.commandsConfig.getBoolean("commands.prestige.enable")) {
			  prestigeCommand = new PrestigeCommand("prestige");
			  CommandLoader.registerCommand("prestige", prestigeCommand);
			  }
			  if(configManager.commandsConfig.getBoolean("commands.rankupmax.enable")) {
			  rankupMaxCommand = new RankupMaxCommand("rankupmax");
			  CommandLoader.registerCommand("rankupmax", rankupMaxCommand);
			  }
			  if(configManager.commandsConfig.getBoolean("commands.ranks.enable")) {
			  ranksCommand = new RanksCommand("ranks");
			  CommandLoader.registerCommand("ranks", ranksCommand);
			  }
			  if(configManager.commandsConfig.getBoolean("commands.rebirth.enable")) {
			  rebirthCommand = new RebirthCommand("rebirth");
			  CommandLoader.registerCommand("rebirth", rebirthCommand);
			  }
			  if(configManager.commandsConfig.getBoolean("commands.prestiges.enable")) {
			  prestigesCommand = new PrestigesCommand("prestiges");
			  CommandLoader.registerCommand("prestiges", prestigesCommand);
			  }
			  if(configManager.commandsConfig.getBoolean("commands.rebirths.enable")) {
			  rebirthsCommand = new RebirthsCommand("rebirths");
			  CommandLoader.registerCommand("rebirths", rebirthsCommand);
			  }
			  if(configManager.commandsConfig.getBoolean("commands.prx.enable")) {
			  prxCommand = new PRXCommand("prx");
			  CommandLoader.registerCommand("prx", prxCommand);
			  }
			  if(configManager.commandsConfig.getBoolean("commands.autorankup.enable")) {
			  autoRankupCommand = new AutoRankupCommand("autorankup");
			  CommandLoader.registerCommand("autorankup", autoRankupCommand);
			  }
			  if(configManager.commandsConfig.getBoolean("commands.forcerankup.enable")) {
			  forceRankupCommand = new ForceRankupCommand("forcerankup");
			  CommandLoader.registerCommand("forcerankup", forceRankupCommand);
			  }
			  // }
			  // API Setup {
			  prxAPI = new PRXAPI();
			  prxAPI.setup();
			  rankupAPI = new Rankup();
			  prestigeAPI = new Prestige();
			  rankupMaxAPI = new me.prisonranksx.api.RankupMax();
			  ranksAPI = new Ranks();
			  ranksAPI.load();
			  prestigesAPI = new Prestiges();
			  prestigesAPI.load();
			  rebirthAPI = new Rebirth();
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
		  if(Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
			  luckperms = LuckPerms.getApi();
		  } else if (Bukkit.getPluginManager().isPluginEnabled("GroupManager")) {
			  groupManager = new GMHook(this);
		  }
			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §aEnabled.");
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
		            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + database + "." + table + " (uuid varchar(255), name varchar(255),rank varchar(255), prestige varchar(255), rebirth varchat(255));");
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
				  guiManager = new GuiListManager(this);
				  guiManager.setupConstantItems();
				  startAsyncUpdateTask();
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
    
    public void updateMySqlData(Player player, String rank, String prestige, String rebirth) {
		try {
			OfflinePlayer p = player;
			ResultSet result = statement.executeQuery("SELECT * FROM " + database + "." + table + " WHERE uuid = '" + p.getUniqueId().toString() + "'");
			if(result.next()) {
				statement.executeUpdate("DELETE FROM " + database + "." + table + " WHERE uuid='" + p.getUniqueId().toString() + "';");
				statement.executeUpdate("INSERT INTO " + database + "." + table + " (uuid, name, rank, prestige, rebirth) VALUES ('" + p.getUniqueId().toString() + "', '" + p.getName() + "', '" + prxAPI.getPlayerRank(p) + "', '" + prxAPI.getPlayerPrestige(p) + "', '" + prxAPI.getPlayerRebirth(p) + "');");
			} else {
				statement.executeUpdate("INSERT INTO " + database + "." + table +" (uuid, name, rank, prestige, rebirth) VALUES ('" + p.getUniqueId().toString() + "', '" + p.getName() + "', '" + prxAPI.getPlayerRank(p) + "', '" + prxAPI.getPlayerPrestige(p) + "', '" +prxAPI.getPlayerRebirth(p) + "');");
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data update failed.");
			e1.printStackTrace();
			getLogger().info("ERROR Updating Player SQL Data");
		}
    }
	//
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §eSaving data...");
		playerStorage.savePlayersData();
		rankStorage.saveRanksData();
		prestigeStorage.savePrestigesData();
		rebirthStorage.saveRebirthsData();
		configManager.saveConfigs();
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
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onJoin(AsyncPlayerPreLoginEvent e) {
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
		if((playerStorage.isRegistered(playerUUID))) {
			 return;
		}
			if((!playerStorage.isRegistered(playerUUID))) {
			 playerStorage.register(playerUUID);
			 RankPath rankPath = new RankPath(globalStorage.getStringData("defaultrank"), globalStorage.getStringData("defaultpath"));
			 playerStorage.setPlayerRankPath(playerUUID, rankPath);
		     playerStorage.setPlayerRank(playerUUID, new RankPath(globalStorage.getStringData("defaultrank"), globalStorage.getStringData("defaultpath")));
            return;
			}
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
		 //CUSTOM COLOR SECTION From RapidTables.
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
		      String nextRebirth = prxAPI.getPlayerNextRebirth(p);
		      Boolean sendFirework = rebirthStorage.isSendFirework(nextRebirth);
		      if(!sendFirework) {
		      	  return;
		      }
			  Firework fz = (Firework) p.getPlayer().getWorld().spawnEntity(p.getPlayer().getLocation(), EntityType.FIREWORK);
	    	  Map<String, Object> fbuilder = rebirthStorage.getFireworkBuilder(nextRebirth);
	          boolean fbuilder_flicker = (boolean)fbuilder.get("flicker");
	          boolean fbuilder_trail = (boolean)fbuilder.get("trail");
	          List<String> fbuilder_effect = (List<String>)fbuilder.get("effect");
	          List<String> fbuilder_color = (List<String>)fbuilder.get("color");
	          List<String> fbuilder_fade = (List<String>)fbuilder.get("fade");
	          Integer fbuilder_power = (Integer)fbuilder.get("power");
	          for(String eff : fbuilder_effect) {
	    	  	   String effecto = eff;
		  	  FireworkMeta fm = fz.getFireworkMeta();
	          fm.addEffect(FireworkEffect.builder()
	            .flicker(fbuilder_flicker)
	            .trail(fbuilder_trail)
	            .with(FireworkEffect.Type.valueOf(effecto.replace("SPARKLE", "BURST").replace("STARS", "STAR")))
	            .withColor(fbuilder_color)
	            .withFade(fbuilder_fade)
	            .build());
	          fm.setPower(fbuilder_power);
	          fz.setFireworkMeta(fm);   
	          }
	    }
	@SuppressWarnings("unused")
	public void sendPrestigeFirework(Player p) {
	      String nextPrestige = prxAPI.getPlayerNextPrestige(p);
	      Boolean sendFirework = prestigeStorage.isSendFirework(nextPrestige);
	      if(!sendFirework) {
	      	  return;
	      }
		  Firework fz = (Firework) p.getPlayer().getWorld().spawnEntity(p.getPlayer().getLocation(), EntityType.FIREWORK);
    	  Map<String, Object> fbuilder = prestigeStorage.getFireworkBuilder(nextPrestige);
          boolean fbuilder_flicker = (boolean)fbuilder.get("flicker");
          boolean fbuilder_trail = (boolean)fbuilder.get("trail");
          List<String> fbuilder_effect = (List<String>)fbuilder.get("effect");
          List<String> fbuilder_color = (List<String>)fbuilder.get("color");
          List<String> fbuilder_fade = (List<String>)fbuilder.get("fade");
          Integer fbuilder_power = (Integer)fbuilder.get("power");
          for(String eff : fbuilder_effect) {
    	  	   String effecto = eff;
	  	  FireworkMeta fm = fz.getFireworkMeta();
          fm.addEffect(FireworkEffect.builder()
            .flicker(fbuilder_flicker)
            .trail(fbuilder_trail)
            .with(FireworkEffect.Type.valueOf(effecto.replace("SPARKLE", "BURST").replace("STARS", "STAR")))
            .withColor(fbuilder_color)
            .withFade(fbuilder_fade)
            .build());
          fm.setPower(fbuilder_power);
          fz.setFireworkMeta(fm);   
          }
    }
	@SuppressWarnings("unused")
	public void sendRankFirework(Player p) {
		String playerRank = null;
			playerRank = playerStorage.getPlayerRank(p);
	    String nextRank = prxAPI.getPlayerNextRank(p);
	    boolean sendFirework = rankStorage.isSendFirework(playerStorage.getPlayerRankPath(p));
	    if(!sendFirework) {
	    	return;
	    }
		Firework fz = (Firework) p.getPlayer().getWorld().spawnEntity(p.getPlayer().getLocation(), EntityType.FIREWORK);
    	Map<String, Object> fbuilder = rankStorage.getFireworkBuilder(playerStorage.getPlayerRankPath(p));
    boolean fbuilder_flicker = (boolean)fbuilder.get("flicker");
    boolean fbuilder_trail = (boolean)fbuilder.get("trail");
    List<String> fbuilder_effect = (List<String>)fbuilder.get("effect");
    List<String> fbuilder_color = (List<String>)fbuilder.get("color");
    List<String> fbuilder_fade = (List<String>)fbuilder.get("fade");
    Integer fbuilder_power = (Integer)fbuilder.get("power");
    	   for(String eff : fbuilder_effect) {
    		   String effecto = eff;
        @SuppressWarnings("null")
		FireworkMeta fm = fz.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder()
        .flicker(fbuilder_flicker)
        .trail(fbuilder_trail)
        .with(FireworkEffect.Type.valueOf(effecto.replace("SPARKLE", "BURST").replace("STARS", "STAR")))
        .withColor(fbuilder_color)
        .withFade(fbuilder_fade)
        .build());
        fm.setPower(fbuilder_power);
        fz.setFireworkMeta(fm);   
    }

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
		if(playerStorage.getPlayerRankPath(p) == null && isRankEnabled) {
			p.sendMessage(prxAPI.c("&cInvalid rank, please relogin to solve this problem."));
			e.setCancelled(true);
			return;
		}
		RankPath playerRankPath = null;
		if(isRankEnabled) {
			 playerRankPath = playerStorage.getPlayerRankPath(p);
		}
		String playerRank = playerRankPath == null ? "" : this.getStringWithoutPAPI(rankStorage.getDisplayName(playerRankPath));
		String playerPrestige = playerStorage.getPlayerPrestige(p) != null  && isPrestigeEnabled ? 
				this.getStringWithoutPAPI(prestigeStorage.getDisplayName(playerStorage.getPlayerPrestige(p))) + " ": getStringWithoutPAPI(globalStorage.getStringData("Options.no-prestige-display"));
		String playerRebirth = playerStorage.getPlayerRebirth(p) != null && isRebirthEnabled ? 
				this.getStringWithoutPAPI(rebirthStorage.getDisplayName(playerStorage.getPlayerRebirth(p))) + " ": getStringWithoutPAPI(globalStorage.getStringData("Options.no-rebirth-display"));
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
    		   if(!p.isOp()) {
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
		String Z = getConfig().getString("Moneyformatter.zillion");
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
					  // if the player tried to execute a command that is not mentioned in executecmds's [op] commands while in temp op mode
					  // stop him :o
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
			Player p = e.getPlayer();
			UUID uuid = p.getUniqueId();
			String rank = prxAPI.getPlayerRank(p);
			String prestige = prxAPI.getPlayerPrestige(p);
			String rebirth = prxAPI.getPlayerRebirth(p);
			if(isVaultGroups) { 
			String nextRank = e.getRankup();
			if(vaultPlugin.equalsIgnoreCase("Vault")) {
			   perms.playerAddGroup(p, nextRank);
			} else if (vaultPlugin.equalsIgnoreCase("LuckPerms")) {
				luckperms.getUser(uuid).setPrimaryGroup(nextRank);
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
				updateMySqlData(p, rank, prestige, rebirth);
			}
		}
		
		@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
		public void onAutoRankup(XAutoRankupEvent e) {
			Player p = e.getPlayer();
			UUID uuid = p.getUniqueId();
			String rank = prxAPI.getPlayerRank(p);
			String prestige = prxAPI.getPlayerPrestige(p);
			String rebirth = prxAPI.getPlayerRebirth(p);
			if(isVaultGroups) { 
			String nextRank = e.getRankupTo();
			if(vaultPlugin.equalsIgnoreCase("Vault")) {
			   perms.playerAddGroup(p, nextRank);
			} else if (vaultPlugin.equalsIgnoreCase("LuckPerms")) {
				luckperms.getUser(uuid).setPrimaryGroup(nextRank);
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
				updateMySqlData(p, rank, prestige, rebirth);
			}
		}
		
		@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
		public void onRankupMax(XRankupMaxEvent e) {
			Player p = e.getPlayer();
			UUID uuid = p.getUniqueId();
			String rank = prxAPI.getPlayerRank(p);
			String prestige = prxAPI.getPlayerPrestige(p);
			String rebirth = prxAPI.getPlayerRebirth(p);
			if(isVaultGroups) { 
			String nextRank = e.getFinalRankup();
			if(vaultPlugin.equalsIgnoreCase("Vault")) {
			   perms.playerAddGroup(p, nextRank);
			} else if (vaultPlugin.equalsIgnoreCase("LuckPerms")) {
				luckperms.getUser(uuid).setPrimaryGroup(nextRank);
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
				updateMySqlData(p, rank, prestige, rebirth);
			}
		}
		
		@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
		public void onPrestige(XPrestigeEvent e) {
			Player p = e.getPlayer();
			String rank = prxAPI.getPlayerRank(p);
			String prestige = prxAPI.getPlayerPrestige(p);
			String rebirth = prxAPI.getPlayerRebirth(p);
			if(isMySql) {
				updateMySqlData(p, rank, prestige, rebirth);
			}
		}
		
		@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
		public void onRebirth(XRebirthEvent e) {
			Player p = e.getPlayer();
			String rank = prxAPI.getPlayerRank(p);
			String prestige = prxAPI.getPlayerPrestige(p);
			String rebirth = prxAPI.getPlayerRebirth(p);
			if(isMySql) {
				updateMySqlData(p, rank, prestige, rebirth);
			}
		}
		
		
}
