package me.zyypj.booter.minecraft.spigot.lang.text.utils.response

import me.zyypj.booter.minecraft.spigot.file.YAML

data class ResponseMessages(
    val cancel: String,
    val integerError: String,
    val doubleError: String,
    val stringError: String,
    val confirmationError: String
) {
    companion object {
        private val DEFAULT = ResponseMessages(
            cancel = "§cAção cancelada!",
            integerError = "§cVocê deve digitar um número válido.",
            doubleError = "§cVocê deve digitar um número decimal válido.",
            stringError = "§cEntrada inválida.",
            confirmationError = "§cVocê deve digitar 'sim' para confirmar ou 'não' para cancelar."
        )

        fun fromYaml(yaml: YAML, basePath: String = "responses"): ResponseMessages {
            yaml.saveDefaultConfig()
            return ResponseMessages(
                cancel = yaml.getString("$basePath.cancel", true) ?: DEFAULT.cancel,
                integerError = yaml.getString("$basePath.integerError", true) ?: DEFAULT.integerError,
                doubleError = yaml.getString("$basePath.doubleError", true) ?: DEFAULT.doubleError,
                stringError = yaml.getString("$basePath.stringError", true) ?: DEFAULT.stringError,
                confirmationError = yaml.getString("$basePath.confirmationError", true) ?: DEFAULT.confirmationError
            )
        }

        fun errorFor(type: ResponseWaiter.RequiredType, messages: ResponseMessages): String = when (type) {
            ResponseWaiter.RequiredType.INTEGER -> messages.integerError
            ResponseWaiter.RequiredType.DOUBLE -> messages.doubleError
            ResponseWaiter.RequiredType.STRING -> messages.stringError
            ResponseWaiter.RequiredType.CONFIRMATION -> messages.confirmationError
        }
    }
}