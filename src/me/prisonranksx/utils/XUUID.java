package me.prisonranksx.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import me.prisonranksx.PrisonRanksX;

public class XUUID {

	private static UUID uuid;
	private static final PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	
	public XUUID(OfflinePlayer player) {
		UUID u;
		if(main.isBefore1_7) {
			u = UUID.nameUUIDFromBytes(player.getName().getBytes());
		} else {
			u = player.getUniqueId();
		}
		uuid = u;
	}
	
	public static UUID tryNameConvert(String name) {
		if(name.contains("-")) {
			// is uuid
			return UUID.fromString(name);
		}
		return UUID.nameUUIDFromBytes(name.getBytes());
	}
	
	/**
	 * 
	 * @param player
	 * @return Real UUID for versions beyond 1.7 | otherwise it will return a fake UUID for 1.6/1.5/1.4 etc...
	 */
	public static UUID getXUUID(OfflinePlayer player) {
		UUID u;
		if(main.isBefore1_7) {
			u = UUID.nameUUIDFromBytes(player.getName().getBytes());
		} else {
			u = player.getUniqueId();
		}
		uuid = u;
		return u;
	}
	
	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}
	
	public UUID getUUID() {
		return this.uuid;
	}
	
}
