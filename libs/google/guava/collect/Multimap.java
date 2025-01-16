package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.CompatibleWith;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@DoNotMock("Use ImmutableMultimap, HashMultimap, or another implementation")
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface Multimap<K, V> {
  int size();
  
  boolean isEmpty();
  
  boolean containsKey(@CheckForNull @CompatibleWith("K") Object paramObject);
  
  boolean containsValue(@CheckForNull @CompatibleWith("V") Object paramObject);
  
  boolean containsEntry(@CheckForNull @CompatibleWith("K") Object paramObject1, @CheckForNull @CompatibleWith("V") Object paramObject2);
  
  @CanIgnoreReturnValue
  boolean put(@ParametricNullness K paramK, @ParametricNullness V paramV);
  
  @CanIgnoreReturnValue
  boolean remove(@CheckForNull @CompatibleWith("K") Object paramObject1, @CheckForNull @CompatibleWith("V") Object paramObject2);
  
  @CanIgnoreReturnValue
  boolean putAll(@ParametricNullness K paramK, Iterable<? extends V> paramIterable);
  
  @CanIgnoreReturnValue
  boolean putAll(Multimap<? extends K, ? extends V> paramMultimap);
  
  @CanIgnoreReturnValue
  Collection<V> replaceValues(@ParametricNullness K paramK, Iterable<? extends V> paramIterable);
  
  @CanIgnoreReturnValue
  Collection<V> removeAll(@CheckForNull @CompatibleWith("K") Object paramObject);
  
  void clear();
  
  Collection<V> get(@ParametricNullness K paramK);
  
  Set<K> keySet();
  
  Multiset<K> keys();
  
  Collection<V> values();
  
  Collection<Map.Entry<K, V>> entries();
  
  default void forEach(BiConsumer<? super K, ? super V> action) {
    Preconditions.checkNotNull(action);
    entries().forEach(entry -> action.accept(entry.getKey(), entry.getValue()));
  }
  
  Map<K, Collection<V>> asMap();
  
  boolean equals(@CheckForNull Object paramObject);
  
  int hashCode();
}
