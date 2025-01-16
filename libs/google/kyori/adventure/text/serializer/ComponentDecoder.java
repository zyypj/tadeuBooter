package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer;

import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface ComponentDecoder<S, O extends me.syncwrld.booter.libs.google.kyori.adventure.text.Component> {
  @NotNull
  O deserialize(@NotNull S paramS);
  
  @Contract(value = "!null -> !null; null -> null", pure = true)
  @Nullable
  default O deserializeOrNull(@Nullable S input) {
    return deserializeOr(input, null);
  }
  
  @Contract(value = "!null, _ -> !null; null, _ -> param2", pure = true)
  @Nullable
  default O deserializeOr(@Nullable S input, @Nullable O fallback) {
    if (input == null)
      return fallback; 
    return deserialize(input);
  }
}
