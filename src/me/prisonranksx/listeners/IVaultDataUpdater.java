package me.prisonranksx.listeners;

import org.bukkit.entity.Player;

public interface IVaultDataUpdater {

	void set(Player player, String group);
	
	void remove(Player player);
	
	String get(Player player);
	
}
