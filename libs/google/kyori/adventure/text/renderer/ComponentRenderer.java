package me.syncwrld.booter.libs.google.kyori.adventure.text.renderer;

import java.util.function.Function;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.jtann.NotNull;

public interface ComponentRenderer<C> {
  @NotNull
  Component render(@NotNull Component paramComponent, @NotNull C paramC);
  
  default <T> ComponentRenderer<T> mapContext(Function<T, C> transformer) {
    return (component, ctx) -> render(component, transformer.apply(ctx));
  }
}
