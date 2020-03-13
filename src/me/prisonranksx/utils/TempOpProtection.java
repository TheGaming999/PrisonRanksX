package me.prisonranksx.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.OfflinePlayer;

import me.prisonranksx.PrisonRanksX;

public class TempOpProtection {

	private List<String> allowedCommands;
	private static Set<OfflinePlayer> operators;
	
	public TempOpProtection() {
		operators = new HashSet<OfflinePlayer>();
		allowedCommands = new ArrayList<String>();
	}
	
	public void setTempOp(OfflinePlayer player, boolean op) {
		if(op) {
			operators.add(player);
		} else {
			operators.remove(player);
		}
	}
	
	public boolean isTempOp(OfflinePlayer player) {
		if(operators.contains(player)) {
			return true;
		}
		return false;
	}
	
	public void setAllowedCommands(List<String> allowedCommands) {
		this.allowedCommands = allowedCommands;
	}
	
	public Set<OfflinePlayer> getPlayers() {
		return operators;
	}
	
	public List<String> getAllowedCommands() {
		return allowedCommands;
	}
	
	public boolean isAllowed(String command) {
		if(allowedCommands.contains(command)) {
			return true;
		}
		return false;
	}
	
	public void addCommand(String command) {
		allowedCommands.add(command);
	}
	
	public void delCommand(String command) {
		allowedCommands.remove(command);
	}
	
	public void clearCommands() {
		this.allowedCommands.clear();
	}
	
}
