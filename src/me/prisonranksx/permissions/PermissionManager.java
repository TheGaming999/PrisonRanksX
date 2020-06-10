package me.prisonranksx.permissions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.prisonranksx.PrisonRanksX;

public class PermissionManager {
	
    private PrisonRanksX main;
    
	public PermissionManager(PrisonRanksX main) {
		this.main = main;
	}
	
	public void addPermission(final Player player, final String perm) {
		String permission = perm;
		if(permission.startsWith("[") && permission.contains("]")) {
			int worldIndex = permission.lastIndexOf("]");
			String world = permission.substring(worldIndex, permission.length()).replace("[", "");
			String node = permission.substring(0, worldIndex + 1);
		    main.getPermissions().playerAdd(Bukkit.getWorld(world).getName(), player, node);
		    return;
		}
        main.getPermissions().playerAdd(null, player, permission);
	}
	
	@Deprecated
	public void addPermission(final String playerName, final String permission) {
		if(permission.startsWith("[") && permission.contains("]")) {
		      String fullpermission = permission;
		        Pattern worldName = Pattern.compile("\\[(.*?)\\]");
		        Matcher m3 = worldName.matcher(fullpermission);
		        String world = "";
		        while (m3.find()) {
		           world = m3.group(1);
		        }
		        String node = fullpermission.split("]")[1].substring(1);
		        main.getPermissions().playerAdd(Bukkit.getWorld(world).getName(), playerName, node);
		        return;
		}
      main.getPermissions().playerAdd(Bukkit.getPlayer(playerName), permission);
	}
	
	public void delPermission(final Player player, final String perm) {
		String permission = perm;
		if(permission.startsWith("[") && permission.contains("]")) {
			int worldIndex = permission.lastIndexOf("]");
			String world = permission.substring(worldIndex, permission.length()).replace("[", "");
			String node = permission.substring(0, worldIndex + 1);
		    main.getPermissions().playerRemove(Bukkit.getWorld(world).getName(), player, node);
		    return;
		}
        main.getPermissions().playerRemove(null, player, permission);
	}
	
	@Deprecated
	public void delPermission(final String playerName, final String permission) {
		if(permission.startsWith("[") && permission.contains("]")) {
		      String fullpermission = permission;
		        Pattern worldName = Pattern.compile("\\[(.*?)\\]");
		        Matcher m3 = worldName.matcher(fullpermission);
		        String world = "";
		        while (m3.find()) {
		           world = m3.group(1);
		        }
		        String node = fullpermission.split("]")[1].substring(1);
		        main.getPermissions().playerRemove(Bukkit.getWorld(world).getName(), playerName, node);
		        return;
		}
      main.getPermissions().playerRemove(Bukkit.getPlayer(playerName), permission);
	}
	
}
