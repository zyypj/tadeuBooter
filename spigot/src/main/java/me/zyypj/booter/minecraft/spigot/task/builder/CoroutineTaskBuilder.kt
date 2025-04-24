package me.zyypj.booter.minecraft.spigot.task.builder

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import me.zyypj.booter.minecraft.spigot.task.coroutine.CoroutineScheduler
import org.bukkit.plugin.Plugin

/**
 * Builder DSL para agendar coroutines com pause/resume.
 */
class CoroutineTaskBuilder(
    private val id: String,
    private val plugin: Plugin
) {
    private val scheduler: CoroutineScheduler = CoroutineScheduler(plugin)
    private var async: Boolean = false
    private var delayTicks: Long = 0
    private var periodTicks: Long = 0
    private var repeating: Boolean = false
    private var action: suspend CoroutineScope.() -> Unit = {}
    private var onComplete: (suspend CoroutineScope.() -> Unit)? = null
    private val pauseController = PauseController()

    fun async() = apply { this.async = true }
    fun sync() = apply { this.async = false }
    fun delay(ticks: Long) = apply { this.delayTicks = ticks }
    fun period(ticks: Long) = apply { this.periodTicks = ticks; this.repeating = true }
    fun action(block: suspend CoroutineScope.() -> Unit) = apply { this.action = block }
    fun onComplete(block: suspend CoroutineScope.() -> Unit) = apply { this.onComplete = block }

    /** Agenda e retorna um [CoroutineTaskHandle] para controle total. */
    fun schedule(): CoroutineTaskHandle {
        val job: Job = if (!repeating) {
            if (async) scheduler.scheduleAsync(delayTicks) {
                // single-shot: respeita pauseController once
                pauseController.awaitResume()
                action(); onComplete?.invoke(this)
            } else scheduler.scheduleSync(delayTicks) {
                pauseController.awaitResume()
                action(); onComplete?.invoke(this)
            }
        } else {
            if (async) scheduler.scheduleAsyncRepeating(delayTicks, periodTicks) {
                pauseController.awaitResume()
                action()
            } else scheduler.scheduleSyncRepeating(delayTicks, periodTicks) {
                pauseController.awaitResume()
                action()
            }
        }
        return CoroutineTaskHandle(job, pauseController)
    }
}