package me.prisonranksx.gui;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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
	
    @Override
    public int hashCode() {
        return new HashCodeBuilder(18, 31).
            append(rebirth).
            append(levelState).
            toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
       if (!(obj instanceof RebirthState))
            return false;
        if (obj == this)
            return true;

        RebirthState rhs = (RebirthState) obj;
        return new EqualsBuilder().
            append(rebirth, rhs.rebirth).
            append(levelState, rhs.levelState).
            isEquals();
    }
    
	public String toString() {
		return "[path:" + rebirth + "]||[state:" + levelState.name() + "]";
	}
}
