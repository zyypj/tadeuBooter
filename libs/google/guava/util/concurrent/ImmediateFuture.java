package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
class ImmediateFuture<V> implements ListenableFuture<V> {
  static final ListenableFuture<?> NULL = new ImmediateFuture(null);
  
  private static final LazyLogger log = new LazyLogger(ImmediateFuture.class);
  
  @ParametricNullness
  private final V value;
  
  ImmediateFuture(@ParametricNullness V value) {
    this.value = value;
  }
  
  public void addListener(Runnable listener, Executor executor) {
    Preconditions.checkNotNull(listener, "Runnable was null.");
    Preconditions.checkNotNull(executor, "Executor was null.");
    try {
      executor.execute(listener);
    } catch (Exception e) {
      log.get()
        .log(Level.SEVERE, "RuntimeException while executing runnable " + listener + " with executor " + executor, e);
    } 
  }
  
  public boolean cancel(boolean mayInterruptIfRunning) {
    return false;
  }
  
  @ParametricNullness
  public V get() {
    return this.value;
  }
  
  @ParametricNullness
  public V get(long timeout, TimeUnit unit) throws ExecutionException {
    Preconditions.checkNotNull(unit);
    return get();
  }
  
  public boolean isCancelled() {
    return false;
  }
  
  public boolean isDone() {
    return true;
  }
  
  public String toString() {
    return super.toString() + "[status=SUCCESS, result=[" + this.value + "]]";
  }
  
  static final class ImmediateFailedFuture<V> extends AbstractFuture.TrustedFuture<V> {
    ImmediateFailedFuture(Throwable thrown) {
      setException(thrown);
    }
  }
  
  static final class ImmediateCancelledFuture<V> extends AbstractFuture.TrustedFuture<V> {
    @CheckForNull
    static final ImmediateCancelledFuture<Object> INSTANCE = AbstractFuture.GENERATE_CANCELLATION_CAUSES ? null : new ImmediateCancelledFuture();
    
    ImmediateCancelledFuture() {
      cancel(false);
    }
  }
}
