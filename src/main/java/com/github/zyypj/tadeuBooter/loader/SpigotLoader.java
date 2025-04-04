package com.github.zyypj.tadeuBooter.loader;

import com.github.zyypj.tadeuBooter.api.file.Configurable;
import com.github.zyypj.tadeuBooter.api.file.YAML;
import com.github.zyypj.tadeuBooter.api.minecraft.logger.Debug;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Getter
public abstract class SpigotLoader extends JavaPlugin {

    private YAML configuration;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private String prefix = "&a&l[Desconhecido]&f";

    @Override
    public void onLoad() {
        whenLoad();
    }

    @Override
    public void onEnable() {
        if (this instanceof Configurable) {
            Configurable configurable = (Configurable) this;
            try {
                this.configuration = new YAML("configuration.yml", this);
                this.configuration.createDefaults();
                configurable.loadConfig();
            } catch (Exception e) {
                Debug.log("&a[MRK-CORE] &fErro aco carregar configuration.yml: " + e.getMessage(), false);
            }
        }
        Debug.setPlugin(this);
        whenEnable();
    }

    @Override
    public void onDisable() {
        whenDisable();
    }

    public abstract void whenLoad();

    public abstract void whenEnable();

    public abstract void whenDisable();

    public void registerCommand(String name, CommandExecutor executor) {
        getCommand(name).setExecutor(executor);
    }

    public void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    public YAML getConfigurationFile() {
        return configuration;
    }

    public void saveConfig() {
        if (configuration != null) configuration.save();
    }

    public void reloadConfig() {
        if (configuration != null) configuration.reload();
    }

    public void log(String message) {
        if (configuration.getBoolean("debug")) {
            message = prefix + " " + message;
            getServer().getConsoleSender().sendMessage(message.replace("&", "§"));
        }
    }

    public void log(String... messages) {
        for (String msg : messages) log(msg);
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix.replace("&", "§");
    }

    public void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    public void callEvents(Event... events) {
        for (Event event : events) callEvent(event);
    }

    public File getConfigFile(String name) {
        return new File(getDataFolder(), name.endsWith(".yml") ? name : name + ".yml");
    }

    public FileConfiguration getConfigOf(String name) {
        return YamlConfiguration.loadConfiguration(getConfigFile(name));
    }

    public Server server() {
        return getServer();
    }

    public PluginManager pluginManager() {
        return getServer().getPluginManager();
    }

    public void runLater(Runnable runnable, long seconds) {
        Bukkit.getScheduler().runTaskLater(this, runnable, seconds * 20L);
    }

    public void startRepeatingRunnable(Runnable runnable, long ticks) {
        Bukkit.getScheduler().runTaskTimer(this, runnable, 0L, ticks);
    }

    public void saveResourceFile(String fileName, boolean replace) {
        if (fileName == null || fileName.isEmpty()) return;

        fileName = fileName.replace('\\', '/');
        InputStream in = getResource(fileName);
        if (in == null) return;

        File outFile = new File(getDataFolder(), fileName);
        File outDir = new File(getDataFolder(), fileName.substring(0, Math.max(fileName.lastIndexOf('/'), 0)));
        if (!outDir.exists()) outDir.mkdirs();

        try {
            if (outFile.exists() && !replace) return;

            try (OutputStream out = Files.newOutputStream(outFile.toPath())) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }
        } catch (IOException e) {
            Debug.log("&a[MRK-CORE] &fErro ao salvar o arquivo " + fileName + ": " + e.getMessage(), false);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                Debug.log("&a[MRK-CORE] &fErro ao fechar o InputStream: " + e.getMessage(), false);
            }
        }
    }
}