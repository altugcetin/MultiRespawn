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
        if (plugin.getTransferManager().hasPendingTransfer(player.getUniqueId())) {
            plugin.debug("Player " + player.getName() + " respawned with pending transfer");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getTransferManager().cancelTransfer(event.getPlayer().getUniqueId());
        plugin.debug("Cancelled pending transfer for " + event.getPlayer().getName() + " (player quit)");
    }
}
