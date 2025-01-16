package com.github.zyypj.booter.minecraft.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


/**
 * API para criar placeholders personalizados com suporte a jogadores offline.
 */
public class CustomOfflinePlaceholderBuilder extends PlaceholderExpansion {

    private final String identifier;
    private final String author;
    private final String version;
    private final Map<String, Function<OfflinePlayer, String>> placeholders;

    /**
     * Construtor para criar a API de placeholders com suporte a jogadores offline.
     *
     * @param identifier O identificador principal do plugin.
     * @param author     O autor do plugin.
     * @param version    A versão do plugin.
     */
    public CustomOfflinePlaceholderBuilder(String identifier, String author, String version) {
        this.identifier = identifier;
        this.author = author;
        this.version = version;
        this.placeholders = new HashMap<>();
    }

    /**
     * Registra um novo placeholder.
     *
     * @param placeholder O nome do placeholder (sem o prefixo `%identifier%`).
     * @param function    A função que retorna o valor do placeholder.
     */
    public void registerPlaceholder(String placeholder, Function<OfflinePlayer, String> function) {
        placeholders.put(placeholder.toLowerCase(), function);
    }

    /**
     * Remove um placeholder registrado.
     *
     * @param placeholder O nome do placeholder a ser removido.
     */
    public void unregisterPlaceholder(String placeholder) {
        placeholders.remove(placeholder.toLowerCase());
    }

    /**
     * Limpa todos os placeholders registrados.
     */
    public void clearPlaceholders() {
        placeholders.clear();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return identifier;
    }

    @Override
    public @NotNull String getAuthor() {
        return author;
    }

    @Override
    public @NotNull String getVersion() {
        return version;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player != null) {
            Function<OfflinePlayer, String> function = placeholders.get(params.toLowerCase());
            return function != null ? function.apply(player) : null;
        }
        return null;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        Function<OfflinePlayer, String> function = placeholders.get(params.toLowerCase());
        return function != null ? function.apply(player) : null;
    }

//    public static void initializeExample() {
//        CustomOfflinePlaceholderBuilder placeholderAPI = new CustomOfflinePlaceholderBuilder("myplugin", "tadeu", "1.0");

//        placeholderAPI.registerPlaceholder("player_name", OfflinePlayer::getName);
//        placeholderAPI.registerPlaceholder("is_online", player -> player.isOnline() ? "Online" : "Offline");
//        placeholderAPI.registerPlaceholder("last_seen", player -> {
//            if (player.isOnline()) {
//                return "Agora mesmo";
//            }
//            long lastSeen = player.getLastPlayed();
//            return lastSeen > 0 ? new java.util.Date(lastSeen).toString() : "Nunca logou";
//        });
//
//        placeholderAPI.register();
//    }
}