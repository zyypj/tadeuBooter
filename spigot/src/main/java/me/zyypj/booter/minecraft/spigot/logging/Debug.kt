package me.zyypj.booter.minecraft.spigot.logging

import me.zyypj.booter.minecraft.spigot.logging.Debug.setPlugin
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin

/**
 * Utilitário para log de mensagens de debug no console do servidor.
 */
object Debug {

    private var plugin: JavaPlugin? = null
    private var debugPrefix: String = "§8§l[DEBUG] §f"
    private var prefix: String = "§7[Booter] §f"

    /**
     * Define o plugin que será usado para obter configurações de debug.
     *
     * @param javaPlugin Instância do seu JavaPlugin principal.
     */
    @JvmStatic
    fun setPlugin(javaPlugin: JavaPlugin) {
        plugin = javaPlugin
    }

    /**
     * Configura o prefixo aplicado em todas mensagens.
     *
     * @param newPrefix Texto de prefixo com códigos ‘&’ para cores.
     */
    @JvmStatic
    fun setPrefix(newPrefix: String) {
        prefix = ChatColor.translateAlternateColorCodes('&', newPrefix)
    }

    /**
     * Configura o prefixo aplicado apenas em mensagens de debug.
     *
     * @param newPrefix Texto de prefixo com códigos ‘&’ para cores.
     */
    @JvmStatic
    fun setDebugPrefix(newPrefix: String) {
        debugPrefix = ChatColor.translateAlternateColorCodes('&', newPrefix)
    }

    /**
     * Envia uma mensagem para o console.
     *
     * Se [debug] for true, só será exibida caso a chave `debug` no config.yml esteja
     * como true, e receberá o prefixo configurado. Caso contrário, é exibida sem prefixo.
     *
     * @param message Texto da mensagem, com possíveis códigos ‘&’ para cores.
     * @param debug   Se true → mensagem de debug; se false → mensagem normal.
     *
     * @throws IllegalStateException se [setPlugin] não tiver sido chamado antes.
     */
    @JvmStatic
    fun log(message: String, debug: Boolean) {
        val pluginInstance = plugin
            ?: throw IllegalStateException(
                "§4O plugin não foi definido. Use Debug.setPlugin(JavaPlugin) antes de usar o debug. (MANDE ESSE ERRO PARA O DESENVOLVEDOR DO PLUGIN!)"
            )

        val translated = ChatColor.translateAlternateColorCodes('&', message)

        if (debug) {
            if (pluginInstance.config.getBoolean("debug", true)) {
                Bukkit.getConsoleSender().sendMessage(debugPrefix + translated)
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(prefix + translated)
        }
    }
}