package me.syncwrld.booter.libs.google.guava.collect;

import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@Deprecated
@ElementTypesAreNonnullByDefault
@GwtCompatible
public class ComputationException extends RuntimeException {
  private static final long serialVersionUID = 0L;
  
  public ComputationException(@CheckForNull Throwable cause) {
    super(cause);
  }
}
