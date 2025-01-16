package me.syncwrld.booter.libs.google.guava.collect;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotCall;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.LazyInit;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.j2objc.annotations.RetainedWith;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@DoNotMock("Use ImmutableMap.of or another implementation")
@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true, emulated = true)
public abstract class ImmutableMap<K, V> implements Map<K, V>, Serializable {
  public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction) {
    return CollectCollectors.toImmutableMap(keyFunction, valueFunction);
  }
  
  public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction, BinaryOperator<V> mergeFunction) {
    return CollectCollectors.toImmutableMap(keyFunction, valueFunction, mergeFunction);
  }
  
  public static <K, V> ImmutableMap<K, V> of() {
    return (ImmutableMap)RegularImmutableMap.EMPTY;
  }
  
  public static <K, V> ImmutableMap<K, V> of(K k1, V v1) {
    return ImmutableBiMap.of(k1, v1);
  }
  
  public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2) {
    return RegularImmutableMap.fromEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2) });
  }
  
  public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
    return RegularImmutableMap.fromEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3) });
  }
  
  public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    return RegularImmutableMap.fromEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4) });
  }
  
  public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
    return RegularImmutableMap.fromEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4), entryOf(k5, v5) });
  }
  
  public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
    return RegularImmutableMap.fromEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), 
          entryOf(k2, v2), 
          entryOf(k3, v3), 
          entryOf(k4, v4), 
          entryOf(k5, v5), 
          entryOf(k6, v6) });
  }
  
  public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
    return RegularImmutableMap.fromEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), 
          entryOf(k2, v2), 
          entryOf(k3, v3), 
          entryOf(k4, v4), 
          entryOf(k5, v5), 
          entryOf(k6, v6), 
          entryOf(k7, v7) });
  }
  
  public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8) {
    return RegularImmutableMap.fromEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), 
          entryOf(k2, v2), 
          entryOf(k3, v3), 
          entryOf(k4, v4), 
          entryOf(k5, v5), 
          entryOf(k6, v6), 
          entryOf(k7, v7), 
          entryOf(k8, v8) });
  }
  
  public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
    return RegularImmutableMap.fromEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), 
          entryOf(k2, v2), 
          entryOf(k3, v3), 
          entryOf(k4, v4), 
          entryOf(k5, v5), 
          entryOf(k6, v6), 
          entryOf(k7, v7), 
          entryOf(k8, v8), 
          entryOf(k9, v9) });
  }
  
  public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10) {
    return RegularImmutableMap.fromEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), 
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
  
  @SafeVarargs
  public static <K, V> ImmutableMap<K, V> ofEntries(Map.Entry<? extends K, ? extends V>... entries) {
    Map.Entry<? extends K, ? extends V>[] arrayOfEntry = entries;
    return RegularImmutableMap.fromEntries((Map.Entry[])arrayOfEntry);
  }
  
  static <K, V> Map.Entry<K, V> entryOf(K key, V value) {
    return new ImmutableMapEntry<>(key, value);
  }
  
  public static <K, V> Builder<K, V> builder() {
    return new Builder<>();
  }
  
  public static <K, V> Builder<K, V> builderWithExpectedSize(int expectedSize) {
    CollectPreconditions.checkNonnegative(expectedSize, "expectedSize");
    return new Builder<>(expectedSize);
  }
  
  static void checkNoConflict(boolean safe, String conflictDescription, Object entry1, Object entry2) {
    if (!safe)
      throw conflictException(conflictDescription, entry1, entry2); 
  }
  
  static IllegalArgumentException conflictException(String conflictDescription, Object entry1, Object entry2) {
    return new IllegalArgumentException("Multiple entries with same " + conflictDescription + ": " + entry1 + " and " + entry2);
  }
  
  @DoNotMock
  public static class Builder<K, V> {
    @CheckForNull
    Comparator<? super V> valueComparator;
    
    Map.Entry<K, V>[] entries;
    
    int size;
    
    boolean entriesUsed;
    
    public Builder() {
      this(4);
    }
    
    Builder(int initialCapacity) {
      this.entries = (Map.Entry<K, V>[])new Map.Entry[initialCapacity];
      this.size = 0;
      this.entriesUsed = false;
    }
    
    private void ensureCapacity(int minCapacity) {
      if (minCapacity > this.entries.length) {
        this
          .entries = Arrays.<Map.Entry<K, V>>copyOf(this.entries, 
            ImmutableCollection.Builder.expandedCapacity(this.entries.length, minCapacity));
        this.entriesUsed = false;
      } 
    }
    
    @CanIgnoreReturnValue
    public Builder<K, V> put(K key, V value) {
      ensureCapacity(this.size + 1);
      Map.Entry<K, V> entry = ImmutableMap.entryOf(key, value);
      this.entries[this.size++] = entry;
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<K, V> put(Map.Entry<? extends K, ? extends V> entry) {
      return put(entry.getKey(), entry.getValue());
    }
    
    @CanIgnoreReturnValue
    public Builder<K, V> putAll(Map<? extends K, ? extends V> map) {
      return putAll(map.entrySet());
    }
    
    @CanIgnoreReturnValue
    public Builder<K, V> putAll(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
      if (entries instanceof Collection)
        ensureCapacity(this.size + ((Collection)entries).size()); 
      for (Map.Entry<? extends K, ? extends V> entry : entries)
        put(entry); 
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<K, V> orderEntriesByValue(Comparator<? super V> valueComparator) {
      Preconditions.checkState((this.valueComparator == null), "valueComparator was already set");
      this.valueComparator = (Comparator<? super V>)Preconditions.checkNotNull(valueComparator, "valueComparator");
      return this;
    }
    
    @CanIgnoreReturnValue
    Builder<K, V> combine(Builder<K, V> other) {
      Preconditions.checkNotNull(other);
      ensureCapacity(this.size + other.size);
      System.arraycopy(other.entries, 0, this.entries, this.size, other.size);
      this.size += other.size;
      return this;
    }
    
    private ImmutableMap<K, V> build(boolean throwIfDuplicateKeys) {
      Map.Entry<K, V> onlyEntry;
      Map.Entry[] arrayOfEntry;
      switch (this.size) {
        case 0:
          return ImmutableMap.of();
        case 1:
          onlyEntry = Objects.<Map.Entry<K, V>>requireNonNull(this.entries[0]);
          return ImmutableMap.of(onlyEntry.getKey(), onlyEntry.getValue());
      } 
      int localSize = this.size;
      if (this.valueComparator == null) {
        Map.Entry<K, V>[] localEntries = this.entries;
      } else {
        Map.Entry[] arrayOfEntry1;
        if (this.entriesUsed)
          this.entries = Arrays.<Map.Entry<K, V>>copyOf(this.entries, this.size); 
        Map.Entry<K, V>[] nonNullEntries = this.entries;
        if (!throwIfDuplicateKeys) {
          arrayOfEntry1 = lastEntryForEachKey(nonNullEntries, this.size);
          localSize = arrayOfEntry1.length;
        } 
        Arrays.sort(arrayOfEntry1, 0, localSize, 
            
            Ordering.<V>from(this.valueComparator).onResultOf(Maps.valueFunction()));
        arrayOfEntry = arrayOfEntry1;
      } 
      this.entriesUsed = true;
      return RegularImmutableMap.fromEntryArray(localSize, (Map.Entry<K, V>[])arrayOfEntry, throwIfDuplicateKeys);
    }
    
    public ImmutableMap<K, V> build() {
      return buildOrThrow();
    }
    
    public ImmutableMap<K, V> buildOrThrow() {
      return build(true);
    }
    
    public ImmutableMap<K, V> buildKeepingLast() {
      return build(false);
    }
    
    @VisibleForTesting
    ImmutableMap<K, V> buildJdkBacked() {
      Map.Entry<K, V> onlyEntry;
      Preconditions.checkState((this.valueComparator == null), "buildJdkBacked is only for testing; can't use valueComparator");
      switch (this.size) {
        case 0:
          return ImmutableMap.of();
        case 1:
          onlyEntry = Objects.<Map.Entry<K, V>>requireNonNull(this.entries[0]);
          return ImmutableMap.of(onlyEntry.getKey(), onlyEntry.getValue());
      } 
      this.entriesUsed = true;
      return JdkBackedImmutableMap.create(this.size, this.entries, true);
    }
    
    private static <K, V> Map.Entry<K, V>[] lastEntryForEachKey(Map.Entry<K, V>[] entries, int size) {
      Set<K> seen = new HashSet<>();
      BitSet dups = new BitSet();
      for (int i = size - 1; i >= 0; i--) {
        if (!seen.add(entries[i].getKey()))
          dups.set(i); 
      } 
      if (dups.isEmpty())
        return entries; 
      Map.Entry[] arrayOfEntry = new Map.Entry[size - dups.cardinality()];
      for (int inI = 0, outI = 0; inI < size; inI++) {
        if (!dups.get(inI))
          arrayOfEntry[outI++] = entries[inI]; 
      } 
      return (Map.Entry<K, V>[])arrayOfEntry;
    }
  }
  
  public static <K, V> ImmutableMap<K, V> copyOf(Map<? extends K, ? extends V> map) {
    if (map instanceof ImmutableMap && !(map instanceof java.util.SortedMap)) {
      ImmutableMap<K, V> kvMap = (ImmutableMap)map;
      if (!kvMap.isPartialView())
        return kvMap; 
    } else if (map instanceof EnumMap) {
      ImmutableMap<K, V> kvMap = (ImmutableMap)copyOfEnumMap((EnumMap<?, ? extends V>)map);
      return kvMap;
    } 
    return copyOf(map.entrySet());
  }
  
  public static <K, V> ImmutableMap<K, V> copyOf(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
    Map.Entry<K, V> onlyEntry;
    Map.Entry[] arrayOfEntry = Iterables.<Map.Entry>toArray((Iterable)entries, (Map.Entry[])EMPTY_ENTRY_ARRAY);
    switch (arrayOfEntry.length) {
      case 0:
        return of();
      case 1:
        onlyEntry = Objects.<Map.Entry<K, V>>requireNonNull(arrayOfEntry[0]);
        return of(onlyEntry.getKey(), onlyEntry.getValue());
    } 
    return RegularImmutableMap.fromEntries((Map.Entry<K, V>[])arrayOfEntry);
  }
  
  private static <K extends Enum<K>, V> ImmutableMap<K, ? extends V> copyOfEnumMap(EnumMap<?, ? extends V> original) {
    EnumMap<K, V> copy = new EnumMap<>((EnumMap)original);
    for (Map.Entry<K, V> entry : copy.entrySet())
      CollectPreconditions.checkEntryNotNull(entry.getKey(), entry.getValue()); 
    return ImmutableEnumMap.asImmutable(copy);
  }
  
  static final Map.Entry<?, ?>[] EMPTY_ENTRY_ARRAY = (Map.Entry<?, ?>[])new Map.Entry[0];
  
  @LazyInit
  @CheckForNull
  @RetainedWith
  private transient ImmutableSet<Map.Entry<K, V>> entrySet;
  
  @LazyInit
  @CheckForNull
  @RetainedWith
  private transient ImmutableSet<K> keySet;
  
  @LazyInit
  @CheckForNull
  @RetainedWith
  private transient ImmutableCollection<V> values;
  
  @LazyInit
  @CheckForNull
  private transient ImmutableSetMultimap<K, V> multimapView;
  
  private static final long serialVersionUID = -889275714L;
  
  static abstract class IteratorBasedImmutableMap<K, V> extends ImmutableMap<K, V> {
    Spliterator<Map.Entry<K, V>> entrySpliterator() {
      return Spliterators.spliterator(
          entryIterator(), 
          size(), 1297);
    }
    
    ImmutableSet<K> createKeySet() {
      return new ImmutableMapKeySet<>(this);
    }
    
    ImmutableSet<Map.Entry<K, V>> createEntrySet() {
      class EntrySetImpl extends ImmutableMapEntrySet<K, V> {
        ImmutableMap<K, V> map() {
          return ImmutableMap.IteratorBasedImmutableMap.this;
        }
        
        public UnmodifiableIterator<Map.Entry<K, V>> iterator() {
          return ImmutableMap.IteratorBasedImmutableMap.this.entryIterator();
        }
        
        @J2ktIncompatible
        @GwtIncompatible
        Object writeReplace() {
          return super.writeReplace();
        }
      };
      return new EntrySetImpl();
    }
    
    ImmutableCollection<V> createValues() {
      return new ImmutableMapValues<>(this);
    }
    
    @J2ktIncompatible
    @GwtIncompatible
    Object writeReplace() {
      return super.writeReplace();
    }
    
    abstract UnmodifiableIterator<Map.Entry<K, V>> entryIterator();
  }
  
  @Deprecated
  @CheckForNull
  @CanIgnoreReturnValue
  @DoNotCall("Always throws UnsupportedOperationException")
  public final V put(K k, V v) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @CheckForNull
  @CanIgnoreReturnValue
  @DoNotCall("Always throws UnsupportedOperationException")
  public final V putIfAbsent(K key, V value) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("Always throws UnsupportedOperationException")
  public final boolean replace(K key, V oldValue, V newValue) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @CheckForNull
  @DoNotCall("Always throws UnsupportedOperationException")
  public final V replace(K key, V value) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("Always throws UnsupportedOperationException")
  public final V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @CheckForNull
  @DoNotCall("Always throws UnsupportedOperationException")
  public final V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @CheckForNull
  @DoNotCall("Always throws UnsupportedOperationException")
  public final V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @CheckForNull
  @DoNotCall("Always throws UnsupportedOperationException")
  public final V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> function) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("Always throws UnsupportedOperationException")
  public final void putAll(Map<? extends K, ? extends V> map) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("Always throws UnsupportedOperationException")
  public final void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @CheckForNull
  @DoNotCall("Always throws UnsupportedOperationException")
  public final V remove(@CheckForNull Object o) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("Always throws UnsupportedOperationException")
  public final boolean remove(@CheckForNull Object key, @CheckForNull Object value) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("Always throws UnsupportedOperationException")
  public final void clear() {
    throw new UnsupportedOperationException();
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public boolean containsKey(@CheckForNull Object key) {
    return (get(key) != null);
  }
  
  public boolean containsValue(@CheckForNull Object value) {
    return values().contains(value);
  }
  
  @CheckForNull
  public final V getOrDefault(@CheckForNull Object key, @CheckForNull V defaultValue) {
    V result = get(key);
    if (result != null)
      return result; 
    return defaultValue;
  }
  
  public ImmutableSet<Map.Entry<K, V>> entrySet() {
    ImmutableSet<Map.Entry<K, V>> result = this.entrySet;
    return (result == null) ? (this.entrySet = createEntrySet()) : result;
  }
  
  public ImmutableSet<K> keySet() {
    ImmutableSet<K> result = this.keySet;
    return (result == null) ? (this.keySet = createKeySet()) : result;
  }
  
  UnmodifiableIterator<K> keyIterator() {
    final UnmodifiableIterator<Map.Entry<K, V>> entryIterator = entrySet().iterator();
    return new UnmodifiableIterator<K>(this) {
        public boolean hasNext() {
          return entryIterator.hasNext();
        }
        
        public K next() {
          return (K)((Map.Entry)entryIterator.next()).getKey();
        }
      };
  }
  
  Spliterator<K> keySpliterator() {
    return CollectSpliterators.map(entrySet().spliterator(), Map.Entry::getKey);
  }
  
  public ImmutableCollection<V> values() {
    ImmutableCollection<V> result = this.values;
    return (result == null) ? (this.values = createValues()) : result;
  }
  
  public ImmutableSetMultimap<K, V> asMultimap() {
    if (isEmpty())
      return ImmutableSetMultimap.of(); 
    ImmutableSetMultimap<K, V> result = this.multimapView;
    return (result == null) ? (
      this
      .multimapView = new ImmutableSetMultimap<>(new MapViewOfValuesAsSingletonSets(), size(), null)) : 
      result;
  }
  
  private final class MapViewOfValuesAsSingletonSets extends IteratorBasedImmutableMap<K, ImmutableSet<V>> {
    private MapViewOfValuesAsSingletonSets() {}
    
    public int size() {
      return ImmutableMap.this.size();
    }
    
    ImmutableSet<K> createKeySet() {
      return ImmutableMap.this.keySet();
    }
    
    public boolean containsKey(@CheckForNull Object key) {
      return ImmutableMap.this.containsKey(key);
    }
    
    @CheckForNull
    public ImmutableSet<V> get(@CheckForNull Object key) {
      V outerValue = (V)ImmutableMap.this.get(key);
      return (outerValue == null) ? null : ImmutableSet.<V>of(outerValue);
    }
    
    boolean isPartialView() {
      return ImmutableMap.this.isPartialView();
    }
    
    public int hashCode() {
      return ImmutableMap.this.hashCode();
    }
    
    boolean isHashCodeFast() {
      return ImmutableMap.this.isHashCodeFast();
    }
    
    UnmodifiableIterator<Map.Entry<K, ImmutableSet<V>>> entryIterator() {
      final Iterator<Map.Entry<K, V>> backingIterator = ImmutableMap.this.entrySet().iterator();
      return new UnmodifiableIterator<Map.Entry<K, ImmutableSet<V>>>(this) {
          public boolean hasNext() {
            return backingIterator.hasNext();
          }
          
          public Map.Entry<K, ImmutableSet<V>> next() {
            final Map.Entry<K, V> backingEntry = backingIterator.next();
            return (Map.Entry)new AbstractMapEntry<K, ImmutableSet<ImmutableSet<V>>>(this) {
                public K getKey() {
                  return (K)backingEntry.getKey();
                }
                
                public ImmutableSet<V> getValue() {
                  return ImmutableSet.of((V)backingEntry.getValue());
                }
              };
          }
        };
    }
    
    @J2ktIncompatible
    @GwtIncompatible
    Object writeReplace() {
      return super.writeReplace();
    }
  }
  
  public boolean equals(@CheckForNull Object object) {
    return Maps.equalsImpl(this, object);
  }
  
  public int hashCode() {
    return Sets.hashCodeImpl(entrySet());
  }
  
  boolean isHashCodeFast() {
    return false;
  }
  
  public String toString() {
    return Maps.toStringImpl(this);
  }
  
  @J2ktIncompatible
  static class SerializedForm<K, V> implements Serializable {
    private static final boolean USE_LEGACY_SERIALIZATION = true;
    
    private final Object keys;
    
    private final Object values;
    
    private static final long serialVersionUID = 0L;
    
    SerializedForm(ImmutableMap<K, V> map) {
      Object[] keys = new Object[map.size()];
      Object[] values = new Object[map.size()];
      int i = 0;
      for (UnmodifiableIterator<Map.Entry<? extends Object, ? extends Object>> unmodifiableIterator = map.entrySet().iterator(); unmodifiableIterator.hasNext(); ) {
        Map.Entry<? extends Object, ? extends Object> entry = unmodifiableIterator.next();
        keys[i] = entry.getKey();
        values[i] = entry.getValue();
        i++;
      } 
      this.keys = keys;
      this.values = values;
    }
    
    final Object readResolve() {
      if (!(this.keys instanceof ImmutableSet))
        return legacyReadResolve(); 
      ImmutableSet<K> keySet = (ImmutableSet<K>)this.keys;
      ImmutableCollection<V> values = (ImmutableCollection<V>)this.values;
      ImmutableMap.Builder<K, V> builder = makeBuilder(keySet.size());
      UnmodifiableIterator<K> keyIter = keySet.iterator();
      UnmodifiableIterator<V> valueIter = values.iterator();
      while (keyIter.hasNext())
        builder.put(keyIter.next(), valueIter.next()); 
      return builder.buildOrThrow();
    }
    
    final Object legacyReadResolve() {
      K[] keys = (K[])this.keys;
      V[] values = (V[])this.values;
      ImmutableMap.Builder<K, V> builder = makeBuilder(keys.length);
      for (int i = 0; i < keys.length; i++)
        builder.put(keys[i], values[i]); 
      return builder.buildOrThrow();
    }
    
    ImmutableMap.Builder<K, V> makeBuilder(int size) {
      return new ImmutableMap.Builder<>(size);
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
  
  @CheckForNull
  public abstract V get(@CheckForNull Object paramObject);
  
  abstract ImmutableSet<Map.Entry<K, V>> createEntrySet();
  
  abstract ImmutableSet<K> createKeySet();
  
  abstract ImmutableCollection<V> createValues();
  
  abstract boolean isPartialView();
}
