package com.github.zyypj.tadeuBooter.api.database;

import com.github.zyypj.tadeuBooter.api.database.config.DatabaseConfig;
import com.github.zyypj.tadeuBooter.api.database.factory.DatabaseConnectorFactory;
import com.github.zyypj.tadeuBooter.api.database.connector.DatabaseConnector;
import com.github.zyypj.tadeuBooter.api.database.json.DatabaseJsonConfigLoader;
import java.io.File;
import java.sql.Connection;

/**
 * Gerenciador central para obter conexões com o banco de dados.
 */
public class DatabaseManager {
    private final DatabaseConnector connector;

    public DatabaseManager(DatabaseConfig config) {
        this.connector = DatabaseConnectorFactory.create(config);
    }

    /**
     * Obtém uma conexão com o banco de dados.
     *
     * @return um objeto Connection
     * @throws Exception se ocorrer erro ao conectar
     */
    public Connection getConnection() throws Exception {
        return connector.connect();
    }

    /*
     * Esse é um exemplo básico de como usar o gerenciador de banco de dados.
     */
    public static void main(String[] args) {
        try {
            File configFile = new File("database-config.json");
            DatabaseConfig config = DatabaseJsonConfigLoader.loadConfig(configFile);

            DatabaseManager manager = new DatabaseManager(config);
            Connection connection = manager.getConnection();
            System.out.println("Conexão estabelecida com sucesso: " + connection);
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}