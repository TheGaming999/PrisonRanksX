package me.prisonranksx.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * 
 * Get uuid for legacy versions (1.6-) and modern versions (1.7+)
 *
 */
@SuppressWarnings("deprecation")
public class XUUID {

	private final static List<String> LEGACY_VERSIONS = Arrays.asList("1.6", "1.5", "1.4", "1.3");
	private final static String VER = Bukkit.getVersion();
	private final static boolean LEGACY = LEGACY_VERSIONS.stream().anyMatch(VER::contains);
	private final static Map<String, String> LEGACY_NAMES = new HashMap<>();
	private final static Map<String, UUID> LEGACY_UUIDS = new HashMap<>();
	private final static UUIDRetriever UUID_RETRIEVER = LEGACY ? new UUIDRetrieverLegacy() : new UUIDRetrieverDefault();

	public static JavaPlugin getProvidingPlugin(Class<?> providingClass) {
		if (!LEGACY)
			return JavaPlugin.getProvidingPlugin(providingClass);

		ClassLoader loader = providingClass.getClassLoader();
		String pluginName = null;
		try {
			Field loaderField = loader.getClass().getDeclaredField("loader");
			loaderField.setAccessible(true);
			try {
				Field loadersField = loaderField.get(loader).getClass().getDeclaredField("loaders");
				loadersField.setAccessible(true);
				Map<?, ?> pluginsMap = (Map<?, ?>) loadersField.get(loaderField.get(loader));
				Set<?> pluginNamesCollection = pluginsMap.keySet();
				String[] pluginNamesArray = pluginNamesCollection.toArray(new String[] {});
				pluginName = pluginNamesArray[pluginNamesCollection.size()-1];
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} catch (IllegalArgumentException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		return (JavaPlugin) Bukkit.getPluginManager().getPlugin(pluginName);
	}



	/**
	 * 
	 * @return version is < 1.7
	 */
	public static boolean isLegacy() {
		return LEGACY;
	}

	public static void nukeData() {
		LEGACY_NAMES.clear();
		LEGACY_UUIDS.clear();
	}

	public static String getNameFromUUID(UUID uuid) {
		return UUID_RETRIEVER.getNameFromUUID(uuid);
	}

	public static Player getPlayer(UUID uuid) {
		return UUID_RETRIEVER.getPlayer(uuid);
	}

	public static OfflinePlayer getOfflinePlayer(UUID uuid) {
		return UUID_RETRIEVER.getOfflinePlayer(uuid);
	}

	public static UUID getUUID(String name) {
		return UUID_RETRIEVER.getUUID(name);
	}

	public UUID parseUUID(String name) {
		return UUID_RETRIEVER.parseUUID(name);
	}

	public UUID uuid() {
		return UUID_RETRIEVER.uuid();
	}

	public static UUID getUUIDOffline(String name) {
		return UUID_RETRIEVER.getUUIDOffline(name);
	}

	public static UUID getUUID(Player player) {
		return UUID_RETRIEVER.getUUID(player);
	}

	public static UUID getUUIDOffline(OfflinePlayer offlinePlayer) {
		return UUID_RETRIEVER.getUUIDOffline(offlinePlayer);
	}

	private static interface UUIDRetriever {

		String getNameFromUUID(UUID uniqueId);
		Player getPlayer(UUID uniqueId);
		OfflinePlayer getOfflinePlayer(UUID uniqueId);
		UUID getUUID(String name);
		UUID getUUID(Player player);
		UUID parseUUID(String name);
		UUID getUUIDOffline(OfflinePlayer offlinePlayer);
		UUID getUUIDOffline(String name);
		UUID uuid();

	}

	private static class UUIDRetrieverDefault implements UUIDRetriever {

		private UUID uuid;

		public String getNameFromUUID(UUID uuid) {
			return Bukkit.getPlayer(uuid).getName();
		}

		public Player getPlayer(UUID uuid) {
			return Bukkit.getPlayer(uuid);
		}

		public OfflinePlayer getOfflinePlayer(UUID uuid) {
			return Bukkit.getOfflinePlayer(uuid);
		}

		public UUID getUUID(String name) {
			return Bukkit.getPlayer(name).getUniqueId();
		}

		public UUID parseUUID(String name) {
			return uuid = Bukkit.getPlayer(name).getUniqueId();
		}

		public UUID uuid() {
			return uuid;
		}

		public UUID getUUIDOffline(String name) {
			return Bukkit.getOfflinePlayer(name).getUniqueId();
		}

		public UUID getUUID(Player player) {
			return player.getUniqueId();
		}

		public UUID getUUIDOffline(OfflinePlayer offlinePlayer) {
			return offlinePlayer.getUniqueId();
		}

	}

	private static class UUIDRetrieverLegacy implements UUIDRetriever {

		private final Listeners listeners;

		public UUIDRetrieverLegacy() {
			listeners = new Listeners();
			listeners.register();
		}

		class Listeners implements Listener {

			private JavaPlugin mainPlugin = getProvidingPlugin(XUUID.class);

			public void register() {
				Bukkit.getPluginManager().registerEvents(this, mainPlugin);
			}

			@EventHandler(priority=EventPriority.LOWEST)
			public void onJoin(PlayerJoinEvent e) {
				addCache(e.getPlayer());
			}

		}

		private UUID uuid;

		public String getNameFromUUID(UUID uuid) {
			return LEGACY_NAMES.get(uuid.toString());
		}

		public Player getPlayer(UUID uuid) {
			return Bukkit.getPlayer(getNameFromUUID(uuid));
		}

		public OfflinePlayer getOfflinePlayer(UUID uuid) {
			return Bukkit.getOfflinePlayer(getNameFromUUID(uuid));
		}

		public UUID getUUID(String name) {
			return getCachedUUID(name);
		}

		public UUID parseUUID(String name) {
			return uuid = LEGACY_UUIDS.get(name);
		}

		public UUID uuid() {
			return uuid;
		}

		public UUID getUUIDOffline(String name) {
			return LEGACY_UUIDS.get(name);
		}

		public UUID getUUID(Player player) {
			return getCachedUUID(player.getName());
		}

		public UUID getUUIDOffline(OfflinePlayer offlinePlayer) {
			return LEGACY_UUIDS.get(offlinePlayer.getName());
		}

		private UUID getCachedUUID(String name) {
			UUID uuid = LEGACY_UUIDS.get(name);
			if(uuid == null) {
				uuid = UUID.nameUUIDFromBytes(name.getBytes());
				LEGACY_NAMES.put(uuid.toString(), name);
				LEGACY_UUIDS.put(name, uuid);
			}
			return uuid;
		}

		private UUID addCache(Player player) {
			return getCachedUUID(player.getName());
		}

		@SuppressWarnings("unused")
		private void removeCache(String name, UUID uuid) {
			LEGACY_NAMES.remove(uuid.toString());
			LEGACY_UUIDS.remove(name);
		}

	}

}
