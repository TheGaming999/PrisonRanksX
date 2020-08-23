package me.prisonranksx.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncPrestigeMaxEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private int prestigeStreak;
	private double takenBalance;
	private String finalPrestige;
	private String startingPrestige;
	
	public AsyncPrestigeMaxEvent(Player player, String startingPrestige, String finalPrestige, int prestigeStreak, double takenBalance) {
		super(true);
		this.player = player;
		this.startingPrestige = startingPrestige;
		this.finalPrestige = finalPrestige;
		this.prestigeStreak = prestigeStreak;
		this.takenBalance = takenBalance;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Player getPlayer() {
		return this.player;
	}
	
	public String getFromPrestige() {
		return this.startingPrestige;
	}
	
	public String getFinalPrestige() {
		return this.finalPrestige;
	}
	
	public int getPrestigeStreak() {
		return this.prestigeStreak;
	}
	
	public double getTakenBalance() {
		return this.takenBalance;
	}
	
}
