package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.gson;

import java.util.UUID;
import me.syncwrld.booter.libs.google.gson.Gson;
import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.gson.TypeAdapterFactory;
import me.syncwrld.booter.libs.google.gson.reflect.TypeToken;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.text.BlockNBTComponent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.text.TranslationArgument;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.ClickEvent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.HoverEvent;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.Style;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.TextColor;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.TextDecoration;
import me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.json.JSONOptions;
import me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.json.LegacyHoverEventSerializer;
import me.syncwrld.booter.libs.google.kyori.option.OptionState;

final class SerializerFactory implements TypeAdapterFactory {
  static final Class<Key> KEY_TYPE = Key.class;
  
  static final Class<Component> COMPONENT_TYPE = Component.class;
  
  static final Class<Style> STYLE_TYPE = Style.class;
  
  static final Class<ClickEvent.Action> CLICK_ACTION_TYPE = ClickEvent.Action.class;
  
  static final Class<HoverEvent.Action> HOVER_ACTION_TYPE = HoverEvent.Action.class;
  
  static final Class<HoverEvent.ShowItem> SHOW_ITEM_TYPE = HoverEvent.ShowItem.class;
  
  static final Class<HoverEvent.ShowEntity> SHOW_ENTITY_TYPE = HoverEvent.ShowEntity.class;
  
  static final Class<String> STRING_TYPE = String.class;
  
  static final Class<TextColorWrapper> COLOR_WRAPPER_TYPE = TextColorWrapper.class;
  
  static final Class<TextColor> COLOR_TYPE = TextColor.class;
  
  static final Class<TextDecoration> TEXT_DECORATION_TYPE = TextDecoration.class;
  
  static final Class<BlockNBTComponent.Pos> BLOCK_NBT_POS_TYPE = BlockNBTComponent.Pos.class;
  
  static final Class<UUID> UUID_TYPE = UUID.class;
  
  static final Class<TranslationArgument> TRANSLATION_ARGUMENT_TYPE = TranslationArgument.class;
  
  private final OptionState features;
  
  private final LegacyHoverEventSerializer legacyHoverSerializer;
  
  SerializerFactory(OptionState features, LegacyHoverEventSerializer legacyHoverSerializer) {
    this.features = features;
    this.legacyHoverSerializer = legacyHoverSerializer;
  }
  
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    Class<? super T> rawType = type.getRawType();
    if (COMPONENT_TYPE.isAssignableFrom(rawType))
      return (TypeAdapter)ComponentSerializerImpl.create(this.features, gson); 
    if (KEY_TYPE.isAssignableFrom(rawType))
      return (TypeAdapter)KeySerializer.INSTANCE; 
    if (STYLE_TYPE.isAssignableFrom(rawType))
      return (TypeAdapter)StyleSerializer.create(this.legacyHoverSerializer, this.features, gson); 
    if (CLICK_ACTION_TYPE.isAssignableFrom(rawType))
      return (TypeAdapter)ClickEventActionSerializer.INSTANCE; 
    if (HOVER_ACTION_TYPE.isAssignableFrom(rawType))
      return (TypeAdapter)HoverEventActionSerializer.INSTANCE; 
    if (SHOW_ITEM_TYPE.isAssignableFrom(rawType))
      return (TypeAdapter)ShowItemSerializer.create(gson); 
    if (SHOW_ENTITY_TYPE.isAssignableFrom(rawType))
      return (TypeAdapter)ShowEntitySerializer.create(gson); 
    if (COLOR_WRAPPER_TYPE.isAssignableFrom(rawType))
      return TextColorWrapper.Serializer.INSTANCE; 
    if (COLOR_TYPE.isAssignableFrom(rawType))
      return ((Boolean)this.features.value(JSONOptions.EMIT_RGB)).booleanValue() ? (TypeAdapter)TextColorSerializer.INSTANCE : (TypeAdapter)TextColorSerializer.DOWNSAMPLE_COLOR; 
    if (TEXT_DECORATION_TYPE.isAssignableFrom(rawType))
      return (TypeAdapter)TextDecorationSerializer.INSTANCE; 
    if (BLOCK_NBT_POS_TYPE.isAssignableFrom(rawType))
      return (TypeAdapter)BlockNBTComponentPosSerializer.INSTANCE; 
    if (UUID_TYPE.isAssignableFrom(rawType))
      return (TypeAdapter)UUIDSerializer.uuidSerializer(this.features); 
    if (TRANSLATION_ARGUMENT_TYPE.isAssignableFrom(rawType))
      return (TypeAdapter)TranslationArgumentSerializer.create(gson); 
    return null;
  }
}
