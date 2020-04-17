package me.prisonranksx.gui;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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
	
    @Override
    public int hashCode() {
        return new HashCodeBuilder(18, 31).
            append(prestige).
            append(levelState).
            toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
       if (!(obj instanceof PrestigeState))
            return false;
        if (obj == this)
            return true;

        PrestigeState rhs = (PrestigeState) obj;
        return new EqualsBuilder().
            append(prestige, rhs.prestige).
            append(levelState, rhs.levelState).
            isEquals();
    }
    
	public String toString() {
		return "[path:" + prestige + "]||[state:" + levelState.name() + "]";
	}
	
}
