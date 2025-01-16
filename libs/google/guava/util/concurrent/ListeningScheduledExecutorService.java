package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public interface ListeningScheduledExecutorService extends ScheduledExecutorService, ListeningExecutorService {
  default ListenableScheduledFuture<?> schedule(Runnable command, Duration delay) {
    return schedule(command, Internal.toNanosSaturated(delay), TimeUnit.NANOSECONDS);
  }
  
  default <V> ListenableScheduledFuture<V> schedule(Callable<V> callable, Duration delay) {
    return schedule(callable, Internal.toNanosSaturated(delay), TimeUnit.NANOSECONDS);
  }
  
  default ListenableScheduledFuture<?> scheduleAtFixedRate(Runnable command, Duration initialDelay, Duration period) {
    return scheduleAtFixedRate(command, 
        Internal.toNanosSaturated(initialDelay), Internal.toNanosSaturated(period), TimeUnit.NANOSECONDS);
  }
  
  default ListenableScheduledFuture<?> scheduleWithFixedDelay(Runnable command, Duration initialDelay, Duration delay) {
    return scheduleWithFixedDelay(command, 
        Internal.toNanosSaturated(initialDelay), Internal.toNanosSaturated(delay), TimeUnit.NANOSECONDS);
  }
  
  ListenableScheduledFuture<?> schedule(Runnable paramRunnable, long paramLong, TimeUnit paramTimeUnit);
  
  <V> ListenableScheduledFuture<V> schedule(Callable<V> paramCallable, long paramLong, TimeUnit paramTimeUnit);
  
  ListenableScheduledFuture<?> scheduleAtFixedRate(Runnable paramRunnable, long paramLong1, long paramLong2, TimeUnit paramTimeUnit);
  
  ListenableScheduledFuture<?> scheduleWithFixedDelay(Runnable paramRunnable, long paramLong1, long paramLong2, TimeUnit paramTimeUnit);
}
