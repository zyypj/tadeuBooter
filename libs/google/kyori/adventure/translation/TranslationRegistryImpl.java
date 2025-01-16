package me.syncwrld.booter.libs.google.kyori.adventure.translation;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.util.TriState;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class TranslationRegistryImpl implements Examinable, TranslationRegistry {
  private final Key name;
  
  private final Map<String, Translation> translations = new ConcurrentHashMap<>();
  
  private Locale defaultLocale = Locale.US;
  
  TranslationRegistryImpl(Key name) {
    this.name = name;
  }
  
  public void register(@NotNull String key, @NotNull Locale locale, @NotNull MessageFormat format) {
    ((Translation)this.translations.computeIfAbsent(key, x$0 -> new Translation(x$0))).register(locale, format);
  }
  
  public void unregister(@NotNull String key) {
    this.translations.remove(key);
  }
  
  @NotNull
  public Key name() {
    return this.name;
  }
  
  public boolean contains(@NotNull String key) {
    return this.translations.containsKey(key);
  }
  
  @NotNull
  public TriState hasAnyTranslations() {
    if (!this.translations.isEmpty())
      return TriState.TRUE; 
    return TriState.FALSE;
  }
  
  @Nullable
  public MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
    Translation translation = this.translations.get(key);
    if (translation == null)
      return null; 
    return translation.translate(locale);
  }
  
  public void defaultLocale(@NotNull Locale defaultLocale) {
    this.defaultLocale = Objects.<Locale>requireNonNull(defaultLocale, "defaultLocale");
  }
  
  @NotNull
  public Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(ExaminableProperty.of("translations", this.translations));
  }
  
  public boolean equals(Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof TranslationRegistryImpl))
      return false; 
    TranslationRegistryImpl that = (TranslationRegistryImpl)other;
    return (this.name.equals(that.name) && this.translations
      .equals(that.translations) && this.defaultLocale
      .equals(that.defaultLocale));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.name, this.translations, this.defaultLocale });
  }
  
  public String toString() {
    return Internals.toString(this);
  }
  
  final class Translation implements Examinable {
    private final String key;
    
    private final Map<Locale, MessageFormat> formats;
    
    Translation(String key) {
      this.key = Objects.<String>requireNonNull(key, "translation key");
      this.formats = new ConcurrentHashMap<>();
    }
    
    void register(@NotNull Locale locale, @NotNull MessageFormat format) {
      if (this.formats.putIfAbsent(Objects.<Locale>requireNonNull(locale, "locale"), Objects.<MessageFormat>requireNonNull(format, "message format")) != null)
        throw new IllegalArgumentException(String.format("Translation already exists: %s for %s", new Object[] { this.key, locale })); 
    }
    
    @Nullable
    MessageFormat translate(@NotNull Locale locale) {
      MessageFormat format = this.formats.get(Objects.requireNonNull(locale, "locale"));
      if (format == null) {
        format = this.formats.get(new Locale(locale.getLanguage()));
        if (format == null) {
          format = this.formats.get(TranslationRegistryImpl.this.defaultLocale);
          if (format == null)
            format = this.formats.get(TranslationLocales.global()); 
        } 
      } 
      return format;
    }
    
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("key", this.key), 
            ExaminableProperty.of("formats", this.formats) });
    }
    
    public boolean equals(Object other) {
      if (this == other)
        return true; 
      if (!(other instanceof Translation))
        return false; 
      Translation that = (Translation)other;
      return (this.key.equals(that.key) && this.formats
        .equals(that.formats));
    }
    
    public int hashCode() {
      return Objects.hash(new Object[] { this.key, this.formats });
    }
    
    public String toString() {
      return Internals.toString(this);
    }
  }
}
