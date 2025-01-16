package me.syncwrld.booter.libs.google.gson.internal.sql;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import me.syncwrld.booter.libs.google.gson.Gson;
import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.gson.TypeAdapterFactory;
import me.syncwrld.booter.libs.google.gson.reflect.TypeToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonWriter;

class SqlTimestampTypeAdapter extends TypeAdapter<Timestamp> {
  static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        if (typeToken.getRawType() == Timestamp.class) {
          TypeAdapter<Date> dateTypeAdapter = gson.getAdapter(Date.class);
          return new SqlTimestampTypeAdapter(dateTypeAdapter);
        } 
        return null;
      }
    };
  
  private final TypeAdapter<Date> dateTypeAdapter;
  
  private SqlTimestampTypeAdapter(TypeAdapter<Date> dateTypeAdapter) {
    this.dateTypeAdapter = dateTypeAdapter;
  }
  
  public Timestamp read(JsonReader in) throws IOException {
    Date date = (Date)this.dateTypeAdapter.read(in);
    return (date != null) ? new Timestamp(date.getTime()) : null;
  }
  
  public void write(JsonWriter out, Timestamp value) throws IOException {
    this.dateTypeAdapter.write(out, value);
  }
}
