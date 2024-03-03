package org.aidan.mythicaddon.mechanics.Stat;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.modifier.TemporaryStatModifier;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class StatSkill implements ITargetedEntitySkill {
    private final JavaPlugin plugin;
    private final String statsType;
    private final ModifierType modifierType;
    private final double value;
    // Duration time in ticks
    private final long duration;

    public StatSkill(JavaPlugin plugin, MythicLineConfig config) {
        this.plugin = plugin;
        this.statsType = config.getString("statsType", "MAX_HEALTH");
        ModifierType modifierType1;
        try {
            modifierType1 = ModifierType.valueOf(config.getString("modifierType"));
        } catch(IllegalArgumentException error) {
            System.out.println("The modifier type of StatSkill doesn't fit the name pattern (FLAT or RELATIVE). ModifierType set to FLAT by default.");
            modifierType1 = ModifierType.FLAT;
        }
        this.modifierType = modifierType1;
        this.value = config.getDouble("value", 1.0d);
        this.duration = config.getLong(new String[] { "duration" }, 100l);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        final MMOPlayerData targetMMOPlayerData = MMOPlayerData.get(abstractEntity.getUniqueId());
        if (targetMMOPlayerData == null) {
            return SkillResult.INVALID_TARGET;
        }

        final String keyStats = UUID.randomUUID().toString();
        TemporaryStatModifier statModifier = new TemporaryStatModifier(keyStats, this.statsType, this.value, this.modifierType, EquipmentSlot.OTHER, ModifierSource.OTHER);
        statModifier.register(targetMMOPlayerData, this.duration);
        return SkillResult.SUCCESS;
    }
}
