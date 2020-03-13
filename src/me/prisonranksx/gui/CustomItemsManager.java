package me.prisonranksx.gui;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.LevelType;
import me.prisonranksx.utils.NumberAPI;
import me.prisonranksx.utils.XMaterial;

public class CustomItemsManager {
	
	
           PrisonRanksX plugin = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
           NumberAPI numberAPI = null;
	public CustomItemsManager() {numberAPI = new NumberAPI();}
	
	public String c(String stringValue) {
		return ChatColor.translateAlternateColorCodes('&', stringValue);
	}
	
	public String readCustomLevelItemName(LevelType levelType, LevelState levelState,String name) {
		String levelName = StringUtils.capitalize(levelType.name().toLowerCase());
		String stateName = levelState.name().toLowerCase();
		ConfigurationSection section = (ConfigurationSection)plugin.globalStorage.getMap().get(levelName + "list-gui." + stateName + "-format.custom").get(name);   
		return section.getString("itemNAME");
	}
	
	public int readCustomLevelItemAmount(LevelType levelType, LevelState levelState,String name) {
		String levelName = StringUtils.capitalize(levelType.name().toLowerCase());
		String stateName = levelState.name().toLowerCase();
		ConfigurationSection section = (ConfigurationSection)plugin.globalStorage.getMap().get(levelName + "list-gui." + stateName + "-format.custom").get(name);
		return section.getInt("itemAMOUNT");
	}
	
	public String readCustomLevelItemDisplayName(LevelType levelType, LevelState levelState,String name) {
		String levelName = StringUtils.capitalize(levelType.name().toLowerCase());
		String stateName = levelState.name().toLowerCase();
		ConfigurationSection section = (ConfigurationSection)plugin.globalStorage.getMap().get(levelName + "list-gui." + stateName + "-format.custom").get(name);
		return section.getString("itemDISPLAYNAME");
	}
	
	public List<String> readCustomLevelItemLore(LevelType levelType, LevelState levelState,String name) {
		String levelName = StringUtils.capitalize(levelType.name().toLowerCase());
		String stateName = levelState.name().toLowerCase();
		ConfigurationSection section = (ConfigurationSection)plugin.globalStorage.getMap().get(levelName + "list-gui." + stateName + "-format.custom").get(name);
		return section.getStringList("itemLORE");
	}
	
	public List<String> readCustomLevelItemEnchantments(LevelType levelType, LevelState levelState,String name) {
		String levelName = StringUtils.capitalize(levelType.name().toLowerCase());
		String stateName = levelState.name().toLowerCase();
		ConfigurationSection section = (ConfigurationSection)plugin.globalStorage.getMap().get(levelName + "list-gui." + stateName + "-format.custom").get(name);
		return section.getStringList("itemENCHANTMENTS");
	}
	
	public List<String> readCustomLevelItemFlags(LevelType levelType, LevelState levelState,String name) {
		String levelName = StringUtils.capitalize(levelType.name().toLowerCase());
		String stateName = levelState.name().toLowerCase();
		ConfigurationSection section = (ConfigurationSection)plugin.globalStorage.getMap().get(levelName + "list-gui." + stateName + "-format.custom").get(name);
		return section.getStringList("itemFLAGS");
	}
	
	public List<String> readCustomLevelItemCommands(LevelType levelType, LevelState levelState,String name) {
		String levelName = StringUtils.capitalize(levelType.name().toLowerCase());
		String stateName = levelState.name().toLowerCase();
		ConfigurationSection section = (ConfigurationSection)plugin.globalStorage.getMap().get(levelName + "list-gui." + stateName + "-format.custom").get(name);
		return section.getStringList("itemCOMMANDS");
	}
	
	public ItemStack readCustomItem(String stringValue) {
		ItemStack stringStack = new ItemStack(Material.STONE, 1);
		ItemMeta stackMeta = stringStack.getItemMeta();;
		int amount = 1;
		String displayName = "";
		List<String> lore = new ArrayList<>();
		for(String stringMeta : stringValue.split(" ")) {
			if(stringMeta.startsWith("item=")) { //item stack with data support
				String itemNameWithData = stringMeta.substring(5);
				if(itemNameWithData.contains(":")) {
					String itemName = itemNameWithData.split(":")[0];
					byte itemData = Byte.parseByte(itemNameWithData.split(":")[1]);
					stringStack = XMaterial.matchXMaterial(itemName, itemData).parseItem(true);
				} else {
					String itemName = itemNameWithData;
					stringStack = XMaterial.matchXMaterial(itemName).parseItem(true);
				}
				stackMeta = stringStack.getItemMeta();
			} //item stack check
			if(stringMeta.startsWith("amount=")) {
              if(numberAPI.isNumber(stringMeta.substring(7))) {
            	  amount = Integer.parseInt(stringMeta.substring(7));
              } else {
            	  plugin.getLogger().info("amount=<?> is not a number setting it to 1 by default");
            	  amount = 1;
              }
              stringStack.setAmount(amount);
			} //amount check
			if(stringMeta.startsWith("name=")) {
				displayName = c(stringMeta.substring(5));
				stackMeta.setDisplayName(displayName.replace("_", " "));
			} //name check
			if(stringMeta.startsWith("lore=")) {
				String fullLore = stringMeta.substring(5);
				if(fullLore.contains("@")) {
					for(String loreLine : fullLore.split("@")) {
						lore.add(c(loreLine).replace("_", " "));
					}
				} else {
					lore.add(c(fullLore).replace("_", " "));
				}
				stackMeta.setLore(lore);
			} //lore check
			if(stringMeta.startsWith("enchantments=")) {
				String fullEnchantmentWithLvl = stringMeta.substring(13);
				if(fullEnchantmentWithLvl.contains("@")) {
					for(String singleEnchantmentWithLvl : fullEnchantmentWithLvl.split("@")) {
						String enchantment = singleEnchantmentWithLvl.split(":")[0];
						Integer lvl = Integer.valueOf(singleEnchantmentWithLvl.split(":")[1]);
						stackMeta.addEnchant(EnchantmentReader.matchEnchantment(enchantment), lvl, true);
					}
				} else {
					String enchantment = fullEnchantmentWithLvl.split(":")[0];
					Integer lvl = Integer.valueOf(fullEnchantmentWithLvl.split(":")[1]);
					stackMeta.addEnchant(EnchantmentReader.matchEnchantment(enchantment), lvl, true);
				}
			} //enchantments check
			if(stringMeta.startsWith("flags=")) {
				String flagsList = stringMeta.substring(6);
				if(flagsList.contains("@")) {
					for(String singleFlag : flagsList.split("@")) {
						stackMeta.addItemFlags(ItemFlagReader.matchItemFlag(singleFlag));
					}
				} else {
					stackMeta.addItemFlags(ItemFlagReader.matchItemFlag(flagsList));
				}
			} //item flags check
			stringStack.setItemMeta(stackMeta);
		}
		return stringStack;
	}
	
	public int readCustomItemSlot(String stringValue) {
		for(String stringMeta : stringValue.split(" ")) {
			if(stringMeta.startsWith("slot=")) {
				Integer slotNumber = Integer.parseInt(stringMeta.substring(5));
				return slotNumber;
			}
		}
		return 0;
	}
	
	public int readCustomItemPage(String stringValue) {
		for(String stringMeta : stringValue.split(" ")) {
			if(stringMeta.startsWith("page=")) {
				Integer pageNumber = Integer.parseInt(stringMeta.substring(5));
				return pageNumber;
			}
		}
		return 1;
	}
}
