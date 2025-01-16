package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
class TrustedListenableFutureTask<V> extends FluentFuture.TrustedFuture<V> implements RunnableFuture<V> {
  @CheckForNull
  private volatile InterruptibleTask<?> task;
  
  static <V> TrustedListenableFutureTask<V> create(AsyncCallable<V> callable) {
    return new TrustedListenableFutureTask<>(callable);
  }
  
  static <V> TrustedListenableFutureTask<V> create(Callable<V> callable) {
    return new TrustedListenableFutureTask<>(callable);
  }
  
  static <V> TrustedListenableFutureTask<V> create(Runnable runnable, @ParametricNullness V result) {
    return new TrustedListenableFutureTask<>(Executors.callable(runnable, result));
  }
  
  TrustedListenableFutureTask(Callable<V> callable) {
    this.task = new TrustedFutureInterruptibleTask(callable);
  }
  
  TrustedListenableFutureTask(AsyncCallable<V> callable) {
    this.task = new TrustedFutureInterruptibleAsyncTask(callable);
  }
  
  public void run() {
    InterruptibleTask<?> localTask = this.task;
    if (localTask != null)
      localTask.run(); 
    this.task = null;
  }
  
  protected void afterDone() {
    super.afterDone();
    if (wasInterrupted()) {
      InterruptibleTask<?> localTask = this.task;
      if (localTask != null)
        localTask.interruptTask(); 
    } 
    this.task = null;
  }
  
  @CheckForNull
  protected String pendingToString() {
    InterruptibleTask<?> localTask = this.task;
    if (localTask != null)
      return "task=[" + localTask + "]"; 
    return super.pendingToString();
  }
  
  private final class TrustedFutureInterruptibleTask extends InterruptibleTask<V> {
    private final Callable<V> callable;
    
    TrustedFutureInterruptibleTask(Callable<V> callable) {
      this.callable = (Callable<V>)Preconditions.checkNotNull(callable);
    }
    
    final boolean isDone() {
      return TrustedListenableFutureTask.this.isDone();
    }
    
    @ParametricNullness
    V runInterruptibly() throws Exception {
      return this.callable.call();
    }
    
    void afterRanInterruptiblySuccess(@ParametricNullness V result) {
      TrustedListenableFutureTask.this.set(result);
    }
    
    void afterRanInterruptiblyFailure(Throwable error) {
      TrustedListenableFutureTask.this.setException(error);
    }
    
    String toPendingString() {
      return this.callable.toString();
    }
  }
  
  private final class TrustedFutureInterruptibleAsyncTask extends InterruptibleTask<ListenableFuture<V>> {
    private final AsyncCallable<V> callable;
    
    TrustedFutureInterruptibleAsyncTask(AsyncCallable<V> callable) {
      this.callable = (AsyncCallable<V>)Preconditions.checkNotNull(callable);
    }
    
    final boolean isDone() {
      return TrustedListenableFutureTask.this.isDone();
    }
    
    ListenableFuture<V> runInterruptibly() throws Exception {
      return (ListenableFuture<V>)Preconditions.checkNotNull(this.callable
          .call(), "AsyncCallable.call returned null instead of a Future. Did you mean to return immediateFuture(null)? %s", this.callable);
    }
    
    void afterRanInterruptiblySuccess(ListenableFuture<V> result) {
      TrustedListenableFutureTask.this.setFuture(result);
    }
    
    void afterRanInterruptiblyFailure(Throwable error) {
      TrustedListenableFutureTask.this.setException(error);
    }
    
    String toPendingString() {
      return this.callable.toString();
    }
  }
}
