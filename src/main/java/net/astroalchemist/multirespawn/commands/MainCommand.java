package net.astroalchemist.multirespawn.commands;

import net.astroalchemist.multirespawn.MultiRespawn;
import net.astroalchemist.multirespawn.managers.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main command handler for MultiRespawn
 * 
 * @author AstroAlchemist
 */
public class MainCommand implements CommandExecutor, TabCompleter {

    private final MultiRespawn plugin;
    private final List<String> subcommands = Arrays.asList("reload", "info", "help");

    public MainCommand(MultiRespawn plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        var messages = plugin.getMessageManager();

        if (!sender.hasPermission("multirespawn.admin")) {
            sender.sendMessage(messages.getNoPermission());
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reload();
                sender.sendMessage(messages.getReloadSuccess());
                break;
            case "info":
                sendInfo(sender);
                break;
            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text("═══════════════════════════════════", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("       MultiRespawn ", NamedTextColor.GOLD, TextDecoration.BOLD)
                .append(Component.text("v" + plugin.getPluginMeta().getVersion(), NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("       Developer: ", NamedTextColor.GRAY)
                .append(Component.text("AstroAlchemist", NamedTextColor.AQUA)));
        sender.sendMessage(Component.text("═══════════════════════════════════", NamedTextColor.GOLD));
        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text(" /multirespawn reload ", NamedTextColor.YELLOW)
                .append(Component.text("- Reload configuration", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text(" /multirespawn info ", NamedTextColor.YELLOW)
                .append(Component.text("- Show plugin information", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text(" /multirespawn help ", NamedTextColor.YELLOW)
                .append(Component.text("- Show this help menu", NamedTextColor.GRAY)));
        sender.sendMessage(Component.empty());
    }

    private void sendInfo(CommandSender sender) {
        ConfigManager config = plugin.getConfigManager();
        var messages = plugin.getMessageManager();

        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text("═══════════════════════════════════", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("       MultiRespawn ", NamedTextColor.GOLD, TextDecoration.BOLD)
                .append(Component.text("Info", NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("═══════════════════════════════════", NamedTextColor.GOLD));
        sender.sendMessage(Component.empty());

        String status = config.isEnabled() ? messages.getStatusEnabled() : messages.getStatusDisabled();
        sender.sendMessage(Component.text(" Status: ", NamedTextColor.GRAY).append(messages.colorize(status)));

        sender.sendMessage(Component.text(" Target Server: ", NamedTextColor.GRAY)
                .append(Component.text(config.getDefaultTargetServer(), NamedTextColor.AQUA)));

        int delaySec = config.getTeleportDelay() / 20;
        sender.sendMessage(Component.text(" Transfer Delay: ", NamedTextColor.GRAY)
                .append(Component.text(delaySec + messages.getSecondsUnit(), NamedTextColor.YELLOW)));

        String mode = config.isUseModernTransfer() ? "Modern (1.20.5+)" : "BungeeCord";
        sender.sendMessage(Component.text(" Transfer Mode: ", NamedTextColor.GRAY)
                .append(Component.text(mode, NamedTextColor.YELLOW)));

        String pvpOnly = config.isOnlyPvpDeaths() ? messages.getYes() : messages.getNo();
        sender.sendMessage(Component.text(" PvP Only: ", NamedTextColor.GRAY).append(messages.colorize(pvpOnly)));

        String debug = config.isDebugEnabled() ? messages.getYes() : messages.getNo();
        sender.sendMessage(Component.text(" Debug Mode: ", NamedTextColor.GRAY).append(messages.colorize(debug)));

        sender.sendMessage(Component.empty());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("multirespawn.admin")) {
            return new ArrayList<>();
        }
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (String sub : subcommands) {
                if (sub.startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
            return completions;
        }
        return new ArrayList<>();
    }
}
