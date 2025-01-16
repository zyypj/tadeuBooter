package com.github.zyypj.booter.minecraft.tool.text;

public class ProgressBarBuilder {

    public static String getBar(final double percentage, final String activeSymbol, final String inactiveSymbol, final String activeColor, final String inactiveColor, final int maxChars) {
        StringBuilder bar = new StringBuilder(activeColor);

        double activeSymbolsAmount = 0;
        final double percentagePerSymbol = 100.0 / maxChars;
        for (int i = 1; i < maxChars + 1; i++) {
            if (percentage < percentagePerSymbol * i) break;
            bar.append(activeSymbol);
            activeSymbolsAmount++;
        }
        bar.append(inactiveColor);

        for (int i = 0; i < maxChars - activeSymbolsAmount; i++) {
            bar.append(inactiveSymbol);
        }
        return bar.toString();
    }
}
