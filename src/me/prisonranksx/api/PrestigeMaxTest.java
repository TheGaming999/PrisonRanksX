package me.prisonranksx.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.workloads.PrestigeWorkload;
import me.prisonranksx.workloads.WorkloadRunnable;

public class PrestigeMaxTest {

	private PrisonRanksX plugin;
	private final WorkloadRunnable workloadRunnable = new WorkloadRunnable();
	
	public PrestigeMaxTest(PrisonRanksX plugin) {
		this.plugin = plugin;
	}
	
	public void run(Player p) {
		plugin.debug("Run player.");
		PrestigeWorkload runnable = new PrestigeWorkload();
		this.workloadRunnable.addWorkload(runnable);
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this.workloadRunnable, 1, 1);
	}


	
}
