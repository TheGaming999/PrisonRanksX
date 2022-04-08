package me.prisonranksx.utils;

import java.util.Set;

import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

/**
 * OnlinePlayers for 1.5-1.18
 *
 */
public class OnlinePlayers {
	
    private final static Set<Player> players = Sets.newHashSet();
    
    public static void add(Player player) {
    	players.add(player);
    }
    
    public static void delete(Player player) {
    	players.remove(player);
    }
    
    public static void clear() {
    	players.clear();
    }
    
    /**
     * @return gets every player in the server in a list
     */
	public static Set<Player> getPlayers() {
	    return players;
	}
	
	/**
	 * @return size amount of the entries in the online players list
	 */
    public static int size() {
    	return players.size();
    }

    public static boolean isEmpty() {
    	return size() == 0 ? true : false;	
    }
}