package me.syncwrld.booter.libs.google.guava.escape;

import java.util.Objects;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
final class Platform {
  static char[] charBufferFromThreadLocal() {
    return Objects.<char[]>requireNonNull(DEST_TL.get());
  }
  
  private static final ThreadLocal<char[]> DEST_TL = new ThreadLocal<char[]>() {
      protected char[] initialValue() {
        return new char[1024];
      }
    };
}
