package me.syncwrld.booter.libs.google.guava.collect;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotCall;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true, emulated = true)
public final class ImmutableSortedMap<K, V> extends ImmutableMap<K, V> implements NavigableMap<K, V> {
  public static <T, K, V> Collector<T, ?, ImmutableSortedMap<K, V>> toImmutableSortedMap(Comparator<? super K> comparator, Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction) {
    return CollectCollectors.toImmutableSortedMap(comparator, keyFunction, valueFunction);
  }
  
  public static <T, K, V> Collector<T, ?, ImmutableSortedMap<K, V>> toImmutableSortedMap(Comparator<? super K> comparator, Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction, BinaryOperator<V> mergeFunction) {
    return CollectCollectors.toImmutableSortedMap(comparator, keyFunction, valueFunction, mergeFunction);
  }
  
  private static final Comparator<Comparable> NATURAL_ORDER = Ordering.natural();
  
  private static final ImmutableSortedMap<Comparable, Object> NATURAL_EMPTY_MAP = new ImmutableSortedMap(
      
      ImmutableSortedSet.emptySet(Ordering.natural()), ImmutableList.of());
  
  private final transient RegularImmutableSortedSet<K> keySet;
  
  private final transient ImmutableList<V> valueList;
  
  @CheckForNull
  private transient ImmutableSortedMap<K, V> descendingMap;
  
  private static final long serialVersionUID = 0L;
  
  static <K, V> ImmutableSortedMap<K, V> emptyMap(Comparator<? super K> comparator) {
    if (Ordering.<Comparable>natural().equals(comparator))
      return of(); 
    return new ImmutableSortedMap<>(
        ImmutableSortedSet.emptySet(comparator), ImmutableList.of());
  }
  
  public static <K, V> ImmutableSortedMap<K, V> of() {
    return (ImmutableSortedMap)NATURAL_EMPTY_MAP;
  }
  
  public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1) {
    return of(Ordering.natural(), k1, v1);
  }
  
  private static <K, V> ImmutableSortedMap<K, V> of(Comparator<? super K> comparator, K k1, V v1) {
    return new ImmutableSortedMap<>(new RegularImmutableSortedSet<>(
          ImmutableList.of(k1), (Comparator<? super K>)Preconditions.checkNotNull(comparator)), 
        ImmutableList.of(v1));
  }
  
  public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2) {
    return fromEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2) });
  }
  
  public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
    return fromEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3) });
  }
  
  public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    return fromEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4) });
  }
  
  public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
    return fromEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4), entryOf(k5, v5) });
  }
  
  public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
    return fromEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), 
          entryOf(k2, v2), 
          entryOf(k3, v3), 
          entryOf(k4, v4), 
          entryOf(k5, v5), 
          entryOf(k6, v6) });
  }
  
  public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
    return fromEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), 
          entryOf(k2, v2), 
          entryOf(k3, v3), 
          entryOf(k4, v4), 
          entryOf(k5, v5), 
          entryOf(k6, v6), 
          entryOf(k7, v7) });
  }
  
  public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8) {
    return fromEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), 
          entryOf(k2, v2), 
          entryOf(k3, v3), 
          entryOf(k4, v4), 
          entryOf(k5, v5), 
          entryOf(k6, v6), 
          entryOf(k7, v7), 
          entryOf(k8, v8) });
  }
  
  public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
    return fromEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), 
          entryOf(k2, v2), 
          entryOf(k3, v3), 
          entryOf(k4, v4), 
          entryOf(k5, v5), 
          entryOf(k6, v6), 
          entryOf(k7, v7), 
          entryOf(k8, v8), 
          entryOf(k9, v9) });
  }
  
  public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10) {
    return fromEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), 
          entryOf(k2, v2), 
          entryOf(k3, v3), 
          entryOf(k4, v4), 
          entryOf(k5, v5), 
          entryOf(k6, v6), 
          entryOf(k7, v7), 
          entryOf(k8, v8), 
          entryOf(k9, v9), 
          entryOf(k10, v10) });
  }
  
  public static <K, V> ImmutableSortedMap<K, V> copyOf(Map<? extends K, ? extends V> map) {
    Ordering<K> naturalOrder = (Ordering)NATURAL_ORDER;
    return copyOfInternal(map, naturalOrder);
  }
  
  public static <K, V> ImmutableSortedMap<K, V> copyOf(Map<? extends K, ? extends V> map, Comparator<? super K> comparator) {
    return copyOfInternal(map, (Comparator<? super K>)Preconditions.checkNotNull(comparator));
  }
  
  public static <K, V> ImmutableSortedMap<K, V> copyOf(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
    Ordering<K> naturalOrder = (Ordering)NATURAL_ORDER;
    return copyOf(entries, naturalOrder);
  }
  
  public static <K, V> ImmutableSortedMap<K, V> copyOf(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries, Comparator<? super K> comparator) {
    return fromEntries((Comparator<? super K>)Preconditions.checkNotNull(comparator), false, entries);
  }
  
  public static <K, V> ImmutableSortedMap<K, V> copyOfSorted(SortedMap<K, ? extends V> map) {
    Comparator<Comparable> comparator1;
    Comparator<? super K> comparator = map.comparator();
    if (comparator == null)
      comparator1 = NATURAL_ORDER; 
    if (map instanceof ImmutableSortedMap) {
      ImmutableSortedMap<K, V> kvMap = (ImmutableSortedMap)map;
      if (!kvMap.isPartialView())
        return kvMap; 
    } 
    return fromEntries((Comparator)comparator1, true, map.entrySet());
  }
  
  private static <K, V> ImmutableSortedMap<K, V> copyOfInternal(Map<? extends K, ? extends V> map, Comparator<? super K> comparator) {
    boolean sameComparator = false;
    if (map instanceof SortedMap) {
      SortedMap<?, ?> sortedMap = (SortedMap<?, ?>)map;
      Comparator<?> comparator2 = sortedMap.comparator();
      sameComparator = (comparator2 == null) ? ((comparator == NATURAL_ORDER)) : comparator.equals(comparator2);
    } 
    if (sameComparator && map instanceof ImmutableSortedMap) {
      ImmutableSortedMap<K, V> kvMap = (ImmutableSortedMap)map;
      if (!kvMap.isPartialView())
        return kvMap; 
    } 
    return fromEntries(comparator, sameComparator, map.entrySet());
  }
  
  private static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> fromEntries(Map.Entry<K, V>... entries) {
    return fromEntries(Ordering.natural(), false, entries, entries.length);
  }
  
  private static <K, V> ImmutableSortedMap<K, V> fromEntries(Comparator<? super K> comparator, boolean sameComparator, Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
    Map.Entry[] arrayOfEntry = Iterables.<Map.Entry>toArray((Iterable)entries, (Map.Entry[])EMPTY_ENTRY_ARRAY);
    return fromEntries(comparator, sameComparator, (Map.Entry<K, V>[])arrayOfEntry, arrayOfEntry.length);
  }
  
  private static <K, V> ImmutableSortedMap<K, V> fromEntries(Comparator<? super K> comparator, boolean sameComparator, Map.Entry<K, V>[] entryArray, int size) {
    Map.Entry<K, V> onlyEntry;
    switch (size) {
      case 0:
        return emptyMap(comparator);
      case 1:
        onlyEntry = Objects.<Map.Entry<K, V>>requireNonNull(entryArray[0]);
        return of(comparator, onlyEntry.getKey(), onlyEntry.getValue());
    } 
    Object[] keys = new Object[size];
    Object[] values = new Object[size];
    if (sameComparator) {
      for (int i = 0; i < size; i++) {
        Map.Entry<K, V> entry = Objects.<Map.Entry<K, V>>requireNonNull(entryArray[i]);
        Object key = entry.getKey();
        Object value = entry.getValue();
        CollectPreconditions.checkEntryNotNull(key, value);
        keys[i] = key;
        values[i] = value;
      } 
    } else {
      Arrays.sort(entryArray, 0, size, (e1, e2) -> {
            Objects.requireNonNull(e1);
            Objects.requireNonNull(e2);
            return comparator.compare(e1.getKey(), e2.getKey());
          });
      Map.Entry<K, V> firstEntry = Objects.<Map.Entry<K, V>>requireNonNull(entryArray[0]);
      K prevKey = firstEntry.getKey();
      keys[0] = prevKey;
      values[0] = firstEntry.getValue();
      CollectPreconditions.checkEntryNotNull(keys[0], values[0]);
      for (int i = 1; i < size; i++) {
        Map.Entry<K, V> prevEntry = Objects.<Map.Entry<K, V>>requireNonNull(entryArray[i - 1]);
        Map.Entry<K, V> entry = Objects.<Map.Entry<K, V>>requireNonNull(entryArray[i]);
        K key = entry.getKey();
        V value = entry.getValue();
        CollectPreconditions.checkEntryNotNull(key, value);
        keys[i] = key;
        values[i] = value;
        checkNoConflict((comparator.compare(prevKey, key) != 0), "key", prevEntry, entry);
        prevKey = key;
      } 
    } 
    return new ImmutableSortedMap<>(new RegularImmutableSortedSet<>(new RegularImmutableList<>(keys), comparator), new RegularImmutableList<>(values));
  }
  
  public static <K extends Comparable<?>, V> Builder<K, V> naturalOrder() {
    return new Builder<>(Ordering.natural());
  }
  
  public static <K, V> Builder<K, V> orderedBy(Comparator<K> comparator) {
    return new Builder<>(comparator);
  }
  
  public static <K extends Comparable<?>, V> Builder<K, V> reverseOrder() {
    return new Builder<>(Ordering.<Comparable>natural().reverse());
  }
  
  public static class Builder<K, V> extends ImmutableMap.Builder<K, V> {
    private final Comparator<? super K> comparator;
    
    public Builder(Comparator<? super K> comparator) {
      this.comparator = (Comparator<? super K>)Preconditions.checkNotNull(comparator);
    }
    
    @CanIgnoreReturnValue
    public Builder<K, V> put(K key, V value) {
      super.put(key, value);
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<K, V> put(Map.Entry<? extends K, ? extends V> entry) {
      super.put(entry);
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<K, V> putAll(Map<? extends K, ? extends V> map) {
      super.putAll(map);
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<K, V> putAll(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
      super.putAll(entries);
      return this;
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @DoNotCall("Always throws UnsupportedOperationException")
    public final Builder<K, V> orderEntriesByValue(Comparator<? super V> valueComparator) {
      throw new UnsupportedOperationException("Not available on ImmutableSortedMap.Builder");
    }
    
    Builder<K, V> combine(ImmutableMap.Builder<K, V> other) {
      super.combine(other);
      return this;
    }
    
    public ImmutableSortedMap<K, V> build() {
      return buildOrThrow();
    }
    
    public ImmutableSortedMap<K, V> buildOrThrow() {
      Map.Entry<K, V> onlyEntry;
      switch (this.size) {
        case 0:
          return ImmutableSortedMap.emptyMap(this.comparator);
        case 1:
          onlyEntry = Objects.<Map.Entry<K, V>>requireNonNull(this.entries[0]);
          return ImmutableSortedMap.of(this.comparator, onlyEntry.getKey(), onlyEntry.getValue());
      } 
      return ImmutableSortedMap.fromEntries(this.comparator, false, this.entries, this.size);
    }
    
    @Deprecated
    @DoNotCall
    public final ImmutableSortedMap<K, V> buildKeepingLast() {
      throw new UnsupportedOperationException("ImmutableSortedMap.Builder does not yet implement buildKeepingLast()");
    }
  }
  
  ImmutableSortedMap(RegularImmutableSortedSet<K> keySet, ImmutableList<V> valueList) {
    this(keySet, valueList, (ImmutableSortedMap<K, V>)null);
  }
  
  ImmutableSortedMap(RegularImmutableSortedSet<K> keySet, ImmutableList<V> valueList, @CheckForNull ImmutableSortedMap<K, V> descendingMap) {
    this.keySet = keySet;
    this.valueList = valueList;
    this.descendingMap = descendingMap;
  }
  
  public int size() {
    return this.valueList.size();
  }
  
  public void forEach(BiConsumer<? super K, ? super V> action) {
    Preconditions.checkNotNull(action);
    ImmutableList<K> keyList = this.keySet.asList();
    for (int i = 0; i < size(); i++)
      action.accept(keyList.get(i), this.valueList.get(i)); 
  }
  
  @CheckForNull
  public V get(@CheckForNull Object key) {
    int index = this.keySet.indexOf(key);
    return (index == -1) ? null : this.valueList.get(index);
  }
  
  boolean isPartialView() {
    return (this.keySet.isPartialView() || this.valueList.isPartialView());
  }
  
  public ImmutableSet<Map.Entry<K, V>> entrySet() {
    return super.entrySet();
  }
  
  ImmutableSet<Map.Entry<K, V>> createEntrySet() {
    class EntrySet extends ImmutableMapEntrySet<K, V> {
      public UnmodifiableIterator<Map.Entry<K, V>> iterator() {
        return asList().iterator();
      }
      
      public Spliterator<Map.Entry<K, V>> spliterator() {
        return asList().spliterator();
      }
      
      public void forEach(Consumer<? super Map.Entry<K, V>> action) {
        asList().forEach(action);
      }
      
      ImmutableList<Map.Entry<K, V>> createAsList() {
        return new ImmutableAsList<Map.Entry<K, V>>() {
            public Map.Entry<K, V> get(int index) {
              return new AbstractMap.SimpleImmutableEntry<>(ImmutableSortedMap.this
                  .keySet.asList().get(index), (V)ImmutableSortedMap.this.valueList.get(index));
            }
            
            public Spliterator<Map.Entry<K, V>> spliterator() {
              return CollectSpliterators.indexed(
                  size(), 1297, this::get);
            }
            
            ImmutableCollection<Map.Entry<K, V>> delegateCollection() {
              return ImmutableSortedMap.EntrySet.this;
            }
            
            @J2ktIncompatible
            @GwtIncompatible
            Object writeReplace() {
              return super.writeReplace();
            }
          };
      }
      
      ImmutableMap<K, V> map() {
        return ImmutableSortedMap.this;
      }
      
      @J2ktIncompatible
      @GwtIncompatible
      Object writeReplace() {
        return super.writeReplace();
      }
    };
    return isEmpty() ? ImmutableSet.<Map.Entry<K, V>>of() : new EntrySet();
  }
  
  public ImmutableSortedSet<K> keySet() {
    return this.keySet;
  }
  
  ImmutableSet<K> createKeySet() {
    throw new AssertionError("should never be called");
  }
  
  public ImmutableCollection<V> values() {
    return this.valueList;
  }
  
  ImmutableCollection<V> createValues() {
    throw new AssertionError("should never be called");
  }
  
  public Comparator<? super K> comparator() {
    return keySet().comparator();
  }
  
  public K firstKey() {
    return keySet().first();
  }
  
  public K lastKey() {
    return keySet().last();
  }
  
  private ImmutableSortedMap<K, V> getSubMap(int fromIndex, int toIndex) {
    if (fromIndex == 0 && toIndex == size())
      return this; 
    if (fromIndex == toIndex)
      return emptyMap(comparator()); 
    return new ImmutableSortedMap(this.keySet
        .getSubSet(fromIndex, toIndex), this.valueList.subList(fromIndex, toIndex));
  }
  
  public ImmutableSortedMap<K, V> headMap(K toKey) {
    return headMap(toKey, false);
  }
  
  public ImmutableSortedMap<K, V> headMap(K toKey, boolean inclusive) {
    return getSubMap(0, this.keySet.headIndex((K)Preconditions.checkNotNull(toKey), inclusive));
  }
  
  public ImmutableSortedMap<K, V> subMap(K fromKey, K toKey) {
    return subMap(fromKey, true, toKey, false);
  }
  
  public ImmutableSortedMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
    Preconditions.checkNotNull(fromKey);
    Preconditions.checkNotNull(toKey);
    Preconditions.checkArgument(
        (comparator().compare(fromKey, toKey) <= 0), "expected fromKey <= toKey but %s > %s", fromKey, toKey);
    return headMap(toKey, toInclusive).tailMap(fromKey, fromInclusive);
  }
  
  public ImmutableSortedMap<K, V> tailMap(K fromKey) {
    return tailMap(fromKey, true);
  }
  
  public ImmutableSortedMap<K, V> tailMap(K fromKey, boolean inclusive) {
    return getSubMap(this.keySet.tailIndex((K)Preconditions.checkNotNull(fromKey), inclusive), size());
  }
  
  @CheckForNull
  public Map.Entry<K, V> lowerEntry(K key) {
    return headMap(key, false).lastEntry();
  }
  
  @CheckForNull
  public K lowerKey(K key) {
    return Maps.keyOrNull(lowerEntry(key));
  }
  
  @CheckForNull
  public Map.Entry<K, V> floorEntry(K key) {
    return headMap(key, true).lastEntry();
  }
  
  @CheckForNull
  public K floorKey(K key) {
    return Maps.keyOrNull(floorEntry(key));
  }
  
  @CheckForNull
  public Map.Entry<K, V> ceilingEntry(K key) {
    return tailMap(key, true).firstEntry();
  }
  
  @CheckForNull
  public K ceilingKey(K key) {
    return Maps.keyOrNull(ceilingEntry(key));
  }
  
  @CheckForNull
  public Map.Entry<K, V> higherEntry(K key) {
    return tailMap(key, false).firstEntry();
  }
  
  @CheckForNull
  public K higherKey(K key) {
    return Maps.keyOrNull(higherEntry(key));
  }
  
  @CheckForNull
  public Map.Entry<K, V> firstEntry() {
    return isEmpty() ? null : entrySet().asList().get(0);
  }
  
  @CheckForNull
  public Map.Entry<K, V> lastEntry() {
    return isEmpty() ? null : entrySet().asList().get(size() - 1);
  }
  
  @Deprecated
  @CheckForNull
  @CanIgnoreReturnValue
  @DoNotCall("Always throws UnsupportedOperationException")
  public final Map.Entry<K, V> pollFirstEntry() {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @CheckForNull
  @CanIgnoreReturnValue
  @DoNotCall("Always throws UnsupportedOperationException")
  public final Map.Entry<K, V> pollLastEntry() {
    throw new UnsupportedOperationException();
  }
  
  public ImmutableSortedMap<K, V> descendingMap() {
    ImmutableSortedMap<K, V> result = this.descendingMap;
    if (result == null) {
      if (isEmpty())
        return emptyMap(Ordering.<K>from(comparator()).reverse()); 
      return new ImmutableSortedMap((RegularImmutableSortedSet<K>)this.keySet
          .descendingSet(), this.valueList.reverse(), this);
    } 
    return result;
  }
  
  public ImmutableSortedSet<K> navigableKeySet() {
    return this.keySet;
  }
  
  public ImmutableSortedSet<K> descendingKeySet() {
    return this.keySet.descendingSet();
  }
  
  @J2ktIncompatible
  private static class SerializedForm<K, V> extends ImmutableMap.SerializedForm<K, V> {
    private final Comparator<? super K> comparator;
    
    private static final long serialVersionUID = 0L;
    
    SerializedForm(ImmutableSortedMap<K, V> sortedMap) {
      super(sortedMap);
      this.comparator = sortedMap.comparator();
    }
    
    ImmutableSortedMap.Builder<K, V> makeBuilder(int size) {
      return new ImmutableSortedMap.Builder<>(this.comparator);
    }
  }
  
  @J2ktIncompatible
  Object writeReplace() {
    return new SerializedForm<>(this);
  }
  
  @J2ktIncompatible
  private void readObject(ObjectInputStream stream) throws InvalidObjectException {
    throw new InvalidObjectException("Use SerializedForm");
  }
  
  @Deprecated
  @DoNotCall("Use toImmutableSortedMap")
  public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("Use toImmutableSortedMap")
  public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction, BinaryOperator<V> mergeFunction) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("Use naturalOrder")
  public static <K, V> Builder<K, V> builder() {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("Use naturalOrder (which does not accept an expected size)")
  public static <K, V> Builder<K, V> builderWithExpectedSize(int expectedSize) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("Pass a key of type Comparable")
  public static <K, V> ImmutableSortedMap<K, V> of(K k1, V v1) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("Pass keys of type Comparable")
  public static <K, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("Pass keys of type Comparable")
  public static <K, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("Pass keys of type Comparable")
  public static <K, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("Pass keys of type Comparable")
  public static <K, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("Pass keys of type Comparable")
  public static <K, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("Pass keys of type Comparable")
  public static <K, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("Pass keys of type Comparable")
  public static <K, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("Pass keys of type Comparable")
  public static <K, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("Pass keys of type Comparable")
  public static <K, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("ImmutableSortedMap.ofEntries not currently available; use ImmutableSortedMap.copyOf")
  public static <K, V> ImmutableSortedMap<K, V> ofEntries(Map.Entry<? extends K, ? extends V>... entries) {
    throw new UnsupportedOperationException();
  }
}
