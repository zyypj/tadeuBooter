package me.syncwrld.booter.libs.google.gson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.syncwrld.booter.libs.google.gson.internal.NonNullElementWrapperList;

public final class JsonArray extends JsonElement implements Iterable<JsonElement> {
  private final ArrayList<JsonElement> elements;
  
  public JsonArray() {
    this.elements = new ArrayList<>();
  }
  
  public JsonArray(int capacity) {
    this.elements = new ArrayList<>(capacity);
  }
  
  public JsonArray deepCopy() {
    if (!this.elements.isEmpty()) {
      JsonArray result = new JsonArray(this.elements.size());
      for (JsonElement element : this.elements)
        result.add(element.deepCopy()); 
      return result;
    } 
    return new JsonArray();
  }
  
  public void add(Boolean bool) {
    this.elements.add((bool == null) ? JsonNull.INSTANCE : new JsonPrimitive(bool));
  }
  
  public void add(Character character) {
    this.elements.add((character == null) ? JsonNull.INSTANCE : new JsonPrimitive(character));
  }
  
  public void add(Number number) {
    this.elements.add((number == null) ? JsonNull.INSTANCE : new JsonPrimitive(number));
  }
  
  public void add(String string) {
    this.elements.add((string == null) ? JsonNull.INSTANCE : new JsonPrimitive(string));
  }
  
  public void add(JsonElement element) {
    if (element == null)
      element = JsonNull.INSTANCE; 
    this.elements.add(element);
  }
  
  public void addAll(JsonArray array) {
    this.elements.addAll(array.elements);
  }
  
  public JsonElement set(int index, JsonElement element) {
    return this.elements.set(index, (element == null) ? JsonNull.INSTANCE : element);
  }
  
  public boolean remove(JsonElement element) {
    return this.elements.remove(element);
  }
  
  public JsonElement remove(int index) {
    return this.elements.remove(index);
  }
  
  public boolean contains(JsonElement element) {
    return this.elements.contains(element);
  }
  
  public int size() {
    return this.elements.size();
  }
  
  public boolean isEmpty() {
    return this.elements.isEmpty();
  }
  
  public Iterator<JsonElement> iterator() {
    return this.elements.iterator();
  }
  
  public JsonElement get(int i) {
    return this.elements.get(i);
  }
  
  private JsonElement getAsSingleElement() {
    int size = this.elements.size();
    if (size == 1)
      return this.elements.get(0); 
    throw new IllegalStateException("Array must have size 1, but has size " + size);
  }
  
  public Number getAsNumber() {
    return getAsSingleElement().getAsNumber();
  }
  
  public String getAsString() {
    return getAsSingleElement().getAsString();
  }
  
  public double getAsDouble() {
    return getAsSingleElement().getAsDouble();
  }
  
  public BigDecimal getAsBigDecimal() {
    return getAsSingleElement().getAsBigDecimal();
  }
  
  public BigInteger getAsBigInteger() {
    return getAsSingleElement().getAsBigInteger();
  }
  
  public float getAsFloat() {
    return getAsSingleElement().getAsFloat();
  }
  
  public long getAsLong() {
    return getAsSingleElement().getAsLong();
  }
  
  public int getAsInt() {
    return getAsSingleElement().getAsInt();
  }
  
  public byte getAsByte() {
    return getAsSingleElement().getAsByte();
  }
  
  @Deprecated
  public char getAsCharacter() {
    return getAsSingleElement().getAsCharacter();
  }
  
  public short getAsShort() {
    return getAsSingleElement().getAsShort();
  }
  
  public boolean getAsBoolean() {
    return getAsSingleElement().getAsBoolean();
  }
  
  public List<JsonElement> asList() {
    return (List<JsonElement>)new NonNullElementWrapperList(this.elements);
  }
  
  public boolean equals(Object o) {
    return (o == this || (o instanceof JsonArray && ((JsonArray)o).elements.equals(this.elements)));
  }
  
  public int hashCode() {
    return this.elements.hashCode();
  }
}
