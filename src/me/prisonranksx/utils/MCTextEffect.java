package me.prisonranksx.utils;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.util.Strings;

import com.google.common.collect.Lists;

public class MCTextEffect {
	
	public final static String IDENTIFIER_START_GLOW = "glow";
	// public final static String IDENTIFIER_END_GLOW = "/glow";
	/**
	 *  Example: {@literal<test>this is a text</test> | output: <test> </test>}
	 */
	public final static Pattern EFFECT_PATTERN = Pattern.compile("\\<(.*?)\\>");
	// public final static Pattern EFFECT_PATTERN = Pattern.compile("<([^>]*)>");
	/** 
	 * Example: {@literal<test>this is a text</test>} | output: this is a text
	 */
	public final static Pattern TEXT_PATTERN = Pattern.compile(">(.+?)</");
	
	// <glow first=&c middle=&4 last=&c>hello guys</glow>
	
	/**
	 * 
	 * @param text to be parsed, <%effect_start%>%effect_text%<%effect_end%>
	 * @return Entry<%effect_start%, %effect_text%>, null if no effect is found.
	 */
	public static Entry<String, String> parse(String text) {
		Matcher effectMatcher = EFFECT_PATTERN.matcher(text);
		Matcher textMatcher = TEXT_PATTERN.matcher(text);
		Map.Entry<String, String> entry;
		if(!effectMatcher.find() || !textMatcher.find())
			return null;
		entry = new AbstractMap.SimpleEntry<String, String>(effectMatcher.group(1), textMatcher.group(1));
		return entry;
	}
	
	/**
	 * 
	 * @param text to be parsed, <%effect_start%>%effect_text%<%effect_end%>
	 * @return Entry<%effect_start%, %effect_text%>, null if no effect is found.
	 */
	public static Entry<String, String> parsePrecisely(String text) {
		Matcher effectMatcher = EFFECT_PATTERN.matcher(text);
		Matcher textMatcher = TEXT_PATTERN.matcher(text);
		Map.Entry<String, String> entry;
		String startingIdentifier = null;
		String closingIdentifier = null;
		List<String> foundStuff = Lists.newArrayList();
		while (effectMatcher.find()) {
			String foundString = effectMatcher.group();
			foundStuff.add(foundString.substring(1, foundString.length()-1));
		}
		textMatcher.find();
        startingIdentifier = foundStuff.get(0);
        closingIdentifier = foundStuff.get(1);
		if(closingIdentifier == null || closingIdentifier.isEmpty() || !closingIdentifier.startsWith("/"))
			return null;
		entry = new AbstractMap.SimpleEntry<String, String>(startingIdentifier, textMatcher.group(1));
		return entry;
	}
	
	public static boolean hasGlow(String string) {
		return string.contains("<" + IDENTIFIER_START_GLOW + " ");		
	}
	
	public static boolean isGlow(Entry<String, String> parsedEntry) {
		return parsedEntry.getKey().startsWith(IDENTIFIER_START_GLOW + " ");
	}
	/*
	private static String insertPrefixChars(String string, String character) {
		String newString = Strings.EMPTY;
		for(char ch : string.toCharArray()) {
			newString += character + ch;
		}
		return newString;
	}
	/*
	
	/**
	 * 
	 * @param string to retrieve color codes from.
	 * @return Color codes like in the following example: 
	 * <p> string = "&1Blue&2&nGreen", result = "&1&2&n"
	 */
	public static String getAllColorCodes(String string) {
		int skipCounter = 0;
		int skipChars = -69;
		boolean check = false;
		String retrievedColorCodes = Strings.EMPTY;
		int length = string.length();
		for(int in = 0; in < length ; in++) {
			if(in < length && string.charAt(in) == '&') {
				skipCounter = in;
				check = false;
				for(int counter = skipCounter-1; counter < length ; counter++) {
					if(counter+1 < length && string.charAt(counter+1) == '&') {
						check = true;
						skipChars = counter;
						break;
					}
				}
				if(check) retrievedColorCodes += String.valueOf(string.charAt(skipChars+1)) + String.valueOf(string.charAt(skipChars+2));
			}
		}
		return retrievedColorCodes;
	}
	
	/**
	 * 
	 * @param string
	 * @return Color codes of a character within a string, <br>starting from the index of the preceding character until a non-color code pattern is reached.
	 * <p> "&1&l&nH&2e&3l&4l&5o" for example:
	 * <p> indexStart = 0; result = "&1&l&n"
	 * <br> indexStart = 6; result = "&2"
	 */
	public static String getColorCodesAfterChar(String string, int charIndex) {
		int skipCounter = 0;
		int skipChars = -69;
		int stringLength = string.length();
		boolean fetchColorCodes = false;
		boolean endLoop = false;
		String fetchedColorCodes = Strings.EMPTY;
		for(int in = charIndex; in < stringLength ; in++) {
			if(in < stringLength && string.charAt(in) == '&') {
				skipCounter = in;
				fetchColorCodes = false;
				for(int counter = skipCounter; counter < stringLength;) {
					if(string.charAt(counter) == '&') {
						if(string.charAt(counter+2) != '&') {
							fetchColorCodes = true;
							skipChars = counter;
							endLoop = true;
							break;
						} else {
							fetchColorCodes = true;
							skipChars = counter;
							break;
						}
					} else if (string.charAt(counter) != '&') {
						fetchColorCodes = false;
						endLoop = true;
						break;
					}
				}
				if(fetchColorCodes) { 
					fetchedColorCodes += String.valueOf(string.charAt(skipChars)) + String.valueOf(string.charAt(skipChars+1)); 
					if(endLoop) break;
				}
				if(endLoop) break;
			}
		}
		return fetchedColorCodes;
	}
	
	/**
	 * 
	 * @param string
	 * @return Color codes of a character within a string, <br>starting from the index of the preceding character until a non-color code pattern is reached.
	 * <p> "&1&l&nH&2e&3l&4l&5o" for example:
	 * <p> indexStart = 0; result = "&1&l&n"
	 * <br> indexStart = 6; result = "&2"
	 */
	public static String getColorCodesAfterCharWithPrecision(String string, int charIndex) {
		int skipCounter = 0;
		int skipChars = -69;
		int stringLength = string.length();
		boolean fetchColorCodes = false;
		boolean endLoop = false;
		String fetchedColorCodes = Strings.EMPTY;
		/*
		if(charIndex <= 1) {
			charIndex = 0;
		} else {
			if(string.charAt(charIndex) != '&') {
				
			}
			charIndex = charIndex-1;
		}
		*/
		for(int in = charIndex; in < stringLength ; in++) {
			if(in < stringLength && string.charAt(in) == '&') {
				skipCounter = in;
				fetchColorCodes = false;
				for(int counter = skipCounter; counter < stringLength;) {
					if(string.charAt(counter) == '&') {
						if(string.charAt(counter+2) != '&') {
							fetchColorCodes = true;
							skipChars = counter;
							endLoop = true;
							break;
						} else {
							fetchColorCodes = true;
							skipChars = counter;
							break;
						}
					} else if (string.charAt(counter) != '&') {
						fetchColorCodes = false;
						endLoop = true;
						break;
					}
				}
				if(fetchColorCodes) { 
					fetchedColorCodes += String.valueOf(string.charAt(skipChars)) + String.valueOf(string.charAt(skipChars+1)); 
					if(endLoop) break;
				}
				if(endLoop) break;
			}
		}
		return fetchedColorCodes;
	}
	
	/**
	 * 
	 * @param string
	 * @return Color codes of a character within a string, <br>starting from the index of the preceding character until a non-color code pattern is reached. Also, the reached point.
	 * <p> "&1&l&nH&2e&3l&4l&5o" for example:
	 * <p> indexStart = 0; result = "&1&l&n"
	 * <br> indexStart = 6; result = "&2"
	 */
	public static Entry<String, Integer> getColorCodesWithPositionAfterChar(String string, int charIndex) {
		int skipCounter = 0;
		int skipChars = -69;
		int stringLength = string.length();
		boolean fetchColorCodes = false;
		
		boolean endLoop = false;
		String fetchedColorCodes = Strings.EMPTY;
		for(int in = charIndex; in < stringLength ; in++) {
			if(in < stringLength && string.charAt(in) == '&') {
				skipCounter = in;
				fetchColorCodes = false;
				for(int counter = skipCounter; counter < stringLength;) {
					if(string.charAt(counter) == '&') {
						if(string.charAt(counter+2) != '&') {
							fetchColorCodes = true;
							skipChars = counter;
							endLoop = true;
							break;
						} else {
							fetchColorCodes = true;
							skipChars = counter;
							break;
						}
					} else if (string.charAt(counter) != '&') {
						fetchColorCodes = false;
						endLoop = true;
						break;
					}
				}
				if(fetchColorCodes) { 
					fetchedColorCodes += String.valueOf(string.charAt(skipChars)) + String.valueOf(string.charAt(skipChars+1)); 
					if(endLoop) break;
				}
				if(endLoop) break;
			}
		}
		Map.Entry<String,Integer> entry =
			    new AbstractMap.SimpleEntry<String, Integer>(fetchedColorCodes, skipChars);
		return entry;
	}
	
	/**
	 * 
	 * @param string
	 * @return Color codes of a character within a string, <br>starting from the index of the preceding character until a non-color code pattern that comes after the character is reached.
	 * <p> "&1&l&nH&2e&3l&4l&5o" for example:
	 * <p> indexStart = 0; result = "&1&l&nH"
	 * <br> indexStart = 6; result = "&2e"
	 */
	public static String getColorCodesWithNextChar(String string, int charIndex) {
		int skipCounter = 0;
		int skipChars = -69;
		int stringLength = string.length();
		boolean fetchColorCodes = false;
		boolean endLoop = false;
		String fetchedColorCodes = Strings.EMPTY;
		charIndex = charIndex-1;
		if(charIndex < 0) charIndex = 0;
		for(int in = charIndex; in < stringLength ; in++) {
			if(in < stringLength && string.charAt(in) == '&') {
				skipCounter = in;
				fetchColorCodes = false;
				for(int counter = skipCounter; counter < stringLength;) {
					if(string.charAt(counter) == '&') {
						if(string.charAt(counter+2) != '&') {
							fetchColorCodes = true;
							skipChars = counter;
							endLoop = true;
							break;
						} else {
							fetchColorCodes = true;
							skipChars = counter;
							break;
						}
					} else if (string.charAt(counter) != '&') {
						fetchColorCodes = false;
						endLoop = true;
						break;
					}
				}
				if(fetchColorCodes) { 
					fetchedColorCodes += String.valueOf(string.charAt(skipChars)) + String.valueOf(string.charAt(skipChars+1)); 
					if(endLoop) break;
				}
				if(endLoop) break;
			}
		}
		return fetchedColorCodes;
	}
	
	public static List<String> readGlow(String string) {
		// String effectName = IDENTIFIER_START_GLOW;
		if(!hasGlow(string))
			return Lists.newArrayList(string);
		Entry<String, String> entry = parse(string);
		if(entry == null)
			return Lists.newArrayList();
		String effect = entry.getKey();
		String text = entry.getValue();
        String[] splitter = effect.split(" ");
        
        // dummy color codes if no color codes were specified
        String initialColor = "&f";
        String midColor = "&4";
        String endColor = "&f";
        
        String first = entry.getValue();
        // list for saving final effect
        List<String> parsedList = new LinkedList<>();
		initialColor = splitter[1].split("=")[1];
		midColor = splitter[2].split("=")[1];
		endColor = splitter[3].split("=")[1];
		text = entry.getValue();
		String ogColor = null;
		boolean hasStartColor = false;
		// check if the text starts with a color code
		if(text.startsWith("&")) {
			hasStartColor = true;
			// try to get all of the color codes at the beginning of the text
			ogColor = getColorCodesAfterChar(text, 0);
			// separate the text from the color code
			text = entry.getValue().substring(ogColor.length(), text.length());
		} else {
			ogColor = "&f";
			hasStartColor = false;
		}
		// save real color codes so we can use them in the end
		String realInitialColor = initialColor;
		String realMidColor = midColor;
		String realEndColor = endColor;
		String realOriginalColor = ogColor;
		// fake color codes, so we don't need to make a calculation based on the color codes length.
		// and to support more than one color code
		initialColor = "??";
		midColor = "?/";
		endColor = "/?";
		ogColor = "!/";
		// cache text length because it will be used alot
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
				// final text with replacements and real color codes
				String parsedText = newText
						.replace(initialColor, realInitialColor)
						.replace(midColor, realMidColor)
						.replace(endColor, realEndColor)
						.replace(ogColor, realOriginalColor);
				parsedList.add(parsedText);
				editCount++;
			}
			parsedList.add(first);
			return parsedList;
	}
	
	public static List<String> parseGlow(List<String> stringList) {
		List<String> glowLines = CollectionUtils.hasPatternAndContainsIgnoreCaseReturnAll(stringList, EFFECT_PATTERN, "<" + IDENTIFIER_START_GLOW + " ");
		for (String glowLine : glowLines) {
			stringList = CollectionUtils.replaceElementWithList(stringList, glowLine, readGlow(glowLine));
		}
		return stringList;
	}
	
}
