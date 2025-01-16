package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Iterator;
import java.util.ListIterator;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingListIterator<E> extends ForwardingIterator<E> implements ListIterator<E> {
  public void add(@ParametricNullness E element) {
    delegate().add(element);
  }
  
  public boolean hasPrevious() {
    return delegate().hasPrevious();
  }
  
  public int nextIndex() {
    return delegate().nextIndex();
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  public E previous() {
    return delegate().previous();
  }
  
  public int previousIndex() {
    return delegate().previousIndex();
  }
  
  public void set(@ParametricNullness E element) {
    delegate().set(element);
  }
  
  protected abstract ListIterator<E> delegate();
}
