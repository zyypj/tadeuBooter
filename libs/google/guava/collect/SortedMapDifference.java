package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Map;
import java.util.SortedMap;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface SortedMapDifference<K, V> extends MapDifference<K, V> {
  SortedMap<K, V> entriesOnlyOnLeft();
  
  SortedMap<K, V> entriesOnlyOnRight();
  
  SortedMap<K, V> entriesInCommon();
  
  SortedMap<K, MapDifference.ValueDifference<V>> entriesDiffering();
}
