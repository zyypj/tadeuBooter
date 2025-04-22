package com.github.zyypj.tadeuBooter.api.minecraft.location.world

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.generator.ChunkGenerator
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.function.Predicate
import kotlin.jvm.JvmStatic

/**
 * Utilitários para manipulação de mundos no Bukkit em Kotlin.
 * Métodos marcados com @JvmStatic são acessíveis diretamente de Java.
 */
object WorldUtils {

    /**
     * Obtém um mundo pelo nome.
     */
    @JvmStatic
    fun getWorld(name: String): World? = Bukkit.getWorld(name)

    /**
     * Verifica se um mundo existe.
     */
    @JvmStatic
    fun worldExists(name: String): Boolean = File(Bukkit.getWorldContainer(), name).exists()

    /**
     * Cria um novo mundo.
     */
    @JvmStatic
    fun createWorld(name: String,
                    environment: World.Environment,
                    generator: ChunkGenerator?): World? {
        val creator = WorldCreator(name)
            .environment(environment)
        if (generator != null) creator.generator(generator)
        return Bukkit.createWorld(creator)
    }

    /**
     * Deleta um mundo.
     */
    @JvmStatic
    fun deleteWorld(world: World?): Boolean {
        if (world == null) return false
        val folder = world.worldFolder
        Bukkit.unloadWorld(world, false)
        return deleteFolder(folder)
    }

    private fun deleteFolder(folder: File): Boolean {
        if (!folder.exists()) return false
        folder.listFiles()?.forEach { file ->
            if (file.isDirectory) deleteFolder(file)
            else file.delete()
        }
        return folder.delete()
    }

    /**
     * Define o tempo do mundo.
     */
    @JvmStatic
    fun setWorldTime(world: World?, time: Long) {
        world?.time = time
    }

    /**
     * Define o clima do mundo.
     */
    @JvmStatic
    fun setWeather(world: World?, storm: Boolean, duration: Int) {
        world?.let {
            it.isThundering = storm
            it.weatherDuration = duration
        }
    }

    /**
     * Obtém todos os jogadores em um mundo.
     */
    @JvmStatic
    fun getPlayersInWorld(world: World): List<Player> = world.players

    /**
     * Teletransporta um jogador para um mundo.
     */
    @JvmStatic
    fun teleportPlayerToWorld(player: Player?, world: World?, safe: Boolean): Boolean {
        if (player == null || world == null) return false
        var spawn = world.spawnLocation
        if (safe) spawn = findSafeLocation(spawn)
        return player.teleport(spawn)
    }

    /**
     * Encontra um local seguro para teleportar.
     */
    @JvmStatic
    fun findSafeLocation(location: Location): Location {
        val world = location.world ?: return location
        val x = location.blockX
        val z = location.blockZ
        val y = world.getHighestBlockYAt(x, z)
        return Location(world, x + 0.5, y + 1.0, z + 0.5)
    }

    /**
     * Regenera um chunk no mundo.
     */
    @JvmStatic
    fun regenerateChunk(world: World, x: Int, z: Int) {
        world.regenerateChunk(x, z)
    }

    /**
     * Remove todas as entidades do mundo com base em um filtro.
     */
    @JvmStatic
    fun removeEntities(world: World, filter: Predicate<Entity>) {
        world.entities.stream()
            .filter(filter)
            .forEach(Entity::remove)
    }

    /**
     * Remove todas as entidades de um determinado tipo no mundo.
     */
    @JvmStatic
    fun removeEntitiesByType(world: World, type: EntityType) {
        removeEntities(world, Predicate { it.type == type })
    }

    /**
     * Impede que jogadores caiam no vazio em um mundo.
     */
    @JvmStatic
    fun preventVoidFall(plugin: Plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            Bukkit.getWorlds().forEach { world ->
                world.players.forEach { player ->
                    if (player.location.y < 5) {
                        player.teleport(world.spawnLocation)
                        player.sendMessage("§cVocê foi salvo de cair no vazio!")
                    }
                }
            }
        }, 0L, 20L)
    }
}