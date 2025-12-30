package net.astroalchemist.multirespawn.commands;

import net.astroalchemist.multirespawn.MultiRespawn;
import net.astroalchemist.multirespawn.managers.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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
    private static final TextColor MAIN_COLOR = TextColor.fromHexString("#00fb9a");

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
        sender.sendMessage(Component.text("═══════════════════════════════════", MAIN_COLOR));
        sender.sendMessage(Component.text("       MultiRespawn ", MAIN_COLOR, TextDecoration.BOLD)
                .append(Component.text("v" + plugin.getPluginMeta().getVersion(), NamedTextColor.WHITE)));
        sender.sendMessage(Component.text("       Geliştirici: ", NamedTextColor.GRAY)
                .append(Component.text("AstroAlchemist", MAIN_COLOR)));
        sender.sendMessage(Component.text("═══════════════════════════════════", MAIN_COLOR));
        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text(" /multirespawn reload ", MAIN_COLOR)
                .append(Component.text("- Konfigürasyonu yeniden yükle", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text(" /multirespawn info ", MAIN_COLOR)
                .append(Component.text("- Plugin bilgilerini göster", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text(" /multirespawn help ", MAIN_COLOR)
                .append(Component.text("- Bu yardım menüsünü göster", NamedTextColor.GRAY)));
        sender.sendMessage(Component.empty());
    }

    private void sendInfo(CommandSender sender) {
        ConfigManager config = plugin.getConfigManager();
        var messages = plugin.getMessageManager();

        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text("═══════════════════════════════════", MAIN_COLOR));
        sender.sendMessage(Component.text("       MultiRespawn ", MAIN_COLOR, TextDecoration.BOLD)
                .append(Component.text("Bilgi", NamedTextColor.WHITE)));
        sender.sendMessage(Component.text("═══════════════════════════════════", MAIN_COLOR));
        sender.sendMessage(Component.empty());

        String status = config.isEnabled() ? messages.getStatusEnabled() : messages.getStatusDisabled();
        sender.sendMessage(Component.text(" Durum: ", NamedTextColor.GRAY).append(messages.colorize(status)));

        sender.sendMessage(Component.text(" Respawn Komutu: ", NamedTextColor.GRAY)
                .append(Component.text("/" + config.getRespawnCommand(), MAIN_COLOR)));

        int delaySec = config.getRespawnDelay() / 20;
        sender.sendMessage(Component.text(" Respawn Gecikmesi: ", NamedTextColor.GRAY)
                .append(Component.text(delaySec + messages.getSecondsUnit(), NamedTextColor.WHITE)));

        String pvpOnly = config.isOnlyPvpDeaths() ? messages.getYes() : messages.getNo();
        sender.sendMessage(Component.text(" Sadece PvP: ", NamedTextColor.GRAY).append(messages.colorize(pvpOnly)));

        String debug = config.isDebugEnabled() ? messages.getYes() : messages.getNo();
        sender.sendMessage(Component.text(" Debug Modu: ", NamedTextColor.GRAY).append(messages.colorize(debug)));

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
