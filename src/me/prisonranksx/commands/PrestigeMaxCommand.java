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
	      main.prxAPI.getPrestigeMax().executeOnAsyncMultiThreadedQueue(p);
		} else if (args.length == 1) {
			Player p = Bukkit.getPlayer(args[0]);
			if(p == null) {
				AccessibleBukkitTask abt = new AccessibleBukkitTask();
				abt.runAsyncLoop(main, () -> {
					sender.sendMessage(abt.getLoopValue("test").toString());
				}, 0, 10, true, "test");
				return true;
			}
			if(!p.hasPermission(this.getPermission() + ".other")) {return true;}
			main.prxAPI.getPrestigeMax().execute(p);
		}
		return true;
	}
}
