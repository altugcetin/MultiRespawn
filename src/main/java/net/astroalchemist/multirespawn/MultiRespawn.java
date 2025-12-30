package net.astroalchemist.multirespawn;

import net.astroalchemist.multirespawn.commands.MainCommand;
import net.astroalchemist.multirespawn.listeners.DeathListener;
import net.astroalchemist.multirespawn.listeners.FreezeListener;
import net.astroalchemist.multirespawn.listeners.RespawnListener;
import net.astroalchemist.multirespawn.managers.ConfigManager;
import net.astroalchemist.multirespawn.managers.MessageManager;
import net.astroalchemist.multirespawn.managers.RespawnManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * MultiRespawn - Respawn command plugin for Folia
 * 
 * @author AstroAlchemist
 * @version 1.0.0
 */
public class MultiRespawn extends JavaPlugin {

    private static MultiRespawn instance;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private RespawnManager respawnManager;

    @Override
    public void onEnable() {
        instance = this;

        // Save default configs
        saveDefaultConfig();
        saveResource("messages.yml", false);

        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.respawnManager = new RespawnManager(this);

        // Register listeners
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(new RespawnListener(this), this);
        getServer().getPluginManager().registerEvents(new FreezeListener(this), this);

        // Register commands
        MainCommand mainCommand = new MainCommand(this);
        getCommand("multirespawn").setExecutor(mainCommand);
        getCommand("multirespawn").setTabCompleter(mainCommand);

        log(Level.INFO, "MultiRespawn v" + getPluginMeta().getVersion() + " enabled!");
        log(Level.INFO, "Developer: AstroAlchemist");
        log(Level.INFO, "Respawn command: /" + configManager.getRespawnCommand());
    }

    @Override
    public void onDisable() {
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
