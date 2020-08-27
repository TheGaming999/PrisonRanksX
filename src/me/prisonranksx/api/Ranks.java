package me.prisonranksx.api;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.utils.CollectionUtils;
import me.prisonranksx.utils.CollectionUtils.PaginatedList;

public class Ranks {

	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	String rankCurrentFormat;
	String rankCompletedFormat;
	String rankOtherFormat;
	boolean enablePages;
	int rankPerPage;
	List<String> rankWithPagesListFormat;
	List<String> rankListFormat;
	boolean isCustomList;
	List<String> rankListFormatHeader;
	List<String> rankListFormatFooter;
	List<String> header;
	List<String> footer;
	List<String> ranksCollection;
	List<String> currentRanks;
	List<String> completedRanks;
	List<String> otherRanks;
	List<String> nonPagedRanks;
	String lastPageReached;
	public String rankListConsole;
	public String rankListInvalidPage;
	/**
	    *
	    * @param sender The sender to send the list to
	    * @param list The list to paginate
	    * @param page The page number to display.
	    * @param countAll The count of all available entries 
	    */
	@Deprecated
	  public void paginate(CommandSender sender, List<String> list, int page, List<String> header, List<String> footer)
	  {
		  rankPerPage = main.globalStorage.getIntegerData("Ranklist-text.rank-per-page");
	      int totalPageCount = 1;
	 
	      if((list.size() % rankPerPage) == 0)
	      {
	        if(list.size() > 0)
	        {
	            totalPageCount = list.size() / rankPerPage;
	        }     
	      }
	      else
	      {
	        totalPageCount = (list.size() / rankPerPage) + 1;
	      }
	 
	      if(page <= totalPageCount)
	      {

	        //beginline
           if(header != null) {
        	  for(String line : header) {
        		  sender.sendMessage(line.replace("%totalpages%", String.valueOf(totalPageCount)));
        	  }
           }

	        if(list.isEmpty())
	        {
	            sender.sendMessage(ChatColor.BLACK + "[?] ranks list is empty [?]");
	        }
	        else
	        {
	            int i = 0, k = 0;
	            page--;
	 
	            for (String entry : list)
	            {
	              k++;
	              if ((((page * rankPerPage) + i + 1) == k) && (k != ((page * rankPerPage) + rankPerPage + 1)))
	              {
	                  i++;
	                  sender.sendMessage(entry.replace("%totalpages%", String.valueOf(totalPageCount)).replace("%currentpage%", String.valueOf(page)));
	              }
	            }
	        }
	 //endline
	           if(footer != null) {
	         	  for(String line : footer) {
	         		  sender.sendMessage(line.replace("%totalpages%", String.valueOf(totalPageCount)));
	         	  }
	            }
	      }
	      else
	      {
	        sender.sendMessage(main.prxAPI.c(lastPageReached.replace("%page%", String.valueOf(totalPageCount))));
	      }
	  }
	
		public void load() {
			rankCurrentFormat = main.globalStorage.getStringData("Ranklist-text.rank-current-format");
			rankCompletedFormat = main.globalStorage.getStringData("Ranklist-text.rank-completed-format");
			rankOtherFormat = main.globalStorage.getStringData("Ranklist-text.rank-other-format");
			enablePages = main.globalStorage.getBooleanData("Ranklist-text.enable-pages");
			rankPerPage = main.globalStorage.getIntegerData("Ranklist-text.rank-per-page");
			rankWithPagesListFormat = main.globalStorage.getStringListData("Ranklist-text.rank-with-pages-list-format");
			rankListFormat = main.globalStorage.getStringListData("Ranklist-text.rank-list-format");
			rankListFormatHeader = new ArrayList<>();
			rankListFormatFooter = new ArrayList<>();
			ranksCollection = new LinkedList<>();
			currentRanks = new ArrayList<>();
			completedRanks = new ArrayList<>();
			otherRanks = new ArrayList<>();
			nonPagedRanks = new ArrayList<>();
			lastPageReached = main.messagesStorage.getStringMessage("ranklist-last-page-reached");
			rankListConsole = main.messagesStorage.getStringMessage("ranklist-console");
			if(enablePages) {
				if(rankWithPagesListFormat.contains("[rankslist]")) {
					isCustomList = false;
				} else {
					isCustomList = true;
				}
			} else {
				if(rankListFormat.contains("[rankslist]")) {
					isCustomList = false;
				} else {
					isCustomList = true;
				}
			}
			rankListInvalidPage = main.messagesStorage.getStringMessage("ranklist-invalid-page");
		}
	  
	public Ranks() {}
	
	/**
	 * 
	 * @param pageNumber put null if you want to send a normal list
	 * @param sender
	 */
	public void send(final String pageNumber, final CommandSender sender) {
		if(!enablePages || pageNumber == null) {
			sendList(sender);
		} else {
			if(!main.prxAPI.numberAPI.isNumber(pageNumber) || Integer.valueOf(pageNumber) < 1) {
				sender.sendMessage(main.prxAPI.c(rankListInvalidPage).replace("%page%", pageNumber));
				return;
			}
			sendPagedList(pageNumber, sender);
		}
	}
	
	/**
	 * 
	 * @param pageNumber put null if you want to send a normal list
	 * @param player
	 */
	public void send(final String pageNumber, final Player player) {
		if(!enablePages || pageNumber == null) {
			sendList(player);
		} else {
			if(!main.prxAPI.numberAPI.isNumber(pageNumber) || Integer.valueOf(pageNumber) < 1) {
				player.sendMessage(main.prxAPI.c(rankListInvalidPage).replace("%page%", pageNumber));
				return;
			}
			sendPagedList(pageNumber, player);
		}
	}
	
	private void sendList(CommandSender sender) {
		if(!enablePages) {
			// no enable pages
			if(isCustomList) {
				rankListFormat.forEach(format -> sender.sendMessage(main.prxAPI.c(format)));
				return;
			}
			Player p = (Player)sender;
			RankPath rp = main.prxAPI.getPlayerRankPath(p);
			String pathName = rp.getPathName();
			String rankName = rp.getRankName();
			String prestige = main.prxAPI.getPlayerPrestige(p);
			String rebirth = main.prxAPI.getPlayerRebirth(p);
			List<String> newRanksCollection = main.rankStorage.getRanksCollection(pathName);
			if(ranksCollection.isEmpty()) {
                ranksCollection = newRanksCollection;
			}
			
			if(ranksCollection.size() != newRanksCollection.size()) {
				ranksCollection = newRanksCollection;
			}
			Integer varIndex = rankListFormat.indexOf(String.valueOf("[rankslist]"));
			// header and footer setup {
			if(rankListFormatHeader.isEmpty() && rankListFormatFooter.isEmpty() && rankListFormat.size() > 1) {
				  for(int i = 0; i < rankListFormat.size(); i++) {
					  if(varIndex > i) {
						  rankListFormatHeader.add(rankListFormat.get(i));
					  } if (varIndex < i) {
						  rankListFormatFooter.add(rankListFormat.get(i));
				  	  }
				  }
			}
			// }
			// send header {
			for(String header : rankListFormatHeader) {
		       sender.sendMessage(main.prxAPI.c(header));
			}
			// }
			// send ranks list {
			
		    currentRanks.clear();
			completedRanks.clear();
			otherRanks.clear();
			int currentRankIndex = ranksCollection.indexOf(rankName);
			for(String rank : ranksCollection) {
				if(currentRankIndex == ranksCollection.indexOf(rank)) {
					// save rank current format {
					RankPath rankPath = RankPath.getRankPath(rank, pathName);
					if(!main.rankStorage.getRankupName(rankPath).equalsIgnoreCase("lastrank")) {
					String format = main.prxAPI.cp(rankCurrentFormat.replace("%rank_name%", rank)
							.replace("%rank_displayname%", main.rankStorage.getDisplayName(rankPath))
							.replace("%nextrank_name%", main.rankStorage.getRankupName(rankPath))
							.replace("%nextrank_displayname%", main.rankStorage.getRankupDisplayName(rankPath))
							.replace("%nextrank_cost%", String.valueOf(main.prxAPI.getIncreasedRankupCostX(rebirth ,prestige, main.rankStorage.getRankupCost(rankPath))))
							.replace("%nextrank_cost_formatted%", main.prxAPI.formatBalance(main.prxAPI.getIncreasedRankupCostX(rebirth,prestige, main.rankStorage.getRankupCost(rankPath))))
                            , p);			
					    currentRanks.add(format);
					}
					// }
				} if (currentRankIndex > ranksCollection.indexOf(rank)) {
					// save rank completed format {
					RankPath rankPath = RankPath.getRankPath(rank, pathName);
					if(!main.rankStorage.getRankupName(rankPath).equalsIgnoreCase("lastrank")) {
					String format = main.prxAPI.cp(rankCompletedFormat.replace("%rank_name%", rank)
							.replace("%rank_displayname%", main.rankStorage.getDisplayName(rankPath))
							.replace("%nextrank_name%", main.rankStorage.getRankupName(rankPath))
							.replace("%nextrank_displayname%", main.rankStorage.getRankupDisplayName(rankPath))
							.replace("%nextrank_cost%", String.valueOf(main.prxAPI.getIncreasedRankupCostX(rebirth,prestige, main.rankStorage.getRankupCost(rankPath))))
							.replace("%nextrank_cost_formatted%", main.prxAPI.formatBalance(main.prxAPI.getIncreasedRankupCostX(rebirth,prestige, main.rankStorage.getRankupCost(rankPath))))
                            , p);			
					completedRanks.add(format);
					}
					// }
				} if (currentRankIndex < ranksCollection.indexOf(rank)) {
					// save rank other format {
					RankPath rankPath = RankPath.getRankPath(rank, pathName);
					if(!main.rankStorage.getRankupName(rankPath).equalsIgnoreCase("lastrank")) {
					String format = main.prxAPI.cp(rankOtherFormat.replace("%rank_name%", rank)
							.replace("%rank_displayname%", main.rankStorage.getDisplayName(rankPath))
							.replace("%nextrank_name%", main.rankStorage.getRankupName(rankPath))
							.replace("%nextrank_displayname%", main.rankStorage.getRankupDisplayName(rankPath))
							.replace("%nextrank_cost%", String.valueOf(main.prxAPI.getIncreasedRankupCostX(rebirth,prestige, main.rankStorage.getRankupCost(rankPath))))
							.replace("%nextrank_cost_formatted%", main.prxAPI.formatBalance(main.prxAPI.getIncreasedRankupCostX(rebirth,prestige, main.rankStorage.getRankupCost(rankPath))))
                            , p);			
					otherRanks.add(format);
					}
					// }
				}
			}
            completedRanks.forEach(line -> {sender.sendMessage(line);});
			currentRanks.forEach(line -> {sender.sendMessage(line);});
			otherRanks.forEach(line -> {sender.sendMessage(line);});
			for(String footer : rankListFormatFooter) {
				sender.sendMessage(main.prxAPI.c(footer));
			}
			// }
			return;
		}
	}
	
	private void sendPagedList(String pageNumber, CommandSender sender) {
		if(enablePages) {
			if(isCustomList) {
				//this.paginate(sender, rankWithPagesListFormat, Integer.parseInt(pageNumber), null, null);
				List<String> customList = CollectionUtils.paginateList(rankWithPagesListFormat, rankPerPage, Integer.parseInt(pageNumber));
				customList.forEach(line -> {
					sender.sendMessage(main.getString(line, sender.getName()));
				});
				return;
			}
			Player p = (Player)sender;
			RankPath rp = main.prxAPI.getPlayerRankPath(p);
			String pathName = rp.getPathName();
			String rankName = rp.getRankName();
			String prestige = main.prxAPI.getPlayerPrestige(p);
			String rebirth = main.prxAPI.getPlayerRebirth(p);
			List<String> newRanksCollection = main.rankStorage.getRanksCollection(pathName);
			ranksCollection = newRanksCollection;
			//int finalPage = ranksCollection.size() / rankPerPage < 1 ? 1 : ranksCollection.size() / rankPerPage;
			//if(Integer.valueOf(pageNumber) > finalPage) {
				//main.debug(sender.getName() + " executed '/ranks' ,Max Pages Reached (?:" + finalPage + ")");
				//return;
			//}
			PaginatedList paginatedList = CollectionUtils.paginateListCollectable(ranksCollection, rankPerPage, Integer.parseInt(pageNumber));
			int currentPage = paginatedList.getCurrentPage();
			int finalPage = paginatedList.getFinalPage();
			if(currentPage > finalPage) {
				  sender.sendMessage(main.prxAPI.c(lastPageReached.replace("%page%", String.valueOf(finalPage))));
				  return;
			}
			ranksCollection = paginatedList.collect();
			int varIndex = rankWithPagesListFormat.indexOf("[rankslist]");
			// header and footer setup {
			if(rankListFormatHeader.isEmpty() && rankListFormatFooter.isEmpty() && rankListFormat.size() > 1) {
			  for(int i = 0; i < rankWithPagesListFormat.size(); i++) {
				  if(varIndex > i) {
					  rankListFormatHeader.add(rankWithPagesListFormat.get(i));
				  } if (varIndex < i) {
					  rankListFormatFooter.add(rankWithPagesListFormat.get(i));
			  	  }
			  }
			}
			// }
			// send header {
			if(header == null) {
				header = new ArrayList<String>();
			}
			header.clear();
			for(String header : rankListFormatHeader) {
		       this.header.add(main.prxAPI.c(header.replace("%currentpage%", pageNumber).replace("%totalpages%", String.valueOf(finalPage))));
			}
			// }
			// send ranks list {
		    currentRanks.clear();
			completedRanks.clear();
			otherRanks.clear();
			int currentRankIndex = newRanksCollection.indexOf(rankName);
			for(String rank : ranksCollection) {
				if(currentRankIndex == newRanksCollection.indexOf(rank)) {
					// save rank current format {
					RankPath rankPath = RankPath.getRankPath(rank, pathName);
					if(!main.rankStorage.getRankupName(rankPath).equalsIgnoreCase("lastrank")) {
					String format = main.prxAPI.cp(rankCurrentFormat.replace("%rank_name%", rank)
							.replace("%rank_displayname%", main.rankStorage.getDisplayName(rankPath))
							.replace("%nextrank_name%", main.rankStorage.getRankupName(rankPath))
							.replace("%nextrank_displayname%", main.rankStorage.getRankupDisplayName(rankPath))
							.replace("%nextrank_cost%", String.valueOf(main.prxAPI.getIncreasedRankupCostX(rebirth, prestige, main.rankStorage.getRankupCost(rankPath))))
							.replace("%nextrank_cost_formatted%", main.prxAPI.formatBalance(main.prxAPI.getIncreasedRankupCostX(rebirth,prestige, main.rankStorage.getRankupCost(rankPath))))
                            , p);			
					    currentRanks.add(format);
					}
					// }
				} if (currentRankIndex > newRanksCollection.indexOf(rank)) {
					// save rank completed format {
					RankPath rankPath = RankPath.getRankPath(rank, pathName);
					if(!main.rankStorage.getRankupName(rankPath).equalsIgnoreCase("lastrank")) {
					String format = main.prxAPI.cp(rankCompletedFormat.replace("%rank_name%", rank)
							.replace("%rank_displayname%", main.rankStorage.getDisplayName(rankPath))
							.replace("%nextrank_name%", main.rankStorage.getRankupName(rankPath))
							.replace("%nextrank_displayname%", main.rankStorage.getRankupDisplayName(rankPath))
							.replace("%nextrank_cost%", String.valueOf(main.prxAPI.getIncreasedRankupCostX(rebirth, prestige, main.rankStorage.getRankupCost(rankPath))))
							.replace("%nextrank_cost_formatted%", main.prxAPI.formatBalance(main.prxAPI.getIncreasedRankupCostX(rebirth, prestige, main.rankStorage.getRankupCost(rankPath))))
                            , p);			
					completedRanks.add(format);
					}
					// }
				} if (currentRankIndex < newRanksCollection.indexOf(rank)) {
					// save rank other format {
					RankPath rankPath = RankPath.getRankPath(rank, pathName);
                    //main.debug("&3from RanksAPI: &f" + main.rankStorage.getDataHandler(rankPath.get()).readImportantValues());
					if(!main.rankStorage.getRankupName(rankPath).equalsIgnoreCase("lastrank")) {
					String format = main.prxAPI.cp(rankOtherFormat.replace("%rank_name%", rank)
							.replace("%rank_displayname%", main.rankStorage.getDisplayName(rankPath))
							.replace("%nextrank_name%", main.rankStorage.getRankupName(rankPath))
							.replace("%nextrank_displayname%", main.rankStorage.getRankupDisplayName(rankPath))
							.replace("%nextrank_cost%", String.valueOf(main.prxAPI.getIncreasedRankupCostX(rebirth, prestige, main.rankStorage.getRankupCost(rankPath))))
							.replace("%nextrank_cost_formatted%", main.prxAPI.formatBalance(main.prxAPI.getIncreasedRankupCostX(rebirth, prestige, main.rankStorage.getRankupCost(rankPath))))
                            , p);			
					otherRanks.add(format);
					}
					// }
				}
			}
			nonPagedRanks.clear();
            completedRanks.forEach(line -> {nonPagedRanks.add(line);});
			currentRanks.forEach(line -> {nonPagedRanks.add(line);});
			otherRanks.forEach(line -> {nonPagedRanks.add(line);});
			if(footer == null) {
				footer = new ArrayList<String>();
			}
			footer.clear();
			for(String footer : rankListFormatFooter) {
				this.footer.add(main.prxAPI.c(footer.replace("%currentpage%", pageNumber).replace("%totalpages%", String.valueOf(finalPage))));
			}
			this.header.forEach(sender::sendMessage);
			this.nonPagedRanks.forEach(sender::sendMessage);
			this.footer.forEach(sender::sendMessage);
            //paginate(sender, nonPagedRanks, Integer.valueOf(pageNumber), header, footer);

			
		}
	}
}
