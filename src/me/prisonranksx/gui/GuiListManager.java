package me.prisonranksx.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cloutteam.samjakob.gui.buttons.GUIButton;
import cloutteam.samjakob.gui.types.PaginatedGUI;
import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.utils.XMaterial;

public class GuiListManager {

	public PrisonRanksX main;
	public PaginatedGUI ranksGUI;
	public PaginatedGUI prestigesGUI;
	public PaginatedGUI rebirthsGUI;
	public List<Integer> allowedRankSlots, allowedPrestigeSlots, allowedRebirthSlots;
	public GuiListManager(PrisonRanksX main) {this.main = main;
	ranksGUI = new PaginatedGUI(this.main.prxAPI.c(this.main.globalStorage.getStringData("Ranklist-gui.title")));
	prestigesGUI = new PaginatedGUI(this.main.prxAPI.c(this.main.globalStorage.getStringData("Prestigelist-gui.title")));
	rebirthsGUI = new PaginatedGUI(this.main.prxAPI.c(this.main.globalStorage.getStringData("Rebirthlist-gui.title")));
	allowedRankSlots = new ArrayList<>();
	allowedPrestigeSlots = new ArrayList<>();
	allowedRebirthSlots = new ArrayList<>();
	}
	
	public void setupConstantItems() {
		if(!this.main.globalStorage.getStringListData("Ranklist-gui.constant-items").isEmpty()) {
			List<String> constantItems = main.globalStorage.getStringListData("Ranklist-gui.constant-items");
			for(String item : constantItems) {
				GUIButton button = new GUIButton(main.cim.readCustomItem(item));
				button.setListener(event -> {event.setCancelled(true);});
				int slot = main.cim.readCustomItemSlot(item);
				int page = main.cim.readCustomItemPage(item);
				ranksGUI.setButton(slot * page, button);
			}
			int i1 = -1;
			for(ItemStack constantStack : ranksGUI.getInventory().getContents()) {
				i1++;
				if(constantStack == null || constantStack.getType() == Material.AIR) {
					allowedRankSlots.add(i1);
				}
			}
		}

		if(!this.main.globalStorage.getStringListData("Prestigelist-gui.constant-items").isEmpty()) {
			List<String> constantItems = main.globalStorage.getStringListData("Prestigelist-gui.constant-items");
			for(String item : constantItems) {
				GUIButton button = new GUIButton(main.cim.readCustomItem(item));
				button.setListener(event -> {event.setCancelled(true);});
				int slot = main.cim.readCustomItemSlot(item);
				int page = main.cim.readCustomItemPage(item);
				prestigesGUI.setButton(slot * page, button);
			}
			int i2 = -1;
			for(ItemStack constantStack : prestigesGUI.getInventory().getContents()) {
				i2++;
				if(constantStack == null || constantStack.getType() == Material.AIR) {
					allowedPrestigeSlots.add(i2);
				}
			}
		}

		if(!this.main.globalStorage.getStringListData("Rebirthlist-gui.constant-items").isEmpty()) {
			List<String> constantItems = main.globalStorage.getStringListData("Rebirthlist-gui.constant-items");
			for(String item : constantItems) {
				GUIButton button = new GUIButton(main.cim.readCustomItem(item));
				button.setListener(event -> {event.setCancelled(true);});
				int slot = main.cim.readCustomItemSlot(item);
				int page = main.cim.readCustomItemPage(item);
				rebirthsGUI.setButton(slot * page, button);
			}
			int i3 = -1;
			for(ItemStack constantStack : rebirthsGUI.getInventory().getContents()) {
				i3++;
				if(constantStack == null || constantStack.getType() == Material.AIR) {
					allowedRebirthSlots.add(i3);
				}
			}
		}
	}
	
	public void openRanksGUI(Player player) {
		Player p = player;
		RankPath rp = main.prxAPI.getPlayerRankPath(p);
		String playerRank = rp.getRankName();
		String playerPath = rp.getPathName();
		List<String> ranksCollection = main.prxAPI.getRanksCollection(playerPath);
		int playerRankIndex = ranksCollection.indexOf(playerRank);
		String playerPrestige = main.prxAPI.getPlayerPrestige(p);
		for(String rank : ranksCollection) {
			int rankIndex = ranksCollection.indexOf(rank);
			if(playerRankIndex > rankIndex) { // if completed
				// placeholders {
				String rankName = rank;
				RankPath xrp = RankPath.getRankPath(rankName + "#~#" + playerPath);
				String rankDisplayName = main.prxAPI.c(main.prxAPI.getRankDisplay(xrp));
				double rankCostNumber = (main.prxAPI.getIncreasedRankupCostNB(playerPrestige, xrp));
				String rankCost = String.valueOf(rankCostNumber);
				String formattedRankCost = main.formatBalance(rankCostNumber);
				// }
				String itemName = main.globalStorage.getStringData("Ranklist-gui.completed-format.itemNAME");
				int itemAmount = main.globalStorage.getIntegerData("Ranklist-gui.completed-format.itemAMOUNT");
				String itemDisplayName = main.globalStorage.getStringData("Ranklist-gui.completed-format.itemDISPLAYNAME").replace("%completedrank%", rankName)
						.replace("%completedrank_display%", rankDisplayName)
						.replace("%completedrank_cost%", rankCost)
						.replace("%completedrank_cost_formatted%", formattedRankCost);
				List<String> itemLore = main.globalStorage.getStringListData("Ranklist-gui.completed-format.itemLORE");
				List<String> itemEnchantments = main.globalStorage.getStringListData("Ranklist-gui.completed-format.itemENCHANTMENTS");
				List<String> itemFlags = main.globalStorage.getStringListData("Ranklist-gui.completed-format.itemFLAGS");
				List<String> itemCommands = main.globalStorage.getStringListData("Ranklist-gui.completed-format.itemCOMMANDS");
				List<String> realItemCommands = new ArrayList<String>();
				List<String> coloredItemLore = new ArrayList<String>();
				ItemStack completedItem = XMaterial.matchXMaterial(itemName).parseItem(true);
				completedItem.setAmount(itemAmount);
				ItemMeta completedMeta = completedItem.getItemMeta();
				completedMeta.setDisplayName(main.prxAPI.c(itemDisplayName));
				itemLore.forEach(line -> {coloredItemLore.add(main.getString(line.replace("%completedrank%", rankName)
						.replace("%completedrank_display%", rankDisplayName)	.replace("%completedrank_cost%", rankCost)
						.replace("%completedrank_cost_formatted%", formattedRankCost), p.getName()));});
				completedMeta.setLore(coloredItemLore);
				itemEnchantments.forEach(line -> {
					String[] splittedLine = line.split(" ");
					Enchantment enchant = EnchantmentReader.matchEnchantment(splittedLine[0]);
					int lvl = Integer.valueOf(splittedLine[1]);
					completedMeta.addEnchant(enchant, lvl, true);
				});
				itemFlags.forEach(line -> {completedMeta.addItemFlags(ItemFlagReader.matchItemFlag(line));});
				completedItem.setItemMeta(completedMeta);
				GUIButton completedButton = new GUIButton(completedItem);
				completedButton.setListener(event -> {
					if(itemCommands.contains("(cancel-item_move)")) {
						event.setCancelled(true);
					}
					if(itemCommands.contains("(closeinv)")) {
						p.closeInventory();
					}
                    for(String command : itemCommands) {
                    	if(command.startsWith("[")) {
                    		realItemCommands.add(main.getString(command
                    				.replace("%completedrank%", rankName).replace("%completedrank_display%", rankName).replace("%completedrank_cost%", rankCost)
            						.replace("%completedrank_cost_formatted%", formattedRankCost), p.getName()));
                    	}
                    }
                    main.executeCommands(p, realItemCommands);
				});
				if(!allowedRankSlots.isEmpty()) {
			        ranksGUI.setButton(allowedRankSlots.get(rankIndex), completedButton);
					} else {
						ranksGUI.setButton(rankIndex, completedButton);
					}
		        
			}
			if(playerRankIndex == rankIndex) { // if current
				// placeholders {
				String rankName = rank;
				RankPath xrp = RankPath.getRankPath(rankName + "#~#" + playerPath);
				String rankDisplayName = main.prxAPI.c(main.prxAPI.getRankDisplay(xrp));
				double rankCostNumber = (main.prxAPI.getIncreasedRankupCostNB(playerPrestige, xrp));
				String rankCost = String.valueOf(rankCostNumber);
				String formattedRankCost = main.formatBalance(rankCostNumber);
				// }
				String itemName = main.globalStorage.getStringData("Ranklist-gui.current-format.itemNAME");
				int itemAmount = main.globalStorage.getIntegerData("Ranklist-gui.current-format.itemAMOUNT");
				String itemDisplayName = main.globalStorage.getStringData("Ranklist-gui.current-format.itemDISPLAYNAME").replace("%currentrank%", rankName)
						.replace("%currentrank_display%", rankDisplayName)
						.replace("%currentrank_cost%", rankCost)
						.replace("%currentrank_cost_formatted%", formattedRankCost);
				List<String> itemLore = main.globalStorage.getStringListData("Ranklist-gui.current-format.itemLORE");
				List<String> itemEnchantments = main.globalStorage.getStringListData("Ranklist-gui.current-format.itemENCHANTMENTS");
				List<String> itemFlags = main.globalStorage.getStringListData("Ranklist-gui.current-format.itemFLAGS");
				List<String> itemCommands = main.globalStorage.getStringListData("Ranklist-gui.current-format.itemCOMMANDS");
				List<String> realItemCommands = new ArrayList<String>();
				List<String> coloredItemLore = new ArrayList<String>();
				ItemStack currentItem = XMaterial.matchXMaterial(itemName).parseItem(true);
				currentItem.setAmount(itemAmount);
				ItemMeta currentMeta = currentItem.getItemMeta();
				currentMeta.setDisplayName(main.prxAPI.c(itemDisplayName));
				itemLore.forEach(line -> {coloredItemLore.add(main.getString(line.replace("%currentrank%", rankName)
						.replace("%currentrank_display%", rankDisplayName).replace("%currentrank_cost%", rankCost)
						.replace("%currentrank_cost_formatted%", formattedRankCost), p.getName()));});
				currentMeta.setLore(coloredItemLore);
				itemEnchantments.forEach(line -> {
					String[] splittedLine = line.split(" ");
					Enchantment enchant = EnchantmentReader.matchEnchantment(splittedLine[0]);
					int lvl = Integer.valueOf(splittedLine[1]);
					currentMeta.addEnchant(enchant, lvl, true);
				});
				itemFlags.forEach(line -> {currentMeta.addItemFlags(ItemFlagReader.matchItemFlag(line));});
				currentItem.setItemMeta(currentMeta);
				GUIButton currentButton = new GUIButton(currentItem);
				currentButton.setListener(event -> {
					if(itemCommands.contains("(cancel-item_move)")) {
						event.setCancelled(true);
					}
					if(itemCommands.contains("(closeinv)")) {
						p.closeInventory();
					}
                    for(String command : itemCommands) {
                    	if(command.startsWith("[")) {
                    		realItemCommands.add(main.getString(command
                    				.replace("%currentrank%", rankName).replace("%currentrank_display%", rankName)	.replace("%currentrank_cost%", rankCost)
            						.replace("%currentrank_cost_formatted%", formattedRankCost), p.getName()));
                    	}
                    }
                    main.executeCommands(p, realItemCommands);
				});
				if(!allowedRankSlots.isEmpty()) {
			        ranksGUI.setButton(allowedRankSlots.get(rankIndex), currentButton);
					} else {
						ranksGUI.setButton(rankIndex, currentButton);
					}
			}
			if(playerRankIndex < rankIndex) { // if not completed
				// placeholders {
				String rankName = rank;
				RankPath xrp = RankPath.getRankPath(rankName + "#~#" + playerPath);
				String rankDisplayName = main.prxAPI.c(main.prxAPI.getRankDisplay(xrp));
				double rankCostNumber = (main.prxAPI.getIncreasedRankupCostNB(playerPrestige, xrp));
				String rankCost = String.valueOf(rankCostNumber);
				String formattedRankCost = main.formatBalance(rankCostNumber);
				// }
				String itemName = main.globalStorage.getStringData("Ranklist-gui.other-format.itemNAME");
				int itemAmount = main.globalStorage.getIntegerData("Ranklist-gui.other-format.itemAMOUNT");
				String itemDisplayName = main.globalStorage.getStringData("Ranklist-gui.other-format.itemDISPLAYNAME").replace("%otherrank%", rankName)
						.replace("%otherrank_display%", rankDisplayName)
						.replace("%otherrank_cost%", rankCost)
						.replace("%otherrank_cost_formatted%", formattedRankCost);
				List<String> itemLore = main.globalStorage.getStringListData("Ranklist-gui.other-format.itemLORE");
				List<String> itemEnchantments = main.globalStorage.getStringListData("Ranklist-gui.other-format.itemENCHANTMENTS");
				List<String> itemFlags = main.globalStorage.getStringListData("Ranklist-gui.other-format.itemFLAGS");
				List<String> itemCommands = main.globalStorage.getStringListData("Ranklist-gui.other-format.itemCOMMANDS");
				List<String> realItemCommands = new ArrayList<String>();
				List<String> coloredItemLore = new ArrayList<String>();
				ItemStack otherItem = XMaterial.matchXMaterial(itemName).parseItem(true);
				otherItem.setAmount(itemAmount);
				ItemMeta otherMeta = otherItem.getItemMeta();
				otherMeta.setDisplayName(main.prxAPI.c(itemDisplayName));
				itemLore.forEach(line -> {coloredItemLore.add(main.getString(line.replace("%otherrank%", rankName)
						.replace("%otherrank_display%", rankDisplayName).replace("%otherrank_cost%", rankCost)
						.replace("%otherrank_cost_formatted%", formattedRankCost), p.getName()));});
				otherMeta.setLore(coloredItemLore);
				itemEnchantments.forEach(line -> {
					String[] splittedLine = line.split(" ");
					Enchantment enchant = EnchantmentReader.matchEnchantment(splittedLine[0]);
					int lvl = Integer.valueOf(splittedLine[1]);
					otherMeta.addEnchant(enchant, lvl, true);
				});
				itemFlags.forEach(line -> {otherMeta.addItemFlags(ItemFlagReader.matchItemFlag(line));});
				otherItem.setItemMeta(otherMeta);
				GUIButton otherButton = new GUIButton(otherItem);
				otherButton.setListener(event -> {
					if(itemCommands.contains("(cancel-item_move)")) {
						event.setCancelled(true);
					}
					if(itemCommands.contains("(closeinv)")) {
						p.closeInventory();
					}
                    for(String command : itemCommands) {
                    	if(command.startsWith("[")) {
                    		realItemCommands.add(main.getString(command
                    				.replace("%otherrank%", rankName).replace("%otherrank_display%", rankName).replace("%otherrank_cost%", rankCost)
            						.replace("%otherrank_cost_formatted%", formattedRankCost), p.getName()));
                    	}
                    }
                    main.executeCommands(p, realItemCommands);
				});
				if(!allowedRankSlots.isEmpty()) {
		        ranksGUI.setButton(allowedRankSlots.get(rankIndex), otherButton);
				} else {
					ranksGUI.setButton(rankIndex, otherButton);
				}
			}
		}
		p.openInventory(ranksGUI.getInventory());
	}
	
	public void openPrestigesGUI(Player player) {
		Player p = player;
		String playerPrestige = main.prxAPI.getPlayerPrestige(p);
		List<String> prestigesCollection = main.prxAPI.getPrestigesCollection();
		int playerPrestigeIndex = prestigesCollection.indexOf(playerPrestige);
		for(String prestige : prestigesCollection) {
			int prestigeIndex = prestigesCollection.indexOf(prestige);
			if(playerPrestigeIndex > prestigeIndex) { // if completed
				// placeholders {
				String prestigeName = prestige;
				String prestigeDisplayName = main.prxAPI.c(main.prestigeStorage.getDisplayName(prestigeName));
				double prestigeCostNumber = (main.prestigeStorage.getCost(prestigeName));
				String prestigeCost = String.valueOf(prestigeCostNumber);
				String formattedPrestigeCost = main.formatBalance(prestigeCostNumber);
				// }
				String itemName = main.globalStorage.getStringData("Prestigelist-gui.completed-format.itemNAME");
				int itemAmount = main.globalStorage.getIntegerData("Prestigelist-gui.completed-format.itemAMOUNT");
				String itemDisplayName = main.globalStorage.getStringData("Prestigelist-gui.completed-format.itemDISPLAYNAME").replace("%completedprestige%", prestigeName)
						.replace("%completedprestige_display%", prestigeDisplayName)
						.replace("%completedprestige_cost%", prestigeCost)
						.replace("%completedprestige_cost_formatted%", formattedPrestigeCost);
				List<String> itemLore = main.globalStorage.getStringListData("Prestigelist-gui.completed-format.itemLORE");
				List<String> itemEnchantments = main.globalStorage.getStringListData("Prestigelist-gui.completed-format.itemENCHANTMENTS");
				List<String> itemFlags = main.globalStorage.getStringListData("Prestigelist-gui.completed-format.itemFLAGS");
				List<String> itemCommands = main.globalStorage.getStringListData("Prestigelist-gui.completed-format.itemCOMMANDS");
				List<String> realItemCommands = new ArrayList<String>();
				List<String> coloredItemLore = new ArrayList<String>();
				ItemStack completedItem = XMaterial.matchXMaterial(itemName).parseItem(true);
				completedItem.setAmount(itemAmount);
				ItemMeta completedMeta = completedItem.getItemMeta();
				completedMeta.setDisplayName(main.prxAPI.c(itemDisplayName));
				itemLore.forEach(line -> {coloredItemLore.add(main.getString(line.replace("%completedprestige%", prestigeName)
						.replace("%completedprestige_display%", prestigeDisplayName)	.replace("%completedprestige_cost%", prestigeCost)
						.replace("%completedprestige_cost_formatted%", formattedPrestigeCost), p.getName()));});
				completedMeta.setLore(coloredItemLore);
				itemEnchantments.forEach(line -> {
					String[] splittedLine = line.split(" ");
					Enchantment enchant = EnchantmentReader.matchEnchantment(splittedLine[0]);
					int lvl = Integer.valueOf(splittedLine[1]);
					completedMeta.addEnchant(enchant, lvl, true);
				});
				itemFlags.forEach(line -> {completedMeta.addItemFlags(ItemFlagReader.matchItemFlag(line));});
				completedItem.setItemMeta(completedMeta);
				GUIButton completedButton = new GUIButton(completedItem);
				completedButton.setListener(event -> {
					if(itemCommands.contains("(cancel-item_move)")) {
						event.setCancelled(true);
					}
					if(itemCommands.contains("(closeinv)")) {
						p.closeInventory();
					}
                    for(String command : itemCommands) {
                    	if(command.startsWith("[")) {
                    		realItemCommands.add(main.getString(command
                    				.replace("%completedprestige%", prestigeName).replace("%completedprestige_display%", prestigeName).replace("%completedprestige_cost%", prestigeCost)
            						.replace("%completedprestige_cost_formatted%", formattedPrestigeCost), p.getName()));
                    	}
                    }
                    main.executeCommands(p, realItemCommands);
				});
				if(!allowedPrestigeSlots.isEmpty()) {
		        prestigesGUI.setButton(allowedPrestigeSlots.get(prestigeIndex), completedButton);
			    } else {
			    	prestigesGUI.setButton(prestigeIndex, completedButton);
			    }
			}
			if(playerPrestigeIndex == prestigeIndex) { // if current
				// placeholders {
				String prestigeName = prestige;
				String prestigeDisplayName = main.prxAPI.c(main.prestigeStorage.getDisplayName(prestigeName));
				double prestigeCostNumber = (main.prestigeStorage.getCost(prestigeName));
				String prestigeCost = String.valueOf(prestigeCostNumber);
				String formattedPrestigeCost = main.formatBalance(prestigeCostNumber);
				// }
				String itemName = main.globalStorage.getStringData("Prestigelist-gui.current-format.itemNAME");
				int itemAmount = main.globalStorage.getIntegerData("Prestigelist-gui.current-format.itemAMOUNT");
				String itemDisplayName = main.globalStorage.getStringData("Prestigelist-gui.current-format.itemDISPLAYNAME").replace("%currentprestige%", prestigeName)
						.replace("%currentprestige_display%", prestigeDisplayName)
						.replace("%currentprestige_cost%", prestigeCost)
						.replace("%currentprestige_cost_formatted%", formattedPrestigeCost);
				List<String> itemLore = main.globalStorage.getStringListData("Prestigelist-gui.current-format.itemLORE");
				List<String> itemEnchantments = main.globalStorage.getStringListData("Prestigelist-gui.current-format.itemENCHANTMENTS");
				List<String> itemFlags = main.globalStorage.getStringListData("Prestigelist-gui.current-format.itemFLAGS");
				List<String> itemCommands = main.globalStorage.getStringListData("Prestigelist-gui.current-format.itemCOMMANDS");
				List<String> realItemCommands = new ArrayList<String>();
				List<String> coloredItemLore = new ArrayList<String>();
				ItemStack currentItem = XMaterial.matchXMaterial(itemName).parseItem(true);
				currentItem.setAmount(itemAmount);
				ItemMeta currentMeta = currentItem.getItemMeta();
				currentMeta.setDisplayName(main.prxAPI.c(itemDisplayName));
				itemLore.forEach(line -> {coloredItemLore.add(main.getString(line.replace("%currentprestige%", prestigeName)
						.replace("%currentprestige_display%", prestigeDisplayName).replace("%currentprestige_cost%", prestigeCost)
						.replace("%currentprestige_cost_formatted%", formattedPrestigeCost), p.getName()));});
				currentMeta.setLore(coloredItemLore);
				itemEnchantments.forEach(line -> {
					String[] splittedLine = line.split(" ");
					Enchantment enchant = EnchantmentReader.matchEnchantment(splittedLine[0]);
					int lvl = Integer.valueOf(splittedLine[1]);
					currentMeta.addEnchant(enchant, lvl, true);
				});
				itemFlags.forEach(line -> {currentMeta.addItemFlags(ItemFlagReader.matchItemFlag(line));});
				currentItem.setItemMeta(currentMeta);
				GUIButton currentButton = new GUIButton(currentItem);
				currentButton.setListener(event -> {
					if(itemCommands.contains("(cancel-item_move)")) {
						event.setCancelled(true);
					}
					if(itemCommands.contains("(closeinv)")) {
						p.closeInventory();
					}
                    for(String command : itemCommands) {
                    	if(command.startsWith("[")) {
                    		realItemCommands.add(main.getString(command
                    				.replace("%currentprestige%", prestigeName).replace("%currentprestige_display%", prestigeName)	.replace("%currentprestige_cost%", prestigeCost)
            						.replace("%currentprestige_cost_formatted%", formattedPrestigeCost), p.getName()));
                    	}
                    }
                    main.executeCommands(p, realItemCommands);
				});
				if(!allowedPrestigeSlots.isEmpty()) {
			        prestigesGUI.setButton(allowedPrestigeSlots.get(prestigeIndex), currentButton);
				    } else {
				    	prestigesGUI.setButton(prestigeIndex, currentButton);
				    }
			}
			if(playerPrestigeIndex < prestigeIndex) { // if not completed
				// placeholders {
				String prestigeName = prestige;
				String prestigeDisplayName = main.prxAPI.c(main.prestigeStorage.getDisplayName(prestigeName));
				double prestigeCostNumber = (main.prestigeStorage.getCost(prestigeName));
				String prestigeCost = String.valueOf(prestigeCostNumber);
				String formattedPrestigeCost = main.formatBalance(prestigeCostNumber);
				// }
				String itemName = main.globalStorage.getStringData("Prestigelist-gui.other-format.itemNAME");
				int itemAmount = main.globalStorage.getIntegerData("Prestigelist-gui.other-format.itemAMOUNT");
				String itemDisplayName = main.globalStorage.getStringData("Prestigelist-gui.other-format.itemDISPLAYNAME").replace("%otherprestige%", prestigeName)
						.replace("%otherprestige_display%", prestigeDisplayName)
						.replace("%otherprestige_cost%", prestigeCost)
						.replace("%otherprestige_cost_formatted%", formattedPrestigeCost);
				List<String> itemLore = main.globalStorage.getStringListData("Prestigelist-gui.other-format.itemLORE");
				List<String> itemEnchantments = main.globalStorage.getStringListData("Prestigelist-gui.other-format.itemENCHANTMENTS");
				List<String> itemFlags = main.globalStorage.getStringListData("Prestigelist-gui.other-format.itemFLAGS");
				List<String> itemCommands = main.globalStorage.getStringListData("Prestigelist-gui.other-format.itemCOMMANDS");
				List<String> realItemCommands = new ArrayList<String>();
				List<String> coloredItemLore = new ArrayList<String>();
				ItemStack otherItem = XMaterial.matchXMaterial(itemName).parseItem(true);
				otherItem.setAmount(itemAmount);
				ItemMeta otherMeta = otherItem.getItemMeta();
				otherMeta.setDisplayName(main.prxAPI.c(itemDisplayName));
				itemLore.forEach(line -> {coloredItemLore.add(main.getString(line.replace("%otherprestige%", prestigeName)
						.replace("%otherprestige_display%", prestigeDisplayName).replace("%otherprestige_cost%", prestigeCost)
						.replace("%otherprestige_cost_formatted%", formattedPrestigeCost), p.getName()));});
				otherMeta.setLore(coloredItemLore);
				itemEnchantments.forEach(line -> {
					String[] splittedLine = line.split(" ");
					Enchantment enchant = EnchantmentReader.matchEnchantment(splittedLine[0]);
					int lvl = Integer.valueOf(splittedLine[1]);
					otherMeta.addEnchant(enchant, lvl, true);
				});
				itemFlags.forEach(line -> {otherMeta.addItemFlags(ItemFlagReader.matchItemFlag(line));});
				otherItem.setItemMeta(otherMeta);
				GUIButton otherButton = new GUIButton(otherItem);
				otherButton.setListener(event -> {
					if(itemCommands.contains("(cancel-item_move)")) {
						event.setCancelled(true);
					}
					if(itemCommands.contains("(closeinv)")) {
						p.closeInventory();
					}
                    for(String command : itemCommands) {
                    	if(command.startsWith("[")) {
                    		realItemCommands.add(main.getString(command
                    				.replace("%otherprestige%", prestigeName).replace("%otherprestige_display%", prestigeName).replace("%otherprestige_cost%", prestigeCost)
            						.replace("%otherprestige_cost_formatted%", formattedPrestigeCost), p.getName()));
                    	}
                    }
                    main.executeCommands(p, realItemCommands);
				});
				if(!allowedPrestigeSlots.isEmpty()) {
			        prestigesGUI.setButton(allowedPrestigeSlots.get(prestigeIndex), otherButton);
				    } else {
				    	prestigesGUI.setButton(prestigeIndex, otherButton);
				    }
			}
		}
		p.openInventory(prestigesGUI.getInventory());
	}
	
	public void openRebirthsGUI(Player player) {
		Player p = player;
		String playerRebirth = main.prxAPI.getPlayerRebirth(p);
		List<String> rebirthsCollection = main.prxAPI.getRebirthsCollection();
		int playerRebirthIndex = rebirthsCollection.indexOf(playerRebirth);
		for(String rebirth : rebirthsCollection) {
			int rebirthIndex = rebirthsCollection.indexOf(rebirth);
			if(playerRebirthIndex > rebirthIndex) { // if completed
				// placeholders {
				String rebirthName = rebirth;
				String rebirthDisplayName = main.prxAPI.c(main.rebirthStorage.getDisplayName(rebirthName));
				double rebirthCostNumber = (main.rebirthStorage.getCost(rebirthName));
				String rebirthCost = String.valueOf(rebirthCostNumber);
				String formattedRebirthCost = main.formatBalance(rebirthCostNumber);
				// }
				String itemName = main.globalStorage.getStringData("Rebirthlist-gui.completed-format.itemNAME");
				int itemAmount = main.globalStorage.getIntegerData("Rebirthlist-gui.completed-format.itemAMOUNT");
				String itemDisplayName = main.globalStorage.getStringData("Rebirthlist-gui.completed-format.itemDISPLAYNAME").replace("%completedrebirth%", rebirthName)
						.replace("%completedrebirth_display%", rebirthDisplayName)
						.replace("%completedrebirth_cost%", rebirthCost)
						.replace("%completedrebirth_cost_formatted%", formattedRebirthCost);
				List<String> itemLore = main.globalStorage.getStringListData("Rebirthlist-gui.completed-format.itemLORE");
				List<String> itemEnchantments = main.globalStorage.getStringListData("Rebirthlist-gui.completed-format.itemENCHANTMENTS");
				List<String> itemFlags = main.globalStorage.getStringListData("Rebirthlist-gui.completed-format.itemFLAGS");
				List<String> itemCommands = main.globalStorage.getStringListData("Rebirthlist-gui.completed-format.itemCOMMANDS");
				List<String> realItemCommands = new ArrayList<String>();
				List<String> coloredItemLore = new ArrayList<String>();
				ItemStack completedItem = XMaterial.matchXMaterial(itemName).parseItem(true);
				completedItem.setAmount(itemAmount);
				ItemMeta completedMeta = completedItem.getItemMeta();
				completedMeta.setDisplayName(main.prxAPI.c(itemDisplayName));
				itemLore.forEach(line -> {coloredItemLore.add(main.getString(line.replace("%completedrebirth%", rebirthName)
						.replace("%completedrebirth_display%", rebirthDisplayName)	.replace("%completedrebirth_cost%", rebirthCost)
						.replace("%completedrebirth_cost_formatted%", formattedRebirthCost), p.getName()));});
				completedMeta.setLore(coloredItemLore);
				itemEnchantments.forEach(line -> {
					String[] splittedLine = line.split(" ");
					Enchantment enchant = EnchantmentReader.matchEnchantment(splittedLine[0]);
					int lvl = Integer.valueOf(splittedLine[1]);
					completedMeta.addEnchant(enchant, lvl, true);
				});
				itemFlags.forEach(line -> {completedMeta.addItemFlags(ItemFlagReader.matchItemFlag(line));});
				completedItem.setItemMeta(completedMeta);
				GUIButton completedButton = new GUIButton(completedItem);
				completedButton.setListener(event -> {
					if(itemCommands.contains("(cancel-item_move)")) {
						event.setCancelled(true);
					}
					if(itemCommands.contains("(closeinv)")) {
						p.closeInventory();
					}
                    for(String command : itemCommands) {
                    	if(command.startsWith("[")) {
                    		realItemCommands.add(main.getString(command
                    				.replace("%completedrebirth%", rebirthName).replace("%completedrebirth_display%", rebirthName).replace("%completedrebirth_cost%", rebirthCost)
            						.replace("%completedrebirth_cost_formatted%", formattedRebirthCost), p.getName()));
                    	}
                    }
                    main.executeCommands(p, realItemCommands);
				});
				if(!allowedRebirthSlots.isEmpty()) {
		        rebirthsGUI.setButton(allowedRebirthSlots.get(rebirthIndex), completedButton);
				} else {
					rebirthsGUI.setButton(rebirthIndex, completedButton);
				}
			}
			if(playerRebirthIndex == rebirthIndex) { // if current
				// placeholders {
				String rebirthName = rebirth;
				String rebirthDisplayName = main.prxAPI.c(main.rebirthStorage.getDisplayName(rebirthName));
				double rebirthCostNumber = (main.rebirthStorage.getCost(rebirthName));
				String rebirthCost = String.valueOf(rebirthCostNumber);
				String formattedRebirthCost = main.formatBalance(rebirthCostNumber);
				// }
				String itemName = main.globalStorage.getStringData("Rebirthlist-gui.current-format.itemNAME");
				int itemAmount = main.globalStorage.getIntegerData("Rebirthlist-gui.current-format.itemAMOUNT");
				String itemDisplayName = main.globalStorage.getStringData("Rebirthlist-gui.current-format.itemDISPLAYNAME").replace("%currentrebirth%", rebirthName)
						.replace("%currentrebirth_display%", rebirthDisplayName)
						.replace("%currentrebirth_cost%", rebirthCost)
						.replace("%currentrebirth_cost_formatted%", formattedRebirthCost);
				List<String> itemLore = main.globalStorage.getStringListData("Rebirthlist-gui.current-format.itemLORE");
				List<String> itemEnchantments = main.globalStorage.getStringListData("Rebirthlist-gui.current-format.itemENCHANTMENTS");
				List<String> itemFlags = main.globalStorage.getStringListData("Rebirthlist-gui.current-format.itemFLAGS");
				List<String> itemCommands = main.globalStorage.getStringListData("Rebirthlist-gui.current-format.itemCOMMANDS");
				List<String> realItemCommands = new ArrayList<String>();
				List<String> coloredItemLore = new ArrayList<String>();
				ItemStack currentItem = XMaterial.matchXMaterial(itemName).parseItem(true);
				currentItem.setAmount(itemAmount);
				ItemMeta currentMeta = currentItem.getItemMeta();
				currentMeta.setDisplayName(main.prxAPI.c(itemDisplayName));
				itemLore.forEach(line -> {coloredItemLore.add(main.getString(line.replace("%currentrebirth%", rebirthName)
						.replace("%currentrebirth_display%", rebirthDisplayName).replace("%currentrebirth_cost%", rebirthCost)
						.replace("%currentrebirth_cost_formatted%", formattedRebirthCost), p.getName()));});
				currentMeta.setLore(coloredItemLore);
				itemEnchantments.forEach(line -> {
					String[] splittedLine = line.split(" ");
					Enchantment enchant = EnchantmentReader.matchEnchantment(splittedLine[0]);
					int lvl = Integer.valueOf(splittedLine[1]);
					currentMeta.addEnchant(enchant, lvl, true);
				});
				itemFlags.forEach(line -> {currentMeta.addItemFlags(ItemFlagReader.matchItemFlag(line));});
				currentItem.setItemMeta(currentMeta);
				GUIButton currentButton = new GUIButton(currentItem);
				currentButton.setListener(event -> {
					if(itemCommands.contains("(cancel-item_move)")) {
						event.setCancelled(true);
					}
					if(itemCommands.contains("(closeinv)")) {
						p.closeInventory();
					}
                    for(String command : itemCommands) {
                    	if(command.startsWith("[")) {
                    		realItemCommands.add(main.getString(command
                    				.replace("%currentrebirth%", rebirthName).replace("%currentrebirth_display%", rebirthName)	.replace("%currentrebirth_cost%", rebirthCost)
            						.replace("%currentrebirth_cost_formatted%", formattedRebirthCost), p.getName()));
                    	}
                    }
                    main.executeCommands(p, realItemCommands);
				});
				if(!allowedRebirthSlots.isEmpty()) {
			        rebirthsGUI.setButton(allowedRebirthSlots.get(rebirthIndex), currentButton);
					} else {
						rebirthsGUI.setButton(rebirthIndex, currentButton);
					}
			}
			if(playerRebirthIndex < rebirthIndex) { // if not completed
				// placeholders {
				String rebirthName = rebirth;
				String rebirthDisplayName = main.prxAPI.c(main.rebirthStorage.getDisplayName(rebirthName));
				double rebirthCostNumber = (main.rebirthStorage.getCost(rebirthName));
				String rebirthCost = String.valueOf(rebirthCostNumber);
				String formattedRebirthCost = main.formatBalance(rebirthCostNumber);
				// }
				String itemName = main.globalStorage.getStringData("Rebirthlist-gui.other-format.itemNAME");
				int itemAmount = main.globalStorage.getIntegerData("Rebirthlist-gui.other-format.itemAMOUNT");
				String itemDisplayName = main.globalStorage.getStringData("Rebirthlist-gui.other-format.itemDISPLAYNAME").replace("%otherrebirth%", rebirthName)
						.replace("%otherrebirth_display%", rebirthDisplayName)
						.replace("%otherrebirth_cost%", rebirthCost)
						.replace("%otherrebirth_cost_formatted%", formattedRebirthCost);
				List<String> itemLore = main.globalStorage.getStringListData("Rebirthlist-gui.other-format.itemLORE");
				List<String> itemEnchantments = main.globalStorage.getStringListData("Rebirthlist-gui.other-format.itemENCHANTMENTS");
				List<String> itemFlags = main.globalStorage.getStringListData("Rebirthlist-gui.other-format.itemFLAGS");
				List<String> itemCommands = main.globalStorage.getStringListData("Rebirthlist-gui.other-format.itemCOMMANDS");
				List<String> realItemCommands = new ArrayList<String>();
				List<String> coloredItemLore = new ArrayList<String>();
				ItemStack otherItem = XMaterial.matchXMaterial(itemName).parseItem(true);
				otherItem.setAmount(itemAmount);
				ItemMeta otherMeta = otherItem.getItemMeta();
				otherMeta.setDisplayName(main.prxAPI.c(itemDisplayName));
				itemLore.forEach(line -> {coloredItemLore.add(main.getString(line.replace("%otherrebirth%", rebirthName)
						.replace("%otherrebirth_display%", rebirthDisplayName).replace("%otherrebirth_cost%", rebirthCost)
						.replace("%otherrebirth_cost_formatted%", formattedRebirthCost), p.getName()));});
				otherMeta.setLore(coloredItemLore);
				itemEnchantments.forEach(line -> {
					String[] splittedLine = line.split(" ");
					Enchantment enchant = EnchantmentReader.matchEnchantment(splittedLine[0]);
					int lvl = Integer.valueOf(splittedLine[1]);
					otherMeta.addEnchant(enchant, lvl, true);
				});
				itemFlags.forEach(line -> {otherMeta.addItemFlags(ItemFlagReader.matchItemFlag(line));});
				otherItem.setItemMeta(otherMeta);
				GUIButton otherButton = new GUIButton(otherItem);
				otherButton.setListener(event -> {
					if(itemCommands.contains("(cancel-item_move)")) {
						event.setCancelled(true);
					}
					if(itemCommands.contains("(closeinv)")) {
						p.closeInventory();
					}
                    for(String command : itemCommands) {
                    	if(command.startsWith("[")) {
                    		realItemCommands.add(main.getString(command
                    				.replace("%otherrebirth%", rebirthName).replace("%otherrebirth_display%", rebirthName).replace("%otherrebirth_cost%", rebirthCost)
            						.replace("%otherrebirth_cost_formatted%", formattedRebirthCost), p.getName()));
                    	}
                    }
                    main.executeCommands(p, realItemCommands);
				});
				if(!allowedRebirthSlots.isEmpty()) {
			        rebirthsGUI.setButton(allowedRebirthSlots.get(rebirthIndex), otherButton);
					} else {
						rebirthsGUI.setButton(rebirthIndex, otherButton);
					}
			}
		}
		p.openInventory(rebirthsGUI.getInventory());
	}
}
