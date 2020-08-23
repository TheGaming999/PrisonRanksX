package me.prisonranksx.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncAutoPrestigeEvent extends Event implements Cancellable {
	
	    private Player player;
	    private String prestigeTo;
	    private String prestigeFrom;
	    private boolean isCancelled;
	    private static final HandlerList handlers = new HandlerList();
	    public HandlerList getHandlers() {
	        return handlers;
	    }
	     
	    public static HandlerList getHandlerList() {
	        return handlers;
	    }
	    
	    public AsyncAutoPrestigeEvent(Player player, String prestigeTo, String prestigeFrom) {
	    	super(true);
	    	this.player = player;
	    	this.prestigeFrom = prestigeFrom;
	    	this.prestigeTo = prestigeTo;
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
		
		/**
		 * @return Gets the prestige that the player will prestige from
		 */
		public String getPrestigeFrom() {
			return this.prestigeFrom;
		}
		
		/**
		 * @return Gets the prestige that the player will prestige to
		 */
		public String getPrestigeTo() {
			return this.prestigeTo;
		}
}
