package me.prisonranksx.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;

public class AutoPrestigeCommand extends BukkitCommand 
{
	
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	public AutoPrestigeCommand(String commandName) {
		super(commandName);
		this.setDescription(main.getStringWithoutPAPI(main.configManager.commandsConfig.getString("commands." + commandName + ".description", "automatically prestige while having enough money to rankup")));
		this.setUsage(main.getStringWithoutPAPI(main.configManager.commandsConfig.getString("commands." + commandName + ".usage", "/autoprestige")));
		this.setPermission(main.configManager.commandsConfig.getString("commands." + commandName + ".permission", "prisonranksx.autoprestige"));
		this.setPermissionMessage(main.getStringWithoutPAPI(main.configManager.commandsConfig.getString("commands." + commandName + ".permission-message", "&cYou don't have permission to execute this command.")));
		this.setAliases(main.configManager.commandsConfig.getStringList("commands." + commandName + ".aliases"));
	}
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(!main.isPrestigeEnabled) {
			return true;
		}
		if(!(sender instanceof Player)) {
			return true;
		}
		Player p = (Player)sender;
		if(main.isBefore1_7) {
	        if(args.length == 0) {
	            main.prestigeLegacy.autoPrestige(p);
	        } else if (args.length == 1) {
	        	if(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("true")) {
	        		main.prestigeLegacy.autoPrestige(p, true);
	        	} else if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("false")) {
	        		main.prestigeLegacy.autoPrestige(p, false);
	        	}
	        }
			return true;
		}
        if(args.length == 0) {
            main.prestigeAPI.autoPrestige(p);
        } else if (args.length == 1) {
        	if(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("true")) {
        		main.prestigeAPI.autoPrestige(p, true);
        	} else if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("false")) {
        		main.prestigeAPI.autoPrestige(p, false);
        	}
        }
		return true;
	}
}