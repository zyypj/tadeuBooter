package me.syncwrld.booter.libs.google.guava.collect;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.Objects;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.primitives.Ints;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
class CompactHashSet<E> extends AbstractSet<E> implements Serializable {
  @VisibleForTesting
  static final double HASH_FLOODING_FPP = 0.001D;
  
  private static final int MAX_HASH_BUCKET_LENGTH = 9;
  
  @CheckForNull
  private transient Object table;
  
  @CheckForNull
  private transient int[] entries;
  
  @CheckForNull
  @VisibleForTesting
  transient Object[] elements;
  
  private transient int metadata;
  
  private transient int size;
  
  public static <E> CompactHashSet<E> create() {
    return new CompactHashSet<>();
  }
  
  public static <E> CompactHashSet<E> create(Collection<? extends E> collection) {
    CompactHashSet<E> set = createWithExpectedSize(collection.size());
    set.addAll(collection);
    return set;
  }
  
  @SafeVarargs
  public static <E> CompactHashSet<E> create(E... elements) {
    CompactHashSet<E> set = createWithExpectedSize(elements.length);
    Collections.addAll(set, elements);
    return set;
  }
  
  public static <E> CompactHashSet<E> createWithExpectedSize(int expectedSize) {
    return new CompactHashSet<>(expectedSize);
  }
  
  CompactHashSet() {
    init(3);
  }
  
  CompactHashSet(int expectedSize) {
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
    this.elements = new Object[expectedSize];
    return expectedSize;
  }
  
  @CheckForNull
  @VisibleForTesting
  Set<E> delegateOrNull() {
    if (this.table instanceof Set)
      return (Set<E>)this.table; 
    return null;
  }
  
  private Set<E> createHashFloodingResistantDelegate(int tableSize) {
    return new LinkedHashSet<>(tableSize, 1.0F);
  }
  
  @VisibleForTesting
  @CanIgnoreReturnValue
  Set<E> convertToHashFloodingResistantImplementation() {
    Set<E> newDelegate = createHashFloodingResistantDelegate(hashTableMask() + 1);
    for (int i = firstEntryIndex(); i >= 0; i = getSuccessor(i))
      newDelegate.add(element(i)); 
    this.table = newDelegate;
    this.entries = null;
    this.elements = null;
    incrementModCount();
    return newDelegate;
  }
  
  @VisibleForTesting
  boolean isUsingHashFloodingResistance() {
    return (delegateOrNull() != null);
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
  
  @CanIgnoreReturnValue
  public boolean add(@ParametricNullness E object) {
    if (needsAllocArrays())
      allocArrays(); 
    Set<E> delegate = delegateOrNull();
    if (delegate != null)
      return delegate.add(object); 
    int[] entries = requireEntries();
    Object[] elements = requireElements();
    int newEntryIndex = this.size;
    int newSize = newEntryIndex + 1;
    int hash = Hashing.smearedHash(object);
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
          Objects.equal(object, elements[entryIndex]))
          return false; 
        next = CompactHashing.getNext(entry, mask);
        bucketLength++;
      } while (next != 0);
      if (bucketLength >= 9)
        return convertToHashFloodingResistantImplementation().add(object); 
      if (newSize > mask) {
        mask = resizeTable(mask, CompactHashing.newCapacity(mask), hash, newEntryIndex);
      } else {
        entries[entryIndex] = CompactHashing.maskCombine(entry, newEntryIndex + 1, mask);
      } 
    } 
    resizeMeMaybe(newSize);
    insertEntry(newEntryIndex, object, hash, mask);
    this.size = newSize;
    incrementModCount();
    return true;
  }
  
  void insertEntry(int entryIndex, @ParametricNullness E object, int hash, int mask) {
    setEntry(entryIndex, CompactHashing.maskCombine(hash, 0, mask));
    setElement(entryIndex, object);
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
    this.elements = Arrays.copyOf(requireElements(), newCapacity);
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
  
  public boolean contains(@CheckForNull Object object) {
    if (needsAllocArrays())
      return false; 
    Set<E> delegate = delegateOrNull();
    if (delegate != null)
      return delegate.contains(object); 
    int hash = Hashing.smearedHash(object);
    int mask = hashTableMask();
    int next = CompactHashing.tableGet(requireTable(), hash & mask);
    if (next == 0)
      return false; 
    int hashPrefix = CompactHashing.getHashPrefix(hash, mask);
    while (true) {
      int entryIndex = next - 1;
      int entry = entry(entryIndex);
      if (CompactHashing.getHashPrefix(entry, mask) == hashPrefix && 
        Objects.equal(object, element(entryIndex)))
        return true; 
      next = CompactHashing.getNext(entry, mask);
      if (next == 0)
        return false; 
    } 
  }
  
  @CanIgnoreReturnValue
  public boolean remove(@CheckForNull Object object) {
    if (needsAllocArrays())
      return false; 
    Set<E> delegate = delegateOrNull();
    if (delegate != null)
      return delegate.remove(object); 
    int mask = hashTableMask();
    int index = CompactHashing.remove(object, null, mask, 
        
        requireTable(), 
        requireEntries(), 
        requireElements(), null);
    if (index == -1)
      return false; 
    moveLastEntry(index, mask);
    this.size--;
    incrementModCount();
    return true;
  }
  
  void moveLastEntry(int dstIndex, int mask) {
    Object table = requireTable();
    int[] entries = requireEntries();
    Object[] elements = requireElements();
    int srcIndex = size() - 1;
    if (dstIndex < srcIndex) {
      Object object = elements[srcIndex];
      elements[dstIndex] = object;
      elements[srcIndex] = null;
      entries[dstIndex] = entries[srcIndex];
      entries[srcIndex] = 0;
      int tableIndex = Hashing.smearedHash(object) & mask;
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
      elements[dstIndex] = null;
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
  
  public Iterator<E> iterator() {
    Set<E> delegate = delegateOrNull();
    if (delegate != null)
      return delegate.iterator(); 
    return new Iterator<E>() {
        int expectedMetadata = CompactHashSet.this.metadata;
        
        int currentIndex = CompactHashSet.this.firstEntryIndex();
        
        int indexToRemove = -1;
        
        public boolean hasNext() {
          return (this.currentIndex >= 0);
        }
        
        @ParametricNullness
        public E next() {
          checkForConcurrentModification();
          if (!hasNext())
            throw new NoSuchElementException(); 
          this.indexToRemove = this.currentIndex;
          E result = CompactHashSet.this.element(this.currentIndex);
          this.currentIndex = CompactHashSet.this.getSuccessor(this.currentIndex);
          return result;
        }
        
        public void remove() {
          checkForConcurrentModification();
          CollectPreconditions.checkRemove((this.indexToRemove >= 0));
          incrementExpectedModCount();
          CompactHashSet.this.remove(CompactHashSet.this.element(this.indexToRemove));
          this.currentIndex = CompactHashSet.this.adjustAfterRemove(this.currentIndex, this.indexToRemove);
          this.indexToRemove = -1;
        }
        
        void incrementExpectedModCount() {
          this.expectedMetadata += 32;
        }
        
        private void checkForConcurrentModification() {
          if (CompactHashSet.this.metadata != this.expectedMetadata)
            throw new ConcurrentModificationException(); 
        }
      };
  }
  
  public Spliterator<E> spliterator() {
    if (needsAllocArrays())
      return Spliterators.spliterator(new Object[0], 17); 
    Set<E> delegate = delegateOrNull();
    return (delegate != null) ? 
      delegate.spliterator() : 
      Spliterators.<E>spliterator(
        requireElements(), 0, this.size, 17);
  }
  
  public void forEach(Consumer<? super E> action) {
    Preconditions.checkNotNull(action);
    Set<E> delegate = delegateOrNull();
    if (delegate != null) {
      delegate.forEach(action);
    } else {
      for (int i = firstEntryIndex(); i >= 0; i = getSuccessor(i))
        action.accept(element(i)); 
    } 
  }
  
  public int size() {
    Set<E> delegate = delegateOrNull();
    return (delegate != null) ? delegate.size() : this.size;
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public Object[] toArray() {
    if (needsAllocArrays())
      return new Object[0]; 
    Set<E> delegate = delegateOrNull();
    return (delegate != null) ? delegate.toArray() : Arrays.<Object>copyOf(requireElements(), this.size);
  }
  
  @CanIgnoreReturnValue
  public <T> T[] toArray(T[] a) {
    if (needsAllocArrays()) {
      if (a.length > 0)
        a[0] = null; 
      return a;
    } 
    Set<E> delegate = delegateOrNull();
    return (delegate != null) ? 
      delegate.<T>toArray(a) : 
      ObjectArrays.<T>toArrayImpl(requireElements(), 0, this.size, a);
  }
  
  public void trimToSize() {
    if (needsAllocArrays())
      return; 
    Set<E> delegate = delegateOrNull();
    if (delegate != null) {
      Set<E> newDelegate = createHashFloodingResistantDelegate(size());
      newDelegate.addAll(delegate);
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
    Set<E> delegate = delegateOrNull();
    if (delegate != null) {
      this
        .metadata = Ints.constrainToRange(size(), 3, 1073741823);
      delegate.clear();
      this.table = null;
      this.size = 0;
    } else {
      Arrays.fill(requireElements(), 0, this.size, (Object)null);
      CompactHashing.tableClear(requireTable());
      Arrays.fill(requireEntries(), 0, this.size, 0);
      this.size = 0;
    } 
  }
  
  @J2ktIncompatible
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    stream.writeInt(size());
    for (E e : this)
      stream.writeObject(e); 
  }
  
  @J2ktIncompatible
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    int elementCount = stream.readInt();
    if (elementCount < 0)
      throw new InvalidObjectException("Invalid size: " + elementCount); 
    init(elementCount);
    for (int i = 0; i < elementCount; i++) {
      E element = (E)stream.readObject();
      add(element);
    } 
  }
  
  private Object requireTable() {
    return Objects.requireNonNull(this.table);
  }
  
  private int[] requireEntries() {
    return Objects.<int[]>requireNonNull(this.entries);
  }
  
  private Object[] requireElements() {
    return Objects.<Object[]>requireNonNull(this.elements);
  }
  
  private E element(int i) {
    return (E)requireElements()[i];
  }
  
  private int entry(int i) {
    return requireEntries()[i];
  }
  
  private void setElement(int i, E value) {
    requireElements()[i] = value;
  }
  
  private void setEntry(int i, int value) {
    requireEntries()[i] = value;
  }
}
