package me.syncwrld.booter.libs.google.gson.internal.sql;

import java.io.IOException;
import java.sql.Date;
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

final class SqlDateTypeAdapter extends TypeAdapter<Date> {
  static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        return (typeToken.getRawType() == Date.class) ? 
          new SqlDateTypeAdapter() : null;
      }
    };
  
  private final DateFormat format = new SimpleDateFormat("MMM d, yyyy");
  
  public Date read(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.NULL) {
      in.nextNull();
      return null;
    } 
    String s = in.nextString();
    try {
      Date utilDate;
      synchronized (this) {
        utilDate = this.format.parse(s);
      } 
      return new Date(utilDate.getTime());
    } catch (ParseException e) {
      Date utilDate;
      throw new JsonSyntaxException("Failed parsing '" + s + "' as SQL Date; at path " + in.getPreviousPath(), utilDate);
    } 
  }
  
  public void write(JsonWriter out, Date value) throws IOException {
    String dateString;
    if (value == null) {
      out.nullValue();
      return;
    } 
    synchronized (this) {
      dateString = this.format.format(value);
    } 
    out.value(dateString);
  }
  
  private SqlDateTypeAdapter() {}
}
