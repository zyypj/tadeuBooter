package me.syncwrld.booter.libs.hikari.metrics.prometheus;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Summary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import me.syncwrld.booter.libs.hikari.metrics.IMetricsTracker;

class PrometheusMetricsTracker implements IMetricsTracker {
  private static final Counter CONNECTION_TIMEOUT_COUNTER = ((Counter.Builder)((Counter.Builder)((Counter.Builder)Counter.build()
    .name("hikaricp_connection_timeout_total"))
    .labelNames(new String[] { "pool" })).help("Connection timeout total count"))
    .create();
  
  private static final Summary ELAPSED_ACQUIRED_SUMMARY = createSummary("hikaricp_connection_acquired_nanos", "Connection acquired time (ns)");
  
  private static final Summary ELAPSED_USAGE_SUMMARY = createSummary("hikaricp_connection_usage_millis", "Connection usage (ms)");
  
  private static final Summary ELAPSED_CREATION_SUMMARY = createSummary("hikaricp_connection_creation_millis", "Connection creation (ms)");
  
  private static final Map<CollectorRegistry, PrometheusMetricsTrackerFactory.RegistrationStatus> registrationStatuses = new ConcurrentHashMap<>();
  
  private final String poolName;
  
  private final HikariCPCollector hikariCPCollector;
  
  private final Counter.Child connectionTimeoutCounterChild;
  
  private final Summary.Child elapsedAcquiredSummaryChild;
  
  private final Summary.Child elapsedUsageSummaryChild;
  
  private final Summary.Child elapsedCreationSummaryChild;
  
  PrometheusMetricsTracker(String poolName, CollectorRegistry collectorRegistry, HikariCPCollector hikariCPCollector) {
    registerMetrics(collectorRegistry);
    this.poolName = poolName;
    this.hikariCPCollector = hikariCPCollector;
    this.connectionTimeoutCounterChild = (Counter.Child)CONNECTION_TIMEOUT_COUNTER.labels(new String[] { poolName });
    this.elapsedAcquiredSummaryChild = (Summary.Child)ELAPSED_ACQUIRED_SUMMARY.labels(new String[] { poolName });
    this.elapsedUsageSummaryChild = (Summary.Child)ELAPSED_USAGE_SUMMARY.labels(new String[] { poolName });
    this.elapsedCreationSummaryChild = (Summary.Child)ELAPSED_CREATION_SUMMARY.labels(new String[] { poolName });
  }
  
  private void registerMetrics(CollectorRegistry collectorRegistry) {
    if (registrationStatuses.putIfAbsent(collectorRegistry, PrometheusMetricsTrackerFactory.RegistrationStatus.REGISTERED) == null) {
      CONNECTION_TIMEOUT_COUNTER.register(collectorRegistry);
      ELAPSED_ACQUIRED_SUMMARY.register(collectorRegistry);
      ELAPSED_USAGE_SUMMARY.register(collectorRegistry);
      ELAPSED_CREATION_SUMMARY.register(collectorRegistry);
    } 
  }
  
  public void recordConnectionAcquiredNanos(long elapsedAcquiredNanos) {
    this.elapsedAcquiredSummaryChild.observe(elapsedAcquiredNanos);
  }
  
  public void recordConnectionUsageMillis(long elapsedBorrowedMillis) {
    this.elapsedUsageSummaryChild.observe(elapsedBorrowedMillis);
  }
  
  public void recordConnectionCreatedMillis(long connectionCreatedMillis) {
    this.elapsedCreationSummaryChild.observe(connectionCreatedMillis);
  }
  
  public void recordConnectionTimeout() {
    this.connectionTimeoutCounterChild.inc();
  }
  
  private static Summary createSummary(String name, String help) {
    return ((Summary.Builder)((Summary.Builder)((Summary.Builder)Summary.build()
      .name(name))
      .labelNames(new String[] { "pool" })).help(help))
      .quantile(0.5D, 0.05D)
      .quantile(0.95D, 0.01D)
      .quantile(0.99D, 0.001D)
      .maxAgeSeconds(TimeUnit.MINUTES.toSeconds(5L))
      .ageBuckets(5)
      .create();
  }
  
  public void close() {
    this.hikariCPCollector.remove(this.poolName);
    CONNECTION_TIMEOUT_COUNTER.remove(new String[] { this.poolName });
    ELAPSED_ACQUIRED_SUMMARY.remove(new String[] { this.poolName });
    ELAPSED_USAGE_SUMMARY.remove(new String[] { this.poolName });
    ELAPSED_CREATION_SUMMARY.remove(new String[] { this.poolName });
  }
}
