package me.prisonranksx.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class AccessibleBukkitTask {

	private BukkitTask bukkitTask;
	private final Map<String, AtomicInteger> loopMap = new HashMap<>();
	
	public AccessibleBukkitTask() {
		this.bukkitTask = null;
	}
	
	public AccessibleBukkitTask(BukkitTask bukkitTask) {
		this.bukkitTask = bukkitTask;
	}
	
	public void set(BukkitTask bukkitTask) {
		this.bukkitTask = bukkitTask;
	}
	
	public BukkitTask get() {
		return this.bukkitTask;
	}
	
	public BukkitTask cancel() {
		this.bukkitTask.cancel();
		return this.bukkitTask;
	}
	
	public BukkitTask runAsyncTask(JavaPlugin plugin, Runnable runnable) {
		return this.bukkitTask = Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
	}
	
	public int runAsyncLoop(JavaPlugin plugin, Runnable runnable, int beginNum, int finalNum, boolean increase, String name) {
		AtomicInteger finish = new AtomicInteger(0);
		AtomicInteger returnInt = new AtomicInteger(beginNum);
		if(increase) {
			AtomicInteger beginNumber = new AtomicInteger(beginNum);
			AtomicInteger finalNumber = new AtomicInteger(finalNum);
			this.bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
				int i = beginNumber.incrementAndGet();
				for(int beg = beginNum; beg < 10; beg++) {
				beginNumber.set(beginNumber.get()+1);
				if(i >= finalNumber.get()) {
					finish.set(1);
				}
				returnInt.set(beginNumber.get());
				loopMap.put(name, returnInt);
				if(finish.get() == 0) {
				Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
				} else {
					this.cancel();
				}
				}
			}, 0, 1);
		}
		return returnInt.get();
	}
	
	public AtomicInteger getLoopValue(String name) {
		return loopMap.get(name);
	}
	
}
