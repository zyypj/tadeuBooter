package me.zyypj.booter.dao.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import me.zyypj.booter.dao.config.DatabaseConfig;

/** Utilitário para carregar e salvar a configuração do banco de dados via JSON. */
public class DatabaseJsonConfigLoader {
    private static final Gson GSON = new GsonBuilder().create();

    /**
     * Carrega a configuração do banco de dados a partir do arquivo JSON.
     *
     * @param file o arquivo de configuração
     * @return um objeto DatabaseConfig
     * @throws IOException se ocorrer erro de leitura
     */
    public static DatabaseConfig loadConfig(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            return GSON.fromJson(reader, DatabaseConfig.class);
        }
    }

    /**
     * Salva a configuração do banco de dados no arquivo JSON.
     *
     * @param file o arquivo de destino
     * @param config o objeto de configuração
     * @throws IOException se ocorrer erro de escrita
     */
    public static void saveConfig(File file, DatabaseConfig config) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(config, writer);
        }
    }
}
