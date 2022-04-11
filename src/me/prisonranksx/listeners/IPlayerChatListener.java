package me.prisonranksx.listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public interface IPlayerChatListener extends Listener {

	default void unregister() {
		AsyncPlayerChatEvent.getHandlerList().unregister(this);
	}
	
	public void onChat(AsyncPlayerChatEvent e);
	
}
