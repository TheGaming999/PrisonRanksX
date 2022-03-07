package me.prisonranksx.hooks;


import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.utils.AtomicObject;

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
		HDHologram holo = new HDHologram();
		holo.plugin = plugin;
		holo.location = location;
		holo.threadSafe = threadSafe;
		holo.hologramName = hologramName;
		if(threadSafe) {
			holo.createThreadSafe();
		} else {
			holo.createNonSafe();
		}
		return holo;
	}

	@Override
	public void addLine(String line, boolean threadSafe) {
		if(threadSafe) {
			plugin.scheduler.runTaskLater(plugin, () -> hologramHD.appendTextLine(line), 1);
		} else {
			hologramHD.appendTextLine(line);
		}
	}

	@Override
	public void addLine(List<String> line, boolean threadSafe) {
		if(threadSafe) {
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				line.forEach(hologramHD::appendTextLine);
			}, 1);
		} else {
			line.forEach(hologramHD::appendTextLine);
		}
	}
	
	@Override
	public void delete() {
		plugin.scheduler.runTask(plugin, () -> this.hologramHD.delete());
	}

	@Override
	public void delete(int removeTime) {
		plugin.scheduler.runTaskLater(plugin, () -> this.hologramHD.delete(), 20L * removeTime);
	}

	@SuppressWarnings("unused")
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
