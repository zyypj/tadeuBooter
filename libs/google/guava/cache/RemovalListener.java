package me.syncwrld.booter.libs.google.guava.cache;

import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@FunctionalInterface
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface RemovalListener<K, V> {
  void onRemoval(RemovalNotification<K, V> paramRemovalNotification);
}
