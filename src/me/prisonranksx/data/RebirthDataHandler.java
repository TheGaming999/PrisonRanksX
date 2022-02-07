package me.prisonranksx.data;

import java.util.List;
import java.util.Map;

public class RebirthDataHandler {
  
	private String rebirthName;
	private String rebirthDisplayName;
	private Double rebirthCost;
	private String nextRebirthName;
	private String nextRebirthDisplayName;
	private Double nextRebirthCost;
	private Double prestigeCostIncreasePercentage;
	private List<String> rebirthCommands;
	private List<String> actionbarMessages;
	private int actionbarInterval;
	private List<String> actions;
	private List<String> broadcastMessages;
	private List<String> messages;
	private List<String> addPermissionList;
	private List<String> delPermissionList;
	private boolean sendFirework;
	private RebirthRandomCommands randomCommandsManager;
	private FireworkDataHandler fireworkDataHandler;
	private int requiredPrestiges;
	private Map<String, Double> numberRequirements;
	private Map<String, String> stringRequirements;
	private List<String> customRequirementMessage;
	
	/**
	 * 
	 * @param rebirthName
	 */
	public RebirthDataHandler(String rebirthName) {this.rebirthName = rebirthName;}
	
	public String getName() {
		return rebirthName;
	}
	
	public void setName(String rebirthName) {
		this.rebirthName = rebirthName;
	}
	
	public Double getCost() {
		return rebirthCost;
	}
	
	public void setCost(Double rebirthCost) {
		this.rebirthCost = rebirthCost;
	}
	
	public String getDisplayName() {
		return rebirthDisplayName;
	}
	
	public void setDisplayName(String rebirthDisplayName) {
		this.rebirthDisplayName = rebirthDisplayName;
	}
	
	public String getNextRebirthName() {
		return nextRebirthName;
	}
	
	public void setNextRebirthName(String nextRebirthName) {
		this.nextRebirthName = nextRebirthName;
	}
	
	public String getNextRebirthDisplayName() {
		return nextRebirthDisplayName;
	}
	
	public void setNextRebirthDisplayName(String nextRebirthDisplayName) {
		this.nextRebirthDisplayName = nextRebirthDisplayName;
	}
	
	public Double getNextRebirthCost() {
		return nextRebirthCost;
	}
	
	public void setNextRebirthCost(Double nextRebirthCost) {
		this.nextRebirthCost = nextRebirthCost;
	}
	
	public List<String> getRebirthCommands() {
		return rebirthCommands;
	}
	
	public void setRebirthCommands(List<String> rebirthCommands) {
		if(rebirthCommands != null && !rebirthCommands.isEmpty()) {
		this.rebirthCommands = rebirthCommands;
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
		if(actionbarInterval != 0) {
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
	
	public RebirthRandomCommands getRandomCommandsManager() {
		return randomCommandsManager;
	}
	
	public void setRandomCommandsManager(RebirthRandomCommands randomCommandsSet) {
		if(randomCommandsSet != null && randomCommandsSet.getRandomCommandsMap() != null
				&& !randomCommandsSet.getRandomCommandsMap().isEmpty()) {
		this.randomCommandsManager = randomCommandsSet;
		}
	}
	
	public boolean isSendFirework() {
		return sendFirework;
	}
	
	public void setSendFirework(boolean sendFirework) {
		if(sendFirework) {
		this.sendFirework = sendFirework;
		}
	}
  
	public Double getPrestigeCostIncreasePercentage() {
		return prestigeCostIncreasePercentage;
	}
	
	public void setPrestigeCostIncreasePercentage(Double prestigeCostIncreasePercentage) {
		this.prestigeCostIncreasePercentage = prestigeCostIncreasePercentage;
	}
	
	public int getRequiredPrestiges() {
		return requiredPrestiges;
	}
	
	public void setRequiredPrestiges(int requiredPrestiges) {
		this.requiredPrestiges = requiredPrestiges;
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

	public FireworkDataHandler getFireworkDataHandler() {
		return fireworkDataHandler;
	}

	public void setFireworkDataHandler(FireworkDataHandler fireworkDataHandler) {
		this.fireworkDataHandler = fireworkDataHandler;
	}
	
}
