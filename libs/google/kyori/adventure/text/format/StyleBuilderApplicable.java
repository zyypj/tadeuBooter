package me.syncwrld.booter.libs.google.kyori.adventure.text.format;

import me.syncwrld.booter.libs.google.kyori.adventure.text.ComponentBuilder;
import me.syncwrld.booter.libs.google.kyori.adventure.text.ComponentBuilderApplicable;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;

@FunctionalInterface
public interface StyleBuilderApplicable extends ComponentBuilderApplicable {
  @Contract(mutates = "param")
  void styleApply(Style.Builder paramBuilder);
  
  default void componentBuilderApply(@NotNull ComponentBuilder<?, ?> component) {
    component.style(this::styleApply);
  }
}
