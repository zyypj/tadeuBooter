package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;

@DoNotMock("Use FakeTimeLimiter")
@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public interface TimeLimiter {
  <T> T newProxy(T paramT, Class<T> paramClass, long paramLong, TimeUnit paramTimeUnit);
  
  default <T> T newProxy(T target, Class<T> interfaceType, Duration timeout) {
    return newProxy(target, interfaceType, Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  <T> T callWithTimeout(Callable<T> paramCallable, long paramLong, TimeUnit paramTimeUnit) throws TimeoutException, InterruptedException, ExecutionException;
  
  @ParametricNullness
  @CanIgnoreReturnValue
  default <T> T callWithTimeout(Callable<T> callable, Duration timeout) throws TimeoutException, InterruptedException, ExecutionException {
    return callWithTimeout(callable, Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  <T> T callUninterruptiblyWithTimeout(Callable<T> paramCallable, long paramLong, TimeUnit paramTimeUnit) throws TimeoutException, ExecutionException;
  
  @ParametricNullness
  @CanIgnoreReturnValue
  default <T> T callUninterruptiblyWithTimeout(Callable<T> callable, Duration timeout) throws TimeoutException, ExecutionException {
    return callUninterruptiblyWithTimeout(callable, 
        Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
  
  void runWithTimeout(Runnable paramRunnable, long paramLong, TimeUnit paramTimeUnit) throws TimeoutException, InterruptedException;
  
  default void runWithTimeout(Runnable runnable, Duration timeout) throws TimeoutException, InterruptedException {
    runWithTimeout(runnable, Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
  
  void runUninterruptiblyWithTimeout(Runnable paramRunnable, long paramLong, TimeUnit paramTimeUnit) throws TimeoutException;
  
  default void runUninterruptiblyWithTimeout(Runnable runnable, Duration timeout) throws TimeoutException {
    runUninterruptiblyWithTimeout(runnable, Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
}
