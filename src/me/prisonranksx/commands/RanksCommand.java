package me.prisonranksx.commands;


import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;

public class RanksCommand extends BukkitCommand {
	
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	
	public RanksCommand(String commandName) {
		super(commandName);
		this.setDescription(main.getStringWithoutPAPI(main.configManager.commandsConfig.getString("commands." + commandName + ".description", "shows a list of prison ranks")));
		this.setUsage(main.getStringWithoutPAPI(main.configManager.commandsConfig.getString("commands." + commandName + ".usage", "/ranks")));
		this.setPermission(main.configManager.commandsConfig.getString("commands." + commandName + ".permission", "prisonranksx.ranks"));
		this.setPermissionMessage(main.getStringWithoutPAPI(main.configManager.commandsConfig.getString("commands." + commandName + ".permission-message", "&cYou don't have permission to execute this command.")));
		this.setAliases(main.configManager.commandsConfig.getStringList("commands." + commandName + ".aliases"));
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(!sender.hasPermission(this.getPermission())) {
			sender.sendMessage(main.prxAPI.g(this.getPermissionMessage()));
			return true;
		}
		if(!main.isRankEnabled) {
			return true;
		}
		if(!(sender instanceof Player)) {
			try {
			sender.sendMessage(main.prxAPI.c(main.ranksAPI.rankListConsole));
			main.prxAPI.getRanksCollection("default").forEach(rank -> {sender.sendMessage(rank);});
			} catch (NullPointerException err) {
				
			}
			return true;
		}
		if(args.length == 0) {
			if(main.globalStorage.getBooleanData("Options.GUI-RANKLIST")) {
				main.guiManager.openRanksGUI((Player)sender);
				return true;
			}
			main.ranksAPI.send("1", sender);
		} else if (args.length == 1) {
            main.ranksAPI.send(args[0], sender);
		}
		return true;
	}

	
	
}
