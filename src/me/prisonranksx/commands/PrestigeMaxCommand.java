package me.prisonranksx.commands;


import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.utils.AccessibleBukkitTask;

public class PrestigeMaxCommand extends BukkitCommand {
	
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	public PrestigeMaxCommand(String commandName) {
		super(commandName);
		this.setDescription(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".description", "prestige to the latest prestige you can reach")));
		this.setUsage(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".usage", "/prestigemax")));
		this.setPermission(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".permission", "prisonranksx.prestigemax"));
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
	      if(!(sender instanceof Player)) {
	    	  return true;
	      } 
	      Player p = (Player)sender;
	      if(main.isInDisabledWorld(p)) {return true;}
	      String prestigeMaxType = main.getGlobalStorage().getStringData("Options.prestigemax-type");
	      prestigeMaxType = prestigeMaxType == null ? "AMTQ" : prestigeMaxType;
	      if(main.isInfinitePrestige) {
	    	  main.prxAPI.getPrestigeMax().executeOnAsyncQueue(p, true);
	    	  return true;
	      }
	      if(prestigeMaxType.equals("AR")) {
	    	  main.prxAPI.getPrestigeMax().execute(p);
	      } else if (prestigeMaxType.equals("ASTQ")) {
	    	  main.prxAPI.getPrestigeMax().executeOnAsyncQueue(p);
	      } else if (prestigeMaxType.equals("AMTQ")) {
	          main.prxAPI.getPrestigeMax().executeOnAsyncMultiThreadedQueue(p);
	      } else if (prestigeMaxType.equals("ARS")) {
	    	  main.prxAPI.getPrestigeMax().execute(p, true);
	      } else {
	    	  main.prxAPI.getPrestigeMax().executeOnAsyncMultiThreadedQueue(p);
	      }
		}
		return true;
	}
}
