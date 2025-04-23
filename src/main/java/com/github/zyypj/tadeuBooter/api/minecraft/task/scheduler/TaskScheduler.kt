package com.github.zyypj.tadeuBooter.api.minecraft.task.scheduler

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.ConcurrentHashMap

/**
 * Gerencia e registra todas as tasks por ID, além de callbacks de conclusão.
 */
object TaskScheduler {
    internal lateinit var plugin: JavaPlugin
        private set

    private val tasks = ConcurrentHashMap<String, BukkitTask>()
    private val finishCallbacks = ConcurrentHashMap<String, MutableList<() -> Unit>>()

    /** Deve ser chamado em onEnable() do Plugin. */
    fun init(p: JavaPlugin) { plugin = p }

    /** Inicia um builder para a task [id]. */
    fun create(id: String): com.github.zyypj.tadeuBooter.api.minecraft.task.builder.TaskBuilder =
        com.github.zyypj.tadeuBooter.api.minecraft.task.builder.TaskBuilder(id)

    internal fun register(id: String, task: BukkitTask, single: Boolean) {
        tasks[id]?.cancel()
        tasks[id] = task
        if (single) {
            finishCallbacks[id]?.forEach { it() }
            finishCallbacks.remove(id)
        }
    }

    /** Cancela a task com [id]. */
    fun cancel(id: String) { tasks.remove(id)?.cancel() }

    /** Cancela todas as tasks. */
    fun cancelAll() { tasks.values.forEach { it.cancel() }; tasks.clear() }

    /** Registra um callback que dispara quando a task [id] completar (tarefa única). */
    fun onFinish(id: String, callback: () -> Unit) {
        finishCallbacks.computeIfAbsent(id) { mutableListOf() }.add(callback)
    }
}