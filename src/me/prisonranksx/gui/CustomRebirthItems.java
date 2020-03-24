package me.prisonranksx.gui;

import java.util.HashMap;
import java.util.Map;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.LevelType;

public class CustomRebirthItems {

	private Map<RebirthState, RebirthItem> customRebirthItems;
	private PrisonRanksX main;
	
	public CustomRebirthItems(PrisonRanksX main) {this.customRebirthItems = new HashMap<>(); this.main = main;}
	
	public Map<RebirthState, RebirthItem> getCustomRebirthItems() {
		return this.customRebirthItems;
	}
	
	public void setup() {
		for(String rebirth : main.rebirthStorage.getRebirthData().keySet()) {
			for(int i = 0; i < 3; i++) {
				LevelState ls = LevelState.values()[i];
				if(main.cim.hasCustomLevelItem(LevelType.REBIRTH, ls) && main.cim.hasCustomFormat(LevelType.REBIRTH, ls, rebirth)) {
					RebirthItem ri = new RebirthItem();
					RebirthState rs = new RebirthState();
					rs.setLevelState(ls);
					rs.setRebirth(rebirth);
					ri.setMaterial(main.cim.readCustomLevelItemName(LevelType.REBIRTH, ls, rebirth));
					ri.setAmount(main.cim.readCustomLevelItemAmount(LevelType.REBIRTH, ls, rebirth));
					ri.setDisplayName(main.cim.readCustomLevelItemDisplayName(LevelType.REBIRTH, ls, rebirth));
					ri.setLore(main.cim.readCustomLevelItemLore(LevelType.REBIRTH, ls, rebirth));
					ri.setEnchantments(main.cim.readCustomLevelItemEnchantments(LevelType.REBIRTH, ls, rebirth));
					ri.setFlags(main.cim.readCustomLevelItemFlags(LevelType.REBIRTH, ls, rebirth));
					ri.setCommands(main.cim.readCustomLevelItemCommands(LevelType.REBIRTH, ls, rebirth));
					customRebirthItems.put(rs, ri);
				}
			}
		}
	}
	
}
