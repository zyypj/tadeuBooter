package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractListMultimap<K, V> extends AbstractMapBasedMultimap<K, V> implements ListMultimap<K, V> {
  private static final long serialVersionUID = 6588350623831699109L;
  
  protected AbstractListMultimap(Map<K, Collection<V>> map) {
    super(map);
  }
  
  List<V> createUnmodifiableEmptyCollection() {
    return Collections.emptyList();
  }
  
  <E> Collection<E> unmodifiableCollectionSubclass(Collection<E> collection) {
    return Collections.unmodifiableList((List<? extends E>)collection);
  }
  
  Collection<V> wrapCollection(@ParametricNullness K key, Collection<V> collection) {
    return wrapList(key, (List<V>)collection, (AbstractMapBasedMultimap<K, V>.WrappedCollection)null);
  }
  
  public List<V> get(@ParametricNullness K key) {
    return (List<V>)super.get(key);
  }
  
  @CanIgnoreReturnValue
  public List<V> removeAll(@CheckForNull Object key) {
    return (List<V>)super.removeAll(key);
  }
  
  @CanIgnoreReturnValue
  public List<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
    return (List<V>)super.replaceValues(key, values);
  }
  
  @CanIgnoreReturnValue
  public boolean put(@ParametricNullness K key, @ParametricNullness V value) {
    return super.put(key, value);
  }
  
  public Map<K, Collection<V>> asMap() {
    return super.asMap();
  }
  
  public boolean equals(@CheckForNull Object object) {
    return super.equals(object);
  }
  
  abstract List<V> createCollection();
}
