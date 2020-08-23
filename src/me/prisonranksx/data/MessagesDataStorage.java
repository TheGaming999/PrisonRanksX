package me.prisonranksx.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.prisonranksx.PrisonRanksX;

public class MessagesDataStorage {

	public Map<String, String> stringData = new HashMap<>();
	public Map<String, List<String>> stringListData = new HashMap<>();
	
	private PrisonRanksX main;
	
	public MessagesDataStorage(PrisonRanksX main) {this.main = main;}
	
	public GlobalDataStorage gds() {
		return main.getGlobalStorage();
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
	}
	
	public String getStringMessage(String configMessage) {
		return stringData.get(configMessage).replace("\\n", "\n");
	}
	
	public List<String> getStringListMessage(String configMessage) {
		return stringListData.get(configMessage);
	}
	
	public String notEnoughMoneyMessage() {return stringData.get("Messages.notenoughmoney");}
}
