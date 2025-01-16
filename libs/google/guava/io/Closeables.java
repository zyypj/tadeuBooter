package me.syncwrld.booter.libs.google.guava.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public final class Closeables {
  @VisibleForTesting
  static final Logger logger = Logger.getLogger(Closeables.class.getName());
  
  public static void close(@CheckForNull Closeable closeable, boolean swallowIOException) throws IOException {
    if (closeable == null)
      return; 
    try {
      closeable.close();
    } catch (IOException e) {
      if (swallowIOException) {
        logger.log(Level.WARNING, "IOException thrown while closing Closeable.", e);
      } else {
        throw e;
      } 
    } 
  }
  
  public static void closeQuietly(@CheckForNull InputStream inputStream) {
    try {
      close(inputStream, true);
    } catch (IOException impossible) {
      throw new AssertionError(impossible);
    } 
  }
  
  public static void closeQuietly(@CheckForNull Reader reader) {
    try {
      close(reader, true);
    } catch (IOException impossible) {
      throw new AssertionError(impossible);
    } 
  }
}
