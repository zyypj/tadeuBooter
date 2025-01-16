package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.gson;

import java.io.IOException;
import me.syncwrld.booter.libs.google.gson.JsonParseException;
import me.syncwrld.booter.libs.google.gson.JsonSyntaxException;
import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonWriter;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.TextColor;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.TextDecoration;
import me.syncwrld.booter.libs.jtann.Nullable;

final class TextColorWrapper {
  @Nullable
  final TextColor color;
  
  @Nullable
  final TextDecoration decoration;
  
  final boolean reset;
  
  TextColorWrapper(@Nullable TextColor color, @Nullable TextDecoration decoration, boolean reset) {
    this.color = color;
    this.decoration = decoration;
    this.reset = reset;
  }
  
  static final class Serializer extends TypeAdapter<TextColorWrapper> {
    static final Serializer INSTANCE = new Serializer();
    
    public void write(JsonWriter out, TextColorWrapper value) {
      throw new JsonSyntaxException("Cannot write TextColorWrapper instances");
    }
    
    public TextColorWrapper read(JsonReader in) throws IOException {
      String input = in.nextString();
      TextColor color = TextColorSerializer.fromString(input);
      TextDecoration decoration = (TextDecoration)TextDecoration.NAMES.value(input);
      boolean reset = (decoration == null && input.equals("reset"));
      if (color == null && decoration == null && !reset)
        throw new JsonParseException("Don't know how to parse " + input + " at " + in.getPath()); 
      return new TextColorWrapper(color, decoration, reset);
    }
  }
}
