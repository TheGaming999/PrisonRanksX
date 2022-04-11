package me.prisonranksx.listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public interface IPlayerQuitListener extends Listener {

	default void unregister() {
		PlayerQuitEvent.getHandlerList().unregister(this);
	}
	
	public void onQuit(PlayerQuitEvent e);
	
}
