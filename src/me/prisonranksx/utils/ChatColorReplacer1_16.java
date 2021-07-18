package me.prisonranksx.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import me.prisonranksx.PrisonRanksX;
import net.md_5.bungee.api.ChatColor;

public class ChatColorReplacer1_16 implements ChatColorReplacer {

	private PrisonRanksX main;
	public ChatColorReplacer1_16(PrisonRanksX main) {
		this.main = main;
	}
	
	private String c(String textToTranslate) {
		return ChatColor.translateAlternateColorCodes('&', textToTranslate);
	}
	
	@Override
	public String parsePlaceholders(String message) {
		return main.getPlaceholderReplacer().parseCached(c(message));
	}
	
	@Override
	public String parseRegular(String message) {
		return c(message);
	}
	
	@Override
	public List<String> parsePlaceholders(List<String> message) {
		List<String> newList = Lists.newArrayList();
		message.forEach(line -> {
			newList.add(main.getPlaceholderReplacer().parseCached(c(line)));
		});
		return newList;
	}
	
	@Override
	public List<String> parseRegular(List<String> message) {
		List<String> newList = Lists.newArrayList();
		message.forEach(line -> {
			newList.add(c(line));
		});
		return newList;
	}
	
	@Override
	public String parsePlaceholders(String message, Player player) {
		return main.getPlaceholderReplacer().parse(c(message), player);
	}
	@Override
	public String parsePlaceholders(String message, OfflinePlayer offlinePlayer) {
		return main.getPlaceholderReplacer().parse(c(message), offlinePlayer);
	}
	@Override
	public String parsePlaceholders(String message, String playerName) {
		return main.getPlaceholderReplacer().parse(c(message), Bukkit.getPlayer(playerName));
	}
	
	
}
