package me.syncwrld.booter.libs.google.kyori.adventure.translation;

import java.text.MessageFormat;
import java.util.Locale;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.text.TranslatableComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.util.TriState;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface Translator {
  @Nullable
  static Locale parseLocale(@NotNull String string) {
    String[] segments = string.split("_", 3);
    int length = segments.length;
    if (length == 1)
      return new Locale(string); 
    if (length == 2)
      return new Locale(segments[0], segments[1]); 
    if (length == 3)
      return new Locale(segments[0], segments[1], segments[2]); 
    return null;
  }
  
  @NotNull
  Key name();
  
  @NotNull
  default TriState hasAnyTranslations() {
    return TriState.NOT_SET;
  }
  
  @Nullable
  MessageFormat translate(@NotNull String paramString, @NotNull Locale paramLocale);
  
  @Nullable
  default Component translate(@NotNull TranslatableComponent component, @NotNull Locale locale) {
    return null;
  }
}
