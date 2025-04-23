package com.github.zyypj.tadeuBooter.api.minecraft.command.annotation

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

/**
 * Provedor de tab-complete para caminho hierárquico.
 */
@Target(FUNCTION)
@Retention(RUNTIME)
annotation class TabComplete(
    val path: Array<String> = []
)