package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Map;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Objects;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingMapEntry<K, V> extends ForwardingObject implements Map.Entry<K, V> {
  @ParametricNullness
  public K getKey() {
    return delegate().getKey();
  }
  
  @ParametricNullness
  public V getValue() {
    return delegate().getValue();
  }
  
  @ParametricNullness
  public V setValue(@ParametricNullness V value) {
    return delegate().setValue(value);
  }
  
  public boolean equals(@CheckForNull Object object) {
    return delegate().equals(object);
  }
  
  public int hashCode() {
    return delegate().hashCode();
  }
  
  protected boolean standardEquals(@CheckForNull Object object) {
    if (object instanceof Map.Entry) {
      Map.Entry<?, ?> that = (Map.Entry<?, ?>)object;
      return (Objects.equal(getKey(), that.getKey()) && 
        Objects.equal(getValue(), that.getValue()));
    } 
    return false;
  }
  
  protected int standardHashCode() {
    K k = getKey();
    V v = getValue();
    return ((k == null) ? 0 : k.hashCode()) ^ ((v == null) ? 0 : v.hashCode());
  }
  
  protected String standardToString() {
    return (new StringBuilder()).append(getKey()).append("=").append(getValue()).toString();
  }
  
  protected abstract Map.Entry<K, V> delegate();
}
