package me.prisonranksx.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;

public class RankupCommand extends BukkitCommand {

	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	public RankupCommand(String commandName) {
		super(commandName);
		this.setDescription(main.getStringWithoutPAPI(main.configManager.commandsConfig.getString("commands." + commandName + ".description", "get promoted to the next rank")));
		this.setUsage(main.getStringWithoutPAPI(main.configManager.commandsConfig.getString("commands." + commandName + ".usage", "/rankup [player]")));
		this.setPermission(main.configManager.commandsConfig.getString("commands." + commandName + ".permission", "prisonranksx.rankup"));
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
		if(args.length == 0) {
	      if(!(sender instanceof Player)) {
	    	  String runFromConsole = main.messagesStorage.getStringMessage("Messages.runfromconsole");
	    	  sender.sendMessage(main.prxAPI.c(runFromConsole));
	    	  return true;
	      } 
	      if(main.isBefore1_7) {
	    	  main.prxAPI.rankupLegacy((Player)sender);
	    	  return true;
	      }
       main.prxAPI.rankup((Player)sender);
		} else if (args.length == 1) {
			// do rankup other
		}
		return true;
	}

}
