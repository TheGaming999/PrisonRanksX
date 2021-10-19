package me.prisonranksx.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import me.prisonranksx.PrisonRanksX;

public class HolidayUtils {

	public enum Holiday {NONE, HALLOWEEN, CHRISTMAS, VALENTINE}
	
	
	private PrisonRanksX main;
	private List<String> helpMessage1;
	private List<String> helpMessage2;
	private List<String> helpMessage3;
	private Holiday holiday;
	private boolean isCeleberation;
	
	public HolidayUtils(PrisonRanksX main) {
		this.main = main;
		this.helpMessage1 = new ArrayList<>();
		this.helpMessage2 = new ArrayList<>();
		this.helpMessage3 = new ArrayList<>();
		this.holiday = Holiday.NONE;
		findHoliday();	
	}
	
	private String c(String textToTranslate) {
		return ChatColor.translateAlternateColorCodes('&', textToTranslate);
	}
	
	private void setupDefault() {
		helpMessage1.clear();
		helpMessage2.clear();
		helpMessage3.clear();
		helpMessage1
		.add(c("&3[&6PrisonRanksX&3] &av%version%"));
		helpMessage1
		.add(c("&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix"));
		helpMessage1
		.add(c("&c&m+                                                                    +&c"));
		helpMessage1
		.add(c("&c/&6prx help [page] &7⎟ &3show the available commands"));
		helpMessage1
		.add(c("&c/&6prx reload &7⎟ &3reload the entire plugin"));
		helpMessage1
		.add(c("&c/&6prx save &7⎟ &3save levels and players data"));
		helpMessage1
		.add(c("&c/&6prx createrank <name> <cost> [displayname] (-path:)[pathname]"));
		helpMessage1
		.add(c("&c/&6prx setrankcost <name> <cost>"));
		helpMessage1
		.add(c("&c/&6prx setrankdisplay <name> <displayname>"));
		helpMessage1
		.add(c("&c/&6prx setrankpath <name> <path>"));
		helpMessage1
		.add(c("&c/&6prx delrank <name>"));
		helpMessage1
		.add(c("&c/&6prx setdefaultrank <name>"));
		helpMessage1
		.add(c("&c/&6prx setlastrank <name>"));
		helpMessage1
		.add(c("&c/&6prx setrank <player> <rank> [pathname]"));
		helpMessage1
		.add(c("&c/&6prx resetrank <player> [pathname]"));
		helpMessage1
		.add(c("&c/&6forcerankup <player>"));
		helpMessage1
		.add(c("&3[&6Page&3] &7(&f1&7/&f3&7)"));
		helpMessage1
		.add(c("&c&m+                                                                    +&c"));
		///////////////////////////////////////
		helpMessage2
		.add(c("&3[&6PrisonRanksX&3] &av%version%"));
		helpMessage2
		.add(c("&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix"));
		helpMessage2
		.add(c("&c&m+                                                                    +&c"));
		helpMessage2
		.add(c("&c/&6prx createprestige <name> <cost> [displayname]"));
		helpMessage2
		.add(c("&c/&6prx setprestigecost <name> <cost>"));
		helpMessage2
		.add(c("&c/&6prx setprestigedisplay <name> <displayname>"));
		helpMessage2
		.add(c("&c/&6prx delprestige <name>"));
		helpMessage2
		.add(c("&c/&6prx setfirstprestige <name>"));
		helpMessage2
		.add(c("&c/&6prx setlastprestige <name>"));
		helpMessage2
		.add(c("&c/&6prx setprestige <player> <prestige>"));
		helpMessage2
		.add(c("&c/&6prx resetprestige <player>"));
		helpMessage2
		.add(c("&c/&6prx delplayerprestige <player>"));
		helpMessage2
		.add(c("&3[&6Page&3] &7(&f2&7/&f3&7)"));
		helpMessage2
		.add(c("&c&m+                                                                    +&3"));
		//////////////////////////////////////////
		helpMessage3
		.add(c("&3[&6PrisonRanksX&3] &av%version%"));
		helpMessage3
		.add(c("&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix"));
		helpMessage3
		.add(c("&c&m+                                                                    +&3"));
		helpMessage3
		.add(c("&c/&6prx createrebirth <name> <cost> [displayname]"));
		helpMessage3
		.add(c("&c/&6prx setrebirthcost <name> <cost>"));
		helpMessage3
		.add(c("&c/&6prx setrebirthdisplay <name> <displayname>"));
		helpMessage3
		.add(c("&c/&6prx delrebirth <name>"));
		helpMessage3
		.add(c("&c/&6prx setfirstrebirth <name>"));
		helpMessage3
		.add(c("&c/&6prx setlastrebirth <name>"));
		helpMessage3
		.add(c("&c/&6prx setrebirth <player> <rebirth>"));
		helpMessage3
		.add(c("&c/&6prx resetrebirth <player>"));
		helpMessage3
		.add(c("&c/&6prx delplayerrebirth <player>"));
		helpMessage3
		.add(c("&3[&6Page&3] &7(&f3&7/&f3&7)"));
		helpMessage3
		.add(c("&c&m+                                                                    +&3"));
		this.setHelpMessage1(helpMessage1);
		this.setHelpMessage2(helpMessage2);
		this.setHelpMessage3(helpMessage3);
	}
	
	private void setupHalloween() {
		helpMessage1.clear();
		helpMessage2.clear();
		helpMessage3.clear();
		helpMessage1
		.add(c("&8[&6PrisonRanks&cX&8] &ev&c%version%"));
		helpMessage1
		.add(c("&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix"));
		helpMessage1
		.add(c("&8&m+                                                                    +&8"));
		helpMessage1
		.add(c("&6/&7prx help [page] &8⎟ &cshow the available commands"));
		helpMessage1
		.add(c("&6/&7prx reload &8⎟ &creload the entire plugin"));
		helpMessage1
		.add(c("&6/&7prx save &8⎟ &csave levels and players data"));
		helpMessage1
		.add(c("&6/&7prx createrank <name> <cost> [displayname] (-path:)[pathname]"));
		helpMessage1
		.add(c("&6/&7prx setrankcost <name> <cost>"));
		helpMessage1
		.add(c("&6/&7prx setrankdisplay <name> <displayname>"));
		helpMessage1
		.add(c("&6/&7prx setrankpath <name> <path>"));
		helpMessage1
		.add(c("&6/&7prx delrank <name>"));
		helpMessage1
		.add(c("&6/&7prx setdefaultrank <name>"));
		helpMessage1
		.add(c("&6/&7prx setlastrank <name>"));
		helpMessage1
		.add(c("&6/&7prx setrank <player> <rank> [pathname]"));
		helpMessage1
		.add(c("&6/&7prx resetrank <player> [pathname]"));
		helpMessage1
		.add(c("&6/&7forcerankup <player>"));
		helpMessage1
		.add(c("&8[&6Page&8] &7(&e1&7/&e3&7)"));
		helpMessage1
		.add(c("&8&m+                                                                    +&8"));
		///////////////////////////////////////
		helpMessage2
		.add(c("&8[&6PrisonRanks&cX&8] &ev&c%version%"));
		helpMessage2
		.add(c("&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix"));
		helpMessage2
		.add(c("&8&m+                                                                    +&8"));
		helpMessage2
		.add(c("&6/&7prx createprestige <name> <cost> [displayname]"));
		helpMessage2
		.add(c("&6/&7prx setprestigecost <name> <cost>"));
		helpMessage2
		.add(c("&6/&7prx setprestigedisplay <name> <displayname>"));
		helpMessage2
		.add(c("&6/&7prx delprestige <name>"));
		helpMessage2
		.add(c("&6/&7prx setfirstprestige <name>"));
		helpMessage2
		.add(c("&6/&7prx setlastprestige <name>"));
		helpMessage2
		.add(c("&6/&7prx setprestige <player> <prestige>"));
		helpMessage2
		.add(c("&6/&7prx resetprestige <player>"));
		helpMessage2
		.add(c("&6/&7prx delplayerprestige <player>"));
		helpMessage2
		.add(c("&8[&6Page&8] &7(&e2&7/&e3&7)"));
		helpMessage2
		.add(c("&8&m+                                                                    +&8"));
		//////////////////////////////////////////
		helpMessage3
		.add(c("&8[&6PrisonRanks&cX&8] &ev&c%version%"));
		helpMessage3
		.add(c("&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix"));
		helpMessage3
		.add(c("&8&m+                                                                    +&8"));
		helpMessage3
		.add(c("&6/&7prx createrebirth <name> <cost> [displayname]"));
		helpMessage3
		.add(c("&6/&7prx setrebirthcost <name> <cost>"));
		helpMessage3
		.add(c("&6/&7prx setrebirthdisplay <name> <displayname>"));
		helpMessage3
		.add(c("&6/&7prx delrebirth <name>"));
		helpMessage3
		.add(c("&6/&7prx setfirstrebirth <name>"));
		helpMessage3
		.add(c("&6/&7prx setlastrebirth <name>"));
		helpMessage3
		.add(c("&6/&7prx setrebirth <player> <rebirth>"));
		helpMessage3
		.add(c("&6/&7prx resetrebirth <player>"));
		helpMessage3
		.add(c("&6/&7prx delplayerrebirth <player>"));
		helpMessage3
		.add(c("&8[&6Page&8] &7(&e3&7/&e3&7)"));
		helpMessage3
		.add(c("&8&m+                                                                    +&8"));
		this.setHelpMessage1(helpMessage1);
		this.setHelpMessage2(helpMessage2);
		this.setHelpMessage3(helpMessage3);
	}
	
	private void setupChristmas() {
		helpMessage1.clear();
		helpMessage2.clear();
		helpMessage3.clear();
		helpMessage1
		.add(c("&f☃ &aP&cr&ai&cs&ao&cn&aR&ca&an&ck&as&cX &f☃ &cv&a%version%"));
		helpMessage1
		.add(c("&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix"));
		helpMessage1
		.add(c("&c[&f&l❄&a]&2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &a[&f&l❄&c]"));
		helpMessage1
		.add(c("&c/&aprx help [page] &f⎟ &2show the available commands"));
		helpMessage1
		.add(c("&c/&aprx reload &f⎟ &2reload the entire plugin"));
		helpMessage1
		.add(c("&c/&aprx save &f⎟ &2save levels and players data"));
		helpMessage1
		.add(c("&c/&aprx createrank <name> <cost> [displayname] (-path:)[pathname]"));
		helpMessage1
		.add(c("&c/&aprx setrankcost <name> <cost>"));
		helpMessage1
		.add(c("&c/&aprx setrankdisplay <name> <displayname>"));
		helpMessage1
		.add(c("&c/&aprx setrankpath <name> <path>"));
		helpMessage1
		.add(c("&c/&aprx delrank <name>"));
		helpMessage1
		.add(c("&c/&aprx setdefaultrank <name>"));
		helpMessage1
		.add(c("&c/&aprx setlastrank <name>"));
		helpMessage1
		.add(c("&c/&aprx setrank <player> <rank> [pathname]"));
		helpMessage1
		.add(c("&c/&aprx resetrank <player> [pathname]"));
		helpMessage1
		.add(c("&c/&aforcerankup <player>"));
		helpMessage1
		.add(c("&f☃ &cP&aa&cg&ae &f☃ &c(&a1&c/&a3&c)"));
		helpMessage1
		.add(c("&c[&f&l❄&a]&2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &a[&f&l❄&c]"));
		///////////////////////////////////////
		helpMessage2
		.add(c("&f☃ &aP&cr&ai&cs&ao&cn&aR&ca&an&ck&as&cX &f☃ &cv&a%version%"));
		helpMessage2
		.add(c("&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix"));
		helpMessage2
		.add(c("&c[&f&l❄&a]&2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &a[&f&l❄&c]"));
		helpMessage2
		.add(c("&c/&aprx createprestige <name> <cost> [displayname]"));
		helpMessage2
		.add(c("&c/&aprx setprestigecost <name> <cost>"));
		helpMessage2
		.add(c("&c/&aprx setprestigedisplay <name> <displayname>"));
		helpMessage2
		.add(c("&c/&aprx delprestige <name>"));
		helpMessage2
		.add(c("&c/&aprx setfirstprestige <name>"));
		helpMessage2
		.add(c("&c/&aprx setlastprestige <name>"));
		helpMessage2
		.add(c("&c/&aprx setprestige <player> <prestige>"));
		helpMessage2
		.add(c("&c/&aprx resetprestige <player>"));
		helpMessage2
		.add(c("&c/&aprx delplayerprestige <player>"));
		helpMessage2
		.add(c("&f☃ &cP&aa&cg&ae &f☃ &c(&a2&c/&a3&c)"));
		helpMessage2
		.add(c("&c[&f&l❄&a]&2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &a[&f&l❄&c]"));
		//////////////////////////////////////////
		helpMessage3
		.add(c("&f☃ &aP&cr&ai&cs&ao&cn&aR&ca&an&ck&as&cX &f☃ &cv&a%version%"));
		helpMessage3
		.add(c("&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix"));
		helpMessage3
		.add(c("&c[&f&l❄&a]&2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &a[&f&l❄&c]"));
		helpMessage3
		.add(c("&c/&aprx createrebirth <name> <cost> [displayname]"));
		helpMessage3
		.add(c("&c/&aprx setrebirthcost <name> <cost>"));
		helpMessage3
		.add(c("&c/&aprx setrebirthdisplay <name> <displayname>"));
		helpMessage3
		.add(c("&c/&aprx delrebirth <name>"));
		helpMessage3
		.add(c("&c/&aprx setfirstrebirth <name>"));
		helpMessage3
		.add(c("&c/&aprx setlastrebirth <name>"));
		helpMessage3
		.add(c("&c/&aprx setrebirth <player> <rebirth>"));
		helpMessage3
		.add(c("&c/&aprx resetrebirth <player>"));
		helpMessage3
		.add(c("&c/&aprx delplayerrebirth <player>"));
		helpMessage3
		.add(c("&f☃ &cP&aa&cg&ae &f☃ &c(&a3&c/&a3&c)"));
		helpMessage3
		.add(c("&c[&f&l❄&a]&2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &4&m &2&m &a[&f&l❄&c]"));
		this.setHelpMessage1(helpMessage1);
		this.setHelpMessage2(helpMessage2);
		this.setHelpMessage3(helpMessage3);
	}
	
	private void setupValentine() {
		helpMessage1.clear();
		helpMessage2.clear();
		helpMessage3.clear();
		helpMessage1
		.add(c("&4❤ &c&n&lPrisonRanksX&r &4❤ &7v&4%version%"));
		helpMessage1
		.add(c("&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix"));
		helpMessage1
		.add(c("&c&l[❣]&4&m                                          &c&l[❣]"));
		helpMessage1
		.add(c("&4/&cprx help [page] &f⎟ &dshow the available commands"));
		helpMessage1
		.add(c("&4/&cprx reload &f⎟ &dreload the entire plugin"));
		helpMessage1
		.add(c("&4/&cprx save &f⎟ &dsave levels and players data"));
		helpMessage1
		.add(c("&4/&cprx createrank <name> <cost> [displayname] (-path:)[pathname]"));
		helpMessage1
		.add(c("&4/&cprx setrankcost <name> <cost>"));
		helpMessage1
		.add(c("&4/&cprx setrankdisplay <name> <displayname>"));
		helpMessage1
		.add(c("&4/&cprx setrankpath <name> <path>"));
		helpMessage1
		.add(c("&4/&cprx delrank <name>"));
		helpMessage1
		.add(c("&4/&cprx setdefaultrank <name>"));
		helpMessage1
		.add(c("&4/&cprx setlastrank <name>"));
		helpMessage1
		.add(c("&4/&cprx setrank <player> <rank> [pathname]"));
		helpMessage1
		.add(c("&4/&cprx resetrank <player> [pathname]"));
		helpMessage1
		.add(c("&4/&cforcerankup <player>"));
		helpMessage1
		.add(c("&4❤ &c&n&lPage&r &4❤ &7(&c1&4/&c3&7)"));
		helpMessage1
		.add(c("&c&l[❣]&4&m                                          &c&l[❣]"));
		///////////////////////////////////////
		helpMessage2
		.add(c("&4❤ &c&n&lPrisonRanksX&r &4❤ &7v&4%version%"));
		helpMessage2
		.add(c("&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix"));
		helpMessage2
		.add(c("&c&l[❣]&4&m                                          &c&l[❣]"));
		helpMessage2
		.add(c("&4/&cprx createprestige <name> <cost> [displayname]"));
		helpMessage2
		.add(c("&4/&cprx setprestigecost <name> <cost>"));
		helpMessage2
		.add(c("&4/&cprx setprestigedisplay <name> <displayname>"));
		helpMessage2
		.add(c("&4/&cprx delprestige <name>"));
		helpMessage2
		.add(c("&4/&cprx setfirstprestige <name>"));
		helpMessage2
		.add(c("&4/&cprx setlastprestige <name>"));
		helpMessage2
		.add(c("&4/&cprx setprestige <player> <prestige>"));
		helpMessage2
		.add(c("&4/&cprx resetprestige <player>"));
		helpMessage2
		.add(c("&4/&cprx delplayerprestige <player>"));
		helpMessage2
		.add(c("&4❤ &c&n&lPage&r &4❤ &7(&c2&4/&c3&7)"));
		helpMessage2
		.add(c("&c&l[❣]&4&m                                          &c&l[❣]"));
		//////////////////////////////////////////
		helpMessage3
		.add(c("&4❤ &c&n&lPrisonRanksX&r &4❤ &7v&4%version%"));
		helpMessage3
		.add(c("&7<> = required &8⎟ &7[] = optional &8⎟ &7() = optional prefix"));
		helpMessage3
		.add(c("&c&l[❣]&4&m                                          &c&l[❣]"));
		helpMessage3
		.add(c("&4/&cprx createrebirth <name> <cost> [displayname]"));
		helpMessage3
		.add(c("&4/&cprx setrebirthcost <name> <cost>"));
		helpMessage3
		.add(c("&4/&cprx setrebirthdisplay <name> <displayname>"));
		helpMessage3
		.add(c("&4/&cprx delrebirth <name>"));
		helpMessage3
		.add(c("&4/&cprx setfirstrebirth <name>"));
		helpMessage3
		.add(c("&4/&cprx setlastrebirth <name>"));
		helpMessage3
		.add(c("&4/&cprx setrebirth <player> <rebirth>"));
		helpMessage3
		.add(c("&4/&cprx resetrebirth <player>"));
		helpMessage3
		.add(c("&4/&cprx delplayerrebirth <player>"));
		helpMessage3
		.add(c("&4❤ &c&n&lPage&r &4❤ &7(&c3&4/&c3&7)"));
		helpMessage3
		.add(c("&c&l[❣]&4&m                                          &c&l[❣]"));
		this.setHelpMessage1(helpMessage1);
		this.setHelpMessage2(helpMessage2);
		this.setHelpMessage3(helpMessage3);
	}
	
	private void findHoliday() {
		int month = java.time.LocalDate.now().getMonthValue();
		int day = java.time.LocalDate.now().getDayOfMonth();
        if(month == 10) {
        	if(day > 15) {
        		// is halloween
                this.holiday = Holiday.HALLOWEEN;
                this.isCeleberation = true;
        	}
        } else if (month == 11) {
        	if(day < 8) {
        		// is halloween
        		this.holiday = Holiday.HALLOWEEN;
        		this.isCeleberation = true;
        	}
        } else if (month == 2) {
        	if(day > 9 && day < 25) {
        		// is valentine
        		this.holiday = Holiday.VALENTINE;
        		this.isCeleberation = false;
        	}
        } else if (month == 12) {
        	if(day > 18) {
        		// is christmas
        		this.holiday = Holiday.CHRISTMAS;
        		this.isCeleberation = true;
        	}
        } else if (month == 1) {
        	if(day < 1) {
        		// is christmas
        		this.holiday = Holiday.CHRISTMAS;
        		this.isCeleberation = true;
        	}
        } else {
        	this.holiday = Holiday.NONE;
        	this.isCeleberation = false;
        }
	}
	
	public void setup() {
        if(this.getHoliday() == Holiday.HALLOWEEN) {
        	setupHalloween();
        } else if (this.getHoliday() == Holiday.CHRISTMAS) {
        	setupChristmas();
        } else if (this.getHoliday() == Holiday.VALENTINE) {
        	setupValentine();
        } else if (this.getHoliday() == Holiday.NONE) {
        	setupDefault();
        } else {
        	setupDefault();
        }
	}

	public List<String> getHelpMessage1() {
		return helpMessage1;
	}

	public void setHelpMessage1(List<String> helpMessage1) {
		this.helpMessage1 = helpMessage1;
	}

	public List<String> getHelpMessage2() {
		return helpMessage2;
	}

	public void setHelpMessage2(List<String> helpMessage2) {
		this.helpMessage2 = helpMessage2;
	}

	public List<String> getHelpMessage3() {
		return helpMessage3;
	}

	public void setHelpMessage3(List<String> helpMessage3) {
		this.helpMessage3 = helpMessage3;
	}

	public Holiday getHoliday() {
		return holiday;
	}

	public void setHoliday(Holiday holiday) {
		this.holiday = holiday;
	}

	public boolean isCeleberation() {
		return isCeleberation;
	}

	public void setCeleberation(boolean isCeleberation) {
		this.isCeleberation = isCeleberation;
	}
	
}
