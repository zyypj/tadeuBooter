package me.zyypj.booter.minecraft.spigot.dependency

/**
 * Informações essenciais de uma dependência.
 */
data class DependencyInfo(
    val name: String,
    val version: String,
    val downloadURL: String
)
