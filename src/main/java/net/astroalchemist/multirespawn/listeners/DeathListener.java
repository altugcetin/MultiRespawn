package net.astroalchemist.multirespawn.listeners;

import net.astroalchemist.multirespawn.MultiRespawn;
import net.astroalchemist.multirespawn.data.DeathLocation;
import net.astroalchemist.multirespawn.managers.ConfigManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

        if (!config.isEnabled())
            return;

        if (player.hasPermission("multirespawn.bypass")) {
            if (config.isShowBypassMessage()) {
                player.sendMessage(plugin.getMessageManager().getBypassMessage());
            }
            return;
        }

        String worldName = player.getWorld().getName();
        if (!config.isWorldEnabled(worldName))
            return;

        if (config.isRequirePermission() && !player.hasPermission(config.getPermissionNode()))
            return;

        if (config.isOnlyPvpDeaths()) {
            Long lastPvp = lastPvpDamage.get(player.getUniqueId());
            if (lastPvp == null || System.currentTimeMillis() - lastPvp > PVP_TIMEOUT)
                return;
        }

        Location loc = player.getLocation();
        DeathLocation deathLoc = new DeathLocation(
                player.getUniqueId().toString(),
                config.getServerName(),
                worldName,
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                loc.getYaw(),
                loc.getPitch());
        plugin.getDatabaseManager().saveDeathLocation(deathLoc);
        plugin.debug("Saved death location for " + player.getName() + " at " + worldName);

        if (config.isSkipRespawnScreen()) {
            plugin.getRespawnManager().executeInstantRespawn(player);
        } else {
            plugin.getRespawnManager().scheduleRespawn(player);
        }

        lastPvpDamage.remove(player.getUniqueId());
    }
}
