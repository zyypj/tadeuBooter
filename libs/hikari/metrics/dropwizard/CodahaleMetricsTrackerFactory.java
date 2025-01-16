package me.syncwrld.booter.libs.hikari.metrics.dropwizard;

import com.codahale.metrics.MetricRegistry;
import me.syncwrld.booter.libs.hikari.metrics.IMetricsTracker;
import me.syncwrld.booter.libs.hikari.metrics.MetricsTrackerFactory;
import me.syncwrld.booter.libs.hikari.metrics.PoolStats;

public final class CodahaleMetricsTrackerFactory implements MetricsTrackerFactory {
  private final MetricRegistry registry;
  
  public CodahaleMetricsTrackerFactory(MetricRegistry registry) {
    this.registry = registry;
  }
  
  public MetricRegistry getRegistry() {
    return this.registry;
  }
  
  public IMetricsTracker create(String poolName, PoolStats poolStats) {
    return new CodaHaleMetricsTracker(poolName, poolStats, this.registry);
  }
}
