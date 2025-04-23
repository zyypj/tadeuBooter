package com.github.zyypj.tadeuBooter.api.minecraft.command.annotation

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

/**
 * Marca um método como executor de comando (raiz ou subcomando).
 * Deve retornar Boolean e receber (CommandSender, Array<String>).
 */
@Target(FUNCTION)
@Retention(RUNTIME)
annotation class Execute