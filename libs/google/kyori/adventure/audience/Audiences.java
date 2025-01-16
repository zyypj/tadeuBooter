package me.syncwrld.booter.libs.google.kyori.adventure.audience;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import me.syncwrld.booter.libs.google.kyori.adventure.resource.ResourcePackCallback;
import me.syncwrld.booter.libs.google.kyori.adventure.resource.ResourcePackStatus;
import me.syncwrld.booter.libs.google.kyori.adventure.text.ComponentLike;
import me.syncwrld.booter.libs.jtann.NotNull;

public final class Audiences {
  static final Collector<? super Audience, ?, ForwardingAudience> COLLECTOR;
  
  static {
    COLLECTOR = Collectors.collectingAndThen(
        Collectors.toCollection(ArrayList::new), audiences -> Audience.audience(Collections.unmodifiableCollection(audiences)));
  }
  
  @NotNull
  public static Consumer<? super Audience> sendingMessage(@NotNull ComponentLike message) {
    return audience -> audience.sendMessage(message);
  }
  
  @NotNull
  static ResourcePackCallback unwrapCallback(Audience forwarding, Audience dest, @NotNull ResourcePackCallback cb) {
    if (cb == ResourcePackCallback.noOp())
      return cb; 
    return (uuid, status, audience) -> cb.packEventReceived(uuid, status, (audience == dest) ? forwarding : audience);
  }
}
