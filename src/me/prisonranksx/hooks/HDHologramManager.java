package me.prisonranksx.hooks;

import java.util.concurrent.CompletableFuture;

import org.bukkit.Location;

import me.prisonranksx.PrisonRanksX;

public class HDHologramManager implements HologramManager {

	private PrisonRanksX plugin;
	
	public HDHologramManager(PrisonRanksX plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public IHologram createHologram(String hologramName, Location location, boolean threadSafe) {
		return (new HDHologram()).create(plugin, hologramName, location, threadSafe);
	}

	@Override
	public void deleteHologram(IHologram hologram) {
		hologram.delete();
	}

	@Override
	public void deleteHologram(IHologram hologram, int removeTime) {
		hologram.delete(removeTime);
	}

	@Override
	public void addHologramLine(IHologram hologram, String line, boolean threadSafe) {
		try {
			hologram.addLine(line, threadSafe);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

}
