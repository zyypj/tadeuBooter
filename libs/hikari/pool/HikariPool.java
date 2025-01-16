package me.syncwrld.booter.libs.hikari.pool;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import me.syncwrld.booter.libs.hikari.HikariConfig;
import me.syncwrld.booter.libs.hikari.HikariPoolMXBean;
import me.syncwrld.booter.libs.hikari.metrics.MetricsTrackerFactory;
import me.syncwrld.booter.libs.hikari.metrics.PoolStats;
import me.syncwrld.booter.libs.hikari.metrics.dropwizard.CodahaleHealthChecker;
import me.syncwrld.booter.libs.hikari.metrics.dropwizard.CodahaleMetricsTrackerFactory;
import me.syncwrld.booter.libs.hikari.metrics.micrometer.MicrometerMetricsTrackerFactory;
import me.syncwrld.booter.libs.hikari.util.ClockSource;
import me.syncwrld.booter.libs.hikari.util.ConcurrentBag;
import me.syncwrld.booter.libs.hikari.util.SuspendResumeLock;
import me.syncwrld.booter.libs.hikari.util.UtilityElf;
import me.syncwrld.booter.libs.javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HikariPool extends PoolBase implements HikariPoolMXBean, ConcurrentBag.IBagStateListener {
  private final Logger logger = LoggerFactory.getLogger(HikariPool.class);
  
  public static final int POOL_NORMAL = 0;
  
  public static final int POOL_SUSPENDED = 1;
  
  public static final int POOL_SHUTDOWN = 2;
  
  public volatile int poolState;
  
  private final long aliveBypassWindowMs = Long.getLong("me.syncwrld.booter.libs.hikari.aliveBypassWindowMs", TimeUnit.MILLISECONDS.toMillis(500L)).longValue();
  
  private final long housekeepingPeriodMs = Long.getLong("me.syncwrld.booter.libs.hikari.housekeeping.periodMs", TimeUnit.SECONDS.toMillis(30L)).longValue();
  
  private static final String EVICTED_CONNECTION_MESSAGE = "(connection was evicted)";
  
  private static final String DEAD_CONNECTION_MESSAGE = "(connection is dead)";
  
  private final PoolEntryCreator poolEntryCreator = new PoolEntryCreator(null);
  
  private final PoolEntryCreator postFillPoolEntryCreator = new PoolEntryCreator("After adding ");
  
  private final Collection<Runnable> addConnectionQueueReadOnlyView;
  
  private final ThreadPoolExecutor addConnectionExecutor;
  
  private final ThreadPoolExecutor closeConnectionExecutor;
  
  private final ConcurrentBag<PoolEntry> connectionBag;
  
  private final ProxyLeakTaskFactory leakTaskFactory;
  
  private final SuspendResumeLock suspendResumeLock;
  
  private final ScheduledExecutorService houseKeepingExecutorService;
  
  private ScheduledFuture<?> houseKeeperTask;
  
  public HikariPool(HikariConfig config) {
    super(config);
    this.connectionBag = new ConcurrentBag(this);
    this.suspendResumeLock = config.isAllowPoolSuspension() ? new SuspendResumeLock() : SuspendResumeLock.FAUX_LOCK;
    this.houseKeepingExecutorService = initializeHouseKeepingExecutorService();
    checkFailFast();
    if (config.getMetricsTrackerFactory() != null) {
      setMetricsTrackerFactory(config.getMetricsTrackerFactory());
    } else {
      setMetricRegistry(config.getMetricRegistry());
    } 
    setHealthCheckRegistry(config.getHealthCheckRegistry());
    handleMBeans(this, true);
    ThreadFactory threadFactory = config.getThreadFactory();
    int maxPoolSize = config.getMaximumPoolSize();
    LinkedBlockingQueue<Runnable> addConnectionQueue = new LinkedBlockingQueue<>(maxPoolSize);
    this.addConnectionQueueReadOnlyView = Collections.unmodifiableCollection(addConnectionQueue);
    this.addConnectionExecutor = UtilityElf.createThreadPoolExecutor(addConnectionQueue, this.poolName + " connection adder", threadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
    this.closeConnectionExecutor = UtilityElf.createThreadPoolExecutor(maxPoolSize, this.poolName + " connection closer", threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
    this.leakTaskFactory = new ProxyLeakTaskFactory(config.getLeakDetectionThreshold(), this.houseKeepingExecutorService);
    this.houseKeeperTask = this.houseKeepingExecutorService.scheduleWithFixedDelay(new HouseKeeper(), 100L, this.housekeepingPeriodMs, TimeUnit.MILLISECONDS);
    if (Boolean.getBoolean("me.syncwrld.booter.libs.hikari.blockUntilFilled") && config.getInitializationFailTimeout() > 1L) {
      this.addConnectionExecutor.setMaximumPoolSize(Math.min(16, Runtime.getRuntime().availableProcessors()));
      this.addConnectionExecutor.setCorePoolSize(Math.min(16, Runtime.getRuntime().availableProcessors()));
      long startTime = ClockSource.currentTime();
      while (ClockSource.elapsedMillis(startTime) < config.getInitializationFailTimeout() && getTotalConnections() < config.getMinimumIdle())
        UtilityElf.quietlySleep(TimeUnit.MILLISECONDS.toMillis(100L)); 
      this.addConnectionExecutor.setCorePoolSize(1);
      this.addConnectionExecutor.setMaximumPoolSize(1);
    } 
  }
  
  public Connection getConnection() throws SQLException {
    return getConnection(this.connectionTimeout);
  }
  
  public Connection getConnection(long hardTimeout) throws SQLException {
    this.suspendResumeLock.acquire();
    long startTime = ClockSource.currentTime();
    try {
      long timeout = hardTimeout;
      do {
        PoolEntry poolEntry = (PoolEntry)this.connectionBag.borrow(timeout, TimeUnit.MILLISECONDS);
        if (poolEntry == null)
          break; 
        long now = ClockSource.currentTime();
        if (poolEntry.isMarkedEvicted() || (ClockSource.elapsedMillis(poolEntry.lastAccessed, now) > this.aliveBypassWindowMs && !isConnectionAlive(poolEntry.connection))) {
          closeConnection(poolEntry, poolEntry.isMarkedEvicted() ? "(connection was evicted)" : "(connection is dead)");
          timeout = hardTimeout - ClockSource.elapsedMillis(startTime);
        } else {
          this.metricsTracker.recordBorrowStats(poolEntry, startTime);
          return poolEntry.createProxyConnection(this.leakTaskFactory.schedule(poolEntry), now);
        } 
      } while (timeout > 0L);
      this.metricsTracker.recordBorrowTimeoutStats(startTime);
      throw createTimeoutException(startTime);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new SQLException(this.poolName + " - Interrupted during connection acquisition", e);
    } finally {
      this.suspendResumeLock.release();
    } 
  }
  
  public synchronized void shutdown() throws InterruptedException {
    try {
      this.poolState = 2;
      if (this.addConnectionExecutor == null)
        return; 
      logPoolState(new String[] { "Before shutdown " });
      if (this.houseKeeperTask != null) {
        this.houseKeeperTask.cancel(false);
        this.houseKeeperTask = null;
      } 
      softEvictConnections();
      this.addConnectionExecutor.shutdown();
      this.addConnectionExecutor.awaitTermination(getLoginTimeout(), TimeUnit.SECONDS);
      destroyHouseKeepingExecutorService();
      this.connectionBag.close();
      ExecutorService assassinExecutor = UtilityElf.createThreadPoolExecutor(this.config.getMaximumPoolSize(), this.poolName + " connection assassinator", this.config
          .getThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
      try {
        long start = ClockSource.currentTime();
        do {
          abortActiveConnections(assassinExecutor);
          softEvictConnections();
        } while (getTotalConnections() > 0 && ClockSource.elapsedMillis(start) < TimeUnit.SECONDS.toMillis(10L));
      } finally {
        assassinExecutor.shutdown();
        assassinExecutor.awaitTermination(10L, TimeUnit.SECONDS);
      } 
      shutdownNetworkTimeoutExecutor();
      this.closeConnectionExecutor.shutdown();
      this.closeConnectionExecutor.awaitTermination(10L, TimeUnit.SECONDS);
    } finally {
      logPoolState(new String[] { "After shutdown " });
      handleMBeans(this, false);
      this.metricsTracker.close();
    } 
  }
  
  public void evictConnection(Connection connection) {
    ProxyConnection proxyConnection = (ProxyConnection)connection;
    proxyConnection.cancelLeakTask();
    try {
      softEvictConnection(proxyConnection.getPoolEntry(), "(connection evicted by user)", !connection.isClosed());
    } catch (SQLException sQLException) {}
  }
  
  public void setMetricRegistry(Object metricRegistry) {
    if (metricRegistry != null && UtilityElf.safeIsAssignableFrom(metricRegistry, "com.codahale.metrics.MetricRegistry")) {
      setMetricsTrackerFactory((MetricsTrackerFactory)new CodahaleMetricsTrackerFactory((MetricRegistry)metricRegistry));
    } else if (metricRegistry != null && UtilityElf.safeIsAssignableFrom(metricRegistry, "io.micrometer.core.instrument.MeterRegistry")) {
      setMetricsTrackerFactory((MetricsTrackerFactory)new MicrometerMetricsTrackerFactory((MeterRegistry)metricRegistry));
    } else {
      setMetricsTrackerFactory((MetricsTrackerFactory)null);
    } 
  }
  
  public void setMetricsTrackerFactory(MetricsTrackerFactory metricsTrackerFactory) {
    if (metricsTrackerFactory != null) {
      this.metricsTracker = new PoolBase.MetricsTrackerDelegate(metricsTrackerFactory.create(this.config.getPoolName(), getPoolStats()));
    } else {
      this.metricsTracker = new PoolBase.NopMetricsTrackerDelegate();
    } 
  }
  
  public void setHealthCheckRegistry(Object healthCheckRegistry) {
    if (healthCheckRegistry != null)
      CodahaleHealthChecker.registerHealthChecks(this, this.config, (HealthCheckRegistry)healthCheckRegistry); 
  }
  
  public void addBagItem(int waiting) {
    boolean shouldAdd = (waiting - this.addConnectionQueueReadOnlyView.size() >= 0);
    if (shouldAdd) {
      this.addConnectionExecutor.submit(this.poolEntryCreator);
    } else {
      this.logger.debug("{} - Add connection elided, waiting {}, queue {}", new Object[] { this.poolName, Integer.valueOf(waiting), Integer.valueOf(this.addConnectionQueueReadOnlyView.size()) });
    } 
  }
  
  public int getActiveConnections() {
    return this.connectionBag.getCount(1);
  }
  
  public int getIdleConnections() {
    return this.connectionBag.getCount(0);
  }
  
  public int getTotalConnections() {
    return this.connectionBag.size();
  }
  
  public int getThreadsAwaitingConnection() {
    return this.connectionBag.getWaitingThreadCount();
  }
  
  public void softEvictConnections() {
    this.connectionBag.values().forEach(poolEntry -> softEvictConnection(poolEntry, "(connection evicted)", false));
  }
  
  public synchronized void suspendPool() {
    if (this.suspendResumeLock == SuspendResumeLock.FAUX_LOCK)
      throw new IllegalStateException(this.poolName + " - is not suspendable"); 
    if (this.poolState != 1) {
      this.suspendResumeLock.suspend();
      this.poolState = 1;
    } 
  }
  
  public synchronized void resumePool() {
    if (this.poolState == 1) {
      this.poolState = 0;
      fillPool();
      this.suspendResumeLock.resume();
    } 
  }
  
  void logPoolState(String... prefix) {
    if (this.logger.isDebugEnabled())
      this.logger.debug("{} - {}stats (total={}, active={}, idle={}, waiting={})", new Object[] { this.poolName, 
            (prefix.length > 0) ? prefix[0] : "", 
            Integer.valueOf(getTotalConnections()), Integer.valueOf(getActiveConnections()), Integer.valueOf(getIdleConnections()), Integer.valueOf(getThreadsAwaitingConnection()) }); 
  }
  
  void recycle(PoolEntry poolEntry) {
    this.metricsTracker.recordConnectionUsage(poolEntry);
    this.connectionBag.requite(poolEntry);
  }
  
  void closeConnection(PoolEntry poolEntry, String closureReason) {
    if (this.connectionBag.remove(poolEntry)) {
      Connection connection = poolEntry.close();
      this.closeConnectionExecutor.execute(() -> {
            quietlyCloseConnection(connection, closureReason);
            if (this.poolState == 0)
              fillPool(); 
          });
    } 
  }
  
  int[] getPoolStateCounts() {
    return this.connectionBag.getStateCounts();
  }
  
  private PoolEntry createPoolEntry() {
    try {
      PoolEntry poolEntry = newPoolEntry();
      long maxLifetime = this.config.getMaxLifetime();
      if (maxLifetime > 0L) {
        long variance = (maxLifetime > 10000L) ? ThreadLocalRandom.current().nextLong(maxLifetime / 40L) : 0L;
        long lifetime = maxLifetime - variance;
        poolEntry.setFutureEol(this.houseKeepingExecutorService.schedule(new MaxLifetimeTask(poolEntry), lifetime, TimeUnit.MILLISECONDS));
      } 
      long keepaliveTime = this.config.getKeepaliveTime();
      if (keepaliveTime > 0L) {
        long variance = ThreadLocalRandom.current().nextLong(keepaliveTime / 10L);
        long heartbeatTime = keepaliveTime - variance;
        poolEntry.setKeepalive(this.houseKeepingExecutorService.scheduleWithFixedDelay(new KeepaliveTask(poolEntry), heartbeatTime, heartbeatTime, TimeUnit.MILLISECONDS));
      } 
      return poolEntry;
    } catch (ConnectionSetupException e) {
      if (this.poolState == 0) {
        this.logger.error("{} - Error thrown while acquiring connection from data source", this.poolName, e.getCause());
        this.lastConnectionFailure.set(e);
      } 
    } catch (Exception e) {
      if (this.poolState == 0)
        this.logger.debug("{} - Cannot acquire connection from data source", this.poolName, e); 
    } 
    return null;
  }
  
  private synchronized void fillPool() {
    int connectionsToAdd = Math.min(this.config.getMaximumPoolSize() - getTotalConnections(), this.config.getMinimumIdle() - getIdleConnections()) - this.addConnectionQueueReadOnlyView.size();
    if (connectionsToAdd <= 0)
      this.logger.debug("{} - Fill pool skipped, pool is at sufficient level.", this.poolName); 
    for (int i = 0; i < connectionsToAdd; i++)
      this.addConnectionExecutor.submit((i < connectionsToAdd - 1) ? this.poolEntryCreator : this.postFillPoolEntryCreator); 
  }
  
  private void abortActiveConnections(ExecutorService assassinExecutor) {
    for (PoolEntry poolEntry : this.connectionBag.values(1)) {
      Connection connection = poolEntry.close();
      try {
        connection.abort(assassinExecutor);
      } catch (Throwable e) {
        quietlyCloseConnection(connection, "(connection aborted during shutdown)");
      } finally {
        this.connectionBag.remove(poolEntry);
      } 
    } 
  }
  
  private void checkFailFast() {
    long initializationTimeout = this.config.getInitializationFailTimeout();
    if (initializationTimeout < 0L)
      return; 
    long startTime = ClockSource.currentTime();
    do {
      PoolEntry poolEntry = createPoolEntry();
      if (poolEntry != null) {
        if (this.config.getMinimumIdle() > 0) {
          this.connectionBag.add(poolEntry);
          this.logger.debug("{} - Added connection {}", this.poolName, poolEntry.connection);
        } else {
          quietlyCloseConnection(poolEntry.close(), "(initialization check complete and minimumIdle is zero)");
        } 
        return;
      } 
      if (getLastConnectionFailure() instanceof PoolBase.ConnectionSetupException)
        throwPoolInitializationException(getLastConnectionFailure().getCause()); 
      UtilityElf.quietlySleep(TimeUnit.SECONDS.toMillis(1L));
    } while (ClockSource.elapsedMillis(startTime) < initializationTimeout);
    if (initializationTimeout > 0L)
      throwPoolInitializationException(getLastConnectionFailure()); 
  }
  
  private void throwPoolInitializationException(Throwable t) {
    this.logger.error("{} - Exception during pool initialization.", this.poolName, t);
    destroyHouseKeepingExecutorService();
    throw new PoolInitializationException(t);
  }
  
  private boolean softEvictConnection(PoolEntry poolEntry, String reason, boolean owner) {
    poolEntry.markEvicted();
    if (owner || this.connectionBag.reserve(poolEntry)) {
      closeConnection(poolEntry, reason);
      return true;
    } 
    return false;
  }
  
  private ScheduledExecutorService initializeHouseKeepingExecutorService() {
    if (this.config.getScheduledExecutor() == null) {
      ThreadFactory threadFactory = Optional.<ThreadFactory>ofNullable(this.config.getThreadFactory()).orElseGet(() -> new UtilityElf.DefaultThreadFactory(this.poolName + " housekeeper", true));
      ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, threadFactory, new ThreadPoolExecutor.DiscardPolicy());
      executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
      executor.setRemoveOnCancelPolicy(true);
      return executor;
    } 
    return this.config.getScheduledExecutor();
  }
  
  private void destroyHouseKeepingExecutorService() {
    if (this.config.getScheduledExecutor() == null)
      this.houseKeepingExecutorService.shutdownNow(); 
  }
  
  private PoolStats getPoolStats() {
    return new PoolStats(TimeUnit.SECONDS.toMillis(1L)) {
        protected void update() {
          this.pendingThreads = HikariPool.this.getThreadsAwaitingConnection();
          this.idleConnections = HikariPool.this.getIdleConnections();
          this.totalConnections = HikariPool.this.getTotalConnections();
          this.activeConnections = HikariPool.this.getActiveConnections();
          this.maxConnections = HikariPool.this.config.getMaximumPoolSize();
          this.minConnections = HikariPool.this.config.getMinimumIdle();
        }
      };
  }
  
  private SQLException createTimeoutException(long startTime) {
    logPoolState(new String[] { "Timeout failure " });
    this.metricsTracker.recordConnectionTimeout();
    String sqlState = null;
    Throwable originalException = getLastConnectionFailure();
    if (originalException instanceof SQLException)
      sqlState = ((SQLException)originalException).getSQLState(); 
    SQLException connectionException = new SQLTransientConnectionException(this.poolName + " - Connection is not available, request timed out after " + ClockSource.elapsedMillis(startTime) + "ms.", sqlState, originalException);
    if (originalException instanceof SQLException)
      connectionException.setNextException((SQLException)originalException); 
    return connectionException;
  }
  
  private final class PoolEntryCreator implements Callable<Boolean> {
    private final String loggingPrefix;
    
    PoolEntryCreator(String loggingPrefix) {
      this.loggingPrefix = loggingPrefix;
    }
    
    public Boolean call() {
      long sleepBackoff = 250L;
      while (HikariPool.this.poolState == 0 && shouldCreateAnotherConnection()) {
        PoolEntry poolEntry = HikariPool.this.createPoolEntry();
        if (poolEntry != null) {
          HikariPool.this.connectionBag.add(poolEntry);
          HikariPool.this.logger.debug("{} - Added connection {}", HikariPool.this.poolName, poolEntry.connection);
          if (this.loggingPrefix != null)
            HikariPool.this.logPoolState(new String[] { this.loggingPrefix }); 
          return Boolean.TRUE;
        } 
        if (this.loggingPrefix != null)
          HikariPool.this.logger.debug("{} - Connection add failed, sleeping with backoff: {}ms", HikariPool.this.poolName, Long.valueOf(sleepBackoff)); 
        UtilityElf.quietlySleep(sleepBackoff);
        sleepBackoff = Math.min(TimeUnit.SECONDS.toMillis(10L), Math.min(HikariPool.this.connectionTimeout, (long)(sleepBackoff * 1.5D)));
      } 
      return Boolean.FALSE;
    }
    
    private synchronized boolean shouldCreateAnotherConnection() {
      return (HikariPool.this.getTotalConnections() < HikariPool.this.config.getMaximumPoolSize() && (HikariPool.this
        .connectionBag.getWaitingThreadCount() > 0 || HikariPool.this.getIdleConnections() < HikariPool.this.config.getMinimumIdle()));
    }
  }
  
  private final class HouseKeeper implements Runnable {
    private volatile long previous = ClockSource.plusMillis(ClockSource.currentTime(), -HikariPool.this.housekeepingPeriodMs);
    
    public void run() {
      try {
        HikariPool.this.connectionTimeout = HikariPool.this.config.getConnectionTimeout();
        HikariPool.this.validationTimeout = HikariPool.this.config.getValidationTimeout();
        HikariPool.this.leakTaskFactory.updateLeakDetectionThreshold(HikariPool.this.config.getLeakDetectionThreshold());
        HikariPool.this.catalog = (HikariPool.this.config.getCatalog() != null && !HikariPool.this.config.getCatalog().equals(HikariPool.this.catalog)) ? HikariPool.this.config.getCatalog() : HikariPool.this.catalog;
        long idleTimeout = HikariPool.this.config.getIdleTimeout();
        long now = ClockSource.currentTime();
        if (ClockSource.plusMillis(now, 128L) < ClockSource.plusMillis(this.previous, HikariPool.this.housekeepingPeriodMs)) {
          HikariPool.this.logger.warn("{} - Retrograde clock change detected (housekeeper delta={}), soft-evicting connections from pool.", HikariPool.this.poolName, 
              ClockSource.elapsedDisplayString(this.previous, now));
          this.previous = now;
          HikariPool.this.softEvictConnections();
          return;
        } 
        if (now > ClockSource.plusMillis(this.previous, 3L * HikariPool.this.housekeepingPeriodMs / 2L))
          HikariPool.this.logger.warn("{} - Thread starvation or clock leap detected (housekeeper delta={}).", HikariPool.this.poolName, ClockSource.elapsedDisplayString(this.previous, now)); 
        this.previous = now;
        String afterPrefix = "Pool ";
        if (idleTimeout > 0L && HikariPool.this.config.getMinimumIdle() < HikariPool.this.config.getMaximumPoolSize()) {
          HikariPool.this.logPoolState(new String[] { "Before cleanup " });
          afterPrefix = "After cleanup  ";
          List<PoolEntry> notInUse = HikariPool.this.connectionBag.values(0);
          int toRemove = notInUse.size() - HikariPool.this.config.getMinimumIdle();
          for (PoolEntry entry : notInUse) {
            if (toRemove > 0 && ClockSource.elapsedMillis(entry.lastAccessed, now) > idleTimeout && HikariPool.this.connectionBag.reserve(entry)) {
              HikariPool.this.closeConnection(entry, "(connection has passed idleTimeout)");
              toRemove--;
            } 
          } 
        } 
        HikariPool.this.logPoolState(new String[] { afterPrefix });
        HikariPool.this.fillPool();
      } catch (Exception e) {
        HikariPool.this.logger.error("Unexpected exception in housekeeping task", e);
      } 
    }
    
    private HouseKeeper() {}
  }
  
  private final class MaxLifetimeTask implements Runnable {
    private final PoolEntry poolEntry;
    
    MaxLifetimeTask(PoolEntry poolEntry) {
      this.poolEntry = poolEntry;
    }
    
    public void run() {
      if (HikariPool.this.softEvictConnection(this.poolEntry, "(connection has passed maxLifetime)", false))
        HikariPool.this.addBagItem(HikariPool.this.connectionBag.getWaitingThreadCount()); 
    }
  }
  
  private final class KeepaliveTask implements Runnable {
    private final PoolEntry poolEntry;
    
    KeepaliveTask(PoolEntry poolEntry) {
      this.poolEntry = poolEntry;
    }
    
    public void run() {
      if (HikariPool.this.connectionBag.reserve(this.poolEntry))
        if (!HikariPool.this.isConnectionAlive(this.poolEntry.connection)) {
          HikariPool.this.softEvictConnection(this.poolEntry, "(connection is dead)", true);
          HikariPool.this.addBagItem(HikariPool.this.connectionBag.getWaitingThreadCount());
        } else {
          HikariPool.this.connectionBag.unreserve(this.poolEntry);
          HikariPool.this.logger.debug("{} - keepalive: connection {} is alive", HikariPool.this.poolName, this.poolEntry.connection);
        }  
    }
  }
  
  public static class PoolInitializationException extends RuntimeException {
    private static final long serialVersionUID = 929872118275916520L;
    
    public PoolInitializationException(Throwable t) {
      super("Failed to initialize pool: " + t.getMessage(), t);
    }
  }
}
