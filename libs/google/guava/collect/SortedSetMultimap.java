package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface SortedSetMultimap<K, V> extends SetMultimap<K, V> {
  SortedSet<V> get(@ParametricNullness K paramK);
  
  @CanIgnoreReturnValue
  SortedSet<V> removeAll(@CheckForNull Object paramObject);
  
  @CanIgnoreReturnValue
  SortedSet<V> replaceValues(@ParametricNullness K paramK, Iterable<? extends V> paramIterable);
  
  Map<K, Collection<V>> asMap();
  
  @CheckForNull
  Comparator<? super V> valueComparator();
}
