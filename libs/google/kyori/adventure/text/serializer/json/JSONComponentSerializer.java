package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.json;

import java.util.function.Consumer;
import java.util.function.Supplier;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.ComponentSerializer;
import me.syncwrld.booter.libs.google.kyori.adventure.util.PlatformAPI;
import me.syncwrld.booter.libs.google.kyori.option.OptionState;
import me.syncwrld.booter.libs.jtann.ApiStatus.Internal;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface JSONComponentSerializer extends ComponentSerializer<Component, Component, String> {
  @NotNull
  static JSONComponentSerializer json() {
    return JSONComponentSerializerAccessor.Instances.INSTANCE;
  }
  
  static Builder builder() {
    return JSONComponentSerializerAccessor.Instances.BUILDER_SUPPLIER.get();
  }
  
  @PlatformAPI
  @Internal
  public static interface Provider {
    @PlatformAPI
    @Internal
    @NotNull
    JSONComponentSerializer instance();
    
    @PlatformAPI
    @Internal
    @NotNull
    Supplier<JSONComponentSerializer.Builder> builder();
  }
  
  public static interface Builder {
    @NotNull
    Builder options(@NotNull OptionState param1OptionState);
    
    @NotNull
    Builder editOptions(@NotNull Consumer<OptionState.Builder> param1Consumer);
    
    @Deprecated
    @NotNull
    Builder downsampleColors();
    
    @NotNull
    Builder legacyHoverEventSerializer(@Nullable LegacyHoverEventSerializer param1LegacyHoverEventSerializer);
    
    @Deprecated
    @NotNull
    Builder emitLegacyHoverEvent();
    
    @NotNull
    JSONComponentSerializer build();
  }
}
