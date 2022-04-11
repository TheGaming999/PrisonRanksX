package me.prisonranksx.permissions;

import java.util.UUID;

import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.utils.EZLuckPerms;
import net.luckperms.api.model.user.User;

public class LPVaultDataUpdater implements IVaultDataUpdater {

	private PrisonRanksX plugin;

	public LPVaultDataUpdater(PrisonRanksX plugin) {
		this.plugin = plugin;
	}

	@Override
	public void set(Player player, String group, String oldGroup) {
		plugin.newSharedChain("LuckPerms").async(() -> {
			EZLuckPerms.setPlayerGroup(player.getUniqueId(), EZLuckPerms.getGroup(group), true);
		}).execute();
	}

	@Override
	public void set(Player player, String group) {
		EZLuckPerms.setPlayerGroup(player.getUniqueId(), EZLuckPerms.getGroup(group), true);
	}

	@Override
	public void remove(Player player) {
		EZLuckPerms.deletePlayerGroups(player.getUniqueId(), true);
	}

	@Override
	public void remove(Player player, String group) {
		EZLuckPerms.deletePlayerGroup(player.getUniqueId(), group);
	}

	@Override
	public String get(Player player) {
		return EZLuckPerms.getPlayerGroup(player.getUniqueId()).getName();
	}

	@Override
	public void update(Player player) {
		plugin.newSharedChain("LuckPerms").async(() -> {
			UUID uuid = player.getUniqueId();
			User lpUser = EZLuckPerms.getUser(uuid);
			if(!lpUser.getPrimaryGroup().equalsIgnoreCase(plugin.prxAPI.getPlayerRank(uuid))) {
				plugin.prxAPI.setPlayerRank(uuid, plugin.manager.matchRank(lpUser.getPrimaryGroup()));
			}
		}).execute();
	}

}
