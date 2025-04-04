package com.github.zyypj.tadeuBooter.api.database.factory;

import com.github.zyypj.tadeuBooter.api.database.config.DatabaseConfig;
import com.github.zyypj.tadeuBooter.api.database.connector.DatabaseConnector;
import com.github.zyypj.tadeuBooter.api.database.connector.MySQLDatabaseConnector;
import com.github.zyypj.tadeuBooter.api.database.connector.SQLiteDatabaseConnector;
import com.github.zyypj.tadeuBooter.api.database.connector.HikariDatabaseConnector;

/**
 * Fábrica responsável por criar a instância correta de DatabaseConnector
 * com base na configuração informada.
 */
public class DatabaseConnectorFactory {
    public static DatabaseConnector create(DatabaseConfig config) {
        switch (config.getType()) {
            case MYSQL:
                return new MySQLDatabaseConnector(config.getMysql());
            case SQLITE:
                return new SQLiteDatabaseConnector(config.getSqlite());
            case HIKARI:
                return new HikariDatabaseConnector(config.getHikari());
            default:
                throw new IllegalArgumentException("Unsupported database type: " + config.getType());
        }
    }
}