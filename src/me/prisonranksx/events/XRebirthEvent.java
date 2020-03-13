package me.prisonranksx.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class XRebirthEvent extends Event implements Cancellable {
    private Player player;
    private String rebirthReason;
    private boolean isCancelled;
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
    public XRebirthEvent(Player player, String rebirthReason) {
    	this.player = player;
    	this.rebirthReason = rebirthReason;
    	this.isCancelled = false;
    	
    }
	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return this.isCancelled;
	}
	@Override
	public void setCancelled(boolean cancel) {
		// TODO Auto-generated method stub
		this.isCancelled = cancel;
	};
	public Player getPlayer() {
		return this.player;
		
	}
	/**
	 * rebirth reasons strings:
	 * - REBIRTHUP
	 * - REBIRTHUP_BY_OTHER
	 * - SETREBIRTH
	 * - RESETREBIRTH
	 * - DELREBIRTH
	 */
	public String getRebirthReason() {
		return this.rebirthReason;
	}
}
