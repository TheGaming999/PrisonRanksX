package me.prisonranksx.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;

public class RebirthCommand extends BukkitCommand {
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	
	public RebirthCommand(String commandName) {
		super(commandName);
		this.setDescription(main.getStringWithoutPAPI(main.configManager.commandsConfig.getString("commands." + commandName + ".description", "reborn")));
		this.setUsage(main.getStringWithoutPAPI(main.configManager.commandsConfig.getString("commands." + commandName + ".usage", "/rebirth")));
		this.setPermission(main.configManager.commandsConfig.getString("commands." + commandName + ".permission", "prisonranksx.rebirth"));
		this.setPermissionMessage(main.getStringWithoutPAPI(main.configManager.commandsConfig.getString("commands." + commandName + ".permission-message", "&cYou don't have permission to execute this command.")));
		this.setAliases(main.configManager.commandsConfig.getStringList("commands." + commandName + ".aliases"));
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(!sender.hasPermission(this.getPermission())) {
			sender.sendMessage(this.getPermissionMessage());
			return true;
		}
		if(!main.isRebirthEnabled) {
			return true;
		}
		if(args.length == 0) {
	      if(!(sender instanceof Player)) {
	    	  return true;
	      } 
	      if(main.isBefore1_7) {
	    	  main.prxAPI.rebirthLegacy((Player)sender);
	    	  return true;
	      }
           // do rebirth
	      main.prxAPI.rebirth((Player)sender);
		} else if (args.length == 1) {
			// do force rebirth
		}
		return true;
	}
}
