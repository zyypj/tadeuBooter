package me.syncwrld.booter.libs.google.kyori.adventure.chat;

import java.time.Instant;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.adventure.identity.Identified;
import me.syncwrld.booter.libs.google.kyori.adventure.identity.Identity;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.text.ComponentLike;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.ApiStatus.NonExtendable;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

@NonExtendable
public interface SignedMessage extends Identified, Examinable {
  @Contract(value = "_ -> new", pure = true)
  @NotNull
  static Signature signature(byte[] signature) {
    return new SignedMessageImpl.SignatureImpl(signature);
  }
  
  @Contract(value = "_, _ -> new", pure = true)
  @NotNull
  static SignedMessage system(@NotNull String message, @Nullable ComponentLike unsignedContent) {
    return new SignedMessageImpl(message, ComponentLike.unbox(unsignedContent));
  }
  
  @Contract(pure = true)
  @NotNull
  Instant timestamp();
  
  @Contract(pure = true)
  long salt();
  
  @Contract(pure = true)
  @Nullable
  Signature signature();
  
  @Contract(pure = true)
  @Nullable
  Component unsignedContent();
  
  @Contract(pure = true)
  @NotNull
  String message();
  
  @Contract(pure = true)
  default boolean isSystem() {
    return (identity() == Identity.nil());
  }
  
  @Contract(pure = true)
  default boolean canDelete() {
    return (signature() != null);
  }
  
  @NotNull
  default Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("timestamp", timestamp()), 
          ExaminableProperty.of("salt", salt()), 
          ExaminableProperty.of("signature", signature()), 
          ExaminableProperty.of("unsignedContent", unsignedContent()), 
          ExaminableProperty.of("message", message()) });
  }
  
  @NonExtendable
  public static interface Signature extends Examinable {
    @Contract(pure = true)
    byte[] bytes();
    
    @NotNull
    default Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(ExaminableProperty.of("bytes", bytes()));
    }
  }
}
