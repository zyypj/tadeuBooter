package me.syncwrld.booter.libs.google.gson;

public final class JsonNull extends JsonElement {
  public static final JsonNull INSTANCE = new JsonNull();
  
  public JsonNull deepCopy() {
    return INSTANCE;
  }
  
  public int hashCode() {
    return JsonNull.class.hashCode();
  }
  
  public boolean equals(Object other) {
    return other instanceof JsonNull;
  }
}
