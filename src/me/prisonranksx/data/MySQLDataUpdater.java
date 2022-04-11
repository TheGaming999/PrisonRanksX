package me.prisonranksx.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.utils.MySqlUtils;

public class MySQLDataUpdater {

	private PrisonRanksX plugin;

	public MySQLDataUpdater(PrisonRanksX plugin) {
		this.plugin = plugin;
	}

	public void updateMySqlData(final Player player, final String rank, final String prestige, final String rebirth, final String path) {
		plugin.scheduler.runTaskAsynchronously(plugin, () -> {
			try {
				Player p = player;
				String name = p.getName();
				String u = XUser.getXUser(p).getUUID().toString();
				String rankName = rank == null ? plugin.prxAPI.getDefaultRank() : rank;
				String prestigeName = prestige == null ? "none" : prestige;
				String rebirthName = rebirth == null ? "none" : rebirth;
				String pathName = path == null ? plugin.prxAPI.getDefaultPath() : path;
				Statement statement = plugin.getConnection().createStatement();
				MySqlUtils util = new MySqlUtils(statement, plugin.getDatabase() + "." + plugin.getTable());
				ResultSet result = statement.executeQuery("SELECT * FROM " + plugin.getDatabase() + "." + plugin.getTable() + " WHERE uuid = '" + u + "'");
				if(result.next()) {
					util.set(u, "rank", rankName);
					util.set(u, "path", pathName);
					util.set(u, "prestige", prestigeName);
					util.set(u, "rebirth", rebirthName);
					util.set(u, "name", name);
					util.set(u, "rankscore", plugin.prxAPI.getRankNumber(pathName, rankName));
					util.set(u, "prestigescore", plugin.prxAPI.getPrestigeNumber(prestigeName));
					util.set(u, "rebirthscore", plugin.prxAPI.getRebirthNumber(rebirthName));
					util.set(u, "stagescore", String.valueOf(plugin.prxAPI.getPower(rank, path, prestige, rebirth)));
					util.executeThenClose();
				} else {
					statement.executeUpdate("INSERT INTO " + plugin.getDatabase() + "." + plugin.getTable() +" (`uuid`, `name`, `rank`, `prestige`, `rebirth`, `path`) VALUES ('" + u + "', '" + name + "', '" + rankName + "', '" + prestigeName + "', '" + rebirthName + "', '" + pathName + "');");
					statement.close();
				}
			} catch (SQLException e1) {
				Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data update failed.");
				e1.printStackTrace();
				plugin.getLogger().info("ERROR Updating Player SQL Data");
			}
		});
	}

	public void updateMySqlData(final Player player) {
		plugin.scheduler.runTaskAsynchronously(plugin, () -> {
			try {
				Player p = player;
				String name = p.getName();
				UUID uu = XUser.getXUser(p).getUUID();
				String u = uu.toString();
				String rankName = plugin.prxAPI.getPlayerRank(uu) == null ? plugin.prxAPI.getDefaultRank() : plugin.prxAPI.getPlayerRank(uu);
				String prestigeName = plugin.prxAPI.getPlayerPrestige(uu) == null ? "none" : plugin.prxAPI.getPlayerPrestige(uu);
				String rebirthName = plugin.prxAPI.getPlayerRebirth(uu) == null ? "none" : plugin.prxAPI.getPlayerRebirth(uu);
				String pathName = plugin.prxAPI.getPlayerRankPath(uu).getPathName() == null ? plugin.prxAPI.getDefaultPath() : plugin.prxAPI.getPlayerRankPath(uu).getPathName();
				Statement statement = plugin.getConnection().createStatement();
				MySqlUtils util = new MySqlUtils(statement, plugin.getDatabase() + "." + plugin.getTable());
				ResultSet result = statement.executeQuery("SELECT * FROM " + plugin.getDatabase() + "." + plugin.getTable() + " WHERE uuid = '" + u + "'");
				if(result.next()) {
					util.set(u, "rank", rankName);
					util.set(u, "path", pathName);
					util.set(u, "prestige", prestigeName);
					util.set(u, "rebirth", rebirthName);
					util.set(u, "name", name); 
					util.set(u, "rankscore", plugin.prxAPI.getRankNumber(pathName, rankName));
					util.set(u, "prestigescore", plugin.prxAPI.getPrestigeNumber(prestigeName));
					util.set(u, "rebirthscore", plugin.prxAPI.getRebirthNumber(rebirthName));
					util.set(u, "stagescore", String.valueOf(plugin.prxAPI.getPower(rankName, pathName, prestigeName, rebirthName)));
					util.executeThenClose();
				} else {
					statement.executeUpdate("INSERT INTO " + plugin.getDatabase() + "." + plugin.getTable() +" (`uuid`, `name`, `rank`, `prestige`, `rebirth`, `path`) VALUES ('" + u + "', '" + name + "', '" + rankName + "', '" + prestigeName + "', '" + rebirthName + "', '" + pathName + "');");
					statement.close();
				}
			} catch (SQLException e1) {
				Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data update failed.");
				e1.printStackTrace();
				plugin.getLogger().info("ERROR Updating Player SQL Data");
			}
		});
	}

	/**
	 * 1.7+ only, use updateMySqlData(UUID uuid, String name) for 1.6 and earlier
	 * @param uuid
	 */
	public void updateMySqlData(final UUID uuid) {
		plugin.scheduler.runTaskAsynchronously(plugin, () -> {
			try {
				UUID uu = XUser.getXUser(uuid).getUUID();
				String name = plugin.prxAPI.getPlayerNameFromUUID(uu);
				String u = uu.toString();
				String rankName = plugin.prxAPI.getPlayerRank(uu) == null ? plugin.prxAPI.getDefaultRank() : plugin.prxAPI.getPlayerRank(uu);
				String prestigeName = plugin.prxAPI.getPlayerPrestige(uu) == null ? "none" : plugin.prxAPI.getPlayerPrestige(uu);
				String rebirthName = plugin.prxAPI.getPlayerRebirth(uu) == null ? "none" : plugin.prxAPI.getPlayerRebirth(uu);
				String pathName = plugin.prxAPI.getPlayerRankPath(uu).getPathName() == null ? plugin.prxAPI.getDefaultPath() : plugin.prxAPI.getPlayerRankPath(uu).getPathName();
				Statement statement = plugin.getConnection().createStatement();
				MySqlUtils util = new MySqlUtils(statement, plugin.getDatabase() + "." + plugin.getTable());
				ResultSet result = statement.executeQuery("SELECT * FROM " + plugin.getDatabase() + "." + plugin.getTable() + " WHERE uuid = '" + u + "'");
				if(result.next()) {
					util.set(u, "rank", rankName);
					util.set(u, "path", pathName);
					util.set(u, "prestige", prestigeName);
					util.set(u, "rebirth", rebirthName);
					util.set(u, "name", name);
					util.set(u, "rankscore", plugin.prxAPI.getRankNumber(pathName, rankName));
					util.set(u, "prestigescore", plugin.prxAPI.getPrestigeNumber(prestigeName));
					util.set(u, "rebirthscore", plugin.prxAPI.getRebirthNumber(rebirthName));
					util.set(u, "stagescore", String.valueOf(plugin.prxAPI.getPower(rankName, pathName, prestigeName, rebirthName)));
					util.executeThenClose();
				} else {
					statement.executeUpdate("INSERT INTO " + plugin.getDatabase() + "." + plugin.getTable() +" (`uuid`, `name`, `rank`, `prestige`, `rebirth`, `path`) VALUES ('" + u + "', '" + name + "', '" + rankName + "', '" + prestigeName + "', '" + rebirthName + "', '" + pathName + "');");
					statement.close();
				}
			} catch (SQLException e1) {
				Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data update failed.");
				e1.printStackTrace();
				plugin.getLogger().info("ERROR Updating Player SQL Data");
			}
		});
	}

	/**
	 * 1.0 - 1.15 mc versions
	 * @param uuid
	 */
	public void updateMySqlData(final UUID uuid, final String name) {
		plugin.scheduler.runTaskAsynchronously(plugin, () -> {
			try {
				UUID uu = XUser.getXUser(uuid).getUUID();
				String u = uu.toString();
				String rankName = plugin.prxAPI.getPlayerRank(uu) == null ? plugin.prxAPI.getDefaultRank() : plugin.prxAPI.getPlayerRank(uu);
				String prestigeName = plugin.prxAPI.getPlayerPrestige(uu) == null ? "none" : plugin.prxAPI.getPlayerPrestige(uu);
				String rebirthName = plugin.prxAPI.getPlayerRebirth(uu) == null ? "none" : plugin.prxAPI.getPlayerRebirth(uu);
				String pathName = plugin.prxAPI.getPlayerRankPath(uu).getPathName() == null ? plugin.prxAPI.getDefaultPath() : plugin.prxAPI.getPlayerRankPath(uu).getPathName();
				Statement statement = plugin.getConnection().createStatement();
				MySqlUtils util = new MySqlUtils(statement, plugin.getDatabase() + "." + plugin.getTable());
				ResultSet result = statement.executeQuery("SELECT * FROM " + plugin.getDatabase() + "." + plugin.getTable() + " WHERE uuid = '" + u + "'");
				if(result.next()) {
					util.set(u, "rank", rankName);
					util.set(u, "path", pathName);
					util.set(u, "prestige", prestigeName);
					util.set(u, "rebirth", rebirthName);
					util.set(u, "name", name);  
					util.set(u, "rankscore", plugin.prxAPI.getRankNumber(pathName, rankName));
					util.set(u, "prestigescore", plugin.prxAPI.getPrestigeNumber(prestigeName));
					util.set(u, "rebirthscore", plugin.prxAPI.getRebirthNumber(rebirthName));
					util.set(u, "stagescore", String.valueOf(plugin.prxAPI.getPower(rankName, pathName, prestigeName, rebirthName)));
					util.executeThenClose();
				} else {
					statement.executeUpdate("INSERT INTO " + plugin.getDatabase() + "." + plugin.getTable() +" (`uuid`, `name`, `rank`, `prestige`, `rebirth`, `path`) VALUES ('" + u + "', '" + name + "', '" + rankName + "', '" + prestigeName + "', '" + rebirthName + "', '" + pathName + "');");
					statement.close();
				}
			} catch (SQLException e1) {
				Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data update failed.");
				e1.printStackTrace();
				plugin.getLogger().info("ERROR Updating Player SQL Data");
			}
		});
	}
}
