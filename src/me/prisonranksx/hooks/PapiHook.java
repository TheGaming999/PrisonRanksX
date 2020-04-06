package me.prisonranksx.hooks;

import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.api.PRXAPI;

public class PapiHook extends PlaceholderExpansion{
private PrisonRanksX main;
PRXAPI prxAPI;
	public PapiHook(PrisonRanksX main) {
		super();
		this.main = main;
		prxAPI = main.prxAPI;
	}


    @Override
    public boolean persist(){
        return true;
    }
    
    @Override
    public boolean canRegister(){
        return true;
    }

	public String onRequest(OfflinePlayer arg0, String arg1) {
        OfflinePlayer p = arg0;
		if(arg1.equalsIgnoreCase("currentrank_name")) {
			if(prxAPI.isLastRank(p) && prxAPI.main.globalStorage.getBooleanData("PlaceholderAPI.currentrank-lastrank-enabled")) {
				return prxAPI.getPluginMainClass().getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.currentrank-lastrank"), arg0.getName());
			}
			return String.valueOf(prxAPI.getPlayerRank(p));
		}
		if(arg1.equalsIgnoreCase("currentrank_displayname")) {
			if(prxAPI.isLastRank(p) && prxAPI.main.globalStorage.getBooleanData("PlaceholderAPI.currentrank-lastrank-enabled")) {
				return prxAPI.getPluginMainClass().getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.currentrank-lastrank"), arg0.getName());
			}
			return String.valueOf(prxAPI.getPlayerRankDisplay(p));
		}
		if(arg1.equalsIgnoreCase("rankup_percentage")) {
			if(prxAPI.getPlayerNextRank(p) == null) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.rankup-percentage-lastrank"), arg0.getName());
			}
			if(prxAPI.isPercentSignBehind()) {
			return prxAPI.getPercentSign() + String.valueOf(prxAPI.getPlayerRankupPercentageDirect(p));
			} else {
				return String.valueOf(prxAPI.getPlayerRankupPercentageDirect(p)) + prxAPI.getPercentSign();
			}
		}
		if(arg1.equalsIgnoreCase("next_percentage")) {
			if(prxAPI.isPercentSignBehind()) {
				return prxAPI.getPercentSign() + String.valueOf(prxAPI.getPlayerNextPercentage(p).getPercentage());
			} else {
				return String.valueOf(prxAPI.getPlayerNextPercentage(p).getPercentage()) + prxAPI.getPercentSign();
			}
		}
		if(arg1.equalsIgnoreCase("next_percentage_decimal")) {
			if(prxAPI.isPercentSignBehind()) {
				return prxAPI.getPercentSign() + String.valueOf(prxAPI.getPlayerNextPercentageDecimal(p));
			} else {
				return String.valueOf(prxAPI.getPlayerNextPercentageDecimal(p)) + prxAPI.getPercentSign();
			}
		}
		if(arg1.equalsIgnoreCase("rankup_percentage_decimal")) {
			if(prxAPI.getPlayerNextRank(p) == null) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.rankup-percentage-lastrank"), arg0.getName());
			}
			if(prxAPI.isPercentSignBehind()) {
			return prxAPI.getPercentSign() + String.valueOf(prxAPI.getPlayerRankupPercentageDecimalDirect(p));
			} else {
				return String.valueOf(prxAPI.getPlayerRankupPercentageDecimalDirect(p)) + prxAPI.getPercentSign();
			}
		}
		if(arg1.equalsIgnoreCase("rankup_percentage_nolimit")) {
			if(prxAPI.getPlayerNextRank(p) == null) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.rankup-percentage-lastrank"), arg0.getName());
			}
			if(prxAPI.isPercentSignBehind()) {
			return prxAPI.getPercentSign() + String.valueOf(prxAPI.getPlayerRankupPercentageNoLimitDirect(p));
			} else {
				return String.valueOf(prxAPI.getPlayerRankupPercentageNoLimitDirect(p)) + prxAPI.getPercentSign();
			}
		}
		if(arg1.equalsIgnoreCase("rankup_percentage_decimal_nolimit")) {
			if(prxAPI.getPlayerNextRank(p) == null) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.rankup-percentage-lastrank"), arg0.getName());
			}
			if(prxAPI.isPercentSignBehind()) {
			return prxAPI.getPercentSign() + String.valueOf(prxAPI.getPlayerRankupPercentageDecimalNoLimitDirect(p));
			} else {
				return String.valueOf(prxAPI.getPlayerRankupPercentageDecimalNoLimitDirect(p)) + prxAPI.getPercentSign();
			}
		}
		if(arg1.equalsIgnoreCase("rankup_progress")) {
			if(prxAPI.getPlayerNextRank(p) == null) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-lastrank"), arg0.getName());
			}
			return String.valueOf(prxAPI.getPlayerRankupProgressBar(p));
		}
		if(arg1.equalsIgnoreCase("next_progress")) {
			return String.valueOf((prxAPI.getPlayerNextProgress(p)));
		}
		if(arg1.equalsIgnoreCase("next_progress_double")) {
			return String.valueOf((prxAPI.getPlayerNextProgressExtended(p)));
		}
		if(arg1.equalsIgnoreCase("rankup_progress_double") || arg1.equalsIgnoreCase("rankup_progress_doubled")) {
			if(prxAPI.getPlayerNextRank(p) == null) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.rankup-progress-lastrank"), arg0.getName());
			}
			return String.valueOf(prxAPI.getPlayerRankupProgressBarExtended(p));
		}
		if(arg1.equalsIgnoreCase("rankup_name")) {
			if(prxAPI.getPlayerNextRank(p) == null) {
				return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.rankup-lastrank"), arg0.getName());
			}
			return String.valueOf(prxAPI.getPlayerNextRank(p));
		}
		if(arg1.equalsIgnoreCase("rankup_displayname")) {
			if(prxAPI.getPlayerNextRank(p) == null) {
				return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.rankup-lastrank"), arg0.getName());
			}
			return String.valueOf(prxAPI.getPlayerRankupDisplay(p));
		}
		if((arg1.equalsIgnoreCase("rankup_cost"))) {
			if(prxAPI.getPlayerNextRank(p) == null) {
				return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.rankup-cost-lastrank"), arg0.getName());
			}
			if(prxAPI.isCurrencySymbolBehind()) {
			return  String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol()) + String.valueOf(prxAPI.getPlayerRankupCostWithIncreaseDirect(p));
			} else {
				return String.valueOf(prxAPI.getPlayerRankupCostWithIncreaseDirect(p)) + String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol());
			}
		}
		if((arg1.equalsIgnoreCase("rankup_cost_formatted"))) {
			if(prxAPI.getPlayerNextRank(p) == null) {
				return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.rankup-cost-lastrank"), arg0.getName());
			}
			if(prxAPI.isCurrencySymbolBehind()) {
			return String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol()) + String.valueOf(prxAPI.getPluginMainClass().formatBalance(prxAPI.getPlayerRankupCostWithIncreaseDirect(p)));
			} else {
				return String.valueOf(prxAPI.getPluginMainClass().formatBalance(prxAPI.getPlayerRankupCostWithIncreaseDirect(p)))  + String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol());
			}
		}
		if(arg1.equalsIgnoreCase("money_nonformatted")) {
			return String.valueOf(prxAPI.getPluginMainClass().econ.getBalance(p));
		}
		if((arg1.equalsIgnoreCase("money"))) {
			return String.valueOf(prxAPI.getPluginMainClass().formatBalance(prxAPI.getPluginMainClass().econ.getBalance(p)));
		}
		if(arg1.equalsIgnoreCase("rebirth_name")) {
			if(!prxAPI.hasRebirthed(p)) {
			  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.rebirth-notrebirthed"), arg0.getName());
			}
			return String.valueOf(prxAPI.getPlayerRebirth(p));
		}
		if(arg1.equalsIgnoreCase("rebirth_displayname")) {
			if(!prxAPI.hasRebirthed(p)) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.rebirth-notrebirthed"), arg0.getName());
				}
			return String.valueOf(prxAPI.getPlayerRebirthDisplay(p));
		}
		if(arg1.equalsIgnoreCase("prestige_name")) {
			if(!prxAPI.hasPrestiged(p)) {
			  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.prestige-notprestiged"), arg0.getName());
			}
			return String.valueOf(prxAPI.getPlayerPrestige(p));
		}
		if(arg1.equalsIgnoreCase("prestige_displayname")) {
			if(!prxAPI.hasPrestiged(p)) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.prestige-notprestiged"), arg0.getName());
				}
			return String.valueOf(prxAPI.getPlayerPrestigeDisplay(p));
		}
		if(arg1.equalsIgnoreCase("prestige_cost")) {
			if(!prxAPI.hasPrestiged(p)) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.prestige-notprestiged"), arg0.getName());
				}
			if(prxAPI.isCurrencySymbolBehind()) {
			return String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol()) + String.valueOf(prxAPI.getPlayerPrestigeCost(p));
			} else {
				return String.valueOf(prxAPI.getPlayerPrestigeCost(p)) + String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol());
			}
		}
		if(arg1.equalsIgnoreCase("prestige_cost_formatted")) {
			if(!prxAPI.hasPrestiged(p)) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.prestige-notprestiged"), arg0.getName());
				}
			if(prxAPI.isCurrencySymbolBehind()) {
			return String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol()) + String.valueOf(prxAPI.getPlayerPrestigeCostFormatted(p));
			} else {
				return String.valueOf(prxAPI.getPlayerPrestigeCostFormatted(p)) + String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol());
			}
		}
		if(arg1.equalsIgnoreCase("nextrebirth_name")) {
			if(!prxAPI.hasRebirthed(p)) {
				return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.nextrebirth-notrebirthed"), arg0.getName());
			}
			if(!prxAPI.hasNextRebirth(p)) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.rebirth-lastrebirth"), arg0.getName());
			}
			return String.valueOf(prxAPI.getPlayerNextRebirth(p));
		}
		if(arg1.equalsIgnoreCase("nextrebirth_displayname")) {
			if(!prxAPI.hasRebirthed(p)) {
				return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.nextrebirth-notrebirthed"), arg0.getName());
			}
			if(!prxAPI.hasNextRebirth(p)) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.rebirth-lastrebirth"), arg0.getName());
			}
			return String.valueOf(prxAPI.getPlayerNextRebirthDisplay(p));
		}
		if(arg1.equalsIgnoreCase("nextprestige_name")) {
			if(!prxAPI.hasPrestiged(p)) {
				return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.nextprestige-notprestiged"), arg0.getName());
			}
			if(!prxAPI.hasNextPrestige(p)) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.prestige-lastprestige"), arg0.getName());
			}
			return String.valueOf(prxAPI.getPlayerNextPrestige(p));
		}
		if(arg1.equalsIgnoreCase("nextprestige_displayname")) {
			if(!prxAPI.hasPrestiged(p)) {
				return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.nextprestige-notprestiged"), arg0.getName());
			}
			if(!prxAPI.hasNextPrestige(p)) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.prestige-lastprestige"), arg0.getName());
			}
			return String.valueOf(prxAPI.getPlayerNextPrestigeDisplay(p));
		}
		if(arg1.equalsIgnoreCase("nextprestige_cost")) {
			if(!prxAPI.hasNextPrestige(p)) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.prestige-lastprestige"), arg0.getName());
			}
			if(prxAPI.isCurrencySymbolBehind()) {
			return String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol()) + String.valueOf(prxAPI.getPlayerNextPrestigeCostInString(p)); 
			} else {
				return String.valueOf(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p) + String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol())); 
			}
		}
		if(arg1.equalsIgnoreCase("nextprestige_cost_formatted")) {
			if(!prxAPI.hasNextPrestige(p)) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.prestige-lastprestige"), arg0.getName());
			}
			if(prxAPI.isCurrencySymbolBehind()) {
			return String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol()) + String.valueOf(prxAPI.getPlayerNextPrestigeCostFormatted(p));
			} else {
				return String.valueOf(main.formatBalance(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p))) + String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol());
			}
		}
		if((arg0 == null)) {
			return null;
		}
		return null;
		
	}

	@Override
	public String getAuthor() {
		return "TheGaming999";
	}

	@Override
	public String getIdentifier() {
		return "prisonranksx";
	}

	@Override
	public String getVersion() {
		return "2.6";
	}
 
}
