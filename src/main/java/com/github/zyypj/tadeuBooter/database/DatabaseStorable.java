package com.github.zyypj.tadeuBooter.database;

import lombok.NonNull;

import java.util.Collections;
import java.util.Map;

public interface DatabaseStorable {

    boolean isInDatabase();

    void setInDatabase(boolean in);

    boolean isDirty();

    void setDirty(boolean dirty);

    boolean isDeleted();

    void setDeleted(boolean deleted);

    @NonNull
    default Map<String, Object> saveToDatabase(Database<?> database, String table) {
        return Collections.emptyMap();
    }

    @NonNull
    default Map<String, Object> updateToDatabase(Database<?> database, String table, Map<String, Object> where) {
        return Collections.emptyMap();
    }

    @NonNull
    default Map<String, Object> deleteFromDatabase(Database<?> database, String table) {
        return Collections.emptyMap();
    }
}
