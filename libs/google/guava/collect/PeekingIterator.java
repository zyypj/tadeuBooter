package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Iterator;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@DoNotMock("Use Iterators.peekingIterator")
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface PeekingIterator<E> extends Iterator<E> {
  @ParametricNullness
  E peek();
  
  @ParametricNullness
  @CanIgnoreReturnValue
  E next();
  
  void remove();
}
