package org.aidan.mythicaddon.mechanics.TimeStop;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TimeStopSkill implements INoTargetSkill {
    private final int duration; // Duration of the time stop effect
    private final TimeStopAbility timeStopAbility; // Reference to TimeStopAbility
    private final JavaPlugin plugin; // Reference to the plugin

    public TimeStopSkill(JavaPlugin plugin, MythicLineConfig config) {
        this.plugin = plugin;
        this.timeStopAbility = TimeStopAbility.getInstance(plugin);
        this.duration = config.getInteger(new String[]{"duration", "d"}, 200); // Retrieves duration from config, default is 200
    }

    // Method to cast the Time Stop skill
    @Override
    public SkillResult cast(SkillMetadata data) {
        try {
            timeStopAbility.startFreezingProjectiles();
            plugin.getLogger().info("[TimeStopSkill] Time Stop state after activation: " + timeStopAbility.isTimeStopped);
            Bukkit.getScheduler().runTaskLater(plugin, () -> timeStopAbility.resumeProjectiles(), duration); // Schedules task to resume projectiles after the duration
            return SkillResult.SUCCESS; // Returns success result
        } catch (Exception e) {
            plugin.getLogger().severe("Error casting TimeStopSkill: " + e.getMessage()); // Logs any errors
            e.printStackTrace();
            return SkillResult.ERROR; // Returns error result
        }
    }
}
