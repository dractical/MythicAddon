package org.aidan.mythicaddon.mechanics.Stat;

public enum CalculModifier {
    RELATIVE("COMPOUND_MULTIPLIER", "RELATIVE"),
    FLAT("ADDITIVE", "FLAT");

    private final String MMobModifierType;
    private final String MLibModifierType;

    private CalculModifier(String MMobType, String MLibType) {
        this.MMobModifierType = MMobType;
        this.MLibModifierType = MLibType;
    }

    public String getModifierByType(PluginType pluginType) {
        switch (pluginType) {
            case MLIB:
                return this.MLibModifierType;
            case MYTHIC:
                return this.MMobModifierType;
            default:
                return "";
        }
    }

    public String getMLibModifierType() {
        return this.MLibModifierType;
    }

    public String getMMobModifierType() {
        return this.MMobModifierType;
    }
}
