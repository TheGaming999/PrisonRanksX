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
	private CompletableFuture<IHologram> hologramFuture;
	
	/**
	 * DecentHolograms holograms are async, so holograms are always thread safe.
	 */
	@Override
	public IHologram create(PrisonRanksX plugin, String hologramName, Location location, boolean threadSafe)
			throws InterruptedException, ExecutionException {
		hologramFuture = CompletableFuture.supplyAsync(() -> {
		    this.hologramDH = DHAPI.createHologram(hologramName, location);
		    return this;
		});
		return hologramFuture.get();
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
