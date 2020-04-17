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
	private List<UUID> players;
	Map<UUID, Integer> values;
	
	public LeaderboardManager(PrisonRanksX main) {
		this.main = main;
		list = new ArrayList<>();
		players = new ArrayList<>();
		values = new HashMap<>();
	}
	
	private boolean indexExists(List<?> list, int index) {
		if(list.size() > index) {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param position
	 * @return an entry of player uuid and prestige number integer. 
	 *  @null  null if there is not a player in that position || no player joined to take this position.
	 */
	public Entry<UUID, Integer> getPlayerFromPositionPrestige(int position) {
		list.clear();
		list.addAll(getPrestigeLeaderboard().entrySet());
		return indexExists(list, position - 1) ? list.get(position - 1) : null;
	}
	
	/**
	 * 
	 * @param position
	 * @return an entry of player uuid and prestige number integer. 
	 *  @String  fallback if there is not a player in that position || no player joined to take this position.
	 */
	public String getPlayerNameFromPositionPrestige(int position, String fallback) {
       if(getPlayerFromPositionPrestige(position) == null) {
    	   return fallback;
       }
       return Bukkit.getOfflinePlayer(getPlayerFromPositionPrestige(position).getKey()).getName();
	}
	
	public String getPlayerPrestigeFromPosition(int position, String fallback) {
		if(getPlayerFromPositionPrestige(position) == null) {
			return fallback;
		}
		return main.prxAPI.getPlayerPrestige(getPlayerFromPositionPrestige(position).getKey());
	}
	
	public Entry<UUID, Integer> getPlayerFromPositionRebirth(int position) {
		list.clear();
		list.addAll(getPrestigeLeaderboard().entrySet());
		return indexExists(list, position - 1) ? list.get(position - 1) : null;
	}
	
	public String getPlayerRebirthFromPosition(int position, String fallback) {
		if(getPlayerFromPositionRebirth(position) == null) {
			return fallback;
		}
		return main.prxAPI.getPlayerRebirth(getPlayerFromPositionRebirth(position).getKey());
	}
	
	public String getPlayerNameFromPositionRebirth(int position, String fallback) {
	       if(getPlayerFromPositionRebirth(position) == null) {
	    	   return fallback;
	       }
	       return Bukkit.getOfflinePlayer(getPlayerFromPositionRebirth(position).getKey()).getName();
		}
	
	public int getPlayerPrestigePosition(OfflinePlayer player) {
		players.clear();
		players.addAll(getPrestigeLeaderboard().keySet());
		return players.indexOf(player.getUniqueId()) + 1;
	}
	
	public int getPlayerRebirthPosition(OfflinePlayer player) {
		players.clear();
		players.addAll(getRebirthLeaderboard().keySet());
		return players.indexOf(player.getUniqueId()) + 1;
	}
	
	public int getPlayerPrestigeValue(OfflinePlayer player) {
		return getPrestigeLeaderboard().get(player.getUniqueId());
	}
	
	public int getPlayerRebirthValue(OfflinePlayer player) {
		return getRebirthLeaderboard().get(player.getUniqueId());
	}
	
	public Map<UUID, Integer> getPrestigeLeaderboard() {
	    values.clear();
	    Set<String> playerUUIDs = main.playerStorage.getPlayerData().keySet();
	    for (String player : playerUUIDs) {
	    	UUID u = UUID.fromString(player);
	        values.put(u, main.prxAPI.getPlayerPrestigeNumber(u));
	    }
	    return values.entrySet()
	            .stream()
	            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
	            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (i, i2) -> i, LinkedHashMap::new));
	}
	
	public Map<UUID, Integer> getRebirthLeaderboard() {
	    values.clear();
	    Set<String> playerUUIDs = main.playerStorage.getPlayerData().keySet();
	    for (String player : playerUUIDs) {
	    	UUID u = UUID.fromString(player);
	        values.put(u, main.prxAPI.getPlayerRebirthNumber(u));
	    }
	    return values.entrySet()
	            .stream()
	            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
	            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (i, i2) -> i, LinkedHashMap::new));
	}
	
}
