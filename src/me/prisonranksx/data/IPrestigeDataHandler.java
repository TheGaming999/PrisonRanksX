package me.prisonranksx.data;

import java.util.List;
import java.util.Map;

public interface IPrestigeDataHandler {

	public String getValues();
	public String getName();
	public void setName(String name);
	public double getCost();
	public void setCost(double cost);
	public String getDisplayName();
	public void setDisplayName(String displayName);
	public String getNextPrestigeName();
	public void setNextPrestigeName(String nextPrestigeName);
	public String getNextPrestigeDisplayName();
	public void setNextPrestigeDisplayName(String nextPrestigeDisplayName);
	public double getNextPrestigeCost();
	public void setNextPrestigeCost(double nextPrestigeCost);
	public double getRankupCostIncreasePercentage();
	public void setRankupCostIncreasePercentage(double rankupCostIncreasePercentage);
	public List<String> getPrestigeCommands();
	public void setPrestigeCommands(List<String> prestigeCommands);
	public List<String> getActionbarMessages();
	public void setActionbarMessages(List<String> actionbarMessages);
	public int getActionbarInterval();
	public void setActionbarInterval(int actionbarInterval);
	public List<String> getBroadcast();
	public void setBroadcastMessages(List<String> broadcastMessages);
	public List<String> getMsg();
	public void setMsg(List<String> messages);
	public List<String> getActions();
	public void setActions(List<String> actions);
	public List<String> getAddPermissionList();
	public void setAddPermissionList(List<String> addPermissionList);
	public List<String> getDelPermissionList();
	public void setDelPermissionList(List<String> delPermissionList);
	public PrestigeRandomCommands getRandomCommandsManager();
	public void setRandomCommandsManager(PrestigeRandomCommands prestigeRandomCommands);
	public FireworkManager getFireworkManager();
	public void setFireworkManager(FireworkManager fireworkManager);
	public boolean getSendFirework();
	public void setSendFirework(boolean sendFirework);
	public Map<String, Double> getNumberRequirements();
	public void setNumberRequirements(Map<String, Double> numberRequirements);
	public Map<String, String> getStringRequirements();
	public void setStringRequirements(Map<String, String> stringRequirements);
	public List<String> getCustomRequirementMessage();
	public void setCustomRequirementMessage(List<String> customRequirementMessage);
	
}
