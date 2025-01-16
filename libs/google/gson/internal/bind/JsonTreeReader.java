package me.syncwrld.booter.libs.google.gson.internal.bind;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import me.syncwrld.booter.libs.google.gson.JsonArray;
import me.syncwrld.booter.libs.google.gson.JsonElement;
import me.syncwrld.booter.libs.google.gson.JsonObject;
import me.syncwrld.booter.libs.google.gson.JsonPrimitive;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonToken;
import me.syncwrld.booter.libs.google.gson.stream.MalformedJsonException;

public final class JsonTreeReader extends JsonReader {
  private static final Reader UNREADABLE_READER = new Reader() {
      public int read(char[] buffer, int offset, int count) {
        throw new AssertionError();
      }
      
      public void close() {
        throw new AssertionError();
      }
    };
  
  private static final Object SENTINEL_CLOSED = new Object();
  
  private Object[] stack = new Object[32];
  
  private int stackSize = 0;
  
  private String[] pathNames = new String[32];
  
  private int[] pathIndices = new int[32];
  
  public JsonTreeReader(JsonElement element) {
    super(UNREADABLE_READER);
    push(element);
  }
  
  public void beginArray() throws IOException {
    expect(JsonToken.BEGIN_ARRAY);
    JsonArray array = (JsonArray)peekStack();
    push(array.iterator());
    this.pathIndices[this.stackSize - 1] = 0;
  }
  
  public void endArray() throws IOException {
    expect(JsonToken.END_ARRAY);
    popStack();
    popStack();
    if (this.stackSize > 0)
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1; 
  }
  
  public void beginObject() throws IOException {
    expect(JsonToken.BEGIN_OBJECT);
    JsonObject object = (JsonObject)peekStack();
    push(object.entrySet().iterator());
  }
  
  public void endObject() throws IOException {
    expect(JsonToken.END_OBJECT);
    this.pathNames[this.stackSize - 1] = null;
    popStack();
    popStack();
    if (this.stackSize > 0)
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1; 
  }
  
  public boolean hasNext() throws IOException {
    JsonToken token = peek();
    return (token != JsonToken.END_OBJECT && token != JsonToken.END_ARRAY && token != JsonToken.END_DOCUMENT);
  }
  
  public JsonToken peek() throws IOException {
    if (this.stackSize == 0)
      return JsonToken.END_DOCUMENT; 
    Object o = peekStack();
    if (o instanceof Iterator) {
      boolean isObject = this.stack[this.stackSize - 2] instanceof JsonObject;
      Iterator<?> iterator = (Iterator)o;
      if (iterator.hasNext()) {
        if (isObject)
          return JsonToken.NAME; 
        push(iterator.next());
        return peek();
      } 
      return isObject ? JsonToken.END_OBJECT : JsonToken.END_ARRAY;
    } 
    if (o instanceof JsonObject)
      return JsonToken.BEGIN_OBJECT; 
    if (o instanceof JsonArray)
      return JsonToken.BEGIN_ARRAY; 
    if (o instanceof JsonPrimitive) {
      JsonPrimitive primitive = (JsonPrimitive)o;
      if (primitive.isString())
        return JsonToken.STRING; 
      if (primitive.isBoolean())
        return JsonToken.BOOLEAN; 
      if (primitive.isNumber())
        return JsonToken.NUMBER; 
      throw new AssertionError();
    } 
    if (o instanceof me.syncwrld.booter.libs.google.gson.JsonNull)
      return JsonToken.NULL; 
    if (o == SENTINEL_CLOSED)
      throw new IllegalStateException("JsonReader is closed"); 
    throw new MalformedJsonException("Custom JsonElement subclass " + o.getClass().getName() + " is not supported");
  }
  
  private Object peekStack() {
    return this.stack[this.stackSize - 1];
  }
  
  private Object popStack() {
    Object result = this.stack[--this.stackSize];
    this.stack[this.stackSize] = null;
    return result;
  }
  
  private void expect(JsonToken expected) throws IOException {
    if (peek() != expected)
      throw new IllegalStateException("Expected " + expected + " but was " + 
          peek() + locationString()); 
  }
  
  private String nextName(boolean skipName) throws IOException {
    expect(JsonToken.NAME);
    Iterator<?> i = (Iterator)peekStack();
    Map.Entry<?, ?> entry = (Map.Entry<?, ?>)i.next();
    String result = (String)entry.getKey();
    this.pathNames[this.stackSize - 1] = skipName ? "<skipped>" : result;
    push(entry.getValue());
    return result;
  }
  
  public String nextName() throws IOException {
    return nextName(false);
  }
  
  public String nextString() throws IOException {
    JsonToken token = peek();
    if (token != JsonToken.STRING && token != JsonToken.NUMBER)
      throw new IllegalStateException("Expected " + JsonToken.STRING + " but was " + token + 
          locationString()); 
    String result = ((JsonPrimitive)popStack()).getAsString();
    if (this.stackSize > 0)
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1; 
    return result;
  }
  
  public boolean nextBoolean() throws IOException {
    expect(JsonToken.BOOLEAN);
    boolean result = ((JsonPrimitive)popStack()).getAsBoolean();
    if (this.stackSize > 0)
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1; 
    return result;
  }
  
  public void nextNull() throws IOException {
    expect(JsonToken.NULL);
    popStack();
    if (this.stackSize > 0)
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1; 
  }
  
  public double nextDouble() throws IOException {
    JsonToken token = peek();
    if (token != JsonToken.NUMBER && token != JsonToken.STRING)
      throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + token + 
          locationString()); 
    double result = ((JsonPrimitive)peekStack()).getAsDouble();
    if (!isLenient() && (Double.isNaN(result) || Double.isInfinite(result)))
      throw new MalformedJsonException("JSON forbids NaN and infinities: " + result); 
    popStack();
    if (this.stackSize > 0)
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1; 
    return result;
  }
  
  public long nextLong() throws IOException {
    JsonToken token = peek();
    if (token != JsonToken.NUMBER && token != JsonToken.STRING)
      throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + token + 
          locationString()); 
    long result = ((JsonPrimitive)peekStack()).getAsLong();
    popStack();
    if (this.stackSize > 0)
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1; 
    return result;
  }
  
  public int nextInt() throws IOException {
    JsonToken token = peek();
    if (token != JsonToken.NUMBER && token != JsonToken.STRING)
      throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + token + 
          locationString()); 
    int result = ((JsonPrimitive)peekStack()).getAsInt();
    popStack();
    if (this.stackSize > 0)
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1; 
    return result;
  }
  
  JsonElement nextJsonElement() throws IOException {
    JsonToken peeked = peek();
    if (peeked == JsonToken.NAME || peeked == JsonToken.END_ARRAY || peeked == JsonToken.END_OBJECT || peeked == JsonToken.END_DOCUMENT)
      throw new IllegalStateException("Unexpected " + peeked + " when reading a JsonElement."); 
    JsonElement element = (JsonElement)peekStack();
    skipValue();
    return element;
  }
  
  public void close() throws IOException {
    this.stack = new Object[] { SENTINEL_CLOSED };
    this.stackSize = 1;
  }
  
  public void skipValue() throws IOException {
    String unused;
    JsonToken peeked = peek();
    switch (peeked) {
      case NAME:
        unused = nextName(true);
      case END_ARRAY:
        endArray();
      case END_OBJECT:
        endObject();
      case END_DOCUMENT:
        return;
    } 
    popStack();
    if (this.stackSize > 0)
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1; 
  }
  
  public String toString() {
    return getClass().getSimpleName() + locationString();
  }
  
  public void promoteNameToValue() throws IOException {
    expect(JsonToken.NAME);
    Iterator<?> i = (Iterator)peekStack();
    Map.Entry<?, ?> entry = (Map.Entry<?, ?>)i.next();
    push(entry.getValue());
    push(new JsonPrimitive((String)entry.getKey()));
  }
  
  private void push(Object newTop) {
    if (this.stackSize == this.stack.length) {
      int newLength = this.stackSize * 2;
      this.stack = Arrays.copyOf(this.stack, newLength);
      this.pathIndices = Arrays.copyOf(this.pathIndices, newLength);
      this.pathNames = Arrays.<String>copyOf(this.pathNames, newLength);
    } 
    this.stack[this.stackSize++] = newTop;
  }
  
  private String getPath(boolean usePreviousPath) {
    StringBuilder result = (new StringBuilder()).append('$');
    for (int i = 0; i < this.stackSize; i++) {
      if (this.stack[i] instanceof JsonArray) {
        if (++i < this.stackSize && this.stack[i] instanceof Iterator) {
          int pathIndex = this.pathIndices[i];
          if (usePreviousPath && pathIndex > 0 && (i == this.stackSize - 1 || i == this.stackSize - 2))
            pathIndex--; 
          result.append('[').append(pathIndex).append(']');
        } 
      } else if (this.stack[i] instanceof JsonObject && 
        ++i < this.stackSize && this.stack[i] instanceof Iterator) {
        result.append('.');
        if (this.pathNames[i] != null)
          result.append(this.pathNames[i]); 
      } 
    } 
    return result.toString();
  }
  
  public String getPreviousPath() {
    return getPath(true);
  }
  
  public String getPath() {
    return getPath(false);
  }
  
  private String locationString() {
    return " at path " + getPath();
  }
}
