package me.syncwrld.booter.libs.google.gson.internal;

import java.lang.reflect.Type;

public final class Primitives {
  public static boolean isPrimitive(Type type) {
    return (type instanceof Class && ((Class)type).isPrimitive());
  }
  
  public static boolean isWrapperType(Type type) {
    return (type == Integer.class || type == Float.class || type == Byte.class || type == Double.class || type == Long.class || type == Character.class || type == Boolean.class || type == Short.class || type == Void.class);
  }
  
  public static <T> Class<T> wrap(Class<T> type) {
    if (type == int.class)
      return (Class)Integer.class; 
    if (type == float.class)
      return (Class)Float.class; 
    if (type == byte.class)
      return (Class)Byte.class; 
    if (type == double.class)
      return (Class)Double.class; 
    if (type == long.class)
      return (Class)Long.class; 
    if (type == char.class)
      return (Class)Character.class; 
    if (type == boolean.class)
      return (Class)Boolean.class; 
    if (type == short.class)
      return (Class)Short.class; 
    if (type == void.class)
      return (Class)Void.class; 
    return type;
  }
  
  public static <T> Class<T> unwrap(Class<T> type) {
    if (type == Integer.class)
      return (Class)int.class; 
    if (type == Float.class)
      return (Class)float.class; 
    if (type == Byte.class)
      return (Class)byte.class; 
    if (type == Double.class)
      return (Class)double.class; 
    if (type == Long.class)
      return (Class)long.class; 
    if (type == Character.class)
      return (Class)char.class; 
    if (type == Boolean.class)
      return (Class)boolean.class; 
    if (type == Short.class)
      return (Class)short.class; 
    if (type == Void.class)
      return (Class)void.class; 
    return type;
  }
}
