package me.syncwrld.booter.libs.google.guava.cache;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.CompatibleWith;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableMap;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@DoNotMock("Use CacheBuilder.newBuilder().build()")
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface Cache<K, V> {
  @CheckForNull
  @CanIgnoreReturnValue
  V getIfPresent(@CompatibleWith("K") Object paramObject);
  
  @CanIgnoreReturnValue
  V get(K paramK, Callable<? extends V> paramCallable) throws ExecutionException;
  
  ImmutableMap<K, V> getAllPresent(Iterable<? extends Object> paramIterable);
  
  void put(K paramK, V paramV);
  
  void putAll(Map<? extends K, ? extends V> paramMap);
  
  void invalidate(@CompatibleWith("K") Object paramObject);
  
  void invalidateAll(Iterable<? extends Object> paramIterable);
  
  void invalidateAll();
  
  long size();
  
  CacheStats stats();
  
  ConcurrentMap<K, V> asMap();
  
  void cleanUp();
}
