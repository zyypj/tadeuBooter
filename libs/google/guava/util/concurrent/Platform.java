package me.syncwrld.booter.libs.google.guava.util.concurrent;

import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
final class Platform {
  static boolean isInstanceOfThrowableClass(@CheckForNull Throwable t, Class<? extends Throwable> expectedClass) {
    return expectedClass.isInstance(t);
  }
  
  static void restoreInterruptIfIsInterruptedException(Throwable t) {
    Preconditions.checkNotNull(t);
    if (t instanceof InterruptedException)
      Thread.currentThread().interrupt(); 
  }
}
