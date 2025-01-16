package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractSetMultimap<K, V> extends AbstractMapBasedMultimap<K, V> implements SetMultimap<K, V> {
  private static final long serialVersionUID = 7431625294878419160L;
  
  protected AbstractSetMultimap(Map<K, Collection<V>> map) {
    super(map);
  }
  
  Set<V> createUnmodifiableEmptyCollection() {
    return Collections.emptySet();
  }
  
  <E> Collection<E> unmodifiableCollectionSubclass(Collection<E> collection) {
    return Collections.unmodifiableSet((Set<? extends E>)collection);
  }
  
  Collection<V> wrapCollection(@ParametricNullness K key, Collection<V> collection) {
    return new AbstractMapBasedMultimap.WrappedSet(this, key, (Set<V>)collection);
  }
  
  public Set<V> get(@ParametricNullness K key) {
    return (Set<V>)super.get(key);
  }
  
  public Set<Map.Entry<K, V>> entries() {
    return (Set<Map.Entry<K, V>>)super.entries();
  }
  
  @CanIgnoreReturnValue
  public Set<V> removeAll(@CheckForNull Object key) {
    return (Set<V>)super.removeAll(key);
  }
  
  @CanIgnoreReturnValue
  public Set<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
    return (Set<V>)super.replaceValues(key, values);
  }
  
  public Map<K, Collection<V>> asMap() {
    return super.asMap();
  }
  
  @CanIgnoreReturnValue
  public boolean put(@ParametricNullness K key, @ParametricNullness V value) {
    return super.put(key, value);
  }
  
  public boolean equals(@CheckForNull Object object) {
    return super.equals(object);
  }
  
  abstract Set<V> createCollection();
}
