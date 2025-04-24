package me.zyypj.booter.minecraft.spigot.dependency

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Representa uma dependência a baixar e carregar dinamicamente.
 */
class Dependency(val dependencyInfo: DependencyInfo) {

    companion object {
        private var plugin: JavaPlugin? = null
        private var prefix: String = "§8§l[DependencyLoader] §f"

        /** Define o plugin que gerencia os downloads (deve ser chamado antes). */
        @JvmStatic
        fun setPlugin(p: JavaPlugin) {
            plugin = p
        }

        /** Prefixo para logs. */
        @JvmStatic
        fun setPrefix(p: String) {
            prefix = p
        }
    }

    var bytesSize: Long = 0L
        private set

    /** Inicia download assíncrono. */
    fun download(): CompletableFuture<Void> = CompletableFuture.runAsync {
        val pl = plugin ?: throw IllegalStateException(
            "O plugin não foi definido. Use Dependency.setPlugin() antes."
        )
        if (Bukkit.getPluginManager().getPlugin(dependencyInfo.name) != null) return@runAsync

        val outFile = File(
            pl.dataFolder.parentFile,
            "${dependencyInfo.name.lowercase(Locale.getDefault())}-${dependencyInfo.version}.jar"
        )
        if (outFile.exists()) {
            log(pl, "§eDependência já instalada: ${dependencyInfo.name} v${dependencyInfo.version}", true)
            return@runAsync
        }

        val stopwatch = System.nanoTime()
        try {
            val conn = URL(dependencyInfo.downloadURL).openConnection() as HttpURLConnection
            conn.setRequestProperty("User-Agent", "Mozilla/5.0")
            conn.requestMethod = "GET"
            conn.connectTimeout = 10000
            conn.readTimeout = 30000
            conn.connect()
            conn.inputStream.use { ins ->
                Files.copy(ins, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            bytesSize = outFile.length()
            val elapsed = (System.nanoTime() - stopwatch) / 1_000_000
            log(
                pl, "§aBaixado: ${dependencyInfo.name} v${dependencyInfo.version} - Tempo: ${elapsed}ms - Tamanho: ${
                    asReadableSize(
                        bytesSize
                    )
                }", true
            )
            pl.server.scheduler.runTask(pl) { loadPlugin(outFile) }
        } catch (e: IOException) {
            log(
                pl, "§cFalha ao baixar ${dependencyInfo.name} v${dependencyInfo.version} - Causa: ${e.message}", true
            )
        }
    }

    private fun loadPlugin(file: File) {
        try {
            val loaded = Bukkit.getPluginManager().loadPlugin(file)
            if (loaded != null) Bukkit.getPluginManager().enablePlugin(loaded)
            if (dependencyInfo.name.equals("PlaceholderAPI", ignoreCase = true)) {
                log(
                    plugin!!, "§cPlaceholderAPI instalado! Reinicie o servidor para evitar erros.", true
                )
                plugin!!.server.dispatchCommand(
                    plugin!!.server.consoleSender, "restart"
                )
            }
        } catch (e: Exception) {
            log(
                plugin!!, "§cFalha ao iniciar dependência: ${dependencyInfo.name} - Causa: ${e.message}", true
            )
        }
    }

    private fun log(pl: JavaPlugin, message: String, debug: Boolean) {
        val msg = ChatColor.translateAlternateColorCodes('&', message)
        if (debug && pl.config.getBoolean("debug", true)) {
            Bukkit.getConsoleSender().sendMessage(prefix + msg)
        } else {
            Bukkit.getConsoleSender().sendMessage(msg)
        }
    }

    private fun asReadableSize(bytes: Long): String = when {
        bytes < 1024 -> "$bytes B"
        bytes < 1_048_576 -> "${bytes / 1024} KB"
        bytes < 1_073_741_824 -> "${bytes / 1_048_576} MB"
        else -> "${bytes / 1_073_741_824} GB"
    }
}