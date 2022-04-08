package me.prisonranksx.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;

public class AutoRankupCommand extends BukkitCommand 
{
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	public AutoRankupCommand(String commandName) {
		super(commandName);
		this.main = PrisonRanksX.getInstance();
		this.setDescription(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".description", "automatically rankup while having enough money to rankup")));
		this.setUsage(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".usage", "/autorankup")));
		this.setPermission(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".permission", "prisonranksx.autorankup"));
		this.setPermissionMessage(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".permission-message", "&cYou don't have permission to execute this command.")));
		this.setAliases(main.getConfigManager().commandsConfig.getStringList("commands." + commandName + ".aliases"));
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
		if(!(sender instanceof Player)) {
			return true;
		}
		
		if(main.isBefore1_7) {		
	        if(args.length == 0) {
	        	Player p = (Player)sender;
	            main.rankupLegacy.autoRankup(p);
	        } else if (args.length == 1) {
	        	if(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("true")) {
	        		Player p = (Player)sender;
	        		main.rankupLegacy.autoRankup(p, true);
	        	} else if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("false")) {
	        		Player p = (Player)sender;
	        		main.rankupLegacy.autoRankup(p, false);
	        	}
	        }
			return true;
		}
        if(args.length == 0) {
        	Player p = (Player)sender;
            main.rankupAPI.autoRankup(p);
        } else if (args.length == 1) {
        	if(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("true")) {
        		Player p = (Player)sender;
        		main.rankupAPI.autoRankup(p, true);
        	} else if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("false")) {
        		Player p = (Player)sender;
        		main.rankupAPI.autoRankup(p, false);
        	} else {
        		if(!sender.hasPermission(this.getPermission() + ".other")) {
        			sender.sendMessage(this.getPermissionMessage());
        			return true;
        		}
        		Player target = Bukkit.getPlayer(args[0]);
        		if(target == null) {
        			return true;
        		}
        		if(main.rankupAPI.autoRankup(target)) {
        			String enableMessage = main.messagesStorage.getStringMessage("autorankup-enabled-other");
        			if(enableMessage != null && !enableMessage.isEmpty()) {
        			sender.sendMessage(enableMessage
        					.replace("%player%", target.getName()));
        			}
        		} else {
        			String disableMessage = main.messagesStorage.getStringMessage("autorankup-disabled-other");
        			if(disableMessage != null && !disableMessage.isEmpty()) {
        			sender.sendMessage(disableMessage
        					.replace("%player%", target.getName()));
        			}
        		}
        	}
        } else if (args.length == 2) {
        	if(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("true")) {
        		if(!sender.hasPermission(this.getPermission() + ".other")) {
        			sender.sendMessage(this.getPermissionMessage());
        			return true;
        		}
        		Player target = Bukkit.getPlayer(args[1]);
        		if(target == null) {
        			return true;
        		}
        		main.rankupAPI.autoRankup(target, true);
        		String enableMessage = main.messagesStorage.getStringMessage("autorankup-enabled-other");
    			if(enableMessage != null && !enableMessage.isEmpty()) {
    			sender.sendMessage(enableMessage
    					.replace("%player%", target.getName()));
    			}
        	} else if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("false")) {
        		if(!sender.hasPermission(this.getPermission() + ".other")) {
        			sender.sendMessage(this.getPermissionMessage());
        			return true;
        		}
        		Player target = Bukkit.getPlayer(args[1]);
        		if(target == null) {
        			return true;
        		}
        		main.rankupAPI.autoRankup(target, false);
        		String disableMessage = main.messagesStorage.getStringMessage("autorankup-disabled-other");
    			if(disableMessage != null && !disableMessage.isEmpty()) {
    			sender.sendMessage(disableMessage
    					.replace("%player%", target.getName()));
    			}
        	} else {
        		if(!sender.hasPermission(this.getPermission() + ".other")) {
        			sender.sendMessage(this.getPermissionMessage());
        			return true;
        		}
        		Player target = Bukkit.getPlayer(args[0]);
        		if(target == null) {
        			return true;
        		}
        		if(args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("enable") || args[1].equalsIgnoreCase("true")) {
        			String enableMessage = main.messagesStorage.getStringMessage("autorankup-enabled-other");
        			if(enableMessage != null && !enableMessage.isEmpty()) {
        			sender.sendMessage(enableMessage
        					.replace("%player%", target.getName()));
        			}
        		} else if (args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("disable") || args[1].equalsIgnoreCase("false")) {
        			main.rankupAPI.autoRankup(target, false);
            		String disableMessage = main.messagesStorage.getStringMessage("autorankup-disabled-other");
        			if(disableMessage != null && !disableMessage.isEmpty()) {
        			sender.sendMessage(disableMessage
        					.replace("%player%", target.getName()));
        			}
        		}
        	}
        }
		return true;
	}
}
