package me.syncwrld.booter.libs.google.guava.graph;

import java.util.Map;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
final class MapRetrievalCache<K, V> extends MapIteratorCache<K, V> {
  @CheckForNull
  private volatile transient CacheEntry<K, V> cacheEntry1;
  
  @CheckForNull
  private volatile transient CacheEntry<K, V> cacheEntry2;
  
  MapRetrievalCache(Map<K, V> backingMap) {
    super(backingMap);
  }
  
  @CheckForNull
  V get(Object key) {
    Preconditions.checkNotNull(key);
    V value = getIfCached(key);
    if (value != null)
      return value; 
    value = getWithoutCaching(key);
    if (value != null)
      addToCache((K)key, value); 
    return value;
  }
  
  @CheckForNull
  V getIfCached(@CheckForNull Object key) {
    V value = super.getIfCached(key);
    if (value != null)
      return value; 
    CacheEntry<K, V> entry = this.cacheEntry1;
    if (entry != null && entry.key == key)
      return entry.value; 
    entry = this.cacheEntry2;
    if (entry != null && entry.key == key) {
      addToCache(entry);
      return entry.value;
    } 
    return null;
  }
  
  void clearCache() {
    super.clearCache();
    this.cacheEntry1 = null;
    this.cacheEntry2 = null;
  }
  
  private void addToCache(K key, V value) {
    addToCache(new CacheEntry<>(key, value));
  }
  
  private void addToCache(CacheEntry<K, V> entry) {
    this.cacheEntry2 = this.cacheEntry1;
    this.cacheEntry1 = entry;
  }
  
  private static final class CacheEntry<K, V> {
    final K key;
    
    final V value;
    
    CacheEntry(K key, V value) {
      this.key = key;
      this.value = value;
    }
  }
}
