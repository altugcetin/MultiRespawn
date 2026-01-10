package net.astroalchemist.multirespawn.managers;

import net.astroalchemist.multirespawn.MultiRespawn;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;

public class ConfigManager {
    private final MultiRespawn plugin;

    private boolean enabled;
    private boolean debugEnabled;
    private int respawnDelay;
    private int spawnTeleportDuration;
    private boolean skipRespawnScreen;
    private String serverName;

    private String respawnCommand;
    private boolean runAsConsole;
    private String consoleCommand;

    private boolean showDeathMessage;
    private boolean showActionbar;
    private boolean showBypassMessage;

    private boolean onlyPvpDeaths;
    private boolean requirePermission;
    private String permissionNode;
    private List<String> enabledWorlds;
    private List<String> disabledWorlds;

    private boolean freezeEnabled;
    private boolean freezeRotation;
    private String freezeMessage;

    private String mysqlHost;
    private int mysqlPort;
    private String mysqlDatabase;
    private String mysqlUsername;
    private String mysqlPassword;
    private String redisHost;
    private int redisPort;
    private String redisPassword;
    private boolean backCommandEnabled;

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
        serverName = plugin.getConfig().getString("settings.server-name", "lobby");

        respawnCommand = plugin.getConfig().getString("respawn.command", "spawn");
        runAsConsole = plugin.getConfig().getBoolean("respawn.run-as-console", true);
        consoleCommand = plugin.getConfig().getString("respawn.console-command", "huskhomes:spawn %player%");

        showDeathMessage = plugin.getConfig().getBoolean("messages.show-death-message", false);
        showActionbar = plugin.getConfig().getBoolean("messages.show-actionbar", false);
        showBypassMessage = plugin.getConfig().getBoolean("messages.show-bypass-message", false);

        onlyPvpDeaths = plugin.getConfig().getBoolean("conditions.only-pvp-deaths", false);
        requirePermission = plugin.getConfig().getBoolean("conditions.require-permission", false);
        permissionNode = plugin.getConfig().getString("conditions.permission-node", "multirespawn.use");
        enabledWorlds = plugin.getConfig().getStringList("conditions.enabled-worlds");
        disabledWorlds = plugin.getConfig().getStringList("conditions.disabled-worlds");

        freezeEnabled = plugin.getConfig().getBoolean("freeze.enabled", true);
        freezeRotation = plugin.getConfig().getBoolean("freeze.freeze-rotation", false);
        freezeMessage = plugin.getConfig().getString("freeze.freeze-message",
                "&c&lTeleport sırasında hareket edemezsiniz!");

        mysqlHost = plugin.getConfig().getString("database.mysql.host", "localhost");
        mysqlPort = plugin.getConfig().getInt("database.mysql.port", 3306);
        mysqlDatabase = plugin.getConfig().getString("database.mysql.database", "multirespawn");
        mysqlUsername = plugin.getConfig().getString("database.mysql.username", "root");
        mysqlPassword = plugin.getConfig().getString("database.mysql.password", "");
        redisHost = plugin.getConfig().getString("database.redis.host", "localhost");
        redisPort = plugin.getConfig().getInt("database.redis.port", 6379);
        redisPassword = plugin.getConfig().getString("database.redis.password", "");
        backCommandEnabled = plugin.getConfig().getBoolean("back-command.enabled", true);
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

    public String getServerName() {
        return serverName;
    }

    public String getMysqlHost() {
        return mysqlHost;
    }

    public int getMysqlPort() {
        return mysqlPort;
    }

    public String getMysqlDatabase() {
        return mysqlDatabase;
    }

    public String getMysqlUsername() {
        return mysqlUsername;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public String getRedisHost() {
        return redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public String getRedisPassword() {
        return redisPassword;
    }

    public boolean isBackCommandEnabled() {
        return backCommandEnabled;
    }
}
