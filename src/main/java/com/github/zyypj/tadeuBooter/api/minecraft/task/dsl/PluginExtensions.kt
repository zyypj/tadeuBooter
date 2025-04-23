package com.github.zyypj.tadeuBooter.api.minecraft.task.dsl

import org.bukkit.plugin.Plugin
import com.github.zyypj.tadeuBooter.api.minecraft.task.builder.TaskBuilder
import com.github.zyypj.tadeuBooter.api.minecraft.task.scheduler.TaskScheduler
import com.github.zyypj.tadeuBooter.api.minecraft.task.scheduler.TaskHandle

/**
 * Agendamento simplificado via extensão Kotlin.
 */
inline fun Plugin.task(
    id: String,
    configure: TaskBuilder.() -> Unit
): TaskHandle =
    TaskScheduler.create(id).apply(configure).schedule()