package me.prisonranksx.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.api.PRXAPI;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.data.XUser;
import me.prisonranksx.events.AsyncRankRegisterEvent;
import me.prisonranksx.utils.OnlinePlayers;

public class PlayerLoginListener implements IPlayerLoginListener {

	private PrisonRanksX plugin;

	public PlayerLoginListener(PrisonRanksX plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	@Override
	public void onLogin(AsyncPlayerPreLoginEvent e) {
		plugin.scheduler.runTaskAsynchronously(plugin, () -> registerUserData(e.getUniqueId(), e.getName()));
	}

	@EventHandler
	@Override
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		OnlinePlayers.add(p);
		String name = p.getName();
		PRXAPI.AUTO_RANKUP_PLAYERS.remove(name);
		PRXAPI.AUTO_PRESTIGE_PLAYERS.remove(name);
		plugin.scheduler.runTaskLater(plugin, () -> {
			if(plugin.isVaultGroups && plugin.checkVault) {
				plugin.vaultDataUpdater.update(p);
			}
		}, 5);
		if(plugin.isEBProgress)
			plugin.scheduler.runTaskLater(plugin, () -> plugin.ebprogress.enable(p), 120);
		if(!plugin.isABProgress)
			return;
		plugin.scheduler.runTaskLater(plugin, () -> {
			plugin.abprogress.enable(p);
		}, 20);
	}

	@Override
	public void registerUserData(UUID uuid, String name) {
		XUser user;
		user = new XUser(uuid);
		UUID playerUUID = user.getUUID();
		RankPath defaultRankPath = RankPath.getRankPath(plugin.prxAPI.getDefaultRank(), plugin.prxAPI.getDefaultPath());
		AsyncRankRegisterEvent event = new AsyncRankRegisterEvent(playerUUID, name, defaultRankPath);
		if(!plugin.getPlayerStorage().hasData(playerUUID) && !plugin.getPlayerStorage().isRegistered(playerUUID)) {
			Bukkit.getPluginManager().callEvent(event);
			if(event.isCancelled()) {
				return;
			}
			plugin.getPlayerStorage().register(playerUUID, name, true);
			plugin.prxAPI.setPlayerRankPath(playerUUID, plugin.isRankEnabled ? defaultRankPath : null);
			if(plugin.isMySql()) plugin.updateMySqlData(playerUUID, name);
		} else {
			plugin.getPlayerStorage().loadPlayerData(playerUUID, name);
		}
	}

}
