package me.syncwrld.booter.libs.google.kyori.adventure.translation;

import java.util.Locale;
import java.util.function.Supplier;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.properties.AdventureProperties;

final class TranslationLocales {
  private static final Supplier<Locale> GLOBAL;
  
  static {
    String property = (String)AdventureProperties.DEFAULT_TRANSLATION_LOCALE.value();
    if (property == null || property.isEmpty()) {
      GLOBAL = (() -> Locale.US);
    } else if (property.equals("system")) {
      GLOBAL = Locale::getDefault;
    } else {
      Locale locale = Translator.parseLocale(property);
      GLOBAL = (() -> locale);
    } 
  }
  
  static Locale global() {
    return GLOBAL.get();
  }
}
