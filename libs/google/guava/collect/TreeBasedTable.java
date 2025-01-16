package me.syncwrld.booter.libs.google.guava.collect;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.base.Supplier;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true)
public class TreeBasedTable<R, C, V> extends StandardRowSortedTable<R, C, V> {
  private final Comparator<? super C> columnComparator;
  
  private static final long serialVersionUID = 0L;
  
  private static class Factory<C, V> implements Supplier<TreeMap<C, V>>, Serializable {
    final Comparator<? super C> comparator;
    
    private static final long serialVersionUID = 0L;
    
    Factory(Comparator<? super C> comparator) {
      this.comparator = comparator;
    }
    
    public TreeMap<C, V> get() {
      return new TreeMap<>(this.comparator);
    }
  }
  
  public static <R extends Comparable, C extends Comparable, V> TreeBasedTable<R, C, V> create() {
    return new TreeBasedTable<>(Ordering.natural(), Ordering.natural());
  }
  
  public static <R, C, V> TreeBasedTable<R, C, V> create(Comparator<? super R> rowComparator, Comparator<? super C> columnComparator) {
    Preconditions.checkNotNull(rowComparator);
    Preconditions.checkNotNull(columnComparator);
    return new TreeBasedTable<>(rowComparator, columnComparator);
  }
  
  public static <R, C, V> TreeBasedTable<R, C, V> create(TreeBasedTable<R, C, ? extends V> table) {
    TreeBasedTable<R, C, V> result = new TreeBasedTable<>(table.rowComparator(), table.columnComparator());
    result.putAll(table);
    return result;
  }
  
  TreeBasedTable(Comparator<? super R> rowComparator, Comparator<? super C> columnComparator) {
    super(new TreeMap<>(rowComparator), (Supplier)new Factory<>(columnComparator));
    this.columnComparator = columnComparator;
  }
  
  @Deprecated
  public Comparator<? super R> rowComparator() {
    return Objects.<Comparator<? super R>>requireNonNull(rowKeySet().comparator());
  }
  
  @Deprecated
  public Comparator<? super C> columnComparator() {
    return this.columnComparator;
  }
  
  public SortedMap<C, V> row(R rowKey) {
    return new TreeRow(rowKey);
  }
  
  private class TreeRow extends StandardTable<R, C, V>.Row implements SortedMap<C, V> {
    @CheckForNull
    final C lowerBound;
    
    @CheckForNull
    final C upperBound;
    
    @CheckForNull
    transient SortedMap<C, V> wholeRow;
    
    TreeRow(R rowKey) {
      this(rowKey, null, null);
    }
    
    TreeRow(@CheckForNull R rowKey, @CheckForNull C lowerBound, C upperBound) {
      super(rowKey);
      this.lowerBound = lowerBound;
      this.upperBound = upperBound;
      Preconditions.checkArgument((lowerBound == null || upperBound == null || 
          compare(lowerBound, upperBound) <= 0));
    }
    
    public SortedSet<C> keySet() {
      return new Maps.SortedKeySet<>(this);
    }
    
    public Comparator<? super C> comparator() {
      return TreeBasedTable.this.columnComparator();
    }
    
    int compare(Object a, Object b) {
      Comparator<Object> cmp = (Comparator)comparator();
      return cmp.compare(a, b);
    }
    
    boolean rangeContains(@CheckForNull Object o) {
      return (o != null && (this.lowerBound == null || 
        compare(this.lowerBound, o) <= 0) && (this.upperBound == null || 
        compare(this.upperBound, o) > 0));
    }
    
    public SortedMap<C, V> subMap(C fromKey, C toKey) {
      Preconditions.checkArgument((rangeContains(Preconditions.checkNotNull(fromKey)) && rangeContains(Preconditions.checkNotNull(toKey))));
      return new TreeRow(this.rowKey, fromKey, toKey);
    }
    
    public SortedMap<C, V> headMap(C toKey) {
      Preconditions.checkArgument(rangeContains(Preconditions.checkNotNull(toKey)));
      return new TreeRow(this.rowKey, this.lowerBound, toKey);
    }
    
    public SortedMap<C, V> tailMap(C fromKey) {
      Preconditions.checkArgument(rangeContains(Preconditions.checkNotNull(fromKey)));
      return new TreeRow(this.rowKey, fromKey, this.upperBound);
    }
    
    public C firstKey() {
      updateBackingRowMapField();
      if (this.backingRowMap == null)
        throw new NoSuchElementException(); 
      return (C)((SortedMap)this.backingRowMap).firstKey();
    }
    
    public C lastKey() {
      updateBackingRowMapField();
      if (this.backingRowMap == null)
        throw new NoSuchElementException(); 
      return (C)((SortedMap)this.backingRowMap).lastKey();
    }
    
    void updateWholeRowField() {
      if (this.wholeRow == null || (this.wholeRow.isEmpty() && TreeBasedTable.this.backingMap.containsKey(this.rowKey)))
        this.wholeRow = (SortedMap<C, V>)TreeBasedTable.this.backingMap.get(this.rowKey); 
    }
    
    @CheckForNull
    SortedMap<C, V> computeBackingRowMap() {
      updateWholeRowField();
      SortedMap<C, V> map = this.wholeRow;
      if (map != null) {
        if (this.lowerBound != null)
          map = map.tailMap(this.lowerBound); 
        if (this.upperBound != null)
          map = map.headMap(this.upperBound); 
        return map;
      } 
      return null;
    }
    
    void maintainEmptyInvariant() {
      updateWholeRowField();
      if (this.wholeRow != null && this.wholeRow.isEmpty()) {
        TreeBasedTable.this.backingMap.remove(this.rowKey);
        this.wholeRow = null;
        this.backingRowMap = null;
      } 
    }
    
    public boolean containsKey(@CheckForNull Object key) {
      return (rangeContains(key) && super.containsKey(key));
    }
    
    @CheckForNull
    public V put(C key, V value) {
      Preconditions.checkArgument(rangeContains(Preconditions.checkNotNull(key)));
      return super.put(key, value);
    }
  }
  
  public SortedSet<R> rowKeySet() {
    return super.rowKeySet();
  }
  
  public SortedMap<R, Map<C, V>> rowMap() {
    return super.rowMap();
  }
  
  Iterator<C> createColumnKeyIterator() {
    final Comparator<? super C> comparator = columnComparator();
    final Iterator<C> merged = Iterators.mergeSorted(
        Iterables.transform(this.backingMap
          .values(), input -> input.keySet().iterator()), comparator);
    return new AbstractIterator<C>(this) {
        @CheckForNull
        C lastValue;
        
        @CheckForNull
        protected C computeNext() {
          while (merged.hasNext()) {
            C next = merged.next();
            boolean duplicate = (this.lastValue != null && comparator.compare(next, this.lastValue) == 0);
            if (!duplicate) {
              this.lastValue = next;
              return this.lastValue;
            } 
          } 
          this.lastValue = null;
          return endOfData();
        }
      };
  }
}
