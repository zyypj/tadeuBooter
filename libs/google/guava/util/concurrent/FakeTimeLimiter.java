package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public final class FakeTimeLimiter implements TimeLimiter {
  @CanIgnoreReturnValue
  public <T> T newProxy(T target, Class<T> interfaceType, long timeoutDuration, TimeUnit timeoutUnit) {
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(interfaceType);
    Preconditions.checkNotNull(timeoutUnit);
    return target;
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  public <T> T callWithTimeout(Callable<T> callable, long timeoutDuration, TimeUnit timeoutUnit) throws ExecutionException {
    Preconditions.checkNotNull(callable);
    Preconditions.checkNotNull(timeoutUnit);
    try {
      return callable.call();
    } catch (RuntimeException e) {
      throw new UncheckedExecutionException(e);
    } catch (Exception e) {
      Platform.restoreInterruptIfIsInterruptedException(e);
      throw new ExecutionException(e);
    } catch (Error e) {
      throw new ExecutionError(e);
    } 
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  public <T> T callUninterruptiblyWithTimeout(Callable<T> callable, long timeoutDuration, TimeUnit timeoutUnit) throws ExecutionException {
    return callWithTimeout(callable, timeoutDuration, timeoutUnit);
  }
  
  public void runWithTimeout(Runnable runnable, long timeoutDuration, TimeUnit timeoutUnit) {
    Preconditions.checkNotNull(runnable);
    Preconditions.checkNotNull(timeoutUnit);
    try {
      runnable.run();
    } catch (Exception e) {
      throw new UncheckedExecutionException(e);
    } catch (Error e) {
      throw new ExecutionError(e);
    } 
  }
  
  public void runUninterruptiblyWithTimeout(Runnable runnable, long timeoutDuration, TimeUnit timeoutUnit) {
    runWithTimeout(runnable, timeoutDuration, timeoutUnit);
  }
}
