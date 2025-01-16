package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Map;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
abstract class ArrayListMultimapGwtSerializationDependencies<K, V> extends AbstractListMultimap<K, V> {
  ArrayListMultimapGwtSerializationDependencies(Map<K, Collection<V>> map) {
    super(map);
  }
}
