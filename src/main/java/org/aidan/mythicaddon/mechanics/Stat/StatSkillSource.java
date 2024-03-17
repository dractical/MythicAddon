package org.aidan.mythicaddon.mechanics.Stat;

import io.lumine.mythic.core.skills.stats.StatSource;

import java.util.UUID;

public class StatSkillSource implements StatSource {
    private final UUID uuid;

    public StatSkillSource() {
        this.uuid = UUID.randomUUID();
    }
}
