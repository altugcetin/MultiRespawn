package net.astroalchemist.multirespawn.listeners;

import net.astroalchemist.multirespawn.MultiRespawn;
import net.astroalchemist.multirespawn.managers.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Listens for player movement and damage, prevents both during respawn/teleport
 * 
 * @author AstroAlchemist
 */
public class FreezeListener implements Listener {

    private final MultiRespawn plugin;

    public FreezeListener(MultiRespawn plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ConfigManager config = plugin.getConfigManager();

        // Check if freeze is enabled
        if (!config.isFreezeEnabled()) {
            return;
        }

        // Check if player is frozen (pending respawn OR teleporting)
        if (!plugin.getRespawnManager().isFrozen(player.getUniqueId())) {
            return;
        }

        // Check if actually moved (not just looked around)
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {

            // Only looking around, check if we should freeze rotation too
            if (!config.isFreezeRotation()) {
                return;
            }
        }

        // Cancel the movement
        event.setCancelled(true);

        // Send freeze message if configured (only if pending, not during teleport)
        if (plugin.getRespawnManager().hasPendingRespawn(player.getUniqueId())) {
            String freezeMessage = config.getFreezeMessage();
            if (freezeMessage != null && !freezeMessage.isEmpty()) {
                player.sendActionBar(config.colorize(freezeMessage));
            }
        }

        plugin.debug("Blocked movement for " + player.getName() + " (frozen)");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // Check if player is frozen (pending respawn OR teleporting)
        if (!plugin.getRespawnManager().isFrozen(player.getUniqueId())) {
            return;
        }

        // Cancel all damage while frozen/teleporting
        event.setCancelled(true);
        plugin.debug("Blocked damage for " + player.getName() + " (frozen/teleporting)");
    }
}
