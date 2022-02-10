package me.prisonranksx.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.plugin.java.JavaPlugin;

import me.prisonranksx.PrisonRanksX;

public class ConfigCreator {

	private final static Map<String, FileConfiguration> configs = new ConcurrentHashMap<>();
	private final static JavaPlugin plugin = PrisonRanksX.getInstance();
	
	public static void createNonUsableConfig(String configName) {
		File configFile = new File(plugin.getDataFolder(), configName);
        if (!configFile.exists()) { configFile.getParentFile().mkdirs(); plugin.saveResource(configName, false); }
	}
	
	/**
	 * Creates a config file and load it if it doesn't exist. otherwise just load it.
	 * @param configName (config .yml name) (example: data.yml)
	 * @return FileConfiguration object of the config name
	 */
	public static FileConfiguration loadConfig(String configName) {
        File configFile = new File(plugin.getDataFolder(), configName);
        if (!configFile.exists()) { configFile.getParentFile().mkdirs(); plugin.saveResource(configName, false); }
        FileConfiguration configYaml = new YamlConfiguration();
            try {
				configYaml.load(configFile);
			} catch (FileNotFoundException e) { 	System.out.print("File Not Found <?>"); e.printStackTrace(); } catch (IOException e) {
     	System.out.print("Read write Failed <!>"); e.printStackTrace(); } catch (InvalidConfigurationException e) { System.out.print("Corrupted Configuration File <?>");
     	e.printStackTrace(); }
            configs.put(configName, configYaml);
		return configYaml;
	}
	


	/**
	 * Creates a config file and load it if it doesn't exist. otherwise just load it.
	 * @param configName (config .yml name) (example: data.yml)
	 * @return FileConfiguration object of the config name
	 */
	/*
	public static FileConfiguration initConfig(String configName, String savePath) {
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(configName);
		FileUtils.copyInputStreamToFile(stream, new File(savePath, configName));
        File configFile = new File(plugin.getDataFolder(), correctConfigName(configName));
        if (!configFile.exists()) { configFile.getParentFile().mkdirs(); plugin.saveResource(correctConfigName(configName), false); }
        FileConfiguration configYaml = new YamlConfiguration();
            try {
				configYaml.load(configFile);
			} catch (FileNotFoundException e) { 	System.out.print("File Not Found <?>"); e.printStackTrace(); } catch (IOException e) {
     	System.out.print("Read write Failed <!>"); e.printStackTrace(); } catch (InvalidConfigurationException e) { System.out.print("Corrupted Configuration File <?>");
     	e.printStackTrace(); }
            configs.put(configName, configYaml);
		return configYaml;
	}
	*/
	
	/**
	 * Creates a config file and load it if it doesn't exist. otherwise just load it.
	 * @param configName (config .yml name) (example: data.yml)
	 * @param copyDefaults should we copy default config sections and paths ?
	 * @return FileConfiguration object of the config name
	 */
	public static FileConfiguration loadConfig(String configName, boolean copyDefaults) {
        File configFile = new File(plugin.getDataFolder(), configName);
        if (!configFile.exists()) { configFile.getParentFile().mkdirs(); plugin.saveResource(configName, false); }
        FileConfiguration configYaml = new YamlConfiguration();
            try {
				configYaml.load(configFile);
			} catch (FileNotFoundException e) { 	System.out.print("File Not Found <?>"); e.printStackTrace(); } catch (IOException e) {
     	System.out.print("Read write Failed <!>"); e.printStackTrace(); } catch (InvalidConfigurationException e) { System.out.print("Corrupted Configuration File <?>");
     	e.printStackTrace(); }
            if(copyDefaults) {
            	configYaml.getDefaults().options().copyDefaults(true);
            }
            configs.put(configName, configYaml);
		return configYaml;
	}
	
	/**
	 * 
	 * @param configName
	 * @return cached config from the map
	 */
	public static FileConfiguration getConfig(String configName) {
		return configs.get(configName);
	}
	
	/**
	 * 
	 * @param configName
	 * @return reloads a config and returns it.
	 */
	public static FileConfiguration reloadConfig(String configName) {
		FileConfiguration configYaml = configs.get(configName);
		configYaml = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), configName));
		return configYaml;
	}
	
	/**
	 * 
	 * @param configName
	 * @return saves the loaded config to your plugin folder and returns it.
	 */
	public static FileConfiguration saveConfig(String configName) {
		FileConfiguration configYaml = configs.get(configName);
		try {
			configYaml.save(new File(plugin.getDataFolder(), configName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return configYaml;
	}
	
}

