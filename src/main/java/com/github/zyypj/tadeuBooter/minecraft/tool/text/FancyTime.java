package com.github.zyypj.tadeuBooter.minecraft.tool.text;

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

    private final int days;
    private final int hours;
    private final int minutes;
    private final int seconds;

    // Padrão para parsing de strings como "2d 4h 15m 30s"
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)([dhms])");

    /**
     * Construtor principal.
     *
     * @param days    Dias (deve ser >= 0).
     * @param hours   Horas (deve ser entre 0 e 23).
     * @param minutes Minutos (deve ser entre 0 e 59).
     * @param seconds Segundos (deve ser entre 0 e 59).
     */
    public FancyTime(int days, int hours, int minutes, int seconds) {
        Preconditions.checkArgument(days >= 0, "days must be >= 0");
        Preconditions.checkArgument(hours >= 0 && hours < 24, "hours must be between 0 and 23");
        Preconditions.checkArgument(minutes >= 0 && minutes < 60, "minutes must be between 0 and 59");
        Preconditions.checkArgument(seconds >= 0 && seconds < 60, "seconds must be between 0 and 59");

        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    /**
     * Construtor que inicializa a partir de uma string no formato "2d 3h 15m 10s".
     *
     * @param inputString String representando o tempo.
     */
    public FancyTime(String inputString) {
        Preconditions.checkNotNull(inputString, "Input string cannot be null");
        int days = 0, hours = 0, minutes = 0, seconds = 0;

        Matcher matcher = TIME_PATTERN.matcher(inputString);
        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            char unit = matcher.group(2).toLowerCase().charAt(0);

            switch (unit) {
                case 'd':
                    days += value;
                    break;
                case 'h':
                    hours += value;
                    break;
                case 'm':
                    minutes += value;
                    break;
                case 's':
                    seconds += value;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown time unit: " + unit);
            }
        }

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
        long totalSeconds = Duration.ofDays(days).getSeconds()
                + Duration.ofHours(hours).getSeconds()
                + Duration.ofMinutes(minutes).getSeconds()
                + seconds;
        return totalSeconds * 20;
    }

    /**
     * Converte o tempo armazenado para uma string no formato "2d 3h 15m 10s".
     *
     * @return String formatada representando o tempo.
     */
    public String toFancyString() {
        StringBuilder builder = new StringBuilder();
        if (days > 0) builder.append(days).append("d ");
        if (hours > 0) builder.append(hours).append("h ");
        if (minutes > 0) builder.append(minutes).append("m ");
        if (seconds > 0) builder.append(seconds).append("s");
        return builder.toString().trim();
    }
}