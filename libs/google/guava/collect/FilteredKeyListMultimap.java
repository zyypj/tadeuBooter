package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.List;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Predicate;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class FilteredKeyListMultimap<K, V> extends FilteredKeyMultimap<K, V> implements ListMultimap<K, V> {
  FilteredKeyListMultimap(ListMultimap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
    super(unfiltered, keyPredicate);
  }
  
  public ListMultimap<K, V> unfiltered() {
    return (ListMultimap<K, V>)super.unfiltered();
  }
  
  public List<V> get(@ParametricNullness K key) {
    return (List<V>)super.get(key);
  }
  
  public List<V> removeAll(@CheckForNull Object key) {
    return (List<V>)super.removeAll(key);
  }
  
  public List<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
    return (List<V>)super.replaceValues(key, values);
  }
}
