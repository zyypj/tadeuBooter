package me.syncwrld.booter.libs.google.gson.internal.bind;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import me.syncwrld.booter.libs.google.gson.JsonSyntaxException;
import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.gson.TypeAdapterFactory;
import me.syncwrld.booter.libs.google.gson.internal.JavaVersion;
import me.syncwrld.booter.libs.google.gson.internal.PreJava9DateFormatProvider;
import me.syncwrld.booter.libs.google.gson.internal.bind.util.ISO8601Utils;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonWriter;

public final class DefaultDateTypeAdapter<T extends Date> extends TypeAdapter<T> {
  private static final String SIMPLE_NAME = "DefaultDateTypeAdapter";
  
  private final DateType<T> dateType;
  
  public static abstract class DateType<T extends Date> {
    public static final DateType<Date> DATE = new DateType<Date>(Date.class) {
        protected Date deserialize(Date date) {
          return date;
        }
      };
    
    private final Class<T> dateClass;
    
    protected DateType(Class<T> dateClass) {
      this.dateClass = dateClass;
    }
    
    protected abstract T deserialize(Date param1Date);
    
    private TypeAdapterFactory createFactory(DefaultDateTypeAdapter<T> adapter) {
      return TypeAdapters.newFactory(this.dateClass, adapter);
    }
    
    public final TypeAdapterFactory createAdapterFactory(String datePattern) {
      return createFactory(new DefaultDateTypeAdapter<>(this, datePattern));
    }
    
    public final TypeAdapterFactory createAdapterFactory(int style) {
      return createFactory(new DefaultDateTypeAdapter<>(this, style));
    }
    
    public final TypeAdapterFactory createAdapterFactory(int dateStyle, int timeStyle) {
      return createFactory(new DefaultDateTypeAdapter<>(this, dateStyle, timeStyle));
    }
    
    public final TypeAdapterFactory createDefaultsAdapterFactory() {
      return createFactory(new DefaultDateTypeAdapter<>(this, 2, 2));
    }
  }
  
  private final List<DateFormat> dateFormats = new ArrayList<>();
  
  private DefaultDateTypeAdapter(DateType<T> dateType, String datePattern) {
    this.dateType = Objects.<DateType<T>>requireNonNull(dateType);
    this.dateFormats.add(new SimpleDateFormat(datePattern, Locale.US));
    if (!Locale.getDefault().equals(Locale.US))
      this.dateFormats.add(new SimpleDateFormat(datePattern)); 
  }
  
  private DefaultDateTypeAdapter(DateType<T> dateType, int style) {
    this.dateType = Objects.<DateType<T>>requireNonNull(dateType);
    this.dateFormats.add(DateFormat.getDateInstance(style, Locale.US));
    if (!Locale.getDefault().equals(Locale.US))
      this.dateFormats.add(DateFormat.getDateInstance(style)); 
    if (JavaVersion.isJava9OrLater())
      this.dateFormats.add(PreJava9DateFormatProvider.getUSDateFormat(style)); 
  }
  
  private DefaultDateTypeAdapter(DateType<T> dateType, int dateStyle, int timeStyle) {
    this.dateType = Objects.<DateType<T>>requireNonNull(dateType);
    this.dateFormats.add(DateFormat.getDateTimeInstance(dateStyle, timeStyle, Locale.US));
    if (!Locale.getDefault().equals(Locale.US))
      this.dateFormats.add(DateFormat.getDateTimeInstance(dateStyle, timeStyle)); 
    if (JavaVersion.isJava9OrLater())
      this.dateFormats.add(PreJava9DateFormatProvider.getUSDateTimeFormat(dateStyle, timeStyle)); 
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
  
  public T read(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.NULL) {
      in.nextNull();
      return null;
    } 
    Date date = deserializeToDate(in);
    return this.dateType.deserialize(date);
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
  
  public String toString() {
    DateFormat defaultFormat = this.dateFormats.get(0);
    if (defaultFormat instanceof SimpleDateFormat)
      return "DefaultDateTypeAdapter(" + ((SimpleDateFormat)defaultFormat).toPattern() + ')'; 
    return "DefaultDateTypeAdapter(" + defaultFormat.getClass().getSimpleName() + ')';
  }
}
