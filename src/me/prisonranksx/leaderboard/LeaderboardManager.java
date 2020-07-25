package me.prisonranksx.leaderboard;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import me.prisonranksx.PrisonRanksX;

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
	private Map<UUID, Integer> values;
	private Map<UUID, Integer> valuesp;
	private Map<UUID, Integer> valuesr;
	private Map<UUID, Integer> valuesGlobal;
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
		values = Collections.synchronizedMap(new LinkedHashMap<>());
		valuesp = Collections.synchronizedMap(new LinkedHashMap<>());
		valuesr = Collections.synchronizedMap(new LinkedHashMap<>());
		valuesGlobal = Collections.synchronizedMap(new LinkedHashMap<>());
		setUpdate(true);
		Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
			setUpdate(true);
		}, 60 * 5, 60 * 5);
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
		listp.clear();
		listp.addAll(getPrestigeLeaderboard().entrySet());
		return indexExists(listp, position - 1) ? listp.get(position - 1) : null;
	}
	
	/**
	 * 
	 * @param position
	 * @return an entry of player uuid and rank number integer. 
	 *  @null  null if there is not a player in that position || no player joined to take this position.
	 */
	public Entry<UUID, Integer> getPlayerFromPositionRank(final int position) {
		list.clear();
		list.addAll(getRankLeaderboard().entrySet());
		return indexExists(list, position - 1) ? list.get(position - 1) : null;
	}
	
	public Entry<UUID, Integer> getPlayerFromPositionGlobal(final int position) {
		listGlobal.clear();
		listGlobal.addAll(getGlobalLeaderboard().entrySet());
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
		listr.clear();
		listr.addAll(getRebirthLeaderboard().entrySet());
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
		if(!update && updatedValues != null) {
			return updatedValues;
		}
	    values.clear();
	    Set<String> playerUUIDs = main.playerStorage.getPlayerData().keySet();
	    synchronized(playerUUIDs) {
	    for (String player : playerUUIDs) {
	    	UUID u = UUID.fromString(player);
	        values.put(u, main.prxAPI.getPlayerRankNumber(u));
	    }
	    }
	    Map<UUID, Integer> linked = Collections.synchronizedMap(values.entrySet()
	            .stream()
	            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
	            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (i, i2) -> i, LinkedHashMap::new)));
	    updatedValues = linked;
	    update = false;
	    return linked;
	}
	
	public Map<UUID, Integer> getPrestigeLeaderboard() {
		if(!update && updatedValuesP != null) {
			return updatedValuesP;
		}
	    valuesp.clear();
	    Set<String> playerUUIDs = main.playerStorage.getPlayerData().keySet();
	    synchronized(playerUUIDs) {
	    for (String player : playerUUIDs) {
	    	UUID u = UUID.fromString(player);
	        valuesp.put(u, main.prxAPI.getPlayerPrestigeNumber(u));
	    }
	    }
	    Map<UUID, Integer> linked = Collections.synchronizedMap(valuesp.entrySet()
	            .stream()
	            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
	            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (i, i2) -> i, LinkedHashMap::new)));
	    updatedValuesP = linked;
	    update = false;
	    return linked;
	}
	
	public Map<UUID, Integer> getRebirthLeaderboard() {
		if(!update && updatedValuesR != null) {
			return updatedValuesR;
		}
	    valuesr.clear();
	    Set<String> playerUUIDs = main.playerStorage.getPlayerData().keySet();
	    synchronized(playerUUIDs) {
	    for (String player : playerUUIDs) {
	    	UUID u = UUID.fromString(player);
	        valuesr.put(u, main.prxAPI.getPlayerRebirthNumber(u));
	    }
	    }
	    Map<UUID, Integer> linked = Collections.synchronizedMap(valuesr.entrySet()
	            .stream()
	            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
	            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (i, i2) -> i, LinkedHashMap::new)));
	    updatedValuesR = linked;
	    update = false;
	    return linked;
	}
	
	public Map<UUID, Integer> getGlobalLeaderboard() {
		if(!update && updatedValuesGlobal != null) {
			return updatedValuesGlobal;
		}
		valuesGlobal.clear();
		Set<String> playerUUIDs = main.playerStorage.getPlayerData().keySet();
		synchronized(playerUUIDs) {
		for (String player : playerUUIDs) {
			UUID u = UUID.fromString(player);
			valuesGlobal.put(u, main.prxAPI.getPlayerPromotionsAmount(u));
		}
		}
		Map<UUID, Integer> linked = Collections.synchronizedMap(valuesGlobal.entrySet()
				.stream()
				.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (i, i2) -> i, LinkedHashMap::new)));
		updatedValuesGlobal = linked;
		update = false;
		return linked;
		
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}
	
}
