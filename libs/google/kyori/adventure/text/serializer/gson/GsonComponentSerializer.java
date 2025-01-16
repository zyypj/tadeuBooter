package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.gson;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import me.syncwrld.booter.libs.google.gson.Gson;
import me.syncwrld.booter.libs.google.gson.GsonBuilder;
import me.syncwrld.booter.libs.google.gson.JsonElement;
import me.syncwrld.booter.libs.google.kyori.adventure.builder.AbstractBuilder;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.json.JSONOptions;
import me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.json.LegacyHoverEventSerializer;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Buildable;
import me.syncwrld.booter.libs.google.kyori.adventure.util.PlatformAPI;
import me.syncwrld.booter.libs.google.kyori.option.OptionState;
import me.syncwrld.booter.libs.jtann.ApiStatus.Internal;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface GsonComponentSerializer extends JSONComponentSerializer, Buildable<GsonComponentSerializer, GsonComponentSerializer.Builder> {
  @NotNull
  static GsonComponentSerializer gson() {
    return GsonComponentSerializerImpl.Instances.INSTANCE;
  }
  
  @NotNull
  static GsonComponentSerializer colorDownsamplingGson() {
    return GsonComponentSerializerImpl.Instances.LEGACY_INSTANCE;
  }
  
  static Builder builder() {
    return new GsonComponentSerializerImpl.BuilderImpl();
  }
  
  @NotNull
  Gson serializer();
  
  @NotNull
  UnaryOperator<GsonBuilder> populator();
  
  @NotNull
  Component deserializeFromTree(@NotNull JsonElement paramJsonElement);
  
  @NotNull
  JsonElement serializeToTree(@NotNull Component paramComponent);
  
  @PlatformAPI
  @Internal
  public static interface Provider {
    @PlatformAPI
    @Internal
    @NotNull
    GsonComponentSerializer gson();
    
    @PlatformAPI
    @Internal
    @NotNull
    GsonComponentSerializer gsonLegacy();
    
    @PlatformAPI
    @Internal
    @NotNull
    Consumer<GsonComponentSerializer.Builder> builder();
  }
  
  public static interface Builder extends AbstractBuilder<GsonComponentSerializer>, Buildable.Builder<GsonComponentSerializer>, JSONComponentSerializer.Builder {
    @NotNull
    default Builder downsampleColors() {
      return editOptions(features -> features.value(JSONOptions.EMIT_RGB, Boolean.valueOf(false)));
    }
    
    @Deprecated
    @NotNull
    default Builder legacyHoverEventSerializer(@Nullable LegacyHoverEventSerializer serializer) {
      return legacyHoverEventSerializer(serializer);
    }
    
    @Deprecated
    @NotNull
    default Builder emitLegacyHoverEvent() {
      return editOptions(b -> b.value(JSONOptions.EMIT_HOVER_EVENT_TYPE, JSONOptions.HoverEventValueMode.BOTH));
    }
    
    @NotNull
    Builder options(@NotNull OptionState param1OptionState);
    
    @NotNull
    Builder editOptions(@NotNull Consumer<OptionState.Builder> param1Consumer);
    
    @NotNull
    Builder legacyHoverEventSerializer(LegacyHoverEventSerializer param1LegacyHoverEventSerializer);
    
    @NotNull
    GsonComponentSerializer build();
  }
}
