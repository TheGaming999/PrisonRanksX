package me.prisonranksx.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;

import me.prisonranksx.PrisonRanksX;

public class RankDataStorage {

	private Map<String, RankDataHandler> rankData;
	private PrisonRanksX main;
	private Set<String> paths;
	private Map<String, List<String>> pathRanks;
	
	public RankDataStorage(PrisonRanksX main) {this.main = main;
	   this.rankData = new LinkedHashMap<String, RankDataHandler>();
	   this.paths = new HashSet<>();
	   this.pathRanks = new LinkedHashMap<>();
	}
	
	public Map<String, List<String>> getPathRanksMap() {
		return this.pathRanks;
	}
	
	public void putPathRank(String path, String rank) {
		if(!pathRanks.containsKey(path)) {
			pathRanks.put(path, new ArrayList<>());
		}
		if(pathRanks.get(path).contains(rank)) {
			return;
		}
		List<String> ranks = pathRanks.get(path);
		ranks.add(rank);
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
		for(String pathName : main.configManager.ranksConfig.getConfigurationSection("Ranks").getKeys(false)) {
			for(String rankName : main.configManager.ranksConfig.getConfigurationSection("Ranks." + pathName).getKeys(false)) {
				String rankupName = main.configManager.ranksConfig.getString("Ranks." + pathName + "." +  rankName + ".nextrank");
				String rankDisplayName = main.configManager.ranksConfig.getString("Ranks." + pathName + "." +  rankName + ".display");
				Double rankCost = main.configManager.ranksConfig.getDouble("Ranks." + pathName + "." +  rankName + ".cost", 0.0);
				Double rankupCost = main.configManager.ranksConfig.getDouble("Ranks." + pathName + "." +  rankupName + ".cost", 0.0);
				String rankupDisplayName = main.configManager.ranksConfig.getString("Ranks." + pathName + "." +  rankupName + ".display");
				boolean allowPrestige = main.configManager.ranksConfig.getBoolean("Ranks." + pathName + "." +  rankName + ".allow-prestige");
				List<String> rankupCommands = main.configManager.ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".executecmds");
				List<String> actionbarMessages = main.configManager.ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".actionbar.text");
				int actionbarInterval = main.configManager.ranksConfig.getInt("Ranks." + pathName + "." +  rankupName + ".actionbar.interval");
				List<String> broadcastMessages = main.configManager.ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".broadcast");
				List<String> messages = main.configManager.ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".text");
				List<String> actions = main.configManager.ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".actions");
				List<String> addPermissionList = main.configManager.ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".addpermission");
				List<String> delPermissionList = main.configManager.ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".delpermission");
				RankRandomCommands randomCommandsManager = new RankRandomCommands(rankupName, false, pathName, true);
				FireworkManager fireworkManager = new FireworkManager(rankupName, LevelType.RANK, pathName);
				boolean sendFirework = main.configManager.ranksConfig.getBoolean("Ranks." + pathName + "." +  rankName + ".send-firework");
				RankDataHandler rdh = new RankDataHandler(rankName, pathName);
				RankPath rankPath = new RankPath(rankName, pathName);
				rdh.setName(rankName);
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
                rdh.setFireworkManager(fireworkManager);
                rdh.setSendFirework(sendFirework);
                rdh.setPathName(pathName);
                rankData.put(rankPath.get(), rdh);
                paths.add(pathName);
                putPathRank(pathName, rankName);
			 }
			}
	}
	
	public int getInteger(String node) {
		if(node == null || main.configManager.ranksConfig.get(node) == null) {
			return 0;
		}
		return main.configManager.ranksConfig.getInt(node);
	}
	
	public void loadRankData(String rankName, String pathName) {
		String rankupName = main.configManager.ranksConfig.getString("Ranks." + pathName + "." +  rankName + ".nextrank");
		String rankDisplayName = main.configManager.ranksConfig.getString("Ranks." + pathName + "." +  rankName + ".display");
		Double rankCost = main.configManager.ranksConfig.getDouble("Ranks." + pathName + "." +  rankName + ".cost", 0.0);
		Double rankupCost = main.configManager.ranksConfig.getDouble("Ranks." + pathName + "." +  rankupName + ".cost", 0.0);
		String rankupDisplayName = main.configManager.ranksConfig.getString("Ranks." + pathName + "." +  rankupName + ".display");
		boolean allowPrestige = main.configManager.ranksConfig.getBoolean("Ranks." + pathName + "." +  rankName + ".allow-prestige");
		List<String> rankupCommands = main.configManager.ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".executecmds");
		List<String> actionbarMessages = main.configManager.ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".actionbar.text");
		int actionbarInterval = getInteger("Ranks." + pathName + "." +  rankupName + ".actionbar.interval");
		List<String> broadcastMessages = main.configManager.ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".broadcast");
		List<String> messages = main.configManager.ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".text");
		List<String> actions = main.configManager.ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".actions");
		List<String> addPermissionList = main.configManager.ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".addpermission");
		List<String> delPermissionList = main.configManager.ranksConfig.getStringList("Ranks." + pathName + "." +  rankupName + ".delpermission");
		RankRandomCommands randomCommandsManager = new RankRandomCommands(rankupName, false, pathName, true);
		FireworkManager fireworkManager = new FireworkManager(rankupName, LevelType.RANK, pathName);
		boolean sendFirework = main.configManager.ranksConfig.getBoolean("Ranks." + pathName + "." +  rankName + ".send-firework");
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
        rdh.setFireworkManager(fireworkManager);
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
	public RankPath getRankPath(String rankName) {
		for(String rankPaths : rankData.keySet()) {
			if(rankPaths.split("#~#")[0].equals(rankName)) {
				return new RankPath(rankName, rankPaths.split("#~#")[1]);
			}
		}
		return null;
	}
	
	public String getRankName(RankPath rankPath) {
		for(String rankPaths : rankData.keySet()) {
			if(rankPaths.split("#~#")[1] == rankPath.get().split("#~#")[1]) {
				return rankPath.getRank();
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
	
	public Double getRankupCost(RankPath rankPath) {
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
	
	public FireworkManager getFireworkManager(RankPath rankPath) {
		return rankData.get(rankPath.get()).getFireworkManager();
	}
	
	public Map<String, Object> getFireworkBuilder(RankPath rankPath) {
		return rankData.get(rankPath.get()).getFireworkManager().getFireworkBuilder();
	}
	
	public boolean isSendFirework(RankPath rankPath) {
		return rankData.get(rankPath.get()).isSendFirework();
	}
	
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
		main.configManager.ranksConfig.set(node, value);
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
				//if(main.configManager.ranksConfig.getConfigurationSection("Ranks." + pathName + "." + rankName) == null) {
					//main.configManager.ranksConfig.createSection("Ranks." + pathName + "." + rankName);
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
                 if(rank.getValue().getFireworkManager() != null && rank.getValue().isSendFirework()) {
                 // setData("Ranks." + pathName + "." +  rankup + ".firework-builder", rank.getValue().getFireworkManager().getFireworkBuilder());
                 }
                 if(rank.getValue().isSendFirework()) {
                 setData("Ranks." + pathName + "." +  rankup + ".send-firework", rank.getValue().isSendFirework());
                 }
			}
	}
}
