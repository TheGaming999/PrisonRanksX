package me.prisonranksx.hooks;

import org.bukkit.Location;

import me.prisonranksx.PrisonRanksX;

public interface IHologram {

	public abstract Object create(PrisonRanksX plugin, String hologramName, Location location, boolean threadSafe);
	
	public abstract void addLine(String line, boolean threadSafe);
	
	public abstract void delete();
	
	public abstract void delete(int removeTime);
	
}
