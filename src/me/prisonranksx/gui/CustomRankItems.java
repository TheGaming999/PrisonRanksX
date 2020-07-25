package me.prisonranksx.gui;

import java.util.HashMap;
import java.util.Map;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.GlobalDataStorage;
import me.prisonranksx.data.LevelType;
import me.prisonranksx.data.RankPath;

public class CustomRankItems {

	private Map<String, RankItem> customRankItems;
	private PrisonRanksX main;
	
	public CustomRankItems(PrisonRanksX main) {this.customRankItems = new HashMap<>(); this.main = main;}
	
	public Map<String, RankItem> getCustomRankItems() {
		return this.customRankItems;
	}
	
	public GlobalDataStorage gds() {
		return main.getGlobalStorage();
	}
	
	public void setup() {
		for(String path : main.rankStorage.getEntireData().keySet()) {
			RankPath rankPath = RankPath.getRankPath(path);
			String rank = rankPath.getRankName();
			for(int i = 0; i < 3; i++) {
				LevelState ls = LevelState.values()[i];
				if(main.cim.hasCustomLevelItem(LevelType.RANK, ls) && main.cim.hasCustomFormat(LevelType.RANK, ls, rank)) {
					RankItem ri = new RankItem();
					RankState rs = new RankState();
					rs.setLevelState(ls);
					rs.setRankPath(rankPath);
					ri.setMaterial(main.cim.readCustomLevelItemName(LevelType.RANK, ls, rank));
					ri.setAmount(main.cim.readCustomLevelItemAmount(LevelType.RANK, ls, rank));
					ri.setDisplayName(gds().translateHexColorCodes(main.cim.readCustomLevelItemDisplayName(LevelType.RANK, ls, rank)));
					ri.setLore(gds().translateHexColorCodes(main.cim.readCustomLevelItemLore(LevelType.RANK, ls, rank)));
					ri.setEnchantments(main.cim.readCustomLevelItemEnchantments(LevelType.RANK, ls, rank));
					ri.setFlags(main.cim.readCustomLevelItemFlags(LevelType.RANK, ls, rank));
					ri.setCommands(gds().translateHexColorCodes(main.cim.readCustomLevelItemCommands(LevelType.RANK, ls, rank)));
					customRankItems.put(rs.toString(), ri);
				}
			}
		}
	}
	
}
