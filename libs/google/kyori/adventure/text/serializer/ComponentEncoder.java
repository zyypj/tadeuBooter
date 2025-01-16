package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer;

import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface ComponentEncoder<I extends me.syncwrld.booter.libs.google.kyori.adventure.text.Component, R> {
  @NotNull
  R serialize(@NotNull I paramI);
  
  @Contract(value = "!null -> !null; null -> null", pure = true)
  @Nullable
  default R serializeOrNull(@Nullable I component) {
    return serializeOr(component, null);
  }
  
  @Contract(value = "!null, _ -> !null; null, _ -> param2", pure = true)
  @Nullable
  default R serializeOr(@Nullable I component, @Nullable R fallback) {
    if (component == null)
      return fallback; 
    return serialize(component);
  }
}
