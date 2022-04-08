package me.prisonranksx.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Color;


public class FireworkColor {

	private static final Map<String, Color> colors = new HashMap<>();
	
	static {
		for(Field field : Color.class.getFields()) {
			if(field.getType().equals(Color.class)) {
				String colorName = field.getName();
				try {
					colors.put(colorName, (Color)field.get(Color.class));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static Color getColor(String name) {
		return colors.get(name.toUpperCase());
	}
	
}
