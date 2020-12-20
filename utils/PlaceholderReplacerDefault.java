package me.prisonranksx.utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceholderReplacerDefault implements PlaceholderReplacer {

	@Override
	public String parse(String message, Player player) {
		return parseCached(message);
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
		return parseCached(message);
	}

}
