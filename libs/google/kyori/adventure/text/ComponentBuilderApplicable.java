package me.syncwrld.booter.libs.google.kyori.adventure.text;

import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;

@FunctionalInterface
public interface ComponentBuilderApplicable {
  @Contract(mutates = "param")
  void componentBuilderApply(@NotNull ComponentBuilder<?, ?> paramComponentBuilder);
}
