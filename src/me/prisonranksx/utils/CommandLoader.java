package me.prisonranksx.utils;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;

public class CommandLoader {

	public static CommandMap getCommandMapper() {
	  Field bukkitCommandMap = null;
	try {
		bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
	} catch (NoSuchFieldException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SecurityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  bukkitCommandMap.setAccessible(true);
	  CommandMap commandMap = null;
	try {
		commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
	} catch (IllegalArgumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  return commandMap;
	}
	
	public static void registerCommand(String commandName, BukkitCommand bukkitCommandClass) {
		getCommandMapper().register(commandName, bukkitCommandClass);
	}
	
	public static Command unregisterCommand(String commandName) {
		getCommandMapper().getCommand(commandName).unregister(getCommandMapper());
        return getCommandMapper().getCommand(commandName);
	}
	
}
