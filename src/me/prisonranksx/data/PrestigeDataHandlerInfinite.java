package me.prisonranksx.data;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.prisonranksx.PrisonRanksX;

public class PrestigeDataHandlerInfinite implements IPrestigeDataHandler {
	
	private String prestigeName;
	private String prestigeDisplayName;
	private double prestigeCost;
	private String nextPrestigeName;
	private String nextPrestigeDisplayName;
	private double nextPrestigeCost;
	private double rankupCostIncreasePercentage;
	private List<String> prestigeCommands;
	private List<String> actionbarMessages;
	private int actionbarInterval = 0;
	private List<String> actions;
	private List<String> broadcastMessages;
	private List<String> messages;
	private List<String> addPermissionList;
	private List<String> delPermissionList;
	private Boolean sendFirework;
	private PrestigeRandomCommands randomCommandsManager;
	private FireworkManager fireworkManager;
	private Map<String, Double> numberRequirements;
	private Map<String, String> stringRequirements;
	private List<String> customRequirementMessage;
	private PrisonRanksX instance;
	private InfinitePrestigeSettings ips;
	private Map<Long, InfinitePrestigeSettings> cps;
	
	public PrestigeDataHandlerInfinite(String prestigeName) {this.prestigeName = prestigeName;
	this.sendFirework = false;
	this.instance = PrisonRanksX.getInstance();
	this.ips = instance.infinitePrestigeSettings;
	this.cps = instance.infinitePrestigeSettings.getConstantPrestigeSettings();}
	
	public String getValues() {
		String x = "[]";
		try {
		x = "[@prestige.name: " + prestigeName + ", " + "@prestige.display.name: " + prestigeDisplayName + ", " +
		"@prestige.cost: " + String.valueOf(prestigeCost) + ", " + "@next.prestige.name: " + nextPrestigeName + ", " +
				"@next.prestige.display.name: " + nextPrestigeDisplayName + ", " + "@next.prestige.cost: " + 
		String.valueOf(nextPrestigeCost) + ", " + "@rankup.cost.increase.percentage: " + 
				String.valueOf(rankupCostIncreasePercentage) + ", " + "@prestige.commands: " + prestigeCommands.toString() +
				", " + "@actionbar.messages: " + actionbarMessages.toString() + ", " + "@actionbar.interval: " + String.valueOf(actionbarInterval) +
				", " + "@actions: " + actions.toString() + ", " + "@broadcast.messages: " + broadcastMessages.toString() +
				", " + "@messages: " + messages.toString() + ", " + "@add.permission.list: " + addPermissionList.toString() +
				", " + "@del.permission.list: " + delPermissionList.toString() + ", " + "@send.firework: " + sendFirework.toString() +
				", " + "@random.commands.manager: %OOP_OBJECT%" + ", @firework.manager: %OOP_OBJECT%]";
		} catch (NullPointerException err) {
			x = "[NullPointerException] %String% getValues()";
		}
		return x;
	}
	
	public String getName() {
		return prestigeName;
	}
	
	public PrestigeDataHandlerInfinite setName(String prestigeName) {
		this.prestigeName = prestigeName;
		return this;
	}
	
	public double getCost() {
			return instance.prxAPI.numberAPI.calculate(ips.getCostExpression()
					.replace("{number}", getName()));
	}
	
	public void setCost(double prestigeCost) {
		this.prestigeCost = prestigeCost;
	}
	
	public String getDisplayName() {
		String display = "";
		display = ips.getDisplay().replace("{number}", getName());
		if(!cps.isEmpty()) {
		   	for(Entry<Long, InfinitePrestigeSettings> cons : cps.entrySet()) {
		   		long prestigeNumber = Long.valueOf(prestigeName);
		   		InfinitePrestigeSettings ipsc = cons.getValue();
		   		if(prestigeNumber >= cons.getKey() && prestigeNumber < ipsc.getFinalPrestige()) {
		   			display = ipsc.getDisplay().replace("{number}", getName());
		   			break;
		   		}
		   	}
		}	
		return display;
	}
	
	public void setDisplayName(String prestigeDisplayName) {
		this.prestigeDisplayName = prestigeDisplayName;
	}
	
	public String getNextPrestigeName() {
		return String.valueOf(Integer.parseInt(getName())+1);
	}
	
	public void setNextPrestigeName(String nextPrestigeName) {
		this.nextPrestigeName = nextPrestigeName;
	}
	
	public String getNextPrestigeDisplayName() {
		return ips.getDisplay().replace("{number}", getNextPrestigeName());
	}
	
	public void setNextPrestigeDisplayName(String nextPrestigeDisplayName) {
		this.nextPrestigeDisplayName = nextPrestigeDisplayName;
	}
	
	public double getNextPrestigeCost() {
		return instance.prxAPI.numberAPI.calculate(ips.getCostExpression()
				.replace("{number}", getNextPrestigeName()));
	}
	
	public void setNextPrestigeCost(double nextPrestigeCost) {
		this.nextPrestigeCost = nextPrestigeCost;
	}
	
	public double getRankupCostIncreasePercentage() {
		return 100;
	}
	
	public void setRankupCostIncreasePercentage(double rankupCostIncreasePercentage) {
		this.rankupCostIncreasePercentage = rankupCostIncreasePercentage;
	}
	
	public List<String> getPrestigeCommands() {
		return prestigeCommands;
	}
	
	public void setPrestigeCommands(List<String> nextPrestigeCommands) {
		if(nextPrestigeCommands != null && !nextPrestigeCommands.isEmpty()) {
		this.prestigeCommands = nextPrestigeCommands;
        }
	}
	
	public List<String> getActionbarMessages() {
		return actionbarMessages;
	}
	
	public void setActionbarMessages(List<String> actionbarMessages) {
		if(actionbarMessages != null && !actionbarMessages.isEmpty()) {
		this.actionbarMessages = actionbarMessages;
		}
	}
	
	public int getActionbarInterval() {
		return actionbarInterval;
	}
	
	public void setActionbarInterval(int actionbarInterval) {
		if(actionbarMessages != null && actionbarInterval != 0 && !actionbarMessages.isEmpty()) {
		this.actionbarInterval = actionbarInterval;
		}
	}
	
	public List<String> getBroadcast() {
		return broadcastMessages;
	}
	
	public void setBroadcastMessages(List<String> broadcastMessages) {
		if(broadcastMessages != null && !broadcastMessages.isEmpty()) {
		this.broadcastMessages = broadcastMessages;
		}
	}
	
	public List<String> getMsg() {
		return messages;
	}
	
	public void setMsg(List<String> messages) {
		if(messages != null && !messages.isEmpty()) {
		this.messages = messages;
		}
	}
	
	public List<String> getActions() {
		return actions;
	}
	
	public void setActions(List<String> actions) {
		if(actions != null && !actions.isEmpty()) {
		this.actions = actions;
		}
	}
	
	public List<String> getAddPermissionList() {
		return addPermissionList;
	}
	
	public void setAddPermissionList(List<String> addPermissionList) {
		if(addPermissionList != null && !addPermissionList.isEmpty()) {
		this.addPermissionList = addPermissionList;
		}
	}
	
	public List<String> getDelPermissionList() {
		return delPermissionList;
	}
	
	public void setDelPermissionList(List<String> delPermissionList) {
		if(delPermissionList != null && !delPermissionList.isEmpty()) {
		this.delPermissionList = delPermissionList;
		}
	}
	
	public PrestigeRandomCommands getRandomCommandsManager() {
		return randomCommandsManager;
	}
	
	public void setRandomCommandsManager(PrestigeRandomCommands randomCommandsSet) {
		if(randomCommandsSet != null && randomCommandsSet.getRandomCommandsMap() != null
				&& !randomCommandsSet.getRandomCommandsMap().isEmpty()) {
		this.randomCommandsManager = randomCommandsSet;
		}
	}
	
	public FireworkManager getFireworkManager() {
		return fireworkManager;
	}
	
	public void setFireworkManager(FireworkManager fireworkManager) {
		if(fireworkManager != null) {
			if(fireworkManager.getFireworkBuilder() == null) {
				return;
			}
			if(fireworkManager.getFireworkBuilder().isEmpty()) {
				return;
			}
		this.fireworkManager = fireworkManager;
		}
	}
	
	public boolean getSendFirework() {
		return sendFirework;
	}
	
	public void setSendFirework(boolean sendFirework) {
		if(sendFirework) {
		this.sendFirework = sendFirework;
		}
	}

	public Map<String, Double> getNumberRequirements() {
		return numberRequirements;
	}

	public void setNumberRequirements(Map<String, Double> numberRequirements) {
		this.numberRequirements = numberRequirements;
	}

	public Map<String, String> getStringRequirements() {
		return stringRequirements;
	}

	public void setStringRequirements(Map<String, String> stringRequirements) {
		this.stringRequirements = stringRequirements;
	}

	public List<String> getCustomRequirementMessage() {
		return customRequirementMessage;
	}

	public void setCustomRequirementMessage(List<String> customRequirementMessage) {
		this.customRequirementMessage = customRequirementMessage;
	}
	
	/**
	 * @return prestige data as string
	 */
	@Override
	public String toString() {
		return getValues();
	}

	@Override
	public IPrestigeDataHandler getHandler(String prestigeName) {
		return this;
	}
	
}
