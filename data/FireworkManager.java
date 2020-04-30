package me.prisonranksx.data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.prisonranksx.PrisonRanksX;

public class FireworkManager {

	private String levelName;
	private String pathName;
	private LevelType levelType;
	private PrisonRanksX main = (PrisonRanksX)Bukkit.getPluginManager().getPlugin("PrisonRanksX");
	
	public FireworkManager(String levelName, LevelType levelType, String pathName) {this.levelName = levelName; this.pathName = pathName; this.levelType = levelType;};
	
	public Map<String, Object> getFireworkBuilder() {
		if(levelType == LevelType.RANK) {
			if(main.configManager.ranksConfig.getConfigurationSection("Ranks." + pathName + "." + levelName + ".firework-builder") == null) {
				return new HashMap<String, Object>();
			}
		return main.configManager.ranksConfig.getConfigurationSection("Ranks." + pathName + "." + levelName + ".firework-builder").getValues(false);
		} else if (levelType == LevelType.PRESTIGE) {
			if(main.configManager.prestigesConfig.getConfigurationSection("Prestiges." + levelName + ".firework-builder") == null) {
				return new HashMap<String, Object>();
			}
			return main.configManager.prestigesConfig.getConfigurationSection("Prestiges." + levelName + ".firework-builder").getValues(false);
		} else if (levelType == LevelType.REBIRTH) {
			if(main.configManager.rebirthsConfig.getConfigurationSection("Rebirths." + levelName + ".firework-builder") == null) {
				return new HashMap<String, Object>();
			}
			return main.configManager.rebirthsConfig.getConfigurationSection("Rebirths." + levelName + ".firework-builder").getValues(false);
		} else if (levelType == LevelType.OTHER) {
			FileConfiguration x = callOtherConfig(main.getDataFolder() + "/" + levelName + "s" + ".yml");
			return x.getConfigurationSection(levelName + "s." + levelName + ".firework-builder").getValues(true);
		} else {
			return null;
		}
	}
	
	public FileConfiguration callOtherConfig(String configFilePath) {
		return YamlConfiguration.loadConfiguration(new File(configFilePath));
	}
}
