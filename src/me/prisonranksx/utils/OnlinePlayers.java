package me.prisonranksx.utils;


import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
/**
 * version independent OnlinePlayers Class 1.4/1.5/1.6/1.7/1.8/1.9/1.10/1.11/1.12/1.13
 *
 */
public class OnlinePlayers {
	
    private static int size = 0;
    private static int amount = 0;

    /**
     * @return gets every player in the server in a list
     */
	public static List<Player> getPlayers() {
	    List<Player> playerList = Lists.newArrayList();
	    for (World wrld : Bukkit.getWorlds()) {
	        playerList.addAll(wrld.getPlayers());
	    }
	    size = playerList.size();
	    return Collections.unmodifiableList(playerList);
	}
	/**
	 * @return size amount of the entries in the online players list
	 */
    public static int size() {
    	Bukkit.getWorlds().forEach(world -> {
    		world.getPlayers().forEach(player -> amount++);
  	    });
    	return amount;
    }
    
    /**
     * @deprecated
     * @return amount of last cached online players in the server
     */
    public static int getCount() {
    	return size;
    }

    public static boolean isEmpty() {
    	return size == 0 ? true : false;	
    }
}