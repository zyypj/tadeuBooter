package me.syncwrld.booter.libs.google.kyori.adventure.key;

import me.syncwrld.booter.libs.jtann.NotNull;

public interface Namespaced {
  @NotNull
  @Namespace
  String namespace();
}
