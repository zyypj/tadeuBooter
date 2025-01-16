package me.syncwrld.booter.libs.google.kyori.adventure.key;

import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public final class InvalidKeyException extends RuntimeException {
  private static final long serialVersionUID = -5413304087321449434L;
  
  private final String keyNamespace;
  
  private final String keyValue;
  
  InvalidKeyException(@NotNull String keyNamespace, @NotNull String keyValue, @Nullable String message) {
    super(message);
    this.keyNamespace = keyNamespace;
    this.keyValue = keyValue;
  }
  
  @NotNull
  public final String keyNamespace() {
    return this.keyNamespace;
  }
  
  @NotNull
  public final String keyValue() {
    return this.keyValue;
  }
}
