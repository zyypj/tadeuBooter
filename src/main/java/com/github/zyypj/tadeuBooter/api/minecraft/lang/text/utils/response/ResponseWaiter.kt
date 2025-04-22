package com.github.zyypj.tadeuBooter.api.minecraft.lang.text.utils.response

import com.github.zyypj.tadeuBooter.api.file.YAML
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.math.BigInteger

class ResponseWaiter(
    plugin: JavaPlugin, yaml: YAML = YAML("config", plugin), basePath: String = "responses"
) : Listener {

    private val messages = ResponseMessages.fromYaml(yaml, basePath)
    private val responses = mutableMapOf<Player, ResponseData>()

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun ask(player: Player, type: RequiredType, onComplete: (String) -> Unit) {
        responses[player] = ResponseData(type, onComplete)
    }

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val player = event.player
        val data = responses[player] ?: return

        event.isCancelled = true
        val msg = event.message

        if (msg.equals("cancelar", ignoreCase = true)) {
            responses.remove(player)
            player.sendMessage(messages.cancel)
            return
        }

        if (!data.type.isValid(msg)) {
            player.sendMessage(ResponseMessages.errorFor(data.type, messages))
            return
        }

        responses.remove(player)
        data.onComplete(msg)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        responses.remove(event.player)
    }

    private data class ResponseData(
        val type: RequiredType, val onComplete: (String) -> Unit
    )

    enum class RequiredType {
        INTEGER, DOUBLE, STRING, CONFIRMATION;

        fun isValid(input: String): Boolean = try {
            when (this) {
                INTEGER -> {
                    BigInteger(input); true
                }

                DOUBLE -> {
                    input.toDouble(); true
                }

                CONFIRMATION -> input.equals("sim", ignoreCase = true) || input.equals("não", ignoreCase = true)
                STRING -> input.isNotBlank()
            }
        } catch (_: Exception) {
            false
        }
    }
}