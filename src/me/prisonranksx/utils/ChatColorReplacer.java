package me.prisonranksx.utils;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface ChatColorReplacer {

	String parsePlaceholders(String message);
	String parseRegular(String message);
	List<String> parsePlaceholders(List<String> message);
	List<String> parseRegular(List<String> message);
	String parsePlaceholders(String message, Player player);
	String parsePlaceholders(String message, OfflinePlayer offlinePlayer);
	String parsePlaceholders(String message, String playerName);
	
}
