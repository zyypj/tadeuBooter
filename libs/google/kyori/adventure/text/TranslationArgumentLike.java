package me.syncwrld.booter.libs.google.kyori.adventure.text;

import me.syncwrld.booter.libs.jtann.NotNull;

@FunctionalInterface
public interface TranslationArgumentLike extends ComponentLike {
  @NotNull
  TranslationArgument asTranslationArgument();
  
  @NotNull
  default Component asComponent() {
    return asTranslationArgument().asComponent();
  }
}
