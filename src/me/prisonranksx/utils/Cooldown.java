package me.prisonranksx.utils;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.collect.Maps;

class IntegerLong {
	
	private int n1;
	private long n2;
	
	public IntegerLong(int n1, long n2) {
		this.n1 = n1;
		this.n2 = n2;
	}
	
	public int getInt() { return n1; }
	
	public long getLong() { return n2; }	
	
	public int setInt(int number) {
		return this.n1 = number;
	}
	
	public long setLong(long number) {
		return this.n2 = number;
	}
	
	public IntegerLong set(int intNumber, long longNumber) {
		this.n1 = intNumber;
		this.n2 = longNumber;
		return this;
	}
	
}

/**
 * 
 * @apiNote Simple utility class to manage, create, and edit cooldowns using System time.
 */
public class Cooldown {	
	
	private final static Map<String, IntegerLong> cooldowns = Maps.newLinkedHashMap();
	private final static long th = 1000;
		
	/**
	 * Begins a cooldown. Should be inserted after / under the code to be cooled down.
	 * @param name player name / key name or any other identifier
	 * @param cooldownTime how long should it take for the cooldown to finish in seconds, so the wanted process can be performed again
	 */
	public static void start(final String name, final int cooldownTime) {
		cooldowns.put(name, new IntegerLong(cooldownTime, System.currentTimeMillis()));
	}
	
	/**
	 * Removes or resets the cooldown for the provided player name or identifier.
	 * @param name player name / key name or any other identifier
	 */
	public static void clear(final String name) {
		cooldowns.remove(name);
	}
	
	/**
	 * Resets the cooldown for all.
	 */
	public static void clearAll() {
		cooldowns.clear();
	}
	
	/**
	 * @param name player name / key name or the wanted identifier
	 * @return true when the cooldown is still going on, and false if the cooldown has ended or (name) doesn't have a cooldown yet.
	 */
	public static boolean isCoolingDown(final String name) {
		if(cooldowns.containsKey(name)) {
			IntegerLong mapValue = cooldowns.get(name);
			long secondsLeft = ((mapValue.getLong()/th)+mapValue.getInt()) - (System.currentTimeMillis()/th);
			if(secondsLeft>0) {
                // Still cooling down
                return true;
            }
			cooldowns.remove(name);
			return false;
		}
		return false;
	}
	
	/**
	 * @param name player name / key name or the wanted identifier
	 * @param Performs the specified operation if cooldown has ended.
	 * @return true when the cooldown is still going on, and false if the cooldown has ended or (name) doesn't have a cooldown yet.
	 */
	public static boolean checkAndDo(final String name, final Consumer<String> operation) {
		if(cooldowns.containsKey(name)) {
			IntegerLong mapValue = cooldowns.get(name);
			long secondsLeft = ((mapValue.getLong()/th)+mapValue.getInt()) - (System.currentTimeMillis()/th);
			if(secondsLeft>0) return true;
			cooldowns.remove(name);
		}
		operation.accept(name);
		return false;
	}
	
	/**
	 * @param name player name / key name or the wanted identifier
	 * @return seconds left before the cooldown ends, and an exception if isCoolingDown(...) returns false.
	 * @exception NullPointerException when (name) is not under cooldown
	 */
	public static long getOrThrow(final String name) throws NullPointerException {
		IntegerLong mapValue = cooldowns.get(name);
		long secondsLeft = ((mapValue.getLong()/th)+mapValue.getInt()) - (System.currentTimeMillis()/th);
		long correctedSecondsLeft = secondsLeft > 0 ? secondsLeft : 0;
		return correctedSecondsLeft;
	}
	
	/**
	 * 
	 * @param name player name / key name or the wanted identifier
	 * @return null if (name) is not under cooldown / cooldown ended, otherwise return cooldown in seconds.
	 */
	public static Long getOrNull(final String name) {
		IntegerLong mapValue = cooldowns.get(name);
		if(mapValue == null) return null;
		long secondsLeft = ((mapValue.getLong()/th)+mapValue.getInt()) - (System.currentTimeMillis()/th);
		long correctedSecondsLeft = secondsLeft > 0 ? secondsLeft : 0;
		return correctedSecondsLeft;
	}
	
	/**
	 * 
	 * @param name player name / key name or the wanted identifier
	 * @param nullValue value to return when (name) is not under cooldown
	 * @return (nullValue) if (name) is not under cooldown / cooldown ended, otherwise return cooldown in seconds.
	 */
	public static long getOrNull(final String name, final long nullValue) {
		IntegerLong mapValue = cooldowns.get(name);
		if(mapValue == null) return nullValue;
		long secondsLeft = ((mapValue.getLong()/th)+mapValue.getInt()) - (System.currentTimeMillis()/th);
		long correctedSecondsLeft = secondsLeft > 0 ? secondsLeft : 0;
		return correctedSecondsLeft;
	}
	
	/**
	 * Changes a player / key cooldown time while it is still going on
	 * @param name player name / key name
	 * @param cooldownTime wanted new cooldown time
	 */
	public static void setTime(final String name, final int cooldownTime) {
		cooldowns.put(name, new IntegerLong(cooldownTime, cooldowns.get(name).getLong()));
	}
	
	/**
	 * @return keys / players who are under cooldown.
	 */
	public static Set<String> getCoolingDownKeys() {
		return cooldowns.keySet();
	}
	
}
