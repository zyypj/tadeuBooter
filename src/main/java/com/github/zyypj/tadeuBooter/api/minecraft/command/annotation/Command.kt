package com.github.zyypj.tadeuBooter.api.minecraft.command.annotation

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS

/**
 * Marca uma classe como comando principal.
 * @property name             Nome do comando
 * @property description      Descrição para /help
 * @property usage            Uso exibido em caso de argumentos inválidos
 * @property aliases          Aliases adicionais
 * @property permission       Permissão necessária para usar o comando raiz
 * @property permissionMessage Mensagem customizada ao faltar permissão
 */
@Target(CLASS)
@Retention(RUNTIME)
annotation class Command(
    val name: String,
    val description: String = "",
    val usage: String = "",
    val aliases: Array<String> = [],
    val permission: String = "",
    val permissionMessage: String = "§cVocê não tem permissão para usar este comando."
)