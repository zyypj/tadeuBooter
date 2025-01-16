package me.syncwrld.booter.libs.google.guava.collect;

import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SortedSet;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotCall;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
public abstract class ContiguousSet<C extends Comparable> extends ImmutableSortedSet<C> {
  final DiscreteDomain<C> domain;
  
  public static <C extends Comparable> ContiguousSet<C> create(Range<C> range, DiscreteDomain<C> domain) {
    boolean empty;
    Preconditions.checkNotNull(range);
    Preconditions.checkNotNull(domain);
    Range<C> effectiveRange = range;
    try {
      if (!range.hasLowerBound())
        effectiveRange = effectiveRange.intersection((Range)Range.atLeast((Comparable<?>)domain.minValue())); 
      if (!range.hasUpperBound())
        effectiveRange = effectiveRange.intersection((Range)Range.atMost((Comparable<?>)domain.maxValue())); 
    } catch (NoSuchElementException e) {
      throw new IllegalArgumentException(e);
    } 
    if (effectiveRange.isEmpty()) {
      empty = true;
    } else {
      Comparable comparable1 = Objects.<Comparable>requireNonNull((Comparable)range.lowerBound.leastValueAbove(domain));
      Comparable comparable2 = Objects.<Comparable>requireNonNull((Comparable)range.upperBound.greatestValueBelow(domain));
      empty = (Range.compareOrThrow(comparable1, comparable2) > 0);
    } 
    return empty ? 
      new EmptyContiguousSet<>(domain) : 
      new RegularContiguousSet<>(effectiveRange, domain);
  }
  
  public static ContiguousSet<Integer> closed(int lower, int upper) {
    return create(Range.closed(Integer.valueOf(lower), Integer.valueOf(upper)), DiscreteDomain.integers());
  }
  
  public static ContiguousSet<Long> closed(long lower, long upper) {
    return create(Range.closed(Long.valueOf(lower), Long.valueOf(upper)), DiscreteDomain.longs());
  }
  
  public static ContiguousSet<Integer> closedOpen(int lower, int upper) {
    return create(Range.closedOpen(Integer.valueOf(lower), Integer.valueOf(upper)), DiscreteDomain.integers());
  }
  
  public static ContiguousSet<Long> closedOpen(long lower, long upper) {
    return create(Range.closedOpen(Long.valueOf(lower), Long.valueOf(upper)), DiscreteDomain.longs());
  }
  
  ContiguousSet(DiscreteDomain<C> domain) {
    super(Ordering.natural());
    this.domain = domain;
  }
  
  public ContiguousSet<C> headSet(C toElement) {
    return headSetImpl((C)Preconditions.checkNotNull(toElement), false);
  }
  
  @GwtIncompatible
  public ContiguousSet<C> headSet(C toElement, boolean inclusive) {
    return headSetImpl((C)Preconditions.checkNotNull(toElement), inclusive);
  }
  
  public ContiguousSet<C> subSet(C fromElement, C toElement) {
    Preconditions.checkNotNull(fromElement);
    Preconditions.checkNotNull(toElement);
    Preconditions.checkArgument((comparator().compare(fromElement, toElement) <= 0));
    return subSetImpl(fromElement, true, toElement, false);
  }
  
  @GwtIncompatible
  public ContiguousSet<C> subSet(C fromElement, boolean fromInclusive, C toElement, boolean toInclusive) {
    Preconditions.checkNotNull(fromElement);
    Preconditions.checkNotNull(toElement);
    Preconditions.checkArgument((comparator().compare(fromElement, toElement) <= 0));
    return subSetImpl(fromElement, fromInclusive, toElement, toInclusive);
  }
  
  public ContiguousSet<C> tailSet(C fromElement) {
    return tailSetImpl((C)Preconditions.checkNotNull(fromElement), true);
  }
  
  @GwtIncompatible
  public ContiguousSet<C> tailSet(C fromElement, boolean inclusive) {
    return tailSetImpl((C)Preconditions.checkNotNull(fromElement), inclusive);
  }
  
  @GwtIncompatible
  ImmutableSortedSet<C> createDescendingSet() {
    return new DescendingImmutableSortedSet<>(this);
  }
  
  public String toString() {
    return range().toString();
  }
  
  @Deprecated
  @DoNotCall("Always throws UnsupportedOperationException")
  public static <E> ImmutableSortedSet.Builder<E> builder() {
    throw new UnsupportedOperationException();
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  Object writeReplace() {
    return super.writeReplace();
  }
  
  abstract ContiguousSet<C> headSetImpl(C paramC, boolean paramBoolean);
  
  abstract ContiguousSet<C> subSetImpl(C paramC1, boolean paramBoolean1, C paramC2, boolean paramBoolean2);
  
  abstract ContiguousSet<C> tailSetImpl(C paramC, boolean paramBoolean);
  
  public abstract ContiguousSet<C> intersection(ContiguousSet<C> paramContiguousSet);
  
  public abstract Range<C> range();
  
  public abstract Range<C> range(BoundType paramBoundType1, BoundType paramBoundType2);
}
