package me.prisonranksx.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class AccessibleBukkitTask {

	private BukkitTask bukkitTask;
	private boolean isCancelled;
	private String identifier;
	private boolean isFrozen;
	
	public AccessibleBukkitTask() {
		this.bukkitTask = null;
		this.isCancelled = false;
		this.identifier = null;
	}
	
	public AccessibleBukkitTask(BukkitTask bukkitTask) {
		this.bukkitTask = bukkitTask;
	}
	
	public static AccessibleBukkitTask create() {
		return new AccessibleBukkitTask();
	}
	
	public void set(BukkitTask bukkitTask) {
		this.bukkitTask = bukkitTask;
	}
	
	public BukkitTask setAndGet(BukkitTask bukkitTask) {
		return this.bukkitTask = bukkitTask;
	}
	
	public BukkitTask getAndSet(BukkitTask bukkitTask) {
		BukkitTask previousTask = this.bukkitTask;
		this.bukkitTask = bukkitTask;
		return previousTask;
	}
	
	public BukkitTask get() {
		return this.bukkitTask;
	}
	
	public BukkitTask cancel() {
		this.bukkitTask.cancel();
		this.isCancelled = true;
		return this.bukkitTask;
	}
	
	public boolean isCancelled() {
		return this.isCancelled;
	}
	
	public boolean isReallyCancelled() {
		return this.bukkitTask.isCancelled();
	}
	
	public boolean isSync() {
		return this.bukkitTask.isSync();
	}
	
	public int getTaskId() {
		return this.bukkitTask.getTaskId();
	}
	
	public Plugin getOwner() {
		return this.bukkitTask.getOwner();
	}
	
	public boolean setFakeCancel(boolean cancel) {
		return this.isCancelled = cancel;
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
	
	public void pause(long millisecondsToWait) {
		synchronized(this.bukkitTask) {
			try {;
				this.bukkitTask.wait(millisecondsToWait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean freeze() {
		synchronized(this.bukkitTask) {
			try {
				this.bukkitTask.wait();
				return this.isFrozen = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return this.isFrozen;
	}
	
	public void forceResume() {
		synchronized(this.bukkitTask) {
			this.bukkitTask.notifyAll();
			this.isFrozen = false;
		}
	}
	
	public void resume() {
		synchronized(this.bukkitTask) {
			if(isFrozen) {
				this.bukkitTask.notifyAll();
				this.isFrozen = false;
			}
		}
	}
	
	public boolean isFrozen() {
		return this.isFrozen;
	}
	
	public BukkitTask runAsyncTask(JavaPlugin plugin, Runnable runnable) {
		return this.bukkitTask = Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
	}
	
}
