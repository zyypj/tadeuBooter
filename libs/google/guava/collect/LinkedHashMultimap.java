package me.syncwrld.booter.libs.google.guava.collect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.Objects;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true, emulated = true)
public final class LinkedHashMultimap<K, V> extends LinkedHashMultimapGwtSerializationDependencies<K, V> {
  private static final int DEFAULT_KEY_CAPACITY = 16;
  
  private static final int DEFAULT_VALUE_SET_CAPACITY = 2;
  
  @VisibleForTesting
  static final double VALUE_SET_LOAD_FACTOR = 1.0D;
  
  public static <K, V> LinkedHashMultimap<K, V> create() {
    return new LinkedHashMultimap<>(16, 2);
  }
  
  public static <K, V> LinkedHashMultimap<K, V> create(int expectedKeys, int expectedValuesPerKey) {
    return new LinkedHashMultimap<>(
        Maps.capacity(expectedKeys), Maps.capacity(expectedValuesPerKey));
  }
  
  public static <K, V> LinkedHashMultimap<K, V> create(Multimap<? extends K, ? extends V> multimap) {
    LinkedHashMultimap<K, V> result = create(multimap.keySet().size(), 2);
    result.putAll(multimap);
    return result;
  }
  
  private static <K, V> void succeedsInValueSet(ValueSetLink<K, V> pred, ValueSetLink<K, V> succ) {
    pred.setSuccessorInValueSet(succ);
    succ.setPredecessorInValueSet(pred);
  }
  
  private static <K, V> void succeedsInMultimap(ValueEntry<K, V> pred, ValueEntry<K, V> succ) {
    pred.setSuccessorInMultimap(succ);
    succ.setPredecessorInMultimap(pred);
  }
  
  private static <K, V> void deleteFromValueSet(ValueSetLink<K, V> entry) {
    succeedsInValueSet(entry.getPredecessorInValueSet(), entry.getSuccessorInValueSet());
  }
  
  private static <K, V> void deleteFromMultimap(ValueEntry<K, V> entry) {
    succeedsInMultimap(entry.getPredecessorInMultimap(), entry.getSuccessorInMultimap());
  }
  
  @VisibleForTesting
  static final class ValueEntry<K, V> extends ImmutableEntry<K, V> implements ValueSetLink<K, V> {
    final int smearedValueHash;
    
    @CheckForNull
    ValueEntry<K, V> nextInValueBucket;
    
    @CheckForNull
    private LinkedHashMultimap.ValueSetLink<K, V> predecessorInValueSet;
    
    @CheckForNull
    private LinkedHashMultimap.ValueSetLink<K, V> successorInValueSet;
    
    @CheckForNull
    private ValueEntry<K, V> predecessorInMultimap;
    
    @CheckForNull
    private ValueEntry<K, V> successorInMultimap;
    
    ValueEntry(@ParametricNullness K key, @ParametricNullness V value, int smearedValueHash, @CheckForNull ValueEntry<K, V> nextInValueBucket) {
      super(key, value);
      this.smearedValueHash = smearedValueHash;
      this.nextInValueBucket = nextInValueBucket;
    }
    
    static <K, V> ValueEntry<K, V> newHeader() {
      return new ValueEntry<>(null, null, 0, null);
    }
    
    boolean matchesValue(@CheckForNull Object v, int smearedVHash) {
      return (this.smearedValueHash == smearedVHash && Objects.equal(getValue(), v));
    }
    
    public LinkedHashMultimap.ValueSetLink<K, V> getPredecessorInValueSet() {
      return Objects.<LinkedHashMultimap.ValueSetLink<K, V>>requireNonNull(this.predecessorInValueSet);
    }
    
    public LinkedHashMultimap.ValueSetLink<K, V> getSuccessorInValueSet() {
      return Objects.<LinkedHashMultimap.ValueSetLink<K, V>>requireNonNull(this.successorInValueSet);
    }
    
    public void setPredecessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> entry) {
      this.predecessorInValueSet = entry;
    }
    
    public void setSuccessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> entry) {
      this.successorInValueSet = entry;
    }
    
    public ValueEntry<K, V> getPredecessorInMultimap() {
      return Objects.<ValueEntry<K, V>>requireNonNull(this.predecessorInMultimap);
    }
    
    public ValueEntry<K, V> getSuccessorInMultimap() {
      return Objects.<ValueEntry<K, V>>requireNonNull(this.successorInMultimap);
    }
    
    public void setSuccessorInMultimap(ValueEntry<K, V> multimapSuccessor) {
      this.successorInMultimap = multimapSuccessor;
    }
    
    public void setPredecessorInMultimap(ValueEntry<K, V> multimapPredecessor) {
      this.predecessorInMultimap = multimapPredecessor;
    }
  }
  
  @VisibleForTesting
  transient int valueSetCapacity = 2;
  
  private transient ValueEntry<K, V> multimapHeaderEntry;
  
  @GwtIncompatible
  @J2ktIncompatible
  private static final long serialVersionUID = 1L;
  
  private LinkedHashMultimap(int keyCapacity, int valueSetCapacity) {
    super(Platform.newLinkedHashMapWithExpectedSize(keyCapacity));
    CollectPreconditions.checkNonnegative(valueSetCapacity, "expectedValuesPerKey");
    this.valueSetCapacity = valueSetCapacity;
    this.multimapHeaderEntry = ValueEntry.newHeader();
    succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
  }
  
  Set<V> createCollection() {
    return Platform.newLinkedHashSetWithExpectedSize(this.valueSetCapacity);
  }
  
  Collection<V> createCollection(@ParametricNullness K key) {
    return new ValueSet(key, this.valueSetCapacity);
  }
  
  @CanIgnoreReturnValue
  public Set<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
    return super.replaceValues(key, values);
  }
  
  public Set<Map.Entry<K, V>> entries() {
    return super.entries();
  }
  
  public Set<K> keySet() {
    return super.keySet();
  }
  
  public Collection<V> values() {
    return super.values();
  }
  
  @VisibleForTesting
  final class ValueSet extends Sets.ImprovedAbstractSet<V> implements ValueSetLink<K, V> {
    @ParametricNullness
    private final K key;
    
    @VisibleForTesting
    LinkedHashMultimap.ValueEntry<K, V>[] hashTable;
    
    private int size = 0;
    
    private int modCount = 0;
    
    private LinkedHashMultimap.ValueSetLink<K, V> firstEntry;
    
    private LinkedHashMultimap.ValueSetLink<K, V> lastEntry;
    
    ValueSet(K key, int expectedValues) {
      this.key = key;
      this.firstEntry = this;
      this.lastEntry = this;
      int tableSize = Hashing.closedTableSize(expectedValues, 1.0D);
      LinkedHashMultimap.ValueEntry[] arrayOfValueEntry = new LinkedHashMultimap.ValueEntry[tableSize];
      this.hashTable = (LinkedHashMultimap.ValueEntry<K, V>[])arrayOfValueEntry;
    }
    
    private int mask() {
      return this.hashTable.length - 1;
    }
    
    public LinkedHashMultimap.ValueSetLink<K, V> getPredecessorInValueSet() {
      return this.lastEntry;
    }
    
    public LinkedHashMultimap.ValueSetLink<K, V> getSuccessorInValueSet() {
      return this.firstEntry;
    }
    
    public void setPredecessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> entry) {
      this.lastEntry = entry;
    }
    
    public void setSuccessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> entry) {
      this.firstEntry = entry;
    }
    
    public Iterator<V> iterator() {
      return new Iterator<V>() {
          LinkedHashMultimap.ValueSetLink<K, V> nextEntry = LinkedHashMultimap.ValueSet.this.firstEntry;
          
          @CheckForNull
          LinkedHashMultimap.ValueEntry<K, V> toRemove;
          
          int expectedModCount = LinkedHashMultimap.ValueSet.this.modCount;
          
          private void checkForComodification() {
            if (LinkedHashMultimap.ValueSet.this.modCount != this.expectedModCount)
              throw new ConcurrentModificationException(); 
          }
          
          public boolean hasNext() {
            checkForComodification();
            return (this.nextEntry != LinkedHashMultimap.ValueSet.this);
          }
          
          @ParametricNullness
          public V next() {
            if (!hasNext())
              throw new NoSuchElementException(); 
            LinkedHashMultimap.ValueEntry<K, V> entry = (LinkedHashMultimap.ValueEntry<K, V>)this.nextEntry;
            V result = entry.getValue();
            this.toRemove = entry;
            this.nextEntry = entry.getSuccessorInValueSet();
            return result;
          }
          
          public void remove() {
            checkForComodification();
            Preconditions.checkState((this.toRemove != null), "no calls to next() since the last call to remove()");
            LinkedHashMultimap.ValueSet.this.remove(this.toRemove.getValue());
            this.expectedModCount = LinkedHashMultimap.ValueSet.this.modCount;
            this.toRemove = null;
          }
        };
    }
    
    public void forEach(Consumer<? super V> action) {
      Preconditions.checkNotNull(action);
      LinkedHashMultimap.ValueSetLink<K, V> entry = this.firstEntry;
      for (; entry != this; 
        entry = entry.getSuccessorInValueSet())
        action.accept((V)((LinkedHashMultimap.ValueEntry)entry).getValue()); 
    }
    
    public int size() {
      return this.size;
    }
    
    public boolean contains(@CheckForNull Object o) {
      int smearedHash = Hashing.smearedHash(o);
      LinkedHashMultimap.ValueEntry<K, V> entry = this.hashTable[smearedHash & mask()];
      for (; entry != null; 
        entry = entry.nextInValueBucket) {
        if (entry.matchesValue(o, smearedHash))
          return true; 
      } 
      return false;
    }
    
    public boolean add(@ParametricNullness V value) {
      int smearedHash = Hashing.smearedHash(value);
      int bucket = smearedHash & mask();
      LinkedHashMultimap.ValueEntry<K, V> rowHead = this.hashTable[bucket];
      for (LinkedHashMultimap.ValueEntry<K, V> entry = rowHead; entry != null; entry = entry.nextInValueBucket) {
        if (entry.matchesValue(value, smearedHash))
          return false; 
      } 
      LinkedHashMultimap.ValueEntry<K, V> newEntry = new LinkedHashMultimap.ValueEntry<>(this.key, value, smearedHash, rowHead);
      LinkedHashMultimap.succeedsInValueSet(this.lastEntry, newEntry);
      LinkedHashMultimap.succeedsInValueSet(newEntry, this);
      LinkedHashMultimap.succeedsInMultimap(LinkedHashMultimap.this.multimapHeaderEntry.getPredecessorInMultimap(), newEntry);
      LinkedHashMultimap.succeedsInMultimap(newEntry, LinkedHashMultimap.this.multimapHeaderEntry);
      this.hashTable[bucket] = newEntry;
      this.size++;
      this.modCount++;
      rehashIfNecessary();
      return true;
    }
    
    private void rehashIfNecessary() {
      if (Hashing.needsResizing(this.size, this.hashTable.length, 1.0D)) {
        LinkedHashMultimap.ValueEntry[] arrayOfValueEntry = new LinkedHashMultimap.ValueEntry[this.hashTable.length * 2];
        this.hashTable = (LinkedHashMultimap.ValueEntry<K, V>[])arrayOfValueEntry;
        int mask = arrayOfValueEntry.length - 1;
        LinkedHashMultimap.ValueSetLink<K, V> entry = this.firstEntry;
        for (; entry != this; 
          entry = entry.getSuccessorInValueSet()) {
          LinkedHashMultimap.ValueEntry<K, V> valueEntry = (LinkedHashMultimap.ValueEntry<K, V>)entry;
          int bucket = valueEntry.smearedValueHash & mask;
          valueEntry.nextInValueBucket = arrayOfValueEntry[bucket];
          arrayOfValueEntry[bucket] = valueEntry;
        } 
      } 
    }
    
    @CanIgnoreReturnValue
    public boolean remove(@CheckForNull Object o) {
      int smearedHash = Hashing.smearedHash(o);
      int bucket = smearedHash & mask();
      LinkedHashMultimap.ValueEntry<K, V> prev = null;
      LinkedHashMultimap.ValueEntry<K, V> entry = this.hashTable[bucket];
      for (; entry != null; 
        prev = entry, entry = entry.nextInValueBucket) {
        if (entry.matchesValue(o, smearedHash)) {
          if (prev == null) {
            this.hashTable[bucket] = entry.nextInValueBucket;
          } else {
            prev.nextInValueBucket = entry.nextInValueBucket;
          } 
          LinkedHashMultimap.deleteFromValueSet(entry);
          LinkedHashMultimap.deleteFromMultimap(entry);
          this.size--;
          this.modCount++;
          return true;
        } 
      } 
      return false;
    }
    
    public void clear() {
      Arrays.fill((Object[])this.hashTable, (Object)null);
      this.size = 0;
      LinkedHashMultimap.ValueSetLink<K, V> entry = this.firstEntry;
      for (; entry != this; 
        entry = entry.getSuccessorInValueSet()) {
        LinkedHashMultimap.ValueEntry<K, V> valueEntry = (LinkedHashMultimap.ValueEntry<K, V>)entry;
        LinkedHashMultimap.deleteFromMultimap(valueEntry);
      } 
      LinkedHashMultimap.succeedsInValueSet(this, this);
      this.modCount++;
    }
  }
  
  Iterator<Map.Entry<K, V>> entryIterator() {
    return new Iterator<Map.Entry<K, V>>() {
        LinkedHashMultimap.ValueEntry<K, V> nextEntry = LinkedHashMultimap.this.multimapHeaderEntry.getSuccessorInMultimap();
        
        @CheckForNull
        LinkedHashMultimap.ValueEntry<K, V> toRemove;
        
        public boolean hasNext() {
          return (this.nextEntry != LinkedHashMultimap.this.multimapHeaderEntry);
        }
        
        public Map.Entry<K, V> next() {
          if (!hasNext())
            throw new NoSuchElementException(); 
          LinkedHashMultimap.ValueEntry<K, V> result = this.nextEntry;
          this.toRemove = result;
          this.nextEntry = this.nextEntry.getSuccessorInMultimap();
          return result;
        }
        
        public void remove() {
          Preconditions.checkState((this.toRemove != null), "no calls to next() since the last call to remove()");
          LinkedHashMultimap.this.remove(this.toRemove.getKey(), this.toRemove.getValue());
          this.toRemove = null;
        }
      };
  }
  
  Spliterator<Map.Entry<K, V>> entrySpliterator() {
    return Spliterators.spliterator(entries(), 17);
  }
  
  Iterator<V> valueIterator() {
    return Maps.valueIterator(entryIterator());
  }
  
  Spliterator<V> valueSpliterator() {
    return CollectSpliterators.map(entrySpliterator(), Map.Entry::getValue);
  }
  
  public void clear() {
    super.clear();
    succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    stream.writeInt(keySet().size());
    for (K key : keySet())
      stream.writeObject(key); 
    stream.writeInt(size());
    for (Map.Entry<K, V> entry : entries()) {
      stream.writeObject(entry.getKey());
      stream.writeObject(entry.getValue());
    } 
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    this.multimapHeaderEntry = ValueEntry.newHeader();
    succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
    this.valueSetCapacity = 2;
    int distinctKeys = stream.readInt();
    Map<K, Collection<V>> map = Platform.newLinkedHashMapWithExpectedSize(12);
    for (int i = 0; i < distinctKeys; i++) {
      K key = (K)stream.readObject();
      map.put(key, createCollection(key));
    } 
    int entries = stream.readInt();
    for (int j = 0; j < entries; j++) {
      K key = (K)stream.readObject();
      V value = (V)stream.readObject();
      ((Collection<V>)Objects.<Collection<V>>requireNonNull(map.get(key))).add(value);
    } 
    setMap(map);
  }
  
  private static interface ValueSetLink<K, V> {
    ValueSetLink<K, V> getPredecessorInValueSet();
    
    ValueSetLink<K, V> getSuccessorInValueSet();
    
    void setPredecessorInValueSet(ValueSetLink<K, V> param1ValueSetLink);
    
    void setSuccessorInValueSet(ValueSetLink<K, V> param1ValueSetLink);
  }
}
