package me.prisonranksx.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;

public class RankupMaxCommand extends BukkitCommand{
	
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	public RankupMaxCommand(String commandName) {
		super(commandName);
		this.setDescription(main.getStringWithoutPAPI(main.configManager.commandsConfig.getString("commands." + commandName + ".description", "rankup to the latest rank you can reach")));
		this.setUsage(main.getStringWithoutPAPI(main.configManager.commandsConfig.getString("commands." + commandName + ".usage", "/rankupmax")));
		this.setPermission(main.configManager.commandsConfig.getString("commands." + commandName + ".permission", "prisonranksx.rankupmax"));
		this.setPermissionMessage(main.getStringWithoutPAPI(main.configManager.commandsConfig.getString("commands." + commandName + ".permission-message", "&cYou don't have permission to execute this command.")));
		this.setAliases(main.configManager.commandsConfig.getStringList("commands." + commandName + ".aliases"));
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(!sender.hasPermission(this.getPermission())) {
			sender.sendMessage(this.getPermissionMessage());
			return true;
		}
		if(!main.isRankEnabled) {
			return true;
		}
		if(args.length == 0) {
	      if(!(sender instanceof Player)) {
	    	  return true;
	      } 
       //do rankup max
	     main.prxAPI.rankupMax((Player)sender);
		} else if (args.length == 1) {
			String rank = main.manager.matchRank(args[0]);
			if(!main.prxAPI.rankExists(rank)) {
				return true;
			}
			main.prxAPI.rankupMaxLimit((Player)sender, rank);
		}
		return true;
	}
}
