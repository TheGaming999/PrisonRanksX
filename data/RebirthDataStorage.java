package me.prisonranksx.data;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.utils.MCTextEffect;

public class RebirthDataStorage {

	public Map<String, RebirthDataHandler> rebirthData;
	private PrisonRanksX main;

	public RebirthDataStorage(PrisonRanksX main) {
		this.main = main;
		this.rebirthData = new LinkedHashMap<>();
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

	public GlobalDataStorage gds() {
		return this.main.globalStorage;
	}
	
	/**
	 * Should only be used onEnable()
	 * can be used as a reload
	 */
	public void loadRebirthsData() {
		for(String rebirthName : main.getConfigManager().rebirthsConfig.getConfigurationSection("Rebirths").getKeys(false)) {
			String nextRebirthName = main.getConfigManager().rebirthsConfig.getString("Rebirths." + rebirthName + ".nextrebirth");
			String rebirthDisplayName = main.getConfigManager().rebirthsConfig.getString("Rebirths." + rebirthName + ".display");
			double rebirthCost = main.getConfigManager().rebirthsConfig.getDouble("Rebirths." + rebirthName + ".cost", 0.0);
			double prestigeIncrease = 0.0;
			prestigeIncrease = loadDouble("Rebirths." + rebirthName + ".prestige_cost_increase_percentage");
			List<String> rebirthCommands = main.getConfigManager().rebirthsConfig.getStringList("Rebirths." + rebirthName + ".executecmds");
			List<String> actionbarMessages = MCTextEffect.parseGlow(main.getConfigManager().rebirthsConfig.getStringList("Rebirths." + rebirthName + ".actionbar.text"));
			int actionbarInterval = main.getConfigManager().rebirthsConfig.getInt("Rebirths." + rebirthName + ".actionbar.interval");
			List<String> broadcastMessages = main.getConfigManager().rebirthsConfig.getStringList("Rebirths." + rebirthName + ".broadcast");
			List<String> messages = main.getConfigManager().rebirthsConfig.getStringList("Rebirths." + rebirthName + ".msg");
			List<String> actions = main.getConfigManager().rebirthsConfig.getStringList("Rebirths." + rebirthName + ".actions");
			List<String> addPermissionList = main.getConfigManager().rebirthsConfig.getStringList("Rebirths." + rebirthName + ".addpermission");
			List<String> delPermissionList = main.getConfigManager().rebirthsConfig.getStringList("Rebirths." + rebirthName + ".delpermission");
			int requiredPrestiges = 0;
			requiredPrestiges = loadInt("Rebirths." + rebirthName + ".required_prestiges");
			RebirthRandomCommands randomCommandsManager = new RebirthRandomCommands(rebirthName, false, true);
			boolean sendFirework = main.getConfigManager().rebirthsConfig.getBoolean("Rebirths." + rebirthName + ".send-firework");
			RebirthDataHandler rbdh = new RebirthDataHandler(rebirthName);
			Map<String, Double> numberRequirements = new LinkedHashMap<>();
			Map<String, String> stringRequirements = new LinkedHashMap<>();
			List<String> customRequirementMessage = Lists.newArrayList();
			if(main.getConfigManager().rebirthsConfig.isSet("Rebirths." + rebirthName + ".requirements")) {
				for(String requirementCondition : main.getConfigManager().rebirthsConfig.getStringList("Rebirths." + rebirthName + ".requirements")) {
					if(requirementCondition.contains("->")) {
						String[] splitter = requirementCondition.split("->");
						String requirement = splitter[0];
						String value = splitter[1];
						stringRequirements.put(requirement, value);
					} else if (requirementCondition.contains(">>")) {
						String[] splitter = requirementCondition.split(">>");
						String requirement = splitter[0];
						double value = Double.valueOf(splitter[1]);
						numberRequirements.put(requirement, value);
					}
				}
			}
			if(main.getConfigManager().rebirthsConfig.isSet("Rebirths." + rebirthName + ".custom-requirement-message")) {
				for(String messageLine : main.getConfigManager().rebirthsConfig.getStringList("Rebirths." + rebirthName + ".custom-requirement-message")) {
					customRequirementMessage.add(gds().parseHexColorCodes(messageLine));
				}
			}
			if(!stringRequirements.isEmpty()) {
				rbdh.setStringRequirements(stringRequirements);
			}
			if(!numberRequirements.isEmpty()) {
				rbdh.setNumberRequirements(numberRequirements);
			}
			if(!customRequirementMessage.isEmpty()) {
				rbdh.setCustomRequirementMessage(customRequirementMessage);
			}
			rbdh.setName(rebirthName);
			rbdh.setDisplayName(gds().parseHexColorCodes(rebirthDisplayName));
			rbdh.setCost(rebirthCost);
			rbdh.setNextRebirthName(nextRebirthName);
			rbdh.setPrestigeCostIncreasePercentage(prestigeIncrease);
			rbdh.setRebirthCommands(gds().parseHexColorCodes(rebirthCommands));
			rbdh.setActionbarMessages(gds().parseHexColorCodes(actionbarMessages));
			rbdh.setActionbarInterval(actionbarInterval);
			rbdh.setBroadcastMessages(gds().parseHexColorCodes(broadcastMessages));
			rbdh.setMsg(gds().parseHexColorCodes(messages));
			rbdh.setActions(gds().parseHexColorCodes(actions));
			rbdh.setAddPermissionList(addPermissionList);
			rbdh.setDelPermissionList(delPermissionList);
			rbdh.setRandomCommandsManager(randomCommandsManager);
			rbdh.setFireworkDataHandler(main.getFireworkManager().readFromConfig(LevelType.REBIRTH, rebirthName, null));
			rbdh.setSendFirework(sendFirework);
			rbdh.setRequiredPrestiges(requiredPrestiges);
			getRebirthData().put(rebirthName, rbdh);
		}
	}

	public double loadDouble(String node) {
		if(!main.getConfigManager().rebirthsConfig.isSet(node)) {
			return 0.0;
		}
		return main.getConfigManager().rebirthsConfig.getDouble(node, 0.0);
	}

	public int loadInt(String node) {
		if(!main.getConfigManager().rebirthsConfig.isSet(node) || !main.getConfigManager().rebirthsConfig.isInt(node)) {
			return 0;
		}
		return main.getConfigManager().rebirthsConfig.getInt(node, 0);
	}

	public void loadRebirthData(String rebirthName) {
		String nextRebirthName = main.getConfigManager().rebirthsConfig.getString("Rebirths." + rebirthName + ".nextrebirth");
		String rebirthDisplayName = main.getConfigManager().rebirthsConfig.getString("Rebirths." + rebirthName + ".display");
		Double rebirthCost = main.getConfigManager().rebirthsConfig.getDouble("Rebirths." + rebirthName + ".cost", 0.0);
		Double nextRebirthCost = main.getConfigManager().rebirthsConfig.getDouble("Rebirths." + nextRebirthName + ".cost", 0.0);
		String nextRebirthDisplayName = main.getConfigManager().rebirthsConfig.getString("Rebirths." + nextRebirthName + ".display");
		List<String> rebirthCommands = main.getConfigManager().rebirthsConfig.getStringList("Rebirths." + rebirthName + ".executecmds");
		List<String> actionbarMessages = main.getConfigManager().rebirthsConfig.getStringList("Rebirths." + rebirthName + ".actionbar.text");
		int actionbarInterval = main.getConfigManager().rebirthsConfig.getInt("Rebirths." + rebirthName + ".actionbar.interval");
		List<String> broadcastMessages = main.getConfigManager().rebirthsConfig.getStringList("Rebirths." + rebirthName + ".broadcast");
		List<String> messages = main.getConfigManager().rebirthsConfig.getStringList("Rebirths." + rebirthName + ".text");
		List<String> actions = main.getConfigManager().rebirthsConfig.getStringList("Rebirths." + rebirthName + ".actions");
		List<String> addPermissionList = main.getConfigManager().rebirthsConfig.getStringList("Rebirths." + rebirthName + ".addpermission");
		List<String> delPermissionList = main.getConfigManager().rebirthsConfig.getStringList("Rebirths." + rebirthName + ".delpermission");
		RebirthRandomCommands randomCommandsManager = new RebirthRandomCommands(rebirthName, true);
		boolean sendFirework = main.getConfigManager().rebirthsConfig.getBoolean("Rebirths." + rebirthName + ".send-firework");
		RebirthDataHandler rbdh = new RebirthDataHandler(rebirthName);
		rbdh.setName(rebirthName);
		rbdh.setDisplayName(rebirthDisplayName);
		rbdh.setCost(rebirthCost);
		rbdh.setNextRebirthName(nextRebirthName);
		rbdh.setNextRebirthCost(nextRebirthCost);
		rbdh.setNextRebirthDisplayName(nextRebirthDisplayName);
		rbdh.setRebirthCommands(rebirthCommands);
		rbdh.setActionbarMessages(actionbarMessages);
		rbdh.setActionbarInterval(actionbarInterval);
		rbdh.setBroadcastMessages(broadcastMessages);
		rbdh.setMsg(messages);
		rbdh.setActions(actions);
		rbdh.setAddPermissionList(addPermissionList);
		rbdh.setDelPermissionList(delPermissionList);
		rbdh.setRandomCommandsManager(randomCommandsManager);
		rbdh.setFireworkDataHandler(main.getFireworkManager().readFromConfig(LevelType.REBIRTH, rebirthName, null));
		rbdh.setSendFirework(sendFirework);
		getRebirthData().put(rebirthName, rbdh);
	}

	/**
	 * 
	 * @return rebirths collection
	 */
	public List<String> getRebirthsCollection() {
		return new LinkedList<>(this.rebirthData.keySet());
	}

	public String getNextRebirthName(String rebirthName) {
		return rebirthData.get(rebirthName).getNextRebirthName();
	}

	public double getCost(String rebirthName) {
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

	public FireworkDataHandler getFireworkDataHandler(String rebirthName) {
		return rebirthData.get(rebirthName).getFireworkDataHandler();
	}

	public boolean isSendFirework(String rebirthName) {
		return rebirthData.get(rebirthName).isSendFirework();
	}

	public double getPrestigeCostIncreasePercentage(String rebirthName) {
		return rebirthData.get(rebirthName).getPrestigeCostIncreasePercentage();
	}

	public int getRequiredPrestiges(String rebirthName) {
		return rebirthData.get(rebirthName).getRequiredPrestiges();
	}

	public void setData(String node, Object value) {
		if(value == null || node.contains("LASTPRESTIGE")) 	return;
		if(value instanceof Integer) {
			if((int)value == 0) return;
		}
		if(value instanceof Boolean) {
			if((boolean)value == false) return;
		}
		if(value instanceof List) {
			if(((List<?>)value).isEmpty()) return;
		}
		if(value instanceof Set) {
			if(((Set<?>)value).isEmpty()) return;
		}
		if(value instanceof Map) {
			if(((Map<?, ?>)value).isEmpty()) return;
		}
		main.getConfigManager().prestigesConfig.set(node, value);
	}

	/**
	 * 
	 * Use when you want to update a Rebirth option then
	 * use the method loadRebirthData(String rebirthName) to load the data in game
	 */
	public void saveRebirthData(String rebirthName) {
		RebirthDataHandler handler = rebirthData.get(rebirthName);
		setData("Rebirths." + rebirthName + ".nextrebirth", handler.getNextRebirthName());
		setData("Rebirths." + rebirthName + ".cost", handler.getCost());
		setData("Rebirths." + rebirthName + ".display", handler.getDisplayName());
	}
	
	/**
	 * Should only be used onDisable()
	 */
	public void saveRebirthsData() {
		if(!main.isRebirthEnabled) return;
		for(Entry<String, RebirthDataHandler> rebirth : rebirthData.entrySet()) {
			String key = rebirth.getKey();
			RebirthDataHandler value = rebirth.getValue();
			setData("Rebirths." + key + ".nextrebirth", value.getNextRebirthName());
			setData("Rebirths." + key + ".cost", value.getCost());
			setData("Rebirths." + key + ".executecmds", value.getRebirthCommands());
			setData("Rebirths." + key + ".actionbar.interval", value.getActionbarInterval());
			setData("Rebirths." + key + ".actionbar.text", value.getActionbarMessages());
			setData("Rebirths." + key + ".broadcast", value.getBroadcast());
			setData("Rebirths." + key + ".msg", value.getMsg());
			setData("Rebirths." + key + ".actions", value.getActions());
			setData("Rebirths." + key + ".addpermission", value.getAddPermissionList());
			setData("Rebirths." + key + ".delpermission", value.getDelPermissionList());
			setData("Rebirths." + key + ".prestige_cost_increase_percentage", value.getPrestigeCostIncreasePercentage());
			setData("Rebirths." + key + ".required_prestiges", value.getRequiredPrestiges());
			setData("Rebirths." + key + ".send-firework", value.isSendFirework());
		}
	}
	
}
