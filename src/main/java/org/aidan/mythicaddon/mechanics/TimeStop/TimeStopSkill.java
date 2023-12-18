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
    private final boolean spawnProjectile;
    private final boolean restoreVelocity;

    public TimeStopSkill(JavaPlugin plugin, MythicLineConfig config) {
        this.plugin = plugin;
        this.timeStopAbility = TimeStopAbility.getInstance(plugin);
        this.duration = config.getInteger(new String[]{"duration", "d"}, 200);

        // Initialize new attributes from config
        this.spawnProjectile = config.getBoolean("spawnprojectile", false);
        this.restoreVelocity = config.getBoolean("restorevelocity", true);
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        try {
            // Pass new attributes to the TimeStopAbility instance
            timeStopAbility.startFreezingProjectiles(spawnProjectile, restoreVelocity);
            Bukkit.getScheduler().runTaskLater(plugin,
                    () -> timeStopAbility.resumeProjectiles(restoreVelocity), duration);
            return SkillResult.SUCCESS;
        } catch (Exception e) {
            plugin.getLogger().severe("Error casting TimeStopSkill: " + e.getMessage());
            e.printStackTrace();
            return SkillResult.ERROR;
        }
    }
}
