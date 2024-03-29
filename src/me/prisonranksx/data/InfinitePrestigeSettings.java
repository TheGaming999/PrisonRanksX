package me.prisonranksx.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.prisonranksx.PrisonRanksX;

public class InfinitePrestigeSettings {

	private PrisonRanksX plugin;
	private FileConfiguration infinitePrestigeConfig;
	private String display;
	private String costExpression;
	private String rankupCostIncreaseExpression;
	private List<String> maxPrestigeCommands;
	private List<String> commands;
	private List<String> broadcast;
	private List<String> msg;
	private long finalPrestige;
	private Map<Long, InfinitePrestigeSettings> continuousPrestigeSettings;
	private Map<Long, InfinitePrestigeSettings> constantPrestigeSettings;
	
	public InfinitePrestigeSettings(PrisonRanksX plugin) {
		this.plugin = plugin;
		this.constantPrestigeSettings = new HashMap<>();
		this.continuousPrestigeSettings = new HashMap<>();
		this.infinitePrestigeConfig = this.plugin.getConfigManager().infinitePrestigeConfig;
	}
	
	@SuppressWarnings("unchecked")
	public void load() {
		this.display = this.plugin.getChatColorReplacer().parseRegular(infinitePrestigeConfig.getString("Global-Settings.display"));
		this.costExpression = infinitePrestigeConfig.getString("Global-Settings.cost-expression");
		this.rankupCostIncreaseExpression = infinitePrestigeConfig.getString("Global-Settings.rankup-cost-increase-expression");
		this.commands = infinitePrestigeConfig.getStringList("Global-Settings.commands");
		this.broadcast = this.plugin.getChatColorReplacer().parseRegular(infinitePrestigeConfig.getStringList("Global-Settings.broadcast"));
		this.finalPrestige = infinitePrestigeConfig.getLong("Global-Settings.final-prestige");
		this.maxPrestigeCommands = (List<String>)infinitePrestigeConfig.getList("Global-Settings.max-prestige-commands", null);
		ConfigurationSection continuous = infinitePrestigeConfig.getConfigurationSection("Continuous-Prestiges-Settings");
		ConfigurationSection constant = infinitePrestigeConfig.getConfigurationSection("Constant-Prestiges-Settings");
		if(continuous != null) {
			Set<String> cont_amount = continuous.getKeys(false);
			if(cont_amount.size() != 0) {
				for (String prestigeNumber : cont_amount) {
					InfinitePrestigeSettings newSetting = new InfinitePrestigeSettings(plugin);
					String contDisplay = infinitePrestigeConfig.getString("Continuous-Prestiges-Settings." + prestigeNumber + ".display");
					String contCostExpression = infinitePrestigeConfig.getString("Continuous-Prestiges-Settings." + prestigeNumber + ".cost-expression");
					List<String> commands = infinitePrestigeConfig.getStringList("Continuous-Prestiges-Settings." + prestigeNumber + ".executecmds");
					List<String> broadcast = infinitePrestigeConfig.getStringList("Continuous-Prestiges-Settings." + prestigeNumber + ".broadcast");
					List<String> msg = infinitePrestigeConfig.getStringList("Continuous-Prestiges-Settings." + prestigeNumber + ".msg");
				    if(contDisplay != null)
				    	newSetting.setDisplay(this.plugin.getChatColorReplacer().parseRegular(contDisplay));
				    if(contCostExpression != null)
				    	newSetting.setCostExpression(contCostExpression);
				    if(!commands.isEmpty())
				    	newSetting.setCommands(commands);
				    if(!broadcast.isEmpty())
				    	newSetting.setBroadcast(this.plugin.getChatColorReplacer().parseRegular(broadcast));
				    if(!msg.isEmpty())
				    	newSetting.setMsg(this.plugin.getChatColorReplacer().parseRegular(msg));
				    this.continuousPrestigeSettings.put(Long.valueOf(prestigeNumber), newSetting);
				}
			}
		}
		if(constant != null) {
			Set<String> cons_amount = constant.getKeys(false);
			if(cons_amount.size() != 0) {
				for (String prestigeNumber : cons_amount) {
					InfinitePrestigeSettings newSetting = new InfinitePrestigeSettings(plugin);
					String consDisplay = infinitePrestigeConfig.getString("Constant-Prestiges-Settings." + prestigeNumber + ".display");
					String consCostExpression = infinitePrestigeConfig.getString("Constant-Prestiges-Settings." + prestigeNumber + ".cost-expression");
					List<String> commands = infinitePrestigeConfig.getStringList("Constant-Prestiges-Settings." + prestigeNumber + ".executecmds");
					List<String> broadcast = infinitePrestigeConfig.getStringList("Constant-Prestiges-Settings." + prestigeNumber + ".broadcast");
                    long nextPrestigeNumber = infinitePrestigeConfig.getLong("Constant-Prestiges-Settings." + prestigeNumber + ".to");
				    if(consDisplay != null)
				    	newSetting.setDisplay(this.plugin.getChatColorReplacer().parseRegular(consDisplay));
				    if(consCostExpression != null)
				    	newSetting.setCostExpression(consCostExpression);
				    if(!commands.isEmpty())
				    	newSetting.setCommands(commands);
				    if(!broadcast.isEmpty())
				    	newSetting.setBroadcast(this.plugin.getChatColorReplacer().parseRegular(broadcast));
				    if(nextPrestigeNumber != 0) 
				    	newSetting.setFinalPrestige(nextPrestigeNumber);
				    this.constantPrestigeSettings.put(Long.valueOf(prestigeNumber), newSetting);
				}
			}
		}
	}

	public String getNonReplacedDisplay(IPrestigeDataHandler prestigeDataHandler) {
		PrestigeDataHandlerInfinite prestigeDataHandlerI = (PrestigeDataHandlerInfinite)prestigeDataHandler;
		String display = prestigeDataHandlerI.ips.getDisplay();
		if(!prestigeDataHandlerI.cps.isEmpty()) {
		   	for(Entry<Long, InfinitePrestigeSettings> cons : prestigeDataHandlerI.cps.entrySet()) {
		   		long prestigeNumber = Long.valueOf(prestigeDataHandlerI.getName());
		   		InfinitePrestigeSettings ipsc = cons.getValue();
		   		if(prestigeNumber >= cons.getKey() && prestigeNumber < ipsc.getFinalPrestige()) {
		   			display = ipsc.getDisplay();
		   			break;
		   		}
		   	}
		}	
		return display;
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

	public List<String> getMsg() {
		return msg;
	}
	
	public void setMsg(List<String> msg) {
		this.msg = msg;
	}
	
	public long getFinalPrestige() {
		return finalPrestige;
	}

	public void setFinalPrestige(long finalPrestige) {
		this.finalPrestige = finalPrestige;
	}

	public Map<Long, InfinitePrestigeSettings> getContinuousPrestigeSettings() {
		return continuousPrestigeSettings;
	}

	public void setContinuousPrestigeSettings(Map<Long, InfinitePrestigeSettings> continuousPrestigeSettings) {
		this.continuousPrestigeSettings = continuousPrestigeSettings;
	}

	public Map<Long, InfinitePrestigeSettings> getConstantPrestigeSettings() {
		return constantPrestigeSettings;
	}

	public void setConstantPrestigeSettings(Map<Long, InfinitePrestigeSettings> constantPrestigeSettings) {
		this.constantPrestigeSettings = constantPrestigeSettings;
	}

	public List<String> getMaxPrestigeCommands() {
		return maxPrestigeCommands;
	}

	public void setMaxPrestigeCommands(List<String> maxPrestigeCommands) {
		this.maxPrestigeCommands = maxPrestigeCommands;
	}
	
}
