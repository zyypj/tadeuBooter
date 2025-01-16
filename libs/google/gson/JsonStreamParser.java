package me.syncwrld.booter.libs.google.gson;

import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.NoSuchElementException;
import me.syncwrld.booter.libs.google.gson.internal.Streams;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonToken;

public final class JsonStreamParser implements Iterator<JsonElement> {
  private final JsonReader parser;
  
  private final Object lock;
  
  public JsonStreamParser(String json) {
    this(new StringReader(json));
  }
  
  public JsonStreamParser(Reader reader) {
    this.parser = new JsonReader(reader);
    this.parser.setLenient(true);
    this.lock = new Object();
  }
  
  public JsonElement next() throws JsonParseException {
    if (!hasNext())
      throw new NoSuchElementException(); 
    try {
      return Streams.parse(this.parser);
    } catch (StackOverflowError e) {
      throw new JsonParseException("Failed parsing JSON source to Json", e);
    } catch (OutOfMemoryError e) {
      throw new JsonParseException("Failed parsing JSON source to Json", e);
    } 
  }
  
  public boolean hasNext() {
    synchronized (this.lock) {
      return (this.parser.peek() != JsonToken.END_DOCUMENT);
    } 
  }
  
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
