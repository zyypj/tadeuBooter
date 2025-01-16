package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.concurrent.ScheduledFuture;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface ListenableScheduledFuture<V> extends ScheduledFuture<V>, ListenableFuture<V> {}
