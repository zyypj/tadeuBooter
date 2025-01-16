package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public abstract class ForwardingDeque<E> extends ForwardingQueue<E> implements Deque<E> {
  public void addFirst(@ParametricNullness E e) {
    delegate().addFirst(e);
  }
  
  public void addLast(@ParametricNullness E e) {
    delegate().addLast(e);
  }
  
  public Iterator<E> descendingIterator() {
    return delegate().descendingIterator();
  }
  
  @ParametricNullness
  public E getFirst() {
    return delegate().getFirst();
  }
  
  @ParametricNullness
  public E getLast() {
    return delegate().getLast();
  }
  
  @CanIgnoreReturnValue
  public boolean offerFirst(@ParametricNullness E e) {
    return delegate().offerFirst(e);
  }
  
  @CanIgnoreReturnValue
  public boolean offerLast(@ParametricNullness E e) {
    return delegate().offerLast(e);
  }
  
  @CheckForNull
  public E peekFirst() {
    return delegate().peekFirst();
  }
  
  @CheckForNull
  public E peekLast() {
    return delegate().peekLast();
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public E pollFirst() {
    return delegate().pollFirst();
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public E pollLast() {
    return delegate().pollLast();
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  public E pop() {
    return delegate().pop();
  }
  
  public void push(@ParametricNullness E e) {
    delegate().push(e);
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  public E removeFirst() {
    return delegate().removeFirst();
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  public E removeLast() {
    return delegate().removeLast();
  }
  
  @CanIgnoreReturnValue
  public boolean removeFirstOccurrence(@CheckForNull Object o) {
    return delegate().removeFirstOccurrence(o);
  }
  
  @CanIgnoreReturnValue
  public boolean removeLastOccurrence(@CheckForNull Object o) {
    return delegate().removeLastOccurrence(o);
  }
  
  protected abstract Deque<E> delegate();
}
