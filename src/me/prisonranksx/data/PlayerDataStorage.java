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

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
	
	public PlayerDataStorage(PrisonRanksX main) {this.main = main;
	  this.playerData = new HashMap<String, PlayerDataHandler>();
	  this.loadedUUIDs = new HashSet<>();
	  defaultPath = this.main.globalStorage.getStringData("defaultpath");
	  defaultRank = this.main.globalStorage.getStringData("defaultrank");
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
		    		   String rankName = result.getString("rank") == null ? defaultRank : result.getString("rank");
		    		   String pathName = result.getString("path") == null ? defaultPath : result.getString("path");
		    		   String prestigeName = result.getString("prestige").equals("none") ? null : result.getString("prestige");
		    		   String rebirthName = result.getString("rebirth").equals("none") ? null : result.getString("rebirth");
		    		   XUser user = XUser.getXUser(uuid);
                       PlayerDataHandler pdh =new PlayerDataHandler(user);
                       RankPath rp = new RankPath(rankName, pathName);
                       pdh.setUUID(user.getUUID());
                       pdh.setRankPath(rp);
                       if(prestigeName != null) {
                    	 pdh.setPrestige(prestigeName);  
                       }
                       if(rebirthName != null) {
                    	 pdh.setRebirth(rebirthName);
                       }
                       getPlayerData().put(uuid.toString(), pdh);
	                   
	                  
	           }
	               statement.close();


	           }
	           catch (SQLException e) {
	    			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data load failed.");
	    			e.printStackTrace();
	    			main.getLogger().info("Error loading player sql data");
	           }
	           return;
	}
		for(String strg : main.configManager.rankDataConfig.getConfigurationSection("players").getKeys(false)) {
			XUser xu = XUser.getXUser(strg);
			String str = xu.getUUID().toString();
			PlayerDataHandler pdh = new PlayerDataHandler(xu);
			RankPath rankPath = new RankPath(main.configManager.rankDataConfig.getString("players." + str + ".rank"), main.configManager.rankDataConfig.getString("players." + str + ".path"));
			if(main.configManager.prestigeDataConfig.getString("players." + str) != null) {
				pdh.setPrestige(main.configManager.prestigeDataConfig.getString("players."+ str));
			}
			if(main.configManager.rebirthDataConfig.getString("players." + str) != null) {
				pdh.setRebirth(main.configManager.rebirthDataConfig.getString("players." + str));
			}
			pdh.setRankPath(rankPath);
			pdh.setUUID(xu.getUUID());
			getPlayerData().put(str, pdh);
		}

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
	
	public void loadPlayerData(Player player) {
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
		getPlayerData().put(xu.getUUID().toString(), pdh);
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
			try {
				while (result.next()) {
				    rank = result.getString("rank") == null ? defaultRank : result.getString("rank");
				    prestige = result.getString("prestige").equals("none") ? null : result.getString("prestige");
				    rebirth = result.getString("rebirth").equals("none") ? null : result.getString("rebirth");
				    path = result.getString("path") == null ? defaultPath : result.getString("path");
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
	
	public String getPlayerRank(OfflinePlayer player) {
		return getPlayerData().get(XUser.getXUser(player).getUUID().toString()).getRankPath().getRankName();
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
	
	public void savePlayersData() {
		if(main.isMySql()) {
		           try {
		               String sql = "UPDATE " + main.database + "." + main.table + " SET rank=?,prestige=?,rebirth=?,name=?,path=? WHERE uuid=?";
		               PreparedStatement statement = main.connection.prepareStatement(sql);
		               boolean executeRequired = false;
		               for (String p : getPlayerData().keySet()) {
		            	   PlayerDataHandler value = getPlayerData().get(p);
			    			String rankName = value.getRankPath().getRankName() == null ? defaultRank : value.getRankPath().getRankName();
			    			String pathName = value.getRankPath().getPathName() == null ? defaultPath : value.getRankPath().getPathName();
			    			String prestigeName = value.getPrestige() == null ? "none" : value.getPrestige();
			    			String rebirthName = value.getRebirth() == null ? "none" : value.getRebirth();
			    			String name = main.isBefore1_7 ? XUUID.getLegacyName(UUID.fromString(p)) : Bukkit.getOfflinePlayer(UUID.fromString(p)).getName();
		                   statement.setString(1, p);
		                   statement.setString(2, name);
                           statement.setString(3, rankName);
                           statement.setString(4, prestigeName);
                           statement.setString(5, rebirthName);
                           statement.setString(6, pathName);
		                   statement.addBatch();
		                   executeRequired = true;
		               }
		               if (executeRequired) {
		                   statement.executeBatch();
		               }
		               statement.close();


		           }
		           catch (SQLException e) {
		    			Bukkit.getConsoleSender().sendMessage("§e[§9PrisonRanksX§e] §cSQL data update failed.");
		    			e.printStackTrace();
		    			main.getLogger().info("ERROR Updating Player SQL Data");
		           }
		           return;
		}
			for(Entry<String, PlayerDataHandler> player : getPlayerData().entrySet()) {
				if(player.getKey() != null) {
		main.configManager.rankDataConfig.set("players." + player.getKey() + ".rank", player.getValue().getRankPath().getRank());
		main.configManager.rankDataConfig.set("players." + player.getKey() + ".path", player.getValue().getRankPath().getPath());
		main.configManager.prestigeDataConfig.set("players." + player.getKey(), player.getValue().getPrestige());
		main.configManager.rebirthDataConfig.set("players." + player.getKey(), player.getValue().getRebirth());
				}
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
			    			String name = main.isBefore1_7 ? XUUID.getLegacyName(UUID.fromString(u)) : Bukkit.getOfflinePlayer(UUID.fromString(u)).getName();
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
			    			main.getLogger().info("ERROR Updating Player SQL Data");
			    		}
				}
			
			return;
		}
			
				if(player != null) {
					PlayerDataHandler pdh = getPlayerData().get(XUUID.getXUUID(player).toString());
					String u = pdh.getUUID().toString();
		main.configManager.rankDataConfig.set("players." + u + ".rank", pdh.getRankPath().getRank());
		main.configManager.rankDataConfig.set("players." + u + ".path", pdh.getRankPath().getPath());
		main.configManager.prestigeDataConfig.set("players." + u, pdh.getPrestige());
		main.configManager.rebirthDataConfig.set("players." + u, pdh.getRebirth());
				}
			
	}
	
}
