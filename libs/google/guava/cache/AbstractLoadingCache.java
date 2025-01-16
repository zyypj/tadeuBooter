package me.syncwrld.booter.libs.google.guava.cache;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableMap;
import me.syncwrld.booter.libs.google.guava.collect.Maps;
import me.syncwrld.booter.libs.google.guava.util.concurrent.UncheckedExecutionException;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class AbstractLoadingCache<K, V> extends AbstractCache<K, V> implements LoadingCache<K, V> {
  @CanIgnoreReturnValue
  public V getUnchecked(K key) {
    try {
      return get(key);
    } catch (ExecutionException e) {
      throw new UncheckedExecutionException(e.getCause());
    } 
  }
  
  public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException {
    Map<K, V> result = Maps.newLinkedHashMap();
    for (K key : keys) {
      if (!result.containsKey(key))
        result.put(key, get(key)); 
    } 
    return ImmutableMap.copyOf(result);
  }
  
  public final V apply(K key) {
    return getUnchecked(key);
  }
  
  public void refresh(K key) {
    throw new UnsupportedOperationException();
  }
}
