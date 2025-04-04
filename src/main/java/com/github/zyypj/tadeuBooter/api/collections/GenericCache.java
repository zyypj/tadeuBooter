package com.github.zyypj.tadeuBooter.api.collections;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

@Data
@AllArgsConstructor
public abstract class GenericCache<K, V, T> {

    protected final Map<K, V> cache;

    protected GenericCache() {
        cache = getInitialMap();
    }

    public void add(T t) {
        MappablePair<K, V> apply = apply(t);
        cache.put(apply.getFirst(), apply.getSecond());
    }

    public void addAll(Collection<T> collection) {
        for (T t : collection) add(t);
    }

    public void addAll(Map<K, V> map) {
        cache.putAll(map);
    }

    public Optional<V> remove(@Nullable K key) {
        if (key == null) return Optional.empty();
        return Optional.ofNullable(cache.remove(key));
    }

    public boolean has(@Nullable K key) {
        return key != null && cache.containsKey(key);
    }

    public Optional<V> get(@Nullable K key) {
        if (key == null) return Optional.empty();
        return Optional.ofNullable(cache.get(key));
    }

    public V getOrInsert(K key, T def) {
        if (cache.containsKey(key)) return cache.get(key);
        MappablePair<K, V> pair = apply(def);
        add(def);
        return pair.getSecond();
    }

    public V getOrInsert(K key, Supplier<T> defaultValue) {
        if (cache.containsKey(key)) return cache.get(key);
        T def = defaultValue.get();
        if (def == null) throw new NullPointerException("The supplier returned a null value");
        MappablePair<K, V> pair = apply(def);
        add(def);
        return pair.getSecond();
    }

    public Set<K> keySet() {
        return cache.keySet();
    }

    public Collection<V> getAll() {
        return cache.values();
    }

    public void clear() {
        cache.clear();
    }

    public int size() {
        return cache.size();
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }

    public abstract MappablePair<K, V> apply(T t);

    protected Map<K, V> getInitialMap() {
        return new HashMap<>();
    }

    public List<V> get(Iterable<K> keys) {
        List<V> result = new ArrayList<>(size());
        for (K key : keys) get(key).ifPresent(result::add);
        return result;
    }
}