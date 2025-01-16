package com.github.zyypj.tadeuBooter.database;

import com.github.zyypj.tadeuBooter.annotation.Modifier;
import lombok.Data;

import java.util.Properties;

@Data
public abstract class DatabaseModel<D extends Database<?>> {

    private final String type;
    private final String baseUrl;

    public abstract D createDatabase(DatabaseHolder holder, Properties properties);

    public abstract String getModifierCommand(Modifier modifier);
}