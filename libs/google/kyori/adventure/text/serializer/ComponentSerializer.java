package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer;

import me.syncwrld.booter.libs.jtann.ApiStatus.ScheduledForRemoval;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface ComponentSerializer<I extends me.syncwrld.booter.libs.google.kyori.adventure.text.Component, O extends me.syncwrld.booter.libs.google.kyori.adventure.text.Component, R> extends ComponentEncoder<I, R>, ComponentDecoder<R, O> {
  @NotNull
  O deserialize(@NotNull R paramR);
  
  @Deprecated
  @ScheduledForRemoval(inVersion = "5.0.0")
  @Contract(value = "!null -> !null; null -> null", pure = true)
  @Nullable
  default O deseializeOrNull(@Nullable R input) {
    return super.deserializeOrNull(input);
  }
  
  @Contract(value = "!null -> !null; null -> null", pure = true)
  @Nullable
  default O deserializeOrNull(@Nullable R input) {
    return super.deserializeOr(input, null);
  }
  
  @Contract(value = "!null, _ -> !null; null, _ -> param2", pure = true)
  @Nullable
  default O deserializeOr(@Nullable R input, @Nullable O fallback) {
    return super.deserializeOr(input, fallback);
  }
  
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
