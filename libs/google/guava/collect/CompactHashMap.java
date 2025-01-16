package me.syncwrld.booter.libs.google.guava.collect;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.LazyInit;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.Objects;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.primitives.Ints;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
class CompactHashMap<K, V> extends AbstractMap<K, V> implements Serializable {
  public static <K, V> CompactHashMap<K, V> create() {
    return new CompactHashMap<>();
  }
  
  public static <K, V> CompactHashMap<K, V> createWithExpectedSize(int expectedSize) {
    return new CompactHashMap<>(expectedSize);
  }
  
  private static final Object NOT_FOUND = new Object();
  
  @VisibleForTesting
  static final double HASH_FLOODING_FPP = 0.001D;
  
  private static final int MAX_HASH_BUCKET_LENGTH = 9;
  
  @CheckForNull
  private transient Object table;
  
  @CheckForNull
  @VisibleForTesting
  transient int[] entries;
  
  @CheckForNull
  @VisibleForTesting
  transient Object[] keys;
  
  @CheckForNull
  @VisibleForTesting
  transient Object[] values;
  
  private transient int metadata;
  
  private transient int size;
  
  @LazyInit
  @CheckForNull
  private transient Set<K> keySetView;
  
  @LazyInit
  @CheckForNull
  private transient Set<Map.Entry<K, V>> entrySetView;
  
  @LazyInit
  @CheckForNull
  private transient Collection<V> valuesView;
  
  CompactHashMap() {
    init(3);
  }
  
  CompactHashMap(int expectedSize) {
    init(expectedSize);
  }
  
  void init(int expectedSize) {
    Preconditions.checkArgument((expectedSize >= 0), "Expected size must be >= 0");
    this.metadata = Ints.constrainToRange(expectedSize, 1, 1073741823);
  }
  
  @VisibleForTesting
  boolean needsAllocArrays() {
    return (this.table == null);
  }
  
  @CanIgnoreReturnValue
  int allocArrays() {
    Preconditions.checkState(needsAllocArrays(), "Arrays already allocated");
    int expectedSize = this.metadata;
    int buckets = CompactHashing.tableSize(expectedSize);
    this.table = CompactHashing.createTable(buckets);
    setHashTableMask(buckets - 1);
    this.entries = new int[expectedSize];
    this.keys = new Object[expectedSize];
    this.values = new Object[expectedSize];
    return expectedSize;
  }
  
  @CheckForNull
  @VisibleForTesting
  Map<K, V> delegateOrNull() {
    if (this.table instanceof Map)
      return (Map<K, V>)this.table; 
    return null;
  }
  
  Map<K, V> createHashFloodingResistantDelegate(int tableSize) {
    return new LinkedHashMap<>(tableSize, 1.0F);
  }
  
  @VisibleForTesting
  @CanIgnoreReturnValue
  Map<K, V> convertToHashFloodingResistantImplementation() {
    Map<K, V> newDelegate = createHashFloodingResistantDelegate(hashTableMask() + 1);
    for (int i = firstEntryIndex(); i >= 0; i = getSuccessor(i))
      newDelegate.put(key(i), value(i)); 
    this.table = newDelegate;
    this.entries = null;
    this.keys = null;
    this.values = null;
    incrementModCount();
    return newDelegate;
  }
  
  private void setHashTableMask(int mask) {
    int hashTableBits = 32 - Integer.numberOfLeadingZeros(mask);
    this
      .metadata = CompactHashing.maskCombine(this.metadata, hashTableBits, 31);
  }
  
  private int hashTableMask() {
    return (1 << (this.metadata & 0x1F)) - 1;
  }
  
  void incrementModCount() {
    this.metadata += 32;
  }
  
  void accessEntry(int index) {}
  
  @CheckForNull
  @CanIgnoreReturnValue
  public V put(@ParametricNullness K key, @ParametricNullness V value) {
    if (needsAllocArrays())
      allocArrays(); 
    Map<K, V> delegate = delegateOrNull();
    if (delegate != null)
      return delegate.put(key, value); 
    int[] entries = requireEntries();
    Object[] keys = requireKeys();
    Object[] values = requireValues();
    int newEntryIndex = this.size;
    int newSize = newEntryIndex + 1;
    int hash = Hashing.smearedHash(key);
    int mask = hashTableMask();
    int tableIndex = hash & mask;
    int next = CompactHashing.tableGet(requireTable(), tableIndex);
    if (next == 0) {
      if (newSize > mask) {
        mask = resizeTable(mask, CompactHashing.newCapacity(mask), hash, newEntryIndex);
      } else {
        CompactHashing.tableSet(requireTable(), tableIndex, newEntryIndex + 1);
      } 
    } else {
      int entryIndex, entry, hashPrefix = CompactHashing.getHashPrefix(hash, mask);
      int bucketLength = 0;
      do {
        entryIndex = next - 1;
        entry = entries[entryIndex];
        if (CompactHashing.getHashPrefix(entry, mask) == hashPrefix && 
          Objects.equal(key, keys[entryIndex])) {
          V oldValue = (V)values[entryIndex];
          values[entryIndex] = value;
          accessEntry(entryIndex);
          return oldValue;
        } 
        next = CompactHashing.getNext(entry, mask);
        bucketLength++;
      } while (next != 0);
      if (bucketLength >= 9)
        return convertToHashFloodingResistantImplementation().put(key, value); 
      if (newSize > mask) {
        mask = resizeTable(mask, CompactHashing.newCapacity(mask), hash, newEntryIndex);
      } else {
        entries[entryIndex] = CompactHashing.maskCombine(entry, newEntryIndex + 1, mask);
      } 
    } 
    resizeMeMaybe(newSize);
    insertEntry(newEntryIndex, key, value, hash, mask);
    this.size = newSize;
    incrementModCount();
    return null;
  }
  
  void insertEntry(int entryIndex, @ParametricNullness K key, @ParametricNullness V value, int hash, int mask) {
    setEntry(entryIndex, CompactHashing.maskCombine(hash, 0, mask));
    setKey(entryIndex, key);
    setValue(entryIndex, value);
  }
  
  private void resizeMeMaybe(int newSize) {
    int entriesSize = (requireEntries()).length;
    if (newSize > entriesSize) {
      int newCapacity = Math.min(1073741823, entriesSize + Math.max(1, entriesSize >>> 1) | 0x1);
      if (newCapacity != entriesSize)
        resizeEntries(newCapacity); 
    } 
  }
  
  void resizeEntries(int newCapacity) {
    this.entries = Arrays.copyOf(requireEntries(), newCapacity);
    this.keys = Arrays.copyOf(requireKeys(), newCapacity);
    this.values = Arrays.copyOf(requireValues(), newCapacity);
  }
  
  @CanIgnoreReturnValue
  private int resizeTable(int oldMask, int newCapacity, int targetHash, int targetEntryIndex) {
    Object newTable = CompactHashing.createTable(newCapacity);
    int newMask = newCapacity - 1;
    if (targetEntryIndex != 0)
      CompactHashing.tableSet(newTable, targetHash & newMask, targetEntryIndex + 1); 
    Object oldTable = requireTable();
    int[] entries = requireEntries();
    for (int oldTableIndex = 0; oldTableIndex <= oldMask; oldTableIndex++) {
      int oldNext = CompactHashing.tableGet(oldTable, oldTableIndex);
      while (oldNext != 0) {
        int entryIndex = oldNext - 1;
        int oldEntry = entries[entryIndex];
        int hash = CompactHashing.getHashPrefix(oldEntry, oldMask) | oldTableIndex;
        int newTableIndex = hash & newMask;
        int newNext = CompactHashing.tableGet(newTable, newTableIndex);
        CompactHashing.tableSet(newTable, newTableIndex, oldNext);
        entries[entryIndex] = CompactHashing.maskCombine(hash, newNext, newMask);
        oldNext = CompactHashing.getNext(oldEntry, oldMask);
      } 
    } 
    this.table = newTable;
    setHashTableMask(newMask);
    return newMask;
  }
  
  private int indexOf(@CheckForNull Object key) {
    if (needsAllocArrays())
      return -1; 
    int hash = Hashing.smearedHash(key);
    int mask = hashTableMask();
    int next = CompactHashing.tableGet(requireTable(), hash & mask);
    if (next == 0)
      return -1; 
    int hashPrefix = CompactHashing.getHashPrefix(hash, mask);
    while (true) {
      int entryIndex = next - 1;
      int entry = entry(entryIndex);
      if (CompactHashing.getHashPrefix(entry, mask) == hashPrefix && 
        Objects.equal(key, key(entryIndex)))
        return entryIndex; 
      next = CompactHashing.getNext(entry, mask);
      if (next == 0)
        return -1; 
    } 
  }
  
  public boolean containsKey(@CheckForNull Object key) {
    Map<K, V> delegate = delegateOrNull();
    return (delegate != null) ? delegate.containsKey(key) : ((indexOf(key) != -1));
  }
  
  @CheckForNull
  public V get(@CheckForNull Object key) {
    Map<K, V> delegate = delegateOrNull();
    if (delegate != null)
      return delegate.get(key); 
    int index = indexOf(key);
    if (index == -1)
      return null; 
    accessEntry(index);
    return value(index);
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public V remove(@CheckForNull Object key) {
    Map<K, V> delegate = delegateOrNull();
    if (delegate != null)
      return delegate.remove(key); 
    Object oldValue = removeHelper(key);
    return (oldValue == NOT_FOUND) ? null : (V)oldValue;
  }
  
  private Object removeHelper(@CheckForNull Object key) {
    if (needsAllocArrays())
      return NOT_FOUND; 
    int mask = hashTableMask();
    int index = CompactHashing.remove(key, null, mask, 
        
        requireTable(), 
        requireEntries(), 
        requireKeys(), null);
    if (index == -1)
      return NOT_FOUND; 
    Object oldValue = value(index);
    moveLastEntry(index, mask);
    this.size--;
    incrementModCount();
    return oldValue;
  }
  
  void moveLastEntry(int dstIndex, int mask) {
    Object table = requireTable();
    int[] entries = requireEntries();
    Object[] keys = requireKeys();
    Object[] values = requireValues();
    int srcIndex = size() - 1;
    if (dstIndex < srcIndex) {
      Object key = keys[srcIndex];
      keys[dstIndex] = key;
      values[dstIndex] = values[srcIndex];
      keys[srcIndex] = null;
      values[srcIndex] = null;
      entries[dstIndex] = entries[srcIndex];
      entries[srcIndex] = 0;
      int tableIndex = Hashing.smearedHash(key) & mask;
      int next = CompactHashing.tableGet(table, tableIndex);
      int srcNext = srcIndex + 1;
      if (next == srcNext) {
        CompactHashing.tableSet(table, tableIndex, dstIndex + 1);
      } else {
        int entryIndex, entry;
        do {
          entryIndex = next - 1;
          entry = entries[entryIndex];
          next = CompactHashing.getNext(entry, mask);
        } while (next != srcNext);
        entries[entryIndex] = CompactHashing.maskCombine(entry, dstIndex + 1, mask);
      } 
    } else {
      keys[dstIndex] = null;
      values[dstIndex] = null;
      entries[dstIndex] = 0;
    } 
  }
  
  int firstEntryIndex() {
    return isEmpty() ? -1 : 0;
  }
  
  int getSuccessor(int entryIndex) {
    return (entryIndex + 1 < this.size) ? (entryIndex + 1) : -1;
  }
  
  int adjustAfterRemove(int indexBeforeRemove, int indexRemoved) {
    return indexBeforeRemove - 1;
  }
  
  private abstract class Itr<T> implements Iterator<T> {
    int expectedMetadata = CompactHashMap.this.metadata;
    
    int currentIndex = CompactHashMap.this.firstEntryIndex();
    
    int indexToRemove = -1;
    
    public boolean hasNext() {
      return (this.currentIndex >= 0);
    }
    
    @ParametricNullness
    public T next() {
      checkForConcurrentModification();
      if (!hasNext())
        throw new NoSuchElementException(); 
      this.indexToRemove = this.currentIndex;
      T result = getOutput(this.currentIndex);
      this.currentIndex = CompactHashMap.this.getSuccessor(this.currentIndex);
      return result;
    }
    
    public void remove() {
      checkForConcurrentModification();
      CollectPreconditions.checkRemove((this.indexToRemove >= 0));
      incrementExpectedModCount();
      CompactHashMap.this.remove(CompactHashMap.this.key(this.indexToRemove));
      this.currentIndex = CompactHashMap.this.adjustAfterRemove(this.currentIndex, this.indexToRemove);
      this.indexToRemove = -1;
    }
    
    void incrementExpectedModCount() {
      this.expectedMetadata += 32;
    }
    
    private void checkForConcurrentModification() {
      if (CompactHashMap.this.metadata != this.expectedMetadata)
        throw new ConcurrentModificationException(); 
    }
    
    private Itr() {}
    
    @ParametricNullness
    abstract T getOutput(int param1Int);
  }
  
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    Preconditions.checkNotNull(function);
    Map<K, V> delegate = delegateOrNull();
    if (delegate != null) {
      delegate.replaceAll(function);
    } else {
      for (int i = 0; i < this.size; i++)
        setValue(i, function.apply(key(i), value(i))); 
    } 
  }
  
  public Set<K> keySet() {
    return (this.keySetView == null) ? (this.keySetView = createKeySet()) : this.keySetView;
  }
  
  Set<K> createKeySet() {
    return new KeySetView();
  }
  
  class KeySetView extends Maps.KeySet<K, V> {
    KeySetView() {
      super(CompactHashMap.this);
    }
    
    public Object[] toArray() {
      if (CompactHashMap.this.needsAllocArrays())
        return new Object[0]; 
      Map<K, V> delegate = CompactHashMap.this.delegateOrNull();
      return (delegate != null) ? 
        delegate.keySet().toArray() : 
        ObjectArrays.copyAsObjectArray(CompactHashMap.this.requireKeys(), 0, CompactHashMap.this.size);
    }
    
    public <T> T[] toArray(T[] a) {
      if (CompactHashMap.this.needsAllocArrays()) {
        if (a.length > 0) {
          T[] arrayOfT = a;
          arrayOfT[0] = null;
        } 
        return a;
      } 
      Map<K, V> delegate = CompactHashMap.this.delegateOrNull();
      return (delegate != null) ? 
        (T[])delegate.keySet().toArray((Object[])a) : 
        ObjectArrays.<T>toArrayImpl(CompactHashMap.this.requireKeys(), 0, CompactHashMap.this.size, a);
    }
    
    public boolean remove(@CheckForNull Object o) {
      Map<K, V> delegate = CompactHashMap.this.delegateOrNull();
      return (delegate != null) ? 
        delegate.keySet().remove(o) : (
        (CompactHashMap.this.removeHelper(o) != CompactHashMap.NOT_FOUND));
    }
    
    public Iterator<K> iterator() {
      return CompactHashMap.this.keySetIterator();
    }
    
    public Spliterator<K> spliterator() {
      if (CompactHashMap.this.needsAllocArrays())
        return Spliterators.spliterator(new Object[0], 17); 
      Map<K, V> delegate = CompactHashMap.this.delegateOrNull();
      return (delegate != null) ? 
        delegate.keySet().spliterator() : 
        Spliterators.<K>spliterator(CompactHashMap.this
          .requireKeys(), 0, CompactHashMap.this.size, 17);
    }
    
    public void forEach(Consumer<? super K> action) {
      Preconditions.checkNotNull(action);
      Map<K, V> delegate = CompactHashMap.this.delegateOrNull();
      if (delegate != null) {
        delegate.keySet().forEach(action);
      } else {
        for (int i = CompactHashMap.this.firstEntryIndex(); i >= 0; i = CompactHashMap.this.getSuccessor(i))
          action.accept(CompactHashMap.this.key(i)); 
      } 
    }
  }
  
  Iterator<K> keySetIterator() {
    Map<K, V> delegate = delegateOrNull();
    if (delegate != null)
      return delegate.keySet().iterator(); 
    return new Itr<K>() {
        @ParametricNullness
        K getOutput(int entry) {
          return CompactHashMap.this.key(entry);
        }
      };
  }
  
  public void forEach(BiConsumer<? super K, ? super V> action) {
    Preconditions.checkNotNull(action);
    Map<K, V> delegate = delegateOrNull();
    if (delegate != null) {
      delegate.forEach(action);
    } else {
      for (int i = firstEntryIndex(); i >= 0; i = getSuccessor(i))
        action.accept(key(i), value(i)); 
    } 
  }
  
  public Set<Map.Entry<K, V>> entrySet() {
    return (this.entrySetView == null) ? (this.entrySetView = createEntrySet()) : this.entrySetView;
  }
  
  Set<Map.Entry<K, V>> createEntrySet() {
    return new EntrySetView();
  }
  
  class EntrySetView extends Maps.EntrySet<K, V> {
    Map<K, V> map() {
      return CompactHashMap.this;
    }
    
    public Iterator<Map.Entry<K, V>> iterator() {
      return CompactHashMap.this.entrySetIterator();
    }
    
    public Spliterator<Map.Entry<K, V>> spliterator() {
      Map<K, V> delegate = CompactHashMap.this.delegateOrNull();
      return (delegate != null) ? 
        delegate.entrySet().spliterator() : 
        CollectSpliterators.<Map.Entry<K, V>>indexed(CompactHashMap.this
          .size, 17, x$0 -> new CompactHashMap.MapEntry(x$0));
    }
    
    public boolean contains(@CheckForNull Object o) {
      Map<K, V> delegate = CompactHashMap.this.delegateOrNull();
      if (delegate != null)
        return delegate.entrySet().contains(o); 
      if (o instanceof Map.Entry) {
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
        int index = CompactHashMap.this.indexOf(entry.getKey());
        return (index != -1 && Objects.equal(CompactHashMap.this.value(index), entry.getValue()));
      } 
      return false;
    }
    
    public boolean remove(@CheckForNull Object o) {
      Map<K, V> delegate = CompactHashMap.this.delegateOrNull();
      if (delegate != null)
        return delegate.entrySet().remove(o); 
      if (o instanceof Map.Entry) {
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
        if (CompactHashMap.this.needsAllocArrays())
          return false; 
        int mask = CompactHashMap.this.hashTableMask();
        int index = CompactHashing.remove(entry
            .getKey(), entry
            .getValue(), mask, CompactHashMap.this
            
            .requireTable(), CompactHashMap.this
            .requireEntries(), CompactHashMap.this
            .requireKeys(), CompactHashMap.this
            .requireValues());
        if (index == -1)
          return false; 
        CompactHashMap.this.moveLastEntry(index, mask);
        CompactHashMap.this.size--;
        CompactHashMap.this.incrementModCount();
        return true;
      } 
      return false;
    }
  }
  
  Iterator<Map.Entry<K, V>> entrySetIterator() {
    Map<K, V> delegate = delegateOrNull();
    if (delegate != null)
      return delegate.entrySet().iterator(); 
    return new Itr<Map.Entry<K, V>>() {
        Map.Entry<K, V> getOutput(int entry) {
          return new CompactHashMap.MapEntry(entry);
        }
      };
  }
  
  final class MapEntry extends AbstractMapEntry<K, V> {
    @ParametricNullness
    private final K key;
    
    private int lastKnownIndex;
    
    MapEntry(int index) {
      this.key = CompactHashMap.this.key(index);
      this.lastKnownIndex = index;
    }
    
    @ParametricNullness
    public K getKey() {
      return this.key;
    }
    
    private void updateLastKnownIndex() {
      if (this.lastKnownIndex == -1 || this.lastKnownIndex >= CompactHashMap.this
        .size() || 
        !Objects.equal(this.key, CompactHashMap.this.key(this.lastKnownIndex)))
        this.lastKnownIndex = CompactHashMap.this.indexOf(this.key); 
    }
    
    @ParametricNullness
    public V getValue() {
      Map<K, V> delegate = CompactHashMap.this.delegateOrNull();
      if (delegate != null)
        return NullnessCasts.uncheckedCastNullableTToT(delegate.get(this.key)); 
      updateLastKnownIndex();
      return (this.lastKnownIndex == -1) ? NullnessCasts.<V>unsafeNull() : CompactHashMap.this.value(this.lastKnownIndex);
    }
    
    @ParametricNullness
    public V setValue(@ParametricNullness V value) {
      Map<K, V> delegate = CompactHashMap.this.delegateOrNull();
      if (delegate != null)
        return NullnessCasts.uncheckedCastNullableTToT(delegate.put(this.key, value)); 
      updateLastKnownIndex();
      if (this.lastKnownIndex == -1) {
        CompactHashMap.this.put(this.key, value);
        return NullnessCasts.unsafeNull();
      } 
      V old = CompactHashMap.this.value(this.lastKnownIndex);
      CompactHashMap.this.setValue(this.lastKnownIndex, value);
      return old;
    }
  }
  
  public int size() {
    Map<K, V> delegate = delegateOrNull();
    return (delegate != null) ? delegate.size() : this.size;
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public boolean containsValue(@CheckForNull Object value) {
    Map<K, V> delegate = delegateOrNull();
    if (delegate != null)
      return delegate.containsValue(value); 
    for (int i = 0; i < this.size; i++) {
      if (Objects.equal(value, value(i)))
        return true; 
    } 
    return false;
  }
  
  public Collection<V> values() {
    return (this.valuesView == null) ? (this.valuesView = createValues()) : this.valuesView;
  }
  
  Collection<V> createValues() {
    return new ValuesView();
  }
  
  class ValuesView extends Maps.Values<K, V> {
    ValuesView() {
      super(CompactHashMap.this);
    }
    
    public Iterator<V> iterator() {
      return CompactHashMap.this.valuesIterator();
    }
    
    public void forEach(Consumer<? super V> action) {
      Preconditions.checkNotNull(action);
      Map<K, V> delegate = CompactHashMap.this.delegateOrNull();
      if (delegate != null) {
        delegate.values().forEach(action);
      } else {
        for (int i = CompactHashMap.this.firstEntryIndex(); i >= 0; i = CompactHashMap.this.getSuccessor(i))
          action.accept(CompactHashMap.this.value(i)); 
      } 
    }
    
    public Spliterator<V> spliterator() {
      if (CompactHashMap.this.needsAllocArrays())
        return Spliterators.spliterator(new Object[0], 16); 
      Map<K, V> delegate = CompactHashMap.this.delegateOrNull();
      return (delegate != null) ? 
        delegate.values().spliterator() : 
        Spliterators.<V>spliterator(CompactHashMap.this.requireValues(), 0, CompactHashMap.this.size, 16);
    }
    
    public Object[] toArray() {
      if (CompactHashMap.this.needsAllocArrays())
        return new Object[0]; 
      Map<K, V> delegate = CompactHashMap.this.delegateOrNull();
      return (delegate != null) ? 
        delegate.values().toArray() : 
        ObjectArrays.copyAsObjectArray(CompactHashMap.this.requireValues(), 0, CompactHashMap.this.size);
    }
    
    public <T> T[] toArray(T[] a) {
      if (CompactHashMap.this.needsAllocArrays()) {
        if (a.length > 0) {
          T[] arrayOfT = a;
          arrayOfT[0] = null;
        } 
        return a;
      } 
      Map<K, V> delegate = CompactHashMap.this.delegateOrNull();
      return (delegate != null) ? 
        (T[])delegate.values().toArray((Object[])a) : 
        ObjectArrays.<T>toArrayImpl(CompactHashMap.this.requireValues(), 0, CompactHashMap.this.size, a);
    }
  }
  
  Iterator<V> valuesIterator() {
    Map<K, V> delegate = delegateOrNull();
    if (delegate != null)
      return delegate.values().iterator(); 
    return new Itr<V>() {
        @ParametricNullness
        V getOutput(int entry) {
          return CompactHashMap.this.value(entry);
        }
      };
  }
  
  public void trimToSize() {
    if (needsAllocArrays())
      return; 
    Map<K, V> delegate = delegateOrNull();
    if (delegate != null) {
      Map<K, V> newDelegate = createHashFloodingResistantDelegate(size());
      newDelegate.putAll(delegate);
      this.table = newDelegate;
      return;
    } 
    int size = this.size;
    if (size < (requireEntries()).length)
      resizeEntries(size); 
    int minimumTableSize = CompactHashing.tableSize(size);
    int mask = hashTableMask();
    if (minimumTableSize < mask)
      resizeTable(mask, minimumTableSize, 0, 0); 
  }
  
  public void clear() {
    if (needsAllocArrays())
      return; 
    incrementModCount();
    Map<K, V> delegate = delegateOrNull();
    if (delegate != null) {
      this
        .metadata = Ints.constrainToRange(size(), 3, 1073741823);
      delegate.clear();
      this.table = null;
      this.size = 0;
    } else {
      Arrays.fill(requireKeys(), 0, this.size, (Object)null);
      Arrays.fill(requireValues(), 0, this.size, (Object)null);
      CompactHashing.tableClear(requireTable());
      Arrays.fill(requireEntries(), 0, this.size, 0);
      this.size = 0;
    } 
  }
  
  @J2ktIncompatible
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    stream.writeInt(size());
    Iterator<Map.Entry<K, V>> entryIterator = entrySetIterator();
    while (entryIterator.hasNext()) {
      Map.Entry<K, V> e = entryIterator.next();
      stream.writeObject(e.getKey());
      stream.writeObject(e.getValue());
    } 
  }
  
  @J2ktIncompatible
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    int elementCount = stream.readInt();
    if (elementCount < 0)
      throw new InvalidObjectException("Invalid size: " + elementCount); 
    init(elementCount);
    for (int i = 0; i < elementCount; i++) {
      K key = (K)stream.readObject();
      V value = (V)stream.readObject();
      put(key, value);
    } 
  }
  
  private Object requireTable() {
    return Objects.requireNonNull(this.table);
  }
  
  private int[] requireEntries() {
    return Objects.<int[]>requireNonNull(this.entries);
  }
  
  private Object[] requireKeys() {
    return Objects.<Object[]>requireNonNull(this.keys);
  }
  
  private Object[] requireValues() {
    return Objects.<Object[]>requireNonNull(this.values);
  }
  
  private K key(int i) {
    return (K)requireKeys()[i];
  }
  
  private V value(int i) {
    return (V)requireValues()[i];
  }
  
  private int entry(int i) {
    return requireEntries()[i];
  }
  
  private void setKey(int i, K key) {
    requireKeys()[i] = key;
  }
  
  private void setValue(int i, V value) {
    requireValues()[i] = value;
  }
  
  private void setEntry(int i, int value) {
    requireEntries()[i] = value;
  }
}
