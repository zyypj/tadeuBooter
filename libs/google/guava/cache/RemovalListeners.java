package me.syncwrld.booter.libs.google.guava.cache;

import java.util.concurrent.Executor;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public final class RemovalListeners {
  public static <K, V> RemovalListener<K, V> asynchronous(RemovalListener<K, V> listener, Executor executor) {
    Preconditions.checkNotNull(listener);
    Preconditions.checkNotNull(executor);
    return notification -> executor.execute(());
  }
}
