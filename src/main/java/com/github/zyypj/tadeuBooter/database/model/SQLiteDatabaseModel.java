package com.github.zyypj.tadeuBooter.database.model;

import com.github.zyypj.tadeuBooter.annotation.Modifier;
import com.github.zyypj.tadeuBooter.database.DatabaseHolder;
import com.github.zyypj.tadeuBooter.database.DatabaseModel;
import com.github.zyypj.tadeuBooter.database.SQLiteDatabase;

import java.util.Properties;

public class SQLiteDatabaseModel extends DatabaseModel<SQLiteDatabase> {

    public SQLiteDatabaseModel() {
        super("sqlite", "jdbc:sqlite:{database}");
    }

    @Override
    public SQLiteDatabase createDatabase(DatabaseHolder holder, Properties properties) {
        return new SQLiteDatabase(this, properties, holder);
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
                return "AUTOINCREMENT";
            default:
                throw new IllegalArgumentException("Unknown modifier: " + modifier);
        }
    }
}