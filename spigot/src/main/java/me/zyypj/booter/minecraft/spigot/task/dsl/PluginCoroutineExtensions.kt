package me.zyypj.booter.minecraft.spigot.task.dsl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import me.zyypj.booter.minecraft.spigot.task.builder.CoroutineTaskBuilder
import me.zyypj.booter.minecraft.spigot.task.builder.CoroutineTaskHandle
import me.zyypj.booter.minecraft.spigot.task.coroutine.CoroutineScheduler
import org.bukkit.plugin.Plugin

/**
 * Cria uma instância de CoroutineScheduler vinculada ao plugin.
 */
fun Plugin.coroutineScheduler(): CoroutineScheduler = CoroutineScheduler(this)

inline fun Plugin.launchSync(
    scheduler: CoroutineScheduler = coroutineScheduler(),
    crossinline block: suspend CoroutineScope.() -> Unit
): Job = scheduler.launchSync { block() }
inline fun Plugin.launchAsync(
    scheduler: CoroutineScheduler = coroutineScheduler(),
    crossinline block: suspend CoroutineScope.() -> Unit
): Job = scheduler.launchAsync { block() }

inline fun Plugin.scheduleSync(
    delayTicks: Long,
    scheduler: CoroutineScheduler = coroutineScheduler(),
    crossinline block: suspend CoroutineScope.() -> Unit
): Job = scheduler.scheduleSync(delayTicks) { block() }
inline fun Plugin.scheduleAsync(
    delayTicks: Long,
    scheduler: CoroutineScheduler = coroutineScheduler(),
    crossinline block: suspend CoroutineScope.() -> Unit
): Job = scheduler.scheduleAsync(delayTicks) { block() }

inline fun Plugin.scheduleSyncRepeating(
    delayTicks: Long, periodTicks: Long,
    scheduler: CoroutineScheduler = coroutineScheduler(),
    crossinline block: suspend CoroutineScope.() -> Unit
): Job = scheduler.scheduleSyncRepeating(delayTicks, periodTicks) { block() }
inline fun Plugin.scheduleAsyncRepeating(
    delayTicks: Long, periodTicks: Long,
    scheduler: CoroutineScheduler = coroutineScheduler(),
    crossinline block: suspend CoroutineScope.() -> Unit
): Job = scheduler.scheduleAsyncRepeating(delayTicks, periodTicks) { block() }

fun Plugin.scheduleSync(
    delayTicks: Long,
    scheduler: CoroutineScheduler,
    runnable: Runnable
): Job = scheduler.scheduleSync(delayTicks) { runnable.run() }
fun Plugin.scheduleAsync(
    delayTicks: Long,
    scheduler: CoroutineScheduler,
    runnable: Runnable
): Job = scheduler.scheduleAsync(delayTicks) { runnable.run() }
fun Plugin.scheduleSyncRepeating(
    delayTicks: Long, periodTicks: Long,
    scheduler: CoroutineScheduler,
    runnable: Runnable
): Job = scheduler.scheduleSyncRepeating(delayTicks, periodTicks) { runnable.run() }
fun Plugin.scheduleAsyncRepeating(
    delayTicks: Long, periodTicks: Long,
    scheduler: CoroutineScheduler,
    runnable: Runnable
): Job = scheduler.scheduleAsyncRepeating(delayTicks, periodTicks) { runnable.run() }

inline fun Plugin.coroutineTask(
    id: String,
    configure: CoroutineTaskBuilder.() -> Unit
): CoroutineTaskHandle =
    CoroutineTaskBuilder(id, this).apply(configure).schedule()