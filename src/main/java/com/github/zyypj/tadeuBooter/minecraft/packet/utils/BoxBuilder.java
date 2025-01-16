package com.github.zyypj.tadeuBooter.minecraft.packet.utils;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class BoxBuilder {

    private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    private static final Map<UUID, PlayerBox> BOXES = new HashMap<>();

    /**
     * Cria uma nova caixa visível apenas para o jogador que a colocou.
     *
     * @param player   O jogador que colocará a caixa.
     * @param location A localização onde a caixa será colocada.
     * @param material O material da caixa (exemplo: CHEST ou ENDER_CHEST).
     * @return UUID único da caixa criada.
     */
    public static UUID createBox(Player player, Location location, Material material) {
        UUID boxId = UUID.randomUUID();
        PlayerBox box = new PlayerBox(boxId, player, location, material);
        BOXES.put(boxId, box);
        box.show();
        return boxId;
    }

    /**
     * Remove uma caixa pelo UUID.
     *
     * @param boxId UUID da caixa a ser removida.
     */
    public static void removeBox(UUID boxId) {
        PlayerBox box = BOXES.remove(boxId);
        if (box != null) {
            box.destroy();
        }
    }

    /**
     * Move uma caixa para uma nova localização.
     *
     * @param boxId      UUID da caixa.
     * @param newLocation Nova localização da caixa.
     */
    public static void moveBox(UUID boxId, Location newLocation) {
        PlayerBox box = BOXES.get(boxId);
        if (box != null) {
            box.updateLocation(newLocation);
        }
    }

    /**
     * Classe interna que representa uma caixa visível apenas para um jogador.
     */
    private static class PlayerBox {
        private final UUID id;
        private final Player owner;
        private Location location;
        private final Material material;
        private final int entityId;

        public PlayerBox(UUID id, Player owner, Location location, Material material) {
            this.id = id;
            this.owner = owner;
            this.location = location;
            this.material = material;
            this.entityId = new Random().nextInt(Integer.MAX_VALUE);
        }

        /**
         * Mostra a caixa para o jogador que a criou.
         */
        public void show() {
            PacketContainer blockChangePacket = protocolManager.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
            blockChangePacket.getBlockPositionModifier().write(0, new com.comphenix.protocol.wrappers.BlockPosition(
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ()
            ));
            blockChangePacket.getBlockData().write(0, WrappedBlockData.createData(material));

            try {
                protocolManager.sendServerPacket(owner, blockChangePacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Atualiza a localização da caixa.
         *
         * @param newLocation Nova localização da caixa.
         */
        public void updateLocation(Location newLocation) {
            destroy();
            this.location = newLocation;
            show();
        }

        /**
         * Remove a caixa para o jogador que a criou.
         */
        public void destroy() {
            PacketContainer blockChangePacket = protocolManager.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
            blockChangePacket.getBlockPositionModifier().write(0, new com.comphenix.protocol.wrappers.BlockPosition(
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ()
            ));
            blockChangePacket.getBlockData().write(0, WrappedBlockData.createData(Material.AIR));

            try {
                protocolManager.sendServerPacket(owner, blockChangePacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Envia a animação de "abrir" para a caixa.
         */
        public void openAnimation() {
            PacketContainer openPacket = protocolManager.createPacket(PacketType.Play.Server.BLOCK_ACTION);
            openPacket.getBlockPositionModifier().write(0, new com.comphenix.protocol.wrappers.BlockPosition(
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ()
            ));
            openPacket.getIntegers().write(0, 1);
            openPacket.getIntegers().write(1, 1);

            try {
                protocolManager.sendServerPacket(owner, openPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}