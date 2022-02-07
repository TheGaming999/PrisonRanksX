package me.prisonranksx.hooks;

import java.util.concurrent.ExecutionException;

import org.bukkit.Location;

import me.prisonranksx.PrisonRanksX;

public interface IHologram {

	public abstract Object create(PrisonRanksX plugin, String hologramName, Location location, boolean threadSafe) throws InterruptedException, ExecutionException;
	
	public abstract void addLine(String line, boolean threadSafe) throws IllegalArgumentException, InterruptedException, ExecutionException;
	
	public abstract void delete();
	
	public abstract void delete(int removeTime);
	
}
