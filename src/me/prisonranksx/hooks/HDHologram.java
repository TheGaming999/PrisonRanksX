package me.prisonranksx.hooks;

import java.util.concurrent.ExecutionException;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.prisonranksx.PrisonRanksX;

@SuppressWarnings("deprecation")
public class HDHologram implements IHologram {

	private Hologram hologramHD;
	private PrisonRanksX plugin;
	private Location location;
	@SuppressWarnings("unused")
	private boolean threadSafe;
	@SuppressWarnings("unused")
	private String hologramName;
	
	public HDHologram() {}
	
	@Override
	public IHologram create(PrisonRanksX plugin, String hologramName, Location location, boolean threadSafe) {
		this.plugin = plugin;
		this.location = location;
		this.threadSafe = threadSafe;
		this.hologramName = hologramName;
		if(threadSafe) { 
			createThreadSafe(); 
		} else {
			createNonSafe();
		}
		return this;
	}

	@Override
	public void addLine(String line, boolean threadSafe) {
		if(threadSafe) {
			Bukkit.getScheduler().runTask(plugin, () -> hologramHD.appendTextLine(line));
		} else {
			hologramHD.appendTextLine(line);
		}
	}

	@Override
	public void delete() {
		this.hologramHD.delete();
	}

	@Override
	public void delete(int removeTime) {
		Bukkit.getScheduler().runTaskLater(plugin, () -> this.hologramHD.delete(), 20L * removeTime);
	}
	
	private void createNonSafe() {
		this.hologramHD = HologramsAPI.createHologram(plugin, location);
		this.hologramHD.setAllowPlaceholders(true);
	}
	
	private void createThreadSafe() {
		Bukkit.getScheduler().runTask(plugin, () -> {
		this.hologramHD = HologramsAPI.createHologram(plugin, location);
		this.hologramHD.setAllowPlaceholders(true);  
		});
	}
	
}
