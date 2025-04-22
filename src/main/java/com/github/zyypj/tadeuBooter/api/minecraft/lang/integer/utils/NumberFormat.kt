package com.github.zyypj.tadeuBooter.api.minecraft.lang.integer.utils

import java.text.DecimalFormat
import kotlin.jvm.JvmStatic

/**
 * Formata números com sufixos (K, M, B, ...).
 * Métodos anotados com @JvmStatic estão disponíveis como métodos estáticos em Java:
 * NumberFormat.format(12345.0)
 */
object NumberFormat {

    private val suffixes = listOf(
        "", "K", "M", "B", "T", "Q", "QQ", "S", "SS",
        "OC", "N", "D", "UN", "DD", "TR", "QT", "QN",
        "SD", "SPD", "OD", "ND"
    )

    /**
     * Formata um número com o sufixo apropriado.
     * Exemplo: 1500.0 -> "1.5K"
     */
    @JvmStatic
    fun format(number: Double): String {
        var n = number
        var index = 0
        while (n >= 1_000 && index < suffixes.lastIndex) {
            n /= 1_000
            index++
        }
        return formatNumber(n) + suffixes[index]
    }

    /**
     * Formata número com até uma casa decimal.
     */
    @JvmStatic
    private fun formatNumber(number: Double): String {
        val df = DecimalFormat("0.#")
        return df.format(number)
    }
}