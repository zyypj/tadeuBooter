package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.Objects;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.jtann.ApiStatus.NonExtendable;
import me.syncwrld.booter.libs.jtann.NotNull;

@NonExtendable
public interface TranslationArgument extends TranslationArgumentLike, Examinable {
  @NotNull
  static TranslationArgument bool(boolean value) {
    return new TranslationArgumentImpl(Boolean.valueOf(value));
  }
  
  @NotNull
  static TranslationArgument numeric(@NotNull Number value) {
    return new TranslationArgumentImpl(Objects.requireNonNull(value, "value"));
  }
  
  @NotNull
  static TranslationArgument component(@NotNull ComponentLike value) {
    if (value instanceof TranslationArgumentLike)
      return ((TranslationArgumentLike)value).asTranslationArgument(); 
    return new TranslationArgumentImpl(Objects.requireNonNull(((ComponentLike)Objects.<ComponentLike>requireNonNull(value, "value")).asComponent(), "value.asComponent()"));
  }
  
  @NotNull
  Object value();
  
  @NotNull
  default TranslationArgument asTranslationArgument() {
    return this;
  }
}
