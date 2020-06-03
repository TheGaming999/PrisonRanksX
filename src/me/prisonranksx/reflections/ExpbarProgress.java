package me.prisonranksx.reflections;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Sets;

import me.clip.placeholderapi.PlaceholderAPI;
import me.prisonranksx.PrisonRanksX;

public class ExpbarProgress {

	private Set<UUID> players;
	private PrisonRanksX main;
	private boolean isTaskOn;
	private String expbarFormat;
	private int expbarUpdater;
	private BukkitTask scheduler;
	
	public ExpbarProgress(PrisonRanksX main) {
		this.main = main;
		this.players = Sets.newConcurrentHashSet();
		this.isTaskOn = false;
		this.expbarFormat = this.main.globalStorage.getStringData("Options.expbar-progress-format");
		this.expbarUpdater = this.main.globalStorage.getIntegerData("Options.expbar-progress-updater");
	}
	
	public void setExpbarFormat(String expbarFormat) {
		this.expbarFormat = expbarFormat;
	}
	
	public String getExpbarFormat() {
		return this.expbarFormat;
	}
	
	public Set<UUID> getPlayers() {
		return this.players;
	}
	
	public void enable(Player p) {
      players.add(p.getUniqueId());
      if(!isTaskOn) {
    	  isTaskOn = true;
    	  startProgressTask();
      }
	}
	
	public void disable(Player p) {
	  players.remove(p.getUniqueId());
	  if(players.size() == 0 && isTaskOn) {
		  isTaskOn = false;
		  scheduler.cancel();
	  }
	}
	
	public void clear() {
		players.clear();
	}
	
	public void clear(boolean completely) {
		players.clear();
		if(completely) {
		  if(isTaskOn) {
			  isTaskOn = false;
			  scheduler.cancel();
		  }
		}
	}
	
	private void startProgressTask() {
		scheduler = Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
			for(UUID u : players) {
				Player p = Bukkit.getPlayer(u);
				int levels = Integer.valueOf(main.prxAPI.getPlayerRankupPercentageSafe(p));
				p.setExp(Float.valueOf(levels) / 100);
				//Experience.changeExp(p, levels);
			    p.setLevel(levels);
				//p.setExp(Float.valueOf(PlaceholderAPI.setPlaceholders(p, expbarFormat)));
			}
		}, expbarUpdater, expbarUpdater);
	}
	
	
}

