package net.astroalchemist.multirespawn.managers;

import net.astroalchemist.multirespawn.MultiRespawn;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages plugin messages from messages.yml
 * 
 * @author AstroAlchemist
 */
public class MessageManager {

    private final MultiRespawn plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;
    private final Map<String, String> messageCache = new HashMap<>();

    public MessageManager(MultiRespawn plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    public void loadMessages() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        InputStream defaultStream = plugin.getResource("messages.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            messagesConfig.setDefaults(defaultConfig);
        }

        messageCache.clear();
        plugin.debug("Messages loaded from messages.yml");
    }

    public void reload() {
        loadMessages();
    }

    public String getRaw(String path) {
        if (messageCache.containsKey(path)) {
            return messageCache.get(path);
        }
        String message = messagesConfig.getString(path, "Missing: " + path);
        messageCache.put(path, message);
        return message;
    }

    public String getRaw(String path, String... replacements) {
        String message = getRaw(path);
        for (int i = 0; i < replacements.length - 1; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        return message;
    }

    public String getPrefix() {
        return getRaw("prefix");
    }

    public Component colorize(String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    public Component get(String path) {
        return colorize(getPrefix() + getRaw(path));
    }

    public Component get(String path, String... replacements) {
        return colorize(getPrefix() + getRaw(path, replacements));
    }

    public Component getTransferDeathMessage(String serverName) {
        return get("transfer.death-message", "{server}", serverName);
    }

    public Component getTransferSuccess() {
        return get("transfer.success");
    }

    public Component getTransferFailed() {
        return get("transfer.failed");
    }

    public Component getActionBar(int seconds) {
        return colorize(getRaw("transfer.actionbar", "{seconds}", String.valueOf(seconds)));
    }

    public Component getBypassMessage() {
        return get("bypass.message");
    }

    public Component getReloadSuccess() {
        return get("commands.reload-success");
    }

    public Component getNoPermission() {
        return get("commands.no-permission");
    }

    public String getStatusEnabled() {
        return getRaw("status.enabled");
    }

    public String getStatusDisabled() {
        return getRaw("status.disabled");
    }

    public String getYes() {
        return getRaw("status.yes");
    }

    public String getNo() {
        return getRaw("status.no");
    }

    public String getSecondsUnit() {
        return getRaw("status.seconds");
    }
}
