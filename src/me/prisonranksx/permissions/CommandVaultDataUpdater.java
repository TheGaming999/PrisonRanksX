package me.prisonranksx.permissions;

import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;

public class CommandVaultDataUpdater implements IVaultDataUpdater {

	private PrisonRanksX plugin;
	private String commandLine;

	public CommandVaultDataUpdater(PrisonRanksX plugin) {
		this.plugin = plugin;
		this.commandLine = plugin.globalStorage.getStringData("Options.rankup-vault-groups-plugin");
	}

	@Override
	public void set(Player player, String group, String oldGroup) {
		plugin.newSharedChain("Command").sync(() -> {
			plugin.executeCommand(player, commandLine.replace("%rank%", group));
		}).execute();
	}

	@Override
	public void set(Player player, String group) {
		plugin.executeCommand(player, commandLine.replace("%rank%", group));
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
	public void update(Player player) {}

}
