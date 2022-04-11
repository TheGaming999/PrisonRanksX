package me.prisonranksx.permissions;

import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;

public class VaultDataUpdater implements IVaultDataUpdater {

	private PrisonRanksX plugin;
	
	public VaultDataUpdater(PrisonRanksX plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void set(Player player, String group, String oldGroup) {
		if(plugin.getPermissions().playerInGroup(player, oldGroup)) 
			plugin.getPermissions().playerRemoveGroup(player, oldGroup);
		plugin.getPermissions().playerAddGroup(player, group);
	}

	@Override
	public void set(Player player, String group) {
		plugin.getPermissions().playerRemoveGroup(player, plugin.getPermissions().getPrimaryGroup(player));
		plugin.getPermissions().playerAddGroup(player, group);
	}
	
	@Override
	public void remove(Player player) {
		plugin.getPermissions().playerRemoveGroup(player, plugin.getPermissions().getPrimaryGroup(player));
	}

	@Override
	public void remove(Player player, String group) {
		plugin.getPermissions().playerRemoveGroup(player, group);
	}

	@Override
	public String get(Player player) {
		return plugin.getPermissions().getPrimaryGroup(player);
	}

	@Override
	public void update(Player player) {
		String group = plugin.getPermissions().getPrimaryGroup(player);
		if(!group.equalsIgnoreCase(plugin.prxAPI.getPlayerRank(player))) {
			plugin.prxAPI.setPlayerRank(player, group);
		}
	}


	
}
