package me.prisonranksx.data;

public class PercentageState {

	private String percentage;
	private LevelType levelType;
	
	public PercentageState () {}
	
	public String getPercentage() {
		return this.percentage;
	}
	
	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}
	
	public LevelType getLevelType() {
		return this.levelType;
	}
	
	public void setLevelType(LevelType levelType) {
		this.levelType = levelType;
	}
}
