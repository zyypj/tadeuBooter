package com.github.zyypj.tadeuBooter.api.minecraft.cooldown

import org.bukkit.entity.Player
import java.util.HashMap
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Controlador de Cooldowns para jogadores no Bukkit.
 * Permite criar, verificar e gerenciar cooldowns associados a jogadores.
 */
class CooldownController private constructor() {

    private val cooldowns: HashMap<String, Long> = HashMap()

    /**
     * Cria um cooldown associado a uma chave.
     * @param key A chave do cooldown.
     * @param seconds A duração do cooldown em segundos.
     */
    fun createCooldown(key: String, seconds: Int) {
        val expiresAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds.toLong())
        cooldowns[key] = expiresAt
    }

    /**
     * Remove um cooldown associado a uma chave.
     * @param key A chave do cooldown a ser removido.
     */
    fun deleteCooldown(key: String) {
        cooldowns.remove(key)
    }

    /**
     * Obtém o tempo restante de um cooldown em milissegundos ou 0 se não existir.
     * @param key A chave do cooldown.
     * @return Tempo restante em milissegundos.
     */
    fun getCooldown(key: String): Long {
        val expiresAt = cooldowns[key] ?: return 0
        return expiresAt - System.currentTimeMillis()
    }

    /**
     * Verifica se um cooldown ainda está ativo.
     * @param key A chave do cooldown.
     * @return true se ativo, false caso contrário.
     */
    fun isInCooldown(key: String): Boolean = getCooldown(key) > 0

    companion object {
        private val controllers: HashMap<UUID, CooldownController> = HashMap()

        /**
         * Obtém ou cria o controlador de cooldowns de um jogador.
         * Em Java, pode chamar diretamente como CooldownController.getCooldownController(player).
         */
        @JvmStatic
        fun getCooldownController(player: Player): CooldownController =
            controllers.getOrPut(player.uniqueId) { CooldownController() }

        /**
         * Remove o controlador de cooldowns associado a um jogador.
         * Em Java, pode chamar diretamente como CooldownController.removeCooldownController(player).
         */
        @JvmStatic
        fun removeCooldownController(player: Player) {
            controllers.remove(player.uniqueId)
        }
    }
}