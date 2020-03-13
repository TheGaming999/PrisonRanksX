package me.prisonranksx.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;

public class AutoRankupCommand extends BukkitCommand 
{
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	public AutoRankupCommand(String commandName) {
		super(commandName);
		this.setDescription(main.getStringWithoutPAPI(main.configManager.commandsConfig.getString("commands." + commandName + ".description", "automatically rankup while having enough money to rankup")));
		this.setUsage(main.getStringWithoutPAPI(main.configManager.commandsConfig.getString("commands." + commandName + ".usage", "/autorankup")));
		this.setPermission(main.configManager.commandsConfig.getString("commands." + commandName + ".permission", "prisonranksx.autorankup"));
		this.setPermissionMessage(main.getStringWithoutPAPI(main.configManager.commandsConfig.getString("commands." + commandName + ".permission-message", "&cYou don't have permission to execute this command.")));
		this.setAliases(main.configManager.commandsConfig.getStringList("commands." + commandName + ".aliases"));
	}
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(!(sender instanceof Player)) {
			return true;
		}
		Player p = (Player)sender;
        if(args.length == 0) {
            main.rankupAPI.autoRankup(p);
        } else if (args.length == 1) {
        	if(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("true")) {
        		main.rankupAPI.autoRankup(p, true);
        	} else if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("false")) {
        		main.rankupAPI.autoRankup(p, false);
        	}
        }
		return true;
	}
}
