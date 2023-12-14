package org.aidan.mythicaddon;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimeStopAbility {

    private final JavaPlugin plugin;
    private Map<UUID, Location> frozenProjectiles = new HashMap<>();
    private Map<UUID, Vector> projectileVelocities = new HashMap<>();
    private BukkitTask freezeTask;

    public TimeStopAbility(JavaPlugin plugin) {
        this.plugin = plugin;
        setupPacketInterception();
    }

    public void startFreezingProjectiles() {
        freezeTask = Bukkit.getScheduler().runTaskTimer(plugin, this::freezeProjectiles, 0L, 1L);
    }

    private void freezeProjectiles() {
        Bukkit.getWorlds().forEach(world -> {
            world.getEntitiesByClass(Projectile.class).forEach(projectile -> {
                frozenProjectiles.putIfAbsent(projectile.getUniqueId(), projectile.getLocation());
                UUID projectileId = projectile.getUniqueId();
                if (!frozenProjectiles.containsKey(projectileId)) {
                    frozenProjectiles.put(projectileId, projectile.getLocation());
                    projectileVelocities.put(projectileId, projectile.getVelocity());
                }
                Location storedLocation = frozenProjectiles.get(projectile.getUniqueId());
                if (storedLocation != null) {
                    projectile.teleport(storedLocation);
                    projectile.setGravity(false);
                }
            });
        });
    }

    public void resumeProjectiles() {
        if (freezeTask != null) {
            freezeTask.cancel();
        }
        for (UUID projectileUuid : frozenProjectiles.keySet()) {
            Entity entity = Bukkit.getEntity(projectileUuid);
            if (entity instanceof Projectile) {
                Projectile projectile = (Projectile) entity;
                projectile.setGravity(true);
                Vector originalVelocity = projectileVelocities.get(projectileUuid);
                if (originalVelocity != null) {
                    projectile.setVelocity(originalVelocity);
                }
            }
        }
        frozenProjectiles.clear();
        projectileVelocities.clear();
    }

    public void setupPacketInterception() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.ENTITY_TELEPORT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {
                    int entityId = event.getPacket().getIntegers().read(0);
                    Entity entity = event.getPlayer().getWorld().getEntities().stream()
                            .filter(e -> e.getEntityId() == entityId)
                            .findFirst()
                            .orElse(null);
                    if (entity instanceof Projectile) {
                        UUID entityUuid = entity.getUniqueId();
                        if (frozenProjectiles.containsKey(entityUuid)) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        });
    }
}
