package me.prisonranksx.hooks;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.api.PRXAPI;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.leaderboard.LeaderboardManager;
import me.prisonranksx.utils.CollectionUtils;
import me.prisonranksx.utils.MCTextEffect;

public class PapiHook extends PlaceholderExpansion {
	
    private PrisonRanksX main;
    private PRXAPI prxAPI;
    private LeaderboardManager lbm;
    private String nullNameRank;
    private String nullValueRank;
    private String nullNamePrestige;
    private String nullValuePrestige;
    private String nullNameRebirth;
    private String nullValueRebirth;
    private final static String PAPI = "PlaceholderAPI."; 
    public final static DecimalFormat df = new DecimalFormat("###,###.##");
    public final static NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);

	public PapiHook(PrisonRanksX main) {
		super();
		this.main = main;
		prxAPI = this.main.prxAPI;
		lbm = this.main.lbm;
		nullNameRank = this.main.globalStorage.getStringData("PlaceholderAPI.leaderboard-name-rank-null");
		nullValueRank = this.main.globalStorage.getStringData("PlaceholderAPI.leaderboard-value-rank-null");
		nullNamePrestige = this.main.globalStorage.getStringData("PlaceholderAPI.leaderboard-name-prestige-null");
		nullValuePrestige = this.main.globalStorage.getStringData("PlaceholderAPI.leaderboard-value-prestige-null");
		nullNameRebirth = this.main.globalStorage.getStringData("PlaceholderAPI.leaderboard-name-rebirth-null");
		nullValueRebirth = this.main.globalStorage.getStringData("PlaceholderAPI.leaderboard-value-rebirth-null");
	}

	public String getNullNameRank() {
		return this.nullNameRank;
	}

	public String getNullValueRank() {
		return this.nullValueRank;
	}
	
	public String getNullNamePrestige() {
		return this.nullNamePrestige;
	}
	
	public String getNullValuePrestige() {
		return this.nullValuePrestige;
	}
	
	public String getNullNameRebirth() {
		return this.nullNameRebirth;
	}
	
	public String getNullValueRebirth() {
		return this.nullValueRebirth;
	}
	
    @Override
    public boolean persist(){
        return true;
    }
    
    @Override
    public boolean canRegister(){
        return true;
    }

    @Nonnull
    public String getChatColorsInString(final String string) {
    	String chatColors = "";
    	if(string == null || !ChatColor.stripColor(string).contains("&")) {
    		return chatColors;
    	} else {
    		int i = -1;
    		for(char character : string.toCharArray()) {
    			i++;
    			if(character == '&') {
    				chatColors = chatColors + String.valueOf(string.charAt(i + 1));
    			}
    		}
    	}
    	return chatColors;
    }
    
    @Override
	public String onRequest(OfflinePlayer arg0, String arg1) {
        OfflinePlayer p = arg0;
        if (is(arg1, "canprestige")) return s(prxAPI.canPrestige(p.getPlayer()));        	
        if (is(arg1, "canrebirth")) return s(prxAPI.canRebirth(p.getPlayer()));
        if (is(arg1, "currentrank_number")) return s(prxAPI.getPlayerRankNumber(p));
        if (is(arg1, "currentprestige_number")) return s(prxAPI.getPlayerPrestigeNumber(p));
        if (is(arg1, "currentrebirth_number")) return s(prxAPI.getPlayerRebirthNumber(p));
        if (is(arg1, "current_displayname")) return prxAPI.getStageDisplay(p.getPlayer(), " ", true);
        if (is(arg1, "higheststage_name")) return prxAPI.getHighestStage(p.getPlayer());
        if (is(arg1, "higheststage_displayname")) return prxAPI.getHighestStageDisplay(p.getPlayer());
        if (is(arg1, "higheststage_name_norebirth")) return prxAPI.getHighestStage(p.getPlayer(), true);
        if (is(arg1, "higheststage_displayname_norebirth")) return prxAPI.getHighestStageDisplay(p.getPlayer(), true);
        if (sw(arg1, "current_displayname_customspace_")) return prxAPI.getStageDisplay(p.getPlayer(), swr(arg1, "current_displayname_customspace_"), true);
		if (is(arg1, "currentrank_name")) return prxAPI.isLastRank(p) && boolDataP("currentrank-lastrank-enabled") ? stringDataP("currentrank-lastrank", arg0) : s(prxAPI.getPlayerRank(p));
		if (sw(arg1, "currentrank_name_")) return swp(arg1, "currentrank_name_", "null", player -> prxAPI.getPlayerRank(player));
		if (is(arg1, "currentrank_displayname")) return prxAPI.isLastRank(p) && boolDataP("currentrank-lastrank-enabled") ? stringDataP("currentrank-lastrank", arg0) : s(prxAPI.getPlayerRankDisplay(p));
		if (is(arg1, "currentrank_firstcolor")) return s(MCTextEffect.getColorCodesAfterChar(prxAPI.getPlayerRankDisplay(p), 0));
		if (is(arg1, "currentrank_lastchar")) return s(prxAPI.getPlayerRankDisplay(p).charAt(prxAPI.getPlayerRankDisplay(p).length() - 1));
		if (is(arg1, "currentrank_lastcolors")) return s(ChatColor.getLastColors(prxAPI.getPlayerRankDisplay(p)));
		if (is(arg1, "currentrank_secondcolor")) {
			String stripped = ChatColor.stripColor(prxAPI.getPlayerRankDisplay(p));
			char thirdChar = stripped.charAt(3);
			return s(thirdChar) == null ? "&r" : s(thirdChar);
		}
		if (is(arg1, "currentrank_thirdcolor")) {
			String stripped = ChatColor.stripColor(prxAPI.getPlayerRankDisplay(p));
			return s(stripped.charAt(5)) == null ? "&r" : s(stripped.charAt(5));
		}
		if (is(arg1, "currentrank_afterbracketcolor")) {
			String rankDisplay = prxAPI.getPlayerRankDisplay(p);
			return s(rankDisplay.charAt(rankDisplay.indexOf("[") + 2));
		}
		if (is(arg1, "currentrank_afterspacecolor")) {
			String rankDisplay = prxAPI.getPlayerRankDisplay(p);
			return s(rankDisplay.charAt(rankDisplay.indexOf(" ") + 2));
		}
		if (is(arg1, "currentrank_colors")) return getChatColorsInString(prxAPI.getPlayerRankDisplay(p));
		if (is(arg1, "rankup_percentage")) {
			if (prxAPI.getPlayerNextRank(p) == null) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.rankup-percentage-lastrank"), arg0.getName());
			}
			if (prxAPI.isPercentSignBehind()) {
				return prxAPI.getPercentSign() + s(prxAPI.getPlayerRankupPercentageDirect(p));
			} else {
				return s(prxAPI.getPlayerRankupPercentageDirect(p)) + prxAPI.getPercentSign();
			}
		}
		if (sw(arg1, "rank_percentage_")) {
			if (sw(arg1, "rank_percentage_decimal_nolimit_")) {
				String path = prxAPI.getPlayerRankPath(p).getPathName();
				String rank = swr(arg1, "rank_percentage_decimal_nolimit_");
				RankPath rp = RankPath.getRankPath(rank, path);
				if (prxAPI.isPercentSignBehind()) {
					return prxAPI.getPercentSign() + s(prxAPI.getPlayerRankPercentageDecimalNoLimitDirect(p, rp));
				} else {
					return s(prxAPI.getPlayerRankPercentageDecimalNoLimitDirect(p, rp)) + prxAPI.getPercentSign();
				}
			 } else if (sw(arg1, "rank_percentage_decimal_")) {
				String path = prxAPI.getPlayerRankPath(p).getPathName();
				String rank = swr(arg1, "rank_percentage_decimal_");
				RankPath rp = RankPath.getRankPath(rank, path);
				if (prxAPI.isPercentSignBehind()) {
					return prxAPI.getPercentSign() + s(prxAPI.getPlayerRankPercentageDecimalDirect(p, rp));
				} else {
					return s(prxAPI.getPlayerRankPercentageDecimalDirect(p, rp)) + prxAPI.getPercentSign();
				}
			} else if (sw(arg1, "rankup_percentage_plain_")) {
				String path = prxAPI.getPlayerRankPath(p).getPathName();
				String rank = swr(arg1, "rankup_percentage_plain_");
				RankPath rp = RankPath.getRankPath(rank, path);
				return s(prxAPI.getPlayerRankPercentage(p, rp));
			} else if (sw(arg1, "rank_percentage_nolimit_")) {
				String path = prxAPI.getPlayerRankPath(p).getPathName();
				String rank = swr(arg1, "rank_percentage_nolimit_");
				RankPath rp = RankPath.getRankPath(rank, path);
				if (prxAPI.isPercentSignBehind()) {
					return prxAPI.getPercentSign() + s(prxAPI.getPlayerRankPercentageNoLimitDirect(p, rp));
				} else {
					return s(prxAPI.getPlayerRankPercentageNoLimitDirect(p, rp)) + prxAPI.getPercentSign();
				}
			} else {
				String path = prxAPI.getPlayerRankPath(p).getPathName();
				String rank = swr(arg1, "rank_percentage_");
				RankPath rp = RankPath.getRankPath(rank, path);
				if (prxAPI.isPercentSignBehind()) {
					return prxAPI.getPercentSign() + s(prxAPI.getPlayerRankPercentage(p, rp));
				} else {
					return s(prxAPI.getPlayerRankPercentage(p, rp)) + prxAPI.getPercentSign();
				}
			}
		}
		if (is(arg1, "rankup_percentage_plain")) return prxAPI.getPlayerNextRank(p) == null ? "100" : s(prxAPI.getPlayerRankupPercentageDirect(p));
		if (sw(arg1, "plaindecimal_")) {
			String bsed = PlaceholderAPI.setBracketPlaceholders(p, swr(arg1, "plaindecimal_"));
			if (!prxAPI.numberAPI.isNumber(bsed)) {
				return s(prxAPI.numberAPI.keepNumbersWithDots(bsed));
			}
			return s(prxAPI.numberAPI.keepNumbersWithDots(prxAPI.numberAPI.deleteScientificNotationA(Double.valueOf(bsed))));
		}
		if (sw(arg1, "plain_")) {
			String bsed = PlaceholderAPI.setBracketPlaceholders(p, swr(arg1, "plain_"));
			return s(prxAPI.numberAPI.keepNumbers(bsed));
		}
		if (sw(arg1, "integerize_")) {
			String integerize = PlaceholderAPI.setBracketPlaceholders(p, prxAPI.numberAPI.keepNumbersWithDots(swr(arg1, "integerize_")));
		    if (prxAPI.numberAPI.isNumber(integerize)) {
		    	double val = Double.valueOf(integerize);
		    	return s(prxAPI.numberAPI.toFakeInteger(val));
		    } else {
		    	return integerize;
		    }
		}
		if (sw(arg1, "usformat_")) {
			String bsed = PlaceholderAPI.setBracketPlaceholders(p, swr(arg1, "usformat_"));
			return nf.format(Double.valueOf(bsed));
		}
		if (is(arg1, "next_percentage")) {
			if (prxAPI.isPercentSignBehind()) {
				return prxAPI.getPercentSign() + s(prxAPI.getPlayerNextPercentage(p).getPercentage());
			} else {
				return s(prxAPI.getPlayerNextPercentage(p).getPercentage()) + prxAPI.getPercentSign();
			}
		}
		if (is(arg1, "next_percentage_plain")) {
           return s(prxAPI.getPlayerNextPercentage(p).getPercentage());
		}
		if (is(arg1, "next_percentage_decimal")) {
			if (prxAPI.isPercentSignBehind()) {
				return prxAPI.getPercentSign() + s(prxAPI.getPlayerNextPercentageDecimal(p));
			} else {
				return s(prxAPI.getPlayerNextPercentageDecimal(p)) + prxAPI.getPercentSign();
			}
		}
		if (is(arg1, "rankup_percentage_decimal")) {
			if (prxAPI.getPlayerNextRank(p) == null) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.rankup-percentage-lastrank"), arg0.getName());
			}
			if (prxAPI.isPercentSignBehind()) {
				return prxAPI.getPercentSign() + s(prxAPI.getPlayerRankupPercentageDecimalDirect(p));
			} else {
				return s(prxAPI.getPlayerRankupPercentageDecimalDirect(p)) + prxAPI.getPercentSign();
			}
		}
		if (is(arg1, "rankup_percentage_nolimit")) {
			if (prxAPI.getPlayerNextRank(p) == null) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.rankup-percentage-lastrank"), arg0.getName());
			}
			if (prxAPI.isPercentSignBehind()) {
				return prxAPI.getPercentSign() + s(prxAPI.getPlayerRankupPercentageNoLimitDirect(p));
			} else {
				return s(prxAPI.getPlayerRankupPercentageNoLimitDirect(p)) + prxAPI.getPercentSign();
			}
		}	
		if (is(arg1, "rankup_percentage_decimal_nolimit")) {
			if (prxAPI.getPlayerNextRank(p) == null) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.rankup-percentage-lastrank"), arg0.getName());
			}
			if (prxAPI.isPercentSignBehind()) {
				return prxAPI.getPercentSign() + s(prxAPI.getPlayerRankupPercentageDecimalNoLimitDirect(p));
			} else {
				return s(prxAPI.getPlayerRankupPercentageDecimalNoLimitDirect(p)) + prxAPI.getPercentSign();
			}
		}
		if (is(arg1, "rankup_progress")) return prxAPI.getPlayerNextRank(p) == null ? stringDataP("rankup-progress-lastrank", arg0) : s(prxAPI.getPlayerRankupProgressBar(p));
		if(arg1.startsWith("rank_progress_")) {
			if(arg1.startsWith("rank_progress_double_")) {
				String path = prxAPI.getPlayerRankPath(p).getPathName();
				String rank = arg1.replace("rank_progress_double_", "");
				RankPath rp = RankPath.getRankPath(rank, path);
				return String.valueOf(prxAPI.getPlayerRankProgressBarExtended(p, rp));
			} else {
				String path = prxAPI.getPlayerRankPath(p).getPathName();
				String rank = arg1.replace("rank_progress_", "");
				RankPath rp = RankPath.getRankPath(rank, path);
				return String.valueOf(prxAPI.getPlayerRankProgressBar(p, rp));
			}
		}
		if(arg1.equalsIgnoreCase("next_progress")) {
			return String.valueOf((prxAPI.getPlayerNextProgress(p)));
		}
		if(arg1.equalsIgnoreCase("next_progress_double")) {
			return String.valueOf((prxAPI.getPlayerNextProgressExtended(p)));
		}
		if(arg1.equalsIgnoreCase("rankup_progress_double")) {
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
		if(arg1.startsWith("rankup_name_")) {
			Player player = Bukkit.getPlayer(arg1.replace("rankup_name_", ""));
			if(player == null) {
				return "null";
			}
			return String.valueOf(prxAPI.getPlayerNextRank(p));
		}
		if(arg1.equalsIgnoreCase("rankup_displayname")) {
			if(prxAPI.getPlayerNextRank(p) == null) {
				return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.rankup-lastrank"), arg0.getName());
			}
			return String.valueOf(prxAPI.getPlayerRankupDisplay(p));
		}
		if(arg1.equalsIgnoreCase("")) {
			
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
		if((arg1.equalsIgnoreCase("rankup_cost_integer"))) {
			if(prxAPI.getPlayerNextRank(p) == null) {
				return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.rankup-cost-lastrank"), arg0.getName());
			}
			if(prxAPI.isCurrencySymbolBehind()) {
			return  String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol()) + String.valueOf(prxAPI.numberAPI.toFakeInteger(prxAPI.getPlayerRankupCostWithIncreaseDirect(p)));
			} else {
				return String.valueOf(prxAPI.numberAPI.toFakeInteger(prxAPI.getPlayerRankupCostWithIncreaseDirect(p))) + String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol());
			}
		}
		if((arg1.equalsIgnoreCase("rankup_cost_plain"))) {
			if(prxAPI.getPlayerNextRank(p) == null) {
				return "0.0";
			}
           return String.valueOf(prxAPI.numberAPI.deleteScientificNotationA(prxAPI.getPlayerRankupCostWithIncreaseDirect(p)));
		}
		if((arg1.equalsIgnoreCase("rankup_cost_integer_plain"))) {
           return String.valueOf(prxAPI.numberAPI.toFakeInteger(prxAPI.getPlayerRankupCostWithIncreaseDirect(p)));
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
			return String.valueOf(prxAPI.getEconomy().getBalance(p));
		}
		if(arg1.equalsIgnoreCase("money_decimalformatted")) {
			String finalv = df.format(prxAPI.getEconomy().getBalance(p));
			return finalv;
		}
		if(arg1.startsWith("decimalformat_")) {
			try {
			double placeholderToFormat = Double.valueOf(PlaceholderAPI.setBracketPlaceholders(p, (arg1.replace("decimalformat_", ""))));
			String formatted = df.format(placeholderToFormat);
			return formatted;
			} catch (NumberFormatException nfe) {
				return String.valueOf(PlaceholderAPI.setBracketPlaceholders(p, arg1.replace("decimalformat_", "")));
			}
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
		if(arg1.startsWith("rebirth_name_")) {
			Player player = Bukkit.getPlayer(arg1.replace("rebirth_name_", ""));
			if(player == null) {
				return "null";
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
		if(arg1.equalsIgnoreCase("prestige_number")) {
			if(!prxAPI.hasPrestiged(p)) {
				return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.prestige-notprestiged"), arg0.getName());
			}
			if(main.isInfinitePrestige) {
				return String.valueOf(prxAPI.getPlayerPrestige(p));
			}
			return String.valueOf(prxAPI.getPlayerPrestigeNumber(p));
		}
		if(arg1.equalsIgnoreCase("prestige_number_formatted")) {
			if(!prxAPI.hasPrestiged(p)) {
				return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.prestige-notprestiged"), arg0.getName());
			}
			if(main.isInfinitePrestige) {
				return String.valueOf(prxAPI.formatBalance(Double.valueOf(prxAPI.getPlayerPrestige(p))));
			}
			return String.valueOf(prxAPI.formatBalance(prxAPI.getPlayerPrestigeNumber(p)));
		}
		if(arg1.equalsIgnoreCase("prestige_number_usformat")) {
			if(!prxAPI.hasPrestiged(p)) {
				return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.prestige-notprestiged"), arg0.getName());
			}
			if(main.isInfinitePrestige) {
				return s(nf.format(Long.valueOf(prxAPI.getPlayerPrestige(p))));
			}
			return s(nf.format(prxAPI.getPlayerPrestigeNumber(p)));
		}
		if(arg1.startsWith("prestige_name_")) {
			Player player = Bukkit.getPlayer(arg1.replace("prestige_name_", ""));
			if(player == null) {
				return "null";
			}
			return String.valueOf(prxAPI.getPlayerPrestige(p));
		}
		if(arg1.startsWith("prestige_number_usformat_")) {
			Player player = Bukkit.getPlayer(arg1.replace("prestige_number_usformat_", ""));
			if(player == null) {
				return "null";
			}
			return String.valueOf(prxAPI.getPlayerPrestige(p));
		}
		if(arg1.equalsIgnoreCase("prestige_displayname")) {
			if(!prxAPI.hasPrestiged(p)) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.prestige-notprestiged"), arg0.getName());
			}
			return String.valueOf(prxAPI.getPlayerPrestigeDisplay(p));
		}
		if(arg1.equalsIgnoreCase("prestige_displayname_usformat")) {
			if(!prxAPI.hasPrestiged(p)) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.prestige-notprestiged"), arg0.getName());
			}
			if(main.isInfinitePrestige) {
				return main.getString(main.infinitePrestigeSettings.getDisplay().replace("{number}", nf.format(Long.valueOf(prxAPI.getPlayerPrestige(p)))));
			}
			return s(nf.format(prxAPI.getNumberAPI().keepNumbersWith(prxAPI.getPlayerPrestigeDisplay(p), "§")));
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
		if(arg1.equalsIgnoreCase("prestige_cost_plain")) {
			if(!prxAPI.hasPrestiged(p)) {
				return "0.0";
			}
			return String.valueOf(prxAPI.getPlayerPrestigeCost(p));
		}
		if(arg1.equalsIgnoreCase("prestige_cost_integer_plain")) {
			if(!prxAPI.hasPrestiged(p)) {
				return "0";
			}
			return String.valueOf(prxAPI.numberAPI.toFakeInteger(prxAPI.getPlayerPrestigeCost(p)));
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
		if(arg1.startsWith("nextrebirth_name_")) {
			Player player = Bukkit.getPlayer(arg1.replace("nextrebirth_name_", ""));
			if(player == null) {
				return "null";
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
		if(arg1.startsWith("nextprestige_name_")) {
			Player player = Bukkit.getPlayer(arg1.replace("nextprestige_name_", ""));
			if(player == null) {
				return "null";
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
			return String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol()) + String.valueOf(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p)); 
			} else {
				return String.valueOf(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p) + String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol())); 
			}
		}
		if(arg1.equalsIgnoreCase("nextprestige_cost_integer")) {
			if(!prxAPI.hasNextPrestige(p)) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.prestige-lastprestige"), arg0.getName());
			}
			if(prxAPI.isCurrencySymbolBehind()) {
			return String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol()) + String.valueOf(prxAPI.numberAPI.toFakeInteger(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p))); 
			} else {
				return String.valueOf(prxAPI.numberAPI.toFakeInteger(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p)) + String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol())); 
			}
		}
		if(arg1.equalsIgnoreCase("nextprestige_cost_plain")) {
			return String.valueOf(prxAPI.numberAPI.deleteScientificNotationA(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p)));
		}
		if(arg1.equalsIgnoreCase("nextprestige_cost_integer_plain")) {
			return String.valueOf(prxAPI.numberAPI.toFakeInteger(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p)));
		}
		if(arg1.equalsIgnoreCase("nextrebirth_cost")) {
			if(!prxAPI.hasNextRebirth(p)) {
				return main.getString(main.globalStorage.getStringData("PlaceholderAPI.rebirth-lastrebirth"), arg0.getName());
			}
			if(prxAPI.isCurrencySymbolBehind()) {
				return String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol()) + String.valueOf(prxAPI.getPlayerNextRebirthCost(p));
			} else {
				return String.valueOf(prxAPI.getPlayerNextRebirthCost(p)) + String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol());
			}
		}
		if(arg1.equalsIgnoreCase("nextrebirth_cost_integer")) {
			if(!prxAPI.hasNextRebirth(p)) {
				return main.getString(main.globalStorage.getStringData("PlaceholderAPI.rebirth-lastrebirth"), arg0.getName());
			}
			if(prxAPI.isCurrencySymbolBehind()) {
				return String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol()) + String.valueOf(prxAPI.numberAPI.toFakeInteger(prxAPI.getPlayerNextRebirthCost(p)));
			} else {
				return String.valueOf(prxAPI.numberAPI.toFakeInteger(prxAPI.getPlayerNextRebirthCost(p))) + String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol());
			}
		}
		if(arg1.equalsIgnoreCase("nextrebirth_cost_plain")) {
			if(!prxAPI.hasNextRebirth(p)) {
				return "0.0";
			}
			return String.valueOf(prxAPI.numberAPI.deleteScientificNotationA(prxAPI.getPlayerNextRebirthCost(p)));
		}
		if(arg1.equalsIgnoreCase("nextrebirth_cost_integer_plain")) {
			if(!prxAPI.hasNextRebirth(p)) {
				return "0";
			}
			return String.valueOf(prxAPI.numberAPI.toFakeInteger(prxAPI.getPlayerNextRebirthCost(p)));
		}
		if(arg1.equalsIgnoreCase("nextprestige_cost_formatted")) {
			if(!prxAPI.hasNextPrestige(p)) {
				  return main.getString(prxAPI.main.globalStorage.getStringData("PlaceholderAPI.prestige-lastprestige"), arg0.getName());
			}
			if(prxAPI.isCurrencySymbolBehind()) {
			return String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol()) + String.valueOf(main.formatBalance(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p)));
			} else {
				return String.valueOf(main.formatBalance(prxAPI.getPlayerNextPrestigeCostWithIncreaseDirect(p))) + String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol());
			}
		}
		if(arg1.equalsIgnoreCase("nextrebirth_cost_formatted")) {
			if(!prxAPI.hasNextRebirth(p)) {
				return main.getString(main.globalStorage.getStringData("PlaceholderAPI.rebirth-lastrebirth"), arg0.getName());
			}
			if(prxAPI.isCurrencySymbolBehind()) {
				return String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol()) + String.valueOf(main.formatBalance(prxAPI.getPlayerNextRebirthCost(p)));
			} else {
				return String.valueOf(main.formatBalance(prxAPI.getPlayerNextRebirthCost(p))) + String.valueOf(prxAPI.getPlaceholderAPICurrencySymbol());
			}
		}
		if(arg1.equalsIgnoreCase("can_prestige")) {
			return String.valueOf(prxAPI.canPrestige(p.getPlayer()));
		}
		if(arg1.equalsIgnoreCase("can_rebirth")) {
			return String.valueOf(prxAPI.canRebirth(p.getPlayer()));
		}
		if(arg1.startsWith("rank_displayname_")) {
			String rank = arg1.split("_")[2];
			RankPath rp = new RankPath(rank, prxAPI.getDefaultPath());
			if(!prxAPI.rankPathExists(rp)) {
				return rank + " is not available";
			}
			return prxAPI.getRankDisplay(rp);
		}
		if(arg1.startsWith("rank_cost_integer_")) {
			String rank = arg1.split("_")[3];
			RankPath rp = new RankPath(rank, prxAPI.getDefaultPath());
			if(!prxAPI.rankPathExists(rp)) {
				return rank + " is not available";
			}
			String prestige = prxAPI.getPlayerPrestige(p);
			String rebirth = prxAPI.getPlayerRebirth(p);
			double rankCost = prxAPI.getRankCost(rp);
			double increasedRankCost = prxAPI.getIncreasedRankupCostX(rebirth, prestige, rankCost);
			return String.valueOf(prxAPI.numberAPI.toFakeInteger(increasedRankCost));
		}
		if(arg1.startsWith("rank_cost_")) {
			String rank = arg1.split("_")[2];
			RankPath rp = new RankPath(rank, prxAPI.getDefaultPath());
			if(!prxAPI.rankPathExists(rp)) {
				return rank + " is not available";
			}
			String prestige = prxAPI.getPlayerPrestige(p);
			String rebirth = prxAPI.getPlayerRebirth(p);
			double rankCost = prxAPI.getRankCost(rp);
			String increasedRankCost = String.valueOf(prxAPI.getIncreasedRankupCostX(rebirth, prestige, rankCost));
			return String.valueOf(increasedRankCost);
		}
		if(arg1.startsWith("rank_costformatted_")) {
			String rank = arg1.split("_")[2];
			RankPath rp = new RankPath(rank, prxAPI.getDefaultPath());
			if(!prxAPI.rankPathExists(rp)) {
				return rank + " is not available";
			}
			String prestige = prxAPI.getPlayerPrestige(p);
			String rebirth = prxAPI.getPlayerRebirth(p);
			double rankCost = prxAPI.getRankCost(rp);
			double increasedRankCost = prxAPI.getIncreasedRankupCostX(rebirth, prestige, rankCost);
			return String.valueOf(prxAPI.formatBalance(increasedRankCost));
		}
		if(arg1.startsWith("prestige_displayname_")) {
			String prestige = arg1.split("_")[2];
			return String.valueOf(prxAPI.getPrestigeDisplay(prestige));
		}
		if(arg1.startsWith("prestige_cost_integer_")) {
			String prestige = arg1.split("_")[3];
			String rebirth = prxAPI.getPlayerRebirth(p);
			double prestigeCost = prxAPI.getPrestigeCost(prestige);
			double increasedPrestigeCost = prxAPI.getIncreasedPrestigeCost(rebirth, prestigeCost);
			return String.valueOf(prxAPI.numberAPI.toFakeInteger(increasedPrestigeCost));
		}
		if(arg1.startsWith("prestige_cost_")) {
			String prestige = arg1.split("_")[2];
			String rebirth = prxAPI.getPlayerRebirth(p);
			double prestigeCost = prxAPI.getPrestigeCost(prestige);
			double increasedPrestigeCost = prxAPI.getIncreasedPrestigeCost(rebirth, prestigeCost);
			return String.valueOf(increasedPrestigeCost);
		}
		if(arg1.startsWith("prestige_costformatted_")) {
			String prestige = arg1.split("_")[2];
			String rebirth = prxAPI.getPlayerRebirth(p);
			double prestigeCost = prxAPI.getPrestigeCost(prestige);
			double increasedPrestigeCost = prxAPI.getIncreasedPrestigeCost(rebirth, prestigeCost);
			return String.valueOf(prxAPI.formatBalance(increasedPrestigeCost));
		}
		if(arg1.startsWith("rebirth_displayname_")) {
			String rebirth = arg1.split("_")[2];
			return prxAPI.getRebirthDisplay(rebirth);
		}
		if(arg1.startsWith("rebirth_cost_integer_")) {
			String rebirth = arg1.split("_")[3];
			return String.valueOf(prxAPI.numberAPI.toFakeInteger(prxAPI.getRebirthCost(rebirth)));
		}
		if(arg1.startsWith("rebirth_cost_")) {
			String rebirth = arg1.split("_")[2];
			return String.valueOf(prxAPI.getRebirthCost(rebirth));
		}
		if(arg1.startsWith("rebirth_costformatted_")) {
			String rebirth = arg1.split("_")[2];
			return String.valueOf(prxAPI.getRebirthCostFormatted(rebirth));
		}
		if(arg1.startsWith("name_prestige_")) {
			int position = Integer.valueOf(arg1.split("_")[2]);
			return String.valueOf(lbm.getPlayerNameFromPositionPrestige(position, getNullNamePrestige()));
		}
		if(arg1.startsWith("value_prestige_")) {
			int position = Integer.valueOf(arg1.split("_")[2]);
			return String.valueOf(lbm.getPlayerPrestigeFromPosition(position, getNullValuePrestige()));
		}
		if(arg1.startsWith("valuedisplay_prestige_")) {
			int position = Integer.valueOf(arg1.split("_")[2]);
			return String.valueOf(lbm.getPlayerPrestigeDisplayNameFromPosition(position, getNullValuePrestige()));
		}
		if(arg1.startsWith("name_rebirth_")) {
			int position = Integer.valueOf(arg1.split("_")[2]);
			return String.valueOf(lbm.getPlayerNameFromPositionRebirth(position, getNullNameRebirth()));
		}
		if(arg1.startsWith("value_rebirth_")) {
			int position = Integer.valueOf(arg1.split("_")[2]);
			return String.valueOf(lbm.getPlayerRebirthFromPosition(position, getNullValueRebirth()));
		}
		if(arg1.startsWith("valuedisplay_rebirth_")) {
			int position = Integer.valueOf(arg1.split("_")[2]);
			return String.valueOf(lbm.getPlayerRebirthDisplayNameFromPosition(position, getNullValueRebirth()));
		}
		if(arg1.startsWith("name_rank_")) {
			int position = Integer.valueOf(arg1.split("_")[2]);
			return String.valueOf(lbm.getPlayerNameFromPositionRank(position, getNullNameRank()));
		}
		if(arg1.startsWith("value_rank_")) {
			int position = Integer.valueOf(arg1.split("_")[2]);
			return String.valueOf(lbm.getPlayerRankFromPosition(position, getNullValueRank()));
		}
		if(arg1.startsWith("valuedisplay_rank_")) {
			int position = Integer.valueOf(arg1.split("_")[2]);
			return String.valueOf(lbm.getPlayerRankDisplayNameFromPosition(position, getNullValueRank()));
		}
		if(arg1.startsWith("name_stage_")) {
			int position = Integer.valueOf(arg1.split("_")[2]);
            return String.valueOf(lbm.getPlayerNameFromPositionGlobal(position, getNullNameRank()));
		}
		if(arg1.startsWith("value_stage_")) {
			int position = Integer.valueOf(arg1.split("_")[2]);
			return String.valueOf(lbm.getPlayerStageFromPosition(position, getNullValueRank()));
		}
		if(arg1.startsWith("valuedisplay_stage_")) {
			int position = Integer.valueOf(arg1.split("_")[2]);
			return String.valueOf(lbm.getPlayerStageDisplayNameFromPosition(position, getNullValueRank()));
		}
		if(arg1.startsWith("prestigerebirth_stage_")) {
			int position = Integer.valueOf(arg1.replace("prestigerebirth_stage_", ""));
			Entry<UUID, Integer> playerPosition = lbm.getPlayerFromPositionGlobal(position);
			String display = "";
			if(playerPosition == null) {
				display = "";
			} else {
				display = prxAPI.getPrestigeAndRebirthDisplay(playerPosition.getKey(), " ");
			}
			return String.valueOf(display);
		}
		if(arg1.startsWith("has_prestiged")) {
			return String.valueOf(prxAPI.hasPrestiged(p));
		}
		if(arg1.startsWith("has_rebirthed")) {
			return String.valueOf(prxAPI.hasRebirthed(p));
		}
		if((arg0 == null)) {
			return null;
		}
		return null;
		
	}

	@Override
	public String getAuthor() {
		return CollectionUtils.collectionToString(main.getDescription().getAuthors(), ", ");
	}

	@Override
	public String getIdentifier() {
		return "prisonranksx";
	}

	@Override
	public String getVersion() {
		return main.getDescription().getVersion();
	}
	
	private boolean is(String arg1, String placeholder) {
		return arg1.equalsIgnoreCase(placeholder);
	}
	
	private boolean sw(String arg1, String placeholder) {
		return arg1.startsWith(placeholder);
	}
	
	private String swr(String arg1, String unwanted) {
		return arg1.replace(unwanted, "");
	}
	
	private String swp(String arg1, String unwanted, String notOnlineResult, Function<Player, String> onlineFunctionResult) {
		Player player = Bukkit.getPlayer(swr(arg1, unwanted));
		return player == null ? notOnlineResult : onlineFunctionResult.apply(player);
	}
	
	private String stringData(String string) {
		return main.getGlobalStorage().getStringData(string);
	}
	
	private String stringDataP(String string) {
		return stringData(PAPI + string);
	}
	
	private String stringData(String string, OfflinePlayer player) {
		return main.getString(stringData(string), player.getName());
	}
	
	private String stringDataP(String string, OfflinePlayer player) {
		return stringData(PAPI + string, player);
	}
	
	private boolean boolData(String string) {
		return main.getGlobalStorage().getBooleanData(string);
	}
	
	private boolean boolDataP(String string) {
		return main.getGlobalStorage().getBooleanData(PAPI + string);
	}
	
	private String s(Object obj) {
		return String.valueOf(obj);
	}
 
}
