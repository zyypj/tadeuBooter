package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.gson;

import java.io.IOException;
import java.util.UUID;
import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonWriter;
import me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.json.JSONOptions;
import me.syncwrld.booter.libs.google.kyori.option.OptionState;

final class UUIDSerializer extends TypeAdapter<UUID> {
  private final boolean emitIntArray;
  
  static TypeAdapter<UUID> uuidSerializer(OptionState features) {
    return (new UUIDSerializer(((Boolean)features.value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY)).booleanValue())).nullSafe();
  }
  
  private UUIDSerializer(boolean emitIntArray) {
    this.emitIntArray = emitIntArray;
  }
  
  public void write(JsonWriter out, UUID value) throws IOException {
    if (this.emitIntArray) {
      int msb0 = (int)(value.getMostSignificantBits() >> 32L);
      int msb1 = (int)(value.getMostSignificantBits() & 0xFFFFFFFFL);
      int lsb0 = (int)(value.getLeastSignificantBits() >> 32L);
      int lsb1 = (int)(value.getLeastSignificantBits() & 0xFFFFFFFFL);
      out.beginArray()
        .value(msb0)
        .value(msb1)
        .value(lsb0)
        .value(lsb1)
        .endArray();
    } else {
      out.value(value.toString());
    } 
  }
  
  public UUID read(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.BEGIN_ARRAY) {
      in.beginArray();
      int msb0 = in.nextInt();
      int msb1 = in.nextInt();
      int lsb0 = in.nextInt();
      int lsb1 = in.nextInt();
      in.endArray();
      return new UUID(msb0 << 32L | msb1 & 0xFFFFFFFFL, lsb0 << 32L | lsb1 & 0xFFFFFFFFL);
    } 
    return UUID.fromString(in.nextString());
  }
}
