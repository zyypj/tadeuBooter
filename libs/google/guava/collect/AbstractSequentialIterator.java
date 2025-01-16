package me.syncwrld.booter.libs.google.guava.collect;

import java.util.NoSuchElementException;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class AbstractSequentialIterator<T> extends UnmodifiableIterator<T> {
  @CheckForNull
  private T nextOrNull;
  
  protected AbstractSequentialIterator(@CheckForNull T firstOrNull) {
    this.nextOrNull = firstOrNull;
  }
  
  @CheckForNull
  protected abstract T computeNext(T paramT);
  
  public final boolean hasNext() {
    return (this.nextOrNull != null);
  }
  
  public final T next() {
    if (this.nextOrNull == null)
      throw new NoSuchElementException(); 
    T oldNext = this.nextOrNull;
    this.nextOrNull = computeNext(oldNext);
    return oldNext;
  }
}
