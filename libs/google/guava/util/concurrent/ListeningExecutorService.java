package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;

@DoNotMock("Use TestingExecutors.sameThreadScheduledExecutor, or wrap a real Executor from java.util.concurrent.Executors with MoreExecutors.listeningDecorator")
@ElementTypesAreNonnullByDefault
@GwtIncompatible
public interface ListeningExecutorService extends ExecutorService {
  @J2ktIncompatible
  default <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, Duration timeout) throws InterruptedException {
    return invokeAll(tasks, Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
  
  @J2ktIncompatible
  default <T> T invokeAny(Collection<? extends Callable<T>> tasks, Duration timeout) throws InterruptedException, ExecutionException, TimeoutException {
    return invokeAny(tasks, Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
  
  @J2ktIncompatible
  default boolean awaitTermination(Duration timeout) throws InterruptedException {
    return awaitTermination(Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
  
  <T> ListenableFuture<T> submit(Callable<T> paramCallable);
  
  ListenableFuture<?> submit(Runnable paramRunnable);
  
  <T> ListenableFuture<T> submit(Runnable paramRunnable, @ParametricNullness T paramT);
  
  <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> paramCollection) throws InterruptedException;
  
  <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> paramCollection, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException;
}
