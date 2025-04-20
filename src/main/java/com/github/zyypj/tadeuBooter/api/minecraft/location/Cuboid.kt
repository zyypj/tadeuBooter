package com.github.zyypj.tadeuBooter.api.minecraft.location

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Player
import java.util.*

/**
 * Representa um cuboide definido por dois pontos no mundo do Bukkit.
 * Oferece métodos para iteração de blocos, cálculos de distância e verificações de presença.
 */
class Cuboid constructor(
    point1: Location,
    point2: Location
) {
    private val xMin: Int
    private val xMax: Int
    private val yMin: Int
    private val yMax: Int
    private val zMin: Int
    private val zMax: Int
    private val world: World
    private val xMinCentered: Double
    private val xMaxCentered: Double
    private val yMinCentered: Double
    private val yMaxCentered: Double
    private val zMinCentered: Double
    private val zMaxCentered: Double

    init {
        xMin = minOf(point1.blockX, point2.blockX)
        xMax = maxOf(point1.blockX, point2.blockX)
        yMin = minOf(point1.blockY, point2.blockY)
        yMax = maxOf(point1.blockY, point2.blockY)
        zMin = minOf(point1.blockZ, point2.blockZ)
        zMax = maxOf(point1.blockZ, point2.blockZ)
        world = point1.world!!
        xMinCentered = xMin + 0.5
        xMaxCentered = xMax + 0.5
        yMinCentered = yMin + 0.5
        yMaxCentered = yMax + 0.5
        zMinCentered = zMin + 0.5
        zMaxCentered = zMax + 0.5
    }

    /** Retorna um iterador com todos os blocos contidos no cuboide. */
    fun blockList(): Iterator<Block> {
        val list = ArrayList<Block>(getTotalBlockSize())
        for (x in xMin..xMax) {
            for (y in yMin..yMax) {
                for (z in zMin..zMax) {
                    list.add(world.getBlockAt(x, y, z))
                }
            }
        }
        return list.iterator()
    }

    /** Ponto central exato do cuboide. */
    fun getCenter(): Location =
        Location(world, (xMax - xMin) / 2.0 + xMin, (yMax - yMin) / 2.0 + yMin, (zMax - zMin) / 2.0 + zMin)

    /** Distância entre os pontos extremes (P1 e P2). */
    fun getDistance(): Double = getPoint1().distance(getPoint2())

    /** Distância ao quadrado entre P1 e P2. */
    fun getDistanceSquared(): Double = getPoint1().distanceSquared(getPoint2())

    /** Altura total (Y). */
    fun getHeight(): Int = yMax - yMin + 1

    /** Ponto mínimo (xMin, yMin, zMin). */
    fun getPoint1(): Location = Location(world, xMin.toDouble(), yMin.toDouble(), zMin.toDouble())

    /** Ponto máximo (xMax, yMax, zMax). */
    fun getPoint2(): Location = Location(world, xMax.toDouble(), yMax.toDouble(), zMax.toDouble())

    /** Localização aleatória dentro dos limites do cuboide. */
    fun getRandomLocation(): Location {
        val rand = Random()
        val x = rand.nextInt((xMax - xMin + 1).coerceAtLeast(1)) + xMin
        val y = rand.nextInt((yMax - yMin + 1).coerceAtLeast(1)) + yMin
        val z = rand.nextInt((zMax - zMin + 1).coerceAtLeast(1)) + zMin
        return Location(world, x.toDouble(), y.toDouble(), z.toDouble())
    }

    /** Número total de blocos dentro do cuboide. */
    fun getTotalBlockSize(): Int = getHeight() * getXWidth() * getZWidth()

    /** Largura em X. */
    fun getXWidth(): Int = xMax - xMin + 1

    /** Largura em Z. */
    fun getZWidth(): Int = zMax - zMin + 1

    /** Verifica se uma localização está dentro do cuboide (coordenadas de bloco). */
    fun hasLocation(loc: Location): Boolean =
        loc.world == world &&
                loc.blockX in xMin..xMax &&
                loc.blockY in yMin..yMax &&
                loc.blockZ in zMin..zMax

    /** Verifica se o jogador está dentro do cuboide. */
    fun itsInside(player: Player): Boolean = hasLocation(player.location)

    /**
     * Verifica se uma localização está dentro dos limites expandidos por uma margem.
     * @param loc Localização testada.
     * @param marge Margem em blocos.
     */
    fun isInWithMarge(loc: Location, marge: Double): Boolean =
        loc.world == world &&
                loc.x in (xMinCentered - marge)..(xMaxCentered + marge) &&
                loc.y in (yMinCentered - marge)..(yMaxCentered + marge) &&
                loc.z in (zMinCentered - marge)..(zMaxCentered + marge)
}