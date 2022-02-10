package me.prisonranksx.leaderboard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.PlayerDataHandler;
import me.prisonranksx.data.PlayerDataStorage.PlayerDataType;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.data.XUser;

public class LeaderboardManager {

	private PrisonRanksX main;
	private List<Entry<UUID, Integer>> list;
	private List<Entry<UUID, Integer>> listp;
	private List<Entry<UUID, Integer>> listr;
	private List<Entry<UUID, Integer>> listGlobal;
	private List<UUID> players;
	private List<UUID> playersp;
	private List<UUID> playersr;
	private List<UUID> playersGlobal;
	private Map<UUID, Integer> updatedValues;
	private Map<UUID, Integer> updatedValuesP;
	private Map<UUID, Integer> updatedValuesR;
	private Map<UUID, Integer> updatedValuesGlobal;
	private Map<UUID, RankPath> rankMySQL;
	private Map<UUID, String> prestigeMySQL;
	private Map<UUID, String> rebirthMySQL;
	private Map<UUID, PlayerDataHandler> globalMySQL;
	private boolean update;
	public boolean isMYSQL;
	
	public LeaderboardManager(PrisonRanksX main) {
		this.main = main;
		isMYSQL = main.isMySql();
		if(!main.getGlobalStorage().getBooleanData("Options.enable-leaderboard")) {
			return;
		}
		list = Collections.synchronizedList(new LinkedList<>());
		listp = Collections.synchronizedList(new LinkedList<>());
		listr = Collections.synchronizedList(new LinkedList<>());
		listGlobal = Collections.synchronizedList(new LinkedList<>());
		players = Collections.synchronizedList(new LinkedList<>());
		playersp = Collections.synchronizedList(new LinkedList<>());
		playersr = Collections.synchronizedList(new LinkedList<>());
		playersGlobal = Collections.synchronizedList(new LinkedList<>());
		updatedValues = Collections.synchronizedMap(new LinkedHashMap<>());
		updatedValuesP = Collections.synchronizedMap(new LinkedHashMap<>());
		updatedValuesR = Collections.synchronizedMap(new LinkedHashMap<>());
		updatedValuesGlobal = Collections.synchronizedMap(new LinkedHashMap<>());
		rankMySQL = Collections.synchronizedMap(new LinkedHashMap<>());
	    prestigeMySQL = Collections.synchronizedMap(new LinkedHashMap<>());
		rebirthMySQL = Collections.synchronizedMap(new LinkedHashMap<>());
		globalMySQL = Collections.synchronizedMap(new LinkedHashMap<>());
		clearUpdatedValues();
		setUpdate(true);
		getPrestigeLeaderboard();
		getRankLeaderboard();
		getRebirthLeaderboard();
		getGlobalLeaderboard();
		Bukkit.getScheduler().runTaskTimerAsynchronously(this.main, () -> {
			clearUpdatedValues();
			setUpdate(true);
			getPrestigeLeaderboard();
			getRankLeaderboard();
			getRebirthLeaderboard();
			getGlobalLeaderboard();
		}, 20 * 60, 20 * 60);
	}
	
	private boolean indexExists(final List<?> list, final int index) {
		if(index >= list.size() || index < 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param position
	 * @return an entry of player uuid and prestige number integer. 
	 *  @null  null if there is not a player in that position || no player joined to take this position.
	 */
	public Entry<UUID, Integer> getPlayerFromPositionPrestige(final int position) {
		return indexExists(listp, position - 1) ? listp.get(position - 1) : null;
	}
	
	/**
	 * 
	 * @param position
	 * @return an entry of player uuid and rank number integer. 
	 *  @null  null if there is not a player in that position || no player joined to take this position.
	 */
	public Entry<UUID, Integer> getPlayerFromPositionRank(final int position) {
		main.debug(list);
		return indexExists(list, position - 1) ? list.get(position - 1) : null;
	}
	
	public Entry<UUID, Integer> getPlayerFromPositionGlobal(final int position) {
		main.debug(listGlobal);
		return indexExists(listGlobal, position - 1) ? listGlobal.get(position - 1) : null;
	}
	
	/**
	 * 
	 * @param position
	 * @return player name
	 *  @String  fallback if there is not a player in that position || no player joined to take this position.
	 */
	public String getPlayerNameFromPositionPrestige(final int position, final String fallback) {
       if(getPlayerFromPositionPrestige(position) == null) {
    	   return fallback;
       }
       return getPlayerNameFromUUID(getPlayerFromPositionPrestige(position).getKey());
	}
	
	@Deprecated
	public String getPlayerValueFromPositionPrestige(final int position, final String fallback) {
		if(getPlayerFromPositionPrestige(position) == null) {
			return fallback;
		}
		return main.prxAPI.getPlayerPrestige(getPlayerFromPositionPrestige(position).getKey());
	}
	
	public String getPlayerNameFromPositionRank(final int position, final String fallback) {
		if(getPlayerFromPositionRank(position) == null) {
			return fallback;
		}
		return getPlayerNameFromUUID(getPlayerFromPositionRank(position).getKey());
	}
	
	public String getPlayerNameFromPositionGlobal(final int position, final String fallback) {
		if(getPlayerFromPositionGlobal(position) == null) {
			return fallback;
		}
		return getPlayerNameFromUUID(getPlayerFromPositionGlobal(position).getKey());
	}
	
	@Deprecated
	public String getPlayerValueFromPositionRank(final int position, final String fallback) {
		if(getPlayerFromPositionRank(position) == null) {
			return fallback;
		}
		return main.prxAPI.getPlayerRank(getPlayerFromPositionRank(position).getKey());
	}
	
	/**
	 * 
	 * @param position
	 * @param fallback
	 * @return player's prestige
	 * @String returns String fallback if there is no player took that position.
	 */
	public String getPlayerPrestigeFromPosition(final int position, final String fallback) {
		Entry<UUID, Integer> entry = getPlayerFromPositionPrestige(position);
		if(entry == null) {
			return fallback;
		}
		UUID u = entry.getKey();
		String prestige = isMYSQL ? prestigeMySQL.get(u) : main.prxAPI.getPlayerPrestige(u);
		return prestige == null ? fallback : prestige;
	}
	
	public String getPlayerPrestigeDisplayNameFromPosition(final int position, final String fallback) {
		Entry<UUID, Integer> entry = getPlayerFromPositionPrestige(position);
		if(entry == null) {
			return fallback;
		}
		UUID u = entry.getKey();
		String prestigeDisplayName = isMYSQL ? main.prxAPI.getPrestigeDisplay(prestigeMySQL.get(u)) : main.prxAPI.getPlayerPrestigeDisplay(u);
		return prestigeDisplayName == null ? fallback : prestigeDisplayName;
	}
	
	public String getPlayerStageFromPosition(final int position, final String fallback) {
		if(getPlayerFromPositionGlobal(position) == null) {
			return fallback;
		}
		String stage = main.prxAPI.getStageName(getPlayerFromPositionGlobal(position).getKey(), " ");
		return stage == null ? fallback : stage;
	}
	
	public String getPlayerStageDisplayNameFromPosition(final int position, final String fallback) {
		Entry<UUID, Integer> entry = getPlayerFromPositionGlobal(position);
		if(entry == null) {
			return fallback;
		}
		UUID u = entry.getKey();
		PlayerDataHandler pdh = globalMySQL.get(u);
		String stageDisplayName = isMYSQL ? main.prxAPI.organizeStageDisplay(pdh.getRankPath().getRankName(), pdh.getRankPath().getPathName(), pdh.getPrestige(), pdh.getRebirth(), " ", true): main.prxAPI.getStageDisplay(u, " ", true);
		return stageDisplayName == null ? fallback : stageDisplayName;
	}
	
	/**
	 * 
	 * @param position
	 * @param fallback
	 * @return player's rank
	 * @String returns String fallback if there is no player took that position.
	 */
	public String getPlayerRankFromPosition(final int position, final String fallback) {
		Entry<UUID, Integer> entry = getPlayerFromPositionRank(position);
		if(entry == null) {
			return fallback;
		}
		UUID u = entry.getKey();
		String rank = isMYSQL ? rankMySQL.get(u).getRankName() : main.prxAPI.getPlayerRank(u);
		return rank == null ? fallback : rank;
	}
	
	public String getPlayerRankDisplayNameFromPosition(final int position, final String fallback) {
		Entry<UUID, Integer> entry = getPlayerFromPositionRank(position);
		if(entry == null) {
			return fallback;
		}
		UUID u = entry.getKey();
		String rankDisplayName = isMYSQL ? main.prxAPI.getRankDisplay(rankMySQL.get(u)) : main.prxAPI.getPlayerRankDisplay(u);
		return rankDisplayName == null ? fallback : rankDisplayName;
	}
	
	public Entry<UUID, Integer> getPlayerFromPositionRebirth(final int position) {
		return indexExists(listr, position - 1) ? listr.get(position - 1) : null;
	}
	
	public String getPlayerRebirthFromPosition(final int position, final String fallback) {
		Entry<UUID, Integer> entry = getPlayerFromPositionRebirth(position);
		if(entry == null) {
			return fallback;
		}
		UUID u = entry.getKey();
		String rebirth = isMYSQL ? rebirthMySQL.get(u) : main.prxAPI.getPlayerRebirth(u);
		return rebirth == null ? fallback : rebirth;
	}
	
	public String getPlayerRebirthDisplayNameFromPosition(final int position, final String fallback) {
		if(getPlayerFromPositionRebirth(position) == null) {
			return fallback;
		}
		String rebirthDisplayName = main.prxAPI.getPlayerRebirthDisplay(getPlayerFromPositionRebirth(position).getKey());
		return rebirthDisplayName == null ? fallback : rebirthDisplayName;
	}
	
	public String getPlayerNameFromPositionRebirth(final int position, final String fallback) {
	       if(getPlayerFromPositionRebirth(position) == null) {
	    	   return fallback;
	       }
	       return getPlayerNameFromUUID(getPlayerFromPositionRebirth(position).getKey());
		}
	
	public String getPlayerNameFromUUID(UUID uuid) {
		if(!main.getPlayerStorage().isRegistered(uuid)) {
			return Bukkit.getOfflinePlayer(uuid).getName();
		}
		
		return main.getPlayerStorage().getPlayerData().get(uuid.toString()).getName();
	}
	
	public int getPlayerPrestigePosition(final OfflinePlayer player) {
		playersp.clear();
		playersp.addAll(getPrestigeLeaderboard().keySet());
		return playersp.indexOf(player.getUniqueId()) + 1;
	}
	
	public int getPlayerRebirthPosition(final OfflinePlayer player) {
		playersr.clear();
		playersr.addAll(getRebirthLeaderboard().keySet());
		return playersr.indexOf(player.getUniqueId()) + 1;
	}
	
	public int getPlayerRankPosition(final OfflinePlayer player) {
		players.clear();
		players.addAll(getRankLeaderboard().keySet());
		return players.indexOf(player.getUniqueId()) + 1;
	}
	
	public int getPlayerGlobalPosition(final OfflinePlayer player) {
		playersGlobal.clear();
		playersGlobal.addAll(getGlobalLeaderboard().keySet());
		return playersGlobal.indexOf(player.getUniqueId()) + 1;
	}
	
	public int getPlayerRankValue(final OfflinePlayer player) {
		return getRankLeaderboard().get(player.getUniqueId());
	}
	
	public int getPlayerPrestigeValue(final OfflinePlayer player) {
		return getPrestigeLeaderboard().get(player.getUniqueId());
	}
	
	public int getPlayerRebirthValue(final OfflinePlayer player) {
		return getRebirthLeaderboard().get(player.getUniqueId());
	}
	
	public int getPlayerGlobalValue(final OfflinePlayer player) {
		return getGlobalLeaderboard().get(player.getUniqueId());
	}
	
	public synchronized Map<UUID, Integer> getRankLeaderboard() {
		if(!update && !updatedValues.isEmpty()) {
			return updatedValues;
		}
		main.getTaskChainFactory().newSharedChain("dataSave").current(() -> {
		updatedValues.clear();
		main.getPlayerStorage().storePlayersData(PlayerDataType.RANK);
		if(main.isMySql()) {
			String sql = "SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " ORDER BY `rankscore` DESC LIMIT 100;";
			Statement statement = null;
			try {
				statement = main.getConnection().createStatement();
				ResultSet result = statement.executeQuery(sql);
				while(result.next()) {
					String uuid = result.getString("uuid");
					int rankScore = result.getInt("rankscore");
					String rank = result.getString("rank");
					String path = result.getString("path");
					UUID u = UUID.fromString(uuid);
					updatedValues.put(u, rankScore);
					RankPath rp = new RankPath(rank, path);
					rankMySQL.put(u, rp);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				statement.close();
			} catch (SQLException e) {
				
			}
			
		} else {
		ConfigurationSection cf = main.getConfigManager().rankDataConfig.getConfigurationSection("players");
		cf.getValues(false)
		  .entrySet()
		  .stream()
		  .sorted((a1, a2) -> {
			MemorySection uuidMemory1 = null;
			uuidMemory1 = (MemorySection) a1.getValue();
			MemorySection uuidMemory2 = null;
			uuidMemory2 = (MemorySection) a2.getValue();
			int number1 = 0; 
			int number2 = 0;
			String path1 = uuidMemory1.getString("path");
			String path2 = uuidMemory2.getString("path");
			path1 = path1 == null ? main.prxAPI.getDefaultPath() : path1;
			path2 = path2 == null ? main.prxAPI.getDefaultPath() : path2;
		    number1 = Integer.valueOf(main.prxAPI.getRankNumber(path1, uuidMemory1.getString("rank")));
		    number2 = Integer.valueOf(main.prxAPI.getRankNumber(path2, uuidMemory2.getString("rank")));
		    return number2 - number1;
		  })
		  .limit(25)
		  .forEach(f -> {
			UUID uuid = UUID.fromString(f.getKey());
			MemorySection value = (MemorySection)f.getValue();
			int finalNumber = Integer.valueOf(main.prxAPI.getRankNumber(value.getString("path"), value.getString("rank")));
		    updatedValues.put(uuid, finalNumber);
		  });
		}
	   update = false;
	   list.clear();
	   list.addAll(updatedValues.entrySet());
		}).execute();
	   return updatedValues;
	}
	
	public synchronized Map<UUID, Integer> getPrestigeLeaderboard() {
		if(!update && !updatedValuesP.isEmpty()) {
			return updatedValuesP;
		}
		main.getTaskChainFactory().newSharedChain("dataSave").current(() -> {
		updatedValuesP.clear();
		main.getPlayerStorage().storePlayersData(PlayerDataType.PRESTIGE);
		if(main.isMySql()) {
			String sql = "SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " ORDER BY `prestigescore` DESC LIMIT 100;";
			Statement statement = null;
			try {
				statement = main.getConnection().createStatement();
				ResultSet result = statement.executeQuery(sql);
				while(result.next()) {
					String uuid = result.getString("uuid");
					int prestigeScore = result.getInt("prestigescore");
					String prestige = result.getString("prestige");
					UUID u = UUID.fromString(uuid);
					updatedValuesP.put(u, prestigeScore);
					prestigeMySQL.put(u, prestige);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		} else {
		ConfigurationSection cf = main.getConfigManager().prestigeDataConfig.getConfigurationSection("players");
		cf.getValues(false)
		  .entrySet()
		  .stream()
		  .sorted((a1, a2) -> {
			String value1 = (String)a1.getValue();
			String value2 = (String)a2.getValue();
			int number1 = 0; 
			int number2 = 0;
		    number1 = Integer.valueOf(main.prxAPI.getPrestigeNumber(value1));
		    number2 = Integer.valueOf(main.prxAPI.getPrestigeNumber(value2));
		    return number2 - number1;
		  })
		  .limit(25)
		  .forEach(f -> {
			UUID uuid = UUID.fromString(f.getKey());
			String value = (String)f.getValue();
			int finalNumber = Integer.valueOf(main.prxAPI.getPrestigeNumber(value));
		    updatedValuesP.put(uuid, finalNumber);
		  });
		}
	   update = false;
	   listp.clear();
	   listp.addAll(updatedValuesP.entrySet());
		}).execute();
	   return updatedValuesP;
	}
	
	public synchronized Map<UUID, Integer> getRebirthLeaderboard() {
		if(!update && !updatedValuesR.isEmpty()) {
			return updatedValuesR;
		}
		main.getTaskChainFactory().newSharedChain("dataSave").current(() -> {
		updatedValuesR.clear();
		main.getPlayerStorage().storePlayersData(PlayerDataType.REBIRTH);
		if(main.isMySql()) {
			String sql = "SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " ORDER BY `rebirthscore` DESC LIMIT 100;";
			Statement statement = null;
			try {
				statement = main.getConnection().createStatement();
				ResultSet result = statement.executeQuery(sql);
				while(result.next()) {
					String uuid = result.getString("uuid");
					int rebirthScore = result.getInt("rebirthscore");
					String rebirth = result.getString("rebirth");
					UUID u = UUID.fromString(uuid);
					updatedValuesR.put(UUID.fromString(uuid), rebirthScore);
					rebirthMySQL.put(u, rebirth);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				statement.close();
			} catch (SQLException e) {
				
			}
			
		} else {
		ConfigurationSection cf = main.getConfigManager().rebirthDataConfig.getConfigurationSection("players");
		cf.getValues(false)
		  .entrySet()
		  .stream()
		  .sorted((a1, a2) -> {
			String value1 = (String)a1.getValue();
			String value2 = (String)a2.getValue();
			int number1 = 0; 
			int number2 = 0;
		    number1 = Integer.valueOf(main.prxAPI.getRebirthNumber(value1));
		    number2 = Integer.valueOf(main.prxAPI.getRebirthNumber(value2));
		    return number2 - number1;
		  })
		  .limit(25)
		  .forEach(f -> {
			UUID uuid = UUID.fromString(f.getKey());
			String value = (String)f.getValue();
			int finalNumber = Integer.valueOf(main.prxAPI.getRebirthNumber(value));
		    updatedValuesR.put(uuid, finalNumber);
		  });
		}
	   update = false;
	   listr.clear();
	   listr.addAll(updatedValuesR.entrySet());
		}).execute();
	   return updatedValuesR;
	}
	
	public synchronized Map<UUID, Integer> getGlobalLeaderboard() {
		if(!update && !updatedValuesGlobal.isEmpty()) {
			return updatedValuesGlobal;
		}
		main.getTaskChainFactory().newSharedChain("dataSave").current(() -> {
		updatedValuesGlobal.clear();
		main.getPlayerStorage().storePlayersData(PlayerDataType.ALL);
		if(main.isMySql()) {
			String sql = "SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " ORDER BY `stagescore` DESC LIMIT 50;";
			Statement statement = null;
			try {
				statement = main.getConnection().createStatement();
				ResultSet result = statement.executeQuery(sql);
				while(result.next()) {
					String uuid = result.getString("uuid");
					int stageScore = result.getInt("stagescore");					
					main.debug("fetched mysql stagedata: " + uuid + "||" + stageScore);
					UUID u = UUID.fromString(uuid);
					PlayerDataHandler pdh = new PlayerDataHandler(XUser.getXUser(u));
					String name = result.getString("name");
					String rank = result.getString("rank");
					String path = result.getString("path");
					String prestige = result.getString("prestige");
					prestige = prestige.equals("none") ? null : prestige;
					String rebirth = result.getString("rebirth");
					rebirth = rebirth.equals("none") ? null : rebirth;
					pdh.setUUID(u);
                    pdh.setRankPath(RankPath.getRankPath(rank, path));
                    pdh.setPrestige(prestige);
                    pdh.setRebirth(rebirth);
                    pdh.setName(name);
					updatedValuesGlobal.put(u, stageScore);
					globalMySQL.put(u, pdh);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				statement.close();
			} catch (SQLException e) {
				
			}
			
		} else {
			
		ConfigurationSection cf = main.getConfigManager().rankDataConfig.getConfigurationSection("players");
		Map<UUID, Integer> temp = new HashMap<>();
		cf.getValues(false).entrySet().forEach(entry -> {
			UUID u = UUID.fromString(entry.getKey());
			temp.put(u, main.prxAPI.getPlayerPromotionsAmount(u));
		});
		updatedValuesGlobal = sortByValue(temp);
		/*cf.getValues(false)
		.entrySet()
		  .stream()
		  .sorted((a1, a2) -> {
			MemorySection uuidMemory1 = null;
			uuidMemory1 = (MemorySection) a1.getValue();
			MemorySection uuidMemory2 = null;
			uuidMemory2 = (MemorySection) a2.getValue();
			main.debug(uuidMemory1.getName());
			main.debug(uuidMemory2.getName());
			int number1 = 0; 
			int number2 = 0;
		    number1 = Integer.valueOf(main.prxAPI.getPlayerPromotionsAmount(UUID.fromString(uuidMemory1.getName())));
		    number2 = Integer.valueOf(main.prxAPI.getPlayerPromotionsAmount(UUID.fromString(uuidMemory2.getName())));
		    return number2 - number1;
		  })
		  .limit(25)
		  .forEach(f -> {
			UUID uuid = UUID.fromString(f.getKey());
			int finalNumber = Integer.valueOf(main.prxAPI.getPlayerPromotionsAmount(uuid));
		    updatedValuesGlobal.put(uuid, finalNumber);
		  });
		}
		*/
		}
	   update = false;
	   listGlobal.clear();
	   listGlobal.addAll(updatedValuesGlobal.entrySet());
		}).execute();
	   return updatedValuesGlobal;
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}
	
	public void clearUpdatedValues() {
		this.updatedValues.clear();
		this.updatedValuesP.clear();
		this.updatedValuesR.clear();
		this.updatedValuesGlobal.clear();
	}
	
	private Map<UUID, Integer> sortByValue(Map<UUID, Integer> unsortMap) {

	    // 1. Convert Map to List of Map
	    List<Map.Entry<UUID, Integer>> list =
	            new LinkedList<Map.Entry<UUID, Integer>>(unsortMap.entrySet());

	    // 2. Sort list with Collections.sort(), provide a custom Comparator
	    //    Try switch the o1 o2 position for a different order
	    Collections.sort(list, new Comparator<Map.Entry<UUID, Integer>>() {
	        public int compare(Map.Entry<UUID, Integer> o1,
	                           Map.Entry<UUID, Integer> o2) {
	            return (o1.getValue()).compareTo(o2.getValue());
	        }
	    });

	    // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
	    Map<UUID, Integer> sortedMap = new LinkedHashMap<UUID, Integer>();
	    for (Map.Entry<UUID, Integer> entry : list) {
	        sortedMap.put(entry.getKey(), entry.getValue());
	    }

	    /*
	    //classic iterator example
	    for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
	        Map.Entry<String, Integer> entry = it.next();
	        sortedMap.put(entry.getKey(), entry.getValue());
	    }*/


	    return sortedMap;
	}
	
}
