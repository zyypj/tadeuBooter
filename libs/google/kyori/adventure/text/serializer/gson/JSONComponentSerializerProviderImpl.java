package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.gson;

import java.util.function.Supplier;
import me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Services;
import me.syncwrld.booter.libs.jtann.ApiStatus.Internal;
import me.syncwrld.booter.libs.jtann.NotNull;

@Internal
public final class JSONComponentSerializerProviderImpl implements JSONComponentSerializer.Provider, Services.Fallback {
  @NotNull
  public JSONComponentSerializer instance() {
    return GsonComponentSerializer.gson();
  }
  
  @NotNull
  public Supplier<JSONComponentSerializer.Builder> builder() {
    return GsonComponentSerializer::builder;
  }
  
  public String toString() {
    return "JSONComponentSerializerProviderImpl[GsonComponentSerializer]";
  }
}
