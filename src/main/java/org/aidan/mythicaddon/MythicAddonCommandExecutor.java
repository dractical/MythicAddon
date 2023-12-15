package org.aidan.mythicaddon;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MythicAddonCommandExecutor implements CommandExecutor {
    private MythicAddon plugin;
    public MythicAddonCommandExecutor(MythicAddon plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("mythicaddon.reload")) {
                plugin.reloadConfig();
                // Validate the config after reloading
                if (validateConfig()) {
                    sender.sendMessage("Configuration reloaded and validated.");
                } else {
                    sender.sendMessage("Configuration reloaded, but validation failed. Check your config.");
                }
            } else {
                sender.sendMessage("You do not have permission to use this command.");
            }
            return true;
        }
        return false;
    }

    // Validate the plugin configuration
    private boolean validateConfig() {
        boolean isValid = true;
        // Example validation for a boolean setting
        if (plugin.getConfig().isSet("TimeStop.ProjectileSpawning") && !plugin.getConfig().isBoolean("TimeStop.ProjectileSpawning")) {
            isValid = false;
            plugin.getLogger().warning("Invalid configuration: 'TimeStop.ProjectileSpawning' must be a boolean.");
        }

        return isValid;
    }
}