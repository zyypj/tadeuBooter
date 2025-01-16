package me.syncwrld.booter.libs.google.kyori.adventure.util;

import java.util.function.BooleanSupplier;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public enum TriState {
  NOT_SET, FALSE, TRUE;
  
  @Nullable
  public Boolean toBoolean() {
    switch (this) {
      case TRUE:
        return Boolean.TRUE;
      case FALSE:
        return Boolean.FALSE;
    } 
    return null;
  }
  
  public boolean toBooleanOrElse(boolean other) {
    switch (this) {
      case TRUE:
        return true;
      case FALSE:
        return false;
    } 
    return other;
  }
  
  public boolean toBooleanOrElseGet(@NotNull BooleanSupplier supplier) {
    switch (this) {
      case TRUE:
        return true;
      case FALSE:
        return false;
    } 
    return supplier.getAsBoolean();
  }
  
  @NotNull
  public static TriState byBoolean(boolean value) {
    return value ? TRUE : FALSE;
  }
  
  @NotNull
  public static TriState byBoolean(@Nullable Boolean value) {
    return (value == null) ? NOT_SET : byBoolean(value.booleanValue());
  }
}
