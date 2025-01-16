package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingSortedSetMultimap<K, V> extends ForwardingSetMultimap<K, V> implements SortedSetMultimap<K, V> {
  public SortedSet<V> get(@ParametricNullness K key) {
    return delegate().get(key);
  }
  
  public SortedSet<V> removeAll(@CheckForNull Object key) {
    return delegate().removeAll(key);
  }
  
  public SortedSet<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
    return delegate().replaceValues(key, values);
  }
  
  @CheckForNull
  public Comparator<? super V> valueComparator() {
    return delegate().valueComparator();
  }
  
  protected abstract SortedSetMultimap<K, V> delegate();
}
