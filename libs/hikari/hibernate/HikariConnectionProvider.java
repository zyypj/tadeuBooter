package me.syncwrld.booter.libs.hikari.hibernate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import me.syncwrld.booter.libs.hikari.HikariConfig;
import me.syncwrld.booter.libs.hikari.HikariDataSource;
import me.syncwrld.booter.libs.javax.sql.DataSource;
import org.hibernate.HibernateException;
import org.hibernate.Version;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.hibernate.service.spi.Configurable;
import org.hibernate.service.spi.Stoppable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HikariConnectionProvider implements ConnectionProvider, Configurable, Stoppable {
  private static final long serialVersionUID = -9131625057941275711L;
  
  private static final Logger LOGGER = LoggerFactory.getLogger(HikariConnectionProvider.class);
  
  private HikariConfig hcfg = null;
  
  private HikariDataSource hds = null;
  
  public HikariConnectionProvider() {
    if (Version.getVersionString().substring(0, 5).compareTo("4.3.6") >= 1)
      LOGGER.warn("me.syncwrld.booter.libs.hikari.hibernate.HikariConnectionProvider has been deprecated for versions of Hibernate 4.3.6 and newer.  Please switch to org.hibernate.hikaricp.internal.HikariCPConnectionProvider."); 
  }
  
  public void configure(Map props) throws HibernateException {
    try {
      LOGGER.debug("Configuring HikariCP");
      this.hcfg = HikariConfigurationUtil.loadConfiguration(props);
      this.hds = new HikariDataSource(this.hcfg);
    } catch (Exception e) {
      throw new HibernateException(e);
    } 
    LOGGER.debug("HikariCP Configured");
  }
  
  public Connection getConnection() throws SQLException {
    Connection conn = null;
    if (this.hds != null)
      conn = this.hds.getConnection(); 
    return conn;
  }
  
  public void closeConnection(Connection conn) throws SQLException {
    conn.close();
  }
  
  public boolean supportsAggressiveRelease() {
    return false;
  }
  
  public boolean isUnwrappableAs(Class<?> unwrapType) {
    return (ConnectionProvider.class.equals(unwrapType) || HikariConnectionProvider.class.isAssignableFrom(unwrapType));
  }
  
  public <T> T unwrap(Class<T> unwrapType) {
    if (ConnectionProvider.class.equals(unwrapType) || HikariConnectionProvider.class
      .isAssignableFrom(unwrapType))
      return (T)this; 
    if (DataSource.class.isAssignableFrom(unwrapType))
      return (T)this.hds; 
    throw new UnknownUnwrapTypeException(unwrapType);
  }
  
  public void stop() {
    this.hds.close();
  }
}
