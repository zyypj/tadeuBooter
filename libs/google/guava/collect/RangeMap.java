package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Map;
import java.util.function.BiFunction;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@DoNotMock("Use ImmutableRangeMap or TreeRangeMap")
@ElementTypesAreNonnullByDefault
@GwtIncompatible
public interface RangeMap<K extends Comparable, V> {
  @CheckForNull
  V get(K paramK);
  
  @CheckForNull
  Map.Entry<Range<K>, V> getEntry(K paramK);
  
  Range<K> span();
  
  void put(Range<K> paramRange, V paramV);
  
  void putCoalescing(Range<K> paramRange, V paramV);
  
  void putAll(RangeMap<K, ? extends V> paramRangeMap);
  
  void clear();
  
  void remove(Range<K> paramRange);
  
  void merge(Range<K> paramRange, @CheckForNull V paramV, BiFunction<? super V, ? super V, ? extends V> paramBiFunction);
  
  Map<Range<K>, V> asMapOfRanges();
  
  Map<Range<K>, V> asDescendingMapOfRanges();
  
  RangeMap<K, V> subRangeMap(Range<K> paramRange);
  
  boolean equals(@CheckForNull Object paramObject);
  
  int hashCode();
  
  String toString();
}
