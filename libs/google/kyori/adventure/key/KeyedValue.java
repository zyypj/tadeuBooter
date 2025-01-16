package me.syncwrld.booter.libs.google.kyori.adventure.key;

import java.util.Objects;
import me.syncwrld.booter.libs.jtann.ApiStatus.ScheduledForRemoval;
import me.syncwrld.booter.libs.jtann.NotNull;

public interface KeyedValue<T> extends Keyed {
  @NotNull
  static <T> KeyedValue<T> keyedValue(@NotNull Key key, @NotNull T value) {
    return new KeyedValueImpl<>(key, Objects.requireNonNull(value, "value"));
  }
  
  @Deprecated
  @ScheduledForRemoval(inVersion = "5.0.0")
  @NotNull
  static <T> KeyedValue<T> of(@NotNull Key key, @NotNull T value) {
    return new KeyedValueImpl<>(key, Objects.requireNonNull(value, "value"));
  }
  
  @NotNull
  T value();
}
