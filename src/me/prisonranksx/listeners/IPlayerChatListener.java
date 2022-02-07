package me.prisonranksx.listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public interface IPlayerChatListener extends Listener {

	public void onChat(AsyncPlayerChatEvent e);
	
}
