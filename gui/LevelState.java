package me.prisonranksx.gui;

public enum LevelState { CURRENT, COMPLETED, OTHER;

	   private static LevelState levelState = CURRENT;
		
	   public void setLevelState(LevelState levelState) {
		   LevelState.levelState = levelState;
	   }
	   
	   public LevelState getLevelState() {
		   return levelState;
	   }
}
