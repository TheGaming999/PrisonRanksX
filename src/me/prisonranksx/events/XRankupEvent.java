package me.prisonranksx.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class XRankupEvent extends Event implements Cancellable{
    private Player player;
    private RankupAction rankupaction;
    private boolean isCancelled;
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
    public XRankupEvent(Player player, RankupAction rankupaction) {
    	this.player = player;
    	this.rankupaction = rankupaction;
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
	public RankupAction getRankupAction() {
		return this.rankupaction;
	}
	public void setRankupAction(RankupAction rankupaction) {
		this.rankupaction = rankupaction;
	}
}
