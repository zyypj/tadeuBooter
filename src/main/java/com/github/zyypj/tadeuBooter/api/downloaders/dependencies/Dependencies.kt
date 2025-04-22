package com.github.zyypj.tadeuBooter.api.downloaders.dependencies;

import com.github.zyypj.tadeuBooter.loader.Constants
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * Gerencia fila de downloads de dependências para um Plugin.
 */
class Dependencies private constructor(private val plugin: Plugin) {

    private val downloadQueue: Queue<CompletableFuture<Void>> = ConcurrentLinkedQueue()
    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    companion object {
        /** Cria instância associada a um Plugin. */
        @JvmStatic
        fun of(plugin: Plugin): Dependencies = Dependencies(plugin)

        /** Carrega dependências de um JSON em arquivo. */
        @JvmStatic
        @Throws(IOException::class)
        fun of(file: File): Array<Dependency> {
            val list = mutableListOf<Dependency>()
            FileReader(file).use { fr ->
                JsonReader(fr).use { jr ->
                    val obj = Constants.GSON.fromJson<JsonObject>(jr, JsonObject::class.java)
                    for ((_, elem) in obj.entrySet()) {
                        val arr: JsonArray = elem.asJsonArray
                        if (arr.size() >= 3) {
                            val info = DependencyInfo(
                                arr[0].asString,
                                arr[1].asString,
                                arr[2].asString
                            )
                            list += Dependency(info)
                        } else {
                            Bukkit.getConsoleSender().sendMessage("§4Dependência inválida no JSON: $file")
                        }
                    }
                }
            }
            return list.toTypedArray()
        }
    }

    /** Enfileira download de uma dependência. */
    fun queue(dependency: Dependency) {
        downloadQueue.add(dependency.download())
    }

    /** Enfileira múltiplas dependências. */
    fun queue(vararg dependencies: Dependency) {
        dependencies.forEach { queue(it) }
    }

    /** Remove da fila. */
    fun remove(dependency: Dependency) {
        val id = dependency.dependencyInfo.name
        downloadQueue.removeIf { it.toString().contains(id) }
    }

    /** Executa a fila e aguarda conclusão. */
    fun runQueue() {
        if (downloadQueue.isEmpty()) {
            Bukkit.getConsoleSender().sendMessage("§2Nenhuma dependência para baixar.")
            return
        }
        CompletableFuture.allOf(*(downloadQueue.toTypedArray())).join()
    }
}