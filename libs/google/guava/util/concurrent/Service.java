package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;

@DoNotMock("Create an AbstractIdleService")
@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public interface Service {
  @CanIgnoreReturnValue
  Service startAsync();
  
  boolean isRunning();
  
  State state();
  
  @CanIgnoreReturnValue
  Service stopAsync();
  
  void awaitRunning();
  
  default void awaitRunning(Duration timeout) throws TimeoutException {
    awaitRunning(Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
  
  void awaitRunning(long paramLong, TimeUnit paramTimeUnit) throws TimeoutException;
  
  void awaitTerminated();
  
  default void awaitTerminated(Duration timeout) throws TimeoutException {
    awaitTerminated(Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
  
  void awaitTerminated(long paramLong, TimeUnit paramTimeUnit) throws TimeoutException;
  
  Throwable failureCause();
  
  void addListener(Listener paramListener, Executor paramExecutor);
  
  public enum State {
    NEW, STARTING, RUNNING, STOPPING, TERMINATED, FAILED;
  }
  
  public static abstract class Listener {
    public void starting() {}
    
    public void running() {}
    
    public void stopping(Service.State from) {}
    
    public void terminated(Service.State from) {}
    
    public void failed(Service.State from, Throwable failure) {}
  }
}
