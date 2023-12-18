package org.aidan.mythicaddon.mechanics.DropItemSlot;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class DropItemSlotMechanic implements ITargetedEntitySkill {
    private int slot;
    private final JavaPlugin plugin;

    public DropItemSlotMechanic(JavaPlugin plugin, MythicLineConfig config) {
        this.plugin = plugin;
        this.slot = config.getInteger(new String[]{"slot", "s"}, -1);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (target.isLiving()) {
            LivingEntity entity = (LivingEntity) target.getBukkitEntity();

            ItemStack itemToDrop;
            if (entity instanceof Player) {
                // For players, handle all inventory slots
                Player player = (Player) entity;
                itemToDrop = player.getInventory().getItem(slot);
                if (itemToDrop != null && !itemToDrop.getType().isAir()) {
                    entity.getWorld().dropItemNaturally(entity.getLocation(), itemToDrop);
                    player.getInventory().setItem(slot, null);
                    return SkillResult.SUCCESS;
                }
            } else {
                EntityEquipment equipment = entity.getEquipment();
                if (equipment != null) {
                    EquipmentSlot equipmentSlot = convertSlotToEquipmentSlot(slot);
                    if (equipmentSlot != null) {
                        itemToDrop = equipment.getItem(equipmentSlot);
                        if (itemToDrop != null && !itemToDrop.getType().isAir()) {
                            entity.getWorld().dropItemNaturally(entity.getLocation(), itemToDrop);
                            equipment.setItem(equipmentSlot, null);
                            return SkillResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return SkillResult.ERROR;
    }
    private EquipmentSlot convertSlotToEquipmentSlot(int slot) {
        switch (slot) {
            case 0:
                return EquipmentSlot.HAND; // Main hand
            case 1:
                return EquipmentSlot.OFF_HAND; // Offhand
            case 2:
                return EquipmentSlot.FEET; // Feet
            case 3:
                return EquipmentSlot.LEGS; // Legs
            case 4:
                return EquipmentSlot.CHEST; // Chest
            case 5:
                return EquipmentSlot.HEAD; // Head
            default:
                return null;
        }
    }
}