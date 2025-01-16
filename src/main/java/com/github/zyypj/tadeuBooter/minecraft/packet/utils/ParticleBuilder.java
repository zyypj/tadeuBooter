package com.github.zyypj.tadeuBooter.minecraft.packet.utils;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ParticleBuilder {

    private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    private static final Map<String, String> PARTICLE_MAP = new HashMap<>();

    static {
        PARTICLE_MAP.put("flame", "flame");
        PARTICLE_MAP.put("cloud", "cloud");
        PARTICLE_MAP.put("heart", "heart");
        PARTICLE_MAP.put("crit", "crit");
        PARTICLE_MAP.put("smoke", "smoke");
        PARTICLE_MAP.put("spell", "spell");
    }

    /**
     * Exibe uma partícula para um jogador em uma localização específica.
     *
     * @param player    O jogador que verá a partícula.
     * @param particle  O tipo da partícula (ex.: "flame", "cloud").
     * @param location  A localização onde a partícula será exibida.
     * @param count     A quantidade de partículas exibidas.
     * @param offsetX   O deslocamento no eixo X.
     * @param offsetY   O deslocamento no eixo Y.
     * @param offsetZ   O deslocamento no eixo Z.
     * @param speed     A velocidade das partículas.
     */
    public static void displayParticle(Player player, String particle, Location location, int count, float offsetX, float offsetY, float offsetZ, float speed) {
        try {
            String mappedParticle = PARTICLE_MAP.get(particle.toLowerCase());
            if (mappedParticle == null) {
                throw new IllegalArgumentException("Partícula desconhecida: " + particle);
            }

            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.WORLD_PARTICLES);
            packet.getStrings().write(0, mappedParticle);
            packet.getFloat().write(0, (float) location.getX());
            packet.getFloat().write(1, (float) location.getY());
            packet.getFloat().write(2, (float) location.getZ());
            packet.getFloat().write(3, offsetX);
            packet.getFloat().write(4, offsetY);
            packet.getFloat().write(5, offsetZ);
            packet.getFloat().write(6, speed);
            packet.getIntegers().write(0, count);

            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Exibe partículas para todos os jogadores em uma localização específica.
     *
     * @param particle O tipo da partícula.
     * @param location A localização onde as partículas serão exibidas.
     * @param count    A quantidade de partículas exibidas.
     * @param offsetX  O deslocamento no eixo X.
     * @param offsetY  O deslocamento no eixo Y.
     * @param offsetZ  O deslocamento no eixo Z.
     * @param speed    A velocidade das partículas.
     */
    public static void displayParticle(String particle, Location location, int count, float offsetX, float offsetY, float offsetZ, float speed) {
        Bukkit.getOnlinePlayers().forEach(player -> displayParticle(player, particle, location, count, offsetX, offsetY, offsetZ, speed));
    }

    /**
     * Inicia uma animação personalizada de partículas.
     *
     * @param location   A localização base da animação.
     * @param duration   A duração da animação em segundos.
     * @param frameLogic A lógica para exibir partículas em cada frame.
     */
    public static void startAnimation(Location location, double duration, Consumer<Location> frameLogic) {
        new BukkitRunnable() {
            double time = 0;
            final double interval = 0.1;

            @Override
            public void run() {
                if (time >= duration) {
                    cancel();
                    return;
                }
                frameLogic.accept(location);
                time += interval;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugins()[0], 0L, 2L);
    }

    /**
     * Animações de partículas pré-definidas.
     */
    public static class ExampleAnimations {

        /**
         * Exibe partículas em forma de círculo giratório.
         *
         * @param location A localização central do círculo.
         * @param radius   O raio do círculo.
         * @param duration A duração da animação em segundos.
         * @param particle O tipo da partícula.
         */
        public static void spinningCircle(Location location, double radius, double duration, String particle) {
            startAnimation(location, duration, loc -> {
                double angleStep = Math.PI / 16;
                for (double angle = 0; angle < Math.PI * 2; angle += angleStep) {
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    loc.add(x, 0, z);
                    displayParticle(particle, loc, 1, 0, 0, 0, 0);
                    loc.subtract(x, 0, z);
                }
            });
        }

        /**
         * Exibe partículas em forma de hélice (espiral).
         *
         * @param location A base da hélice.
         * @param radius   O raio da hélice.
         * @param height   A altura da hélice.
         * @param duration A duração da animação em segundos.
         * @param particle O tipo da partícula.
         */
        public static void helix(Location location, double radius, double height, double duration, String particle) {
            startAnimation(location, duration, loc -> {
                double angleStep = Math.PI / 8;
                double yStep = height / (duration * 20);
                double currentHeight = 0;

                for (double angle = 0; angle < Math.PI * 2; angle += angleStep) {
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    loc.add(x, currentHeight, z);
                    displayParticle(particle, loc, 1, 0, 0, 0, 0);
                    loc.subtract(x, currentHeight, z);
                    currentHeight += yStep;
                    if (currentHeight > height) break;
                }
            });
        }

        /**
         * Exibe partículas em forma de esfera.
         *
         * @param location A localização central da esfera.
         * @param radius   O raio da esfera.
         * @param duration A duração da animação em segundos.
         * @param particle O tipo da partícula.
         */
        public static void sphere(Location location, double radius, double duration, String particle) {
            startAnimation(location, duration, loc -> {
                double step = Math.PI / 8;
                for (double theta = 0; theta < Math.PI; theta += step) {
                    for (double phi = 0; phi < Math.PI * 2; phi += step) {
                        double x = radius * Math.sin(theta) * Math.cos(phi);
                        double y = radius * Math.sin(theta) * Math.sin(phi);
                        double z = radius * Math.cos(theta);
                        loc.add(x, y, z);
                        displayParticle(particle, loc, 1, 0, 0, 0, 0);
                        loc.subtract(x, y, z);
                    }
                }
            });
        }
    }
}