package me.syncwrld.booter.libs.google.kyori.adventure.text.event;

import java.util.function.Supplier;
import me.syncwrld.booter.libs.google.kyori.adventure.audience.Audience;
import me.syncwrld.booter.libs.google.kyori.adventure.permission.PermissionChecker;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Services;
import me.syncwrld.booter.libs.google.kyori.adventure.util.TriState;
import me.syncwrld.booter.libs.jtann.NotNull;

final class ClickCallbackInternals {
  static final PermissionChecker ALWAYS_FALSE = PermissionChecker.always(TriState.FALSE);
  
  static final ClickCallback.Provider PROVIDER = Services.service(ClickCallback.Provider.class)
    .orElseGet(Fallback::new);
  
  static final class Fallback implements ClickCallback.Provider {
    @NotNull
    public ClickEvent create(@NotNull ClickCallback<Audience> callback, ClickCallback.Options options) {
      return ClickEvent.suggestCommand("Callbacks are not supported on this platform!");
    }
  }
}
