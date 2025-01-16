package me.syncwrld.booter.libs.google.kyori.adventure.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public final class Index<K, V> {
  private final Map<K, V> keyToValue;
  
  private final Map<V, K> valueToKey;
  
  private Index(Map<K, V> keyToValue, Map<V, K> valueToKey) {
    this.keyToValue = keyToValue;
    this.valueToKey = valueToKey;
  }
  
  @NotNull
  public static <K, V extends Enum<V>> Index<K, V> create(Class<V> type, @NotNull Function<? super V, ? extends K> keyFunction) {
    return create(type, keyFunction, type.getEnumConstants());
  }
  
  @SafeVarargs
  @NotNull
  public static <K, V extends Enum<V>> Index<K, V> create(Class<V> type, @NotNull Function<? super V, ? extends K> keyFunction, @NotNull V... values) {
    return create(values, length -> new EnumMap<>(type), keyFunction);
  }
  
  @SafeVarargs
  @NotNull
  public static <K, V> Index<K, V> create(@NotNull Function<? super V, ? extends K> keyFunction, @NotNull V... values) {
    return create(values, HashMap::new, keyFunction);
  }
  
  @NotNull
  public static <K, V> Index<K, V> create(@NotNull Function<? super V, ? extends K> keyFunction, @NotNull List<V> constants) {
    return create(constants, HashMap::new, keyFunction);
  }
  
  @NotNull
  private static <K, V> Index<K, V> create(V[] values, IntFunction<Map<V, K>> valueToKeyFactory, @NotNull Function<? super V, ? extends K> keyFunction) {
    return create(Arrays.asList(values), valueToKeyFactory, keyFunction);
  }
  
  @NotNull
  private static <K, V> Index<K, V> create(List<V> values, IntFunction<Map<V, K>> valueToKeyFactory, @NotNull Function<? super V, ? extends K> keyFunction) {
    int length = values.size();
    Map<K, V> keyToValue = new HashMap<>(length);
    Map<V, K> valueToKey = valueToKeyFactory.apply(length);
    for (int i = 0; i < length; i++) {
      V value = values.get(i);
      K key = keyFunction.apply(value);
      if (keyToValue.putIfAbsent(key, value) != null)
        throw new IllegalStateException(String.format("Key %s already mapped to value %s", new Object[] { key, keyToValue.get(key) })); 
      if (valueToKey.putIfAbsent(value, key) != null)
        throw new IllegalStateException(String.format("Value %s already mapped to key %s", new Object[] { value, valueToKey.get(value) })); 
    } 
    return new Index<>(Collections.unmodifiableMap(keyToValue), Collections.unmodifiableMap(valueToKey));
  }
  
  @NotNull
  public Set<K> keys() {
    return Collections.unmodifiableSet(this.keyToValue.keySet());
  }
  
  @Nullable
  public K key(@NotNull V value) {
    return this.valueToKey.get(value);
  }
  
  @NotNull
  public K keyOrThrow(@NotNull V value) {
    K key = key(value);
    if (key == null)
      throw new NoSuchElementException("There is no key for value " + value); 
    return key;
  }
  
  @Contract("_, null -> null; _, !null -> !null")
  public K keyOr(@NotNull V value, @Nullable K defaultKey) {
    K key = key(value);
    return (key == null) ? defaultKey : key;
  }
  
  @NotNull
  public Set<V> values() {
    return Collections.unmodifiableSet(this.valueToKey.keySet());
  }
  
  @Nullable
  public V value(@NotNull K key) {
    return this.keyToValue.get(key);
  }
  
  @NotNull
  public V valueOrThrow(@NotNull K key) {
    V value = value(key);
    if (value == null)
      throw new NoSuchElementException("There is no value for key " + key); 
    return value;
  }
  
  @Contract("_, null -> null; _, !null -> !null")
  public V valueOr(@NotNull K key, @Nullable V defaultValue) {
    V value = value(key);
    return (value == null) ? defaultValue : value;
  }
  
  @NotNull
  public Map<K, V> keyToValue() {
    return Collections.unmodifiableMap(this.keyToValue);
  }
  
  @NotNull
  public Map<V, K> valueToKey() {
    return Collections.unmodifiableMap(this.valueToKey);
  }
}
