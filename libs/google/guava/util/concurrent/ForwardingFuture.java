package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.ForwardingObject;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingFuture<V> extends ForwardingObject implements Future<V> {
  @CanIgnoreReturnValue
  public boolean cancel(boolean mayInterruptIfRunning) {
    return delegate().cancel(mayInterruptIfRunning);
  }
  
  public boolean isCancelled() {
    return delegate().isCancelled();
  }
  
  public boolean isDone() {
    return delegate().isDone();
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  public V get() throws InterruptedException, ExecutionException {
    return delegate().get();
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    return delegate().get(timeout, unit);
  }
  
  protected abstract Future<? extends V> delegate();
  
  public static abstract class SimpleForwardingFuture<V> extends ForwardingFuture<V> {
    private final Future<V> delegate;
    
    protected SimpleForwardingFuture(Future<V> delegate) {
      this.delegate = (Future<V>)Preconditions.checkNotNull(delegate);
    }
    
    protected final Future<V> delegate() {
      return this.delegate;
    }
  }
}
