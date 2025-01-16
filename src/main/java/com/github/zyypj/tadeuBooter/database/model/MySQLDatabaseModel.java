package com.github.zyypj.tadeuBooter.database.model;

import com.github.zyypj.tadeuBooter.annotation.Modifier;
import com.github.zyypj.tadeuBooter.database.DatabaseHolder;
import com.github.zyypj.tadeuBooter.database.DatabaseModel;
import com.github.zyypj.tadeuBooter.database.MySQLDatabase;

import java.util.Properties;

public class MySQLDatabaseModel extends DatabaseModel<MySQLDatabase> {

    public MySQLDatabaseModel() {
        super("mysql", "jdbc:mysql://{host}:{port}/{database}");
    }

    @Override
    public MySQLDatabase createDatabase(DatabaseHolder holder, Properties properties) {
        return new MySQLDatabase(this, properties, holder);
    }

    @Override
    public String getModifierCommand(Modifier modifier) {
        switch (modifier) {
            case UNIQUE:
                return "UNIQUE";
            case PRIMARY_KEY:
                return "PRIMARY KEY";
            case NOT_NULL:
                return "NOT NULL";
            case AUTO_INCREMENT:
                return "AUTO_INCREMENT";
            default:
                throw new IllegalArgumentException("Unknown modifier: " + modifier);
        }
    }
}
