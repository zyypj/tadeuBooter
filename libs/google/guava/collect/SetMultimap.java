package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface SetMultimap<K, V> extends Multimap<K, V> {
  Set<V> get(@ParametricNullness K paramK);
  
  @CanIgnoreReturnValue
  Set<V> removeAll(@CheckForNull Object paramObject);
  
  @CanIgnoreReturnValue
  Set<V> replaceValues(@ParametricNullness K paramK, Iterable<? extends V> paramIterable);
  
  Set<Map.Entry<K, V>> entries();
  
  Map<K, Collection<V>> asMap();
  
  boolean equals(@CheckForNull Object paramObject);
}
