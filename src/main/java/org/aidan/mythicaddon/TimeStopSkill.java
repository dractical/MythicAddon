package org.aidan.mythicaddon;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TimeStopSkill implements INoTargetSkill {
    private final int duration;
    private final TimeStopAbility timeStopAbility;
    private final JavaPlugin plugin;
    public TimeStopSkill(JavaPlugin plugin, MythicLineConfig config) {
        this.plugin = plugin;
        this.timeStopAbility = new TimeStopAbility(plugin);
        this.duration = config.getInteger(new String[]{"duration", "d"}, 200); // Default duration
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        try {
            timeStopAbility.startFreezingProjectiles();
            Bukkit.getScheduler().runTaskLater(plugin, () -> timeStopAbility.resumeProjectiles(), duration);
            return SkillResult.SUCCESS;
        } catch (Exception e) {
            plugin.getLogger().severe("Error casting TimeStopSkill: " + e.getMessage());
            e.printStackTrace();
            return SkillResult.ERROR;
        }
    }
}
