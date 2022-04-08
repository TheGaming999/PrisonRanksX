package me.prisonranksx.utils;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;

public class CommandLoader {

	private CommandMap commandMap;

	public CommandLoader() {	  
		Field bukkitCommandMap = null;
		try {
			bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		bukkitCommandMap.setAccessible(true);
		CommandMap commandMap = null;
		try {
			commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
			this.commandMap = commandMap;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}};

		public CommandMap getCommandMap() {
			return commandMap;
		}

		public void registerCommand(String commandName, BukkitCommand bukkitCommandClass) {
			getCommandMap().register(commandName, bukkitCommandClass);
		}

		public Command unregisterCommand(String commandName) {
			getCommandMap().getCommand(commandName).unregister(getCommandMap());
			return getCommandMap().getCommand(commandName);
		}

}
