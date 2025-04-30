package me.zyypj.booter.minecraft.spigot.command.annotation

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

/**
 * Impõe cooldown por jogador para este executor.
 * @param seconds duração em segundos
 * @param key opcional: chave customizada (padrão combina nome do comando + path)
 * @param message mensagem exibida quando em cooldown, use {time} para tempo restante
 */
@Target(FUNCTION)
@Retention(RUNTIME)
annotation class Cooldown(
    val seconds: Int,
    val key: String = "",
    val message: String = "§cAguarde {time}s para usar este comando novamente."
)