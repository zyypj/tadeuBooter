package me.syncwrld.booter.libs.google.guava.util.concurrent;

import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class SettableFuture<V> extends AbstractFuture.TrustedFuture<V> {
  public static <V> SettableFuture<V> create() {
    return new SettableFuture<>();
  }
  
  @CanIgnoreReturnValue
  public boolean set(@ParametricNullness V value) {
    return super.set(value);
  }
  
  @CanIgnoreReturnValue
  public boolean setException(Throwable throwable) {
    return super.setException(throwable);
  }
  
  @CanIgnoreReturnValue
  public boolean setFuture(ListenableFuture<? extends V> future) {
    return super.setFuture(future);
  }
}
