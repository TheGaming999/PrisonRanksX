package me.prisonranksx.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import me.prisonranksx.PrisonRanksX;

public class TopRebirthsCommand extends BukkitCommand {

	private int rebirthPerPage;
	private String lastPageReached;
	private List<String> topRebirths;
	private List<String> leaderboardLines;
	private List<String> header;
	private List<String> footer;
	
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	
	public TopRebirthsCommand(String commandName) {
		super(commandName);
		this.setDescription(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".description", "top rebirth leaderboard")));
		this.setUsage(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".usage", "/toprebirths")));
		this.setPermission(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".permission", "prisonranksx.toprebirths"));
		this.setPermissionMessage(main.getString(main.getConfigManager().commandsConfig.getString("commands." + commandName + ".permission-message", "&cYou don't have permission to execute this command.")));
		this.setAliases(main.getConfigManager().commandsConfig.getStringList("commands." + commandName + ".aliases"));
	}

	public String getName(int position) {
		return String.valueOf(main.lbm.getPlayerNameFromPositionRebirth(position, "none"));
	}
	
	public String getValue(int position) {
		return String.valueOf(main.lbm.getPlayerRebirthFromPosition(position, "none"));
	}
	
	public void load() {
		rebirthPerPage = 10;
		lastPageReached = main.prxAPI.g("top-rebirths-last-page-reached");
		topRebirths = main.prxAPI.h("top-rebirths");
		leaderboardLines = new ArrayList<>();
		header = new ArrayList<>();
		footer = new ArrayList<>();
		topRebirths.forEach(line -> {
			if(line.contains("%name") && line.contains("%value")) {
				leaderboardLines.add(line);
			} else if (line.startsWith("[header]")) {
				header.add(line.substring(8));
			} else if (line.startsWith("[footer]")) {
				footer.add(line.substring(8));
			}
		});
        rebirthPerPage = leaderboardLines.size();
	}
	
	/**
    *
    * @param sender The sender to send the list to
    * @param list The list to paginate
    * @param page The page number to display.
    * @param countAll The count of all available entries 
    */
  public void paginate(CommandSender sender, List<String> list, int page, List<String> header, List<String> footer, int size)
  {
	  header = main.prxAPI.cl(header);
	  footer = main.prxAPI.cl(footer);
      int totalPageCount = 1;
      int cpos = (rebirthPerPage * (page - 1));
      if((size % rebirthPerPage) == 0)
      {
        if(size > 0)
        {
            totalPageCount = size / rebirthPerPage;
        }     
      }
      else
      {
        totalPageCount = (size / rebirthPerPage) + 1;
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
              if ((((page * rebirthPerPage) + i + 1) == k) && (k != ((page * rebirthPerPage) + rebirthPerPage + 1)))
              {
                  i++;
                  sender.sendMessage(main.prxAPI.c(entry)
                		  .replace("%name1%", getName(1 + cpos)).replace("%value1%", getValue(1 + cpos))
                		  .replace("%name2%", getName(2 + cpos)).replace("%value2%", getValue(2 + cpos))
                		  .replace("%name3%", getName(3 + cpos)).replace("%value3%", getValue(3 + cpos))
                		  .replace("%name4%", getName(4 + cpos)).replace("%value4%", getValue(4 + cpos))
                		  .replace("%name5%", getName(5 + cpos)).replace("%value5%", getValue(5 + cpos))
                		  .replace("%name6%", getName(6 + cpos)).replace("%value6%", getValue(6 + cpos))
                		  .replace("%name7%", getName(7 + cpos)).replace("%value7%", getValue(7 + cpos))
                		  .replace("%name8%", getName(8 + cpos)).replace("%value8%", getValue(8 + cpos))
                		  .replace("%name9%", getName(9 + cpos)).replace("%value9%", getValue(9 + cpos))
                		  .replace("%name10%", getName(10 + cpos)).replace("%value10%", getValue(10 + cpos))
                		  );
              }
            }
        }
 //endline
           if(footer != null) {
	         	  for(String line : footer) {
	         		  sender.sendMessage(line.replace("%currentpage%", String.valueOf(page + 1)).replace("%totalpages%", String.valueOf(totalPageCount)));
	         	  }
	            }
      }
      else
      {
        sender.sendMessage(main.prxAPI.c(lastPageReached.replace("%page%", String.valueOf(totalPageCount))));
      }
  }
	
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(!sender.hasPermission(this.getPermission())) {
			sender.sendMessage(this.getPermissionMessage());
			return true;
		}
		if(args.length == 0) {
            this.paginate(sender, leaderboardLines, 1, header, footer, main.lbm.getRebirthLeaderboard().size());		
		} else if (args.length == 1) {
			this.paginate(sender, leaderboardLines, Integer.valueOf(args[0]), header, footer, main.lbm.getRebirthLeaderboard().size());
		}
		return true;
	}



}

	

