package me.syncwrld.booter.libs.google.gson.internal.bind;

import me.syncwrld.booter.libs.google.gson.Gson;
import me.syncwrld.booter.libs.google.gson.JsonDeserializer;
import me.syncwrld.booter.libs.google.gson.JsonSerializer;
import me.syncwrld.booter.libs.google.gson.TypeAdapter;
import me.syncwrld.booter.libs.google.gson.TypeAdapterFactory;
import me.syncwrld.booter.libs.google.gson.annotations.JsonAdapter;
import me.syncwrld.booter.libs.google.gson.internal.ConstructorConstructor;
import me.syncwrld.booter.libs.google.gson.reflect.TypeToken;

public final class JsonAdapterAnnotationTypeAdapterFactory implements TypeAdapterFactory {
  private final ConstructorConstructor constructorConstructor;
  
  public JsonAdapterAnnotationTypeAdapterFactory(ConstructorConstructor constructorConstructor) {
    this.constructorConstructor = constructorConstructor;
  }
  
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> targetType) {
    Class<? super T> rawType = targetType.getRawType();
    JsonAdapter annotation = rawType.<JsonAdapter>getAnnotation(JsonAdapter.class);
    if (annotation == null)
      return null; 
    return (TypeAdapter)getTypeAdapter(this.constructorConstructor, gson, targetType, annotation);
  }
  
  TypeAdapter<?> getTypeAdapter(ConstructorConstructor constructorConstructor, Gson gson, TypeToken<?> type, JsonAdapter annotation) {
    TypeAdapter<?> typeAdapter;
    Object instance = constructorConstructor.get(TypeToken.get(annotation.value())).construct();
    boolean nullSafe = annotation.nullSafe();
    if (instance instanceof TypeAdapter) {
      typeAdapter = (TypeAdapter)instance;
    } else if (instance instanceof TypeAdapterFactory) {
      typeAdapter = ((TypeAdapterFactory)instance).create(gson, type);
    } else if (instance instanceof JsonSerializer || instance instanceof JsonDeserializer) {
      JsonSerializer<?> serializer = (instance instanceof JsonSerializer) ? (JsonSerializer)instance : null;
      JsonDeserializer<?> deserializer = (instance instanceof JsonDeserializer) ? (JsonDeserializer)instance : null;
      TypeAdapter<?> tempAdapter = new TreeTypeAdapter(serializer, deserializer, gson, type, null, nullSafe);
      typeAdapter = tempAdapter;
      nullSafe = false;
    } else {
      throw new IllegalArgumentException("Invalid attempt to bind an instance of " + instance
          .getClass().getName() + " as a @JsonAdapter for " + type.toString() + ". @JsonAdapter value must be a TypeAdapter, TypeAdapterFactory, JsonSerializer or JsonDeserializer.");
    } 
    if (typeAdapter != null && nullSafe)
      typeAdapter = typeAdapter.nullSafe(); 
    return typeAdapter;
  }
}
