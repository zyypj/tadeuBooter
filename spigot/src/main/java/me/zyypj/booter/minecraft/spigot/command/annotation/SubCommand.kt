package me.zyypj.booter.minecraft.spigot.command.annotation

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

/**
 * Marca um método como subcomando aninhado.
 * @property name               Nome do subcomando
 * @property permission         Permissão necessária para executar este subcomando
 * @property permissionMessage  Mensagem customizada ao faltar permissão
 */
@Target(FUNCTION)
@Retention(RUNTIME)
annotation class SubCommand(
    val path: Array<String>,
    val permission: String = "",
    val permissionMessage: String = "§4§lERRO! §cVocê não tem permissão para isso."
)