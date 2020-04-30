package me.prisonranksx.gui;

import org.bukkit.inventory.ItemFlag;

public class ItemFlagReader {
	private static String parseItemFlag(String string) {
    	if(string.equalsIgnoreCase("noenchant")) {
    		return "HIDE_ENCHANTS";
    	} else if (string.equalsIgnoreCase("hide_enchantments")) {
    		return "HIDE_ENCHANTS";
    	} else if (string.equalsIgnoreCase("hideenchantments")) {
    		return "HIDE_ENCHANTS";
    	} else if (string.equalsIgnoreCase("hideenchant")) {
    		return "HIDE_ENCHANTS";
    	} else if (string.equalsIgnoreCase("noenchants")) {
    		return "HIDE_ENCHANTS";
    	} else if (string.equalsIgnoreCase("hideenchants")) {
    		return "HIDE_ENCHANTS";
    	} else if (string.equalsIgnoreCase("noattribute")) {
    		return "HIDE_ATTRIBUTES";
    	} else if (string.equalsIgnoreCase("noattributes")) {
    		return "HIDE_ATTRIBUTES";
    	} else if (string.equalsIgnoreCase("hideattribute")) {
    		return "HIDE_ATTRIBUTES";
    	} else if (string.equalsIgnoreCase("hideattributes")) {
    		return "HIDE_ATTRIBUTES";
    	} else if (string.equalsIgnoreCase("hideunbreakable")) {
    		return "HIDE_UNBREAKABLE";
    	} else if (string.equalsIgnoreCase("nounbreakable")) {
    		return "HIDE_UNBREAKABLE";
    	} else if (string.equalsIgnoreCase("hidelore")) {
    		return "HIDE_ATTRIBUTES";
    	} else if (string.equalsIgnoreCase("nolore")) {
    		return "HIDE_ATTRIBUTES";
    	}
    	return string.toUpperCase();
    }
	public static String matchStringItemFlag(String string) {
		return parseItemFlag(string);
	}
	public static ItemFlag matchItemFlag(String string) {
		return ItemFlag.valueOf(parseItemFlag(string));
	}
}
