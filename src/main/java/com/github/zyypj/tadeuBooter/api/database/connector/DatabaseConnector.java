package com.github.zyypj.tadeuBooter.api.database.connector;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnector {
    Connection connect() throws SQLException;
}