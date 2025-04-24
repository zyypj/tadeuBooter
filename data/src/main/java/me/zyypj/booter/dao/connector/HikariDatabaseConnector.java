package me.zyypj.booter.dao.connector;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import me.zyypj.booter.dao.config.HikariCPConfig;

public class HikariDatabaseConnector implements DatabaseConnector {
    private final HikariDataSource dataSource;

    public HikariDatabaseConnector(HikariCPConfig config) {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(config.getJdbcUrl());
        hikariConfig.setUsername(config.getUser());
        hikariConfig.setPassword(config.getPassword());

        hikariConfig.setMaximumPoolSize(config.getMaximumPoolSize());
        hikariConfig.setMinimumIdle(config.getMinimumIdle());
        hikariConfig.setConnectionTimeout(config.getConnectionTimeout());
        hikariConfig.setIdleTimeout(config.getIdleTimeout());
        hikariConfig.setMaxLifetime(config.getMaxLifetime());

        if (config.getPoolName() != null && !config.getPoolName().isEmpty()) {
            hikariConfig.setPoolName(config.getPoolName());
        }

        hikariConfig.setLeakDetectionThreshold(config.getLeakDetectionThreshold());

        if (config.getConnectionTestQuery() != null && !config.getConnectionTestQuery().isEmpty()) {
            hikariConfig.setConnectionTestQuery(config.getConnectionTestQuery());
        }

        hikariConfig.setAutoCommit(config.isAutoCommit());
        hikariConfig.setValidationTimeout(config.getValidationTimeout());

        this.dataSource = new HikariDataSource(hikariConfig);
    }

    @Override
    public Connection connect() throws SQLException {
        return dataSource.getConnection();
    }
}
