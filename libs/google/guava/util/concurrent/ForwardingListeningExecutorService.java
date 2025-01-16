package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public abstract class ForwardingListeningExecutorService extends ForwardingExecutorService implements ListeningExecutorService {
  public <T> ListenableFuture<T> submit(Callable<T> task) {
    return delegate().submit(task);
  }
  
  public ListenableFuture<?> submit(Runnable task) {
    return delegate().submit(task);
  }
  
  public <T> ListenableFuture<T> submit(Runnable task, @ParametricNullness T result) {
    return delegate().submit(task, result);
  }
  
  protected abstract ListeningExecutorService delegate();
}
