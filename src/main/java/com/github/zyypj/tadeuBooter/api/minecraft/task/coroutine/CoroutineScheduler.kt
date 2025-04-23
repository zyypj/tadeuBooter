package com.github.zyypj.tadeuBooter.api.minecraft.task.coroutine

import kotlinx.coroutines.*
import org.bukkit.plugin.Plugin
import kotlin.coroutines.CoroutineContext

/**
 * CoroutineScheduler: gerencia coroutines de forma performática.
 * Instanciável e configurável por construtor.
 */
class CoroutineScheduler(
    private val plugin: Plugin,
    parentContext: CoroutineContext = SupervisorJob(),
    private val mainDispatcher: CoroutineDispatcher = BukkitDispatcher(plugin, false),
    private val asyncDispatcher: CoroutineDispatcher = BukkitDispatcher(plugin, true)
) {
    private val scope: CoroutineScope = CoroutineScope(parentContext + mainDispatcher)

    private suspend fun CoroutineScope.safe(block: suspend CoroutineScope.() -> Unit) {
        try { block() }
        catch (ex: Throwable) {
            plugin.logger.severe("[CoroutineScheduler] Task failed: ${'$'}{ex.message}")
            ex.printStackTrace()
        }
    }

    /** Lança imediatamente na MAIN thread. */
    fun launchSync(block: suspend CoroutineScope.() -> Unit): Job =
        scope.launch(mainDispatcher) { safe(block) }

    /** Lança imediatamente em background. */
    fun launchAsync(block: suspend CoroutineScope.() -> Unit): Job =
        scope.launch(asyncDispatcher) { safe(block) }

    /** Agenda execução única após delay na MAIN thread. */
    fun scheduleSync(delayTicks: Long, block: suspend CoroutineScope.() -> Unit): Job =
        scope.launch(mainDispatcher) {
            delay(delayTicks * 50)
            safe(block)
        }

    /** Agenda execução única após delay em background. */
    fun scheduleAsync(delayTicks: Long, block: suspend CoroutineScope.() -> Unit): Job =
        scope.launch(asyncDispatcher) {
            delay(delayTicks * 50)
            safe(block)
        }

    /** Agenda execuções repetidas na MAIN thread. */
    fun scheduleSyncRepeating(
        delayTicks: Long, periodTicks: Long,
        block: suspend CoroutineScope.() -> Unit
    ): Job = scope.launch(mainDispatcher) {
        delay(delayTicks * 50)
        while (isActive) {
            safe(block)
            delay(periodTicks * 50)
        }
    }

    /** Agenda execuções repetidas em background. */
    fun scheduleAsyncRepeating(
        delayTicks: Long, periodTicks: Long,
        block: suspend CoroutineScope.() -> Unit
    ): Job = scope.launch(asyncDispatcher) {
        delay(delayTicks * 50)
        while (isActive) {
            safe(block)
            delay(periodTicks * 50)
        }
    }

    /** Cancela um Job específico. */
    fun cancel(job: Job) = job.cancel()

    /** Cancela todas as coroutines agendadas. */
    fun cancelAll() = scope.coroutineContext.cancelChildren()
}