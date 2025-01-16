package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableCollection;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class CombinedFuture<V> extends AggregateFuture<Object, V> {
  @CheckForNull
  private CombinedFutureInterruptibleTask<?> task;
  
  CombinedFuture(ImmutableCollection<? extends ListenableFuture<?>> futures, boolean allMustSucceed, Executor listenerExecutor, AsyncCallable<V> callable) {
    super(futures, allMustSucceed, false);
    this.task = new AsyncCallableInterruptibleTask(callable, listenerExecutor);
    init();
  }
  
  CombinedFuture(ImmutableCollection<? extends ListenableFuture<?>> futures, boolean allMustSucceed, Executor listenerExecutor, Callable<V> callable) {
    super(futures, allMustSucceed, false);
    this.task = new CallableInterruptibleTask(callable, listenerExecutor);
    init();
  }
  
  void collectOneValue(int index, @CheckForNull Object returnValue) {}
  
  void handleAllCompleted() {
    CombinedFutureInterruptibleTask<?> localTask = this.task;
    if (localTask != null)
      localTask.execute(); 
  }
  
  void releaseResources(AggregateFuture.ReleaseResourcesReason reason) {
    super.releaseResources(reason);
    if (reason == AggregateFuture.ReleaseResourcesReason.OUTPUT_FUTURE_DONE)
      this.task = null; 
  }
  
  protected void interruptTask() {
    CombinedFutureInterruptibleTask<?> localTask = this.task;
    if (localTask != null)
      localTask.interruptTask(); 
  }
  
  private abstract class CombinedFutureInterruptibleTask<T> extends InterruptibleTask<T> {
    private final Executor listenerExecutor;
    
    CombinedFutureInterruptibleTask(Executor listenerExecutor) {
      this.listenerExecutor = (Executor)Preconditions.checkNotNull(listenerExecutor);
    }
    
    final boolean isDone() {
      return CombinedFuture.this.isDone();
    }
    
    final void execute() {
      try {
        this.listenerExecutor.execute(this);
      } catch (RejectedExecutionException e) {
        CombinedFuture.this.setException(e);
      } 
    }
    
    final void afterRanInterruptiblySuccess(@ParametricNullness T result) {
      CombinedFuture.this.task = null;
      setValue(result);
    }
    
    final void afterRanInterruptiblyFailure(Throwable error) {
      CombinedFuture.this.task = null;
      if (error instanceof ExecutionException) {
        CombinedFuture.this.setException(((ExecutionException)error).getCause());
      } else if (error instanceof java.util.concurrent.CancellationException) {
        CombinedFuture.this.cancel(false);
      } else {
        CombinedFuture.this.setException(error);
      } 
    }
    
    abstract void setValue(@ParametricNullness T param1T);
  }
  
  private final class AsyncCallableInterruptibleTask extends CombinedFutureInterruptibleTask<ListenableFuture<V>> {
    private final AsyncCallable<V> callable;
    
    AsyncCallableInterruptibleTask(AsyncCallable<V> callable, Executor listenerExecutor) {
      super(listenerExecutor);
      this.callable = (AsyncCallable<V>)Preconditions.checkNotNull(callable);
    }
    
    ListenableFuture<V> runInterruptibly() throws Exception {
      ListenableFuture<V> result = this.callable.call();
      return (ListenableFuture<V>)Preconditions.checkNotNull(result, "AsyncCallable.call returned null instead of a Future. Did you mean to return immediateFuture(null)? %s", this.callable);
    }
    
    void setValue(ListenableFuture<V> value) {
      CombinedFuture.this.setFuture(value);
    }
    
    String toPendingString() {
      return this.callable.toString();
    }
  }
  
  private final class CallableInterruptibleTask extends CombinedFutureInterruptibleTask<V> {
    private final Callable<V> callable;
    
    CallableInterruptibleTask(Callable<V> callable, Executor listenerExecutor) {
      super(listenerExecutor);
      this.callable = (Callable<V>)Preconditions.checkNotNull(callable);
    }
    
    @ParametricNullness
    V runInterruptibly() throws Exception {
      return this.callable.call();
    }
    
    void setValue(@ParametricNullness V value) {
      CombinedFuture.this.set(value);
    }
    
    String toPendingString() {
      return this.callable.toString();
    }
  }
}
