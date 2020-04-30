package me.prisonranksx.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;



public class PrxTabComplete implements TabCompleter{
	private static List<String> COMMANDS = new ArrayList<>();
	@Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		if(args.length == 1) {
        //create new array
		COMMANDS.add("setrank");
		COMMANDS.add("resetrank");
		COMMANDS.add("setprestige");
		COMMANDS.add("setrebirth");
		COMMANDS.add("createrebirth");
		COMMANDS.add("setrebirthdisplay");
		COMMANDS.add("createrank");
		COMMANDS.add("setrankcost");
		COMMANDS.add("setrankdisplay");
		COMMANDS.add("delrank");
		COMMANDS.add("setnextrank");
		COMMANDS.add("createprestige");
		COMMANDS.add("setprestigecost");
		COMMANDS.add("getplaceholders");
		COMMANDS.add("delplayerprestige");
		COMMANDS.add("delplayerrank");
		COMMANDS.add("setdefaultrank");
		COMMANDS.add("setlastrank");
		COMMANDS.add("setfirstprestige");
		COMMANDS.add("setlastprestige");
        final List<String> completions = new ArrayList<>();
        //copy matches of first argument from list (ex: if first arg is 'm' will return just 'minecraft')
        StringUtil.copyPartialMatches(args[0], COMMANDS, completions);
        //sort the list
        Collections.sort(completions);
        return completions;
		} else {
			return null;
		}
    }
}

