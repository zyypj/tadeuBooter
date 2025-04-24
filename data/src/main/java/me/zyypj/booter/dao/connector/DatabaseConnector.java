package me.zyypj.booter.dao.connector;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnector {
    Connection connect() throws SQLException;
}
