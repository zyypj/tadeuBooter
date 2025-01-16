package me.syncwrld.booter.libs.google.kyori.adventure.text.format;

import java.util.EnumMap;
import java.util.Map;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.ClickEvent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.HoverEvent;
import me.syncwrld.booter.libs.jtann.ApiStatus.NonExtendable;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

@NonExtendable
public interface StyleGetter {
  @Nullable
  Key font();
  
  @Nullable
  TextColor color();
  
  default boolean hasDecoration(@NotNull TextDecoration decoration) {
    return (decoration(decoration) == TextDecoration.State.TRUE);
  }
  
  TextDecoration.State decoration(@NotNull TextDecoration paramTextDecoration);
  
  @NotNull
  default Map<TextDecoration, TextDecoration.State> decorations() {
    Map<TextDecoration, TextDecoration.State> decorations = new EnumMap<>(TextDecoration.class);
    for (int i = 0, length = DecorationMap.DECORATIONS.length; i < length; i++) {
      TextDecoration decoration = DecorationMap.DECORATIONS[i];
      TextDecoration.State value = decoration(decoration);
      decorations.put(decoration, value);
    } 
    return decorations;
  }
  
  @Nullable
  ClickEvent clickEvent();
  
  @Nullable
  HoverEvent<?> hoverEvent();
  
  @Nullable
  String insertion();
}
