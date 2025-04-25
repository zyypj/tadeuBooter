package me.zyypj.booter.minecraft.spigot.command.annotation

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

/**
 * Especifica os tipos de remetente permitidos para o executor do comando.
 */
@Target(FUNCTION)
@Retention(RUNTIME)
annotation class AllowedSenders(
    val value: SenderType,
    val errorMessage: String = "§cVocê não pode usar esse comando."
)