package me.prisonranksx.data;

import java.util.List;

import org.bukkit.FireworkEffect;

public class FireworkDataHandler {

	private int power;
	private List<FireworkEffect> fireworkEffects;
	
	public FireworkDataHandler() {
		this.power = 1;
		this.fireworkEffects = null;
	}
	
	public void setPower(int power) {
		this.power = power;
	}
	
	public int getPower() {
		return this.power;
	}
	
	public void setFireworkEffects(List<FireworkEffect> fireworkEffects) {
		this.fireworkEffects = fireworkEffects;
	}
	
	public List<FireworkEffect> getFireworkEffects() {
		return this.fireworkEffects;
	}
	
	
	
}
