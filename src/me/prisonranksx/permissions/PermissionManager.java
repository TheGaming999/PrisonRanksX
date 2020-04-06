package me.prisonranksx.permissions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.api.PRXAPI;

public class PermissionManager {
    PrisonRanksX main;
    PRXAPI prxAPI;
	public PermissionManager(PrisonRanksX main) {
		this.main = main;
		prxAPI = this.main.prxAPI;
	}
	
	public void addPermission(Player player, String permission) {
		if(permission.startsWith("[") && permission.contains("]")) {
		      String fullpermission = permission;
		        Pattern worldName = Pattern.compile("\\[(.*?)\\]");
		        Matcher m3 = worldName.matcher(fullpermission);
		        String world = "";
		        while (m3.find()) {
		           world = m3.group(1);
		        }
		        String node = fullpermission.split("]")[1].substring(1);
		        prxAPI.getPluginMainClass().getPermissions().playerAdd(Bukkit.getWorld(world).getName(), player, node);
		        return;
		}
        main.getPermissions().playerAdd(null, player, permission);
	}
	public void delPermission(Player player, String permission) {
		if(permission.startsWith("[") && permission.contains("]")) {
		      String fullpermission = permission;
		        Pattern worldName = Pattern.compile("\\[(.*?)\\]");
		        Matcher m3 = worldName.matcher(fullpermission);
		        String world = "";
		        while (m3.find()) {
		           world = m3.group(1);
		        }
		        String node = fullpermission.split("]")[1].substring(1);
		        prxAPI.getPluginMainClass().getPermissions().playerRemove(Bukkit.getWorld(world).getName(), player, node);
		        return;
		}
        main.getPermissions().playerRemove(null, player, permission);
	}
}
