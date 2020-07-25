package me.prisonranksx.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;

public class XUUID {

	private UUID uuid;
	private static final PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	private static final Map<UUID, String> legacyPlayers = new HashMap<>();
	
	public XUUID(OfflinePlayer player) {
		UUID u;
		if(main.isBefore1_7) {
			String playerName = player.getName();
			u = UUID.nameUUIDFromBytes(playerName.getBytes());
			legacyPlayers.put(u, playerName);
		} else {
			u = player.getUniqueId();
		}
		uuid = u;
	}
	
	/**
	 * 
	 * @param uuid
	 * @return 1.7- player name from the fake uuid
	 */
	public static String getLegacyName(UUID uuid) {
		return legacyPlayers.get(uuid);
	}
	
	public static UUID tryNameConvert(String name) {
		if(name.contains("-")) {
			// is uuid
			return UUID.fromString(name);
		}
		legacyPlayers.put(UUID.nameUUIDFromBytes(name.getBytes()), name);
		return UUID.nameUUIDFromBytes(name.getBytes());
	}
	
	/**
	 * 
	 * @param player
	 * @return Real UUID for versions beyond 1.7 | otherwise it will return a fake UUID for 1.6/1.5/1.4 etc...
	 */
	public static UUID getXUUID(OfflinePlayer player) {
		return new XUUID(player).getUUID();
	}
	
	public static UUID getXUUID(Player player) {
		return new XUUID(player).getUUID();
	}
	
	public static String getNameFromUUID(UUID uuid) {
		if(main.isBefore1_7) {
		    return legacyPlayers.get(uuid);
		}
		return Bukkit.getOfflinePlayer(uuid).getName();
	}
	
	/**
	 * 
	 * @param uuid
	 * @return the correct uuid counting the server version.
	 */
	public static UUID fetchUUID(UUID uuid) {
		if(main.isBefore1_7) {
			if(legacyPlayers.containsKey(uuid)) {
				return uuid;
			}
		}
		return Bukkit.getOfflinePlayer(uuid).getUniqueId();
	}
	
	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}
	
	public UUID getUUID() {
		return this.uuid;
	}
	
}
