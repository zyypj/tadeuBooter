package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.collect.ForwardingQueue;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public abstract class ForwardingBlockingQueue<E> extends ForwardingQueue<E> implements BlockingQueue<E> {
  @CanIgnoreReturnValue
  public int drainTo(Collection<? super E> c, int maxElements) {
    return delegate().drainTo(c, maxElements);
  }
  
  @CanIgnoreReturnValue
  public int drainTo(Collection<? super E> c) {
    return delegate().drainTo(c);
  }
  
  @CanIgnoreReturnValue
  public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
    return delegate().offer(e, timeout, unit);
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public E poll(long timeout, TimeUnit unit) throws InterruptedException {
    return delegate().poll(timeout, unit);
  }
  
  public void put(E e) throws InterruptedException {
    delegate().put(e);
  }
  
  public int remainingCapacity() {
    return delegate().remainingCapacity();
  }
  
  @CanIgnoreReturnValue
  public E take() throws InterruptedException {
    return delegate().take();
  }
  
  protected abstract BlockingQueue<E> delegate();
}
