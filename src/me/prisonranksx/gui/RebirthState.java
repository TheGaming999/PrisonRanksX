package me.prisonranksx.gui;

public class RebirthState {
	private String rebirth;
	private LevelState levelState;
	
	public RebirthState() {}
	
	public RebirthState(String rebirth) {
		this.rebirth = rebirth;
	}
	
	public void setRebirth(String rebirth) {
		this.rebirth = rebirth;
	}
	
	public String getRebirth() {
		return this.rebirth;
	}
	
	public void setLevelState(LevelState levelState) {
		this.levelState = levelState;
	}
	
	public LevelState getLevelState() {
		return this.levelState;
	}
}
