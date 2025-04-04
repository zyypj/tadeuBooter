package com.github.zyypj.tadeuBooter.api.minecraft.lang.text.utils;

import com.google.common.base.Preconditions;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe para representar e manipular períodos de tempo no formato "FancyTime".
 */
@Data
@EqualsAndHashCode
@ToString
public class FancyTime implements Serializable {

    private static final long serialVersionUID = 1L;

    // Padrão para parsing de strings como "2a 3m 1s 4d 5h 15min 30seg"
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)(a|m|s|d|h|min|seg)");

    private final int years;
    private final int months;
    private final int weeks;
    private final int days;
    private final int hours;
    private final int minutes;
    private final int seconds;

    /**
     * Construtor principal.
     *
     * @param years   Anos (deve ser >= 0).
     * @param months  Meses (deve ser >= 0).
     * @param weeks   Semanas (deve ser >= 0).
     * @param days    Dias (deve ser >= 0).
     * @param hours   Horas (deve ser entre 0 e 23).
     * @param minutes Minutos (deve ser entre 0 e 59).
     * @param seconds Segundos (deve ser entre 0 e 59).
     */
    public FancyTime(int years, int months, int weeks, int days, int hours, int minutes, int seconds) {
        Preconditions.checkArgument(years >= 0, "years must be >= 0");
        Preconditions.checkArgument(months >= 0, "months must be >= 0");
        Preconditions.checkArgument(weeks >= 0, "weeks must be >= 0");
        Preconditions.checkArgument(days >= 0, "days must be >= 0");
        Preconditions.checkArgument(hours >= 0 && hours < 24, "hours must be between 0 and 23");
        Preconditions.checkArgument(minutes >= 0 && minutes < 60, "minutes must be between 0 and 59");
        Preconditions.checkArgument(seconds >= 0 && seconds < 60, "seconds must be between 0 and 59");

        this.years = years;
        this.months = months;
        this.weeks = weeks;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    /**
     * Construtor que inicializa a partir de uma string no formato "2a 3m 1s 4d 5h 15min 10seg".
     *
     * @param inputString String representando o tempo.
     */
    public FancyTime(String inputString) {
        Preconditions.checkNotNull(inputString, "Input string cannot be null");
        int years = 0, months = 0, weeks = 0, days = 0, hours = 0, minutes = 0, seconds = 0;

        Matcher matcher = TIME_PATTERN.matcher(inputString.toLowerCase());
        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "a":
                    years += value;
                    break;
                case "m":
                    months += value;
                    break;
                case "s":
                    weeks += value;
                    break;
                case "d":
                    days += value;
                    break;
                case "h":
                    hours += value;
                    break;
                case "min":
                    minutes += value;
                    break;
                case "seg":
                    seconds += value;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown time unit: " + unit);
            }
        }

        this.years = years;
        this.months = months;
        this.weeks = weeks;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    /**
     * Converte o tempo armazenado para "ticks" no Minecraft (20 ticks por segundo).
     *
     * @return Tempo total em ticks.
     */
    public long toTicks() {
        long totalSeconds = Duration.ofDays(years * 365L).getSeconds()
                + Duration.ofDays(months * 30L).getSeconds()
                + Duration.ofDays(weeks * 7L).getSeconds()
                + Duration.ofDays(days).getSeconds()
                + Duration.ofHours(hours).getSeconds()
                + Duration.ofMinutes(minutes).getSeconds()
                + seconds;
        return totalSeconds * 20;
    }

    /**
     * Converte o tempo armazenado para milissegundos.
     *
     * @return Tempo total em milissegundos.
     */
    public long toMillis() {
        return toTicks() * 50; // 1 tick = 50 ms
    }

    /**
     * Converte o tempo armazenado para uma string no formato "2a 3m 1s 4d 5h 15min 10seg".
     *
     * @return String formatada representando o tempo.
     */
    public String toFancyString() {
        StringBuilder builder = new StringBuilder();
        if (years > 0) builder.append(years).append("a ");
        if (months > 0) builder.append(months).append("m ");
        if (weeks > 0) builder.append(weeks).append("s ");
        if (days > 0) builder.append(days).append("d ");
        if (hours > 0) builder.append(hours).append("h ");
        if (minutes > 0) builder.append(minutes).append("min ");
        if (seconds > 0) builder.append(seconds).append("seg");
        return builder.toString().trim();
    }

    /**
     * Verifica se o período representado já expirou considerando o tempo de início informado.
     *
     * @param startTimeMillis O instante (em milissegundos) em que o período começou.
     * @return true se o tempo atual já ultrapassou (startTimeMillis + período), false caso contrário.
     */
    public boolean isExpired(long startTimeMillis) {
        long expirationMillis = startTimeMillis + this.toMillis();
        return System.currentTimeMillis() >= expirationMillis;
    }
}