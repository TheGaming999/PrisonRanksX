package me.prisonranksx.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import co.aikar.taskchain.TaskChain;
import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.utils.AccessibleBukkitTask;
import me.prisonranksx.utils.AccessibleString;
import me.prisonranksx.utils.MySqlUtils;
import me.prisonranksx.utils.OnlinePlayers;
import me.prisonranksx.utils.XUUID;

public class PlayerDataStorage {

	private PrisonRanksX main;
	private Map<String, PlayerDataHandler> playerData;
	private Set<String> loadedUUIDs;
	private String defaultPath = null;
	private String defaultRank = null;
	private int i = 0;
	public int largeDataCounter;
	public int databaseSaveSpeed = 1;
	private CompletableFuture<BukkitTask> superTaskNotifier;
	private CompletableFuture<Boolean> taskFinishNotifier;

	public PlayerDataStorage(PrisonRanksX main) {
		this.main = main;
		this.playerData = new HashMap<>();
		this.loadedUUIDs = new HashSet<>();
		this.defaultPath = this.main.globalStorage.getStringData("defaultpath");
		this.defaultRank = this.main.globalStorage.getStringData("defaultrank");
		this.largeDataCounter = 125;
		this.databaseSaveSpeed = 1;
		this.superTaskNotifier = new CompletableFuture<>();
		this.taskFinishNotifier = null;
	}

	public Set<String> getLoadedUUIDs() {
		return this.loadedUUIDs;
	}

	public Map<String, PlayerDataHandler> getPlayerData() {
		return this.playerData;
	}


	public void loadPlayersData() {
		if(main.isMySql()) {
			try {
				String sql = "SELECT * FROM " + main.getDatabase() + "." + main.getTable();
				Statement statement = main.getConnection().createStatement();
				ResultSet result = statement.executeQuery(sql);
				while (result.next()) {
					String uuid = result.getString("uuid");
					String name = result.getString("name");
					String rankName = result.getString("rank") == null ? defaultRank : result.getString("rank");
					String pathName = result.getString("path") == null ? defaultPath : result.getString("path");
					String prestigeName = result.getString("prestige").equals("none") ? null : result.getString("prestige");
					String rebirthName = result.getString("rebirth").equals("none") ? null : result.getString("rebirth");
					XUser user = XUser.getXUser(uuid);
					PlayerDataHandler pdh = new PlayerDataHandler(user);
					RankPath rp = new RankPath(rankName, pathName);
					pdh.setUUID(user.getUUID());
					pdh.setRankPath(rp);
					if(prestigeName != null) {
						pdh.setPrestige(prestigeName);  
					}
					if(rebirthName != null) {
						pdh.setRebirth(rebirthName);
					}
					pdh.setName(name != null ? name : Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
					if(!isDummy(pdh)) {
						getPlayerData().put(uuid.toString(), pdh);
					} else {
						pdh = null;
					}

				}
				statement.close();


			}
			catch (SQLException e) {
				Bukkit.getConsoleSender().sendMessage("�e[�9PrisonRanksX�e] �cSQL data load failed.");
				e.printStackTrace();
				main.getLogger().info("<Error> Loading player sql data..");
			}
			return;
		}
		ConfigurationSection players = main.getConfigManager().rankDataConfig.getConfigurationSection("players");
		players.getKeys(false).forEach(strg -> {
			XUser xu = XUser.getXUser(strg);
			String str = xu.getUUID().toString();
			if(str != null && !str.equalsIgnoreCase("null")) {
				PlayerDataHandler pdh = new PlayerDataHandler(xu);
				String path = main.getConfigManager().rankDataConfig.getString("players." + str + ".path");
				RankPath rankPath = new RankPath(main.getConfigManager().rankDataConfig.getString("players." + str + ".rank"), path != null ? path : defaultPath);
				if(main.getConfigManager().prestigeDataConfig.getString("players." + str) != null) {
					pdh.setPrestige(main.getConfigManager().prestigeDataConfig.getString("players."+ str));
				}
				if(main.getConfigManager().rebirthDataConfig.getString("players." + str) != null) {
					pdh.setRebirth(main.getConfigManager().rebirthDataConfig.getString("players." + str));
				}
				String name = main.getConfigManager().rankDataConfig.getString("players." + str + ".name");
				pdh.setRankPath(rankPath);
				pdh.setUUID(xu.getUUID());
				pdh.setName(name != null ? name : Bukkit.getOfflinePlayer(UUID.fromString(str)).getName());
				if(!isDummy(pdh)) {
					//if(!foundNames.contains(pdh.getName())) {
					getPlayerData().put(str, pdh);
					//}
					//foundNames.add(pdh.getName());
				} else {
					pdh = null;
				}
			}
		});
	}

	@Deprecated
	public void loadPlayerData(OfflinePlayer player) {
		XUser xu = XUser.getXUser(player);
		PlayerDataHandler pdh = new PlayerDataHandler(xu);
		RankPath rankPath = new RankPath(main.getConfigManager().rankDataConfig.getString("players." + xu.getUUID() + ".rank"), main.getConfigManager().rankDataConfig.getString("players." + xu.getUUID() + ".path"));
		if(main.getConfigManager().prestigeDataConfig.getString("players." + xu.getUUID()) != null) {
			pdh.setPrestige(main.getConfigManager().prestigeDataConfig.getString("players." + xu.getUUID()));
		}
		if(main.getConfigManager().rebirthDataConfig.getString("players." + xu.getUUID()) != null) {
			pdh.setRebirth(main.getConfigManager().rebirthDataConfig.getString("players." + xu.getUUID()));
		}
		pdh.setRankPath(rankPath);
		pdh.setUUID(xu.getUUID());
		getPlayerData().put(xu.getUUID().toString(), pdh);
	}

	public void loadPlayerData(final Player player) {
		XUser xu = XUser.getXUser(player);
		if(main.isMySql()) {
			Statement statement = null;
			ResultSet result = null;
			try {
				statement = main.getConnection().createStatement();
				result = statement.executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + xu.getUUID().toString() + "';");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String rank = null;
			String prestige = null;
			String rebirth = null;
			String path = null;
			try {
				while (result.next()) {
					rank = result.getString("rank") == null ? defaultRank : result.getString("rank");
					prestige = result.getString("prestige");
					rebirth = result.getString("rebirth");
					path = result.getString("path") == null ? defaultPath : result.getString("path");
				}
				PlayerDataHandler pdh = new PlayerDataHandler(xu);
				RankPath rankPath = new RankPath(rank, path);
				if(prestige != null) {
					pdh.setPrestige(prestige);
				}
				if(rebirth != null) {
					pdh.setRebirth(rebirth);
				}
				pdh.setRankPath(rankPath);
				pdh.setUUID(xu.getUUID());
				pdh.setName(player.getName());
				getPlayerData().put(xu.getUUID().toString(), pdh);
				loadedUUIDs.add(xu.getUUID().toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		PlayerDataHandler pdh = new PlayerDataHandler(xu);
		RankPath rankPath = new RankPath(main.getConfigManager().rankDataConfig.getString("players." + xu.getUUID() + ".rank"), main.getConfigManager().rankDataConfig.getString("players." + xu.getUUID() + ".path"));
		if(main.getConfigManager().prestigeDataConfig.getString("players." + xu.getUUID()) != null) {
			pdh.setPrestige(main.getConfigManager().prestigeDataConfig.getString("players." + xu.getUUID()));
		}
		if(main.getConfigManager().rebirthDataConfig.getString("players." + xu.getUUID()) != null) {
			pdh.setRebirth(main.getConfigManager().rebirthDataConfig.getString("players." + xu.getUUID()));
		}
		pdh.setRankPath(rankPath);
		pdh.setUUID(xu.getUUID());
		pdh.setName(player.getName());
		//if(!foundNames.contains(player.getName())) {
		getPlayerData().put(xu.getUUID().toString(), pdh);
		//}
		//foundNames.add(player.getName());
		loadedUUIDs.add(xu.getUUID().toString());
	}

	public boolean hasData(UUID uuid) {
		AtomicBoolean returnedValue = new AtomicBoolean(false);
		if(main.isMySql()) {
			main.newSharedChain("dataSave").current(() -> {
				ResultSet result = null;
				Statement statement = null;
				try {
					statement = main.getConnection().createStatement();
					result = statement.executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + uuid.toString() + "';");
				} catch (SQLException ex) {
					ex.printStackTrace();
					returnedValue.set(false);
				}
				try {
					if(result.next()) {
						returnedValue.set(true);
					} else {
						returnedValue.set(false);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					returnedValue.set(false);
				}
			}).execute();
		} else {
			if(main.getConfigManager().rankDataConfig.isConfigurationSection("players." + uuid.toString())) {
				returnedValue.set(true);
			} else {
				returnedValue.set(false);
			}
		}
		return returnedValue.get();
	}

	/**
	 * loads player data from mysql database or yaml file
	 * @param uuid
	 */
	public boolean loadPlayerData(UUID uuid) {
		XUser xu = XUser.getXUser(uuid.toString());
		if(main.isMySql()) {
			ResultSet result = null;
			Statement statement = null;
			try {
				statement = main.getConnection().createStatement();
				result = statement.executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + xu.getUUID().toString() + "';");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String rank = null;
			String prestige = null;
			String rebirth = null;
			String path = null;
			String name = null;
			try {
				while (result.next()) {
					rank = result.getString("rank") == null ? defaultRank : result.getString("rank");
					prestige = result.getString("prestige").equals("none") ? null : result.getString("prestige");
					rebirth = result.getString("rebirth").equals("none") ? null : result.getString("rebirth");
					path = result.getString("path") == null ? defaultPath : result.getString("path");
					name = result.getString("name") == null ? Bukkit.getOfflinePlayer(xu.getUUID()).getName() : result.getString("name"); 
					PlayerDataHandler pdh = new PlayerDataHandler(xu);
					if(rank.isEmpty()) {
						rank = defaultRank;
					}
					if(path.isEmpty()) {
						path = defaultPath;
					}
					RankPath rankPath = new RankPath(rank, path);
					if(prestige != null && !prestige.isEmpty()) {
						pdh.setPrestige(prestige);
					}
					if(rebirth != null && !rebirth.isEmpty()) {
						pdh.setRebirth(rebirth);
					}
					pdh.setRankPath(rankPath);
					pdh.setUUID(xu.getUUID());
					pdh.setName(name);
					getPlayerData().put(xu.getUUID().toString(), pdh);
					loadedUUIDs.add(xu.getUUID().toString());
				}
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		PlayerDataHandler pdh = new PlayerDataHandler(xu);
		if(!main.getConfigManager().rankDataConfig.isConfigurationSection("players." + xu.getUUID().toString())) {
			return false;
		}
		String path = main.getConfigManager().rankDataConfig.getString("players." + xu.getUUID().toString() + ".path");
		RankPath rankPath = new RankPath(main.getConfigManager().rankDataConfig.getString("players." + xu.getUUID().toString() + ".rank"), path != null ? path : defaultPath);
		if(main.getConfigManager().prestigeDataConfig.getString("players." + xu.getUUID().toString()) != null) {
			pdh.setPrestige(main.getConfigManager().prestigeDataConfig.getString("players." + xu.getUUID().toString()));
		}
		if(main.getConfigManager().rebirthDataConfig.getString("players." + xu.getUUID().toString()) != null) {
			pdh.setRebirth(main.getConfigManager().rebirthDataConfig.getString("players." + xu.getUUID().toString()));
		}
		String name = main.getConfigManager().rankDataConfig.getString("players." + xu.getUUID().toString() + ".name");
		pdh.setRankPath(rankPath);
		pdh.setUUID(xu.getUUID());
		pdh.setName(name != null ? name : Bukkit.getOfflinePlayer(xu.getUUID()).getName());
		getPlayerData().put(xu.getUUID().toString(), pdh);
		loadedUUIDs.add(xu.getUUID().toString());
		return true;
	}

	public void loadPlayerData(UUID uuid, String playerName) {
		XUser xu = XUser.getXUser(uuid.toString());
		if(main.isMySql()) {
			main.getTaskChainFactory().newSharedChain("dataSave").delay(1).current(() -> {
				ResultSet result = null;
				Statement statement = null;
				try {
					statement = main.getConnection().createStatement();
					result = statement.executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + xu.getUUID().toString() + "';");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String rank = null;
				String prestige = null;
				String rebirth = null;
				String path = null;
				String name = null;
				try {
					while (result.next()) {
						rank = result.getString("rank") == null ? defaultRank : result.getString("rank");
						prestige = result.getString("prestige").equals("none") ? null : result.getString("prestige");
						rebirth = result.getString("rebirth").equals("none") ? null : result.getString("rebirth");
						path = result.getString("path") == null ? defaultPath : result.getString("path");
						name = result.getString("name") == null ? Bukkit.getOfflinePlayer(xu.getUUID()).getName() : result.getString("name"); 
						PlayerDataHandler pdh = new PlayerDataHandler(xu);
						RankPath rankPath = new RankPath(rank, path);
						if(prestige != null) {
							pdh.setPrestige(prestige);
						}
						if(rebirth != null) {
							pdh.setRebirth(rebirth);
						}
						pdh.setRankPath(rankPath);
						pdh.setUUID(xu.getUUID());
						pdh.setName(name);
						getPlayerData().put(xu.getUUID().toString(), pdh);
						loadedUUIDs.add(xu.getUUID().toString());
					}
					statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}).execute();
		}
		PlayerDataHandler pdh = new PlayerDataHandler(xu);
		String strUUID = xu.getUUID().toString();
		if(!main.getConfigManager().rankDataConfig.isConfigurationSection("players." + strUUID)) {
			return;
		}
		String path = main.getConfigManager().rankDataConfig.getString("players." + strUUID + ".path");
		RankPath rankPath = new RankPath(main.getConfigManager().rankDataConfig.getString("players." + strUUID + ".rank"), path != null ? path : defaultPath);
		if(main.getConfigManager().prestigeDataConfig.getString("players." + strUUID) != null) {
			pdh.setPrestige(main.getConfigManager().prestigeDataConfig.getString("players." + strUUID));
		}
		if(main.getConfigManager().rebirthDataConfig.getString("players." + strUUID) != null) {
			pdh.setRebirth(main.getConfigManager().rebirthDataConfig.getString("players." + strUUID));
		}
		// String name = main.getConfigManager().rankDataConfig.getString("players." + xu.getUUID().toString() + ".name");
		pdh.setRankPath(rankPath);
		pdh.setUUID(xu.getUUID());
		pdh.setName(playerName);
		getPlayerData().put(strUUID, pdh);
		loadedUUIDs.add(strUUID);
	}

	public boolean isRegistered(OfflinePlayer player) {
		return getPlayerData().get(XUUID.getUUIDOffline(player).toString()) != null;
	}

	public boolean isRegistered(String uuid) {
		return getPlayerData().get(uuid) != null;
	}

	public boolean isRegistered(UUID uuid) {
		return getPlayerData().get(uuid.toString()) != null;
	}

	public boolean isInitialized(String playerName) {
		for (PlayerDataHandler pdh : getPlayerData().values()) {
			if(pdh.getName().equals(playerName)) {
				return true;
			}
		}
		return false;
	}

	public boolean isLoaded(UUID uuid) {
		return loadedUUIDs.contains(uuid.toString());
	}

	public boolean isLoaded(String uuid) {
		return loadedUUIDs.contains(uuid);
	}

	@Deprecated
	public boolean isLoaded(Player player) {
		return loadedUUIDs.contains(player.getUniqueId().toString());
	}

	@Deprecated
	public boolean isLoaded(OfflinePlayer player) {
		return loadedUUIDs.contains(player.getUniqueId().toString());
	}

	public boolean register(OfflinePlayer player) {
		getPlayerData().put(XUser.getXUser(player).getUUID().toString(), new PlayerDataHandler(XUser.getXUser(player)));
		return true;
	}

	public boolean register(final OfflinePlayer player, boolean defaultValues) {
		XUser xuser = XUser.getXUser(player);
		PlayerDataHandler pdh = new PlayerDataHandler(xuser);
		if(defaultValues) {
			pdh.setRankPath(new RankPath(defaultRank, defaultPath));
			pdh.setUUID(XUUID.getUUIDOffline(player));
			pdh.setName(player.getName());
			getPlayerData().put(xuser.getUUID().toString(), pdh);
		} else {
			getPlayerData().put(xuser.getUUID().toString(), pdh);
		}
		return true;
	}

	/**
	 * @deprecated
	 * @param uuid player uuid
	 * @param defaultValues should we register the data automatically
	 */
	public void register(final UUID uuid, boolean defaultValues) {
		XUser xuser = XUser.getXUser(uuid);
		PlayerDataHandler pdh = new PlayerDataHandler(xuser);
		if(defaultValues) {
			pdh.setRankPath(new RankPath(defaultRank, defaultPath));
			pdh.setUUID(uuid);
			pdh.setName(Bukkit.getOfflinePlayer(uuid).getName());
			getPlayerData().put(xuser.getUUID().toString(), pdh);
		} else {
			getPlayerData().put(xuser.getUUID().toString(), pdh);
		}
	}

	/**
	 * @param uuid player uuid
	 * @param defaultValues should we register the data automatically
	 */
	public boolean register(final UUID uuid, boolean defaultValues, boolean ignoreName) {
		XUser xuser = XUser.getXUser(uuid);
		PlayerDataHandler pdh = new PlayerDataHandler(xuser);
		if(defaultValues) {
			pdh.setRankPath(new RankPath(defaultRank, defaultPath));
			pdh.setUUID(uuid);
			getPlayerData().put(xuser.getUUID().toString(), pdh);
		} else {
			getPlayerData().put(xuser.getUUID().toString(), pdh);
		}
		return true;
	}

	/**
	 * 
	 * @param uuid
	 * @param name
	 * @param defaultValues
	 */
	public boolean register(final UUID uuid, final String name, boolean defaultValues) {
		XUser xuser = new XUser(uuid);
		PlayerDataHandler pdh = new PlayerDataHandler(xuser);
		if(defaultValues) {
			pdh.setRankPath(new RankPath(defaultRank, defaultPath));
			pdh.setPrestige(null);
			pdh.setRebirth(null);
			pdh.setUUID(uuid);
			pdh.setName(name);
			getPlayerData().put(xuser.getUUID().toString(), pdh);
		} else {
			getPlayerData().put(xuser.getUUID().toString(), pdh);
		}
		return true;
	}

	public boolean register(UUID uuid) {
		getPlayerData().put(uuid.toString(), new PlayerDataHandler(new XUser(uuid)));
		return true;
	}

	public boolean register(String uuid) {
		getPlayerData().put(uuid, new PlayerDataHandler(XUser.getXUser(uuid)));
		return true;
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

	/**
	 * 
	 * @return list of player uuids
	 */
	public Set<String> getPlayers() {
		return getPlayerData().keySet();
	}

	public String getPlayerRank(final OfflinePlayer player) {
		XUser user = XUser.getXUser(player);
		String uuid = user.getUUID().toString();
		if(!isRegistered(uuid)) {
			return getPlayerRankOffline(player);
		}
		return getPlayerData().get(uuid).getRankPath().getRankName();
	}

	public String getPlayerRankOffline(final OfflinePlayer player) {
		XUser user = XUser.getXUser(player);
		UUID uuid = user.getUUID();
		String uuidString = uuid.toString();	
		AccessibleString rank = new AccessibleString();
		if(main.isMySql()) {
			main.getTaskChainFactory().newSharedChain("dataSave").async(() -> {
				ResultSet result = null;
				Statement statement = null;
				try {
					statement = main.getConnection().createStatement();
					result = statement.executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + uuidString + "';");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				};
				try {
					while (result.next()) {
						rank.setString(result.getString("rank") == null ? defaultRank : result.getString("rank"));
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}).execute();
		} else {
			rank.setString(main.getConfigManager().rankDataConfig.getString("players." + uuidString + ".rank"));
		}
		return rank.getString();
	}

	public String getPlayerRankOffline(final UUID uuid) {
		String uuidString = uuid.toString();	
		AccessibleString rank = new AccessibleString();
		if(main.isMySql()) {
			main.getTaskChainFactory().newSharedChain("dataSave").async(() -> {
				ResultSet result = null;
				Statement statement = null;
				try {
					statement = main.getConnection().createStatement();
					result = statement.executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + uuidString + "';");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				};
				try {
					while (result.next()) {
						rank.setString(result.getString("rank") == null ? defaultRank : result.getString("rank"));
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}).execute();
		} else {
			rank.setString(main.getConfigManager().rankDataConfig.getString("players." + uuidString + ".rank"));
		}
		return rank.getString();
	}

	public String getPlayerRank(UUID uuid) {
		String uuidString = uuid.toString();
		if(!isRegistered(uuidString)) {
			return getPlayerRankOffline(uuid);
		}
		return getPlayerData().get(uuidString).getRankPath().getRankName();
	}

	public boolean setPlayerRank(OfflinePlayer player, String rankName, String pathName) {
		RankPath rankPath = new RankPath(rankName, pathName);
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRankPath(rankPath);
		return true;
	}

	public boolean setPlayerRank(OfflinePlayer player, String rankName) {
		PlayerDataHandler playerData = getPlayerData().get(XUser.getXUser(player).getUUID().toString());
		if(playerData == null || playerData.getRankPath() == null) this.register(player, true);
		playerData = getPlayerData().get(XUser.getXUser(player).getUUID().toString());
		RankPath rankPath = new RankPath(rankName, playerData.getRankPath().getPathName());
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRankPath(rankPath);
		return true;
	}

	public boolean setPlayerRank(OfflinePlayer player, RankPath rankPath) {
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRankPath(rankPath);
		return true;
	}

	public boolean setPlayerRank(UUID uuid, RankPath rankPath) {
		getPlayerData().get(uuid.toString()).setRankPath(rankPath);
		return true;
	}

	public boolean setPlayerRank(UUID uuid, String rankName) {
		RankPath rankPath = new RankPath(rankName, getPlayerData().get(XUser.getXUser(uuid.toString()).getUUID().toString()).getRankPath().getPathName());
		getPlayerData().get(XUser.getXUser(uuid.toString()).getUUID().toString()).setRankPath(rankPath);
		return true;
	}

	public String getPlayerPrestige(OfflinePlayer player) {
		XUser user = XUser.getXUser(player);
		String uuidString = user.getUUID().toString();
		if(!isRegistered(uuidString)) {
			return getPlayerPrestigeOffline(player);
		}
		return getPlayerData().get(uuidString).getPrestige();
	}

	public String getPlayerPrestigeOffline(final OfflinePlayer player) {
		XUser user = XUser.getXUser(player);
		UUID uuid = user.getUUID();
		String uuidString = uuid.toString();	
		AccessibleString prestige = new AccessibleString();
		if(main.isMySql()) {
			main.getTaskChainFactory().newSharedChain("dataSave").async(() -> {
				ResultSet result = null;
				Statement statement = null;
				try {
					statement = main.getConnection().createStatement();
					result = statement.executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + uuidString + "';");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					while (result.next()) {
						prestige.setString(result.getString("prestige").equals("none") ? null : result.getString("prestige"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}).execute();
		} else {
			prestige.setString(main.getConfigManager().prestigeDataConfig.getString("players." + uuidString));
		}
		return prestige.getString();
	}

	public String getPlayerPrestigeOffline(final UUID uuid) {
		String uuidString = uuid.toString();	
		AccessibleString prestige = new AccessibleString("unloaded");
		if(main.isMySql()) {
			main.debug("Getting offline prestige of: " + uuidString);
			main.getTaskChainFactory().newSharedChain("dataSave").async(() -> {
				ResultSet result = null;
				Statement statement = null;
				try {
					statement = main.getConnection().createStatement();
					result = statement.executeQuery("SELECT prestige FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + uuidString + "';");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				};
				try {
					while (result.next()) {
						prestige.setString(result.getString("prestige").equals("none") ? null : result.getString("prestige"));
						main.debug(uuidString + "'s prestige is:" + prestige.getString());
						break;
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}).execute();
		} else {
			prestige.setString(main.getConfigManager().prestigeDataConfig.getString("players." + uuidString));
		}
		main.debug("[CONFIRMATION] " + uuidString + "'s prestige is:" + prestige.getString());
		return prestige.getString();
	}

	public String getPlayerPrestige(UUID uuid) {
		if(!isRegistered(uuid)) {
			return getPlayerPrestigeOffline(uuid);
		}
		return getPlayerData().get(uuid.toString()).getPrestige();
	}

	public boolean setPlayerPrestige(OfflinePlayer player, String prestigeName) {
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setPrestige(prestigeName);
		return true;
	}

	public boolean setPlayerPrestige(Player player, String prestigeName) {
		getPlayerData().get(player.getUniqueId().toString()).setPrestige(prestigeName);
		return true;
	}

	public boolean setPlayerPrestige(UUID uuid, String prestigeName) {
		if(getPlayerData().containsKey(uuid.toString())) {
			getPlayerData().get(uuid.toString()).setPrestige(prestigeName);
			return true;
		} else {
			return false;
		}
	}

	public CompletableFuture<PlayerDataHandler> getPlayerDataHandler(UUID uuid) {
		CompletableFuture<PlayerDataHandler> future = CompletableFuture.supplyAsync(() -> {
			return getPlayerData().get(uuid.toString());
		});
		return future;
	}

	public PlayerDataHandler waitForPlayerDataHandler(UUID uuid) {
		CompletableFuture<PlayerDataHandler> future = CompletableFuture.supplyAsync(() -> {
			return getPlayerData().get(uuid.toString());
		});
		return future.join();
	}

	public String getPlayerRebirth(OfflinePlayer player) {
		if(!isRegistered(player)) {
			return getPlayerRebirthOffline(player);
		}
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRebirth();
	}

	public String getPlayerRebirthOffline(final OfflinePlayer player) {
		XUser user = XUser.getXUser(player);
		UUID uuid = user.getUUID();
		String uuidString = uuid.toString();	
		AccessibleString rebirth = new AccessibleString();
		if(main.isMySql()) {
			main.getTaskChainFactory().newSharedChain("dataSave").async(() -> {
				ResultSet result = null;
				Statement statement = null;
				try {
					statement = main.getConnection().createStatement();
					result = statement.executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + uuidString + "';");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				};
				try {
					while (result.next()) {
						rebirth.setString(result.getString("rebirth").equals("none") ? null : result.getString("rebirth"));
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}).execute();
		} else {
			rebirth.setString(main.getConfigManager().rebirthDataConfig.getString("players." + uuidString));
		}
		return rebirth.getString();
	}

	public String getPlayerRebirthOffline(final UUID uuid) {
		String uuidString = uuid.toString();	
		AccessibleString rebirth = new AccessibleString();
		if(main.isMySql()) {
			main.getTaskChainFactory().newSharedChain("dataSave").async(() -> {
				ResultSet result = null;
				Statement statement = null;
				try {
					statement = main.getConnection().createStatement();
					result = statement.executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + uuidString + "';");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				};
				try {
					while (result.next()) {
						rebirth.setString(result.getString("rebirth").equals("none") ? null : result.getString("rebirth"));
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}).execute();
		} else {
			rebirth.setString(main.getConfigManager().rebirthDataConfig.getString("players." + uuidString));
		}
		return rebirth.getString();
	}

	public String getPlayerRebirth(UUID uuid) {
		if(!isRegistered(uuid)) {
			return getPlayerRebirthOffline(uuid);
		}
		return getPlayerData().get(uuid.toString()).getRebirth();
	}

	public boolean setPlayerRebirth(OfflinePlayer player, String rebirthName) {
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRebirth(rebirthName);
		return true;
	}

	public boolean setPlayerRebirth(UUID uuid, String rebirthName) {
		getPlayerData().get(new XUser(uuid).getUUID().toString()).setRebirth(rebirthName);
		return true;
	}

	public String getPlayerPath(OfflinePlayer player) {
		PlayerDataHandler pdh = getPlayerData().get(XUser.getXUser(player).getUUID().toString());
		if(pdh == null || pdh.getRankPath() == null || pdh.getRankPath().getPathName() == null) {
			return defaultPath;
		}
		return pdh.getRankPath().getPathName();
	}

	public String getPlayerPath(UUID uuid) {
		return getPlayerData().get(uuid.toString()).getRankPath().getPathName();
	}

	public boolean setPlayerPath(OfflinePlayer player, String pathName) {
		RankPath rankPath = new RankPath(getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath().getRankName(), pathName);
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRankPath(rankPath);
		return true;
	}

	public boolean setPlayerPath(UUID uuid, String pathName) {
		RankPath rankPath = new RankPath(getPlayerData().get(new XUser(uuid).getUUID().toString()).getRankPath().getRankName(), pathName);
		getPlayerData().get(new XUser(uuid).getUUID().toString()).setRankPath(rankPath);
		return true;
	}

	public boolean setPlayerRankPath(OfflinePlayer player, RankPath rankPath) {
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRankPath(rankPath);
		return true;
	}

	public boolean setPlayerRankPath(UUID uuid, RankPath rankPath) {
		getPlayerData().get(uuid.toString()).setRankPath(rankPath);
		return true;
	}

	public RankPath getPlayerRankPathOffline(final OfflinePlayer player) {
		XUser user = XUser.getXUser(player);
		UUID uuid = user.getUUID();
		String uuidString = uuid.toString();	
		AccessibleString rank = new AccessibleString();
		AccessibleString path = new AccessibleString();
		if(main.isMySql()) {
			main.getTaskChainFactory().newSharedChain("dataSave").async(() -> {
				ResultSet result = null;
				Statement statement = null;
				try {
					statement = main.getConnection().createStatement();
					result = statement.executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + uuidString + "';");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				};
				try {
					while (result.next()) {
						rank.setString(result.getString("rank") == null ? defaultRank : result.getString("rank"));
						path.setString(result.getString("path") == null ? defaultPath : result.getString("path"));
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}).execute();
		} else {
			rank.setString(main.getConfigManager().rankDataConfig.getString("players." + uuidString + ".rank"));
			path.setString(main.getConfigManager().rankDataConfig.getString("players." + uuidString + ".path"));
		}
		return new RankPath(rank.getString(), path.getString());
	}

	public RankPath getPlayerRankPathOffline(final UUID uuid) {
		String uuidString = uuid.toString();	
		AccessibleString rank = new AccessibleString();
		AccessibleString path = new AccessibleString();
		if(main.isMySql()) {
			main.getTaskChainFactory().newSharedChain("dataSave").async(() -> {
				ResultSet result = null;
				Statement statement = null;
				try {
					statement = main.getConnection().createStatement();
					result = statement.executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + uuidString + "';");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				};
				try {
					while (result.next()) {
						rank.setString(result.getString("rank") == null ? defaultRank : result.getString("rank"));
						path.setString(result.getString("path") == null ? defaultPath : result.getString("path"));
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}).execute();
		} else {
			rank.setString(main.getConfigManager().rankDataConfig.getString("players." + uuidString + ".rank"));
			path.setString(main.getConfigManager().rankDataConfig.getString("players." + uuidString + ".path"));
		}
		return new RankPath(rank.getString(), path.getString());
	}

	public RankPath getPlayerRankPath(OfflinePlayer player) {
		if(!isRegistered(player)) {
			return getPlayerRankPathOffline(player);
		}
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath();
	}

	public RankPath getPlayerRankPath(UUID uuid) {
		if(!isRegistered(uuid)) {
			return getPlayerRankPathOffline(uuid);
		}
		return getPlayerData().get(uuid.toString()).getRankPath();
	}

	/**
	 * 
	 * @param handler
	 * @return true if player data is useless
	 */
	public boolean isDummy(@Nonnull final PlayerDataHandler handler) {
		if(main.isRankEnabled && handler.getRankPath() == null) {
			return true;
		}
		if(handler.getName() == null || handler.getName().equals("null")) {
			return true;
		}
		if(handler.getUUID() == null) {
			return true;
		}
		if(main.isRankEnabled && (handler.getRankPath().getRankName() == null || handler.getRankPath().getPathName() == null)) {
			main.getLogger().info(handler.getName() + " has invalid data, fixing...");
			this.fixNulls(handler.getUUID(), handler.getName());
			return false;
		}
		if(main.isRankEnabled && (!handler.getRankPath().getRankName().equalsIgnoreCase(defaultRank))) {
			return false;
		} else {
			if(!main.isRankEnabled && (handler.getPrestige() == null && handler.getRebirth() == null)) {
				return true;
			} else {
				return false;
			}
		}
	}

	private String getWordForm(int i, String singular, String plural) {
		return i <= 1 ? singular : plural;
	}

	@SuppressWarnings("unchecked")
	public synchronized CompletableFuture<Boolean> saveLargePlayersData() {
		AtomicInteger entryIndexHolder = new AtomicInteger(-1);
		AtomicBoolean stopEntrySwitching = new AtomicBoolean(false);
		Entry<String, PlayerDataHandler>[] dataArray = (Entry<String, PlayerDataHandler>[])getPlayerData().entrySet().toArray(new Entry[0]);
		int size = dataArray.length;
		if(size != 0)
			if(main.isMySql()) {
				try {
					main.getConnection().setAutoCommit(false);
					String sql = "UPDATE " + main.getDatabase() + "." + main.getTable() + " SET name=?,rank=?,prestige=?,rebirth=?,path=?,rankscore=?,prestigescore=?,rebirthscore=?,stagescore=? WHERE uuid=?";
					PreparedStatement statement = main.getConnection().prepareStatement(sql);
					AccessibleBukkitTask atomicTask = AccessibleBukkitTask.create();
					atomicTask.set(Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
						if(stopEntrySwitching.get()) return;
						if(entryIndexHolder.get() == size-1) {
							stopEntrySwitching.set(true);
							int entryIndex = entryIndexHolder.get();
							main.debug("[MYSQL-LARGEDATA] &aFinal data handler reached. Current: " + entryIndex + " / Final: " + (size-1));	
							try {
								main.debug("[MYSQL-LARGEDATA] Statement batch executed.");
								statement.executeBatch();
								main.debug("[MYSQL-LARGEDATA] Data committed by the connection.");
								main.getConnection().commit();
								main.debug("[MYSQL-LARGEDATA] Statement closed.");
								statement.close();
								main.console.sendMessage(PrisonRanksX.PREFIX + " �9Updated �a" + String.valueOf(entryIndex+1) + " �9" + getWordForm(entryIndex+1, "Entry", "Entries") + ".");
							} catch (SQLException e) {
								e.printStackTrace();
							}
							try {
								main.getConnection().setAutoCommit(true);
							} catch (SQLException e) {
								e.printStackTrace();
							}
							main.debug("[MYSQL-LARGEDATA] Save task ended.");
							superTaskNotifier = CompletableFuture.supplyAsync(() -> atomicTask.cancel());
						} else if (entryIndexHolder.get() < size-1) {
							entryIndexHolder.incrementAndGet();
							int entryIndex = entryIndexHolder.get();
							String uuid = dataArray[entryIndex].getKey();
							PlayerDataHandler value = dataArray[entryIndex].getValue();
							if(value.getRankPath() == null) value.setRankPath(new RankPath(defaultRank, defaultPath));
							RankPath rp = value.getRankPath();
							String rankName = rp.getRankName() == null ? defaultRank : value.getRankPath().getRankName();
							String pathName = rp.getPathName() == null ? defaultPath : value.getRankPath().getPathName();
							String prestigeName = value.getPrestige() == null ? "none" : value.getPrestige();
							String rebirthName = value.getRebirth() == null ? "none" : value.getRebirth();
							String name = value.getName();
							main.debug("[MYSQL-LARGEDATA] Saving data for player: " + name);
							try {
								statement.setString(10, uuid);
								statement.setString(1, name);
								statement.setString(2, rankName);
								statement.setString(3, prestigeName);
								statement.setString(4, rebirthName);
								statement.setString(5, pathName);
								statement.setInt(6, main.prxAPI.getRankNumberX(pathName, rankName));
								statement.setInt(7, main.prxAPI.getPrestigeNumberX(prestigeName));
								statement.setInt(8, main.prxAPI.getRebirthNumberX(rebirthName));
								statement.setInt(9, main.prxAPI.getPower(rankName, pathName, prestigeName, rebirthName));
								statement.addBatch();
								main.debug("[MYSQL-LARGEDATA] Save information added for: " + name);
							} catch (SQLException ex) {
								main.console.sendMessage(PrisonRanksX.PREFIX + " �cSQL data update failed.");
								ex.printStackTrace();
								main.debug("[MYSQL-LARGEDATA] Inside loop error.");
								superTaskNotifier = CompletableFuture.supplyAsync(() -> atomicTask.cancel());
							}
						}
					}, this.databaseSaveSpeed, this.databaseSaveSpeed));

				}
				catch (SQLException e) {
					main.console.sendMessage(PrisonRanksX.PREFIX + " �cSQL data update failed.");
					e.printStackTrace();
					main.getLogger().info("<Error> Updating player sql data...");
					main.debug("[MYSQL-LARGEDATA] Outside loop error.");
					superTaskNotifier.completeExceptionally(e);
				}
			} else {
				AccessibleBukkitTask atomicTask = new AccessibleBukkitTask();
				atomicTask.set(Bukkit.getScheduler().runTaskTimer(main, () -> {
					TaskChain<?> saveChain = main.getTaskChainFactory().newSharedChain("dataSave");
					saveChain.async(() -> {
						entryIndexHolder.incrementAndGet();
						int b = entryIndexHolder.get();
						if(b >= size) {
							entryIndexHolder.set(-1);
							main.getConfigManager().saveRankDataConfig();
							main.getConfigManager().savePrestigeDataConfig();
							main.getConfigManager().saveRebirthDataConfig();
							saveChain.abortChain();
							atomicTask.cancel();
						}
						Entry<String, PlayerDataHandler> player = dataArray[b];
						if(player.getKey() != null && !isDummy(player.getValue())) {
							String key = player.getKey();
							PlayerDataHandler value = player.getValue();
							RankPath rp = value.getRankPath();
							main.getConfigManager().rankDataConfig.set("players." + key + ".rank", rp.getRankName() != null ? rp.getRankName() : defaultRank);
							main.getConfigManager().rankDataConfig.set("players." + key + ".path", rp.getPathName() != null ? rp.getPathName() : defaultPath);
							main.getConfigManager().rankDataConfig.set("players." + key + ".name", value.getName());
							main.getConfigManager().prestigeDataConfig.set("players." + key, value.getPrestige());
							main.getConfigManager().rebirthDataConfig.set("players." + key, value.getRebirth());
						}
					}).execute();
				}, 1, 1));
			}
		taskFinishNotifier = CompletableFuture.supplyAsync(() -> {
			superTaskNotifier.join();
			return true;
		});
		return taskFinishNotifier;
	}

	public void savePlayersData() {
		if(OnlinePlayers.size() > largeDataCounter && main.isMySql()) {
			saveLargePlayersData();
			return;
		}
		i = 0;
		if(main.isMySql()) {
			try {

				String sql = "UPDATE " + main.getDatabase() + "." + main.getTable() + " SET `name`=?,`rank`=?,`prestige`=?,`rebirth`=?,`path`=?,`rankscore`=?,`prestigescore`=?,`rebirthscore`=?,`stagescore`=? WHERE uuid=?";
				main.getConnection().setAutoCommit(false);
				PreparedStatement statement = main.getConnection().prepareStatement(sql);

				getPlayerData().values().stream().filter(val -> !isDummy(val)).forEach(val -> {
					i++;
					PlayerDataHandler value = val;
					if(val.getUUID() == null) {
						val.setUUID(UUID.randomUUID());
					}
					String uuid = val.getUUID().toString();
					if(value.getRankPath() == null) {
						value.setRankPath(new RankPath(defaultRank, defaultPath));
					}
					RankPath rp = value.getRankPath();
					String rankName = rp.getRankName() == null ? defaultRank : value.getRankPath().getRankName();
					String pathName = rp.getPathName() == null ? defaultPath : value.getRankPath().getPathName();
					String prestigeName = value.getPrestige() == null ? "none" : value.getPrestige();
					String rebirthName = value.getRebirth() == null ? "none" : value.getRebirth();
					String name = value.getName();
					try {
						statement.setString(10, uuid);
						statement.setString(1, name);
						statement.setString(2, rankName);
						statement.setString(3, prestigeName);
						statement.setString(4, rebirthName);
						statement.setString(5, pathName);
						statement.setInt(6, main.prxAPI.getRankNumberX(pathName, rankName));
						statement.setInt(7, main.prxAPI.getPrestigeNumberX(prestigeName));
						statement.setInt(8, main.prxAPI.getRebirthNumberX(rebirthName));
						statement.setInt(9, main.prxAPI.getPower(rankName, pathName, prestigeName, rebirthName));
						statement.addBatch();
					} catch (SQLException ex) {
						Bukkit.getConsoleSender().sendMessage("�e[�9PrisonRanksX�e] �cSQL data update failed.");
						ex.printStackTrace();
						main.getLogger().info("<Error> Updating player sql data..");
					}
				});
				try {
					main.getConnection().commit();
				} catch (SQLException err) {
					main.getLogger().warning("[MySql] Couldn't commit because autocommit is already enabled.");
				}
				Bukkit.getConsoleSender().sendMessage("�e[�9PrisonRanksX�e] �9Updated �a" + String.valueOf(i) + " �9" + getWordForm(i, "Entry", "Entries") + ".");
				statement.close();
				main.getConnection().setAutoCommit(true);
			}
			catch (SQLException e) {
				Bukkit.getConsoleSender().sendMessage("�e[�9PrisonRanksX�e] �cSQL data update failed.");
				e.printStackTrace();
				main.getLogger().info("<Error> Updating player sql data..");
			}
			return;
		}
		for(Entry<String, PlayerDataHandler> player : getPlayerData().entrySet()) {
			if(player.getKey() != null && !isDummy(player.getValue())) {
				String key = player.getKey();
				PlayerDataHandler value = player.getValue();
				if(value == null) return;
				RankPath rp = value.getRankPath();
				if(rp == null) return;
				main.getConfigManager().rankDataConfig.set("players." + key + ".rank", rp.getRankName() != null ? rp.getRankName() : defaultRank);
				main.getConfigManager().rankDataConfig.set("players." + key + ".path", rp.getPathName() != null ? rp.getPathName() : defaultPath);
				main.getConfigManager().rankDataConfig.set("players." + key + ".name", value.getName());
				main.getConfigManager().prestigeDataConfig.set("players." + key, value.getPrestige());
				main.getConfigManager().rebirthDataConfig.set("players." + key, value.getRebirth());
			}
		}
		main.getConfigManager().saveRankDataConfig();
		main.getConfigManager().savePrestigeDataConfig();
		main.getConfigManager().saveRebirthDataConfig();
	}

	public void savePlayersData(boolean ignoreLargeMethod) {

		i = 0;
		if(main.isMySql()) {
			try {

				String sql = "UPDATE " + main.getDatabase() + "." + main.getTable() + " SET `name`=?,`rank`=?,`prestige`=?,`rebirth`=?,`path`=?,`rankscore`=?,`prestigescore`=?,`rebirthscore`=?,`stagescore`=? WHERE uuid=?";
				main.getConnection().setAutoCommit(false);
				PreparedStatement statement = main.getConnection().prepareStatement(sql);

				getPlayerData().values().stream().filter(val -> !isDummy(val)).forEach(val -> {
					i++;
					PlayerDataHandler value = val;
					if(val.getUUID() == null) {
						val.setUUID(UUID.randomUUID());
					}
					String uuid = val.getUUID().toString();
					if(value.getRankPath() == null) {
						value.setRankPath(new RankPath(defaultRank, defaultPath));
					}
					RankPath rp = value.getRankPath();
					String rankName = rp.getRankName() == null ? defaultRank : value.getRankPath().getRankName();
					String pathName = rp.getPathName() == null ? defaultPath : value.getRankPath().getPathName();
					String prestigeName = value.getPrestige() == null ? "none" : value.getPrestige();
					String rebirthName = value.getRebirth() == null ? "none" : value.getRebirth();
					String name = value.getName();
					try {
						statement.setString(10, uuid);
						statement.setString(1, name);
						statement.setString(2, rankName);
						statement.setString(3, prestigeName);
						statement.setString(4, rebirthName);
						statement.setString(5, pathName);
						statement.setInt(6, main.prxAPI.getRankNumberX(pathName, rankName));
						statement.setInt(7, main.prxAPI.getPrestigeNumberX(prestigeName));
						statement.setInt(8, main.prxAPI.getRebirthNumberX(rebirthName));
						statement.setInt(9, main.prxAPI.getPower(rankName, pathName, prestigeName, rebirthName));
						statement.addBatch();
					} catch (SQLException ex) {
						Bukkit.getConsoleSender().sendMessage("�e[�9PrisonRanksX�e] �cSQL data update failed.");
						ex.printStackTrace();
						main.getLogger().info("<Error> Updating player sql data..");
					}
				});
				try {
					main.getConnection().commit();
				} catch (SQLException err) {
					main.getLogger().warning("[MySql] Couldn't commit because autocommit is already enabled.");
				}
				Bukkit.getConsoleSender().sendMessage("�e[�9PrisonRanksX�e] �9Updated �a" + String.valueOf(i) + " �9" + getWordForm(i, "Entry", "Entries") + ".");
				statement.close();
				main.getConnection().setAutoCommit(true);
			}
			catch (SQLException e) {
				Bukkit.getConsoleSender().sendMessage("�e[�9PrisonRanksX�e] �cSQL data update failed.");
				e.printStackTrace();
				main.getLogger().info("<Error> Updating player sql data..");
			}
			return;
		}
		for(Entry<String, PlayerDataHandler> player : getPlayerData().entrySet()) {
			if(player.getKey() != null && !isDummy(player.getValue())) {
				String key = player.getKey();
				PlayerDataHandler value = player.getValue();
				if(value == null) return;
				RankPath rp = value.getRankPath();
				if(rp == null) return;
				main.getConfigManager().rankDataConfig.set("players." + key + ".rank", rp.getRankName() != null ? rp.getRankName() : defaultRank);
				main.getConfigManager().rankDataConfig.set("players." + key + ".path", rp.getPathName() != null ? rp.getPathName() : defaultPath);
				main.getConfigManager().rankDataConfig.set("players." + key + ".name", value.getName());
				main.getConfigManager().prestigeDataConfig.set("players." + key, value.getPrestige());
				main.getConfigManager().rebirthDataConfig.set("players." + key, value.getRebirth());
			}
		}
		main.getConfigManager().saveRankDataConfig();
		main.getConfigManager().savePrestigeDataConfig();
		main.getConfigManager().saveRebirthDataConfig();
	}

	public void savePlayersDataMySql() {
		if(OnlinePlayers.size() > largeDataCounter) {
			saveLargePlayersData();
			return;
		}
		i = 0;
		if(!main.isMySql()) return;
		main.newSharedChain("dataSave").current(() -> {
			try {
				String sql = "UPDATE " + main.getDatabase() + "." + main.getTable() + " SET `name`=?,`rank`=?,`prestige`=?,`rebirth`=?,`path`=?,`rankscore`=?,`prestigescore`=?,`rebirthscore`=?,`stagescore`=? WHERE `uuid`=?";
				main.getConnection().setAutoCommit(false);
				PreparedStatement statement = main.getConnection().prepareStatement(sql);

				getPlayerData().values().stream().filter(val -> !isDummy(val)).forEach(val -> {
					i++;
					PlayerDataHandler value = val;
					if(val.getUUID() == null) {
						val.setUUID(UUID.randomUUID());
					}
					String uuid = val.getUUID().toString();
					if(value.getRankPath() == null) {
						value.setRankPath(new RankPath(defaultRank, defaultPath));
					}
					RankPath rp = value.getRankPath();
					String rankName = rp.getRankName() == null ? defaultRank : value.getRankPath().getRankName();
					String pathName = rp.getPathName() == null ? defaultPath : value.getRankPath().getPathName();
					String prestigeName = value.getPrestige() == null ? "none" : value.getPrestige();
					String rebirthName = value.getRebirth() == null ? "none" : value.getRebirth();
					String name = value.getName();
					try {
						statement.setString(10, uuid);
						statement.setString(1, name);
						statement.setString(2, rankName);
						statement.setString(3, prestigeName);
						statement.setString(4, rebirthName);
						statement.setString(5, pathName);
						statement.setInt(6, main.prxAPI.getRankNumberX(pathName, rankName));
						statement.setInt(7, main.prxAPI.getPrestigeNumberX(prestigeName));
						statement.setInt(8, main.prxAPI.getRebirthNumberX(rebirthName));
						statement.setInt(9, main.prxAPI.getPower(rankName, pathName, prestigeName, rebirthName));
						statement.addBatch();
					} catch (SQLException ex) {
						Bukkit.getConsoleSender().sendMessage("�e[�9PrisonRanksX�e] �cSQL data update failed.");
						ex.printStackTrace();
						main.getLogger().info("<Error> Updating player sql data..");
					}
				});
				statement.executeBatch();  
				try {
					main.getConnection().commit();
				} catch (SQLException err) {
					main.getLogger().warning("[MySql] Couldn't commit because autocommit is already enabled.");
				}
				statement.close();
				main.getConnection().setAutoCommit(true);
			}
			catch (SQLException e) {
				Bukkit.getConsoleSender().sendMessage("�e[�9PrisonRanksX�e] �cSQL data update failed.");
				e.printStackTrace();
				main.getLogger().info("<Error> Updating player sql data..");
			}
			return;
		}).execute();
	}

	public enum PlayerDataType {
		RANK, PRESTIGE, REBIRTH, NAME, ALL
	}

	public boolean storePlayerData(UUID uuid, PlayerDataType playerDataType) {
		String key = uuid.toString();
		PlayerDataHandler pdh = this.getPlayerData().get(key);
		if(playerDataType == PlayerDataType.RANK) {
			RankPath rp = pdh.getRankPath();
			main.getConfigManager().rankDataConfig.set("players." + key + ".rank", rp.getRankName());
			main.getConfigManager().rankDataConfig.set("players." + key + ".path", rp.getPathName());
		} else if (playerDataType == PlayerDataType.PRESTIGE) {
			main.getConfigManager().prestigeDataConfig.set("players." + key, pdh.getPrestige());
		} else if (playerDataType == PlayerDataType.REBIRTH) { 
			main.getConfigManager().rebirthDataConfig.set("players." + key, pdh.getRebirth());
		} else if (playerDataType == PlayerDataType.NAME) {
			main.getConfigManager().rankDataConfig.set("players." + key + ".name", pdh.getName());
		} else {
			RankPath rp = pdh.getRankPath();
			main.getConfigManager().rankDataConfig.set("players." + key + ".rank", rp.getRankName());
			main.getConfigManager().rankDataConfig.set("players." + key + ".path", rp.getPathName());
			main.getConfigManager().rankDataConfig.set("players." + key + ".name", pdh.getName());
			main.getConfigManager().prestigeDataConfig.set("players." + key, pdh.getPrestige());
			main.getConfigManager().rebirthDataConfig.set("players." + key, pdh.getRebirth());
		}
		return true;
	}

	/**
	 * update player data temporarily to refresh the leaderboards
	 * @param playerDataType
	 */
	public void storePlayersData(PlayerDataType playerDataType) {
		if(main.isMySql()) {
			if(playerDataType == PlayerDataType.ALL) {
				savePlayersDataMySql();
			}
		}
		for(Entry<String, PlayerDataHandler> entry : this.getPlayerData().entrySet()) {
			String key = entry.getKey();
			PlayerDataHandler pdh = entry.getValue();
			if(playerDataType == PlayerDataType.RANK) {
				RankPath rp = pdh.getRankPath();
				if(rp == null) return;
				main.getConfigManager().rankDataConfig.set("players." + key + ".rank", rp.getRankName());
				main.getConfigManager().rankDataConfig.set("players." + key + ".path", rp.getPathName());
			} else if (playerDataType == PlayerDataType.PRESTIGE) {
				main.getConfigManager().prestigeDataConfig.set("players." + key, pdh.getPrestige());
			} else if (playerDataType == PlayerDataType.REBIRTH) { 
				main.getConfigManager().rebirthDataConfig.set("players." + key, pdh.getRebirth());
			} else if (playerDataType == PlayerDataType.NAME) {
				main.getConfigManager().rankDataConfig.set("players." + key + ".name", pdh.getName());
			} else {
				RankPath rp = pdh.getRankPath();
				if(rp != null) {
					main.getConfigManager().rankDataConfig.set("players." + key + ".rank", rp.getRankName());
					main.getConfigManager().rankDataConfig.set("players." + key + ".path", rp.getPathName());
				}
				main.getConfigManager().rankDataConfig.set("players." + key + ".name", pdh.getName());
				main.getConfigManager().prestigeDataConfig.set("players." + key, pdh.getPrestige());
				main.getConfigManager().rebirthDataConfig.set("players." + key, pdh.getRebirth());
			}
		}
	}

	public boolean storePlayersData(PlayerDataType playerDataType, boolean saveToDisk) {
		boolean saveRankDisk = false;
		boolean savePrestigeDisk = false;
		boolean saveRebirthDisk = false;
		boolean saveAllDisk = false;
		for(Entry<String, PlayerDataHandler> entry : this.getPlayerData().entrySet()) {
			String key = entry.getKey();
			PlayerDataHandler pdh = entry.getValue();
			if(playerDataType == PlayerDataType.RANK) {
				RankPath rp = pdh.getRankPath();
				if(rp != null) {
					main.getConfigManager().rankDataConfig.set("players." + key + ".rank", rp.getRankName());
					main.getConfigManager().rankDataConfig.set("players." + key + ".path", rp.getPathName());
					saveRankDisk = true;
				}
			} else if (playerDataType == PlayerDataType.PRESTIGE) {
				main.getConfigManager().prestigeDataConfig.set("players." + key, pdh.getPrestige());
				savePrestigeDisk = true;
			} else if (playerDataType == PlayerDataType.REBIRTH) { 
				main.getConfigManager().rebirthDataConfig.set("players." + key, pdh.getRebirth());
				saveRebirthDisk = true;
			} else if (playerDataType == PlayerDataType.NAME) {
				main.getConfigManager().rankDataConfig.set("players." + key + ".name", pdh.getName());
				saveRankDisk = true;
			} else {
				RankPath rp = pdh.getRankPath();
				if(rp != null) {
					main.getConfigManager().rankDataConfig.set("players." + key + ".rank", rp.getRankName());
					main.getConfigManager().rankDataConfig.set("players." + key + ".path", rp.getPathName());
				}
				main.getConfigManager().rankDataConfig.set("players." + key + ".name", pdh.getName());
				main.getConfigManager().prestigeDataConfig.set("players." + key, pdh.getPrestige());
				main.getConfigManager().rebirthDataConfig.set("players." + key, pdh.getRebirth());
				saveAllDisk = true;
			}
		}
		if(saveRankDisk) {
			main.getConfigManager().saveRankDataConfig();
		} else if (savePrestigeDisk) {
			main.getConfigManager().savePrestigeDataConfig();
		} else if (saveRebirthDisk) {
			main.getConfigManager().saveRebirthDataConfig();
		} else if (saveAllDisk) {
			main.getConfigManager().saveRankDataConfig();
			main.getConfigManager().savePrestigeDataConfig();
			main.getConfigManager().saveRebirthDataConfig();
		}
		return true;
	}

	public boolean storePlayerData(UUID uuid, PlayerDataType playerDataType, boolean saveToDisk) {
		String key = uuid.toString();
		PlayerDataHandler pdh = this.getPlayerData().get(key);
		if(playerDataType == PlayerDataType.RANK) {
			RankPath rp = pdh.getRankPath();
			if(rp != null) {
				main.getConfigManager().rankDataConfig.set("players." + key + ".rank", rp.getRankName());
				main.getConfigManager().rankDataConfig.set("players." + key + ".path", rp.getPathName());
				if(saveToDisk) main.getConfigManager().saveRankDataConfig();
			}
		} else if (playerDataType == PlayerDataType.PRESTIGE) {
			main.getConfigManager().prestigeDataConfig.set("players." + key, pdh.getPrestige());
			if(saveToDisk) main.getConfigManager().savePrestigeDataConfig();
		} else if (playerDataType == PlayerDataType.REBIRTH) { 
			main.getConfigManager().rebirthDataConfig.set("players." + key, pdh.getRebirth());
			if(saveToDisk) main.getConfigManager().saveRebirthDataConfig();
		} else if (playerDataType == PlayerDataType.NAME) {
			main.getConfigManager().rankDataConfig.set("players." + key + ".name", pdh.getName());
			if(saveToDisk) main.getConfigManager().saveRankDataConfig();
		} else {
			RankPath rp = pdh.getRankPath();
			if(rp != null) {
				main.getConfigManager().rankDataConfig.set("players." + key + ".rank", rp.getRankName());
				main.getConfigManager().rankDataConfig.set("players." + key + ".path", rp.getPathName());
			}
			main.getConfigManager().rankDataConfig.set("players." + key + ".name", pdh.getName());
			main.getConfigManager().prestigeDataConfig.set("players." + key, pdh.getPrestige());
			main.getConfigManager().rebirthDataConfig.set("players." + key, pdh.getRebirth());
			if(saveToDisk) {
				main.getConfigManager().saveRankDataConfig();
				main.getConfigManager().savePrestigeDataConfig();
				main.getConfigManager().saveRebirthDataConfig();
			}
		}
		return true;
	}

	public PlayerDataHandler unload(UUID uuid) {
		return getPlayerData().remove(uuid.toString());
	}

	public void savePlayerData(UUID uuid) {
		UUID player = uuid;
		if(main.isMySql()) {
			if(player != null) {
				PlayerDataHandler value = getPlayerData().get(player.toString());
				try {
					String u = value.getUUID().toString();
					String rankName = value.getRankPath().getRankName() == null ? defaultRank : value.getRankPath().getRankName();
					String pathName = value.getRankPath().getPathName() == null ? defaultPath : value.getRankPath().getPathName();
					String prestigeName = value.getPrestige() == null ? "none" : value.getPrestige();
					String rebirthName = value.getRebirth() == null ? "none" : value.getRebirth();
					Statement statement = main.getConnection().createStatement();
					MySqlUtils util = new MySqlUtils(statement, main.getDatabase() + "." + main.getTable());
					String name = value.getName();
					ResultSet result = statement.executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + u + "'");
					if(result.next()) {
						util.set(u, "rank", rankName);
						util.set(u, "path", pathName);
						util.set(u, "prestige", prestigeName);
						util.set(u, "rebirth", rebirthName);
						util.set(u, "name", name);
						util.set(u, "rankscore", main.prxAPI.getRankNumber(pathName, rankName));
						util.set(u, "prestigescore", main.prxAPI.getPrestigeNumber(prestigeName));
						util.set(u, "rebirthscore", main.prxAPI.getRebirthNumber(rebirthName));
						util.set(u, "stagescore", String.valueOf(main.prxAPI.getPower(rankName, pathName, prestigeName, rebirthName)));
						util.executeThenClose();
					} else {
						statement.executeUpdate("INSERT INTO " + main.getDatabase() + "." + main.getTable() +" (`uuid`, `name`, `rank`, `prestige`, `rebirth`, `path`) VALUES ('" + u + "', '" + name + "', '" + rankName + "', '" + prestigeName + "', '" + rebirthName + "', '" + pathName + "');");
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					Bukkit.getConsoleSender().sendMessage("�e[�9PrisonRanksX�e] �cSQL data update failed.");
					e1.printStackTrace();
					main.getLogger().info("<Error> Updating player sql data..");
				}
			}

			return;
		}

		if(player != null) {
			PlayerDataHandler pdh = getPlayerData().get(player.toString());
			if(pdh == null) {
				return;
			}
			if(pdh.getUUID() == null) {
				return;
			}
			String u = pdh.getUUID().toString();
			if(main.isRankEnabled) {
				main.getConfigManager().rankDataConfig.set("players." + u + ".rank", pdh.getRankPath().getRankName() != null ? pdh.getRankPath().getRankName() : defaultRank);
				main.getConfigManager().rankDataConfig.set("players." + u + ".path", pdh.getRankPath().getPathName() != null ? pdh.getRankPath().getPathName() : defaultPath);
			}
			main.getConfigManager().rankDataConfig.set("players." + u + ".name", pdh.getName());
			main.getConfigManager().prestigeDataConfig.set("players." + u, pdh.getPrestige());
			main.getConfigManager().rebirthDataConfig.set("players." + u, pdh.getRebirth());
		}

	}

	public void savePlayerData(Player player) {
		if(main.isMySql()) {
			if(player != null) {
				PlayerDataHandler value = getPlayerData().get(XUUID.getUUID(player).toString());
				try {
					String u = value.getUUID().toString();
					String rankName = value.getRankPath().getRankName() == null ? defaultRank : value.getRankPath().getRankName();
					String pathName = value.getRankPath().getPathName() == null ? defaultPath : value.getRankPath().getPathName();
					String prestigeName = value.getPrestige() == null ? "none" : value.getPrestige();
					String rebirthName = value.getRebirth() == null ? "none" : value.getRebirth();
					Statement statement = main.getConnection().createStatement();
					MySqlUtils util = new MySqlUtils(statement, main.getDatabase() + "." + main.getTable());
					String name = value.getName();
					ResultSet result = statement.executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + u + "'");
					if(result.next()) {
						util.set(u, "rank", rankName);
						util.set(u, "path", pathName);
						util.set(u, "prestige", prestigeName);
						util.set(u, "rebirth", rebirthName);
						util.set(u, "name", name);
						util.set(u, "rankscore", main.prxAPI.getRankNumber(pathName, rankName));
						util.set(u, "prestigescore", main.prxAPI.getPrestigeNumber(prestigeName));
						util.set(u, "rebirthscore", main.prxAPI.getRebirthNumber(rebirthName));
						util.set(u, "stagescore", String.valueOf(main.prxAPI.getPower(rankName, pathName, prestigeName, rebirthName)));
						util.executeThenClose();
					} else {
						statement.executeUpdate("INSERT INTO " + main.getDatabase() + "." + main.getTable() +" (`uuid`, `name`, `rank`, `prestige`, `rebirth`, `path`) VALUES ('" + u + "', '" + name + "', '" + rankName + "', '" + prestigeName + "', '" + rebirthName + "', '" + pathName + "');");
					}
				} catch (SQLException e1) {
					Bukkit.getConsoleSender().sendMessage("�e[�9PrisonRanksX�e] �cSQL data update failed.");
					e1.printStackTrace();
					main.getLogger().info("<Error> Updating player sql data..");
				}
			}

			return;
		}
		if(player != null) {
			PlayerDataHandler pdh = getPlayerData().get(XUUID.getUUID(player).toString());
			if(pdh.getUUID() == null) {
				return;
			}
			String u = pdh.getUUID().toString();
			main.getConfigManager().rankDataConfig.set("players." + u + ".rank", pdh.getRankPath().getRankName());
			main.getConfigManager().rankDataConfig.set("players." + u + ".path", pdh.getRankPath().getPathName());
			main.getConfigManager().rankDataConfig.set("players." + u + ".name", pdh.getName());
			main.getConfigManager().prestigeDataConfig.set("players." + u, pdh.getPrestige());
			main.getConfigManager().rebirthDataConfig.set("players." + u, pdh.getRebirth());
		}
	}

	public boolean fixNulls(final UUID uuid, final String name) {
		PlayerDataHandler pdh = null;
		if(getPlayerData().containsKey(uuid.toString())) {
			pdh = getPlayerData().get(uuid.toString());
		} else {
			pdh = new PlayerDataHandler(XUser.getXUser(uuid));
		}
		if(pdh.getUUID() == null) {
			pdh.setUUID(uuid);
		}
		if(pdh.getName() == null) {
			pdh.setName(name);
		}
		if(pdh.getRankPath() == null) {
			RankPath rp = new RankPath(defaultRank, defaultPath);
			pdh.setRankPath(rp);
		}
		if(pdh.getRankPath().getRankName() == null) {
			pdh.getRankPath().setRankName(defaultRank);
		}
		if(pdh.getRankPath().getPathName() == null) {
			pdh.getRankPath().setPathName(defaultPath);
		}
		getPlayerData().put(uuid.toString(), pdh);
		return true;
	}

}
