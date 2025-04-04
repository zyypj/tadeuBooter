package com.github.zyypj.tadeuBooter.api.minecraft.lang.integer.utils;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class NumberFormat {
    private static final List<String> suffixes = Arrays.asList(
            "", "K", "M", "B", "T", "Q", "QQ", "S", "SS",
            "OC", "N", "D", "UN", "DD", "TR", "QT", "QN",
            "SD", "SPD", "OD", "ND"
    );

    /**
     * Formata um número com o sufixo apropriado da lista de sufixos.
     *
     * @param number O número a ser formatado.
     * @return Uma string representando o número formatado com sufixo.
     */
    public static String format(double number) {
        int suffixIndex = 0;

        while (number >= 1_000 && suffixIndex < suffixes.size() - 1) {
            number /= 1_000;
            suffixIndex++;
        }

        return formatNumber(number) + suffixes.get(suffixIndex);
    }

    /**
     * Formata um número com até uma casa decimal.
     *
     * @param number O número a ser formatado.
     * @return Uma string do número formatado.
     */
    private static String formatNumber(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("0.#");
        return decimalFormat.format(number);
    }
}