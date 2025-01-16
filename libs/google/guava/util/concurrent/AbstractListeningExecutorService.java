package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.CheckReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;

@CheckReturnValue
@ElementTypesAreNonnullByDefault
@GwtIncompatible
@J2ktIncompatible
public abstract class AbstractListeningExecutorService extends AbstractExecutorService implements ListeningExecutorService {
  @CanIgnoreReturnValue
  protected final <T> RunnableFuture<T> newTaskFor(Runnable runnable, @ParametricNullness T value) {
    return TrustedListenableFutureTask.create(runnable, value);
  }
  
  @CanIgnoreReturnValue
  protected final <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
    return TrustedListenableFutureTask.create(callable);
  }
  
  @CanIgnoreReturnValue
  public ListenableFuture<?> submit(Runnable task) {
    return (ListenableFuture)super.submit(task);
  }
  
  @CanIgnoreReturnValue
  public <T> ListenableFuture<T> submit(Runnable task, @ParametricNullness T result) {
    return (ListenableFuture<T>)super.<T>submit(task, result);
  }
  
  @CanIgnoreReturnValue
  public <T> ListenableFuture<T> submit(Callable<T> task) {
    return (ListenableFuture<T>)super.<T>submit(task);
  }
}
