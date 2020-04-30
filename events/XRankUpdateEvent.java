package me.prisonranksx.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class XRankUpdateEvent extends Event implements Cancellable{
    private Player player;
    private RankUpdateCause rankUpdateCause;
    private String rankup;
    private boolean isCancelled;
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
    public XRankUpdateEvent(Player player, RankUpdateCause rankUpdateCause, String rankup) {
    	this.player = player;
    	this.rankUpdateCause = rankUpdateCause;
    	this.isCancelled = false;
    	this.rankup = rankup;
    }
    
    public XRankUpdateEvent(Player player, RankUpdateCause rankUpdateCause) {
    	this.player = player;
    	this.rankUpdateCause = rankUpdateCause;
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
	
	public String getRankup() {
		return this.rankup;
	}
	
	public void setRankup(String rankup) {
		this.rankup = rankup;
	}
	
	public RankUpdateCause getCause() {
		return this.rankUpdateCause;
	}
	
	public void setRankUpdateCause(RankUpdateCause rankUpdateCause) {
		this.rankUpdateCause = rankUpdateCause;
	}
	
}
