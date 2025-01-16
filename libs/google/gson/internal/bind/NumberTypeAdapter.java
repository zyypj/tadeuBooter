package me.syncwrld.booter.libs.google.gson.internal.bind;

import java.io.IOException;
import me.syncwrld.booter.libs.google.gson.Gson;
import me.syncwrld.booter.libs.google.gson.JsonSyntaxException;
import me.syncwrld.booter.libs.google.gson.ToNumberPolicy;
import me.syncwrld.booter.libs.google.gson.ToNumberStrategy;
import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.gson.TypeAdapterFactory;
import me.syncwrld.booter.libs.google.gson.reflect.TypeToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonWriter;

public final class NumberTypeAdapter extends TypeAdapter<Number> {
  private static final TypeAdapterFactory LAZILY_PARSED_NUMBER_FACTORY = newFactory((ToNumberStrategy)ToNumberPolicy.LAZILY_PARSED_NUMBER);
  
  private final ToNumberStrategy toNumberStrategy;
  
  private NumberTypeAdapter(ToNumberStrategy toNumberStrategy) {
    this.toNumberStrategy = toNumberStrategy;
  }
  
  private static TypeAdapterFactory newFactory(ToNumberStrategy toNumberStrategy) {
    final NumberTypeAdapter adapter = new NumberTypeAdapter(toNumberStrategy);
    return new TypeAdapterFactory() {
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
          return (type.getRawType() == Number.class) ? adapter : null;
        }
      };
  }
  
  public static TypeAdapterFactory getFactory(ToNumberStrategy toNumberStrategy) {
    if (toNumberStrategy == ToNumberPolicy.LAZILY_PARSED_NUMBER)
      return LAZILY_PARSED_NUMBER_FACTORY; 
    return newFactory(toNumberStrategy);
  }
  
  public Number read(JsonReader in) throws IOException {
    JsonToken jsonToken = in.peek();
    switch (jsonToken) {
      case NULL:
        in.nextNull();
        return null;
      case NUMBER:
      case STRING:
        return this.toNumberStrategy.readNumber(in);
    } 
    throw new JsonSyntaxException("Expecting number, got: " + jsonToken + "; at path " + in.getPath());
  }
  
  public void write(JsonWriter out, Number value) throws IOException {
    out.value(value);
  }
}
