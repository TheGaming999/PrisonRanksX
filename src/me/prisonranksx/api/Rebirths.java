package me.prisonranksx.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;

public class Rebirths {
	
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	String rebirthCurrentFormat;
	String rebirthCompletedFormat;
	String rebirthOtherFormat;
	boolean enablePages;
	int rebirthPerPage;
	List<String> rebirthWithPagesListFormat;
	List<String> rebirthListFormat;
	boolean isCustomList;
	List<String> rebirthListFormatHeader;
	List<String> rebirthListFormatFooter;
	List<String> rebirthsCollection;
	List<String> currentRebirths;
	List<String> completedRebirths;
	List<String> otherRebirths;
	List<String> nonPagedRebirths;
	String lastPageReached;
	public String rebirthListConsole;
	List<String> header;
	List<String> footer;
	/**
	    *
	    * @param sender The sender to send the list to
	    * @param list The list to paginate
	    * @param page The page number to display.
	    * @param countAll The count of all available entries 
	    */
	  public void paginate(CommandSender sender, List<String> list, int page, List<String> header, List<String> footer)
	  {
		  rebirthPerPage = main.globalStorage.getIntegerData("Rebirthlist-text.rebirth-per-page");
	      int totalPageCount = 1;
	 
	      if((list.size() % rebirthPerPage) == 0)
	      {
	        if(list.size() > 0)
	        {
	            totalPageCount = list.size() / rebirthPerPage;
	        }     
	      }
	      else
	      {
	        totalPageCount = (list.size() / rebirthPerPage) + 1;
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
	            sender.sendMessage(ChatColor.BLACK + "[?] rebirths list is empty [?]");
	        }
	        else
	        {
	            int i = 0, k = 0;
	            page--;
	 
	            for (String entry : list)
	            {
	              k++;
	              if ((((page * rebirthPerPage) + i + 1) == k) && (k != ((page * rebirthPerPage) + rebirthPerPage + 1)))
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
			rebirthCurrentFormat = main.globalStorage.getStringData("Rebirthlist-text.rebirth-current-format");
			rebirthCompletedFormat = main.globalStorage.getStringData("Rebirthlist-text.rebirth-completed-format");
			rebirthOtherFormat = main.globalStorage.getStringData("Rebirthlist-text.rebirth-other-format");
			enablePages = main.globalStorage.getBooleanData("Rebirthlist-text.enable-pages");
			rebirthPerPage = main.globalStorage.getIntegerData("Rebirthlist-text.rebirth-per-page");
			rebirthWithPagesListFormat = main.globalStorage.getStringListData("Rebirthlist-text.rebirth-with-pages-list-format");
			rebirthListFormat = main.globalStorage.getStringListData("Rebirthlist-text.rebirth-list-format");
			rebirthListFormatHeader = new ArrayList<>();
			rebirthListFormatFooter = new ArrayList<>();
			rebirthsCollection = new ArrayList<>();
			currentRebirths = new ArrayList<>();
			completedRebirths = new ArrayList<>();
			otherRebirths = new ArrayList<>();
			nonPagedRebirths = new ArrayList<>();
			lastPageReached = main.messagesStorage.getStringMessage("rebirthlist-last-page-reached");
			rebirthListConsole = main.messagesStorage.getStringMessage("rebirthlist-console");
			if(enablePages) {
				if(rebirthWithPagesListFormat.contains("[rebirthslist]")) {
					isCustomList = false;
				} else {
					isCustomList = true;
				}
			} else {
				if(rebirthListFormat.contains("[rebirthslist]")) {
					isCustomList = false;
				} else {
					isCustomList = true;
				}
			}
		}
	  
	public Rebirths() {}
	
	/**
	 * 
	 * @param pageNumber put null if you want to send a normal list
	 * @param sender
	 */
	public void send(String pageNumber, CommandSender sender) {
		if(!enablePages || pageNumber == null) {
			sendList(sender);
		} else {
			sendPagedList(pageNumber, sender);
		}
	}
	
	private void sendList(CommandSender sender) {
		if(!enablePages) {
			// no enable pages
			if(isCustomList) {
				rebirthListFormat.forEach(format -> sender.sendMessage(main.prxAPI.c(format)));
				return;
			}
			Player p = (Player)sender;
			String rebirthName = main.prxAPI.getPlayerRebirth(p);
			List<String> newRebirthsCollection = main.rebirthStorage.getRebirthsCollection();
			if(rebirthsCollection.isEmpty()) {
                rebirthsCollection = newRebirthsCollection;
			}
			
			if(rebirthsCollection.size() != newRebirthsCollection.size()) {
				rebirthsCollection = newRebirthsCollection;
			}
			Integer varIndex = rebirthListFormat.indexOf(String.valueOf("[rebirthslist]"));
			// header and footer setup {
			if(rebirthListFormatHeader.isEmpty() && rebirthListFormatFooter.isEmpty() && rebirthListFormat.size() > 1) {
			  for(String line : rebirthListFormat) {
				  if(varIndex > rebirthListFormat.indexOf(line)) {
					  rebirthListFormatHeader.add(line);
				  } if (varIndex < rebirthListFormat.indexOf(line)) {
					  rebirthListFormatFooter.add(line);
			  	  }
			  }
			}
			// }
			// send header {
			for(String header : rebirthListFormatHeader) {
		       sender.sendMessage(main.prxAPI.c(header));
			}
			// }
			// send ranks list {
		    currentRebirths.clear();
			completedRebirths.clear();
			otherRebirths.clear();
			int currentRebirthIndex = rebirthsCollection.indexOf(rebirthName);
			for(String rebirth : rebirthsCollection) {
				if(currentRebirthIndex == rebirthsCollection.indexOf(rebirth)) {
					// save rank current format {
					String rebirth2 = rebirth;
					if(!main.rebirthStorage.getNextRebirthName(rebirth2).equalsIgnoreCase("lastrebirth")) {
					String format = main.prxAPI.cp(rebirthCurrentFormat.replace("%rebirth_name%", rebirth2)
							.replace("%rebirth_displayname%", main.rebirthStorage.getDisplayName(rebirth2))
							.replace("%nextrebirth_name%", main.rebirthStorage.getDisplayName(rebirth2))
							.replace("%nextrebirth_displayname%", main.rebirthStorage.getNextRebirthDisplayName(rebirth2))
							.replace("%nextrebirth_cost%", String.valueOf(main.rebirthStorage.getNextRebirthCost(rebirth2)))
							.replace("%nextrebirth_cost_formatted%", main.prxAPI.formatBalance(main.rebirthStorage.getNextRebirthCost(rebirth2)))
                            , p);			
					    currentRebirths.add(format);
					}
					// }
				} if (currentRebirthIndex > rebirthsCollection.indexOf(rebirth)) {
					// save rank completed format {
					String rebirth2 = rebirth;
					if(!main.rebirthStorage.getNextRebirthName(rebirth2).equalsIgnoreCase("lastrebirth")) {
					String format = main.prxAPI.cp(rebirthCompletedFormat.replace("%rebirth_name%", rebirth2)
							.replace("%rebirth_displayname%", main.rebirthStorage.getDisplayName(rebirth2))
							.replace("%nextrebirth_name%", main.rebirthStorage.getNextRebirthName(rebirth2))
							.replace("%nextrebirth_displayname%", main.rebirthStorage.getNextRebirthDisplayName(rebirth2))
							.replace("%nextrebirth_cost%", String.valueOf(main.rebirthStorage.getNextRebirthCost(rebirth2)))
							.replace("%nextrebirth_cost_formatted%", main.prxAPI.formatBalance(main.rebirthStorage.getNextRebirthCost(rebirth2)))
                            , p);			
					completedRebirths.add(format);
					}
					// }
				} if (currentRebirthIndex < rebirthsCollection.indexOf(rebirth)) {
					// save rank other format {
					String rebirth2 = rebirth;
					if(!main.rebirthStorage.getNextRebirthName(rebirth2).equalsIgnoreCase("lastrebirth")) {
					String format = main.prxAPI.cp(rebirthOtherFormat.replace("%rebirth_name%", rebirth2)
							.replace("%rebirth_displayname%", main.rebirthStorage.getDisplayName(rebirth2))
							.replace("%nextrebirth_name%", main.rebirthStorage.getNextRebirthName(rebirth2))
							.replace("%nextrebirth_displayname%", main.rebirthStorage.getNextRebirthDisplayName(rebirth2))
							.replace("%nextrebirth_cost%", String.valueOf(main.rebirthStorage.getNextRebirthCost(rebirth2)))
							.replace("%nextrebirth_cost_formatted%", main.prxAPI.formatBalance(main.rebirthStorage.getNextRebirthCost(rebirth2)))
                            , p);			
					otherRebirths.add(format);
					}
					// }
				}
			}
            completedRebirths.forEach(line -> {sender.sendMessage(line);});
			currentRebirths.forEach(line -> {sender.sendMessage(line);});
			otherRebirths.forEach(line -> {sender.sendMessage(line);});
			for(String footer : rebirthListFormatFooter) {
				sender.sendMessage(main.prxAPI.c(footer));
			}
			// }
			return;
		}
	}
	
	private void sendPagedList(String pageNumber, CommandSender sender) {
		if(enablePages) {
			if(isCustomList) {
				this.paginate(sender, rebirthWithPagesListFormat, Integer.parseInt(pageNumber), null, null);
				return;
			}
			Player p = (Player)sender;
            String rebirthName = main.prxAPI.getPlayerRebirth(p);
			List<String> newRebirthsCollection = main.rebirthStorage.getRebirthsCollection();
			if(rebirthsCollection.isEmpty()) {
                rebirthsCollection = newRebirthsCollection;
			}
			
			if(rebirthsCollection.size() != newRebirthsCollection.size()) {
				rebirthsCollection = newRebirthsCollection;
			}
			Integer varIndex = rebirthWithPagesListFormat.indexOf("[rebirthslist]");
			// header and footer setup {
			if(rebirthListFormatHeader.isEmpty() && rebirthListFormatFooter.isEmpty() && rebirthListFormat.size() > 1) {
			  for(int i = 0; i < rebirthWithPagesListFormat.size(); i++) {
				  if(varIndex > i) {
					  rebirthListFormatHeader.add(rebirthWithPagesListFormat.get(i));
				  } if (varIndex < i) {
					  rebirthListFormatFooter.add(rebirthWithPagesListFormat.get(i));
			  	  }
			  }
			}
			// }
			// send header {
			if(header == null) {
				header = new ArrayList<String>();
			}
			header.clear();
			for(String header : rebirthListFormatHeader) {
		       this.header.add(main.prxAPI.c(header.replace("%currentpage%", pageNumber)));
			}
			// }
			// send rebirths list {
		    currentRebirths.clear();
			completedRebirths.clear();
			otherRebirths.clear();
			int currentRebirthIndex = rebirthsCollection.indexOf(rebirthName);
			for(String rebirth : rebirthsCollection) {
				if(currentRebirthIndex == rebirthsCollection.indexOf(rebirth)) {
					// save rebirth current format {
					String rebirth2 = rebirth;
					if(!main.rebirthStorage.getNextRebirthName(rebirth2).equalsIgnoreCase("lastrebirth")) {
					String format = main.prxAPI.cp(rebirthCurrentFormat.replace("%rebirth_name%", rebirth2)
							.replace("%rebirth_displayname%", main.rebirthStorage.getDisplayName(rebirth2))
							.replace("%nextrebirth_name%", main.rebirthStorage.getNextRebirthName(rebirth2))
							.replace("%nextrebirth_displayname%", main.rebirthStorage.getNextRebirthDisplayName(rebirth2))
							.replace("%nextrebirth_cost%", String.valueOf(main.rebirthStorage.getNextRebirthCost(rebirth2)))
							.replace("%nextrebirth_cost_formatted%", main.prxAPI.formatBalance(main.rebirthStorage.getNextRebirthCost(rebirth2)))
                            , p);			
					    currentRebirths.add(format);
					}
					// }
				} if (currentRebirthIndex > rebirthsCollection.indexOf(rebirth)) {
					// save rebirth completed format {
					String rebirth2 = rebirth;
					if(!main.rebirthStorage.getNextRebirthName(rebirth2).equalsIgnoreCase("lastrebirth")) {
					String format = main.prxAPI.cp(rebirthCompletedFormat.replace("%rebirth_name%", rebirth2)
							.replace("%rebirth_displayname%", main.rebirthStorage.getDisplayName(rebirth2))
							.replace("%nextrebirth_name%", main.rebirthStorage.getNextRebirthName(rebirth2))
							.replace("%nextrebirth_displayname%", main.rebirthStorage.getNextRebirthDisplayName(rebirth2))
							.replace("%nextrebirth_cost%", String.valueOf(main.rebirthStorage.getNextRebirthCost(rebirth2)))
							.replace("%nextrebirth_cost_formatted%", main.prxAPI.formatBalance(main.rebirthStorage.getNextRebirthCost(rebirth2)))
                            , p);			
					completedRebirths.add(format);
					}
					// }
				} if (currentRebirthIndex < rebirthsCollection.indexOf(rebirth)) {
					// save rank other format {
					String rebirth2 = rebirth;
					if(!main.rebirthStorage.getNextRebirthName(rebirth2).equalsIgnoreCase("lastrebirth")) {
					String format = main.prxAPI.cp(rebirthOtherFormat.replace("%rebirth_name%", rebirth2)
							.replace("%rebirth_displayname%", main.rebirthStorage.getDisplayName(rebirth2))
							.replace("%nextrebirth_name%", main.rebirthStorage.getNextRebirthName(rebirth2))
							.replace("%nextrebirth_displayname%", main.rebirthStorage.getNextRebirthDisplayName(rebirth2))
							.replace("%nextrebirth_cost%", String.valueOf(main.rebirthStorage.getNextRebirthCost(rebirth2)))
							.replace("%nextrebirth_cost_formatted%", main.prxAPI.formatBalance(main.rebirthStorage.getNextRebirthCost(rebirth2)))
                            , p);			
					otherRebirths.add(format);
					}
					// }
				}
			}
			nonPagedRebirths.clear();
            completedRebirths.forEach(line -> {nonPagedRebirths.add(line);});
			currentRebirths.forEach(line -> {nonPagedRebirths.add(line);});
			otherRebirths.forEach(line -> {nonPagedRebirths.add(line);});
			if(footer == null) {
				footer = new ArrayList<String>();
			}
			footer.clear();
			for(String footer : rebirthListFormatFooter) {
				this.footer.add(main.prxAPI.c(footer.replace("%currentpage%", pageNumber)));
			}
            paginate(sender, nonPagedRebirths, Integer.valueOf(pageNumber), header, footer);

			
		}
	}
}
