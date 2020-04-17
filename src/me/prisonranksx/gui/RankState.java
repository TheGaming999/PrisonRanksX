package me.prisonranksx.gui;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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
	
	public String toString() {
		return "[path:" + rankPath.get() + "]||[state:" + levelState.name() + "]";
	}
	
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
            append(rankPath).
            append(levelState).
            toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
       if (!(obj instanceof RankState))
            return false;
        if (obj == this)
            return true;

        RankState rhs = (RankState) obj;
        return new EqualsBuilder().
            append(rankPath, rhs.rankPath).
            append(levelState, rhs.levelState).
            isEquals();
    }
	
}
