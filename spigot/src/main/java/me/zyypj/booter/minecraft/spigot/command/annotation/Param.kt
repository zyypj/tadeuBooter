package me.zyypj.booter.minecraft.spigot.command.annotation

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

/**
 * Injeta e converte automaticamente um parâmetro de comando.
 * @param index índice no array de args (0-based)
 * @param type tipo esperado
 * @param default valor padrão se ausente
 * @param errorMessage mensagem em caso de falha de conversão
 */
@Target(VALUE_PARAMETER)
@Retention(RUNTIME)
annotation class Param(
    val index: Int,
    val type: ArgType,
    val default: String = "",
    val errorMessage: String = "§cArgumento inválido"
)