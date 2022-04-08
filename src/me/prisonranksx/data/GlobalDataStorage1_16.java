package me.prisonranksx.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

import me.prisonranksx.PrisonRanksX;

public class GlobalDataStorage1_16 implements GlobalDataStorage {


	private final char COLOR_CHAR = '\u00A7';
	private final char PARSE_COLOR_CHAR = '&';

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
	public static String startTag = "&#";
	public static String endTag = "";
	public static final Pattern HEX_PATTERN = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);

	/**
	 * Thanks to Elementeral a.k.a Sullivan_Bognar
	 */
	@Override
	public String translateHexColorCodes(String message)
	{
		if(message == null || message.isEmpty() || !message.contains("&#")) {
			return message;
		}
		Matcher matcher = HEX_PATTERN.matcher(message);
		StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
		while (matcher.find())
		{
			String group = matcher.group(1);
			matcher.appendReplacement(buffer, COLOR_CHAR + "x"
					+ COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
					+ COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
					+ COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
					);
		}
		return matcher.appendTail(buffer).toString();
	}

	@Deprecated
	public String translateHexColorCodesOld(final String message) {
		Matcher matcher = HEX_PATTERN.matcher(message);
		StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
		while (matcher.find()) {
			String group = matcher.group(1);
			matcher.appendReplacement(buffer, COLOR_CHAR + "x" 
					+ COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1) 
					+ COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
					+ COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
					);
		}
		return matcher.appendTail(buffer).toString();
	}

	@Override
	public List<String> translateHexColorCodes(final List<String> message) {
		if(message == null || message.isEmpty()) {
			return message;
		}
		List<String> newList = Lists.newArrayList();
		message.forEach(line -> {
			newList.add(translateHexColorCodes(line));
		});
		return newList;
	}

	/**
	 * If you want to register your config thing just use one of register#### methods onEnable of your plugin
	 */
	public GlobalDataStorage1_16(PrisonRanksX main) {
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
	@Override
	public String registerStringData(String configNode) {
		getStringMap().put(configNode, this.translateHexColorCodes(main.getConfig().getString(configNode)));
		getGlobalMap().put(configNode, this.translateHexColorCodes(main.getConfig().getString(configNode)));
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
	@Override
	public List<String> registerStringListData(String configNode) {
		getStringListMap().put(configNode, this.translateHexColorCodes(main.getConfig().getStringList(configNode)));
		getGlobalMap().put(configNode, this.translateHexColorCodes(main.getConfig().getStringList(configNode)));
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
	public String parseHexColorCodes(String message) {
		if(message == null || message.isEmpty() || !message.contains("&#")) {
			return message;
		}
		Matcher matcher = HEX_PATTERN.matcher(message);
		StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
		while (matcher.find())
		{
			String group = matcher.group(1);
			matcher.appendReplacement(buffer, PARSE_COLOR_CHAR + "x"
					+ PARSE_COLOR_CHAR + group.charAt(0) + PARSE_COLOR_CHAR + group.charAt(1)
					+ PARSE_COLOR_CHAR + group.charAt(2) + PARSE_COLOR_CHAR + group.charAt(3)
					+ PARSE_COLOR_CHAR + group.charAt(4) + PARSE_COLOR_CHAR + group.charAt(5)
					);
		}
		return matcher.appendTail(buffer).toString();
	}

	@Override
	public List<String> parseHexColorCodes(List<String> message) {
		if(message == null || message.isEmpty()) {
			return message;
		}
		List<String> newList = Lists.newArrayList();
		message.forEach(line -> {
			newList.add(parseHexColorCodes(line));
		});
		return newList;
	}

	public char getParseColorChar() {
		return PARSE_COLOR_CHAR;
	}

}
