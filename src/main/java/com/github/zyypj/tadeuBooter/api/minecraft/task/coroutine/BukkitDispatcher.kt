package com.github.zyypj.tadeuBooter.api.minecraft.task.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlin.coroutines.CoroutineContext
import org.bukkit.plugin.Plugin

/**
 * Dispatcher que usa BukkitScheduler para dispatch sync/async.
 */
class BukkitDispatcher(
    private val plugin: Plugin,
    private val async: Boolean
) : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (async) plugin.server.scheduler.runTaskAsynchronously(plugin, block)
        else      plugin.server.scheduler.runTask(plugin, block)
    }
}