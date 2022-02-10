package me.prisonranksx.hooks;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;


import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.prisonranksx.PrisonRanksX;

public class DHHologram implements IHologram {

	private Hologram hologramDH;
	private CompletableFuture<DHHologram> hologramFuture;
	
	/**
	 * DecentHolograms holograms are async, so holograms are always thread safe.
	 */
	@Override
	public IHologram create(PrisonRanksX plugin, String hologramName, Location location, boolean threadSafe) {
		hologramFuture = CompletableFuture.supplyAsync(() -> {
		    this.hologramDH = DHAPI.createHologram(hologramName, location);
		    return this;
		});
		try {
			return hologramFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void addLine(String line, boolean threadSafe) {
		hologramFuture.thenRunAsync(() -> {
			try {
				DHAPI.addHologramLine(this.hologramDH, line);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} 
		});
	}

	@Override
	public void delete() {
		hologramFuture.thenRunAsync(() -> hologramDH.delete());
	}

	@Override
	public void delete(int removeTime) {
		hologramFuture.thenRunAsync(() -> {
			try {
				TimeUnit.SECONDS.sleep(removeTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			hologramDH.delete();
		});
	}
	
}
