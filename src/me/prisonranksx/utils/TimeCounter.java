package me.prisonranksx.utils;

public class TimeCounter {

	private long beginningTime;
	private double finalTime;
	
	public TimeCounter() {beginningTime = System.currentTimeMillis();}
	
	public TimeCounter(boolean begin) {if(begin) beginningTime = System.currentTimeMillis();}
	
	/**
	 * start / restart the timer
	 * @return a new TimeCounter to start the process.
	 */
	public TimeCounter begin() {
		return new TimeCounter();
	}
	
	/**
	 * stops the timer and gives the final result
	 * @return time taken in milliseconds
	 */
	public long end() {
		finalTime = System.currentTimeMillis() - beginningTime;
		return (long)finalTime;
	}
	
	/**
	 * stops the timer and gives the final result
	 * @return time taken in seconds
	 */
	public double endAsSeconds() {
		finalTime = System.currentTimeMillis() - beginningTime;
		return (double)finalTime / 1000;
	}
	
	/**
	 * stops the timer and gives the final result
	 * @return time taken in milliseconds if it didn't reach higher than 1000 milliseconds which is equivalent to 1 second, otherwise return
	 * the time in seconds.
	 */
	public double tryEndingAsSeconds() {
		finalTime = System.currentTimeMillis() - beginningTime;
		return (double)finalTime > 1000 ? (double)finalTime / 1000 : (double)finalTime;
	}
	
	public static String toSeconds(final long time) {
	    if(time > 1000) {
	    	return String.valueOf(time / 1000) + " s";
	    } else {
	    	return String.valueOf(time) + " ms";
	    }
	}
	
	/**
	 * stops the timer and gives the final result
	 * @return time taken in milliseconds or seconds (same as tryEndingAsSeconds()) with the corresponding suffix (ms / s).
	 */
	public String tryEndingAsSecondsFormatted() {
		finalTime = System.currentTimeMillis() - beginningTime;
		return finalTime > 1000 ? String.valueOf(Double.valueOf((double)finalTime / (double)1000)) + " s" : String.valueOf((double)finalTime) + " ms"; 
	}
	
	/**
	 * stops the timer and gives the final result
	 * @return time taken in milliseconds or seconds (same as tryEndingAsSeconds()) with the corresponding suffix (ms / s).
	 */
	public String tryEndingAsSecondsFormattedAndRestart() {
		finalTime = System.currentTimeMillis() - beginningTime;
		begin();
		return finalTime > 1000 ? String.valueOf(Double.valueOf((double)finalTime / (double)1000)) + " s" : String.valueOf((double)finalTime) + " ms"; 
	}
	
	public void clearFinalTime() {
		finalTime = 0;
	}
	
	public void storeFinalTime() {
		finalTime = System.currentTimeMillis() - beginningTime;
	}
	
	public double getStoredFinalTime() {
		return finalTime;
	}
	
	public String getStoredFinalTime(boolean string) {
		return String.valueOf(finalTime);
	}
	
}
