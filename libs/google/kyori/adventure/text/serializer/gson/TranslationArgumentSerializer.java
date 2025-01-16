package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.gson;

import java.io.IOException;
import me.syncwrld.booter.libs.google.gson.Gson;
import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonWriter;
import me.syncwrld.booter.libs.google.kyori.adventure.text.ComponentLike;
import me.syncwrld.booter.libs.google.kyori.adventure.text.TranslationArgument;

final class TranslationArgumentSerializer extends TypeAdapter<TranslationArgument> {
  private final Gson gson;
  
  static TypeAdapter<TranslationArgument> create(Gson gson) {
    return (new TranslationArgumentSerializer(gson)).nullSafe();
  }
  
  private TranslationArgumentSerializer(Gson gson) {
    this.gson = gson;
  }
  
  public void write(JsonWriter out, TranslationArgument value) throws IOException {
    Object raw = value.value();
    if (raw instanceof Boolean) {
      out.value((Boolean)raw);
    } else if (raw instanceof Number) {
      out.value((Number)raw);
    } else if (raw instanceof me.syncwrld.booter.libs.google.kyori.adventure.text.Component) {
      this.gson.toJson(raw, SerializerFactory.COMPONENT_TYPE, out);
    } else {
      throw new IllegalStateException("Unable to serialize translatable argument of type " + raw.getClass() + ": " + raw);
    } 
  }
  
  public TranslationArgument read(JsonReader in) throws IOException {
    switch (in.peek()) {
      case BOOLEAN:
        return TranslationArgument.bool(in.nextBoolean());
      case NUMBER:
        return TranslationArgument.numeric((Number)this.gson.fromJson(in, Number.class));
    } 
    return TranslationArgument.component((ComponentLike)this.gson.fromJson(in, SerializerFactory.COMPONENT_TYPE));
  }
}
