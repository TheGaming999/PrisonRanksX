package me.prisonranksx.listeners;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.api.PRXAPI;
import me.prisonranksx.utils.OnlinePlayers;
import me.prisonranksx.utils.XUUID;

public class PlayerQuitListenerLegacy implements IPlayerQuitListener {

	private PrisonRanksX plugin;
	
	public PlayerQuitListenerLegacy(PrisonRanksX plugin) {
		this.plugin = plugin;
	}
	
	@Override
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		OnlinePlayers.delete(p);
		String name = p.getName();
		PRXAPI.AUTO_RANKUP_PLAYERS.remove(name);
		PRXAPI.AUTO_PRESTIGE_PLAYERS.remove(name);
		PRXAPI.TASKED_PLAYERS.remove(name);
		if(plugin.getPrestigeMax().isProcessing(name)) plugin.getPrestigeMax().sendStopSignal(name);		
		plugin.rankupMaxLegacy.rankupMaxProcess.remove(p);
		if(plugin.isSaveOnLeave) {
			XUUID atomicUUID = new XUUID();
			atomicUUID.parseUUID(name);
			plugin.getTaskChainFactory().newSharedChain("dataSave").async(() -> {
				UUID uuidNew = atomicUUID.uuid();
				if(plugin.isMySql()) {
					plugin.updateMySqlData(uuidNew, name);
				} else {	
					plugin.getPlayerStorage().savePlayerData(uuidNew);
					plugin.getConfigManager().saveRankDataConfig();
					plugin.getConfigManager().savePrestigeDataConfig();
					plugin.getConfigManager().saveRebirthDataConfig();
				}
				plugin.getPlayerStorage().unload(uuidNew);
			}).execute();
		}
		if(plugin.isEBProgress) plugin.ebprogress.disable(p);
	}

}
