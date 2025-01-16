package me.syncwrld.booter.libs.hikari.pool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import me.syncwrld.booter.libs.hikari.SQLExceptionOverride;
import me.syncwrld.booter.libs.hikari.util.ClockSource;
import me.syncwrld.booter.libs.hikari.util.FastList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProxyConnection implements Connection {
  static final int DIRTY_BIT_READONLY = 1;
  
  static final int DIRTY_BIT_AUTOCOMMIT = 2;
  
  static final int DIRTY_BIT_ISOLATION = 4;
  
  static final int DIRTY_BIT_CATALOG = 8;
  
  static final int DIRTY_BIT_NETTIMEOUT = 16;
  
  static final int DIRTY_BIT_SCHEMA = 32;
  
  private static final Logger LOGGER = LoggerFactory.getLogger(ProxyConnection.class);
  
  private static final Set<String> ERROR_STATES = new HashSet<>();
  
  static {
    ERROR_STATES.add("0A000");
    ERROR_STATES.add("57P01");
    ERROR_STATES.add("57P02");
    ERROR_STATES.add("57P03");
    ERROR_STATES.add("01002");
    ERROR_STATES.add("JZ0C0");
    ERROR_STATES.add("JZ0C1");
  }
  
  private static final Set<Integer> ERROR_CODES = new HashSet<>();
  
  protected Connection delegate;
  
  private final PoolEntry poolEntry;
  
  private final ProxyLeakTask leakTask;
  
  private final FastList<Statement> openStatements;
  
  private int dirtyBits;
  
  private long lastAccess;
  
  private boolean isCommitStateDirty;
  
  private boolean isReadOnly;
  
  private boolean isAutoCommit;
  
  private int networkTimeout;
  
  private int transactionIsolation;
  
  private String dbcatalog;
  
  private String dbschema;
  
  static {
    ERROR_CODES.add(Integer.valueOf(500150));
    ERROR_CODES.add(Integer.valueOf(2399));
  }
  
  protected ProxyConnection(PoolEntry poolEntry, Connection connection, FastList<Statement> openStatements, ProxyLeakTask leakTask, long now, boolean isReadOnly, boolean isAutoCommit) {
    this.poolEntry = poolEntry;
    this.delegate = connection;
    this.openStatements = openStatements;
    this.leakTask = leakTask;
    this.lastAccess = now;
    this.isReadOnly = isReadOnly;
    this.isAutoCommit = isAutoCommit;
  }
  
  public final String toString() {
    return getClass().getSimpleName() + '@' + System.identityHashCode(this) + " wrapping " + this.delegate;
  }
  
  final boolean getAutoCommitState() {
    return this.isAutoCommit;
  }
  
  final String getCatalogState() {
    return this.dbcatalog;
  }
  
  final String getSchemaState() {
    return this.dbschema;
  }
  
  final int getTransactionIsolationState() {
    return this.transactionIsolation;
  }
  
  final boolean getReadOnlyState() {
    return this.isReadOnly;
  }
  
  final int getNetworkTimeoutState() {
    return this.networkTimeout;
  }
  
  final PoolEntry getPoolEntry() {
    return this.poolEntry;
  }
  
  final SQLException checkException(SQLException sqle) {
    boolean evict = false;
    SQLException nse = sqle;
    SQLExceptionOverride exceptionOverride = (this.poolEntry.getPoolBase()).exceptionOverride;
    for (int depth = 0; this.delegate != ClosedConnection.CLOSED_CONNECTION && nse != null && depth < 10; depth++) {
      String sqlState = nse.getSQLState();
      if ((sqlState != null && sqlState.startsWith("08")) || nse instanceof java.sql.SQLTimeoutException || ERROR_STATES
        
        .contains(sqlState) || ERROR_CODES
        .contains(Integer.valueOf(nse.getErrorCode()))) {
        if (exceptionOverride != null && exceptionOverride.adjudicate(nse) == SQLExceptionOverride.Override.DO_NOT_EVICT)
          break; 
        evict = true;
        break;
      } 
      nse = nse.getNextException();
    } 
    if (evict) {
      SQLException exception = (nse != null) ? nse : sqle;
      LOGGER.warn("{} - Connection {} marked as broken because of SQLSTATE({}), ErrorCode({})", new Object[] { this.poolEntry
            .getPoolName(), this.delegate, exception.getSQLState(), Integer.valueOf(exception.getErrorCode()), exception });
      this.leakTask.cancel();
      this.poolEntry.evict("(connection is broken)");
      this.delegate = ClosedConnection.CLOSED_CONNECTION;
    } 
    return sqle;
  }
  
  final synchronized void untrackStatement(Statement statement) {
    this.openStatements.remove(statement);
  }
  
  final void markCommitStateDirty() {
    if (this.isAutoCommit) {
      this.lastAccess = ClockSource.currentTime();
    } else {
      this.isCommitStateDirty = true;
    } 
  }
  
  void cancelLeakTask() {
    this.leakTask.cancel();
  }
  
  private synchronized <T extends Statement> T trackStatement(T statement) {
    this.openStatements.add(statement);
    return statement;
  }
  
  private synchronized void closeStatements() {
    int size = this.openStatements.size();
    if (size > 0) {
      for (int i = 0; i < size && this.delegate != ClosedConnection.CLOSED_CONNECTION; i++) {
        try {
          Statement ignored = (Statement)this.openStatements.get(i);
          if (ignored != null)
            ignored.close(); 
        } catch (SQLException e) {
          LOGGER.warn("{} - Connection {} marked as broken because of an exception closing open statements during Connection.close()", this.poolEntry
              .getPoolName(), this.delegate);
          this.leakTask.cancel();
          this.poolEntry.evict("(exception closing Statements during Connection.close())");
          this.delegate = ClosedConnection.CLOSED_CONNECTION;
        } 
      } 
      this.openStatements.clear();
    } 
  }
  
  public final void close() throws SQLException {
    closeStatements();
    if (this.delegate != ClosedConnection.CLOSED_CONNECTION) {
      this.leakTask.cancel();
      try {
        if (this.isCommitStateDirty && !this.isAutoCommit) {
          this.delegate.rollback();
          this.lastAccess = ClockSource.currentTime();
          LOGGER.debug("{} - Executed rollback on connection {} due to dirty commit state on close().", this.poolEntry.getPoolName(), this.delegate);
        } 
        if (this.dirtyBits != 0) {
          this.poolEntry.resetConnectionState(this, this.dirtyBits);
          this.lastAccess = ClockSource.currentTime();
        } 
        this.delegate.clearWarnings();
      } catch (SQLException e) {
        if (!this.poolEntry.isMarkedEvicted())
          throw checkException(e); 
      } finally {
        this.delegate = ClosedConnection.CLOSED_CONNECTION;
        this.poolEntry.recycle(this.lastAccess);
      } 
    } 
  }
  
  public boolean isClosed() throws SQLException {
    return (this.delegate == ClosedConnection.CLOSED_CONNECTION);
  }
  
  public Statement createStatement() throws SQLException {
    return ProxyFactory.getProxyStatement(this, trackStatement(this.delegate.createStatement()));
  }
  
  public Statement createStatement(int resultSetType, int concurrency) throws SQLException {
    return ProxyFactory.getProxyStatement(this, trackStatement(this.delegate.createStatement(resultSetType, concurrency)));
  }
  
  public Statement createStatement(int resultSetType, int concurrency, int holdability) throws SQLException {
    return ProxyFactory.getProxyStatement(this, trackStatement(this.delegate.createStatement(resultSetType, concurrency, holdability)));
  }
  
  public CallableStatement prepareCall(String sql) throws SQLException {
    return ProxyFactory.getProxyCallableStatement(this, trackStatement(this.delegate.prepareCall(sql)));
  }
  
  public CallableStatement prepareCall(String sql, int resultSetType, int concurrency) throws SQLException {
    return ProxyFactory.getProxyCallableStatement(this, trackStatement(this.delegate.prepareCall(sql, resultSetType, concurrency)));
  }
  
  public CallableStatement prepareCall(String sql, int resultSetType, int concurrency, int holdability) throws SQLException {
    return ProxyFactory.getProxyCallableStatement(this, trackStatement(this.delegate.prepareCall(sql, resultSetType, concurrency, holdability)));
  }
  
  public PreparedStatement prepareStatement(String sql) throws SQLException {
    return ProxyFactory.getProxyPreparedStatement(this, trackStatement(this.delegate.prepareStatement(sql)));
  }
  
  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
    return ProxyFactory.getProxyPreparedStatement(this, trackStatement(this.delegate.prepareStatement(sql, autoGeneratedKeys)));
  }
  
  public PreparedStatement prepareStatement(String sql, int resultSetType, int concurrency) throws SQLException {
    return ProxyFactory.getProxyPreparedStatement(this, trackStatement(this.delegate.prepareStatement(sql, resultSetType, concurrency)));
  }
  
  public PreparedStatement prepareStatement(String sql, int resultSetType, int concurrency, int holdability) throws SQLException {
    return ProxyFactory.getProxyPreparedStatement(this, trackStatement(this.delegate.prepareStatement(sql, resultSetType, concurrency, holdability)));
  }
  
  public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
    return ProxyFactory.getProxyPreparedStatement(this, trackStatement(this.delegate.prepareStatement(sql, columnIndexes)));
  }
  
  public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
    return ProxyFactory.getProxyPreparedStatement(this, trackStatement(this.delegate.prepareStatement(sql, columnNames)));
  }
  
  public DatabaseMetaData getMetaData() throws SQLException {
    markCommitStateDirty();
    return ProxyFactory.getProxyDatabaseMetaData(this, this.delegate.getMetaData());
  }
  
  public void commit() throws SQLException {
    this.delegate.commit();
    this.isCommitStateDirty = false;
    this.lastAccess = ClockSource.currentTime();
  }
  
  public void rollback() throws SQLException {
    this.delegate.rollback();
    this.isCommitStateDirty = false;
    this.lastAccess = ClockSource.currentTime();
  }
  
  public void rollback(Savepoint savepoint) throws SQLException {
    this.delegate.rollback(savepoint);
    this.isCommitStateDirty = false;
    this.lastAccess = ClockSource.currentTime();
  }
  
  public void setAutoCommit(boolean autoCommit) throws SQLException {
    this.delegate.setAutoCommit(autoCommit);
    this.isAutoCommit = autoCommit;
    this.dirtyBits |= 0x2;
  }
  
  public void setReadOnly(boolean readOnly) throws SQLException {
    this.delegate.setReadOnly(readOnly);
    this.isReadOnly = readOnly;
    this.isCommitStateDirty = false;
    this.dirtyBits |= 0x1;
  }
  
  public void setTransactionIsolation(int level) throws SQLException {
    this.delegate.setTransactionIsolation(level);
    this.transactionIsolation = level;
    this.dirtyBits |= 0x4;
  }
  
  public void setCatalog(String catalog) throws SQLException {
    this.delegate.setCatalog(catalog);
    this.dbcatalog = catalog;
    this.dirtyBits |= 0x8;
  }
  
  public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
    this.delegate.setNetworkTimeout(executor, milliseconds);
    this.networkTimeout = milliseconds;
    this.dirtyBits |= 0x10;
  }
  
  public void setSchema(String schema) throws SQLException {
    this.delegate.setSchema(schema);
    this.dbschema = schema;
    this.dirtyBits |= 0x20;
  }
  
  public final boolean isWrapperFor(Class<?> iface) throws SQLException {
    return (iface.isInstance(this.delegate) || (this.delegate != null && this.delegate.isWrapperFor(iface)));
  }
  
  public final <T> T unwrap(Class<T> iface) throws SQLException {
    if (iface.isInstance(this.delegate))
      return (T)this.delegate; 
    if (this.delegate != null)
      return this.delegate.unwrap(iface); 
    throw new SQLException("Wrapped connection is not an instance of " + iface);
  }
  
  private static final class ClosedConnection {
    static final Connection CLOSED_CONNECTION = getClosedConnection();
    
    private static Connection getClosedConnection() {
      InvocationHandler handler = (proxy, method, args) -> {
          String methodName = method.getName();
          if ("isClosed".equals(methodName))
            return Boolean.TRUE; 
          if ("isValid".equals(methodName))
            return Boolean.FALSE; 
          if ("abort".equals(methodName))
            return void.class; 
          if ("close".equals(methodName))
            return void.class; 
          if ("toString".equals(methodName))
            return ClosedConnection.class.getCanonicalName(); 
          throw new SQLException("Connection is closed");
        };
      return (Connection)Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class[] { Connection.class }, handler);
    }
  }
}
