package me.syncwrld.booter.libs.google.guava.io;

import java.io.Flushable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public final class Flushables {
  private static final Logger logger = Logger.getLogger(Flushables.class.getName());
  
  public static void flush(Flushable flushable, boolean swallowIOException) throws IOException {
    try {
      flushable.flush();
    } catch (IOException e) {
      if (swallowIOException) {
        logger.log(Level.WARNING, "IOException thrown while flushing Flushable.", e);
      } else {
        throw e;
      } 
    } 
  }
  
  @Beta
  public static void flushQuietly(Flushable flushable) {
    try {
      flush(flushable, true);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "IOException should not have been thrown.", e);
    } 
  }
}
