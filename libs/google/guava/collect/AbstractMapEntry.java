package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Map;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Objects;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractMapEntry<K, V> implements Map.Entry<K, V> {
  @ParametricNullness
  public abstract K getKey();
  
  @ParametricNullness
  public abstract V getValue();
  
  @ParametricNullness
  public V setValue(@ParametricNullness V value) {
    throw new UnsupportedOperationException();
  }
  
  public boolean equals(@CheckForNull Object object) {
    if (object instanceof Map.Entry) {
      Map.Entry<?, ?> that = (Map.Entry<?, ?>)object;
      return (Objects.equal(getKey(), that.getKey()) && 
        Objects.equal(getValue(), that.getValue()));
    } 
    return false;
  }
  
  public int hashCode() {
    K k = getKey();
    V v = getValue();
    return ((k == null) ? 0 : k.hashCode()) ^ ((v == null) ? 0 : v.hashCode());
  }
  
  public String toString() {
    return (new StringBuilder()).append(getKey()).append("=").append(getValue()).toString();
  }
}
