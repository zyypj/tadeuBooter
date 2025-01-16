package me.syncwrld.booter.libs.hikari.metrics.micrometer;

import io.micrometer.core.instrument.MeterRegistry;
import me.syncwrld.booter.libs.hikari.metrics.IMetricsTracker;
import me.syncwrld.booter.libs.hikari.metrics.MetricsTrackerFactory;
import me.syncwrld.booter.libs.hikari.metrics.PoolStats;

public class MicrometerMetricsTrackerFactory implements MetricsTrackerFactory {
  private final MeterRegistry registry;
  
  public MicrometerMetricsTrackerFactory(MeterRegistry registry) {
    this.registry = registry;
  }
  
  public IMetricsTracker create(String poolName, PoolStats poolStats) {
    return new MicrometerMetricsTracker(poolName, poolStats, this.registry);
  }
}
