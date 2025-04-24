package me.zyypj.booter.minecraft.spigot.location.geographic

import org.bukkit.World

/**
 * Representa uma localização simples com nome do mundo e coordenadas inteiras.
 * Suporta serialização para string e desserialização.
 */
data class SimpleLocation(
    val worldName: String,
    val x: Int,
    val y: Int,
    val z: Int
) {
    /**
     * Construtor alternativo para instanciar diretamente a partir de um objeto World.
     */
    constructor(world: World, x: Int, y: Int, z: Int) : this(world.name, x, y, z)

    /**
     * Converte esta instância em string no formato: worldName x y z
     */
    fun serialize(): String = listOf(worldName, x, y, z).joinToString(" ")

    companion object {
        private const val SPACE_PLACEHOLDER = "-"

        /**
         * Fábrica estática para chamadas Java a partir de World e coordenadas.
         */
        @JvmStatic
        fun from(world: World, x: Int, y: Int, z: Int): SimpleLocation =
            SimpleLocation(world.name, x, y, z)

        /**
         * Constrói uma instância a partir de string serializada no formato: worldName x y z
         */
        @JvmStatic
        fun deserialize(data: String): SimpleLocation {
            val parts = data.split(" ")
            return SimpleLocation(
                parts[0].replace(SPACE_PLACEHOLDER, " "),
                parts[1].toInt(),
                parts[2].toInt(),
                parts[3].toInt()
            )
        }
    }
}