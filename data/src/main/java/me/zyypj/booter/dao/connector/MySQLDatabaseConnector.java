package me.zyypj.booter.dao.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import me.zyypj.booter.dao.config.MySQLConfig;

/** Implementação do DatabaseConnector para MySQL. */
public class MySQLDatabaseConnector implements DatabaseConnector {
    private final MySQLConfig config;

    public MySQLDatabaseConnector(MySQLConfig config) {
        this.config = config;
    }

    @Override
    public Connection connect() throws SQLException {
        String url =
                "jdbc:mysql://"
                        + config.getHost()
                        + ":"
                        + config.getPort()
                        + "/"
                        + config.getDatabase();
        return DriverManager.getConnection(url, config.getUser(), config.getPassword());
    }
}
