package me.syncwrld.booter.libs.google.kyori.adventure.internal.properties;

import java.util.function.Function;
import me.syncwrld.booter.libs.jtann.ApiStatus.Internal;
import me.syncwrld.booter.libs.jtann.ApiStatus.NonExtendable;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

@Internal
public final class AdventureProperties {
  public static final Property<Boolean> DEBUG = property("debug", Boolean::parseBoolean, Boolean.valueOf(false));
  
  public static final Property<String> DEFAULT_TRANSLATION_LOCALE = property("defaultTranslationLocale", Function.identity(), null);
  
  public static final Property<Boolean> SERVICE_LOAD_FAILURES_ARE_FATAL = property("serviceLoadFailuresAreFatal", Boolean::parseBoolean, Boolean.TRUE);
  
  public static final Property<Boolean> TEXT_WARN_WHEN_LEGACY_FORMATTING_DETECTED = property("text.warnWhenLegacyFormattingDetected", Boolean::parseBoolean, Boolean.FALSE);
  
  @NotNull
  public static <T> Property<T> property(@NotNull String name, @NotNull Function<String, T> parser, @Nullable T defaultValue) {
    return AdventurePropertiesImpl.property(name, parser, defaultValue);
  }
  
  @Internal
  @NonExtendable
  public static interface Property<T> {
    @Nullable
    T value();
  }
}
