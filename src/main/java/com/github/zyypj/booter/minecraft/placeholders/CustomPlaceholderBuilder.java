package com.github.zyypj.booter.minecraft.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * API para criar placeholders personalizados para PlaceholderAPI.
 */
public class CustomPlaceholderBuilder extends PlaceholderExpansion {

    private final String identifier;
    private final String author;
    private final String version;
    private final Map<String, Function<Player, String>> placeholders;

    /**
     * Construtor para criar a API de placeholders personalizados.
     *
     * @param identifier O identificador principal do plugin.
     * @param author O autor do plugin.
     * @param version A versão do plugin.
     */
    public CustomPlaceholderBuilder(String identifier, String author, String version) {
        this.identifier = identifier;
        this.author = author;
        this.version = version;
        this.placeholders = new HashMap<>();
    }

    /**
     * Registra um novo placeholder.
     *
     * @param placeholder O nome do placeholder (sem o prefixo `%identifier%`).
     * @param function A função que retorna o valor do placeholder.
     */
    public void registerPlaceholder(String placeholder, Function<Player, String> function) {
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
        if (player == null) {
            return null;
        }

        Function<Player, String> function = placeholders.get(params.toLowerCase());
        return function != null ? function.apply(player) : null;
    }

//      EXEMPLO DE UTILIZAÇÃO
//    public static void initializeExample() {
//        CustomPlaceholderAPI placeholderAPI = new CustomPlaceholderAPI("myplugin", "tadeu", "1.0");
//
//        placeholderAPI.registerPlaceholder("player_name", Player::getName);
//        placeholderAPI.registerPlaceholder("player_health", player -> String.format("%.2f", player.getHealth()));
//        placeholderAPI.registerPlaceholder("online_players", player -> String.valueOf(player.getServer().getOnlinePlayers().size()));
//        placeholderAPI.register();
//
//    }
}