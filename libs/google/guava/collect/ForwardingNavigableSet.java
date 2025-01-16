package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class ForwardingNavigableSet<E> extends ForwardingSortedSet<E> implements NavigableSet<E> {
  @CheckForNull
  public E lower(@ParametricNullness E e) {
    return delegate().lower(e);
  }
  
  @CheckForNull
  protected E standardLower(@ParametricNullness E e) {
    return Iterators.getNext(headSet(e, false).descendingIterator(), null);
  }
  
  @CheckForNull
  public E floor(@ParametricNullness E e) {
    return delegate().floor(e);
  }
  
  @CheckForNull
  protected E standardFloor(@ParametricNullness E e) {
    return Iterators.getNext(headSet(e, true).descendingIterator(), null);
  }
  
  @CheckForNull
  public E ceiling(@ParametricNullness E e) {
    return delegate().ceiling(e);
  }
  
  @CheckForNull
  protected E standardCeiling(@ParametricNullness E e) {
    return Iterators.getNext(tailSet(e, true).iterator(), null);
  }
  
  @CheckForNull
  public E higher(@ParametricNullness E e) {
    return delegate().higher(e);
  }
  
  @CheckForNull
  protected E standardHigher(@ParametricNullness E e) {
    return Iterators.getNext(tailSet(e, false).iterator(), null);
  }
  
  @CheckForNull
  public E pollFirst() {
    return delegate().pollFirst();
  }
  
  @CheckForNull
  protected E standardPollFirst() {
    return Iterators.pollNext(iterator());
  }
  
  @CheckForNull
  public E pollLast() {
    return delegate().pollLast();
  }
  
  @CheckForNull
  protected E standardPollLast() {
    return Iterators.pollNext(descendingIterator());
  }
  
  @ParametricNullness
  protected E standardFirst() {
    return iterator().next();
  }
  
  @ParametricNullness
  protected E standardLast() {
    return descendingIterator().next();
  }
  
  public NavigableSet<E> descendingSet() {
    return delegate().descendingSet();
  }
  
  protected class StandardDescendingSet extends Sets.DescendingSet<E> {
    public StandardDescendingSet(ForwardingNavigableSet<E> this$0) {
      super(this$0);
    }
  }
  
  public Iterator<E> descendingIterator() {
    return delegate().descendingIterator();
  }
  
  public NavigableSet<E> subSet(@ParametricNullness E fromElement, boolean fromInclusive, @ParametricNullness E toElement, boolean toInclusive) {
    return delegate().subSet(fromElement, fromInclusive, toElement, toInclusive);
  }
  
  protected NavigableSet<E> standardSubSet(@ParametricNullness E fromElement, boolean fromInclusive, @ParametricNullness E toElement, boolean toInclusive) {
    return tailSet(fromElement, fromInclusive).headSet(toElement, toInclusive);
  }
  
  protected SortedSet<E> standardSubSet(@ParametricNullness E fromElement, @ParametricNullness E toElement) {
    return subSet(fromElement, true, toElement, false);
  }
  
  public NavigableSet<E> headSet(@ParametricNullness E toElement, boolean inclusive) {
    return delegate().headSet(toElement, inclusive);
  }
  
  protected SortedSet<E> standardHeadSet(@ParametricNullness E toElement) {
    return headSet(toElement, false);
  }
  
  public NavigableSet<E> tailSet(@ParametricNullness E fromElement, boolean inclusive) {
    return delegate().tailSet(fromElement, inclusive);
  }
  
  protected SortedSet<E> standardTailSet(@ParametricNullness E fromElement) {
    return tailSet(fromElement, true);
  }
  
  protected abstract NavigableSet<E> delegate();
}
