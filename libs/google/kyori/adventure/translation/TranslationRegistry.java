package me.syncwrld.booter.libs.google.kyori.adventure.translation;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface TranslationRegistry extends Translator {
  public static final Pattern SINGLE_QUOTE_PATTERN = Pattern.compile("'");
  
  @NotNull
  static TranslationRegistry create(Key name) {
    return new TranslationRegistryImpl(Objects.<Key>requireNonNull(name, "name"));
  }
  
  default void registerAll(@NotNull Locale locale, @NotNull Map<String, MessageFormat> formats) {
    Objects.requireNonNull(formats);
    registerAll(locale, formats.keySet(), formats::get);
  }
  
  default void registerAll(@NotNull Locale locale, @NotNull Path path, boolean escapeSingleQuotes) {
    try {
      BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
      try {
        registerAll(locale, new PropertyResourceBundle(reader), escapeSingleQuotes);
        if (reader != null)
          reader.close(); 
      } catch (Throwable throwable) {
        if (reader != null)
          try {
            reader.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (IOException iOException) {}
  }
  
  default void registerAll(@NotNull Locale locale, @NotNull ResourceBundle bundle, boolean escapeSingleQuotes) {
    registerAll(locale, bundle.keySet(), key -> {
          String format = bundle.getString(key);
          return new MessageFormat(escapeSingleQuotes ? SINGLE_QUOTE_PATTERN.matcher(format).replaceAll("''") : format, locale);
        });
  }
  
  default void registerAll(@NotNull Locale locale, @NotNull Set<String> keys, Function<String, MessageFormat> function) {
    IllegalArgumentException firstError = null;
    int errorCount = 0;
    for (String key : keys) {
      try {
        register(key, locale, function.apply(key));
      } catch (IllegalArgumentException e) {
        if (firstError == null)
          firstError = e; 
        errorCount++;
      } 
    } 
    if (firstError != null) {
      if (errorCount == 1)
        throw firstError; 
      if (errorCount > 1)
        throw new IllegalArgumentException(String.format("Invalid key (and %d more)", new Object[] { Integer.valueOf(errorCount - 1) }), firstError); 
    } 
  }
  
  boolean contains(@NotNull String paramString);
  
  @Nullable
  MessageFormat translate(@NotNull String paramString, @NotNull Locale paramLocale);
  
  void defaultLocale(@NotNull Locale paramLocale);
  
  void register(@NotNull String paramString, @NotNull Locale paramLocale, @NotNull MessageFormat paramMessageFormat);
  
  void unregister(@NotNull String paramString);
}
