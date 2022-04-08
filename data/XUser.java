package me.prisonranksx.data;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.prisonranksx.utils.XUUID;

public class XUser {

	private UUID uuid;

	public XUser(UUID uuid) {
		this.uuid = uuid;
	}

	public static XUser getXUser(OfflinePlayer offlinePlayer) {
		return new XUser(XUUID.getXUUID(offlinePlayer));
	}

	public static XUser getXUser(Player player) {
		return new XUser(XUUID.getXUUID(player));
	}

	public static XUser getXUser(String uuid) {
		return new XUser(UUID.fromString(uuid));
	}

	public static XUser getXUser(UUID uuid) {
		return new XUser(uuid);
	}

	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	public String getOfflineName() {
		return Bukkit.getOfflinePlayer(uuid).getName();
	}

	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(uuid);
	}

	public UUID getUUID() {
		return this.uuid;
	}

}
