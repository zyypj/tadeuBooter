package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.gson;

import java.io.IOException;
import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonWriter;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;

final class KeySerializer extends TypeAdapter<Key> {
  static final TypeAdapter<Key> INSTANCE = (new KeySerializer()).nullSafe();
  
  public void write(JsonWriter out, Key value) throws IOException {
    out.value(value.asString());
  }
  
  public Key read(JsonReader in) throws IOException {
    return Key.key(in.nextString());
  }
}
