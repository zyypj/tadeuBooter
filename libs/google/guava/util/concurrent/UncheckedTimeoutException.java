package me.syncwrld.booter.libs.google.guava.util.concurrent;

import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public class UncheckedTimeoutException extends RuntimeException {
  private static final long serialVersionUID = 0L;
  
  public UncheckedTimeoutException() {}
  
  public UncheckedTimeoutException(@CheckForNull String message) {
    super(message);
  }
  
  public UncheckedTimeoutException(@CheckForNull Throwable cause) {
    super(cause);
  }
  
  public UncheckedTimeoutException(@CheckForNull String message, @CheckForNull Throwable cause) {
    super(message, cause);
  }
}
