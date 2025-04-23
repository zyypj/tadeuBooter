// File: annotation/Validate.kt
package com.github.zyypj.tadeuBooter.api.minecraft.command.annotation

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.ANNOTATION_CLASS

/**
 * Valida um argumento específico no método executor.
 * @param index índice do argumento (0-based)
 * @param type tipo esperado
 * @param errorMessage mensagem exibida em caso de falha de validação
 */
@Repeatable
@Target(FUNCTION)
@Retention(RUNTIME)
annotation class Validate(
    val index: Int,
    val type: ArgType,
    val errorMessage: String = "§cArgumento inválido"
)