package me.prisonranksx.gui;

import java.util.HashMap;
import java.util.Map;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.GlobalDataStorage;
import me.prisonranksx.data.LevelType;

public class CustomRebirthItems {

	private Map<String, RebirthItem> customRebirthItems;
	private PrisonRanksX main;
	
	public CustomRebirthItems(PrisonRanksX main) {this.customRebirthItems = new HashMap<>(); this.main = main;}
	
	public Map<String, RebirthItem> getCustomRebirthItems() {
		return this.customRebirthItems;
	}
	
	public GlobalDataStorage gds() {
		return main.getGlobalStorage();
	}
	
	public void setup() {
		for(String rebirth : main.rebirthStorage.getRebirthData().keySet()) {
			for(int i = 0; i < 3; i++) {
				LevelState ls = LevelState.values()[i];
				if(main.getCustomItemsManager().hasCustomLevelItem(LevelType.REBIRTH, ls) && main.getCustomItemsManager().hasCustomFormat(LevelType.REBIRTH, ls, rebirth)) {
					RebirthItem ri = new RebirthItem();
					RebirthState rs = new RebirthState();
					rs.setLevelState(ls);
					rs.setRebirth(rebirth);
					ri.setMaterial(main.getCustomItemsManager().readCustomLevelItemName(LevelType.REBIRTH, ls, rebirth));
					ri.setAmount(main.getCustomItemsManager().readCustomLevelItemAmount(LevelType.REBIRTH, ls, rebirth));
					ri.setDisplayName(gds().translateHexColorCodes(main.getCustomItemsManager().readCustomLevelItemDisplayName(LevelType.REBIRTH, ls, rebirth)));
					ri.setLore(gds().translateHexColorCodes(main.getCustomItemsManager().readCustomLevelItemLore(LevelType.REBIRTH, ls, rebirth)));
					ri.setEnchantments(main.getCustomItemsManager().readCustomLevelItemEnchantments(LevelType.REBIRTH, ls, rebirth));
					ri.setFlags(main.getCustomItemsManager().readCustomLevelItemFlags(LevelType.REBIRTH, ls, rebirth));
					ri.setCommands(gds().translateHexColorCodes(main.getCustomItemsManager().readCustomLevelItemCommands(LevelType.REBIRTH, ls, rebirth)));
					customRebirthItems.put(rs.toString(), ri);
				}
			}
		}
	}
	
}
