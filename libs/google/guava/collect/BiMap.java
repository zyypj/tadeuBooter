package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface BiMap<K, V> extends Map<K, V> {
  @CheckForNull
  @CanIgnoreReturnValue
  V put(@ParametricNullness K paramK, @ParametricNullness V paramV);
  
  @CheckForNull
  @CanIgnoreReturnValue
  V forcePut(@ParametricNullness K paramK, @ParametricNullness V paramV);
  
  void putAll(Map<? extends K, ? extends V> paramMap);
  
  Set<V> values();
  
  BiMap<V, K> inverse();
}
