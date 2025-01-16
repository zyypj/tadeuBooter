package me.syncwrld.booter.libs.google.gson.internal.bind;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.syncwrld.booter.libs.google.gson.Gson;
import me.syncwrld.booter.libs.google.gson.JsonElement;
import me.syncwrld.booter.libs.google.gson.JsonPrimitive;
import me.syncwrld.booter.libs.google.gson.JsonSyntaxException;
import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.gson.TypeAdapterFactory;
import me.syncwrld.booter.libs.google.gson.internal.;
import me.syncwrld.booter.libs.google.gson.internal.ConstructorConstructor;
import me.syncwrld.booter.libs.google.gson.internal.JsonReaderInternalAccess;
import me.syncwrld.booter.libs.google.gson.internal.ObjectConstructor;
import me.syncwrld.booter.libs.google.gson.internal.Streams;
import me.syncwrld.booter.libs.google.gson.reflect.TypeToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonWriter;

public final class MapTypeAdapterFactory implements TypeAdapterFactory {
  private final ConstructorConstructor constructorConstructor;
  
  final boolean complexMapKeySerialization;
  
  public MapTypeAdapterFactory(ConstructorConstructor constructorConstructor, boolean complexMapKeySerialization) {
    this.constructorConstructor = constructorConstructor;
    this.complexMapKeySerialization = complexMapKeySerialization;
  }
  
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
    Type type = typeToken.getType();
    Class<? super T> rawType = typeToken.getRawType();
    if (!Map.class.isAssignableFrom(rawType))
      return null; 
    Type[] keyAndValueTypes = .Gson.Types.getMapKeyAndValueTypes(type, rawType);
    TypeAdapter<?> keyAdapter = getKeyAdapter(gson, keyAndValueTypes[0]);
    TypeAdapter<?> valueAdapter = gson.getAdapter(TypeToken.get(keyAndValueTypes[1]));
    ObjectConstructor<T> constructor = this.constructorConstructor.get(typeToken);
    TypeAdapter<T> result = (TypeAdapter)new Adapter<>(gson, keyAndValueTypes[0], keyAdapter, keyAndValueTypes[1], valueAdapter, (ObjectConstructor)constructor);
    return result;
  }
  
  private TypeAdapter<?> getKeyAdapter(Gson context, Type keyType) {
    return (keyType == boolean.class || keyType == Boolean.class) ? 
      TypeAdapters.BOOLEAN_AS_STRING : 
      context.getAdapter(TypeToken.get(keyType));
  }
  
  private final class Adapter<K, V> extends TypeAdapter<Map<K, V>> {
    private final TypeAdapter<K> keyTypeAdapter;
    
    private final TypeAdapter<V> valueTypeAdapter;
    
    private final ObjectConstructor<? extends Map<K, V>> constructor;
    
    public Adapter(Gson context, Type keyType, TypeAdapter<K> keyTypeAdapter, Type valueType, TypeAdapter<V> valueTypeAdapter, ObjectConstructor<? extends Map<K, V>> constructor) {
      this.keyTypeAdapter = new TypeAdapterRuntimeTypeWrapper<>(context, keyTypeAdapter, keyType);
      this.valueTypeAdapter = new TypeAdapterRuntimeTypeWrapper<>(context, valueTypeAdapter, valueType);
      this.constructor = constructor;
    }
    
    public Map<K, V> read(JsonReader in) throws IOException {
      JsonToken peek = in.peek();
      if (peek == JsonToken.NULL) {
        in.nextNull();
        return null;
      } 
      Map<K, V> map = (Map<K, V>)this.constructor.construct();
      if (peek == JsonToken.BEGIN_ARRAY) {
        in.beginArray();
        while (in.hasNext()) {
          in.beginArray();
          K key = (K)this.keyTypeAdapter.read(in);
          V value = (V)this.valueTypeAdapter.read(in);
          V replaced = map.put(key, value);
          if (replaced != null)
            throw new JsonSyntaxException("duplicate key: " + key); 
          in.endArray();
        } 
        in.endArray();
      } else {
        in.beginObject();
        while (in.hasNext()) {
          JsonReaderInternalAccess.INSTANCE.promoteNameToValue(in);
          K key = (K)this.keyTypeAdapter.read(in);
          V value = (V)this.valueTypeAdapter.read(in);
          V replaced = map.put(key, value);
          if (replaced != null)
            throw new JsonSyntaxException("duplicate key: " + key); 
        } 
        in.endObject();
      } 
      return map;
    }
    
    public void write(JsonWriter out, Map<K, V> map) throws IOException {
      int i;
      if (map == null) {
        out.nullValue();
        return;
      } 
      if (!MapTypeAdapterFactory.this.complexMapKeySerialization) {
        out.beginObject();
        for (Map.Entry<K, V> entry : map.entrySet()) {
          out.name(String.valueOf(entry.getKey()));
          this.valueTypeAdapter.write(out, entry.getValue());
        } 
        out.endObject();
        return;
      } 
      boolean hasComplexKeys = false;
      List<JsonElement> keys = new ArrayList<>(map.size());
      List<V> values = new ArrayList<>(map.size());
      for (Map.Entry<K, V> entry : map.entrySet()) {
        JsonElement keyElement = this.keyTypeAdapter.toJsonTree(entry.getKey());
        keys.add(keyElement);
        values.add(entry.getValue());
        i = hasComplexKeys | ((keyElement.isJsonArray() || keyElement.isJsonObject()) ? 1 : 0);
      } 
      if (i != 0) {
        out.beginArray();
        for (int j = 0, size = keys.size(); j < size; j++) {
          out.beginArray();
          Streams.write(keys.get(j), out);
          this.valueTypeAdapter.write(out, values.get(j));
          out.endArray();
        } 
        out.endArray();
      } else {
        out.beginObject();
        for (int j = 0, size = keys.size(); j < size; j++) {
          JsonElement keyElement = keys.get(j);
          out.name(keyToString(keyElement));
          this.valueTypeAdapter.write(out, values.get(j));
        } 
        out.endObject();
      } 
    }
    
    private String keyToString(JsonElement keyElement) {
      if (keyElement.isJsonPrimitive()) {
        JsonPrimitive primitive = keyElement.getAsJsonPrimitive();
        if (primitive.isNumber())
          return String.valueOf(primitive.getAsNumber()); 
        if (primitive.isBoolean())
          return Boolean.toString(primitive.getAsBoolean()); 
        if (primitive.isString())
          return primitive.getAsString(); 
        throw new AssertionError();
      } 
      if (keyElement.isJsonNull())
        return "null"; 
      throw new AssertionError();
    }
  }
}
