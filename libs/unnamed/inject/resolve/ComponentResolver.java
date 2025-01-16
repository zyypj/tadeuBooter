package me.syncwrld.booter.libs.unnamed.inject.resolve;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;

public class ComponentResolver {
  static final KeyResolver KEY_RESOLVER = new KeyResolver();
  
  static final Map<TypeReference<?>, Solution> SOLUTIONS = new ConcurrentHashMap<>();
  
  private static final ConstructorResolver CONSTRUCTOR_RESOLVER = new ConstructorResolver();
  
  private static final FieldResolver FIELD_RESOLVER = new FieldResolver();
  
  private static final MethodResolver METHOD_RESOLVER = new MethodResolver();
  
  public static KeyResolver keys() {
    return KEY_RESOLVER;
  }
  
  public static ConstructorResolver constructor() {
    return CONSTRUCTOR_RESOLVER;
  }
  
  public static MethodResolver methods() {
    return METHOD_RESOLVER;
  }
  
  public static FieldResolver fields() {
    return FIELD_RESOLVER;
  }
}
