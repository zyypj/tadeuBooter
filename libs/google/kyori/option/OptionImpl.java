package me.syncwrld.booter.libs.google.kyori.option;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class OptionImpl<V> implements Option<V> {
  private static final Set<String> KNOWN_KEYS = ConcurrentHashMap.newKeySet();
  
  private final String id;
  
  private final Class<V> type;
  
  @Nullable
  private final V defaultValue;
  
  OptionImpl(@NotNull String id, @NotNull Class<V> type, @Nullable V defaultValue) {
    this.id = id;
    this.type = type;
    this.defaultValue = defaultValue;
  }
  
  static <T> Option<T> option(String id, Class<T> type, @Nullable T defaultValue) {
    if (!KNOWN_KEYS.add(id))
      throw new IllegalStateException("Key " + id + " has already been used. Option keys must be unique."); 
    return new OptionImpl<>(
        Objects.<String>requireNonNull(id, "id"), 
        Objects.<Class<T>>requireNonNull(type, "type"), defaultValue);
  }
  
  @NotNull
  public String id() {
    return this.id;
  }
  
  @NotNull
  public Class<V> type() {
    return this.type;
  }
  
  @Nullable
  public V defaultValue() {
    return this.defaultValue;
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (other == null || getClass() != other.getClass())
      return false; 
    OptionImpl<?> that = (OptionImpl)other;
    return (Objects.equals(this.id, that.id) && 
      Objects.equals(this.type, that.type));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.id, this.type });
  }
  
  public String toString() {
    return getClass().getSimpleName() + "{id=" + this.id + ",type=" + this.type + ",defaultValue=" + this.defaultValue + '}';
  }
}
