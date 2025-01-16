package me.syncwrld.booter.libs.google.gson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import me.syncwrld.booter.libs.google.gson.internal.LazilyParsedNumber;

public final class JsonPrimitive extends JsonElement {
  private final Object value;
  
  public JsonPrimitive(Boolean bool) {
    this.value = Objects.requireNonNull(bool);
  }
  
  public JsonPrimitive(Number number) {
    this.value = Objects.requireNonNull(number);
  }
  
  public JsonPrimitive(String string) {
    this.value = Objects.requireNonNull(string);
  }
  
  public JsonPrimitive(Character c) {
    this.value = ((Character)Objects.<Character>requireNonNull(c)).toString();
  }
  
  public JsonPrimitive deepCopy() {
    return this;
  }
  
  public boolean isBoolean() {
    return this.value instanceof Boolean;
  }
  
  public boolean getAsBoolean() {
    if (isBoolean())
      return ((Boolean)this.value).booleanValue(); 
    return Boolean.parseBoolean(getAsString());
  }
  
  public boolean isNumber() {
    return this.value instanceof Number;
  }
  
  public Number getAsNumber() {
    if (this.value instanceof Number)
      return (Number)this.value; 
    if (this.value instanceof String)
      return (Number)new LazilyParsedNumber((String)this.value); 
    throw new UnsupportedOperationException("Primitive is neither a number nor a string");
  }
  
  public boolean isString() {
    return this.value instanceof String;
  }
  
  public String getAsString() {
    if (this.value instanceof String)
      return (String)this.value; 
    if (isNumber())
      return getAsNumber().toString(); 
    if (isBoolean())
      return ((Boolean)this.value).toString(); 
    throw new AssertionError("Unexpected value type: " + this.value.getClass());
  }
  
  public double getAsDouble() {
    return isNumber() ? getAsNumber().doubleValue() : Double.parseDouble(getAsString());
  }
  
  public BigDecimal getAsBigDecimal() {
    return (this.value instanceof BigDecimal) ? (BigDecimal)this.value : new BigDecimal(getAsString());
  }
  
  public BigInteger getAsBigInteger() {
    return (this.value instanceof BigInteger) ? 
      (BigInteger)this.value : new BigInteger(getAsString());
  }
  
  public float getAsFloat() {
    return isNumber() ? getAsNumber().floatValue() : Float.parseFloat(getAsString());
  }
  
  public long getAsLong() {
    return isNumber() ? getAsNumber().longValue() : Long.parseLong(getAsString());
  }
  
  public short getAsShort() {
    return isNumber() ? getAsNumber().shortValue() : Short.parseShort(getAsString());
  }
  
  public int getAsInt() {
    return isNumber() ? getAsNumber().intValue() : Integer.parseInt(getAsString());
  }
  
  public byte getAsByte() {
    return isNumber() ? getAsNumber().byteValue() : Byte.parseByte(getAsString());
  }
  
  @Deprecated
  public char getAsCharacter() {
    String s = getAsString();
    if (s.isEmpty())
      throw new UnsupportedOperationException("String value is empty"); 
    return s.charAt(0);
  }
  
  public int hashCode() {
    if (this.value == null)
      return 31; 
    if (isIntegral(this)) {
      long value = getAsNumber().longValue();
      return (int)(value ^ value >>> 32L);
    } 
    if (this.value instanceof Number) {
      long value = Double.doubleToLongBits(getAsNumber().doubleValue());
      return (int)(value ^ value >>> 32L);
    } 
    return this.value.hashCode();
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null || getClass() != obj.getClass())
      return false; 
    JsonPrimitive other = (JsonPrimitive)obj;
    if (this.value == null)
      return (other.value == null); 
    if (isIntegral(this) && isIntegral(other))
      return (getAsNumber().longValue() == other.getAsNumber().longValue()); 
    if (this.value instanceof Number && other.value instanceof Number) {
      double a = getAsNumber().doubleValue();
      double b = other.getAsNumber().doubleValue();
      return (a == b || (Double.isNaN(a) && Double.isNaN(b)));
    } 
    return this.value.equals(other.value);
  }
  
  private static boolean isIntegral(JsonPrimitive primitive) {
    if (primitive.value instanceof Number) {
      Number number = (Number)primitive.value;
      return (number instanceof BigInteger || number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte);
    } 
    return false;
  }
}
