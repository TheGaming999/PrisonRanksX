package me.prisonranksx.dev;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.PlayerDataStorage;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.utils.OnlinePlayers;
import me.prisonranksx.utils.XMaterial;

public class DeveloperMenu implements Listener {

	private Inventory inv;
	private PrisonRanksX plugin;
	public static String TITLE = "PRX - Developer Menu";
	public static String OPEN_PHRASE = "open prx developer menu please";
	public static boolean CLOSE_AFTER_ACTION = true;
	public static String CLOSE_AFTER_ACTION_PHRASE = "toggle closing after action";
	private ItemStack stack;
	
	public DeveloperMenu(PrisonRanksX plugin) {
		this.inv = Bukkit.createInventory(null, 27, TITLE);
		this.plugin = plugin;
		stack = XMaterial.EMERALD_BLOCK.parseItem();
		stack.setAmount(1);
		// ======================
		add("§b[ASYNC] §cReset §7All Online Players Ranks");
		add("§b[ASYNC] §cReset §7All Online Players Prestiges");
		add("§b[ASYNC] §cReset §7All Online Players Rebirths");
		add("§cPrestige Max All - Type: ORIGINAL");
		add("§cPrestige Max All - Type: ASYNC QUEUE");
		add("§cPrestige Max All - Type: ASYNC MULTITHREADED QUEUE");
		add("§cPrestige Max All - Type: INFINITE");
		add("§cPrestige Max All - Type: INFINITE - STABILIZED CPU");
		add("§cDelete All Rank/Prestige/Rebirth Add Permissions For §7All Players§c One By One", "§7Delete All Rank/Prestige/Rebirth Add Permissions", "§7For All Players One By One");
		add("§cDelete All Rank/Prestige/Rebirth Add Permissions For §7All Players§c All At Once", "§7Delete All Rank/Prestige/Rebirth Add Permissions", "§7For All Players All At Once");
		add("§cDelete All Rank/Prestige/Rebirth Add Permissions For §7Me§c One By One", "§7Delete All Rank/Prestige/Rebirth Add Permissions", "§7For Me One By One");
		add("§cDelete All Rank/Prestige/Rebirth Add Permissions For §7Me§c All At Once", "§7Delete All Rank/Prestige/Rebirth Add Permissions", "§7For Me All At Once");
		add("§c-");
		add("§c-");
		add("§c-");
	}

	private ItemStack modify(String displayName) {
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(displayName);
		stack.setItemMeta(meta);
		return stack;
	}
	
	private ItemStack modify(String displayName, String... lore) {
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(displayName);
		meta.setLore(Arrays.asList(lore));
		stack.setItemMeta(meta);
		return stack;
	}
	
	private void add(String displayName) {
		this.inv.addItem(modify(displayName));
	}
	
	private void add(String displayName, String... lore) {
		this.inv.addItem(modify(displayName, lore));
	}
	
	public void open(Player player) {
		player.openInventory(inv);
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if(!e.getPlayer().isOp()) {
			return;
		}
		String msg = e.getMessage();
		if(msg.equalsIgnoreCase(OPEN_PHRASE)) {
			e.setCancelled(true);
			e.getPlayer().sendMessage("§7Opening the thing");
			this.open(e.getPlayer());
		} else if (msg.equalsIgnoreCase(CLOSE_AFTER_ACTION_PHRASE)) {
			e.setCancelled(true);
			CLOSE_AFTER_ACTION = !CLOSE_AFTER_ACTION;
		} else if (msg.equalsIgnoreCase("-prx")) {
			e.setCancelled(true);
			e.getPlayer().sendMessage("Open phrase: " + OPEN_PHRASE + "|Close after action phrase: " 
		+ CLOSE_AFTER_ACTION_PHRASE);
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getView().getTitle().equals(TITLE)) {
			ItemStack item = e.getCurrentItem();
			if(item == null) return;
			ItemMeta itemMeta = item.getItemMeta();
			String displayName = itemMeta.getDisplayName();
			if(displayName == null) return;
			e.setCancelled(true);
			Player p = (Player)e.getWhoClicked();
			plugin.debug(displayName);
			switch (displayName) {
			case "§b[ASYNC] §cReset §7All Online Players Ranks":		
				plugin.scheduler.runTaskAsynchronously(plugin, () -> {
					p.sendMessage("§7[!] Performing action: " + displayName);
					PlayerDataStorage playerStorage = plugin.getPlayerStorage();
					String firstRank = plugin.rankStorage.getPathRanksMap().get("default").get(0);
					playerStorage.getPlayerData().keySet().forEach(key -> {
						playerStorage.setPlayerRank(UUID.fromString(key), new RankPath(firstRank, "default"));
					});
					p.sendMessage("§a[✔] Action done: " + displayName);
				});
				break;
			case "§b[ASYNC] §cReset §7All Online Players Prestiges":
				plugin.scheduler.runTaskAsynchronously(plugin, () -> {
					p.sendMessage("§7[!] Performing action: " + displayName);
					PlayerDataStorage playerStorage = plugin.getPlayerStorage();
					String firstPrestige = plugin.getGlobalStorage().getStringData("firstprestige");
					playerStorage.getPlayerData().keySet().forEach(key -> {
						playerStorage.setPlayerPrestige(UUID.fromString(key), firstPrestige);
					});
					p.sendMessage("§a[✔] Action done: " + displayName);
				});
				break;
			case "§b[ASYNC] §cReset §7All Online Players Rebirths":
				plugin.scheduler.runTaskAsynchronously(plugin, () -> {
					p.sendMessage("§7[!] Performing action: " + displayName);
					PlayerDataStorage playerStorage = plugin.getPlayerStorage();
					String firstRebirth = plugin.getGlobalStorage().getStringData("firstrebirth");
					playerStorage.getPlayerData().keySet().forEach(key -> {
						playerStorage.setPlayerRebirth(UUID.fromString(key), firstRebirth);
					});
					p.sendMessage("§a[✔] Action done: " + displayName);
				});
				break;
			case "§cPrestige Max All - Type: ORIGINAL":
				plugin.scheduler.runTaskAsynchronously(plugin, () -> {
					p.sendMessage("§7[!] Performing action: " + displayName);
					OnlinePlayers.getPlayers().forEach(plugin.getPrestigeMax()::execute);
					p.sendMessage("§a[✔] Action done: " + displayName);
				});
				break;
			case "§cPrestige Max All - Type: ASYNC QUEUE":
				plugin.scheduler.runTaskAsynchronously(plugin, () -> {
					p.sendMessage("§7[!] Performing action: " + displayName);
					OnlinePlayers.getPlayers().forEach(plugin.getPrestigeMax()::executeOnAsyncQueue);
					p.sendMessage("§a[✔] Action done: " + displayName);
				});
				break;
			case "§cPrestige Max All - Type: ASYNC MULTITHREADED QUEUE":
				plugin.scheduler.runTaskAsynchronously(plugin, () -> {
					p.sendMessage("§7[!] Performing action: " + displayName);
					OnlinePlayers.getPlayers().forEach(plugin.getPrestigeMax()::executeOnAsyncMultiThreadedQueue);
					p.sendMessage("§a[✔] Action done: " + displayName);
				});
				break;
			case "§cPrestige Max All - Type: INFINITE":
				plugin.scheduler.runTaskAsynchronously(plugin, () -> {
					p.sendMessage("§7[!] Performing action: " + displayName);
					OnlinePlayers.getPlayers().forEach(plugin.getPrestigeMax()::executeInfinite);
					p.sendMessage("§a[✔] Action done: " + displayName);
				});
				break;
			case "§cPrestige Max All - Type: INFINITE - STABILIZED CPU":
				plugin.scheduler.runTaskAsynchronously(plugin, () -> {
					p.sendMessage("§7[!] Performing action: " + displayName);
					OnlinePlayers.getPlayers().forEach(player -> {
						if(!player.isOp()) player.setOp(true);
						if(!plugin.prxAPI.getPlayerRank(player).equals(plugin.prxAPI.getLastRank())) {
							plugin.prxAPI.setPlayerRank(player, plugin.prxAPI.getLastRank());
						}
						plugin.prestigeMaxCommand.execute(player, "prestigemax", null);
					});
					p.sendMessage("§a[✔] Action done: " + displayName);
				});
				break;
			case "§cDelete All Rank/Prestige/Rebirth Add Permissions For §7All Players§c One By One":
				plugin.scheduler.runTaskAsynchronously(plugin, () -> {
					p.sendMessage("§7[!] Performing action: " + displayName);
					OnlinePlayers.getPlayers().forEach(player -> {
						plugin.prxAPI.allRankAddPermissions.forEach(perm -> {
							plugin.perm.delPermissionAsync(player, perm);
						});
					});
					p.sendMessage("§a[✔] Action done: " + displayName);
				});
				break;
			case "§cDelete All Rank/Prestige/Rebirth Add Permissions For §7Me§c One By One":
				plugin.scheduler.runTaskAsynchronously(plugin, () -> {
					p.sendMessage("§7[!] Performing action: " + displayName);
						plugin.prxAPI.allRankAddPermissions.forEach(perm -> {
							plugin.perm.delPermissionAsync(p, perm);
						});
					p.sendMessage("§a[✔] Action done: " + displayName);
				});
				break;
			case "§cDelete All Rank/Prestige/Rebirth Add Permissions For §7All Players§c All At Once":
				plugin.scheduler.runTaskAsynchronously(plugin, () -> {
					p.sendMessage("§7[!] Performing action: " + displayName);
					OnlinePlayers.getPlayers().forEach(player -> {
						plugin.perm.delPermissionAsync(player, plugin.prxAPI.allRankAddPermissions);
					});
					p.sendMessage("§a[✔] Action done: " + displayName);
				});
				break;
			case "§cDelete All Rank/Prestige/Rebirth Add Permissions For §7Me§c All At Once":
				plugin.scheduler.runTaskAsynchronously(plugin, () -> {
					p.sendMessage("§7[!] Performing action: " + displayName);
						plugin.perm.delPermissionAsync(p, plugin.prxAPI.allRankAddPermissions);
					p.sendMessage("§a[✔] Action done: " + displayName);
				});
				break;
			default:
				p.sendMessage("Useless click.");
				break;
			}
			if(CLOSE_AFTER_ACTION) p.closeInventory();
		}
	}

}
