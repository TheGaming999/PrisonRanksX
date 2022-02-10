package me.prisonranksx.data;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.utils.CollectionUtils;

public class RankRandomCommands {

	private String rankName;
	private String pathName;
	private boolean withKeys;
	private Map<String, Object> randomCommandsMap;
	private List<List<String>> commandsList;
	private List<Double> chances;
	
	private static PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	
	public RankRandomCommands(String rankName, boolean withKeys, String pathName) {this.setRankName(rankName); this.withKeys = withKeys; this.setPathName(pathName);
	this.commandsList = CollectionUtils.EMPTY_STRINGLIST_LIST;
	this.chances = CollectionUtils.EMPTY_DOUBLE_LIST;
	this.randomCommandsMap = CollectionUtils.EMPTY_STRING_TO_OBJECT_MAP;
	}
	
	public RankRandomCommands(String rankName, boolean withKeys, String pathName, boolean loadSections) {this.setRankName(rankName); this.withKeys = withKeys; this.setPathName(pathName);
	this.commandsList = CollectionUtils.EMPTY_STRINGLIST_LIST;
	this.chances = CollectionUtils.EMPTY_DOUBLE_LIST;
	this.randomCommandsMap = CollectionUtils.EMPTY_STRING_TO_OBJECT_MAP;
	if(loadSections) {
		loadSections(RankPath.getRankPath(rankName, pathName));
	}
	}
	
	public void loadSections(RankPath rankPath) {
        String pathName = rankPath.getPathName();
        String rankName = rankPath.getRankName();
		Map<String, Object> randomCommandsMap = CollectionUtils.EMPTY_STRING_TO_OBJECT_MAP;
		if(main.getConfigManager().ranksConfig.getConfigurationSection("Ranks." + pathName + "." + rankName + ".randomcmds") != null &&
				!main.getConfigManager().ranksConfig.getConfigurationSection("Ranks." + pathName + "." + rankName + ".randomcmds").getKeys(false).isEmpty()) {
		randomCommandsMap = main.getConfigManager().ranksConfig.getConfigurationSection("Ranks." + pathName + "." + rankName + ".randomcmds").getValues(withKeys);
		
		}
		List<List<String>> commandsList = CollectionUtils.EMPTY_STRINGLIST_LIST;
		List<Double> chances = CollectionUtils.EMPTY_DOUBLE_LIST;
		if(!randomCommandsMap.isEmpty()) {
		for(String section : randomCommandsMap.keySet()) {
			commandsList.add(main.getConfigManager().ranksConfig.getStringList("Ranks." + pathName + "." + rankName + ".randomcmds." + section + ".commands"));
			chances.add(main.getConfigManager().ranksConfig.getDouble("Ranks." + pathName + "." + rankName + ".randomcmds." + section + ".chance"));
		}
		}
		setRandomCommandsMap(randomCommandsMap);
		setCommandsList(commandsList);
		setChances(chances);
	}
	//public Map<String, Object> getCommandSections() {
		//return main.getConfigManager().ranksConfig.getConfigurationSection("Ranks." + pathName + "." + rankName + ".randomcmds").getValues(false);
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

	public String getRankName() {
		return rankName;
	}

	public void setRankName(String rankName) {
		this.rankName = rankName;
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}
	
}
