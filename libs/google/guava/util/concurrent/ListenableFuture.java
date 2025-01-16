package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;

@DoNotMock("Use the methods in Futures (like immediateFuture) or SettableFuture")
@ElementTypesAreNonnullByDefault
public interface ListenableFuture<V> extends Future<V> {
  void addListener(Runnable paramRunnable, Executor paramExecutor);
}
