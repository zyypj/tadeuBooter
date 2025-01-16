package com.github.zyypj.booter.updater;

import com.github.zyypj.booter.http.Request;

import java.io.*;
import java.net.URL;
import java.util.logging.Logger;

public class AutoUpdater {

    private final String currentVersion;
    private final String updateCheckUrl;
    private final String downloadUrl;
    private final File pluginFile;
    private final Logger logger;

    /**
     * @param currentVersion   A versão atual do plugin.
     * @param updateCheckUrl   A URL para verificar a versão mais recente.
     * @param downloadUrl      A URL para baixar o novo arquivo.
     * @param pluginFile       O arquivo atual do plugin (JAR).
     * @param logger           O logger para saída de logs.
     */
    public AutoUpdater(String currentVersion, String updateCheckUrl, String downloadUrl, File pluginFile, Logger logger) {
        this.currentVersion = currentVersion;
        this.updateCheckUrl = updateCheckUrl;
        this.downloadUrl = downloadUrl;
        this.pluginFile = pluginFile;
        this.logger = logger;
    }

    /**
     * Inicia o processo de verificação e atualização.
     */
    public void checkAndUpdate() {
        try {
            logger.info("Verificando atualizações...");

            String latestVersion = getLatestVersion();
            if (latestVersion == null) {
                logger.warning("Não foi possível verificar a versão mais recente.");
                return;
            }

            if (isUpToDate(latestVersion)) {
                logger.info("O plugin já está na versão mais recente: " + currentVersion);
                return;
            }

            logger.info("Nova versão encontrada: " + latestVersion + ". Iniciando atualização...");

            if (downloadUpdate()) {
                logger.info("Atualização concluída com sucesso. Reinicie o servidor para aplicar.");
            } else {
                logger.warning("Falha ao baixar a atualização.");
            }

        } catch (Exception e) {
            logger.severe("Erro durante o processo de atualização: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Verifica a versão mais recente no servidor.
     *
     * @return A versão mais recente, ou null se houver falha.
     */
    private String getLatestVersion() {
        Request<String> request = new Request<>(updateCheckUrl);
        if (request.execute()) {
            return request.getResponse().getRawData().trim();
        }
        return null;
    }

    /**
     * Verifica se a versão atual está atualizada.
     *
     * @param latestVersion A versão mais recente disponível.
     * @return true se estiver atualizado, false caso contrário.
     */
    private boolean isUpToDate(String latestVersion) {
        return currentVersion.equalsIgnoreCase(latestVersion);
    }

    /**
     * Baixa a nova versão do plugin.
     *
     * @return true se o download for bem-sucedido, false caso contrário.
     */
    private boolean downloadUpdate() {
        try (BufferedInputStream in = new BufferedInputStream(new URL(downloadUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(new File(pluginFile.getParent(), "update-" + pluginFile.getName()))) {

            byte[] dataBuffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }

            logger.info("Atualização baixada com sucesso para: " + pluginFile.getParent());
            return true;
        } catch (IOException e) {
            logger.severe("Erro ao baixar a atualização: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}