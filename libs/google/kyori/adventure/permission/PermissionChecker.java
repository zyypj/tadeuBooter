package me.syncwrld.booter.libs.google.kyori.adventure.permission;

import java.util.Objects;
import java.util.function.Predicate;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.pointer.Pointer;
import me.syncwrld.booter.libs.google.kyori.adventure.util.TriState;
import me.syncwrld.booter.libs.jtann.NotNull;

public interface PermissionChecker extends Predicate<String> {
  public static final Pointer<PermissionChecker> POINTER = Pointer.pointer(PermissionChecker.class, Key.key("adventure", "permission"));
  
  @NotNull
  static PermissionChecker always(@NotNull TriState state) {
    Objects.requireNonNull(state);
    if (state == TriState.TRUE)
      return PermissionCheckers.TRUE; 
    if (state == TriState.FALSE)
      return PermissionCheckers.FALSE; 
    return PermissionCheckers.NOT_SET;
  }
  
  default boolean test(@NotNull String permission) {
    return (value(permission) == TriState.TRUE);
  }
  
  @NotNull
  TriState value(@NotNull String paramString);
}
