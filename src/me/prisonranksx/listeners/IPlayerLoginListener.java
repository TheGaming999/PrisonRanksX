package me.prisonranksx.listeners;

import java.util.UUID;

import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public interface IPlayerLoginListener extends Listener {

	default void unregister() {
		AsyncPlayerPreLoginEvent.getHandlerList().unregister(this);
		PlayerJoinEvent.getHandlerList().unregister(this);
	}
	
	public void onLogin(AsyncPlayerPreLoginEvent e);
	
	public void onJoin(PlayerJoinEvent e);
	
	public void registerUserData(UUID uuid, String name);
	
}
