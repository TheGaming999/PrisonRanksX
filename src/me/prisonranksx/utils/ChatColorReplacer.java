package me.prisonranksx.utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface ChatColorReplacer {

	String parsePlaceholders(String message);
	String parsePlaceholders(String message, Player player);
	String parsePlaceholders(String message, OfflinePlayer offlinePlayer);
	String parsePlaceholders(String message, String playerName);
	
}
