package me.prisonranksx.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.utils.OnlinePlayers;

public class ForceRankupCommand extends BukkitCommand {

	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	public ForceRankupCommand(String commandName) {
		super(commandName);
		this.setDescription(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".description", "force promote other players without losing anything")));
		this.setUsage(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".usage", "/forcerankup [player]")));
		this.setPermission(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".permission", "prisonranksx.forcerankup"));
		this.setPermissionMessage(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".permission-message", "&cYou don't have permission to execute this command.")));
		this.setAliases(main.getConfigManager().commandsConfig.getStringList("commands." + commandName + ".aliases"));
	}
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(!main.isRankEnabled) {
			return true;
		}
		if(!sender.hasPermission(this.getPermission())) {
			sender.sendMessage(this.getPermissionMessage());
			return true;
		}
		if(main.isBefore1_7) {
	        if(args.length == 0) {
	        	sender.sendMessage(main.prxAPI.g("forcerankup-noargs"));
	        	return true;
	        } else if (args.length == 1) {
	        	if(args[0].equalsIgnoreCase("*")) {
	        		for(Player player : OnlinePlayers.getEveryPlayer()) {
	        			main.rankupLegacy.forceRankup(player, sender);
	        		}
	        		return true;
	        	}
	        	if(Bukkit.getPlayer(args[0]) == null) {
	        		sender.sendMessage(main.prxAPI.g("playernotfound").replace("%player%", args[0]));
	        		return true;
	        	}
	        	main.rankupLegacy.forceRankup(Bukkit.getPlayer(args[0]), sender);
	        }
			return true;
		}
        if(args.length == 0) {
        	sender.sendMessage(main.prxAPI.g("forcerankup-noargs"));
        	return true;
        } else if (args.length == 1) {
        	if(args[0].equalsIgnoreCase("*")) {
        		for(Player player : OnlinePlayers.getEveryPlayer()) {
        			main.rankupAPI.forceRankup(player, sender);
        		}
        		return true;
        	}
        	if(Bukkit.getPlayer(args[0]) == null) {
        		sender.sendMessage(main.prxAPI.g("playernotfound").replace("%player%", args[0]));
        		return true;
        	}
        	main.rankupAPI.forceRankup(Bukkit.getPlayer(args[0]), sender);
        }
		return true;
	}
	
}
