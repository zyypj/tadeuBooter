package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractSortedSetMultimap<K, V> extends AbstractSetMultimap<K, V> implements SortedSetMultimap<K, V> {
  private static final long serialVersionUID = 430848587173315748L;
  
  protected AbstractSortedSetMultimap(Map<K, Collection<V>> map) {
    super(map);
  }
  
  SortedSet<V> createUnmodifiableEmptyCollection() {
    return unmodifiableCollectionSubclass(createCollection());
  }
  
  <E> SortedSet<E> unmodifiableCollectionSubclass(Collection<E> collection) {
    if (collection instanceof NavigableSet)
      return Sets.unmodifiableNavigableSet((NavigableSet<E>)collection); 
    return Collections.unmodifiableSortedSet((SortedSet<E>)collection);
  }
  
  Collection<V> wrapCollection(@ParametricNullness K key, Collection<V> collection) {
    if (collection instanceof NavigableSet)
      return new AbstractMapBasedMultimap.WrappedNavigableSet(this, key, (NavigableSet<V>)collection, null); 
    return new AbstractMapBasedMultimap.WrappedSortedSet(this, key, (SortedSet<V>)collection, null);
  }
  
  public SortedSet<V> get(@ParametricNullness K key) {
    return (SortedSet<V>)super.get(key);
  }
  
  @CanIgnoreReturnValue
  public SortedSet<V> removeAll(@CheckForNull Object key) {
    return (SortedSet<V>)super.removeAll(key);
  }
  
  @CanIgnoreReturnValue
  public SortedSet<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
    return (SortedSet<V>)super.replaceValues(key, values);
  }
  
  public Map<K, Collection<V>> asMap() {
    return super.asMap();
  }
  
  public Collection<V> values() {
    return super.values();
  }
  
  abstract SortedSet<V> createCollection();
}
