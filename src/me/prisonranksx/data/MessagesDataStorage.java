package me.prisonranksx.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.utils.HolidayUtils;
import me.prisonranksx.utils.HolidayUtils.Holiday;
import net.md_5.bungee.api.ChatColor;

public class MessagesDataStorage {

	public Map<String, String> stringData;
	public Map<String, List<String>> stringListData;
	private List<String> helpMessage1;
	private List<String> helpMessage2;
	private List<String> helpMessage3;

	private PrisonRanksX main;

	public MessagesDataStorage(PrisonRanksX main) {
		this.main = main;
		this.stringData = new HashMap<>();
		this.stringListData = new HashMap<>();
		this.helpMessage1 = new ArrayList<>();
		this.helpMessage2 = new ArrayList<>();
		this.helpMessage3 = new ArrayList<>();
		this.setupHelpMessage();
	}

	public GlobalDataStorage gds() {
		return main.getGlobalStorage();
	}

	public String colorize(String textToColorize) {
		return ChatColor.translateAlternateColorCodes('&', textToColorize);
	}

	public List<String> colorize(String... textToColorize) {
		List<String> list = new ArrayList<>();
		for (String line : textToColorize) {
			list.add(colorize(line));
		}
		return list;
	}

	public String registerStringMessage(final String configNode) {
		stringData.put(configNode, gds().translateHexColorCodes(main.getConfigManager().messagesConfig.getString("Messages." + configNode)));
		return gds().translateHexColorCodes(main.getConfigManager().messagesConfig.getString("Messages." + configNode));
	}

	public List<String> registerStringListMessage(final String configNode) {
		stringListData.put(configNode, gds().translateHexColorCodes(main.getConfigManager().messagesConfig.getStringList("Messages." + configNode)));
		return gds().translateHexColorCodes(main.getConfigManager().messagesConfig.getStringList("Messages." + configNode));
	}

	@SuppressWarnings("unused")
	public void loadMessages() {
		List<String> notEnoughMoney = registerStringListMessage("notenoughmoney");
		List<String> notEnoughMoneyOther = registerStringListMessage("notenoughmoney-other");
		String noPermission = registerStringMessage("nopermission");
		String playerNotFound = registerStringMessage("playernotfound");
		String delRank = registerStringMessage("delrank");
		String setRank = registerStringMessage("setrank");
		String resetRank = registerStringMessage("resetrank");
		String createRank = registerStringMessage("createrank");
		String setNextRank = registerStringMessage("setnextrank");
		String setRankDisplay = registerStringMessage("setrankdisplay");
		String setRankCost = registerStringMessage("setrankcost");
		String delPlayerRank = registerStringMessage("delplayerrank");
		String addRankCmd = registerStringMessage("addrankcmd");
		String addRankBroadcast = registerStringMessage("addrankbroadcast");
		String addRankMsg = registerStringMessage("addrankmsg");
		String setDefaultRank = registerStringMessage("setdefaultrank");
		String setLastRank = registerStringMessage("setlastrank");
		String setPlayerPath = registerStringMessage("setplayerpath");
		String setDefaultPath = registerStringMessage("setdefaultpath");
		String setRankPath = registerStringMessage("setrankpath");
		String reload = registerStringMessage("reload");
		String save = registerStringMessage("save");
		String pathNotFound = registerStringMessage("path-notfound");
		String rankNotFound = registerStringMessage("rank-notfound");
		String prestigeNotFound = registerStringMessage("prestige-notfound");
		String rebirthNotFound = registerStringMessage("rebirth-notfound");
		String setFirstRebirth = registerStringMessage("setfirstrebirth");
		String setLastRebirth = registerStringMessage("setlastrebirth");
		String setRebirth = registerStringMessage("setrebirth");
		String resetRebirth = registerStringMessage("resetrebirth");
		String createRebirth = registerStringMessage("createrebirth");
		String setRebirthDisplay = registerStringMessage("setrebirthdisplay");
		String setRebirthCost = registerStringMessage("setrebirthcost");
		String runFromConsole = registerStringMessage("runfromconsole");
		String notCorrectWorld = registerStringMessage("notcorrectworld");
		String rankup = registerStringMessage("rankup");
		String delPrestige = registerStringMessage("delprestige");
		String setFirstPrestige = registerStringMessage("setfirstprestige");
		String setLastPrestige = registerStringMessage("setlastprestige");
		String setPrestige = registerStringMessage("setprestige");
		String resetPrestige = registerStringMessage("resetprestige");
		String createPrestige = registerStringMessage("createprestige");
		String setNextPrestige = registerStringMessage("setnextprestige");
		String setPrestigeDisplay = registerStringMessage("setprestigedisplay");
		String setPrestigeCost = registerStringMessage("setprestigecost");
		String prestige = registerStringMessage("prestige");
		String noPrestige = registerStringMessage("noprestige");
		String noRebirth = registerStringMessage("norebirth");
		String delPlayerPrestige = registerStringMessage("delplayerprestige");
		String delPlayerRebirth = registerStringMessage("delplayerrebirth");
		List<String> prestigeNotEnoughMoney = registerStringListMessage("prestige-notenoughmoney");
		List<String> lastPrestige = registerStringListMessage("lastprestige");
		List<String> lastPrestigeOther = registerStringListMessage("lastprestige-other");
		List<String> lastRank = registerStringListMessage("lastrank");
		String forceRankupMsg = registerStringMessage("forcerankup-msg");
		String forceRankupLastRank = registerStringMessage("forcerankup-lastrank");
		String forceRankupNoPermission = registerStringMessage("forcerankup-nopermission");
		String rankupNoPermission = registerStringMessage("rankup-nopermission");
		String rankupOtherNoPermission = registerStringMessage("rankup-other-nopermission");
		String ranksGuiOpen = registerStringMessage("ranksgui-open");
		String prestigesGuiOpen = registerStringMessage("prestigesgui-open");
		String autoRankupEnabled = registerStringMessage("autorankup-enabled");
		String autoRankupDisabled = registerStringMessage("autorankup-disabled");
		String autoRankupEnabledOther = registerStringMessage("autorankup-enabled-other");
		String autoRankupDisabledOther = registerStringMessage("autorankup-disabled-other");
		String autoRankupNoPermission = registerStringMessage("autorankup-nopermission");
		String autoRankupLastRank = registerStringMessage("autorankup-lastrank");
		String autoPrestigeEnabled = registerStringMessage("autoprestige-enabled");
		String autoPrestigeDisabled = registerStringMessage("autoprestige-disabled");
		String autoRebirthEnabled = registerStringMessage("autorebirth-enabled");
		String autoRebirthDisabled = registerStringMessage("autorebirth-disabled");
		String rebirth = registerStringMessage("rebirth");
		List<String> rebirthNotEnoughMoney = registerStringListMessage("rebirth-notenoughmoney");
		List<String> lastRebirth = registerStringListMessage("lastrebirth");
		String commandSpam = registerStringMessage("commandspam");
		String rankupMaxIsOn = registerStringMessage("rankupmax-is-on");
		String prestigeMaxIsOn = registerStringMessage("prestigemax-is-on");
		String rankListLastPageReached = registerStringMessage("ranklist-last-page-reached");
		String rankListInvalidPage = registerStringMessage("ranklist-invalid-page");
		String prestigeListLastPageReached = registerStringMessage("prestigelist-last-page-reached");
		String prestigeListInvalidPage = registerStringMessage("prestigelist-invalid-page");
		String rebirthListLastPageReached = registerStringMessage("rebirthlist-last-page-reached");
		String rebirthListInvalidPage = registerStringMessage("rebirthlist-invalid-page");
		String rankListConsole = registerStringMessage("ranklist-console");
		String prestigeListConsole = registerStringMessage("prestigelist-console");
		String rebirthListConsole = registerStringMessage("rebirthlist-console");
		String forceRankupNoArgs = registerStringMessage("forcerankup-noargs");
		List<String> topPrestiges = registerStringListMessage("top-prestiges");
		String topPrestigesLastPageReached = registerStringMessage("top-prestiges-last-page-reached");
		List<String> topRebirths = registerStringListMessage("top-rebirths");
		String topRebirthsLastPageReached = registerStringMessage("top-rebirths-last-page-reached");
		String rebirthFailed = registerStringMessage("rebirth-failed");
		String rankupMax = registerStringMessage("rankupmax");
		String prestigeMax = registerStringMessage("prestigemax");
	}

	public String getStringMessage(String configMessage) {
		return stringData.get(configMessage).replace("\\n", "\n");
	}

	public List<String> getStringListMessage(String configMessage) {
		return stringListData.get(configMessage);
	}

	public List<String> getHelpMessage(int id) {
		switch(id) {
		case 1:
			return helpMessage1;
		case 2:
			return helpMessage2;
		case 3:
			return helpMessage3;
		default:
			return helpMessage1;
		}
	}
	
	public void setupHelpMessage() {
		HolidayUtils holi = main.getHolidayUtils();
		if(holi.getHoliday() == Holiday.CHRISTMAS_EVE) {
			setupChristmas();
		} else if (holi.getHoliday() == Holiday.HALLOWEEN_DAY) {
			setupHalloween();
		} else if (holi.getHoliday() == Holiday.VALENTINE_DAY) {
			setupValentine();
		} else if (holi.getHoliday() == Holiday.SAINT_PATRICK_DAY) {
			setupCustom("&2", "&a", "&e");
		} else if (holi.getHoliday() == Holiday.APRIL_FOOLS_DAY) {
			setupCustom("&2", "&2", "&2");
		} else if (holi.getHoliday() == Holiday.EARTH_DAY) {
			setupCustom("&a", "&b", "&2");
		} else if (holi.getHoliday() == Holiday.MOTHERS_DAY) {
			setupCustom("&5", "&d", "&c", "&5❤");
		} else if (holi.getHoliday() == Holiday.NEW_YEAR_EVE) {
			setupCustom("&6", "&e", "&f", "&6✩", "Happy new year");
		} else if (holi.getHoliday() == Holiday.FATHERS_DAY) {
			setupCustom("&3", "&7", "&f", "&a❤");
		} else if (holi.getHoliday() == Holiday.SIBLINGS_DAY) {
			setupCustom("&a", "&b", "&f", "&c❤");
		} else {
			setupDefault();
		}
	}
	
	/**
	 * 
	 * @param firstColor brackets
	 * @param secondColor 
	 * @param thirdColor
	 */
	private void setupCustom(String firstColor, String secondColor, String thirdColor) {
		helpMessage1 = colorize("{first}[{second}PrisonRanksX{first}] {second}v%version%", 	
				"&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix", 
				"{first}&m+                                                                    +{first}", 
				"{first}/{second}prx help [page] {first}⎟ {third}show the available commands", 
				"{first}/{second}prx reload {first}⎟ {third}reload the entire plugin", 
				"{first}/{second}prx save {first}⎟ {third}save levels and players data", 
				"{first}/{second}prx createrank <name> <cost> [displayname] (-path:)[pathname]", 
				"{first}/{second}prx setrankcost <name> <cost>", 
				"{first}/{second}prx setrankdisplay <name> <displayname>", 
				"{first}/{second}prx setrankpath <name> <path>", 
				"{first}/{second}prx delrank <name>", 
				"{first}/{second}prx setdefaultrank <name>", 
				"{first}/{second}prx setlastrank <name>", 
				"{first}/{second}prx setrank <player> <rank> [pathname]", 
				"{first}/{second}prx resetrank <player> [pathname]", 
				"{first}/{second}forcerankup <player>", 
				"{first}[{second}Page{first}] {first}({third}1{first}/{third}3{first})", 
				"{first}&m+                                                                    +{first}");
		///////////////////////////////////////
		helpMessage2 = colorize("{first}[{second}PrisonRanksX{first}] {second}v%version%", 
				"&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix", 
				"{first}&m+                                                                    +{first}", 
				"{first}/{second}prx createprestige <name> <cost> [displayname]", 
				"{first}/{second}prx setprestigecost <name> <cost>", 
				"{first}/{second}prx setprestigedisplay <name> <displayname>", 
				"{first}/{second}prx delprestige <name>", 
				"{first}/{second}prx setfirstprestige <name>", 
				"{first}/{second}prx setlastprestige <name>", 
				"{first}/{second}prx setprestige <player> <prestige>", 
				"{first}/{second}prx resetprestige <player>", 
				"{first}/{second}prx delplayerprestige <player>", 
				"{first}[{second}Page{first}] {first}({third}2{first}/{third}3{first})", 
				"{first}&m+                                                                    +{first}");
		//////////////////////////////////////////
		helpMessage3 = colorize("{first}[{second}PrisonRanks{first}X{first}] {second}v%version%", 
				"&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix", 
				"{first}&m+                                                                    +{first}", 
				"{first}/{second}prx createrebirth <name> <cost> [displayname]", 
				"{first}/{second}prx setrebirthcost <name> <cost>", 
				"{first}/{second}prx setrebirthdisplay <name> <displayname>", 
				"{first}/{second}prx delrebirth <name>", 
				"{first}/{second}prx setfirstrebirth <name>", 
				"{first}/{second}prx setlastrebirth <name>", 
				"{first}/{second}prx setrebirth <player> <rebirth>", 
				"{first}/{second}prx resetrebirth <player>", 
				"{first}/{second}prx delplayerrebirth <player>", 
				"{first}[{second}Page{first}] {first}({third}3{first}/{third}3{first})", 
				"{first}&m+                                                                    +{first}");
		helpMessage1 = helpMessage1.stream().map(line -> colorize(line.replace("{first}", firstColor)
				.replace("{second}", secondColor).replace("{third}", thirdColor)))
		.collect(Collectors.toList());
		helpMessage2 = helpMessage2.stream().map(line -> colorize(line.replace("{first}", firstColor)
				.replace("{second}", secondColor).replace("{third}", thirdColor)))
		.collect(Collectors.toList());
		helpMessage3 = helpMessage3.stream().map(line -> colorize(line.replace("{first}", firstColor)
				.replace("{second}", secondColor).replace("{third}", thirdColor)))
		.collect(Collectors.toList());
	}
	
	public void setupCustom(String firstColor, String secondColor, String thirdColor, String prefix) {
		if(prefix == null) {
			setupCustom(firstColor, secondColor, thirdColor);
			return;
		}
		helpMessage1 = colorize("{prefix} {first}[{second}PrisonRanksX{first}] {prefix} {second}v%version%", 	
				"&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix", 
				"{prefix} {first}&m+                                                                    +{first} {prefix}", 
				"{prefix} {first}/{second}prx help [page] {first}⎟ {third}show the available commands", 
				"{prefix} {first}/{second}prx reload {first}⎟ {third}reload the entire plugin", 
				"{prefix} {first}/{second}prx save {first}⎟ {third}save levels and players data", 
				"{prefix} {first}/{second}prx createrank <name> <cost> [displayname] (-path:)[pathname]", 
				"{prefix} {first}/{second}prx setrankcost <name> <cost>", 
				"{prefix} {first}/{second}prx setrankdisplay <name> <displayname>", 
				"{prefix} {first}/{second}prx setrankpath <name> <path>", 
				"{prefix} {first}/{second}prx delrank <name>", 
				"{prefix} {first}/{second}prx setdefaultrank <name>", 
				"{prefix} {first}/{second}prx setlastrank <name>", 
				"{prefix} {first}/{second}prx setrank <player> <rank> [pathname]", 
				"{prefix} {first}/{second}prx resetrank <player> [pathname]", 
				"{prefix} {first}/{second}forcerankup <player>", 
				"{prefix} {first}[{second}Page{first}] {first}({third}1{first}/{third}3{first}) {prefix}", 
				"{prefix} {first}&m+                                                                    +{first} {prefix}");
		///////////////////////////////////////
		helpMessage2 = colorize("{prefix} {first}[{second}PrisonRanksX{first}] {prefix} {second}v%version%", 
				"&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix", 
				"{prefix} {first}&m+                                                                    +{first} {prefix}", 
				"{prefix} {first}/{second}prx createprestige <name> <cost> [displayname]", 
				"{prefix} {first}/{second}prx setprestigecost <name> <cost>", 
				"{prefix} {first}/{second}prx setprestigedisplay <name> <displayname>", 
				"{prefix} {first}/{second}prx delprestige <name>", 
				"{prefix} {first}/{second}prx setfirstprestige <name>", 
				"{prefix} {first}/{second}prx setlastprestige <name>", 
				"{prefix} {first}/{second}prx setprestige <player> <prestige>", 
				"{prefix} {first}/{second}prx resetprestige <player>", 
				"{prefix} {first}/{second}prx delplayerprestige <player>", 
				"{prefix} {first}[{second}Page{first}] {first}({third}2{first}/{third}3{first}) {prefix}", 
				"{prefix} {first}&m+                                                                    +{first} {prefix}");
		//////////////////////////////////////////
		helpMessage3 = colorize("{prefix} {first}[{second}PrisonRanks{first}X{first}] {prefix} {second}v%version%", 
				"&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix", 
				"{prefix} {first}&m+                                                                    +{first} {prefix}", 
				"{prefix} {first}/{second}prx createrebirth <name> <cost> [displayname]", 
				"{prefix} {first}/{second}prx setrebirthcost <name> <cost>", 
				"{prefix} {first}/{second}prx setrebirthdisplay <name> <displayname>", 
				"{prefix} {first}/{second}prx delrebirth <name>", 
				"{prefix} {first}/{second}prx setfirstrebirth <name>", 
				"{prefix} {first}/{second}prx setlastrebirth <name>", 
				"{prefix} {first}/{second}prx setrebirth <player> <rebirth>", 
				"{prefix} {first}/{second}prx resetrebirth <player>", 
				"{prefix} {first}/{second}prx delplayerrebirth <player>", 
				"{prefix} {first}[{second}Page{first}] {first}({third}3{first}/{third}3{first})", 
				"{prefix} {first}&m+                                                                    +{first}");
		helpMessage1 = helpMessage1.stream().map(line -> colorize(line.replace("{first}", firstColor)
				.replace("{second}", secondColor).replace("{third}", thirdColor).replace("{prefix}", prefix)))
		.collect(Collectors.toList());
		helpMessage2 = helpMessage2.stream().map(line -> colorize(line.replace("{first}", firstColor)
				.replace("{second}", secondColor).replace("{third}", thirdColor).replace("{prefix}", prefix)))
		.collect(Collectors.toList());
		helpMessage3 = helpMessage3.stream().map(line -> colorize(line.replace("{first}", firstColor)
				.replace("{second}", secondColor).replace("{third}", thirdColor).replace("{prefix}", prefix)))
		.collect(Collectors.toList());
	}
	
	public void setupCustom(String firstColor, String secondColor, String thirdColor, String prefix, String message) {
		if(prefix == null) {
			setupCustom(firstColor, secondColor, thirdColor);
			return;
		}
		if(message == null) {
			setupCustom(firstColor, secondColor, thirdColor, prefix);
			return;
		}
		helpMessage1 = colorize("{prefix} {first}[{second}PrisonRanksX{first}] {prefix} {second}v%version% {message}", 	
				"&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix", 
				"{prefix} {first}&m+                                                                    +{first} {prefix}", 
				"{prefix} {first}/{second}prx help [page] {first}⎟ {third}show the available commands", 
				"{prefix} {first}/{second}prx reload {first}⎟ {third}reload the entire plugin", 
				"{prefix} {first}/{second}prx save {first}⎟ {third}save levels and players data", 
				"{prefix} {first}/{second}prx createrank <name> <cost> [displayname] (-path:)[pathname]", 
				"{prefix} {first}/{second}prx setrankcost <name> <cost>", 
				"{prefix} {first}/{second}prx setrankdisplay <name> <displayname>", 
				"{prefix} {first}/{second}prx setrankpath <name> <path>", 
				"{prefix} {first}/{second}prx delrank <name>", 
				"{prefix} {first}/{second}prx setdefaultrank <name>", 
				"{prefix} {first}/{second}prx setlastrank <name>", 
				"{prefix} {first}/{second}prx setrank <player> <rank> [pathname]", 
				"{prefix} {first}/{second}prx resetrank <player> [pathname]", 
				"{prefix} {first}/{second}forcerankup <player>", 
				"{prefix} {first}[{second}Page{first}] {first}({third}1{first}/{third}3{first}) {prefix}", 
				"{prefix} {first}&m+                                                                    +{first} {prefix}");
		///////////////////////////////////////
		helpMessage2 = colorize("{prefix} {first}[{second}PrisonRanksX{first}] {prefix} {second}v%version% {message}", 
				"&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix", 
				"{prefix} {first}&m+                                                                    +{first} {prefix}", 
				"{prefix} {first}/{second}prx createprestige <name> <cost> [displayname]", 
				"{prefix} {first}/{second}prx setprestigecost <name> <cost>", 
				"{prefix} {first}/{second}prx setprestigedisplay <name> <displayname>", 
				"{prefix} {first}/{second}prx delprestige <name>", 
				"{prefix} {first}/{second}prx setfirstprestige <name>", 
				"{prefix} {first}/{second}prx setlastprestige <name>", 
				"{prefix} {first}/{second}prx setprestige <player> <prestige>", 
				"{prefix} {first}/{second}prx resetprestige <player>", 
				"{prefix} {first}/{second}prx delplayerprestige <player>", 
				"{prefix} {first}[{second}Page{first}] {first}({third}2{first}/{third}3{first}) {prefix}", 
				"{prefix} {first}&m+                                                                    +{first} {prefix}");
		//////////////////////////////////////////
		helpMessage3 = colorize("{prefix} {first}[{second}PrisonRanks{first}X{first}] {prefix} {second}v%version% {message}", 
				"&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix", 
				"{prefix} {first}&m+                                                                    +{first} {prefix}", 
				"{prefix} {first}/{second}prx createrebirth <name> <cost> [displayname]", 
				"{prefix} {first}/{second}prx setrebirthcost <name> <cost>", 
				"{prefix} {first}/{second}prx setrebirthdisplay <name> <displayname>", 
				"{prefix} {first}/{second}prx delrebirth <name>", 
				"{prefix} {first}/{second}prx setfirstrebirth <name>", 
				"{prefix} {first}/{second}prx setlastrebirth <name>", 
				"{prefix} {first}/{second}prx setrebirth <player> <rebirth>", 
				"{prefix} {first}/{second}prx resetrebirth <player>", 
				"{prefix} {first}/{second}prx delplayerrebirth <player>", 
				"{prefix} {first}[{second}Page{first}] {first}({third}3{first}/{third}3{first})", 
				"{prefix} {first}&m+                                                                    +{first}");
		helpMessage1 = helpMessage1.stream().map(line -> colorize(line.replace("{first}", firstColor)
				.replace("{second}", secondColor).replace("{third}", thirdColor).replace("{prefix}", prefix))
				.replace("{message}", message))
		.collect(Collectors.toList());
		helpMessage2 = helpMessage2.stream().map(line -> colorize(line.replace("{first}", firstColor)
				.replace("{second}", secondColor).replace("{third}", thirdColor).replace("{prefix}", prefix))
				.replace("{message}", message))
		.collect(Collectors.toList());
		helpMessage3 = helpMessage3.stream().map(line -> colorize(line.replace("{first}", firstColor)
				.replace("{second}", secondColor).replace("{third}", thirdColor).replace("{prefix}", prefix))
				.replace("{message}", message))
		.collect(Collectors.toList());
	}
	
	private void setupDefault() {
		helpMessage1 = colorize("&3[&6PrisonRanks&cX&3] &av%version%", 	
				"&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix", 
				"&c&m+                                                                    +&c", 
				"&c/&6prx help [page] &7⎟ &3show the available commands", 
				"&c/&6prx reload &7⎟ &3reload the entire plugin", 
				"&c/&6prx save &7⎟ &3save levels and players data", 
				"&c/&6prx createrank <name> <cost> [displayname] (-path:)[pathname]", 
				"&c/&6prx setrankcost <name> <cost>", 
				"&c/&6prx setrankdisplay <name> <displayname>", 
				"&c/&6prx setrankpath <name> <path>", 
				"&c/&6prx delrank <name>", 
				"&c/&6prx setdefaultrank <name>", 
				"&c/&6prx setlastrank <name>", 
				"&c/&6prx setrank <player> <rank> [pathname]", 
				"&c/&6prx resetrank <player> [pathname]", 
				"&c/&6forcerankup <player>", 
				"&3[&6Page&3] &7(&f1&7/&f3&7)", 
				"&c&m+                                                                    +&c");
		///////////////////////////////////////
		helpMessage2 = colorize("&3[&6PrisonRanks&cX&3] &av%version%", 
				"&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix", 
				"&c&m+                                                                    +&c", 
				"&c/&6prx createprestige <name> <cost> [displayname]", 
				"&c/&6prx setprestigecost <name> <cost>", 
				"&c/&6prx setprestigedisplay <name> <displayname>", 
				"&c/&6prx delprestige <name>", 
				"&c/&6prx setfirstprestige <name>", 
				"&c/&6prx setlastprestige <name>", 
				"&c/&6prx setprestige <player> <prestige>", 
				"&c/&6prx resetprestige <player>", 
				"&c/&6prx delplayerprestige <player>", 
				"&3[&6Page&3] &7(&f2&7/&f3&7)", 
				"&c&m+                                                                    +&3"); 
		//////////////////////////////////////////
		helpMessage3 = colorize("&3[&6PrisonRanks&cX&3] &av%version%", 
				"&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix", 
				"&c&m+                                                                    +&3", 
				"&c/&6prx createrebirth <name> <cost> [displayname]", 
				"&c/&6prx setrebirthcost <name> <cost>", 
				"&c/&6prx setrebirthdisplay <name> <displayname>", 
				"&c/&6prx delrebirth <name>", 
				"&c/&6prx setfirstrebirth <name>", 
				"&c/&6prx setlastrebirth <name>", 
				"&c/&6prx setrebirth <player> <rebirth>", 
				"&c/&6prx resetrebirth <player>", 
				"&c/&6prx delplayerrebirth <player>", 
				"&3[&6Page&3] &7(&f3&7/&f3&7)", 
				"&c&m+                                                                    +&3");
	}

	private void setupHalloween() {		
		helpMessage1 = colorize("&8[&6PrisonRanks&cX&8] &ev&c%version%", 
				"&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix", 
				"&8&m+                                                                    +&8", 
				"&6/&7prx help [page] &8⎟ &cshow the available commands", 
				"&6/&7prx reload &8⎟ &creload the entire plugin", 
				"&6/&7prx save &8⎟ &csave levels and players data", 
				"&6/&7prx createrank <name> <cost> [displayname] (-path:)[pathname]", 
				"&6/&7prx setrankcost <name> <cost>", 
				"&6/&7prx setrankdisplay <name> <displayname>", 
				"&6/&7prx setrankpath <name> <path>", 
				"&6/&7prx delrank <name>", 
				"&6/&7prx setdefaultrank <name>", 
				"&6/&7prx setlastrank <name>", 
				"&6/&7prx setrank <player> <rank> [pathname]", 
				"&6/&7prx resetrank <player> [pathname]", 
				"&6/&7forcerankup <player>", 
				"&8[&6Page&8] &7(&e1&7/&e3&7)", 
				"&8&m+                                                                    +&8");
		///////////////////////////////////////
		helpMessage2 = colorize("&8[&6PrisonRanks&cX&8] &ev&c%version%", 
				"&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix", 
				"&8&m+                                                                    +&8", 
				"&6/&7prx createprestige <name> <cost> [displayname]", 
				"&6/&7prx setprestigecost <name> <cost>", 
				"&6/&7prx setprestigedisplay <name> <displayname>", 
				"&6/&7prx delprestige <name>", 
				"&6/&7prx setfirstprestige <name>", 
				"&6/&7prx setlastprestige <name>", 
				"&6/&7prx setprestige <player> <prestige>", 
				"&6/&7prx resetprestige <player>", 
				"&6/&7prx delplayerprestige <player>", 
				"&8[&6Page&8] &7(&e2&7/&e3&7)", 
				"&8&m+                                                                    +&8");
		//////////////////////////////////////////
		helpMessage3 = colorize("&8[&6PrisonRanks&cX&8] &ev&c%version%", 
				"&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix", 
				"&8&m+                                                                    +&8", 
				"&6/&7prx createrebirth <name> <cost> [displayname]", 
				"&6/&7prx setrebirthcost <name> <cost>", 
				"&6/&7prx setrebirthdisplay <name> <displayname>", 
				"&6/&7prx delrebirth <name>", 
				"&6/&7prx setfirstrebirth <name>", 
				"&6/&7prx setlastrebirth <name>", 
				"&6/&7prx setrebirth <player> <rebirth>", 
				"&6/&7prx resetrebirth <player>", 
				"&6/&7prx delplayerrebirth <player>", 
				"&8[&6Page&8] &7(&e3&7/&e3&7)", 
				"&8&m+                                                                    +&8");
	}

	private void setupChristmas() {
		helpMessage1 = colorize("&f☃ &aP&cr&ai&cs&ao&cn&aR&ca&an&ck&as&cX &f☃ &cv&a%version%", 
				"&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix", 
				"&c[&f&l❄&a]&2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &a[&f&l❄&c]", 
				"&c/&aprx help [page] &f⎟ &2show the available commands", 
				"&c/&aprx reload &f⎟ &2reload the entire plugin", 
				"&c/&aprx save &f⎟ &2save levels and players data", 
				"&c/&aprx createrank <name> <cost> [displayname] (-path:)[pathname]", 
				"&c/&aprx setrankcost <name> <cost>", 
				"&c/&aprx setrankdisplay <name> <displayname>", 
				"&c/&aprx setrankpath <name> <path>", 
				"&c/&aprx delrank <name>", 
				"&c/&aprx setdefaultrank <name>", 
				"&c/&aprx setlastrank <name>", 
				"&c/&aprx setrank <player> <rank> [pathname]", 
				"&c/&aprx resetrank <player> [pathname]", 
				"&c/&aforcerankup <player>", 
				"&f☃ &cP&aa&cg&ae &f☃ &c(&a1&c/&a3&c)", 
				"&c[&f&l❄&a]&2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &a[&f&l❄&c]"); 
		///////////////////////////////////////
		helpMessage2 = colorize("&f☃ &aP&cr&ai&cs&ao&cn&aR&ca&an&ck&as&cX &f☃ &cv&a%version%", 
				"&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix", 
				"&c[&f&l❄&a]&2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &a[&f&l❄&c]", 
				"&c/&aprx createprestige <name> <cost> [displayname]", 
				"&c/&aprx setprestigecost <name> <cost>", 
				"&c/&aprx setprestigedisplay <name> <displayname>", 
				"&c/&aprx delprestige <name>", 
				"&c/&aprx setfirstprestige <name>", 
				"&c/&aprx setlastprestige <name>", 
				"&c/&aprx setprestige <player> <prestige>", 
				"&c/&aprx resetprestige <player>", 
				"&c/&aprx delplayerprestige <player>", 
				"&f☃ &cP&aa&cg&ae &f☃ &c(&a2&c/&a3&c)", 
				"&c[&f&l❄&a]&2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &a[&f&l❄&c]");
		//////////////////////////////////////////
		helpMessage3 = colorize("&f☃ &aP&cr&ai&cs&ao&cn&aR&ca&an&ck&as&cX &f☃ &cv&a%version%", 
				"&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix", 
				"&c[&f&l❄&a]&2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &a[&f&l❄&c]", 
				"&c/&aprx createrebirth <name> <cost> [displayname]", 
				"&c/&aprx setrebirthcost <name> <cost>", 
				"&c/&aprx setrebirthdisplay <name> <displayname>", 
				"&c/&aprx delrebirth <name>", 
				"&c/&aprx setfirstrebirth <name>", 
				"&c/&aprx setlastrebirth <name>", 
				"&c/&aprx setrebirth <player> <rebirth>", 
				"&c/&aprx resetrebirth <player>", 
				"&c/&aprx delplayerrebirth <player>", 
				"&f☃ &cP&aa&cg&ae &f☃ &c(&a3&c/&a3&c)", 
				"&c[&f&l❄&a]&2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &a[&f&l❄&c]"); 
	}

	private void setupValentine() {
		helpMessage1 = colorize("&4❤ &c&n&lPrisonRanksX&r &4❤ &7v&4%version%", 
				"&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix", 
				"&c&l[❣]&4&m                                          &c&l[❣]", 
				"&4/&cprx help [page] &f⎟ &dshow the available commands", 
				"&4/&cprx reload &f⎟ &dreload the entire plugin", 
				"&4/&cprx save &f⎟ &dsave levels and players data", 
				"&4/&cprx createrank <name> <cost> [displayname] (-path:)[pathname]", 
				"&4/&cprx setrankcost <name> <cost>", 
				"&4/&cprx setrankdisplay <name> <displayname>", 
				"&4/&cprx setrankpath <name> <path>", 
				"&4/&cprx delrank <name>", 
				"&4/&cprx setdefaultrank <name>", 
				"&4/&cprx setlastrank <name>", 
				"&4/&cprx setrank <player> <rank> [pathname]", 
				"&4/&cprx resetrank <player> [pathname]", 
				"&4/&cforcerankup <player>", 
				"&4❤ &c&n&lPage&r &4❤ &7(&c1&4/&c3&7)", 
				"&c&l[❣]&4&m                                          &c&l[❣]");
		///////////////////////////////////////
		helpMessage2 = colorize("&4❤ &c&n&lPrisonRanksX&r &4❤ &7v&4%version%", 
				"&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix", 
				"&c&l[❣]&4&m                                          &c&l[❣]", 
				"&4/&cprx createprestige <name> <cost> [displayname]", 
				"&4/&cprx setprestigecost <name> <cost>", 
				"&4/&cprx setprestigedisplay <name> <displayname>", 
				"&4/&cprx delprestige <name>", 
				"&4/&cprx setfirstprestige <name>", 
				"&4/&cprx setlastprestige <name>", 
				"&4/&cprx setprestige <player> <prestige>", 
				"&4/&cprx resetprestige <player>", 
				"&4/&cprx delplayerprestige <player>", 
				"&4❤ &c&n&lPage&r &4❤ &7(&c2&4/&c3&7)", 
				"&c&l[❣]&4&m                                          &c&l[❣]"); 
		//////////////////////////////////////////
		helpMessage3 = colorize("&4❤ &c&n&lPrisonRanksX&r &4❤ &7v&4%version%" ,
				"&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix", 
				"&c&l[❣]&4&m                                          &c&l[❣]", 
				"&4/&cprx createrebirth <name> <cost> [displayname]", 
				"&4/&cprx setrebirthcost <name> <cost>", 
				"&4/&cprx setrebirthdisplay <name> <displayname>", 
				"&4/&cprx delrebirth <name>", 
				"&4/&cprx setfirstrebirth <name>", 
				"&4/&cprx setlastrebirth <name>", 
				"&4/&cprx setrebirth <player> <rebirth>", 
				"&4/&cprx resetrebirth <player>", 
				"&4/&cprx delplayerrebirth <player>", 
				"&4❤ &c&n&lPage&r &4❤ &7(&c3&4/&c3&7)", 
				"&c&l[❣]&4&m                                          &c&l[❣]");
	}

}
