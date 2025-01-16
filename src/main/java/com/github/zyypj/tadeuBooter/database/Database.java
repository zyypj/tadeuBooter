package com.github.zyypj.tadeuBooter.database;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

@Data
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Database<M extends DatabaseModel<?>> {

    private final M model;
    private final DatabaseHolder holder;
    protected final Properties properties;
    protected Connection connection;
    private final @NonNull Logger logger;

    public Database(@NonNull M model, @NonNull DatabaseHolder<?> holder, @NonNull Properties properties) {
        this(model, holder, properties, holder.getLogger());
    }

    public boolean isClosed() {
        return connection == null;
    }

    public void open(Consumer<Database<M>> callback) {
        try {
            if (!isClosed()) return;
            connection = DriverManager.getConnection(getJdbcUrl(), properties);
            if (callback != null)
                callback.accept(this);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error while opening the database connection", e);
        }
    }

    public void open() {
        open(null);
    }

    public void close() {
        close(null);
    }

    public void close(Consumer<Database<M>> callback) {
        try {
            if (isClosed()) return;
            connection.close();
            connection = null;
            if (callback != null)
                callback.accept(this);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error while closing the database connection", e);
        }
    }

    public Database<M> update(String command, Object... replacements) {
        try {
            returnUpdate(command, replacements).close();
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "Error while executing \"" + command + "\" with the arguments " + Arrays.toString(replacements), e);
        }
        return this;
    }

    public PreparedStatement returnUpdate(String command, Object... replacements) {
        if (isClosed()) return null;
        try {
            PreparedStatement statement = connection.prepareStatement(command, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < replacements.length; i++)
                statement.setObject(i + 1, replacements[i]);
            statement.executeUpdate();
            return statement;
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error while executing \"" + command + "\" with the arguments " + Arrays.toString(replacements), e);
        }
        return null;
    }

    public ResultSet query(String query, Object... replacements) {
        if (isClosed()) return null;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < replacements.length; i++)
                statement.setObject(i + 1, replacements[i]);
            return statement.executeQuery();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error while executing \"" + query + "\" with the arguments " + Arrays.toString(replacements), e);
            return null;
        }
    }

    public abstract String getJdbcUrl();

    public void save(DatabaseStorable object, String table) {
        if (object == null)
            return;

        if (object.isDeleted()) {
            delete(object, table);
            return;
        }

        if (object.isInDatabase()) {
            if (object.isDirty())
                update(object, table);
            return;
        }

        insert(object, table);
    }

    protected void update(DatabaseStorable object, String table) {
        if (object == null)
            return;

        StringBuilder builder = new StringBuilder("UPDATE " + table + " SET ");
        Map<String, Object> where = new HashMap<>();
        Map<String, Object> data = object.updateToDatabase(this, table, where);

        if (data.isEmpty())
            return;

        for (Map.Entry<String, Object> entry : data.entrySet())
            builder.append(entry.getKey()).append(" = ?, ");
        builder.delete(builder.length() - 2, builder.length());

        if (!where.isEmpty()) {
            builder.append(" WHERE ");
            for (Map.Entry<String, Object> entry : where.entrySet())
                builder.append(entry.getKey()).append(" = ? AND ");
            builder.delete(builder.length() - 5, builder.length());
        }

        builder.append(";");

        object.setDirty(false);

        List<Object> list = new ArrayList<>(data.values());
        list.addAll(where.values());

        update(builder.toString(), list.toArray());
    }

    protected void delete(DatabaseStorable object, String table) {
        if (object == null)
            return;

        if (!object.isInDatabase())
            return;

        Map<String, Object> where = object.deleteFromDatabase(this, table);
        if (where.isEmpty())
            return;

        StringBuilder builder = new StringBuilder("DELETE FROM " + table + " WHERE ");
        for (Map.Entry<String, Object> entry : where.entrySet())
            builder.append(entry.getKey()).append(" = ? AND ");
        builder.delete(builder.length() - 5, builder.length());

        builder.append(";");

        update(builder.toString(), where.values().toArray());
    }

    protected void insert(DatabaseStorable object, String table) {
        if (object == null)
            return;

        Map<String, Object> data = object.saveToDatabase(this, table);
        if (data.isEmpty())
            return;

        StringBuilder builder = new StringBuilder("INSERT INTO ").append(table).append(" (");
        for (String s : data.keySet()) {
            builder.append(s).append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());

        builder.append(") VALUES (");
        for (int i = 0; i < data.size(); i++) {
            builder.append("?, ");
        }
        builder.delete(builder.length() - 2, builder.length());

        builder.append(");");

        object.setInDatabase(true);
        object.setDirty(false);

        update(builder.toString(), data.values().toArray());
    }

    public void save(@NonNull String table, @NonNull DatabaseStorable... objects) {
        for (DatabaseStorable object : objects)
            save(object, table);
    }

    public void save(@NonNull Iterable<? extends DatabaseStorable> objects, @NonNull String table) {
        for (DatabaseStorable object : objects)
            save(object, table);
    }
}