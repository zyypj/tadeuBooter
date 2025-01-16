package me.syncwrld.booter.libs.google.kyori.adventure.translation;

import java.util.Locale;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.jtann.ApiStatus.ScheduledForRemoval;
import me.syncwrld.booter.libs.jtann.NotNull;

public interface GlobalTranslator extends Translator, Examinable {
  @NotNull
  static GlobalTranslator translator() {
    return GlobalTranslatorImpl.INSTANCE;
  }
  
  @Deprecated
  @ScheduledForRemoval(inVersion = "5.0.0")
  @NotNull
  static GlobalTranslator get() {
    return GlobalTranslatorImpl.INSTANCE;
  }
  
  @NotNull
  static TranslatableComponentRenderer<Locale> renderer() {
    return GlobalTranslatorImpl.INSTANCE.renderer;
  }
  
  @NotNull
  static Component render(@NotNull Component component, @NotNull Locale locale) {
    return renderer().render(component, locale);
  }
  
  @NotNull
  Iterable<? extends Translator> sources();
  
  boolean addSource(@NotNull Translator paramTranslator);
  
  boolean removeSource(@NotNull Translator paramTranslator);
}
