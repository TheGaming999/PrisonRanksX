package me.prisonranksx.events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncRankupMaxEvent extends Event implements Cancellable{
	    private Player player;
	    private String rankup;
	    private String rank;
	    private int streak;
	    private List<String> rankups;
	    private boolean isCancelled;
	    private static final HandlerList handlers = new HandlerList();
	    @Override
	    public HandlerList getHandlers() {
	        return handlers;
	    }
	     
	    public static HandlerList getHandlerList() {
	        return handlers;
	    }
	    public AsyncRankupMaxEvent(Player player,String rank, String rankup, int streak, List<String> rankups) {
	    	super(true);
	    	this.player = player;
	    	this.rank = rank;
	    	this.rankup = rankup;
	    	this.streak = streak;
	    	this.rankups = rankups;
	    	this.isCancelled = false;
	    }
		@Override
		public boolean isCancelled() {
			// TODO Auto-generated method stub
			return this.isCancelled;
		}
        /**
         * Cancels saving player rank data
         * @param cancel true = cancel saving player rank data, false = save player rank data
         * @return
         */
		public void setCancelled(boolean cancel) {
			// TODO Auto-generated method stub
			this.isCancelled = cancel;
		}
		public Player getPlayer() {
			return this.player;
		}
        /**
         * get the player rank when he started the rankupmax process
         * @return rank name
         */
		public String getRankupFrom() {
			return this.rank;
		}
        /**
         * counts how many ranks the player leveled up from the beginning of the rankup max process to final rankup
         * @return rankup streak
         */
		public int getRankupStreak() {
			return this.streak;
		}
		/**
		 * gets the ranks that the player passed and went through.
		 * @return a list of ranks
		 */
		public List<String> getPassedRankups() {
			return rankups;
		}
        /**
         * get the latest rank a player ranked up to.
         * @return rankup name
         */
		public String getFinalRankup() {
			return this.rankup;
		}
}
