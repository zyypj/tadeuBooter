package com.github.zyypj.booter.minecraft.packet.utils;

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

public class HologramBuilder {

    private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    private static final Map<UUID, Hologram> HOLOGRAMS = new HashMap<>();

    /**
     * Cria um novo holograma na localização especificada.
     *
     * @param location Localização do holograma.
     * @param lines    Linhas de texto do holograma.
     * @return O UUID único do holograma.
     */
    public static UUID createHologram(Location location, List<String> lines) {
        UUID hologramId = UUID.randomUUID();
        Hologram hologram = new Hologram(hologramId, location, lines);
        HOLOGRAMS.put(hologramId, hologram);
        return hologramId;
    }

    /**
     * Remove um holograma.
     *
     * @param hologramId O UUID do holograma a ser removido.
     */
    public static void removeHologram(UUID hologramId) {
        Hologram hologram = HOLOGRAMS.remove(hologramId);
        if (hologram != null) {
            hologram.destroy();
        }
    }

    /**
     * Atualiza as linhas de um holograma.
     *
     * @param hologramId O UUID do holograma a ser atualizado.
     * @param newLines   Novas linhas de texto.
     */
    public static void updateHologram(UUID hologramId, List<String> newLines) {
        Hologram hologram = HOLOGRAMS.get(hologramId);
        if (hologram != null) {
            hologram.updateLines(newLines);
        }
    }

    /**
     * Exibe um holograma para um jogador específico.
     *
     * @param hologramId O UUID do holograma a ser exibido.
     * @param player     O jogador que verá o holograma.
     */
    public static void showHologram(UUID hologramId, Player player) {
        Hologram hologram = HOLOGRAMS.get(hologramId);
        if (hologram != null) {
            hologram.show(player);
        }
    }

    /**
     * Oculta um holograma de um jogador específico.
     *
     * @param hologramId O UUID do holograma a ser ocultado.
     * @param player     O jogador do qual o holograma será ocultado.
     */
    public static void hideHologram(UUID hologramId, Player player) {
        Hologram hologram = HOLOGRAMS.get(hologramId);
        if (hologram != null) {
            hologram.hide(player);
        }
    }

    /**
     * Classe interna para representar um holograma.
     */
    private static class Hologram {
        private final UUID id;
        private final Location location;
        private final List<Integer> entityIds = new ArrayList<>();
        private List<String> lines;

        public Hologram(UUID id, Location location, List<String> lines) {
            this.id = id;
            this.location = location;
            this.lines = new ArrayList<>(lines);
            spawnEntities();
        }

        /**
         * Cria as entidades de texto para o holograma.
         */
        private void spawnEntities() {
            Location lineLocation = location.clone();
            double lineSpacing = 0.25;

            for (String line : lines) {
                int entityId = createEntity(lineLocation, line);
                entityIds.add(entityId);
                lineLocation.subtract(0, lineSpacing, 0);
            }
        }

        /**
         * Atualiza as linhas de texto do holograma.
         *
         * @param newLines Novas linhas de texto.
         */
        public void updateLines(List<String> newLines) {
            destroy();
            this.lines = new ArrayList<>(newLines);
            spawnEntities();
        }

        /**
         * Mostra o holograma para um jogador.
         *
         * @param player O jogador que verá o holograma.
         */
        public void show(Player player) {
            Location lineLocation = location.clone();
            double lineSpacing = 0.25;

            for (int i = 0; i < lines.size(); i++) {
                sendSpawnPacket(player, entityIds.get(i), lineLocation, lines.get(i));
                lineLocation.subtract(0, lineSpacing, 0);
            }
        }

        /**
         * Oculta o holograma de um jogador.
         *
         * @param player O jogador do qual o holograma será ocultado.
         */
        public void hide(Player player) {
            for (int entityId : entityIds) {
                sendDestroyPacket(player, entityId);
            }
        }

        /**
         * Destrói todas as entidades do holograma.
         */
        public void destroy() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                hide(player);
            }
            entityIds.clear();
        }

        /**
         * Cria uma entidade de texto (armor stand invisível).
         *
         * @param location A localização da entidade.
         * @param text     O texto exibido pela entidade.
         * @return O ID da entidade criada.
         */
        private int createEntity(Location location, String text) {
            int entityId = new Random().nextInt(Integer.MAX_VALUE);

            PacketContainer spawnPacket = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
            spawnPacket.getIntegers().write(0, entityId);
            spawnPacket.getIntegers().write(1, 1);
            spawnPacket.getDoubles().write(0, location.getX());
            spawnPacket.getDoubles().write(1, location.getY());
            spawnPacket.getDoubles().write(2, location.getZ());

            WrappedDataWatcher watcher = new WrappedDataWatcher();
            watcher.setObject(0, Registry.get(Byte.class), (byte) 0x20);
            watcher.setObject(2, Registry.getChatComponentSerializer(false), text);
            watcher.setObject(3, Registry.get(Boolean.class), true);

            spawnPacket.getDataWatcherModifier().write(0, watcher);

            for (Player player : Bukkit.getOnlinePlayers()) {
                try {
                    protocolManager.sendServerPacket(player, spawnPacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return entityId;
        }

        /**
         * Envia o pacote para criar a entidade para o jogador.
         */
        private void sendSpawnPacket(Player player, int entityId, Location location, String text) {
            PacketContainer spawnPacket = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
            spawnPacket.getIntegers().write(0, entityId);
            spawnPacket.getIntegers().write(1, 1);
            spawnPacket.getDoubles().write(0, location.getX());
            spawnPacket.getDoubles().write(1, location.getY());
            spawnPacket.getDoubles().write(2, location.getZ());

            WrappedDataWatcher watcher = new WrappedDataWatcher();
            watcher.setObject(0, Registry.get(Byte.class), (byte) 0x20);
            watcher.setObject(2, Registry.getChatComponentSerializer(false), text);
            watcher.setObject(3, Registry.get(Boolean.class), true);

            spawnPacket.getDataWatcherModifier().write(0, watcher);

            try {
                protocolManager.sendServerPacket(player, spawnPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Envia o pacote para destruir a entidade para o jogador.
         */
        private void sendDestroyPacket(Player player, int entityId) {
            PacketContainer destroyPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            destroyPacket.getIntLists().write(0, Collections.singletonList(entityId));

            try {
                protocolManager.sendServerPacket(player, destroyPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}