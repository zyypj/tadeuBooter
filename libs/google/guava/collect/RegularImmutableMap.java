package me.syncwrld.booter.libs.google.guava.collect;

import java.io.Serializable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true, emulated = true)
final class RegularImmutableMap<K, V> extends ImmutableMap<K, V> {
  static final ImmutableMap<Object, Object> EMPTY = new RegularImmutableMap((Map.Entry[])ImmutableMap.EMPTY_ENTRY_ARRAY, null, 0);
  
  @VisibleForTesting
  static final double MAX_LOAD_FACTOR = 1.2D;
  
  @VisibleForTesting
  static final double HASH_FLOODING_FPP = 0.001D;
  
  @VisibleForTesting
  static final int MAX_HASH_BUCKET_LENGTH = 8;
  
  @VisibleForTesting
  final transient Map.Entry<K, V>[] entries;
  
  @CheckForNull
  private final transient ImmutableMapEntry<K, V>[] table;
  
  private final transient int mask;
  
  @J2ktIncompatible
  private static final long serialVersionUID = 0L;
  
  static <K, V> ImmutableMap<K, V> fromEntries(Map.Entry<K, V>... entries) {
    return fromEntryArray(entries.length, entries, true);
  }
  
  static <K, V> ImmutableMap<K, V> fromEntryArray(int n, Map.Entry<K, V>[] entryArray, boolean throwIfDuplicateKeys) {
    Preconditions.checkPositionIndex(n, entryArray.length);
    if (n == 0)
      return (ImmutableMap)EMPTY; 
    try {
      return fromEntryArrayCheckingBucketOverflow(n, entryArray, throwIfDuplicateKeys);
    } catch (BucketOverflowException e) {
      return JdkBackedImmutableMap.create(n, entryArray, throwIfDuplicateKeys);
    } 
  }
  
  private static <K, V> ImmutableMap<K, V> fromEntryArrayCheckingBucketOverflow(int n, Map.Entry<K, V>[] entryArray, boolean throwIfDuplicateKeys) throws BucketOverflowException {
    Map.Entry[] arrayOfEntry;
    Map.Entry<K, V>[] entries = (n == entryArray.length) ? entryArray : (Map.Entry[])ImmutableMapEntry.createEntryArray(n);
    int tableSize = Hashing.closedTableSize(n, 1.2D);
    ImmutableMapEntry[] arrayOfImmutableMapEntry = (ImmutableMapEntry[])ImmutableMapEntry.createEntryArray(tableSize);
    int mask = tableSize - 1;
    IdentityHashMap<Map.Entry<K, V>, Boolean> duplicates = null;
    int dupCount = 0;
    for (int entryIndex = n - 1; entryIndex >= 0; entryIndex--) {
      Map.Entry<K, V> entry = Objects.<Map.Entry<K, V>>requireNonNull(entryArray[entryIndex]);
      K key = entry.getKey();
      V value = entry.getValue();
      CollectPreconditions.checkEntryNotNull(key, value);
      int tableIndex = Hashing.smear(key.hashCode()) & mask;
      ImmutableMapEntry<K, V> keyBucketHead = arrayOfImmutableMapEntry[tableIndex];
      ImmutableMapEntry<K, V> effectiveEntry = checkNoConflictInKeyBucket(key, value, keyBucketHead, throwIfDuplicateKeys);
      if (effectiveEntry == null) {
        effectiveEntry = (keyBucketHead == null) ? makeImmutable(entry, key, value) : new ImmutableMapEntry.NonTerminalImmutableMapEntry<>(key, value, keyBucketHead);
        arrayOfImmutableMapEntry[tableIndex] = effectiveEntry;
      } else {
        if (duplicates == null)
          duplicates = new IdentityHashMap<>(); 
        duplicates.put(effectiveEntry, Boolean.valueOf(true));
        dupCount++;
        if (entries == entryArray) {
          Map.Entry<K, V>[] originalEntries = entries;
          arrayOfEntry = (Map.Entry[])originalEntries.clone();
        } 
      } 
      arrayOfEntry[entryIndex] = effectiveEntry;
    } 
    if (duplicates != null) {
      arrayOfEntry = removeDuplicates((Map.Entry<K, V>[])arrayOfEntry, n, n - dupCount, duplicates);
      int newTableSize = Hashing.closedTableSize(arrayOfEntry.length, 1.2D);
      if (newTableSize != tableSize)
        return fromEntryArrayCheckingBucketOverflow(arrayOfEntry.length, (Map.Entry<K, V>[])arrayOfEntry, true); 
    } 
    return new RegularImmutableMap<>((Map.Entry<K, V>[])arrayOfEntry, (ImmutableMapEntry<K, V>[])arrayOfImmutableMapEntry, mask);
  }
  
  static <K, V> Map.Entry<K, V>[] removeDuplicates(Map.Entry<K, V>[] entries, int n, int newN, IdentityHashMap<Map.Entry<K, V>, Boolean> duplicates) {
    ImmutableMapEntry[] arrayOfImmutableMapEntry = (ImmutableMapEntry[])ImmutableMapEntry.createEntryArray(newN);
    for (int in = 0, out = 0; in < n; in++) {
      Map.Entry<K, V> entry = entries[in];
      Boolean status = duplicates.get(entry);
      if (status != null)
        if (status.booleanValue()) {
          duplicates.put(entry, Boolean.valueOf(false));
        } else {
          continue;
        }  
      arrayOfImmutableMapEntry[out++] = (ImmutableMapEntry)entry;
      continue;
    } 
    return (Map.Entry<K, V>[])arrayOfImmutableMapEntry;
  }
  
  static <K, V> ImmutableMapEntry<K, V> makeImmutable(Map.Entry<K, V> entry, K key, V value) {
    boolean reusable = (entry instanceof ImmutableMapEntry && ((ImmutableMapEntry)entry).isReusable());
    return reusable ? (ImmutableMapEntry<K, V>)entry : new ImmutableMapEntry<>(key, value);
  }
  
  static <K, V> ImmutableMapEntry<K, V> makeImmutable(Map.Entry<K, V> entry) {
    return makeImmutable(entry, entry.getKey(), entry.getValue());
  }
  
  private RegularImmutableMap(Map.Entry<K, V>[] entries, @CheckForNull ImmutableMapEntry<K, V>[] table, int mask) {
    this.entries = entries;
    this.table = table;
    this.mask = mask;
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  static <K, V> ImmutableMapEntry<K, V> checkNoConflictInKeyBucket(Object key, Object newValue, @CheckForNull ImmutableMapEntry<K, V> keyBucketHead, boolean throwIfDuplicateKeys) throws BucketOverflowException {
    int bucketSize = 0;
    for (; keyBucketHead != null; keyBucketHead = keyBucketHead.getNextInKeyBucket()) {
      if (keyBucketHead.getKey().equals(key))
        if (throwIfDuplicateKeys) {
          checkNoConflict(false, "key", keyBucketHead, key + "=" + newValue);
        } else {
          return keyBucketHead;
        }  
      if (++bucketSize > 8)
        throw new BucketOverflowException(); 
    } 
    return null;
  }
  
  static class BucketOverflowException extends Exception {}
  
  @CheckForNull
  public V get(@CheckForNull Object key) {
    return get(key, (ImmutableMapEntry<?, V>[])this.table, this.mask);
  }
  
  @CheckForNull
  static <V> V get(@CheckForNull Object key, @CheckForNull ImmutableMapEntry<?, V>[] keyTable, int mask) {
    if (key == null || keyTable == null)
      return null; 
    int index = Hashing.smear(key.hashCode()) & mask;
    ImmutableMapEntry<?, V> entry = keyTable[index];
    for (; entry != null; 
      entry = entry.getNextInKeyBucket()) {
      Object candidateKey = entry.getKey();
      if (key.equals(candidateKey))
        return entry.getValue(); 
    } 
    return null;
  }
  
  public void forEach(BiConsumer<? super K, ? super V> action) {
    Preconditions.checkNotNull(action);
    for (Map.Entry<K, V> entry : this.entries)
      action.accept(entry.getKey(), entry.getValue()); 
  }
  
  public int size() {
    return this.entries.length;
  }
  
  boolean isPartialView() {
    return false;
  }
  
  ImmutableSet<Map.Entry<K, V>> createEntrySet() {
    return new ImmutableMapEntrySet.RegularEntrySet<>(this, this.entries);
  }
  
  ImmutableSet<K> createKeySet() {
    return new KeySet<>(this);
  }
  
  @GwtCompatible(emulated = true)
  private static final class KeySet<K> extends IndexedImmutableSet<K> {
    private final RegularImmutableMap<K, ?> map;
    
    KeySet(RegularImmutableMap<K, ?> map) {
      this.map = map;
    }
    
    K get(int index) {
      return this.map.entries[index].getKey();
    }
    
    public boolean contains(@CheckForNull Object object) {
      return this.map.containsKey(object);
    }
    
    boolean isPartialView() {
      return true;
    }
    
    public int size() {
      return this.map.size();
    }
    
    @J2ktIncompatible
    @GwtIncompatible
    Object writeReplace() {
      return super.writeReplace();
    }
    
    @GwtIncompatible
    @J2ktIncompatible
    private static class SerializedForm<K> implements Serializable {
      final ImmutableMap<K, ?> map;
      
      @J2ktIncompatible
      private static final long serialVersionUID = 0L;
      
      SerializedForm(ImmutableMap<K, ?> map) {
        this.map = map;
      }
      
      Object readResolve() {
        return this.map.keySet();
      }
    }
  }
  
  ImmutableCollection<V> createValues() {
    return new Values<>(this);
  }
  
  @GwtCompatible(emulated = true)
  private static final class Values<K, V> extends ImmutableList<V> {
    final RegularImmutableMap<K, V> map;
    
    Values(RegularImmutableMap<K, V> map) {
      this.map = map;
    }
    
    public V get(int index) {
      return this.map.entries[index].getValue();
    }
    
    public int size() {
      return this.map.size();
    }
    
    boolean isPartialView() {
      return true;
    }
    
    @J2ktIncompatible
    @GwtIncompatible
    Object writeReplace() {
      return super.writeReplace();
    }
    
    @GwtIncompatible
    @J2ktIncompatible
    private static class SerializedForm<V> implements Serializable {
      final ImmutableMap<?, V> map;
      
      @J2ktIncompatible
      private static final long serialVersionUID = 0L;
      
      SerializedForm(ImmutableMap<?, V> map) {
        this.map = map;
      }
      
      Object readResolve() {
        return this.map.values();
      }
    }
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  Object writeReplace() {
    return super.writeReplace();
  }
}
