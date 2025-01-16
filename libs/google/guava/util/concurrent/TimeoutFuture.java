package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
final class TimeoutFuture<V> extends FluentFuture.TrustedFuture<V> {
  @CheckForNull
  private ListenableFuture<V> delegateRef;
  
  @CheckForNull
  private ScheduledFuture<?> timer;
  
  static <V> ListenableFuture<V> create(ListenableFuture<V> delegate, long time, TimeUnit unit, ScheduledExecutorService scheduledExecutor) {
    TimeoutFuture<V> result = new TimeoutFuture<>(delegate);
    Fire<V> fire = new Fire<>(result);
    result.timer = scheduledExecutor.schedule(fire, time, unit);
    delegate.addListener(fire, MoreExecutors.directExecutor());
    return result;
  }
  
  private TimeoutFuture(ListenableFuture<V> delegate) {
    this.delegateRef = (ListenableFuture<V>)Preconditions.checkNotNull(delegate);
  }
  
  private static final class Fire<V> implements Runnable {
    @CheckForNull
    TimeoutFuture<V> timeoutFutureRef;
    
    Fire(TimeoutFuture<V> timeoutFuture) {
      this.timeoutFutureRef = timeoutFuture;
    }
    
    public void run() {
      TimeoutFuture<V> timeoutFuture = this.timeoutFutureRef;
      if (timeoutFuture == null)
        return; 
      ListenableFuture<V> delegate = timeoutFuture.delegateRef;
      if (delegate == null)
        return; 
      this.timeoutFutureRef = null;
      if (delegate.isDone()) {
        timeoutFuture.setFuture(delegate);
      } else {
        try {
          ScheduledFuture<?> timer = timeoutFuture.timer;
          timeoutFuture.timer = null;
          String message = "Timed out";
          try {
            if (timer != null) {
              long overDelayMs = Math.abs(timer.getDelay(TimeUnit.MILLISECONDS));
              if (overDelayMs > 10L)
                message = message + " (timeout delayed by " + overDelayMs + " ms after scheduled time)"; 
            } 
            message = message + ": " + delegate;
          } finally {
            timeoutFuture.setException(new TimeoutFuture.TimeoutFutureException(message));
          } 
        } finally {
          delegate.cancel(true);
        } 
      } 
    }
  }
  
  private static final class TimeoutFutureException extends TimeoutException {
    private TimeoutFutureException(String message) {
      super(message);
    }
    
    public synchronized Throwable fillInStackTrace() {
      setStackTrace(new StackTraceElement[0]);
      return this;
    }
  }
  
  @CheckForNull
  protected String pendingToString() {
    ListenableFuture<? extends V> localInputFuture = this.delegateRef;
    ScheduledFuture<?> localTimer = this.timer;
    if (localInputFuture != null) {
      String message = "inputFuture=[" + localInputFuture + "]";
      if (localTimer != null) {
        long delay = localTimer.getDelay(TimeUnit.MILLISECONDS);
        if (delay > 0L)
          message = message + ", remaining delay=[" + delay + " ms]"; 
      } 
      return message;
    } 
    return null;
  }
  
  protected void afterDone() {
    maybePropagateCancellationTo(this.delegateRef);
    Future<?> localTimer = this.timer;
    if (localTimer != null)
      localTimer.cancel(false); 
    this.delegateRef = null;
    this.timer = null;
  }
}
