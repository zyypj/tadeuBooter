package me.syncwrld.booter.libs.hikari.metrics;

import java.util.concurrent.atomic.AtomicLong;
import me.syncwrld.booter.libs.hikari.util.ClockSource;

public abstract class PoolStats {
  private final AtomicLong reloadAt;
  
  private final long timeoutMs;
  
  protected volatile int totalConnections;
  
  protected volatile int idleConnections;
  
  protected volatile int activeConnections;
  
  protected volatile int pendingThreads;
  
  protected volatile int maxConnections;
  
  protected volatile int minConnections;
  
  public PoolStats(long timeoutMs) {
    this.timeoutMs = timeoutMs;
    this.reloadAt = new AtomicLong();
  }
  
  public int getTotalConnections() {
    if (shouldLoad())
      update(); 
    return this.totalConnections;
  }
  
  public int getIdleConnections() {
    if (shouldLoad())
      update(); 
    return this.idleConnections;
  }
  
  public int getActiveConnections() {
    if (shouldLoad())
      update(); 
    return this.activeConnections;
  }
  
  public int getPendingThreads() {
    if (shouldLoad())
      update(); 
    return this.pendingThreads;
  }
  
  public int getMaxConnections() {
    if (shouldLoad())
      update(); 
    return this.maxConnections;
  }
  
  public int getMinConnections() {
    if (shouldLoad())
      update(); 
    return this.minConnections;
  }
  
  protected abstract void update();
  
  private boolean shouldLoad() {
    while (true) {
      long now = ClockSource.currentTime();
      long reloadTime = this.reloadAt.get();
      if (reloadTime > now)
        return false; 
      if (this.reloadAt.compareAndSet(reloadTime, ClockSource.plusMillis(now, this.timeoutMs)))
        return true; 
    } 
  }
}
