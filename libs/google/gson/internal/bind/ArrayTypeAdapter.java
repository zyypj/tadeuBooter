package me.syncwrld.booter.libs.google.gson.internal.bind;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import me.syncwrld.booter.libs.google.gson.Gson;
import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.gson.TypeAdapterFactory;
import me.syncwrld.booter.libs.google.gson.internal.;
import me.syncwrld.booter.libs.google.gson.reflect.TypeToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonReader;
import me.syncwrld.booter.libs.google.gson.stream.JsonToken;
import me.syncwrld.booter.libs.google.gson.stream.JsonWriter;

public final class ArrayTypeAdapter<E> extends TypeAdapter<Object> {
  public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Type type = typeToken.getType();
        if (!(type instanceof java.lang.reflect.GenericArrayType) && (!(type instanceof Class) || !((Class)type).isArray()))
          return null; 
        Type componentType = .Gson.Types.getArrayComponentType(type);
        TypeAdapter<?> componentTypeAdapter = gson.getAdapter(TypeToken.get(componentType));
        TypeAdapter<T> arrayAdapter = new ArrayTypeAdapter(gson, componentTypeAdapter, .Gson.Types.getRawType(componentType));
        return arrayAdapter;
      }
    };
  
  private final Class<E> componentType;
  
  private final TypeAdapter<E> componentTypeAdapter;
  
  public ArrayTypeAdapter(Gson context, TypeAdapter<E> componentTypeAdapter, Class<E> componentType) {
    this.componentTypeAdapter = new TypeAdapterRuntimeTypeWrapper<>(context, componentTypeAdapter, componentType);
    this.componentType = componentType;
  }
  
  public Object read(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.NULL) {
      in.nextNull();
      return null;
    } 
    ArrayList<E> list = new ArrayList<>();
    in.beginArray();
    while (in.hasNext()) {
      E instance = (E)this.componentTypeAdapter.read(in);
      list.add(instance);
    } 
    in.endArray();
    int size = list.size();
    if (this.componentType.isPrimitive()) {
      Object object = Array.newInstance(this.componentType, size);
      for (int i = 0; i < size; i++)
        Array.set(object, i, list.get(i)); 
      return object;
    } 
    E[] array = (E[])Array.newInstance(this.componentType, size);
    return list.toArray(array);
  }
  
  public void write(JsonWriter out, Object array) throws IOException {
    if (array == null) {
      out.nullValue();
      return;
    } 
    out.beginArray();
    for (int i = 0, length = Array.getLength(array); i < length; i++) {
      E value = (E)Array.get(array, i);
      this.componentTypeAdapter.write(out, value);
    } 
    out.endArray();
  }
}
