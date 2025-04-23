package com.github.zyypj.tadeuBooter.api.minecraft.task.builder

import kotlinx.coroutines.CompletableDeferred

/**
 * Controla estado de pausa e retomada de coroutines.
 */
class PauseController {
    private var resumeSignal = CompletableDeferred<Unit>().apply { complete(Unit) }

    /** Suspende enquanto estiver pausado. */
    suspend fun awaitResume() {
        resumeSignal.await()
    }

    /** Pausa o fluxo, coroutines ficam suspensas em awaitResume(). */
    fun pause() {
        if (resumeSignal.isCompleted) {
            resumeSignal = CompletableDeferred()
        }
    }

    /** Retoma coroutines suspensas. */
    fun resume() {
        if (!resumeSignal.isCompleted) {
            resumeSignal.complete(Unit)
        }
    }
}