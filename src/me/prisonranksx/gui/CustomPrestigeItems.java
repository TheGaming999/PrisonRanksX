package me.prisonranksx.gui;

import java.util.HashMap;
import java.util.Map;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.GlobalDataStorage;
import me.prisonranksx.data.LevelType;

public class CustomPrestigeItems {

	private Map<String, PrestigeItem> customPrestigeItems;
	private PrisonRanksX main;
	
	public CustomPrestigeItems(PrisonRanksX main) {this.customPrestigeItems = new HashMap<>(); this.main = main;}
	
	public Map<String, PrestigeItem> getCustomPrestigeItems() {
		return this.customPrestigeItems;
	}
	
	public GlobalDataStorage gds() {
		return main.getGlobalStorage();
	}
	
	public void setup() {
		for(String prestige : main.prestigeStorage.getPrestigeData().keySet()) {
			for(int i = 0; i < 3; i++) {
				LevelState ls = LevelState.values()[i];
				if(main.getCustomItemsManager().hasCustomLevelItem(LevelType.PRESTIGE, ls) && main.getCustomItemsManager().hasCustomFormat(LevelType.PRESTIGE, ls, prestige)) {
					PrestigeItem pi = new PrestigeItem();
					PrestigeState ps = new PrestigeState();
					ps.setLevelState(ls);
					ps.setPrestige(prestige);
					pi.setMaterial(main.getCustomItemsManager().readCustomLevelItemName(LevelType.PRESTIGE, ls, prestige));
					pi.setAmount(main.getCustomItemsManager().readCustomLevelItemAmount(LevelType.PRESTIGE, ls, prestige));
					pi.setDisplayName(gds().translateHexColorCodes(main.getCustomItemsManager().readCustomLevelItemDisplayName(LevelType.PRESTIGE, ls, prestige)));
					pi.setLore(gds().translateHexColorCodes(main.getCustomItemsManager().readCustomLevelItemLore(LevelType.PRESTIGE, ls, prestige)));
					pi.setEnchantments(main.getCustomItemsManager().readCustomLevelItemEnchantments(LevelType.PRESTIGE, ls, prestige));
					pi.setFlags(main.getCustomItemsManager().readCustomLevelItemFlags(LevelType.PRESTIGE, ls, prestige));
					pi.setCommands(gds().translateHexColorCodes(main.getCustomItemsManager().readCustomLevelItemCommands(LevelType.PRESTIGE, ls, prestige)));
					customPrestigeItems.put(ps.toString(), pi);
				}
			}
		}
	}
	
}
