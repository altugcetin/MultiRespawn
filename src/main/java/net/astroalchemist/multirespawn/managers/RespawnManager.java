package net.astroalchemist.multirespawn.managers;

import net.astroalchemist.multirespawn.MultiRespawn;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Manages player respawn and command execution
 * 
 * @author AstroAlchemist
 */
public class RespawnManager {

    private final MultiRespawn plugin;
    private final Set<UUID> pendingRespawns = new HashSet<>();
    private final Set<UUID> scheduledRespawns = new HashSet<>();
    private final Set<UUID> teleportingPlayers = new HashSet<>(); // Players currently teleporting

    public RespawnManager(MultiRespawn plugin) {
        this.plugin = plugin;
    }

    public void scheduleRespawn(Player player) {
        UUID uuid = player.getUniqueId();

        if (scheduledRespawns.contains(uuid)) {
            plugin.debug("Respawn already scheduled for " + player.getName());
            return;
        }

        scheduledRespawns.add(uuid);
        pendingRespawns.add(uuid);

        ConfigManager config = plugin.getConfigManager();
        MessageManager messages = plugin.getMessageManager();
        int delayTicks = config.getRespawnDelay();

        // Only show death message if enabled
        if (config.isShowDeathMessage()) {
            player.sendMessage(messages.getRespawnMessage());
        }

        // Only show action bar if enabled
        if (config.isActionBarEnabled()) {
            startActionBarCountdown(player, delayTicks);
        }

        player.getScheduler().runDelayed(plugin, task -> {
            if (player.isOnline()) {
                executeRespawnCommand(player);
            }
            scheduledRespawns.remove(uuid);
        }, null, delayTicks);

        plugin.debug("Scheduled respawn for " + player.getName() + " in " + delayTicks + " ticks");
    }

    /**
     * Execute instant respawn (skip respawn screen)
     * Used when skip-respawn-screen is enabled
     */
    public void executeInstantRespawn(Player player) {
        UUID uuid = player.getUniqueId();

        if (scheduledRespawns.contains(uuid)) {
            plugin.debug("Respawn already scheduled for " + player.getName());
            return;
        }

        scheduledRespawns.add(uuid);
        teleportingPlayers.add(uuid);

        ConfigManager config = plugin.getConfigManager();

        // Only show death message if enabled
        if (config.isShowDeathMessage()) {
            player.sendMessage(plugin.getMessageManager().getRespawnMessage());
        }

        // Execute command after a very short delay (1 tick) to ensure death is
        // processed
        player.getScheduler().runDelayed(plugin, task -> {
            if (player.isOnline()) {
                // Respawn the player first (removes death screen)
                player.spigot().respawn();

                // Then execute the spawn command
                executeRespawnCommand(player);
            }
            scheduledRespawns.remove(uuid);
        }, null, 1L);

        plugin.debug("Instant respawn scheduled for " + player.getName());
    }

    private void startActionBarCountdown(Player player, int totalTicks) {
        MessageManager messages = plugin.getMessageManager();
        int totalSeconds = totalTicks / 20;

        if (totalSeconds <= 0) {
            return;
        }

        for (int i = 0; i < totalSeconds; i++) {
            final int secondsRemaining = totalSeconds - i;
            long delayTicks = (i * 20L) + 1;

            player.getScheduler().runDelayed(plugin, task -> {
                if (player.isOnline() && pendingRespawns.contains(player.getUniqueId())) {
                    player.sendActionBar(messages.getActionBar(secondsRemaining));
                }
            }, null, delayTicks);
        }
    }

    public void executeRespawnCommand(Player player) {
        if (!player.isOnline()) {
            pendingRespawns.remove(player.getUniqueId());
            return;
        }

        UUID uuid = player.getUniqueId();
        ConfigManager config = plugin.getConfigManager();
        String command = config.getRespawnCommand();

        // Mark player as teleporting (for freeze + damage protection)
        teleportingPlayers.add(uuid);
        pendingRespawns.remove(uuid);

        if (config.isRunAsConsole()) {
            // Run command as console using Global Region Scheduler (Folia thread-safe)
            String consoleCommand = config.getConsoleCommand().replace("%player%", player.getName());
            Bukkit.getGlobalRegionScheduler().run(plugin, task -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleCommand);
            });
            plugin.debug("Executed console command via GlobalRegionScheduler: " + consoleCommand);
        } else {
            // Run command as player (must be on player's thread)
            player.performCommand(command);
            plugin.debug("Player " + player.getName() + " executed command: /" + command);
        }

        // Schedule removal of teleporting status after spawn teleport duration
        int teleportDuration = config.getSpawnTeleportDuration();
        player.getScheduler().runDelayed(plugin, task -> {
            teleportingPlayers.remove(uuid);
            plugin.debug("Removed teleporting status for " + player.getName());
        }, null, teleportDuration);

        plugin.debug("Player " + player.getName() + " will be protected for " + (teleportDuration / 20) + " seconds");
    }

    public boolean hasPendingRespawn(UUID uuid) {
        return pendingRespawns.contains(uuid);
    }

    public boolean isTeleporting(UUID uuid) {
        return teleportingPlayers.contains(uuid);
    }

    public boolean isFrozen(UUID uuid) {
        return pendingRespawns.contains(uuid) || teleportingPlayers.contains(uuid);
    }

    public void cancelRespawn(UUID uuid) {
        pendingRespawns.remove(uuid);
        scheduledRespawns.remove(uuid);
        teleportingPlayers.remove(uuid);
    }
}
