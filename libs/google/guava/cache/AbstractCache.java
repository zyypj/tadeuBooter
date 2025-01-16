package me.syncwrld.booter.libs.google.guava.cache;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableMap;
import me.syncwrld.booter.libs.google.guava.collect.Maps;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class AbstractCache<K, V> implements Cache<K, V> {
  public V get(K key, Callable<? extends V> valueLoader) throws ExecutionException {
    throw new UnsupportedOperationException();
  }
  
  public ImmutableMap<K, V> getAllPresent(Iterable<? extends Object> keys) {
    Map<K, V> result = Maps.newLinkedHashMap();
    for (Object key : keys) {
      if (!result.containsKey(key)) {
        K castKey = (K)key;
        V value = getIfPresent(key);
        if (value != null)
          result.put(castKey, value); 
      } 
    } 
    return ImmutableMap.copyOf(result);
  }
  
  public void put(K key, V value) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends K, ? extends V> m) {
    for (Map.Entry<? extends K, ? extends V> entry : m.entrySet())
      put(entry.getKey(), entry.getValue()); 
  }
  
  public void cleanUp() {}
  
  public long size() {
    throw new UnsupportedOperationException();
  }
  
  public void invalidate(Object key) {
    throw new UnsupportedOperationException();
  }
  
  public void invalidateAll(Iterable<? extends Object> keys) {
    for (Object key : keys)
      invalidate(key); 
  }
  
  public void invalidateAll() {
    throw new UnsupportedOperationException();
  }
  
  public CacheStats stats() {
    throw new UnsupportedOperationException();
  }
  
  public ConcurrentMap<K, V> asMap() {
    throw new UnsupportedOperationException();
  }
  
  public static final class SimpleStatsCounter implements StatsCounter {
    private final LongAddable hitCount = LongAddables.create();
    
    private final LongAddable missCount = LongAddables.create();
    
    private final LongAddable loadSuccessCount = LongAddables.create();
    
    private final LongAddable loadExceptionCount = LongAddables.create();
    
    private final LongAddable totalLoadTime = LongAddables.create();
    
    private final LongAddable evictionCount = LongAddables.create();
    
    public void recordHits(int count) {
      this.hitCount.add(count);
    }
    
    public void recordMisses(int count) {
      this.missCount.add(count);
    }
    
    public void recordLoadSuccess(long loadTime) {
      this.loadSuccessCount.increment();
      this.totalLoadTime.add(loadTime);
    }
    
    public void recordLoadException(long loadTime) {
      this.loadExceptionCount.increment();
      this.totalLoadTime.add(loadTime);
    }
    
    public void recordEviction() {
      this.evictionCount.increment();
    }
    
    public CacheStats snapshot() {
      return new CacheStats(
          negativeToMaxValue(this.hitCount.sum()), 
          negativeToMaxValue(this.missCount.sum()), 
          negativeToMaxValue(this.loadSuccessCount.sum()), 
          negativeToMaxValue(this.loadExceptionCount.sum()), 
          negativeToMaxValue(this.totalLoadTime.sum()), 
          negativeToMaxValue(this.evictionCount.sum()));
    }
    
    private static long negativeToMaxValue(long value) {
      return (value >= 0L) ? value : Long.MAX_VALUE;
    }
    
    public void incrementBy(AbstractCache.StatsCounter other) {
      CacheStats otherStats = other.snapshot();
      this.hitCount.add(otherStats.hitCount());
      this.missCount.add(otherStats.missCount());
      this.loadSuccessCount.add(otherStats.loadSuccessCount());
      this.loadExceptionCount.add(otherStats.loadExceptionCount());
      this.totalLoadTime.add(otherStats.totalLoadTime());
      this.evictionCount.add(otherStats.evictionCount());
    }
  }
  
  public static interface StatsCounter {
    void recordHits(int param1Int);
    
    void recordMisses(int param1Int);
    
    void recordLoadSuccess(long param1Long);
    
    void recordLoadException(long param1Long);
    
    void recordEviction();
    
    CacheStats snapshot();
  }
}
