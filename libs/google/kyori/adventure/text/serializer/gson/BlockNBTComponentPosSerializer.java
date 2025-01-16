package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.gson;

import java.io.IOException;
import me.syncwrld.booter.libs.google.gson.JsonParseException;
import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonWriter;
import me.syncwrld.booter.libs.google.kyori.adventure.text.BlockNBTComponent;

final class BlockNBTComponentPosSerializer extends TypeAdapter<BlockNBTComponent.Pos> {
  static final TypeAdapter<BlockNBTComponent.Pos> INSTANCE = (new BlockNBTComponentPosSerializer()).nullSafe();
  
  public BlockNBTComponent.Pos read(JsonReader in) throws IOException {
    String string = in.nextString();
    try {
      return BlockNBTComponent.Pos.fromString(string);
    } catch (IllegalArgumentException ex) {
      throw new JsonParseException("Don't know how to turn " + string + " into a Position");
    } 
  }
  
  public void write(JsonWriter out, BlockNBTComponent.Pos value) throws IOException {
    out.value(value.asString());
  }
}
