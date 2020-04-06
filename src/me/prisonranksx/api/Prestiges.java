package me.prisonranksx.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;

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
			prestigesCollection = new ArrayList<>();
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
		if(!enablePages || pageNumber == null) {
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
				prestigeListFormat.forEach(format -> sender.sendMessage(main.prxAPI.c(format)));
				return;
			}
			Player p = (Player)sender;
			String prestigeName = main.prxAPI.getPlayerPrestige(p);
			String rebirth = main.prxAPI.getPlayerRebirth(p);
			List<String> newPrestigesCollection = main.prestigeStorage.getPrestigesCollection();
			if(prestigesCollection.isEmpty()) {
                prestigesCollection = newPrestigesCollection;
			}
			
			if(prestigesCollection.size() != newPrestigesCollection.size()) {
				prestigesCollection = newPrestigesCollection;
			}
			Integer varIndex = prestigeListFormat.indexOf(String.valueOf("[prestigeslist]"));
			// header and footer setup {
			if(prestigeListFormatHeader.isEmpty() && prestigeListFormatFooter.isEmpty() && prestigeListFormat.size() > 1) {
			  for(String line : prestigeListFormat) {
				  if(varIndex > prestigeListFormat.indexOf(line)) {
					  prestigeListFormatHeader.add(line);
				  } if (varIndex < prestigeListFormat.indexOf(line)) {
					  prestigeListFormatFooter.add(line);
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
			int currentPrestigeIndex = prestigesCollection.indexOf(prestigeName);
			for(String prestige : prestigesCollection) {
				if(currentPrestigeIndex == prestigesCollection.indexOf(prestige)) {
					// save rank current format {
					String prestige2 = prestige;
					if(!main.prestigeStorage.getNextPrestigeName(prestige2).equalsIgnoreCase("lastprestige")) {
					String format = main.prxAPI.cp(prestigeCurrentFormat.replace("%prestige_name%", prestige2)
							.replace("%prestige_displayname%", main.prestigeStorage.getDisplayName(prestige2))
							.replace("%nextprestige_name%", main.prestigeStorage.getDisplayName(prestige2))
							.replace("%nextprestige_displayname%", main.prestigeStorage.getNextPrestigeDisplayName(prestige2))
							.replace("%nextprestige_cost%", String.valueOf(main.prxAPI.getIncreasedPrestigeCost(rebirth ,prestige2)))
							.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(prestige2))))
                            , p);			
					    currentPrestiges.add(format);
					}
					// }
				} if (currentPrestigeIndex > prestigesCollection.indexOf(prestige)) {
					// save rank completed format {
					String prestige2 = prestige;
					if(!main.prestigeStorage.getNextPrestigeName(prestige2).equalsIgnoreCase("lastprestige")) {
					String format = main.prxAPI.cp(prestigeCompletedFormat.replace("%prestige_name%", prestige2)
							.replace("%prestige_displayname%", main.prestigeStorage.getDisplayName(prestige2))
							.replace("%nextprestige_name%", main.prestigeStorage.getNextPrestigeName(prestige2))
							.replace("%nextprestige_displayname%", main.prestigeStorage.getNextPrestigeDisplayName(prestige2))
							.replace("%nextprestige_cost%", String.valueOf(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(prestige2))))
							.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(prestige2))))
                            , p);			
					completedPrestiges.add(format);
					}
					// }
				} if (currentPrestigeIndex < prestigesCollection.indexOf(prestige)) {
					// save rank other format {
					String prestige2 = prestige;
					if(!main.prestigeStorage.getNextPrestigeName(prestige2).equalsIgnoreCase("lastprestige")) {
					String format = main.prxAPI.cp(prestigeOtherFormat.replace("%prestige_name%", prestige2)
							.replace("%prestige_displayname%", main.prestigeStorage.getDisplayName(prestige2))
							.replace("%nextprestige_name%", main.prestigeStorage.getNextPrestigeName(prestige2))
							.replace("%nextprestige_displayname%", main.prestigeStorage.getNextPrestigeDisplayName(prestige2))
							.replace("%nextprestige_cost%", String.valueOf(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(prestige2))))
							.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(prestige2))))
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
		if(enablePages) {
			if(isCustomList) {
				this.paginate(sender, prestigeWithPagesListFormat, Integer.parseInt(pageNumber), null, null);
				return;
			}
			Player p = (Player)sender;
            String prestigeName = main.prxAPI.getPlayerPrestige(p);
            String rebirth = main.prxAPI.getPlayerRebirth(p);
			List<String> newPrestigesCollection = main.prestigeStorage.getPrestigesCollection();
			if(prestigesCollection.isEmpty()) {
                prestigesCollection = newPrestigesCollection;
			}
			
			if(prestigesCollection.size() != newPrestigesCollection.size()) {
				prestigesCollection = newPrestigesCollection;
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
			// }
			// send header {
			if(header == null) {
				header = new ArrayList<String>();
			}
			header.clear();
			for(String header : prestigeListFormatHeader) {
		       this.header.add(main.prxAPI.c(header.replace("%currentpage%", pageNumber)));
			}
			// }
			// send prestiges list {
		    currentPrestiges.clear();
			completedPrestiges.clear();
			otherPrestiges.clear();
			int currentPrestigeIndex = prestigesCollection.indexOf(prestigeName);
			for(String prestige : prestigesCollection) {
				if(currentPrestigeIndex == prestigesCollection.indexOf(prestige)) {
					// save prestige current format {
					String prestige2 = prestige;
					if(!main.prestigeStorage.getNextPrestigeName(prestige2).equalsIgnoreCase("lastprestige")) {
					String format = main.prxAPI.cp(prestigeCurrentFormat.replace("%prestige_name%", prestige2)
							.replace("%prestige_displayname%", main.prestigeStorage.getDisplayName(prestige2))
							.replace("%nextprestige_name%", main.prestigeStorage.getNextPrestigeName(prestige2))
							.replace("%nextprestige_displayname%", main.prestigeStorage.getNextPrestigeDisplayName(prestige2))
							.replace("%nextprestige_cost%", String.valueOf(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(prestige2))))
							.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(prestige2))))
                            , p);			
					    currentPrestiges.add(format);
					}
					// }
				} if (currentPrestigeIndex > prestigesCollection.indexOf(prestige)) {
					// save prestige completed format {
					String prestige2 = prestige;
					if(!main.prestigeStorage.getNextPrestigeName(prestige2).equalsIgnoreCase("lastprestige")) {
					String format = main.prxAPI.cp(prestigeCompletedFormat.replace("%prestige_name%", prestige2)
							.replace("%prestige_displayname%", main.prestigeStorage.getDisplayName(prestige2))
							.replace("%nextprestige_name%", main.prestigeStorage.getNextPrestigeName(prestige2))
							.replace("%nextprestige_displayname%", main.prestigeStorage.getNextPrestigeDisplayName(prestige2))
							.replace("%nextprestige_cost%", String.valueOf(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(prestige2))))
							.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(prestige2))))
                            , p);			
					completedPrestiges.add(format);
					}
					// }
				} if (currentPrestigeIndex < prestigesCollection.indexOf(prestige)) {
					// save rank other format {
					String prestige2 = prestige;
					if(!main.prestigeStorage.getNextPrestigeName(prestige2).equalsIgnoreCase("lastprestige")) {
					String format = main.prxAPI.cp(prestigeOtherFormat.replace("%prestige_name%", prestige2)
							.replace("%prestige_displayname%", main.prestigeStorage.getDisplayName(prestige2))
							.replace("%nextprestige_name%", main.prestigeStorage.getNextPrestigeName(prestige2))
							.replace("%nextprestige_displayname%", main.prestigeStorage.getNextPrestigeDisplayName(prestige2))
							.replace("%nextprestige_cost%", String.valueOf(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(prestige2))))
							.replace("%nextprestige_cost_formatted%", main.prxAPI.formatBalance(main.prxAPI.getIncreasedPrestigeCost(rebirth, main.prxAPI.getPrestigeCost(prestige2))))
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
				this.footer.add(main.prxAPI.c(footer.replace("%currentpage%", pageNumber)));
			}
            paginate(sender, nonPagedPrestiges, Integer.valueOf(pageNumber), header, footer);

			
		}
	}
}
