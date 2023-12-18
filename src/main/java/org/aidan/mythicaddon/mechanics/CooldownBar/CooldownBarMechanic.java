package org.aidan.mythicaddon.mechanics.CooldownBar;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class CooldownBarMechanic implements ITargetedEntitySkill {
    private final int duration;
    private final String text;
    private final BarColor color;
    private final BarStyle style;
    private final boolean fillUp;
    private final JavaPlugin plugin;

    public CooldownBarMechanic(JavaPlugin plugin, MythicLineConfig config) {
        this.plugin = plugin;
        this.duration = config.getInteger(new String[]{"duration", "d"}, 100);
        this.text = config.getString(new String[]{"text", "t"}, "Cooldown");
        this.color = BarColor.valueOf(config.getString(new String[]{"color", "c"}, "RED").toUpperCase());
        this.style = BarStyle.valueOf(config.getString(new String[]{"style", "s"}, "SOLID").toUpperCase());
        this.fillUp = config.getBoolean("fillup", false);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (target.isPlayer()) {
            Player player = (Player) target.getBukkitEntity();

            BossBar bossBar = Bukkit.createBossBar(text, color, style);
            bossBar.setProgress(fillUp ? 0.0 : 1.0);
            bossBar.addPlayer(player);
            bossBar.setVisible(true);

            final BukkitTask[] taskHolder = new BukkitTask[1];

            taskHolder[0] = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                private int ticks = 0;

                @Override
                public void run() {
                    if (ticks >= duration) {
                        bossBar.setVisible(false);
                        bossBar.removePlayer(player);
                        taskHolder[0].cancel();
                        return;
                    }

                    double progress = (double) ticks / duration;
                    bossBar.setProgress(fillUp ? progress : 1 - progress);
                    ticks++;
                }
            }, 0L, 1L);

            return SkillResult.SUCCESS;
        }
        return SkillResult.CONDITION_FAILED;
    }
}