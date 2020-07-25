package me.prisonranksx.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import me.clip.placeholderapi.PlaceholderAPI;
import me.prisonranksx.PrisonRanksX;

public class ChatColorReplacer1_16 implements ChatColorReplacer {

	private PrisonRanksX main;
	public ChatColorReplacer1_16(PrisonRanksX main) {
		this.main = main;
	}
	
	@Override
	public String getString(String player, String configstring) {
		if(main.ishooked) {
			String configholdedstring;
 if(Bukkit.getPlayer(player) == null) {
	 configholdedstring = configstring;
 } else {
	 configholdedstring = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(player), configstring);
	
 }
			
			return ChatColor.translateAlternateColorCodes('&', configholdedstring
					
					.replace("[>>]", "»")
					.replace("[<<]", "«")
					.replace("[coolarrow]", "➤")
					.replace("[<3]", "�?�")
					.replace("[shadowarrow]", "➢")
					.replace("[shadowarrow_2]", "➣")
					.replace("[shadowarrow_down]", "⧨")
					.replace("[shadowsquare]", "�?�")
					.replace("[nuke]", "☢")
					.replace("[+]", "✚")
					.replace("[correct]", "✔")
					.replace("[incorrect]", "✖")
					.replace("[bowarrow]", "➸")
					.replace("[squaredot]", "◼")
					.replace("[square]", "■")
					.replace("[happyface]", "☺")
					.replace("[|]", "⎟"));
		}
		return ChatColor.translateAlternateColorCodes('&', configstring
				
		.replace("[>>]", "»")
		.replace("[<<]", "«")
		.replace("[coolarrow]", "➤")
		.replace("[<3]", "�?�")
		.replace("[shadowarrow]", "➢")
		.replace("[shadowarrow_2]", "➣")
		.replace("[shadowarrow_down]", "⧨")
		.replace("[shadowsquare]", "�?�")
		.replace("[nuke]", "☢")
		.replace("[+]", "✚")
		.replace("[correct]", "✔")
		.replace("[incorrect]", "✖")
		.replace("[bowarrow]", "➸")
		.replace("[squaredot]", "◼")
		.replace("[square]", "■")
		.replace("[happyface]", "☺")
		.replace("[|]", "⎟"));
	}

	@Override
	public String getStringWithoutPAPI(String configstring) {
		return ChatColor.translateAlternateColorCodes('&', configstring)
		.replace("[>>]", "»")
		.replace("[<<]", "«")
		.replace("[coolarrow]", "➤")
		.replace("[<3]", "�?�")
		.replace("[shadowarrow]", "➢")
		.replace("[shadowarrow_2]", "➣")
		.replace("[shadowarrow_down]", "⧨")
		.replace("[shadowsquare]", "�?�")
		.replace("[nuke]", "☢")
		.replace("[+]", "✚")
		.replace("[correct]", "✔")
		.replace("[incorrect]", "✖")
		.replace("[bowarrow]", "➸")
		.replace("[squaredot]", "◼")
		.replace("[square]", "■")
		.replace("[happyface]", "☺")
		.replace("[|]", "⎟");
	}
}
