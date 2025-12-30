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
     * Player dies normally, then spawn command runs immediately
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

        // Execute spawn command immediately after death
        // Do NOT call player.spigot().respawn() - let spawn command handle respawn
        // This ensures /back returns to actual death location
        executeRespawnCommand(player);
        scheduledRespawns.remove(uuid);

        plugin.debug("Instant spawn command executed for " + player.getName());
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
        String playerName = player.getName();

        // Mark player as teleporting (for freeze + damage protection)
        teleportingPlayers.add(uuid);
        pendingRespawns.remove(uuid);

        // All commands must run via GlobalRegionScheduler for Folia thread safety
        Bukkit.getGlobalRegionScheduler().run(plugin, task -> {
            if (config.isRunAsConsole()) {
                // Run command as console
                String consoleCommand = config.getConsoleCommand().replace("%player%", playerName);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleCommand);
                plugin.debug("Executed console command: " + consoleCommand);
            } else {
                // Run command as console but formatted for player (fallback)
                String command = config.getRespawnCommand() + " " + playerName;
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                plugin.debug("Executed command as console fallback: " + command);
            }
        });

        // Schedule removal of teleporting status after spawn teleport duration
        int teleportDuration = config.getSpawnTeleportDuration();
        Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task -> {
            teleportingPlayers.remove(uuid);
            plugin.debug("Removed teleporting status for " + playerName);
        }, teleportDuration);

        plugin.debug("Player " + playerName + " will be protected for " + (teleportDuration / 20) + " seconds");
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
