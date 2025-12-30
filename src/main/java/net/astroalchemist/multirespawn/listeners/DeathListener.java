package net.astroalchemist.multirespawn.listeners;

import net.astroalchemist.multirespawn.MultiRespawn;
import net.astroalchemist.multirespawn.managers.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Listens for player death events
 * 
 * @author AstroAlchemist
 */
public class DeathListener implements Listener {

    private final MultiRespawn plugin;
    private final Map<UUID, Long> lastPvpDamage = new HashMap<>();
    private static final long PVP_TIMEOUT = 10000;

    public DeathListener(MultiRespawn plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim))
            return;
        if (!(event.getDamager() instanceof Player))
            return;
        if (event.isCancelled())
            return;
        lastPvpDamage.put(victim.getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        ConfigManager config = plugin.getConfigManager();

        if (!config.isEnabled()) {
            plugin.debug("Plugin is disabled, skipping death event for " + player.getName());
            return;
        }

        if (player.hasPermission("multirespawn.bypass")) {
            if (config.isShowBypassMessage()) {
                player.sendMessage(plugin.getMessageManager().getBypassMessage());
            }
            plugin.debug("Player " + player.getName() + " has bypass permission");
            return;
        }

        String worldName = player.getWorld().getName();
        if (!config.isWorldEnabled(worldName)) {
            plugin.debug("World " + worldName + " is not enabled for respawn command");
            return;
        }

        if (config.isRequirePermission() && !player.hasPermission(config.getPermissionNode())) {
            plugin.debug(
                    "Player " + player.getName() + " doesn't have required permission: " + config.getPermissionNode());
            return;
        }

        if (config.isOnlyPvpDeaths()) {
            Long lastPvp = lastPvpDamage.get(player.getUniqueId());
            if (lastPvp == null || System.currentTimeMillis() - lastPvp > PVP_TIMEOUT) {
                plugin.debug("Player " + player.getName() + " died from non-PvP, skipping (PvP-only mode)");
                return;
            }
        }

        plugin.debug("Player " + player.getName() + " died in " + worldName + ", scheduling respawn command");

        // If skip-respawn-screen is enabled, execute command immediately after a short
        // delay
        // This prevents the respawn screen from appearing
        if (config.isSkipRespawnScreen()) {
            // Keep inventory to prevent items dropping (respawn will happen instantly)
            event.setKeepInventory(true);
            event.getDrops().clear();
            event.setKeepLevel(true);
            event.setDroppedExp(0);

            // Execute spawn command immediately (with small delay for safety)
            plugin.getRespawnManager().executeInstantRespawn(player);
        } else {
            // Normal respawn flow - wait for respawn screen
            plugin.getRespawnManager().scheduleRespawn(player);
        }

        lastPvpDamage.remove(player.getUniqueId());
    }
}
