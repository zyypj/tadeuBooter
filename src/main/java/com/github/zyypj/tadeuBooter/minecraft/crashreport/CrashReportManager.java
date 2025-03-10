package com.github.zyypj.tadeuBooter.minecraft.crashreport;

import com.github.zyypj.tadeuBooter.diagnostics.DiagnosticTools;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrashReportManager {

    private static final String HEADER = "# ESSA LOG FOI SALVA PELO TADEUBOOTER\n" +
            "# USE EM GITHUB.COM/ZYYPJ/TADEUBOOTER\n\n";
    private static final String REPORT_DIRECTORY = "plugins/crash-report/";

    private final JavaPlugin plugin;

    public CrashReportManager(JavaPlugin plugin) {
        this.plugin = plugin;
        createReportDirectory();
    }

    private void createReportDirectory() {
        File dir = new File(REPORT_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Gera um relatório de crash com informações detalhadas e salva em um arquivo.
     *
     * @param throwable A exceção que causou o crash.
     */
    public void generateCrashReport(Throwable throwable) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String fileName = REPORT_DIRECTORY + "crash_report_" + timestamp + ".txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println(HEADER);
            writer.println("Timestamp: " + timestamp);
            writer.println();

            writer.println("TPS: " + DiagnosticTools.getTPS());
            writer.println("Memory Usage: " + DiagnosticTools.getMemoryUsage());
            writer.println("System Info: " + DiagnosticTools.getSystemInfo());
            writer.println();

            writer.println("Exception: " + throwable.toString());
            writer.println("Stack Trace:");
            throwable.printStackTrace(writer);
            writer.flush();

            Bukkit.getLogger().info("[CrashReportManager] Relatório de crash gerado: " + fileName);
        } catch (IOException e) {
            Bukkit.getLogger().severe("[CrashReportManager] Erro ao gerar relatório de crash: " + e.getMessage());
        }
    }
}