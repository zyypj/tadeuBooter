package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingSetMultimap<K, V> extends ForwardingMultimap<K, V> implements SetMultimap<K, V> {
  public Set<Map.Entry<K, V>> entries() {
    return delegate().entries();
  }
  
  public Set<V> get(@ParametricNullness K key) {
    return delegate().get(key);
  }
  
  @CanIgnoreReturnValue
  public Set<V> removeAll(@CheckForNull Object key) {
    return delegate().removeAll(key);
  }
  
  @CanIgnoreReturnValue
  public Set<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
    return delegate().replaceValues(key, values);
  }
  
  protected abstract SetMultimap<K, V> delegate();
}
