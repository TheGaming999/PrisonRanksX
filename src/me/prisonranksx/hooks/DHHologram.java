package me.prisonranksx.hooks;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Location;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.prisonranksx.PrisonRanksX;

public class DHHologram implements IHologram {

	private Hologram hologramDH;
	private PrisonRanksX plugin;

	/**
	 * DecentHolograms holograms are async, so holograms are always thread safe.
	 */
	@Override
	public IHologram create(PrisonRanksX plugin, String hologramName, Location location, boolean threadSafe) {
		if(threadSafe) {
			return CompletableFuture.supplyAsync(() -> {
				DHHologram holo = new DHHologram();
				holo.plugin = plugin;
				holo.hologramDH = DHAPI.createHologram(hologramName, location);
				return holo;
			}).join();
		} else {
			DHHologram holo = new DHHologram();
			holo.plugin = plugin;
			holo.hologramDH = DHAPI.createHologram(hologramName, location);
			return holo;
		}
	}

	@Override
	public void addLine(String line, boolean threadSafe) {
		try {
			DHAPI.addHologramLine(this.hologramDH, line);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} 
	}

	@Override
	public void addLine(List<String> line, boolean threadSafe) {
		try {
			line.forEach(li -> {
				DHAPI.addHologramLine(this.hologramDH, li);
			});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} 
	}
	
	@Override
	public void delete() {
		hologramDH.delete();
	}

	@Override
	public void delete(int removeTime) {
		plugin.scheduler.runTaskLater(plugin, () -> {
			hologramDH.delete();
		}, removeTime * 20);
	}

}
