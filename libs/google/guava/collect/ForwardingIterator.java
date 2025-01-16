package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Iterator;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingIterator<T> extends ForwardingObject implements Iterator<T> {
  public boolean hasNext() {
    return delegate().hasNext();
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  public T next() {
    return delegate().next();
  }
  
  public void remove() {
    delegate().remove();
  }
  
  protected abstract Iterator<T> delegate();
}
