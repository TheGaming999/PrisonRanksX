package me.prisonranksx.leaderboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
	private List<UUID> players;
	private List<UUID> playersp;
	private List<UUID> playersr;
	private Map<UUID, Integer> values;
	private Map<UUID, Integer> valuesp;
	private Map<UUID, Integer> valuesr;
	
	public LeaderboardManager(PrisonRanksX main) {
		this.main = main;
		list = new ArrayList<>();
		listp = new ArrayList<>();
		listr = new ArrayList<>();
		players = new ArrayList<>();
		playersp = new ArrayList<>();
		playersr = new ArrayList<>();
		values = new HashMap<>();
		valuesp = new HashMap<>();
		valuesr = new HashMap<>();
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
       return Bukkit.getOfflinePlayer(getPlayerFromPositionPrestige(position).getKey()).getName();
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
		return Bukkit.getOfflinePlayer(getPlayerFromPositionRank(position).getKey()).getName();
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
	
	public Entry<UUID, Integer> getPlayerFromPositionRebirth(final int position) {
		listr.clear();
		listr.addAll(getPrestigeLeaderboard().entrySet());
		return indexExists(listr, position - 1) ? listr.get(position - 1) : null;
	}
	
	public String getPlayerRebirthFromPosition(final int position, final String fallback) {
		if(getPlayerFromPositionRebirth(position) == null) {
			return fallback;
		}
		String rebirth = main.prxAPI.getPlayerRebirth(getPlayerFromPositionRebirth(position).getKey());
		return rebirth == null ? fallback : rebirth;
	}
	
	public String getPlayerNameFromPositionRebirth(final int position, final String fallback) {
	       if(getPlayerFromPositionRebirth(position) == null) {
	    	   return fallback;
	       }
	       return Bukkit.getOfflinePlayer(getPlayerFromPositionRebirth(position).getKey()).getName();
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
	
	public int getPlayerRankValue(final OfflinePlayer player) {
		return getRankLeaderboard().get(player.getUniqueId());
	}
	
	public int getPlayerPrestigeValue(final OfflinePlayer player) {
		return getPrestigeLeaderboard().get(player.getUniqueId());
	}
	
	public int getPlayerRebirthValue(final OfflinePlayer player) {
		return getRebirthLeaderboard().get(player.getUniqueId());
	}
	
	public Map<UUID, Integer> getRankLeaderboard() {
	    values.clear();
	    Set<String> playerUUIDs = main.playerStorage.getPlayerData().keySet();
	    for (String player : playerUUIDs) {
	    	UUID u = UUID.fromString(player);
	        values.put(u, main.prxAPI.getPlayerRankNumber(u));
	    }
	    return values.entrySet()
	            .stream()
	            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
	            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (i, i2) -> i, LinkedHashMap::new));
	}
	
	public Map<UUID, Integer> getPrestigeLeaderboard() {
	    valuesp.clear();
	    Set<String> playerUUIDs = main.playerStorage.getPlayerData().keySet();
	    for (String player : playerUUIDs) {
	    	UUID u = UUID.fromString(player);
	        valuesp.put(u, main.prxAPI.getPlayerPrestigeNumber(u));
	    }
	    return valuesp.entrySet()
	            .stream()
	            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
	            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (i, i2) -> i, LinkedHashMap::new));
	}
	
	public Map<UUID, Integer> getRebirthLeaderboard() {
	    valuesr.clear();
	    Set<String> playerUUIDs = main.playerStorage.getPlayerData().keySet();
	    for (String player : playerUUIDs) {
	    	UUID u = UUID.fromString(player);
	        valuesr.put(u, main.prxAPI.getPlayerRebirthNumber(u));
	    }
	    return valuesr.entrySet()
	            .stream()
	            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
	            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (i, i2) -> i, LinkedHashMap::new));
	}
	
}
