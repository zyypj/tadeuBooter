package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface ListMultimap<K, V> extends Multimap<K, V> {
  List<V> get(@ParametricNullness K paramK);
  
  @CanIgnoreReturnValue
  List<V> removeAll(@CheckForNull Object paramObject);
  
  @CanIgnoreReturnValue
  List<V> replaceValues(@ParametricNullness K paramK, Iterable<? extends V> paramIterable);
  
  Map<K, Collection<V>> asMap();
  
  boolean equals(@CheckForNull Object paramObject);
}
