package net.astroalchemist.multirespawn.commands;

import net.astroalchemist.multirespawn.MultiRespawn;
import net.astroalchemist.multirespawn.data.DeathLocation;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackCommand implements CommandExecutor {
    private final MultiRespawn plugin;

    public BackCommand(MultiRespawn plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }

        if (!player.hasPermission("multirespawn.back")) {
            player.sendMessage(plugin.getMessageManager().getNoPermission());
            return true;
        }

        if (plugin.getDatabaseManager() == null) {
            player.sendMessage("§cDatabase not available.");
            return true;
        }

        DeathLocation loc = plugin.getDatabaseManager().getDeathLocation(player.getUniqueId().toString());
        if (loc == null) {
            player.sendMessage(plugin.getMessageManager().getBackNoLocation());
            return true;
        }

        player.sendMessage(plugin.getMessageManager().getBackTeleporting());

        String cmd = String.format("huskhomes:tp %s %.2f %.2f %.2f %.2f %.2f %s %s",
                player.getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), loc.getWorld(),
                loc.getServer());

        Bukkit.getGlobalRegionScheduler().run(plugin, task -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        });

        plugin.debug("Back command: " + cmd);
        return true;
    }
}
