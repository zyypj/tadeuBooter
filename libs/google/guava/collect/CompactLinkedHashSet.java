package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
class CompactLinkedHashSet<E> extends CompactHashSet<E> {
  private static final int ENDPOINT = -2;
  
  @CheckForNull
  private transient int[] predecessor;
  
  @CheckForNull
  private transient int[] successor;
  
  private transient int firstEntry;
  
  private transient int lastEntry;
  
  public static <E> CompactLinkedHashSet<E> create() {
    return new CompactLinkedHashSet<>();
  }
  
  public static <E> CompactLinkedHashSet<E> create(Collection<? extends E> collection) {
    CompactLinkedHashSet<E> set = createWithExpectedSize(collection.size());
    set.addAll(collection);
    return set;
  }
  
  @SafeVarargs
  public static <E> CompactLinkedHashSet<E> create(E... elements) {
    CompactLinkedHashSet<E> set = createWithExpectedSize(elements.length);
    Collections.addAll(set, elements);
    return set;
  }
  
  public static <E> CompactLinkedHashSet<E> createWithExpectedSize(int expectedSize) {
    return new CompactLinkedHashSet<>(expectedSize);
  }
  
  CompactLinkedHashSet() {}
  
  CompactLinkedHashSet(int expectedSize) {
    super(expectedSize);
  }
  
  void init(int expectedSize) {
    super.init(expectedSize);
    this.firstEntry = -2;
    this.lastEntry = -2;
  }
  
  int allocArrays() {
    int expectedSize = super.allocArrays();
    this.predecessor = new int[expectedSize];
    this.successor = new int[expectedSize];
    return expectedSize;
  }
  
  @CanIgnoreReturnValue
  Set<E> convertToHashFloodingResistantImplementation() {
    Set<E> result = super.convertToHashFloodingResistantImplementation();
    this.predecessor = null;
    this.successor = null;
    return result;
  }
  
  private int getPredecessor(int entry) {
    return requirePredecessors()[entry] - 1;
  }
  
  int getSuccessor(int entry) {
    return requireSuccessors()[entry] - 1;
  }
  
  private void setSuccessor(int entry, int succ) {
    requireSuccessors()[entry] = succ + 1;
  }
  
  private void setPredecessor(int entry, int pred) {
    requirePredecessors()[entry] = pred + 1;
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
  
  void insertEntry(int entryIndex, @ParametricNullness E object, int hash, int mask) {
    super.insertEntry(entryIndex, object, hash, mask);
    setSucceeds(this.lastEntry, entryIndex);
    setSucceeds(entryIndex, -2);
  }
  
  void moveLastEntry(int dstIndex, int mask) {
    int srcIndex = size() - 1;
    super.moveLastEntry(dstIndex, mask);
    setSucceeds(getPredecessor(dstIndex), getSuccessor(dstIndex));
    if (dstIndex < srcIndex) {
      setSucceeds(getPredecessor(srcIndex), dstIndex);
      setSucceeds(dstIndex, getSuccessor(srcIndex));
    } 
    requirePredecessors()[srcIndex] = 0;
    requireSuccessors()[srcIndex] = 0;
  }
  
  void resizeEntries(int newCapacity) {
    super.resizeEntries(newCapacity);
    this.predecessor = Arrays.copyOf(requirePredecessors(), newCapacity);
    this.successor = Arrays.copyOf(requireSuccessors(), newCapacity);
  }
  
  int firstEntryIndex() {
    return this.firstEntry;
  }
  
  int adjustAfterRemove(int indexBeforeRemove, int indexRemoved) {
    return (indexBeforeRemove >= size()) ? indexRemoved : indexBeforeRemove;
  }
  
  public Object[] toArray() {
    return ObjectArrays.toArrayImpl(this);
  }
  
  public <T> T[] toArray(T[] a) {
    return ObjectArrays.toArrayImpl(this, a);
  }
  
  public Spliterator<E> spliterator() {
    return Spliterators.spliterator(this, 17);
  }
  
  public void clear() {
    if (needsAllocArrays())
      return; 
    this.firstEntry = -2;
    this.lastEntry = -2;
    if (this.predecessor != null && this.successor != null) {
      Arrays.fill(this.predecessor, 0, size(), 0);
      Arrays.fill(this.successor, 0, size(), 0);
    } 
    super.clear();
  }
  
  private int[] requirePredecessors() {
    return Objects.<int[]>requireNonNull(this.predecessor);
  }
  
  private int[] requireSuccessors() {
    return Objects.<int[]>requireNonNull(this.successor);
  }
}
