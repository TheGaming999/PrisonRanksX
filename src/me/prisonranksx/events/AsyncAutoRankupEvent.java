package me.prisonranksx.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncAutoRankupEvent extends Event implements Cancellable {
	
	    private Player player;
	    private String rankupTo;
	    private String rankupFrom;
	    private boolean isCancelled;
	    private static final HandlerList handlers = new HandlerList();
	    public HandlerList getHandlers() {
	        return handlers;
	    }
	     
	    public static HandlerList getHandlerList() {
	        return handlers;
	    }
	    
	    public AsyncAutoRankupEvent(Player player, String rankupTo, String rankupFrom) {
	    	super(true);
	    	this.player = player;
	    	this.rankupFrom = rankupFrom;
	    	this.rankupTo = rankupTo;
	    	this.isCancelled = false;
	    	
	    }

		public boolean isCancelled() {
			// TODO Auto-generated method stub
			return this.isCancelled;
		}

		public void setCancelled(boolean cancel) {
			// TODO Auto-generated method stub
			this.isCancelled = cancel;
		}
		public Player getPlayer() {
			return this.player;
			
		}
		/*
		 * Gets the rank that the player ranked up from
		 */
		public String getRankupFrom() {
			return this.rankupFrom;
		}
		/*
		 * Gets the rank that the player ranked up to
		 */
		public String getRankupTo() {
			return this.rankupTo;
		}
}
