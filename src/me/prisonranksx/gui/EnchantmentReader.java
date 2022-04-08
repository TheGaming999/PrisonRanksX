package me.prisonranksx.gui;

import org.bukkit.enchantments.Enchantment;

public class EnchantmentReader {
	private static String parseEnchantment(String enchant) {
		if(enchant.equalsIgnoreCase("protection")) {
			return "PROTECTION_ENVIRONMENTAL";
		} else if (enchant.equalsIgnoreCase("protect")) {
			return "PROTECTION_ENVIRONMENTAL";
		} else if (enchant.equalsIgnoreCase("pro")) {
			return "PROTECTION_ENVIRONMENTAL";
		} else if (enchant.equalsIgnoreCase("fireprotect")) {
			return "PROTECTION_FIRE";
		} else if (enchant.equalsIgnoreCase("fireprotection")) {
			return "PROTECTION_FIRE";
		} else if (enchant.equalsIgnoreCase("protectionfire")) {
			return "PROTECTION_FIRE";
		} else if (enchant.equalsIgnoreCase("protectfire")) {
			return "PROTECTION_FIRE";
		} else if (enchant.equalsIgnoreCase("firepro")) {
			return "PROTECTION_FIRE";
		} else if (enchant.equalsIgnoreCase("blastprotect")) {
			return "PROTECTION_EXPLOSION";
		} else if (enchant.equalsIgnoreCase("blastprotection")) {
			return "PROTECTION_EXPLOSION";
		} else if (enchant.equalsIgnoreCase("protectionblast")) {
			return "PROTECTION_EXPLOSION";
		} else if (enchant.equalsIgnoreCase("protectblast")) {
			return "PROTECTION_EXPLOSION";
		} else if (enchant.equalsIgnoreCase("protectionprojectile")) {
			return "PROTECTION_PROJECTILE";
		} else if (enchant.equalsIgnoreCase("protectprojectile")) {
			return "PROTECTION_PROJECTILE";
		} else if (enchant.equalsIgnoreCase("projectileprotection")) {
			return "PROTECTION_PROJECTILE";
		} else if (enchant.equalsIgnoreCase("projectileprotect")) {
			return "PROTECTION_PROJECTILE";
		} else if (enchant.equalsIgnoreCase("proprotection")) {
			return "PROTECTION_PROJECTILE";
		} else if (enchant.equalsIgnoreCase("protectionpro")) {
			return "PROTECTION_PROJECTILE";
		} else if (enchant.equalsIgnoreCase("aquaaffinity")) {
			return "WATER_WORKER";
		} else if (enchant.equalsIgnoreCase("waterworker")) {
			return "WATER_WORKER";
		} else if (enchant.equalsIgnoreCase("featherfalling")) {
			return "PROTECTION_FALL";
		} else if (enchant.equalsIgnoreCase("featherfall")) {
			return "PROTECTION_FALL";
		} else if (enchant.equalsIgnoreCase("respiration")) {
			return "OXYGEN";
		} else if (enchant.equalsIgnoreCase("resp")) {
			return "OXYGEN";
		} else if (enchant.equalsIgnoreCase("fireaspect")) {
			return "FIRE_ASPECT";
		} else if (enchant.equalsIgnoreCase("fire")) {
			return "FIRE_ASPECT";
		} else if (enchant.equalsIgnoreCase("firesword")) {
			return "FIRE_ASPECT";
		} else if (enchant.equalsIgnoreCase("aspectfire")) {
			return "FIRE_ASPECT";
		} else if (enchant.equalsIgnoreCase("fire_aspect")) {
			return "FIRE_ASPECT";
		} else if (enchant.equalsIgnoreCase("sharpness")) {
			return "DAMAGE_ALL";
		} else if (enchant.equalsIgnoreCase("sharp")) {
			return "DAMAGE_ALL";
		} else if (enchant.equalsIgnoreCase("sharpeness")) {
			return "DAMAGE_ALL";
		} else if (enchant.equalsIgnoreCase("smite")) {
			return "DAMAGE_UNDEAD";
		} else if (enchant.equalsIgnoreCase("unbreaking")) {
			return "DURABILITY";
		} else if (enchant.equalsIgnoreCase("unbreak")) {
			return "DURABILITY";
		} else if (enchant.equalsIgnoreCase("nobreak")) {
			return "DURABILITY";
		} else if (enchant.equalsIgnoreCase("looting")) {
			return "LOOT_BONUS_MOBS";
		} else if (enchant.equalsIgnoreCase("loot")) {
			return "LOOT_BONUS_MOBS";
		} else if (enchant.equalsIgnoreCase("baneofarthropods")) {
			return "DAMAGE_ARTHROPODS";
		} else if (enchant.equalsIgnoreCase("boa")) {
			return "DAMAGE_ARTHROPODS";
		} else if (enchant.equalsIgnoreCase("bane")) {
			return "DAMAGE_ARTHROPODS";
		} else if (enchant.equalsIgnoreCase("depthstrider")) {
			return "DEPTH_STRIDER";
		} else if (enchant.equalsIgnoreCase("fortune")) {
			return "LOOT_BONUS_BLOCKS";
		} else if (enchant.equalsIgnoreCase("fort")) {
			return "LOOT_BONUS_BLOCKS";
		} else if (enchant.equalsIgnoreCase("silktouch")) {
			return "SILK_TOUCH";
		} else if (enchant.equalsIgnoreCase("silk")) {
			return "SILK_TOUCH";
		} else if (enchant.equalsIgnoreCase("thorns")) {
			return "THORNS";
		} else if (enchant.equalsIgnoreCase("thorn")) {
			return "THORNS";
		} else if (enchant.equalsIgnoreCase("efficiency")) {
			return "DIG_SPEED";
		} else if (enchant.equalsIgnoreCase("eff")) {
			return "DIG_SPEED";
		} else if (enchant.equalsIgnoreCase("power")) {
			return "ARROW_DAMAGE";
		} else if (enchant.equalsIgnoreCase("flame")) {
			return "ARROW_FIRE";
		} else if (enchant.equalsIgnoreCase("infinite")) {
			return "ARROW_INFINITE";
		} else if (enchant.equalsIgnoreCase("infinity")) {
			return "ARROW_INFINITY";
		} else if (enchant.equalsIgnoreCase("punch")) {
			return "ARROW_KNOCKBACK";
		} else if (enchant.equalsIgnoreCase("speed_dig")) {
			return "DIG_SPEED";
		} else if (enchant.equalsIgnoreCase("speed_digging")) {
			return "DIG_SPEED";
		} else if (enchant.equalsIgnoreCase("digspeed")) {
			return "DIG_SPEED";
		} else if (enchant.equalsIgnoreCase("strength")) {
			return "PROTECTION_ENVIRONMENTAL";
		} else if (enchant.equalsIgnoreCase("strong")) {
			return "PROTECTION_ENVIRONMENTAL";
		} else if (enchant.equalsIgnoreCase("unbreakable")) {
			return "DURABILITY";
		} else if (enchant.equalsIgnoreCase("lure")) {
			return "LURE";
		} else if (enchant.equalsIgnoreCase("aqua")) {
			return "WATER_WORKER";
		} else if (enchant.equalsIgnoreCase("mending")) {
			return "MENDING";
		}
		return enchant.toUpperCase();
	}

	public static String matchStringEnchantment(String enchant) {
		return parseEnchantment(enchant);
	}
	
	@SuppressWarnings("deprecation")
	public static Enchantment matchEnchantment(String enchant) {
		return Enchantment.getByName(parseEnchantment(enchant));
	}
}
