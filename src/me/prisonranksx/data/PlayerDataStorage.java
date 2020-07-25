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

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;
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
	  this.playerData = new HashMap<String, PlayerDataHandler>();
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
	               String sql = "SELECT * FROM " + main.database + "." + main.table;
	               Statement statement = main.connection.createStatement();
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
		ConfigurationSection players = main.configManager.rankDataConfig.getConfigurationSection("players");
		players.getKeys(false).forEach(strg -> {
			XUser xu = XUser.getXUser(strg);
			String str = xu.getUUID().toString();
			if(str != null && !str.equalsIgnoreCase("null")) {
			PlayerDataHandler pdh = new PlayerDataHandler(xu);
			String path = main.configManager.rankDataConfig.getString("players." + str + ".path");
			RankPath rankPath = new RankPath(main.configManager.rankDataConfig.getString("players." + str + ".rank"), path != null ? path : defaultPath);
			if(main.configManager.prestigeDataConfig.getString("players." + str) != null) {
				pdh.setPrestige(main.configManager.prestigeDataConfig.getString("players."+ str));
			}
			if(main.configManager.rebirthDataConfig.getString("players." + str) != null) {
				pdh.setRebirth(main.configManager.rebirthDataConfig.getString("players." + str));
			}
			String name = main.configManager.rankDataConfig.getString("players." + str + ".name");
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
			RankPath rankPath = new RankPath(main.configManager.rankDataConfig.getString("players." + xu.getUUID() + ".rank"), main.configManager.rankDataConfig.getString("players." + xu.getUUID() + ".path"));
			if(main.configManager.prestigeDataConfig.getString("players." + xu.getUUID()) != null) {
				pdh.setPrestige(main.configManager.prestigeDataConfig.getString("players." + xu.getUUID()));
			}
			if(main.configManager.rebirthDataConfig.getString("players." + xu.getUUID()) != null) {
				pdh.setRebirth(main.configManager.rebirthDataConfig.getString("players." + xu.getUUID()));
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
				result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.database + "." + main.table + " WHERE UUID = " + xu.getUUID().toString() + ";");
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
		RankPath rankPath = new RankPath(main.configManager.rankDataConfig.getString("players." + xu.getUUID() + ".rank"), main.configManager.rankDataConfig.getString("players." + xu.getUUID() + ".path"));
		if(main.configManager.prestigeDataConfig.getString("players." + xu.getUUID()) != null) {
			pdh.setPrestige(main.configManager.prestigeDataConfig.getString("players." + xu.getUUID()));
		}
		if(main.configManager.rebirthDataConfig.getString("players." + xu.getUUID()) != null) {
			pdh.setRebirth(main.configManager.rebirthDataConfig.getString("players." + xu.getUUID()));
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

	/**
	 * loads player data from mysql database or yaml file
	 * @param uuid
	 */
	public void loadPlayerData(UUID uuid) {
		XUser xu = XUser.getXUser(uuid.toString());
		if(main.isMySql()) {
			ResultSet result = null;
			try {
				result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.database + "." + main.table + " WHERE uuid = '" + xu.getUUID().toString() + "';");
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
		if(!main.configManager.rankDataConfig.isConfigurationSection("players." + xu.getUUID().toString())) {
			return;
		}
		String path = main.configManager.rankDataConfig.getString("players." + xu.getUUID().toString() + ".path");
		RankPath rankPath = new RankPath(main.configManager.rankDataConfig.getString("players." + xu.getUUID().toString() + ".rank"), path != null ? path : defaultPath);
		if(main.configManager.prestigeDataConfig.getString("players." + xu.getUUID().toString()) != null) {
			pdh.setPrestige(main.configManager.prestigeDataConfig.getString("players." + xu.getUUID().toString()));
		}
		if(main.configManager.rebirthDataConfig.getString("players." + xu.getUUID().toString()) != null) {
			pdh.setRebirth(main.configManager.rebirthDataConfig.getString("players." + xu.getUUID().toString()));
		}
		String name = main.configManager.rankDataConfig.getString("players." + xu.getUUID().toString() + ".name");
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
				result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.database + "." + main.table + " WHERE uuid = '" + xu.getUUID().toString() + "';");
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
		if(!main.configManager.rankDataConfig.isConfigurationSection("players." + xu.getUUID().toString())) {
			return;
		}
		String path = main.configManager.rankDataConfig.getString("players." + xu.getUUID().toString() + ".path");
		RankPath rankPath = new RankPath(main.configManager.rankDataConfig.getString("players." + xu.getUUID().toString() + ".rank"), path != null ? path : defaultPath);
		if(main.configManager.prestigeDataConfig.getString("players." + xu.getUUID().toString()) != null) {
			pdh.setPrestige(main.configManager.prestigeDataConfig.getString("players." + xu.getUUID().toString()));
		}
		if(main.configManager.rebirthDataConfig.getString("players." + xu.getUUID()) != null) {
			pdh.setRebirth(main.configManager.rebirthDataConfig.getString("players." + xu.getUUID().toString()));
		}
		// String name = main.configManager.rankDataConfig.getString("players." + xu.getUUID().toString() + ".name");
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
		try {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath().getRankName();
		} catch (NullPointerException exception) {
			main.getLogger().info(player.getName() + " rankpath is unavailable, resetting him to the first rank.");
			this.register(player, true);
			if(main.debug) {
				exception.printStackTrace();
			}
			return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath().getRankName();
		}
	}
	
	public String getPlayerRank(UUID uuid) {
		return getPlayerData().get(uuid.toString()).getRankPath().getRankName();
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
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getPrestige();
	}
	
	public String getPlayerPrestige(UUID uuid) {
		return getPlayerData().get(uuid.toString()).getPrestige();
	}
	
	public void setPlayerPrestige(OfflinePlayer player, String prestigeName) {
		getPlayerData().get(XUser.getXUser(player).getUUID().toString()).setPrestige(prestigeName);
	}
	
	public void setPlayerPrestige(UUID uuid, String prestigeName) {
		getPlayerData().get(new XUser(uuid).getUUID().toString()).setPrestige(prestigeName);
	}
	
	public String getPlayerRebirth(OfflinePlayer player) {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRebirth();
	}
	
	public String getPlayerRebirth(UUID uuid) {
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
	
	public RankPath getPlayerRankPath(OfflinePlayer player) {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath();
	}
	
	public RankPath getPlayerRankPath(UUID uuid) {
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
	
	public void savePlayersData() {
		i = 0;
		if(main.isMySql()) {
		           try {
		               String sql = "UPDATE " + main.database + "." + main.table + " SET name=?,rank=?,prestige=?,rebirth=?,path=? WHERE uuid=?";
		               PreparedStatement statement = main.connection.prepareStatement(sql);
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
			    			String rankName = value.getRankPath().getRankName() == null ? defaultRank : value.getRankPath().getRankName();
			    			String pathName = value.getRankPath().getPathName() == null ? defaultPath : value.getRankPath().getPathName();
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
		                   statement.execute();
			    			} catch (SQLException ex) {
			    				Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data update failed.");
				    			ex.printStackTrace();
				    			main.getLogger().info("<Error> Updating player sql data..");
			    			}
		               });
		               Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §9Updated §a" + String.valueOf(i) + " §9" + getWordForm(i, "Entry", "Entries") + ".");
		               statement.close();
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
		main.configManager.rankDataConfig.set("players." + key + ".rank", rp.getRankName() != null ? rp.getRankName() : defaultRank);
		main.configManager.rankDataConfig.set("players." + key + ".path", rp.getPathName() != null ? rp.getPathName() : defaultPath);
		main.configManager.rankDataConfig.set("players." + key + ".name", value.getName());
		main.configManager.prestigeDataConfig.set("players." + key, value.getPrestige());
		main.configManager.rebirthDataConfig.set("players." + key, value.getRebirth());
				}
			}
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
		    			MySqlUtils util = new MySqlUtils(main.getMySqlStatement(), main.database + "." + main.table);
		    			String name = value.getName();
		    			ResultSet result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.database + "." + main.table + " WHERE uuid = '" + u + "'");
		    			if(result.next()) {
		    				util.set(u, "rank", rankName);
		    				util.set(u, "path", pathName);
		    				util.set(u, "prestige", prestigeName);
		    				util.set(u, "rebirth", rebirthName);
		    				util.set(u, "name", name);
		    			} else {
		    				main.getMySqlStatement().executeUpdate("INSERT INTO " + main.database + "." + main.table +" (uuid, name, rank, prestige, rebirth, path) VALUES ('" + u + "', '" + name + "', '" + rankName + "', '" + prestigeName + "', '" + rebirthName + "', '" + pathName + "');");
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
	main.configManager.rankDataConfig.set("players." + u + ".rank", pdh.getRankPath().getRankName() != null ? pdh.getRankPath().getRankName() : defaultRank);
	main.configManager.rankDataConfig.set("players." + u + ".path", pdh.getRankPath().getPathName() != null ? pdh.getRankPath().getPathName() : defaultPath);
	main.configManager.rankDataConfig.set("players." + u + ".name", pdh.getName());
	main.configManager.prestigeDataConfig.set("players." + u, pdh.getPrestige());
	main.configManager.rebirthDataConfig.set("players." + u, pdh.getRebirth());
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
			    			MySqlUtils util = new MySqlUtils(main.getMySqlStatement(), main.database + "." + main.table);
			    			String name = value.getName();
			    			ResultSet result = main.getMySqlStatement().executeQuery("SELECT * FROM " + main.database + "." + main.table + " WHERE uuid = '" + u + "'");
			    			if(result.next()) {
			    				util.set(u, "rank", rankName);
			    				util.set(u, "path", pathName);
			    				util.set(u, "prestige", prestigeName);
			    				util.set(u, "rebirth", rebirthName);
			    				util.set(u, "name", name);
			    			} else {
			    				main.getMySqlStatement().executeUpdate("INSERT INTO " + main.database + "." + main.table +" (uuid, name, rank, prestige, rebirth, path) VALUES ('" + u + "', '" + name + "', '" + rankName + "', '" + prestigeName + "', '" + rebirthName + "', '" + pathName + "');");
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
		main.configManager.rankDataConfig.set("players." + u + ".rank", pdh.getRankPath().getRankName());
		main.configManager.rankDataConfig.set("players." + u + ".path", pdh.getRankPath().getPathName());
		main.configManager.rankDataConfig.set("players." + u + ".name", pdh.getName());
		main.configManager.prestigeDataConfig.set("players." + u, pdh.getPrestige());
		main.configManager.rebirthDataConfig.set("players." + u, pdh.getRebirth());
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
