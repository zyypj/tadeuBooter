package com.github.zyypj.tadeuBooter.database.controler;

import com.github.zyypj.tadeuBooter.database.controler.listener.ControllerListener;
import com.github.zyypj.tadeuBooter.database.dao.DAO;
import com.github.zyypj.tadeuBooter.minecraft.object.Cache;
import lombok.Data;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

@Data
public abstract class Controller<K, V, C extends Cache<K, V, ?>, D extends DAO<K, V>> {

    @NonNull
    private final C cache;
    @NonNull
    private final D dao;
    private final @NonNull Logger logger;
    private final @NonNull Set<ControllerListener> listeners = new HashSet<>();

    public Controller(@NonNull C cache, @NonNull D dao) {
        this.cache = cache;
        this.dao = dao;
        this.logger = Logger.getLogger(getClass().getSimpleName());
    }

    public Controller(@NonNull C cache, @NonNull D dao, @NonNull Logger logger) {
        this.cache = cache;
        this.dao = dao;
        this.logger = logger;
    }

    /**
     * <p>Gets the value from the cache and if it's not present there, gets it from the database.</p>
     * @param key the key
     * @return a {@link CompletableFuture} that will be completed with the value
     */
    public CompletableFuture<V> get(@NonNull K key) {
        return cache.get(key)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> dao.find(key).whenComplete((value, throwable) -> {
                    if (throwable != null) {
                        onGetError(key, throwable);
                        return;
                    }

                    if (value == null)
                        return;

                    cache(value);
                }));
    }

    protected void onGetError(@NonNull K key, @NonNull Throwable throwable) {
        getLogger().log(Level.SEVERE, "Error while getting the value with the key " + key, throwable);
    }

    /**
     * <p>Caches the value in the {@link #getCache()}.</p>
     * @param value the value
     */
    public abstract void cache(@NonNull V value);

    /**
     * <p>Caches all the given values in the {@link #getCache()}</p>
     * @param values the values
     */
    public void cacheAll(@NonNull Iterable<V> values) {
        for (V value : values)
            cache(value);
    }

    /**
     * <p>
     *     Called when {@code this} {@link Controller} shutdowns.
     *     The default implementation calls the {@link #save()} method and waits for the {@link CompletableFuture} to complete.
     * </p>
     * @apiNote this method is not called natively by WLib, you need to call it manually
     */
    public void shutdown() {
        save().join();
    }

    /**
     * <p>
     *     Called when {@code this} {@link Controller} is requested to save all the data.
     *     The Spigot implementation of WLib will call this method every 5 minutes.
     * </p>
     */
    public CompletableFuture<Void> save() {
        return CompletableFuture.allOf(cache.getAll().stream().map(dao::update).toArray(CompletableFuture[]::new));
    }

    @Override
    public int hashCode() {
        return Controller.class.hashCode();
    }

    public void addListener(@NonNull ControllerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(@NonNull ControllerListener listener) {
        listeners.remove(listener);
    }
}