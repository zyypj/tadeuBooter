package com.github.zyypj.booter.diagnostics;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.DecimalFormat;

public class DiagnosticTools {

    private static boolean debugMode = false;
    private static int tickCount = 0;
    private static long[] tickTimes = new long[600];
    private static final DecimalFormat df = new DecimalFormat("##.##");

    /**
     * Inicia o monitoramento manual de TPS.
     *
     * @param plugin O plugin principal para agendamento de tarefas.
     */
    public static void startTPSMonitor(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            tickTimes[tickCount % tickTimes.length] = System.currentTimeMillis();
            tickCount++;
        }, 0L, 1L);
    }

    /**
     * Ativa ou desativa o modo de debug.
     *
     * @param enable true para ativar, false para desativar.
     */
    public static void setDebugMode(boolean enable) {
        debugMode = enable;
        log("Modo debug " + (debugMode ? "ativado" : "desativado") + ".");
    }

    /**
     * Verifica se o modo de debug está ativado.
     *
     * @return true se o modo debug estiver ativado, false caso contrário.
     */
    public static boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Loga mensagens de debug se o modo debug estiver ativado.
     *
     * @param message A mensagem a ser logada.
     */
    public static void logDebug(String message) {
        if (debugMode) {
            Bukkit.getLogger().info("[DEBUG] " + message);
        }
    }

    /**
     * Loga mensagens informativas.
     *
     * @param message A mensagem a ser logada.
     */
    public static void log(String message) {
        Bukkit.getLogger().info("[DIAGNOSTIC] " + message);
    }

    /**
     * Obtém o TPS atual do servidor usando o monitoramento manual.
     *
     * @return Uma string formatada com o TPS médio.
     */
    public static String getTPS() {
        return "TPS Atual: " + df.format(getTPS(100)) + ", 5 min: " + df.format(getTPS(300)) + ", 15 min: " + df.format(getTPS(600));
    }

    /**
     * Calcula o TPS médio para um intervalo de tempo específico.
     *
     * @param ticks O número de ticks para calcular.
     * @return O TPS médio.
     */
    private static double getTPS(int ticks) {
        if (tickCount < ticks) {
            return 20.0;
        }

        int target = (tickCount - 1 - ticks) % tickTimes.length;
        long elapsed = System.currentTimeMillis() - tickTimes[target];

        return ticks / (elapsed / 1000.0);
    }

    /**
     * Obtém informações de uso de memória.
     *
     * @return Uma string com o uso de memória.
     */
    public static String getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        long freeMemory = runtime.freeMemory();
        return String.format("Memória Usada: %d MB, Disponível: %d MB, Máximo: %d MB",
                usedMemory / 1024 / 1024,
                freeMemory / 1024 / 1024,
                maxMemory / 1024 / 1024);
    }

    /**
     * Obtém informações do sistema operacional.
     *
     * @return Uma string com informações sobre o sistema.
     */
    public static String getSystemInfo() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        return String.format("Sistema: %s %s, Núcleos: %d, Load Average: %.2f",
                osBean.getName(),
                osBean.getVersion(),
                osBean.getAvailableProcessors(),
                osBean.getSystemLoadAverage());
    }
}