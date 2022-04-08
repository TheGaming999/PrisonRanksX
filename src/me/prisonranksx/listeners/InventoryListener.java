package me.prisonranksx.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import cloutteam.samjakob.gui.types.PaginatedGUI;
import me.prisonranksx.PrisonRanksX;

public class InventoryListener implements Listener {

	private PrisonRanksX plugin;

	public InventoryListener(PrisonRanksX plugin) {
		this.plugin = plugin;
	}

	public void unregister() {
		InventoryClickEvent.getHandlerList().unregister(this);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if(inv == null) return;
		InventoryHolder holder = inv.getHolder();
		if(holder == null) return;
		if(holder instanceof PaginatedGUI) {
			e.setResult(org.bukkit.event.Event.Result.DENY);
			e.setCancelled(true);
			plugin.debug("Yes it's a prisonranksx inventory");
		}
	}

}
