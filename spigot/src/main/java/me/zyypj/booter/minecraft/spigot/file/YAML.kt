package me.zyypj.booter.minecraft.spigot.file

import org.bukkit.ChatColor
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Extensão de YamlConfiguration que facilita criação, carregamento e manipulação de arquivos YML.
 * Construtores com @JvmOverloads disponíveis para uso em Java.
 */
class YAML @JvmOverloads constructor(
    name: String,
    private val plugin: Plugin,
    folder: File? = null
) : YamlConfiguration() {

    private val configFile: File

    init {
        val dir = folder ?: plugin.dataFolder
        if (!dir.exists() && !dir.mkdirs()) {
            throw IOException("Could not create folder: ${dir.absolutePath}")
        }
        val fileName = if (name.endsWith(".yml")) name else "$name.yml"
        configFile = File(dir, fileName)
        loadConfig()
    }

    @Throws(IOException::class, InvalidConfigurationException::class)
    private fun loadConfig() {
        if (!configFile.exists()) {
            val resource = plugin.getResource(configFile.name)
            if (resource != null) {
                copyResource(configFile.name, configFile)
            } else if (!configFile.createNewFile()) {
                throw IOException("Could not create config file: ${configFile.absolutePath}")
            }
        }
        load(configFile)
    }

    @Throws(IOException::class)
    private fun copyResource(resourcePath: String, destination: File) {
        plugin.getResource(resourcePath)?.use { input ->
            Files.copy(input, destination.toPath(), StandardCopyOption.REPLACE_EXISTING)
        } ?: throw IOException("Resource $resourcePath not found in plugin jar")
    }

    /** Salva recurso ‘default’ se não existir. */
    fun saveDefaultConfig() {
        if (!configFile.exists()) {
            plugin.getResource(configFile.name)?.let {
                try {
                    copyResource(configFile.name, configFile)
                } catch (e: IOException) {
                    logError("Erro ao salvar recurso padrão para o arquivo ${configFile.name}", e)
                }
            } ?: run {
                try {
                    configFile.parentFile?.let { parent ->
                        if (!parent.exists() && !parent.mkdirs())
                            logError(
                                "Não foi possível criar diretório para o arquivo ${configFile.name}",
                                IOException("mkdirs retornou false")
                            )
                    }
                    if (!configFile.createNewFile())
                        logError(
                            "Não foi possível criar o arquivo ${configFile.name}",
                            IOException("createNewFile retornou false")
                        )
                } catch (e: IOException) {
                    logError("Erro ao criar o arquivo ${configFile.name}", e)
                }
            }
        }
    }

    /** Alias de saveDefaultConfig. */
    fun createDefaults() = saveDefaultConfig()

    /** Verifica existência do arquivo. */
    fun exists(): Boolean = configFile.exists()

    /** Deleta o arquivo. */
    fun delete(): Boolean = configFile.delete()

    /** Cria backup com sufixo. */
    fun backup(suffix: String) {
        val backup = File(configFile.parent, "${configFile.name}.$suffix.backup")
        try {
            Files.copy(configFile.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING)
        } catch (e: IOException) {
            plugin.logger.warning("Não foi possível criar o backup de: ${configFile.name}")
        }
    }

    /** Salva alterações no arquivo. */
    fun save() {
        try {
            save(configFile)
        } catch (e: IOException) {
            logError("Erro ao salvar o arquivo ${configFile.name}", e)
        }
    }

    /** Recarrega do disco. */
    fun reload() {
        try {
            loadConfig()
        } catch (e: Exception) {
            logError("Erro ao recarregar o arquivo ${configFile.name}", e)
        }
    }

    /** Ajusta valor em path e salva se indicado. */
    fun set(path: String, value: Any?, save: Boolean) {
        set(path, value)
        if (save) save()
    }

    /** Define default, se não existir, e salva. */
    fun setDefault(path: String, value: Any) {
        if (!contains(path)) {
            set(path, value)
            save()
        }
    }

    /** Obtém string, opcionalmente traduzindo cores `&`. */
    fun getString(path: String, translateColors: Boolean): String? {
        val valRaw = getString(path)
        return if (translateColors && valRaw != null)
            ChatColor.translateAlternateColorCodes('&', valRaw)
        else valRaw
    }

    /** Lista de strings, opcionalmente traduzindo cores. */
    fun getStringList(path: String, translateColors: Boolean): List<String> {
        val list = getStringList(path)
        return if (translateColors)
            list.map { ChatColor.translateAlternateColorCodes('&', it) }
        else list
    }

    /** Retorna T e seta default se não existe. */
    @Suppress("UNCHECKED_CAST")
    fun <T> getOrSet(path: String, default: T): T {
        if (!contains(path)) {
            set(path, default)
            save()
        }
        return get(path) as T
    }

    /** Formata string usando String.format. */
    fun getFormatted(path: String, vararg args: Any): String? {
        val raw = getString(path, true) ?: return null
        return String.format(raw, *args)
    }

    /** Chaves recursivas dentro de uma seção. */
    fun getKeysRecursive(path: String): List<String> {
        val section = getConfigurationSection(path) ?: return emptyList()
        return section.getKeys(true).toList()
    }

    private fun logError(msg: String, t: Throwable) {
        plugin.logger.severe("§c[YAML] $msg")
        t.printStackTrace()
    }
}