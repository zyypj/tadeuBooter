package com.github.zyypj.tadeuBooter.api.minecraft.command.annotation

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

/**
 * Marca um método como provedor de tab-complete.
 * @property forSub  Nome do subcomando que este provider atende ("" = raiz)
 */
@Target(FUNCTION)
@Retention(RUNTIME)
annotation class TabComplete(
    val forSub: String = ""
)