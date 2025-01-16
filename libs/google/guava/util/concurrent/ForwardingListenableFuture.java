package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingListenableFuture<V> extends ForwardingFuture<V> implements ListenableFuture<V> {
  public void addListener(Runnable listener, Executor exec) {
    delegate().addListener(listener, exec);
  }
  
  protected abstract ListenableFuture<? extends V> delegate();
  
  public static abstract class SimpleForwardingListenableFuture<V> extends ForwardingListenableFuture<V> {
    private final ListenableFuture<V> delegate;
    
    protected SimpleForwardingListenableFuture(ListenableFuture<V> delegate) {
      this.delegate = (ListenableFuture<V>)Preconditions.checkNotNull(delegate);
    }
    
    protected final ListenableFuture<V> delegate() {
      return this.delegate;
    }
  }
}
