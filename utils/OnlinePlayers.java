package me.prisonranksx.utils;


import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.google.common.collect.Lists;
//version independent OnlinePlayers Class 1.4/1.5/1.6/1.7/1.8/1.9/1.10/1.11/1.12/1.13
public class OnlinePlayers {
	
private static Integer size = 0;
private static Integer count = size;
    /**
     * @param getEveryPlayer gets every player in the server in a list
     * @return
     */
	public static List<Player> getEveryPlayer() {
	    List<Player> playerlist = Lists.newArrayList();
	    playerlist.clear();
	    for (World wrld : Bukkit.getWorlds()) {
	        playerlist.addAll(wrld.getPlayers());
	    }
	    size = playerlist.size();
	    return Collections.unmodifiableList(playerlist);
	}
	/**
	 * @param size amount of the entries in the online players list
	 * @return
	 */
    public static Integer size() {
    	return size;
    }
    /**
     * @param getCount amount of online players in the server
     * @return
     */
    public static Integer getCount() {
    	return count;
    }
    /**
     * @param OBOPlayer one by one player , loop
     * @return
     */
    public static Player getOBOPlayer() {
    	for(Player plr : getEveryPlayer()) {
    		return plr;
    	}
		return null;
    }
    public static Boolean isEmpty() {
    	if(count == 0) {
    		return true;
    	} else {
    		return false;
    	}
    	
    }
}