package me.syncwrld.booter.libs.google.gson;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import me.syncwrld.booter.libs.google.gson.internal.Streams;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonToken;
import me.syncwrld.booter.libs.google.gson.stream.MalformedJsonException;

public final class JsonParser {
  public static JsonElement parseString(String json) throws JsonSyntaxException {
    return parseReader(new StringReader(json));
  }
  
  public static JsonElement parseReader(Reader reader) throws JsonIOException, JsonSyntaxException {
    try {
      JsonReader jsonReader = new JsonReader(reader);
      JsonElement element = parseReader(jsonReader);
      if (!element.isJsonNull() && jsonReader.peek() != JsonToken.END_DOCUMENT)
        throw new JsonSyntaxException("Did not consume the entire document."); 
      return element;
    } catch (MalformedJsonException e) {
      throw new JsonSyntaxException(e);
    } catch (IOException e) {
      throw new JsonIOException(e);
    } catch (NumberFormatException e) {
      throw new JsonSyntaxException(e);
    } 
  }
  
  public static JsonElement parseReader(JsonReader reader) throws JsonIOException, JsonSyntaxException {
    boolean lenient = reader.isLenient();
    reader.setLenient(true);
    try {
      return Streams.parse(reader);
    } catch (StackOverflowError e) {
      throw new JsonParseException("Failed parsing JSON source: " + reader + " to Json", e);
    } catch (OutOfMemoryError e) {
      throw new JsonParseException("Failed parsing JSON source: " + reader + " to Json", e);
    } finally {
      reader.setLenient(lenient);
    } 
  }
  
  @Deprecated
  public JsonElement parse(String json) throws JsonSyntaxException {
    return parseString(json);
  }
  
  @Deprecated
  public JsonElement parse(Reader json) throws JsonIOException, JsonSyntaxException {
    return parseReader(json);
  }
  
  @Deprecated
  public JsonElement parse(JsonReader json) throws JsonIOException, JsonSyntaxException {
    return parseReader(json);
  }
}
