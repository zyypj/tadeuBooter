package com.github.zyypj.tadeuBooter.api.minecraft.command.annotation

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

/**
 * Coleta métrica de uso e latência do comando.
 * @param name identificador da métrica
 */
@Target(FUNCTION)
@Retention(RUNTIME)
annotation class Metric(val name: String = "")