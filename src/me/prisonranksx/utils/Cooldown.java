package me.prisonranksx.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

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

class AsyncCooldown {

	public double cooldown;
	public double left;

	public AsyncCooldown(double cooldown, double left) {
		this.cooldown = cooldown;
		this.left = left;
	}

}

/**
 * 
 * @apiNote Simple utility class to manage, create, and edit cooldowns.
 */
public class Cooldown {	

	private final static Map<String, IntegerLong> COOLDOWNS = Maps.newHashMap();
	private final static Map<String, AsyncCooldown> ASYNC_COOLDOWNS = Maps.newHashMap();
	private static AsyncCooldownScheduler asyncScheduler;
	private final static long th = 1000;

	public static class SimpleCooldownScheduler {

		private long secondsLeft;
		private String name;
		private boolean value;

		public SimpleCooldownScheduler(String name, long secondsLeft, boolean value) {
			this.name = name;
			this.secondsLeft = secondsLeft;
			this.value = value;
		}

		public SimpleCooldownScheduler execute(Runnable runnable) {
			runnable.run();
			return this;
		}

		public SimpleCooldownScheduler execute(Consumer<Long> consumer) {
			consumer.accept(secondsLeft);
			return this;
		}

		public SimpleCooldownScheduler ifTrue(Runnable runnable) {
			if(value) runnable.run();
			return this;
		}

		public SimpleCooldownScheduler ifTrue(Consumer<Long> consumer) {
			if(value) consumer.accept(secondsLeft);
			return this;
		}

		public SimpleCooldownScheduler schedule(int cooldown) {
			if(value) Cooldown.start(name, cooldown);
			return this;
		}

		public SimpleCooldownScheduler orElse(Consumer<Long> consumer) {
			if(!value) consumer.accept(secondsLeft);
			return this;
		}

		public SimpleCooldownScheduler orElse(Runnable runnable) {
			if(!value) runnable.run();
			return this;
		}

		public long timeLeft() {
			return this.secondsLeft;
		}

	}

	public static class AsyncCooldownScheduler {

		private Plugin plugin;
		private String name;

		private void schedule(String name, double cooldown) {
			this.name = name;
			if(!Cooldown.ASYNC_COOLDOWNS.containsKey(name))
				Cooldown.ASYNC_COOLDOWNS.put(name, new AsyncCooldown(cooldown, cooldown));
			startIfNotStarted();
		}

		public AsyncCooldownScheduler execute(Runnable runnable) {
			runnable.run();
			return this;
		}

		public AsyncCooldownScheduler execute(Consumer<Double> consumer) throws NullPointerException {
			consumer.accept(Cooldown.ASYNC_COOLDOWNS.get(name).left);
			return this;
		}
		
		public AsyncCooldownScheduler executeNonNull(Consumer<Double> consumer) {
			AsyncCooldown ac = Cooldown.ASYNC_COOLDOWNS.get(name);
			double left = 0;
			if(ac != null) left = ac.left;			
			consumer.accept(left);
			return this;
		}
		
		public AsyncCooldownScheduler ifTrue(Runnable runnable) {
			if(!Cooldown.ASYNC_COOLDOWNS.containsKey(name))
				runnable.run();
			return this;
		}

		public AsyncCooldownScheduler orElse(Consumer<Double> consumer) {
			if(Cooldown.ASYNC_COOLDOWNS.containsKey(name))
				consumer.accept(Cooldown.ASYNC_COOLDOWNS.get(name).left);
			return this;
		}
		
		public AsyncCooldownScheduler orElse(Runnable runnable) {
			if(Cooldown.ASYNC_COOLDOWNS.containsKey(name))
				runnable.run();
			return this;
		}

		public AsyncCooldownScheduler schedule(double cooldown) {
			if(!Cooldown.ASYNC_COOLDOWNS.containsKey(name))
				schedule(name, cooldown);
			return this;
		}

		private AsyncCooldownScheduler(Plugin plugin) {
			this.plugin = plugin;	
		}

		private double twoDecis(double value) {
			return Math.floor(value * 100) / 100;
		}

		private void startIfNotStarted() {
			if(!Cooldown.ASYNC_COOLDOWNS.isEmpty()) 
				new BukkitRunnable() {
				public void run() {
					if(Cooldown.ASYNC_COOLDOWNS.isEmpty()) {
						cancel();
						return;
					}
					Iterator<Map.Entry<String, AsyncCooldown>> iterator = 
							Cooldown.ASYNC_COOLDOWNS.entrySet().iterator();
					while (iterator.hasNext()) {
						Map.Entry<String, AsyncCooldown> entry = iterator.next();
						double left = twoDecis(entry.getValue().left);
						if(left <= 0.0) {
							Cooldown.ASYNC_COOLDOWNS.remove(entry.getKey());
							return;
						}
						Cooldown.ASYNC_COOLDOWNS.put(entry.getKey(), new AsyncCooldown(entry.getValue().cooldown, twoDecis(left-0.1)));
					}
				}
			}.runTaskTimerAsynchronously(plugin, 1L, 1L);
		}
	}

	/**
	 * Begins a cooldown. Should be inserted after / under the code to be cooled down.
	 * @param name player name / key name or any other identifier
	 * @param cooldownTime how long should it take for the cooldown to finish in seconds, so the wanted process can be performed again
	 */
	public static void start(final String name, final int cooldownTime) {
		COOLDOWNS.put(name, new IntegerLong(cooldownTime, System.currentTimeMillis()));
	}

	/**
	 * Removes or resets the cooldown for the provided player name or identifier.
	 * @param name player name / key name or any other identifier
	 */
	public static void clear(final String name) {
		COOLDOWNS.remove(name);
	}

	/**
	 * Resets the cooldown for all.
	 */
	public static void clearAll() {
		COOLDOWNS.clear();
	}

	/**
	 * @param name player name / key name or the wanted identifier
	 * @return true when the cooldown is still going on, and false if the cooldown has ended or (name) doesn't have a cooldown yet.
	 */
	public static boolean isCoolingDown(final String name) {
		if(COOLDOWNS.containsKey(name)) {
			IntegerLong mapValue = COOLDOWNS.get(name);
			long secondsLeft = ((mapValue.getLong()/th)+mapValue.getInt()) - (System.currentTimeMillis()/th);
			if(secondsLeft>0) return true;
			COOLDOWNS.remove(name);
		}
		return false;
	}

	/**
	 * @param name player name / key name or the wanted identifier
	 * @return SimpleCooldownScheduler in which you can perform actions depending on {name} state regarding the cooldown.
	 */
	public static SimpleCooldownScheduler run(final String name) {
		SimpleCooldownScheduler runnable;
		if(COOLDOWNS.containsKey(name)) {
			IntegerLong mapValue = COOLDOWNS.get(name);
			long secondsLeft = ((mapValue.getLong()/th)+mapValue.getInt()) - (System.currentTimeMillis()/th);
			long correctedSecondsLeft = secondsLeft > 0 ? secondsLeft : 0;
			if(secondsLeft>0) {	
				runnable = new SimpleCooldownScheduler(name, correctedSecondsLeft, false);
				return runnable;
			}
			COOLDOWNS.remove(name);
		}
		runnable = new SimpleCooldownScheduler(name, 0, true);
		return runnable;
	}

	public static AsyncCooldownScheduler runAsync(final String name, final Plugin plugin) {
		if(asyncScheduler != null)
			return asyncScheduler;
		else
			return asyncScheduler = new AsyncCooldownScheduler(plugin);
	}

	/**
	 * @param name player name / key name or the wanted identifier
	 * @param failOperation performs the specified operation with consumable seconds left if cooldown is still going on.
	 * @param successOperation performs the specified operation if cooldown has ended.
	 * @return seconds left if the cooldown is still going on, and -1 if the cooldown has ended or (name) doesn't have a cooldown yet.
	 */
	public static void acceptOrElseRun(final String name, final Consumer<Long> failOperation, final Runnable successOperation) {
		if(COOLDOWNS.containsKey(name)) {
			IntegerLong mapValue = COOLDOWNS.get(name);
			long secondsLeft = ((mapValue.getLong()/th)+mapValue.getInt()) - (System.currentTimeMillis()/th);
			long correctedSecondsLeft = secondsLeft > 0 ? secondsLeft : 0;
			if(secondsLeft>0) {
				failOperation.accept(correctedSecondsLeft);
				return;
			}
			COOLDOWNS.remove(name);
		}
		successOperation.run();
	}

	/**
	 * @param name player name / key name or the wanted identifier
	 * @param failOperation performs the specified operation with consumable seconds left if cooldown is still going on.
	 * @param successOperation performs the specified operation with consumable name if cooldown has ended.
	 * @return seconds left if the cooldown is still going on, and -1 if the cooldown has ended or (name) doesn't have a cooldown yet.
	 */
	public static long getThenAccept(final String name, final Consumer<Long> failOperation, final Consumer<String> successOperation) {
		if(COOLDOWNS.containsKey(name)) {
			IntegerLong mapValue = COOLDOWNS.get(name);
			long secondsLeft = ((mapValue.getLong()/th)+mapValue.getInt()) - (System.currentTimeMillis()/th);
			long correctedSecondsLeft = secondsLeft > 0 ? secondsLeft : 0;
			if(secondsLeft>0) {
				failOperation.accept(correctedSecondsLeft);
				return correctedSecondsLeft;
			}
			COOLDOWNS.remove(name);
		}
		successOperation.accept(name);
		return -1;
	}

	/**
	 * 
	 * @param name player name / key name or the wanted identifier
	 * @return -1 if (name) is not under cooldown / cooldown ended, otherwise return cooldown in seconds.
	 */
	public static long get(final String name) {
		IntegerLong mapValue = COOLDOWNS.get(name);
		if(mapValue == null) return -1;
		long secondsLeft = ((mapValue.getLong()/th)+mapValue.getInt()) - (System.currentTimeMillis()/th);
		long correctedSecondsLeft = secondsLeft > 0 ? secondsLeft : -1;
		return correctedSecondsLeft;
	}

	/**
	 * 
	 * @param name player name / key name or the wanted identifier
	 * @param nullValue value to return when (name) is not under cooldown
	 * @return (nullValue) if (name) is not under cooldown / cooldown ended, otherwise return cooldown in seconds.
	 */
	public static long getOrElse(final String name, final long nullValue) {
		IntegerLong mapValue = COOLDOWNS.get(name);
		if(mapValue == null) return nullValue;
		long secondsLeft = ((mapValue.getLong()/th)+mapValue.getInt()) - (System.currentTimeMillis()/th);
		long correctedSecondsLeft = secondsLeft > 0 ? secondsLeft : nullValue;
		return correctedSecondsLeft;
	}

	/**
	 * Changes a player / key cooldown time while it is still going on
	 * @param name player name / key name
	 * @param cooldownTime wanted new cooldown time
	 */
	public static void setTime(final String name, final int cooldownTime) {
		COOLDOWNS.put(name, new IntegerLong(cooldownTime, COOLDOWNS.get(name).getLong()));
	}

	/**
	 * @return keys / players who are under cooldown.
	 */
	public static Set<String> getCoolingDownKeys() {
		return COOLDOWNS.keySet();
	}

}
