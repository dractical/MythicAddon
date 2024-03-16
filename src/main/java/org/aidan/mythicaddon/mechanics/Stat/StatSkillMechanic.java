package org.aidan.mythicaddon.mechanics.Stat;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.stats.StatModifierType;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.modifier.TemporaryStatModifier;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.UUID;

public class StatSkillMechanic implements ITargetedEntitySkill {
    private final JavaPlugin plugin;
    private final String statsType;
    private final PluginType pluginType;
    // FLAT OR RELATIVE
    private final CalculModifier modifierType;
    private final double value;
    // Duration time in ticks
    private final long duration;

    public StatSkillMechanic(JavaPlugin plugin, MythicLineConfig config) {
        this.plugin = plugin;
        this.statsType = config.getString("statsType", "MAX_HEALTH").toUpperCase();

        PluginType pluginTypeCkeck;
        final String pType = config.getString("pluginType", "MYTHIC").toUpperCase();
        if (!pType.equals("MYTHIC") && !pType.equals("MLIB")) {
            System.out.println("Wrong plugin type in StatSkill, set to MYTHIC by default.");
            pluginTypeCkeck = PluginType.MYTHIC;
        } else {
            try {
                // Normally can't throw IllegalArgumentException but just in case
                pluginTypeCkeck = PluginType.valueOf(pType);
            } catch (IllegalArgumentException error) {
                System.out.println("If you see this message that's not normal.");
                error.printStackTrace();
                pluginTypeCkeck = PluginType.MYTHIC;
            }
        }
        this.pluginType = pluginTypeCkeck;

        CalculModifier modifierTypeCheck;
        try {
            modifierTypeCheck = CalculModifier.valueOf(config.getString("modifierType").toUpperCase());
        } catch(IllegalArgumentException error) {
            System.out.println("The modifier type of StatSkill doesn't fit the name pattern (FLAT or RELATIVE). ModifierType set to FLAT by default.");
            modifierTypeCheck = CalculModifier.FLAT;
        }
        this.modifierType = modifierTypeCheck;

        this.value = config.getDouble("value", 1.0d);
        this.duration = config.getLong(new String[] { "duration" }, 100l);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if (abstractEntity == null) {
            return SkillResult.INVALID_TARGET;
        }

        if (this.pluginType.equals(PluginType.MYTHIC)) {
            return this.handleMMob(skillMetadata, abstractEntity);
        } else if (this.pluginType.equals(PluginType.MLIB)) {
            return this.handlePlayerMLib(skillMetadata, abstractEntity);
        }

        return SkillResult.INVALID_CONFIG;
    }

    private SkillResult handlePlayerMLib(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if (this.plugin.getServer().getPluginManager().getPlugin("MythicLib") == null) {
            System.out.println("To use the StatSkill with MMOCore you need to have MythicLib installed.");
            return SkillResult.ERROR;
        }

        final MMOPlayerData targetMMOPlayerData = MMOPlayerData.getOrNull(abstractEntity.getUniqueId());
        if (targetMMOPlayerData == null) {
            return SkillResult.INVALID_TARGET;
        }

        final String keyStats = UUID.randomUUID().toString();
        ModifierType statModifierType;
        try {
            statModifierType = ModifierType.valueOf(this.modifierType.getModifierByType(this.pluginType));
        } catch(IllegalArgumentException error) {
            statModifierType = ModifierType.FLAT;
        }

        TemporaryStatModifier statModifier = new TemporaryStatModifier(keyStats, this.statsType, this.value, statModifierType, EquipmentSlot.OTHER, ModifierSource.OTHER);
        statModifier.register(targetMMOPlayerData, this.duration);
        return SkillResult.SUCCESS;
    }

    private SkillResult handleMMob(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        final Optional<StatRegistry> entityStatRegistry = MythicBukkit.inst().getStatManager().getStatRegistry(abstractEntity);
        if (!entityStatRegistry.isPresent()) {
            return SkillResult.INVALID_TARGET;
        }

        final Optional<StatType> statsOpt = MythicBukkit.inst().getStatManager().getStat(this.statsType);
        if (!statsOpt.isPresent()) {
            System.out.println("invalid config");
            return SkillResult.INVALID_CONFIG;
        }

        final StatType type = statsOpt.get();
        final Optional<StatRegistry.StatMap> statMapOpt = entityStatRegistry.get().getStatData(type);
        if (!statMapOpt.isPresent()) {
            return SkillResult.MISSING_COMPATIBILITY;
        }

        final StatRegistry.StatMap statMap = statMapOpt.get();
        final StatSkillSource statSource = new StatSkillSource();
        StatModifierType statModifierType;
        try {
            statModifierType = StatModifierType.valueOf(this.modifierType.getModifierByType(this.pluginType));
        } catch(IllegalArgumentException error) {
            statModifierType = StatModifierType.ADDITIVE;
        }

        statMap.put(statSource, statModifierType, this.value);

        if (this.duration > 0l) {
            Schedulers.sync().runLater(() -> statMap.remove(statSource), this.duration);
        }
        return SkillResult.SUCCESS;
    }
}
