package net.astroalchemist.multirespawn.managers;

import net.astroalchemist.multirespawn.MultiRespawn;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RespawnManager {
    private final MultiRespawn plugin;
    private final Set<UUID> pendingRespawns = ConcurrentHashMap.newKeySet();
    private final Set<UUID> scheduledRespawns = ConcurrentHashMap.newKeySet();
    private final Set<UUID> teleportingPlayers = ConcurrentHashMap.newKeySet();

    public RespawnManager(MultiRespawn plugin) {
        this.plugin = plugin;
    }

    public void scheduleRespawn(Player player) {
        UUID uuid = player.getUniqueId();
        if (scheduledRespawns.contains(uuid))
            return;

        scheduledRespawns.add(uuid);
        pendingRespawns.add(uuid);

        ConfigManager config = plugin.getConfigManager();
        int delayTicks = config.getRespawnDelay();

        if (config.isShowDeathMessage()) {
            player.sendMessage(plugin.getMessageManager().getRespawnMessage());
        }

        if (config.isActionBarEnabled()) {
            startActionBarCountdown(player, delayTicks);
        }

        player.getScheduler().runDelayed(plugin, task -> {
            if (player.isOnline())
                executeRespawnCommand(player);
            scheduledRespawns.remove(uuid);
        }, null, delayTicks);
    }

    public void executeInstantRespawn(Player player) {
        UUID uuid = player.getUniqueId();
        if (scheduledRespawns.contains(uuid))
            return;

        scheduledRespawns.add(uuid);
        teleportingPlayers.add(uuid);

        if (plugin.getConfigManager().isShowDeathMessage()) {
            player.sendMessage(plugin.getMessageManager().getRespawnMessage());
        }

        executeRespawnCommand(player);
        scheduledRespawns.remove(uuid);
    }

    private void startActionBarCountdown(Player player, int totalTicks) {
        MessageManager messages = plugin.getMessageManager();
        int totalSeconds = totalTicks / 20;
        if (totalSeconds <= 0)
            return;

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

        teleportingPlayers.add(uuid);
        pendingRespawns.remove(uuid);

        Bukkit.getGlobalRegionScheduler().run(plugin, task -> {
            String cmd = config.isRunAsConsole()
                    ? config.getConsoleCommand().replace("%player%", playerName)
                    : config.getRespawnCommand() + " " + playerName;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            plugin.debug("Command: " + cmd);
        });

        int duration = config.getSpawnTeleportDuration();
        Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task -> {
            teleportingPlayers.remove(uuid);
        }, duration);
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
