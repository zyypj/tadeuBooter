package me.syncwrld.booter.libs.google.guava.collect;

import java.util.NoSuchElementException;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class AbstractIterator<T> extends UnmodifiableIterator<T> {
  private State state = State.NOT_READY;
  
  @CheckForNull
  private T next;
  
  @CheckForNull
  protected abstract T computeNext();
  
  private enum State {
    READY, NOT_READY, DONE, FAILED;
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  protected final T endOfData() {
    this.state = State.DONE;
    return null;
  }
  
  public final boolean hasNext() {
    Preconditions.checkState((this.state != State.FAILED));
    switch (this.state) {
      case DONE:
        return false;
      case READY:
        return true;
    } 
    return tryToComputeNext();
  }
  
  private boolean tryToComputeNext() {
    this.state = State.FAILED;
    this.next = computeNext();
    if (this.state != State.DONE) {
      this.state = State.READY;
      return true;
    } 
    return false;
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  public final T next() {
    if (!hasNext())
      throw new NoSuchElementException(); 
    this.state = State.NOT_READY;
    T result = NullnessCasts.uncheckedCastNullableTToT(this.next);
    this.next = null;
    return result;
  }
  
  @ParametricNullness
  public final T peek() {
    if (!hasNext())
      throw new NoSuchElementException(); 
    return NullnessCasts.uncheckedCastNullableTToT(this.next);
  }
}
