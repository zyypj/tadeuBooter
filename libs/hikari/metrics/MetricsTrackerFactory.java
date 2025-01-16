package me.syncwrld.booter.libs.hikari.metrics;

public interface MetricsTrackerFactory {
  IMetricsTracker create(String paramString, PoolStats paramPoolStats);
}
