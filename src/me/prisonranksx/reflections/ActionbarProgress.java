package me.prisonranksx.reflections;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Sets;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.utils.XMaterial;

public class ActionbarProgress {

	private Set<UUID> players;
	private PrisonRanksX main;
	private boolean isTaskOn;
	private String actionbarMessage;
	private int actionbarUpdater;
	private BukkitTask scheduler;
	private boolean actionbarProgressOnlyPickaxe;
	private Material diamondPickaxe;
	
	public ActionbarProgress(PrisonRanksX main) {
		this.main = main;
		this.players = Sets.newConcurrentHashSet();
		this.isTaskOn = false;
		this.actionbarMessage = this.main.globalStorage.getStringData("Options.actionbar-progress-format");
		this.actionbarUpdater = this.main.globalStorage.getIntegerData("Options.actionbar-progress-updater");
		this.actionbarProgressOnlyPickaxe = this.main.globalStorage.getBooleanData("Options.actionbar-progress-only-pickaxe");
		this.diamondPickaxe = XMaterial.DIAMOND_PICKAXE.parseMaterial();
	}
	
	public boolean isOnlyPickaxe() {
		return this.actionbarProgressOnlyPickaxe;
	}
	
	public void setOnlyPickaxe(boolean onlyPickaxe) {
		this.actionbarProgressOnlyPickaxe = onlyPickaxe;
	}
	
	public void setDiamondPickaxe(Material material) {
		this.diamondPickaxe = material;
	}
	
	public Material getDiamondPickaxe() {
		return this.diamondPickaxe;
	}
	
	public void setActionbarMessage(String actionbarMessage) {
		this.actionbarMessage = actionbarMessage;
	}
	
	public String getActionbarMessage() {
		return this.actionbarMessage;
	}
	
	public Set<UUID> getPlayers() {
		return this.players;
	}
	
	public void enable(Player p) {
      players.add(p.getUniqueId());
      if(!isTaskOn) {
    	  isTaskOn = true;
    	  if(isOnlyPickaxe()) {
    		  startProgressTaskAdvanced();
    		  return;
    	  }
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
	
	public boolean isHoldingDiamondPickaxe(Player player) {
		Player p = player;
		if(p == null || !p.isOnline()) {
			return false;
		}
		if(p.getItemInHand() == null) {
			return false;
		}
		return p.getItemInHand().getType() == diamondPickaxe;
	}
	
	private void startProgressTask() {
		scheduler = Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
			for(UUID u : players) {
				if(main.hasActionbarOn(u)) {
					return;
				}
				Player p = Bukkit.getPlayer(u);
				Actionbar.sendActionBar(p, main.getString(actionbarMessage, p.getName()));
			}
		}, actionbarUpdater, actionbarUpdater);
	}
	
	private void startProgressTaskAdvanced() {
		scheduler = Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
			for(UUID u : players) {
				if(main.hasActionbarOn(u)) {
					return;
				}
				Player p = Bukkit.getPlayer(u);
				if(isHoldingDiamondPickaxe(p)) {
				Actionbar.sendActionBar(p, main.getString(actionbarMessage, p.getName()));
				}
			}
		}, actionbarUpdater, actionbarUpdater);
	}
	
}
