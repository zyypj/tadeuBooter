package me.syncwrld.booter.libs.google.kyori.adventure.text;

import me.syncwrld.booter.libs.jtann.NotNull;

@FunctionalInterface
public interface ComponentApplicable {
  @NotNull
  Component componentApply(@NotNull Component paramComponent);
}
