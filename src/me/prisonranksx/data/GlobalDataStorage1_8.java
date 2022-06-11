package me.prisonranksx.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.prisonranksx.PrisonRanksX;

public class GlobalDataStorage1_8 implements GlobalDataStorage {

	private Map<String, String> stringData;
	private Map<String, Integer> integerData;
	private Map<String, Double> doubleData;
	private Map<String, Boolean> booleanData;
	private Map<String, List<String>> stringListData;
	private Map<String, Set<String>> stringSetData;
	private Map<String, Map<String, Object>> mapData;
	private Map<String, Object> globalData;
	private PrisonRanksX main;
	public boolean isRankEnabled;
	public String rankupProgressStyle;

	public GlobalDataStorage1_8(PrisonRanksX main) {
		this.main = main;
		this.stringData = new HashMap<String, String>();
		this.integerData = new HashMap<String, Integer>();
		this.doubleData = new HashMap<String, Double>();
		this.booleanData = new HashMap<String, Boolean>();
		this.stringListData = new HashMap<String, List<String>>();
		this.stringSetData = new HashMap<String, Set<String>>();
		this.mapData = new HashMap<String, Map<String, Object>>();
		this.globalData = new HashMap<String, Object>();
	}

	public Map<String, String> getStringMap() {
		return this.stringData;
	}

	public Map<String, Integer> getIntegerMap() {
		return this.integerData;
	}

	public Map<String, Double> getDoubleMap() {
		return this.doubleData;
	}

	public Map<String, Boolean> getBooleanMap() {
		return this.booleanData;
	}

	public Map<String, List<String>> getStringListMap() {
		return this.stringListData;
	}

	public Map<String, Set<String>> getStringSetMap() {
		return this.stringSetData;
	}

	public Map<String, Map<String, Object>> getMap() {
		return this.mapData;
	}

	public Map<String, Object> getGlobalMap() {
		return this.globalData;
	}
	/**
	 * 
	 * @param configNode
	 * @return loaded string
	 */
	public String registerStringData(String configNode) {
		getStringMap().put(configNode, main.getConfig().getString(configNode));
		getGlobalMap().put(configNode, main.getConfig().getString(configNode));
		return main.getConfig().getString(configNode);
	}

	/**
	 * 
	 * @param configNode
	 * @return loaded integer
	 */
	public Integer registerIntegerData(String configNode) {
		getIntegerMap().put(configNode, main.getConfig().getInt(configNode));
		getGlobalMap().put(configNode, main.getConfig().getInt(configNode));
		return main.getConfig().getInt(configNode);
	}

	/**
	 * 
	 * @param configNode
	 * @return loaded double
	 */
	public double registerDoubleData(String configNode) {
		getDoubleMap().put(configNode, main.getConfig().getDouble(configNode));
		getGlobalMap().put(configNode, main.getConfig().getDouble(configNode));
		return main.getConfig().getDouble(configNode);
	}

	/**
	 * 
	 * @param configNode
	 * @return loaded boolean
	 */
	public boolean registerBooleanData(String configNode) {
		getBooleanMap().put(configNode, main.getConfig().getBoolean(configNode));
		getGlobalMap().put(configNode, main.getConfig().getBoolean(configNode));
		return main.getConfig().getBoolean(configNode);
	}

	/**
	 * 
	 * @param configNode
	 * @return loaded string list
	 */
	public List<String> registerStringListData(String configNode) {
		getStringListMap().put(configNode, main.getConfig().getStringList(configNode));
		getGlobalMap().put(configNode, main.getConfig().getStringList(configNode));
		return main.getConfig().getStringList(configNode);
	}

	/**
	 * 
	 * @param configNode
	 * @return loaded string set
	 */
	public Set<String> registerStringSetData(String configNode) {
		getStringSetMap().put(configNode, main.getConfig().getConfigurationSection(configNode).getKeys(false));
		getGlobalMap().put(configNode, main.getConfig().getConfigurationSection(configNode).getKeys(false));
		return main.getConfig().getConfigurationSection(configNode).getKeys(false);
	}

	/**
	 * 
	 * @param configNode
	 * @param withKeys
	 * @return loaded string set
	 */
	public Set<String> registerStringSetData(String configNode, boolean withKeys) {
		getStringSetMap().put(configNode, main.getConfig().getConfigurationSection(configNode).getKeys(withKeys));
		getGlobalMap().put(configNode, main.getConfig().getConfigurationSection(configNode).getKeys(withKeys));
		return main.getConfig().getConfigurationSection(configNode).getKeys(withKeys);
	}

	public Map<String, Object> registerMapData(String configNode, boolean withKeys) {
		if(main.getConfig().getConfigurationSection(configNode) != null) {
			getMap().put(configNode, main.getConfig().getConfigurationSection(configNode).getValues(withKeys));
			getGlobalMap().put(configNode, main.getConfig().getConfigurationSection(configNode).getValues(withKeys));
			return main.getConfig().getConfigurationSection(configNode).getValues(withKeys);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param configNode
	 * @return loaded object
	 */
	public Object registerData(String configNode) {
		getGlobalMap().put(configNode, main.getConfig().get(configNode));
		return main.getConfig().get(configNode);
	}

	public String getStringData(String configNode) {
		return getStringMap().get(configNode);
	}

	public int getIntegerData(String configNode) {
		return getIntegerMap().get(configNode);
	}

	public double getDoubleData(String configNode) {
		return getDoubleMap().get(configNode);
	}

	public boolean getBooleanData(String configNode) {
		return getBooleanMap().get(configNode);
	}

	public List<String> getStringListData(String configNode) {
		return getStringListMap().get(configNode);
	}

	public Set<String> getStringSetData(String configNode) {
		return getStringSetMap().get(configNode);
	}

	public Object getData(String configNode) {
		return getGlobalMap().get(configNode);
	}

	@Override
	public String translateHexColorCodes(String message) {
		return message;
	}

	@Override
	public List<String> translateHexColorCodes(List<String> message) {
		return message;
	}

	@Override
	public String parseHexColorCodes(String message) {
		// TODO Auto-generated method stub
		return message;
	}

	@Override
	public List<String> parseHexColorCodes(List<String> message) {
		// TODO Auto-generated method stub
		return message;
	}

}
