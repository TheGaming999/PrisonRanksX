package me.prisonranksx.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.prisonranksx.PrisonRanksX;

public class ConfigManager {

	private PrisonRanksX main;
	
	public ConfigManager(PrisonRanksX main) {
		this.main = main;
	}
	
	public File messagesFile;
	public FileConfiguration messagesConfig;
	public File commandsFile;
	public FileConfiguration commandsConfig;
	public File prestigeDataFile;
	public FileConfiguration prestigeDataConfig;
	public File prestigesFile;
	public FileConfiguration prestigesConfig;
	public File rankDataFile;
	public FileConfiguration rankDataConfig;
	public File ranksFile;
	public FileConfiguration ranksConfig;
	public File rebirthDataFile;
	public FileConfiguration rebirthDataConfig;
	public File rebirthsFile;
	public FileConfiguration rebirthsConfig;
	public List<String> ignoredSections;
	
	public void loadConfigs() {
		ignoredSections = new ArrayList<>();
		ignoredSections.add("Ranklist-gui.current-format.custom");
		ignoredSections.add("Ranklist-gui.completed-format.custom");
		ignoredSections.add("Ranklist-gui.other-format.custom");
		ignoredSections.add("Prestigelist-gui.current-format.custom");
		ignoredSections.add("Prestigelist-gui.completed-format.custom");
		ignoredSections.add("Prestigelist-gui.other-format.custom");
		ignoredSections.add("Rebirthlist-gui.current-format.custom");
		ignoredSections.add("Rebirthlist-gui.completed-format.custom");
		ignoredSections.add("Rebirthlist-gui.other-format.custom");
		createMessagesConfig();
		createCommandsConfig();
		createRankDataConfig();
		createRanksConfig();
		createPrestigeDataConfig();
		createPrestigesConfig();
		createRebirthDataConfig();
		createRebirthsConfig();
		if(!messagesConfig.contains("Messages")) {
		messagesConfig.options().copyDefaults(true);
		}
    	try {
			ConfigUpdater.update(main, "messages.yml", new File(main.getDataFolder() + "/messages.yml"), new ArrayList<String>());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!commandsConfig.contains("commands")) {
			commandsConfig.options().copyDefaults(true);
		}
		if(!rankDataConfig.contains("players")) {
		rankDataConfig.options().copyDefaults(true);
		}
		if(!ranksConfig.contains("Ranks")) {
		ranksConfig.options().copyDefaults(true);
		}
		if(!prestigeDataConfig.contains("players")) {
		prestigeDataConfig.options().copyDefaults(true);
		}
		if(!prestigesConfig.contains("Prestiges")) {
		prestigesConfig.options().copyDefaults(true);
		}
		if(!rebirthDataConfig.contains("players")) {
		rebirthDataConfig.options().copyDefaults(true);
		}
		if(!rebirthsConfig.contains("Rebirths")) {
		rebirthsConfig.options().copyDefaults(true);
		}
	}
	public void saveConfigs() {
		saveCustomYml(messagesConfig, messagesFile);
		saveCustomYml(commandsConfig, commandsFile);
	    saveCustomYml(rankDataConfig, rankDataFile);
		saveCustomYml(ranksConfig, ranksFile);
		saveCustomYml(prestigeDataConfig, prestigeDataFile);
		saveCustomYml(prestigesConfig, prestigesFile);
		saveCustomYml(rebirthDataConfig, rebirthDataFile);
		saveCustomYml(rebirthsConfig, rebirthsFile);
	}
	
	public void saveMessagesConfig() {
		saveCustomYml(messagesConfig, messagesFile);
	}
	
	public void saveCommandsConfig() {
		saveCustomYml(commandsConfig, commandsFile);
	}
	
	public void saveRankDataConfig() {
		saveCustomYml(rankDataConfig, rankDataFile);
	}
	
	public void saveRanksConfig() {
		saveCustomYml(ranksConfig, ranksFile);
	}
	
	public void savePrestigeDataConfig() {
		saveCustomYml(prestigeDataConfig, prestigeDataFile);
	}
	
	public void savePrestigesConfig() {
		saveCustomYml(prestigesConfig, prestigesFile);
	}
	
	public void saveRebirthDataConfig() {
		saveCustomYml(rebirthDataConfig, rebirthDataFile);
	}
	
	public void saveRebirthsConfig() {
		saveCustomYml(rebirthsConfig, rebirthsFile);
	}
	
	public void saveCustomYml(FileConfiguration fileConfiguration, File file) {
		if(file == null) {
			return;
		}
		try {
			fileConfiguration.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void unloadConfigs() {
		messagesFile = null;
		messagesConfig = null;
		commandsFile = null;
		commandsConfig = null;
		rankDataFile = null;
		rankDataConfig = null;
		ranksFile = null;
		ranksConfig = null;
		prestigeDataFile = null;
		prestigeDataConfig = null;
		prestigesFile = null;
		prestigesConfig = null;
		rebirthDataFile = null;
		rebirthDataConfig = null;
		rebirthsFile = null;
		rebirthsConfig = null;
	}
	
	 private void createMessagesConfig() {
	        messagesFile = new File(main.getDataFolder(), "messages.yml");
	        if (!messagesFile.exists()) { messagesFile.getParentFile().mkdirs(); main.saveResource("messages.yml", false); }
	        messagesConfig = new YamlConfiguration();
	            try {
					messagesConfig.load(messagesFile);
				} catch (FileNotFoundException e) { 	System.out.print("File Not Found <?>"); e.printStackTrace(); } catch (IOException e) {
         	System.out.print("Read write Failed <!>"); e.printStackTrace(); } catch (InvalidConfigurationException e) { System.out.print("Corrupted Configuration File <?>");
         	e.printStackTrace(); }
	}
	 private void createCommandsConfig() {
	        commandsFile = new File(main.getDataFolder(), "commands.yml");
	        if (!commandsFile.exists()) { commandsFile.getParentFile().mkdirs(); main.saveResource("commands.yml", false); }
	        commandsConfig = new YamlConfiguration();
	            try {
					commandsConfig.load(commandsFile);
				} catch (FileNotFoundException e) { 	System.out.print("File Not Found <?>"); e.printStackTrace(); } catch (IOException e) {
      	System.out.print("Read write Failed <!>"); e.printStackTrace(); } catch (InvalidConfigurationException e) { System.out.print("Corrupted Configuration File <?>");
      	e.printStackTrace(); }
	}
	 private void createPrestigeDataConfig() {
	        prestigeDataFile = new File(main.getDataFolder(), "prestigedata.yml");
	        if (!prestigeDataFile.exists()) { prestigeDataFile.getParentFile().mkdirs(); main.saveResource("prestigedata.yml", false); }
	        prestigeDataConfig = new YamlConfiguration();
	            try {
					prestigeDataConfig.load(prestigeDataFile);
				} catch (FileNotFoundException e) { 	System.out.print("File Not Found <?>"); e.printStackTrace(); } catch (IOException e) {
   	System.out.print("Read write Failed <!>"); e.printStackTrace(); } catch (InvalidConfigurationException e) { System.out.print("Corrupted Configuration File <?>");
   	e.printStackTrace(); }
	}
	 private void createPrestigesConfig() {
	        prestigesFile = new File(main.getDataFolder(), "prestiges.yml");
	        if (!prestigesFile.exists()) { prestigesFile.getParentFile().mkdirs(); main.saveResource("prestiges.yml", false); }
	        prestigesConfig = new YamlConfiguration();
	            try {
					prestigesConfig.load(prestigesFile);
				} catch (FileNotFoundException e) { 	System.out.print("File Not Found <?>"); e.printStackTrace(); } catch (IOException e) {
	System.out.print("Read write Failed <!>"); e.printStackTrace(); } catch (InvalidConfigurationException e) { System.out.print("Corrupted Configuration File <?>");
	e.printStackTrace(); }
	}
	 private void createRankDataConfig() {
	        rankDataFile = new File(main.getDataFolder(), "rankdata.yml");
	        if (!rankDataFile.exists()) { rankDataFile.getParentFile().mkdirs(); main.saveResource("rankdata.yml", false); }
	        rankDataConfig = new YamlConfiguration();
	            try {
					rankDataConfig.load(rankDataFile);
				} catch (FileNotFoundException e) { 	System.out.print("File Not Found <?>"); e.printStackTrace(); } catch (IOException e) {
	System.out.print("Read write Failed <!>"); e.printStackTrace(); } catch (InvalidConfigurationException e) { System.out.print("Corrupted Configuration File <?>");
	e.printStackTrace(); }
	}
	 private void createRanksConfig() {
	        ranksFile = new File(main.getDataFolder(), "ranks.yml");
	        if (!ranksFile.exists()) { ranksFile.getParentFile().mkdirs(); main.saveResource("ranks.yml", false); }
	        ranksConfig = new YamlConfiguration();
	            try {
					ranksConfig.load(ranksFile);
				} catch (FileNotFoundException e) { 	System.out.print("File Not Found <?>"); e.printStackTrace(); } catch (IOException e) {
	System.out.print("Read write Failed <!>"); e.printStackTrace(); } catch (InvalidConfigurationException e) { System.out.print("Corrupted Configuration File <?>");
	e.printStackTrace(); }
	}
	 private void createRebirthDataConfig() {
	        rebirthDataFile = new File(main.getDataFolder(), "rebirthdata.yml");
	        if (!rebirthDataFile.exists()) { rebirthDataFile.getParentFile().mkdirs(); main.saveResource("rebirthdata.yml", false); }
	        rebirthDataConfig = new YamlConfiguration();
	            try {
					rebirthDataConfig.load(rebirthDataFile);
				} catch (FileNotFoundException e) { 	System.out.print("File Not Found <?>"); e.printStackTrace(); } catch (IOException e) {
	System.out.print("Read write Failed <!>"); e.printStackTrace(); } catch (InvalidConfigurationException e) { System.out.print("Corrupted Configuration File <?>");
	e.printStackTrace(); }
	}
	 private void createRebirthsConfig() {
	        rebirthsFile = new File(main.getDataFolder(), "rebirths.yml");
	        if (!rebirthsFile.exists()) { rebirthDataFile.getParentFile().mkdirs(); main.saveResource("rebirths.yml", false); }
	        rebirthsConfig = new YamlConfiguration();
	            try {
					rebirthsConfig.load(rebirthsFile);
				} catch (FileNotFoundException e) { 	System.out.print("File Not Found <?>"); e.printStackTrace(); } catch (IOException e) {
	System.out.print("Read write Failed <!>"); e.printStackTrace(); } catch (InvalidConfigurationException e) { System.out.print("Corrupted Configuration File <?>");
	e.printStackTrace(); }
	}
	 
	 public void reloadMainConfig() {
	 	    try {
					ConfigUpdater.update(main, "config.yml", new File(main.getDataFolder() + "/config.yml"), ignoredSections);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
 	    main.reloadConfig();
	 }
	 
	 public void saveMainConfig() {
		 main.saveConfig();
	 	    try {
					ConfigUpdater.update(main, "config.yml", new File(main.getDataFolder() + "/config.yml"), ignoredSections);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	 }
	 
    public void reloadConfigs() {
    	try {
			ConfigUpdater.update(main, "messages.yml", new File(main.getDataFolder() + "/messages.yml"), new ArrayList<String>());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			messagesConfig = YamlConfiguration.loadConfiguration(new File("plugins/PrisonRanksX/messages.yml"));
			commandsConfig = YamlConfiguration.loadConfiguration(new File("plugins/PrisonRanksX/commands.yml"));
			rankDataConfig = YamlConfiguration.loadConfiguration(new File("plugins/PrisonRanksX/rankdata.yml"));
			ranksConfig = YamlConfiguration.loadConfiguration(new File("plugins/PrisonRanksX/ranks.yml"));
			prestigeDataConfig = YamlConfiguration.loadConfiguration(new File("plugins/PrisonRanksX/prestigedata.yml"));
			prestigesConfig = YamlConfiguration.loadConfiguration(new File("plugins/PrisonRanksX/prestiges.yml"));
			rebirthDataConfig = YamlConfiguration.loadConfiguration(new File("plugins/PrisonRanksX/rebirthdata.yml"));
			rebirthsConfig = YamlConfiguration.loadConfiguration(new File("plugins/PrisonRanksX/rebirths.yml"));
    }
	
}
