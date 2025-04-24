package me.zyypj.booter.minecraft.spigot.factories

/**
 * Fábrica de barras de progresso customizáveis para chat Minecraft.
 * Suporta porcentagem (0–100), fração (0–1), direção opcional e estilos de caracteres.
 */
object ProgressBarFactory {

    /**
     * Gera uma barra de progresso baseada em percentual (0.0–100.0).
     *
     * @param percentage Valor de progresso em porcentagem (encaixado entre 0.0 e 100.0).
     * @param activeSymbol Símbolo a ser usado para as unidades preenchidas.
     * @param inactiveSymbol Símbolo a ser usado para as unidades não preenchidas.
     * @param activeColor Código de cor (por exemplo ChatColor) aplicado antes dos símbolos preenchidos.
     * @param inactiveColor Código de cor aplicado antes dos símbolos não preenchidos.
     * @param maxChars Número total de caracteres que a barra deve conter.
     * @param showPercentage Se true, anexa o valor percentual formatado ao final da barra.
     * @param reverse Se true, inverte o preenchimento (preenche da direita para a esquerda).
     *
     * @return String contendo a barra de progresso com cores e símbolos aplicados.
     */
    @JvmOverloads
    fun getBarPercent(
        percentage: Double,
        activeSymbol: String,
        inactiveSymbol: String,
        activeColor: String,
        inactiveColor: String,
        maxChars: Int,
        showPercentage: Boolean = false,
        reverse: Boolean = false
    ): String = buildString {
        val pct = percentage.coerceIn(0.0, 100.0)
        val filled = ((pct / 100.0) * maxChars).toInt()

        if (!reverse) {
            append(activeColor)
            repeat(filled) { append(activeSymbol) }
            append(inactiveColor)
            repeat(maxChars - filled) { append(inactiveSymbol) }
        } else {
            append(inactiveColor)
            repeat(maxChars - filled) { append(inactiveSymbol) }
            append(activeColor)
            repeat(filled) { append(activeSymbol) }
        }

        if (showPercentage) {
            append(" ").append("%.0f%%".format(pct))
        }
    }

    /**
     * Gera uma barra de progresso baseada em fração (0.0–1.0).
     *
     * @param fraction Valor de progresso em fração (encaixado entre 0.0 e 1.0).
     * @param activeSymbol Símbolo a ser usado para as unidades preenchidas.
     * @param inactiveSymbol Símbolo a ser usado para as unidades não preenchidas.
     * @param activeColor Código de cor aplicado antes dos símbolos preenchidos.
     * @param inactiveColor Código de cor aplicado antes dos símbolos não preenchidos.
     * @param maxChars Número total de caracteres que a barra deve conter.
     * @param showPercentage Se true, anexa o valor percentual formatado ao final da barra.
     * @param reverse Se true, inverte o preenchimento (preenche da direita para a esquerda).
     *
     * @return String contendo a barra de progresso com cores e símbolos aplicados.
     */
    @JvmOverloads
    fun getBarFraction(
        fraction: Double,
        activeSymbol: String,
        inactiveSymbol: String,
        activeColor: String,
        inactiveColor: String,
        maxChars: Int,
        showPercentage: Boolean = false,
        reverse: Boolean = false
    ): String = getBarPercent(
        fraction * 100, activeSymbol, inactiveSymbol, activeColor, inactiveColor, maxChars, showPercentage, reverse
    )

    /**
     * Gera uma barra de progresso “suave” usando blocos parciais (▏▎▍▌▋▊▉█).
     *
     * @param fraction Valor de progresso em fração (encaixado entre 0.0 e 1.0).
     * @param activeColor Código de cor aplicado antes dos blocos preenchidos.
     * @param inactiveColor Código de cor aplicado antes dos blocos vazios.
     * @param maxChars Número de caracteres (cada um dividido em 8 subunidades).
     * @param showPercentage Se true, anexa o valor percentual formatado ao final da barra.
     * @param reverse Se true, inverte o preenchimento (preenche da direita para a esquerda).
     *
     * @return String contendo a barra suave com cores e símbolos aplicados.
     */
    @JvmOverloads
    fun getSmoothBar(
        fraction: Double,
        activeColor: String,
        inactiveColor: String,
        maxChars: Int,
        showPercentage: Boolean = false,
        reverse: Boolean = false
    ): String = buildString {
        val pct = fraction.coerceIn(0.0, 1.0)
        val totalSub = maxChars * 8
        val filledSub = (pct * totalSub).toInt()
        val fullBlocks = filledSub / 8
        val rem = filledSub % 8
        val partials = listOf('▏', '▎', '▍', '▌', '▋', '▊', '▉')

        val segments = buildList {
            addAll(List(fullBlocks) { activeColor to '█' })
            if (rem > 0) add(activeColor to partials[rem - 1])
            val emptyBlocks = maxChars - ((filledSub + 7) / 8)
            addAll(List(emptyBlocks) { inactiveColor to '█' })
        }

        val iterable = if (!reverse) segments else segments.asReversed()
        for ((color, symbol) in iterable) {
            append(color).append(symbol)
        }

        if (showPercentage) {
            append(" ").append("%.0f%%".format(pct * 100))
        }
    }
}