package com.github.zyypj.tadeuBooter.ai;

import com.github.zyypj.tadeuBooter.diagnostics.DiagnosticTools;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnomalyDetectorAI {

    private static final double TPS_THRESHOLD = 15.0;
    private static final int ENTITY_THRESHOLD = 600;
    private static final double MEMORY_USAGE_THRESHOLD = 0.8;

    /**
     * Executa a detecção de anomalias uma única vez.
     *
     * @param plugin Instância do plugin.
     */
    public static void runDetection(JavaPlugin plugin) {
        double currentTPS = parseCurrentTPS(DiagnosticTools.getTPS());
        int totalEntities = countEntities();
        double memoryUsagePercent = parseMemoryUsagePercent(DiagnosticTools.getMemoryUsage());

        StringBuilder report = new StringBuilder();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        report.append("# ESSA LOG FOI SALVA PELO TADEUBOOTER\n")
                .append("# USE EM GITHUB.COM/ZYYPJ/TADEUBOOTER\n\n");
        report.append("Timestamp: ").append(timestamp).append("\n");
        report.append("TPS: ").append(currentTPS).append("\n");
        report.append("Total Entities: ").append(totalEntities).append("\n");
        report.append("Memory Usage: ").append(DiagnosticTools.getMemoryUsage()).append("\n\n");

        boolean anomalyDetected = false;
        if (currentTPS < TPS_THRESHOLD) {
            anomalyDetected = true;
            report.append("Anomaly Detected: TPS below threshold (")
                    .append(currentTPS).append(" < ").append(TPS_THRESHOLD).append(")\n");
        }
        if (totalEntities > ENTITY_THRESHOLD) {
            anomalyDetected = true;
            report.append("Anomaly Detected: Total Entities exceed threshold (")
                    .append(totalEntities).append(" > ").append(ENTITY_THRESHOLD).append(")\n");
        }
        if (memoryUsagePercent > MEMORY_USAGE_THRESHOLD) {
            anomalyDetected = true;
            DecimalFormat df = new DecimalFormat("##.##");
            report.append("Anomaly Detected: High memory usage (")
                    .append(df.format(memoryUsagePercent * 100)).append("% used)\n");
        }

        if (anomalyDetected) {
            saveReport(report.toString());
            notifyAdmins(report.toString());
        } else {
            Bukkit.getLogger().info("[AnomalyDetector] No anomalies detected at " + timestamp);
        }
    }

    private static double parseCurrentTPS(String tpsString) {
        Pattern pattern = Pattern.compile("TPS Atual:\\s*([\\d\\.]+)");
        Matcher matcher = pattern.matcher(tpsString);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                return 20.0;
            }
        }
        return 20.0;
    }

    private static int countEntities() {
        int count = 0;
        for (World world : Bukkit.getWorlds()) {
            count += world.getEntities().size();
        }
        return count;
    }

    private static double parseMemoryUsagePercent(String memoryUsage) {
        try {
            String[] parts = memoryUsage.split(",");
            if (parts.length >= 3) {
                String maxPart = parts[2].trim();
                String usedPart = parts[0].trim();
                int maxMemory = Integer.parseInt(maxPart.split(":")[1].replace("MB", "").trim());
                int usedMemory = Integer.parseInt(usedPart.split(":")[1].replace("MB", "").trim());
                return (double) usedMemory / maxMemory;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private static void saveReport(String reportContent) {
        File dir = new File("plugins/anomaly-report/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = "anomaly_report_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".txt";
        File reportFile = new File(dir, fileName);
        try (FileWriter writer = new FileWriter(reportFile)) {
            writer.write(reportContent);
            Bukkit.getLogger().info("[AnomalyDetector] Report saved to " + reportFile.getAbsolutePath());
        } catch (IOException e) {
            Bukkit.getLogger().severe("[AnomalyDetector] Error saving report: " + e.getMessage());
        }
    }

    private static void notifyAdmins(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("booter.admin")) {
                player.sendMessage("§c[AnomalyDetector] Anomaly detected:\n" + message);
            }
        }
    }
}