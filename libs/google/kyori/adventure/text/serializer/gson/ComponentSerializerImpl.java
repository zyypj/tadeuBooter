package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import me.syncwrld.booter.libs.google.gson.Gson;
import me.syncwrld.booter.libs.google.gson.JsonElement;
import me.syncwrld.booter.libs.google.gson.JsonObject;
import me.syncwrld.booter.libs.google.gson.JsonParseException;
import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.gson.reflect.TypeToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonWriter;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.text.BlockNBTComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.BuildableComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.text.ComponentBuilder;
import me.syncwrld.booter.libs.google.kyori.adventure.text.ComponentLike;
import me.syncwrld.booter.libs.google.kyori.adventure.text.EntityNBTComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.KeybindComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.NBTComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.ScoreComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.SelectorComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.StorageNBTComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.TextComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.TranslatableComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.TranslationArgument;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.Style;
import me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.json.JSONOptions;
import me.syncwrld.booter.libs.google.kyori.option.OptionState;
import me.syncwrld.booter.libs.jtann.Nullable;

final class ComponentSerializerImpl extends TypeAdapter<Component> {
  static final Type COMPONENT_LIST_TYPE = (new TypeToken<List<Component>>() {
    
    }).getType();
  
  static final Type TRANSLATABLE_ARGUMENT_LIST_TYPE = (new TypeToken<List<TranslationArgument>>() {
    
    }).getType();
  
  private final boolean emitCompactTextComponent;
  
  private final Gson gson;
  
  static TypeAdapter<Component> create(OptionState features, Gson gson) {
    return (new ComponentSerializerImpl(((Boolean)features.value(JSONOptions.EMIT_COMPACT_TEXT_COMPONENT)).booleanValue(), gson)).nullSafe();
  }
  
  private ComponentSerializerImpl(boolean emitCompactTextComponent, Gson gson) {
    this.emitCompactTextComponent = emitCompactTextComponent;
    this.gson = gson;
  }
  
  public BuildableComponent<?, ?> read(JsonReader in) throws IOException {
    BuildableComponent<?, ?> buildableComponent;
    StorageNBTComponent.Builder builder;
    JsonToken token = in.peek();
    if (token == JsonToken.STRING || token == JsonToken.NUMBER || token == JsonToken.BOOLEAN)
      return (BuildableComponent<?, ?>)Component.text(GsonHacks.readString(in)); 
    if (token == JsonToken.BEGIN_ARRAY) {
      ComponentBuilder<?, ?> parent = null;
      in.beginArray();
      while (in.hasNext()) {
        BuildableComponent<?, ?> child = read(in);
        if (parent == null) {
          parent = child.toBuilder();
          continue;
        } 
        parent.append((Component)child);
      } 
      if (parent == null)
        throw notSureHowToDeserialize(in.getPath()); 
      in.endArray();
      return parent.build();
    } 
    if (token != JsonToken.BEGIN_OBJECT)
      throw notSureHowToDeserialize(in.getPath()); 
    JsonObject style = new JsonObject();
    List<Component> extra = Collections.emptyList();
    String text = null;
    String translate = null;
    String translateFallback = null;
    List<TranslationArgument> translateWith = null;
    String scoreName = null;
    String scoreObjective = null;
    String scoreValue = null;
    String selector = null;
    String keybind = null;
    String nbt = null;
    boolean nbtInterpret = false;
    BlockNBTComponent.Pos nbtBlock = null;
    String nbtEntity = null;
    Key nbtStorage = null;
    Component separator = null;
    in.beginObject();
    while (in.hasNext()) {
      String fieldName = in.nextName();
      if (fieldName.equals("text")) {
        text = GsonHacks.readString(in);
        continue;
      } 
      if (fieldName.equals("translate")) {
        translate = in.nextString();
        continue;
      } 
      if (fieldName.equals("fallback")) {
        translateFallback = in.nextString();
        continue;
      } 
      if (fieldName.equals("with")) {
        translateWith = (List<TranslationArgument>)this.gson.fromJson(in, TRANSLATABLE_ARGUMENT_LIST_TYPE);
        continue;
      } 
      if (fieldName.equals("score")) {
        in.beginObject();
        while (in.hasNext()) {
          String scoreFieldName = in.nextName();
          if (scoreFieldName.equals("name")) {
            scoreName = in.nextString();
            continue;
          } 
          if (scoreFieldName.equals("objective")) {
            scoreObjective = in.nextString();
            continue;
          } 
          if (scoreFieldName.equals("value")) {
            scoreValue = in.nextString();
            continue;
          } 
          in.skipValue();
        } 
        if (scoreName == null || scoreObjective == null)
          throw new JsonParseException("A score component requires a name and objective"); 
        in.endObject();
        continue;
      } 
      if (fieldName.equals("selector")) {
        selector = in.nextString();
        continue;
      } 
      if (fieldName.equals("keybind")) {
        keybind = in.nextString();
        continue;
      } 
      if (fieldName.equals("nbt")) {
        nbt = in.nextString();
        continue;
      } 
      if (fieldName.equals("interpret")) {
        nbtInterpret = in.nextBoolean();
        continue;
      } 
      if (fieldName.equals("block")) {
        nbtBlock = (BlockNBTComponent.Pos)this.gson.fromJson(in, SerializerFactory.BLOCK_NBT_POS_TYPE);
        continue;
      } 
      if (fieldName.equals("entity")) {
        nbtEntity = in.nextString();
        continue;
      } 
      if (fieldName.equals("storage")) {
        nbtStorage = (Key)this.gson.fromJson(in, SerializerFactory.KEY_TYPE);
        continue;
      } 
      if (fieldName.equals("extra")) {
        extra = (List<Component>)this.gson.fromJson(in, COMPONENT_LIST_TYPE);
        continue;
      } 
      if (fieldName.equals("separator")) {
        buildableComponent = read(in);
        continue;
      } 
      style.add(fieldName, (JsonElement)this.gson.fromJson(in, JsonElement.class));
    } 
    if (text != null) {
      TextComponent.Builder builder1 = Component.text().content(text);
    } else if (translate != null) {
      if (translateWith != null) {
        TranslatableComponent.Builder builder1 = Component.translatable().key(translate).fallback(translateFallback).arguments(translateWith);
      } else {
        TranslatableComponent.Builder builder1 = Component.translatable().key(translate).fallback(translateFallback);
      } 
    } else if (scoreName != null && scoreObjective != null) {
      if (scoreValue == null) {
        ScoreComponent.Builder builder1 = Component.score().name(scoreName).objective(scoreObjective);
      } else {
        ScoreComponent.Builder builder1 = Component.score().name(scoreName).objective(scoreObjective).value(scoreValue);
      } 
    } else if (selector != null) {
      SelectorComponent.Builder builder1 = Component.selector().pattern(selector).separator((ComponentLike)buildableComponent);
    } else if (keybind != null) {
      KeybindComponent.Builder builder1 = Component.keybind().keybind(keybind);
    } else if (nbt != null) {
      if (nbtBlock != null) {
        BlockNBTComponent.Builder builder1 = ((BlockNBTComponent.Builder)nbt(Component.blockNBT(), nbt, nbtInterpret, (Component)buildableComponent)).pos(nbtBlock);
      } else if (nbtEntity != null) {
        EntityNBTComponent.Builder builder1 = ((EntityNBTComponent.Builder)nbt(Component.entityNBT(), nbt, nbtInterpret, (Component)buildableComponent)).selector(nbtEntity);
      } else if (nbtStorage != null) {
        builder = ((StorageNBTComponent.Builder)nbt(Component.storageNBT(), nbt, nbtInterpret, (Component)buildableComponent)).storage(nbtStorage);
      } else {
        throw notSureHowToDeserialize(in.getPath());
      } 
    } else {
      throw notSureHowToDeserialize(in.getPath());
    } 
    builder.style((Style)this.gson.fromJson((JsonElement)style, SerializerFactory.STYLE_TYPE))
      .append(extra);
    in.endObject();
    return builder.build();
  }
  
  private static <C extends NBTComponent<C, B>, B extends me.syncwrld.booter.libs.google.kyori.adventure.text.NBTComponentBuilder<C, B>> B nbt(B builder, String nbt, boolean interpret, @Nullable Component separator) {
    return (B)builder
      .nbtPath(nbt)
      .interpret(interpret)
      .separator((ComponentLike)separator);
  }
  
  public void write(JsonWriter out, Component value) throws IOException {
    if (value instanceof TextComponent && value
      
      .children().isEmpty() && 
      !value.hasStyling() && this.emitCompactTextComponent) {
      out.value(((TextComponent)value).content());
      return;
    } 
    out.beginObject();
    if (value.hasStyling()) {
      JsonElement style = this.gson.toJsonTree(value.style(), SerializerFactory.STYLE_TYPE);
      if (style.isJsonObject())
        for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)style.getAsJsonObject().entrySet()) {
          out.name(entry.getKey());
          this.gson.toJson(entry.getValue(), out);
        }  
    } 
    if (!value.children().isEmpty()) {
      out.name("extra");
      this.gson.toJson(value.children(), COMPONENT_LIST_TYPE, out);
    } 
    if (value instanceof TextComponent) {
      out.name("text");
      out.value(((TextComponent)value).content());
    } else if (value instanceof TranslatableComponent) {
      TranslatableComponent translatable = (TranslatableComponent)value;
      out.name("translate");
      out.value(translatable.key());
      String fallback = translatable.fallback();
      if (fallback != null) {
        out.name("fallback");
        out.value(fallback);
      } 
      if (!translatable.arguments().isEmpty()) {
        out.name("with");
        this.gson.toJson(translatable.arguments(), TRANSLATABLE_ARGUMENT_LIST_TYPE, out);
      } 
    } else if (value instanceof ScoreComponent) {
      ScoreComponent score = (ScoreComponent)value;
      out.name("score");
      out.beginObject();
      out.name("name");
      out.value(score.name());
      out.name("objective");
      out.value(score.objective());
      if (score.value() != null) {
        out.name("value");
        out.value(score.value());
      } 
      out.endObject();
    } else if (value instanceof SelectorComponent) {
      SelectorComponent selector = (SelectorComponent)value;
      out.name("selector");
      out.value(selector.pattern());
      serializeSeparator(out, selector.separator());
    } else if (value instanceof KeybindComponent) {
      out.name("keybind");
      out.value(((KeybindComponent)value).keybind());
    } else if (value instanceof NBTComponent) {
      NBTComponent<?, ?> nbt = (NBTComponent<?, ?>)value;
      out.name("nbt");
      out.value(nbt.nbtPath());
      out.name("interpret");
      out.value(nbt.interpret());
      serializeSeparator(out, nbt.separator());
      if (value instanceof BlockNBTComponent) {
        out.name("block");
        this.gson.toJson(((BlockNBTComponent)value).pos(), SerializerFactory.BLOCK_NBT_POS_TYPE, out);
      } else if (value instanceof EntityNBTComponent) {
        out.name("entity");
        out.value(((EntityNBTComponent)value).selector());
      } else if (value instanceof StorageNBTComponent) {
        out.name("storage");
        this.gson.toJson(((StorageNBTComponent)value).storage(), SerializerFactory.KEY_TYPE, out);
      } else {
        throw notSureHowToSerialize(value);
      } 
    } else {
      throw notSureHowToSerialize(value);
    } 
    out.endObject();
  }
  
  private void serializeSeparator(JsonWriter out, @Nullable Component separator) throws IOException {
    if (separator != null) {
      out.name("separator");
      write(out, separator);
    } 
  }
  
  static JsonParseException notSureHowToDeserialize(Object element) {
    return new JsonParseException("Don't know how to turn " + element + " into a Component");
  }
  
  private static IllegalArgumentException notSureHowToSerialize(Component component) {
    return new IllegalArgumentException("Don't know how to serialize " + component + " as a Component");
  }
}
