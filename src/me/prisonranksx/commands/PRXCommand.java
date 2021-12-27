package me.prisonranksx.commands;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;
import com.google.common.io.Files;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.IPrestigeDataHandler;
import me.prisonranksx.data.RankDataHandler;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.data.RebirthDataHandler;
import me.prisonranksx.data.XUser;
import me.prisonranksx.events.PrestigeUpdateCause;
import me.prisonranksx.events.RankUpdateCause;
import me.prisonranksx.events.RebirthUpdateCause;
import me.prisonranksx.events.PrestigeUpdateEvent;
import me.prisonranksx.events.RankUpdateEvent;
import me.prisonranksx.events.RebirthUpdateEvent;
import me.prisonranksx.utils.AccessibleBukkitTask;
import me.prisonranksx.utils.AccessibleString;
import me.prisonranksx.utils.CollectionUtils;
import me.prisonranksx.utils.HolidayUtils.Holiday;

public class PRXCommand extends BukkitCommand {
	
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	private String ver = "1.0";
	private List<String> placeholders;
	private boolean is1_16;
	private Set<String> confirmation;
	public PRXCommand(String commandName) {
		super(commandName);
		this.setDescription(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".description", "Manage ranks,prestiges,rebirths settings")));
		this.setUsage(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".usage", "/prx help [page]")));
		this.setPermission(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".permission", "prisonranksx.admin"));
		this.setPermissionMessage(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".permission-message", "&cYou don't have permission to execute this command.")));
		this.setAliases(main.getConfigManager().commandsConfig.getStringList("commands." + commandName + ".aliases"));
		ver = main.getDescription().getVersion();
		is1_16 = Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18") || Bukkit.getVersion().contains("1.19");
		placeholders = Arrays.asList(
				"%prisonranksx_currentrank_name%", "%prisonranksx_currentrank_displayname%"
				,"%prisonranksx_rankup_percentage%", "%prisonranksx_rankup_percentage_decimal%"
				,"%prisonranksx_rankup_percentage_nolimit%", "%prisonranksx_rankup_percentage_decimal_nolimit%"
				,"%prisonranksx_rankup_percentage_plain%", "%prisonranksx_rankup_progress%"
				,"%prisonranksx_rankup_progress_double%", "%prisonranksx_rankup_name%"
				,"%prisonranksx_rankup_displayname%", "%prisonranksx_rankup_cost%"
				,"%prisonranksx_rankup_cost_formatted%", "%prisonranksx_rank_displayname_<rank>%"
				,"%prisonranksx_rank_cost_<rank>%", "%prisonranksx_rank_costformatted_<rank>%"
				,"%prisonranksx_currentrank_lastcolors%", "%prisonranksx_currentrank_afterbracketcolor%"
				,"%prisonranksx_currentrank_afterspacecolor%", "%prisonranksx_currentrank_colors%"
				,"%prisonranksx_currentrank_name_<playername>%", "%prisonranksx_rankup_name_<playername>%"
				,"%prisonranksx_rankup_cost_plain%", "%prisonranksx_rankup_cost_integer%"
				,"%prisonranksx_rankup_cost_integer_plain%", "%prisonranksx_prestige_name%"
				,"%prisonranksx_prestige_displayname%", "%prisonranksx_prestige_cost%"
				,"%prisonranksx_prestige_cost_formatted%", "%prisonranksx_nextprestige_name%"
				,"%prisonranksx_nextprestige_displayname%", "%prisonranksx_nextprestige_cost%"
				,"%prisonranksx_nextprestige_cost_formatted%", "%prisonranksx_prestige_displayname_<prestige>%"
				,"%prisonranksx_prestige_cost_<prestige%", "%prisonranksx_prestige_costformatted_<prestige>%"
				,"%prisonranksx_has_prestiged%", "%prisonranksx_prestige_name_<playername>%"
				,"%prisonranksx_nextprestige_name_<playername>%", "%prisonranksx_nextprestige_cost_plain%"
				,"%prisonranksx_nextprestige_cost_integer%", "%prisonranksx_nextprestige_cost_integer_plain%"
				,"%prisonranksx_rebirth_name%", "%prisonranksx_rebirth_displayname%"
				,"%prisonranksx_nextrebirth_name%" ,"%prisonranksx_nextrebirth_displayname%"
				,"%prisonranksx_nextrebirth_cost%", "%prisonranksx_nextrebirth_cost_formatted%"
				,"%prisonranksx_rebirth_displayname_<rebirth>%", "%prisonranksx_rebirth_cost_<rebirth>%"
				,"%prisonranksx_rebirth_costformatted_<rebirth>%", "%prisonranksx_has_rebirthed%"
				,"%prisonranksx_rebirth_name_<playername>%", "%prisonranksx_nextrebirth_name_<playername>%"
				,"%prisonranksx_nextrebirth_cost_plain%", "%prisonranksx_nextrebirth_cost_integer%"
				,"%prisonranksx_nextrebirth_cost_integer_plain%", "%prisonranksx_name_rank_<number>%"
				,"%prisonranksx_value_rank_<number>%", "%prisonranksx_name_prestige_<number>%"
				,"%prisonranksx_value_prestige_<number>%", "%prisonranksx_name_rebirth_<number>%"
				,"%prisonranksx_value_rebirth_<number>%", "%prisonranksx_money%"
				,"%prisonranksx_money_nonformatted%", "%prisonranksx_money_decimalformatted%"
				,"%prisonranksx_next_percentage%", "%prisonranksx_next_percentage_decimal%"
				,"%prisonranksx_next_progress%", "%prisonranksx_next_progress_double%"
				,"%prisonranksx_plain_{placeholder}%", "%prisonranksx_current_displayname%"
				, "%prisonranksx_name_stage_<number>%", "%prisonranksx_value_stage_<number>%", ".");
		placeholders = CollectionUtils.columnizeList(placeholders, 3, ", ", ".");
		confirmation = Sets.newHashSet();
	}

	public String getRandomHexColors() {
		StringBuilder sb = new StringBuilder("#");
		for(int i = 0 ; i < 6 ; i++) {
			sb.append(getRandomColor());
		}
		return sb.toString();
	}
	
	public String getRandomColor() {
		int rand = main.prxAPI.numberAPI.getRandomInteger(0, 15);
			switch (rand) {
			  case 10: return "a";
			  case 11: return "b";
			  case 12: return "c";
			  case 13: return "d";
			  case 14: return "e";
			  case 15: return "f";
			  default: return String.valueOf(rand);
			}
	}
	
	public String getRandomFormat() {
		int rand = main.prxAPI.numberAPI.getRandomInteger(0, 4);
		switch (rand) {
		case 0: return "l";
		case 1: return "n";
		case 2: return "m";
		case 3: return "o";
		default: return "k";
		}
	}
	
	public String getRandomBasicFormat() {
		int rand = main.prxAPI.numberAPI.getRandomInteger(0, 2);
		switch (rand) {
		case 0: return "l";
		case 1: return "n";
		default: return "o";
		}
	}
	
	/**
	 * <p>Creates a copy of file in addition to paying attention to the amount of the same copied files
	 * <p>For example if we have file named <i>'test.txt'</i>:
	 * <p><pre> the file 'test.txt' didn't have any backup before, therefore the new file name will be: 'test_backup_0.txt'</pre>
	 * <p><pre> if it already had a backup the new file name will be: 'test_backup_1.txt'</pre>
	 * @param file to be copied and preserved.
	 * @return true if the backup was successful, false otherwise.
	 */
	public boolean backupFile(File file) {
		if(file == null) return false;
		String fileName = file.getName();
		String safeName = fileName.split("\\.")[0];
		String firstName = safeName + "_backup_" + "0";
		String newPath = file.getPath().replace(safeName, firstName);
	    File newFile = new File(newPath);
	    AccessibleString lastName = AccessibleString.createNullable(null);
	    if(newFile.exists()) {
	    	File f = new File(file.getPath());
	    	File[] matchingFiles = f.listFiles(new FilenameFilter() {
	    	    public boolean accept(File dir, String name) {
	    	        return name.contains("_backup_");
	    	    }
	    	});
	    	main.debug(matchingFiles.length);
	    	lastName.setString(matchingFiles[matchingFiles.length-1].getPath());
	    	String foundPath = lastName.getString();
	    	String beforeDot = foundPath.split("\\.")[0];
	    	String splitBackupNumber = beforeDot.split("_backup_")[1];
	    	int newInteger = Integer.parseInt(splitBackupNumber)+1;
	    	String newBackupPath = foundPath.replace("_backup_" + splitBackupNumber, "_backup_" + String.valueOf(newInteger));
	    	try {
				Files.copy(file, new File(newBackupPath));
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
	    } else {
	    	try {
				Files.copy(file, newFile);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
	    }
	    
	}
	
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(!sender.hasPermission(this.getPermission())) {
			
			sender.sendMessage(this.getPermissionMessage());
			return true;
		}

        if(args.length == 0) {
        	if(main.isInDisabledWorld(sender)) {return true;}
        	main.getHolidayUtils().getHelpMessage1().forEach(line -> {
        		sender.sendMessage(line.replace("%version%", ver));
        	});
        } else if (args.length == 1) {
        	if(args[0].equalsIgnoreCase("banana")) {
        		if(!main.allowEasterEggs) return true;
        		sender.sendMessage(main.prxAPI.c("&e&oBananizing the leaderboard..."));
        		Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
        		main.lbm.clearUpdatedValues();
        		main.lbm.setUpdate(true);
        		main.lbm.getRankLeaderboard();
        		main.lbm.getPrestigeLeaderboard();
        		main.lbm.getRebirthLeaderboard();
        		main.lbm.getGlobalLeaderboard();
        		});
        		Bukkit.broadcastMessage(main.prxAPI.c("&e&lBANANA!"));
        	}
        	else if (args[0].equalsIgnoreCase("halloween")) {
        		if(!main.allowEasterEggs) return true;
        		Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
        		sender.sendMessage(main.prxAPI.c("&6Changing help menu theme..."));
        		main.getHolidayUtils().setHoliday(Holiday.HALLOWEEN);
        		main.getHolidayUtils().setup();
        		sender.sendMessage(main.prxAPI.c("&6Done."));
        		});
        	}
        	else if (args[0].equalsIgnoreCase("christmas")) {
        		if(!main.allowEasterEggs) return true;
        		Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
        		sender.sendMessage(main.prxAPI.c("&aChanging &chelp &amenu &ctheme&a..."));
        		main.getHolidayUtils().setHoliday(Holiday.CHRISTMAS);
        		main.getHolidayUtils().setup();
        		sender.sendMessage(main.prxAPI.c("&aD&co&an&ce&a."));
        		});
        	}
        	else if (args[0].equalsIgnoreCase("valentine")) {
        		if(!main.allowEasterEggs) return true;
        		Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
        		sender.sendMessage(main.prxAPI.c("&4Changing help menu theme&c..."));
        		main.getHolidayUtils().setHoliday(Holiday.VALENTINE);
        		main.getHolidayUtils().setup();
        		sender.sendMessage(main.prxAPI.c("&4Done&c."));
        		});
        	}
        	else if (args[0].equalsIgnoreCase("none")) {
        		if(!main.allowEasterEggs) return true;
        		Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
        		sender.sendMessage(main.prxAPI.c("&7Changing help menu theme..."));
        		main.getHolidayUtils().setHoliday(Holiday.NONE);
        		main.getHolidayUtils().setup();
        		sender.sendMessage(main.prxAPI.c("&7Done."));
        		});
        	}
        	else if (args[0].equalsIgnoreCase("saveplayerdata")) {
        		Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
                main.simulateAsyncAutoDataSave();
        		});
        	}
        	else if (args[0].equalsIgnoreCase("info")) {
        		if(!main.allowEasterEggs) return true;
        		sender.sendMessage(main.prxAPI.c("&6&lPRX&r &7&lINFO:"));
        		sender.sendMessage(main.prxAPI.c("&eRanks: &b" + main.rankStorage.getEntireData().size()));
        		sender.sendMessage(main.prxAPI.c("&ePrestiges: &b" + main.prestigeStorage.getPrestigeData().size()));
        		sender.sendMessage(main.prxAPI.c("&eRebirths: &b" + main.rebirthStorage.getRebirthData().size()));
        		sender.sendMessage(main.prxAPI.c("&eRegistered players: &b" + main.playerStorage.getPlayerData().size()));
        		sender.sendMessage(main.prxAPI.c("&econfig.yml &7defaultrank: &b" + main.prxAPI.getDefaultRank()));
        		sender.sendMessage(main.prxAPI.c("&econfig.yml &7lastrank: &b" + main.prxAPI.getLastRank()));
        		sender.sendMessage(main.prxAPI.c("&eranks.yml &7default/first rank: &b" + main.rankStorage.getRanksCollection(main.prxAPI.getDefaultPath()).get(0)));
        		sender.sendMessage(main.prxAPI.c("&eranks.yml &7last rank: &b" + main.rankStorage.getLastRank(main.prxAPI.getDefaultPath())));
        	} else if (args[0].equalsIgnoreCase("fix") || args[0].equalsIgnoreCase("scan")) {
        		Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
        			sender.sendMessage(main.prxAPI.c("&b&lScanning..."));
        			main.errorInspector.validateRanks(sender);
        			sender.sendMessage(main.prxAPI.c("&a&lScan Complete!"));
        		});
        	} else if (args[0].startsWith("blabla")) {
        		
        	} else if (args[0].equalsIgnoreCase("resetplayerdata")) {
        		
        			sender.sendMessage(main.prxAPI.c("&7Are you sure you want to reset all player data?"));
        			sender.sendMessage(main.prxAPI.c("&7What will happen:"));
        			sender.sendMessage(main.prxAPI.c("&7Their prestiges and rebirths will be removed, ranks will be reset to first"));
        			sender.sendMessage(main.prxAPI.c("&7All permissions they got from 'addpermission' will be removed from them"));
        			sender.sendMessage(main.prxAPI.c("&7Their balance will be reset to 0"));
        			sender.sendMessage(main.prxAPI.c("&7After that, the server will shutdown automatically."));
        			sender.sendMessage(main.prxAPI.c("&7Type &a/prx resetplayerdata confirm &7to proceed."));
                    confirmation.add(sender.getName());
        		
        	} else if (args[0].equalsIgnoreCase("placeholders")) {
        		if(is1_16) {
        			placeholders.forEach(placeholder -> {
        				if(main.allowEasterEggs) {
        				sender.sendMessage(main.globalStorage.translateHexColorCodes("&" + getRandomHexColors() + placeholder));
        				} else {
        					sender.sendMessage(main.prxAPI.c("&b" + placeholder));
        				}
        			});
        			return true;
        		}
        		placeholders.forEach(placeholder -> {
        			if(main.allowEasterEggs) {
        			sender.sendMessage(main.prxAPI.c("&" + getRandomColor() + "&" + getRandomBasicFormat() + placeholder));
        			} else {
        				sender.sendMessage(main.prxAPI.c("&b" + placeholder));
        			}
        		});
        	}
        	else if(args[0].equalsIgnoreCase("getprestiges")) {
        		sender.sendMessage("Your prestiges: " + String.valueOf(main.prxAPI.getPlayerPrestiges((Player)sender)));
        	}
        	else if(args[0].equalsIgnoreCase("dev")) {
        		Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
        			try {
        				sender.sendMessage(main.prxAPI.c("&2Converting rankdata..."));
                  FileConfiguration oldRanked = YamlConfiguration.loadConfiguration(new File(main.getDataFolder() + "/ranked.yml"));
                  for(String uuids : oldRanked.getConfigurationSection("Players").getKeys(false)) {
                	  String uuid = uuids;
                	  String rank = oldRanked.getString("Players." + uuid);
                	  main.getConfigManager().rankDataConfig.set("players." + uuid + ".rank", rank);
                	  main.getConfigManager().rankDataConfig.set("players." + uuid + ".path", main.prxAPI.getDefaultPath());
                  }
                  main.getConfigManager().saveRankDataConfig();
                  sender.sendMessage(main.prxAPI.c("&eRank data conversion success."));
        			} catch (Exception err) {
        				sender.sendMessage(main.prxAPI.c("&cRank data is already converted."));
        			}
        			try {
        				FileConfiguration oldPrestiged = YamlConfiguration.loadConfiguration(new File(main.getDataFolder() + "/prestiged.yml"));
        				for(String uuids : oldPrestiged.getConfigurationSection("Players").getKeys(false)) {
        					String prestige = oldPrestiged.getString("Players." + uuids);
        					main.getConfigManager().prestigeDataConfig.set("players." + uuids, prestige);
        				}
        				main.getConfigManager().savePrestigeDataConfig();
        				sender.sendMessage(main.prxAPI.c("&ePrestige data conversion success."));
        			} catch (Exception err) {
        				sender.sendMessage(main.prxAPI.c("&cPrestige data is already converted."));
        			}
        		});
        	}
        	else if(args[0].equalsIgnoreCase("terminate")) {
        		if(main.terminateMode) {
        		sender.sendMessage(main.prxAPI.c("&7Terminate mode &cdisabled&7."));
        		sender.sendMessage(main.prxAPI.c("&aThe data will be saved on server shutdown."));
        		main.terminateMode = false;
        		} else {
        			sender.sendMessage(main.prxAPI.c("&7Terminate mode &aenabled&7."));
        			sender.sendMessage(main.prxAPI.c("&7The data will not be saved on server shutdown."));
        			main.terminateMode = true;
        		}
        	}
        	else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
               	main.getHolidayUtils().getHelpMessage1().forEach(line -> {
            		sender.sendMessage(line.replace("%version%", ver));
            	});
        	} else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
        		Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
        		sender.sendMessage(main.prxAPI.c("&eReloading..."));
        		main.manager.reload();
        		sender.sendMessage(main.prxAPI.g("reload"));
        		});
        	} else if (args[0].equalsIgnoreCase("reload-data")) {
        		Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            		sender.sendMessage(main.prxAPI.c("&eReloading Data..."));
            		main.manager.reloadPlayerData();
            		sender.sendMessage(main.prxAPI.g("reload"));
        		});
        	} else if (args[0].equalsIgnoreCase("reenable") || args[0].equalsIgnoreCase("ree")) {
        		Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
        		sender.sendMessage(main.prxAPI.c("&eEnabling..."));
        		main.onEnable();
        		sender.sendMessage(main.prxAPI.c("&eReloading..."));
        		main.manager.reload();
        		sender.sendMessage(main.prxAPI.c("&6Plugin Successfully re-enabled."));
        		});
        	} else if (args[0].equalsIgnoreCase("cleartask")) {
        		main.prxAPI.taskedPlayers.clear();
        		main.prestigeAPI.getTaskedPlayers().clear();
        		main.rankupAPI.getTaskedPlayers().clear();
        		main.rankupMaxAPI.rankupMaxProcess.clear();
        		main.prestigeMax.getProcessingPlayers().clear();
        		sender.sendMessage(main.prxAPI.c("&c&k~&r &4&l&o&nTask limit cleared&r &c&k~"));
        	} else if (args[0].equalsIgnoreCase("errors")) {
        		if(main.errorInspector.getErrors().isEmpty()) {
        			if(is1_16) {
        			sender.sendMessage(main.globalStorage.translateHexColorCodes("&" + getRandomHexColors() + main.prxAPI.c("&l&oNo errors were found.")));
        			} else {
        			sender.sendMessage(main.prxAPI.c("&" + getRandomColor() + "&l&oNo errors were found."));
        			}
        			return true;
        		}
        		main.errorInspector.getErrors().forEach(error -> {
        			sender.sendMessage(main.prxAPI.c(error));
        		});
        	} else if (args[0].equalsIgnoreCase("save")) {
        		Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
        		sender.sendMessage(main.prxAPI.c("&eSaving data..."));
        		main.manager.save();
        		sender.sendMessage(main.prxAPI.g("save"));
        		});
        	} else if (args[0].equalsIgnoreCase("debug")) {
        		if(main.debug) {
        			main.debug = false;
        			sender.sendMessage(main.prxAPI.c("&6Debug &cdisabled&6."));
        		} else {
        			main.debug = true;
        			sender.sendMessage(main.prxAPI.c("&6Debug &aenabled&6."));
        		}
        	} else if (args[0].equalsIgnoreCase("createrank")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx createrank &4<name> <cost> &c[displayname]"));
        	} else if (args[0].equalsIgnoreCase("createprestige")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx createprestige &4<name> <cost> &c[displayname]"));
        	} else if (args[0].equalsIgnoreCase("createrebirth")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx createrebirth &4<name> <cost> &c[displayname]"));
        	} else if (args[0].equalsIgnoreCase("setrankcost")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx setrankcost &4<name> <cost>"));
        	} else if (args[0].equalsIgnoreCase("setprestigecost")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx setprestigecost &4<name> <cost>"));
        	} else if (args[0].equalsIgnoreCase("setrebirthcost")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx setrebirthcost &4<name> <cost>"));
        	} else if (args[0].equalsIgnoreCase("setrankdisplay") || args[0].equalsIgnoreCase("setrankdisplayname")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx setrankdisplay &4<name> <displayname>"));
        	} else if (args[0].equalsIgnoreCase("setprestigedisplay") || args[0].equalsIgnoreCase("setprestigedisplayname")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx setprestigedisplay &4<name> <displayname>"));
        	} else if (args[0].equalsIgnoreCase("setrebirthdisplay") || args[0].equalsIgnoreCase("setrebirthdisplayname")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx setrebirthdisplay &4<name> <displayname>"));
        	} else if (args[0].equalsIgnoreCase("setrankpath") || args[0].equalsIgnoreCase("setrankpathname")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx setrankpath &4<name> <path>"));
        	} else if (args[0].equalsIgnoreCase("delrank") || args[0].equalsIgnoreCase("deleterank")
        			|| args[0].equalsIgnoreCase("removerank") || args[0].equalsIgnoreCase("remrank")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx " + args[0] + " &4<name>"));
        	} else if (args[0].equalsIgnoreCase("delprestige") || args[0].equalsIgnoreCase("deleteprestige")
        			|| args[0].equalsIgnoreCase("removeprestige") || args[0].equalsIgnoreCase("remprestige")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx " + args[0] + " &4<name>"));
        	} else if (args[0].equalsIgnoreCase("delrebirth") || args[0].equalsIgnoreCase("deleterebirth")
        			|| args[0].equalsIgnoreCase("removerebirth") || args[0].equalsIgnoreCase("remrebirth")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx " + args[0] + " &4<name>"));
        	} else if (args[0].equalsIgnoreCase("setdefaultrank") || args[0].equalsIgnoreCase("setfirstrank")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx " + args[0] + " &4<name>"));
        	} else if (args[0].equalsIgnoreCase("setfirstprestige") || args[0].equalsIgnoreCase("setdefaultprestige")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx " + args[0] + " &4<name>"));
        	} else if (args[0].equalsIgnoreCase("setdefaultrebirth") || args[0].equalsIgnoreCase("setfirstrebirth")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx " + args[0] + " &4<name>"));
        	} else if (args[0].equalsIgnoreCase("setlastrank") || args[0].equalsIgnoreCase("setfinalrank")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx " + args[0] + " &4<name>"));
        	} else if (args[0].equalsIgnoreCase("setlastprestige") || args[0].equalsIgnoreCase("setfinalprestige")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx " + args[0] + " &4<name>"));
        	} else if (args[0].equalsIgnoreCase("setlastrebirth") || args[0].equalsIgnoreCase("setfinalrebirth")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx " + args[0] + " &4<name>"));
        	} else if (args[0].equalsIgnoreCase("setrank")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx setrank &4<player> <rankname>"));
        	} else if (args[0].equalsIgnoreCase("setprestige")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx setprestige &4<player> <prestigename>"));
        	} else if (args[0].equalsIgnoreCase("setrebirth")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx setrebirth &4<player> <rebirthname>"));
        	} else if (args[0].equalsIgnoreCase("resetrank")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx resetrank &4<player>"));
        	} else if (args[0].equalsIgnoreCase("resetprestige")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx resetprestige &4<player>"));
        	} else if (args[0].equalsIgnoreCase("resetrebirth")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx resetrebirth &4<player>"));
        	}
        } else if(args.length == 2) {
        	if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
        		if(args[1].equalsIgnoreCase("1")) {
        	       	main.getHolidayUtils().getHelpMessage1().forEach(line -> {
                		sender.sendMessage(line.replace("%version%", ver));
                	});
        		} else if (args[1].equalsIgnoreCase("2")) {
        	       	main.getHolidayUtils().getHelpMessage2().forEach(line -> {
                		sender.sendMessage(line.replace("%version%", ver));
                	});
        		} else if (args[1].equalsIgnoreCase("3")) {
        	       	main.getHolidayUtils().getHelpMessage3().forEach(line -> {
                		sender.sendMessage(line.replace("%version%", ver));
                	});
        		} else if (args[1].equalsIgnoreCase("member")) {
        			sender.sendMessage(main.prxAPI.c("&7- &9Prison Help &7-"));
        			sender.sendMessage(main.prxAPI.c("&6/rankup"));
        			sender.sendMessage(main.prxAPI.c("&6/rankupmax"));
        			sender.sendMessage(main.prxAPI.c("&6/ranks"));
        			sender.sendMessage(main.prxAPI.c("&6/prestige"));
        			sender.sendMessage(main.prxAPI.c("&6/prestiges"));
        			sender.sendMessage(main.prxAPI.c("&6/rebirth"));
        			sender.sendMessage(main.prxAPI.c("&6/rebirths"));
        			sender.sendMessage(main.prxAPI.c("&6/autorankup"));
        			sender.sendMessage(main.prxAPI.c("&6/autoprestige"));
        		}
        	} else if (args[0].equalsIgnoreCase("createrank")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx createrank <name> &4<cost> &c[displayname]"));
        	} else if (args[0].equalsIgnoreCase("createprestige")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx createprestige <name> &4<cost> &c[displayname]"));
        	} else if (args[0].equalsIgnoreCase("createrebirth")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx createrebirth <name> &4<cost> &c[displayname]"));
        	} else if (args[0].equalsIgnoreCase("setrank")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx setrank <player> &4<rank>"));
        	} else if (args[0].equalsIgnoreCase("setprestige")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx setprestige <player> &4<prestige>"));
        	} else if (args[0].equalsIgnoreCase("setrebirth")) {
        		sender.sendMessage(main.prxAPI.c("&c/&6prx setrebirth <player> &4<rebirth>"));
        	}
        	else if (args[0].equalsIgnoreCase("resetplayerdata")) {
        		if(args[1].equalsIgnoreCase("confirm")) {
        			if(!confirmation.contains(sender.getName())) {
        				sender.sendMessage(main.prxAPI.c("&7Please write &a/prx resetplayerdata &7first."));
        			} else {
        				AccessibleBukkitTask abt = new AccessibleBukkitTask();
        				abt.set(Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
        					sender.sendMessage(main.prxAPI.c("&7Starting the reset process..."));
        					sender.sendMessage(main.prxAPI.c("&7Creating a backup..."));
        					try {
								Files.copy(main.getConfigManager().rankDataFile, new File(main.getConfigManager().rankDataFile.getPath().replace("rankdata.yml", "rankdata_old.yml")));
								Files.copy(main.getConfigManager().prestigeDataFile, new File(main.getConfigManager().prestigeDataFile.getPath().replace("prestigedata.yml", "prestigedata_old.yml")));
								Files.copy(main.getConfigManager().rebirthDataFile, new File(main.getConfigManager().rebirthDataFile.getPath().replace("rebirthdata.yml", "rebirthdata_old.yml")));
								backupFile(main.getConfigManager().rankDataFile);
								backupFile(main.getConfigManager().prestigeDataFile);
								backupFile(main.getConfigManager().rebirthDataFile);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								sender.sendMessage(main.prxAPI.c("&cFailed to create a backup, please check the console."));
								sender.sendMessage(main.prxAPI.c("&7Process cancelled."));
								abt.cancel();
								e.printStackTrace();
							}
        					sender.sendMessage(main.prxAPI.c("&aBackup creation done."));
        					sender.sendMessage(main.prxAPI.c("&7Resetting online players data..."));
        					main.getPlayerStorage().getPlayerData().keySet().forEach(id -> {
        						UUID uuid = UUID.fromString(id);
        						main.prxAPI.setPlayerRankPath(uuid, RankPath.getRankPath(main.prxAPI.getDefaultRank(), main.prxAPI.getDefaultPath()));
        						main.prxAPI.deletePlayerPrestige(uuid);
        						main.prxAPI.deletePlayerRebirth(uuid);
        						Player p = Bukkit.getPlayer(uuid);
        						main.prxAPI.allRankAddPermissions.forEach(line -> {
        							main.perm.delPermission(p, line);
        						});
        						main.prxAPI.allPrestigeAddPermissions.forEach(line -> {
        							main.perm.delPermission(p, line);
        						});
        						main.prxAPI.allRebirthAddPermissions.forEach(line -> {
        							main.perm.delPermission(p, line);
        						});
        						main.prxAPI.getEconomy().withdrawPlayer(p, main.prxAPI.getPlayerMoney(p));
        						
        					});
        					sender.sendMessage(main.prxAPI.c("&aOnline players data reset."));
        					sender.sendMessage(main.prxAPI.c("&7Resetting offline players data, this might take a while..."));
        					main.abprogress.clear(true);
        					main.getConfigManager().rankDataConfig.getConfigurationSection("players").getKeys(false).forEach(id -> {
        						UUID uuid = UUID.fromString(id);
        						OfflinePlayer p = Bukkit.getOfflinePlayer(id);
        						main.prxAPI.allRankAddPermissions.forEach(line -> {
        							main.perm.delPermissionOffline(p, line);
        						});
        						main.prxAPI.allPrestigeAddPermissions.forEach(line -> {
        							main.perm.delPermissionOffline(p, line);
        						});
        						main.prxAPI.allRebirthAddPermissions.forEach(line -> {
        							main.perm.delPermissionOffline(p, line);
        						});
        						main.prxAPI.getEconomy().withdrawPlayer(p, (double)main.prxAPI.getPlayerMoney(p));
        						if(sender instanceof Player) {
        						main.getActionbar().sendActionBar((Player)sender, p.getName() + main.prxAPI.c(" &adata reset"));
        						} else {
        							sender.sendMessage(main.prxAPI.c(p.getName() + " &adata reset."));
        						}
        					});
        					PrisonRanksX.getInstance().getConfigManager().rankDataFile.delete();
        					PrisonRanksX.getInstance().getConfigManager().prestigeDataFile.delete();
        					PrisonRanksX.getInstance().getConfigManager().rebirthDataFile.delete();
        					sender.sendMessage(main.prxAPI.c("&aReset successful."));
        					sender.sendMessage(main.prxAPI.c("&7&oRestarting the server..."));
        					Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> {
        						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
        					}, 60);
        				}));
        			}
        		}
        	}
        	else if(args[0].equalsIgnoreCase("delrank")) {
        		String matchedRank = main.manager.matchRank(args[1]);
        		main.manager.delRank(matchedRank);
        		sender.sendMessage(main.prxAPI.g("delrank").replace("%args1%", matchedRank));
        	} else if (args[0].equalsIgnoreCase("delprestige")) {
        		String matchedPrestige = main.manager.matchPrestige(args[1]);
        		main.manager.delPrestige(matchedPrestige);
        		if(main.prxAPI.prestigeExists(matchedPrestige)) {
        		sender.sendMessage(main.prxAPI.g("delprestige").replace("%args1%", matchedPrestige));
        		} else {
        			Player p = Bukkit.getPlayer(args[1]);
        			if(p == null) {
        				sender.sendMessage(main.prxAPI.g("prestige-notfound").replace("%prestige%", matchedPrestige));
        				return true;
        			}
            		XUser user = XUser.getXUser(p);
            		PrestigeUpdateEvent e = new PrestigeUpdateEvent(p, PrestigeUpdateCause.DELPRESTIGE);
            		Bukkit.getPluginManager().callEvent(e);
            		if(e.isCancelled()) {
            			return true;
            		}
            		main.manager.delPlayerPrestige(user);
            		if(main.globalStorage.getStringListMap().get("PrestigeOptions.prestige-delete-cmds").contains("[rankpermissions]")) {
            		    Set<String> perms = main.prxAPI.allRankAddPermissions;
            		    for(String perm : perms) {
            			    main.perm.delPermission(p, perm);
            		    }
            		} if (main.globalStorage.getStringListMap().get("PrestigeOptions.prestige-delete-cmds").contains("[prestigepermissions]")) {
            			Set<String> perms2 = main.prxAPI.allPrestigeAddPermissions;
            			for(String perm : perms2) {
            				main.perm.delPermission(p, perm);
            			}
            		}
            		for(String cmd : main.globalStorage.getStringListMap().get("PrestigeOptions.prestige-delete-cmds")) {
            			if(!cmd.endsWith("permissions] remove")) {
            				main.executeCommand(p, cmd);
            			}
            		}
            		sender.sendMessage(main.prxAPI.g("delplayerprestige").replace("%player%", p.getName()));
        		}
        	} else if (args[0].equalsIgnoreCase("delrebirth")) {
        		String matchedRebirth = main.manager.matchRebirth(args[1]);
        		main.manager.delRebirth(matchedRebirth);
        		if(main.prxAPI.rebirthExists(matchedRebirth)) {
        		sender.sendMessage(main.prxAPI.g("delrebirth").replace("%args1%", matchedRebirth));
        		} else {
        			Player p = Bukkit.getPlayer(args[1]);
        			if(p == null) {
        				sender.sendMessage(main.prxAPI.g("rebirth-notfound").replace("%rebirth%", matchedRebirth));
        				return true;
        			}
        			XUser user = XUser.getXUser(p);
            		RebirthUpdateEvent e = new RebirthUpdateEvent(p, RebirthUpdateCause.DELREBIRTH);
            		Bukkit.getPluginManager().callEvent(e);
            		if(e.isCancelled()) {
            			return true;
            		}
            		main.manager.delPlayerRebirth(user);
            		if(main.globalStorage.getStringListMap().get("RebirthOptions.rebirth-delete-cmds").contains("[rankpermissions]")) {
            		    Set<String> perms = main.prxAPI.allRankAddPermissions;
            		    for(String perm : perms) {
            			    main.perm.delPermission(p, perm);
            		    }
            		} if (main.globalStorage.getStringListMap().get("RebirthOptions.rebirth-delete-cmds").contains("[prestigepermissions]")) {
            			Set<String> perms2 = main.prxAPI.allPrestigeAddPermissions;
            			for(String perm : perms2) {
            				main.perm.delPermission(p, perm);
            			}
            		}
            		if(main.globalStorage.getStringListMap().get("RebirthOptions.rebirth-delete-cmds").contains("[rebirthpermissions]")) {
            			Set<String> perms = main.prxAPI.allRebirthAddPermissions;
            			for(String perm : perms) {
            				main.perm.delPermission(p, perm);
            			}
            		}
            		for(String cmd : main.globalStorage.getStringListMap().get("RebirthOptions.rebirth-delete-cmds")) {
            			if(!cmd.endsWith("permissions] remove")) {
            				main.executeCommand(p, cmd);
            			}
            		}
            		sender.sendMessage(main.prxAPI.g("delplayerrebirth").replace("%player%", p.getName()));
            	
        		}
        	} else  if (args[0].equalsIgnoreCase("resetrank")) {
        		String parsedPlayerName = args[1];
        		if(Bukkit.getPlayer(parsedPlayerName) == null) {
        			sender.sendMessage(main.prxAPI.g("playernotfound").replace("%player%", args[1]));
        			return true;
        		}
        		Player p = Bukkit.getPlayer(parsedPlayerName);
        		RankUpdateEvent e = new RankUpdateEvent(p, RankUpdateCause.RANKSET, main.prxAPI.getDefaultRank());
        		Bukkit.getServer().getPluginManager().callEvent(e);
        		if(e.isCancelled()) {
        			return true;
        		}
        		main.prxAPI.resetPlayerRank(p);
        		sender.sendMessage(main.prxAPI.g("resetrank").replace("%target%", p.getName())
        				.replace("%firstrank%", main.prxAPI.getDefaultRank()));
        		if(!main.getGlobalStorage().getStringListMap().containsKey("RankOptions.rank-reset-cmds")) {
        		  return true;
        		}
        		if(main.globalStorage.getStringListMap().get("RankOptions.rank-reset-cmds").contains("[rankpermissions]")) {
        		    Set<String> perms = main.prxAPI.allRankAddPermissions;
        		    for(String perm : perms) {
        			    main.perm.delPermission(p, perm);
        		    }
        		}
        		for(String command : main.globalStorage.getStringListMap().get("RankOptions.rank-reset-cmds")) {
        			if(!command.startsWith("[rankpermissions") && !command.startsWith("[prestigeperm") && !command.startsWith("[rebirthp")) {
        				main.executeCommand(p, command);
        			}
        		}
        		
        	} else if (args[0].equalsIgnoreCase("resetprestige")) {
        		String parsedPlayerName = args[1];
        		if(Bukkit.getPlayer(parsedPlayerName) == null) {
        			sender.sendMessage(main.prxAPI.g("playernotfound").replace("%player%", args[1]));
        			return true;
        		}
        		Player p = Bukkit.getPlayer(parsedPlayerName);
        		PrestigeUpdateEvent e = new PrestigeUpdateEvent(p, PrestigeUpdateCause.SETPRESTIGE);
        		Bukkit.getServer().getPluginManager().callEvent(e);
        		if(e.isCancelled()) {
        			return true;
        		}
        		main.prxAPI.setPlayerPrestige(p, main.prxAPI.getFirstPrestige());
        		sender.sendMessage(main.prxAPI.g("resetprestige").replace("%target%", p.getName())
        				.replace("%firstprestige%", main.globalStorage.getStringData("firstprestige")));
        		if(main.globalStorage.getStringListMap().get("PrestigeOptions.prestige-reset-cmds").contains("[rankpermissions]")) {
        		    Set<String> perms = main.prxAPI.allRankAddPermissions;
        		    for(String perm : perms) {
        			    main.perm.delPermission(p, perm);
        		    }
        		}
        		if(main.globalStorage.getStringListMap().get("PrestigeOptions.prestige-reset-cmds").contains("[prestigepermissions$1]")) {
        			Set<String> perms = main.prxAPI.allPrestigeAddPermissions;
        			main.prestigeStorage.getAddPermissionList(main.prxAPI.getFirstPrestige()).forEach(fperm -> {
        				perms.remove(fperm);
        			});
        			for(String perm : perms) {
        				main.perm.delPermission(p, perm);
        			}
        		}
        		for(String cmd : main.globalStorage.getStringListMap().get("PrestigeOptions.prestige-reset-cmds")) {
        			if(!cmd.endsWith("permissions] remove")) {
        				main.executeCommand(p, cmd);
        			}
        		}
        	} else if (args[0].equalsIgnoreCase("resetrebirth")) {
        		String parsedPlayerName = args[1];
        		if(Bukkit.getPlayer(parsedPlayerName) == null) {
        			sender.sendMessage(main.prxAPI.g("playernotfound").replace("%player%", args[1]));
        			return true;
        		}
        		Player p = Bukkit.getPlayer(parsedPlayerName);
        		RebirthUpdateEvent e = new RebirthUpdateEvent(p, RebirthUpdateCause.SETREBIRTH);
        		Bukkit.getServer().getPluginManager().callEvent(e);
        		if(e.isCancelled()) {
        			return true;
        		}
        		main.prxAPI.setPlayerRebirth(p, main.prxAPI.getFirstRebirth());
        		sender.sendMessage(main.prxAPI.g("resetrebirth").replace("%target%", p.getName())
        				.replace("%firstrebirth%", main.globalStorage.getStringData("firstrebirth")));
        		if(main.globalStorage.getStringListMap().get("RebirthOptions.rebirth-reset-cmds").contains("[rankpermissions]")) {
        		    Set<String> perms = main.prxAPI.allRankAddPermissions;
        		    for(String perm : perms) {
        			    main.perm.delPermission(p, perm);
        		    }
        		}
        		if(main.globalStorage.getStringListMap().get("RebirthOptions.rebirth-reset-cmds").contains("[prestigepermissions]")) {
        			Set<String> perms = main.prxAPI.allPrestigeAddPermissions;
        			for(String perm : perms) {
        				main.perm.delPermission(p, perm);
        			}
        		}
        		if(main.globalStorage.getStringListMap().get("RebirthOptions.rebirth-reset-cmds").contains("[rebirthpermissions$1]")) {
        			Set<String> perms = main.prxAPI.allRebirthAddPermissions;
        			main.rebirthStorage.getAddPermissionList(main.prxAPI.getFirstRebirth()).forEach(fperm -> {
        				perms.remove(fperm);
        			});
        			for(String perm : perms) {
        				main.perm.delPermission(p, perm);
        			}
        		}
        		for(String cmd : main.globalStorage.getStringListMap().get("RebirthOptions.rebirth-reset-cmds")) {
        			if(!cmd.endsWith("permissions] remove")) {
        				main.executeCommand(p, cmd);
        			}
        		}
        		
        	} else if (args[0].equalsIgnoreCase("setdefaultrank") || args[0].equalsIgnoreCase("setfirstrank")) {
        		String rankn = main.manager.matchRank(args[1]);
        		main.manager.setDefaultRank(rankn, true);
        		sender.sendMessage(main.prxAPI.g("setdefaultrank").replace("%args1%", rankn));
        	} else if (args[0].equalsIgnoreCase("setlastrank")) {
        		String rankn = main.manager.matchRank(args[1]);
        		main.manager.setLastRank(rankn, true);
        		sender.sendMessage(main.prxAPI.g("setlastrank").replace("%args1%", rankn));
        	} else if (args[0].equalsIgnoreCase("setfirstprestige")) {
        		String prestigen = main.manager.matchPrestige(args[1]);
        	    main.manager.setFirstPrestige(prestigen, true);
                sender.sendMessage(main.prxAPI.g("setfirstprestige").replace("%args1%", prestigen));
        	} else if (args[0].equalsIgnoreCase("setlastprestige")) {
        		String prestigen = main.manager.matchPrestige(args[1]);
        		main.manager.setLastPrestige(prestigen, true);
        		sender.sendMessage(main.prxAPI.g("setlastprestige").replace("%args1%", prestigen));
        	} else if (args[0].equalsIgnoreCase("setfirstrebirth")) {
        		String rebirthn = main.manager.matchRebirth(args[1]);
        		main.manager.setFirstRebirth(rebirthn, true);
        		sender.sendMessage(main.prxAPI.g("setfirstrebirth").replace("%args1%", rebirthn));
        	} else if (args[0].equalsIgnoreCase("setlastrebirth")) {
        		String rebirthn = main.manager.matchRebirth(args[1]);
        		main.manager.setLastRebirth(rebirthn, true);
        		sender.sendMessage(main.prxAPI.g("setlastrebirth").replace("%args1%", rebirthn));
        	} else if (args[0].equalsIgnoreCase("delplayerprestige")) {
        		if(Bukkit.getPlayer(args[1]) == null) {
        			sender.sendMessage(main.prxAPI.g("playernotfound").replace("%player%", args[1]));
        			return true;
        		}
        		Player p = Bukkit.getPlayer(args[1]);
        		XUser user = XUser.getXUser(p);
        		PrestigeUpdateEvent e = new PrestigeUpdateEvent(p, PrestigeUpdateCause.DELPRESTIGE);
        		Bukkit.getPluginManager().callEvent(e);
        		if(e.isCancelled()) {
        			return true;
        		}
        		main.manager.delPlayerPrestige(user);
        		if(main.globalStorage.getStringListMap().get("PrestigeOptions.prestige-delete-cmds").contains("[rankpermissions]")) {
        		    Set<String> perms = main.prxAPI.allRankAddPermissions;
        		    for(String perm : perms) {
        			    main.perm.delPermission(p, perm);
        		    }
        		} if (main.globalStorage.getStringListMap().get("PrestigeOptions.prestige-delete-cmds").contains("[prestigepermissions]")) {
        			Set<String> perms2 = main.prxAPI.allPrestigeAddPermissions;
        			for(String perm : perms2) {
        				main.perm.delPermission(p, perm);
        			}
        		}
        		for(String cmd : main.globalStorage.getStringListMap().get("PrestigeOptions.prestige-delete-cmds")) {
        			if(!cmd.endsWith("permissions] remove")) {
        				main.executeCommand(p, cmd);
        			}
        		}
        		sender.sendMessage(main.prxAPI.g("delplayerprestige").replace("%player%", p.getName()));
        		
        	} else if (args[0].equalsIgnoreCase("delplayerrebirth")) {
        		if(Bukkit.getPlayer(args[1]) == null) {
        			sender.sendMessage(main.prxAPI.g("playernotfound").replace("%player%", args[1]));
        			return true;
        		}
        		Player p = Bukkit.getPlayer(args[1]);
        		XUser user = XUser.getXUser(p);
        		RebirthUpdateEvent e = new RebirthUpdateEvent(p, RebirthUpdateCause.DELREBIRTH);
        		Bukkit.getPluginManager().callEvent(e);
        		if(e.isCancelled()) {
        			return true;
        		}
        		main.manager.delPlayerRebirth(user);
        		if(main.globalStorage.getStringListMap().get("RebirthOptions.rebirth-delete-cmds").contains("[rankpermissions]")) {
        		    Set<String> perms = main.prxAPI.allRankAddPermissions;
        		    for(String perm : perms) {
        			    main.perm.delPermission(p, perm);
        		    }
        		} if (main.globalStorage.getStringListMap().get("RebirthOptions.rebirth-delete-cmds").contains("[prestigepermissions]")) {
        			Set<String> perms2 = main.prxAPI.allPrestigeAddPermissions;
        			for(String perm : perms2) {
        				main.perm.delPermission(p, perm);
        			}
        		}
        		if(main.globalStorage.getStringListMap().get("RebirthOptions.rebirth-delete-cmds").contains("[rebirthpermissions]")) {
        			Set<String> perms = main.prxAPI.allRebirthAddPermissions;
        			for(String perm : perms) {
        				main.perm.delPermission(p, perm);
        			}
        		}
        		for(String cmd : main.globalStorage.getStringListMap().get("RebirthOptions.rebirth-delete-cmds")) {
        			if(!cmd.endsWith("permissions] remove")) {
        				main.executeCommand(p, cmd);
        			}
        		}
        		sender.sendMessage(main.prxAPI.g("delplayerrebirth").replace("%player%", p.getName()));
        	}
        } else if(args.length == 3) {
        	if(args[0].equalsIgnoreCase("createrank")) {
        		double costy;
        		if(!main.prxAPI.numberAPI.isNumber(args[2])) {
        			costy = main.prxAPI.numberAPI.parseBalance(args[2]);
        		} else {
        			costy = Double.valueOf(args[2]);
        		}
        		main.manager.createRank(args[1], Double.valueOf(costy));
        		main.getConfigManager().saveRanksConfig();
        		sender.sendMessage(main.prxAPI.g("createrank").replace("%createdrank%", args[1])
        				.replace("%rankcost%", args[2]));
        	} else if (args[0].equalsIgnoreCase("createprestige")) {
        		double costy;
        		if(!main.prxAPI.numberAPI.isNumber(args[2])) {
        			costy = main.prxAPI.numberAPI.parseBalance(args[2]);
        		} else {
        			costy = Double.valueOf(args[2]);
        		}
        		main.manager.createPrestige(args[1], Double.valueOf(costy));
        		main.getConfigManager().savePrestigesConfig();
        		sender.sendMessage(main.prxAPI.g("createprestige").replace("%createdprestige%", args[1])
        				.replace("%prestigecost%", args[2]));
        	} else if (args[0].equalsIgnoreCase("createrebirth")) {
        		double costy;
        		if(!main.prxAPI.numberAPI.isNumber(args[2])) {
        			costy = main.prxAPI.numberAPI.parseBalance(args[2]);
        		} else {
        			costy = Double.valueOf(args[2]);
        		}
        		main.manager.createRebirth(args[1], Double.valueOf(costy));
        		main.getConfigManager().saveRebirthsConfig();
        		sender.sendMessage(main.prxAPI.g("createrebirth").replace("%createdrebirth%", args[1])
        		        .replace("%rebirthcost%", args[2]));
        	} else if (args[0].equalsIgnoreCase("setrankcost")) {
          		double costy;
        		if(!main.prxAPI.numberAPI.isNumber(args[2])) {
        			costy = main.prxAPI.numberAPI.parseBalance(args[2]);
        		} else {
        			costy = Double.valueOf(args[2]);
        		}
        		String matchedRank = main.manager.matchRank(args[1]);
        		main.manager.setRankCost(matchedRank, costy);
        		main.getConfigManager().saveRanksConfig();
        		sender.sendMessage(main.prxAPI.g("setrankcost").replace("%args1%", matchedRank)
        				.replace("%args2%", args[2]));
        	} else if (args[0].equalsIgnoreCase("setprestigecost")) {
        		double costy;
        		if(!main.prxAPI.numberAPI.isNumber(args[2])) {
        			costy = main.prxAPI.numberAPI.parseBalance(args[2]);
        		} else {
        			costy = Double.valueOf(args[2]);
        		}
        		String matchedPrestige = main.manager.matchPrestige(args[1]);
        		main.manager.setPrestigeCost(matchedPrestige, costy);
        		main.getConfigManager().savePrestigesConfig();
        		sender.sendMessage(main.prxAPI.g("setprestigecost").replace("%prestige%", matchedPrestige)
        				.replace("%prestigecost%", args[2]));
        	} else if (args[0].equalsIgnoreCase("setrebirthcost")) {
        		double costy;
        		if(!main.prxAPI.numberAPI.isNumber(args[2])) {
        			costy = main.prxAPI.numberAPI.parseBalance(args[2]);
        		} else {
        			costy = Double.valueOf(args[2]);
        		}
        		String matchedRebirth = main.manager.matchRebirth(args[1]);
        		main.manager.setRebirthCost(matchedRebirth, costy);
        		main.getConfigManager().saveRebirthsConfig();
        		sender.sendMessage(main.prxAPI.g("setrebirthcost").replace("%rebirth%", matchedRebirth)
        				.replace("%rebirthcost%", args[2]));
        	} else if (args[0].equalsIgnoreCase("setrankdisplay")) {
        		String matchedRank = main.manager.matchRank(args[1]);
        		String newDisplayName = main.getArgs(args, 2);
        		main.manager.setRankDisplayName(matchedRank, newDisplayName);
        		main.getConfigManager().saveRanksConfig();
        		sender.sendMessage(main.prxAPI.g("setrankdisplay").replace("%args1%", matchedRank)
        				.replace("%args2%", newDisplayName + " §f=> " + main.getString(main.getGlobalStorage().translateHexColorCodes(newDisplayName))));
        	} else if (args[0].equalsIgnoreCase("setprestigedisplay")) {
        		String matchedPrestige = main.manager.matchPrestige(args[1]);
        		String newDisplayName = main.getArgs(args, 2);
        		main.manager.setPrestigeDisplayName(matchedPrestige, newDisplayName);
        		main.getConfigManager().savePrestigesConfig();
        		sender.sendMessage(main.prxAPI.g("setprestigedisplay").replace("%prestige%", matchedPrestige)
        				.replace("%changeddisplay%", newDisplayName + " §f=> " + main.getString(main.getGlobalStorage().translateHexColorCodes(newDisplayName))));
        	} else if (args[0].equalsIgnoreCase("setrebirthdisplay")) {
        		String matchedRebirth = main.manager.matchRebirth(args[1]);
        		String newDisplayName = main.getArgs(args, 2);
        		main.manager.setRebirthDisplayName(matchedRebirth, newDisplayName);
        		main.getConfigManager().savePrestigesConfig();
        		sender.sendMessage(main.prxAPI.g("setrebirthdisplay").replace("%rebirth%", matchedRebirth)
        				.replace("%changeddisplay%", newDisplayName + " §f=> " + main.getString(main.getGlobalStorage().translateHexColorCodes(newDisplayName))));
        	} else if (args[0].equalsIgnoreCase("setrank")) {
        		if(Bukkit.getPlayer(args[1]) == null) {
            			sender.sendMessage(main.prxAPI.g("playernotfound").replace("%player%", args[1]));
        			return true;
        		}
        		Player p = Bukkit.getPlayer(args[1]);
        		String newRank = main.manager.matchRank(args[2]);
        		if(!main.prxAPI.rankExists(newRank)) {
        			sender.sendMessage(main.prxAPI.g("rank-notfound").replace("%rank%", newRank));
        			return true;
        		}
        		try {
        		RankUpdateEvent e = new RankUpdateEvent(p, RankUpdateCause.RANKSET, newRank);
        		Bukkit.getPluginManager().callEvent(e);
        		if(e.isCancelled()) {
        			return true;
        		}
        		main.prxAPI.setPlayerRank(p, newRank);
        		RankDataHandler rdh = main.prxAPI.getRank(RankPath.getRankPath(newRank, main.prxAPI.getDefaultPath()));
                main.executeCommands(p, rdh.getRankCommands()); 
                if(rdh.getCurrentAddPermissionList() != null) {rdh.getCurrentAddPermissionList().forEach(line -> {
                	main.perm.addPermission(p, line);
                });}
                if(rdh.getCurrentDelPermissionList() != null) {rdh.getCurrentDelPermissionList().forEach(line -> {
                	main.perm.delPermission(p, line);
                });}
        		} catch (Exception exception) {
        			exception.printStackTrace();
        			main.getLogger().warning(p.getName() + " data went wrong. possible reasons:");
        			main.getLogger().warning("some ranks on ranks.yml has an invalid nextrank, (not matching case, rank doesn't exist)");
        			main.getLogger().warning("player has old/wrong rankdata in rankdata.yml therefore rankdata.yml must be deleted while the server is offline to prevent data loss");
        			main.getLogger().warning("rankup-vault-groups option on config.yml is enabled while you don't use it (not having groups in the permission plugin that match prisonranksx ranks on the main track)");
        		}
        		sender.sendMessage(main.prxAPI.g("setrank").replace("%target%", p.getName())
        				.replace("%settedrank%", newRank));
                
        	} else if (args[0].equalsIgnoreCase("setpath")) {
        		if(Bukkit.getPlayer(args[1]) == null) {
        			sender.sendMessage(main.prxAPI.g("playernotfound").replace("%player%", args[1]));
    			    return true;
    		    }
    		    Player p = Bukkit.getPlayer(args[1]);
    		    String newPath = main.manager.matchPath(args[2]);
    		    if(!main.prxAPI.pathExists(newPath)) {
    		    	sender.sendMessage(main.prxAPI.g("path-notfound").replace("%path%", newPath));
    		    	return true;
    		    }
    		    main.prxAPI.setPlayerPath(p, newPath);
    		    if(!main.prxAPI.rankExists(main.prxAPI.getPlayerRank(p), newPath)) {
    		    	sender.sendMessage(main.prxAPI.c("&cUnable to find player's rank within the new path."));
    		    	return true;
    		    }
        	} else if (args[0].equalsIgnoreCase("setprestige")) {
        		if(Bukkit.getPlayer(args[1]) == null) {
        			  sender.sendMessage(main.prxAPI.g("playernotfound").replace("%player%", args[1]));
        			return true;
        		}
        		Player p = Bukkit.getPlayer(args[1]);
        		String newPrestige = main.manager.matchPrestige(args[2]);
        		if(main.isInfinitePrestige) {
        			if(!main.prxAPI.prestigeInfiniteExists(newPrestige)) {
        				sender.sendMessage(main.prxAPI.g("prestige-notfound").replace("%prestige%", newPrestige));
        				return true;
        			}
        			PrestigeUpdateEvent e = new PrestigeUpdateEvent(p, PrestigeUpdateCause.SETPRESTIGE);
            		Bukkit.getPluginManager().callEvent(e);
            		if(e.isCancelled()) {
            			return true;
            		}
            		if(newPrestige.equals("0")) {
            			main.manager.delPlayerPrestige(XUser.getXUser(p));
            			sender.sendMessage(main.prxAPI.g("delplayerprestige").replace("%player%", p.getName()));
            			return true;
            		}
        			main.prxAPI.setPlayerPrestige(p, newPrestige);
        			sender.sendMessage(main.prxAPI.g("setprestige").replace("%target%", p.getName())
            				.replace("%settedprestige%", newPrestige));
        			return true;
        		}
        		if(!main.prxAPI.prestigeExists(newPrestige)) {
        			if(newPrestige.equalsIgnoreCase("P0") || newPrestige.equalsIgnoreCase("0")) {
                		XUser user = XUser.getXUser(p);
                		PrestigeUpdateEvent e = new PrestigeUpdateEvent(p, PrestigeUpdateCause.DELPRESTIGE);
                		Bukkit.getPluginManager().callEvent(e);
                		if(e.isCancelled()) {
                			return true;
                		}
                		main.manager.delPlayerPrestige(user);
                		if(main.globalStorage.getStringListMap().get("PrestigeOptions.prestige-delete-cmds").contains("[rankpermissions]")) {
                		    Set<String> perms = main.prxAPI.allRankAddPermissions;
                		    for(String perm : perms) {
                			    main.perm.delPermission(p, perm);
                		    }
                		} if (main.globalStorage.getStringListMap().get("PrestigeOptions.prestige-delete-cmds").contains("[prestigepermissions]")) {
                			Set<String> perms2 = main.prxAPI.allPrestigeAddPermissions;
                			for(String perm : perms2) {
                				main.perm.delPermission(p, perm);
                			}
                		}
                		for(String cmd : main.globalStorage.getStringListMap().get("PrestigeOptions.prestige-delete-cmds")) {
                			if(!cmd.endsWith("permissions] remove")) {
                				main.executeCommand(p, cmd);
                			}
                		}
                		sender.sendMessage(main.prxAPI.g("delplayerprestige").replace("%player%", p.getName()));
        				return true;
        			} else if (main.prxAPI.numberAPI.isNumber(args[2])) {
        				String prestige = main.prxAPI.getPrestigeNameFromNumber(Integer.valueOf(args[2]));
        				if(prestige != null) {
        		       		PrestigeUpdateEvent e = new PrestigeUpdateEvent(p, PrestigeUpdateCause.SETPRESTIGE);
        	        		Bukkit.getPluginManager().callEvent(e);
        	        		if(e.isCancelled()) {
        	        			return true;
        	        		}
        	        		main.prxAPI.setPlayerPrestige(p, prestige);
        	        		IPrestigeDataHandler pdh = main.prxAPI.getPrestige(prestige);
        	        		if(pdh.getPrestigeCommands() != null) {
        	        		main.executeCommands(p, pdh.getPrestigeCommands());
        	        		}
        	        		if(pdh.getAddPermissionList() != null) {
        	        		pdh.getAddPermissionList().forEach(line -> {
        	        			main.perm.addPermission(p, line);
        	        		});
        	        		}
        	        		if(pdh.getDelPermissionList() != null) {
        	        			pdh.getDelPermissionList().forEach(line -> {
        	        				main.perm.delPermission(p, line);
        	        			});
        	        		}
        	        		sender.sendMessage(main.prxAPI.g("setprestige").replace("%target%", p.getName())
        	        				.replace("%settedprestige%", prestige));
        					return true;
        				}
        			}
        			sender.sendMessage(main.prxAPI.g("prestige-notfound").replace("%prestige%", newPrestige));
        			return true;
        		}
        		PrestigeUpdateEvent e = new PrestigeUpdateEvent(p, PrestigeUpdateCause.SETPRESTIGE);
        		Bukkit.getPluginManager().callEvent(e);
        		if(e.isCancelled()) {
        			return true;
        		}
        		main.prxAPI.setPlayerPrestige(p, newPrestige);
        		IPrestigeDataHandler pdh = main.prxAPI.getPrestige(newPrestige);
        		if(pdh.getPrestigeCommands() != null)
        		main.executeCommands(p, pdh.getPrestigeCommands());
        		if(pdh.getAddPermissionList() != null) {
        		pdh.getAddPermissionList().forEach(line -> {
        			main.perm.addPermission(p, line);
        		});
        		}
        		if(pdh.getDelPermissionList() != null) {
        			pdh.getDelPermissionList().forEach(line -> {
        				main.perm.delPermission(p, line);
        			});
        		}
        		sender.sendMessage(main.prxAPI.g("setprestige").replace("%target%", p.getName())
        				.replace("%settedprestige%", newPrestige));
        		
        	} else if (args[0].equalsIgnoreCase("setrebirth")) {
        		if(Bukkit.getPlayer(args[1]) == null) {
      			  sender.sendMessage(main.prxAPI.g("playernotfound").replace("%player%", args[1]));
      			return true;
      		}
      		Player p = Bukkit.getPlayer(args[1]);
      		String newRebirth = main.manager.matchRebirth(args[2]);
    		if(!main.prxAPI.rebirthExists(newRebirth)) {
    			sender.sendMessage(main.prxAPI.g("rebirth-notfound").replace("%rebirth%", newRebirth));
    			return true;
    		}
    		RebirthUpdateEvent e = new RebirthUpdateEvent(p, RebirthUpdateCause.SETREBIRTH);
    		Bukkit.getPluginManager().callEvent(e);
    		if(e.isCancelled()) {
    			return true;
    		}
      		main.prxAPI.setPlayerRebirth(p, newRebirth);
      		RebirthDataHandler rdh = main.prxAPI.getRebirth(newRebirth);
      		if(rdh.getRebirthCommands() != null)
      		main.executeCommands(p, rdh.getRebirthCommands());
      		if(rdh.getAddPermissionList() != null) {rdh.getAddPermissionList().forEach(line -> {
      			main.perm.addPermission(p, line);
      		});}
      		if(rdh.getDelPermissionList() != null) {rdh.getDelPermissionList().forEach(line -> {
      			main.perm.delPermission(p, line);
      		});}
      		sender.sendMessage(main.prxAPI.g("setrebirth").replace("%target%", p.getName())
      				.replace("%settedrebirth%", newRebirth));
      		
        	} else if (args[0].equalsIgnoreCase("setrankpath")) {
            	String rank = main.manager.matchRank(args[1]);
            	String newPath = main.manager.matchPath(args[2]);
            	main.manager.setRankPathName(rank, newPath);
            	sender.sendMessage(main.prxAPI.g("setrankpath").replace("%args1%", rank)
            			.replace("%args2%", newPath));
            }
        } else if (args.length >= 4) {
        	if (args[0].equalsIgnoreCase("setrank")) {
        		if(Bukkit.getPlayer(args[1]) == null) {
            			sender.sendMessage(main.prxAPI.g("playernotfound").replace("%player%", args[1]));
        			return true;
        		}
        		Player p = Bukkit.getPlayer(args[1]);
        		String newRank = main.manager.matchRank(args[2]);
        		if(!main.prxAPI.rankExists(newRank, true)) {
        			sender.sendMessage(main.prxAPI.g("rank-notfound").replace("%rank%", newRank));
        			return true;
        		}
        		String newPath = main.manager.matchPath(args[3]);
        		RankPath rp = new RankPath(newRank, newPath);
        		if(!main.prxAPI.rankPathExists(rp)) {
        			sender.sendMessage(main.prxAPI.g("path-notfound").replace("%path%", newPath));
        			return true;
        		}
        		RankUpdateEvent e = new RankUpdateEvent(p, RankUpdateCause.RANKSET_BYCONVERT);
        		Bukkit.getPluginManager().callEvent(e);
        		if(e.isCancelled()) {
        			return true;
        		}
        		main.prxAPI.setPlayerRankPath(p, rp);
        		sender.sendMessage(main.prxAPI.g("setrank").replace("%target%", p.getName())
        				.replace("%settedrank%", newRank));
        		sender.sendMessage(main.prxAPI.c("&7Path: " + newPath));
        	}
        	if(args[0].equalsIgnoreCase("createrank")) {
        		double costy;
        		if(!main.prxAPI.numberAPI.isNumber(args[2])) {
        			costy = main.prxAPI.numberAPI.parseBalance(args[2]);
        		} else {
        			costy = Double.valueOf(args[2]);
        		}
        		String multiArgs = main.getArgs(args, 3);
        		String path = "";
        		if(multiArgs.contains("-path:")) {
        		for(String argument : multiArgs.split(" ")) {
        			if(argument.startsWith("-path:")) {
        				path = argument.substring(6);
        			}
        		}
        		path = main.manager.matchPath(path);
        		}
        		String displayName = multiArgs.replace("-path:" + path, "");
        		main.manager.createRank(args[1], Double.valueOf(costy), path, displayName);
        		main.getConfigManager().saveRanksConfig();
        		sender.sendMessage(main.prxAPI.g("createrank").replace("%createdrank%", args[1])
        				.replace("%rankcost%", args[2]));
        		sender.sendMessage(main.prxAPI.c("&7Display: &r" + main.prxAPI.c(main.getGlobalStorage().translateHexColorCodes(displayName))));
        		if(!path.equals("")) {
        			sender.sendMessage(main.prxAPI.c("&7Path: &6" + path));
        		}
        	} else if (args[0].equalsIgnoreCase("createprestige")) {
        		double costy;
        		if(!main.prxAPI.numberAPI.isNumber(args[2])) {
        			costy = main.prxAPI.numberAPI.parseBalance(args[2]);
        		} else {
        			costy = Double.valueOf(args[2]);
        		}
        		String displayName = main.getArgs(args, 3);
        		main.manager.createPrestige(args[1], Double.valueOf(costy), displayName);
        		main.getConfigManager().savePrestigesConfig();
        		sender.sendMessage(main.prxAPI.g("createprestige").replace("%createdprestige", args[1])
        				.replace("%prestigecost%", args[2]));
        		sender.sendMessage(main.prxAPI.c("&7Display: " + main.prxAPI.c(main.getGlobalStorage().translateHexColorCodes(displayName))));
        	} else if (args[0].equalsIgnoreCase("createrebirth")) {
        		double costy;
        		if(!main.prxAPI.numberAPI.isNumber(args[2])) {
        			costy = main.prxAPI.numberAPI.parseBalance(args[2]);
        		} else {
        			costy = Double.valueOf(args[2]);
        		}
        		String displayName = main.getArgs(args, 3);
        		main.manager.createRebirth(args[1], Double.valueOf(costy), displayName);
        		main.getConfigManager().saveRebirthsConfig();
        		sender.sendMessage(main.prxAPI.g("createrebirth").replace("%createdrebirth%", args[1])
        		        .replace("%rebirthcost%", args[2]));
        		sender.sendMessage(main.prxAPI.c("&7Display: " + main.prxAPI.c(main.getGlobalStorage().translateHexColorCodes(displayName))));
        	}
        }
		return true;
	}
}
