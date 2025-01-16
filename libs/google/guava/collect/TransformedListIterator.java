package me.syncwrld.booter.libs.google.guava.collect;

import java.util.ListIterator;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class TransformedListIterator<F, T> extends TransformedIterator<F, T> implements ListIterator<T> {
  TransformedListIterator(ListIterator<? extends F> backingIterator) {
    super(backingIterator);
  }
  
  private ListIterator<? extends F> backingIterator() {
    return (ListIterator<? extends F>)this.backingIterator;
  }
  
  public final boolean hasPrevious() {
    return backingIterator().hasPrevious();
  }
  
  @ParametricNullness
  public final T previous() {
    return transform(backingIterator().previous());
  }
  
  public final int nextIndex() {
    return backingIterator().nextIndex();
  }
  
  public final int previousIndex() {
    return backingIterator().previousIndex();
  }
  
  public void set(@ParametricNullness T element) {
    throw new UnsupportedOperationException();
  }
  
  public void add(@ParametricNullness T element) {
    throw new UnsupportedOperationException();
  }
}
