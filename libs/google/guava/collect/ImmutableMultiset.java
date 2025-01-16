package me.syncwrld.booter.libs.google.guava.collect;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotCall;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.LazyInit;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true, emulated = true)
public abstract class ImmutableMultiset<E> extends ImmutableMultisetGwtSerializationDependencies<E> implements Multiset<E> {
  @LazyInit
  @CheckForNull
  private transient ImmutableList<E> asList;
  
  @LazyInit
  @CheckForNull
  private transient ImmutableSet<Multiset.Entry<E>> entrySet;
  
  private static final long serialVersionUID = -889275714L;
  
  public static <E> Collector<E, ?, ImmutableMultiset<E>> toImmutableMultiset() {
    return CollectCollectors.toImmutableMultiset((Function)Function.identity(), e -> 1);
  }
  
  public static <T, E> Collector<T, ?, ImmutableMultiset<E>> toImmutableMultiset(Function<? super T, ? extends E> elementFunction, ToIntFunction<? super T> countFunction) {
    return CollectCollectors.toImmutableMultiset(elementFunction, countFunction);
  }
  
  public static <E> ImmutableMultiset<E> of() {
    return (ImmutableMultiset)RegularImmutableMultiset.EMPTY;
  }
  
  public static <E> ImmutableMultiset<E> of(E element) {
    return copyFromElements((E[])new Object[] { element });
  }
  
  public static <E> ImmutableMultiset<E> of(E e1, E e2) {
    return copyFromElements((E[])new Object[] { e1, e2 });
  }
  
  public static <E> ImmutableMultiset<E> of(E e1, E e2, E e3) {
    return copyFromElements((E[])new Object[] { e1, e2, e3 });
  }
  
  public static <E> ImmutableMultiset<E> of(E e1, E e2, E e3, E e4) {
    return copyFromElements((E[])new Object[] { e1, e2, e3, e4 });
  }
  
  public static <E> ImmutableMultiset<E> of(E e1, E e2, E e3, E e4, E e5) {
    return copyFromElements((E[])new Object[] { e1, e2, e3, e4, e5 });
  }
  
  public static <E> ImmutableMultiset<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E... others) {
    return (new Builder<>()).add(e1).add(e2).add(e3).add(e4).add(e5).add(e6).add(others).build();
  }
  
  public static <E> ImmutableMultiset<E> copyOf(E[] elements) {
    return copyFromElements(elements);
  }
  
  public static <E> ImmutableMultiset<E> copyOf(Iterable<? extends E> elements) {
    if (elements instanceof ImmutableMultiset) {
      ImmutableMultiset<E> result = (ImmutableMultiset)elements;
      if (!result.isPartialView())
        return result; 
    } 
    Multiset<? extends E> multiset = (elements instanceof Multiset) ? Multisets.<E>cast(elements) : LinkedHashMultiset.<E>create(elements);
    return copyFromEntries(multiset.entrySet());
  }
  
  public static <E> ImmutableMultiset<E> copyOf(Iterator<? extends E> elements) {
    Multiset<E> multiset = LinkedHashMultiset.create();
    Iterators.addAll(multiset, elements);
    return copyFromEntries(multiset.entrySet());
  }
  
  private static <E> ImmutableMultiset<E> copyFromElements(E... elements) {
    Multiset<E> multiset = LinkedHashMultiset.create();
    Collections.addAll(multiset, elements);
    return copyFromEntries(multiset.entrySet());
  }
  
  static <E> ImmutableMultiset<E> copyFromEntries(Collection<? extends Multiset.Entry<? extends E>> entries) {
    if (entries.isEmpty())
      return of(); 
    return RegularImmutableMultiset.create(entries);
  }
  
  public UnmodifiableIterator<E> iterator() {
    final Iterator<Multiset.Entry<E>> entryIterator = entrySet().iterator();
    return new UnmodifiableIterator<E>(this) {
        int remaining;
        
        @CheckForNull
        E element;
        
        public boolean hasNext() {
          return (this.remaining > 0 || entryIterator.hasNext());
        }
        
        public E next() {
          if (this.remaining <= 0) {
            Multiset.Entry<E> entry = entryIterator.next();
            this.element = entry.getElement();
            this.remaining = entry.getCount();
          } 
          this.remaining--;
          return Objects.requireNonNull(this.element);
        }
      };
  }
  
  public ImmutableList<E> asList() {
    ImmutableList<E> result = this.asList;
    return (result == null) ? (this.asList = super.asList()) : result;
  }
  
  public boolean contains(@CheckForNull Object object) {
    return (count(object) > 0);
  }
  
  @Deprecated
  @CanIgnoreReturnValue
  @DoNotCall("Always throws UnsupportedOperationException")
  public final int add(E element, int occurrences) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @CanIgnoreReturnValue
  @DoNotCall("Always throws UnsupportedOperationException")
  public final int remove(@CheckForNull Object element, int occurrences) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @CanIgnoreReturnValue
  @DoNotCall("Always throws UnsupportedOperationException")
  public final int setCount(E element, int count) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @CanIgnoreReturnValue
  @DoNotCall("Always throws UnsupportedOperationException")
  public final boolean setCount(E element, int oldCount, int newCount) {
    throw new UnsupportedOperationException();
  }
  
  @GwtIncompatible
  int copyIntoArray(Object[] dst, int offset) {
    for (UnmodifiableIterator<Multiset.Entry<E>> unmodifiableIterator = entrySet().iterator(); unmodifiableIterator.hasNext(); ) {
      Multiset.Entry<E> entry = unmodifiableIterator.next();
      Arrays.fill(dst, offset, offset + entry.getCount(), entry.getElement());
      offset += entry.getCount();
    } 
    return offset;
  }
  
  public boolean equals(@CheckForNull Object object) {
    return Multisets.equalsImpl(this, object);
  }
  
  public int hashCode() {
    return Sets.hashCodeImpl(entrySet());
  }
  
  public String toString() {
    return entrySet().toString();
  }
  
  public ImmutableSet<Multiset.Entry<E>> entrySet() {
    ImmutableSet<Multiset.Entry<E>> es = this.entrySet;
    return (es == null) ? (this.entrySet = createEntrySet()) : es;
  }
  
  private ImmutableSet<Multiset.Entry<E>> createEntrySet() {
    return isEmpty() ? ImmutableSet.<Multiset.Entry<E>>of() : new EntrySet();
  }
  
  private final class EntrySet extends IndexedImmutableSet<Multiset.Entry<E>> {
    @J2ktIncompatible
    private static final long serialVersionUID = 0L;
    
    private EntrySet() {}
    
    boolean isPartialView() {
      return ImmutableMultiset.this.isPartialView();
    }
    
    Multiset.Entry<E> get(int index) {
      return ImmutableMultiset.this.getEntry(index);
    }
    
    public int size() {
      return ImmutableMultiset.this.elementSet().size();
    }
    
    public boolean contains(@CheckForNull Object o) {
      if (o instanceof Multiset.Entry) {
        Multiset.Entry<?> entry = (Multiset.Entry)o;
        if (entry.getCount() <= 0)
          return false; 
        int count = ImmutableMultiset.this.count(entry.getElement());
        return (count == entry.getCount());
      } 
      return false;
    }
    
    public int hashCode() {
      return ImmutableMultiset.this.hashCode();
    }
    
    @GwtIncompatible
    @J2ktIncompatible
    Object writeReplace() {
      return new ImmutableMultiset.EntrySetSerializedForm(ImmutableMultiset.this);
    }
    
    @GwtIncompatible
    @J2ktIncompatible
    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
      throw new InvalidObjectException("Use EntrySetSerializedForm");
    }
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  static class EntrySetSerializedForm<E> implements Serializable {
    final ImmutableMultiset<E> multiset;
    
    EntrySetSerializedForm(ImmutableMultiset<E> multiset) {
      this.multiset = multiset;
    }
    
    Object readResolve() {
      return this.multiset.entrySet();
    }
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  Object writeReplace() {
    return new SerializedForm((Multiset)this);
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  private void readObject(ObjectInputStream stream) throws InvalidObjectException {
    throw new InvalidObjectException("Use SerializedForm");
  }
  
  public static <E> Builder<E> builder() {
    return new Builder<>();
  }
  
  public abstract ImmutableSet<E> elementSet();
  
  abstract Multiset.Entry<E> getEntry(int paramInt);
  
  public static class Builder<E> extends ImmutableCollection.Builder<E> {
    final Multiset<E> contents;
    
    public Builder() {
      this(LinkedHashMultiset.create());
    }
    
    Builder(Multiset<E> contents) {
      this.contents = contents;
    }
    
    @CanIgnoreReturnValue
    public Builder<E> add(E element) {
      this.contents.add((E)Preconditions.checkNotNull(element));
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<E> add(E... elements) {
      super.add(elements);
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<E> addCopies(E element, int occurrences) {
      this.contents.add((E)Preconditions.checkNotNull(element), occurrences);
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<E> setCount(E element, int count) {
      this.contents.setCount((E)Preconditions.checkNotNull(element), count);
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<E> addAll(Iterable<? extends E> elements) {
      if (elements instanceof Multiset) {
        Multiset<? extends E> multiset = Multisets.cast(elements);
        multiset.forEachEntry((e, n) -> this.contents.add((E)Preconditions.checkNotNull(e), n));
      } else {
        super.addAll(elements);
      } 
      return this;
    }
    
    @CanIgnoreReturnValue
    public Builder<E> addAll(Iterator<? extends E> elements) {
      super.addAll(elements);
      return this;
    }
    
    public ImmutableMultiset<E> build() {
      return ImmutableMultiset.copyOf(this.contents);
    }
    
    @VisibleForTesting
    ImmutableMultiset<E> buildJdkBacked() {
      if (this.contents.isEmpty())
        return ImmutableMultiset.of(); 
      return JdkBackedImmutableMultiset.create(this.contents.entrySet());
    }
  }
  
  static final class ElementSet<E> extends ImmutableSet.Indexed<E> {
    private final List<Multiset.Entry<E>> entries;
    
    private final Multiset<E> delegate;
    
    ElementSet(List<Multiset.Entry<E>> entries, Multiset<E> delegate) {
      this.entries = entries;
      this.delegate = delegate;
    }
    
    E get(int index) {
      return ((Multiset.Entry<E>)this.entries.get(index)).getElement();
    }
    
    public boolean contains(@CheckForNull Object object) {
      return this.delegate.contains(object);
    }
    
    boolean isPartialView() {
      return true;
    }
    
    public int size() {
      return this.entries.size();
    }
    
    @J2ktIncompatible
    @GwtIncompatible
    Object writeReplace() {
      return super.writeReplace();
    }
  }
  
  @J2ktIncompatible
  static final class SerializedForm implements Serializable {
    final Object[] elements;
    
    final int[] counts;
    
    private static final long serialVersionUID = 0L;
    
    SerializedForm(Multiset<? extends Object> multiset) {
      int distinct = multiset.entrySet().size();
      this.elements = new Object[distinct];
      this.counts = new int[distinct];
      int i = 0;
      for (Multiset.Entry<? extends Object> entry : multiset.entrySet()) {
        this.elements[i] = entry.getElement();
        this.counts[i] = entry.getCount();
        i++;
      } 
    }
    
    Object readResolve() {
      LinkedHashMultiset<Object> multiset = LinkedHashMultiset.create(this.elements.length);
      for (int i = 0; i < this.elements.length; i++)
        multiset.add(this.elements[i], this.counts[i]); 
      return ImmutableMultiset.copyOf(multiset);
    }
  }
}
