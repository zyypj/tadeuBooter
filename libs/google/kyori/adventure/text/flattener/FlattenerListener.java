package me.syncwrld.booter.libs.google.kyori.adventure.text.flattener;

import me.syncwrld.booter.libs.google.kyori.adventure.text.format.Style;
import me.syncwrld.booter.libs.jtann.NotNull;

@FunctionalInterface
public interface FlattenerListener {
  default void pushStyle(@NotNull Style style) {}
  
  void component(@NotNull String paramString);
  
  default boolean shouldContinue() {
    return true;
  }
  
  default void popStyle(@NotNull Style style) {}
}
