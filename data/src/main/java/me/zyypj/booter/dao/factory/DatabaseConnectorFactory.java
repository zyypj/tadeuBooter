package me.zyypj.booter.dao.factory;

import me.zyypj.booter.dao.config.DatabaseConfig;
import me.zyypj.booter.dao.connector.DatabaseConnector;
import me.zyypj.booter.dao.connector.HikariDatabaseConnector;
import me.zyypj.booter.dao.connector.MySQLDatabaseConnector;
import me.zyypj.booter.dao.connector.SQLiteDatabaseConnector;

/**
 * Fábrica responsável por criar a instância correta de DatabaseConnector com base na configuração
 * informada.
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
                throw new IllegalArgumentException(
                        "Unsupported database type: " + config.getType());
        }
    }
}
