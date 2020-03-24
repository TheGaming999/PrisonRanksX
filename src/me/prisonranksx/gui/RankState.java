package me.prisonranksx.gui;

import me.prisonranksx.data.RankPath;

public class RankState {

	private RankPath rankPath;
	private LevelState levelState;
	
	public RankState() {}
	
	public RankState(RankPath rankPath) {
		this.rankPath = rankPath;
	}
	
	public void setRankPath(RankPath rankPath) {
		this.rankPath = rankPath;
	}
	
	public RankPath getRankPath() {
		return this.rankPath;
	}
	
	public void setLevelState(LevelState levelState) {
		this.levelState = levelState;
	}
	
	public LevelState getLevelState() {
		return this.levelState;
	}
	
}
