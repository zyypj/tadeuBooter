package com.github.zyypj.tadeuBooter.minecraft.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class YAML extends YamlConfiguration {
    private final Plugin plugin;
    private final File configFile;

    public YAML(String name, Plugin plugin) throws IOException, InvalidConfigurationException {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), name.endsWith(".yml") ? name : name + ".yml");
        this.loadConfig();
    }

    public void save() {
        try {
            this.save(this.configFile);
        } catch (IOException e) {
            plugin.getServer().getConsoleSender().sendMessage("§cOcorreu um erro ao salvar o arquivo " + this.configFile.getName() + ": " + e);
        }
    }

    public void reload() {
        try {
            this.loadConfig();
        } catch (IOException e) {
            plugin.getServer().getConsoleSender().sendMessage("§cOcorreu um erro ao criar o arquivo " + this.configFile.getName() + ": " + e);
        } catch (InvalidConfigurationException e) {
            plugin.getServer().getConsoleSender().sendMessage("§cO arquivo " + this.configFile.getName() + " é inválido: " + e);
        }

    }

    private void loadConfig() throws IOException, InvalidConfigurationException {
        if (!this.plugin.getDataFolder().exists()) {
            this.plugin.getDataFolder().mkdir();
        }

        if (!this.configFile.exists()) {
            try {
                this.plugin.saveResource(this.configFile.getName(), false);
            } catch (IllegalArgumentException var2) {
                this.configFile.createNewFile();
            }
        }

        this.load(this.configFile);
    }

    public void set(String path, Object value, boolean save) {
        this.set(path, value);
        if (save) {
            this.save();
        }
    }

    public void setDefault(String path, Object value) {
        if (!this.contains(path)) {
            this.set(path, value);
            this.save();
        }
    }

    public void createDefaults() {
        if (!this.configFile.exists()) {
            this.plugin.saveResource(this.configFile.getName(), false);
        }
    }

    public String getString(String path, boolean translateColors) {
        return this.getString(path, null, translateColors);
    }

    public String getString(String path, String defaultValue, boolean translateColors) {
        String value = this.getString(path, defaultValue);
        return value != null && translateColors ? ChatColor.translateAlternateColorCodes('&', value) : value;
    }

    @Override
    public int getInt(String path, int defaultValue) {
        return super.getInt(path, defaultValue);
    }

    @Override
    public double getDouble(String path, double defaultValue) {
        return super.getDouble(path, defaultValue);
    }

    @Override
    public boolean getBoolean(String path, boolean defaultValue) {
        return super.getBoolean(path, defaultValue);
    }

    public List<String> getStringList(String path, boolean translateColors) {
        List<String> list = this.getStringList(path);
        if (translateColors && list != null) {
            return list.stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList());
        }
        return list;
    }

    public void create() {
        if (!this.configFile.exists()) {
            this.plugin.saveResource(this.configFile.getName(), false);
        }
    }
}