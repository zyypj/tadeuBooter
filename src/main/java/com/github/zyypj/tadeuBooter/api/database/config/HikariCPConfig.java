package com.github.zyypj.tadeuBooter.api.database.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HikariCPConfig {
    private String jdbcUrl;
    private String user;
    private String password;
    private int maximumPoolSize = 10;
    private int minimumIdle = 2;
    private long connectionTimeout = 30000;
    private long idleTimeout = 600000;
    private long maxLifetime = 1800000;
    private String poolName;
    private long leakDetectionThreshold = 0;
    private String connectionTestQuery;
    private boolean autoCommit = true;
    private long validationTimeout = 5000;
}