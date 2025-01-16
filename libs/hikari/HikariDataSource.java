package me.syncwrld.booter.libs.hikari;

import java.io.Closeable;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import me.syncwrld.booter.libs.hikari.metrics.MetricsTrackerFactory;
import me.syncwrld.booter.libs.hikari.pool.HikariPool;
import me.syncwrld.booter.libs.javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HikariDataSource extends HikariConfig implements DataSource, Closeable {
  private static final Logger LOGGER = LoggerFactory.getLogger(HikariDataSource.class);
  
  private final AtomicBoolean isShutdown = new AtomicBoolean();
  
  private final HikariPool fastPathPool;
  
  private volatile HikariPool pool;
  
  public HikariDataSource() {
    this.fastPathPool = null;
  }
  
  public HikariDataSource(HikariConfig configuration) {
    configuration.validate();
    configuration.copyStateTo(this);
    LOGGER.info("{} - Starting...", configuration.getPoolName());
    this.pool = this.fastPathPool = new HikariPool(this);
    LOGGER.info("{} - Start completed.", configuration.getPoolName());
    seal();
  }
  
  public Connection getConnection() throws SQLException {
    if (isClosed())
      throw new SQLException("HikariDataSource " + this + " has been closed."); 
    if (this.fastPathPool != null)
      return this.fastPathPool.getConnection(); 
    HikariPool result = this.pool;
    if (result == null)
      synchronized (this) {
        result = this.pool;
        if (result == null) {
          validate();
          LOGGER.info("{} - Starting...", getPoolName());
          try {
            this.pool = result = new HikariPool(this);
            seal();
          } catch (me.syncwrld.booter.libs.hikari.pool.HikariPool.PoolInitializationException pie) {
            if (pie.getCause() instanceof SQLException)
              throw (SQLException)pie.getCause(); 
            throw pie;
          } 
          LOGGER.info("{} - Start completed.", getPoolName());
        } 
      }  
    return result.getConnection();
  }
  
  public Connection getConnection(String username, String password) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }
  
  public PrintWriter getLogWriter() throws SQLException {
    HikariPool p = this.pool;
    return (p != null) ? p.getUnwrappedDataSource().getLogWriter() : null;
  }
  
  public void setLogWriter(PrintWriter out) throws SQLException {
    HikariPool p = this.pool;
    if (p != null)
      p.getUnwrappedDataSource().setLogWriter(out); 
  }
  
  public void setLoginTimeout(int seconds) throws SQLException {
    HikariPool p = this.pool;
    if (p != null)
      p.getUnwrappedDataSource().setLoginTimeout(seconds); 
  }
  
  public int getLoginTimeout() throws SQLException {
    HikariPool p = this.pool;
    return (p != null) ? p.getUnwrappedDataSource().getLoginTimeout() : 0;
  }
  
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException();
  }
  
  public <T> T unwrap(Class<T> iface) throws SQLException {
    if (iface.isInstance(this))
      return (T)this; 
    HikariPool p = this.pool;
    if (p != null) {
      DataSource unwrappedDataSource = p.getUnwrappedDataSource();
      if (iface.isInstance(unwrappedDataSource))
        return (T)unwrappedDataSource; 
      if (unwrappedDataSource != null)
        return (T)unwrappedDataSource.unwrap(iface); 
    } 
    throw new SQLException("Wrapped DataSource is not an instance of " + iface);
  }
  
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    if (iface.isInstance(this))
      return true; 
    HikariPool p = this.pool;
    if (p != null) {
      DataSource unwrappedDataSource = p.getUnwrappedDataSource();
      if (iface.isInstance(unwrappedDataSource))
        return true; 
      if (unwrappedDataSource != null)
        return unwrappedDataSource.isWrapperFor(iface); 
    } 
    return false;
  }
  
  public void setMetricRegistry(Object metricRegistry) {
    boolean isAlreadySet = (getMetricRegistry() != null);
    super.setMetricRegistry(metricRegistry);
    HikariPool p = this.pool;
    if (p != null) {
      if (isAlreadySet)
        throw new IllegalStateException("MetricRegistry can only be set one time"); 
      p.setMetricRegistry(getMetricRegistry());
    } 
  }
  
  public void setMetricsTrackerFactory(MetricsTrackerFactory metricsTrackerFactory) {
    boolean isAlreadySet = (getMetricsTrackerFactory() != null);
    super.setMetricsTrackerFactory(metricsTrackerFactory);
    HikariPool p = this.pool;
    if (p != null) {
      if (isAlreadySet)
        throw new IllegalStateException("MetricsTrackerFactory can only be set one time"); 
      p.setMetricsTrackerFactory(getMetricsTrackerFactory());
    } 
  }
  
  public void setHealthCheckRegistry(Object healthCheckRegistry) {
    boolean isAlreadySet = (getHealthCheckRegistry() != null);
    super.setHealthCheckRegistry(healthCheckRegistry);
    HikariPool p = this.pool;
    if (p != null) {
      if (isAlreadySet)
        throw new IllegalStateException("HealthCheckRegistry can only be set one time"); 
      p.setHealthCheckRegistry(getHealthCheckRegistry());
    } 
  }
  
  public boolean isRunning() {
    return (this.pool != null && this.pool.poolState == 0);
  }
  
  public HikariPoolMXBean getHikariPoolMXBean() {
    return (HikariPoolMXBean)this.pool;
  }
  
  public HikariConfigMXBean getHikariConfigMXBean() {
    return this;
  }
  
  public void evictConnection(Connection connection) {
    HikariPool p;
    if (!isClosed() && (p = this.pool) != null && connection.getClass().getName().startsWith("me.syncwrld.booter.libs.hikari"))
      p.evictConnection(connection); 
  }
  
  public void close() {
    if (this.isShutdown.getAndSet(true))
      return; 
    HikariPool p = this.pool;
    if (p != null)
      try {
        LOGGER.info("{} - Shutdown initiated...", getPoolName());
        p.shutdown();
        LOGGER.info("{} - Shutdown completed.", getPoolName());
      } catch (InterruptedException e) {
        LOGGER.warn("{} - Interrupted during closing", getPoolName(), e);
        Thread.currentThread().interrupt();
      }  
  }
  
  public boolean isClosed() {
    return this.isShutdown.get();
  }
  
  public String toString() {
    return "HikariDataSource (" + this.pool + ")";
  }
}
