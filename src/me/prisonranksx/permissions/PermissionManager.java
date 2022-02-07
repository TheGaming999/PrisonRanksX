package me.prisonranksx.permissions;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

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
			String node = permission.substring(worldIndex, permission.length()).replace("[", "").replace("]", "");
			String world = permission.substring(0, worldIndex + 1).replace("[", "").replace("]", "");
		    addPermission(player, node, world);
		    return;
		}
		main.newSharedChain("permission").async(() -> {
        main.getPermissions().playerAdd(null, player, permission);
		}).execute();
	}
	
	public String addPermission(final Player player, final String permission, boolean createThread) {
		if(createThread) {
			addPermission(player, permission);
			return permission;
		}
		if(!permission.startsWith("[")) {
			return String.valueOf(main.getPermissions().playerAdd(null, player, permission));
		}
        int worldIndex = permission.lastIndexOf("]");
		String node = permission.substring(worldIndex, permission.length()).replace("[", "").replace("]", "");
		String world = permission.substring(0, worldIndex + 1).replace("[", "").replace("]", "");
	    addPermission(player, node, world);
		return permission;
	}
	
	public String addPermissionAsync(final Player player, final String permission) {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
		        if(!permission.startsWith("[")) {
		        	 return String.valueOf(main.getPermissions().playerAdd(null, player, permission));
		        }
		        int worldIndex = permission.lastIndexOf("]");
				String node = permission.substring(worldIndex, permission.length()).replace("[", "").replace("]", "");
				String world = permission.substring(0, worldIndex + 1).replace("[", "").replace("]", "");
				return String.valueOf(main.getPermissions().playerAdd(Bukkit.getWorld(world).getName().trim(), player, node.trim()));
		}).thenApply(checked -> {
			return String.valueOf(player.isPermissionSet(permission));
		});
		return future.join();
	}
	
	public boolean addPermissionAsync(final Player player, final Collection<String> permissions) {
		for(String permission : permissions) {
			addPermissionAsync(player, permission);
		}
		return true;
	}
	
	public void addPermission(final Player player, final Collection<String> perm) {
		main.newSharedChain("permission").async(() -> {
			for(String permission : perm) {
				if(!permission.startsWith("[")) {
					main.getPermissions().playerAdd(null, player, permission);
					return;
				}
				int worldIndex = permission.lastIndexOf("]");
				String node = permission.substring(worldIndex, permission.length()).replace("[", "").replace("]", "");
				String world = permission.substring(0, worldIndex + 1).replace("[", "").replace("]", "");
				addPermission(player, node, world);
			}
		}).execute();	
	}
	
	public Collection<String> addPermission(final Player player, final Collection<String> perm, boolean createThread) {
		if(createThread) {
			addPermission(player, perm);
			return perm;
		}
		for(String permission : perm) {
			if(!permission.startsWith("[")) {
				main.getPermissions().playerAdd(null, player, permission);
				return perm;
			}
			int worldIndex = permission.lastIndexOf("]");
			String node = permission.substring(worldIndex, permission.length()).replace("[", "").replace("]", "");
			String world = permission.substring(0, worldIndex + 1).replace("[", "").replace("]", "");
			addPermission(player, node, world);
		}
		return perm;	
	}
	
	public void addPermissionOffline(final OfflinePlayer player, final String perm) {
		String permission = perm;
		if(permission.startsWith("[") && permission.contains("]")) {
			int worldIndex = permission.lastIndexOf("]");
			String node = permission.substring(worldIndex, permission.length()).replace("[", "").replace("]", "");
			String world = permission.substring(0, worldIndex + 1).replace("[", "").replace("]", "");
		    addPermissionOffline(player, node, world);
		    return;
		}
		main.newSharedChain("permission").async(() -> {
        main.getPermissions().playerAdd(null, player, permission);
		}).execute();
	}
	
	private void addPermission(final Player player, final String permission, final String world) {
		main.newSharedChain("permission").async(() -> {
		main.getPermissions().playerAdd(Bukkit.getWorld(world).getName().trim(), player, permission.trim());
		}).execute();
	}
	
	private void addPermissionOffline(final OfflinePlayer player, final String permission, final String world) {
		main.newSharedChain("permission").async(() -> {
		main.getPermissions().playerAdd(Bukkit.getWorld(world).getName().trim(), player, permission.trim());
		}).execute();
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
			String node = permission.substring(worldIndex, permission.length()).replace("[", "").replace("]", "");
			String world = permission.substring(0, worldIndex + 1).replace("[", "").replace("]", "");
		    delPermission(player, node, world);
		    return;
		}
		main.newSharedChain("permission").async(() -> {
        main.getPermissions().playerRemove(null, player, permission);
		}).execute();
	}
	
	public String delPermission(final Player player, final String permission, boolean createThread) {
		if(createThread) {
			delPermission(player, permission);
			return permission;
		}
		if(!permission.startsWith("[")) {
			return String.valueOf(main.getPermissions().playerRemove(null, player, permission));
		}
		int worldIndex = permission.lastIndexOf("]");
		String node = permission.substring(worldIndex, permission.length()).replace("[", "").replace("]", "");
		String world = permission.substring(0, worldIndex + 1).replace("[", "").replace("]", "");
	    delPermission(player, node, world);
		return permission;
	}
	
	public String delPermissionAsync(final Player player, final String permission) {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
		    @Override
		    public String get() {
		        if(!permission.startsWith("[")) {
		        	 return String.valueOf(main.getPermissions().playerRemove(null, player, permission));
		        }
		        int worldIndex = permission.lastIndexOf("]");
				String node = permission.substring(worldIndex, permission.length()).replace("[", "").replace("]", "");
				String world = permission.substring(0, worldIndex + 1).replace("[", "").replace("]", "");
				return String.valueOf(main.getPermissions().playerRemove(Bukkit.getWorld(world).getName().trim(), player, node.trim()));
		    }
		}).thenApply(checked -> {
			return String.valueOf(!player.isPermissionSet(permission));
		});
		return future.join();
	}
	
	public boolean delPermissionAsync(final Player player, final Collection<String> permissions) {
		for(String permission : permissions) {
			delPermissionAsync(player, permission);
		}
		return true;
	}
	
	public void delPermission(final Player player, final Collection<String> perm) {
		main.newSharedChain("permission").async(() -> {
			for(String permission : perm) {
				if(!permission.startsWith("[")) {
					main.getPermissions().playerRemove(null, player, permission);
					return;
				}
				int worldIndex = permission.lastIndexOf("]");
				String node = permission.substring(worldIndex, permission.length()).replace("[", "").replace("]", "");
				String world = permission.substring(0, worldIndex + 1).replace("[", "").replace("]", "");
				delPermission(player, node, world);
			}
		}).execute();	
	}
	
	public Collection<String> delPermission(final Player player, final Collection<String> perm, boolean createThread) {
		if(createThread) {
			delPermission(player, perm);
			return perm;
		}
		for(String permission : perm) {
			if(!permission.startsWith("[")) {
				main.getPermissions().playerRemove(null, player, permission);
				return perm;
			}
			int worldIndex = permission.lastIndexOf("]");
			String node = permission.substring(worldIndex, permission.length()).replace("[", "").replace("]", "");
			String world = permission.substring(0, worldIndex + 1).replace("[", "").replace("]", "");
			delPermission(player, node, world);
		}
		return perm;	
	}
	
	public void delPermissionOffline(final OfflinePlayer player, final String perm) {
		String permission = perm;
		if(permission.startsWith("[") && permission.contains("]")) {
			int worldIndex = permission.lastIndexOf("]");
			String node = permission.substring(worldIndex, permission.length()).replace("[", "").replace("]", "");
			String world = permission.substring(0, worldIndex + 1).replace("[", "").replace("]", "");
		    delPermissionOffline(player, node, world);
		    return;
		}
		main.newSharedChain("delpermission").async(() -> {
        main.getPermissions().playerRemove(null, player, permission);
		}).execute();
	}
	
	public void delPermission(final Player player, final String permission, final String world) {
		main.newSharedChain("delpermission").async(() -> {
			main.getPermissions().playerRemove(Bukkit.getWorld(world).getName().trim(), player, permission.trim());
		}).execute();
	}
	
	public void delPermissionOffline(final OfflinePlayer player, final String permission, final String world) {
		main.newSharedChain("delpermission").async(() -> {
			main.getPermissions().playerRemove(Bukkit.getWorld(world).getName().trim(), player, permission.trim());
		}).execute();
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
