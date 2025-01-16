package me.syncwrld.booter.libs.google.gson.internal.bind;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Currency;
import java.util.Deque;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import me.syncwrld.booter.libs.google.gson.Gson;
import me.syncwrld.booter.libs.google.gson.JsonArray;
import me.syncwrld.booter.libs.google.gson.JsonElement;
import me.syncwrld.booter.libs.google.gson.JsonIOException;
import me.syncwrld.booter.libs.google.gson.JsonNull;
import me.syncwrld.booter.libs.google.gson.JsonObject;
import me.syncwrld.booter.libs.google.gson.JsonPrimitive;
import me.syncwrld.booter.libs.google.gson.JsonSyntaxException;
import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.gson.TypeAdapterFactory;
import me.syncwrld.booter.libs.google.gson.annotations.SerializedName;
import me.syncwrld.booter.libs.google.gson.internal.LazilyParsedNumber;
import me.syncwrld.booter.libs.google.gson.reflect.TypeToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonWriter;

public final class TypeAdapters {
  private TypeAdapters() {
    throw new UnsupportedOperationException();
  }
  
  public static final TypeAdapter<Class> CLASS = (new TypeAdapter<Class>() {
      public void write(JsonWriter out, Class value) throws IOException {
        throw new UnsupportedOperationException("Attempted to serialize java.lang.Class: " + value
            .getName() + ". Forgot to register a type adapter?");
      }
      
      public Class read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException("Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?");
      }
    }).nullSafe();
  
  public static final TypeAdapterFactory CLASS_FACTORY = newFactory(Class.class, CLASS);
  
  public static final TypeAdapter<BitSet> BIT_SET = (new TypeAdapter<BitSet>() {
      public BitSet read(JsonReader in) throws IOException {
        BitSet bitset = new BitSet();
        in.beginArray();
        int i = 0;
        JsonToken tokenType = in.peek();
        while (tokenType != JsonToken.END_ARRAY) {
          boolean set;
          int intValue;
          switch (tokenType) {
            case NUMBER:
            case STRING:
              intValue = in.nextInt();
              if (intValue == 0) {
                boolean bool = false;
                break;
              } 
              if (intValue == 1) {
                boolean bool = true;
                break;
              } 
              throw new JsonSyntaxException("Invalid bitset value " + intValue + ", expected 0 or 1; at path " + in.getPreviousPath());
            case BOOLEAN:
              set = in.nextBoolean();
              break;
            default:
              throw new JsonSyntaxException("Invalid bitset value type: " + tokenType + "; at path " + in.getPath());
          } 
          if (set)
            bitset.set(i); 
          i++;
          tokenType = in.peek();
        } 
        in.endArray();
        return bitset;
      }
      
      public void write(JsonWriter out, BitSet src) throws IOException {
        out.beginArray();
        for (int i = 0, length = src.length(); i < length; i++) {
          int value = src.get(i) ? 1 : 0;
          out.value(value);
        } 
        out.endArray();
      }
    }).nullSafe();
  
  public static final TypeAdapterFactory BIT_SET_FACTORY = newFactory(BitSet.class, BIT_SET);
  
  public static final TypeAdapter<Boolean> BOOLEAN = new TypeAdapter<Boolean>() {
      public Boolean read(JsonReader in) throws IOException {
        JsonToken peek = in.peek();
        if (peek == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        if (peek == JsonToken.STRING)
          return Boolean.valueOf(Boolean.parseBoolean(in.nextString())); 
        return Boolean.valueOf(in.nextBoolean());
      }
      
      public void write(JsonWriter out, Boolean value) throws IOException {
        out.value(value);
      }
    };
  
  public static final TypeAdapter<Boolean> BOOLEAN_AS_STRING = new TypeAdapter<Boolean>() {
      public Boolean read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        return Boolean.valueOf(in.nextString());
      }
      
      public void write(JsonWriter out, Boolean value) throws IOException {
        out.value((value == null) ? "null" : value.toString());
      }
    };
  
  public static final TypeAdapterFactory BOOLEAN_FACTORY = newFactory(boolean.class, (Class)Boolean.class, (TypeAdapter)BOOLEAN);
  
  public static final TypeAdapter<Number> BYTE = new TypeAdapter<Number>() {
      public Number read(JsonReader in) throws IOException {
        int intValue;
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        try {
          intValue = in.nextInt();
        } catch (NumberFormatException e) {
          throw new JsonSyntaxException(e);
        } 
        if (intValue > 255 || intValue < -128)
          throw new JsonSyntaxException("Lossy conversion from " + intValue + " to byte; at path " + in.getPreviousPath()); 
        return Byte.valueOf((byte)intValue);
      }
      
      public void write(JsonWriter out, Number value) throws IOException {
        if (value == null) {
          out.nullValue();
        } else {
          out.value(value.byteValue());
        } 
      }
    };
  
  public static final TypeAdapterFactory BYTE_FACTORY = newFactory(byte.class, (Class)Byte.class, (TypeAdapter)BYTE);
  
  public static final TypeAdapter<Number> SHORT = new TypeAdapter<Number>() {
      public Number read(JsonReader in) throws IOException {
        int intValue;
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        try {
          intValue = in.nextInt();
        } catch (NumberFormatException e) {
          throw new JsonSyntaxException(e);
        } 
        if (intValue > 65535 || intValue < -32768)
          throw new JsonSyntaxException("Lossy conversion from " + intValue + " to short; at path " + in.getPreviousPath()); 
        return Short.valueOf((short)intValue);
      }
      
      public void write(JsonWriter out, Number value) throws IOException {
        if (value == null) {
          out.nullValue();
        } else {
          out.value(value.shortValue());
        } 
      }
    };
  
  public static final TypeAdapterFactory SHORT_FACTORY = newFactory(short.class, (Class)Short.class, (TypeAdapter)SHORT);
  
  public static final TypeAdapter<Number> INTEGER = new TypeAdapter<Number>() {
      public Number read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        try {
          return Integer.valueOf(in.nextInt());
        } catch (NumberFormatException e) {
          throw new JsonSyntaxException(e);
        } 
      }
      
      public void write(JsonWriter out, Number value) throws IOException {
        if (value == null) {
          out.nullValue();
        } else {
          out.value(value.intValue());
        } 
      }
    };
  
  public static final TypeAdapterFactory INTEGER_FACTORY = newFactory(int.class, (Class)Integer.class, (TypeAdapter)INTEGER);
  
  public static final TypeAdapter<AtomicInteger> ATOMIC_INTEGER = (new TypeAdapter<AtomicInteger>() {
      public AtomicInteger read(JsonReader in) throws IOException {
        try {
          return new AtomicInteger(in.nextInt());
        } catch (NumberFormatException e) {
          throw new JsonSyntaxException(e);
        } 
      }
      
      public void write(JsonWriter out, AtomicInteger value) throws IOException {
        out.value(value.get());
      }
    }).nullSafe();
  
  public static final TypeAdapterFactory ATOMIC_INTEGER_FACTORY = newFactory(AtomicInteger.class, ATOMIC_INTEGER);
  
  public static final TypeAdapter<AtomicBoolean> ATOMIC_BOOLEAN = (new TypeAdapter<AtomicBoolean>() {
      public AtomicBoolean read(JsonReader in) throws IOException {
        return new AtomicBoolean(in.nextBoolean());
      }
      
      public void write(JsonWriter out, AtomicBoolean value) throws IOException {
        out.value(value.get());
      }
    }).nullSafe();
  
  public static final TypeAdapterFactory ATOMIC_BOOLEAN_FACTORY = newFactory(AtomicBoolean.class, ATOMIC_BOOLEAN);
  
  public static final TypeAdapter<AtomicIntegerArray> ATOMIC_INTEGER_ARRAY = (new TypeAdapter<AtomicIntegerArray>() {
      public AtomicIntegerArray read(JsonReader in) throws IOException {
        List<Integer> list = new ArrayList<>();
        in.beginArray();
        while (in.hasNext()) {
          try {
            int integer = in.nextInt();
            list.add(Integer.valueOf(integer));
          } catch (NumberFormatException e) {
            throw new JsonSyntaxException(e);
          } 
        } 
        in.endArray();
        int length = list.size();
        AtomicIntegerArray array = new AtomicIntegerArray(length);
        for (int i = 0; i < length; i++)
          array.set(i, ((Integer)list.get(i)).intValue()); 
        return array;
      }
      
      public void write(JsonWriter out, AtomicIntegerArray value) throws IOException {
        out.beginArray();
        for (int i = 0, length = value.length(); i < length; i++)
          out.value(value.get(i)); 
        out.endArray();
      }
    }).nullSafe();
  
  public static final TypeAdapterFactory ATOMIC_INTEGER_ARRAY_FACTORY = newFactory(AtomicIntegerArray.class, ATOMIC_INTEGER_ARRAY);
  
  public static final TypeAdapter<Number> LONG = new TypeAdapter<Number>() {
      public Number read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        try {
          return Long.valueOf(in.nextLong());
        } catch (NumberFormatException e) {
          throw new JsonSyntaxException(e);
        } 
      }
      
      public void write(JsonWriter out, Number value) throws IOException {
        if (value == null) {
          out.nullValue();
        } else {
          out.value(value.longValue());
        } 
      }
    };
  
  public static final TypeAdapter<Number> FLOAT = new TypeAdapter<Number>() {
      public Number read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        return Float.valueOf((float)in.nextDouble());
      }
      
      public void write(JsonWriter out, Number value) throws IOException {
        if (value == null) {
          out.nullValue();
        } else {
          Number floatNumber = (value instanceof Float) ? value : Float.valueOf(value.floatValue());
          out.value(floatNumber);
        } 
      }
    };
  
  public static final TypeAdapter<Number> DOUBLE = new TypeAdapter<Number>() {
      public Number read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        return Double.valueOf(in.nextDouble());
      }
      
      public void write(JsonWriter out, Number value) throws IOException {
        if (value == null) {
          out.nullValue();
        } else {
          out.value(value.doubleValue());
        } 
      }
    };
  
  public static final TypeAdapter<Character> CHARACTER = new TypeAdapter<Character>() {
      public Character read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        String str = in.nextString();
        if (str.length() != 1)
          throw new JsonSyntaxException("Expecting character, got: " + str + "; at " + in.getPreviousPath()); 
        return Character.valueOf(str.charAt(0));
      }
      
      public void write(JsonWriter out, Character value) throws IOException {
        out.value((value == null) ? null : String.valueOf(value));
      }
    };
  
  public static final TypeAdapterFactory CHARACTER_FACTORY = newFactory(char.class, (Class)Character.class, (TypeAdapter)CHARACTER);
  
  public static final TypeAdapter<String> STRING = new TypeAdapter<String>() {
      public String read(JsonReader in) throws IOException {
        JsonToken peek = in.peek();
        if (peek == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        if (peek == JsonToken.BOOLEAN)
          return Boolean.toString(in.nextBoolean()); 
        return in.nextString();
      }
      
      public void write(JsonWriter out, String value) throws IOException {
        out.value(value);
      }
    };
  
  public static final TypeAdapter<BigDecimal> BIG_DECIMAL = new TypeAdapter<BigDecimal>() {
      public BigDecimal read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        String s = in.nextString();
        try {
          return new BigDecimal(s);
        } catch (NumberFormatException e) {
          throw new JsonSyntaxException("Failed parsing '" + s + "' as BigDecimal; at path " + in.getPreviousPath(), e);
        } 
      }
      
      public void write(JsonWriter out, BigDecimal value) throws IOException {
        out.value(value);
      }
    };
  
  public static final TypeAdapter<BigInteger> BIG_INTEGER = new TypeAdapter<BigInteger>() {
      public BigInteger read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        String s = in.nextString();
        try {
          return new BigInteger(s);
        } catch (NumberFormatException e) {
          throw new JsonSyntaxException("Failed parsing '" + s + "' as BigInteger; at path " + in.getPreviousPath(), e);
        } 
      }
      
      public void write(JsonWriter out, BigInteger value) throws IOException {
        out.value(value);
      }
    };
  
  public static final TypeAdapter<LazilyParsedNumber> LAZILY_PARSED_NUMBER = new TypeAdapter<LazilyParsedNumber>() {
      public LazilyParsedNumber read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        return new LazilyParsedNumber(in.nextString());
      }
      
      public void write(JsonWriter out, LazilyParsedNumber value) throws IOException {
        out.value((Number)value);
      }
    };
  
  public static final TypeAdapterFactory STRING_FACTORY = newFactory(String.class, STRING);
  
  public static final TypeAdapter<StringBuilder> STRING_BUILDER = new TypeAdapter<StringBuilder>() {
      public StringBuilder read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        return new StringBuilder(in.nextString());
      }
      
      public void write(JsonWriter out, StringBuilder value) throws IOException {
        out.value((value == null) ? null : value.toString());
      }
    };
  
  public static final TypeAdapterFactory STRING_BUILDER_FACTORY = newFactory(StringBuilder.class, STRING_BUILDER);
  
  public static final TypeAdapter<StringBuffer> STRING_BUFFER = new TypeAdapter<StringBuffer>() {
      public StringBuffer read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        return new StringBuffer(in.nextString());
      }
      
      public void write(JsonWriter out, StringBuffer value) throws IOException {
        out.value((value == null) ? null : value.toString());
      }
    };
  
  public static final TypeAdapterFactory STRING_BUFFER_FACTORY = newFactory(StringBuffer.class, STRING_BUFFER);
  
  public static final TypeAdapter<URL> URL = new TypeAdapter<URL>() {
      public URL read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        String nextString = in.nextString();
        return "null".equals(nextString) ? null : new URL(nextString);
      }
      
      public void write(JsonWriter out, URL value) throws IOException {
        out.value((value == null) ? null : value.toExternalForm());
      }
    };
  
  public static final TypeAdapterFactory URL_FACTORY = newFactory(URL.class, URL);
  
  public static final TypeAdapter<URI> URI = new TypeAdapter<URI>() {
      public URI read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        try {
          String nextString = in.nextString();
          return "null".equals(nextString) ? null : new URI(nextString);
        } catch (URISyntaxException e) {
          throw new JsonIOException(e);
        } 
      }
      
      public void write(JsonWriter out, URI value) throws IOException {
        out.value((value == null) ? null : value.toASCIIString());
      }
    };
  
  public static final TypeAdapterFactory URI_FACTORY = newFactory(URI.class, URI);
  
  public static final TypeAdapter<InetAddress> INET_ADDRESS = new TypeAdapter<InetAddress>() {
      public InetAddress read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        return InetAddress.getByName(in.nextString());
      }
      
      public void write(JsonWriter out, InetAddress value) throws IOException {
        out.value((value == null) ? null : value.getHostAddress());
      }
    };
  
  public static final TypeAdapterFactory INET_ADDRESS_FACTORY = newTypeHierarchyFactory(InetAddress.class, INET_ADDRESS);
  
  public static final TypeAdapter<UUID> UUID = new TypeAdapter<UUID>() {
      public UUID read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        String s = in.nextString();
        try {
          return UUID.fromString(s);
        } catch (IllegalArgumentException e) {
          throw new JsonSyntaxException("Failed parsing '" + s + "' as UUID; at path " + in.getPreviousPath(), e);
        } 
      }
      
      public void write(JsonWriter out, UUID value) throws IOException {
        out.value((value == null) ? null : value.toString());
      }
    };
  
  public static final TypeAdapterFactory UUID_FACTORY = newFactory(UUID.class, UUID);
  
  public static final TypeAdapter<Currency> CURRENCY = (new TypeAdapter<Currency>() {
      public Currency read(JsonReader in) throws IOException {
        String s = in.nextString();
        try {
          return Currency.getInstance(s);
        } catch (IllegalArgumentException e) {
          throw new JsonSyntaxException("Failed parsing '" + s + "' as Currency; at path " + in.getPreviousPath(), e);
        } 
      }
      
      public void write(JsonWriter out, Currency value) throws IOException {
        out.value(value.getCurrencyCode());
      }
    }).nullSafe();
  
  public static final TypeAdapterFactory CURRENCY_FACTORY = newFactory(Currency.class, CURRENCY);
  
  public static final TypeAdapter<Calendar> CALENDAR = new TypeAdapter<Calendar>() {
      private static final String YEAR = "year";
      
      private static final String MONTH = "month";
      
      private static final String DAY_OF_MONTH = "dayOfMonth";
      
      private static final String HOUR_OF_DAY = "hourOfDay";
      
      private static final String MINUTE = "minute";
      
      private static final String SECOND = "second";
      
      public Calendar read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        in.beginObject();
        int year = 0;
        int month = 0;
        int dayOfMonth = 0;
        int hourOfDay = 0;
        int minute = 0;
        int second = 0;
        while (in.peek() != JsonToken.END_OBJECT) {
          String name = in.nextName();
          int value = in.nextInt();
          if ("year".equals(name)) {
            year = value;
            continue;
          } 
          if ("month".equals(name)) {
            month = value;
            continue;
          } 
          if ("dayOfMonth".equals(name)) {
            dayOfMonth = value;
            continue;
          } 
          if ("hourOfDay".equals(name)) {
            hourOfDay = value;
            continue;
          } 
          if ("minute".equals(name)) {
            minute = value;
            continue;
          } 
          if ("second".equals(name))
            second = value; 
        } 
        in.endObject();
        return new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second);
      }
      
      public void write(JsonWriter out, Calendar value) throws IOException {
        if (value == null) {
          out.nullValue();
          return;
        } 
        out.beginObject();
        out.name("year");
        out.value(value.get(1));
        out.name("month");
        out.value(value.get(2));
        out.name("dayOfMonth");
        out.value(value.get(5));
        out.name("hourOfDay");
        out.value(value.get(11));
        out.name("minute");
        out.value(value.get(12));
        out.name("second");
        out.value(value.get(13));
        out.endObject();
      }
    };
  
  public static final TypeAdapterFactory CALENDAR_FACTORY = newFactoryForMultipleTypes(Calendar.class, (Class)GregorianCalendar.class, CALENDAR);
  
  public static final TypeAdapter<Locale> LOCALE = new TypeAdapter<Locale>() {
      public Locale read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        String locale = in.nextString();
        StringTokenizer tokenizer = new StringTokenizer(locale, "_");
        String language = null;
        String country = null;
        String variant = null;
        if (tokenizer.hasMoreElements())
          language = tokenizer.nextToken(); 
        if (tokenizer.hasMoreElements())
          country = tokenizer.nextToken(); 
        if (tokenizer.hasMoreElements())
          variant = tokenizer.nextToken(); 
        if (country == null && variant == null)
          return new Locale(language); 
        if (variant == null)
          return new Locale(language, country); 
        return new Locale(language, country, variant);
      }
      
      public void write(JsonWriter out, Locale value) throws IOException {
        out.value((value == null) ? null : value.toString());
      }
    };
  
  public static final TypeAdapterFactory LOCALE_FACTORY = newFactory(Locale.class, LOCALE);
  
  public static final TypeAdapter<JsonElement> JSON_ELEMENT = new TypeAdapter<JsonElement>() {
      private JsonElement tryBeginNesting(JsonReader in, JsonToken peeked) throws IOException {
        switch (peeked) {
          case BEGIN_ARRAY:
            in.beginArray();
            return (JsonElement)new JsonArray();
          case BEGIN_OBJECT:
            in.beginObject();
            return (JsonElement)new JsonObject();
        } 
        return null;
      }
      
      private JsonElement readTerminal(JsonReader in, JsonToken peeked) throws IOException {
        String number;
        switch (peeked) {
          case STRING:
            return (JsonElement)new JsonPrimitive(in.nextString());
          case NUMBER:
            number = in.nextString();
            return (JsonElement)new JsonPrimitive((Number)new LazilyParsedNumber(number));
          case BOOLEAN:
            return (JsonElement)new JsonPrimitive(Boolean.valueOf(in.nextBoolean()));
          case NULL:
            in.nextNull();
            return (JsonElement)JsonNull.INSTANCE;
        } 
        throw new IllegalStateException("Unexpected token: " + peeked);
      }
      
      public JsonElement read(JsonReader in) throws IOException {
        if (in instanceof JsonTreeReader)
          return ((JsonTreeReader)in).nextJsonElement(); 
        JsonToken peeked = in.peek();
        JsonElement current = tryBeginNesting(in, peeked);
        if (current == null)
          return readTerminal(in, peeked); 
        Deque<JsonElement> stack = new ArrayDeque<>();
        while (true) {
          while (in.hasNext()) {
            String name = null;
            if (current instanceof JsonObject)
              name = in.nextName(); 
            peeked = in.peek();
            JsonElement value = tryBeginNesting(in, peeked);
            boolean isNesting = (value != null);
            if (value == null)
              value = readTerminal(in, peeked); 
            if (current instanceof JsonArray) {
              ((JsonArray)current).add(value);
            } else {
              ((JsonObject)current).add(name, value);
            } 
            if (isNesting) {
              stack.addLast(current);
              current = value;
            } 
          } 
          if (current instanceof JsonArray) {
            in.endArray();
          } else {
            in.endObject();
          } 
          if (stack.isEmpty())
            return current; 
          current = stack.removeLast();
        } 
      }
      
      public void write(JsonWriter out, JsonElement value) throws IOException {
        if (value == null || value.isJsonNull()) {
          out.nullValue();
        } else if (value.isJsonPrimitive()) {
          JsonPrimitive primitive = value.getAsJsonPrimitive();
          if (primitive.isNumber()) {
            out.value(primitive.getAsNumber());
          } else if (primitive.isBoolean()) {
            out.value(primitive.getAsBoolean());
          } else {
            out.value(primitive.getAsString());
          } 
        } else if (value.isJsonArray()) {
          out.beginArray();
          for (JsonElement e : value.getAsJsonArray())
            write(out, e); 
          out.endArray();
        } else if (value.isJsonObject()) {
          out.beginObject();
          for (Map.Entry<String, JsonElement> e : (Iterable<Map.Entry<String, JsonElement>>)value.getAsJsonObject().entrySet()) {
            out.name(e.getKey());
            write(out, e.getValue());
          } 
          out.endObject();
        } else {
          throw new IllegalArgumentException("Couldn't write " + value.getClass());
        } 
      }
    };
  
  public static final TypeAdapterFactory JSON_ELEMENT_FACTORY = newTypeHierarchyFactory(JsonElement.class, JSON_ELEMENT);
  
  private static final class EnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {
    private final Map<String, T> nameToConstant = new HashMap<>();
    
    private final Map<String, T> stringToConstant = new HashMap<>();
    
    private final Map<T, String> constantToName = new HashMap<>();
    
    public EnumTypeAdapter(final Class<T> classOfT) {
      try {
        Field[] constantFields = AccessController.<Field[]>doPrivileged((PrivilegedAction)new PrivilegedAction<Field[]>() {
              public Field[] run() {
                Field[] fields = classOfT.getDeclaredFields();
                ArrayList<Field> constantFieldsList = new ArrayList<>(fields.length);
                for (Field f : fields) {
                  if (f.isEnumConstant())
                    constantFieldsList.add(f); 
                } 
                Field[] constantFields = constantFieldsList.<Field>toArray(new Field[0]);
                AccessibleObject.setAccessible((AccessibleObject[])constantFields, true);
                return constantFields;
              }
            });
        for (Field constantField : constantFields) {
          Enum enum_ = (Enum)constantField.get(null);
          String name = enum_.name();
          String toStringVal = enum_.toString();
          SerializedName annotation = constantField.<SerializedName>getAnnotation(SerializedName.class);
          if (annotation != null) {
            name = annotation.value();
            for (String alternate : annotation.alternate())
              this.nameToConstant.put(alternate, (T)enum_); 
          } 
          this.nameToConstant.put(name, (T)enum_);
          this.stringToConstant.put(toStringVal, (T)enum_);
          this.constantToName.put((T)enum_, name);
        } 
      } catch (IllegalAccessException e) {
        throw new AssertionError(e);
      } 
    }
    
    public T read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
        in.nextNull();
        return null;
      } 
      String key = in.nextString();
      Enum enum_ = (Enum)this.nameToConstant.get(key);
      return (enum_ == null) ? this.stringToConstant.get(key) : (T)enum_;
    }
    
    public void write(JsonWriter out, T value) throws IOException {
      out.value((value == null) ? null : this.constantToName.get(value));
    }
  }
  
  public static final TypeAdapterFactory ENUM_FACTORY = new TypeAdapterFactory() {
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Class<? super T> rawType = typeToken.getRawType();
        if (!Enum.class.isAssignableFrom(rawType) || rawType == Enum.class)
          return null; 
        if (!rawType.isEnum())
          rawType = rawType.getSuperclass(); 
        TypeAdapter<T> adapter = new TypeAdapters.EnumTypeAdapter<>(rawType);
        return adapter;
      }
    };
  
  public static <TT> TypeAdapterFactory newFactory(final TypeToken<TT> type, final TypeAdapter<TT> typeAdapter) {
    return new TypeAdapterFactory() {
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
          return typeToken.equals(type) ? typeAdapter : null;
        }
      };
  }
  
  public static <TT> TypeAdapterFactory newFactory(final Class<TT> type, final TypeAdapter<TT> typeAdapter) {
    return new TypeAdapterFactory() {
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
          return (typeToken.getRawType() == type) ? typeAdapter : null;
        }
        
        public String toString() {
          return "Factory[type=" + type.getName() + ",adapter=" + typeAdapter + "]";
        }
      };
  }
  
  public static <TT> TypeAdapterFactory newFactory(final Class<TT> unboxed, final Class<TT> boxed, final TypeAdapter<? super TT> typeAdapter) {
    return new TypeAdapterFactory() {
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
          Class<? super T> rawType = typeToken.getRawType();
          return (rawType == unboxed || rawType == boxed) ? typeAdapter : null;
        }
        
        public String toString() {
          return "Factory[type=" + boxed.getName() + "+" + unboxed
            .getName() + ",adapter=" + typeAdapter + "]";
        }
      };
  }
  
  public static <TT> TypeAdapterFactory newFactoryForMultipleTypes(final Class<TT> base, final Class<? extends TT> sub, final TypeAdapter<? super TT> typeAdapter) {
    return new TypeAdapterFactory() {
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
          Class<? super T> rawType = typeToken.getRawType();
          return (rawType == base || rawType == sub) ? typeAdapter : null;
        }
        
        public String toString() {
          return "Factory[type=" + base.getName() + "+" + sub
            .getName() + ",adapter=" + typeAdapter + "]";
        }
      };
  }
  
  public static <T1> TypeAdapterFactory newTypeHierarchyFactory(final Class<T1> clazz, final TypeAdapter<T1> typeAdapter) {
    return new TypeAdapterFactory() {
        public <T2> TypeAdapter<T2> create(Gson gson, TypeToken<T2> typeToken) {
          final Class<? super T2> requestedType = typeToken.getRawType();
          if (!clazz.isAssignableFrom(requestedType))
            return null; 
          return new TypeAdapter<T1>() {
              public void write(JsonWriter out, T1 value) throws IOException {
                typeAdapter.write(out, value);
              }
              
              public T1 read(JsonReader in) throws IOException {
                T1 result = (T1)typeAdapter.read(in);
                if (result != null && !requestedType.isInstance(result))
                  throw new JsonSyntaxException("Expected a " + requestedType.getName() + " but was " + result
                      .getClass().getName() + "; at path " + in.getPreviousPath()); 
                return result;
              }
            };
        }
        
        public String toString() {
          return "Factory[typeHierarchy=" + clazz.getName() + ",adapter=" + typeAdapter + "]";
        }
      };
  }
}
