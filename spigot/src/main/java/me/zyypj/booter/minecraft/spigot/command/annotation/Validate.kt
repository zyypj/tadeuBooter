// File: annotation/Validate.kt
package me.zyypj.booter.minecraft.spigot.command.annotation

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

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