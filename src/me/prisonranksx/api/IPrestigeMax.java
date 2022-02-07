package me.prisonranksx.api;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.entity.Player;

import com.google.common.util.concurrent.AtomicDouble;

import me.prisonranksx.utils.AccessibleBukkitTask;
import me.prisonranksx.utils.AccessibleString;

public interface IPrestigeMax {

	void execute(Player player);
	
	void execute(Player player, boolean silent);
	
	void executeOnAsyncQueue(Player player);
	
	void executeOnAsyncQueue(Player player, boolean infinite);
	
	void executeOnAsyncMultiThreadedQueue(Player player);
	
	void executeOnSyncMultiThreadedQueue(Player player);
	
	void executeFinal(AccessibleBukkitTask accessibleBukkitTask, Player player, String name, AccessibleString finalPrestige, AccessibleString prestigeFrom, AtomicInteger prestigeTimes, AtomicDouble takenBalance);
	
	Set<String> getProcessingPlayers();
	
	boolean isProcessing(String name);
	
	void removeProcessingPlayer(String name);
	
	PRXAPI getAPI();

	void executeInfinite(Player player);
	
}
