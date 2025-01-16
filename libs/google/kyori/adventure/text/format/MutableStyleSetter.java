package me.syncwrld.booter.libs.google.kyori.adventure.text.format;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import me.syncwrld.booter.libs.jtann.ApiStatus.NonExtendable;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;

@NonExtendable
public interface MutableStyleSetter<T extends MutableStyleSetter<?>> extends StyleSetter<T> {
  @Contract("_ -> this")
  @NotNull
  T decorate(@NotNull TextDecoration... decorations) {
    for (int i = 0, length = decorations.length; i < length; i++)
      decorate(decorations[i]); 
    return (T)this;
  }
  
  @Contract("_ -> this")
  @NotNull
  default T decorations(@NotNull Map<TextDecoration, TextDecoration.State> decorations) {
    Objects.requireNonNull(decorations, "decorations");
    for (Map.Entry<TextDecoration, TextDecoration.State> entry : decorations.entrySet())
      decoration(entry.getKey(), entry.getValue()); 
    return (T)this;
  }
  
  @Contract("_, _ -> this")
  @NotNull
  default T decorations(@NotNull Set<TextDecoration> decorations, boolean flag) {
    TextDecoration.State state = TextDecoration.State.byBoolean(flag);
    decorations.forEach(decoration -> decoration(decoration, state));
    return (T)this;
  }
}
