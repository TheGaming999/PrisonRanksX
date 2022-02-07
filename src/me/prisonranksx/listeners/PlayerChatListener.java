package me.prisonranksx.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.RankPath;

public class PlayerChatListener implements IPlayerChatListener, Listener {

	private PrisonRanksX plugin;
	private final String rc = "&r";
	private final String empty = "";
	private final String space = " ";
	
	public PlayerChatListener(PrisonRanksX plugin) {
		this.plugin = plugin;
	}
	
	@Override
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if(plugin.isInDisabledWorld(p)) {
			return;
		}
		String eventFormat = e.getFormat();
		RankPath playerRankPath = null;
		if(plugin.isRankEnabled) playerRankPath = plugin.playerStorage.getPlayerRankPath(p);
		String playerRank = playerRankPath == null ? empty : plugin.getString(plugin.rankStorage.getDisplayName(playerRankPath) + rc);
		String playerPrestige = plugin.playerStorage.getPlayerPrestige(p) != null  && plugin.isPrestigeEnabled ? 
				plugin.getString(plugin.prestigeStorage.getDisplayName(plugin.playerStorage.getPlayerPrestige(p)) + rc) + space: plugin.getString(plugin.globalStorage.getStringData("Options.no-prestige-display"));
		String playerRebirth = plugin.playerStorage.getPlayerRebirth(p) != null && plugin.isRebirthEnabled ? 
				plugin.getString(plugin.rebirthStorage.getDisplayName(plugin.playerStorage.getPlayerRebirth(p)) + rc) + space: plugin.getString(plugin.globalStorage.getStringData("Options.no-rebirth-display"));
		e.setFormat(eventFormat.replace("{rank}", playerRank)
        		.replace("{prestige}", playerPrestige)
        		.replace("{rebirth}", playerRebirth));
	}

}
