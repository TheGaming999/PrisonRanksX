package me.prisonranksx.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;


import me.prisonranksx.PrisonRanksX;

public class PrestigeDataStorageInfinite implements IPrestigeDataStorage {
	
	private Map<String, IPrestigeDataHandler> prestigeData;
	private PrisonRanksX main;
	private List<String> prestiges;
	private IPrestigeDataHandler universalHandler;
	
	public PrestigeDataStorageInfinite(PrisonRanksX main) {
		this.main = main;
		this.prestigeData = new LinkedHashMap<String, IPrestigeDataHandler>();
		this.prestiges = new LinkedList<>();
		this.universalHandler = new PrestigeDataHandlerInfinite("NONE");
	}
	
	public void putData(String name, IPrestigeDataHandler prestigeDataHandler) {
		prestigeData.put(name, prestigeDataHandler);
	}
	
	public IPrestigeDataHandler getDataHandler(String name) {
		return prestigeData.get(name);
	}
	
	public GlobalDataStorage gds() {
		return this.main.globalStorage;
	}
	/**
	 * Should only be used onEnable()
	 * can be used as a reload
	 */
	public void loadPrestigesData() {
			for(String prestigeName : main.getConfigManager().infinitePrestigeConfig.getConfigurationSection("Prestiges-Settings").getKeys(false)) {
				List<String> prestigeCommands = main.getConfigManager().infinitePrestigeConfig.getStringList("Prestiges-Settings." + prestigeName + ".executecmds");
				List<String> broadcastMessages = main.getConfigManager().infinitePrestigeConfig.getStringList("Prestiges-Settings." + prestigeName + ".broadcast");
				IPrestigeDataHandler pdh = new PrestigeDataHandlerInfinite(prestigeName);			
				pdh.setName(prestigeName);            
                pdh.setPrestigeCommands(gds().parseHexColorCodes(prestigeCommands));               
                pdh.setBroadcastMessages(gds().parseHexColorCodes(broadcastMessages));
                getPrestigeData().put(prestigeName, pdh);
                if(!prestiges.contains(prestigeName)) {
                prestiges.add(prestigeName);
                }
			}
	}
	
	public void initPrestigeData() {
		this.prestigeData = new LinkedHashMap<String, IPrestigeDataHandler>();
	}
	
	public Map<String, IPrestigeDataHandler> getPrestigeData() {
		return this.prestigeData;
	}
	
	/**
	 * 
	 * @return construct a new array prestiges collection
	 */
	public List<String> getPrestigesCollection() {
		return Arrays.asList(prestigeData.keySet().toArray(new String[0]));
	}
	
	/**
	 * 
	 * @return set prestiges collection taken directly from the map
	 */
	public Set<String> getOriginalPrestigesCollection() {
		return prestigeData.keySet();
	}
	
	/**
	 * 
	 * @return construct a new linked prestiges collection.
	 * @deprecated use getNativeLinkedPrestigesCollection();
	 */
	public List<String> getLinkedPrestigesCollection() {
		return new LinkedList<String>(prestigeData.keySet());
	}
	
	/**
	 * 
	 * @return the cached linked list of prestiges
	 */
	public List<String> getNativeLinkedPrestigesCollection() {
		return prestiges;
	}
	
	public void addToNativeLinkedList(String name) {
		prestiges.add(name);
	}
	
	public IPrestigeDataHandler getHandler(String prestigeName) {
		IPrestigeDataHandler pdhi = getPrestigeData().get(prestigeName);
		return pdhi == null ? universalHandler.setName(prestigeName) : pdhi;
	}
	
	public String getNextPrestigeName(String prestigeName) {
		return getHandler(prestigeName).getNextPrestigeName();
	}
	
	public double getCost(String prestigeName) {
		return getHandler(prestigeName).getCost();
	}
	
	public String getDisplayName(String prestigeName) {
		return getHandler(prestigeName).getDisplayName();
	}
	
	public double getRankupCostIncreasePercentage(String prestigeName) {
		return getHandler(prestigeName).getRankupCostIncreasePercentage();
	}
	
	public double getNextPrestigeCost(String prestigeName) {
		return getHandler(getHandler(prestigeName).getNextPrestigeName()).getCost();
	}
	
	public String getNextPrestigeDisplayName(String prestigeName) {
		return getHandler(getHandler(prestigeName).getNextPrestigeName()).getDisplayName();
	}
	
	public List<String> getPrestigeCommands(String prestigeName) {
		return getHandler(prestigeName).getPrestigeCommands();
	}
	
	public int getActionbarInterval(String prestigeName) {
		return getHandler(prestigeName).getActionbarInterval();
	}
	
	public List<String> getActionbarMessages(String prestigeName) {
		return getHandler(prestigeName).getActionbarMessages();
	}
	
	public List<String> getBroadcast(String prestigeName) {
		return getHandler(prestigeName).getBroadcast();
	}
	
	public List<String> getMsg(String prestigeName) {
		return getHandler(prestigeName).getMsg();
	}
	
	public List<String> getActions(String prestigeName) {
		return getHandler(prestigeName).getActions();
	}
	
	public List<String> getAddPermissionList(String prestigeName) {
		return getHandler(prestigeName).getAddPermissionList();
	}
	
	public List<String> getDelPermissionList(String prestigeName) {
		return getHandler(prestigeName).getDelPermissionList();
	}
	
	public PrestigeRandomCommands getRandomCommandsManager(String prestigeName) {
		return getHandler(prestigeName).getRandomCommandsManager();
	}
	
	public FireworkManager getFireworkManager(String prestigeName) {
		return getHandler(prestigeName).getFireworkManager();
	}
	
	public Map<String, Object> getFireworkBuilder(String prestigeName) {
		return getHandler(prestigeName).getFireworkManager().getFireworkBuilder();
	}
	
	public boolean isSendFirework(String prestigeName) {
		return getHandler(prestigeName).getSendFirework();
	}
	
	public String getValues(String prestigeName) {
		return getHandler(prestigeName).getValues();
	}
	
	/**
	 * 
	 * Use when you want to update a prestige option then
	 * use the method loadRankData(String prestigeName) to load the data in game
	 */
	public void savePrestigeData(String prestigeName) {
			// does nothing
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
	@SuppressWarnings("unchecked")
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
		//main.getConfigManager().prestigesConfig.set(node, value);
	}
	/**
	 * Should only be used onDisable()
	 */
	public void savePrestigesData() {
		if(!main.isPrestigeEnabled) {
			return;
		}
	}

	@Override
	public String loadString(String node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> loadStringList(String node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int loadInt(String node) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean loadBoolean(String node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double loadDouble(String node) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void loadPrestigeData(String prestigeName) {
		// TODO Auto-generated method stub
		
	}
}
