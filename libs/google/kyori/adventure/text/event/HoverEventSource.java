package me.syncwrld.booter.libs.google.kyori.adventure.text.event;

import java.util.function.UnaryOperator;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface HoverEventSource<V> {
  @Nullable
  static <V> HoverEvent<V> unbox(@Nullable HoverEventSource<V> source) {
    return (source != null) ? source.asHoverEvent() : null;
  }
  
  @NotNull
  default HoverEvent<V> asHoverEvent() {
    return asHoverEvent(UnaryOperator.identity());
  }
  
  @NotNull
  HoverEvent<V> asHoverEvent(@NotNull UnaryOperator<V> paramUnaryOperator);
}
