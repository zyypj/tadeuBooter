package me.syncwrld.booter.libs.google.gson.internal.bind;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import me.syncwrld.booter.libs.google.gson.Gson;
import me.syncwrld.booter.libs.google.gson.JsonSyntaxException;
import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.gson.TypeAdapterFactory;
import me.syncwrld.booter.libs.google.gson.internal.JavaVersion;
import me.syncwrld.booter.libs.google.gson.internal.PreJava9DateFormatProvider;
import me.syncwrld.booter.libs.google.gson.internal.bind.util.ISO8601Utils;
import me.syncwrld.booter.libs.google.gson.reflect.TypeToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonWriter;

public final class DateTypeAdapter extends TypeAdapter<Date> {
  public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        return (typeToken.getRawType() == Date.class) ? new DateTypeAdapter() : null;
      }
    };
  
  private final List<DateFormat> dateFormats = new ArrayList<>();
  
  public DateTypeAdapter() {
    this.dateFormats.add(DateFormat.getDateTimeInstance(2, 2, Locale.US));
    if (!Locale.getDefault().equals(Locale.US))
      this.dateFormats.add(DateFormat.getDateTimeInstance(2, 2)); 
    if (JavaVersion.isJava9OrLater())
      this.dateFormats.add(PreJava9DateFormatProvider.getUSDateTimeFormat(2, 2)); 
  }
  
  public Date read(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.NULL) {
      in.nextNull();
      return null;
    } 
    return deserializeToDate(in);
  }
  
  private Date deserializeToDate(JsonReader in) throws IOException {
    String s = in.nextString();
    synchronized (this.dateFormats) {
      for (DateFormat dateFormat : this.dateFormats) {
        try {
          return dateFormat.parse(s);
        } catch (ParseException parseException) {}
      } 
    } 
    try {
      return ISO8601Utils.parse(s, new ParsePosition(0));
    } catch (ParseException e) {
      throw new JsonSyntaxException("Failed parsing '" + s + "' as Date; at path " + in.getPreviousPath(), e);
    } 
  }
  
  public void write(JsonWriter out, Date value) throws IOException {
    String dateFormatAsString;
    if (value == null) {
      out.nullValue();
      return;
    } 
    DateFormat dateFormat = this.dateFormats.get(0);
    synchronized (this.dateFormats) {
      dateFormatAsString = dateFormat.format(value);
    } 
    out.value(dateFormatAsString);
  }
}
