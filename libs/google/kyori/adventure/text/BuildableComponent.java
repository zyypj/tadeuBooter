package me.syncwrld.booter.libs.google.kyori.adventure.text;

import me.syncwrld.booter.libs.google.kyori.adventure.util.Buildable;
import me.syncwrld.booter.libs.jtann.NotNull;

public interface BuildableComponent<C extends BuildableComponent<C, B>, B extends ComponentBuilder<C, B>> extends Buildable<C, B>, Component {
  @NotNull
  B toBuilder();
}
