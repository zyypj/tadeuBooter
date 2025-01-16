package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.gson;

import java.io.IOException;
import java.util.Locale;
import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonWriter;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.NamedTextColor;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.TextColor;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class TextColorSerializer extends TypeAdapter<TextColor> {
  static final TypeAdapter<TextColor> INSTANCE = (new TextColorSerializer(false)).nullSafe();
  
  static final TypeAdapter<TextColor> DOWNSAMPLE_COLOR = (new TextColorSerializer(true)).nullSafe();
  
  private final boolean downsampleColor;
  
  private TextColorSerializer(boolean downsampleColor) {
    this.downsampleColor = downsampleColor;
  }
  
  public void write(JsonWriter out, TextColor value) throws IOException {
    if (value instanceof NamedTextColor) {
      out.value((String)NamedTextColor.NAMES.key(value));
    } else if (this.downsampleColor) {
      out.value((String)NamedTextColor.NAMES.key(NamedTextColor.nearestTo(value)));
    } else {
      out.value(asUpperCaseHexString(value));
    } 
  }
  
  private static String asUpperCaseHexString(TextColor color) {
    return String.format(Locale.ROOT, "%c%06X", new Object[] { Character.valueOf('#'), Integer.valueOf(color.value()) });
  }
  
  @Nullable
  public TextColor read(JsonReader in) throws IOException {
    TextColor color = fromString(in.nextString());
    if (color == null)
      return null; 
    return this.downsampleColor ? (TextColor)NamedTextColor.nearestTo(color) : color;
  }
  
  @Nullable
  static TextColor fromString(@NotNull String value) {
    if (value.startsWith("#"))
      return TextColor.fromHexString(value); 
    return (TextColor)NamedTextColor.NAMES.value(value);
  }
}
