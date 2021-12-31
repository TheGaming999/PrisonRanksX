package me.prisonranksx.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.utils.CollectionUtils;
import me.prisonranksx.utils.CollectionUtils.PaginatedList;

public class PrestigesCommand extends BukkitCommand{
	
private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	
	public PrestigesCommand(String commandName) {
		super(commandName);
		this.setDescription(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".description", "shows a list of prison prestiges")));
		this.setUsage(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".usage", "/prestiges")));
		this.setPermission(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".permission", "prisonranksx.prestiges"));
		this.setPermissionMessage(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".permission-message", "&cYou don't have permission to execute this command.")));
		this.setAliases(main.getConfigManager().commandsConfig.getStringList("commands." + commandName + ".aliases"));
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(!main.isPrestigeEnabled) {
			return true;
		}
		if(!sender.hasPermission(this.getPermission())) {
			sender.sendMessage(this.getPermissionMessage());
			return true;
		}
		if(!(sender instanceof Player)) {
			try {
			sender.sendMessage(main.prxAPI.c(main.prestigesAPI.prestigeListConsole));
			PaginatedList pl = CollectionUtils.paginateListCollectable(main.prxAPI.getPrestigesCollection(), 10, 1);
			if(args.length == 0) {
			pl.collect().forEach(sender::sendMessage);
			sender.sendMessage("Page: " + pl.getCurrentPage() + " of " + pl.getFinalPage());
			} else if (args.length == 1) {
				pl.navigate(Integer.valueOf(args[0]));
				pl.collect().forEach(sender::sendMessage);
				sender.sendMessage("Page: " + pl.getCurrentPage() + " of " + pl.getFinalPage());
			}
			} catch (NullPointerException err) {
				err.printStackTrace();
			}
			return true;
		}
		if(args.length == 0) {
			if(main.isInDisabledWorld(sender)) {return true;}
				if(main.globalStorage.getBooleanData("Options.GUI-PRESTIGELIST")) {
					main.getGuiManager().openPrestigesGUI((Player)sender);
					return true;
				}
			main.prestigesAPI.send("1", sender);
		} else if (args.length == 1) {
			if(main.isInDisabledWorld(sender)) {return true;}
            main.prestigesAPI.send(args[0], sender);
		}
		return true;
	}
}
