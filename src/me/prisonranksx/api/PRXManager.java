package me.prisonranksx.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.PrestigeDataHandler;
import me.prisonranksx.data.PrestigeDataStorage;
import me.prisonranksx.data.RankDataHandler;
import me.prisonranksx.data.RankDataStorage;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.data.RebirthDataHandler;
import me.prisonranksx.data.RebirthDataStorage;
import me.prisonranksx.data.XUser;
import me.prisonranksx.error.ErrorInspector;
import me.prisonranksx.gui.GuiListManager;
import me.prisonranksx.hooks.MVdWPapiHook;
import me.prisonranksx.hooks.PapiHook;
import me.prisonranksx.utils.OnlinePlayers;

public class PRXManager {

	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	List<String> emptyList;
	Set<String> emptySet;
	private String defaultPath;
	
	public PRXManager() {
		emptyList = new ArrayList<String>();
		emptySet = new HashSet<String>();
		defaultPath = this.main.globalStorage.getStringData("defaultpath");
	}
	
	/**
	 * 
	 * @param name rank name
	 * @param pathName rank's path
	 * @return null if there is not a previous rank | otherwise: previous rank name
	 */
	public String getPreviousRank(String name, String pathName) {
		List<String> ranksCollection = main.prxAPI.getRanksCollection(pathName);
		int previousRankIndex = ranksCollection.indexOf(name) > 0 ? ranksCollection.indexOf(name) - 1 : -999;
		if(previousRankIndex == -999) {
			return null;
		}
		String previousRank = ranksCollection.get(previousRankIndex);
		return previousRank;
	}
	
	public String getPreviousPrestige(String name) {
		List<String> prestigesCollection = main.prxAPI.getPrestigesCollection();
		int previousPrestigeIndex = prestigesCollection.indexOf(name) > 0 ? prestigesCollection.indexOf(name) - 1 : -999;
		if(previousPrestigeIndex == -999) {
			return null;
		}
		String previousPrestige = prestigesCollection.get(previousPrestigeIndex);
		return previousPrestige;
	}
	
	public String getPreviousRebirth(String name) {
		List<String> rebirthsCollection = main.prxAPI.getRebirthsCollection();
		int previousRebirthIndex = rebirthsCollection.indexOf(name) > 0 ? rebirthsCollection.indexOf(name) - 1 : -999;
		if(previousRebirthIndex == -999) {
			return null;
		}
		String previousRebirth = rebirthsCollection.get(previousRebirthIndex);
		return previousRebirth;
	}
	
	// A , B , C
	// 0 , 1 , 2
	// 1 , 2 , 3
	public String getNextRank(String name, String pathName) {
		List<String> ranksCollection = main.prxAPI.getRanksCollection(pathName);
		int count = ranksCollection.size() - 1; // starting from 0
		int nextRankIndex = ranksCollection.indexOf(name) < count ? ranksCollection.indexOf(name) + 1 : -999;
		if(nextRankIndex == -999) {
			return null;
		}
		String nextRank = ranksCollection.get(nextRankIndex);
		return nextRank;
	}
	
	public String getNextPrestige(String name) {
		List<String> prestigesCollection = main.prxAPI.getPrestigesCollection();
		int count = prestigesCollection.size() - 1; // starting from 0
		int nextPrestigeIndex = prestigesCollection.indexOf(name) < count ? prestigesCollection.indexOf(name) + 1 : -999;
		if(nextPrestigeIndex == -999) {
			return null;
		}
		String nextPrestige = prestigesCollection.get(nextPrestigeIndex);
		return nextPrestige;
	}
	
	public String getNextRebirth(String name) {
		List<String> rebirthsCollection = main.prxAPI.getRebirthsCollection();
		int count = rebirthsCollection.size() - 1;
		int nextRebirthIndex = rebirthsCollection.indexOf(name) < count ? rebirthsCollection.indexOf(name) + 1 : -999;
		if(nextRebirthIndex == -999) {
			return null;
		}
		String nextRebirth = rebirthsCollection.get(nextRebirthIndex);
		return nextRebirth;
	}
	/**
	 * requires main.configManager.saveRanksConfig();
	 * @param name rank name
	 * @param cost rank cost
	 * creates a rank within the default path
	 */
	public void createRank(String name, double cost) {
		RankDataHandler rdh = new RankDataHandler(name, defaultPath);
		//FireworkManager fm = new FireworkManager(name, LevelType.RANK, defaultPath);
		//RankRandomCommands rrc = new RankRandomCommands(name, false, defaultPath, false);
		rdh.setName(name);
		rdh.setCost(cost);
		rdh.setRankupName("LASTRANK");
		rdh.setDisplayName("[" + name + "]");
		rdh.setMsg(emptyList);
		rdh.setBroadcastMessages(emptyList);
		rdh.setRankupCommands(emptyList);
		rdh.setRankupCost(0.0);
		rdh.setRankupDisplayName(null);
		rdh.setPathName(defaultPath);
		rdh.setActionbarInterval(0);
		rdh.setActionbarMessages(emptyList);
		rdh.setActions(emptyList);
		rdh.setAddPermissionList(emptyList);
		rdh.setDelPermissionList(emptyList);
		rdh.setSendFirework(false);
		rdh.setFireworkManager(null);
		rdh.setRandomCommandsManager(null);
		rdh.setAllowPrestige(false);
		RankPath rankPath = new RankPath(name, defaultPath);
        main.rankStorage.putData(rankPath.get(), rdh);
        main.rankStorage.putPathRank(defaultPath, name);
		if(getPreviousRank(name, defaultPath) != null) {
			String prev = getPreviousRank(name, defaultPath);
			String path = prev + "#~#" + defaultPath;
			main.rankStorage.getDataHandler(path).setRankupName(name);
			main.rankStorage.getDataHandler(path).setRankupDisplayName("[" + name + "]");
			main.rankStorage.getDataHandler(path).setRankupCost(cost);
			RankPath rankPath2 = new RankPath(prev, defaultPath);
			main.rankStorage.saveRankData(rankPath2);
		}
		main.rankStorage.saveRankData(rankPath);
		main.globalStorage.getStringMap().put("lastrank", name);
		main.getConfig().set("lastrank", name);
		main.configManager.saveMainConfig();
	}
	
	/**
	 * requires main.configManager.saveRanksConfig();
	 * @param name
	 * @param cost
	 * @param pathName
	 */
	public void createRank(String name, double cost, String pathName) {
		RankDataHandler rdh = new RankDataHandler(name, pathName);
		//FireworkManager fm = new FireworkManager(name, LevelType.RANK, defaultPath);
		//RankRandomCommands rrc = new RankRandomCommands(name, false, defaultPath, false);
		rdh.setName(name);
		rdh.setCost(cost);
		rdh.setRankupName("LASTRANK");
		rdh.setDisplayName("[" + name + "]");
		rdh.setMsg(emptyList);
		rdh.setBroadcastMessages(emptyList);
		rdh.setRankupCommands(emptyList);
		rdh.setRankupCost(0.0);
		rdh.setRankupDisplayName(null);
		rdh.setPathName(pathName);
		rdh.setActionbarInterval(0);
		rdh.setActionbarMessages(emptyList);
		rdh.setActions(emptyList);
		rdh.setAddPermissionList(emptyList);
		rdh.setDelPermissionList(emptyList);
		rdh.setSendFirework(false);
		rdh.setFireworkManager(null);
		rdh.setRandomCommandsManager(null);
		rdh.setAllowPrestige(false);
		RankPath rankPath = new RankPath(name, pathName);
        main.rankStorage.putData(rankPath.get(), rdh);
        main.rankStorage.putPathRank(pathName, name);
		if(getPreviousRank(name, pathName) != null) {
			String prev = getPreviousRank(name, pathName);
			String path = prev + "#~#" + pathName;
			main.rankStorage.getDataHandler(path).setRankupName(name);
			main.rankStorage.getDataHandler(path).setRankupDisplayName("[" + name + "]");
			main.rankStorage.getDataHandler(path).setRankupCost(cost);
			RankPath rankPath2 = new RankPath(prev, pathName);
			main.rankStorage.saveRankData(rankPath2);
		}
		main.rankStorage.saveRankData(rankPath);
		main.globalStorage.getStringMap().put("lastrank", name);
		main.getConfig().set("lastrank", name);
		main.configManager.saveMainConfig();
	}
	
	/**
	 * requires main.configManager.saveRanksConfig();
	 * @param name
	 * @param cost
	 * @param pathName
	 * @param displayName
	 */
	public void createRank(String name, double cost, String pathName, String displayName) {
		RankDataHandler rdh = new RankDataHandler(name, pathName);
		//FireworkManager fm = new FireworkManager(name, LevelType.RANK, defaultPath);
		//RankRandomCommands rrc = new RankRandomCommands(name, false, defaultPath, false);
		rdh.setName(name);
		rdh.setCost(cost);
		rdh.setRankupName("LASTRANK");
		rdh.setDisplayName(displayName);
		rdh.setMsg(emptyList);
		rdh.setBroadcastMessages(emptyList);
		rdh.setRankupCommands(emptyList);
		rdh.setRankupCost(0.0);
		rdh.setRankupDisplayName(null);
		rdh.setPathName(pathName);
		rdh.setActionbarInterval(0);
		rdh.setActionbarMessages(emptyList);
		rdh.setActions(emptyList);
		rdh.setAddPermissionList(emptyList);
		rdh.setDelPermissionList(emptyList);
		rdh.setSendFirework(false);
		rdh.setFireworkManager(null);
		rdh.setRandomCommandsManager(null);
		rdh.setAllowPrestige(false);
		RankPath rankPath = new RankPath(name, pathName);
        main.rankStorage.putData(rankPath.get(), rdh);
        main.rankStorage.putPathRank(pathName, name);
		if(getPreviousRank(name, pathName) != null) {
			String prev = getPreviousRank(name, pathName);
			String path = prev + "#~#" + pathName;
			main.rankStorage.getDataHandler(path).setRankupName(name);
			main.rankStorage.getDataHandler(path).setRankupDisplayName(displayName);
			main.rankStorage.getDataHandler(path).setRankupCost(cost);
			RankPath rankPath2 = new RankPath(prev, pathName);
			main.rankStorage.saveRankData(rankPath2);
		}
		main.rankStorage.saveRankData(rankPath);
		main.globalStorage.getStringMap().put("lastrank", name);
		main.getConfig().set("lastrank", name);
		main.configManager.saveMainConfig();
	}
	
	/**
	 * requires main.configManager.saveRanksConfig();
	 * @param name
	 * @param newCost
	 */
	public void setRankCost(String name, double newCost) {
	    RankPath rp = new RankPath(name, defaultPath);
		if(main.rankStorage.getEntireData().get(rp.get()) == null) {
			// rank doesn't exist
			return;
		}
	    RankDataHandler rdh = main.rankStorage.getDataHandler(rp.get());
	    if(getPreviousRank(name, defaultPath) != null) {
	    String prev = this.getPreviousRank(name, defaultPath);
	    RankPath rp2 = new RankPath(prev, defaultPath);
	    RankDataHandler rdh2 = main.rankStorage.getDataHandler(rp2.get());
	    rdh2.setRankupCost(newCost);
	    main.rankStorage.putData(rp2.get(), rdh2);
		RankPath rankPath2 = new RankPath(prev, defaultPath);
		main.rankStorage.saveRankData(rankPath2);
	    }
	    rdh.setCost(newCost);
	    main.rankStorage.putData(rp.get(), rdh);
	    main.rankStorage.saveRankData(rp);
	}
	
	/**
	 * requires main.configManager.saveRanksConfig();
	 * @param name
	 * @param newCost
	 * @param pathName
	 */
	public void setRankCost(String name, double newCost, String pathName) {
	    RankPath rp = new RankPath(name, pathName);
		if(main.rankStorage.getEntireData().get(rp.get()) == null) {
			// rank doesn't exist
			return;
		}
	    RankDataHandler rdh = main.rankStorage.getDataHandler(rp.get());
	    if(getPreviousRank(name, pathName) != null) {
	    String prev = this.getPreviousRank(name, pathName);
	    RankPath rp2 = new RankPath(prev, pathName);
	    RankDataHandler rdh2 = main.rankStorage.getDataHandler(rp2.get());
	    rdh2.setRankupCost(newCost);
	    main.rankStorage.putData(rp2.get(), rdh2);
		RankPath rankPath2 = new RankPath(prev, pathName);
		main.rankStorage.saveRankData(rankPath2);
	    }
	    rdh.setCost(newCost);
	    main.rankStorage.putData(rp.get(), rdh);
	    main.rankStorage.saveRankData(rp);
	}
	
	/**
	 * requires main.configManager.saveRanksConfig();
	 * @param name
	 * @param newDisplayName
	 */
	public void setRankDisplayName(String name, String newDisplayName) {
	    RankPath rp = new RankPath(name, defaultPath);
		if(main.rankStorage.getEntireData().get(rp.get()) == null) {
			// rank doesn't exist
			return;
		}
	    RankDataHandler rdh = main.rankStorage.getDataHandler(rp.get());
	    if(getPreviousRank(name, defaultPath) != null) {
	    String prev = this.getPreviousRank(name, defaultPath);
	    RankPath rp2 = new RankPath(prev, defaultPath);
	    RankDataHandler rdh2 = main.rankStorage.getDataHandler(rp2.get());
	    rdh2.setRankupDisplayName(newDisplayName);
	    main.rankStorage.putData(rp2.get(), rdh2);
	    main.rankStorage.saveRankData(rp2);
	    }
	    rdh.setDisplayName(newDisplayName);
	    main.rankStorage.putData(rp.get(), rdh);
	    main.rankStorage.saveRankData(rp);
	}
	
	/**
	 * requires main.configManager.saveRanksConfig();
	 * @param name
	 * @param newPathName
	 */
	public void setRankPathName(String name, String newPathName) {
	    RankPath rp = new RankPath(name, defaultPath);
		if(main.rankStorage.getEntireData().get(rp.get()) == null) {
			// rank doesn't exist
			return;
		}
	    RankDataHandler rdh = main.rankStorage.getDataHandler(rp.get());
	    rdh.setPathName(newPathName);
	    main.rankStorage.putData(rp.get(), rdh);
	    main.rankStorage.saveRankData(rp);
	}
	
	public void delRank(String name) {
		if(main.rankStorage.getEntireData().get(name + "#~#" + defaultPath) == null) {
			// rank doesn't exist
			main.debug("doesn't exi-");
			return;
		}
		RankPath rp = new RankPath(name, defaultPath);
		RankDataHandler rdh = main.rankStorage.getDataHandler(rp.get());
		String bRankupName = rdh.getRankupName();
		String bRankupDisplayName = rdh.getRankupDisplayName();
		double bRankupCost = rdh.getRankupCost();
	    if(getPreviousRank(name, defaultPath) != null) {
	    String prev = this.getPreviousRank(name, defaultPath);
	    RankPath rp2 = new RankPath(prev, defaultPath);
	    RankDataHandler rdh2 = main.rankStorage.getDataHandler(rp2.get());
	    rdh2.setRankupName(bRankupName);
	    rdh2.setRankupDisplayName(bRankupDisplayName);
	    rdh2.setRankupCost(bRankupCost);
	    main.configManager.ranksConfig.set("Ranks." + defaultPath + "." + prev + ".nextrank", bRankupName);
	    main.rankStorage.putData(rp2.get(), rdh2);
	    } else { //if it's A (default rank) || there is no previous rank and it's the first rank
	    	String nextRank = getNextRank(name, defaultPath);
	    	main.globalStorage.getStringMap().put("defaultrank", nextRank);
	    	main.getConfig().set("defaultrank", nextRank);
	    }
	    if(getNextRank(name, defaultPath) != null) {
	    	
	    } else {
	    	//if it's Z (last rank) || there is no next ranks and it's the last rank
	    	String previousRank = getPreviousRank(name, defaultPath);
	        RankPath rp3 = new RankPath(previousRank, defaultPath);
	        RankDataHandler rdh3 = main.rankStorage.getDataHandler(rp3.get());
	        rdh3.setRankupName("LASTRANK");
	        rdh3.setRankupDisplayName(null);
	        rdh3.setRankupCost(0.0);
	        main.configManager.ranksConfig.set("Ranks." + defaultPath + "." + previousRank + ".nextrank", "LASTRANK");
	    	main.globalStorage.getStringMap().put("lastrank", previousRank);
	    	main.getConfig().set("lastrank", previousRank);
	    }
		main.rankStorage.removeData(rp.get());
		main.configManager.ranksConfig.set("Ranks." + defaultPath + "." + name, null);
		main.configManager.saveRanksConfig();
		main.configManager.saveMainConfig();
	}
	
	/**
	 * You don't need to save ranks config.
	 * @param name
	 * @param pathName
	 */
	public void delRank(String name, String pathName) {
		if(main.rankStorage.getEntireData().get(name + "#~#" + pathName) == null) {
			// rank doesn't exist
			main.debug("doesn't exi-");
			return;
		}
		RankPath rp = new RankPath(name, pathName);
		RankDataHandler rdh = main.rankStorage.getDataHandler(rp.get());
		String bRankupName = rdh.getRankupName();
		String bRankupDisplayName = rdh.getRankupDisplayName();
		double bRankupCost = rdh.getRankupCost();
	    if(getPreviousRank(name, pathName) != null) {
	    String prev = this.getPreviousRank(name, pathName);
	    RankPath rp2 = new RankPath(prev, pathName);
	    RankDataHandler rdh2 = main.rankStorage.getDataHandler(rp2.get());
	    rdh2.setRankupName(bRankupName);
	    rdh2.setRankupDisplayName(bRankupDisplayName);
	    rdh2.setRankupCost(bRankupCost);
	    main.configManager.ranksConfig.set("Ranks." + pathName + "." + prev + ".nextrank", bRankupName);
	    main.rankStorage.putData(rp2.get(), rdh2);
	    } else { //if it's A (default rank) || there is no previous rank and it's the first rank
	    	String nextRank = getNextRank(name, pathName);
	    	main.globalStorage.getStringMap().put("defaultrank", nextRank);
	    	main.getConfig().set("defaultrank", nextRank);
	    }
	    if(getNextRank(name, pathName) != null) {
	    	
	    } else {
	    	//if it's Z (last rank) || there is no next ranks and it's the last rank
	    	String previousRank = getPreviousRank(name, pathName);
	        RankPath rp3 = new RankPath(previousRank, pathName);
	        RankDataHandler rdh3 = main.rankStorage.getDataHandler(rp3.get());
	        rdh3.setRankupName("LASTRANK");
	        rdh3.setRankupDisplayName(null);
	        rdh3.setRankupCost(0.0);
	        main.configManager.ranksConfig.set("Ranks." + pathName + "." + previousRank + ".nextrank", "LASTRANK");
	    	main.globalStorage.getStringMap().put("lastrank", previousRank);
	    	main.getConfig().set("lastrank", previousRank);
	    }
		main.rankStorage.removeData(rp.get());
		main.configManager.ranksConfig.set("Ranks." + pathName + "." + name, null);
		main.configManager.saveRanksConfig();
		main.configManager.saveMainConfig();
	}
	
	/**
	 * 
	 * @param name rank name
	 * @param save true = save to config | otherwise it will only be saved in the storage
	 */
	public void setDefaultRank(String name, boolean save) {
		main.globalStorage.getStringMap().put("defaultrank", name);
		main.getConfig().set("defaultrank", name);
		if(save) {
		main.configManager.saveMainConfig();
		}
	}
	
	public void setFirstPrestige(String name, boolean save) {
		main.globalStorage.getStringMap().put("firstprestige", name);
		main.getConfig().set("firstprestige", name);
		if(save) {
			main.configManager.saveMainConfig();
		}
	}
	
	public void setFirstRebirth(String name, boolean save) {
		main.globalStorage.getStringMap().put("firstrebirth", name);
		main.getConfig().set("firstrebirth", name);
		if(save) {
			main.configManager.saveMainConfig();
		}
	}
	/**
	 * 
	 * @param name rank name
	 * @param save true = save to config | otherwise it will only be saved in the storage
	 */
	public void setLastRank(String name, boolean save) {
		main.globalStorage.getStringMap().put("lastrank", name);
		main.getConfig().set("lastrank", name);
		if(save) {
		main.configManager.saveMainConfig();
		}
	}
	
	public void setLastPrestige(String name, boolean save) {
		main.globalStorage.getStringMap().put("lastprestige", name);
		main.getConfig().set("lastprestige", name);
		if(save) {
			main.configManager.saveMainConfig();
		}
	}
	
	public void setLastRebirth(String name, boolean save) {
		main.globalStorage.getStringMap().put("lastrebirth", name);
		main.getConfig().set("lastrebirth", name);
		if(save) {
			main.configManager.saveMainConfig();
		}
	}
	
	public void reloadPlayerData() {
	    main.playerStorage.getPlayerData().clear();
	    main.rankStorage.getEntireData().clear();
	    main.prestigeStorage.getPrestigeData().clear();
	    main.rebirthStorage.getRebirthData().clear();
	    main.rankStorage.loadRanksData();
	    main.prestigeStorage.loadPrestigesData();
	    main.rebirthStorage.loadRebirthsData();
	    main.getPlayerStorage().loadPlayersData();
	}
	
	public void reload() {
	main.globalStorage.getDoubleMap().clear();
    main.globalStorage.getStringMap().clear();
	main.globalStorage.getBooleanMap().clear();
    main.globalStorage.getStringListMap().clear();
	main.globalStorage.getIntegerMap().clear();
    main.globalStorage.getStringSetMap().clear();
	main.globalStorage.getGlobalMap().clear();
	  main.configManager.reloadMainConfig();
      main.configManager.reloadConfigs();
      main.configManager.loadConfigs();
      main.globalStorage.loadGlobalData();
      main.rankStorage = new RankDataStorage(main);
      main.rankStorage.loadRanksData();
      main.prestigeStorage = new PrestigeDataStorage(main);
      main.prestigeStorage.loadPrestigesData();
      main.rebirthStorage = new RebirthDataStorage(main);
      main.rebirthStorage.loadRebirthsData();
      if(main.ishooked) {
    	  Bukkit.getScheduler().runTask(main, () -> {
      main.papi = null;
      main.papi = new PapiHook(main);
      main.papi.register();
    	  });
      }
      if(main.isMvdw) {
    	  main.mvdw = null;
    	  main.mvdw = new MVdWPapiHook(main);
    	  main.mvdw.registerPlaceholders();
      }
      main.messagesStorage.loadMessages();
      main.prxAPI = new PRXAPI();
      main.prxAPI.setup();
      main.prxAPI.loadPermissions();
      main.prxAPI.loadProgressBars();
      main.rankupAPI = new Rankup();
      main.ranksAPI = new Ranks();
      main.ranksAPI.load();
      main.rankupMaxAPI = new RankupMax();
      main.prestigeAPI = new Prestige();
      main.prestigesAPI = new Prestiges();
      main.prestigesAPI.load();
      main.rebirthAPI = new Rebirth();
      main.rebirthsAPI = new Rebirths();
      main.rebirthsAPI.load();
      main.cri.getCustomRankItems().clear();
      main.cri.setup();
      main.cpi.getCustomPrestigeItems().clear();
      main.cpi.setup();
      main.crri.getCustomRebirthItems().clear();
      main.crri.setup();
      main.guiManager = new GuiListManager(main);
      main.guiManager.setupConstantItems();
      main.topPrestigesCommand.load();
      main.topRebirthsCommand.load();
      main.getPlayerStorage().loadPlayersData();
      if(!main.isBefore1_7) {
      main.errorInspector = new ErrorInspector(main);
      main.errorInspector.inspect();
      }
	}
	
	public void save() {
		main.rankStorage.saveRanksData();
		main.prestigeStorage.savePrestigesData();
		main.rebirthStorage.saveRebirthsData();
		main.playerStorage.savePlayersData();
		main.configManager.saveMainConfig();
		if(main.isRankEnabled) {
		main.configManager.saveRanksConfig();
		main.configManager.saveRankDataConfig();
		}
		if(main.isPrestigeEnabled) {
		main.configManager.savePrestigesConfig();
		main.configManager.savePrestigeDataConfig();
		}
		if(main.isRebirthEnabled) {
		main.configManager.saveRebirthsConfig();
		main.configManager.saveRebirthDataConfig();
		}
	}
	
	public void createPrestige(String name, double cost) {
	    PrestigeDataHandler pdh = new PrestigeDataHandler(name);
		pdh.setName(name);
		pdh.setCost(cost);
		pdh.setNextPrestigeName("LASTPRESTIGE");
		pdh.setDisplayName("&e[&6" + name + "&e]");
		pdh.setMsg(emptyList);
		pdh.setBroadcastMessages(emptyList);
		pdh.setPrestigeCommands(emptyList);
		pdh.setNextPrestigeCost(0.0);
		pdh.setNextPrestigeDisplayName(null);
		pdh.setRankupCostIncreasePercentage(0.0);
		pdh.setActionbarInterval(0);
		pdh.setActionbarMessages(emptyList);
		pdh.setActions(emptyList);
		pdh.setAddPermissionList(emptyList);
		pdh.setDelPermissionList(emptyList);
		pdh.setSendFirework(false);
		pdh.setFireworkManager(null);
		pdh.setRandomCommandsManager(null);
        main.prestigeStorage.putData(name, pdh);
        main.prestigeStorage.savePrestigeData(name);
		if(getPreviousPrestige(name) != null) {
			String prev = getPreviousPrestige(name);
			main.prestigeStorage.getDataHandler(prev).setNextPrestigeName(name);
			main.prestigeStorage.savePrestigeData(prev);
		}
		main.globalStorage.getStringMap().put("lastprestige", name);
		main.getConfig().set("lastprestige", name);
		main.configManager.saveMainConfig();
	}
	
	public void createPrestige(String name, double cost, String displayName) {
		String namec = name;
	    PrestigeDataHandler pdh = new PrestigeDataHandler(namec);
		pdh.setName(namec);
		pdh.setCost(cost);
		pdh.setNextPrestigeName("LASTPRESTIGE");
		pdh.setDisplayName(displayName);
		pdh.setMsg(emptyList);
		pdh.setBroadcastMessages(emptyList);
		pdh.setPrestigeCommands(emptyList);
		pdh.setNextPrestigeCost(0.0);
		pdh.setNextPrestigeDisplayName(null);
		pdh.setRankupCostIncreasePercentage(0.0);
		pdh.setActionbarInterval(0);
		pdh.setActionbarMessages(emptyList);
		pdh.setActions(emptyList);
		pdh.setAddPermissionList(emptyList);
		pdh.setDelPermissionList(emptyList);
		pdh.setSendFirework(false);
		pdh.setFireworkManager(null);
		pdh.setRandomCommandsManager(null);
        main.prestigeStorage.putData(namec, pdh);
        main.prestigeStorage.savePrestigeData(namec);
		if(getPreviousPrestige(namec) != null) {
			String prev = getPreviousPrestige(namec);
			main.prestigeStorage.getDataHandler(prev).setNextPrestigeName(namec);
			main.prestigeStorage.savePrestigeData(prev);
		}
		main.globalStorage.getStringMap().put("lastprestige", namec);
		main.getConfig().set("lastprestige", namec);
		main.configManager.saveMainConfig();
	}
	
	public void setPrestigeCost(String name, double cost) {
		if(main.prestigeStorage.getPrestigeData().get(name) == null) {
			// prestige doesn't exist
			return;
		}
		PrestigeDataHandler pdh = new PrestigeDataHandler(name);
		pdh.setCost(cost);
		main.prestigeStorage.putData(name, pdh);
		main.prestigeStorage.savePrestigeData(name);
	}
	
	public void setPrestigeDisplayName(String name, String displayName) {
		if(main.prestigeStorage.getPrestigeData().get(name) == null) {
			// prestige doesn't exist
			return;
		}
		PrestigeDataHandler pdh = new PrestigeDataHandler(name);
		pdh.setDisplayName(name);
		main.prestigeStorage.putData(name, pdh);
		main.prestigeStorage.savePrestigeData(name);
	}
	
	public void delPrestige(String name) {
		if(main.prestigeStorage.getPrestigeData().get(name) == null) {
			// prestige doesn't exist
			return;
		}
		main.prestigeStorage.getPrestigeData().remove(name);
		main.configManager.savePrestigesConfig();
	}
	
	public void createRebirth(String name, double cost) {
	    RebirthDataHandler rdh = new RebirthDataHandler(name);
		rdh.setName(name);
		rdh.setCost(cost);
		rdh.setNextRebirthName("LASTREBIRTH");
		rdh.setDisplayName("[" + name + "]");
		rdh.setMsg(emptyList);
		rdh.setBroadcastMessages(emptyList);
		rdh.setRebirthCommands(emptyList);
		rdh.setNextRebirthCost(0.0);
		rdh.setNextRebirthDisplayName(null);
		rdh.setActionbarInterval(0);
		rdh.setActionbarMessages(emptyList);
		rdh.setActions(emptyList);
		rdh.setAddPermissionList(emptyList);
		rdh.setDelPermissionList(emptyList);
		rdh.setSendFirework(false);
		rdh.setFireworkManager(null);
		rdh.setRandomCommandsManager(null);
        main.rebirthStorage.putData(name, rdh);
        main.rebirthStorage.saveRebirthData(name);
        if(getPreviousRebirth(name) != null) {
        	String prev = getPreviousRebirth(name);
        	main.rebirthStorage.getDataHandler(prev).setNextRebirthName(name);
        	main.rebirthStorage.saveRebirthData(prev);
        }
		main.globalStorage.getStringMap().put("lastrebirth", name);
		main.getConfig().set("lastrebirth", name);
		main.configManager.saveMainConfig();
	}
	
	public void createRebirth(String name, double cost, String displayName) {
	    RebirthDataHandler rdh = new RebirthDataHandler(name);
		rdh.setName(name);
		rdh.setCost(cost);
		rdh.setNextRebirthName("LASTREBIRTH");
		rdh.setDisplayName(displayName);
		rdh.setMsg(emptyList);
		rdh.setBroadcastMessages(emptyList);
		rdh.setRebirthCommands(emptyList);
		rdh.setNextRebirthCost(0.0);
		rdh.setNextRebirthDisplayName(null);
		rdh.setActionbarInterval(0);
		rdh.setActionbarMessages(emptyList);
		rdh.setActions(emptyList);
		rdh.setAddPermissionList(emptyList);
		rdh.setDelPermissionList(emptyList);
		rdh.setSendFirework(false);
		rdh.setFireworkManager(null);
		rdh.setRandomCommandsManager(null);
        main.rebirthStorage.putData(name, rdh);
        main.rebirthStorage.saveRebirthData(name);
        if(getPreviousRebirth(name) != null) {
        	String prev = getPreviousRebirth(name);
        	main.rebirthStorage.getDataHandler(prev).setNextRebirthName(name);
        	main.rebirthStorage.saveRebirthData(prev);
        }
		main.globalStorage.getStringMap().put("lastrebirth", name);
		main.getConfig().set("lastrebirth", name);
		main.configManager.saveMainConfig();
	}
	
	public void setRebirthCost(String name, double cost) {
		if(main.rebirthStorage.getRebirthData().get(name) == null) {
			// rebirth doesn't exist
			return;
		}
		RebirthDataHandler rdh = new RebirthDataHandler(name);
		rdh.setCost(cost);
		main.rebirthStorage.putData(name, rdh);
		main.rebirthStorage.saveRebirthData(name);
	}
	
	public void setRebirthDisplayName(String name, String displayName) {
		if(main.rebirthStorage.getRebirthData().get(name) == null) {
			// rebirth doesn't exist
			return;
		}
		RebirthDataHandler rdh = new RebirthDataHandler(name);
		rdh.setDisplayName(displayName);
		main.rebirthStorage.putData(name, rdh);
		main.rebirthStorage.saveRebirthData(name);
	}
	
	/**
	 * Don't use
	 * @param name
	 */
	public void delRebirth(String name) {
		if(main.rebirthStorage.getRebirthData().get(name) == null) {
			// rebirth doesn't exist
			return;
		}
		main.rebirthStorage.rebirthData.remove(name);
		main.rebirthStorage.saveRebirthData(name);
	}
	
	public void delPlayerPrestige(XUser user) {
		main.playerStorage.getPlayerData().get(user.getUUID().toString()).setPrestige(null);
	}
	
	public void delPlayerRebirth(XUser user) {
		main.playerStorage.getPlayerData().get(user.getUUID().toString()).setRebirth(null);
	}
	
	/**
	 * 
	 * @param rankName in any case
	 * @return rank name in the correct case
	 */
	public String matchRank(String rankName) {
		String matchedRank = rankName;
		for(String str : main.prxAPI.getRanksCollection(defaultPath)) {
			if(str.equalsIgnoreCase(rankName)) {
				matchedRank = str;
			}
		}
		return matchedRank;
	}
	
	/**
	 * 
	 * @param rankName in any case
	 * @return rank name in the correct case
	 */
	public String matchRank(String rankName, String pathName) {
		String matchedRank = rankName;
		for(String str : main.prxAPI.getRanksCollection(pathName)) {
			if(str.equalsIgnoreCase(rankName)) {
				matchedRank = str;
			}
		}
		return matchedRank;
	}
	
	/**
	 * 
	 * @param prestigeName in any case
	 * @return prestige name in the correct case
	 */
	public String matchPrestige(String prestigeName) {
		String matchedPrestige = prestigeName;
		for(String str : main.prxAPI.getPrestigesCollection()) {
			if(str.equalsIgnoreCase(prestigeName)) {
				matchedPrestige = str;
			}
		}
		return matchedPrestige;
	}
	
	public String matchRebirth(String rebirthName) {
		String matchedRebirth = rebirthName;
		for(String str : main.prxAPI.getRebirthsCollection()) {
			if(str.equalsIgnoreCase(rebirthName)) {
				matchedRebirth = str;
			}
		}
		return matchedRebirth;
	}
	
	public String matchPlayerName(String playerName) {
		String matchedName = playerName;
		for(Player players : OnlinePlayers.getEveryPlayer()) {
			if(players.getName().equalsIgnoreCase(playerName)) {
				matchedName = players.getName();
			}
		}
		return matchedName;
	}
	
	public Player matchPlayer(String playerName) {
		String matchedName = playerName;
		for(Player players : OnlinePlayers.getEveryPlayer()) {
			if(players.getName().equalsIgnoreCase(playerName)) {
				matchedName = players.getName();
			}
		}
		return Bukkit.getPlayer(matchedName);
	}
	
	public String matchPath(String path) {
		String matchedPath = path;
		for(String paths : main.rankStorage.getEntireData().keySet()) {
			String pathSplit = paths.split("#~#")[0];
			if(pathSplit.equalsIgnoreCase(path)) {
			matchedPath = pathSplit;
			}
		}
		return matchedPath;
	}
	
	/**
	 * 
	 * @param rank1
	 * @param rank2
	 * @return rankup commands between two ranks
	 * @example rank1: B rank2: F
	 * @itwillreturn the rankup commands of B, C, D, E, and F
	 */
	public List<String> getRankupCommandsBetween(String rank1, String rank2) {
	 List<String> cleanList = main.prxAPI.getRanksCollection();
	 List<String> editedList = new ArrayList<>(cleanList);
	 List<String> rankupCommands = new ArrayList<>();
	 for(int i = cleanList.indexOf(rank1) - 1 ; i > -1 ; i--) {
		 editedList.remove(i);
	 }
	 for(int i2 = cleanList.indexOf(rank2) + 1; i2 < cleanList.size() - 1; i2++) {
		 editedList.remove(i2);
	 }
	 for(String rank : editedList) {
		 main.rankStorage.getRankupCommands(new RankPath(rank, defaultPath)).forEach(command -> {
			 rankupCommands.add(command.replace("%rankup%", rank));
		 });
	 }
	 return rankupCommands;
	}
}
