package com.github.zyypj.tadeuBooter.api.minecraft.task.scheduler

/**
 * Representa um agendamento de task identificado por ID.
 * Permite encadear tarefas com thenSync/thenAsync.
 */
class TaskHandle internal constructor(private val id: String) {

    /** Executa [configure] em sync após completar esta task. */
    fun thenSync(
        nextId: String,
        configure: com.github.zyypj.tadeuBooter.api.minecraft.task.builder.TaskBuilder.() -> Unit
    ): TaskHandle {
        TaskScheduler.onFinish(id) {
            TaskScheduler.create(nextId).apply {
                sync()
                configure()
            }.schedule()
        }
        return TaskHandle(nextId)
    }

    /** Executa [configure] em async após completar esta task. */
    fun thenAsync(
        nextId: String,
        configure: com.github.zyypj.tadeuBooter.api.minecraft.task.builder.TaskBuilder.() -> Unit
    ): TaskHandle {
        TaskScheduler.onFinish(id) {
            TaskScheduler.create(nextId).apply {
                async()
                configure()
            }.schedule()
        }
        return TaskHandle(nextId)
    }
}