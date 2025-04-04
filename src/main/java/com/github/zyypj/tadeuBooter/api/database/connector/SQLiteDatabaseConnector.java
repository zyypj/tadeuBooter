package com.github.zyypj.tadeuBooter.api.database.connector;

import com.github.zyypj.tadeuBooter.api.database.config.SQLiteConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implementação do DatabaseConnector para SQLite.
 */
public class SQLiteDatabaseConnector implements DatabaseConnector {
    private final SQLiteConfig config;

    public SQLiteDatabaseConnector(SQLiteConfig config) {
        this.config = config;
    }

    @Override
    public Connection connect() throws SQLException {
        String url = "jdbc:sqlite:" + config.getFilePath();
        return DriverManager.getConnection(url);
    }
}