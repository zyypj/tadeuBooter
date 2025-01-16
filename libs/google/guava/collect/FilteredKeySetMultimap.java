package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Predicate;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class FilteredKeySetMultimap<K, V> extends FilteredKeyMultimap<K, V> implements FilteredSetMultimap<K, V> {
  FilteredKeySetMultimap(SetMultimap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
    super(unfiltered, keyPredicate);
  }
  
  public SetMultimap<K, V> unfiltered() {
    return (SetMultimap<K, V>)this.unfiltered;
  }
  
  public Set<V> get(@ParametricNullness K key) {
    return (Set<V>)super.get(key);
  }
  
  public Set<V> removeAll(@CheckForNull Object key) {
    return (Set<V>)super.removeAll(key);
  }
  
  public Set<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
    return (Set<V>)super.replaceValues(key, values);
  }
  
  public Set<Map.Entry<K, V>> entries() {
    return (Set<Map.Entry<K, V>>)super.entries();
  }
  
  Set<Map.Entry<K, V>> createEntries() {
    return new EntrySet(this);
  }
  
  class EntrySet extends FilteredKeyMultimap<K, V>.Entries implements Set<Map.Entry<K, V>> {
    EntrySet(FilteredKeySetMultimap this$0) {
      super(this$0);
    }
    
    public int hashCode() {
      return Sets.hashCodeImpl(this);
    }
    
    public boolean equals(@CheckForNull Object o) {
      return Sets.equalsImpl(this, o);
    }
  }
}
