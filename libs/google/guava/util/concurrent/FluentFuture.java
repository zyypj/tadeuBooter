package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Function;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@DoNotMock("Use FluentFuture.from(Futures.immediate*Future) or SettableFuture")
@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
public abstract class FluentFuture<V> extends GwtFluentFutureCatchingSpecialization<V> {
  static abstract class TrustedFuture<V> extends FluentFuture<V> implements AbstractFuture.Trusted<V> {
    @ParametricNullness
    @CanIgnoreReturnValue
    public final V get() throws InterruptedException, ExecutionException {
      return super.get();
    }
    
    @ParametricNullness
    @CanIgnoreReturnValue
    public final V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return super.get(timeout, unit);
    }
    
    public final boolean isDone() {
      return super.isDone();
    }
    
    public final boolean isCancelled() {
      return super.isCancelled();
    }
    
    public final void addListener(Runnable listener, Executor executor) {
      super.addListener(listener, executor);
    }
    
    @CanIgnoreReturnValue
    public final boolean cancel(boolean mayInterruptIfRunning) {
      return super.cancel(mayInterruptIfRunning);
    }
  }
  
  public static <V> FluentFuture<V> from(ListenableFuture<V> future) {
    return (future instanceof FluentFuture) ? 
      (FluentFuture<V>)future : 
      new ForwardingFluentFuture<>(future);
  }
  
  @Deprecated
  public static <V> FluentFuture<V> from(FluentFuture<V> future) {
    return (FluentFuture<V>)Preconditions.checkNotNull(future);
  }
  
  @J2ktIncompatible
  @GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
  public final <X extends Throwable> FluentFuture<V> catching(Class<X> exceptionType, Function<? super X, ? extends V> fallback, Executor executor) {
    return (FluentFuture<V>)Futures.<V, X>catching(this, exceptionType, fallback, executor);
  }
  
  @J2ktIncompatible
  @GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
  public final <X extends Throwable> FluentFuture<V> catchingAsync(Class<X> exceptionType, AsyncFunction<? super X, ? extends V> fallback, Executor executor) {
    return (FluentFuture<V>)Futures.<V, X>catchingAsync(this, exceptionType, fallback, executor);
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public final FluentFuture<V> withTimeout(Duration timeout, ScheduledExecutorService scheduledExecutor) {
    return withTimeout(Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS, scheduledExecutor);
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public final FluentFuture<V> withTimeout(long timeout, TimeUnit unit, ScheduledExecutorService scheduledExecutor) {
    return (FluentFuture<V>)Futures.<V>withTimeout(this, timeout, unit, scheduledExecutor);
  }
  
  public final <T> FluentFuture<T> transformAsync(AsyncFunction<? super V, T> function, Executor executor) {
    return (FluentFuture<T>)Futures.<V, T>transformAsync(this, function, executor);
  }
  
  public final <T> FluentFuture<T> transform(Function<? super V, T> function, Executor executor) {
    return (FluentFuture<T>)Futures.<V, T>transform(this, function, executor);
  }
  
  public final void addCallback(FutureCallback<? super V> callback, Executor executor) {
    Futures.addCallback(this, callback, executor);
  }
}
