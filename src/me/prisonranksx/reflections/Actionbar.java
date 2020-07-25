package me.prisonranksx.reflections;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public interface Actionbar {
  
	void sendActionBar(@Nonnull Player player, @Nullable String message);
	void sendPlayersActionBar(@Nullable String message);
	void sendActionBarWhile(@Nonnull JavaPlugin plugin, @Nonnull Player player, @Nullable String message, @Nonnull Callable<Boolean> callable);
	void sendActionBarWhile(@Nonnull JavaPlugin plugin, @Nonnull Player player, @Nullable Callable<String> message, @Nonnull Callable<Boolean> callable);
	void sendActionBar(@Nonnull JavaPlugin plugin, @Nonnull Player player, @Nullable String message, long duration);
	
}
