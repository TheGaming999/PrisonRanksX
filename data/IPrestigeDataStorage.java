package me.prisonranksx.data;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IPrestigeDataStorage {

	public void putData(String name, IPrestigeDataHandler prestigeDataHandler);
    public IPrestigeDataHandler getDataHandler(String name);
    public IPrestigeDataHandler getHandler(String prestigeName);
    public GlobalDataStorage gds();
    public void loadPrestigesData();
    public String loadString(String node);
    public List<String> loadStringList(String node);
    public int loadInt(String node);
    public boolean loadBoolean(String node);
	public double loadDouble(String node);
    public void loadPrestigeData(String prestigeName);
    public void initPrestigeData();
    public Map<String, IPrestigeDataHandler> getPrestigeData();
    public List<String> getPrestigesCollection();
    public Set<String> getOriginalPrestigesCollection();
    public List<String> getNativeLinkedPrestigesCollection();
    public void addToNativeLinkedList(String name);
    public String getNextPrestigeName(String prestigeName);
    public double getCost(String prestigeName);
    public String getDisplayName(String prestigeName);
    public double getRankupCostIncreasePercentage(String prestigeName);
    public double getNextPrestigeCost(String prestigeName);
    public String getNextPrestigeDisplayName(String prestigeName);
    public List<String> getPrestigeCommands(String prestigeName);
    public int getActionbarInterval(String prestigeName);
    public List<String> getActionbarMessages(String prestigeName);
    public List<String> getBroadcast(String prestigeName);
    public List<String> getMsg(String prestigeName);
    public List<String> getActions(String prestigeName);
    public List<String> getAddPermissionList(String prestigeName);
    public List<String> getDelPermissionList(String prestigeName);
    public PrestigeRandomCommands getRandomCommandsManager(String prestigeName);
    public FireworkDataHandler getFireworkDataHandler(String prestigeName);
    public boolean isSendFirework(String prestigeName);
    public String getValues(String prestigeName);
    public void savePrestigeData(String prestigeName);
    public void setData(String node, Object value);
    public void savePrestigesData();
	
}
