package me.prisonranksx.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.events.AsyncAutoPrestigeEvent;
import me.prisonranksx.events.AsyncAutoRankupEvent;
import me.prisonranksx.events.AsyncPrestigeMaxEvent;
import me.prisonranksx.events.AsyncRankupMaxEvent;
import me.prisonranksx.events.PrePrestigeMaxEvent;
import me.prisonranksx.events.PrestigeUpdateEvent;
import me.prisonranksx.events.RankUpdateEvent;
import me.prisonranksx.events.RebirthUpdateEvent;
import me.prisonranksx.utils.TimeCounter;

public class PrisonRanksXListener implements Listener {

	private PrisonRanksX plugin;
	private TimeCounter timeCounter;

	public PrisonRanksXListener(PrisonRanksX plugin) {
		this.plugin = plugin;
	}

	public void unregister() {
		RankUpdateEvent.getHandlerList().unregister(this);
		AsyncAutoRankupEvent.getHandlerList().unregister(this);
		AsyncRankupMaxEvent.getHandlerList().unregister(this);
		PrestigeUpdateEvent.getHandlerList().unregister(this);
		RebirthUpdateEvent.getHandlerList().unregister(this);
		AsyncPrestigeMaxEvent.getHandlerList().unregister(this);
		AsyncAutoPrestigeEvent.getHandlerList().unregister(this);
		PrePrestigeMaxEvent.getHandlerList().unregister(this);
	}

	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onRankup(RankUpdateEvent e) {
		Player p = e.getPlayer();
		forceSave(p);
		updateVaultData(p, plugin.prxAPI.getPlayerRank(p), e.getRankup());
	}

	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onAutoRankup(AsyncAutoRankupEvent e) {
		Player p = e.getPlayer();
		forceSave(p);
		updateVaultData(p, e.getRankupFrom(), e.getRankupTo());
	}

	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onRankupMax(AsyncRankupMaxEvent e) {
		Player p = e.getPlayer();
		forceSave(p);
		updateVaultData(p, e.getRankupFrom(), e.getFinalRankup());
	}

	@EventHandler
	public void onPrestige(PrestigeUpdateEvent e) {
		Player p = e.getPlayer();
		updateOnPrestige(p);
		forceSave(p);
	}

	@EventHandler
	public void onRebirth(RebirthUpdateEvent e) {
		forceSave(e.getPlayer());
	}

	@EventHandler
	public void onPrestigeMax(AsyncPrestigeMaxEvent e) {
		plugin.debug("Time taken on prestige max: " + timeCounter.tryEndingAsSecondsFormatted());
		Player p = e.getPlayer();
		updateOnPrestige(p);
		forceSave(p);
	}

	@EventHandler
	public void onAutoPrestige(AsyncAutoPrestigeEvent e) {
		Player p = e.getPlayer();
		updateOnPrestige(p);
		forceSave(p);
	}

	@EventHandler
	public void onPrePrestigeMax(PrePrestigeMaxEvent e) {
		timeCounter = new TimeCounter(true);
	}

	public void updateVaultData(Player p, String currentRank, String nextRank) {
		if(plugin.isVaultGroups) plugin.vaultDataUpdater.set(p, nextRank, currentRank);
	}

	public void updateOnPrestige(Player p) {
		if(plugin.isVaultGroups && plugin.checkVault) {
			if(plugin.getGlobalStorage().getBooleanData("PrestigeOptions.ResetRank")) {
				plugin.vaultDataUpdater.remove(p, plugin.prxAPI.getPlayerRank(p));
			}
		}
	}

	public void forceSave(Player player) {
		if(plugin.isForceSave()) plugin.saveDataAsynchronously(player.getUniqueId(), player.getName());
	}

}
