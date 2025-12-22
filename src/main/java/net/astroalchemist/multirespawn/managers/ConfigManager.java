package net.astroalchemist.multirespawn.managers;

import net.astroalchemist.multirespawn.MultiRespawn;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages plugin configuration
 * 
 * @author AstroAlchemist
 */
public class ConfigManager {

    private final MultiRespawn plugin;

    private boolean enabled;
    private boolean debugEnabled;
    private int teleportDelay;
    private boolean showRespawnScreen;

    private String defaultTargetServer;
    private boolean useModernTransfer;
    private boolean useFallbackRespawn;

    private final Map<String, String> worldOverrides = new HashMap<>();

    private boolean onlyPvpDeaths;
    private boolean requirePermission;
    private String permissionNode;
    private List<String> enabledWorlds;
    private List<String> disabledWorlds;

    private boolean actionBarEnabled;

    public ConfigManager(MultiRespawn plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();

        enabled = plugin.getConfig().getBoolean("settings.enabled", true);
        debugEnabled = plugin.getConfig().getBoolean("settings.debug", false);
        teleportDelay = plugin.getConfig().getInt("settings.teleport-delay", 60);
        showRespawnScreen = plugin.getConfig().getBoolean("settings.show-respawn-screen", true);

        defaultTargetServer = plugin.getConfig().getString("transfer.target-server", "lobby");
        useModernTransfer = plugin.getConfig().getBoolean("transfer.use-modern-transfer", false);
        useFallbackRespawn = plugin.getConfig().getBoolean("transfer.fallback.use-default-respawn", true);

        worldOverrides.clear();
        String defaultWorld = plugin.getConfig().getString("worlds.default", "lobby");
        worldOverrides.put("default", defaultWorld);

        ConfigurationSection overrides = plugin.getConfig().getConfigurationSection("worlds.overrides");
        if (overrides != null) {
            for (String world : overrides.getKeys(false)) {
                worldOverrides.put(world.toLowerCase(), overrides.getString(world));
            }
        }

        onlyPvpDeaths = plugin.getConfig().getBoolean("conditions.only-pvp-deaths", false);
        requirePermission = plugin.getConfig().getBoolean("conditions.require-permission", false);
        permissionNode = plugin.getConfig().getString("conditions.permission-node", "multirespawn.transfer");
        enabledWorlds = plugin.getConfig().getStringList("conditions.enabled-worlds");
        disabledWorlds = plugin.getConfig().getStringList("conditions.disabled-worlds");

        actionBarEnabled = plugin.getConfig().getBoolean("actionbar.enabled", true);
    }

    public String getTargetServer(String worldName) {
        String server = worldOverrides.get(worldName.toLowerCase());
        if (server != null) {
            return server;
        }
        return worldOverrides.getOrDefault("default", defaultTargetServer);
    }

    public boolean isWorldEnabled(String worldName) {
        String lowerWorld = worldName.toLowerCase();

        if (disabledWorlds != null && !disabledWorlds.isEmpty()) {
            for (String disabled : disabledWorlds) {
                if (disabled.equalsIgnoreCase(lowerWorld)) {
                    return false;
                }
            }
        }

        if (enabledWorlds == null || enabledWorlds.isEmpty()) {
            return true;
        }

        for (String enabled : enabledWorlds) {
            if (enabled.equalsIgnoreCase(lowerWorld)) {
                return true;
            }
        }

        return false;
    }

    public Component colorize(String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public int getTeleportDelay() {
        return teleportDelay;
    }

    public boolean isShowRespawnScreen() {
        return showRespawnScreen;
    }

    public String getDefaultTargetServer() {
        return defaultTargetServer;
    }

    public boolean isUseModernTransfer() {
        return useModernTransfer;
    }

    public boolean isUseFallbackRespawn() {
        return useFallbackRespawn;
    }

    public boolean isOnlyPvpDeaths() {
        return onlyPvpDeaths;
    }

    public boolean isRequirePermission() {
        return requirePermission;
    }

    public String getPermissionNode() {
        return permissionNode;
    }

    public boolean isActionBarEnabled() {
        return actionBarEnabled;
    }
}
