package me.prisonranksx.workloads;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;

public class PrestigeWorkload implements Workload {

	private PrisonRanksX plugin = PrisonRanksX.getInstance();
	private Player player;
	
	public PrestigeWorkload() {
		this.player = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
	}
	
	public PrestigeWorkload(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public Player setPlayer(Player player) {
		return this.player = player;
	}
	
	@Override
	public void compute() {
		if(player == null) {
			plugin.debug("player is null");
			return;
		}
		plugin.getPrestigeMax().executeInfinite(this.player);
	}

}
