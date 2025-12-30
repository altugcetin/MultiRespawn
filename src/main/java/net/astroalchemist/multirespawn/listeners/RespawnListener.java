package net.astroalchemist.multirespawn.listeners;

import net.astroalchemist.multirespawn.MultiRespawn;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Listens for player respawn events
 * 
 * @author AstroAlchemist
 */
public class RespawnListener implements Listener {

    private final MultiRespawn plugin;

    public RespawnListener(MultiRespawn plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (plugin.getRespawnManager().hasPendingRespawn(player.getUniqueId())) {
            plugin.debug("Player " + player.getName() + " respawned with pending respawn command");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getRespawnManager().cancelRespawn(event.getPlayer().getUniqueId());
        plugin.debug("Cancelled pending respawn for " + event.getPlayer().getName() + " (player quit)");
    }
}
