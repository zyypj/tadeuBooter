package me.syncwrld.booter.libs.google.guava.collect;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotCall;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.LazyInit;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.MoreObjects;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.j2objc.annotations.RetainedWith;
import me.syncwrld.booter.libs.google.j2objc.annotations.Weak;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true, emulated = true)
public class ImmutableSetMultimap<K, V> extends ImmutableMultimap<K, V> implements SetMultimap<K, V> {
  private final transient ImmutableSet<V> emptySet;
  
  @LazyInit
  @CheckForNull
  @RetainedWith
  private transient ImmutableSetMultimap<V, K> inverse;
  
  @LazyInit
  @CheckForNull
  @RetainedWith
  private transient ImmutableSet<Map.Entry<K, V>> entries;
  
  @GwtIncompatible
  @J2ktIncompatible
  private static final long serialVersionUID = 0L;
  
  public static <T, K, V> Collector<T, ?, ImmutableSetMultimap<K, V>> toImmutableSetMultimap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction) {
    return CollectCollectors.toImmutableSetMultimap(keyFunction, valueFunction);
  }
  
  public static <T, K, V> Collector<T, ?, ImmutableSetMultimap<K, V>> flatteningToImmutableSetMultimap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends Stream<? extends V>> valuesFunction) {
    return CollectCollectors.flatteningToImmutableSetMultimap(keyFunction, valuesFunction);
  }
  
  public static <K, V> ImmutableSetMultimap<K, V> of() {
    return EmptyImmutableSetMultimap.INSTANCE;
  }
  
  public static <K, V> ImmutableSetMultimap<K, V> of(K k1, V v1) {
    Builder<K, V> builder = builder();
    builder.put(k1, v1);
    return builder.build();
  }
  
  public static <K, V> ImmutableSetMultimap<K, V> of(K k1, V v1, K k2, V v2) {
    Builder<K, V> builder = builder();
    builder.put(k1, v1);
    builder.put(k2, v2);
    return builder.build();
  }
  
  public static <K, V> ImmutableSetMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
    Builder<K, V> builder = builder();
    builder.put(k1, v1);
    builder.put(k2, v2);
    builder.put(k3, v3);
    return builder.build();
  }
  
  public static <K, V> ImmutableSetMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    Builder<K, V> builder = builder();
    builder.put(k1, v1);
    builder.put(k2, v2);
    builder.put(k3, v3);
    builder.put(k4, v4);
    return builder.build();
  }
  
  public static <K, V> ImmutableSetMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
    Builder<K, V> builder = builder();
    builder.put(k1, v1);
    builder.put(k2, v2);
    builder.put(k3, v3);
    builder.put(k4, v4);
    builder.put(k5, v5);
    return builder.build();
  }
  
  public static <K, V> Builder<K, V> builder() {
    return new Builder<>();
  }
  
  public static final class Builder<K, V> extends ImmutableMultimap.Builder<K, V> {
    Collection<V> newMutableValueCollection() {
      return Platform.preservesInsertionOrderOnAddsSet();
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
    public Builder<K, V> putAll(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
      super.putAll(entries);
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<K, V> putAll(K key, Iterable<? extends V> values) {
      super.putAll(key, values);
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<K, V> putAll(K key, V... values) {
      return putAll(key, Arrays.asList(values));
    }
    
    @CanIgnoreReturnValue
    public Builder<K, V> putAll(Multimap<? extends K, ? extends V> multimap) {
      for (Map.Entry<? extends K, ? extends Collection<? extends V>> entry : (Iterable<Map.Entry<? extends K, ? extends Collection<? extends V>>>)multimap.asMap().entrySet())
        putAll(entry.getKey(), entry.getValue()); 
      return this;
    }
    
    @CanIgnoreReturnValue
    Builder<K, V> combine(ImmutableMultimap.Builder<K, V> other) {
      super.combine(other);
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<K, V> orderKeysBy(Comparator<? super K> keyComparator) {
      super.orderKeysBy(keyComparator);
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<K, V> orderValuesBy(Comparator<? super V> valueComparator) {
      super.orderValuesBy(valueComparator);
      return this;
    }
    
    public ImmutableSetMultimap<K, V> build() {
      Collection<Map.Entry<K, Collection<V>>> mapEntries = this.builderMap.entrySet();
      if (this.keyComparator != null)
        mapEntries = Ordering.<K>from(this.keyComparator).onKeys().immutableSortedCopy(mapEntries); 
      return ImmutableSetMultimap.fromMapEntries(mapEntries, this.valueComparator);
    }
  }
  
  public static <K, V> ImmutableSetMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> multimap) {
    return copyOf(multimap, (Comparator<? super V>)null);
  }
  
  private static <K, V> ImmutableSetMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> multimap, @CheckForNull Comparator<? super V> valueComparator) {
    Preconditions.checkNotNull(multimap);
    if (multimap.isEmpty() && valueComparator == null)
      return of(); 
    if (multimap instanceof ImmutableSetMultimap) {
      ImmutableSetMultimap<K, V> kvMultimap = (ImmutableSetMultimap)multimap;
      if (!kvMultimap.isPartialView())
        return kvMultimap; 
    } 
    return fromMapEntries(multimap.asMap().entrySet(), valueComparator);
  }
  
  public static <K, V> ImmutableSetMultimap<K, V> copyOf(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
    return (new Builder<>()).putAll(entries).build();
  }
  
  static <K, V> ImmutableSetMultimap<K, V> fromMapEntries(Collection<? extends Map.Entry<? extends K, ? extends Collection<? extends V>>> mapEntries, @CheckForNull Comparator<? super V> valueComparator) {
    if (mapEntries.isEmpty())
      return of(); 
    ImmutableMap.Builder<K, ImmutableSet<V>> builder = new ImmutableMap.Builder<>(mapEntries.size());
    int size = 0;
    for (Map.Entry<? extends K, ? extends Collection<? extends V>> entry : mapEntries) {
      K key = entry.getKey();
      Collection<? extends V> values = entry.getValue();
      ImmutableSet<V> set = valueSet(valueComparator, values);
      if (!set.isEmpty()) {
        builder.put(key, set);
        size += set.size();
      } 
    } 
    return new ImmutableSetMultimap<>(builder.buildOrThrow(), size, valueComparator);
  }
  
  ImmutableSetMultimap(ImmutableMap<K, ImmutableSet<V>> map, int size, @CheckForNull Comparator<? super V> valueComparator) {
    super((ImmutableMap)map, size);
    this.emptySet = emptySet(valueComparator);
  }
  
  public ImmutableSet<V> get(K key) {
    ImmutableSet<V> set = (ImmutableSet<V>)this.map.get(key);
    return (ImmutableSet<V>)MoreObjects.firstNonNull(set, this.emptySet);
  }
  
  public ImmutableSetMultimap<V, K> inverse() {
    ImmutableSetMultimap<V, K> result = this.inverse;
    return (result == null) ? (this.inverse = invert()) : result;
  }
  
  private ImmutableSetMultimap<V, K> invert() {
    Builder<V, K> builder = builder();
    for (UnmodifiableIterator<Map.Entry<K, V>> unmodifiableIterator = entries().iterator(); unmodifiableIterator.hasNext(); ) {
      Map.Entry<K, V> entry = unmodifiableIterator.next();
      builder.put(entry.getValue(), entry.getKey());
    } 
    ImmutableSetMultimap<V, K> invertedMultimap = builder.build();
    invertedMultimap.inverse = this;
    return invertedMultimap;
  }
  
  @Deprecated
  @CanIgnoreReturnValue
  @DoNotCall("Always throws UnsupportedOperationException")
  public final ImmutableSet<V> removeAll(@CheckForNull Object key) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @CanIgnoreReturnValue
  @DoNotCall("Always throws UnsupportedOperationException")
  public final ImmutableSet<V> replaceValues(K key, Iterable<? extends V> values) {
    throw new UnsupportedOperationException();
  }
  
  public ImmutableSet<Map.Entry<K, V>> entries() {
    ImmutableSet<Map.Entry<K, V>> result = this.entries;
    return (result == null) ? (this.entries = new EntrySet<>(this)) : result;
  }
  
  private static final class EntrySet<K, V> extends ImmutableSet<Map.Entry<K, V>> {
    @Weak
    private final transient ImmutableSetMultimap<K, V> multimap;
    
    EntrySet(ImmutableSetMultimap<K, V> multimap) {
      this.multimap = multimap;
    }
    
    public boolean contains(@CheckForNull Object object) {
      if (object instanceof Map.Entry) {
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>)object;
        return this.multimap.containsEntry(entry.getKey(), entry.getValue());
      } 
      return false;
    }
    
    public int size() {
      return this.multimap.size();
    }
    
    public UnmodifiableIterator<Map.Entry<K, V>> iterator() {
      return this.multimap.entryIterator();
    }
    
    boolean isPartialView() {
      return false;
    }
    
    @J2ktIncompatible
    @GwtIncompatible
    Object writeReplace() {
      return super.writeReplace();
    }
  }
  
  private static <V> ImmutableSet<V> valueSet(@CheckForNull Comparator<? super V> valueComparator, Collection<? extends V> values) {
    return (valueComparator == null) ? 
      ImmutableSet.<V>copyOf(values) : 
      ImmutableSortedSet.<V>copyOf(valueComparator, values);
  }
  
  private static <V> ImmutableSet<V> emptySet(@CheckForNull Comparator<? super V> valueComparator) {
    return (valueComparator == null) ? 
      ImmutableSet.<V>of() : 
      ImmutableSortedSet.<V>emptySet(valueComparator);
  }
  
  private static <V> ImmutableSet.Builder<V> valuesBuilder(@CheckForNull Comparator<? super V> valueComparator) {
    return (valueComparator == null) ? 
      new ImmutableSet.Builder<>() : 
      new ImmutableSortedSet.Builder<>(valueComparator);
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    stream.writeObject(valueComparator());
    Serialization.writeMultimap(this, stream);
  }
  
  @CheckForNull
  Comparator<? super V> valueComparator() {
    return (this.emptySet instanceof ImmutableSortedSet) ? (
      (ImmutableSortedSet<V>)this.emptySet).comparator() : 
      null;
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  private static final class SetFieldSettersHolder {
    static final Serialization.FieldSetter<ImmutableSetMultimap> EMPTY_SET_FIELD_SETTER = Serialization.getFieldSetter(ImmutableSetMultimap.class, "emptySet");
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    ImmutableMap<Object, ImmutableSet<Object>> tmpMap;
    stream.defaultReadObject();
    Comparator<Object> valueComparator = (Comparator<Object>)stream.readObject();
    int keyCount = stream.readInt();
    if (keyCount < 0)
      throw new InvalidObjectException("Invalid key count " + keyCount); 
    ImmutableMap.Builder<Object, ImmutableSet<Object>> builder = ImmutableMap.builder();
    int tmpSize = 0;
    for (int i = 0; i < keyCount; i++) {
      Object key = Objects.requireNonNull(stream.readObject());
      int valueCount = stream.readInt();
      if (valueCount <= 0)
        throw new InvalidObjectException("Invalid value count " + valueCount); 
      ImmutableSet.Builder<Object> valuesBuilder = valuesBuilder(valueComparator);
      for (int j = 0; j < valueCount; j++)
        valuesBuilder.add(Objects.requireNonNull(stream.readObject())); 
      ImmutableSet<Object> valueSet = valuesBuilder.build();
      if (valueSet.size() != valueCount)
        throw new InvalidObjectException("Duplicate key-value pairs exist for key " + key); 
      builder.put(key, valueSet);
      tmpSize += valueCount;
    } 
    try {
      tmpMap = builder.buildOrThrow();
    } catch (IllegalArgumentException e) {
      throw (InvalidObjectException)(new InvalidObjectException(e.getMessage())).initCause(e);
    } 
    ImmutableMultimap.FieldSettersHolder.MAP_FIELD_SETTER.set(this, tmpMap);
    ImmutableMultimap.FieldSettersHolder.SIZE_FIELD_SETTER.set(this, tmpSize);
    SetFieldSettersHolder.EMPTY_SET_FIELD_SETTER.set(this, emptySet(valueComparator));
  }
}
