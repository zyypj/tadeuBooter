package com.github.zyypj.tadeuBooter.api.minecraft.logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Debug {

    private static JavaPlugin plugin;
    private static String prefix = "§8§l[DEBUG] §f";

    /**
     * Define o JavaPlugin usado pelo sistema de debug.
     *
     * @param javaPlugin O plugin principal.
     */
    public static void setPlugin(JavaPlugin javaPlugin) {
        plugin = javaPlugin;
    }

    /**
     * Define o prefixo das mensagens de debug.
     *
     * @param newPrefix O novo prefixo a ser usado.
     */
    public static void setPrefix(String newPrefix) {
        prefix = ChatColor.translateAlternateColorCodes('&', newPrefix);
    }

    /**
     * Envia uma mensagem de debug para o console.
     *
     * @param message A mensagem a ser enviada.
     * @param debug   Indica se a mensagem é de debug ou normal.
     */
    public static void log(String message, boolean debug) {
        if (plugin == null) {
            throw new IllegalStateException("§4O plugin não foi definido. Use Debug.setPlugin(JavaPlugin) antes de usar o debug. (MANDE ESSE ERRO PARA O DEVELOPER!)");
        }

        message = ChatColor.translateAlternateColorCodes('&', message);

        if (debug) {
            if (plugin.getConfig().getBoolean("debug", true)) {
                Bukkit.getConsoleSender().sendMessage(prefix + message);
            }
            return;
        }

        Bukkit.getConsoleSender().sendMessage(message);
    }
}