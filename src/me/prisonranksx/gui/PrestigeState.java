package me.prisonranksx.gui;

public class PrestigeState {
	private String prestige;
	private LevelState levelState;
	
	public PrestigeState() {}
	
	public PrestigeState(String prestige) {
		this.prestige = prestige;
	}
	
	public void setPrestige(String prestige) {
		this.prestige = prestige;
	}
	
	public String getPrestige() {
		return this.prestige;
	}
	
	public void setLevelState(LevelState levelState) {
		this.levelState = levelState;
	}
	
	public LevelState getLevelState() {
		return this.levelState;
	}
}
