package me.prisonranksx.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import me.prisonranksx.PrisonRanksX;

public class PrestigeDataStorage {
	private Map<String, PrestigeDataHandler> prestigeData;
	private PrisonRanksX main;
	public PrestigeDataStorage(PrisonRanksX main) {
		this.main = main;
		this.prestigeData = new LinkedHashMap<String, PrestigeDataHandler>();
	}
	
	public void putData(String name, PrestigeDataHandler prestigeDataHandler) {
		prestigeData.put(name, prestigeDataHandler);
	}
	
	public PrestigeDataHandler getDataHandler(String name) {
		return prestigeData.get(name);
	}
	/**
	 * Should only be used onEnable()
	 * can be used as a reload
	 */
	public void loadPrestigesData() {

			for(String prestigeName : main.configManager.prestigesConfig.getConfigurationSection("Prestiges").getKeys(false)) {
				String nextPrestigeName = loadString("Prestiges." + prestigeName + ".nextprestige");
				String prestigeDisplayName = loadString("Prestiges." + prestigeName + ".display");
				Double prestigeCost = loadDouble("Prestiges." + prestigeName + ".cost");
				//Double nextPrestigeCost = loadDouble("Prestiges." + nextPrestigeName + ".cost");
				//String nextPrestigeDisplayName = loadString("Prestiges." + nextPrestigeName + ".display");
				Double rankupCostIncreasePercentage = 0.0;
				rankupCostIncreasePercentage = loadDouble("Prestiges." + prestigeName + ".rankup_cost_increase_percentage");
				List<String> prestigeCommands = loadStringList("Prestiges." + prestigeName + ".executecmds");
				List<String> actionbarMessages = loadStringList("Prestiges." + prestigeName + ".actionbar.text");
				Integer actionbarInterval = loadInt("Prestiges." + prestigeName + ".actionbar.interval");
				List<String> broadcastMessages = loadStringList("Prestiges." + prestigeName + ".broadcast");
				List<String> messages = loadStringList("Prestiges." + prestigeName + ".text");
				List<String> actions = loadStringList("Prestiges." + prestigeName + ".actions");
				List<String> addPermissionList = loadStringList("Prestiges." + prestigeName + ".addpermission");
				List<String> delPermissionList = loadStringList("Prestiges." + prestigeName + ".delpermission");
				PrestigeRandomCommands randomCommandsManager = new PrestigeRandomCommands(prestigeName, false, true);
				FireworkManager fireworkManager = new FireworkManager(prestigeName, LevelType.PRESTIGE, "prestige");
				Boolean sendFirework = loadBoolean("Prestiges." + prestigeName + ".send-firework");
				PrestigeDataHandler pdh = new PrestigeDataHandler(prestigeName);
				pdh.setName(prestigeName);
                pdh.setDisplayName(prestigeDisplayName);
                pdh.setCost(prestigeCost);
                pdh.setNextPrestigeName(nextPrestigeName);
                //pdh.setNextPrestigeCost(nextPrestigeCost);
                //pdh.setNextPrestigeDisplayName(nextPrestigeDisplayName);
                pdh.setRankupCostIncreasePercentage(rankupCostIncreasePercentage);
                pdh.setPrestigeCommands(prestigeCommands);
                pdh.setActionbarMessages(actionbarMessages);
                pdh.setActionbarInterval(actionbarInterval);
                pdh.setBroadcastMessages(broadcastMessages);
                pdh.setMsg(messages);
                pdh.setActions(actions);
                pdh.setAddPermissionList(addPermissionList);
                pdh.setDelPermissionList(delPermissionList);
                pdh.setRandomCommandsManager(randomCommandsManager);
                pdh.setFireworkManager(fireworkManager);
                pdh.setSendFirework(sendFirework);
                getPrestigeData().put(prestigeName, pdh);
			}
	}
	
	public String loadString(String node) {
		if(main.configManager.prestigesConfig.getString(node) == null || main.configManager.prestigesConfig.getString(node).isEmpty()) {
			return "null";
		}
		return main.configManager.prestigesConfig.getString(node, "null");
	}
	
	public List<String> loadStringList(String node) {
		if(main.configManager.prestigesConfig.getStringList(node) == null || main.configManager.prestigesConfig.getStringList(node).isEmpty()) {
			return new ArrayList<String>();
		}
		return main.configManager.prestigesConfig.getStringList(node);
	}
	
	public Integer loadInt(String node) {
		if(main.configManager.prestigesConfig.get(node) == null) {
			return 0;
		}
		return main.configManager.prestigesConfig.getInt(node, 0);
	}
	
	public Boolean loadBoolean(String node) {
		return main.configManager.prestigesConfig.getBoolean(node, false);
	}
	
	public Double loadDouble(String node) {
		if(main.configManager.prestigesConfig.get(node) == null) {
			return 0.0;
		}
		return main.configManager.prestigesConfig.getDouble(node, 0.0);
	}
	
	public void loadPrestigeData(String prestigeName) {
		String nextPrestigeName = loadString("Prestiges." + prestigeName + ".nextprestige");
		String prestigeDisplayName = loadString("Prestiges." + prestigeName + ".display");
		Double prestigeCost = loadDouble("Prestiges." + prestigeName + ".cost");
		//Double nextPrestigeCost = loadDouble("Prestiges." + nextPrestigeName + ".cost");
		//String nextPrestigeDisplayName = loadString("Prestiges." + nextPrestigeName + ".display");
		Double rankupCostIncreasePercentage = loadDouble("Prestiges." + prestigeName + ".rankup_cost_increase_percentage");
		List<String> prestigeCommands = loadStringList("Prestiges." + prestigeName + ".executecmds");
		List<String> actionbarMessages = loadStringList("Prestiges." + prestigeName + ".actionbar.text");
		int actionbarInterval = loadInt("Prestiges." + prestigeName + ".actionbar.interval");
		List<String> broadcastMessages = loadStringList("Prestiges." + prestigeName + ".broadcast");
		List<String> messages = loadStringList("Prestiges." + prestigeName + ".text");
		List<String> actions = loadStringList("Prestiges." + prestigeName + ".actions");
		List<String> addPermissionList = loadStringList("Prestiges." + prestigeName + ".addpermission");
		List<String> delPermissionList = loadStringList("Prestiges." + prestigeName + ".delpermission");
		PrestigeRandomCommands randomCommandsManager = new PrestigeRandomCommands(prestigeName, true, true);
		FireworkManager fireworkManager = new FireworkManager(prestigeName, LevelType.PRESTIGE, "prestige");
		boolean sendFirework = loadBoolean("Prestiges." + prestigeName + ".send-firework");
		PrestigeDataHandler pdh = new PrestigeDataHandler(prestigeName);
		pdh.setName(prestigeName);
        pdh.setDisplayName(prestigeDisplayName);
        pdh.setCost(prestigeCost);
        pdh.setNextPrestigeName(nextPrestigeName);
        //pdh.setNextPrestigeCost(nextPrestigeCost);
        //pdh.setNextPrestigeDisplayName(nextPrestigeDisplayName);
        pdh.setRankupCostIncreasePercentage(rankupCostIncreasePercentage);
        pdh.setPrestigeCommands(prestigeCommands);
        pdh.setActionbarMessages(actionbarMessages);
        pdh.setActionbarInterval(actionbarInterval);
        pdh.setBroadcastMessages(broadcastMessages);
        pdh.setMsg(messages);
        pdh.setActions(actions);
        pdh.setAddPermissionList(addPermissionList);
        pdh.setDelPermissionList(delPermissionList);
        pdh.setRandomCommandsManager(randomCommandsManager);
        pdh.setFireworkManager(fireworkManager);
        pdh.setSendFirework(sendFirework);
        getPrestigeData().put(prestigeName, pdh);
	}
	
	public void initPrestigeData() {
		this.prestigeData = new LinkedHashMap<String, PrestigeDataHandler>();
	}
	
	public Map<String, PrestigeDataHandler> getPrestigeData() {
		return this.prestigeData;
	}
	
	/**
	 * 
	 * @return prestiges collection
	 */
	public List<String> getPrestigesCollection() {
		return Arrays.asList(prestigeData.keySet().toArray(new String[0]));
	}
	
	public String getNextPrestigeName(String prestigeName) {
		return getPrestigeData().get(prestigeName).getNextPrestigeName();
	}
	
	public Double getCost(String prestigeName) {
		return getPrestigeData().get(prestigeName).getCost();
	}
	
	public String getDisplayName(String prestigeName) {
		return getPrestigeData().get(prestigeName).getDisplayName();
	}
	
	public Double getRankupCostIncreasePercentage(String prestigeName) {
		return getPrestigeData().get(prestigeName).getRankupCostIncreasePercentage();
	}
	
	public Double getNextPrestigeCost(String prestigeName) {
		return getPrestigeData().get(getPrestigeData().get(prestigeName).getNextPrestigeName()).getCost();
	}
	
	public String getNextPrestigeDisplayName(String prestigeName) {
		return getPrestigeData().get(getPrestigeData().get(prestigeName).getNextPrestigeName()).getDisplayName();
	}
	
	public List<String> getPrestigeCommands(String prestigeName) {
		return getPrestigeData().get(prestigeName).getPrestigeCommands();
	}
	
	public int getActionbarInterval(String prestigeName) {
		return getPrestigeData().get(prestigeName).getActionbarInterval();
	}
	
	public List<String> getActionbarMessages(String prestigeName) {
		return getPrestigeData().get(prestigeName).getActionbarMessages();
	}
	
	public List<String> getBroadcast(String prestigeName) {
		return getPrestigeData().get(prestigeName).getBroadcast();
	}
	
	public List<String> getMsg(String prestigeName) {
		return getPrestigeData().get(prestigeName).getMsg();
	}
	
	public List<String> getActions(String prestigeName) {
		return getPrestigeData().get(prestigeName).getActions();
	}
	
	public List<String> getAddPermissionList(String prestigeName) {
		return getPrestigeData().get(prestigeName).getAddPermissionList();
	}
	
	public List<String> getDelPermissionList(String prestigeName) {
		return getPrestigeData().get(prestigeName).getDelPermissionList();
	}
	
	public PrestigeRandomCommands getRandomCommandsManager(String prestigeName) {
		return getPrestigeData().get(prestigeName).getRandomCommandsManager();
	}
	
	public FireworkManager getFireworkManager(String prestigeName) {
		return getPrestigeData().get(prestigeName).getFireworkManager();
	}
	
	public Map<String, Object> getFireworkBuilder(String prestigeName) {
		return getPrestigeData().get(prestigeName).getFireworkManager().getFireworkBuilder();
	}
	
	public boolean isSendFirework(String prestigeName) {
		return getPrestigeData().get(prestigeName).getSendFirework();
	}
	
	public String getValues(String prestigeName) {
		return getPrestigeData().get(prestigeName).getValues();
	}
	
	/**
	 * 
	 * Use when you want to update a prestige option then
	 * use the method loadRankData(String prestigeName) to load the data in game
	 */
	public void savePrestigeData(String prestigeName) {
			String nextPrestige = getPrestigeData().get(prestigeName).getNextPrestigeName();
			String prestige = prestigeName;
            setData("Prestiges." + prestige + ".nextprestige", nextPrestige);
            setData("Prestiges." + prestige + ".cost", getPrestigeData().get(prestigeName).getCost());
            setData("Prestiges." + prestige + ".display", getPrestigeData().get(prestigeName).getDisplayName());
            setData("Prestiges." + prestige + ".rankup_cost_increase_percentage", getPrestigeData().get(prestigeName).getRankupCostIncreasePercentage());
           // setData("Prestiges." + prestige + ".executecmds", getPrestigeData().get(prestigeName).getPrestigeCommands());
           // setData("Prestiges." + prestige + ".actionbar.interval", getPrestigeData().get(prestigeName).getActionbarInterval());
           // setData("Prestiges." + prestige + ".actionbar.text", getPrestigeData().get(prestigeName).getActionbarMessages());
           // setData("Prestiges." + prestige + ".broadcast", getPrestigeData().get(prestigeName).getBroadcast());
           // setData("Prestiges." + prestige + ".msg", getPrestigeData().get(prestigeName).getMsg());
           // setData("Prestiges." + prestige + ".actions", getPrestigeData().get(prestigeName).getActions());
           // setData("Prestiges." + prestige + ".addpermission", getPrestigeData().get(prestigeName).getAddPermissionList());
           // setData("Prestiges." + prestige + ".delpermission", getPrestigeData().get(prestigeName).getDelPermissionList());
           // setData("Prestiges." + prestige + ".randomcmds", getPrestigeData().get(prestigeName).getRandomCommandsManager().getRandomCommandsMap());
           // setData("Prestiges." + prestige + ".firework", getPrestigeData().get(prestigeName).getFireworkManager());
           // setData("Prestiges." + prestige + ".send-firework", getPrestigeData().get(prestigeName).getSendFirework());
	}
	public void setData(String node, Object value) {
		if(value == null || node.contains("LASTPRESTIGE")) {
			return;
		}
		if(value instanceof Integer) {
			if((int)value == 0) {
				return;
			}
		}
		if(value instanceof Boolean) {
			if((boolean)value == false) {
				return;
			}
		}
		if(value instanceof List) {
			if(((List<String>)value).isEmpty()) {
				return;
			}
		}
		if(value instanceof Set) {
			if(((Set<String>)value).isEmpty()) {
				return;
			}
		}
		if(value instanceof Map) {
			if(((Map<String,Object>)value).isEmpty()) {
				return;
			}
		}
		main.configManager.prestigesConfig.set(node, value);
	}
	/**
	 * Should only be used onDisable()
	 */
	public void savePrestigesData() {
		if(!main.isPrestigeEnabled) {
			return;
		}
			for(Entry<String, PrestigeDataHandler> prestige : prestigeData.entrySet()) {
				String nextPrestige = getPrestigeData().get(prestige.getKey()).getNextPrestigeName();
                 setData("Prestiges." + prestige.getKey() + ".nextprestige", prestige.getValue().getNextPrestigeName());
                 setData("Prestiges." + prestige.getKey() + ".cost", prestige.getValue().getCost());
                 setData("Prestiges." + prestige.getKey() + ".display", prestige.getValue().getDisplayName());
                 setData("Prestiges." + prestige.getKey() + ".rankup_cost_increase_percentage", prestige.getValue().getRankupCostIncreasePercentage());
                 setData("Prestiges." + prestige.getKey() + ".executecmds", prestige.getValue().getPrestigeCommands());
                 setData("Prestiges." + prestige.getKey() + ".actionbar.interval", prestige.getValue().getActionbarInterval());
                 setData("Prestiges." + prestige.getKey() + ".actionbar.text", prestige.getValue().getActionbarMessages());
                 setData("Prestiges." + prestige.getKey() + ".broadcast", prestige.getValue().getBroadcast());
                 setData("Prestiges." + prestige.getKey() + ".msg", prestige.getValue().getMsg());
                 setData("Prestiges." + prestige.getKey() + ".actions", prestige.getValue().getActions());
                 setData("Prestiges." + prestige.getKey() + ".addpermission", prestige.getValue().getAddPermissionList());
                 setData("Prestiges." + prestige.getKey() + ".delpermission", prestige.getValue().getDelPermissionList());
                 if(prestige.getValue().getRandomCommandsManager() != null) {
                // setData("Prestiges." + prestige.getKey() + ".randomcmds", prestige.getValue().getRandomCommandsManager().getRandomCommandsMap());
                 }
                 if(prestige.getValue().getFireworkManager() != null) {
                // setData("Prestiges." + prestige.getKey() + ".firework", prestige.getValue().getFireworkManager());
                 }
                 if(prestige.getValue().getSendFirework()) {
                 setData("Prestiges." + prestige.getKey() + ".send-firework", prestige.getValue().getSendFirework());
                 }
			}
	}
}
