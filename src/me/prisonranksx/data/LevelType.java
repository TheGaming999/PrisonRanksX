package me.prisonranksx.data;

public enum LevelType {
   RANK,PRESTIGE,REBIRTH,OTHER,UNKNOWN;
   
   private static LevelType levelType = UNKNOWN;
	
   public void setLevelType(LevelType levelType) {
	   LevelType.levelType = levelType;
   }
   
   public LevelType getLevelType() {
	   return levelType;
   }
   
}

