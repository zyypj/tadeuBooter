package me.syncwrld.booter.libs.google.gson;

public enum LongSerializationPolicy {
  DEFAULT {
    public JsonElement serialize(Long value) {
      if (value == null)
        return JsonNull.INSTANCE; 
      return new JsonPrimitive(value);
    }
  },
  STRING {
    public JsonElement serialize(Long value) {
      if (value == null)
        return JsonNull.INSTANCE; 
      return new JsonPrimitive(value.toString());
    }
  };
  
  public abstract JsonElement serialize(Long paramLong);
}
