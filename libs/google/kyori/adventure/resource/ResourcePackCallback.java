package me.syncwrld.booter.libs.google.kyori.adventure.resource;

import java.util.UUID;
import java.util.function.BiConsumer;
import me.syncwrld.booter.libs.google.kyori.adventure.audience.Audience;
import me.syncwrld.booter.libs.jtann.NotNull;

@FunctionalInterface
public interface ResourcePackCallback {
  @NotNull
  static ResourcePackCallback noOp() {
    return ResourcePackCallbacks.NO_OP;
  }
  
  @NotNull
  static ResourcePackCallback onTerminal(@NotNull BiConsumer<UUID, Audience> success, @NotNull BiConsumer<UUID, Audience> failure) {
    return (uuid, status, audience) -> {
        if (status == ResourcePackStatus.SUCCESSFULLY_LOADED) {
          success.accept(uuid, audience);
        } else if (!status.intermediate()) {
          failure.accept(uuid, audience);
        } 
      };
  }
  
  void packEventReceived(@NotNull UUID paramUUID, @NotNull ResourcePackStatus paramResourcePackStatus, @NotNull Audience paramAudience);
}
