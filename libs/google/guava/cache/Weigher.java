package me.syncwrld.booter.libs.google.guava.cache;

import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@FunctionalInterface
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface Weigher<K, V> {
  int weigh(K paramK, V paramV);
}
