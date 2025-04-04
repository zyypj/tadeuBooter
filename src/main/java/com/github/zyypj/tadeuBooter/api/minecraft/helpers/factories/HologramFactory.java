package com.github.zyypj.tadeuBooter.api.minecraft.helpers.factories;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.*;

public class HologramFactory {

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
     * Classe interna para representar um holograma.
     */
    private static class Hologram {
        private final UUID id;
        private final Location location;
        private final List<ArmorStand> armorStands = new ArrayList<>();
        private List<String> lines;

        public Hologram(UUID id, Location location, List<String> lines) {
            this.id = id;
            this.location = location;
            this.lines = new ArrayList<>(lines);
            spawnEntities();
        }

        /**
         * Cria os Armor Stands para o holograma.
         */
        private void spawnEntities() {
            Location lineLocation = location.clone();
            double lineSpacing = 0.25;

            for (String line : lines) {
                ArmorStand armorStand = createArmorStand(lineLocation, ChatColor.translateAlternateColorCodes('&', line));
                armorStands.add(armorStand);
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
         * Destrói todas as entidades do holograma.
         */
        public void destroy() {
            for (ArmorStand armorStand : armorStands) {
                armorStand.remove();
            }
            armorStands.clear();
        }

        /**
         * Cria um Armor Stand invisível para representar uma linha do holograma.
         *
         * @param location A localização do Armor Stand.
         * @param text     O texto exibido.
         * @return O Armor Stand criado.
         */
        private ArmorStand createArmorStand(Location location, String text) {
            World world = location.getWorld();
            if (world == null) return null;

            ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
            armorStand.setCustomName(text);
            armorStand.setCustomNameVisible(true);
            armorStand.setGravity(false);
            armorStand.setVisible(false);
            armorStand.setMarker(true);

            return armorStand;
        }
    }
}