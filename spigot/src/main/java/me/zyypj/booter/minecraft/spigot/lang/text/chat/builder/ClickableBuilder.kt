package me.zyypj.booter.minecraft.spigot.lang.text.chat.builder

import me.zyypj.booter.minecraft.spigot.lang.text.chat.impl.ClickableMessage
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.entity.Player

/**
 * Builder para criar instâncias de ClickableMessage.
 */
class ClickableBuilder {
    private val components = mutableListOf<TextComponent>()

    companion object {
        /** Inicia um builder vazio. */
        @JvmStatic
        fun builder() = ClickableBuilder()

        /** Inicia o builder com texto simples. */
        @JvmStatic
        fun of(text: String) = builder().add(text)
    }

    /** Adiciona texto simples. */
    fun add(text: String): ClickableBuilder {
        val comp = TextComponent(ChatColor.translateAlternateColorCodes('&', text))
        components += comp
        return this
    }

    /** Adiciona texto com hover e clique customizáveis. */
    fun add(
        text: String,
        hover: String? = null,
        clickAction: ClickEvent.Action? = null,
        clickValue: String? = null
    ): ClickableBuilder {
        val comp = TextComponent(ChatColor.translateAlternateColorCodes('&', text))
        hover?.let {
            comp.hoverEvent = HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                ComponentBuilder(ChatColor.translateAlternateColorCodes('&', it)).create()
            )
        }
        if (clickAction != null && clickValue != null) {
            comp.clickEvent = ClickEvent(clickAction, clickValue)
        }
        components += comp
        return this
    }

    /** Conveniência: adiciona comando executável. */
    fun runCommand(text: String, hover: String? = null, command: String) =
        add(text, hover, ClickEvent.Action.RUN_COMMAND, command)

    /** Conveniência: adiciona sugestão de comando. */
    fun suggestCommand(text: String, hover: String? = null, suggestion: String) =
        add(text, hover, ClickEvent.Action.SUGGEST_COMMAND, suggestion)

    /** Conveniência: adiciona link externo. */
    fun openUrl(text: String, hover: String? = null, url: String) =
        add(text, hover, ClickEvent.Action.OPEN_URL, url)

    /** Constrói uma instância de ClickableMessage pronta para envio. */
    fun build(): ClickableMessage = ClickableMessage(components)

    /** Cria e envia diretamente ao jogador. */
    fun sendTo(player: Player) = build().sendTo(player)

    /** Cria e envia como linhas ao jogador. */
    fun sendLines(player: Player) = build().sendAsLines(player)
}