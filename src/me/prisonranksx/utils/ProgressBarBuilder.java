package me.prisonranksx.utils;

import org.apache.commons.lang3.StringUtils;

public class ProgressBarBuilder {
	
	public ProgressBarBuilder() {}
	
    public String getChars(String character, int amount) {
    	if(amount <= 0) {
    		return StringUtils.EMPTY;
    	}
    	StringBuilder builder = new StringBuilder(character);
    	for(int i = 0; i < amount; i++) {
    		 builder.append(character);
    	}
    	return builder.toString();
    }
    
    /**
     * @param style progress bar string character
     * @param donePrefix color code of the filled progress
     * @param leftPrefix color code of the incomplete progress
     * @param percentage progress value
     * @return ProgressBar made with string characters
     */
    public String setProgressBar(String style, String donePrefix, String leftPrefix, int percentage) {
    	String progress = donePrefix + getChars(style, percentage / 10)
    	+ leftPrefix + getChars(style, (100 - percentage) / 10);
    	return progress;
    }
    
    /**
     * @param style progress bar string character
     * @param donePrefix color code of the filled progress
     * @param leftPrefix color code of the incomplete progress
     * @param percentage progress value
     * @param charsAmount the string length of the entire progressbar, normally 10 characters (string length)
     * @return ProgressBar made with string characters, charsAmount =/= 0
     */
    public String setProgressBar(String style, String donePrefix, String leftPrefix, int percentage, int charsAmount) {
        int first = 100 / charsAmount;
        int second = 100 / first;
    	String progress = donePrefix + getChars(style, percentage / second)
    	+ leftPrefix + getChars(style, (100 - percentage) / second);
    	return progress;
    }
    
    /**
     * @param doneStyle progress bar string character of the filled progress
     * @param leftStyle progress bar string character of the incomplete progress
     * @param donePrefix color code of the filled progress || can be empty
     * @param leftPrefix color code of the incomplete progress || can be empty
     * @param percentage progress value
     * @param charsAmount the string length of the entire progressbar, normally 10 characters (string length)
     * @return ProgressBar made with string characters, charsAmount =/= 0
     */
    public String setComplexProgressBar(String doneStyle, String leftStyle, String donePrefix, String leftPrefix, int percentage, int charsAmount) {
    	if(charsAmount == 0) {
    		System.out.println("[!] charsAmount equals 0?");
    		throw new java.lang.IllegalStateException();
    	}
        int first = 100 / charsAmount;
        int second = 100 / first;
    	String progress = donePrefix + getChars(doneStyle, percentage / second)
    	+ leftPrefix + getChars(leftStyle, (100 - percentage) / second);
    	return progress;
    }
}
