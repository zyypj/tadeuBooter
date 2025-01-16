package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
abstract class AbstractNavigableMap<K, V> extends Maps.IteratorBasedAbstractMap<K, V> implements NavigableMap<K, V> {
  @CheckForNull
  public abstract V get(@CheckForNull Object paramObject);
  
  @CheckForNull
  public Map.Entry<K, V> firstEntry() {
    return Iterators.<Map.Entry<K, V>>getNext(entryIterator(), null);
  }
  
  @CheckForNull
  public Map.Entry<K, V> lastEntry() {
    return Iterators.<Map.Entry<K, V>>getNext(descendingEntryIterator(), null);
  }
  
  @CheckForNull
  public Map.Entry<K, V> pollFirstEntry() {
    return Iterators.<Map.Entry<K, V>>pollNext(entryIterator());
  }
  
  @CheckForNull
  public Map.Entry<K, V> pollLastEntry() {
    return Iterators.<Map.Entry<K, V>>pollNext(descendingEntryIterator());
  }
  
  @ParametricNullness
  public K firstKey() {
    Map.Entry<K, V> entry = firstEntry();
    if (entry == null)
      throw new NoSuchElementException(); 
    return entry.getKey();
  }
  
  @ParametricNullness
  public K lastKey() {
    Map.Entry<K, V> entry = lastEntry();
    if (entry == null)
      throw new NoSuchElementException(); 
    return entry.getKey();
  }
  
  @CheckForNull
  public Map.Entry<K, V> lowerEntry(@ParametricNullness K key) {
    return headMap(key, false).lastEntry();
  }
  
  @CheckForNull
  public Map.Entry<K, V> floorEntry(@ParametricNullness K key) {
    return headMap(key, true).lastEntry();
  }
  
  @CheckForNull
  public Map.Entry<K, V> ceilingEntry(@ParametricNullness K key) {
    return tailMap(key, true).firstEntry();
  }
  
  @CheckForNull
  public Map.Entry<K, V> higherEntry(@ParametricNullness K key) {
    return tailMap(key, false).firstEntry();
  }
  
  @CheckForNull
  public K lowerKey(@ParametricNullness K key) {
    return Maps.keyOrNull(lowerEntry(key));
  }
  
  @CheckForNull
  public K floorKey(@ParametricNullness K key) {
    return Maps.keyOrNull(floorEntry(key));
  }
  
  @CheckForNull
  public K ceilingKey(@ParametricNullness K key) {
    return Maps.keyOrNull(ceilingEntry(key));
  }
  
  @CheckForNull
  public K higherKey(@ParametricNullness K key) {
    return Maps.keyOrNull(higherEntry(key));
  }
  
  abstract Iterator<Map.Entry<K, V>> descendingEntryIterator();
  
  public SortedMap<K, V> subMap(@ParametricNullness K fromKey, @ParametricNullness K toKey) {
    return subMap(fromKey, true, toKey, false);
  }
  
  public SortedMap<K, V> headMap(@ParametricNullness K toKey) {
    return headMap(toKey, false);
  }
  
  public SortedMap<K, V> tailMap(@ParametricNullness K fromKey) {
    return tailMap(fromKey, true);
  }
  
  public NavigableSet<K> navigableKeySet() {
    return new Maps.NavigableKeySet<>(this);
  }
  
  public Set<K> keySet() {
    return navigableKeySet();
  }
  
  public NavigableSet<K> descendingKeySet() {
    return descendingMap().navigableKeySet();
  }
  
  public NavigableMap<K, V> descendingMap() {
    return new DescendingMap();
  }
  
  private final class DescendingMap extends Maps.DescendingMap<K, V> {
    private DescendingMap() {}
    
    NavigableMap<K, V> forward() {
      return AbstractNavigableMap.this;
    }
    
    Iterator<Map.Entry<K, V>> entryIterator() {
      return AbstractNavigableMap.this.descendingEntryIterator();
    }
  }
}
