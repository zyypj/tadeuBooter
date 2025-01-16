package me.syncwrld.booter.libs.google.guava.collect;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.LazyInit;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.j2objc.annotations.RetainedWith;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true, emulated = true)
class RegularImmutableBiMap<K, V> extends ImmutableBiMap<K, V> {
  static final RegularImmutableBiMap<Object, Object> EMPTY = new RegularImmutableBiMap(null, null, (Map.Entry[])ImmutableMap.EMPTY_ENTRY_ARRAY, 0, 0);
  
  static final double MAX_LOAD_FACTOR = 1.2D;
  
  @CheckForNull
  private final transient ImmutableMapEntry<K, V>[] keyTable;
  
  @CheckForNull
  private final transient ImmutableMapEntry<K, V>[] valueTable;
  
  @VisibleForTesting
  final transient Map.Entry<K, V>[] entries;
  
  private final transient int mask;
  
  private final transient int hashCode;
  
  @LazyInit
  @CheckForNull
  @RetainedWith
  private transient ImmutableBiMap<V, K> inverse;
  
  static <K, V> ImmutableBiMap<K, V> fromEntries(Map.Entry<K, V>... entries) {
    return fromEntryArray(entries.length, entries);
  }
  
  static <K, V> ImmutableBiMap<K, V> fromEntryArray(int n, Map.Entry<K, V>[] entryArray) {
    Preconditions.checkPositionIndex(n, entryArray.length);
    int tableSize = Hashing.closedTableSize(n, 1.2D);
    int mask = tableSize - 1;
    ImmutableMapEntry[] arrayOfImmutableMapEntry1 = (ImmutableMapEntry[])ImmutableMapEntry.createEntryArray(tableSize);
    ImmutableMapEntry[] arrayOfImmutableMapEntry2 = (ImmutableMapEntry[])ImmutableMapEntry.createEntryArray(tableSize);
    Map.Entry<K, V>[] entries = (n == entryArray.length) ? entryArray : (Map.Entry[])ImmutableMapEntry.createEntryArray(n);
    int hashCode = 0;
    for (int i = 0; i < n; i++) {
      Map.Entry<K, V> entry = Objects.<Map.Entry<K, V>>requireNonNull(entryArray[i]);
      K key = entry.getKey();
      V value = entry.getValue();
      CollectPreconditions.checkEntryNotNull(key, value);
      int keyHash = key.hashCode();
      int valueHash = value.hashCode();
      int keyBucket = Hashing.smear(keyHash) & mask;
      int valueBucket = Hashing.smear(valueHash) & mask;
      ImmutableMapEntry<K, V> nextInKeyBucket = arrayOfImmutableMapEntry1[keyBucket];
      ImmutableMapEntry<K, V> nextInValueBucket = arrayOfImmutableMapEntry2[valueBucket];
      try {
        RegularImmutableMap.checkNoConflictInKeyBucket(key, value, nextInKeyBucket, true);
        checkNoConflictInValueBucket(value, entry, nextInValueBucket);
      } catch (BucketOverflowException e) {
        return JdkBackedImmutableBiMap.create(n, entryArray);
      } 
      ImmutableMapEntry<K, V> newEntry = (nextInValueBucket == null && nextInKeyBucket == null) ? RegularImmutableMap.<K, V>makeImmutable(entry, key, value) : new ImmutableMapEntry.NonTerminalImmutableBiMapEntry<>(key, value, nextInKeyBucket, nextInValueBucket);
      arrayOfImmutableMapEntry1[keyBucket] = newEntry;
      arrayOfImmutableMapEntry2[valueBucket] = newEntry;
      entries[i] = newEntry;
      hashCode += keyHash ^ valueHash;
    } 
    return new RegularImmutableBiMap<>((ImmutableMapEntry<K, V>[])arrayOfImmutableMapEntry1, (ImmutableMapEntry<K, V>[])arrayOfImmutableMapEntry2, entries, mask, hashCode);
  }
  
  private RegularImmutableBiMap(@CheckForNull ImmutableMapEntry<K, V>[] keyTable, @CheckForNull ImmutableMapEntry<K, V>[] valueTable, Map.Entry<K, V>[] entries, int mask, int hashCode) {
    this.keyTable = keyTable;
    this.valueTable = valueTable;
    this.entries = entries;
    this.mask = mask;
    this.hashCode = hashCode;
  }
  
  private static void checkNoConflictInValueBucket(Object value, Map.Entry<?, ?> entry, @CheckForNull ImmutableMapEntry<?, ?> valueBucketHead) throws RegularImmutableMap.BucketOverflowException {
    int bucketSize = 0;
    for (; valueBucketHead != null; valueBucketHead = valueBucketHead.getNextInValueBucket()) {
      checkNoConflict(!value.equals(valueBucketHead.getValue()), "value", entry, valueBucketHead);
      if (++bucketSize > 8)
        throw new RegularImmutableMap.BucketOverflowException(); 
    } 
  }
  
  @CheckForNull
  public V get(@CheckForNull Object key) {
    return RegularImmutableMap.get(key, (ImmutableMapEntry<?, V>[])this.keyTable, this.mask);
  }
  
  ImmutableSet<Map.Entry<K, V>> createEntrySet() {
    return isEmpty() ? 
      ImmutableSet.<Map.Entry<K, V>>of() : 
      new ImmutableMapEntrySet.RegularEntrySet<>(this, this.entries);
  }
  
  ImmutableSet<K> createKeySet() {
    return new ImmutableMapKeySet<>(this);
  }
  
  public void forEach(BiConsumer<? super K, ? super V> action) {
    Preconditions.checkNotNull(action);
    for (Map.Entry<K, V> entry : this.entries)
      action.accept(entry.getKey(), entry.getValue()); 
  }
  
  boolean isHashCodeFast() {
    return true;
  }
  
  public int hashCode() {
    return this.hashCode;
  }
  
  boolean isPartialView() {
    return false;
  }
  
  public int size() {
    return this.entries.length;
  }
  
  public ImmutableBiMap<V, K> inverse() {
    if (isEmpty())
      return ImmutableBiMap.of(); 
    ImmutableBiMap<V, K> result = this.inverse;
    return (result == null) ? (this.inverse = new Inverse()) : result;
  }
  
  private final class Inverse extends ImmutableBiMap<V, K> {
    private Inverse() {}
    
    public int size() {
      return inverse().size();
    }
    
    public ImmutableBiMap<K, V> inverse() {
      return RegularImmutableBiMap.this;
    }
    
    public void forEach(BiConsumer<? super V, ? super K> action) {
      Preconditions.checkNotNull(action);
      RegularImmutableBiMap.this.forEach((k, v) -> action.accept(v, k));
    }
    
    @CheckForNull
    public K get(@CheckForNull Object value) {
      if (value == null || RegularImmutableBiMap.this.valueTable == null)
        return null; 
      int bucket = Hashing.smear(value.hashCode()) & RegularImmutableBiMap.this.mask;
      ImmutableMapEntry<K, V> entry = RegularImmutableBiMap.this.valueTable[bucket];
      for (; entry != null; 
        entry = entry.getNextInValueBucket()) {
        if (value.equals(entry.getValue()))
          return entry.getKey(); 
      } 
      return null;
    }
    
    ImmutableSet<V> createKeySet() {
      return new ImmutableMapKeySet<>(this);
    }
    
    ImmutableSet<Map.Entry<V, K>> createEntrySet() {
      return new InverseEntrySet();
    }
    
    final class InverseEntrySet extends ImmutableMapEntrySet<V, K> {
      ImmutableMap<V, K> map() {
        return RegularImmutableBiMap.Inverse.this;
      }
      
      boolean isHashCodeFast() {
        return true;
      }
      
      public int hashCode() {
        return RegularImmutableBiMap.this.hashCode;
      }
      
      public UnmodifiableIterator<Map.Entry<V, K>> iterator() {
        return asList().iterator();
      }
      
      public void forEach(Consumer<? super Map.Entry<V, K>> action) {
        asList().forEach(action);
      }
      
      ImmutableList<Map.Entry<V, K>> createAsList() {
        return new ImmutableAsList<Map.Entry<V, K>>() {
            public Map.Entry<V, K> get(int index) {
              Map.Entry<K, V> entry = RegularImmutableBiMap.this.entries[index];
              return Maps.immutableEntry(entry.getValue(), entry.getKey());
            }
            
            ImmutableCollection<Map.Entry<V, K>> delegateCollection() {
              return RegularImmutableBiMap.Inverse.InverseEntrySet.this;
            }
            
            @J2ktIncompatible
            @GwtIncompatible
            Object writeReplace() {
              return super.writeReplace();
            }
          };
      }
      
      @J2ktIncompatible
      @GwtIncompatible
      Object writeReplace() {
        return super.writeReplace();
      }
    }
    
    boolean isPartialView() {
      return false;
    }
    
    @J2ktIncompatible
    @GwtIncompatible
    Object writeReplace() {
      return new RegularImmutableBiMap.InverseSerializedForm<>(RegularImmutableBiMap.this);
    }
    
    @J2ktIncompatible
    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
      throw new InvalidObjectException("Use InverseSerializedForm");
    }
  }
  
  @J2ktIncompatible
  private static class InverseSerializedForm<K, V> implements Serializable {
    private final ImmutableBiMap<K, V> forward;
    
    private static final long serialVersionUID = 1L;
    
    InverseSerializedForm(ImmutableBiMap<K, V> forward) {
      this.forward = forward;
    }
    
    Object readResolve() {
      return this.forward.inverse();
    }
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  Object writeReplace() {
    return super.writeReplace();
  }
}
