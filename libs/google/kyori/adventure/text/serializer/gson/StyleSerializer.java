package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.gson;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import me.syncwrld.booter.libs.google.gson.Gson;
import me.syncwrld.booter.libs.google.gson.JsonElement;
import me.syncwrld.booter.libs.google.gson.JsonObject;
import me.syncwrld.booter.libs.google.gson.JsonParseException;
import me.syncwrld.booter.libs.google.gson.JsonPrimitive;
import me.syncwrld.booter.libs.google.gson.JsonSyntaxException;
import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonWriter;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.ClickEvent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.HoverEvent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.HoverEventSource;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.Style;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.TextColor;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.TextDecoration;
import me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.json.JSONOptions;
import me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.json.LegacyHoverEventSerializer;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Codec;
import me.syncwrld.booter.libs.google.kyori.option.OptionState;

final class StyleSerializer extends TypeAdapter<Style> {
  private static final TextDecoration[] DECORATIONS = new TextDecoration[] { TextDecoration.BOLD, TextDecoration.ITALIC, TextDecoration.UNDERLINED, TextDecoration.STRIKETHROUGH, TextDecoration.OBFUSCATED };
  
  private final LegacyHoverEventSerializer legacyHover;
  
  private final boolean emitLegacyHover;
  
  private final boolean emitModernHover;
  
  private final boolean strictEventValues;
  
  private final Gson gson;
  
  static {
    Set<TextDecoration> knownDecorations = EnumSet.allOf(TextDecoration.class);
    for (TextDecoration decoration : DECORATIONS)
      knownDecorations.remove(decoration); 
    if (!knownDecorations.isEmpty())
      throw new IllegalStateException("Gson serializer is missing some text decorations: " + knownDecorations); 
  }
  
  static TypeAdapter<Style> create(LegacyHoverEventSerializer legacyHover, OptionState features, Gson gson) {
    JSONOptions.HoverEventValueMode hoverMode = (JSONOptions.HoverEventValueMode)features.value(JSONOptions.EMIT_HOVER_EVENT_TYPE);
    return (new StyleSerializer(legacyHover, (hoverMode == JSONOptions.HoverEventValueMode.LEGACY_ONLY || hoverMode == JSONOptions.HoverEventValueMode.BOTH), (hoverMode == JSONOptions.HoverEventValueMode.MODERN_ONLY || hoverMode == JSONOptions.HoverEventValueMode.BOTH), ((Boolean)features
        
        .value(JSONOptions.VALIDATE_STRICT_EVENTS)).booleanValue(), gson))
      
      .nullSafe();
  }
  
  private StyleSerializer(LegacyHoverEventSerializer legacyHover, boolean emitLegacyHover, boolean emitModernHover, boolean strictEventValues, Gson gson) {
    this.legacyHover = legacyHover;
    this.emitLegacyHover = emitLegacyHover;
    this.emitModernHover = emitModernHover;
    this.strictEventValues = strictEventValues;
    this.gson = gson;
  }
  
  public Style read(JsonReader in) throws IOException {
    in.beginObject();
    Style.Builder style = Style.style();
    while (in.hasNext()) {
      String fieldName = in.nextName();
      if (fieldName.equals("font")) {
        style.font((Key)this.gson.fromJson(in, SerializerFactory.KEY_TYPE));
        continue;
      } 
      if (fieldName.equals("color")) {
        TextColorWrapper color = (TextColorWrapper)this.gson.fromJson(in, SerializerFactory.COLOR_WRAPPER_TYPE);
        if (color.color != null) {
          style.color(color.color);
          continue;
        } 
        if (color.decoration != null)
          style.decoration(color.decoration, TextDecoration.State.TRUE); 
        continue;
      } 
      if (TextDecoration.NAMES.keys().contains(fieldName)) {
        style.decoration((TextDecoration)TextDecoration.NAMES.value(fieldName), GsonHacks.readBoolean(in));
        continue;
      } 
      if (fieldName.equals("insertion")) {
        style.insertion(in.nextString());
        continue;
      } 
      if (fieldName.equals("clickEvent")) {
        in.beginObject();
        ClickEvent.Action action = null;
        String value = null;
        while (in.hasNext()) {
          String clickEventField = in.nextName();
          if (clickEventField.equals("action")) {
            action = (ClickEvent.Action)this.gson.fromJson(in, SerializerFactory.CLICK_ACTION_TYPE);
            continue;
          } 
          if (clickEventField.equals("value")) {
            if (in.peek() == JsonToken.NULL && this.strictEventValues)
              throw ComponentSerializerImpl.notSureHowToDeserialize("value"); 
            value = (in.peek() == JsonToken.NULL) ? null : in.nextString();
            continue;
          } 
          in.skipValue();
        } 
        if (action != null && action.readable() && value != null)
          style.clickEvent(ClickEvent.clickEvent(action, value)); 
        in.endObject();
        continue;
      } 
      if (fieldName.equals("hoverEvent")) {
        JsonObject hoverEventObject = (JsonObject)this.gson.fromJson(in, JsonObject.class);
        if (hoverEventObject != null) {
          JsonPrimitive serializedAction = hoverEventObject.getAsJsonPrimitive("action");
          if (serializedAction == null)
            continue; 
          HoverEvent.Action<Object> action = (HoverEvent.Action<Object>)this.gson.fromJson((JsonElement)serializedAction, SerializerFactory.HOVER_ACTION_TYPE);
          if (action.readable()) {
            Object value;
            Class<?> actionType = action.type();
            if (hoverEventObject.has("contents")) {
              JsonElement rawValue = hoverEventObject.get("contents");
              if (GsonHacks.isNullOrEmpty(rawValue)) {
                if (this.strictEventValues)
                  throw ComponentSerializerImpl.notSureHowToDeserialize(rawValue); 
                value = null;
              } else if (SerializerFactory.COMPONENT_TYPE.isAssignableFrom(actionType)) {
                value = this.gson.fromJson(rawValue, SerializerFactory.COMPONENT_TYPE);
              } else if (SerializerFactory.SHOW_ITEM_TYPE.isAssignableFrom(actionType)) {
                value = this.gson.fromJson(rawValue, SerializerFactory.SHOW_ITEM_TYPE);
              } else if (SerializerFactory.SHOW_ENTITY_TYPE.isAssignableFrom(actionType)) {
                value = this.gson.fromJson(rawValue, SerializerFactory.SHOW_ENTITY_TYPE);
              } else {
                value = null;
              } 
            } else if (hoverEventObject.has("value")) {
              JsonElement element = hoverEventObject.get("value");
              if (GsonHacks.isNullOrEmpty(element)) {
                if (this.strictEventValues)
                  throw ComponentSerializerImpl.notSureHowToDeserialize(element); 
                value = null;
              } else if (SerializerFactory.COMPONENT_TYPE.isAssignableFrom(actionType)) {
                Component rawValue = (Component)this.gson.fromJson(element, SerializerFactory.COMPONENT_TYPE);
                value = legacyHoverEventContents(action, rawValue);
              } else if (SerializerFactory.STRING_TYPE.isAssignableFrom(actionType)) {
                value = this.gson.fromJson(element, SerializerFactory.STRING_TYPE);
              } else {
                value = null;
              } 
            } else {
              if (this.strictEventValues)
                throw ComponentSerializerImpl.notSureHowToDeserialize(hoverEventObject); 
              value = null;
            } 
            if (value != null)
              style.hoverEvent((HoverEventSource)HoverEvent.hoverEvent(action, value)); 
          } 
        } 
        continue;
      } 
      in.skipValue();
    } 
    in.endObject();
    return style.build();
  }
  
  private Object legacyHoverEventContents(HoverEvent.Action<?> action, Component rawValue) {
    if (action == HoverEvent.Action.SHOW_TEXT)
      return rawValue; 
    if (this.legacyHover != null)
      try {
        if (action == HoverEvent.Action.SHOW_ENTITY)
          return this.legacyHover.deserializeShowEntity(rawValue, decoder()); 
        if (action == HoverEvent.Action.SHOW_ITEM)
          return this.legacyHover.deserializeShowItem(rawValue); 
      } catch (IOException ex) {
        throw new JsonParseException(ex);
      }  
    throw new UnsupportedOperationException();
  }
  
  private Codec.Decoder<Component, String, JsonParseException> decoder() {
    return string -> (Component)this.gson.fromJson(string, SerializerFactory.COMPONENT_TYPE);
  }
  
  private Codec.Encoder<Component, String, JsonParseException> encoder() {
    return component -> this.gson.toJson(component, SerializerFactory.COMPONENT_TYPE);
  }
  
  public void write(JsonWriter out, Style value) throws IOException {
    out.beginObject();
    for (int i = 0, length = DECORATIONS.length; i < length; i++) {
      TextDecoration decoration = DECORATIONS[i];
      TextDecoration.State state = value.decoration(decoration);
      if (state != TextDecoration.State.NOT_SET) {
        String name = (String)TextDecoration.NAMES.key(decoration);
        assert name != null;
        out.name(name);
        out.value((state == TextDecoration.State.TRUE));
      } 
    } 
    TextColor color = value.color();
    if (color != null) {
      out.name("color");
      this.gson.toJson(color, SerializerFactory.COLOR_TYPE, out);
    } 
    String insertion = value.insertion();
    if (insertion != null) {
      out.name("insertion");
      out.value(insertion);
    } 
    ClickEvent clickEvent = value.clickEvent();
    if (clickEvent != null) {
      out.name("clickEvent");
      out.beginObject();
      out.name("action");
      this.gson.toJson(clickEvent.action(), SerializerFactory.CLICK_ACTION_TYPE, out);
      out.name("value");
      out.value(clickEvent.value());
      out.endObject();
    } 
    HoverEvent<?> hoverEvent = value.hoverEvent();
    if (hoverEvent != null && ((this.emitModernHover && hoverEvent.action() != HoverEvent.Action.SHOW_ACHIEVEMENT) || this.emitLegacyHover)) {
      out.name("hoverEvent");
      out.beginObject();
      out.name("action");
      HoverEvent.Action<?> action = hoverEvent.action();
      this.gson.toJson(action, SerializerFactory.HOVER_ACTION_TYPE, out);
      if (this.emitModernHover && action != HoverEvent.Action.SHOW_ACHIEVEMENT) {
        out.name("contents");
        if (action == HoverEvent.Action.SHOW_ITEM) {
          this.gson.toJson(hoverEvent.value(), SerializerFactory.SHOW_ITEM_TYPE, out);
        } else if (action == HoverEvent.Action.SHOW_ENTITY) {
          this.gson.toJson(hoverEvent.value(), SerializerFactory.SHOW_ENTITY_TYPE, out);
        } else if (action == HoverEvent.Action.SHOW_TEXT) {
          this.gson.toJson(hoverEvent.value(), SerializerFactory.COMPONENT_TYPE, out);
        } else {
          throw new JsonParseException("Don't know how to serialize " + hoverEvent.value());
        } 
      } 
      if (this.emitLegacyHover) {
        out.name("value");
        serializeLegacyHoverEvent(hoverEvent, out);
      } 
      out.endObject();
    } 
    Key font = value.font();
    if (font != null) {
      out.name("font");
      this.gson.toJson(font, SerializerFactory.KEY_TYPE, out);
    } 
    out.endObject();
  }
  
  private void serializeLegacyHoverEvent(HoverEvent<?> hoverEvent, JsonWriter out) throws IOException {
    if (hoverEvent.action() == HoverEvent.Action.SHOW_TEXT) {
      this.gson.toJson(hoverEvent.value(), SerializerFactory.COMPONENT_TYPE, out);
    } else if (hoverEvent.action() == HoverEvent.Action.SHOW_ACHIEVEMENT) {
      this.gson.toJson(hoverEvent.value(), String.class, out);
    } else if (this.legacyHover != null) {
      Component serialized = null;
      try {
        if (hoverEvent.action() == HoverEvent.Action.SHOW_ENTITY) {
          serialized = this.legacyHover.serializeShowEntity((HoverEvent.ShowEntity)hoverEvent.value(), encoder());
        } else if (hoverEvent.action() == HoverEvent.Action.SHOW_ITEM) {
          serialized = this.legacyHover.serializeShowItem((HoverEvent.ShowItem)hoverEvent.value());
        } 
      } catch (IOException ex) {
        throw new JsonSyntaxException(ex);
      } 
      if (serialized != null) {
        this.gson.toJson(serialized, SerializerFactory.COMPONENT_TYPE, out);
      } else {
        out.nullValue();
      } 
    } else {
      out.nullValue();
    } 
  }
}
