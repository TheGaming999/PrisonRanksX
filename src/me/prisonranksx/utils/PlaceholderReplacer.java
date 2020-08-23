package me.prisonranksx.utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface PlaceholderReplacer {

	String parse(String message, Player player);
	String parse(String message, OfflinePlayer offlinePlayer);
	String parseCached(String message);
	
}
