package com.github.zyypj.tadeuBooter.minecraft.packet.utils;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class HeadAnimationBuilder {

    private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    private static final Map<UUID, AnimatedHead> HEADS = new HashMap<>();

    /**
     * Cria uma nova animação de cabeça na localização especificada.
     *
     * @param location Localização inicial.
     * @param texture  Textura da cabeça.
     * @return UUID único da animação criada.
     */
    public static UUID createAnimatedHead(Location location, String texture) {
        UUID headId = UUID.randomUUID();
        AnimatedHead animatedHead = new AnimatedHead(headId, location, texture);
        HEADS.put(headId, animatedHead);
        return headId;
    }

    /**
     * Remove uma animação de cabeça pelo UUID.
     *
     * @param headId O UUID da animação a ser removida.
     */
    public static void removeAnimatedHead(UUID headId) {
        AnimatedHead head = HEADS.remove(headId);
        if (head != null) {
            head.destroy();
        }
    }

    /**
     * Move a cabeça para uma nova localização.
     *
     * @param headId    UUID da cabeça.
     * @param newLocation Nova localização para a cabeça.
     */
    public static void moveHead(UUID headId, Location newLocation) {
        AnimatedHead head = HEADS.get(headId);
        if (head != null) {
            head.updateLocation(newLocation);
        }
    }

    /**
     * Mostra a cabeça para um jogador específico.
     *
     * @param headId UUID da cabeça.
     * @param player Jogador que verá a cabeça.
     */
    public static void showHead(UUID headId, Player player) {
        AnimatedHead head = HEADS.get(headId);
        if (head != null) {
            head.show(player);
        }
    }

    /**
     * Oculta a cabeça de um jogador específico.
     *
     * @param headId UUID da cabeça.
     * @param player Jogador que não verá mais a cabeça.
     */
    public static void hideHead(UUID headId, Player player) {
        AnimatedHead head = HEADS.get(headId);
        if (head != null) {
            head.hide(player);
        }
    }

    /**
     * Classe interna que representa uma cabeça animada.
     */
    private static class AnimatedHead {
        private final UUID id;
        private Location location;
        private final int entityId;
        private final String texture;

        public AnimatedHead(UUID id, Location location, String texture) {
            this.id = id;
            this.location = location;
            this.texture = texture;
            this.entityId = new Random().nextInt(Integer.MAX_VALUE);
            spawnEntity();
        }

        /**
         * Cria a entidade invisível (ArmorStand) com a cabeça visível.
         */
        private void spawnEntity() {
            PacketContainer spawnPacket = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
            spawnPacket.getIntegers().write(0, entityId);
            spawnPacket.getIntegers().write(1, 30);
            spawnPacket.getDoubles().write(0, location.getX());
            spawnPacket.getDoubles().write(1, location.getY());
            spawnPacket.getDoubles().write(2, location.getZ());

            WrappedDataWatcher watcher = new WrappedDataWatcher();
            watcher.setObject(0, Registry.get(Byte.class), (byte) 0x20);
            watcher.setObject(2, Registry.getChatComponentSerializer(false), "");
            watcher.setObject(3, Registry.get(Boolean.class), true);
            watcher.setObject(5, Registry.get(Boolean.class), true);
            watcher.setObject(15, Registry.get(String.class), texture);

            spawnPacket.getDataWatcherModifier().write(0, watcher);

            for (Player player : Bukkit.getOnlinePlayers()) {
                try {
                    protocolManager.sendServerPacket(player, spawnPacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Atualiza a localização da cabeça.
         *
         * @param newLocation Nova localização.
         */
        public void updateLocation(Location newLocation) {
            this.location = newLocation;
            PacketContainer teleportPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
            teleportPacket.getIntegers().write(0, entityId);
            teleportPacket.getDoubles().write(0, newLocation.getX());
            teleportPacket.getDoubles().write(1, newLocation.getY());
            teleportPacket.getDoubles().write(2, newLocation.getZ());

            for (Player player : Bukkit.getOnlinePlayers()) {
                try {
                    protocolManager.sendServerPacket(player, teleportPacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Mostra a cabeça para um jogador específico.
         *
         * @param player O jogador.
         */
        public void show(Player player) {
            spawnEntity();
        }

        /**
         * Oculta a cabeça de um jogador específico.
         *
         * @param player O jogador.
         */
        public void hide(Player player) {
            PacketContainer destroyPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            destroyPacket.getIntLists().write(0, Collections.singletonList(entityId));

            try {
                protocolManager.sendServerPacket(player, destroyPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Remove a entidade de todos os jogadores.
         */
        public void destroy() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                hide(player);
            }
        }
    }
}