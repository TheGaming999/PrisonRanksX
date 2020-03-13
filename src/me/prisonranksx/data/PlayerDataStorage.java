package me.prisonranksx.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import me.prisonranksx.PrisonRanksX;

public class PlayerDataStorage {
	
	private PrisonRanksX main;
	private Map<String, PlayerDataHandler> playerData;
	
	public PlayerDataStorage(PrisonRanksX main) {this.main = main;
	  this.playerData = new HashMap<String, PlayerDataHandler>();
	}
    
	public Map<String, PlayerDataHandler> getPlayerData() {
		return this.playerData;
	}
	
	
	@SuppressWarnings("deprecation")
	public void loadPlayersData() {
		for(String strg : main.configManager.rankDataConfig.getConfigurationSection("players").getKeys(false)) {
			XUser xu = XUser.getXUser(strg);
			String str = xu.getUUID().toString();
			PlayerDataHandler pdh = new PlayerDataHandler(xu);
			RankPath rankPath = new RankPath(main.configManager.rankDataConfig.getString("players." + str + ".rank"), main.configManager.rankDataConfig.getString("players." + str + ".path"));
			if(main.configManager.prestigeDataConfig.getString("players." + str) != null) {
				pdh.setPrestige(main.configManager.prestigeDataConfig.getString("players."+ str));
			}
			if(main.configManager.rebirthDataConfig.getString("players." + str) != null) {
				pdh.setRebirth(main.configManager.rebirthDataConfig.getString("players." + str));
			}
			pdh.setRankPath(rankPath);
			pdh.setUUID(xu.getUUID());
			getPlayerData().put(str, pdh);
		}
	}
	public void loadPlayerData(OfflinePlayer player) {
		    XUser xu = XUser.getXUser(player);
			PlayerDataHandler pdh = new PlayerDataHandler(xu);
			RankPath rankPath = new RankPath(main.configManager.rankDataConfig.getString("players." + xu.getUUID() + ".rank"), main.configManager.rankDataConfig.getString("players." + xu.getUUID() + ".path"));
			if(main.configManager.prestigeDataConfig.getString("players." + xu.getUUID()) != null) {
				pdh.setPrestige(main.configManager.prestigeDataConfig.getString("players." + xu.getUUID()));
			}
			if(main.configManager.rebirthDataConfig.getString("players." + xu.getUUID()) != null) {
				pdh.setRebirth(main.configManager.rebirthDataConfig.getString("players." + xu.getUUID()));
			}
			pdh.setRankPath(rankPath);
			pdh.setUUID(xu.getUUID());
			getPlayerData().put(xu.getUUID().toString(), pdh);
	}
	
	public boolean isRegistered(OfflinePlayer player) {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()) != null;
	}
	
	public boolean isRegistered(String uuid) {
		return getPlayerData().get(uuid) != null;
	}
	
	public boolean isRegistered(UUID uuid) {
		return getPlayerData().get(uuid.toString()) != null;
	}
	
	public void register(OfflinePlayer player) {
		getPlayerData().put(XUser.getXUser(player).getUUID().toString(), new PlayerDataHandler(XUser.getXUser(player)));
	}
	
	public void register(UUID uuid) {
		getPlayerData().put(uuid.toString(), new PlayerDataHandler(new XUser(uuid)));
	}
	
	public void register(String uuid) {
		getPlayerData().put(uuid, new PlayerDataHandler(XUser.getXUser(uuid)));
	}
	
	public boolean hasRankPath(OfflinePlayer player) {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath() != null;
	}
	
	public boolean hasPrestige(OfflinePlayer player) {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getPrestige() != null;
	}
	
	public boolean hasRebirth(OfflinePlayer player) {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRebirth() != null;
	}
	
	public Set<String> getPlayers() {
		return getPlayerData().keySet();
	}
	
	public String getPlayerRank(OfflinePlayer player) {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath().getRank();
	}
	
	public void setPlayerRank(OfflinePlayer player, String rankName, String pathName) {
		RankPath rankPath = new RankPath(rankName, pathName);
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRankPath(rankPath);
	}
	
	public void setPlayerRank(OfflinePlayer player, String rankName) {
		RankPath rankPath = new RankPath(rankName, getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath().getPath());
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRankPath(rankPath);
	}
	
	public void setPlayerRank(OfflinePlayer player, RankPath rankPath) {
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRankPath(rankPath);
	}
	
	public String getPlayerPrestige(OfflinePlayer player) {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getPrestige();
	}
	
	public void setPlayerPrestige(OfflinePlayer player, String prestigeName) {
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setPrestige(prestigeName);
	}
	
	public String getPlayerRebirth(OfflinePlayer player) {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRebirth();
	}
	
	public void setPlayerRebirth(OfflinePlayer player, String rebirthName) {
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRebirth(rebirthName);
	}
	
	public String getPlayerPath(OfflinePlayer player) {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath().getPath();
	}
	
	public void setPlayerPath(OfflinePlayer player, String pathName) {
		RankPath rankPath = new RankPath(getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath().getRank(), pathName);
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRankPath(rankPath);
	}
	
	public void setPlayerRankPath(OfflinePlayer player, RankPath rankPath) {
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRankPath(rankPath);
	}
	public RankPath getPlayerRankPath(OfflinePlayer player) {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath();
	}
	
	public void savePlayersData() {
			for(Entry<String, PlayerDataHandler> player : getPlayerData().entrySet()) {
				if(player.getKey() != null) {
		main.configManager.rankDataConfig.set("players." + player.getKey() + ".rank", player.getValue().getRankPath().getRank());
		main.configManager.rankDataConfig.set("players." + player.getKey() + ".path", player.getValue().getRankPath().getPath());
		main.configManager.prestigeDataConfig.set("players." + player.getKey(), player.getValue().getPrestige());
		main.configManager.rebirthDataConfig.set("players." + player.getKey(), player.getValue().getRebirth());
				}
			}
	}
	
	public void savePlayerData(OfflinePlayer player) {
		main.configManager.rankDataConfig.set("players." + player.getUniqueId().toString() + ".rank", getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath().getRank());
		main.configManager.rankDataConfig.set("players." + player.getUniqueId().toString() + ".path", getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath().getPath());
		main.configManager.prestigeDataConfig.set("players." + player.getUniqueId().toString(), getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getPrestige());
		main.configManager.rebirthDataConfig.set("players." + player.getUniqueId().toString(), getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRebirth());
	}
	
}
