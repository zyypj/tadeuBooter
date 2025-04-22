package com.github.zyypj.tadeuBooter.api.collections

abstract class GenericCache<K, V, T> {
    protected val cache: MutableMap<K, V> = getInitialMap()

    protected open fun getInitialMap(): MutableMap<K, V> = mutableMapOf()

    abstract fun apply(t: T): Pair<K, V>

    fun add(t: T) {
        val (k, v) = apply(t)
        cache[k] = v
    }

    fun addAll(collection: Collection<T>) = collection.forEach(::add)

    fun addAll(map: Map<K, V>) = cache.putAll(map)

    fun remove(key: K?): V? = key?.let(cache::remove)

    fun has(key: K?): Boolean = key != null && cache.containsKey(key)

    fun get(key: K?): V? = key?.let(cache::get)

    fun getOrInsert(key: K, def: T): V = cache[key] ?: run {
        val (k, v) = apply(def)
        cache[k] = v
        v
    }

    fun getOrInsert(key: K, defaultValue: () -> T): V = cache[key] ?: run {
        val def = defaultValue() ?: throw NullPointerException("Supplier retornou null")
        val (k, v) = apply(def)
        cache[k] = v
        v
    }

    fun keySet(): Set<K> = cache.keys

    fun getAll(): Collection<V> = cache.values

    fun clear() = cache.clear()

    fun size(): Int = cache.size

    fun isEmpty(): Boolean = cache.isEmpty()

    fun get(keys: Iterable<K>): List<V> = keys.mapNotNull { cache[it] }
}