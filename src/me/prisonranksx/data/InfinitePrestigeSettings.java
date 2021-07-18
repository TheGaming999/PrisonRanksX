package me.prisonranksx.data;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import me.prisonranksx.PrisonRanksX;

public class InfinitePrestigeSettings {

	private PrisonRanksX plugin;
	private FileConfiguration infinitePrestigeConfig;
	private String display;
	private String costExpression;
	private String rankupCostIncreaseExpression;
	private List<String> commands;
	private List<String> broadcast;
	private long finalPrestige;
	
	public InfinitePrestigeSettings(PrisonRanksX plugin) {
		this.plugin = plugin;
		this.infinitePrestigeConfig = this.plugin.getConfigManager().infinitePrestigeConfig;
	}
	
	public void load() {
		this.display = this.plugin.getChatColorReplacer().parseRegular(infinitePrestigeConfig.getString("Global-Settings.display"));
		this.costExpression = infinitePrestigeConfig.getString("Global-Settings.cost-expression");
		this.rankupCostIncreaseExpression = infinitePrestigeConfig.getString("Global-Settings.rankup-cost-increase-expression");
		this.commands = infinitePrestigeConfig.getStringList("Global-Settings.commands");
		this.broadcast = this.plugin.getChatColorReplacer().parseRegular(infinitePrestigeConfig.getStringList("Global-Settings.broadcast"));
		this.finalPrestige = infinitePrestigeConfig.getLong("Global-Settings.final-prestige");
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getCostExpression() {
		return costExpression;
	}

	public void setCostExpression(String costExpression) {
		this.costExpression = costExpression;
	}

	public String getRankupCostIncreaseExpression() {
		return rankupCostIncreaseExpression;
	}

	public void setRankupCostIncreaseExpression(String rankupCostIncreaseExpression) {
		this.rankupCostIncreaseExpression = rankupCostIncreaseExpression;
	}

	public List<String> getCommands() {
		return commands;
	}

	public void setCommands(List<String> commands) {
		this.commands = commands;
	}

	public List<String> getBroadcast() {
		return broadcast;
	}

	public void setBroadcast(List<String> broadcast) {
		this.broadcast = broadcast;
	}

	public long getFinalPrestige() {
		return finalPrestige;
	}

	public void setFinalPrestige(long finalPrestige) {
		this.finalPrestige = finalPrestige;
	}
	
}
