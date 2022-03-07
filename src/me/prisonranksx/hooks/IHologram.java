package me.prisonranksx.hooks;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Location;

import me.prisonranksx.PrisonRanksX;

public interface IHologram {

	public abstract IHologram create(PrisonRanksX plugin, String hologramName, Location location, boolean threadSafe);
	
	public abstract void addLine(String line, boolean threadSafe);
	
	public abstract void addLine(List<String> lines, boolean threadSafe);
	
	public abstract void delete();
	
	public abstract void delete(int removeTime);
	
}
