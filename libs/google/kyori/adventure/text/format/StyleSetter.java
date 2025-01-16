package me.syncwrld.booter.libs.google.kyori.adventure.text.format;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.ClickEvent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.HoverEventSource;
import me.syncwrld.booter.libs.jtann.ApiStatus.NonExtendable;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

@NonExtendable
public interface StyleSetter<T extends StyleSetter<?>> {
  @NotNull
  T font(@Nullable Key paramKey);
  
  @NotNull
  T color(@Nullable TextColor paramTextColor);
  
  @NotNull
  T colorIfAbsent(@Nullable TextColor paramTextColor);
  
  @NotNull
  default T decorate(@NotNull TextDecoration decoration) {
    return decoration(decoration, TextDecoration.State.TRUE);
  }
  
  @NotNull
  T decorate(@NotNull TextDecoration... decorations) {
    Map<TextDecoration, TextDecoration.State> map = new EnumMap<>(TextDecoration.class);
    for (int i = 0, length = decorations.length; i < length; i++)
      map.put(decorations[i], TextDecoration.State.TRUE); 
    return decorations(map);
  }
  
  @NotNull
  default T decoration(@NotNull TextDecoration decoration, boolean flag) {
    return decoration(decoration, TextDecoration.State.byBoolean(flag));
  }
  
  @NotNull
  T decoration(@NotNull TextDecoration paramTextDecoration, TextDecoration.State paramState);
  
  @NotNull
  T decorationIfAbsent(@NotNull TextDecoration paramTextDecoration, TextDecoration.State paramState);
  
  @NotNull
  T decorations(@NotNull Map<TextDecoration, TextDecoration.State> paramMap);
  
  @NotNull
  default T decorations(@NotNull Set<TextDecoration> decorations, boolean flag) {
    return decorations((Map<TextDecoration, TextDecoration.State>)decorations.stream().collect(Collectors.toMap(Function.identity(), decoration -> TextDecoration.State.byBoolean(flag))));
  }
  
  @NotNull
  T clickEvent(@Nullable ClickEvent paramClickEvent);
  
  @NotNull
  T hoverEvent(@Nullable HoverEventSource<?> paramHoverEventSource);
  
  @NotNull
  T insertion(@Nullable String paramString);
}
