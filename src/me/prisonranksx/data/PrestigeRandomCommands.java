package me.prisonranksx.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import me.prisonranksx.PrisonRanksX;

public class PrestigeRandomCommands {
	
	private String prestigeName;
	private boolean withKeys;
	private Map<String, Object> randomCommandsMap;
	private List<List<String>> commandsList;
	private List<Double> chances;
	
	private static PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	
	public PrestigeRandomCommands(String prestigeName, boolean withKeys) {this.setPrestigeName(prestigeName); this.withKeys = withKeys;
	this.commandsList = new ArrayList<>();
	this.chances = new ArrayList<>();
	this.randomCommandsMap = new HashMap<String, Object>();
	}
	
	public PrestigeRandomCommands(String prestigeName, boolean withKeys, boolean loadSections) {this.setPrestigeName(prestigeName); this.withKeys = withKeys;
	this.commandsList = new ArrayList<>();
	this.chances = new ArrayList<>();
	this.randomCommandsMap = new HashMap<String, Object>();
	if(loadSections) {
		loadSections(prestigeName);
	}
	}
	
	public void loadSections(String prestigeName) {
		Map<String, Object> randomCommandsMap = new HashMap<String, Object>();
		if(main.configManager.prestigesConfig.getConfigurationSection("Prestiges."  + prestigeName + ".randomcmds") != null &&
				!main.configManager.prestigesConfig.getConfigurationSection("Prestiges." + prestigeName + ".randomcmds").getKeys(false).isEmpty()) {
		randomCommandsMap = main.configManager.prestigesConfig.getConfigurationSection("Prestiges." + prestigeName + ".randomcmds").getValues(withKeys);
		}
		List<List<String>> commandsList = new ArrayList<>();
		List<Double> chances = new ArrayList<>();
		if(!randomCommandsMap.isEmpty()) {
		for(String section : randomCommandsMap.keySet()) {
			commandsList.add(main.configManager.prestigesConfig.getStringList("Prestiges." + prestigeName + ".randomcmds." + section + ".commands"));
			chances.add(main.configManager.prestigesConfig.getDouble("Prestiges." + prestigeName + ".randomcmds." + section + ".chance"));
		}
		}
		setRandomCommandsMap(randomCommandsMap);
		setCommandsList(commandsList);
		setChances(chances);
	}
	//public Map<String, Object> getCommandSections() {
		//return main.configManager.ranksConfig.getConfigurationSection("Ranks." + pathName + "." + rankName + ".randomcmds").getValues(false);
	//}
	
	public void setRandomCommandsMap(Map<String, Object> randomCommandsMap) {
		this.randomCommandsMap = randomCommandsMap;
	}
	
	public void setCommandsList(List<List<String>> commandsList) {
		this.commandsList = commandsList;
	}
	
	public void setChances(List<Double> chances) {
		this.chances = chances;
	}
	
	public Map<String, Object> getRandomCommandsMap() {
		return randomCommandsMap;
	}
	
	public List<List<String>> getCommandsListCollection() {
		return commandsList;
	}
	
	public List<Double> getChancesCollection() {
		return chances;
	}
    
	public List<String> getCommands(String section) {
		if(randomCommandsMap.containsKey(section)) {
		return ((ConfigurationSection)randomCommandsMap.get(section)).getStringList("commands");
		}
		return null;
	}
	
	public Double getChance(String section) {
		return ((ConfigurationSection)randomCommandsMap.get(section)).getDouble("chance");
	}

	public String getPrestigeName() {
		return prestigeName;
	}

	public void setPrestigeName(String prestigeName) {
		this.prestigeName = prestigeName;
	}
	
}
