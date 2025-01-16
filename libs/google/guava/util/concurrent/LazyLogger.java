package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.logging.Logger;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class LazyLogger {
  private final String loggerName;
  
  private volatile Logger logger;
  
  LazyLogger(Class<?> ownerOfLogger) {
    this.loggerName = ownerOfLogger.getName();
  }
  
  Logger get() {
    Logger local = this.logger;
    if (local != null)
      return local; 
    synchronized (this) {
      local = this.logger;
      if (local != null)
        return local; 
      return this.logger = Logger.getLogger(this.loggerName);
    } 
  }
}
