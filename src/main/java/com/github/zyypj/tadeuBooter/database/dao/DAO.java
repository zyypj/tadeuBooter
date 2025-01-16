package com.github.zyypj.tadeuBooter.database.dao;

import com.github.zyypj.tadeuBooter.database.Database;
import lombok.Data;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

@Data
public abstract class DAO<K, V> {

    private final Database<?> database;
    private final ExecutorService executor;
    @NonNull
    private final Logger logger;

    public DAO(Database<?> database, ExecutorService executor) {
        this.database = database;
        this.executor = executor;
        this.logger = Logger.getLogger(getClass().getName());
    }

    protected DAO(Database<?> database, ExecutorService executor, @NonNull Logger logger) {
        this.database = database;
        this.executor = executor;
        this.logger = logger;
    }

    public abstract CompletableFuture<V> find(K key);

    public abstract CompletableFuture<Void> update(V value);

    public CompletableFuture<Void> updateAll(Iterable<V> values) {
        if (values instanceof Collection) {
            Collection<V> collection = (Collection<V>) values;
            return CompletableFuture.allOf(
                    collection.stream()
                            .map(this::update)
                            .toArray(CompletableFuture[]::new)
            );
        }
        throw new IllegalArgumentException("The given Iterable is not recognized by this method");
    }

    public CompletableFuture<Void> updateAll(V... values) {
        return CompletableFuture.allOf(Arrays.stream(values).map(this::update).toArray(CompletableFuture[]::new));
    }

    public abstract CompletableFuture<List<V>> fetchAll();

    public abstract void setupDatabase();
}