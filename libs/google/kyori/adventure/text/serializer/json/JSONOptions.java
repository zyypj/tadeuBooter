package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.json;

import me.syncwrld.booter.libs.google.kyori.option.Option;
import me.syncwrld.booter.libs.google.kyori.option.OptionState;
import me.syncwrld.booter.libs.jtann.NotNull;

public final class JSONOptions {
  private static final int VERSION_INITIAL = 0;
  
  private static final int VERSION_1_16 = 2526;
  
  private static final int VERSION_1_20_3 = 3679;
  
  public static final Option<Boolean> EMIT_RGB = Option.booleanOption(key("emit/rgb"), true);
  
  public static final Option<HoverEventValueMode> EMIT_HOVER_EVENT_TYPE = Option.enumOption(key("emit/hover_value_mode"), HoverEventValueMode.class, HoverEventValueMode.MODERN_ONLY);
  
  public static final Option<Boolean> EMIT_COMPACT_TEXT_COMPONENT = Option.booleanOption(key("emit/compact_text_component"), true);
  
  public static final Option<Boolean> EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY = Option.booleanOption(key("emit/hover_show_entity_id_as_int_array"), true);
  
  public static final Option<Boolean> VALIDATE_STRICT_EVENTS = Option.booleanOption(key("validate/strict_events"), true);
  
  private static final OptionState.Versioned BY_DATA_VERSION;
  
  static {
    BY_DATA_VERSION = OptionState.versionedOptionState().version(0, b -> b.value(EMIT_HOVER_EVENT_TYPE, HoverEventValueMode.LEGACY_ONLY).value(EMIT_RGB, Boolean.valueOf(false)).value(EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, Boolean.valueOf(false)).value(VALIDATE_STRICT_EVENTS, Boolean.valueOf(false))).version(2526, b -> b.value(EMIT_HOVER_EVENT_TYPE, HoverEventValueMode.MODERN_ONLY).value(EMIT_RGB, Boolean.valueOf(true))).version(3679, b -> b.value(EMIT_COMPACT_TEXT_COMPONENT, Boolean.valueOf(true)).value(EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, Boolean.valueOf(true)).value(VALIDATE_STRICT_EVENTS, Boolean.valueOf(true))).build();
  }
  
  private static final OptionState MOST_COMPATIBLE = OptionState.optionState()
    .value(EMIT_HOVER_EVENT_TYPE, HoverEventValueMode.BOTH)
    .value(EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, Boolean.valueOf(false))
    .value(EMIT_COMPACT_TEXT_COMPONENT, Boolean.valueOf(false))
    .value(VALIDATE_STRICT_EVENTS, Boolean.valueOf(false))
    .build();
  
  private static String key(String value) {
    return "adventure:json/" + value;
  }
  
  public static OptionState.Versioned byDataVersion() {
    return BY_DATA_VERSION;
  }
  
  @NotNull
  public static OptionState compatibility() {
    return MOST_COMPATIBLE;
  }
  
  public enum HoverEventValueMode {
    MODERN_ONLY, LEGACY_ONLY, BOTH;
  }
}
