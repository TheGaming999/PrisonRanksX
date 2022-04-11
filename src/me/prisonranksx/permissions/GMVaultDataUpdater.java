package me.prisonranksx.permissions;

import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;

public class GMVaultDataUpdater implements IVaultDataUpdater {

	private PrisonRanksX plugin;
	
	public GMVaultDataUpdater(PrisonRanksX plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void set(Player player, String group, String oldGroup) {
		plugin.groupManager.setGroup(player, group);
	}
	
	@Override
	public void set(Player player, String group) {
		plugin.groupManager.setGroup(player, group);
	}

	@Override
	public void remove(Player player) {
		plugin.getPermissions().playerRemoveGroup(player, get(player));
	}

	@Override
	public void remove(Player player, String group) {
		plugin.getPermissions().playerRemoveGroup(player, group);
	}

	@Override
	public String get(Player player) {
		return plugin.groupManager.getGroup(player);
	}

	@Override
	public void update(Player player) {
		String group = plugin.groupManager.getGroup(player);
		if(!group.equalsIgnoreCase(plugin.prxAPI.getPlayerRank(player))) {
			plugin.prxAPI.setPlayerRank(player, group);
		}
	}

	
}
