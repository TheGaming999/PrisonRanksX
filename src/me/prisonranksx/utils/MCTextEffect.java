package me.prisonranksx.utils;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.util.Strings;

import com.google.common.collect.Lists;

public class MCTextEffect {
	
	public final static String SEPARATE_CHAR = ";";
	public final static String EFFECT_READER_CHAR = "##";
	public final static String GLOW_IDENTIFIER = "GLOW";
	
	public static boolean hasGlow(String string) {
		return string.contains(EFFECT_READER_CHAR + GLOW_IDENTIFIER + EFFECT_READER_CHAR);		
	}
	
	private static String insertPrefixChars(String string, String character) {
		String newString = Strings.EMPTY;
		for(char ch : string.toCharArray()) {
			newString += character + ch;
		}
		return newString;
	}
	
	public static List<String> readGlow(String string) {
		String effectName = GLOW_IDENTIFIER;
		if(!string.contains(EFFECT_READER_CHAR + effectName + EFFECT_READER_CHAR))
			return Lists.newArrayList(string);
        String[] splitter = string.split(EFFECT_READER_CHAR);
        String initialColor = "&f";
        String midColor = "&4";
        String endColor = "&f";
        
        String first = splitter[3];
        String text = null;
        List<String> parsedList = new LinkedList<>();
        int i = -1;
		for(String str : splitter) {
			i++;
			 if(str.contains(SEPARATE_CHAR)) {
				String[] colorSplitter = splitter[i].split(SEPARATE_CHAR);
					initialColor = !colorSplitter[0].contains("&") ? insertPrefixChars(colorSplitter[0], "&") : colorSplitter[0];
					midColor = !colorSplitter[1].contains("&") ? insertPrefixChars(colorSplitter[1], "&") : colorSplitter[1];
					endColor = !colorSplitter[2].contains("&") ? insertPrefixChars(colorSplitter[2], "&") : colorSplitter[2];
			 }
		}
		 int iLength = initialColor.length();
		 int mLength = midColor.length();
		 int eLength = endColor.length();
		 text = splitter[3];
		 String ogColor = null;
		 boolean hasStartColor = false;
		 if(text.startsWith("&")) {
			 hasStartColor = true;
			 ogColor = text.substring(0, 2);
			 text = splitter[3].substring(2, text.length());
		 } else {
			 ogColor = "&f";
			 hasStartColor = false;
		 }
		 
		 int textLength = text.length();
		String newText = Strings.EMPTY;
		StringBuilder sb = new StringBuilder(text);
		int editCount = 0;
			for(int in = 0; in < sb.length()+40 ; in++) {
				sb = new StringBuilder(text);
				if(editCount == textLength+2) break;
				if(editCount >= 2) {
					if(editCount < textLength && (editCount != textLength-1)) {
						if(editCount > 2) {
						newText = sb.insert(in-2, initialColor).insert(in+1, midColor).insert(in+4, endColor).insert(in+7, ogColor).toString();
						if(hasStartColor) newText = ogColor + newText;
						} else {
                        newText = sb.insert(in-2, initialColor).insert(in+1, midColor).insert(in+4, endColor).insert(in+7, ogColor).toString();
						}
					} else if (editCount == textLength-1) {
						newText = sb.insert(in-2, initialColor).insert(in+1, midColor).insert(in+4, endColor).toString();
						if(hasStartColor) newText = ogColor + newText;
					} else if(editCount > textLength-2) {
						if(editCount != textLength+1) {
						newText = sb.insert(in-2, initialColor).insert(in+1, midColor).toString();
						if(hasStartColor) newText = ogColor + newText;
						} else {
							newText = sb.insert(in-2, initialColor).toString();
							if(hasStartColor) newText = ogColor + newText;
						}
					}
				} else {
					if(editCount == 0) {
						newText = sb.insert(in, endColor).insert(in+3, ogColor).toString();
					} else if (editCount == 1) {
						newText = sb.insert(in-1, midColor).insert(in+2, endColor).insert(in+5, ogColor).toString();
					}
				}
				parsedList.add(newText);
				editCount++;
			}
			parsedList.add(first);
			return parsedList;
	}
}
