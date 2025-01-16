package me.syncwrld.booter.libs.google.guava.collect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumMap;
import java.util.HashMap;
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
public final class EnumHashBiMap<K extends Enum<K>, V> extends AbstractBiMap<K, V> {
  transient Class<K> keyTypeOrObjectUnderJ2cl;
  
  @GwtIncompatible
  private static final long serialVersionUID = 0L;
  
  public static <K extends Enum<K>, V> EnumHashBiMap<K, V> create(Class<K> keyType) {
    return new EnumHashBiMap<>(keyType);
  }
  
  public static <K extends Enum<K>, V> EnumHashBiMap<K, V> create(Map<K, ? extends V> map) {
    EnumHashBiMap<K, V> bimap = create(EnumBiMap.inferKeyTypeOrObjectUnderJ2cl(map));
    bimap.putAll(map);
    return bimap;
  }
  
  private EnumHashBiMap(Class<K> keyType) {
    super(new EnumMap<>(keyType), new HashMap<>());
    this.keyTypeOrObjectUnderJ2cl = keyType;
  }
  
  K checkKey(K key) {
    return (K)Preconditions.checkNotNull(key);
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public V put(K key, @ParametricNullness V value) {
    return super.put(key, value);
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public V forcePut(K key, @ParametricNullness V value) {
    return super.forcePut(key, value);
  }
  
  @GwtIncompatible
  public Class<K> keyType() {
    return this.keyTypeOrObjectUnderJ2cl;
  }
  
  @GwtIncompatible
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    stream.writeObject(this.keyTypeOrObjectUnderJ2cl);
    Serialization.writeMap(this, stream);
  }
  
  @GwtIncompatible
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    this.keyTypeOrObjectUnderJ2cl = (Class<K>)Objects.<Object>requireNonNull(stream.readObject());
    setDelegates(new EnumMap<>(this.keyTypeOrObjectUnderJ2cl), new HashMap<>());
    Serialization.populateMap(this, stream);
  }
}
