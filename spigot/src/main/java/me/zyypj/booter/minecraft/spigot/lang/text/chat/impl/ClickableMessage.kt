package me.zyypj.booter.minecraft.spigot.lang.text.chat.impl

import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player

/**
 * Representa mensagem clicável pronta a enviar, criada pelo ClickableBuilder.
 */
class ClickableMessage internal constructor(
    private val components: List<TextComponent>
) {
    /** Converte para array de componentes. */
    fun build(): Array<TextComponent> = components.toTypedArray()

    /** Envia ao jogador como mensagem única. */
    fun sendTo(player: Player) {
        player.spigot().sendMessage(*build())
    }

    /** Envia cada componente em linha separada. */
    fun sendAsLines(player: Player) {
        build().forEach { player.spigot().sendMessage(it) }
    }
}