package me.zyypj.booter.dao.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DatabaseConfig {
    private DatabaseType type;
    private MySQLConfig mysql;
    private HikariCPConfig hikari;
    private SQLiteConfig sqlite;

    public DatabaseConfig() {}
}
