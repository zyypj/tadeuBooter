package com.github.zyypj.tadeuBooter.api.file;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class YAML extends YamlConfiguration {

    private final Plugin plugin;
    private final File configFile;

    public YAML(String name, Plugin plugin) throws IOException, InvalidConfigurationException {
        this(name, plugin, plugin.getDataFolder());
    }

    public YAML(String name, Plugin plugin, File folder) throws IOException, InvalidConfigurationException {
        this.plugin = plugin;
        if (folder == null) folder = plugin.getDataFolder();
        if (!folder.exists()) folder.mkdirs();

        this.configFile = new File(folder, name.endsWith(".yml") ? name : name + ".yml");
        this.loadConfig();
    }

    private void loadConfig() throws IOException, InvalidConfigurationException {
        if (!this.configFile.exists()) {
            try {
                plugin.saveResource(this.configFile.getName(), false);
            } catch (IllegalArgumentException ex) {
                this.configFile.createNewFile();
            }
        }
        this.load(this.configFile);
    }

    public void saveDefaultConfig() {
        if (!this.configFile.exists()) {
            if (plugin.getResource(this.configFile.getName()) != null) {
                plugin.saveResource(this.configFile.getName(), false);
            } else {
                try {
                    this.configFile.getParentFile().mkdirs();
                    this.configFile.createNewFile();
                } catch (IOException e) {
                    logError("Erro ao criar o arquivo " + this.configFile.getName(), e);
                }
            }
        }
    }

    public void save() {
        try {
            this.save(this.configFile);
        } catch (IOException e) {
            logError("Erro ao salvar o arquivo " + this.configFile.getName(), e);
        }
    }

    public void reload() {
        try {
            this.loadConfig();
        } catch (IOException | InvalidConfigurationException e) {
            logError("Erro ao recarregar o arquivo " + this.configFile.getName(), e);
        }
    }

    public void set(String path, Object value, boolean save) {
        this.set(path, value);
        if (save) this.save();
    }

    public void setDefault(String path, Object value) {
        if (!this.contains(path)) {
            this.set(path, value);
            this.save();
        }
    }

    public void createDefaults() {
        if (!this.configFile.exists()) {
            plugin.saveResource(this.configFile.getName(), false);
        }
    }

    public void create() {
        if (!this.configFile.exists()) {
            plugin.saveResource(this.configFile.getName(), false);
        }
    }

    public boolean exists() {
        return this.configFile.exists();
    }

    public boolean delete() {
        return this.configFile.delete();
    }

    public void backup(String suffix) {
        File backupFile = new File(configFile.getParent(), configFile.getName() + "." + suffix + ".backup");
        try {
            Files.copy(configFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            plugin.getLogger().warning("Não foi possível criar o backup de: " + configFile.getName());
        }
    }

    public String getString(String path, boolean translateColors) {
        return getString(path, null, translateColors);
    }

    public String getString(String path, String defaultValue, boolean translateColors) {
        String value = this.getString(path, defaultValue);
        return value != null && translateColors ? ChatColor.translateAlternateColorCodes('&', value) : value;
    }

    public List<String> getStringList(String path, boolean translateColors) {
        List<String> list = this.getStringList(path);
        if (translateColors && list != null) {
            return list.stream()
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .collect(Collectors.toList());
        }
        return list;
    }

    public <T> T getOrSet(String path, T defaultValue) {
        if (!this.contains(path)) {
            this.set(path, defaultValue);
            this.save();
        }
        return (T) this.get(path);
    }

    public String getFormatted(String path, Object... args) {
        String raw = getString(path, true);
        return raw != null ? String.format(raw, args) : null;
    }

    public List<String> getKeysRecursive(String path) {
        org.bukkit.configuration.ConfigurationSection section = this.getConfigurationSection(path);
        if (section == null) return Collections.emptyList();
        return new ArrayList<>(section.getKeys(true));
    }

    private void logError(String msg, Throwable t) {
        plugin.getLogger().severe("§c[YAML] " + msg);
        t.printStackTrace();
    }
}