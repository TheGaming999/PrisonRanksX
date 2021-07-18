package me.prisonranksx.hooks;

import org.bukkit.OfflinePlayer;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.api.PRXAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;

public class MVdWPapiHook {
	
    private PrisonRanksX main;
    private PRXAPI prxAPI;
    
	public MVdWPapiHook(PrisonRanksX main) {
		super();
		this.main = main;
		prxAPI = this.main.prxAPI;
	}
	
	public void registerPlaceholders() {
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_currentrank_name", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(prxAPI.isLastRank(p) && main.getGlobalStorage().getBooleanData("PlaceholderAPI.currentrank-lastrank-enabled")) {
					return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.currentrank-lastrank"), e.getPlayer().getName());
				}
				return String.valueOf(prxAPI.getPlayerRank(p));
			}
	    });	
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_currentrank_displayname", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(prxAPI.isLastRank(p) && main.getGlobalStorage().getBooleanData("PlaceholderAPI.currentrank-lastrank-enabled")) {
					return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.currentrank-lastrank"), e.getPlayer().getName());
				}
				return String.valueOf(prxAPI.getPlayerRankDisplay(p));
			}
	    });	
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_rankup_percentage", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(prxAPI.getPlayerNextRank(p) == null) {
					  return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.rankup-percentage-lastrank"), e.getPlayer().getName());
				}
				if(prxAPI.isPercentSignBehind()) {
				return prxAPI.getPercentSign() + String.valueOf(prxAPI.getPlayerRankupPercentageDirect(p));
				} else {
					return String.valueOf(prxAPI.getPlayerRankupPercentageDirect(p)) + prxAPI.getPercentSign();
				}
			}
	    });	
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_next_percentage", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(prxAPI.isPercentSignBehind()) {
				return prxAPI.getPercentSign() + String.valueOf(prxAPI.getPlayerNextPercentage(p).getPercentage());
				} else {
					return String.valueOf(prxAPI.getPlayerNextPercentage(p).getPercentage()) + prxAPI.getPercentSign();
				}
			}
	    });	
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_next_percentage_decimal", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(prxAPI.isPercentSignBehind()) {
				return prxAPI.getPercentSign() + String.valueOf(prxAPI.getPlayerNextPercentageDecimal(p));
				} else {
					return String.valueOf(prxAPI.getPlayerNextPercentageDecimal(p)) + prxAPI.getPercentSign();
				}
			}
	    });	
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_next_progress", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
                return prxAPI.getPlayerNextProgress(p);
			}
	    });	
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_next_progress_double", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
                return prxAPI.getPlayerNextProgressExtended(p);
			}
	    });	
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_rankup_percentage_decimal", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(prxAPI.getPlayerNextRank(p) == null) {
					  return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.rankup-percentage-lastrank"), e.getPlayer().getName());
				}
				if(prxAPI.isPercentSignBehind()) {
				return prxAPI.getPercentSign() + String.valueOf(prxAPI.getPlayerRankupPercentageDecimalDirect(p));
				} else {
					return String.valueOf(prxAPI.getPlayerRankupPercentageDecimalDirect(p)) + prxAPI.getPercentSign();
				}
			}
	    });	
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_rankup_percentage_nolimit", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(prxAPI.getPlayerNextRank(p) == null) {
					  return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.rankup-percentage-lastrank"), e.getPlayer().getName());
				}
				if(prxAPI.isPercentSignBehind()) {
				return prxAPI.getPercentSign() + String.valueOf(prxAPI.getPlayerRankupPercentageNoLimitDirect(p));
				} else {
					return String.valueOf(prxAPI.getPlayerRankupPercentageNoLimitDirect(p)) + prxAPI.getPercentSign();
				}
			}
	    });
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_rankup_percentage_decimal_nolimit", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(prxAPI.getPlayerNextRank(p) == null) {
					  return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.rankup-percentage-lastrank"), e.getPlayer().getName());
				}
				if(prxAPI.isPercentSignBehind()) {
				return prxAPI.getPercentSign() + String.valueOf(prxAPI.getPlayerRankupPercentageDecimalNoLimitDirect(p));
				} else {
					return String.valueOf(prxAPI.getPlayerRankupPercentageDecimalNoLimitDirect(p)) + prxAPI.getPercentSign();
				}
			}
	    });
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_rankup_progress", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(prxAPI.getPlayerNextRank(p) == null) {
					  return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.rankup-progress-lastrank"), e.getPlayer().getName());
				}
				return String.valueOf(prxAPI.getPlayerRankupProgressBar(p));
			}
	    });
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_rankup_progress_double", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(prxAPI.getPlayerNextRank(p) == null) {
					  return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.rankup-progress-lastrank"), e.getPlayer().getName());
				}
				return String.valueOf(prxAPI.getPlayerRankupProgressBarExtended(p));
			}
	    });
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_rankup_name", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(prxAPI.getPlayerNextRank(p) == null) {
					return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.rankup-lastrank"), e.getPlayer().getName());
				}
				return String.valueOf(prxAPI.getPlayerNextRank(p));
			}
	    });
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_rankup_displayname", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(prxAPI.getPlayerNextRank(p) == null) {
					return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.rankup-lastrank"), e.getPlayer().getName());
				}
				return String.valueOf(prxAPI.getPlayerRankupDisplay(p));
			}
	    });
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_rankup_cost", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(prxAPI.getPlayerNextRank(p) == null) {
					return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.rankup-cost-lastrank"), e.getPlayer().getName());
				}
				if(prxAPI.isCurrencySymbolBehind()) {
				return  String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol()) + String.valueOf(prxAPI.getPlayerRankupCostWithIncreaseDirect(p));
				} else {
					return String.valueOf(prxAPI.getPlayerRankupCostWithIncreaseDirect(p)) + String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol());
				}
			}
	    });
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_rankup_cost_formatted", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(prxAPI.getPlayerNextRank(p) == null) {
					return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.rankup-cost-lastrank"), e.getPlayer().getName());
				}
				if(prxAPI.isCurrencySymbolBehind()) {
				return String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol()) + String.valueOf(prxAPI.getPluginMainClass().formatBalance(prxAPI.getPlayerRankupCostWithIncreaseDirect(p)));
				} else {
					return String.valueOf(prxAPI.getPluginMainClass().formatBalance(prxAPI.getPlayerRankupCostWithIncreaseDirect(p)))  + String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol());
				}
			}
	    });
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_money_nonformatted", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				return String.valueOf(prxAPI.getPluginMainClass().econ.getBalance(p));
			}
	    });
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_money", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				return String.valueOf(prxAPI.getPluginMainClass().formatBalance(prxAPI.getPluginMainClass().econ.getBalance(p)));
			}
	    });
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_rebirth_name", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(!prxAPI.hasRebirthed(p)) {
				 return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.rebirth-notrebirthed"), e.getPlayer().getName());
				}
				return String.valueOf(prxAPI.getPlayerRebirth(p));
			}
	    });
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_rebirth_displayname", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(!prxAPI.hasRebirthed(p)) {
			     return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.rebirth-notrebirthed"), e.getPlayer().getName());
				}
				 return String.valueOf(prxAPI.getPlayerRebirthDisplay(p));
			}
	    });
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_prestige_name", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(!prxAPI.hasPrestiged(p)) {
				 return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.prestige-notprestiged"), e.getPlayer().getName());
				}
				return String.valueOf(prxAPI.getPlayerPrestige(p));
			}
	    });
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_prestige_displayname", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(!prxAPI.hasPrestiged(p)) {
			     return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.prestige-notprestiged"), e.getPlayer().getName());
				}
				 return String.valueOf(prxAPI.getPlayerPrestigeDisplay(p));
			}
	    });
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_prestige_cost", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(!prxAPI.hasPrestiged(p)) {
					  return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.prestige-notprestiged"), e.getPlayer().getName());
					}
				if(prxAPI.isCurrencySymbolBehind()) {
				return String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol()) + String.valueOf(prxAPI.getPlayerPrestigeCost(p));
				} else {
					return String.valueOf(prxAPI.getPlayerPrestigeCost(p)) + String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol());
				}
			}
	    });
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_prestige_cost_formatted", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(!prxAPI.hasPrestiged(p)) {
					  return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.prestige-notprestiged"), e.getPlayer().getName());
					}
				if(prxAPI.isCurrencySymbolBehind()) {
				return String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol()) + String.valueOf(prxAPI.getPlayerPrestigeCostFormatted(p));
				} else {
					return String.valueOf(prxAPI.getPlayerPrestigeCostFormatted(p)) + String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol());
				}
			}
	    });
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_nextprestige_name", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(!prxAPI.hasPrestiged(p)) {
					return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("firstprestige"), e.getPlayer().getName());
				}
				if(!prxAPI.hasNextPrestige(p)) {
					  return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.prestige-lastprestige"), e.getPlayer().getName());
				}
				return String.valueOf(prxAPI.getPlayerNextPrestige(p));
			}
	    });
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_nextprestige_displayname", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(!prxAPI.hasPrestiged(p)) {
					return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("Prestiges." + prxAPI.getFirstPrestige() + ".display"), e.getPlayer().getName());
				}
				if(!prxAPI.hasNextPrestige(p)) {
					  return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.prestige-lastprestige"), e.getPlayer().getName());
				}
				return String.valueOf(prxAPI.getPlayerNextPrestigeDisplay(p));
			}
	    });
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_nextprestige_cost", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(!prxAPI.hasNextPrestige(p)) {
					  return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.prestige-lastprestige"), e.getPlayer().getName());
				}
				if(prxAPI.isCurrencySymbolBehind()) {
				return String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol()) + String.valueOf(prxAPI.getPlayerNextPrestigeCostInString(p)); 
				} else {
					return String.valueOf(prxAPI.getPlayerNextPrestigeCostInString(p) + String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol())); 
				}
			}
	    });
	    PlaceholderAPI.registerPlaceholder(main, "prisonranksx_nextprestige_cost_formatted", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(!e.isOnline()) {
					return "Offline";
				}
				OfflinePlayer p = e.getOfflinePlayer();
				if(!prxAPI.hasNextPrestige(p)) {
					  return prxAPI.getPluginMainClass().getString(main.getGlobalStorage().getStringData("PlaceholderAPI.prestige-lastprestige"), e.getPlayer().getName());
				}
				if(prxAPI.isCurrencySymbolBehind()) {
				return String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol()) + String.valueOf(prxAPI.getPlayerNextPrestigeCostFormatted(p));
				} else {
					return String.valueOf(prxAPI.getPlayerNextPrestigeCostFormatted(p)) + String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol());
				}
			}
	    });
	}
	
}
