package me.zyypj.booter.dao.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import me.zyypj.booter.dao.config.SQLiteConfig;

/** Implementação do DatabaseConnector para SQLite. */
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
