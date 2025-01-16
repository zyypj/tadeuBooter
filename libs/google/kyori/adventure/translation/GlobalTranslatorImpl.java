package me.syncwrld.booter.libs.google.kyori.adventure.translation;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.text.TranslatableComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import me.syncwrld.booter.libs.google.kyori.adventure.util.TriState;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class GlobalTranslatorImpl implements GlobalTranslator {
  private static final Key NAME = Key.key("adventure", "global");
  
  static final GlobalTranslatorImpl INSTANCE = new GlobalTranslatorImpl();
  
  final TranslatableComponentRenderer<Locale> renderer = TranslatableComponentRenderer.usingTranslationSource(this);
  
  private final Set<Translator> sources = Collections.newSetFromMap(new ConcurrentHashMap<>());
  
  @NotNull
  public Key name() {
    return NAME;
  }
  
  @NotNull
  public Iterable<? extends Translator> sources() {
    return Collections.unmodifiableSet(this.sources);
  }
  
  public boolean addSource(@NotNull Translator source) {
    Objects.requireNonNull(source, "source");
    if (source == this)
      throw new IllegalArgumentException("GlobalTranslationSource"); 
    return this.sources.add(source);
  }
  
  public boolean removeSource(@NotNull Translator source) {
    Objects.requireNonNull(source, "source");
    return this.sources.remove(source);
  }
  
  @NotNull
  public TriState hasAnyTranslations() {
    if (!this.sources.isEmpty())
      return TriState.TRUE; 
    return TriState.FALSE;
  }
  
  @Nullable
  public MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
    Objects.requireNonNull(key, "key");
    Objects.requireNonNull(locale, "locale");
    for (Translator source : this.sources) {
      MessageFormat translation = source.translate(key, locale);
      if (translation != null)
        return translation; 
    } 
    return null;
  }
  
  @Nullable
  public Component translate(@NotNull TranslatableComponent component, @NotNull Locale locale) {
    Objects.requireNonNull(component, "component");
    Objects.requireNonNull(locale, "locale");
    for (Translator source : this.sources) {
      Component translation = source.translate(component, locale);
      if (translation != null)
        return translation; 
    } 
    return null;
  }
  
  @NotNull
  public Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(ExaminableProperty.of("sources", this.sources));
  }
}
