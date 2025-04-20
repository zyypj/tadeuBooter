package com.github.zyypj.tadeuBooter.api.minecraft.location.util

import com.github.zyypj.tadeuBooter.api.minecraft.location.geographic.SimpleLocation
import org.bukkit.Location
import org.bukkit.World
import kotlin.jvm.JvmStatic
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Utilitários para criar áreas e formas geométricas a partir de uma Location ou SimpleLocation.
 * Métodos anotados com @JvmStatic são acessíveis diretamente de Java.
 */
object AreaUtils {

    /**
     * Esfera tridimensional (center e raio em blocos).
     */
    @JvmStatic
    fun getSphere(center: SimpleLocation, radius: Int): Collection<SimpleLocation> {
        val result = mutableListOf<SimpleLocation>()
        val cx = center.x
        val cy = center.y
        val cz = center.z
        val worldName = center.worldName
        val r2 = radius * radius
        for (x in cx - radius..cx + radius) {
            for (y in cy - radius..cy + radius) {
                for (z in cz - radius..cz + radius) {
                    val dx = cx - x
                    val dy = cy - y
                    val dz = cz - z
                    if (dx*dx + dy*dy + dz*dz <= r2) {
                        result.add(SimpleLocation(worldName, x, y, z))
                    }
                }
            }
        }
        return result
    }

    /**
     * Círculo horizontal no mesmo y.
     */
    @JvmStatic
    fun getCircle(loc: Location, radius: Int): Collection<SimpleLocation> {
        val result = mutableListOf<SimpleLocation>()
        val worldName = loc.world!!.name
        val cx = loc.blockX
        val y = loc.blockY
        val cz = loc.blockZ
        val r2 = radius * radius
        for (x in cx - radius..cx + radius) {
            for (z in cz - radius..cz + radius) {
                val dx = cx - x
                val dz = cz - z
                if (dx*dx + dz*dz <= r2) {
                    result.add(SimpleLocation(worldName, x, y, z))
                }
            }
        }
        return result
    }

    /**
     * Sinal de mais (+) horizontal.
     */
    @JvmStatic
    fun getPlusSign(loc: Location, radius: Int): Collection<SimpleLocation> {
        val result = mutableListOf<SimpleLocation>()
        val worldName = loc.world!!.name
        val cx = loc.blockX
        val y = loc.blockY
        val cz = loc.blockZ
        for (i in -radius..radius) {
            result.add(SimpleLocation(worldName, cx + i, y, cz))
            result.add(SimpleLocation(worldName, cx, y, cz + i))
        }
        return result
    }

    /**
     * Quadrado no plano horizontal.
     */
    @JvmStatic
    fun getSquare(loc: Location, radius: Int): Collection<SimpleLocation> {
        val result = mutableListOf<SimpleLocation>()
        val worldName = loc.world!!.name
        val cx = loc.blockX
        val y = loc.blockY
        val cz = loc.blockZ
        for (x in cx - radius..cx + radius) {
            for (z in cz - radius..cz + radius) {
                result.add(SimpleLocation(worldName, x, y, z))
            }
        }
        return result
    }

    /**
     * Duas diagonais (star).
     */
    @JvmStatic
    fun getStar(loc: Location, radius: Int): Collection<SimpleLocation> {
        val result = mutableListOf<SimpleLocation>()
        val worldName = loc.world!!.name
        val cx = loc.blockX
        val y = loc.blockY
        val cz = loc.blockZ
        for (i in -radius..radius) {
            result.add(SimpleLocation(worldName, cx + i, y, cz + i))
            result.add(SimpleLocation(worldName, cx + i, y, cz - i))
        }
        return result
    }

    /**
     * Cubo tridimensional.
     */
    @JvmStatic
    fun getCube(loc: Location, radius: Int): Collection<SimpleLocation> {
        val result = mutableListOf<SimpleLocation>()
        val worldName = loc.world!!.name
        val cx = loc.blockX
        val cy = loc.blockY
        val cz = loc.blockZ
        for (x in cx - radius..cx + radius) {
            for (y in cy - radius..cy + radius) {
                for (z in cz - radius..cz + radius) {
                    result.add(SimpleLocation(worldName, x, y, z))
                }
            }
        }
        return result
    }

    /**
     * Triângulo (distância Manhattan).
     */
    @JvmStatic
    fun getTriangle(loc: Location, base: Int): Collection<SimpleLocation> {
        val result = mutableListOf<SimpleLocation>()
        val worldName = loc.world!!.name
        val cx = loc.blockX
        val y = loc.blockY
        val cz = loc.blockZ
        for (x in cx - base..cx + base) {
            for (z in cz - base..cz + base) {
                if (abs(cx - x) + abs(cz - z) <= base) {
                    result.add(SimpleLocation(worldName, x, y, z))
                }
            }
        }
        return result
    }

    /**
     * Pirâmide.
     */
    @JvmStatic
    fun getPyramid(loc: Location, baseLen: Int): Collection<SimpleLocation> {
        val result = mutableListOf<SimpleLocation>()
        val worldName = loc.world!!.name
        val cx = loc.blockX
        val cy = loc.blockY
        val cz = loc.blockZ
        // base
        for (x in cx - baseLen..cx + baseLen) {
            for (z in cz - baseLen..cz + baseLen) {
                if (abs(cx - x) + abs(cz - z) <= baseLen) {
                    result.add(SimpleLocation(worldName, x, cy, z))
                }
            }
        }
        // camadas superiores
        for (dy in 1..baseLen) {
            val layer = baseLen - dy
            for (x in cx - layer..cx + layer) {
                for (z in cz - layer..cz + layer) {
                    result.add(SimpleLocation(worldName, x, cy + dy, z))
                }
            }
        }
        return result
    }

    /**
     * Losango 3D (distância Manhattan total).
     */
    @JvmStatic
    fun getDiamond(loc: Location, radius: Int): Collection<SimpleLocation> {
        val result = mutableListOf<SimpleLocation>()
        val worldName = loc.world!!.name
        val cx = loc.blockX
        val cy = loc.blockY
        val cz = loc.blockZ
        for (x in cx - radius..cx + radius) {
            for (y in cy - radius..cy + radius) {
                for (z in cz - radius..cz + radius) {
                    if (abs(cx - x) + abs(cy - y) + abs(cz - z) <= radius) {
                        result.add(SimpleLocation(worldName, x, y, z))
                    }
                }
            }
        }
        return result
    }

    /**
     * Linha vertical.
     */
    @JvmStatic
    fun getVerticalLine(loc: Location, radius: Int): Collection<SimpleLocation> {
        val result = mutableListOf<SimpleLocation>()
        val worldName = loc.world!!.name
        val cx = loc.blockX
        val cz = loc.blockZ
        val cy = loc.blockY
        for (y in cy - radius..cy + radius) {
            result.add(SimpleLocation(worldName, cx, y, cz))
        }
        return result
    }

    /**
     * Linha horizontal (eixo Z).
     */
    @JvmStatic
    fun getHorizontalLine(loc: Location, radius: Int): Collection<SimpleLocation> {
        val result = mutableListOf<SimpleLocation>()
        val worldName = loc.world!!.name
        val x = loc.blockX
        val y = loc.blockY
        val cz = loc.blockZ
        for (z in cz - radius..cz + radius) {
            result.add(SimpleLocation(worldName, x, y, z))
        }
        return result
    }

    /**
     * Linha entre dois pontos (Bresenham).
     */
    @JvmStatic
    fun getLine(source: Location, destination: Location): Collection<SimpleLocation> {
        val result = mutableListOf<SimpleLocation>()
        val worldName = source.world!!.name
        var x1 = source.blockX
        var y1 = source.blockY
        var z1 = source.blockZ
        val x2 = destination.blockX
        val y2 = destination.blockY
        val z2 = destination.blockZ
        val dx = abs(x2 - x1)
        val dy = abs(y2 - y1)
        val dz = abs(z2 - z1)
        val sx = if (x1 < x2) 1 else -1
        val sy = if (y1 < y2) 1 else -1
        val sz = if (z1 < z2) 1 else -1
        var err1 = dx - dy
        var err2 = dx - dz
        while (true) {
            result.add(SimpleLocation(worldName, x1, y1, z1))
            if (x1 == x2 && y1 == y2 && z1 == z2) break
            val e2 = 2 * err1
            val e3 = 2 * err2
            if (e2 > -dy) { x1 += sx; err1 -= dy }
            if (e2 < dx) { y1 += sy; err1 += dx }
            if (e3 < dx) { z1 += sz; err2 += dx }
        }
        return result
    }

    /**
     * Polígono fechado conectando pontos.
     */
    @JvmStatic
    fun getAbstractConnectedPolygon(vararg points: Location): Collection<SimpleLocation> {
        val result = mutableListOf<SimpleLocation>()
        if (points.isEmpty()) return result
        for (i in points.indices) {
            val current = points[i]
            val next = points[(i + 1) % points.size]
            result.addAll(getLine(current, next))
        }
        return result
    }

    /**
     * Conexões curvas entre pontos (mesma de linha).
     */
    @JvmStatic
    fun getBentConnections(vararg points: Location): Collection<SimpleLocation> =
        getAbstractConnectedPolygon(*points)

    /**
     * Linha no eixo X.
     */
    @JvmStatic
    fun getVerticalX(loc: Location, radius: Int): Collection<SimpleLocation> {
        val result = mutableListOf<SimpleLocation>()
        val worldName = loc.world!!.name
        val cx = loc.blockX
        val y = loc.blockY
        val cz = loc.blockZ
        for (i in -radius..radius) {
            result.add(SimpleLocation(worldName, cx + i, y, cz))
        }
        return result
    }

    /**
     * X (diagonais horizontais).
     */
    @JvmStatic
    fun getX(loc: Location, radius: Int): Collection<SimpleLocation> {
        val result = mutableListOf<SimpleLocation>()
        val worldName = loc.world!!.name
        val cx = loc.blockX
        val y = loc.blockY
        val cz = loc.blockZ
        for (i in -radius..radius) {
            result.add(SimpleLocation(worldName, cx + i, y, cz + i))
            result.add(SimpleLocation(worldName, cx - i, y, cz + i))
        }
        return result
    }

    /**
     * Cruz (+).
     */
    @JvmStatic
    fun getCross(loc: Location, radius: Int): Collection<SimpleLocation> = getPlusSign(loc, radius)

    /**
     * Coração vertical (aproximação com sen e cos).
     */
    @JvmStatic
    fun getVerticalHeart(loc: Location, radius: Int): Collection<SimpleLocation> {
        val result = mutableListOf<SimpleLocation>()
        val worldName = loc.world!!.name
        val cx = loc.blockX
        val cy = loc.blockY
        val cz = loc.blockZ
        for (y in cy - radius..cy + radius) {
            val angle = kotlin.math.acos((cy - y).toDouble() / radius)
            val offset = (radius * kotlin.math.sin(angle)).roundToInt()
            result.add(SimpleLocation(worldName, cx + offset, y, cz))
            result.add(SimpleLocation(worldName, cx - offset, y, cz))
        }
        return result
    }
}