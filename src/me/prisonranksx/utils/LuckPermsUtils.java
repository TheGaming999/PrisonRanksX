package me.prisonranksx.utils;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.entity.Player;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.platform.PlayerAdapter;
import net.luckperms.api.track.Track;

public class LuckPermsUtils {

	private LuckPerms luckperms;
	
	public LuckPermsUtils(LuckPerms luckperms) {
		this.luckperms = luckperms;
	}
	
    public void setGroup(final Player player, final Group group) {
        luckperms.getUserManager().modifyUser(player.getUniqueId(), (User user) -> {

            // Remove all other inherited groups the user had before.
            user.data().clear(NodeType.INHERITANCE::matches);
 
            // Create a node to add to the player.
            Node node = InheritanceNode.builder(group).build();

            // Add the node to the user.
            user.data().add(node);
        });
    }
	
    public Collection<Group> getGroups(final Player player) {
        PlayerAdapter<Player> playerAdapter = luckperms.getPlayerAdapter(Player.class);
        
        // Get a LuckPerms user for the player.
        User user = playerAdapter.getUser(player);
        // Get all of the groups they inherit from on the current server.
        Collection<Group> groups = user.getInheritedGroups(playerAdapter.getQueryOptions(player));
        return groups;
    }
    
    public String getGroup(final Player player) {
    	PlayerAdapter<Player> playerAdapter = luckperms.getPlayerAdapter(Player.class);
    	User user = playerAdapter.getUser(player);
    	return user.getPrimaryGroup();
    }
    
    public String getGroup(final UUID uuid) {
    	luckperms.getUserManager().getUser(uuid);
    	return null;
    }
    
    public boolean trackExists(final String track) {
    	boolean isLoaded = luckperms.getTrackManager().isLoaded(track);
    	return isLoaded;
    }
    
    public void setGroupInTrack(final Player player, final Group group, final String track) {
    	User user = luckperms.getUserManager().getUser(player.getUniqueId());
    	Track tracc = luckperms.getTrackManager().getTrack(track);
    	tracc.promote(user, group.getQueryOptions().context());
        luckperms.getTrackManager().saveTrack(tracc);
    }
    
}
