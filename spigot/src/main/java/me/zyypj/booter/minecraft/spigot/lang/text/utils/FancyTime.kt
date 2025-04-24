package me.zyypj.booter.minecraft.spigot.lang.text.utils

import java.io.Serializable
import java.time.Duration
import java.util.regex.Pattern

/**
 * Representa e manipula períodos de tempo no formato "FancyTime".
 */
data class FancyTime(
    val years: Int = 0,
    val months: Int = 0,
    val weeks: Int = 0,
    val days: Int = 0,
    val hours: Int = 0,
    val minutes: Int = 0,
    val seconds: Int = 0
) : Serializable {
    companion object {
        private val TIME_PATTERN: Pattern = Pattern.compile("(\\d+)(a|m|s|d|h|min|seg)", Pattern.CASE_INSENSITIVE)

        /**
         * Cria FancyTime a partir de string, ex: "2a 3m 1s 4d 5h 15min 30seg".
         */
        @JvmStatic
        fun parse(input: String): FancyTime {
            require(input.isNotBlank()) { "Input string cannot be blank" }
            var y = 0;
            var mo = 0;
            var w = 0;
            var d = 0;
            var h = 0;
            var mi = 0;
            var s = 0
            val matcher = TIME_PATTERN.matcher(input.trim())
            while (matcher.find()) {
                val value = matcher.group(1).toInt()
                when (matcher.group(2).lowercase()) {
                    "a" -> y += value
                    "m" -> mo += value
                    "s" -> w += value
                    "d" -> d += value
                    "h" -> h += value
                    "min" -> mi += value
                    "seg" -> s += value
                    else -> throw IllegalArgumentException("Unknown time unit: ${matcher.group(2)}")
                }
            }
            return FancyTime(y, mo, w, d, h, mi, s).apply { validate() }
        }
    }

    init {
        validate()
    }

    private fun validate() {
        require(years >= 0) { "years must be >= 0" }
        require(months >= 0) { "months must be >= 0" }
        require(weeks >= 0) { "weeks must be >= 0" }
        require(days >= 0) { "days must be >= 0" }
        require(hours in 0..23) { "hours must be between 0 and 23" }
        require(minutes in 0..59) { "minutes must be between 0 and 59" }
        require(seconds in 0..59) { "seconds must be between 0 and 59" }
    }

    /**
     * Converte para ticks (20 ticks por segundo).
     */
    fun toTicks(): Long {
        val totalSec = Duration.ofDays(years.toLong() * 365)
            .plusDays(months.toLong() * 30)
            .plusDays(weeks.toLong() * 7)
            .plusDays(days.toLong())
            .plusHours(hours.toLong())
            .plusMinutes(minutes.toLong())
            .plusSeconds(seconds.toLong())
            .seconds
        return totalSec * 20
    }

    /**
     * Converte para milissegundos.
     */
    fun toMillis(): Long = toTicks() * 50

    /**
     * Formata para string no formato fancy.
     */
    fun toFancyString(): String {
        val parts = mutableListOf<String>()
        if (years > 0) parts += "${years}a"
        if (months > 0) parts += "${months}m"
        if (weeks > 0) parts += "${weeks}s"
        if (days > 0) parts += "${days}d"
        if (hours > 0) parts += "${hours}h"
        if (minutes > 0) parts += "${minutes}min"
        if (seconds > 0) parts += "${seconds}seg"
        return parts.joinToString(" ")
    }

    /**
     * Verifica se expirou dado o timestamp de início.
     */
    fun isExpired(startMillis: Long): Boolean {
        return System.currentTimeMillis() >= startMillis + toMillis()
    }
}