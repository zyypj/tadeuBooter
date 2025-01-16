package me.syncwrld.booter.libs.unnamed.inject.util;

import me.syncwrld.booter.libs.unnamed.inject.error.InjectionException;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;

public final class Validate {
  public static <T> T notNull(T object, String message, Object... parameters) {
    if (object == null) {
      if (message == null)
        throw new NullPointerException(); 
      throw new NullPointerException(String.format(message, parameters));
    } 
    return object;
  }
  
  public static <T> T notNull(T object) {
    return notNull(object, null, new Object[0]);
  }
  
  public static void state(boolean expression, String message, Object... parameters) {
    if (!expression)
      throw new IllegalStateException(String.format(message, parameters)); 
  }
  
  public static void state(boolean expression) {
    state(expression, null, new Object[0]);
  }
  
  public static void argument(boolean expression, String message, Object... parameters) {
    if (!expression)
      throw new IllegalArgumentException(String.format(message, parameters)); 
  }
  
  public static void argument(boolean expression) {
    argument(expression, null, new Object[0]);
  }
  
  public static String notEmpty(String string, String message, Object... parameters) {
    if (string == null)
      throw new NullPointerException(String.format(message, parameters)); 
    if (string.length() == 0)
      throw new IllegalArgumentException(String.format(message, parameters)); 
    return string;
  }
  
  public static String notEmpty(String string) {
    return notEmpty(string, null, new Object[0]);
  }
  
  public static <T> void doesntRequiresContext(Key<T> key) {
    if (key.requiresContext())
      throw new InjectionException("The type '" + key.getType() + "' requires a context to be fully-specified!"); 
  }
}
