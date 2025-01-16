package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.gson;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import me.syncwrld.booter.libs.google.gson.Gson;
import me.syncwrld.booter.libs.google.gson.GsonBuilder;
import me.syncwrld.booter.libs.google.gson.JsonElement;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.json.JSONOptions;
import me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.json.LegacyHoverEventSerializer;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Buildable;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Services;
import me.syncwrld.booter.libs.google.kyori.option.OptionState;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class GsonComponentSerializerImpl implements GsonComponentSerializer {
  private static final Optional<GsonComponentSerializer.Provider> SERVICE = Services.service(GsonComponentSerializer.Provider.class);
  
  static final Consumer<GsonComponentSerializer.Builder> BUILDER = SERVICE
    .<Consumer<GsonComponentSerializer.Builder>>map(GsonComponentSerializer.Provider::builder)
    .orElseGet(() -> ());
  
  private final Gson serializer;
  
  private final UnaryOperator<GsonBuilder> populator;
  
  private final LegacyHoverEventSerializer legacyHoverSerializer;
  
  private final OptionState flags;
  
  static final class Instances {
    static final GsonComponentSerializer INSTANCE = GsonComponentSerializerImpl.SERVICE
      .map(GsonComponentSerializer.Provider::gson)
      .orElseGet(() -> new GsonComponentSerializerImpl((OptionState)JSONOptions.byDataVersion(), null));
    
    static final GsonComponentSerializer LEGACY_INSTANCE = GsonComponentSerializerImpl.SERVICE
      .map(GsonComponentSerializer.Provider::gsonLegacy)
      .orElseGet(() -> new GsonComponentSerializerImpl((OptionState)JSONOptions.byDataVersion().at(2525), null));
  }
  
  GsonComponentSerializerImpl(OptionState flags, LegacyHoverEventSerializer legacyHoverSerializer) {
    this.flags = flags;
    this.legacyHoverSerializer = legacyHoverSerializer;
    this.populator = (builder -> {
        builder.registerTypeAdapterFactory(new SerializerFactory(flags, legacyHoverSerializer));
        return builder;
      });
    this
      
      .serializer = ((GsonBuilder)this.populator.apply((new GsonBuilder()).disableHtmlEscaping())).create();
  }
  
  @NotNull
  public Gson serializer() {
    return this.serializer;
  }
  
  @NotNull
  public UnaryOperator<GsonBuilder> populator() {
    return this.populator;
  }
  
  @NotNull
  public Component deserialize(@NotNull String string) {
    Component component = (Component)serializer().fromJson(string, Component.class);
    if (component == null)
      throw ComponentSerializerImpl.notSureHowToDeserialize(string); 
    return component;
  }
  
  @Nullable
  public Component deserializeOr(@Nullable String input, @Nullable Component fallback) {
    if (input == null)
      return fallback; 
    Component component = (Component)serializer().fromJson(input, Component.class);
    if (component == null)
      return fallback; 
    return component;
  }
  
  @NotNull
  public String serialize(@NotNull Component component) {
    return serializer().toJson(component);
  }
  
  @NotNull
  public Component deserializeFromTree(@NotNull JsonElement input) {
    Component component = (Component)serializer().fromJson(input, Component.class);
    if (component == null)
      throw ComponentSerializerImpl.notSureHowToDeserialize(input); 
    return component;
  }
  
  @NotNull
  public JsonElement serializeToTree(@NotNull Component component) {
    return serializer().toJsonTree(component);
  }
  
  @NotNull
  public GsonComponentSerializer.Builder toBuilder() {
    return new BuilderImpl(this);
  }
  
  static final class BuilderImpl implements GsonComponentSerializer.Builder {
    private OptionState flags = (OptionState)JSONOptions.byDataVersion();
    
    private LegacyHoverEventSerializer legacyHoverSerializer;
    
    BuilderImpl() {
      GsonComponentSerializerImpl.BUILDER.accept(this);
    }
    
    BuilderImpl(GsonComponentSerializerImpl serializer) {
      this();
      this.flags = serializer.flags;
      this.legacyHoverSerializer = serializer.legacyHoverSerializer;
    }
    
    @NotNull
    public GsonComponentSerializer.Builder options(@NotNull OptionState flags) {
      this.flags = Objects.<OptionState>requireNonNull(flags, "flags");
      return this;
    }
    
    @NotNull
    public GsonComponentSerializer.Builder editOptions(@NotNull Consumer<OptionState.Builder> optionEditor) {
      OptionState.Builder builder = OptionState.optionState().values(this.flags);
      ((Consumer<OptionState.Builder>)Objects.<Consumer<OptionState.Builder>>requireNonNull(optionEditor, "flagEditor")).accept(builder);
      this.flags = builder.build();
      return this;
    }
    
    @NotNull
    public GsonComponentSerializer.Builder legacyHoverEventSerializer(LegacyHoverEventSerializer serializer) {
      this.legacyHoverSerializer = serializer;
      return this;
    }
    
    @NotNull
    public GsonComponentSerializer build() {
      return new GsonComponentSerializerImpl(this.flags, this.legacyHoverSerializer);
    }
  }
}
