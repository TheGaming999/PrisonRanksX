package me.prisonranksx.data;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

public class PlayerDataHandler {

	private XUser player;
	private String rank;
	private String prestige;
	private String path;
	private RankPath rankPath;
	private String rebirth;
	private UUID uuid;
	
	public PlayerDataHandler(XUser player) {this.player = player;}
	
	@Deprecated
	public void setRank(String newRank) {this.rank = newRank;}
	
	public void setPrestige(String newPrestige) {this.prestige = newPrestige;}
	
	@Deprecated
	public void setPath(String newPath) {this.path = newPath;}
	
	public void setRankPath(RankPath rankPath) {this.rankPath = rankPath;}
	
	public void setRebirth(String newRebirth) {this.rebirth = newRebirth;}
	
	public void setUUID(UUID uuid) {this.uuid = uuid;}
	
	@Deprecated
	public String getRank() {
		return rank;
	}
	
	public String getPrestige() {
		return prestige;
	}
	
	@Deprecated
	public String getPath() {
		return path;
	}
	
	public RankPath getRankPath() {
		return rankPath;
	}
	
	public String getRebirth() {
		return rebirth;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public XUser getUser() {
		return this.player;
	}
}
