package me.syncwrld.booter.libs.google.guava.collect;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.LazyInit;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Objects;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.j2objc.annotations.RetainedWith;
import me.syncwrld.booter.libs.google.j2objc.annotations.Weak;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
public final class HashBiMap<K, V> extends Maps.IteratorBasedAbstractMap<K, V> implements BiMap<K, V>, Serializable {
  private static final double LOAD_FACTOR = 1.0D;
  
  private transient BiEntry<K, V>[] hashTableKToV;
  
  private transient BiEntry<K, V>[] hashTableVToK;
  
  @CheckForNull
  @Weak
  private transient BiEntry<K, V> firstInKeyInsertionOrder;
  
  @CheckForNull
  @Weak
  private transient BiEntry<K, V> lastInKeyInsertionOrder;
  
  private transient int size;
  
  private transient int mask;
  
  private transient int modCount;
  
  @LazyInit
  @CheckForNull
  @RetainedWith
  private transient BiMap<V, K> inverse;
  
  @GwtIncompatible
  @J2ktIncompatible
  private static final long serialVersionUID = 0L;
  
  public static <K, V> HashBiMap<K, V> create() {
    return create(16);
  }
  
  public static <K, V> HashBiMap<K, V> create(int expectedSize) {
    return new HashBiMap<>(expectedSize);
  }
  
  public static <K, V> HashBiMap<K, V> create(Map<? extends K, ? extends V> map) {
    HashBiMap<K, V> bimap = create(map.size());
    bimap.putAll(map);
    return bimap;
  }
  
  static final class BiEntry<K, V> extends ImmutableEntry<K, V> {
    final int keyHash;
    
    final int valueHash;
    
    @CheckForNull
    BiEntry<K, V> nextInKToVBucket;
    
    @CheckForNull
    @Weak
    BiEntry<K, V> nextInVToKBucket;
    
    @CheckForNull
    @Weak
    BiEntry<K, V> nextInKeyInsertionOrder;
    
    @CheckForNull
    @Weak
    BiEntry<K, V> prevInKeyInsertionOrder;
    
    BiEntry(@ParametricNullness K key, int keyHash, @ParametricNullness V value, int valueHash) {
      super(key, value);
      this.keyHash = keyHash;
      this.valueHash = valueHash;
    }
  }
  
  private HashBiMap(int expectedSize) {
    init(expectedSize);
  }
  
  private void init(int expectedSize) {
    CollectPreconditions.checkNonnegative(expectedSize, "expectedSize");
    int tableSize = Hashing.closedTableSize(expectedSize, 1.0D);
    this.hashTableKToV = createTable(tableSize);
    this.hashTableVToK = createTable(tableSize);
    this.firstInKeyInsertionOrder = null;
    this.lastInKeyInsertionOrder = null;
    this.size = 0;
    this.mask = tableSize - 1;
    this.modCount = 0;
  }
  
  private void delete(BiEntry<K, V> entry) {
    int keyBucket = entry.keyHash & this.mask;
    BiEntry<K, V> prevBucketEntry = null;
    BiEntry<K, V> bucketEntry = this.hashTableKToV[keyBucket];
    for (;; bucketEntry = bucketEntry.nextInKToVBucket) {
      if (bucketEntry == entry) {
        if (prevBucketEntry == null) {
          this.hashTableKToV[keyBucket] = entry.nextInKToVBucket;
          break;
        } 
        prevBucketEntry.nextInKToVBucket = entry.nextInKToVBucket;
        break;
      } 
      prevBucketEntry = bucketEntry;
    } 
    int valueBucket = entry.valueHash & this.mask;
    prevBucketEntry = null;
    BiEntry<K, V> biEntry1 = this.hashTableVToK[valueBucket];
    for (;; biEntry1 = biEntry1.nextInVToKBucket) {
      if (biEntry1 == entry) {
        if (prevBucketEntry == null) {
          this.hashTableVToK[valueBucket] = entry.nextInVToKBucket;
          break;
        } 
        prevBucketEntry.nextInVToKBucket = entry.nextInVToKBucket;
        break;
      } 
      prevBucketEntry = biEntry1;
    } 
    if (entry.prevInKeyInsertionOrder == null) {
      this.firstInKeyInsertionOrder = entry.nextInKeyInsertionOrder;
    } else {
      entry.prevInKeyInsertionOrder.nextInKeyInsertionOrder = entry.nextInKeyInsertionOrder;
    } 
    if (entry.nextInKeyInsertionOrder == null) {
      this.lastInKeyInsertionOrder = entry.prevInKeyInsertionOrder;
    } else {
      entry.nextInKeyInsertionOrder.prevInKeyInsertionOrder = entry.prevInKeyInsertionOrder;
    } 
    this.size--;
    this.modCount++;
  }
  
  private void insert(BiEntry<K, V> entry, @CheckForNull BiEntry<K, V> oldEntryForKey) {
    int keyBucket = entry.keyHash & this.mask;
    entry.nextInKToVBucket = this.hashTableKToV[keyBucket];
    this.hashTableKToV[keyBucket] = entry;
    int valueBucket = entry.valueHash & this.mask;
    entry.nextInVToKBucket = this.hashTableVToK[valueBucket];
    this.hashTableVToK[valueBucket] = entry;
    if (oldEntryForKey == null) {
      entry.prevInKeyInsertionOrder = this.lastInKeyInsertionOrder;
      entry.nextInKeyInsertionOrder = null;
      if (this.lastInKeyInsertionOrder == null) {
        this.firstInKeyInsertionOrder = entry;
      } else {
        this.lastInKeyInsertionOrder.nextInKeyInsertionOrder = entry;
      } 
      this.lastInKeyInsertionOrder = entry;
    } else {
      entry.prevInKeyInsertionOrder = oldEntryForKey.prevInKeyInsertionOrder;
      if (entry.prevInKeyInsertionOrder == null) {
        this.firstInKeyInsertionOrder = entry;
      } else {
        entry.prevInKeyInsertionOrder.nextInKeyInsertionOrder = entry;
      } 
      entry.nextInKeyInsertionOrder = oldEntryForKey.nextInKeyInsertionOrder;
      if (entry.nextInKeyInsertionOrder == null) {
        this.lastInKeyInsertionOrder = entry;
      } else {
        entry.nextInKeyInsertionOrder.prevInKeyInsertionOrder = entry;
      } 
    } 
    this.size++;
    this.modCount++;
  }
  
  @CheckForNull
  private BiEntry<K, V> seekByKey(@CheckForNull Object key, int keyHash) {
    BiEntry<K, V> entry = this.hashTableKToV[keyHash & this.mask];
    for (; entry != null; 
      entry = entry.nextInKToVBucket) {
      if (keyHash == entry.keyHash && Objects.equal(key, entry.key))
        return entry; 
    } 
    return null;
  }
  
  @CheckForNull
  private BiEntry<K, V> seekByValue(@CheckForNull Object value, int valueHash) {
    BiEntry<K, V> entry = this.hashTableVToK[valueHash & this.mask];
    for (; entry != null; 
      entry = entry.nextInVToKBucket) {
      if (valueHash == entry.valueHash && Objects.equal(value, entry.value))
        return entry; 
    } 
    return null;
  }
  
  public boolean containsKey(@CheckForNull Object key) {
    return (seekByKey(key, Hashing.smearedHash(key)) != null);
  }
  
  public boolean containsValue(@CheckForNull Object value) {
    return (seekByValue(value, Hashing.smearedHash(value)) != null);
  }
  
  @CheckForNull
  public V get(@CheckForNull Object key) {
    return Maps.valueOrNull(seekByKey(key, Hashing.smearedHash(key)));
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public V put(@ParametricNullness K key, @ParametricNullness V value) {
    return put(key, value, false);
  }
  
  @CheckForNull
  private V put(@ParametricNullness K key, @ParametricNullness V value, boolean force) {
    int keyHash = Hashing.smearedHash(key);
    int valueHash = Hashing.smearedHash(value);
    BiEntry<K, V> oldEntryForKey = seekByKey(key, keyHash);
    if (oldEntryForKey != null && valueHash == oldEntryForKey.valueHash && 
      
      Objects.equal(value, oldEntryForKey.value))
      return value; 
    BiEntry<K, V> oldEntryForValue = seekByValue(value, valueHash);
    if (oldEntryForValue != null)
      if (force) {
        delete(oldEntryForValue);
      } else {
        throw new IllegalArgumentException("value already present: " + value);
      }  
    BiEntry<K, V> newEntry = new BiEntry<>(key, keyHash, value, valueHash);
    if (oldEntryForKey != null) {
      delete(oldEntryForKey);
      insert(newEntry, oldEntryForKey);
      oldEntryForKey.prevInKeyInsertionOrder = null;
      oldEntryForKey.nextInKeyInsertionOrder = null;
      return oldEntryForKey.value;
    } 
    insert(newEntry, (BiEntry<K, V>)null);
    rehashIfNecessary();
    return null;
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public V forcePut(@ParametricNullness K key, @ParametricNullness V value) {
    return put(key, value, true);
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  private K putInverse(@ParametricNullness V value, @ParametricNullness K key, boolean force) {
    int valueHash = Hashing.smearedHash(value);
    int keyHash = Hashing.smearedHash(key);
    BiEntry<K, V> oldEntryForValue = seekByValue(value, valueHash);
    BiEntry<K, V> oldEntryForKey = seekByKey(key, keyHash);
    if (oldEntryForValue != null && keyHash == oldEntryForValue.keyHash && 
      
      Objects.equal(key, oldEntryForValue.key))
      return key; 
    if (oldEntryForKey != null && !force)
      throw new IllegalArgumentException("key already present: " + key); 
    if (oldEntryForValue != null)
      delete(oldEntryForValue); 
    if (oldEntryForKey != null)
      delete(oldEntryForKey); 
    BiEntry<K, V> newEntry = new BiEntry<>(key, keyHash, value, valueHash);
    insert(newEntry, oldEntryForKey);
    if (oldEntryForKey != null) {
      oldEntryForKey.prevInKeyInsertionOrder = null;
      oldEntryForKey.nextInKeyInsertionOrder = null;
    } 
    if (oldEntryForValue != null) {
      oldEntryForValue.prevInKeyInsertionOrder = null;
      oldEntryForValue.nextInKeyInsertionOrder = null;
    } 
    rehashIfNecessary();
    return Maps.keyOrNull(oldEntryForValue);
  }
  
  private void rehashIfNecessary() {
    BiEntry<K, V>[] oldKToV = this.hashTableKToV;
    if (Hashing.needsResizing(this.size, oldKToV.length, 1.0D)) {
      int newTableSize = oldKToV.length * 2;
      this.hashTableKToV = createTable(newTableSize);
      this.hashTableVToK = createTable(newTableSize);
      this.mask = newTableSize - 1;
      this.size = 0;
      BiEntry<K, V> entry = this.firstInKeyInsertionOrder;
      for (; entry != null; 
        entry = entry.nextInKeyInsertionOrder)
        insert(entry, entry); 
      this.modCount++;
    } 
  }
  
  private BiEntry<K, V>[] createTable(int length) {
    return (BiEntry<K, V>[])new BiEntry[length];
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public V remove(@CheckForNull Object key) {
    BiEntry<K, V> entry = seekByKey(key, Hashing.smearedHash(key));
    if (entry == null)
      return null; 
    delete(entry);
    entry.prevInKeyInsertionOrder = null;
    entry.nextInKeyInsertionOrder = null;
    return entry.value;
  }
  
  public void clear() {
    this.size = 0;
    Arrays.fill((Object[])this.hashTableKToV, (Object)null);
    Arrays.fill((Object[])this.hashTableVToK, (Object)null);
    this.firstInKeyInsertionOrder = null;
    this.lastInKeyInsertionOrder = null;
    this.modCount++;
  }
  
  public int size() {
    return this.size;
  }
  
  private abstract class Itr<T> implements Iterator<T> {
    @CheckForNull
    HashBiMap.BiEntry<K, V> next = HashBiMap.this.firstInKeyInsertionOrder;
    
    @CheckForNull
    HashBiMap.BiEntry<K, V> toRemove = null;
    
    int expectedModCount = HashBiMap.this.modCount;
    
    int remaining = HashBiMap.this.size();
    
    public boolean hasNext() {
      if (HashBiMap.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      return (this.next != null && this.remaining > 0);
    }
    
    public T next() {
      if (!hasNext())
        throw new NoSuchElementException(); 
      HashBiMap.BiEntry<K, V> entry = Objects.<HashBiMap.BiEntry<K, V>>requireNonNull(this.next);
      this.next = entry.nextInKeyInsertionOrder;
      this.toRemove = entry;
      this.remaining--;
      return output(entry);
    }
    
    public void remove() {
      if (HashBiMap.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      if (this.toRemove == null)
        throw new IllegalStateException("no calls to next() since the last call to remove()"); 
      HashBiMap.this.delete(this.toRemove);
      this.expectedModCount = HashBiMap.this.modCount;
      this.toRemove = null;
    }
    
    private Itr() {}
    
    abstract T output(HashBiMap.BiEntry<K, V> param1BiEntry);
  }
  
  public Set<K> keySet() {
    return new KeySet();
  }
  
  private final class KeySet extends Maps.KeySet<K, V> {
    KeySet() {
      super(HashBiMap.this);
    }
    
    public Iterator<K> iterator() {
      return new HashBiMap<K, V>.Itr<K>(this) {
          @ParametricNullness
          K output(HashBiMap.BiEntry<K, V> entry) {
            return entry.key;
          }
        };
    }
    
    public boolean remove(@CheckForNull Object o) {
      HashBiMap.BiEntry<K, V> entry = HashBiMap.this.seekByKey(o, Hashing.smearedHash(o));
      if (entry == null)
        return false; 
      HashBiMap.this.delete(entry);
      entry.prevInKeyInsertionOrder = null;
      entry.nextInKeyInsertionOrder = null;
      return true;
    }
  }
  
  public Set<V> values() {
    return inverse().keySet();
  }
  
  Iterator<Map.Entry<K, V>> entryIterator() {
    return new Itr<Map.Entry<K, V>>() {
        Map.Entry<K, V> output(HashBiMap.BiEntry<K, V> entry) {
          return new MapEntry(entry);
        }
        
        class MapEntry extends AbstractMapEntry<K, V> {
          private HashBiMap.BiEntry<K, V> delegate;
          
          @ParametricNullness
          public K getKey() {
            return this.delegate.key;
          }
          
          @ParametricNullness
          public V getValue() {
            return this.delegate.value;
          }
          
          @ParametricNullness
          public V setValue(@ParametricNullness V value) {
            V oldValue = this.delegate.value;
            int valueHash = Hashing.smearedHash(value);
            if (valueHash == this.delegate.valueHash && Objects.equal(value, oldValue))
              return value; 
            Preconditions.checkArgument((HashBiMap.this.seekByValue(value, valueHash) == null), "value already present: %s", value);
            HashBiMap.this.delete(this.delegate);
            HashBiMap.BiEntry<K, V> newEntry = new HashBiMap.BiEntry<>(this.delegate.key, this.delegate.keyHash, value, valueHash);
            HashBiMap.this.insert(newEntry, this.delegate);
            this.delegate.prevInKeyInsertionOrder = null;
            this.delegate.nextInKeyInsertionOrder = null;
            HashBiMap.null.this.expectedModCount = HashBiMap.this.modCount;
            if (HashBiMap.null.this.toRemove == this.delegate)
              HashBiMap.null.this.toRemove = newEntry; 
            this.delegate = newEntry;
            return oldValue;
          }
        }
      };
  }
  
  public void forEach(BiConsumer<? super K, ? super V> action) {
    Preconditions.checkNotNull(action);
    BiEntry<K, V> entry = this.firstInKeyInsertionOrder;
    for (; entry != null; 
      entry = entry.nextInKeyInsertionOrder)
      action.accept(entry.key, entry.value); 
  }
  
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    Preconditions.checkNotNull(function);
    BiEntry<K, V> oldFirst = this.firstInKeyInsertionOrder;
    clear();
    for (BiEntry<K, V> entry = oldFirst; entry != null; entry = entry.nextInKeyInsertionOrder)
      put(entry.key, function.apply(entry.key, entry.value)); 
  }
  
  public BiMap<V, K> inverse() {
    BiMap<V, K> result = this.inverse;
    return (result == null) ? (this.inverse = new Inverse()) : result;
  }
  
  private final class Inverse extends Maps.IteratorBasedAbstractMap<V, K> implements BiMap<V, K>, Serializable {
    private Inverse() {}
    
    BiMap<K, V> forward() {
      return HashBiMap.this;
    }
    
    public int size() {
      return HashBiMap.this.size;
    }
    
    public void clear() {
      forward().clear();
    }
    
    public boolean containsKey(@CheckForNull Object value) {
      return forward().containsValue(value);
    }
    
    @CheckForNull
    public K get(@CheckForNull Object value) {
      return Maps.keyOrNull(HashBiMap.this.seekByValue(value, Hashing.smearedHash(value)));
    }
    
    @CheckForNull
    @CanIgnoreReturnValue
    public K put(@ParametricNullness V value, @ParametricNullness K key) {
      return HashBiMap.this.putInverse(value, key, false);
    }
    
    @CheckForNull
    public K forcePut(@ParametricNullness V value, @ParametricNullness K key) {
      return HashBiMap.this.putInverse(value, key, true);
    }
    
    @CheckForNull
    public K remove(@CheckForNull Object value) {
      HashBiMap.BiEntry<K, V> entry = HashBiMap.this.seekByValue(value, Hashing.smearedHash(value));
      if (entry == null)
        return null; 
      HashBiMap.this.delete(entry);
      entry.prevInKeyInsertionOrder = null;
      entry.nextInKeyInsertionOrder = null;
      return entry.key;
    }
    
    public BiMap<K, V> inverse() {
      return forward();
    }
    
    public Set<V> keySet() {
      return new InverseKeySet();
    }
    
    private final class InverseKeySet extends Maps.KeySet<V, K> {
      InverseKeySet() {
        super(HashBiMap.Inverse.this);
      }
      
      public boolean remove(@CheckForNull Object o) {
        HashBiMap.BiEntry<K, V> entry = HashBiMap.this.seekByValue(o, Hashing.smearedHash(o));
        if (entry == null)
          return false; 
        HashBiMap.this.delete(entry);
        return true;
      }
      
      public Iterator<V> iterator() {
        return new HashBiMap<K, V>.Itr<V>(this) {
            @ParametricNullness
            V output(HashBiMap.BiEntry<K, V> entry) {
              return entry.value;
            }
          };
      }
    }
    
    public Set<K> values() {
      return forward().keySet();
    }
    
    Iterator<Map.Entry<V, K>> entryIterator() {
      return new HashBiMap<K, V>.Itr<Map.Entry<V, K>>() {
          Map.Entry<V, K> output(HashBiMap.BiEntry<K, V> entry) {
            return new InverseEntry(entry);
          }
          
          class InverseEntry extends AbstractMapEntry<V, K> {
            private HashBiMap.BiEntry<K, V> delegate;
            
            @ParametricNullness
            public V getKey() {
              return this.delegate.value;
            }
            
            @ParametricNullness
            public K getValue() {
              return this.delegate.key;
            }
            
            @ParametricNullness
            public K setValue(@ParametricNullness K key) {
              K oldKey = this.delegate.key;
              int keyHash = Hashing.smearedHash(key);
              if (keyHash == this.delegate.keyHash && Objects.equal(key, oldKey))
                return key; 
              Preconditions.checkArgument((HashBiMap.this.seekByKey(key, keyHash) == null), "value already present: %s", key);
              HashBiMap.this.delete(this.delegate);
              HashBiMap.BiEntry<K, V> newEntry = new HashBiMap.BiEntry<>(key, keyHash, this.delegate.value, this.delegate.valueHash);
              this.delegate = newEntry;
              HashBiMap.this.insert(newEntry, (HashBiMap.BiEntry<K, V>)null);
              HashBiMap.Inverse.null.this.expectedModCount = HashBiMap.this.modCount;
              return oldKey;
            }
          }
        };
    }
    
    public void forEach(BiConsumer<? super V, ? super K> action) {
      Preconditions.checkNotNull(action);
      HashBiMap.this.forEach((k, v) -> action.accept(v, k));
    }
    
    public void replaceAll(BiFunction<? super V, ? super K, ? extends K> function) {
      Preconditions.checkNotNull(function);
      HashBiMap.BiEntry<K, V> oldFirst = HashBiMap.this.firstInKeyInsertionOrder;
      clear();
      for (HashBiMap.BiEntry<K, V> entry = oldFirst; entry != null; entry = entry.nextInKeyInsertionOrder)
        put(entry.value, function.apply(entry.value, entry.key)); 
    }
    
    Object writeReplace() {
      return new HashBiMap.InverseSerializedForm<>(HashBiMap.this);
    }
    
    @GwtIncompatible
    @J2ktIncompatible
    private void readObject(ObjectInputStream in) throws InvalidObjectException {
      throw new InvalidObjectException("Use InverseSerializedForm");
    }
  }
  
  private static final class InverseSerializedForm<K, V> implements Serializable {
    private final HashBiMap<K, V> bimap;
    
    InverseSerializedForm(HashBiMap<K, V> bimap) {
      this.bimap = bimap;
    }
    
    Object readResolve() {
      return this.bimap.inverse();
    }
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    Serialization.writeMap(this, stream);
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    int size = Serialization.readCount(stream);
    init(16);
    Serialization.populateMap(this, stream, size);
  }
}
