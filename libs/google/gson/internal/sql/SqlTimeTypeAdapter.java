package me.syncwrld.booter.libs.google.gson.internal.sql;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import me.syncwrld.booter.libs.google.gson.Gson;
import me.syncwrld.booter.libs.google.gson.JsonSyntaxException;
import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.gson.TypeAdapterFactory;
import me.syncwrld.booter.libs.google.gson.reflect.TypeToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonWriter;

final class SqlTimeTypeAdapter extends TypeAdapter<Time> {
  static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        return (typeToken.getRawType() == Time.class) ? new SqlTimeTypeAdapter() : null;
      }
    };
  
  private final DateFormat format = new SimpleDateFormat("hh:mm:ss a");
  
  public Time read(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.NULL) {
      in.nextNull();
      return null;
    } 
    String s = in.nextString();
    try {
      synchronized (this) {
        Date date = this.format.parse(s);
        return new Time(date.getTime());
      } 
    } catch (ParseException e) {
      throw new JsonSyntaxException("Failed parsing '" + s + "' as SQL Time; at path " + in.getPreviousPath(), e);
    } 
  }
  
  public void write(JsonWriter out, Time value) throws IOException {
    String timeString;
    if (value == null) {
      out.nullValue();
      return;
    } 
    synchronized (this) {
      timeString = this.format.format(value);
    } 
    out.value(timeString);
  }
  
  private SqlTimeTypeAdapter() {}
}
