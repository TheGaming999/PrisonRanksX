package me.prisonranksx.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;

public class PrestigeCommand extends BukkitCommand {
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	
	public PrestigeCommand(String commandName) {
		super(commandName);
		this.main = PrisonRanksX.getInstance();
		this.setDescription(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".description", "get promoted to a higher level of ranks")));
		this.setUsage(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".usage", "/prestige")));
		this.setPermission(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".permission", "prisonranksx.prestige"));
		this.setPermissionMessage(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".permission-message", "&cYou don't have permission to execute this command.")));
		this.setAliases(main.getConfigManager().commandsConfig.getStringList("commands." + commandName + ".aliases"));
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(!sender.hasPermission(this.getPermission())) {
			sender.sendMessage(this.getPermissionMessage());
			return true;
		}
		if(!main.isPrestigeEnabled) {
			return true;
		}
		if(args.length == 0) {
			if(main.isInDisabledWorld(sender)) {return true;}
	      if(!(sender instanceof Player)) {
	    	  return true;
	      } 
	      if(main.isBefore1_7) {
	    	  main.prxAPI.prestigeLegacy((Player)sender);
	    	  return true;
	      }
           // do prestige
	      main.prxAPI.prestige((Player)sender);
		} else if (args.length == 1) {
			// do force prestige
		}
		return true;
	}
}
