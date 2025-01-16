package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Map;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
abstract class HashMultimapGwtSerializationDependencies<K, V> extends AbstractSetMultimap<K, V> {
  HashMultimapGwtSerializationDependencies(Map<K, Collection<V>> map) {
    super(map);
  }
}
