package me.prisonranksx.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.data.XUser;
import me.prisonranksx.events.AsyncRankRegisterEvent;
import me.prisonranksx.utils.XUUID;

public class PlayerLoginListenerLegacy implements IPlayerLoginListener, Listener {

	private PrisonRanksX plugin;
	
	public PlayerLoginListenerLegacy(PrisonRanksX plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	@Override
	public void onLogin(AsyncPlayerPreLoginEvent e) {
		plugin.scheduler.runTaskAsynchronously(plugin, () -> registerUserData(null, e.getName()));
	}
	
	@EventHandler
	@Override
	public void onJoin(PlayerJoinEvent e) {
		plugin.scheduler.runTaskAsynchronously(plugin, () -> registerUserData(null, e.getPlayer().getName()));
	}
	
	@Override
	public void registerUserData(UUID uuid, String name) {
		if(!plugin.isRankEnabled) {
			return;
		}
		XUser user;
		user = new XUser(XUUID.tryNameConvert(name));
		UUID playerUUID = user.getUUID();
		RankPath defaultRankPath = RankPath.getRankPath(plugin.prxAPI.getDefaultRank(), plugin.prxAPI.getDefaultPath());
		AsyncRankRegisterEvent event = new AsyncRankRegisterEvent(playerUUID, name, defaultRankPath);
		if(!plugin.getPlayerStorage().hasData(playerUUID) && !plugin.getPlayerStorage().isRegistered(playerUUID)) {
			Bukkit.getPluginManager().callEvent(event);
		    if(event.isCancelled()) {
		    	return;
		    }
		    plugin.getPlayerStorage().register(playerUUID, name, true);
		    plugin.prxAPI.setPlayerRankPath(playerUUID, defaultRankPath);
			if(plugin.isMySql()) plugin.updateMySqlData(playerUUID, name);
		} else {
			plugin.getPlayerStorage().loadPlayerData(playerUUID, name);
		}
	}
	
}
