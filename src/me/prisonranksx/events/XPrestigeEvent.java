package me.prisonranksx.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class XPrestigeEvent extends Event implements Cancellable {
    private Player player;
    private String prestigereason;
    private boolean isCancelled;
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
    public XPrestigeEvent(Player player, String prestigereason) {
    	this.player = player;
    	this.prestigereason = prestigereason;
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
	 * prestige reasons strings:
	 * - PRESTIGEUP
	 * - PRESTIGEUP_BY_OTHER
	 * - SETPRESTIGE
	 * - RESETPRESTIGE
	 * - DELPRESTIGE
	 */
	public String getPrestigeReason() {
		return this.prestigereason;
	}
}
