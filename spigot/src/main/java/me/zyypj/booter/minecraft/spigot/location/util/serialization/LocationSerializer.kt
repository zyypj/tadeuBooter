package me.zyypj.booter.minecraft.spigot.location.util.serialization

import org.bukkit.Bukkit
import org.bukkit.Location

object LocationSerializer {

    private val SPACE_PLACEHOLDER = "-"

    /**
     * Converte uma Location em String no formato: world x y z yaw pitch
     */
    @JvmStatic
    fun serialize(location: Location): String {
        val worldName = location.world!!.name.replace(" ", SPACE_PLACEHOLDER)
        return listOf(
            worldName,
            location.x.toString(),
            location.y.toString(),
            location.z.toString(),
            location.yaw.toString(),
            location.pitch.toString()
        ).joinToString(" ")
    }

    /**
     * Reconstrói uma Location a partir da String serializada.
     */
    @JvmStatic
    fun deserialize(string: String): Location {
        val parts = string.split(" ")
        val worldName = parts[0].replace(SPACE_PLACEHOLDER, " ")
        val world = Bukkit.getWorld(worldName)
        return Location(
            world,
            parts[1].toDouble(),
            parts[2].toDouble(),
            parts[3].toDouble(),
            parts[4].toFloat(),
            parts[5].toFloat()
        )
    }
}