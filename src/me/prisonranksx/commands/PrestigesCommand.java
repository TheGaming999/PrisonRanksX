package me.prisonranksx.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.utils.CollectionUtils;
import me.prisonranksx.utils.CollectionUtils.PaginatedList;

public class PrestigesCommand extends BukkitCommand {

	private PrisonRanksX main;

	public PrestigesCommand(String commandName) {
		super(commandName);
		this.main = PrisonRanksX.getInstance();
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
				if(!main.isInfinitePrestige) {
					PaginatedList pl = CollectionUtils.paginateListCollectable(main.prxAPI.getPrestigesCollection(), 10, 1);
					if(args.length == 0) {
						pl.collect().forEach(prestigeName -> {
							String coloredDisplay = main.getString(main.prxAPI.getPrestigeDisplay(prestigeName));
							sender.sendMessage(coloredDisplay);
						});
						sender.sendMessage("Page: " + pl.getCurrentPage() + " of " + pl.getFinalPage());
					} else if (args.length == 1) {
						pl.navigate(Integer.valueOf(args[0]));
						pl.collect().forEach(prestigeName -> {
							String coloredDisplay = main.getString(main.prxAPI.getPrestigeDisplay(prestigeName));
							sender.sendMessage(coloredDisplay);
						});
						sender.sendMessage("Page: " + pl.getCurrentPage() + " of " + pl.getFinalPage());
					}
				} else {
					if(args.length == 0) {
						int size = (int)main.infinitePrestigeSettings.getFinalPrestige();
		            	int finalPage = CollectionUtils.getAccurateFinalPage(size, 10);
		            	for	(int i = 0; i < 10; i++) {
		            		int elementIndex = CollectionUtils.paginateIndex(i, 10, 1);
		            		if(elementIndex < 0 || elementIndex >= size) {
		      	    		  break;
		      	    	    }
		            		long prestigeNumber = Long.valueOf(elementIndex+1);
		            		String prestigeName = String.valueOf(prestigeNumber);
		            		String coloredDisplay = main.getString(main.prxAPI.getPrestigeDisplay(prestigeName));
		            		sender.sendMessage(coloredDisplay);
		            	}
		            	sender.sendMessage("CurrentPage: 1" + " / FinalPage: " + finalPage);
					} else if (args.length == 1) {
						int size = (int)main.infinitePrestigeSettings.getFinalPrestige();
						int currentPage = Integer.valueOf(args[0]);
		            	int finalPage = CollectionUtils.getAccurateFinalPage(size, 10);
		            	if(currentPage > finalPage) {
		            		sender.sendMessage("Final page reached!");
		            		return true;
		            	}
		            	for	(int i = 0; i < 10; i++) {
		            		int elementIndex = CollectionUtils.paginateIndex(i, 10, currentPage);
		            		if(elementIndex < 0 || elementIndex >= size) {
		      	    		  break;
		      	    	    }
		            		long prestigeNumber = Long.valueOf(elementIndex+1);
		            		String prestigeName = String.valueOf(prestigeNumber);
		            		String coloredDisplay = main.getString(main.prxAPI.getPrestigeDisplay(prestigeName));
		            		sender.sendMessage(coloredDisplay);
		            	}
		            	sender.sendMessage("CurrentPage: " + currentPage + " / FinalPage: " + finalPage);
					}
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
