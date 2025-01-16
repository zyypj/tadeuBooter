package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.function.BiFunction;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class ForwardingNavigableMap<K, V> extends ForwardingSortedMap<K, V> implements NavigableMap<K, V> {
  @CheckForNull
  public Map.Entry<K, V> lowerEntry(@ParametricNullness K key) {
    return delegate().lowerEntry(key);
  }
  
  @CheckForNull
  protected Map.Entry<K, V> standardLowerEntry(@ParametricNullness K key) {
    return headMap(key, false).lastEntry();
  }
  
  @CheckForNull
  public K lowerKey(@ParametricNullness K key) {
    return delegate().lowerKey(key);
  }
  
  @CheckForNull
  protected K standardLowerKey(@ParametricNullness K key) {
    return Maps.keyOrNull(lowerEntry(key));
  }
  
  @CheckForNull
  public Map.Entry<K, V> floorEntry(@ParametricNullness K key) {
    return delegate().floorEntry(key);
  }
  
  @CheckForNull
  protected Map.Entry<K, V> standardFloorEntry(@ParametricNullness K key) {
    return headMap(key, true).lastEntry();
  }
  
  @CheckForNull
  public K floorKey(@ParametricNullness K key) {
    return delegate().floorKey(key);
  }
  
  @CheckForNull
  protected K standardFloorKey(@ParametricNullness K key) {
    return Maps.keyOrNull(floorEntry(key));
  }
  
  @CheckForNull
  public Map.Entry<K, V> ceilingEntry(@ParametricNullness K key) {
    return delegate().ceilingEntry(key);
  }
  
  @CheckForNull
  protected Map.Entry<K, V> standardCeilingEntry(@ParametricNullness K key) {
    return tailMap(key, true).firstEntry();
  }
  
  @CheckForNull
  public K ceilingKey(@ParametricNullness K key) {
    return delegate().ceilingKey(key);
  }
  
  @CheckForNull
  protected K standardCeilingKey(@ParametricNullness K key) {
    return Maps.keyOrNull(ceilingEntry(key));
  }
  
  @CheckForNull
  public Map.Entry<K, V> higherEntry(@ParametricNullness K key) {
    return delegate().higherEntry(key);
  }
  
  @CheckForNull
  protected Map.Entry<K, V> standardHigherEntry(@ParametricNullness K key) {
    return tailMap(key, false).firstEntry();
  }
  
  @CheckForNull
  public K higherKey(@ParametricNullness K key) {
    return delegate().higherKey(key);
  }
  
  @CheckForNull
  protected K standardHigherKey(@ParametricNullness K key) {
    return Maps.keyOrNull(higherEntry(key));
  }
  
  @CheckForNull
  public Map.Entry<K, V> firstEntry() {
    return delegate().firstEntry();
  }
  
  @CheckForNull
  protected Map.Entry<K, V> standardFirstEntry() {
    return Iterables.<Map.Entry<K, V>>getFirst(entrySet(), null);
  }
  
  protected K standardFirstKey() {
    Map.Entry<K, V> entry = firstEntry();
    if (entry == null)
      throw new NoSuchElementException(); 
    return entry.getKey();
  }
  
  @CheckForNull
  public Map.Entry<K, V> lastEntry() {
    return delegate().lastEntry();
  }
  
  @CheckForNull
  protected Map.Entry<K, V> standardLastEntry() {
    return Iterables.<Map.Entry<K, V>>getFirst(descendingMap().entrySet(), null);
  }
  
  protected K standardLastKey() {
    Map.Entry<K, V> entry = lastEntry();
    if (entry == null)
      throw new NoSuchElementException(); 
    return entry.getKey();
  }
  
  @CheckForNull
  public Map.Entry<K, V> pollFirstEntry() {
    return delegate().pollFirstEntry();
  }
  
  @CheckForNull
  protected Map.Entry<K, V> standardPollFirstEntry() {
    return Iterators.<Map.Entry<K, V>>pollNext(entrySet().iterator());
  }
  
  @CheckForNull
  public Map.Entry<K, V> pollLastEntry() {
    return delegate().pollLastEntry();
  }
  
  @CheckForNull
  protected Map.Entry<K, V> standardPollLastEntry() {
    return Iterators.<Map.Entry<K, V>>pollNext(descendingMap().entrySet().iterator());
  }
  
  public NavigableMap<K, V> descendingMap() {
    return delegate().descendingMap();
  }
  
  protected class StandardDescendingMap extends Maps.DescendingMap<K, V> {
    NavigableMap<K, V> forward() {
      return ForwardingNavigableMap.this;
    }
    
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
      forward().replaceAll(function);
    }
    
    protected Iterator<Map.Entry<K, V>> entryIterator() {
      return new Iterator<Map.Entry<K, V>>() {
          @CheckForNull
          private Map.Entry<K, V> toRemove = null;
          
          @CheckForNull
          private Map.Entry<K, V> nextOrNull = ForwardingNavigableMap.StandardDescendingMap.this.forward().lastEntry();
          
          public boolean hasNext() {
            return (this.nextOrNull != null);
          }
          
          public Map.Entry<K, V> next() {
            if (this.nextOrNull == null)
              throw new NoSuchElementException(); 
            try {
              return this.nextOrNull;
            } finally {
              this.toRemove = this.nextOrNull;
              this.nextOrNull = ForwardingNavigableMap.StandardDescendingMap.this.forward().lowerEntry(this.nextOrNull.getKey());
            } 
          }
          
          public void remove() {
            if (this.toRemove == null)
              throw new IllegalStateException("no calls to next() since the last call to remove()"); 
            ForwardingNavigableMap.StandardDescendingMap.this.forward().remove(this.toRemove.getKey());
            this.toRemove = null;
          }
        };
    }
  }
  
  public NavigableSet<K> navigableKeySet() {
    return delegate().navigableKeySet();
  }
  
  protected class StandardNavigableKeySet extends Maps.NavigableKeySet<K, V> {
    public StandardNavigableKeySet(ForwardingNavigableMap<K, V> this$0) {
      super(this$0);
    }
  }
  
  public NavigableSet<K> descendingKeySet() {
    return delegate().descendingKeySet();
  }
  
  protected NavigableSet<K> standardDescendingKeySet() {
    return descendingMap().navigableKeySet();
  }
  
  protected SortedMap<K, V> standardSubMap(@ParametricNullness K fromKey, @ParametricNullness K toKey) {
    return subMap(fromKey, true, toKey, false);
  }
  
  public NavigableMap<K, V> subMap(@ParametricNullness K fromKey, boolean fromInclusive, @ParametricNullness K toKey, boolean toInclusive) {
    return delegate().subMap(fromKey, fromInclusive, toKey, toInclusive);
  }
  
  public NavigableMap<K, V> headMap(@ParametricNullness K toKey, boolean inclusive) {
    return delegate().headMap(toKey, inclusive);
  }
  
  public NavigableMap<K, V> tailMap(@ParametricNullness K fromKey, boolean inclusive) {
    return delegate().tailMap(fromKey, inclusive);
  }
  
  protected SortedMap<K, V> standardHeadMap(@ParametricNullness K toKey) {
    return headMap(toKey, false);
  }
  
  protected SortedMap<K, V> standardTailMap(@ParametricNullness K fromKey) {
    return tailMap(fromKey, true);
  }
  
  protected abstract NavigableMap<K, V> delegate();
}
