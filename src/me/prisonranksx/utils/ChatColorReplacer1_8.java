package me.prisonranksx.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import me.prisonranksx.PrisonRanksX;

public class ChatColorReplacer1_8 implements ChatColorReplacer {

	private PrisonRanksX main;
	public ChatColorReplacer1_8(PrisonRanksX main) {
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
			
			return configholdedstring.replace("&", "§")
					
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
		return configstring.replace("&", "§")
				
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

	@Override
	public String getStringWithoutPAPI(String configstring) {
		return configstring.replace("&", "§")
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
