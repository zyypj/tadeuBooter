package me.syncwrld.booter.libs.google.guava.util.concurrent;

import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@FunctionalInterface
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface AsyncCallable<V> {
  ListenableFuture<V> call() throws Exception;
}
