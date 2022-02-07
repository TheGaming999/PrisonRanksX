package me.prisonranksx.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.data.XUser;
import me.prisonranksx.events.AsyncRankRegisterEvent;
import net.luckperms.api.model.user.User;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PlayerLoginListener implements IPlayerLoginListener, Listener {

	private PrisonRanksX plugin;
	
	public PlayerLoginListener(PrisonRanksX plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	@Override
	public void onLogin(AsyncPlayerPreLoginEvent e) {
		plugin.scheduler.runTaskAsynchronously(plugin, () -> registerUserData(e.getUniqueId(), e.getName()));
	}
	
	@SuppressWarnings({ "static-access", "deprecation" })
	@EventHandler
	@Override
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
     	UUID playerUUID = p.getUniqueId();
     	String name = p.getName();
		plugin.prxAPI.autoRankupPlayers.remove(name);
		plugin.prxAPI.autoPrestigePlayers.remove(name);
		plugin.scheduler.runTaskLater(plugin, () -> {
		if(plugin.isVaultGroups && plugin.checkVault) {
			if(plugin.vaultPlugin.equalsIgnoreCase("LuckPerms")) {
				plugin.newSharedChain("luckperms").async(() -> {
	    		User lpUser = plugin.lpUtils.getUserQuick(playerUUID);
	    		if(!lpUser.getPrimaryGroup().equalsIgnoreCase(plugin.prxAPI.getPlayerRank(playerUUID))) {
	    			plugin.prxAPI.setPlayerRank(playerUUID, plugin.manager.matchRank(lpUser.getPrimaryGroup()));
	    		}
				}).execute();
	    	}
			else if(plugin.vaultPlugin.equalsIgnoreCase("GroupManager")) {
				String group = plugin.groupManager.getGroup(p);
				if(!group.equalsIgnoreCase(plugin.prxAPI.getPlayerRank(p))) {
					plugin.prxAPI.setPlayerRank(p, group);
				}
			} else if (plugin.vaultPlugin.equalsIgnoreCase("PermissionsEX")) {
				String group = PermissionsEx.getUser(p).getGroups()[0].getName();
				if(!group.equalsIgnoreCase(plugin.prxAPI.getPlayerRank(p))) {
					plugin.prxAPI.setPlayerRank(p, group);
				}
			} else if (plugin.vaultPlugin.equalsIgnoreCase("Vault")) {
				String group = plugin.getPermissions().getPrimaryGroup(p);
				if(!group.equalsIgnoreCase(plugin.prxAPI.getPlayerRank(p))) {
					plugin.prxAPI.setPlayerRank(p, group);
				}
			}
		}
		}, 5);
		if(plugin.isEBProgress)
			plugin.scheduler.runTaskLater(plugin, () -> plugin.ebprogress.enable(p), 120);
		if(!plugin.isABProgress)
			return;
		plugin.scheduler.runTaskLater(plugin, () -> {
			plugin.abprogress.enable(p);
		}, 120);
	}
	
	@Override
	public void registerUserData(UUID uuid, String name) {
		if(!plugin.isRankEnabled) {
			return;
		}
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
		    plugin.prxAPI.setPlayerRankPath(playerUUID, defaultRankPath);
			if(plugin.isMySql()) plugin.updateMySqlData(playerUUID, name);
		} else {
			plugin.getPlayerStorage().loadPlayerData(playerUUID, name);
		}
	}
	
}
