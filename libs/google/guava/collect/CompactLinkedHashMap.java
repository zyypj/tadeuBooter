package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
class CompactLinkedHashMap<K, V> extends CompactHashMap<K, V> {
  private static final int ENDPOINT = -2;
  
  @CheckForNull
  @VisibleForTesting
  transient long[] links;
  
  private transient int firstEntry;
  
  private transient int lastEntry;
  
  private final boolean accessOrder;
  
  public static <K, V> CompactLinkedHashMap<K, V> create() {
    return new CompactLinkedHashMap<>();
  }
  
  public static <K, V> CompactLinkedHashMap<K, V> createWithExpectedSize(int expectedSize) {
    return new CompactLinkedHashMap<>(expectedSize);
  }
  
  CompactLinkedHashMap() {
    this(3);
  }
  
  CompactLinkedHashMap(int expectedSize) {
    this(expectedSize, false);
  }
  
  CompactLinkedHashMap(int expectedSize, boolean accessOrder) {
    super(expectedSize);
    this.accessOrder = accessOrder;
  }
  
  void init(int expectedSize) {
    super.init(expectedSize);
    this.firstEntry = -2;
    this.lastEntry = -2;
  }
  
  int allocArrays() {
    int expectedSize = super.allocArrays();
    this.links = new long[expectedSize];
    return expectedSize;
  }
  
  Map<K, V> createHashFloodingResistantDelegate(int tableSize) {
    return new LinkedHashMap<>(tableSize, 1.0F, this.accessOrder);
  }
  
  @CanIgnoreReturnValue
  Map<K, V> convertToHashFloodingResistantImplementation() {
    Map<K, V> result = super.convertToHashFloodingResistantImplementation();
    this.links = null;
    return result;
  }
  
  private int getPredecessor(int entry) {
    return (int)(link(entry) >>> 32L) - 1;
  }
  
  int getSuccessor(int entry) {
    return (int)link(entry) - 1;
  }
  
  private void setSuccessor(int entry, int succ) {
    long succMask = 4294967295L;
    setLink(entry, link(entry) & (succMask ^ 0xFFFFFFFFFFFFFFFFL) | (succ + 1) & succMask);
  }
  
  private void setPredecessor(int entry, int pred) {
    long predMask = -4294967296L;
    setLink(entry, link(entry) & (predMask ^ 0xFFFFFFFFFFFFFFFFL) | (pred + 1) << 32L);
  }
  
  private void setSucceeds(int pred, int succ) {
    if (pred == -2) {
      this.firstEntry = succ;
    } else {
      setSuccessor(pred, succ);
    } 
    if (succ == -2) {
      this.lastEntry = pred;
    } else {
      setPredecessor(succ, pred);
    } 
  }
  
  void insertEntry(int entryIndex, @ParametricNullness K key, @ParametricNullness V value, int hash, int mask) {
    super.insertEntry(entryIndex, key, value, hash, mask);
    setSucceeds(this.lastEntry, entryIndex);
    setSucceeds(entryIndex, -2);
  }
  
  void accessEntry(int index) {
    if (this.accessOrder) {
      setSucceeds(getPredecessor(index), getSuccessor(index));
      setSucceeds(this.lastEntry, index);
      setSucceeds(index, -2);
      incrementModCount();
    } 
  }
  
  void moveLastEntry(int dstIndex, int mask) {
    int srcIndex = size() - 1;
    super.moveLastEntry(dstIndex, mask);
    setSucceeds(getPredecessor(dstIndex), getSuccessor(dstIndex));
    if (dstIndex < srcIndex) {
      setSucceeds(getPredecessor(srcIndex), dstIndex);
      setSucceeds(dstIndex, getSuccessor(srcIndex));
    } 
    setLink(srcIndex, 0L);
  }
  
  void resizeEntries(int newCapacity) {
    super.resizeEntries(newCapacity);
    this.links = Arrays.copyOf(requireLinks(), newCapacity);
  }
  
  int firstEntryIndex() {
    return this.firstEntry;
  }
  
  int adjustAfterRemove(int indexBeforeRemove, int indexRemoved) {
    return (indexBeforeRemove >= size()) ? indexRemoved : indexBeforeRemove;
  }
  
  Set<Map.Entry<K, V>> createEntrySet() {
    class EntrySetImpl extends CompactHashMap<K, V>.EntrySetView {
      EntrySetImpl(CompactLinkedHashMap this$0) {
        super(this$0);
      }
      
      public Spliterator<Map.Entry<K, V>> spliterator() {
        return Spliterators.spliterator(this, 17);
      }
    };
    return new EntrySetImpl(this);
  }
  
  Set<K> createKeySet() {
    class KeySetImpl extends CompactHashMap<K, V>.KeySetView {
      KeySetImpl(CompactLinkedHashMap this$0) {
        super(this$0);
      }
      
      public Object[] toArray() {
        return ObjectArrays.toArrayImpl(this);
      }
      
      public <T> T[] toArray(T[] a) {
        return ObjectArrays.toArrayImpl(this, a);
      }
      
      public Spliterator<K> spliterator() {
        return Spliterators.spliterator(this, 17);
      }
    };
    return new KeySetImpl(this);
  }
  
  Collection<V> createValues() {
    class ValuesImpl extends CompactHashMap<K, V>.ValuesView {
      ValuesImpl(CompactLinkedHashMap this$0) {
        super(this$0);
      }
      
      public Object[] toArray() {
        return ObjectArrays.toArrayImpl(this);
      }
      
      public <T> T[] toArray(T[] a) {
        return ObjectArrays.toArrayImpl(this, a);
      }
      
      public Spliterator<V> spliterator() {
        return Spliterators.spliterator(this, 16);
      }
    };
    return new ValuesImpl(this);
  }
  
  public void clear() {
    if (needsAllocArrays())
      return; 
    this.firstEntry = -2;
    this.lastEntry = -2;
    if (this.links != null)
      Arrays.fill(this.links, 0, size(), 0L); 
    super.clear();
  }
  
  private long[] requireLinks() {
    return Objects.<long[]>requireNonNull(this.links);
  }
  
  private long link(int i) {
    return requireLinks()[i];
  }
  
  private void setLink(int i, long value) {
    requireLinks()[i] = value;
  }
}
