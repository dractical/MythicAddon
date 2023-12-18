package org.aidan.mythicaddon.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;

public class HealthPercentCondition implements IEntityCondition {
    private final double healthPercent;
    private final String operator;

    public HealthPercentCondition(MythicLineConfig config) {
        String percentString = config.getString(new String[]{"percent", "p"}, "<50%");
        int operatorLength = percentString.startsWith(">=") || percentString.startsWith("<=") ? 2 : 1;
        operator = percentString.substring(0, operatorLength);
        healthPercent = Double.parseDouble(percentString.substring(operatorLength).replace("%", ""));
    }

    @Override
    public boolean check(AbstractEntity target) {
        double currentHealthPercent = (target.getHealth() / target.getMaxHealth()) * 100.0;

        final double tolerance = 0.0; // Tolerance level, e.g., 0.1%

        switch (operator) {
            case "<":
                return currentHealthPercent < healthPercent;
            case ">":
                return currentHealthPercent > healthPercent;
            case "=":
                return Math.abs(currentHealthPercent - healthPercent) < tolerance;
            case ">=":
                return currentHealthPercent >= healthPercent;
            case "<=":
                return currentHealthPercent <= healthPercent;
            default:
                return false;
        }
    }

}
