package me.prisonranksx.permissions;

import org.bukkit.entity.Player;

public interface IVaultDataUpdater {

	void set(Player player, String group);
	
	void remove(Player player);
	
	void remove(Player player, String group);
	
	String get(Player player);

	void set(Player player, String group, String oldGroup);
	
	void update(Player player);
	
}
