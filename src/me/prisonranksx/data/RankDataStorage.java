package me.prisonranksx.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.utils.MCTextEffect;

public class RankDataStorage {

	private Map<String, RankDataHandler> rankData;
	private PrisonRanksX main;
	private Set<String> paths;
	public Map<String, List<String>> pathRanks;
	private Map<String, List<String>> consoleCommands;
	private Map<String, List<String>> playerCommands;
	private Map<String, List<String>> opCommands;
	private Map<String, String> lastRankMap;
	private int pathsAmount;
	
	public RankDataStorage(PrisonRanksX main) {this.main = main;
	   this.rankData = new LinkedHashMap<String, RankDataHandler>();
	   this.paths = new HashSet<>();
	   this.pathRanks = new LinkedHashMap<String, List<String>>();
	   this.consoleCommands = new LinkedHashMap<>();
	   this.playerCommands = new LinkedHashMap<>();
	   this.opCommands = new LinkedHashMap<>();
	   this.lastRankMap = new HashMap<>();
	   this.pathsAmount = 0;
	}
	
	public int getPathsAmount() {
		return this.pathsAmount;
	}
	
	public GlobalDataStorage gds() {
		return this.main.globalStorage;
	}
	
	/**
	 * (String path, List<String> ranks)
	 * @return
	 */
	public Map<String, List<String>> getPathRanksMap() {
		return this.pathRanks;
	}
	
	public Map<String, List<String>> getConsoleCommands() {
		return this.consoleCommands;
	}
	
	public Map<String, List<String>> getOpCommands() {
		return this.opCommands;
	}
	
	public Map<String, List<String>> getPlayerCommands() {
		return this.playerCommands;
	}
	
	
	public void putPathRank(final String path, final String rank) {
		if(!pathRanks.containsKey(path)) {
			pathRanks.put(path, new LinkedList<>());
		}
		if(pathRanks.get(path).contains(rank)) {
			return;
		}
		List<String> ranks = pathRanks.get(path);
		ranks.add(rank);
		main.debugPreEnable("added rank: " + rank + " to path: " + path);
		pathRanks.put(path, ranks);
	}
	
	public RankDataStorage getStorage() {
		return this;
	}
	
	public Map<String, RankDataHandler> getEntireData() {
		return this.rankData;
	}
	
	public void putData(String rankPath, RankDataHandler rdh) {
		getEntireData().put(rankPath, rdh);
	}
	
	public RankDataHandler getDataHandler(String rankPath) {
		return getEntireData().get(rankPath);
	}
	
	public void removeData(String rankPath) {
		getEntireData().remove(rankPath);
	}
	/**
	 * Should only be used onEnable()
	 * can be used as a reload
	 */
	public void loadRanksData() {
		for(String pathName : main.getConfigManager().ranksConfig.getConfigurationSection("Ranks").getKeys(false)) {
			pathsAmount++;
			for(String rankName : main.getConfigManager().ranksConfig.getConfigurationSection("Ranks." + pathName).getKeys(false)) {
				String rankupName = main.getConfigManager().ranksConfig.getString("Ranks." + pathName + "." +  rankName + ".nextrank");
				String rankDisplayName = main.getConfigManager().ranksConfig.getString("Ranks." + pathName + "." +  rankName + ".display", "");
				double rankCost = main.getConfigManager().ranksConfig.getDouble("Ranks." + pathName + "." +  rankName + ".cost", 0.0);
				double rankupCost = main.getConfigManager().ranksConfig.getDouble("Ranks." + pathName + "." +  rankupName + ".cost", 0.0);
				String rankupDisplayName = main.getConfigManager().ranksConfig.getString("Ranks." + pathName + "." +  rankupName + ".display", "");
				boolean allowPrestige = main.getConfigManager().ranksConfig.getBoolean("Ranks." + pathName + "." +  rankName + ".allow-prestige");
				List<String> rankupCommands = getList("Ranks." + pathName + "." +  rankupName + ".executecmds");
				List<String> actionbarMessages = MCTextEffect.parseGlow(main.getConfigManager().ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".actionbar.text"));
				int actionbarInterval = main.getConfigManager().ranksConfig.getInt("Ranks." + pathName + "." +  rankupName + ".actionbar.interval");
				List<String> broadcastMessages = main.getConfigManager().ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".broadcast");
				List<String> messages = main.getConfigManager().ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".msg");
				List<String> actions = main.getConfigManager().ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".actions");
				List<String> addPermissionList = main.getConfigManager().ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".addpermission");
				List<String> delPermissionList = main.getConfigManager().ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".delpermission");
				RankRandomCommands randomCommandsManager = new RankRandomCommands(rankupName, false, pathName, true);
				boolean sendFirework = main.getConfigManager().ranksConfig.getBoolean("Ranks." + pathName + "." +  rankupName + ".send-firework");
				RankDataHandler rdh = new RankDataHandler(rankName, pathName);
				RankPath rankPath = new RankPath(rankName, pathName);
				Map<String, Double> numberRequirements = new LinkedHashMap<>();
				Map<String, String> stringRequirements = new LinkedHashMap<>();
				List<String> customRequirementMessage = Lists.newArrayList();
				if(main.getConfigManager().ranksConfig.isSet("Ranks." + pathName + "." + rankupName + ".requirements")) {
					for(String requirementCondition : main.getConfigManager().ranksConfig.getStringList("Ranks." + pathName + "." + rankupName + ".requirements")) {
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
				if(main.getConfigManager().ranksConfig.isSet("Ranks." + pathName + "." + rankupName + ".custom-requirement-message")) {
					for(String messageLine : main.getConfigManager().ranksConfig.getStringList("Ranks." + pathName + "." + rankupName + ".custom-requirement-message")) {
						customRequirementMessage.add(gds().parseHexColorCodes(messageLine));
					}
				}
				if(!stringRequirements.isEmpty()) {
					rdh.setStringRequirements(stringRequirements);
				}
				if(!numberRequirements.isEmpty()) {
					rdh.setNumberRequirements(numberRequirements);
				}
				if(!customRequirementMessage.isEmpty()) {
					rdh.setCustomRequirementMessage(customRequirementMessage);
				}
				rdh.setName(rankName);
                rdh.setDisplayName(gds().parseHexColorCodes(rankDisplayName));
                rdh.setCost(rankCost);
                rdh.setRankupName(rankupName);
                rdh.setRankupCost(rankupCost);
                rdh.setRankupDisplayName(gds().parseHexColorCodes(rankupDisplayName));
                rdh.setAllowPrestige(allowPrestige);
                rdh.setRankupCommands(gds().parseHexColorCodes(rankupCommands));
                rdh.setActionbarMessages(gds().parseHexColorCodes(actionbarMessages));
                rdh.setActionbarInterval(actionbarInterval);
                rdh.setBroadcastMessages(gds().parseHexColorCodes(broadcastMessages));
                rdh.setMsg(gds().parseHexColorCodes(messages));
                rdh.setActions(gds().parseHexColorCodes(actions));
                rdh.setAddPermissionList(addPermissionList);
                rdh.setDelPermissionList(delPermissionList);
                rdh.setRandomCommandsManager(randomCommandsManager);
                rdh.setFireworkDataHandler(main.getFireworkManager().readFromConfig(LevelType.RANK, rankName, pathName));
                rdh.setSendFirework(sendFirework);
                rdh.setPathName(pathName);
                rdh.setRankCommands(getList("Ranks." + pathName + "." +  rankName + ".executecmds"));
                rdh.setCurrentAddPermissionList(main.getConfigManager().ranksConfig.getStringList("Ranks." + pathName + "." +  rankName + ".addpermission"));
                rdh.setCurrentDelPermissionList(main.getConfigManager().ranksConfig.getStringList("Ranks." + pathName + "." +  rankName + ".delpermission"));
                lastRankMap.put(pathName, rankName);
                rankData.put(rankPath.get(), rdh);
                paths.add(pathName);
                putPathRank(pathName, rankName);
    			List<String> consoleCommands = Lists.newArrayList();
    			List<String> opCommands = Lists.newArrayList();
    			List<String> playerCommands = Lists.newArrayList();
    			if(rankupCommands != null && !rankupCommands.isEmpty()) {
    			for(String command : rankupCommands) {
    				if(command.startsWith("[console]") || !command.startsWith("[")) {
    					consoleCommands.add(command.replace("[console] ", "").replace("%rankup%", rankupName).replace("%rank%", rankName)
    							.replace("%rankup_display%", rankupDisplayName));
    				} else if(command.startsWith("[player]")) {
    					playerCommands.add(command.substring(9).replace("%rankup%", rankupName).replace("%rank%", rankName)
    							.replace("%rankup_display%", rankupDisplayName));
    				} else if(command.startsWith("[op]")) {
    					playerCommands.add(command.substring(5).replace("%rankup%", rankupName).replace("%rank%", rankName)
    							.replace("%rankup_display%", rankupDisplayName));
    				}
    			}
    			String rp = rankPath.get();
    			this.consoleCommands.put(rp, consoleCommands);
    			this.opCommands.put(rp, opCommands);
    			this.playerCommands.put(rp, playerCommands);
    			}
			 }
			}
	}
	
	public int getInteger(String node) {
		if(node == null || main.getConfigManager().ranksConfig.get(node) == null) {
			return 0;
		}
		return main.getConfigManager().ranksConfig.getInt(node);
	}
	
	public List<String> getList(String node) {
		if(node == null || main.getConfigManager().ranksConfig.get(node) == null) {
			return null;
		}
		return main.getConfigManager().ranksConfig.getStringList(node);
	}
	
	public void loadRankData(String rankName, String pathName) {
		String rankupName = main.getConfigManager().ranksConfig.getString("Ranks." + pathName + "." +  rankName + ".nextrank");
		String rankDisplayName = main.getConfigManager().ranksConfig.getString("Ranks." + pathName + "." +  rankName + ".display");
		Double rankCost = main.getConfigManager().ranksConfig.getDouble("Ranks." + pathName + "." +  rankName + ".cost", 0.0);
		Double rankupCost = main.getConfigManager().ranksConfig.getDouble("Ranks." + pathName + "." +  rankupName + ".cost", 0.0);
		String rankupDisplayName = main.getConfigManager().ranksConfig.getString("Ranks." + pathName + "." +  rankupName + ".display");
		boolean allowPrestige = main.getConfigManager().ranksConfig.getBoolean("Ranks." + pathName + "." +  rankName + ".allow-prestige");
		List<String> rankupCommands = main.getConfigManager().ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".executecmds");
		List<String> actionbarMessages = MCTextEffect.parseGlow(main.getConfigManager().ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".actionbar.text"));
		int actionbarInterval = getInteger("Ranks." + pathName + "." +  rankupName + ".actionbar.interval");
		List<String> broadcastMessages = main.getConfigManager().ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".broadcast");
		List<String> messages = main.getConfigManager().ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".text");
		List<String> actions = main.getConfigManager().ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".actions");
		List<String> addPermissionList = main.getConfigManager().ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".addpermission");
		List<String> delPermissionList = main.getConfigManager().ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".delpermission");
		RankRandomCommands randomCommandsManager = new RankRandomCommands(rankupName, false, pathName, true);
		boolean sendFirework = main.getConfigManager().ranksConfig.getBoolean("Ranks." + pathName + "." +  rankupName + ".send-firework");
		RankDataHandler rdh = new RankDataHandler(rankName, pathName);
		rdh.setName(rankName);
        rdh.setPathName(pathName);
        rdh.setDisplayName(rankDisplayName);
        rdh.setCost(rankCost);
        rdh.setRankupName(rankupName);
        rdh.setRankupCost(rankupCost);
        rdh.setRankupDisplayName(rankupDisplayName);
        rdh.setAllowPrestige(allowPrestige);
        rdh.setRankupCommands(rankupCommands);
        rdh.setActionbarMessages(actionbarMessages);
        rdh.setActionbarInterval(actionbarInterval);
        rdh.setBroadcastMessages(broadcastMessages);
        rdh.setMsg(messages);
        rdh.setActions(actions);
        rdh.setAddPermissionList(addPermissionList);
        rdh.setDelPermissionList(delPermissionList);
        rdh.setRandomCommandsManager(randomCommandsManager);
        rdh.setFireworkDataHandler(main.getFireworkManager().readFromConfig(LevelType.RANK, rankName, pathName));
        rdh.setSendFirework(sendFirework);
        RankPath rankPath = new RankPath(rankName, pathName);
        rankData.put(rankPath.get(), rdh);
	}
	
	public Set<String> getPaths() {
		return paths;
	}
	/**
	 * 
	 * @param rankName
	 * @return latest rank name found, so if there is 2 ranks with the same name in different paths this will return the one in the latest path
	 * 
	 */
	@Nullable
	public RankPath getRankPath(String rankName) {
		for(String rankPaths : rankData.keySet()) {
			if(rankPaths.split("#~#")[0].equals(rankName)) {
				return new RankPath(rankName, rankPaths.split("#~#")[1]);
			}
		}
		return null;
	}
	
	@Nullable
	public String getRankName(RankPath rankPath) {
		for(String rankPaths : rankData.keySet()) {
			if(rankPaths.split("#~#")[1] == rankPath.get().split("#~#")[1]) {
				return rankPath.getRankName();
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param pathName
	 * @return empty Collection if there is no pathName found
	 */
	public List<String> getRanksCollection(String pathName) {
		return pathRanks.get(pathName);
	}
//	public List<String> getRanksCollection(String pathName) {
		//List<String> ranksCollection = new ArrayList<>();
	//	for(String rankPaths : rankData.keySet()) {
			//if(RankPath.getRankPath(rankPaths).getPath().equalsIgnoreCase(pathName)) {
			//	ranksCollection.add(RankPath.getRankPath(rankPaths).getRank());
		//	}
		//}
		//Collections.sort(ranksCollection);
		//return ranksCollection;
	//}
	
	//public void setRankupName(RankPath rankPath, String rankupName) {
		//rankData.get(rankPath.get()).setRankupName(rankupName);
	//}
	
	public String getRankupName(RankPath rankPath) {
		return rankData.get(rankPath.get()).getRankupName();
	}
	
	public Double getCost(RankPath rankPath) {
		return rankData.get(rankPath.get()).getCost();
	}
	
	public String getDisplayName(RankPath rankPath) {
		return rankData.get(rankPath.get()).getDisplayName();
	}
	
	public boolean isAllowPrestige(RankPath rankPath) {
		return rankData.get(rankPath.get()).isAllowPrestige();
	}
	
	public double getRankupCost(RankPath rankPath) {
		return rankData.get(rankPath.get()).getRankupCost();
	}
	
	public String getRankupDisplayName(RankPath rankPath) {
		return rankData.get(rankPath.get()).getRankupDisplayName();
	}
	
	public List<String> getRankupCommands(RankPath rankPath) {
		return rankData.get(rankPath.get()).getRankupCommands();
	}
	
	public int getActionbarInterval(RankPath rankPath) {
		return rankData.get(rankPath.get()).getActionbarInterval();
	}
	
	public List<String> getActionbarMessages(RankPath rankPath) {
		return rankData.get(rankPath.get()).getActionbarMessages();
	}
	
	public List<String> getBroadcast(RankPath rankPath) {
		return rankData.get(rankPath.get()).getBroadcast();
	}
	
	public List<String> getMsg(RankPath rankPath) {
		return rankData.get(rankPath.get()).getMsg();
	}
	
	public List<String> getActions(RankPath rankPath) {
		return rankData.get(rankPath.get()).getActions();
	}
	
	public List<String> getAddPermissionList(RankPath rankPath) {
		return rankData.get(rankPath.get()).getAddPermissionList();
	}
	
	public List<String> getDelPermissionList(RankPath rankPath) {
		return rankData.get(rankPath.get()).getDelPermissionList();
	}
	
	public RankRandomCommands getRandomCommandsManager(RankPath rankPath) {
		return rankData.get(rankPath.get()).getRandomCommandsManager();
	}
	
	public Map<String, Object> getRandomCommandsMap(RankPath rankPath) {
		return rankData.get(rankPath.get()).getRandomCommandsManager().getRandomCommandsMap();
	}
	
	public FireworkDataHandler getFireworkDataHandler(RankPath rankPath) {
		return rankData.get(rankPath.get()).getFireworkDataHandler();
	}
	
	public boolean isSendFirework(RankPath rankPath) {
		return rankData.get(rankPath.get()).isSendFirework();
	}
	
	@SuppressWarnings("unchecked")
	public void setData(String node, Object value) {
		if(value == null || node.contains("LASTRANK")) {
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
		main.getConfigManager().ranksConfig.set(node, value);
	}
	/**
	 * 
	 * Use when you want to update a rank option then
	 * use the method loadRankData(String rankName) to load the data in game
	 */
	public void saveRankData(RankPath rankPath) {
		    String rankName = rankPath.getRankName();
			String rankup = rankData.get(rankPath.get()).getRankupName();
			String pathName = rankPath.getPathName();
            setData("Ranks." + pathName + "." +  rankName + ".nextrank", rankData.get(rankPath.get()).getRankupName());
            setData("Ranks." + pathName + "." +  rankName + ".cost", rankData.get(rankPath.get()).getCost());
            setData("Ranks." + pathName + "." +  rankName + ".display", rankData.get(rankPath.get()).getDisplayName());
            setData("Ranks." + pathName + "." +  rankName + ".allow-prestige", rankData.get(rankPath.get()).isAllowPrestige());
            setData("Ranks." + pathName + "." +  rankup + ".cost", rankData.get(rankPath.get()).getRankupCost());
            setData("Ranks." + pathName + "." +  rankup + ".display", rankData.get(rankPath.get()).getRankupDisplayName());
            setData("Ranks." + pathName + "." +  rankup + ".executecmds", rankData.get(rankPath.get()).getRankupCommands());
           // setData("Ranks." + pathName + "." +  rankup + ".actionbar.interval", rankData.get(rankPath.get()).getActionbarInterval());
           // setData("Ranks." + pathName + "." +  rankup + ".actionbar.text", rankData.get(rankPath.get()).getActionbarMessages());
           // setData("Ranks." + pathName + "." +  rankup + ".broadcast", rankData.get(rankPath.get()).getBroadcast());
           // setData("Ranks." + pathName + "." +  rankup + ".msg", rankData.get(rankPath.get()).getMsg());
           // setData("Ranks." + pathName + "." +  rankup + ".actions", rankData.get(rankPath.get()).getActions());
           // setData("Ranks." + pathName + "." +  rankup + ".addpermission", rankData.get(rankPath.get()).getAddPermissionList());
           // setData("Ranks." + pathName + "." +  rankup + ".delpermission", rankData.get(rankPath.get()).getDelPermissionList());
           // setData("Ranks." + pathName + "." +  rankup + ".randomcmds", rankData.get(rankPath.get()).getRandomCommandsManager().getRandomCommandsMap());
           // setData("Ranks." + pathName + "." +  rankup + ".firework", rankData.get(rankPath.get()).getFireworkManager().getFireworkBuilder());
           // setData("Ranks." + pathName + "." +  rankup + ".send-firework", rankData.get(rankPath.get()).isSendFirework());
	}
	/**
	 * Should only be used onDisable()
	 */
	public void saveRanksData() {
		if(!main.isRankEnabled) {
			return;
		}
			for(Entry<String, RankDataHandler> rank : rankData.entrySet()) {
				String rankName = rank.getKey().split("#~#")[0];
				String rankup = rankData.get(rank.getKey()).getRankupName();
				String pathName = rank.getKey().split("#~#")[1];
				//if(main.getConfigManager().ranksConfig.getConfigurationSection("Ranks." + pathName + "." + rankName) == null) {
					//main.getConfigManager().ranksConfig.createSection("Ranks." + pathName + "." + rankName);
				//}
                 setData("Ranks." + pathName + "." +  rankName + ".nextrank", rank.getValue().getRankupName());
                 setData("Ranks." + pathName + "." +  rankName + ".cost", rank.getValue().getCost());
                 setData("Ranks." + pathName + "." +  rankName + ".display", rank.getValue().getDisplayName());
                 if(rank.getValue().isAllowPrestige()) {
                 setData("Ranks." + pathName + "." +  rankName + ".allow-prestige", rank.getValue().isAllowPrestige());
                 }
                 setData("Ranks." + pathName + "." +  rankup + ".cost", rank.getValue().getRankupCost());
                 setData("Ranks." + pathName + "." +  rankup + ".display", rank.getValue().getRankupDisplayName());
                 setData("Ranks." + pathName + "." +  rankup + ".executecmds", rank.getValue().getRankupCommands());
                 setData("Ranks." + pathName + "." +  rankup + ".actionbar.interval", rank.getValue().getActionbarInterval());
                 setData("Ranks." + pathName + "." +  rankup + ".actionbar.text", rank.getValue().getActionbarMessages());
                 setData("Ranks." + pathName + "." +  rankup + ".broadcast", rank.getValue().getBroadcast());
                 setData("Ranks." + pathName + "." +  rankup + ".msg", rank.getValue().getMsg());
                 setData("Ranks." + pathName + "." +  rankup + ".actions", rank.getValue().getActions());
                 setData("Ranks." + pathName + "." +  rankup + ".addpermission", rank.getValue().getAddPermissionList());
                 setData("Ranks." + pathName + "." +  rankup + ".delpermission", rank.getValue().getDelPermissionList());
                 if(rank.getValue().getRandomCommandsManager() != null) {
                // setData("Ranks." + pathName + "." +  rankup + ".randomcmds", rank.getValue().getRandomCommandsManager().getRandomCommandsMap());
                 }

                 if(rank.getValue().isSendFirework()) {
                 setData("Ranks." + pathName + "." +  rankup + ".send-firework", rank.getValue().isSendFirework());
                 }
			}
	}

	public Map<String, String> getLastRankMap() {
		return lastRankMap;
	}
    
	public boolean isLastRank(String pathName, String rankName) {
		if(getLastRankMap().get(pathName).equalsIgnoreCase(rankName)) {
			return true;
		}
		return false;
	}
	
	public boolean isLastRank(RankPath rankPath) {
	    return lastRankMap.get(rankPath.getPathName()).equalsIgnoreCase(rankPath.getRankName());
	}
	
	public boolean isSetToLastRank(String rankName) {
        return lastRankMap.containsValue(rankName);
	}
	
	public String getLastRank(String pathName) {
		return lastRankMap.get(pathName);
	}
	
	public void setLastRankMap(Map<String, String> lastRankMap) {
		this.lastRankMap = lastRankMap;
	}
}
