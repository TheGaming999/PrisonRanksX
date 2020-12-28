package me.prisonranksx.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;

public class RebirthsCommand extends BukkitCommand{
private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	
	public RebirthsCommand(String commandName) {
		super(commandName);
		this.setDescription(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".description", "shows a list of prison rebirths")));
		this.setUsage(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".usage", "/rebirths")));
		this.setPermission(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".permission", "prisonranksx.rebirths"));
		this.setPermissionMessage(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".permission-message", "&cYou don't have permission to execute this command.")));
		this.setAliases(main.getConfigManager().commandsConfig.getStringList("commands." + commandName + ".aliases"));
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
		if(main.isInDisabledWorld(sender)) {return true;}
		if(!(sender instanceof Player)) {
			try {
			sender.sendMessage(main.prxAPI.c(main.rebirthsAPI.rebirthListConsole));
			main.prxAPI.getRebirthsCollection().forEach(rebirth -> {sender.sendMessage(rebirth);});
			} catch (NullPointerException err) {
				
			}
			return true;
		}
		if(args.length == 0) {
			if(main.globalStorage.getBooleanData("Options.GUI-REBIRTHLIST")) {
				main.getGuiManager().openRebirthsGUI((Player)sender);
				return true;
			}
			main.rebirthsAPI.send("1", sender);
		} else if (args.length == 1) {
            main.rebirthsAPI.send(args[0], sender);
		}
		return true;
	}
}
