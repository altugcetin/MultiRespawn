package net.astroalchemist.multirespawn;

import net.astroalchemist.multirespawn.commands.BackCommand;
import net.astroalchemist.multirespawn.commands.MainCommand;
import net.astroalchemist.multirespawn.listeners.DeathListener;
import net.astroalchemist.multirespawn.listeners.FreezeListener;
import net.astroalchemist.multirespawn.listeners.RespawnListener;
import net.astroalchemist.multirespawn.managers.ConfigManager;
import net.astroalchemist.multirespawn.managers.DatabaseManager;
import net.astroalchemist.multirespawn.managers.MessageManager;
import net.astroalchemist.multirespawn.managers.RespawnManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class MultiRespawn extends JavaPlugin {
    private static MultiRespawn instance;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private RespawnManager respawnManager;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveResource("messages.yml", false);

        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.respawnManager = new RespawnManager(this);
        this.databaseManager = new DatabaseManager(this);

        if (!databaseManager.connect()) {
            getLogger().severe("Failed to connect to database! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(new RespawnListener(this), this);
        getServer().getPluginManager().registerEvents(new FreezeListener(this), this);

        MainCommand mainCommand = new MainCommand(this);
        getCommand("multirespawn").setExecutor(mainCommand);
        getCommand("multirespawn").setTabCompleter(mainCommand);

        if (configManager.isBackCommandEnabled()) {
            getCommand("back").setExecutor(new BackCommand(this));
        }

        log(Level.INFO, "MultiRespawn v" + getPluginMeta().getVersion() + " enabled!");
        log(Level.INFO, "Server: " + configManager.getServerName());
    }

    @Override
    public void onDisable() {
        if (databaseManager != null)
            databaseManager.close();
        log(Level.INFO, "MultiRespawn disabled!");
    }

    public static MultiRespawn getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public RespawnManager getRespawnManager() {
        return respawnManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public void reload() {
        reloadConfig();
        configManager.reload();
        messageManager.reload();
    }

    public void log(Level level, String message) {
        getLogger().log(level, message);
    }

    public void debug(String message) {
        if (configManager != null && configManager.isDebugEnabled()) {
            log(Level.INFO, "[DEBUG] " + message);
        }
    }
}
