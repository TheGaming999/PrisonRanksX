package me.prisonranksx.leaderboard;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.PlayerDataStorage.PlayerDataType;
import me.prisonranksx.utils.MySqlStreamer;

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
	private boolean update;
	
	public LeaderboardManager(PrisonRanksX main) {
		this.main = main;
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
		setUpdate(true);
		getRankLeaderboard();
		getPrestigeLeaderboard();
		getRebirthLeaderboard();
		getGlobalLeaderboard();
		Bukkit.getScheduler().runTaskTimerAsynchronously(this.main, () -> {
			setUpdate(true);
			getRankLeaderboard();
			getPrestigeLeaderboard();
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
		if(getPlayerFromPositionPrestige(position) == null) {
			return fallback;
		}
		String prestige = main.prxAPI.getPlayerPrestige(getPlayerFromPositionPrestige(position).getKey());
		return prestige == null ? fallback : prestige;
	}
	
	public String getPlayerPrestigeDisplayNameFromPosition(final int position, final String fallback) {
		if(getPlayerFromPositionPrestige(position) == null) {
			return fallback;
		}
		String prestigeDisplayName = main.prxAPI.getPlayerPrestigeDisplay(getPlayerFromPositionPrestige(position).getKey());
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
		if(getPlayerFromPositionGlobal(position) == null) {
			return fallback;
		}
		String stageDisplayName = main.prxAPI.getStageDisplay(getPlayerFromPositionGlobal(position).getKey(), " ", true);
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
		if(getPlayerFromPositionRank(position) == null) {
			return fallback;
		}
		String rank = main.prxAPI.getPlayerRank(getPlayerFromPositionRank(position).getKey());
		return rank == null ? fallback : rank;
	}
	
	public String getPlayerRankDisplayNameFromPosition(final int position, final String fallback) {
		if(getPlayerFromPositionRank(position) == null) {
			return fallback;
		}
		String rankDisplayName = main.prxAPI.getPlayerRankDisplay(getPlayerFromPositionRank(position).getKey());
		return rankDisplayName == null ? fallback : rankDisplayName;
	}
	
	public Entry<UUID, Integer> getPlayerFromPositionRebirth(final int position) {
		return indexExists(listr, position - 1) ? listr.get(position - 1) : null;
	}
	
	public String getPlayerRebirthFromPosition(final int position, final String fallback) {
		if(getPlayerFromPositionRebirth(position) == null) {
			return fallback;
		}
		String rebirth = main.prxAPI.getPlayerRebirth(getPlayerFromPositionRebirth(position).getKey());
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
	
	public Map<UUID, Integer> getRankLeaderboard() {
		if(!update && !updatedValues.isEmpty()) {
			return updatedValues;
		}
		updatedValues.clear();
		main.getPlayerStorage().storePlayersData(PlayerDataType.RANK);
		if(main.isMySql()) {
			String sql = "SELECT * FROM " + main.getDatabase() + "." + main.getTable();
			MySqlStreamer mySqlStreamer = new MySqlStreamer(main.getConnection());
			try {
				mySqlStreamer.streamQuery(sql)
				.sorted((a1, a2) -> {
					String rank1 = (String)a1.get("rank");
					String path1 = (String)a1.get("path");
					String rank2 = (String)a2.get("rank");
					String path2 = (String)a2.get("path");
					int number1 = Integer.valueOf(main.prxAPI.getRankNumber(path1, rank1));
					int number2 = Integer.valueOf(main.prxAPI.getRankNumber(path2, rank2));
					return number2 - number1;
				})
				.limit(25)
				.forEach(f -> {
					UUID uuid = UUID.fromString((String)f.get("uuid"));
					int finalNumber = Integer.valueOf(main.prxAPI.getRankNumber((String)f.get("path"), (String)f.get("rank")));
					updatedValues.put(uuid, finalNumber);
				});
			} catch (SQLException e) {
				e.printStackTrace();
			}
			mySqlStreamer.getStreamQuery().closeStatement();
			return updatedValues;
		}
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
	   update = false;
	   list.clear();
	   list.addAll(updatedValues.entrySet());
	   return updatedValues;
	}
	
	public Map<UUID, Integer> getPrestigeLeaderboard() {
		if(!update && !updatedValuesP.isEmpty()) {
			return updatedValuesP;
		}
		updatedValuesP.clear();
		main.getPlayerStorage().storePlayersData(PlayerDataType.PRESTIGE);
		if(main.isMySql()) {
			String sql = "SELECT * FROM " + main.getDatabase() + "." + main.getTable();
			MySqlStreamer mySqlStreamer = new MySqlStreamer(main.getConnection());
			try {
				mySqlStreamer.streamQuery(sql)
				.sorted((a1, a2) -> {
					String prestige1 = (String)a1.get("prestige");
					String prestige2 = (String)a2.get("prestige");
					int number1 = Integer.valueOf(main.prxAPI.getPrestigeNumber(prestige1));
					int number2 = Integer.valueOf(main.prxAPI.getPrestigeNumber(prestige2));
					return number2 - number1;
				})
				.limit(25)
				.forEach(f -> {
					UUID uuid = UUID.fromString((String)f.get("uuid"));
					int finalNumber = Integer.valueOf(main.prxAPI.getPrestigeNumber((String)f.get("prestige")));
					updatedValuesP.put(uuid, finalNumber);
				});
			} catch (SQLException e) {
				e.printStackTrace();
			}
			mySqlStreamer.getStreamQuery().closeStatement();
			return updatedValuesP;
		}
		ConfigurationSection cf = main.getConfigManager().prestigeDataConfig.getConfigurationSection("players");
		cf.getValues(false)
		  .entrySet()
		  .stream()
		  .sorted((a1, a2) -> {
			String value1 = (String)a1.getValue();
			String value2 = (String)a2.getValue();
			main.debug("prestige value: " + value1);
			main.debug("prestige key: " + a1.getKey());
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
	   update = false;
	   listp.clear();
	   listp.addAll(updatedValuesP.entrySet());
	   return updatedValuesP;
	}
	
	public Map<UUID, Integer> getRebirthLeaderboard() {
		if(!update && !updatedValuesR.isEmpty()) {
			return updatedValuesR;
		}
		updatedValuesR.clear();
		main.getPlayerStorage().storePlayersData(PlayerDataType.REBIRTH);
		if(main.isMySql()) {
			String sql = "SELECT * FROM " + main.getDatabase() + "." + main.getTable();
			MySqlStreamer mySqlStreamer = new MySqlStreamer(main.getConnection());
			try {
				mySqlStreamer.streamQuery(sql)
				.sorted((a1, a2) -> {
					String rebirth1 = (String)a1.get("rebirth");
					String rebirth2 = (String)a2.get("rebirth");
					int number1 = Integer.valueOf(main.prxAPI.getRebirthNumber(rebirth1));
					int number2 = Integer.valueOf(main.prxAPI.getRebirthNumber(rebirth2));
					return number2 - number1;
				})
				.limit(25)
				.forEach(f -> {
					UUID uuid = UUID.fromString((String)f.get("uuid"));
					int finalNumber = Integer.valueOf(main.prxAPI.getRebirthNumber((String)f.get("rebirth")));
					updatedValuesR.put(uuid, finalNumber);
				});
			} catch (SQLException e) {
				e.printStackTrace();
			}
			mySqlStreamer.getStreamQuery().closeStatement();
			return updatedValuesR;
		}
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
	   update = false;
	   listr.clear();
	   listr.addAll(updatedValuesR.entrySet());
	   return updatedValuesR;
	}
	
	public Map<UUID, Integer> getGlobalLeaderboard() {
		if(!update && !updatedValuesGlobal.isEmpty()) {
			return updatedValuesGlobal;
		}
		updatedValuesGlobal.clear();
		main.getPlayerStorage().storePlayersData(PlayerDataType.ALL);
		if(main.isMySql()) {
			String sql = "SELECT * FROM " + main.getDatabase() + "." + main.getTable();
			MySqlStreamer mySqlStreamer = new MySqlStreamer(main.getConnection());
			try {
				mySqlStreamer.streamQuery(sql)
				.sorted((a1, a2) -> {
					UUID uuid1 = UUID.fromString((String)a1.get("uuid"));
					UUID uuid2 = UUID.fromString((String)a2.get("uuid"));
					int number1 = Integer.valueOf(main.prxAPI.getPlayerPromotionsAmount(uuid1));
					int number2 = Integer.valueOf(main.prxAPI.getPlayerPromotionsAmount(uuid2));
					return number2 - number1;
				})
				.limit(25)
				.forEach(f -> {
					UUID uuid = UUID.fromString((String)f.get("uuid"));
					int finalNumber = Integer.valueOf(main.prxAPI.getPlayerPromotionsAmount(UUID.fromString((String)f.get("uuid"))));
					updatedValuesGlobal.put(uuid, finalNumber);
				});
			} catch (SQLException e) {
				e.printStackTrace();
			}
			mySqlStreamer.getStreamQuery().closeStatement();
			return updatedValuesGlobal;
		}
		ConfigurationSection cf = main.getConfigManager().rankDataConfig.getConfigurationSection("players");
		cf.getValues(false)
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
	   update = false;
	   listGlobal.clear();
	   listGlobal.addAll(updatedValuesGlobal.entrySet());
	   return updatedValuesGlobal;
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}
	
}
