package com.github.zyypj.tadeuBooter.api.minecraft.command.annotation

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS

/**
 * Gera automaticamente a sub-rota 'help' com lista de comandos.
 * @param title título exibido no help
 * @param header cabeçalho do help
 */
@Target(CLASS)
@Retention(RUNTIME)
annotation class AutoHelp(
    val title: String = "",
    val header: String = "=== Help ==="
)