package net.astroalchemist.multirespawn.managers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.astroalchemist.multirespawn.MultiRespawn;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Manages player transfers between servers
 * 
 * @author AstroAlchemist
 */
public class TransferManager {

    private final MultiRespawn plugin;
    private final Set<UUID> pendingTransfers = new HashSet<>();
    private final Set<UUID> scheduledTransfers = new HashSet<>();

    public TransferManager(MultiRespawn plugin) {
        this.plugin = plugin;
    }

    public void scheduleTransfer(Player player, String targetServer) {
        UUID uuid = player.getUniqueId();

        if (scheduledTransfers.contains(uuid)) {
            plugin.debug("Transfer already scheduled for " + player.getName());
            return;
        }

        scheduledTransfers.add(uuid);
        pendingTransfers.add(uuid);

        ConfigManager config = plugin.getConfigManager();
        MessageManager messages = plugin.getMessageManager();
        int delayTicks = config.getTeleportDelay();

        player.sendMessage(messages.getTransferDeathMessage(targetServer));

        if (config.isActionBarEnabled()) {
            startActionBarCountdown(player, delayTicks);
        }

        player.getScheduler().runDelayed(plugin, task -> {
            if (player.isOnline()) {
                executeTransfer(player, targetServer);
            }
            scheduledTransfers.remove(uuid);
        }, null, delayTicks);

        plugin.debug(
                "Scheduled transfer for " + player.getName() + " to " + targetServer + " in " + delayTicks + " ticks");
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
                if (player.isOnline() && pendingTransfers.contains(player.getUniqueId())) {
                    player.sendActionBar(messages.getActionBar(secondsRemaining));
                }
            }, null, delayTicks);
        }
    }

    public void executeTransfer(Player player, String targetServer) {
        if (!player.isOnline()) {
            pendingTransfers.remove(player.getUniqueId());
            return;
        }

        ConfigManager config = plugin.getConfigManager();

        if (config.isUseModernTransfer()) {
            executeModernTransfer(player, targetServer);
        } else {
            executeBungeeTransfer(player, targetServer);
        }

        pendingTransfers.remove(player.getUniqueId());
    }

    private void executeBungeeTransfer(Player player, String targetServer) {
        try {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(targetServer);

            player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
            plugin.debug("Sent BungeeCord Connect message for " + player.getName() + " to " + targetServer);
        } catch (Exception e) {
            plugin.getLogger()
                    .warning("Failed to transfer " + player.getName() + " to " + targetServer + ": " + e.getMessage());
            handleTransferFailure(player);
        }
    }

    private void executeModernTransfer(Player player, String targetServer) {
        try {
            player.transfer(targetServer, 25565);
            plugin.debug("Sent modern transfer for " + player.getName() + " to " + targetServer);
        } catch (Exception e) {
            plugin.getLogger().warning("Modern transfer failed, falling back to BungeeCord: " + e.getMessage());
            executeBungeeTransfer(player, targetServer);
        }
    }

    private void handleTransferFailure(Player player) {
        ConfigManager config = plugin.getConfigManager();
        MessageManager messages = plugin.getMessageManager();

        if (config.isUseFallbackRespawn()) {
            player.sendMessage(messages.getTransferFailed());
        }
    }

    public boolean hasPendingTransfer(UUID uuid) {
        return pendingTransfers.contains(uuid);
    }

    public void cancelTransfer(UUID uuid) {
        pendingTransfers.remove(uuid);
        scheduledTransfers.remove(uuid);
    }

    public void immediateTransfer(Player player, String targetServer) {
        player.getScheduler().runDelayed(plugin, task -> {
            if (player.isOnline()) {
                executeTransfer(player, targetServer);
            }
        }, null, 1L);
    }
}
