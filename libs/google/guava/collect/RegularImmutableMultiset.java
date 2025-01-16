package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.LazyInit;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.Objects;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.primitives.Ints;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true, serializable = true)
class RegularImmutableMultiset<E> extends ImmutableMultiset<E> {
  private static final Multisets.ImmutableEntry<?>[] EMPTY_ARRAY = (Multisets.ImmutableEntry<?>[])new Multisets.ImmutableEntry[0];
  
  static final ImmutableMultiset<Object> EMPTY = create(ImmutableList.of());
  
  @VisibleForTesting
  static final double MAX_LOAD_FACTOR = 1.0D;
  
  @VisibleForTesting
  static final double HASH_FLOODING_FPP = 0.001D;
  
  @VisibleForTesting
  static final int MAX_HASH_BUCKET_LENGTH = 9;
  
  private final transient Multisets.ImmutableEntry<E>[] entries;
  
  private final transient Multisets.ImmutableEntry<?>[] hashTable;
  
  private final transient int size;
  
  private final transient int hashCode;
  
  @LazyInit
  @CheckForNull
  private transient ImmutableSet<E> elementSet;
  
  static <E> ImmutableMultiset<E> create(Collection<? extends Multiset.Entry<? extends E>> entries) {
    int distinct = entries.size();
    Multisets.ImmutableEntry[] arrayOfImmutableEntry1 = new Multisets.ImmutableEntry[distinct];
    if (distinct == 0)
      return new RegularImmutableMultiset<>((Multisets.ImmutableEntry<E>[])arrayOfImmutableEntry1, EMPTY_ARRAY, 0, 0, ImmutableSet.of()); 
    int tableSize = Hashing.closedTableSize(distinct, 1.0D);
    int mask = tableSize - 1;
    Multisets.ImmutableEntry[] arrayOfImmutableEntry2 = new Multisets.ImmutableEntry[tableSize];
    int index = 0;
    int hashCode = 0;
    long size = 0L;
    for (Multiset.Entry<? extends E> entryWithWildcard : entries) {
      Multisets.ImmutableEntry<E> newEntry;
      Multiset.Entry<? extends E> entry1 = entryWithWildcard;
      E element = (E)Preconditions.checkNotNull(entry1.getElement());
      int count = entry1.getCount();
      int hash = element.hashCode();
      int bucket = Hashing.smear(hash) & mask;
      Multisets.ImmutableEntry<E> bucketHead = arrayOfImmutableEntry2[bucket];
      if (bucketHead == null) {
        boolean canReuseEntry = (entry1 instanceof Multisets.ImmutableEntry && !(entry1 instanceof NonTerminalEntry));
        newEntry = canReuseEntry ? (Multisets.ImmutableEntry)entry1 : new Multisets.ImmutableEntry<>(element, count);
      } else {
        newEntry = new NonTerminalEntry<>(element, count, bucketHead);
      } 
      hashCode += hash ^ count;
      arrayOfImmutableEntry1[index++] = newEntry;
      arrayOfImmutableEntry2[bucket] = newEntry;
      size += count;
    } 
    return hashFloodingDetected((Multisets.ImmutableEntry<?>[])arrayOfImmutableEntry2) ? 
      JdkBackedImmutableMultiset.<E>create(ImmutableList.asImmutableList((Object[])arrayOfImmutableEntry1)) : 
      new RegularImmutableMultiset<>((Multisets.ImmutableEntry<E>[])arrayOfImmutableEntry1, (Multisets.ImmutableEntry<?>[])arrayOfImmutableEntry2, 
        Ints.saturatedCast(size), hashCode, null);
  }
  
  private static boolean hashFloodingDetected(Multisets.ImmutableEntry<?>[] hashTable) {
    for (int i = 0; i < hashTable.length; i++) {
      int bucketLength = 0;
      for (Multisets.ImmutableEntry<?> entry = hashTable[i]; entry != null; entry = entry.nextInBucket()) {
        bucketLength++;
        if (bucketLength > 9)
          return true; 
      } 
    } 
    return false;
  }
  
  private RegularImmutableMultiset(Multisets.ImmutableEntry<E>[] entries, Multisets.ImmutableEntry<?>[] hashTable, int size, int hashCode, @CheckForNull ImmutableSet<E> elementSet) {
    this.entries = entries;
    this.hashTable = hashTable;
    this.size = size;
    this.hashCode = hashCode;
    this.elementSet = elementSet;
  }
  
  private static final class NonTerminalEntry<E> extends Multisets.ImmutableEntry<E> {
    private final Multisets.ImmutableEntry<E> nextInBucket;
    
    NonTerminalEntry(E element, int count, Multisets.ImmutableEntry<E> nextInBucket) {
      super(element, count);
      this.nextInBucket = nextInBucket;
    }
    
    public Multisets.ImmutableEntry<E> nextInBucket() {
      return this.nextInBucket;
    }
  }
  
  boolean isPartialView() {
    return false;
  }
  
  public int count(@CheckForNull Object element) {
    Multisets.ImmutableEntry<?>[] hashTable = this.hashTable;
    if (element == null || hashTable.length == 0)
      return 0; 
    int hash = Hashing.smearedHash(element);
    int mask = hashTable.length - 1;
    Multisets.ImmutableEntry<?> entry = hashTable[hash & mask];
    for (; entry != null; 
      entry = entry.nextInBucket()) {
      if (Objects.equal(element, entry.getElement()))
        return entry.getCount(); 
    } 
    return 0;
  }
  
  public int size() {
    return this.size;
  }
  
  public ImmutableSet<E> elementSet() {
    ImmutableSet<E> result = this.elementSet;
    return (result == null) ? (this.elementSet = new ImmutableMultiset.ElementSet<>(Arrays.asList((Multiset.Entry[])this.entries), this)) : result;
  }
  
  Multiset.Entry<E> getEntry(int index) {
    return this.entries[index];
  }
  
  public int hashCode() {
    return this.hashCode;
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  Object writeReplace() {
    return super.writeReplace();
  }
}
