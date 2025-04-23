package com.github.zyypj.tadeuBooter.api.minecraft.task.builder

import kotlinx.coroutines.Job

/**
 * Handle para tarefas agendadas via coroutines com suporte a pause/resume.
 */
class CoroutineTaskHandle internal constructor(
    private val job: Job,
    private val pauseController: PauseController
) {
    /** Cancela a tarefa. */
    fun cancel() {
        job.cancel()
    }

    /** Pausa a tarefa (apenas para tarefas repetidas). */
    fun pause() {
        pauseController.pause()
    }

    /** Retoma a tarefa pausada. */
    fun resume() {
        pauseController.resume()
    }
}