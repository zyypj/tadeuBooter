package com.github.zyypj.tadeuBooter.api.downloaders.dependencies;

import com.google.common.base.Stopwatch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;

/**
 * Essa classe foi inspirada numa implementação já existente!
 *
 * @author syncwrld (github.com/syncwrld)
 */
@Getter
@RequiredArgsConstructor
public class Dependency {
    @Setter
    private static JavaPlugin plugin;

    @Setter
    private static String prefix = "§8§l[DependencyLoader] §f";

    private final DependencyInfo dependencyInfo;
    private long bytesSize = 0L;

    public CompletableFuture<Void> download() {
        return CompletableFuture.runAsync(() -> {
            if (plugin == null) {
                throw new IllegalStateException("O plugin não foi definido. Use Dependency.setPlugin(JavaPlugin) antes de usar essa função.");
            }

            if (Bukkit.getPluginManager().getPlugin(dependencyInfo.getName()) != null) return;

            File pluginFile = new File(plugin.getDataFolder().getParentFile(),
                    dependencyInfo.getName().toLowerCase() + "-" + dependencyInfo.getVersion() + ".jar");

            if (pluginFile.exists()) {
                log("§eDependência já existe: " + dependencyInfo.getName() + " v" + dependencyInfo.getVersion(), true);
                return;
            }

            Stopwatch stopwatch = Stopwatch.createStarted();
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(dependencyInfo.getDownloadURL()).openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(30000);
                connection.connect();

                try (InputStream inputStream = connection.getInputStream()) {
                    Files.copy(inputStream, pluginFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

                this.bytesSize = pluginFile.length();
                log(String.format("§aDownloaded: %s v%s - Time: %s - Size: %s",
                        dependencyInfo.getName(), dependencyInfo.getVersion(), stopwatch, asReadableSize(this.bytesSize)), true);

                plugin.getServer().getScheduler().runTask(plugin, () -> loadPlugin(pluginFile));

            } catch (IOException e) {
                log(String.format("§cFailed to download %s v%s - Cause: %s",
                        dependencyInfo.getName(), dependencyInfo.getVersion(), e.getMessage()), true);
            }
        });
    }

    private void loadPlugin(File pluginFile) {
        try {
            Plugin loadedPlugin = Bukkit.getPluginManager().loadPlugin(pluginFile);
            if (loadedPlugin != null) {
                Bukkit.getPluginManager().enablePlugin(loadedPlugin);
            }
            if (dependencyInfo.getName().equalsIgnoreCase("PlaceholderAPI")) {
                log("§cPlaceholderAPI installed! Restart the server to avoid errors.", true);
            }
        } catch (Exception e) {
            log(String.format("§cFailed to start dependency: %s - Cause: %s", dependencyInfo.getName(), e.getMessage()), true);
        }
    }

    private void log(String message, boolean debug) {
        if (plugin == null) {
            throw new IllegalStateException("O plugin não foi definido. Use Dependency.setPlugin(JavaPlugin) antes de usar essa função.");
        }

        message = ChatColor.translateAlternateColorCodes('&', message);

        if (debug && plugin.getConfig().getBoolean("debug", true)) {
            Bukkit.getConsoleSender().sendMessage(prefix + message);
            return;
        }

        Bukkit.getConsoleSender().sendMessage(message);
    }

    private static String asReadableSize(long byteSize) {
        if (byteSize < 1024) return byteSize + " B";
        if (byteSize < 1048576) return byteSize / 1024 + " KB";
        if (byteSize < 1073741824) return byteSize / 1048576 + " MB";
        return byteSize / 1073741824 + " GB";
    }
}