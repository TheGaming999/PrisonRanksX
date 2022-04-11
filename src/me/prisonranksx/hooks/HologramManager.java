package me.prisonranksx.hooks;

import org.bukkit.Location;

public interface HologramManager {

	public IHologram createHologram(String hologramName, Location location, boolean threadSafe);
	
	public void deleteHologram(IHologram hologram);
	
	public void deleteHologram(IHologram hologram, int removeTime);
	
	public void addHologramLine(IHologram hologram, String line, boolean threadSafe);
	
}
