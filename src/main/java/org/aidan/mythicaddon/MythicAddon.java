package org.aidan.mythicaddon;

import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class MythicAddon extends JavaPlugin implements Listener {

    private Logger log;

    @Override
    public void onEnable() {
        log = this.getLogger();
        Bukkit.getPluginManager().registerEvents(this, this);
        log.info("MythicAddon Plugin Enabled!");
    }

    @Override
    public void onDisable() {
        log.info("MythicAddon Plugin Disabled!");
    }

    @EventHandler
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event) {
        log.info("MythicMechanicLoadEvent called for mechanic " + event.getMechanicName());

        if (event.getMechanicName().equalsIgnoreCase("timestop")) {
            event.register(new TimeStopSkill(this, event.getConfig()));
            log.info("-- Registered TimeStop mechanic!");
        }
    }
}

