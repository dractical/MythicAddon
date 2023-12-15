package org.aidan.mythicaddon;

import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import org.aidan.mythicaddon.mechanics.TimeStop.TimeStopAbility;
import org.aidan.mythicaddon.mechanics.TimeStop.TimeStopSkill;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class MythicAddon extends JavaPlugin implements Listener {
    private Logger log;
    private TimeStopAbility timeStopAbility; // Reference to TimeStopAbility

    // Called when the plugin is enabled
    @Override
    public void onEnable() {
        saveDefaultConfig(); // Saves the default configuration
        updateConfig();
        log = this.getLogger(); // Retrieves the plugin's logger
        log.info("MythicAddon Plugin Enabled!"); // Logs plugin enabled message
        String version = getServerVersion(); // Retrieves the server version
        log.info("Server is running Minecraft version: " + version); // Logs server version

        // Registers the plugin as an event listener
        Bukkit.getPluginManager().registerEvents(this, this);
        timeStopAbility = TimeStopAbility.getInstance(this);
        log.info("[MythicAbilities] Event Listener registered."); // Logs event listener registration
        this.getCommand("mythicaddon").setExecutor(new MythicAddonCommandExecutor(this));
    }

    // Called when the plugin is disabled
    @Override
    public void onDisable() {
        log.info("MythicAddon Plugin Disabled!"); // Logs plugin disabled message
    }

    // Event handler for MythicMechanicLoadEvent
    @EventHandler
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event) {
        log.info("MythicMechanicLoadEvent called for mechanic " + event.getMechanicName()); // Logs event call

        // Checks if the mechanic name is "timestop"
        if (event.getMechanicName().equalsIgnoreCase("timestop")) {
            event.register(new TimeStopSkill(this, event.getConfig())); // Registers TimeStopSkill
            log.info("-- Registered TimeStop mechanic!"); // Logs mechanic registration
        }
    }

    // Event handler for ProjectileLaunchEvent
    @EventHandler
        public void onProjectileLaunch(ProjectileLaunchEvent event) {
        // Checks if time is stopped
        if (timeStopAbility.isTimeStopped && !this.getConfig().getBoolean("TimeStop.ProjectileSpawning")) {
            event.setCancelled(true); // Cancels the projectile launch
        }
    }

    // Event handler for EntityShootBowEvent
    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        // Checks if time is stopped
        if (timeStopAbility.isTimeStopped && !this.getConfig().getBoolean("TimeStop.ProjectileSpawning")) {
            event.setCancelled(true);
        }
    }

    // Retrieves the server version
    private String getServerVersion() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    private void updateConfig() {
        // Define the current config version for your plugin
        final double currentConfigVersion = 1.2;

        // Check if the config version is outdated
        if (this.getConfig().getDouble("config-version", 0.0) < currentConfigVersion) {
            // Update the config
            updateConfiguration();
            // Set the new version
            this.getConfig().set("config-version", currentConfigVersion);
            // Save the config
            this.saveConfig();
        }
    }
    private void updateConfiguration() {
        FileConfiguration config = this.getConfig();
        double currentConfigVersion = 1.2; // Update this to the latest version
        double configVersion = config.getDouble("config-version", 0.0);

        if (configVersion < currentConfigVersion) {
            if (config.contains("TimeStop.CancelProjectileSpawning")) {
                config.set("TimeStop.ProjectileSpawning", false); // Invert the value
                config.set("TimeStop.RestoreVelocities", true);
                config.set("TimeStop.CancelProjectileSpawning", null); // Remove the old key
            }
            config.set("config-version", currentConfigVersion);
            this.saveConfig();
        }
    }
}
