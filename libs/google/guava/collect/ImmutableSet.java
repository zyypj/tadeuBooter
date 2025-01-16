package me.syncwrld.booter.libs.google.guava.collect;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collector;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.LazyInit;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.math.IntMath;
import me.syncwrld.booter.libs.google.j2objc.annotations.RetainedWith;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true, emulated = true)
public abstract class ImmutableSet<E> extends ImmutableCollection<E> implements Set<E> {
  static final int SPLITERATOR_CHARACTERISTICS = 1297;
  
  static final int MAX_TABLE_SIZE = 1073741824;
  
  private static final double DESIRED_LOAD_FACTOR = 0.7D;
  
  private static final int CUTOFF = 751619276;
  
  private static final long serialVersionUID = -889275714L;
  
  public static <E> Collector<E, ?, ImmutableSet<E>> toImmutableSet() {
    return CollectCollectors.toImmutableSet();
  }
  
  public static <E> ImmutableSet<E> of() {
    return RegularImmutableSet.EMPTY;
  }
  
  public static <E> ImmutableSet<E> of(E element) {
    return new SingletonImmutableSet<>(element);
  }
  
  public static <E> ImmutableSet<E> of(E e1, E e2) {
    return construct(2, 2, new Object[] { e1, e2 });
  }
  
  public static <E> ImmutableSet<E> of(E e1, E e2, E e3) {
    return construct(3, 3, new Object[] { e1, e2, e3 });
  }
  
  public static <E> ImmutableSet<E> of(E e1, E e2, E e3, E e4) {
    return construct(4, 4, new Object[] { e1, e2, e3, e4 });
  }
  
  public static <E> ImmutableSet<E> of(E e1, E e2, E e3, E e4, E e5) {
    return construct(5, 5, new Object[] { e1, e2, e3, e4, e5 });
  }
  
  @SafeVarargs
  public static <E> ImmutableSet<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E... others) {
    Preconditions.checkArgument((others.length <= 2147483641), "the total number of elements must fit in an int");
    int paramCount = 6;
    Object[] elements = new Object[6 + others.length];
    elements[0] = e1;
    elements[1] = e2;
    elements[2] = e3;
    elements[3] = e4;
    elements[4] = e5;
    elements[5] = e6;
    System.arraycopy(others, 0, elements, 6, others.length);
    return construct(elements.length, elements.length, elements);
  }
  
  private static <E> ImmutableSet<E> constructUnknownDuplication(int n, Object... elements) {
    return construct(n, 
        
        Math.max(4, 
          
          IntMath.sqrt(n, RoundingMode.CEILING)), elements);
  }
  
  private static <E> ImmutableSet<E> construct(int n, int expectedSize, Object... elements) {
    E elem;
    switch (n) {
      case 0:
        return of();
      case 1:
        elem = (E)elements[0];
        return of(elem);
    } 
    SetBuilderImpl<E> builder = new RegularSetBuilderImpl<>(expectedSize);
    for (int i = 0; i < n; i++) {
      E e = (E)Preconditions.checkNotNull(elements[i]);
      builder = builder.add(e);
    } 
    return builder.review().build();
  }
  
  public static <E> ImmutableSet<E> copyOf(Collection<? extends E> elements) {
    if (elements instanceof ImmutableSet && !(elements instanceof java.util.SortedSet)) {
      ImmutableSet<E> set = (ImmutableSet)elements;
      if (!set.isPartialView())
        return set; 
    } else if (elements instanceof EnumSet) {
      return copyOfEnumSet((EnumSet)elements);
    } 
    Object[] array = elements.toArray();
    if (elements instanceof Set)
      return construct(array.length, array.length, array); 
    return constructUnknownDuplication(array.length, array);
  }
  
  public static <E> ImmutableSet<E> copyOf(Iterable<? extends E> elements) {
    return (elements instanceof Collection) ? 
      copyOf((Collection<? extends E>)elements) : 
      copyOf(elements.iterator());
  }
  
  public static <E> ImmutableSet<E> copyOf(Iterator<? extends E> elements) {
    if (!elements.hasNext())
      return of(); 
    E first = elements.next();
    if (!elements.hasNext())
      return of(first); 
    return (new Builder<>()).add(first).addAll(elements).build();
  }
  
  public static <E> ImmutableSet<E> copyOf(E[] elements) {
    switch (elements.length) {
      case 0:
        return of();
      case 1:
        return of(elements[0]);
    } 
    return constructUnknownDuplication(elements.length, (Object[])elements.clone());
  }
  
  private static ImmutableSet copyOfEnumSet(EnumSet<Enum> enumSet) {
    return ImmutableEnumSet.asImmutable(EnumSet.copyOf(enumSet));
  }
  
  boolean isHashCodeFast() {
    return false;
  }
  
  public boolean equals(@CheckForNull Object object) {
    if (object == this)
      return true; 
    if (object instanceof ImmutableSet && 
      isHashCodeFast() && ((ImmutableSet)object)
      .isHashCodeFast() && 
      hashCode() != object.hashCode())
      return false; 
    return Sets.equalsImpl(this, object);
  }
  
  public int hashCode() {
    return Sets.hashCodeImpl(this);
  }
  
  @GwtCompatible
  static abstract class CachingAsList<E> extends ImmutableSet<E> {
    @LazyInit
    @CheckForNull
    @RetainedWith
    private transient ImmutableList<E> asList;
    
    public ImmutableList<E> asList() {
      ImmutableList<E> result = this.asList;
      if (result == null)
        return this.asList = createAsList(); 
      return result;
    }
    
    ImmutableList<E> createAsList() {
      return new RegularImmutableAsList<>(this, toArray());
    }
    
    @J2ktIncompatible
    @GwtIncompatible
    Object writeReplace() {
      return super.writeReplace();
    }
  }
  
  static abstract class Indexed<E> extends CachingAsList<E> {
    public UnmodifiableIterator<E> iterator() {
      return asList().iterator();
    }
    
    public Spliterator<E> spliterator() {
      return CollectSpliterators.indexed(size(), 1297, this::get);
    }
    
    public void forEach(Consumer<? super E> consumer) {
      Preconditions.checkNotNull(consumer);
      int n = size();
      for (int i = 0; i < n; i++)
        consumer.accept(get(i)); 
    }
    
    int copyIntoArray(Object[] dst, int offset) {
      return asList().copyIntoArray(dst, offset);
    }
    
    ImmutableList<E> createAsList() {
      return new ImmutableAsList<E>() {
          public E get(int index) {
            return ImmutableSet.Indexed.this.get(index);
          }
          
          ImmutableSet.Indexed<E> delegateCollection() {
            return ImmutableSet.Indexed.this;
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
    
    abstract E get(int param1Int);
  }
  
  @J2ktIncompatible
  private static class SerializedForm implements Serializable {
    final Object[] elements;
    
    private static final long serialVersionUID = 0L;
    
    SerializedForm(Object[] elements) {
      this.elements = elements;
    }
    
    Object readResolve() {
      return ImmutableSet.copyOf(this.elements);
    }
  }
  
  @J2ktIncompatible
  Object writeReplace() {
    return new SerializedForm(toArray());
  }
  
  @J2ktIncompatible
  private void readObject(ObjectInputStream stream) throws InvalidObjectException {
    throw new InvalidObjectException("Use SerializedForm");
  }
  
  public static <E> Builder<E> builder() {
    return new Builder<>();
  }
  
  public static <E> Builder<E> builderWithExpectedSize(int expectedSize) {
    CollectPreconditions.checkNonnegative(expectedSize, "expectedSize");
    return new Builder<>(expectedSize);
  }
  
  public static class Builder<E> extends ImmutableCollection.Builder<E> {
    @CheckForNull
    private ImmutableSet.SetBuilderImpl<E> impl;
    
    boolean forceCopy;
    
    public Builder() {
      this(0);
    }
    
    Builder(int capacity) {
      if (capacity > 0) {
        this.impl = new ImmutableSet.RegularSetBuilderImpl<>(capacity);
      } else {
        this.impl = ImmutableSet.EmptySetBuilderImpl.instance();
      } 
    }
    
    Builder(boolean subclass) {
      this.impl = null;
    }
    
    @VisibleForTesting
    void forceJdk() {
      Objects.requireNonNull(this.impl);
      this.impl = new ImmutableSet.JdkBackedSetBuilderImpl<>(this.impl);
    }
    
    final void copyIfNecessary() {
      if (this.forceCopy) {
        copy();
        this.forceCopy = false;
      } 
    }
    
    void copy() {
      Objects.requireNonNull(this.impl);
      this.impl = this.impl.copy();
    }
    
    @CanIgnoreReturnValue
    public Builder<E> add(E element) {
      Objects.requireNonNull(this.impl);
      Preconditions.checkNotNull(element);
      copyIfNecessary();
      this.impl = this.impl.add(element);
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<E> add(E... elements) {
      super.add(elements);
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<E> addAll(Iterable<? extends E> elements) {
      super.addAll(elements);
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<E> addAll(Iterator<? extends E> elements) {
      super.addAll(elements);
      return this;
    }
    
    @CanIgnoreReturnValue
    Builder<E> combine(Builder<E> other) {
      Objects.requireNonNull(this.impl);
      Objects.requireNonNull(other.impl);
      copyIfNecessary();
      this.impl = this.impl.combine(other.impl);
      return this;
    }
    
    public ImmutableSet<E> build() {
      Objects.requireNonNull(this.impl);
      this.forceCopy = true;
      this.impl = this.impl.review();
      return this.impl.build();
    }
  }
  
  private static abstract class SetBuilderImpl<E> {
    E[] dedupedElements;
    
    int distinct;
    
    SetBuilderImpl(int expectedCapacity) {
      this.dedupedElements = (E[])new Object[expectedCapacity];
      this.distinct = 0;
    }
    
    SetBuilderImpl(SetBuilderImpl<E> toCopy) {
      this.dedupedElements = Arrays.copyOf(toCopy.dedupedElements, toCopy.dedupedElements.length);
      this.distinct = toCopy.distinct;
    }
    
    private void ensureCapacity(int minCapacity) {
      if (minCapacity > this.dedupedElements.length) {
        int newCapacity = ImmutableCollection.Builder.expandedCapacity(this.dedupedElements.length, minCapacity);
        this.dedupedElements = Arrays.copyOf(this.dedupedElements, newCapacity);
      } 
    }
    
    final void addDedupedElement(E e) {
      ensureCapacity(this.distinct + 1);
      this.dedupedElements[this.distinct++] = e;
    }
    
    abstract SetBuilderImpl<E> add(E param1E);
    
    final SetBuilderImpl<E> combine(SetBuilderImpl<E> other) {
      SetBuilderImpl<E> result = this;
      for (int i = 0; i < other.distinct; i++)
        result = result.add(Objects.requireNonNull(other.dedupedElements[i])); 
      return result;
    }
    
    abstract SetBuilderImpl<E> copy();
    
    SetBuilderImpl<E> review() {
      return this;
    }
    
    abstract ImmutableSet<E> build();
  }
  
  private static final class EmptySetBuilderImpl<E> extends SetBuilderImpl<E> {
    private static final EmptySetBuilderImpl<Object> INSTANCE = new EmptySetBuilderImpl();
    
    static <E> ImmutableSet.SetBuilderImpl<E> instance() {
      return INSTANCE;
    }
    
    private EmptySetBuilderImpl() {
      super(0);
    }
    
    ImmutableSet.SetBuilderImpl<E> add(E e) {
      return (new ImmutableSet.RegularSetBuilderImpl<>(4)).add(e);
    }
    
    ImmutableSet.SetBuilderImpl<E> copy() {
      return this;
    }
    
    ImmutableSet<E> build() {
      return ImmutableSet.of();
    }
  }
  
  static int chooseTableSize(int setSize) {
    setSize = Math.max(setSize, 2);
    if (setSize < 751619276) {
      int tableSize = Integer.highestOneBit(setSize - 1) << 1;
      while (tableSize * 0.7D < setSize)
        tableSize <<= 1; 
      return tableSize;
    } 
    Preconditions.checkArgument((setSize < 1073741824), "collection too large");
    return 1073741824;
  }
  
  public abstract UnmodifiableIterator<E> iterator();
  
  private static final class RegularSetBuilderImpl<E> extends SetBuilderImpl<E> {
    @CheckForNull
    private Object[] hashTable;
    
    private int maxRunBeforeFallback;
    
    private int expandTableThreshold;
    
    private int hashCode;
    
    static final int MAX_RUN_MULTIPLIER = 13;
    
    RegularSetBuilderImpl(int expectedCapacity) {
      super(expectedCapacity);
      this.hashTable = null;
      this.maxRunBeforeFallback = 0;
      this.expandTableThreshold = 0;
    }
    
    RegularSetBuilderImpl(RegularSetBuilderImpl<E> toCopy) {
      super(toCopy);
      this.hashTable = (toCopy.hashTable == null) ? null : (Object[])toCopy.hashTable.clone();
      this.maxRunBeforeFallback = toCopy.maxRunBeforeFallback;
      this.expandTableThreshold = toCopy.expandTableThreshold;
      this.hashCode = toCopy.hashCode;
    }
    
    ImmutableSet.SetBuilderImpl<E> add(E e) {
      Preconditions.checkNotNull(e);
      if (this.hashTable == null) {
        if (this.distinct == 0) {
          addDedupedElement(e);
          return this;
        } 
        ensureTableCapacity(this.dedupedElements.length);
        E elem = this.dedupedElements[0];
        this.distinct--;
        return insertInHashTable(elem).add(e);
      } 
      return insertInHashTable(e);
    }
    
    private ImmutableSet.SetBuilderImpl<E> insertInHashTable(E e) {
      Objects.requireNonNull(this.hashTable);
      int eHash = e.hashCode();
      int i0 = Hashing.smear(eHash);
      int mask = this.hashTable.length - 1;
      for (int i = i0; i - i0 < this.maxRunBeforeFallback; i++) {
        int index = i & mask;
        Object tableEntry = this.hashTable[index];
        if (tableEntry == null) {
          addDedupedElement(e);
          this.hashTable[index] = e;
          this.hashCode += eHash;
          ensureTableCapacity(this.distinct);
          return this;
        } 
        if (tableEntry.equals(e))
          return this; 
      } 
      return (new ImmutableSet.JdkBackedSetBuilderImpl<>(this)).add(e);
    }
    
    ImmutableSet.SetBuilderImpl<E> copy() {
      return new RegularSetBuilderImpl(this);
    }
    
    ImmutableSet.SetBuilderImpl<E> review() {
      if (this.hashTable == null)
        return this; 
      int targetTableSize = ImmutableSet.chooseTableSize(this.distinct);
      if (targetTableSize * 2 < this.hashTable.length) {
        this.hashTable = rebuildHashTable(targetTableSize, (Object[])this.dedupedElements, this.distinct);
        this.maxRunBeforeFallback = maxRunBeforeFallback(targetTableSize);
        this.expandTableThreshold = (int)(0.7D * targetTableSize);
      } 
      return hashFloodingDetected(this.hashTable) ? new ImmutableSet.JdkBackedSetBuilderImpl<>(this) : this;
    }
    
    ImmutableSet<E> build() {
      switch (this.distinct) {
        case 0:
          return ImmutableSet.of();
        case 1:
          return ImmutableSet.of(Objects.requireNonNull(this.dedupedElements[0]));
      } 
      Object[] elements = (this.distinct == this.dedupedElements.length) ? (Object[])this.dedupedElements : Arrays.<Object>copyOf((Object[])this.dedupedElements, this.distinct);
      return new RegularImmutableSet<>(elements, this.hashCode, 
          Objects.<Object[]>requireNonNull(this.hashTable), this.hashTable.length - 1);
    }
    
    static Object[] rebuildHashTable(int newTableSize, Object[] elements, int n) {
      Object[] hashTable = new Object[newTableSize];
      int mask = hashTable.length - 1;
      for (int i = 0; i < n; ) {
        Object e = Objects.requireNonNull(elements[i]);
        int j0 = Hashing.smear(e.hashCode());
        int j = j0;
        for (;; i++) {
          int index = j & mask;
          if (hashTable[index] == null) {
            hashTable[index] = e;
          } else {
            j++;
            continue;
          } 
        } 
      } 
      return hashTable;
    }
    
    void ensureTableCapacity(int minCapacity) {
      int newTableSize;
      if (this.hashTable == null) {
        newTableSize = ImmutableSet.chooseTableSize(minCapacity);
        this.hashTable = new Object[newTableSize];
      } else if (minCapacity > this.expandTableThreshold && this.hashTable.length < 1073741824) {
        newTableSize = this.hashTable.length * 2;
        this.hashTable = rebuildHashTable(newTableSize, (Object[])this.dedupedElements, this.distinct);
      } else {
        return;
      } 
      this.maxRunBeforeFallback = maxRunBeforeFallback(newTableSize);
      this.expandTableThreshold = (int)(0.7D * newTableSize);
    }
    
    static boolean hashFloodingDetected(Object[] hashTable) {
      int maxRunBeforeFallback = maxRunBeforeFallback(hashTable.length);
      int mask = hashTable.length - 1;
      int knownRunStart = 0;
      int knownRunEnd = 0;
      label22: while (knownRunStart < hashTable.length) {
        if (knownRunStart == knownRunEnd && hashTable[knownRunStart] == null) {
          if (hashTable[knownRunStart + maxRunBeforeFallback - 1 & mask] == null) {
            knownRunStart += maxRunBeforeFallback;
          } else {
            knownRunStart++;
          } 
          knownRunEnd = knownRunStart;
          continue;
        } 
        for (int j = knownRunStart + maxRunBeforeFallback - 1; j >= knownRunEnd; j--) {
          if (hashTable[j & mask] == null) {
            knownRunEnd = knownRunStart + maxRunBeforeFallback;
            knownRunStart = j + 1;
            continue label22;
          } 
        } 
        return true;
      } 
      return false;
    }
    
    static int maxRunBeforeFallback(int tableSize) {
      return 13 * IntMath.log2(tableSize, RoundingMode.UNNECESSARY);
    }
  }
  
  private static final class JdkBackedSetBuilderImpl<E> extends SetBuilderImpl<E> {
    private final Set<Object> delegate;
    
    JdkBackedSetBuilderImpl(ImmutableSet.SetBuilderImpl<E> toCopy) {
      super(toCopy);
      this.delegate = Sets.newHashSetWithExpectedSize(this.distinct);
      for (int i = 0; i < this.distinct; i++)
        this.delegate.add(Objects.requireNonNull(this.dedupedElements[i])); 
    }
    
    ImmutableSet.SetBuilderImpl<E> add(E e) {
      Preconditions.checkNotNull(e);
      if (this.delegate.add(e))
        addDedupedElement(e); 
      return this;
    }
    
    ImmutableSet.SetBuilderImpl<E> copy() {
      return new JdkBackedSetBuilderImpl(this);
    }
    
    ImmutableSet<E> build() {
      switch (this.distinct) {
        case 0:
          return ImmutableSet.of();
        case 1:
          return ImmutableSet.of(Objects.requireNonNull(this.dedupedElements[0]));
      } 
      return new JdkBackedImmutableSet<>(this.delegate, 
          ImmutableList.asImmutableList((Object[])this.dedupedElements, this.distinct));
    }
  }
}
