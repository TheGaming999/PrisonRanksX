package me.prisonranksx.utils;

public class TimeCounter {

	private long beginningTime;
	private long finalTime;
	
	public TimeCounter() {beginningTime = System.currentTimeMillis();}
	
	public TimeCounter(boolean begin) {if(begin) beginningTime = System.currentTimeMillis();}
	
	public static TimeCounter begin() {
		return new TimeCounter();
	}
	
	public long end() {
		finalTime = System.currentTimeMillis() - beginningTime;
		return finalTime;
	}
	
	public long endAsSeconds() {
		finalTime = System.currentTimeMillis() - beginningTime;
		return finalTime / 1000;
	}
	
	public long tryEndingAsSeconds() {
		finalTime = System.currentTimeMillis() - beginningTime;
		return finalTime > 1000 ? finalTime / 1000 : finalTime;
	}
	
	public static String toSeconds(final long time) {
	    if(time > 1000) {
	    	return String.valueOf(time / 1000) + " s";
	    } else {
	    	return String.valueOf(time) + " ms";
	    }
	}
	
	public String tryEndingAsSecondsFormatted() {
		finalTime = System.currentTimeMillis() - beginningTime;
		return finalTime > 1000 ? String.valueOf(Double.valueOf((double)finalTime / (double)1000)) + " s" : String.valueOf((double)finalTime) + " ms"; 
	}
	
}
