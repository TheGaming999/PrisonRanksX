package me.prisonranksx.utils;

import org.bukkit.map.MapFont;
import org.bukkit.map.MinecraftFont;

public class MessageCenterizer {

	private final static int CENTER_PX = 154;

	public static int getCharWidth(char c)
	{
		if(c >= '\u2588' && c <= '\u258F')
		{
			return ('\u258F' - c) + 2;
		}

		switch(c)
		{
		case ' ':
			return 4;
		case '\u2714':
			return 8;
		case '\u2718':
			return 7;
		default:
			MapFont.CharacterSprite mcChar = MinecraftFont.Font.getChar(c);
			if(mcChar != null)
				return mcChar.getWidth() + 1;
			return 0;
		}
	}


	public static String centerMessage(String message){
		if(message == null || message.equals("")) return message;
		// message = ChatColor.translateAlternateColorCodes('&', message);

		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for(char c : message.toCharArray()){
			if(c == '§'){
				previousCode = true;
				continue;
			}else if(previousCode == true){
				previousCode = false;
				if(c == 'l' || c == 'L'){
					isBold = true;
					continue;
				}else isBold = false;
			}else{
				DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
				if(dFI != DefaultFontInfo.DEFAULT) {
					messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
				} else {
					messagePxSize += isBold ? getCharWidth(c)+1 : getCharWidth(c);
				}
				messagePxSize++;
			}
		}

		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = CENTER_PX - halvedMessageSize;
		int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		StringBuilder sb = new StringBuilder();
		while(compensated < toCompensate){
			sb.append(" ");
			compensated += spaceLength;
		}
		return (sb.toString() + message);
	}
}
