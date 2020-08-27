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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.utils.AccessibleString;
import me.prisonranksx.utils.MySqlStreamer;
import me.prisonranksx.utils.MySqlUtils;
import me.prisonranksx.utils.XUUID;

public class PlayerDataStorage {
	
	private PrisonRanksX main;
	private Map<String, PlayerDataHandler> playerData;
	private Set<String> loadedUUIDs;
	private String defaultPath = null;
	private String defaultRank = null;
	private int i = 0;
	
	public PlayerDataStorage(PrisonRanksX main) {this.main = main;
	  this.playerData = new HashMap<>();
	  this.loadedUUIDs = new HashSet<>();
	  this.defaultPath = this.main.globalStorage.getStringData("defaultpath");
	  this.defaultRank = this.main.globalStorage.getStringData("defaultrank");
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
	    			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data load failed.");
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
			ResultSet result = null;
			try {
				result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + xu.getUUID().toString() + "';");
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
		if(main.isMySql()) {
			ResultSet result = null;
			try {
				result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + uuid + "';");
			} catch (SQLException ex) {
				ex.printStackTrace();
				return false;
			}
			try {
				if(result.next()) {
					return true;
				} else {
					return false;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		} else {
		if(main.getConfigManager().rankDataConfig.isConfigurationSection("players." + uuid)) {
			return true;
		}
		return false;
		}
	}
	
	/**
	 * loads player data from mysql database or yaml file
	 * @param uuid
	 */
	public void loadPlayerData(UUID uuid) {
		XUser xu = XUser.getXUser(uuid.toString());
		if(main.isMySql()) {
			ResultSet result = null;
			try {
				result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + xu.getUUID().toString() + "';");
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

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		PlayerDataHandler pdh = new PlayerDataHandler(xu);
		if(!main.getConfigManager().rankDataConfig.isConfigurationSection("players." + xu.getUUID().toString())) {
			return;
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
}
	
	public void loadPlayerData(UUID uuid, String playerName) {
		XUser xu = XUser.getXUser(uuid.toString());
		if(main.isMySql()) {
			ResultSet result = null;
			try {
				result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + xu.getUUID().toString() + "';");
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

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		PlayerDataHandler pdh = new PlayerDataHandler(xu);
		if(!main.getConfigManager().rankDataConfig.isConfigurationSection("players." + xu.getUUID().toString())) {
			return;
		}
		String path = main.getConfigManager().rankDataConfig.getString("players." + xu.getUUID().toString() + ".path");
		RankPath rankPath = new RankPath(main.getConfigManager().rankDataConfig.getString("players." + xu.getUUID().toString() + ".rank"), path != null ? path : defaultPath);
		if(main.getConfigManager().prestigeDataConfig.getString("players." + xu.getUUID().toString()) != null) {
			pdh.setPrestige(main.getConfigManager().prestigeDataConfig.getString("players." + xu.getUUID().toString()));
		}
		if(main.getConfigManager().rebirthDataConfig.getString("players." + xu.getUUID()) != null) {
			pdh.setRebirth(main.getConfigManager().rebirthDataConfig.getString("players." + xu.getUUID().toString()));
		}
		// String name = main.getConfigManager().rankDataConfig.getString("players." + xu.getUUID().toString() + ".name");
		pdh.setRankPath(rankPath);
		pdh.setUUID(xu.getUUID());
		pdh.setName(playerName);
		getPlayerData().put(xu.getUUID().toString(), pdh);
		loadedUUIDs.add(xu.getUUID().toString());
}
	
	public boolean isRegistered(OfflinePlayer player) {
		return getPlayerData().get(XUUID.getXUUID(player).toString()) != null;
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
	
	public void register(OfflinePlayer player) {
		getPlayerData().put(XUser.getXUser(player).getUUID().toString(), new PlayerDataHandler(XUser.getXUser(player)));
	}
	
	public void register(final OfflinePlayer player, boolean defaultValues) {
		XUser xuser = XUser.getXUser(player);
		PlayerDataHandler pdh = new PlayerDataHandler(xuser);
		if(defaultValues) {
			pdh.setRankPath(new RankPath(defaultRank, defaultPath));
			pdh.setUUID(XUUID.getXUUID(player));
			pdh.setName(player.getName());
			getPlayerData().put(xuser.getUUID().toString(), pdh);
		} else {
		getPlayerData().put(xuser.getUUID().toString(), pdh);
		}
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
	public void register(final UUID uuid, boolean defaultValues, boolean ignoreName) {
		XUser xuser = XUser.getXUser(uuid);
		PlayerDataHandler pdh = new PlayerDataHandler(xuser);
		if(defaultValues) {
			pdh.setRankPath(new RankPath(defaultRank, defaultPath));
			pdh.setUUID(uuid);
			getPlayerData().put(xuser.getUUID().toString(), pdh);
		} else {
		getPlayerData().put(xuser.getUUID().toString(), pdh);
		}
    }
	
	/**
	 * 
	 * @param uuid
	 * @param name
	 * @param defaultValues
	 */
	public void register(final UUID uuid, final String name, boolean defaultValues) {
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
			main.getTaskChainFactory().newSharedChain("rank").async(() -> {
			ResultSet result = null;
			try {
				result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + uuidString + "';");
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
			main.getTaskChainFactory().newSharedChain("rank").async(() -> {
			ResultSet result = null;
			try {
				result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + uuidString + "';");
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
	
	public void setPlayerRank(OfflinePlayer player, String rankName, String pathName) {
		RankPath rankPath = new RankPath(rankName, pathName);
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRankPath(rankPath);
	}
	
	public void setPlayerRank(OfflinePlayer player, String rankName) {
		RankPath rankPath = new RankPath(rankName, getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath().getPathName());
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRankPath(rankPath);
	}
	
	public void setPlayerRank(OfflinePlayer player, RankPath rankPath) {
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRankPath(rankPath);
	}
	
	public void setPlayerRank(UUID uuid, RankPath rankPath) {
		getPlayerData().get(uuid.toString()).setRankPath(rankPath);
	}
	
	public void setPlayerRank(UUID uuid, String rankName) {
		RankPath rankPath = new RankPath(rankName, getPlayerData().get(XUser.getXUser(uuid.toString()).getUUID().toString()).getRankPath().getPathName());
		getPlayerData().get(XUser.getXUser(uuid.toString()).getUUID().toString()).setRankPath(rankPath);
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
			main.getTaskChainFactory().newSharedChain("prestige").async(() -> {
			ResultSet result = null;
			try {
				result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + uuidString + "';");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			};
			try {
				while (result.next()) {
				    prestige.setString(result.getString("prestige") == null ? null : result.getString("prestige"));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
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
		AccessibleString prestige = new AccessibleString();
		if(main.isMySql()) {
			main.getTaskChainFactory().newSharedChain("prestige").async(() -> {
			ResultSet result = null;
			try {
				result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + uuidString + "';");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			};
			try {
				while (result.next()) {
				    prestige.setString(result.getString("prestige") == null ? null : result.getString("prestige"));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}).execute();
		} else {
			prestige.setString(main.getConfigManager().prestigeDataConfig.getString("players." + uuidString));
		}
		return prestige.getString();
	}
	
	public String getPlayerPrestige(UUID uuid) {
		if(!isRegistered(uuid)) {
			return getPlayerPrestigeOffline(uuid);
		}
		return getPlayerData().get(uuid.toString()).getPrestige();
	}
	
	public void setPlayerPrestige(OfflinePlayer player, String prestigeName) {
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setPrestige(prestigeName);
	}
	
	public void setPlayerPrestige(UUID uuid, String prestigeName) {
		getPlayerData().get(new XUser(uuid).getUUID().toString()).setPrestige(prestigeName);
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
			main.getTaskChainFactory().newSharedChain("rebirth").async(() -> {
			ResultSet result = null;
			try {
				result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + uuidString + "';");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			};
			try {
				while (result.next()) {
				    rebirth.setString(result.getString("rebirth") == null ? null : result.getString("rebirth"));
				}
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
			main.getTaskChainFactory().newSharedChain("rebirth").async(() -> {
			ResultSet result = null;
			try {
				result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + uuidString + "';");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			};
			try {
				while (result.next()) {
				    rebirth.setString(result.getString("rebirth") == null ? null : result.getString("rebirth"));
				}
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
	
	public void setPlayerRebirth(OfflinePlayer player, String rebirthName) {
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRebirth(rebirthName);
	}
	
	public void setPlayerRebirth(UUID uuid, String rebirthName) {
		getPlayerData().get(new XUser(uuid).getUUID().toString()).setRebirth(rebirthName);
	}
	
	public String getPlayerPath(OfflinePlayer player) {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath().getPathName();
	}
	
	public String getPlayerPath(UUID uuid) {
		return getPlayerData().get(uuid.toString()).getRankPath().getPathName();
	}
	
	public void setPlayerPath(OfflinePlayer player, String pathName) {
		RankPath rankPath = new RankPath(getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath().getRankName(), pathName);
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRankPath(rankPath);
	}
	
	public void setPlayerPath(UUID uuid, String pathName) {
		RankPath rankPath = new RankPath(getPlayerData().get(new XUser(uuid).getUUID().toString()).getRankPath().getRankName(), pathName);
		getPlayerData().get(new XUser(uuid).getUUID().toString()).setRankPath(rankPath);
	}
	
	public void setPlayerRankPath(OfflinePlayer player, RankPath rankPath) {
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setRankPath(rankPath);
	}
	
	public void setPlayerRankPath(UUID uuid, RankPath rankPath) {
		getPlayerData().get(uuid.toString()).setRankPath(rankPath);
	}
	
	public RankPath getPlayerRankPathOffline(final OfflinePlayer player) {
		XUser user = XUser.getXUser(player);
		UUID uuid = user.getUUID();
		String uuidString = uuid.toString();	
		AccessibleString rank = new AccessibleString();
		AccessibleString path = new AccessibleString();
		if(main.isMySql()) {
			main.getTaskChainFactory().newSharedChain("rankpath").async(() -> {
			ResultSet result = null;
			try {
				result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + uuidString + "';");
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
			main.getTaskChainFactory().newSharedChain("rankpath").async(() -> {
			ResultSet result = null;
			try {
				result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + uuidString + "';");
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
		if(handler.getRankPath() == null) {
			return true;
		}
		if(handler.getName() == null || handler.getName().equals("null")) {
			return true;
		}
		if(handler.getUUID() == null) {
			return true;
		}
		if(handler.getRankPath().getRankName() == null || handler.getRankPath().getPathName() == null) {
			main.getLogger().info(handler.getName() + " has invalid data, fixing...");
			this.fixNulls(handler.getUUID(), handler.getName());
			return false;
		}
		if(!handler.getRankPath().getRankName().equalsIgnoreCase(defaultRank)) {
			return false;
		} else {
			if(handler.getPrestige() == null && handler.getRebirth() == null) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	private String getWordForm(int i, String singular, String plural) {
		return i <= 1 ? singular : plural;
	}
	
	public void saveLargePlayersData() {
		AtomicInteger i = new AtomicInteger(-1);
		Entry<String, PlayerDataHandler>[] array = (Entry<String, PlayerDataHandler>[])getPlayerData().entrySet().toArray(new Entry[0]);
		if(main.isMySql()) {
			try {
				main.getConnection().setAutoCommit(false);
	               String sql = "UPDATE " + main.getDatabase() + "." + main.getTable() + " SET name=?,rank=?,prestige=?,rebirth=?,path=? WHERE uuid=?";
	               PreparedStatement statement = main.getConnection().prepareStatement(sql);
	         
	               Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
	            	   main.getTaskChainFactory().newSharedChain("savelarge").async(() -> {
	            	   i.incrementAndGet();
	            	   int b = i.get();
	            	   String uuid = array[b].getKey();
	            	   PlayerDataHandler value = array[b].getValue();
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
		    
	                   statement.setString(1, uuid);
	                   statement.setString(2, name);
                    statement.setString(3, rankName);
                    statement.setString(4, prestigeName);
                    statement.setString(5, rebirthName);
                    statement.setString(6, pathName);
	                   statement.addBatch();
		    			} catch (SQLException ex) {
		    				Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data update failed.");
			    			ex.printStackTrace();
			    			main.getLogger().info("<Error> Updating player sql data..");
		    			}
	            	   }).execute();
	               }, 1, 1);
	               statement.executeBatch();
	               main.getConnection().commit();
	               Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §9Updated §a" + String.valueOf(i.get()) + " §9" + getWordForm(i.get(), "Entry", "Entries") + ".");
	               statement.close();
	               main.getConnection().setAutoCommit(true);
	           }
	           catch (SQLException e) {
	    			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data update failed.");
	    			e.printStackTrace();
	    			main.getLogger().info("<Error> Updating player sql data..");
	           }
           return;
		}
		Bukkit.getScheduler().runTaskTimer(main, () -> {
			main.getTaskChainFactory().newSharedChain("savelarge").async(() -> {
				i.incrementAndGet();
		Entry<String, PlayerDataHandler> player = array[i.get()];
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
		}, 1, 1);
	}
	
	public void savePlayersData() {
		if(Bukkit.getOnlinePlayers().size() > 125) {
			saveLargePlayersData();
			return;
		}
		i = 0;
		if(main.isMySql()) {
		           try {
		        	   main.getConnection().setAutoCommit(false);
		        	   String sql = "UPDATE " + main.getDatabase() + "." + main.getTable() + " SET `name`=?,`rank`=?,`prestige`=?,`rebirth`=?,`path`=? WHERE uuid=?";
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
		                   statement.setString(1, uuid);
		                   statement.setString(2, name);
                           statement.setString(3, rankName);
                           statement.setString(4, prestigeName);
                           statement.setString(5, rebirthName);
                           statement.setString(6, pathName);
		                   statement.addBatch();
			    			} catch (SQLException ex) {
			    				Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data update failed.");
				    			ex.printStackTrace();
				    			main.getLogger().info("<Error> Updating player sql data..");
			    			}
		               });
		               int[] updated = statement.executeBatch();
		               main.getConnection().commit();
		               Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §9Updated §a" + String.valueOf(updated) + " §9" + getWordForm(i, "Entry", "Entries") + ".");
		               statement.close();
		               main.getConnection().setAutoCommit(true);
		           }
		           catch (SQLException e) {
		    			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data update failed.");
		    			e.printStackTrace();
		    			main.getLogger().info("<Error> Updating player sql data..");
		           }
		           return;
		}
			for(Entry<String, PlayerDataHandler> player : getPlayerData().entrySet()) {
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
			}
	}
	
	public void savePlayersDataMySql() {
		if(Bukkit.getOnlinePlayers().size() > 125) {
			saveLargePlayersData();
			return;
		}
		i = 0;
		if(main.isMySql()) {
		           try {
		        	   main.getConnection().setAutoCommit(false);
		        	   String sql = "UPDATE " + main.getDatabase() + "." + main.getTable() + " SET `name`=?,`rank`=?,`prestige`=?,`rebirth`=?,`path`=? WHERE uuid=?";
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
		                   statement.setString(1, uuid);
		                   statement.setString(2, name);
                           statement.setString(3, rankName);
                           statement.setString(4, prestigeName);
                           statement.setString(5, rebirthName);
                           statement.setString(6, pathName);
		                   statement.addBatch();
			    			} catch (SQLException ex) {
			    				Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data update failed.");
				    			ex.printStackTrace();
				    			main.getLogger().info("<Error> Updating player sql data..");
			    			}
		               });
		               statement.executeBatch();  
		               main.getConnection().commit();
		               statement.close();
		               main.getConnection().setAutoCommit(true);
		           }
		           catch (SQLException e) {
		    			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data update failed.");
		    			e.printStackTrace();
		    			main.getLogger().info("<Error> Updating player sql data..");
		           }
		           return;
		}
	}
	
	public enum PlayerDataType {
		RANK, PRESTIGE, REBIRTH, NAME, ALL
	}
	
	public void storePlayerData(UUID uuid, PlayerDataType playerDataType) {
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
	}
	
	/**
	 * update player data temporarily to refresh the leaderboards
	 * @param playerDataType
	 */
	public void storePlayersData(PlayerDataType playerDataType) {
		if(main.isMySql()) {
			savePlayersDataMySql();
			return;
		}
		for(Entry<String, PlayerDataHandler> entry : this.getPlayerData().entrySet()) {
			String key = entry.getKey();
			PlayerDataHandler pdh = entry.getValue();
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
		}
	}
	
	public void storePlayersData(PlayerDataType playerDataType, boolean saveToDisk) {
		boolean saveRankDisk = false;
		boolean savePrestigeDisk = false;
		boolean saveRebirthDisk = false;
		boolean saveAllDisk = false;
		for(Entry<String, PlayerDataHandler> entry : this.getPlayerData().entrySet()) {
			String key = entry.getKey();
			PlayerDataHandler pdh = entry.getValue();
		if(playerDataType == PlayerDataType.RANK) {
			RankPath rp = pdh.getRankPath();
			main.getConfigManager().rankDataConfig.set("players." + key + ".rank", rp.getRankName());
			main.getConfigManager().rankDataConfig.set("players." + key + ".path", rp.getPathName());
			saveRankDisk = true;
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
			main.getConfigManager().rankDataConfig.set("players." + key + ".rank", rp.getRankName());
			main.getConfigManager().rankDataConfig.set("players." + key + ".path", rp.getPathName());
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
	}
	
	public void storePlayerData(UUID uuid, PlayerDataType playerDataType, boolean saveToDisk) {
		String key = uuid.toString();
		PlayerDataHandler pdh = this.getPlayerData().get(key);
		if(playerDataType == PlayerDataType.RANK) {
			RankPath rp = pdh.getRankPath();
			main.getConfigManager().rankDataConfig.set("players." + key + ".rank", rp.getRankName());
			main.getConfigManager().rankDataConfig.set("players." + key + ".path", rp.getPathName());
			if(saveToDisk) main.getConfigManager().saveRankDataConfig();
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
			main.getConfigManager().rankDataConfig.set("players." + key + ".rank", rp.getRankName());
			main.getConfigManager().rankDataConfig.set("players." + key + ".path", rp.getPathName());
			main.getConfigManager().rankDataConfig.set("players." + key + ".name", pdh.getName());
			main.getConfigManager().prestigeDataConfig.set("players." + key, pdh.getPrestige());
			main.getConfigManager().rebirthDataConfig.set("players." + key, pdh.getRebirth());
			if(saveToDisk) {
				main.getConfigManager().saveRankDataConfig();
				main.getConfigManager().savePrestigeDataConfig();
				main.getConfigManager().saveRebirthDataConfig();
			}
		}
	}
	
	public void unload(UUID uuid) {
		getPlayerData().remove(uuid.toString());
	}
	
	public void savePlayerData(UUID uuid) {
		UUID player = uuid;
		if(main.isMySql()) {
			if(player != null) {
				PlayerDataHandler value = getPlayerData().get(XUUID.fetchUUID(player).toString());
		    		try {
		    			String u = value.getUUID().toString();
		    			String rankName = value.getRankPath().getRankName() == null ? defaultRank : value.getRankPath().getRankName();
		    			String pathName = value.getRankPath().getPathName() == null ? defaultPath : value.getRankPath().getPathName();
		    			String prestigeName = value.getPrestige() == null ? "none" : value.getPrestige();
		    			String rebirthName = value.getRebirth() == null ? "none" : value.getRebirth();
		    			MySqlUtils util = new MySqlUtils(main.getMySqlStatement(), main.getDatabase() + "." + main.getTable());
		    			String name = value.getName();
		    			ResultSet result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + u + "'");
		    			if(result.next()) {
		    				util.set(u, "rank", rankName);
		    				util.set(u, "path", pathName);
		    				util.set(u, "prestige", prestigeName);
		    				util.set(u, "rebirth", rebirthName);
		    				util.set(u, "name", name);
		    			} else {
		    				main.getMySqlStatement().executeUpdate("INSERT INTO " + main.getDatabase() + "." + main.getTable() +" (`uuid`, `name`, `rank`, `prestige`, `rebirth`, `path`) VALUES ('" + u + "', '" + name + "', '" + rankName + "', '" + prestigeName + "', '" + rebirthName + "', '" + pathName + "');");
		    			}
		    		} catch (SQLException e1) {
		    			// TODO Auto-generated catch block
		    			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data update failed.");
		    			e1.printStackTrace();
		    			main.getLogger().info("<Error> Updating player sql data..");
		    		}
			}
		
		return;
	}
		
			if(player != null) {
				PlayerDataHandler pdh = getPlayerData().get(XUUID.fetchUUID(player).toString());
				if(pdh.getUUID() == null) {
					return;
				}
				String u = pdh.getUUID().toString();
	main.getConfigManager().rankDataConfig.set("players." + u + ".rank", pdh.getRankPath().getRankName() != null ? pdh.getRankPath().getRankName() : defaultRank);
	main.getConfigManager().rankDataConfig.set("players." + u + ".path", pdh.getRankPath().getPathName() != null ? pdh.getRankPath().getPathName() : defaultPath);
	main.getConfigManager().rankDataConfig.set("players." + u + ".name", pdh.getName());
	main.getConfigManager().prestigeDataConfig.set("players." + u, pdh.getPrestige());
	main.getConfigManager().rebirthDataConfig.set("players." + u, pdh.getRebirth());
			}
		
	}
	
	public void savePlayerData(Player player) {
		if(main.isMySql()) {
				if(player != null) {
					PlayerDataHandler value = getPlayerData().get(XUUID.getXUUID(player).toString());
			    		try {
			    			String u = value.getUUID().toString();
			    			String rankName = value.getRankPath().getRankName() == null ? defaultRank : value.getRankPath().getRankName();
			    			String pathName = value.getRankPath().getPathName() == null ? defaultPath : value.getRankPath().getPathName();
			    			String prestigeName = value.getPrestige() == null ? "none" : value.getPrestige();
			    			String rebirthName = value.getRebirth() == null ? "none" : value.getRebirth();
			    			MySqlUtils util = new MySqlUtils(main.getMySqlStatement(), main.getDatabase() + "." + main.getTable());
			    			String name = value.getName();
			    			ResultSet result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.getDatabase() + "." + main.getTable() + " WHERE uuid = '" + u + "'");
			    			if(result.next()) {
			    				util.set(u, "rank", rankName);
			    				util.set(u, "path", pathName);
			    				util.set(u, "prestige", prestigeName);
			    				util.set(u, "rebirth", rebirthName);
			    				util.set(u, "name", name);
			    			} else {
			    				main.getMySqlStatement().executeUpdate("INSERT INTO " + main.getDatabase() + "." + main.getTable() +" (`uuid`, `name`, `rank`, `prestige`, `rebirth`, `path`) VALUES ('" + u + "', '" + name + "', '" + rankName + "', '" + prestigeName + "', '" + rebirthName + "', '" + pathName + "');");
			    			}
			    		} catch (SQLException e1) {
			    			// TODO Auto-generated catch block
			    			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data update failed.");
			    			e1.printStackTrace();
			    			main.getLogger().info("<Error> Updating player sql data..");
			    		}
				}
			
			return;
		}
			
				if(player != null) {
					PlayerDataHandler pdh = getPlayerData().get(XUUID.getXUUID(player).toString());
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
	
	public void fixNulls(final UUID uuid, final String name) {
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
	}
	
}
