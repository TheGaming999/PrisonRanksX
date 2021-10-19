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


public class Prestiges {
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	String prestigeCurrentFormat;
	String prestigeCompletedFormat;
	String prestigeOtherFormat;
	boolean enablePages;
	int prestigePerPage;
	List<String> prestigeWithPagesListFormat;
	List<String> prestigeListFormat;
	boolean isCustomList;
	List<String> prestigeListFormatHeader;
	List<String> prestigeListFormatFooter;
	List<String> prestigesCollection;
	List<String> currentPrestiges;
	List<String> completedPrestiges;
	List<String> otherPrestiges;
	List<String> nonPagedPrestiges;
	String lastPageReached;
	public String prestigeListConsole;
	List<String> header;
	List<String> footer;
	public String prestigeListInvalidPage;
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
		  prestigePerPage = main.globalStorage.getIntegerData("Prestigelist-text.prestige-per-page");
	      int totalPageCount = 1;
	 
	      if((list.size() % prestigePerPage) == 0)
	      {
	        if(list.size() > 0)
	        {
	            totalPageCount = list.size() / prestigePerPage;
	        }     
	      }
	      else
	      {
	        totalPageCount = (list.size() / prestigePerPage) + 1;
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
	            sender.sendMessage(ChatColor.BLACK + "[?] prestiges list is empty [?]");
	        }
	        else
	        {
	            int i = 0, k = 0;
	            page--;
	 
	            for (String entry : list)
	            {
	              k++;
	              if ((((page * prestigePerPage) + i + 1) == k) && (k != ((page * prestigePerPage) + prestigePerPage + 1)))
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
			prestigeCurrentFormat = main.globalStorage.getStringData("Prestigelist-text.prestige-current-format");
			prestigeCompletedFormat = main.globalStorage.getStringData("Prestigelist-text.prestige-completed-format");
			prestigeOtherFormat = main.globalStorage.getStringData("Prestigelist-text.prestige-other-format");
			enablePages = main.globalStorage.getBooleanData("Prestigelist-text.enable-pages");
			prestigePerPage = main.globalStorage.getIntegerData("Prestigelist-text.prestige-per-page");
			prestigeWithPagesListFormat = main.globalStorage.getStringListData("Prestigelist-text.prestige-with-pages-list-format");
			prestigeListFormat = main.globalStorage.getStringListData("Prestigelist-text.prestige-list-format");
			prestigeListFormatHeader = new ArrayList<>();
			prestigeListFormatFooter = new ArrayList<>();
			prestigesCollection = new LinkedList<>();
			currentPrestiges = new ArrayList<>();
			completedPrestiges = new ArrayList<>();
			otherPrestiges = new ArrayList<>();
			nonPagedPrestiges = new ArrayList<>();
			lastPageReached = main.messagesStorage.getStringMessage("prestigelist-last-page-reached");
			prestigeListConsole = main.messagesStorage.getStringMessage("prestigelist-console");
			if(enablePages) {
				if(prestigeWithPagesListFormat.contains("[prestigeslist]")) {
					isCustomList = false;
				} else {
					isCustomList = true;
				}
			} else {
				if(prestigeListFormat.contains("[prestigeslist]")) {
					isCustomList = false;
				} else {
					isCustomList = true;
				}
			}
			prestigeListInvalidPage = main.messagesStorage.getStringMessage("prestigelist-invalid-page");
		}
	  
	public Prestiges() {}
	
	/**
	 * 
	 * @param pageNumber put null if you want to send a normal list
	 * @param sender
	 */
	public void send(String pageNumber, CommandSender sender) {
		if((!enablePages || pageNumber == null) && (!main.isInfinitePrestige)) {
			sendList(sender);
		} else {
			if(!main.prxAPI.numberAPI.isNumber(pageNumber) || Integer.valueOf(pageNumber) < 1) {
				sender.sendMessage(main.prxAPI.c(prestigeListInvalidPage).replace("%page%", pageNumber));
				return;
			}
			sendPagedList(pageNumber, sender);
		}
	}
	
	private void sendList(CommandSender sender) {
		if(!enablePages) {
			// no enable pages
			if(isCustomList) {
				Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
				prestigeListFormat.forEach(format -> sender.sendMessage(main.prxAPI.c(format)));
				});
				return;
			}
			Player p = (Player)sender;
			String pathName = main.prxAPI.getPlayerRankPath(p).getPathName();
			String prestigeName = main.prxAPI.getPlayerPrestige(p);
			String rebirth = main.prxAPI.getPlayerRebirth(p);
			List<String> newPrestigesCollection = main.prestigeStorage.getNativeLinkedPrestigesCollection();
			if(prestigesCollection.isEmpty()) {
                prestigesCollection = newPrestigesCollection;
			}
			
			if(prestigesCollection.size() != newPrestigesCollection.size()) {
				prestigesCollection = newPrestigesCollection;
			}
			Integer varIndex = prestigeListFormat.indexOf(String.valueOf("[prestigeslist]"));
			// header and footer setup {
			if(prestigeListFormatHeader.isEmpty() && prestigeListFormatFooter.isEmpty() && prestigeListFormat.size() > 1) {
			  for(int i = 0; i < prestigeListFormat.size(); i++) {
				  if(varIndex > i) {
					  prestigeListFormatHeader.add(prestigeListFormat.get(i));
				  } if (varIndex < i) {
					  prestigeListFormatFooter.add(prestigeListFormat.get(i));
			  	  }
			  }
			}
			// }
			// send header {
			for(String header : prestigeListFormatHeader) {
		       sender.sendMessage(main.prxAPI.c(header));
			}
			// }
			// send ranks list {
		    currentPrestiges.clear();
			completedPrestiges.clear();
			otherPrestiges.clear();
			String lastRank = main.prxAPI.getLastRank(pathName);
			String lastRankDisplay = main.prxAPI.getRank(RankPath.getRankPath(lastRank, pathName)).getDisplayName();
			String firstPrestige = main.prxAPI.getFirstPrestige();
			String firstPrestigeDisplay = main.prestigeStorage.getDisplayName(firstPrestige);
			double firstPrestigeCost = main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(firstPrestige));
			if(prestigeName == null) {
				otherPrestiges.add(main.prxAPI.cp(prestigeOtherFormat.replace("%prestige_name%", lastRank)
						.replace("%prestige_displayname%", lastRankDisplay)
						.replace("%nextprestige_name%", firstPrestige)
						.replace("%nextprestige_displayname%", firstPrestigeDisplay)
						.replace("%nextprestige_cost%", String.valueOf(firstPrestigeCost))
						.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(firstPrestigeCost))
						, p));
			} else if (prestigeName.equals(firstPrestige)) {
				completedPrestiges.add(main.prxAPI.cp(prestigeCompletedFormat.replace("%prestige_name%", lastRank)
						.replace("%prestige_displayname%", lastRankDisplay)
						.replace("%nextprestige_name%", firstPrestige)
						.replace("%nextprestige_displayname%", firstPrestigeDisplay)
						.replace("%nextprestige_cost%", String.valueOf(firstPrestigeCost))
						.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(firstPrestigeCost))
						, p));
			} else {
				completedPrestiges.add(main.prxAPI.cp(prestigeCompletedFormat.replace("%prestige_name%", lastRank)
						.replace("%prestige_displayname%", lastRankDisplay)
						.replace("%nextprestige_name%", firstPrestige)
						.replace("%nextprestige_displayname%", firstPrestigeDisplay)
						.replace("%nextprestige_cost%", String.valueOf(firstPrestigeCost))
						.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(firstPrestigeCost))
						, p));
			}
			int currentPrestigeIndex = prestigesCollection.indexOf(prestigeName);
			for(String prestige : prestigesCollection) {
				if(currentPrestigeIndex == prestigesCollection.indexOf(prestige)) {
					// save rank current format {
					String prestige2 = prestige;
					if(!main.prestigeStorage.getNextPrestigeName(prestige2).equalsIgnoreCase("lastprestige")) {
					String nextPrestige = main.prestigeStorage.getNextPrestigeName(prestige2);
					String format = main.prxAPI.cp(prestigeCurrentFormat.replace("%prestige_name%", prestige2)
							.replace("%prestige_displayname%", main.prestigeStorage.getDisplayName(prestige2))
							.replace("%nextprestige_name%", nextPrestige)
							.replace("%nextprestige_displayname%", main.prestigeStorage.getNextPrestigeDisplayName(prestige2))
							.replace("%nextprestige_cost%", String.valueOf(main.prxAPI.getIncreasedPrestigeCost(rebirth, nextPrestige)))
							.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(nextPrestige))))
                            , p);			
					    currentPrestiges.add(format);
					}
					// }
				} if (currentPrestigeIndex > prestigesCollection.indexOf(prestige)) {
					// save rank completed format {
					String prestige2 = prestige;
					if(!main.prestigeStorage.getNextPrestigeName(prestige2).equalsIgnoreCase("lastprestige")) {
					String nextPrestige = main.prestigeStorage.getNextPrestigeName(prestige2);
					String format = main.prxAPI.cp(prestigeCompletedFormat.replace("%prestige_name%", prestige2)
							.replace("%prestige_displayname%", main.prestigeStorage.getDisplayName(prestige2))
							.replace("%nextprestige_name%", nextPrestige)
							.replace("%nextprestige_displayname%", main.prestigeStorage.getNextPrestigeDisplayName(prestige2))
							.replace("%nextprestige_cost%", String.valueOf(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(nextPrestige))))
							.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(nextPrestige))))
                            , p);			
					completedPrestiges.add(format);
					}
					// }
				} if (currentPrestigeIndex < prestigesCollection.indexOf(prestige)) {
					// save rank other format {
					String prestige2 = prestige;
					if(!main.prestigeStorage.getNextPrestigeName(prestige2).equalsIgnoreCase("lastprestige")) {
					String nextPrestige = main.prestigeStorage.getNextPrestigeName(prestige2);
					String format = main.prxAPI.cp(prestigeOtherFormat.replace("%prestige_name%", prestige2)
							.replace("%prestige_displayname%", main.prestigeStorage.getDisplayName(prestige2))
							.replace("%nextprestige_name%", nextPrestige)
							.replace("%nextprestige_displayname%", main.prestigeStorage.getNextPrestigeDisplayName(prestige2))
							.replace("%nextprestige_cost%", String.valueOf(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(nextPrestige))))
							.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(nextPrestige))))
                            , p);			
					otherPrestiges.add(format);
					}
					// }
				}
			}
            completedPrestiges.forEach(line -> {sender.sendMessage(line);});
			currentPrestiges.forEach(line -> {sender.sendMessage(line);});
			otherPrestiges.forEach(line -> {sender.sendMessage(line);});
			for(String footer : prestigeListFormatFooter) {
				sender.sendMessage(main.prxAPI.c(footer));
			}
			// }
			return;
		}
	}
	
	private void sendPagedList(String pageNumber, CommandSender sender) {
		Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
		if(enablePages) {
			if(isCustomList) {
				List<String> customList = CollectionUtils.paginateList(prestigeWithPagesListFormat, prestigePerPage, Integer.parseInt(pageNumber));
				customList.forEach(line -> {
					sender.sendMessage(main.getString(line, sender.getName()));
				});
				//this.paginate(sender, prestigeWithPagesListFormat, Integer.parseInt(pageNumber), null, null);
				return;
			}	
			Player p = (Player)sender;
			String pathName = main.prxAPI.getPlayerRankPath(p).getPathName();
            String prestigeName = main.prxAPI.getPlayerPrestige(p);
            String rebirth = main.prxAPI.getPlayerRebirth(p);
            if(main.isInfinitePrestige) {
            	int size = (int)main.infinitePrestigeSettings.getFinalPrestige();
            	int finalPage = CollectionUtils.getAccurateFinalPage(size, prestigePerPage)-1;
            	long currentPrestige = prestigeName != null ? Long.valueOf(prestigeName) : 0;
            	String finalPrestige = String.valueOf(size);
            	int currentPage = Integer.valueOf(pageNumber);;
            	List<String> virtualList = new LinkedList<>();
            	int counter = 0;
            	if(currentPage > finalPage) {
      			  sender.sendMessage(main.prxAPI.c(lastPageReached.replace("%page%", String.valueOf(finalPage))));
      			  return;
      			}
            	Integer varIndex = prestigeWithPagesListFormat.indexOf("[prestigeslist]");
    			// header and footer setup {
    			if(prestigeListFormatHeader.isEmpty() && prestigeListFormatFooter.isEmpty() && prestigeListFormat.size() > 1) {
    			  for(int i = 0; i < prestigeWithPagesListFormat.size(); i++) {
    				  if(varIndex > i) {
    					  prestigeListFormatHeader.add(prestigeWithPagesListFormat.get(i));
    				  } if (varIndex < i) {
    					  prestigeListFormatFooter.add(prestigeWithPagesListFormat.get(i));
    			  	  }
    			  }
    			}
    			if(header == null) {
    				header = new ArrayList<String>();
    			}
    			header.clear();
    			for(String header : prestigeListFormatHeader) {
    		       this.header.add(main.prxAPI.c(header.replace("%currentpage%", pageNumber).replace("%totalpages%", String.valueOf(finalPage))));
    			}
				completedPrestiges.clear();
				currentPrestiges.clear();
				otherPrestiges.clear();
    			if(currentPage <= 1) {
    				String lastRank = main.prxAPI.getLastRank(pathName);
    				String lastRankDisplay = main.prxAPI.getRank(RankPath.getRankPath(lastRank, pathName)).getDisplayName();
    				String firstPrestige = "1";
    				String firstPrestigeDisplay = main.prestigeStorage.getDisplayName(firstPrestige);
    				double firstPrestigeCost = main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(firstPrestige));
    				if(prestigeName == null) {
    					otherPrestiges.add(main.prxAPI.cp(prestigeOtherFormat.replace("%prestige_name%", lastRank)
    							.replace("%prestige_displayname%", lastRankDisplay)
    							.replace("%nextprestige_name%", firstPrestige)
    							.replace("%nextprestige_displayname%", firstPrestigeDisplay)
    							.replace("%nextprestige_cost%", String.valueOf(firstPrestigeCost))
    							.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(firstPrestigeCost))
    							, p));
    				} else if (prestigeName.equals(firstPrestige)) {
    					completedPrestiges.add(main.prxAPI.cp(prestigeCompletedFormat.replace("%prestige_name%", lastRank)
    							.replace("%prestige_displayname%", lastRankDisplay)
    							.replace("%nextprestige_name%", firstPrestige)
    							.replace("%nextprestige_displayname%", firstPrestigeDisplay)
    							.replace("%nextprestige_cost%", String.valueOf(firstPrestigeCost))
    							.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(firstPrestigeCost))
    							, p));
    				} else {
    					completedPrestiges.add(main.prxAPI.cp(prestigeCompletedFormat.replace("%prestige_name%", lastRank)
    							.replace("%prestige_displayname%", lastRankDisplay)
    							.replace("%nextprestige_name%", firstPrestige)
    							.replace("%nextprestige_displayname%", firstPrestigeDisplay)
    							.replace("%nextprestige_cost%", String.valueOf(firstPrestigeCost))
    							.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(firstPrestigeCost))
    							, p));
    				}
    				}
            	for(int i = 0; i < prestigePerPage; i++) {
            		int elementIndex = CollectionUtils.paginateIndex(counter, prestigePerPage, Integer.valueOf(pageNumber));
            		if(elementIndex < 0 || elementIndex >= size) {
      	    		  break;
      	    	    }
            		long prestigeNumber = Long.valueOf(elementIndex+1);
            		String prestige = String.valueOf(prestigeNumber);
            		if(currentPrestige == prestigeNumber) {
    					// save prestige current format {
    					String prestige2 = prestige;
    					if(!prestige2.equalsIgnoreCase(finalPrestige)) {
    						String nextPrestige = main.prestigeStorage.getNextPrestigeName(prestige2);
    					String format = main.prxAPI.cp(prestigeCurrentFormat.replace("%prestige_name%", prestige2)
    							.replace("%prestige_displayname%", main.prestigeStorage.getDisplayName(prestige2))
    							.replace("%nextprestige_name%", nextPrestige)
    							.replace("%nextprestige_displayname%", main.prestigeStorage.getNextPrestigeDisplayName(prestige2))
    							.replace("%nextprestige_cost%", String.valueOf(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(nextPrestige))))
    							.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(nextPrestige))))
                                , p);			
    					    currentPrestiges.add(format);
    					}
    					// }
    				}
            		if (currentPrestige > prestigeNumber) {
    					// save prestige completed format {
    					String prestige2 = prestige;
    					if(!prestige2.equalsIgnoreCase(finalPrestige)) {
    						String nextPrestige = main.prestigeStorage.getNextPrestigeName(prestige2);
    					String format = main.prxAPI.cp(prestigeCompletedFormat.replace("%prestige_name%", prestige2)
    							.replace("%prestige_displayname%", main.prestigeStorage.getDisplayName(prestige2))
    							.replace("%nextprestige_name%", nextPrestige)
    							.replace("%nextprestige_displayname%", main.prestigeStorage.getNextPrestigeDisplayName(prestige2))
    							.replace("%nextprestige_cost%", String.valueOf(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(nextPrestige))))
    							.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(nextPrestige))))
                                , p);			
    					completedPrestiges.add(format);
    					}
    					// }
    				} if (currentPrestige < prestigeNumber) {
    					// save rank other format {
    					String prestige2 = prestige;
    					if(!main.prestigeStorage.getNextPrestigeName(prestige2).equalsIgnoreCase("lastprestige")) {
    						String nextPrestige = main.prestigeStorage.getNextPrestigeName(prestige2);
    					String format = main.prxAPI.cp(prestigeOtherFormat.replace("%prestige_name%", prestige2)
    							.replace("%prestige_displayname%", main.prestigeStorage.getDisplayName(prestige2))
    							.replace("%nextprestige_name%", nextPrestige)
    							.replace("%nextprestige_displayname%", main.prestigeStorage.getNextPrestigeDisplayName(prestige2))
    							.replace("%nextprestige_cost%", String.valueOf(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(nextPrestige))))
    							.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(nextPrestige))))
                                , p);			
    					otherPrestiges.add(format);
    					}
    					// }
    				}
    				
            		//virtualList.add("-> P" + String.valueOf(elementIndex+1));
            		counter++;
            	}
            	completedPrestiges.forEach(virtualList::add);
				currentPrestiges.forEach(virtualList::add);
				otherPrestiges.forEach(virtualList::add);
				if(footer == null) {
					footer = new ArrayList<String>();
				}
				footer.clear();
				for(String footer : prestigeListFormatFooter) {
					this.footer.add(main.prxAPI.c(footer.replace("%currentpage%", pageNumber).replace("%totalpages%", String.valueOf(finalPage))));
				}
				this.header.forEach(sender::sendMessage);
				virtualList.forEach(sender::sendMessage);
				this.footer.forEach(sender::sendMessage);

				return;
			}
			List<String> newPrestigesCollection = main.prestigeStorage.getNativeLinkedPrestigesCollection();
			if(prestigesCollection.isEmpty()) {
                prestigesCollection = newPrestigesCollection;
			}
			
			if(prestigesCollection.size() != newPrestigesCollection.size()) {
				prestigesCollection = newPrestigesCollection;
			}
			PaginatedList paginatedList = CollectionUtils.paginateListCollectable(prestigesCollection, prestigePerPage, Integer.parseInt(pageNumber));
			int finalPage = paginatedList.getFinalPage();
			int currentPage = paginatedList.getCurrentPage();
			if(currentPage > finalPage) {
			  sender.sendMessage(main.prxAPI.c(lastPageReached.replace("%page%", String.valueOf(finalPage))));
			  return;
			}
			prestigesCollection = paginatedList.collect();
			Integer varIndex = prestigeWithPagesListFormat.indexOf("[prestigeslist]");
			// header and footer setup {
			if(prestigeListFormatHeader.isEmpty() && prestigeListFormatFooter.isEmpty() && prestigeListFormat.size() > 1) {
			  for(int i = 0; i < prestigeWithPagesListFormat.size(); i++) {
				  if(varIndex > i) {
					  prestigeListFormatHeader.add(prestigeWithPagesListFormat.get(i));
				  } if (varIndex < i) {
					  prestigeListFormatFooter.add(prestigeWithPagesListFormat.get(i));
			  	  }
			  }
			}
			// }
			// send header {
			if(header == null) {
				header = new ArrayList<String>();
			}
			header.clear();
			for(String header : prestigeListFormatHeader) {
		       this.header.add(main.prxAPI.c(header.replace("%currentpage%", pageNumber).replace("%totalpages%", String.valueOf(finalPage))));
			}
			// }
			// send prestiges list {
		    currentPrestiges.clear();
			completedPrestiges.clear();
			otherPrestiges.clear();
			if(currentPage <= 1) {
			String lastRank = main.prxAPI.getLastRank(pathName);
			String lastRankDisplay = main.prxAPI.getRank(RankPath.getRankPath(lastRank, pathName)).getDisplayName();
			String firstPrestige = main.prxAPI.getFirstPrestige();
			String firstPrestigeDisplay = main.prestigeStorage.getDisplayName(firstPrestige);
			double firstPrestigeCost = main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(firstPrestige));
			if(prestigeName == null) {
				otherPrestiges.add(main.prxAPI.cp(prestigeOtherFormat.replace("%prestige_name%", lastRank)
						.replace("%prestige_displayname%", lastRankDisplay)
						.replace("%nextprestige_name%", firstPrestige)
						.replace("%nextprestige_displayname%", firstPrestigeDisplay)
						.replace("%nextprestige_cost%", String.valueOf(firstPrestigeCost))
						.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(firstPrestigeCost))
						, p));
			} else if (prestigeName.equals(firstPrestige)) {
				completedPrestiges.add(main.prxAPI.cp(prestigeCompletedFormat.replace("%prestige_name%", lastRank)
						.replace("%prestige_displayname%", lastRankDisplay)
						.replace("%nextprestige_name%", firstPrestige)
						.replace("%nextprestige_displayname%", firstPrestigeDisplay)
						.replace("%nextprestige_cost%", String.valueOf(firstPrestigeCost))
						.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(firstPrestigeCost))
						, p));
			} else {
				completedPrestiges.add(main.prxAPI.cp(prestigeCompletedFormat.replace("%prestige_name%", lastRank)
						.replace("%prestige_displayname%", lastRankDisplay)
						.replace("%nextprestige_name%", firstPrestige)
						.replace("%nextprestige_displayname%", firstPrestigeDisplay)
						.replace("%nextprestige_cost%", String.valueOf(firstPrestigeCost))
						.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(firstPrestigeCost))
						, p));
			}
			}
			int currentPrestigeIndex = newPrestigesCollection.indexOf(prestigeName);
			for(String prestige : prestigesCollection) {
				if(currentPrestigeIndex == newPrestigesCollection.indexOf(prestige)) {
					// save prestige current format {
					String prestige2 = prestige;
					if(!main.prestigeStorage.getNextPrestigeName(prestige2).equalsIgnoreCase("lastprestige")) {
						String nextPrestige = main.prestigeStorage.getNextPrestigeName(prestige2);
					String format = main.prxAPI.cp(prestigeCurrentFormat.replace("%prestige_name%", prestige2)
							.replace("%prestige_displayname%", main.prestigeStorage.getDisplayName(prestige2))
							.replace("%nextprestige_name%", nextPrestige)
							.replace("%nextprestige_displayname%", main.prestigeStorage.getNextPrestigeDisplayName(prestige2))
							.replace("%nextprestige_cost%", String.valueOf(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(nextPrestige))))
							.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(nextPrestige))))
                            , p);			
					    currentPrestiges.add(format);
					}
					// }
				} if (currentPrestigeIndex > newPrestigesCollection.indexOf(prestige)) {
					// save prestige completed format {
					String prestige2 = prestige;
					if(!main.prestigeStorage.getNextPrestigeName(prestige2).equalsIgnoreCase("lastprestige")) {
						String nextPrestige = main.prestigeStorage.getNextPrestigeName(prestige2);
					String format = main.prxAPI.cp(prestigeCompletedFormat.replace("%prestige_name%", prestige2)
							.replace("%prestige_displayname%", main.prestigeStorage.getDisplayName(prestige2))
							.replace("%nextprestige_name%", nextPrestige)
							.replace("%nextprestige_displayname%", main.prestigeStorage.getNextPrestigeDisplayName(prestige2))
							.replace("%nextprestige_cost%", String.valueOf(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(nextPrestige))))
							.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(nextPrestige))))
                            , p);			
					completedPrestiges.add(format);
					}
					// }
				} if (currentPrestigeIndex < newPrestigesCollection.indexOf(prestige)) {
					// save rank other format {
					String prestige2 = prestige;
					if(!main.prestigeStorage.getNextPrestigeName(prestige2).equalsIgnoreCase("lastprestige")) {
						String nextPrestige = main.prestigeStorage.getNextPrestigeName(prestige2);
					String format = main.prxAPI.cp(prestigeOtherFormat.replace("%prestige_name%", prestige2)
							.replace("%prestige_displayname%", main.prestigeStorage.getDisplayName(prestige2))
							.replace("%nextprestige_name%", nextPrestige)
							.replace("%nextprestige_displayname%", main.prestigeStorage.getNextPrestigeDisplayName(prestige2))
							.replace("%nextprestige_cost%", String.valueOf(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(nextPrestige))))
							.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(nextPrestige))))
                            , p);			
					otherPrestiges.add(format);
					}
					// }
				}
			}
			nonPagedPrestiges.clear();
			
            completedPrestiges.forEach(line -> {nonPagedPrestiges.add(line);});
			currentPrestiges.forEach(line -> {nonPagedPrestiges.add(line);});
			otherPrestiges.forEach(line -> {nonPagedPrestiges.add(line);});
			if(footer == null) {
				footer = new ArrayList<String>();
			}
			footer.clear();
			for(String footer : prestigeListFormatFooter) {
				this.footer.add(main.prxAPI.c(footer.replace("%currentpage%", pageNumber).replace("%totalpages%", String.valueOf(finalPage))));
			}
			this.header.forEach(sender::sendMessage);
			this.nonPagedPrestiges.forEach(sender::sendMessage);
			this.footer.forEach(sender::sendMessage);
            //paginate(sender, nonPagedPrestiges, Integer.valueOf(pageNumber), header, footer);

			
		}
		});
	}
}
