package com.github.zyypj.tadeuBooter.api.minecraft.task.coroutine

import kotlinx.coroutines.*
import org.bukkit.plugin.Plugin

/**
 * Gerencia coroutines atreladas ao ciclo de vida do Plugin.
 */
object BotScope {
    private val lock = Any()
    lateinit var scope: CoroutineScope
        private set

    /** Deve ser chamado em onEnable() do Plugin. */
    fun init(plugin: Plugin) = synchronized(lock) {
        if (!::scope.isInitialized) {
            scope = CoroutineScope(SupervisorJob() + BukkitDispatcher(plugin))
        }
    }

    /** Deve ser chamado em onDisable() do Plugin. */
    fun shutdown() = synchronized(lock) {
        if (::scope.isInitialized) scope.cancel()
    }
}