package me.syncwrld.booter.libs.google.guava.collect;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
final class RegularContiguousSet<C extends Comparable> extends ContiguousSet<C> {
  private final Range<C> range;
  
  private static final long serialVersionUID = 0L;
  
  RegularContiguousSet(Range<C> range, DiscreteDomain<C> domain) {
    super(domain);
    this.range = range;
  }
  
  private ContiguousSet<C> intersectionInCurrentDomain(Range<C> other) {
    return this.range.isConnected(other) ? 
      ContiguousSet.<C>create(this.range.intersection(other), this.domain) : 
      new EmptyContiguousSet<>(this.domain);
  }
  
  ContiguousSet<C> headSetImpl(C toElement, boolean inclusive) {
    return intersectionInCurrentDomain((Range)Range.upTo((Comparable<?>)toElement, BoundType.forBoolean(inclusive)));
  }
  
  ContiguousSet<C> subSetImpl(C fromElement, boolean fromInclusive, C toElement, boolean toInclusive) {
    if (fromElement.compareTo(toElement) == 0 && !fromInclusive && !toInclusive)
      return new EmptyContiguousSet<>(this.domain); 
    return intersectionInCurrentDomain(
        (Range)Range.range((Comparable<?>)fromElement, 
          BoundType.forBoolean(fromInclusive), (Comparable<?>)toElement, 
          BoundType.forBoolean(toInclusive)));
  }
  
  ContiguousSet<C> tailSetImpl(C fromElement, boolean inclusive) {
    return intersectionInCurrentDomain((Range)Range.downTo((Comparable<?>)fromElement, BoundType.forBoolean(inclusive)));
  }
  
  @GwtIncompatible
  int indexOf(@CheckForNull Object target) {
    return contains(target) ? (int)this.domain.distance(first(), (C)Objects.<Object>requireNonNull(target)) : -1;
  }
  
  public UnmodifiableIterator<C> iterator() {
    return new AbstractSequentialIterator<C>((Comparable)first()) {
        final C last = RegularContiguousSet.this.last();
        
        @CheckForNull
        protected C computeNext(C previous) {
          return RegularContiguousSet.equalsOrThrow((Comparable<?>)previous, (Comparable<?>)this.last) ? null : RegularContiguousSet.this.domain.next(previous);
        }
      };
  }
  
  @GwtIncompatible
  public UnmodifiableIterator<C> descendingIterator() {
    return new AbstractSequentialIterator<C>((Comparable)last()) {
        final C first = RegularContiguousSet.this.first();
        
        @CheckForNull
        protected C computeNext(C previous) {
          return RegularContiguousSet.equalsOrThrow((Comparable<?>)previous, (Comparable<?>)this.first) ? null : RegularContiguousSet.this.domain.previous(previous);
        }
      };
  }
  
  private static boolean equalsOrThrow(Comparable<?> left, @CheckForNull Comparable<?> right) {
    return (right != null && Range.compareOrThrow(left, right) == 0);
  }
  
  boolean isPartialView() {
    return false;
  }
  
  public C first() {
    return (C)Objects.<Comparable>requireNonNull((Comparable)this.range.lowerBound.leastValueAbove(this.domain));
  }
  
  public C last() {
    return (C)Objects.<Comparable>requireNonNull((Comparable)this.range.upperBound.greatestValueBelow(this.domain));
  }
  
  ImmutableList<C> createAsList() {
    if (this.domain.supportsFastOffset)
      return new ImmutableAsList<C>() {
          ImmutableSortedSet<C> delegateCollection() {
            return RegularContiguousSet.this;
          }
          
          public C get(int i) {
            Preconditions.checkElementIndex(i, size());
            return RegularContiguousSet.this.domain.offset(RegularContiguousSet.this.first(), i);
          }
          
          @J2ktIncompatible
          @GwtIncompatible
          Object writeReplace() {
            return super.writeReplace();
          }
        }; 
    return super.createAsList();
  }
  
  public int size() {
    long distance = this.domain.distance(first(), last());
    return (distance >= 2147483647L) ? Integer.MAX_VALUE : ((int)distance + 1);
  }
  
  public boolean contains(@CheckForNull Object object) {
    if (object == null)
      return false; 
    try {
      return this.range.contains((C)object);
    } catch (ClassCastException e) {
      return false;
    } 
  }
  
  public boolean containsAll(Collection<?> targets) {
    return Collections2.containsAllImpl(this, targets);
  }
  
  public boolean isEmpty() {
    return false;
  }
  
  public ContiguousSet<C> intersection(ContiguousSet<C> other) {
    Preconditions.checkNotNull(other);
    Preconditions.checkArgument(this.domain.equals(other.domain));
    if (other.isEmpty())
      return other; 
    Comparable<Comparable> comparable1 = (Comparable)Ordering.<Comparable>natural().max(first(), (Comparable)other.first());
    Comparable<Comparable> comparable2 = (Comparable)Ordering.<Comparable>natural().min(last(), (Comparable)other.last());
    return (comparable1.compareTo(comparable2) <= 0) ? 
      ContiguousSet.<C>create((Range)Range.closed(comparable1, comparable2), this.domain) : 
      new EmptyContiguousSet<>(this.domain);
  }
  
  public Range<C> range() {
    return range(BoundType.CLOSED, BoundType.CLOSED);
  }
  
  public Range<C> range(BoundType lowerBoundType, BoundType upperBoundType) {
    return (Range)Range.create(this.range.lowerBound
        .withLowerBoundType(lowerBoundType, this.domain), this.range.upperBound
        .withUpperBoundType(upperBoundType, this.domain));
  }
  
  public boolean equals(@CheckForNull Object object) {
    if (object == this)
      return true; 
    if (object instanceof RegularContiguousSet) {
      RegularContiguousSet<?> that = (RegularContiguousSet)object;
      if (this.domain.equals(that.domain))
        return (first().equals(that.first()) && last().equals(that.last())); 
    } 
    return super.equals(object);
  }
  
  public int hashCode() {
    return Sets.hashCodeImpl(this);
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  private static final class SerializedForm<C extends Comparable> implements Serializable {
    final Range<C> range;
    
    final DiscreteDomain<C> domain;
    
    private SerializedForm(Range<C> range, DiscreteDomain<C> domain) {
      this.range = range;
      this.domain = domain;
    }
    
    private Object readResolve() {
      return new RegularContiguousSet<>(this.range, this.domain);
    }
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  Object writeReplace() {
    return new SerializedForm<>(this.range, this.domain);
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  private void readObject(ObjectInputStream stream) throws InvalidObjectException {
    throw new InvalidObjectException("Use SerializedForm");
  }
}
