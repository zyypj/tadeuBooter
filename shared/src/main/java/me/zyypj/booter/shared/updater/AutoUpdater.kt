package me.zyypj.booter.shared.updater

import me.zyypj.booter.shared.http.client.HttpRequest
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.util.logging.Logger

class AutoUpdater(
    private val currentVersion: String,   // Versão atual instalada
    private val updateCheckUrl: String,   // Endpoint que retorna a última versão disponível (texto simples)
    private val downloadUrl: String,      // URL do JAR mais recente
    private val pluginFile: File,         // Arquivo .jar atualmente em uso
    private val logger: Logger            // Logger fornecido pelo chamador
) {

    /**
     * Inicia a verificação e, se necessário, realiza o download da atualização.
     * Pode ser invocado de Java com:  new AutoUpdater(...).checkAndUpdate();
     */
    fun checkAndUpdate() {
        try {
            logger.info("Verificando atualizações…")
            val latestVersion = getLatestVersion()
            if (latestVersion == null) {
                logger.warning("Não foi possível verificar a versão mais recente.")
                return
            }

            if (isUpToDate(latestVersion)) {
                logger.info("O plugin já está na versão mais recente: $currentVersion")
                return
            }

            logger.info("Nova versão encontrada: $latestVersion. Iniciando atualização…")
            if (downloadUpdate()) {
                logger.info("Atualização concluída com sucesso. Reinicie o servidor para aplicar.")
            } else {
                logger.warning("Falha ao baixar a atualização.")
            }
        } catch (e: Exception) {
            logger.severe("Erro durante o processo de atualização: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Consulta o servidor e devolve a última versão em formato de string.
     * @return última versão ou *null* caso a requisição falhe.
     */
    private fun getLatestVersion(): String? {
        val request = HttpRequest<String>(updateCheckUrl)
        return if (request.execute()) request.response?.rawData?.trim() else null
    }

    /**
     * Compara a versão local com a versão disponível.
     * A comparação é *case‑insensitive* e sem análise semântica.
     */
    private fun isUpToDate(latestVersion: String): Boolean = currentVersion.equals(latestVersion, ignoreCase = true)

    /**
     * Faz o download do novo JAR para o mesmo diretório do plugin, com prefixo "update-".
     * @return *true* se o arquivo foi baixado sem erros.
     */
    private fun downloadUpdate(): Boolean {
        val targetFile = File(pluginFile.parentFile, "update-${pluginFile.name}")
        return try {
            BufferedInputStream(URL(downloadUrl).openStream()).use { input ->
                FileOutputStream(targetFile).use { output ->
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                    }
                }
            }
            logger.info("Atualização baixada com sucesso para: ${targetFile.absolutePath}")
            true
        } catch (e: IOException) {
            logger.severe("Erro ao baixar a atualização: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}