package me.prisonranksx.events;

import java.util.UUID;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.prisonranksx.data.RankPath;

public class AsyncRankRegisterEvent extends Event implements Cancellable {
	
    private UUID uuid;
    private String name;
    private RankPath rankPath;
    private boolean isCancelled;
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public AsyncRankRegisterEvent(UUID uuid, String name, RankPath rankPath) {
    	super(true);
        this.uuid = uuid;
        this.name = name;
        this.rankPath = rankPath;
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
	
	/**
	 * 
	 * @return player name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * 
	 * @return player uuid
	 */
	public UUID getUniqueId() {
		return this.uuid;
	}
	
	/**
	 * 
	 * @return registered rankpath
	 */
	public RankPath getRankPath() {
		return this.rankPath;
	}
	
}
