package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingSortedMap<K, V> extends ForwardingMap<K, V> implements SortedMap<K, V> {
  @CheckForNull
  public Comparator<? super K> comparator() {
    return delegate().comparator();
  }
  
  @ParametricNullness
  public K firstKey() {
    return delegate().firstKey();
  }
  
  public SortedMap<K, V> headMap(@ParametricNullness K toKey) {
    return delegate().headMap(toKey);
  }
  
  @ParametricNullness
  public K lastKey() {
    return delegate().lastKey();
  }
  
  public SortedMap<K, V> subMap(@ParametricNullness K fromKey, @ParametricNullness K toKey) {
    return delegate().subMap(fromKey, toKey);
  }
  
  public SortedMap<K, V> tailMap(@ParametricNullness K fromKey) {
    return delegate().tailMap(fromKey);
  }
  
  protected class StandardKeySet extends Maps.SortedKeySet<K, V> {
    public StandardKeySet(ForwardingSortedMap<K, V> this$0) {
      super(this$0);
    }
  }
  
  static int unsafeCompare(@CheckForNull Comparator<?> comparator, @CheckForNull Object o1, @CheckForNull Object o2) {
    if (comparator == null)
      return ((Comparable<Object>)o1).compareTo(o2); 
    return comparator.compare(o1, o2);
  }
  
  protected boolean standardContainsKey(@CheckForNull Object key) {
    try {
      ForwardingSortedMap<K, V> forwardingSortedMap = this;
      Object ceilingKey = forwardingSortedMap.tailMap((K)key).firstKey();
      return (unsafeCompare(comparator(), ceilingKey, key) == 0);
    } catch (ClassCastException|java.util.NoSuchElementException|NullPointerException e) {
      return false;
    } 
  }
  
  protected SortedMap<K, V> standardSubMap(K fromKey, K toKey) {
    Preconditions.checkArgument((unsafeCompare(comparator(), fromKey, toKey) <= 0), "fromKey must be <= toKey");
    return tailMap(fromKey).headMap(toKey);
  }
  
  protected abstract SortedMap<K, V> delegate();
}
