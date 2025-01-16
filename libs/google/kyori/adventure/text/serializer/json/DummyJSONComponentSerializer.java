package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.json;

import java.util.function.Consumer;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.option.OptionState;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class DummyJSONComponentSerializer implements JSONComponentSerializer {
  static final JSONComponentSerializer INSTANCE = new DummyJSONComponentSerializer();
  
  private static final String UNSUPPORTED_MESSAGE = "No JsonComponentSerializer implementation found\n\nAre you missing an implementation artifact like adventure-text-serializer-gson?\nIs your environment configured in a way that causes ServiceLoader to malfunction?";
  
  @NotNull
  public Component deserialize(@NotNull String input) {
    throw new UnsupportedOperationException("No JsonComponentSerializer implementation found\n\nAre you missing an implementation artifact like adventure-text-serializer-gson?\nIs your environment configured in a way that causes ServiceLoader to malfunction?");
  }
  
  @NotNull
  public String serialize(@NotNull Component component) {
    throw new UnsupportedOperationException("No JsonComponentSerializer implementation found\n\nAre you missing an implementation artifact like adventure-text-serializer-gson?\nIs your environment configured in a way that causes ServiceLoader to malfunction?");
  }
  
  static final class BuilderImpl implements JSONComponentSerializer.Builder {
    @NotNull
    public JSONComponentSerializer.Builder options(@NotNull OptionState flags) {
      return this;
    }
    
    @NotNull
    public JSONComponentSerializer.Builder editOptions(@NotNull Consumer<OptionState.Builder> optionEditor) {
      return this;
    }
    
    @Deprecated
    @NotNull
    public JSONComponentSerializer.Builder downsampleColors() {
      return this;
    }
    
    @NotNull
    public JSONComponentSerializer.Builder legacyHoverEventSerializer(@Nullable LegacyHoverEventSerializer serializer) {
      return this;
    }
    
    @Deprecated
    @NotNull
    public JSONComponentSerializer.Builder emitLegacyHoverEvent() {
      return this;
    }
    
    @NotNull
    public JSONComponentSerializer build() {
      return DummyJSONComponentSerializer.INSTANCE;
    }
  }
}
