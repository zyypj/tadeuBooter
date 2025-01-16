package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface RowSortedTable<R, C, V> extends Table<R, C, V> {
  SortedSet<R> rowKeySet();
  
  SortedMap<R, Map<C, V>> rowMap();
}
