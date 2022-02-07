package me.prisonranksx.data;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import me.prisonranksx.PrisonRanksX;

public class FireworkManager {

	private PrisonRanksX main;
	
	public FireworkManager(PrisonRanksX main) {
		this.main = main;
	}
	
	public FireworkDataHandler readFromConfig(LevelType levelType, String levelName, String pathName) {
		ConfigurationSection fireworkSection = null;
		if(levelType == LevelType.RANK) {
			fireworkSection = main.getConfigManager().ranksConfig.getConfigurationSection("Ranks." + pathName + "." + levelName + ".firework-builder");
		} else if (levelType == LevelType.PRESTIGE) {
			fireworkSection = main.getConfigManager().prestigesConfig.getConfigurationSection("Prestiges." + levelName + ".firework-builder");
		} else if (levelType == LevelType.REBIRTH) {
			fireworkSection = main.getConfigManager().rebirthsConfig.getConfigurationSection("Rebirths." + levelName + ".firework-builder");
		}
		if(fireworkSection != null) {
			boolean flicker = fireworkSection.getBoolean("flicker");
			boolean trail = fireworkSection.getBoolean("trail");
			List<Color> color = new ArrayList<>();
			fireworkSection.getStringList("color").forEach(line -> {
				color.add(getColor(line));
			});
			List<Color> fade = new ArrayList<>();
			fireworkSection.getStringList("fade").forEach(line -> {
				fade.add(getColor(line));
			});
			int power = fireworkSection.getInt("power");
			List<FireworkEffect> fireworkEffects = new ArrayList<>();
			fireworkSection.getStringList("effect").forEach(effect -> {
				FireworkEffect fwe = FireworkEffect.builder()
						.with(Type.valueOf(effect.replace("SPARKLE", "BURST").replace("STARS", "STAR")))
						.flicker(flicker)
						.trail(trail)
						.withColor(color)
						.withFade(fade)
						.build();			
				fireworkEffects.add(fwe);
			});
			FireworkDataHandler fdh = new FireworkDataHandler();
			fdh.setPower(power);
			fdh.setFireworkEffects(fireworkEffects);
			return fdh;
		}
		return null;
	}
	
	public void sendRebirthFirework(Player p) {
		main.scheduler.runTask(main, () -> {
			String nextRebirth = main.prxAPI.getPlayerNextRebirth(p);
			if(nextRebirth == null) return;
			boolean sendFirework = main.rebirthStorage.isSendFirework(nextRebirth);
			if(!sendFirework) {
				return;
			}
			Firework firework = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
			FireworkDataHandler fdh = main.rebirthStorage.getFireworkDataHandler(nextRebirth);
    	    FireworkMeta meta = firework.getFireworkMeta();
    	    meta.addEffects(fdh.getFireworkEffects());
    	    meta.setPower(fdh.getPower());
    	    firework.setFireworkMeta(meta);
		});
	}
	
	public void sendPrestigeFirework(Player p) {
		main.scheduler.runTask(main, () -> {
			String nextPrestige = main.prxAPI.getPlayerNextPrestige(p);
			if(nextPrestige == null) return;
			boolean sendFirework = main.prestigeStorage.isSendFirework(nextPrestige);
			if(!sendFirework) {
				return;
			}
			Firework firework = (Firework) p.getWorld().spawnEntity(p.getPlayer().getLocation(), EntityType.FIREWORK);
			FireworkDataHandler fdh = main.prestigeStorage.getFireworkDataHandler(nextPrestige);
    	    FireworkMeta meta = firework.getFireworkMeta();
    	    meta.addEffects(fdh.getFireworkEffects());
    	    meta.setPower(fdh.getPower());
    	    firework.setFireworkMeta(meta);
		});
    }
	
	public void sendRankFirework(Player p) {
		main.scheduler.runTask(main, () -> {
			RankPath currentRankPath = main.prxAPI.getPlayerRankPath(p);
			RankPath nextRankPath = RankPath.getRankPath(main.prxAPI.getPlayerNextRank(p), currentRankPath.getPathName());
			if(nextRankPath == null) return;
	        boolean sendFirework = main.rankStorage.isSendFirework(currentRankPath);
	        if(!sendFirework) {
	        	return;
	        }
		    Firework firework = (Firework) p.getWorld().spawnEntity(p.getPlayer().getLocation(), EntityType.FIREWORK);
    	    FireworkDataHandler fdh = main.rankStorage.getFireworkDataHandler(nextRankPath);
    	    FireworkMeta meta = firework.getFireworkMeta();
    	    meta.addEffects(fdh.getFireworkEffects());
    	    meta.setPower(fdh.getPower());
    	    firework.setFireworkMeta(meta);
		});
	}
	
	public Color getColor(String temp) {
		temp = temp.toUpperCase().replace("_", "").replace(" ", "");
		switch (temp) {
			case "AQUA": return Color.AQUA;
		 	case "BLACK": return Color.BLACK;
		 	case "BLUE": case "DARKBLUE": return Color.BLUE;
		 	case "FUCHSIA": case "PINK": return Color.FUCHSIA;
		 	case "GRAY": case "GREY": return Color.GRAY;
		 	case "GREEN": case "DARKGREEN": return Color.GREEN;
		 	case "LIME": case "LIGHTGREEN": return Color.LIME;
		 	case "MAROON": return Color.MAROON;
		 	case "NAVY": return Color.NAVY;
		 	case "OLIVE": return Color.OLIVE;
		 	case "ORANGE": return Color.ORANGE;
		 	case "PURPLE": case "DARKPURPLE": return Color.PURPLE;
		 	case "RED": case "DARKRED": return Color.RED;
		 	case "SILVER": case "LIGHTGRAY": case "LIGHTGREY": return Color.SILVER;
		 	case "TEAL": return Color.TEAL;
		 	case "WHITE": return Color.WHITE;
		 	// Custom Color Section
		 	case "LIGHTPURPLE": return Color.fromRGB(255, 86, 255);
		 	case "GOLD": return Color.fromRGB(255,215,0);
		 	case "CYAN": return Color.fromRGB(16, 130, 148);
		 	case "BROWN": return Color.fromRGB(139,69,19);
		 	case "LIGHTYELLOW": return Color.fromRGB(255, 255, 154);
		 	case "SKYBLUE": case "BLUESKY": return Color.fromRGB(11, 182, 255);
		 	case "TURQUOISE": case "BLUEGREEN": return Color.fromRGB(11, 255, 198);
		 	case "LIGHTRED": return Color.fromRGB(255, 51, 51);
		 	case "LIGHTBLUE": return Color.fromRGB(118, 118, 239);
		 	default: return Color.WHITE;
		}
	}
	
}
