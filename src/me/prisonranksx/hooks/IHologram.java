package me.prisonranksx.hooks;

import java.util.List;

import org.bukkit.Location;

import me.prisonranksx.PrisonRanksX;

public interface IHologram {

	public IHologram create(PrisonRanksX plugin, String hologramName, Location location, boolean threadSafe);
	
	public void addLine(String line, boolean threadSafe);
	
	public void addLine(List<String> lines, boolean threadSafe);
	
	public void delete();
	
	public void delete(int removeTime);
	
}
