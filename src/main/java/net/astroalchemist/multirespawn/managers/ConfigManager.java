package net.astroalchemist.multirespawn.managers;

import net.astroalchemist.multirespawn.MultiRespawn;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;

/**
 * Manages plugin configuration
 * 
 * @author AstroAlchemist
 */
public class ConfigManager {

    private final MultiRespawn plugin;

    private boolean enabled;
    private boolean debugEnabled;
    private int respawnDelay;
    private int spawnTeleportDuration;
    private boolean skipRespawnScreen;

    // Respawn command settings
    private String respawnCommand;
    private boolean runAsConsole;
    private String consoleCommand;

    // Message settings
    private boolean showDeathMessage;
    private boolean showActionbar;
    private boolean showBypassMessage;

    // Conditions
    private boolean onlyPvpDeaths;
    private boolean requirePermission;
    private String permissionNode;
    private List<String> enabledWorlds;
    private List<String> disabledWorlds;

    // Freeze settings
    private boolean freezeEnabled;
    private boolean freezeRotation;
    private String freezeMessage;

    public ConfigManager(MultiRespawn plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();

        enabled = plugin.getConfig().getBoolean("settings.enabled", true);
        debugEnabled = plugin.getConfig().getBoolean("settings.debug", false);
        respawnDelay = plugin.getConfig().getInt("settings.respawn-delay", 60);
        spawnTeleportDuration = plugin.getConfig().getInt("settings.spawn-teleport-duration", 100);
        skipRespawnScreen = plugin.getConfig().getBoolean("settings.skip-respawn-screen", true);

        // Respawn command settings
        respawnCommand = plugin.getConfig().getString("respawn.command", "spawn");
        runAsConsole = plugin.getConfig().getBoolean("respawn.run-as-console", true);
        consoleCommand = plugin.getConfig().getString("respawn.console-command", "spawn %player%");

        // Message settings
        showDeathMessage = plugin.getConfig().getBoolean("messages.show-death-message", false);
        showActionbar = plugin.getConfig().getBoolean("messages.show-actionbar", false);
        showBypassMessage = plugin.getConfig().getBoolean("messages.show-bypass-message", false);

        // Conditions
        onlyPvpDeaths = plugin.getConfig().getBoolean("conditions.only-pvp-deaths", false);
        requirePermission = plugin.getConfig().getBoolean("conditions.require-permission", false);
        permissionNode = plugin.getConfig().getString("conditions.permission-node", "multirespawn.use");
        enabledWorlds = plugin.getConfig().getStringList("conditions.enabled-worlds");
        disabledWorlds = plugin.getConfig().getStringList("conditions.disabled-worlds");

        // Freeze settings
        freezeEnabled = plugin.getConfig().getBoolean("freeze.enabled", true);
        freezeRotation = plugin.getConfig().getBoolean("freeze.freeze-rotation", false);
        freezeMessage = plugin.getConfig().getString("freeze.freeze-message",
                "&c&lIşınlanma sırasında hareket edemezsiniz!");
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

        for (String world : enabledWorlds) {
            if (world.equalsIgnoreCase(lowerWorld)) {
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

    // Getters
    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public int getRespawnDelay() {
        return respawnDelay;
    }

    public int getSpawnTeleportDuration() {
        return spawnTeleportDuration;
    }

    public String getRespawnCommand() {
        return respawnCommand;
    }

    public boolean isRunAsConsole() {
        return runAsConsole;
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
        return showActionbar;
    }

    public boolean isSkipRespawnScreen() {
        return skipRespawnScreen;
    }

    public String getConsoleCommand() {
        return consoleCommand;
    }

    public boolean isShowDeathMessage() {
        return showDeathMessage;
    }

    public boolean isShowBypassMessage() {
        return showBypassMessage;
    }

    public boolean isFreezeEnabled() {
        return freezeEnabled;
    }

    public boolean isFreezeRotation() {
        return freezeRotation;
    }

    public String getFreezeMessage() {
        return freezeMessage;
    }
}
