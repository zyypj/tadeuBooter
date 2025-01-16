package me.syncwrld.booter.libs.google.guava.collect;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
final class ImmutableMapKeySet<K, V> extends IndexedImmutableSet<K> {
  private final ImmutableMap<K, V> map;
  
  ImmutableMapKeySet(ImmutableMap<K, V> map) {
    this.map = map;
  }
  
  public int size() {
    return this.map.size();
  }
  
  public UnmodifiableIterator<K> iterator() {
    return this.map.keyIterator();
  }
  
  public Spliterator<K> spliterator() {
    return this.map.keySpliterator();
  }
  
  public boolean contains(@CheckForNull Object object) {
    return this.map.containsKey(object);
  }
  
  K get(int index) {
    return (K)((Map.Entry)this.map.entrySet().asList().get(index)).getKey();
  }
  
  public void forEach(Consumer<? super K> action) {
    Preconditions.checkNotNull(action);
    this.map.forEach((k, v) -> action.accept(k));
  }
  
  boolean isPartialView() {
    return true;
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  Object writeReplace() {
    return super.writeReplace();
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  private static class KeySetSerializedForm<K> implements Serializable {
    final ImmutableMap<K, ?> map;
    
    private static final long serialVersionUID = 0L;
    
    KeySetSerializedForm(ImmutableMap<K, ?> map) {
      this.map = map;
    }
    
    Object readResolve() {
      return this.map.keySet();
    }
  }
}
