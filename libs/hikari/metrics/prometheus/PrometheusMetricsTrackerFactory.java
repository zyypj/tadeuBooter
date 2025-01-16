package me.syncwrld.booter.libs.hikari.metrics.prometheus;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.syncwrld.booter.libs.hikari.metrics.IMetricsTracker;
import me.syncwrld.booter.libs.hikari.metrics.MetricsTrackerFactory;
import me.syncwrld.booter.libs.hikari.metrics.PoolStats;

public class PrometheusMetricsTrackerFactory implements MetricsTrackerFactory {
  private static final Map<CollectorRegistry, RegistrationStatus> registrationStatuses = new ConcurrentHashMap<>();
  
  private final HikariCPCollector collector = new HikariCPCollector();
  
  private final CollectorRegistry collectorRegistry;
  
  enum RegistrationStatus {
    REGISTERED;
  }
  
  public PrometheusMetricsTrackerFactory() {
    this(CollectorRegistry.defaultRegistry);
  }
  
  public PrometheusMetricsTrackerFactory(CollectorRegistry collectorRegistry) {
    this.collectorRegistry = collectorRegistry;
  }
  
  public IMetricsTracker create(String poolName, PoolStats poolStats) {
    registerCollector(this.collector, this.collectorRegistry);
    this.collector.add(poolName, poolStats);
    return new PrometheusMetricsTracker(poolName, this.collectorRegistry, this.collector);
  }
  
  private void registerCollector(Collector collector, CollectorRegistry collectorRegistry) {
    if (registrationStatuses.putIfAbsent(collectorRegistry, RegistrationStatus.REGISTERED) == null)
      collector.register(collectorRegistry); 
  }
}
