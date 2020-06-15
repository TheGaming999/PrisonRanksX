package me.prisonranksx.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import me.prisonranksx.PrisonRanksX;

public class RebirthDataStorage {
	
	public HashMap<String, RebirthDataHandler> rebirthData;
	private PrisonRanksX main;
	public RebirthDataStorage(PrisonRanksX main) {
		this.main = main;
		this.rebirthData = new LinkedHashMap<String, RebirthDataHandler>();
	}
	
	public Map<String, RebirthDataHandler> getRebirthData() {
		return this.rebirthData;
	}
	
	public void putData(String name, RebirthDataHandler rebirthDataHandler) {
		getRebirthData().put(name, rebirthDataHandler);
	}
	
	public RebirthDataHandler getDataHandler(String name) {
		return getRebirthData().get(name);
	}
	/**
	 * Should only be used onEnable()
	 * can be used as a reload
	 */
	public void loadRebirthsData() {
			for(String rebirthName : main.configManager.rebirthsConfig.getConfigurationSection("Rebirths").getKeys(false)) {
				String nextRebirthName = main.configManager.rebirthsConfig.getString("Rebirths." + rebirthName + ".nextrebirth");
				String rebirthDisplayName = main.configManager.rebirthsConfig.getString("Rebirths." + rebirthName + ".display");
				Double rebirthCost = main.configManager.rebirthsConfig.getDouble("Rebirths." + rebirthName + ".cost", 0.0);
				//Double nextRebirthCost = main.configManager.rebirthsConfig.getDouble("Rebirths." + nextRebirthName + ".cost", 0.0);
				//String nextRebirthDisplayName = main.configManager.rebirthsConfig.getString("Rebirths." + nextRebirthName + ".display");
				Double prestigeIncrease = 0.0;
                prestigeIncrease = loadDouble("Rebirths." + rebirthName + ".prestige_cost_increase_percentage");
				List<String> rebirthCommands = main.configManager.rebirthsConfig.getStringList("Rebirths." + rebirthName + ".executecmds");
				//List<String> nextRebirthCommands = main.configManager.rebirthsConfig.getStringList("Rebirths." + nextRebirthName + ".executecmds");
				List<String> actionbarMessages = main.configManager.rebirthsConfig.getStringList("Rebirths." + rebirthName + ".actionbar.text");
				int actionbarInterval = main.configManager.rebirthsConfig.getInt("Rebirths." + rebirthName + ".actionbar.interval");
				List<String> broadcastMessages = main.configManager.rebirthsConfig.getStringList("Rebirths." + rebirthName + ".broadcast");
				List<String> messages = main.configManager.rebirthsConfig.getStringList("Rebirths." + rebirthName + ".text");
				List<String> actions = main.configManager.rebirthsConfig.getStringList("Rebirths." + rebirthName + ".actions");
				List<String> addPermissionList = main.configManager.rebirthsConfig.getStringList("Rebirths." + rebirthName + ".addpermission");
				List<String> delPermissionList = main.configManager.rebirthsConfig.getStringList("Rebirths." + rebirthName + ".delpermission");
                int requiredPrestiges = 0;
                requiredPrestiges = loadInt("Rebirths." + rebirthName + ".required_prestiges");
				RebirthRandomCommands randomCommandsManager = new RebirthRandomCommands(rebirthName, false, true);
				FireworkManager fireworkManager = new FireworkManager(rebirthName, LevelType.REBIRTH, "rebirth");
				boolean sendFirework = main.configManager.rebirthsConfig.getBoolean("Rebirths." + rebirthName + ".send-firework");
				RebirthDataHandler rbdh = new RebirthDataHandler(rebirthName);
				rbdh.setName(rebirthName);
                rbdh.setDisplayName(rebirthDisplayName);
                rbdh.setCost(rebirthCost);
                rbdh.setNextRebirthName(nextRebirthName);
                rbdh.setPrestigeCostIncreasePercentage(prestigeIncrease);
                //rbdh.setNextRebirthCost(nextRebirthCost);
                //rbdh.setNextRebirthDisplayName(nextRebirthDisplayName);
                rbdh.setRebirthCommands(rebirthCommands);
                //rbdh.setNextRebirthCommands(nextRebirthCommands);
                rbdh.setActionbarMessages(actionbarMessages);
                rbdh.setActionbarInterval(actionbarInterval);
                rbdh.setBroadcastMessages(broadcastMessages);
                rbdh.setMsg(messages);
                rbdh.setActions(actions);
                rbdh.setAddPermissionList(addPermissionList);
                rbdh.setDelPermissionList(delPermissionList);
                rbdh.setRandomCommandsManager(randomCommandsManager);
                rbdh.setFireworkManager(fireworkManager);
                rbdh.setSendFirework(sendFirework);
                rbdh.setRequiredPrestiges(requiredPrestiges);
                getRebirthData().put(rebirthName, rbdh);
			}
	}
	
	public Double loadDouble(String node) {
		if(!main.configManager.rebirthsConfig.isSet(node) || !main.configManager.rebirthsConfig.isDouble(node)) {
			return 0.0;
		}
		return main.configManager.rebirthsConfig.getDouble(node, 0.0);
	}
	
	public Integer loadInt(String node) {
		if(!main.configManager.rebirthsConfig.isSet(node) || !main.configManager.rebirthsConfig.isInt(node)) {
			return 0;
		}
		return main.configManager.rebirthsConfig.getInt(node, 0);
	}
	
	public void loadRebirthData(String rebirthName) {
		String nextRebirthName = main.configManager.rebirthsConfig.getString("Rebirths." + rebirthName + ".nextrebirth");
		String rebirthDisplayName = main.configManager.rebirthsConfig.getString("Rebirths." + rebirthName + ".display");
		Double rebirthCost = main.configManager.rebirthsConfig.getDouble("Rebirths." + rebirthName + ".cost", 0.0);
		Double nextRebirthCost = main.configManager.rebirthsConfig.getDouble("Rebirths." + nextRebirthName + ".cost", 0.0);
		String nextRebirthDisplayName = main.configManager.rebirthsConfig.getString("Rebirths." + nextRebirthName + ".display");
		List<String> rebirthCommands = main.configManager.rebirthsConfig.getStringList("Rebirths." + rebirthName + ".executecmds");
		//List<String> nextRebirthCommands = main.configManager.rebirthsConfig.getStringList("Rebirths." + nextRebirthName + ".executecmds");
		List<String> actionbarMessages = main.configManager.rebirthsConfig.getStringList("Rebirths." + rebirthName + ".actionbar.text");
		int actionbarInterval = main.configManager.rebirthsConfig.getInt("Rebirths." + rebirthName + ".actionbar.interval");
		List<String> broadcastMessages = main.configManager.rebirthsConfig.getStringList("Rebirths." + rebirthName + ".broadcast");
		List<String> messages = main.configManager.rebirthsConfig.getStringList("Rebirths." + rebirthName + ".text");
		List<String> actions = main.configManager.rebirthsConfig.getStringList("Rebirths." + rebirthName + ".actions");
		List<String> addPermissionList = main.configManager.rebirthsConfig.getStringList("Rebirths." + rebirthName + ".addpermission");
		List<String> delPermissionList = main.configManager.rebirthsConfig.getStringList("Rebirths." + rebirthName + ".delpermission");
		RebirthRandomCommands randomCommandsManager = new RebirthRandomCommands(rebirthName, true);
		FireworkManager fireworkManager = new FireworkManager(rebirthName, LevelType.REBIRTH, "rebirth");
		boolean sendFirework = main.configManager.rebirthsConfig.getBoolean("Rebirths." + rebirthName + ".send-firework");
		RebirthDataHandler rbdh = new RebirthDataHandler(rebirthName);
		rbdh.setName(rebirthName);
        rbdh.setDisplayName(rebirthDisplayName);
        rbdh.setCost(rebirthCost);
        rbdh.setNextRebirthName(nextRebirthName);
        rbdh.setNextRebirthCost(nextRebirthCost);
        rbdh.setNextRebirthDisplayName(nextRebirthDisplayName);
        //rbdh.setNextRebirthCommands(nextRebirthCommands);
        rbdh.setRebirthCommands(rebirthCommands);
        rbdh.setActionbarMessages(actionbarMessages);
        rbdh.setActionbarInterval(actionbarInterval);
        rbdh.setBroadcastMessages(broadcastMessages);
        rbdh.setMsg(messages);
        rbdh.setActions(actions);
        rbdh.setAddPermissionList(addPermissionList);
        rbdh.setDelPermissionList(delPermissionList);
        rbdh.setRandomCommandsManager(randomCommandsManager);
        rbdh.setFireworkManager(fireworkManager);
        rbdh.setSendFirework(sendFirework);
        getRebirthData().put(rebirthName, rbdh);
	}
	
	/**
	 * 
	 * @return rebirths collection
	 */
	public List<String> getRebirthsCollection() {
		return Arrays.asList(this.rebirthData.keySet().toArray(new String[0]));
	}
	
	public String getNextRebirthName(String rebirthName) {
		return rebirthData.get(rebirthName).getNextRebirthName();
	}
	
	public Double getCost(String rebirthName) {
		return rebirthData.get(rebirthName).getCost();
	}
	
	public String getDisplayName(String rebirthName) {
		return rebirthData.get(rebirthName).getDisplayName();
	}
	
	
	public Double getNextRebirthCost(String rebirthName) {
		return rebirthData.get(rebirthData.get(rebirthName).getNextRebirthName()).getCost();
	}
	
	public String getNextRebirthDisplayName(String rebirthName) {
		return rebirthData.get(rebirthData.get(rebirthName).getNextRebirthName()).getDisplayName();
	}
	
	public List<String> getRebirthCommands(String rebirthName) {
		return rebirthData.get(rebirthName).getRebirthCommands();
	}
	
	public List<String> getNextRebirthCommands(String rebirthName) {
		return rebirthData.get(rebirthData.get(rebirthName).getNextRebirthName()).getRebirthCommands();
	}
	
	public int getActionbarInterval(String rebirthName) {
		return rebirthData.get(rebirthName).getActionbarInterval();
	}
	
	public int getNextActionbarInterval(String rebirthName) {
		return rebirthData.get(rebirthData.get(rebirthName).getNextRebirthName()).getActionbarInterval();
	}
	
	public List<String> getActionbarMessages(String rebirthName) {
		return rebirthData.get(rebirthName).getActionbarMessages();
	}
	
	public List<String> getBroadcast(String rebirthName) {
		return rebirthData.get(rebirthName).getBroadcast();
	}
	
	public List<String> getMsg(String rebirthName) {
		return rebirthData.get(rebirthName).getMsg();
	}
	
	public List<String> getActions(String rebirthName) {
		return rebirthData.get(rebirthName).getActions();
	}
	
	public List<String> getAddPermissionList(String rebirthName) {
		return rebirthData.get(rebirthName).getAddPermissionList();
	}
	
	public List<String> getDelPermissionList(String rebirthName) {
		return rebirthData.get(rebirthName).getDelPermissionList();
	}
	
	public RebirthRandomCommands getRandomCommandsManager(String rebirthName) {
		return rebirthData.get(rebirthName).getRandomCommandsManager();
	}
	
	public List<String> getRandomCommands(String rebirthName) {
		return rebirthData.get(rebirthName).getRebirthCommands();
	}
	
	public FireworkManager getFireworkManager(String rebirthName) {
		return rebirthData.get(rebirthName).getFireworkManager();
	}
	
	public Map<String, Object> getFireworkBuilder(String rebirthName) {
		return rebirthData.get(rebirthName).getFireworkManager().getFireworkBuilder();
	}
	
	public boolean isSendFirework(String rebirthName) {
		return rebirthData.get(rebirthName).isSendFirework();
	}
	
	public Double getPrestigeCostIncreasePercentage(String rebirthName) {
		return rebirthData.get(rebirthName).getPrestigeCostIncreasePercentage();
	}
	
	public int getRequiredPrestiges(String rebirthName) {
		return rebirthData.get(rebirthName).getRequiredPrestiges();
	}
	
	public void setData(String node, Object value) {
		if(value == null || node.contains("LASTREBIRTH")) {
			return;
		}
		if(value instanceof Integer) {
			if((int)value == 0) {
				return;
			}
		}
		if(value instanceof Double) {
			if((double)value == 0) {
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
		main.configManager.rebirthsConfig.set(node, value);
	}
	/**
	 * 
	 * Use when you want to update a Rebirth option then
	 * use the method loadRebirthData(String rebirthName) to load the data in game
	 */
	public void saveRebirthData(String rebirthName) {
			String nextRebirth = rebirthData.get(rebirthName).getNextRebirthName();
			String rebirth = rebirthName;
            setData("Rebirths." + rebirth + ".nextrebirth", rebirthData.get(rebirthName).getNextRebirthName());
            setData("Rebirths." + rebirth + ".cost", rebirthData.get(rebirthName).getCost());
            setData("Rebirths." + rebirth + ".display", rebirthData.get(rebirthName).getDisplayName());
            // setData("Rebirths." + rebirth + ".executecmds", rebirthData.get(rebirthName).getRebirthCommands());
            // setData("Rebirths." + nextRebirth + ".cost", rebirthData.get(rebirthName).getNextRebirthCost());
            // setData("Rebirths." + nextRebirth + ".display", rebirthData.get(rebirthName).getNextRebirthDisplayName());
            // setData("Rebirths." + nextRebirth + ".executecmds", rebirthData.get(rebirthName).getNextRebirthCommands());
            // setData("Rebirths." + rebirthData.get(rebirthName) + ".actionbar.interval", rebirthData.get(rebirthName).getActionbarInterval());
            // setData("Rebirths." + rebirthData.get(rebirthName) + ".actionbar.text", rebirthData.get(rebirthName).getActionbarMessages());
            // setData("Rebirths." + rebirthData.get(rebirthName) + ".broadcast", rebirthData.get(rebirthName).getBroadcast());
            // setData("Rebirths." + rebirthData.get(rebirthName) + ".msg", rebirthData.get(rebirthName).getMsg());
            // setData("Rebirths." + rebirthData.get(rebirthName) + ".actions", rebirthData.get(rebirthName).getActions());
            // setData("Rebirths." + rebirthData.get(rebirthName) + ".addpermission", rebirthData.get(rebirthName).getAddPermissionList());
            // setData("Rebirths." + rebirthData.get(rebirthName) + ".delpermission", rebirthData.get(rebirthName).getDelPermissionList());
            // setData("Rebirths." + rebirthData.get(rebirthName) + ".randomcmds", rebirthData.get(rebirthName).getRandomCommandsManager().getRandomCommandsMap());
            // setData("Rebirths." + rebirthData.get(rebirthName) + ".firework", rebirthData.get(rebirthName).getFireworkManager());
            // setData("Rebirths." + rebirthData.get(rebirthName) + ".send-firework", rebirthData.get(rebirthName).isSendFirework());
	}
	/**
	 * Should only be used onDisable()
	 */
	public void saveRebirthsData() {
		if(!main.isRebirthEnabled) {
			return;
		}
			for(Entry<String, RebirthDataHandler> rebirth : rebirthData.entrySet()) {
				String nextRebirth = rebirthData.get(rebirth.getKey()).getNextRebirthName();
                 setData("Rebirths." + rebirth.getKey() + ".nextrebirth", rebirth.getValue().getNextRebirthName());
                 setData("Rebirths." + rebirth.getKey() + ".cost", rebirth.getValue().getCost());
                 setData("Rebirths." + rebirth.getKey() + ".executecmds", rebirth.getValue().getRebirthCommands());
                 //setData("Rebirths." + nextRebirth + ".cost", rebirth.getValue().getNextRebirthCost());
                 //setData("Rebirths." + nextRebirth + ".display", rebirth.getValue().getNextRebirthDisplayName());
                 //setData("Rebirths." + nextRebirth + ".executecmds", rebirth.getValue().getNextRebirthCommands());
                 setData("Rebirths." + rebirth.getKey() + ".actionbar.interval", rebirth.getValue().getActionbarInterval());
                 setData("Rebirths." + rebirth.getKey() + ".actionbar.text", rebirth.getValue().getActionbarMessages());
                 setData("Rebirths." + rebirth.getKey() + ".broadcast", rebirth.getValue().getBroadcast());
                 setData("Rebirths." + rebirth.getKey() + ".msg", rebirth.getValue().getMsg());
                 setData("Rebirths." + rebirth.getKey() + ".actions", rebirth.getValue().getActions());
                 setData("Rebirths." + rebirth.getKey() + ".addpermission", rebirth.getValue().getAddPermissionList());
                 setData("Rebirths." + rebirth.getKey() + ".delpermission", rebirth.getValue().getDelPermissionList());
                 setData("Rebirths." + rebirth.getKey() + ".prestige_cost_increase_percentage", rebirth.getValue().getPrestigeCostIncreasePercentage());
                 setData("Rebirths." + rebirth.getKey() + ".required_prestiges", rebirth.getValue().getRequiredPrestiges());
                 if(rebirth.getValue().getRandomCommandsManager() != null) {
                // setData("Rebirths." + rebirth.getKey() + ".randomcmds", rebirth.getValue().getRandomCommandsManager().getRandomCommandsMap());
                 }
                 if(rebirth.getValue().getFireworkManager() != null) {
                // setData("Rebirths." + rebirth.getKey() + ".firework", rebirth.getValue().getFireworkManager());
                 }
                 if(rebirth.getValue().isSendFirework()) {
                 setData("Rebirths." + rebirth.getKey() + ".send-firework", rebirth.getValue().isSendFirework());
                 }
			}
	}
}
