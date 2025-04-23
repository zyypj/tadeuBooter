package com.github.zyypj.tadeuBooter.api.minecraft.task.builder

import com.github.zyypj.tadeuBooter.api.minecraft.logger.Debug
import org.bukkit.scheduler.BukkitTask
import com.github.zyypj.tadeuBooter.api.minecraft.task.scheduler.TaskScheduler
import com.github.zyypj.tadeuBooter.api.minecraft.task.scheduler.TaskHandle

/**
 * Builder DSL para agendamento de tasks, retorna [TaskHandle] ao agendar.
 */
class TaskBuilder internal constructor(private val id: String) {
    private val plugin get() = TaskScheduler.plugin
    private var delayTicks: Long = 0
    private var periodTicks: Long = 0
    private var async: Boolean = false
    private var action: Runnable? = null
    private var onComplete: Runnable? = null

    fun async() = apply { this.async = true }
    fun sync() = apply { this.async = false }
    fun delay(ticks: Long) = apply { this.delayTicks = ticks }
    fun period(ticks: Long) = apply { this.periodTicks = ticks }
    fun action(block: () -> Unit) = apply { this.action = Runnable { safe(block) } }
    fun onComplete(block: () -> Unit) = apply { this.onComplete = Runnable { safe(block) } }

    /** Agenda e retorna um [TaskHandle] para chaining. */
    fun schedule(): TaskHandle {
        requireNotNull(action) { "Action must be set for task '$id'" }
        val scheduler = plugin.server.scheduler
        val single = periodTicks == 0L
        val bukkitTask: BukkitTask = if (async) {
            if (single) scheduler.runTaskLaterAsynchronously(plugin, wrap(action!!, single), delayTicks)
            else      scheduler.runTaskTimerAsynchronously(plugin, wrap(action!!, false), delayTicks, periodTicks)
        } else {
            if (single) scheduler.runTaskLater(plugin, wrap(action!!, single), delayTicks)
            else      scheduler.runTaskTimer(plugin, wrap(action!!, false), delayTicks, periodTicks)
        }
        TaskScheduler.register(id, bukkitTask, single)

        if (single && onComplete != null) {
            plugin.server.scheduler.runTaskLater(plugin, wrap(onComplete!!, true), delayTicks)
        }
        return TaskHandle(id)
    }

    private fun wrap(r: Runnable, finish: Boolean): Runnable = Runnable {
        try {
            r.run()
        } catch (ex: Exception) {
            Debug.log("§4Task '$id' fahlou: ${ex.message}", false)
            ex.printStackTrace()
        } finally {
            if (finish) TaskScheduler.onFinish(id) { }
        }
    }

    private fun safe(block: () -> Unit) {
        try { block() } catch (e: Exception) {
            Debug.log("§4Erro em task '$id': ${e.message}", false)
            e.printStackTrace()
        }
    }
}