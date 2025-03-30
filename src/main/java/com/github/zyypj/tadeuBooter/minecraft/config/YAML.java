package com.github.zyypj.tadeuBooter.minecraft.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class YAML extends YamlConfiguration {
    private final Plugin plugin;
    private final File configFile;

    /**
     * Construtor que utiliza o data folder do plugin.
     */
    public YAML(String name, Plugin plugin) throws IOException, InvalidConfigurationException {
        this(name, plugin, plugin.getDataFolder());
    }

    /**
     * Construtor que permite especificar uma pasta customizada para o arquivo de configuração.
     *
     * @param name   Nome do arquivo.
     * @param plugin Instância do plugin.
     * @param folder Pasta onde o arquivo será salvo (pode ser o data folder do plugin, a pasta do servidor, etc.).
     */
    public YAML(String name, Plugin plugin, File folder) throws IOException, InvalidConfigurationException {
        this.plugin = plugin;
        if (folder == null) {
            folder = plugin.getDataFolder();
        }
        if (!folder.exists()) {
            folder.mkdirs();
        }
        this.configFile = new File(folder, name.endsWith(".yml") ? name : name + ".yml");
        this.loadConfig();
    }

    /**
     * Salva o arquivo de configuração com o conteúdo padrão contido no plugin, se o arquivo não existir.
     * Essa implementação é semelhante ao método saveDefaultConfig() do JavaPlugin.
     */
    public void saveDefaultConfig() {
        if (!this.configFile.exists()) {
            if (plugin.getResource(this.configFile.getName()) != null) {
                plugin.saveResource(this.configFile.getName(), false);
            } else {
                try {
                    this.configFile.getParentFile().mkdirs();
                    this.configFile.createNewFile();
                } catch (IOException e) {
                    plugin.getServer().getConsoleSender().sendMessage("§cErro ao criar o arquivo "
                            + this.configFile.getName() + ": " + e);
                }
            }
        }
    }

    /**
     * Salva o arquivo de configuração com o conteúdo padrão contido no plugin, se o arquivo não existir, mas em outro diretório.
     * Essa implementação é semelhante ao método saveDefaultConfig() do JavaPlugin.
     */
    public void saveDefaultConfig(String resourcePath) {
        if (!this.configFile.exists()) {
            InputStream resourceStream = this.plugin.getResource(resourcePath);
            if (resourceStream != null) {
                try {
                    Files.copy(resourceStream, this.configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    this.plugin.getServer().getConsoleSender().sendMessage(
                            "§cErro ao copiar o recurso " + resourcePath + " para " + this.configFile.getName() + ": " + ex.getMessage()
                    );
                }
            } else {
                try {
                    this.configFile.getParentFile().mkdirs();
                    this.configFile.createNewFile();
                } catch (IOException e) {
                    this.plugin.getServer().getConsoleSender().sendMessage(
                            "§cErro ao criar o arquivo " + this.configFile.getName() + ": " + e
                    );
                }
            }
        }
    }

    public void save() {
        try {
            this.save(this.configFile);
        } catch (IOException e) {
            plugin.getServer().getConsoleSender().sendMessage("§cOcorreu um erro ao salvar o arquivo "
                    + this.configFile.getName() + ": " + e);
        }
    }

    public void reload() {
        try {
            this.loadConfig();
        } catch (IOException e) {
            plugin.getServer().getConsoleSender().sendMessage("§cOcorreu um erro ao criar o arquivo "
                    + this.configFile.getName() + ": " + e);
        } catch (InvalidConfigurationException e) {
            plugin.getServer().getConsoleSender().sendMessage("§cO arquivo "
                    + this.configFile.getName() + " é inválido: " + e);
        }
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
            plugin.saveResource(this.configFile.getName(), false);
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
            return list.stream()
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .collect(Collectors.toList());
        }
        return list;
    }

    public void create() {
        if (!this.configFile.exists()) {
            plugin.saveResource(this.configFile.getName(), false);
        }
    }
}
