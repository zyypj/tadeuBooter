package me.syncwrld.booter.libs.google.guava.collect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
@J2ktIncompatible
public final class EnumBiMap<K extends Enum<K>, V extends Enum<V>> extends AbstractBiMap<K, V> {
  transient Class<K> keyTypeOrObjectUnderJ2cl;
  
  transient Class<V> valueTypeOrObjectUnderJ2cl;
  
  @GwtIncompatible
  private static final long serialVersionUID = 0L;
  
  public static <K extends Enum<K>, V extends Enum<V>> EnumBiMap<K, V> create(Class<K> keyType, Class<V> valueType) {
    return new EnumBiMap<>(keyType, valueType);
  }
  
  public static <K extends Enum<K>, V extends Enum<V>> EnumBiMap<K, V> create(Map<K, V> map) {
    EnumBiMap<K, V> bimap = create(inferKeyTypeOrObjectUnderJ2cl(map), inferValueTypeOrObjectUnderJ2cl(map));
    bimap.putAll(map);
    return bimap;
  }
  
  private EnumBiMap(Class<K> keyTypeOrObjectUnderJ2cl, Class<V> valueTypeOrObjectUnderJ2cl) {
    super(new EnumMap<>(keyTypeOrObjectUnderJ2cl), (Map)new EnumMap<>(valueTypeOrObjectUnderJ2cl));
    this.keyTypeOrObjectUnderJ2cl = keyTypeOrObjectUnderJ2cl;
    this.valueTypeOrObjectUnderJ2cl = valueTypeOrObjectUnderJ2cl;
  }
  
  static <K extends Enum<K>> Class<K> inferKeyTypeOrObjectUnderJ2cl(Map<K, ?> map) {
    if (map instanceof EnumBiMap)
      return ((EnumBiMap)map).keyTypeOrObjectUnderJ2cl; 
    if (map instanceof EnumHashBiMap)
      return ((EnumHashBiMap)map).keyTypeOrObjectUnderJ2cl; 
    Preconditions.checkArgument(!map.isEmpty());
    return Platform.getDeclaringClassOrObjectForJ2cl((K)map.keySet().iterator().next());
  }
  
  private static <V extends Enum<V>> Class<V> inferValueTypeOrObjectUnderJ2cl(Map<?, V> map) {
    if (map instanceof EnumBiMap)
      return ((EnumBiMap)map).valueTypeOrObjectUnderJ2cl; 
    Preconditions.checkArgument(!map.isEmpty());
    return Platform.getDeclaringClassOrObjectForJ2cl((V)map.values().iterator().next());
  }
  
  @GwtIncompatible
  public Class<K> keyType() {
    return this.keyTypeOrObjectUnderJ2cl;
  }
  
  @GwtIncompatible
  public Class<V> valueType() {
    return this.valueTypeOrObjectUnderJ2cl;
  }
  
  K checkKey(K key) {
    return (K)Preconditions.checkNotNull(key);
  }
  
  V checkValue(V value) {
    return (V)Preconditions.checkNotNull(value);
  }
  
  @GwtIncompatible
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    stream.writeObject(this.keyTypeOrObjectUnderJ2cl);
    stream.writeObject(this.valueTypeOrObjectUnderJ2cl);
    Serialization.writeMap(this, stream);
  }
  
  @GwtIncompatible
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    this.keyTypeOrObjectUnderJ2cl = (Class<K>)Objects.<Object>requireNonNull(stream.readObject());
    this.valueTypeOrObjectUnderJ2cl = (Class<V>)Objects.<Object>requireNonNull(stream.readObject());
    setDelegates(new EnumMap<>(this.keyTypeOrObjectUnderJ2cl), (Map)new EnumMap<>(this.valueTypeOrObjectUnderJ2cl));
    Serialization.populateMap(this, stream);
  }
}
