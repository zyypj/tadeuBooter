package com.github.zyypj.tadeuBooter.api.minecraft.location.world;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Utilitários para manipulação de mundos no Bukkit.
 */
public final class WorldUtils {

    private WorldUtils() {
        throw new UnsupportedOperationException("Esta classe não pode ser instanciada.");
    }

    /**
     * Obtém um mundo pelo nome.
     *
     * @param name Nome do mundo.
     * @return O mundo ou null se não encontrado.
     */
    public static World getWorld(String name) {
        return Bukkit.getWorld(name);
    }

    /**
     * Verifica se um mundo existe.
     *
     * @param name Nome do mundo.
     * @return true se o mundo existir, false caso contrário.
     */
    public static boolean worldExists(String name) {
        return new File(Bukkit.getWorldContainer(), name).exists();
    }

    /**
     * Cria um novo mundo.
     *
     * @param name Nome do mundo.
     * @param environment Tipo do ambiente (NORMAL, NETHER, THE_END).
     * @param generator Gerador de terreno customizado (pode ser null).
     * @return O mundo criado.
     */
    public static World createWorld(String name, World.Environment environment, ChunkGenerator generator) {
        WorldCreator creator = new WorldCreator(name);
        creator.environment(environment);
        if (generator != null) {
            creator.generator(generator);
        }
        return Bukkit.createWorld(creator);
    }

    /**
     * Deleta um mundo.
     *
     * @param world Mundo a ser deletado.
     * @return true se o mundo foi deletado, false caso contrário.
     */
    public static boolean deleteWorld(World world) {
        if (world == null) return false;

        File worldFolder = world.getWorldFolder();
        Bukkit.unloadWorld(world, false);
        return deleteFolder(worldFolder);
    }

    private static boolean deleteFolder(File folder) {
        if (!folder.exists()) return false;
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) deleteFolder(file);
            else file.delete();
        }
        return folder.delete();
    }

    /**
     * Define o tempo do mundo.
     *
     * @param world Mundo alvo.
     * @param time Tempo em ticks (0 = dia, 6000 = meio-dia, 18000 = noite).
     */
    public static void setWorldTime(World world, long time) {
        if (world != null) {
            world.setTime(time);
        }
    }

    /**
     * Define o clima do mundo.
     *
     * @param world Mundo alvo.
     * @param storm Se deve chover.
     * @param duration Duração em ticks da chuva.
     */
    public static void setWeather(World world, boolean storm, int duration) {
        if (world != null) {
            world.setStorm(storm);
            world.setWeatherDuration(duration);
        }
    }

    /**
     * Obtém todos os jogadores em um mundo.
     *
     * @param world Mundo alvo.
     * @return Lista de jogadores no mundo.
     */
    public static List<Player> getPlayersInWorld(World world) {
        return world.getPlayers();
    }

    /**
     * Teletransporta um jogador para um mundo.
     *
     * @param player Jogador a ser teleportado.
     * @param world Mundo de destino.
     * @param safe Se deve buscar um local seguro.
     * @return true se o teleporte foi bem-sucedido, false caso contrário.
     */
    public static boolean teleportPlayerToWorld(Player player, World world, boolean safe) {
        if (player == null || world == null) return false;

        Location spawn = world.getSpawnLocation();
        if (safe) {
            spawn = findSafeLocation(spawn);
        }
        return player.teleport(spawn);
    }

    /**
     * Encontra um local seguro para teleportar.
     *
     * @param location Local inicial.
     * @return Local seguro.
     */
    public static Location findSafeLocation(Location location) {
        World world = location.getWorld();
        int x = location.getBlockX();
        int z = location.getBlockZ();
        int y = world.getHighestBlockYAt(x, z);

        return new Location(world, x + 0.5, y + 1, z + 0.5);
    }

    /**
     * Regenera um chunk no mundo.
     *
     * @param world Mundo alvo.
     * @param x Coordenada X do chunk.
     * @param z Coordenada Z do chunk.
     */
    public static void regenerateChunk(World world, int x, int z) {
        world.regenerateChunk(x, z);
    }

    /**
     * Remove todas as entidades do mundo com base em um filtro.
     *
     * @param world Mundo alvo.
     * @param filter Filtro para selecionar quais entidades remover.
     */
    public static void removeEntities(World world, Predicate<Entity> filter) {
        world.getEntities().stream()
                .filter(filter)
                .forEach(Entity::remove);
    }

    /**
     * Remove todas as entidades de um determinado tipo no mundo.
     *
     * @param world Mundo alvo.
     * @param entityType Tipo da entidade a ser removida.
     */
    public static void removeEntitiesByType(World world, EntityType entityType) {
        removeEntities(world, entity -> entity.getType() == entityType);
    }

    /**
     * Impede que jogadores caiam no vazio em um mundo.
     *
     * @param plugin Plugin que registra o evento.
     */
    public static void preventVoidFall(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (World world : Bukkit.getWorlds()) {
                for (Player player : world.getPlayers()) {
                    if (player.getLocation().getY() < 5) {
                        player.teleport(world.getSpawnLocation());
                        player.sendMessage(ChatColor.RED + "Você foi salvo de cair no vazio!");
                    }
                }
            }
        }, 0L, 20L);
    }
}