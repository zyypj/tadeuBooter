package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.CheckReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.collect.ForwardingObject;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public abstract class ForwardingExecutorService extends ForwardingObject implements ExecutorService {
  @CheckReturnValue
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    return delegate().awaitTermination(timeout, unit);
  }
  
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
    return delegate().invokeAll(tasks);
  }
  
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
    return delegate().invokeAll(tasks, timeout, unit);
  }
  
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
    return delegate().invokeAny(tasks);
  }
  
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    return delegate().invokeAny(tasks, timeout, unit);
  }
  
  public boolean isShutdown() {
    return delegate().isShutdown();
  }
  
  public boolean isTerminated() {
    return delegate().isTerminated();
  }
  
  public void shutdown() {
    delegate().shutdown();
  }
  
  @CanIgnoreReturnValue
  public List<Runnable> shutdownNow() {
    return delegate().shutdownNow();
  }
  
  public void execute(Runnable command) {
    delegate().execute(command);
  }
  
  public <T> Future<T> submit(Callable<T> task) {
    return delegate().submit(task);
  }
  
  public Future<?> submit(Runnable task) {
    return delegate().submit(task);
  }
  
  public <T> Future<T> submit(Runnable task, @ParametricNullness T result) {
    return delegate().submit(task, result);
  }
  
  protected abstract ExecutorService delegate();
}
