package com.github.zyypj.tadeuBooter.api.database.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MySQLConfig {
    private String host;
    private int port;
    private String database;
    private String user;
    private String password;
}