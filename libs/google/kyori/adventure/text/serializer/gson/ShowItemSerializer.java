package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.gson;

import java.io.IOException;
import me.syncwrld.booter.libs.google.gson.Gson;
import me.syncwrld.booter.libs.google.gson.JsonParseException;
import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonWriter;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.nbt.api.BinaryTagHolder;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.HoverEvent;

final class ShowItemSerializer extends TypeAdapter<HoverEvent.ShowItem> {
  private final Gson gson;
  
  static TypeAdapter<HoverEvent.ShowItem> create(Gson gson) {
    return (new ShowItemSerializer(gson)).nullSafe();
  }
  
  private ShowItemSerializer(Gson gson) {
    this.gson = gson;
  }
  
  public HoverEvent.ShowItem read(JsonReader in) throws IOException {
    in.beginObject();
    Key key = null;
    int count = 1;
    BinaryTagHolder nbt = null;
    while (in.hasNext()) {
      String fieldName = in.nextName();
      if (fieldName.equals("id")) {
        key = (Key)this.gson.fromJson(in, SerializerFactory.KEY_TYPE);
        continue;
      } 
      if (fieldName.equals("count")) {
        count = in.nextInt();
        continue;
      } 
      if (fieldName.equals("tag")) {
        JsonToken token = in.peek();
        if (token == JsonToken.STRING || token == JsonToken.NUMBER) {
          nbt = BinaryTagHolder.binaryTagHolder(in.nextString());
          continue;
        } 
        if (token == JsonToken.BOOLEAN) {
          nbt = BinaryTagHolder.binaryTagHolder(String.valueOf(in.nextBoolean()));
          continue;
        } 
        if (token == JsonToken.NULL) {
          in.nextNull();
          continue;
        } 
        throw new JsonParseException("Expected tag to be a string");
      } 
      in.skipValue();
    } 
    if (key == null)
      throw new JsonParseException("Not sure how to deserialize show_item hover event"); 
    in.endObject();
    return HoverEvent.ShowItem.showItem(key, count, nbt);
  }
  
  public void write(JsonWriter out, HoverEvent.ShowItem value) throws IOException {
    out.beginObject();
    out.name("id");
    this.gson.toJson(value.item(), SerializerFactory.KEY_TYPE, out);
    int count = value.count();
    if (count != 1) {
      out.name("count");
      out.value(count);
    } 
    BinaryTagHolder nbt = value.nbt();
    if (nbt != null) {
      out.name("tag");
      out.value(nbt.string());
    } 
    out.endObject();
  }
}
