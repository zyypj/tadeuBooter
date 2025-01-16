package me.syncwrld.booter.libs.google.guava.graph;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.UnmodifiableIterator;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
class MapIteratorCache<K, V> {
  private final Map<K, V> backingMap;
  
  @CheckForNull
  private volatile transient Map.Entry<K, V> cacheEntry;
  
  MapIteratorCache(Map<K, V> backingMap) {
    this.backingMap = (Map<K, V>)Preconditions.checkNotNull(backingMap);
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  final V put(K key, V value) {
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(value);
    clearCache();
    return this.backingMap.put(key, value);
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  final V remove(Object key) {
    Preconditions.checkNotNull(key);
    clearCache();
    return this.backingMap.remove(key);
  }
  
  final void clear() {
    clearCache();
    this.backingMap.clear();
  }
  
  @CheckForNull
  V get(Object key) {
    Preconditions.checkNotNull(key);
    V value = getIfCached(key);
    if (value == null)
      return getWithoutCaching(key); 
    return value;
  }
  
  @CheckForNull
  final V getWithoutCaching(Object key) {
    Preconditions.checkNotNull(key);
    return this.backingMap.get(key);
  }
  
  final boolean containsKey(@CheckForNull Object key) {
    return (getIfCached(key) != null || this.backingMap.containsKey(key));
  }
  
  final Set<K> unmodifiableKeySet() {
    return new AbstractSet<K>() {
        public UnmodifiableIterator<K> iterator() {
          final Iterator<Map.Entry<K, V>> entryIterator = MapIteratorCache.this.backingMap.entrySet().iterator();
          return new UnmodifiableIterator<K>() {
              public boolean hasNext() {
                return entryIterator.hasNext();
              }
              
              public K next() {
                Map.Entry<K, V> entry = entryIterator.next();
                MapIteratorCache.this.cacheEntry = entry;
                return entry.getKey();
              }
            };
        }
        
        public int size() {
          return MapIteratorCache.this.backingMap.size();
        }
        
        public boolean contains(@CheckForNull Object key) {
          return MapIteratorCache.this.containsKey(key);
        }
      };
  }
  
  @CheckForNull
  V getIfCached(@CheckForNull Object key) {
    Map.Entry<K, V> entry = this.cacheEntry;
    if (entry != null && entry.getKey() == key)
      return entry.getValue(); 
    return null;
  }
  
  void clearCache() {
    this.cacheEntry = null;
  }
}
