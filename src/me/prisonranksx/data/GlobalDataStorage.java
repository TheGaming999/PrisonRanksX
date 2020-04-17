package me.prisonranksx.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;

import me.prisonranksx.PrisonRanksX;

public class GlobalDataStorage {

	private Map<String, String> stringData;
	private Map<String, Integer> integerData;
	private Map<String, Double> doubleData;
	private Map<String, Boolean> booleanData;
	private Map<String, List<String>> stringListData;
	private Map<String, Set<String>> stringSetData;
	private Map<String, Map<String, Object>> mapData;
	private Map<String, Object> globalData;
	private PrisonRanksX main;
	
	/**
	 * If you want to register your config thing just use one of register#### methods onEnable of your plugin
	 */
	public GlobalDataStorage(PrisonRanksX main) {
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
	
	/**
	 * must be run onEnable()
	 */
	@SuppressWarnings("unused")
	public void loadGlobalData() {
		List<String> worlds = registerStringListData("worlds");
		//Under Options
		boolean isRankEnabled = registerBooleanData("Options.rank-enabled");
		boolean isPrestigeEnabled = registerBooleanData("Options.prestige-enabled");
		boolean isRebirthEnabled = registerBooleanData("Options.rebirth-enabled");
		String forceDisplayOrder = registerStringData("Options.force-display-order");
		boolean isForceRankDisplay = registerBooleanData("Options.force-rank-display");
		boolean isForcePrestigeDisplay = registerBooleanData("Options.force-prestige-display");
		boolean isAllWorldsBroadcast = registerBooleanData("Options.allworlds-broadcast");
		boolean isForceRebirthDisplay = registerBooleanData("Options.force-rebirth-display");
		String noPrestigeDispaly = registerStringData("Options.no-prestige-display");
		String noRebirthDisplay = registerStringData("Options.no-rebirth-display");
		boolean isSendRankupMsg = registerBooleanData("Options.send-rankupmsg");
		boolean isSendPrestigeMsg = registerBooleanData("Options.send-prestigemsg");
		boolean isSendRebirthMsg = registerBooleanData("Options.send-rebirthmsg");
		boolean isGuiRankList = registerBooleanData("Options.GUI-RANKLIST");
		boolean isGuiPrestigeList = registerBooleanData("Options.GUI-PRESTIGELIST");
		boolean isGuiRebirthList = registerBooleanData("Options.GUI-REBIRTHLIST");
		String prestigeSoundName = registerStringData("Options.prestigesound-name");
		double prestigeSoundVolume = registerDoubleData("Options.prestigesound-volume");
		double prestigeSoundPitch = registerDoubleData("Options.prestigesound-pitch");
		String rankupSoundName = registerStringData("Options.rankupsound-name");
		double rankupSoundVolume = registerDoubleData("Options.rankupsound-volume");
		double rankupSoundPitch = registerDoubleData("Options.rankupsound-pitch");
		String rebirthSoundName = registerStringData("Options.rebirthsound-name");
		double rebirthSoundVolume = registerDoubleData("Options.rebirthsound-volume");
		double rebirthSoundPitch = registerDoubleData("Options.rebirthsound-pitch");
		boolean isPerRankPermission = registerBooleanData("Options.per-rank-permission");
		boolean isRankupMaxBroadcastLastRankOnly = registerBooleanData("Options.rankupmax-broadcastlastrankonly");
		boolean isRankupMaxMsgLastRankOnly = registerBooleanData("Options.rankupmax-msglastrankonly");
		boolean isRankupMaxRankupMsgLastRankOnly = registerBooleanData("Options.rankupmax-rankupmsglastrankonly");
		boolean isRankupVaultGroups = registerBooleanData("Options.rankup-vault-groups");
		String rankupVaultGroupsPlugin = registerStringData("Options.rankup-vault-groups-plugin");
		boolean isAutoRankup = registerBooleanData("Options.autorankup");
		boolean rankupMaxWithPrestige = registerBooleanData("Options.rankupmax-with-prestige");
	    int autoRankupDelay = registerIntegerData("Options.autorankup-delay");
	    int autoPrestigeDelay = registerIntegerData("Options.autoprestige-delay");
	    int autoRebirthDelay = registerIntegerData("Options.autorebirth-delay");
	    boolean actionbarProgress = registerBooleanData("Options.actionbar-progress");
	    boolean actionbarProgressOnlyPickaxe = registerBooleanData("Options.actionbar-progress-only-pickaxe");
	    String actionbarProgressFormat = registerStringData("Options.actionbar-progress-format");
	    int actionbarProgressUpdater = registerIntegerData("Options.actionbar-progress-updater");
		//Under Ranklist-text
		String rankListText_rankCurrentFormat = registerStringData("Ranklist-text.rank-current-format");
		String rankListText_rankCompletedFormat = registerStringData("Ranklist-text.rank-completed-format");
		String rankListText_rankOtherFormat = registerStringData("Ranklist-text.rank-other-format");
		boolean rankListText_isEnablePages = registerBooleanData("Ranklist-text.enable-pages");
		int rankListText_rankPerPage = registerIntegerData("Ranklist-text.rank-per-page");
		List<String> rankListText_rankWithPagesListFormat = registerStringListData("Ranklist-text.rank-with-pages-list-format");
		List<String> rankListText_rankListFormat = registerStringListData("Ranklist-text.rank-list-format");
		//Under Prestigelist-text
		String prestigeListText_prestigeCurrentFormat = registerStringData("Prestigelist-text.prestige-current-format");
		String prestigeListText_prestigeCompletedFormat = registerStringData("Prestigelist-text.prestige-completed-format");
		String prestigeListText_prestigeOtherFormat = registerStringData("Prestigelist-text.prestige-other-format");
		boolean prestigeListText_isEnablePages = registerBooleanData("Prestigelist-text.enable-pages");
		int prestigeListText_prestigePerPage = registerIntegerData("Prestigelist-text.prestige-per-page");
		List<String> prestigeListText_prestigeWithPagesListFormat = registerStringListData("Prestigelist-text.prestige-with-pages-list-format");
		List<String> prestigeListText_prestigeListFormat = registerStringListData("Prestigelist-text.prestige-list-format");
		//Under Rebirthlist-text
		String rebirthListText_rebirthCurrentFormat = registerStringData("Rebirthlist-text.rebirth-current-format");
		String rebirthListText_rebirthCompletedFormat = registerStringData("Rebirthlist-text.rebirth-completed-format");
		String rebirthListText_rebirthOtherFormat = registerStringData("Rebirthlist-text.rebirth-other-format");
		boolean rebirthListText_isEnablePages = registerBooleanData("Rebirthlist-text.enable-pages");
		int rebirthListText_rebirthPerPage = registerIntegerData("Rebirthlist-text.rebirth-per-page");
		List<String> rebirthListText_rebirthWithPagesListFormat = registerStringListData("Rebirthlist-text.rebirth-with-pages-list-format");
		List<String> rebirthListText_rebirthListFormat = registerStringListData("Rebirthlist-text.rebirth-list-format");
		//Under Holograms.rankup
		boolean rankupHologramIsEnable = registerBooleanData("Holograms.rankup.enable");
		int rankupHologramRemoveTime = registerIntegerData("Holograms.rankup.remove-time");
		int rankupHologramHeight = registerIntegerData("Holograms.rankup.height");
		List<String> rankupHologramFormat = registerStringListData("Holograms.rankup.format");
		//Under Holograms.prestige
		boolean prestigeHologramIsEnable = registerBooleanData("Holograms.prestige.enable");
		int prestigeHologramRemoveTime = registerIntegerData("Holograms.prestige.remove-time");
		int prestigeHologramHeight = registerIntegerData("Holograms.prestige.height");
		List<String> prestigeHologramFormat = registerStringListData("Holograms.prestige.format");
		//Under Holograms.rebirth
		boolean rebirthHologramIsEnable = registerBooleanData("Holograms.rebirth.enable");
		int rebirthHologramRemoveTime = registerIntegerData("Holograms.rebirth.remove-time");
		int rebirthHologramHeight = registerIntegerData("Holograms.rebirth.height");
		List<String> rebirthHologramFormat = registerStringListData("Holograms.rebirth.format");
		//Under MySQL
		boolean mySqlIsEnable = registerBooleanData("MySQL.enable");
		String mySqlHost = registerStringData("MySQL.host");
		int mySqlPort = registerIntegerData("MySQL.port");
		String mySqldatabase = registerStringData("MySQL.database");
		String mySqltable = registerStringData("MySQL.table");
		String mySqlUsername = registerStringData("MySQL.username");
		String mySqlPassword = registerStringData("MySQL.password");
		boolean mySqlUseSSL = registerBooleanData("MySQL.useSSL");
		boolean mySqlAutoReconnect = registerBooleanData("MySQL.autoReconnect");
		//Under Main-GUIOptions
		String previousPageItemName = registerStringData("Main-GUIOptions.previouspage-itemNAME");
		String previousPageItemDisplayName = registerStringData("Main-GUIOptions.previouspage-itemDISPLAYNAME");
		List<String> previousPageItemLore = registerStringListData("Main-GUIOptions.previouspage-itemLORE");
		List<String> previousPageItemEnchantments = registerStringListData("Main-GUIOptions.previouspage-itemENCHANTMENTS");
		int previousPageItemData = registerIntegerData("Main-GUIOptions.previouspage-itemDATA");
		String nextPageItemName = registerStringData("Main-GUIOptions.nextpage-itemNAME");
		String nextPageItemDisplayName = registerStringData("Main-GUIOptions.nextpage-itemDISPLAYNAME");
		List<String> nextPageItemLore = registerStringListData("Main-GUIOptions.nextpage-itemLORE");
		List<String> nextPageItemEnchantments = registerStringListData("Main-GUIOptions.nextpage-itemENCHANTMENTS");
		int nextPageItemData = registerIntegerData("Main-GUIOptions.nextpage-itemDATA");
		String noPreviousPages = registerStringData("Main-GUIOptions.no-previous-pages");
		String noAdditonalPages = registerStringData("Main-GUIOptions.no-additional-pages");
		String currentPageItemName = registerStringData("Main-GUIOptions.currentpage-itemNAME");
		String currentPageItemDisplayName = registerStringData("Main-GUIOptions.currentpage-itemDISPLAYNAME");
		List<String> currentPageItemLore = registerStringListData("Main-GUIOptions.currentpage-itemLORE");
		List<String> currentPageItemEnchantments = registerStringListData("Main-GUIOptions.currentpage-itemENCHANTMENTS");
		int currentPageItemData = registerIntegerData("Main-GUIOptions.currentpage-itemDATA");
		//Under Ranklist-gui.current-format
		String rankListGuiTitle = registerStringData("Ranklist-gui.title");
		List<String> rankListGuiConstantItems = registerStringListData("Ranklist-gui.constant-items");
		String rankListGUIAllowedSlots = registerStringData("Ranklist-gui.allowed-slots");
		String rankListGuiCurrentItemName = registerStringData("Ranklist-gui.current-format.itemNAME");
		int rankListGuiCurrentItemAmount = registerIntegerData("Ranklist-gui.current-format.itemAMOUNT");
		String rankListGuiCurrentItemDisplayName = registerStringData("Ranklist-gui.current-format.itemDISPLAYNAME");
		List<String> rankListGuiCurrentItemLore = registerStringListData("Ranklist-gui.current-format.itemLORE");
		List<String> rankListGuiCurrentItemEnchantments = registerStringListData("Ranklist-gui.current-format.itemENCHANTMENTS");
		List<String> rankListGuiCurrentItemFlags = registerStringListData("Ranklist-gui.current-format.itemFLAGS");
		List<String> rankListGuiCurrentItemCommands = registerStringListData("Ranklist-gui.current-format.itemCOMMANDS");
		Map<String, Object> rankListCurrentCustomItems = registerMapData("Ranklist-gui.current-format.custom", true);
		//Under Ranklist-gui.completed-format
		String rankListGuiCompletedItemName = registerStringData("Ranklist-gui.completed-format.itemNAME");
		int rankListGuiCompletedItemAmount = registerIntegerData("Ranklist-gui.completed-format.itemAMOUNT");
		String rankListGuiCompletedItemDisplayName = registerStringData("Ranklist-gui.completed-format.itemDISPLAYNAME");
		List<String> rankListGuiCompletedItemLore = registerStringListData("Ranklist-gui.completed-format.itemLORE");
		List<String> rankListGuiCompletedItemEnchantments = registerStringListData("Ranklist-gui.completed-format.itemENCHANTMENTS");
		List<String> rankListGuiCompletedItemFlags = registerStringListData("Ranklist-gui.completed-format.itemFLAGS");
		List<String> rankListGuiCompletedItemCommands = registerStringListData("Ranklist-gui.completed-format.itemCOMMANDS");
		Map<String, Object> rankListCompletedCustomItems = registerMapData("Ranklist-gui.completed-format.custom", true);
		//Under Ranklist-gui.other-format
		String rankListGuiOtherItemName = registerStringData("Ranklist-gui.other-format.itemNAME");
		int rankListGuiOtherItemAmount = registerIntegerData("Ranklist-gui.other-format.itemAMOUNT");
		String rankListGuiOtherItemDisplayName = registerStringData("Ranklist-gui.other-format.itemDISPLAYNAME");
		List<String> rankListGuiOtherItemLore = registerStringListData("Ranklist-gui.other-format.itemLORE");
		List<String> rankListGuiOtherdItemEnchantments = registerStringListData("Ranklist-gui.other-format.itemENCHANTMENTS");
		List<String> rankListGuiOtherItemFlags = registerStringListData("Ranklist-gui.other-format.itemFLAGS");
		List<String> rankListGuiOtherItemCommands = registerStringListData("Ranklist-gui.other-format.itemCOMMANDS");
		Map<String, Object> rankListOtherCustomItems = registerMapData("Ranklist-gui.other-format.custom", true);
		//Under Prestigelist-gui.current-format
		String prestigeListGuiTitle = registerStringData("Prestigelist-gui.title");
		List<String> prestigeListGuiConstantItems = registerStringListData("Prestigelist-gui.constant-items");
		String prestigeListGUIAllowedSlots = registerStringData("Prestigelist-gui.allowed-slots");
		String prestigeListGuiCurrentItemName = registerStringData("Prestigelist-gui.current-format.itemNAME");
		int prestigeListGuiCurrentItemAmount = registerIntegerData("Prestigelist-gui.current-format.itemAMOUNT");
		String prestigeListGuiCurrentItemDisplayName = registerStringData("Prestigelist-gui.current-format.itemDISPLAYNAME");
		List<String> prestigeListGuiCurrentItemLore = registerStringListData("Prestigelist-gui.current-format.itemLORE");
		List<String> prestigeListGuiCurrentItemEnchantments = registerStringListData("Prestigelist-gui.current-format.itemENCHANTMENTS");
		List<String> prestigeListGuiCurrentItemFlags = registerStringListData("Prestigelist-gui.current-format.itemFLAGS");
		List<String> prestigeListGuiCurrentItemCommands = registerStringListData("Prestigelist-gui.current-format.itemCOMMANDS");
		Map<String, Object> prestigeListCurrentCustomItems = registerMapData("Prestigelist-gui.current-format.custom", true);
		//Under Prestigelist-gui.completed-format
		String prestigeListGuiCompletedItemName = registerStringData("Prestigelist-gui.completed-format.itemNAME");
		int prestigeListGuiCompletedItemAmount = registerIntegerData("Prestigelist-gui.completed-format.itemAMOUNT");
		String prestigeListGuiCompletedItemDisplayName = registerStringData("Prestigelist-gui.completed-format.itemDISPLAYNAME");
		List<String> prestigeListGuiCompletedItemLore = registerStringListData("Prestigelist-gui.completed-format.itemLORE");
		List<String> prestigeListGuiCompletedItemEnchantments = registerStringListData("Prestigelist-gui.completed-format.itemENCHANTMENTS");
		List<String> prestigeListGuiCompletedItemFlags = registerStringListData("Prestigelist-gui.completed-format.itemFLAGS");
		List<String> prestigeListGuiCompletedItemCommands = registerStringListData("Prestigelist-gui.completed-format.itemCOMMANDS");
		Map<String, Object> prestigeListCompletedCustomItems = registerMapData("Prestigelist-gui.completed-format.custom", true);
		//Under Prestigelist-gui.other-format
		String prestigeListGuiOtherItemName = registerStringData("Prestigelist-gui.other-format.itemNAME");
		int prestigeListGuiOtherItemAmount = registerIntegerData("Prestigelist-gui.other-format.itemAMOUNT");
		String prestigeListGuiOtherItemDisplayName = registerStringData("Prestigelist-gui.other-format.itemDISPLAYNAME");
		List<String> prestigeListGuiOtherItemLore = registerStringListData("Prestigelist-gui.other-format.itemLORE");
		List<String> prestigeListGuiOtherdItemEnchantments = registerStringListData("Prestigelist-gui.other-format.itemENCHANTMENTS");
		List<String> prestigeListGuiOtherItemFlags = registerStringListData("Prestigelist-gui.other-format.itemFLAGS");
		List<String> prestigeListGuiOtherItemCommands = registerStringListData("Prestigelist-gui.other-format.itemCOMMANDS");
		Map<String, Object> prestigeListOtherCustomItems = registerMapData("Prestigelist-gui.other-format.custom", true);
		//Under Rebirthlist-gui.current-format
		String rebirthListGuiTitle = registerStringData("Rebirthlist-gui.title");
		List<String> rebirthListGuiConstantItems = registerStringListData("Rebirthlist-gui.constant-items");
		String rebirthListGUIAllowedSlots = registerStringData("Rebirthlist-gui.allowed-slots");
		String rebirthListGuiCurrentItemName = registerStringData("Rebirthlist-gui.current-format.itemNAME");
		int rebirthListGuiCurrentItemAmount = registerIntegerData("Rebirthlist-gui.current-format.itemAMOUNT");
		String rebirthListGuiCurrentItemDisplayName = registerStringData("Rebirthlist-gui.current-format.itemDISPLAYNAME");
		List<String> rebirthListGuiCurrentItemLore = registerStringListData("Rebirthlist-gui.current-format.itemLORE");
		List<String> rebirthListGuiCurrentItemEnchantments = registerStringListData("Rebirthlist-gui.current-format.itemENCHANTMENTS");
		List<String> rebirthListGuiCurrentItemFlags = registerStringListData("Rebirthlist-gui.current-format.itemFLAGS");
		List<String> rebirthListGuiCurrentItemCommands = registerStringListData("Rebirthlist-gui.current-format.itemCOMMANDS");
		Map<String, Object> rebirthListCurrentCustomItems = registerMapData("Rebirthlist-gui.current-format.custom", true);
		//Under Rebirthlist-gui.completed-format
		String rebirthListGuiCompletedItemName = registerStringData("Rebirthlist-gui.completed-format.itemNAME");
		int rebirthListGuiCompletedItemAmount = registerIntegerData("Rebirthlist-gui.completed-format.itemAMOUNT");
		String rebirthListGuiCompletedItemDisplayName = registerStringData("Rebirthlist-gui.completed-format.itemDISPLAYNAME");
		List<String> rebirthListGuiCompletedItemLore = registerStringListData("Rebirthlist-gui.completed-format.itemLORE");
		List<String> rebirthListGuiCompletedItemEnchantments = registerStringListData("Rebirthlist-gui.completed-format.itemENCHANTMENTS");
		List<String> rebirthListGuiCompletedItemFlags = registerStringListData("Rebirthlist-gui.completed-format.itemFLAGS");
		List<String> rebirthListGuiCompletedItemCommands = registerStringListData("Rebirthlist-gui.completed-format.itemCOMMANDS");
		Map<String, Object> rebirthListCompletedCustomItems = registerMapData("Rebirthlist-gui.completed-format.custom", true);
		//Under Rebirthlist-gui.other-format
		String rebirthListGuiOtherItemName = registerStringData("Rebirthlist-gui.other-format.itemNAME");
		int rebirthListGuiOtherItemAmount = registerIntegerData("Rebirthlist-gui.other-format.itemAMOUNT");
		String rebirthListGuiOtherItemDisplayName = registerStringData("Rebirthlist-gui.other-format.itemDISPLAYNAME");
		List<String> rebirthListGuiOtherItemLore = registerStringListData("Rebirthlist-gui.other-format.itemLORE");
		List<String> rebirthListGuiOtherItemEnchantments = registerStringListData("Rebirthlist-gui.other-format.itemENCHANTMENTS");
		List<String> rebirthListGuiOtherItemFlags = registerStringListData("Rebirthlist-gui.other-format.itemFLAGS");
		List<String> rebirthListGuiOtherItemCommands = registerStringListData("Rebirthlist-gui.other-format.itemCOMMANDS");
		Map<String, Object> rebirthListOtherCustomItems = registerMapData("Rebirthlist-gui.other-format.custom", true);
		//Under PrestigeOptions
		boolean prestigeOptionResetMoney = registerBooleanData("PrestigeOptions.ResetMoney");
		boolean prestigeOptionResetRank = registerBooleanData("PrestigeOptions.ResetRank");
		double prestigeOptionRankupCostIncreasePercentage = registerDoubleData("PrestigeOptions.rankup_cost_increase_percentage");
		String prestigeOptionCostIncreaseType = registerStringData("PrestigeOptions.cost_increase_type");
		String prestigeOptionCostIncreaseExpression = registerStringData("PrestigeOptions.cost_increase_expression");
		List<String> prestigeOptionPrestigeCMDS = registerStringListData("PrestigeOptions.prestige-cmds");
		List<String> prestigeOptionPrestigeDeleteCMDS = registerStringListData("PrestigeOptions.prestige-delete-cmds");
		List<String> prestigeOptionPrestigeResetCMDS = registerStringListData("PrestigeOptions.prestige-reset-cmds");
		//Under RebirthOptions
		boolean rebirthOptionResetMoney = registerBooleanData("RebirthOptions.ResetMoney");
		boolean rebirthOptionResetRank = registerBooleanData("RebirthOptions.ResetRank");
		boolean rebirthOptionResetPrestige = registerBooleanData("RebirthOptions.ResetPrestige");
		double rebirthOptionPrestigeCostIncreasePercentage = registerDoubleData("RebirthOptions.prestige_cost_increase_percentage");
		String rebirthOptionCostIncreaseType = registerStringData("RebirthOptions.cost_increase_type");
		String rebirthOptionCostIncreaseExpression = registerStringData("RebirthOptions.cost_increase_expression");
		List<String> rebirthOptionRebirthCMDS = registerStringListData("RebirthOptions.rebirth-cmds");
		List<String> rebirthOptionRebirthDeleteCMDS = registerStringListData("RebirthOptions.rebirth-delete-cmds");
		List<String> rebirthOptionRebirthResetCMDS = registerStringListData("RebirthOptions.rebirth-reset-cmds");
		//Under RankOptions
		List<String> rankOptionRankDeleteCMDS = registerStringListData("RankOptions.rank-delete-cmds");
		List<String> rankOptionRankResetCMDS = registerStringListData("RankOptions.rank-reset-cmds");
		//Under PlaceholderAPI
		String rankupProgressStyle = registerStringData("PlaceholderAPI.rankup-progress-style");
		String rankupProgressFilled = registerStringData("PlaceholderAPI.rankup-progress-filled");
		String rankupProgressNeeded = registerStringData("PlaceholderAPI.rankup-progress-needed");
		boolean rankupProgressFullEnabled = registerBooleanData("PlaceholderAPI.rankup-progress-full-enabled");
		String rankupProgressFull = registerStringData("PlaceholderAPI.rankup-progress-full");
		String rankupProgressLastRank = registerStringData("PlaceholderAPI.rankup-progress-lastrank");
		String rankupPercentageLastRank = registerStringData("PlaceholderAPI.rankup-percentage-lastrank");
		String rankupCostLastRank = registerStringData("PlaceholderAPI.rankup-cost-lastrank");
		String rankupLastRank = registerStringData("PlaceholderAPI.rankup-lastrank");
		boolean currentRankLastRankEnabled = registerBooleanData("PlaceholderAPI.currentrank-lastrank-enabled");
		String currentRankLastRank = registerStringData("PlaceholderAPI.currentrank-lastrank");
		String prestigeLastPrestige = registerStringData("PlaceholderAPI.prestige-lastprestige");
		String prestigeNotPrestiged = registerStringData("PlaceholderAPI.prestige-notprestiged");
		String nextPrestigeNotPrestiged = registerStringData("PlaceholderAPI.nextprestige-notprestiged");
		String rebirthNotRebirthed = registerStringData("PlaceholderAPI.rebirth-notrebirthed");
		String nextRebirthNotRebirthed = registerStringData("PlaceholderAPI.nextrebirth-notrebirthed");
		String rebirthLastRebirth = registerStringData("PlaceholderAPI.rebirth-lastrebirth");
		String currencySymbol = registerStringData("PlaceholderAPI.currency-symbol");
		boolean isCurrencySymbolBehind = registerBooleanData("PlaceholderAPI.currency-symbol-behind");
		String percentSign = registerStringData("PlaceholderAPI.percent-sign");
		boolean isPercentSignBehind = registerBooleanData("PlaceholderAPI.percent-sign-behind");
		String nextProgressStyleRankup = registerStringData("PlaceholderAPI.next-progress-style.rankup");
		String nextProgressFilledRankup = registerStringData("PlaceholderAPI.next-progress-filled.rankup");
		String nextProgressNeededRankup = registerStringData("PlaceholderAPI.next-progress-needed.rankup");
		String nextProgressStylePrestige = registerStringData("PlaceholderAPI.next-progress-style.prestige");
		String nextProgressFilledPrestige = registerStringData("PlaceholderAPI.next-progress-filled.prestige");
		String nextProgressNeededPrestige = registerStringData("PlaceholderAPI.next-progress-needed.prestige");
		String nextProgressStyleRebirth = registerStringData("PlaceholderAPI.next-progress-style.rebirth");
		String nextProgressFilledRebirth = registerStringData("PlaceholderAPI.next-progress-filled.rebirth");
		String nextProgressNeededRebirth = registerStringData("PlaceholderAPI.next-progress-needed.rebirth");
		boolean nextProgressFullIsRankupEnabled = registerBooleanData("PlaceholderAPI.next-progress-full-isrankup-enabled");
		boolean nextProgressFullIsPrestigeEnabled = registerBooleanData("PlaceholderAPI.next-progress-full-isprestige-enabled");
		boolean nextProgressFullIsRebirthEnabled = registerBooleanData("PlaceholderAPI.next-progress-full-isrebirth-enabled");
		boolean nextProgressFullIsLastEnabled = registerBooleanData("PlaceholderAPI.next-progress-full-islast-enabled");
		String nextProgressFullIsRankup = registerStringData("PlaceholderAPI.next-progress-full-isrankup");
	    String nextProgressFullIsPrestige = registerStringData("PlaceholderAPI.next-progress-full-isprestige");
	    String nextProgressFullIsRebirth = registerStringData("PlaceholderAPI.next-progress-full-isrebirth");
	    String nextProgressFullIsLast = registerStringData("PlaceholderAPI.next-progress-full-islast");
	    //Under MoneyFormatter
	    String thousand = registerStringData("MoneyFormatter.thousand");
	    String million = registerStringData("MoneyFormatter.million");
	    String billion = registerStringData("MoneyFormatter.billion");
	    String trillion = registerStringData("MoneyFormatter.trillion");
	    String quadrillion = registerStringData("MoneyFormatter.quadrillion");
	    String quintillion = registerStringData("MoneyFormatter.quintillion");
	    String sextillion = registerStringData("MoneyFormatter.sextillion");
	    String septillion = registerStringData("MoneyFormatter.septillion");
	    String octillion = registerStringData("MoneyFormatter.octillion");
	    String nonillion = registerStringData("MoneyFormatter.nonillion");
	    String decillion = registerStringData("MoneyFormatter.decillion");
	    String undecillion = registerStringData("MoneyFormatter.undecillion");
	    String duoDecillion = registerStringData("MoneyFormatter.Duodecillion");
	    String zillion = registerStringData("MoneyFormatter.zillion");
	    //Under 'NOTHING'
	    String defaultRank = registerStringData("defaultrank");
	    String lastRank = registerStringData("lastrank");
	    String defaultPath = registerStringData("defaultpath");
	    String firstPrestige = registerStringData("firstprestige");
	    String lastPrestige = registerStringData("lastprestige");
	    String firstRebirth = registerStringData("firstrebirth");
	    String lastRebirth = registerStringData("lastrebirth");
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
	
}
