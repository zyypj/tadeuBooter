package me.syncwrld.booter.libs.google.guava.cache;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Function;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableMap;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface LoadingCache<K, V> extends Cache<K, V>, Function<K, V> {
  @CanIgnoreReturnValue
  V get(K paramK) throws ExecutionException;
  
  @CanIgnoreReturnValue
  V getUnchecked(K paramK);
  
  @CanIgnoreReturnValue
  ImmutableMap<K, V> getAll(Iterable<? extends K> paramIterable) throws ExecutionException;
  
  @Deprecated
  V apply(K paramK);
  
  void refresh(K paramK);
  
  ConcurrentMap<K, V> asMap();
}
