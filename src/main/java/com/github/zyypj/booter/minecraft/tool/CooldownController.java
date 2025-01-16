package com.github.zyypj.booter.minecraft.tool;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;

/**
 * Controlador de Cooldowns para jogadores no Bukkit.
 * Permite criar, verificar e gerenciar cooldowns associados a jogadores.
 */
public class CooldownController {

    private final HashMap<String, Long> cooldowns = new HashMap<>();
    private static final Map<UUID, CooldownController> CONTROLLER = new HashMap<>();

    /**
     * Cria um cooldown associado a uma chave.
     *
     * @param key     A chave do cooldown.
     * @param seconds A duração do cooldown em segundos.
     */
    public void createCooldown(String key, int seconds) {
        this.cooldowns.put(key, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds));
    }

    /**
     * Remove um cooldown associado a uma chave.
     *
     * @param key A chave do cooldown a ser removido.
     */
    public void deleteCooldown(String key) {
        this.cooldowns.remove(key);
    }

    /**
     * Obtém o tempo restante de um cooldown associado a uma chave.
     *
     * @param key A chave do cooldown.
     * @return O tempo restante em milissegundos ou 0 se o cooldown não existir.
     */
    public long getCooldown(String key) {
        return this.cooldowns.getOrDefault(key, 0L) - System.currentTimeMillis();
    }

    /**
     * Verifica se um cooldown associado a uma chave ainda está ativo.
     *
     * @param key A chave do cooldown.
     * @return true se o cooldown estiver ativo, caso contrário false.
     */
    public boolean isInCooldown(String key) {
        return this.getCooldown(key) > 0;
    }

    /**
     * Remove o controlador de cooldowns associado a um jogador.
     *
     * @param player O jogador cujo controlador será removido.
     */
    public static void removeCooldownController(Player player) {
        CONTROLLER.remove(player.getUniqueId());
    }

    /**
     * Obtém o controlador de cooldowns de um jogador.
     *
     * @param player O jogador para o qual o controlador será retornado.
     * @return O controlador de cooldowns do jogador.
     */
    public static CooldownController getCooldownController(Player player) {
        return CONTROLLER.computeIfAbsent(player.getUniqueId(), uuid -> new CooldownController());
    }
}