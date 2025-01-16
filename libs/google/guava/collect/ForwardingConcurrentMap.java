package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingConcurrentMap<K, V> extends ForwardingMap<K, V> implements ConcurrentMap<K, V> {
  @CheckForNull
  @CanIgnoreReturnValue
  public V putIfAbsent(K key, V value) {
    return delegate().putIfAbsent(key, value);
  }
  
  @CanIgnoreReturnValue
  public boolean remove(@CheckForNull Object key, @CheckForNull Object value) {
    return delegate().remove(key, value);
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public V replace(K key, V value) {
    return delegate().replace(key, value);
  }
  
  @CanIgnoreReturnValue
  public boolean replace(K key, V oldValue, V newValue) {
    return delegate().replace(key, oldValue, newValue);
  }
  
  protected abstract ConcurrentMap<K, V> delegate();
}
