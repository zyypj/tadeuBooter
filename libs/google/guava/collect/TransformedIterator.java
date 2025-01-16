package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Iterator;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class TransformedIterator<F, T> implements Iterator<T> {
  final Iterator<? extends F> backingIterator;
  
  TransformedIterator(Iterator<? extends F> backingIterator) {
    this.backingIterator = (Iterator<? extends F>)Preconditions.checkNotNull(backingIterator);
  }
  
  @ParametricNullness
  abstract T transform(@ParametricNullness F paramF);
  
  public final boolean hasNext() {
    return this.backingIterator.hasNext();
  }
  
  @ParametricNullness
  public final T next() {
    return transform(this.backingIterator.next());
  }
  
  public final void remove() {
    this.backingIterator.remove();
  }
}
