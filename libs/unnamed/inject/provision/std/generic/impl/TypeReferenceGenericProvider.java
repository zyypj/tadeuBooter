package me.syncwrld.booter.libs.unnamed.inject.provision.std.generic.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;
import me.syncwrld.booter.libs.unnamed.inject.provision.std.generic.GenericProvider;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

public class TypeReferenceGenericProvider implements GenericProvider<TypeReference<?>> {
  public TypeReference<?> get(Key<?> match) {
    TypeReference<?> typeMirror = match.getType();
    Type type = typeMirror.getType();
    Validate.state(type instanceof ParameterizedType, "Unsupported type '" + type + "'. The type must be a parameterized type.", new Object[0]);
    return TypeReference.of(((ParameterizedType)type)
        
        .getActualTypeArguments()[0]);
  }
}
