package me.syncwrld.booter.libs.google.guava.util.concurrent;

import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@FunctionalInterface
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface AsyncFunction<I, O> {
  ListenableFuture<O> apply(@ParametricNullness I paramI) throws Exception;
}
