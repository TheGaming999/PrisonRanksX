package me.prisonranksx.gui;

import java.util.List;

public class RankItem {

	private String material;
	private int amount;
	private String displayName;
	private List<String> lore;
	private List<String> enchantments;
	private List<String> flags;
	private List<String> commands;
	
	public RankItem() {}
	
	public void setMaterial(String material) {
		this.material = material;
	}
	
	public String getMaterial() {
		return material;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setLore(List<String> lore) {
		this.lore = lore;
	}
	
	public List<String> getLore() {
		return lore;
	}
	
	public void setEnchantments(List<String> enchantments) {
		this.enchantments = enchantments;
	}
	
	public List<String> getEnchantments() {
		return enchantments;
	}
	
	public void setFlags(List<String> flags) {
		this.flags = flags;
	}
	
	public List<String> getFlags() {
		return flags;
	}
	
	public void setCommands(List<String> commands) {
		this.commands = commands;
	}
	
	public List<String> getCommands() {
		return commands;
	}
	
	/**
	 * @return important values [material, amount, display-name, lore, commands]
	 */
	public String toString() {
		return "material:" + this.material + ", amount:" + this.amount + ", display-name:" + displayName
				+ ", lore:" + lore.toString() + ", commands:" + commands.toString();
	}
	
}
