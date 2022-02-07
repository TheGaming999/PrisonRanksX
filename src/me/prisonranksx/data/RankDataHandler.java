package me.prisonranksx.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class RankDataHandler {

	private String rankName;
	private String rankDisplayName;
	private Double rankCost;
	private String rankupName;
	private String rankupDisplayName;
	private Double rankupCost;
	private boolean allowPrestige;
	private List<String> rankupCommands;
	private List<String> rankCommands; 
	private List<String> actionbarMessages;
	private int actionbarInterval;
	private List<String> actions;
	private List<String> broadcastMessages;
	private List<String> messages;
	private List<String> addPermissionList;
	private List<String> currentAddPermissionList;
	private List<String> worldAddPermissionList;
	private List<String> serverAddPermissionList;
	private List<String> delPermissionList;
	private List<String> currentDelPermissionList;
	private List<String> worldDelPermissionList;
	private List<String> serverDelPermissionList;
	private boolean sendFirework;
	private RankRandomCommands randomCommandsManager;
	private FireworkDataHandler fireworkDataHandler;
	private String pathName;
	private @Nullable Map<String, Double> numberRequirements;
	private @Nullable Map<String, String> stringRequirements;
	private List<String> customRequirementMessage;
	
	/**
	 * Never touch this, everything will go wrong if you did. that is applied to all data handlers <!>
	 * @param rankName
	 */
	public RankDataHandler(String rankName, String pathName) {this.rankName = rankName; this.pathName = pathName;}
	
	public String readImportantValues() {
		List<String> noneList = new ArrayList<String>();
		noneList.add("none");
		String name = this.rankName != null ? this.rankName : "none";
		String displayName = this.rankDisplayName != null ? this.rankDisplayName : "none";
		double rankCost = this.rankCost != 0.0 ? this.rankCost : -1;
		String rankupName = this.rankupName != null ? this.rankupName : "none";
		String rankupDisplayName = this.rankupDisplayName != null ? this.rankupDisplayName : "none";
		double rankupCost = this.rankupCost != 0.0 ? this.rankupCost : -1;
		boolean allowPrestige = this.allowPrestige;
		List<String> rankupCommands = this.rankupCommands != null ? this.rankupCommands : noneList;
		List<String> actionbarMessages = this.actionbarMessages != null ? this.actionbarMessages : noneList;
		int actionbarInterval = this.actionbarInterval != 0 ? this.actionbarInterval : -1;
		return name + "(DisplayName)->" + displayName + "(RankCost)->" + String.valueOf(rankCost) + "(RankupName)->" + rankupName
				+ "(RankupDisplayName)->" + rankupDisplayName + "(RankupCost)->" + String.valueOf(rankupCost) + "(AllowPrestige)->" + String.valueOf(allowPrestige)
				+ "(RankupCommands)->" + rankupCommands.toString() + "(ActionbarMessages)->" + actionbarMessages.toString() + "(ActionbarInterval)->" + String.valueOf(actionbarInterval);
	}
	
	public String getName() {
		return rankName;
	}
	
	public void setName(String rankName) {
		this.rankName = rankName;
	}
	
	public Double getCost() {
		return rankCost;
	}
	
	public void setCost(Double rankCost) {
		this.rankCost = rankCost;
	}
	
	public String getDisplayName() {
		return rankDisplayName;
	}
	
	public void setDisplayName(String rankDisplayName) {
		this.rankDisplayName = rankDisplayName;
	}
	
	public String getRankupName() {
		return rankupName;
	}
	
	public void setRankupName(String rankupName) {
		this.rankupName = rankupName;
	}
	
	public String getRankupDisplayName() {
		return rankupDisplayName;
	}
	
	public void setRankupDisplayName(String rankupDisplayName) {
		this.rankupDisplayName = rankupDisplayName;
	}
	
	public Double getRankupCost() {
		return rankupCost;
	}
	
	public void setRankupCost(Double rankupCost) {
		this.rankupCost = rankupCost;
	}
	
	public boolean isAllowPrestige() {
		return allowPrestige;
	}
	
	public void setAllowPrestige(boolean allowPrestige) {
		if(allowPrestige) {
		this.allowPrestige = allowPrestige;
		}
	}
	
	public List<String> getRankupCommands() {
		return rankupCommands;
	}
	
	public void setRankupCommands(List<String> rankupCommands) {
		if(rankupCommands != null && !rankupCommands.isEmpty()) {
		this.rankupCommands = rankupCommands;
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
	
	public RankRandomCommands getRandomCommandsManager() {
		return randomCommandsManager;
	}
	
	public void setRandomCommandsManager(RankRandomCommands randomCommandsSet) {
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
	
	public String getPathName() {
		return pathName;
	}
	
	public void setPathName(String pathName) {
		this.pathName = pathName;
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

	public List<String> getRankCommands() {
		return rankCommands;
	}

	public void setRankCommands(List<String> rankCommands) {
		this.rankCommands = rankCommands;
	}

	public List<String> getCurrentAddPermissionList() {
		return currentAddPermissionList;
	}

	public void setCurrentAddPermissionList(List<String> currentAddPermissionList) {
		this.currentAddPermissionList = currentAddPermissionList;
	}

	public List<String> getCurrentDelPermissionList() {
		return currentDelPermissionList;
	}

	public void setCurrentDelPermissionList(List<String> currentDelPermissionList) {
		this.currentDelPermissionList = currentDelPermissionList;
	}

	public FireworkDataHandler getFireworkDataHandler() {
		return fireworkDataHandler;
	}

	public void setFireworkDataHandler(FireworkDataHandler fireworkDataHandler) {
		this.fireworkDataHandler = fireworkDataHandler;
	}

	public List<String> getWorldAddPermissionList() {
		return worldAddPermissionList;
	}

	public void setWorldAddPermissionList(List<String> worldAddPermissionList) {
		this.worldAddPermissionList = worldAddPermissionList;
	}

	public List<String> getServerAddPermissionList() {
		return serverAddPermissionList;
	}

	public void setServerAddPermissionList(List<String> serverAddPermissionList) {
		this.serverAddPermissionList = serverAddPermissionList;
	}

	public List<String> getWorldDelPermissionList() {
		return worldDelPermissionList;
	}

	public void setWorldDelPermissionList(List<String> worldDelPermissionList) {
		this.worldDelPermissionList = worldDelPermissionList;
	}

	public List<String> getServerDelPermissionList() {
		return serverDelPermissionList;
	}

	public void setServerDelPermissionList(List<String> serverDelPermissionList) {
		this.serverDelPermissionList = serverDelPermissionList;
	}
	
}
