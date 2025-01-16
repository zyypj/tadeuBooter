package com.github.zyypj.tadeuBooter.minecraft.packet.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class VirtualEntity {

    private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    private static final Random random = new Random();

    private final int entityId;
    private final UUID entityUUID;
    private final EntityType entityType;
    private final Map<String, Object> metadata = new HashMap<>();
    private Location location;
    private boolean isVisible = true;

    public VirtualEntity(EntityType entityType, Location spawnLocation) {
        this.entityId = random.nextInt(1_000_000);
        this.entityUUID = UUID.randomUUID();
        this.entityType = entityType;
        this.location = spawnLocation;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
        if (visible) {
            spawnForAll();
        } else {
            destroyForAll();
        }
    }

    public void setLocation(Location location) {
        this.location = location;
        updateLocationForAll();
    }

    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
        updateMetadataForAll();
    }

    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    public void spawn(Player player) {
        try {
            PacketContainer spawnPacket = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
            spawnPacket.getIntegers().write(0, entityId);
            spawnPacket.getUUIDs().write(0, entityUUID);
            spawnPacket.getEntityTypeModifier().write(0, entityType);
            spawnPacket.getDoubles()
                    .write(0, location.getX())
                    .write(1, location.getY())
                    .write(2, location.getZ());

            protocolManager.sendServerPacket(player, spawnPacket);

            updateMetadata(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy(Player player) {
        try {
            PacketContainer destroyPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            destroyPacket.getIntLists().write(0, Collections.singletonList(entityId));

            protocolManager.sendServerPacket(player, destroyPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateLocation(Player player) {
        try {
            PacketContainer teleportPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
            teleportPacket.getIntegers().write(0, entityId);
            teleportPacket.getDoubles()
                    .write(0, location.getX())
                    .write(1, location.getY())
                    .write(2, location.getZ());
            teleportPacket.getBytes()
                    .write(0, (byte) (location.getYaw() * 256 / 360))
                    .write(1, (byte) (location.getPitch() * 256 / 360));

            protocolManager.sendServerPacket(player, teleportPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateMetadata(Player player) {
        try {
            PacketContainer metadataPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
            metadataPacket.getIntegers().write(0, entityId);

            WrappedDataWatcher watcher = new WrappedDataWatcher();
            Serializer chatSerializer = WrappedDataWatcher.Registry.getChatComponentSerializer(false);

            if (metadata.containsKey("customName")) {
                String customName = (String) metadata.get("customName");
                WrappedDataWatcher.WrappedDataWatcherObject customNameWatcher =
                        new WrappedDataWatcher.WrappedDataWatcherObject(2, chatSerializer);
                watcher.setObject(customNameWatcher, WrappedChatComponent.fromText(customName));
            }

            WrappedDataWatcher.WrappedDataWatcherObject visibilityWatcher =
                    new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class));
            watcher.setObject(visibilityWatcher, isVisible ? (byte) 0 : (byte) 0x20);

            metadataPacket.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

            protocolManager.sendServerPacket(player, metadataPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void spawnForAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            spawn(player);
        }
    }

    public void destroyForAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            destroy(player);
        }
    }

    public void updateLocationForAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateLocation(player);
        }
    }

    public void updateMetadataForAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateMetadata(player);
        }
    }

    public void animate(Player player, int animationType) {
        try {
            PacketContainer animationPacket = protocolManager.createPacket(PacketType.Play.Server.ANIMATION);
            animationPacket.getIntegers().write(0, entityId);
            animationPacket.getIntegers().write(1, animationType);

            protocolManager.sendServerPacket(player, animationPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void animateForAll(int animationType) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            animate(player, animationType);
        }
    }
}