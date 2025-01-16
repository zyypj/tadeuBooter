package me.syncwrld.booter.libs.google.kyori.adventure.text.serializer.gson;

import java.io.IOException;
import java.util.UUID;
import me.syncwrld.booter.libs.google.gson.Gson;
import me.syncwrld.booter.libs.google.gson.JsonParseException;
import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonWriter;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.text.event.HoverEvent;

final class ShowEntitySerializer extends TypeAdapter<HoverEvent.ShowEntity> {
  private final Gson gson;
  
  static TypeAdapter<HoverEvent.ShowEntity> create(Gson gson) {
    return (new ShowEntitySerializer(gson)).nullSafe();
  }
  
  private ShowEntitySerializer(Gson gson) {
    this.gson = gson;
  }
  
  public HoverEvent.ShowEntity read(JsonReader in) throws IOException {
    in.beginObject();
    Key type = null;
    UUID id = null;
    Component name = null;
    while (in.hasNext()) {
      String fieldName = in.nextName();
      if (fieldName.equals("type")) {
        type = (Key)this.gson.fromJson(in, SerializerFactory.KEY_TYPE);
        continue;
      } 
      if (fieldName.equals("id")) {
        id = (UUID)this.gson.fromJson(in, SerializerFactory.UUID_TYPE);
        continue;
      } 
      if (fieldName.equals("name")) {
        name = (Component)this.gson.fromJson(in, SerializerFactory.COMPONENT_TYPE);
        continue;
      } 
      in.skipValue();
    } 
    if (type == null || id == null)
      throw new JsonParseException("A show entity hover event needs type and id fields to be deserialized"); 
    in.endObject();
    return HoverEvent.ShowEntity.showEntity(type, id, name);
  }
  
  public void write(JsonWriter out, HoverEvent.ShowEntity value) throws IOException {
    out.beginObject();
    out.name("type");
    this.gson.toJson(value.type(), SerializerFactory.KEY_TYPE, out);
    out.name("id");
    this.gson.toJson(value.id(), SerializerFactory.UUID_TYPE, out);
    Component name = value.name();
    if (name != null) {
      out.name("name");
      this.gson.toJson(name, SerializerFactory.COMPONENT_TYPE, out);
    } 
    out.endObject();
  }
}
