package me.syncwrld.booter.libs.google.guava.collect;

import java.io.Serializable;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true)
class ImmutableEntry<K, V> extends AbstractMapEntry<K, V> implements Serializable {
  @ParametricNullness
  final K key;
  
  @ParametricNullness
  final V value;
  
  private static final long serialVersionUID = 0L;
  
  ImmutableEntry(@ParametricNullness K key, @ParametricNullness V value) {
    this.key = key;
    this.value = value;
  }
  
  @ParametricNullness
  public final K getKey() {
    return this.key;
  }
  
  @ParametricNullness
  public final V getValue() {
    return this.value;
  }
  
  @ParametricNullness
  public final V setValue(@ParametricNullness V value) {
    throw new UnsupportedOperationException();
  }
}
