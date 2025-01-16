package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.json;

import java.io.IOException;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.HoverEvent;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Codec;
import me.syncwrld.booter.libs.jtann.NotNull;

public interface LegacyHoverEventSerializer {
  HoverEvent.ShowItem deserializeShowItem(@NotNull Component paramComponent) throws IOException;
  
  @NotNull
  Component serializeShowItem(HoverEvent.ShowItem paramShowItem) throws IOException;
  
  HoverEvent.ShowEntity deserializeShowEntity(@NotNull Component paramComponent, Codec.Decoder<Component, String, ? extends RuntimeException> paramDecoder) throws IOException;
  
  @NotNull
  Component serializeShowEntity(HoverEvent.ShowEntity paramShowEntity, Codec.Encoder<Component, String, ? extends RuntimeException> paramEncoder) throws IOException;
}
