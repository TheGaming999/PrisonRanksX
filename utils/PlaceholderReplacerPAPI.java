package me.prisonranksx.utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class PlaceholderReplacerPAPI implements PlaceholderReplacer {

	@Override
	public String parse(String message, Player player) {
		String parsedMessage = PlaceholderAPI.setPlaceholders(player, message)
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
		return parsedMessage;
	}

	@Override
	public String parseCached(String message) {
		String parsedMessage = message.replace("[>>]", "»")
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
		return parsedMessage;
	}

	@Override
	public String parse(String message, OfflinePlayer offlinePlayer) {
		String parsedMessage = PlaceholderAPI.setPlaceholders(offlinePlayer, message)
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
		return parsedMessage;
	}

}
