package me.prisonranksx.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PrestigeUpdateEvent extends Event implements Cancellable {
    private Player player;
    private PrestigeUpdateCause cause;
    private boolean isCancelled;
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
    public PrestigeUpdateEvent(Player player, PrestigeUpdateCause cause) {
    	this.player = player;
    	this.cause = cause;
    	this.isCancelled = false;
    }
	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}
	
	@Override
	public void setCancelled(boolean cancel) {
		this.isCancelled = cancel;
	}
	
	public Player getPlayer() {
		return this.player;
		
	}

	public PrestigeUpdateCause getCause() {
		return this.cause;
	}
}
