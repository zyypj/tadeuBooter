package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Comparator;
import java.util.Objects;
import java.util.Spliterator;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
final class ImmutableSortedAsList<E> extends RegularImmutableAsList<E> implements SortedIterable<E> {
  ImmutableSortedAsList(ImmutableSortedSet<E> backingSet, ImmutableList<E> backingList) {
    super(backingSet, backingList);
  }
  
  ImmutableSortedSet<E> delegateCollection() {
    return (ImmutableSortedSet<E>)super.delegateCollection();
  }
  
  public Comparator<? super E> comparator() {
    return delegateCollection().comparator();
  }
  
  @GwtIncompatible
  public int indexOf(@CheckForNull Object target) {
    int index = delegateCollection().indexOf(target);
    return (index >= 0 && get(index).equals(target)) ? index : -1;
  }
  
  @GwtIncompatible
  public int lastIndexOf(@CheckForNull Object target) {
    return indexOf(target);
  }
  
  public boolean contains(@CheckForNull Object target) {
    return (indexOf(target) >= 0);
  }
  
  @GwtIncompatible
  ImmutableList<E> subListUnchecked(int fromIndex, int toIndex) {
    ImmutableList<E> parentSubList = super.subListUnchecked(fromIndex, toIndex);
    return (new RegularImmutableSortedSet(parentSubList, comparator())).asList();
  }
  
  public Spliterator<E> spliterator() {
    Objects.requireNonNull(delegateList());
    return CollectSpliterators.indexed(size(), 1301, delegateList()::get, 
        comparator());
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  Object writeReplace() {
    return super.writeReplace();
  }
}
