package me.prisonranksx.data;

import java.util.UUID;

import javax.annotation.Nullable;

@Nullable
public class PlayerDataHandler {

	private XUser player;
	@SuppressWarnings("unused")
	private String rank;
	private String prestige;
	@SuppressWarnings("unused")
	private String path;
	private RankPath rankPath;
	private String rebirth;
	private UUID uuid;
	private String name;

	public PlayerDataHandler(XUser player) {this.player = player;}

	@Deprecated
	public void setRank(String newRank) {this.rank = newRank;}

	public void setPrestige(String newPrestige) {
		this.prestige = newPrestige == "none" ? null : newPrestige;
	}

	@Deprecated
	public void setPath(String newPath) {this.path = newPath;}

	public void setRankPath(RankPath rankPath) {this.rankPath = rankPath;}

	public void setRebirth(String newRebirth) {
		this.rebirth = newRebirth == "none" ? null : newRebirth;
	}

	public void setUUID(UUID uuid) {this.uuid = uuid;}

	@Deprecated
	public String getRank() {
		return rankPath.getRankName();
	}

	public String getPrestige() {
		return prestige;
	}

	@Deprecated
	public String getPath() {
		return rankPath.getPathName();
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

	public String toString() {
		String rank = this.rankPath.getRankName();
		String path = this.rankPath.getPathName();
		String prestige = this.prestige == null ? "null" : this.prestige;
		String rebirth = this.rebirth == null ? "null" : this.rebirth;
		String uuid = this.player.getUUID().toString();
		return "rank:" + rank + "->path:" + path + "||prestige:" + prestige + "||rebirth:" + rebirth + "||uuid:" + uuid;
	}

	public String toString2() {
		String rank = this.rankPath.getRankName();
		String path = this.rankPath.getPathName();
		String prestige = this.prestige == null ? "null" : this.prestige;
		String rebirth = this.rebirth == null ? "null" : this.rebirth;
		String uuid = this.player.getUUID().toString();
		return "rank:" + rank + "->path:" + path + "||prestige:" + prestige + "||rebirth:" + rebirth + "||uuid:" + uuid + "||name:" + this.player.getOfflineName();
	}

	public XUser getUser() {
		return this.player;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
