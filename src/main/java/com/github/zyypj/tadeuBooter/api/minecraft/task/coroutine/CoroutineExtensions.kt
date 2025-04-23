package com.github.zyypj.tadeuBooter.api.minecraft.task.coroutine

import kotlinx.coroutines.suspendCancellableCoroutine
import org.bukkit.plugin.Plugin
import kotlin.coroutines.resume

/**
 * Suspende a coroutine por [ticks] antes de retomar no main thread do Bukkit.
 */
suspend fun Plugin.delayTicks(ticks: Long) = suspendCancellableCoroutine<Unit> { cont ->
    val task = server.scheduler.runTaskLater(this, Runnable { cont.resume(Unit) }, ticks)
    cont.invokeOnCancellation { task.cancel() }
}

/**
 * Faz delay e depois executa [block], retornando seu resultado.
 */
suspend fun <T> Plugin.await(ticks: Long, block: suspend () -> T): T {
    delayTicks(ticks)
    return block()
}