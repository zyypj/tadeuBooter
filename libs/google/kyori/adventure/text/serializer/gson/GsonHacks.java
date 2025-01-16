package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.gson;

import java.io.IOException;
import me.syncwrld.booter.libs.google.gson.JsonElement;
import me.syncwrld.booter.libs.google.gson.JsonParseException;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonToken;
import me.syncwrld.booter.libs.jtann.Nullable;

final class GsonHacks {
  static boolean isNullOrEmpty(@Nullable JsonElement element) {
    return (element == null || element
      .isJsonNull() || (element
      .isJsonArray() && element.getAsJsonArray().size() == 0) || (element
      .isJsonObject() && element.getAsJsonObject().entrySet().isEmpty()));
  }
  
  static boolean readBoolean(JsonReader in) throws IOException {
    JsonToken peek = in.peek();
    if (peek == JsonToken.BOOLEAN)
      return in.nextBoolean(); 
    if (peek == JsonToken.STRING || peek == JsonToken.NUMBER)
      return Boolean.parseBoolean(in.nextString()); 
    throw new JsonParseException("Token of type " + peek + " cannot be interpreted as a boolean");
  }
  
  static String readString(JsonReader in) throws IOException {
    JsonToken peek = in.peek();
    if (peek == JsonToken.STRING || peek == JsonToken.NUMBER)
      return in.nextString(); 
    if (peek == JsonToken.BOOLEAN)
      return String.valueOf(in.nextBoolean()); 
    throw new JsonParseException("Token of type " + peek + " cannot be interpreted as a string");
  }
}
