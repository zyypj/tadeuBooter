package me.syncwrld.booter.libs.unnamed.inject.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import me.syncwrld.booter.libs.unnamed.inject.Named;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

public final class Annotations {
  public static boolean containsOnlyDefaultValues(Annotation annotation) {
    for (Method method : annotation.annotationType().getDeclaredMethods()) {
      Object defaultValue = method.getDefaultValue();
      if (defaultValue == null)
        return false; 
      try {
        Object value = method.invoke(annotation, new Object[0]);
        if (!defaultValue.equals(value))
          return false; 
      } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException illegalAccessException) {}
    } 
    return true;
  }
  
  public static Named createNamed(String name) {
    Validate.notNull(name);
    return new NamedImpl(name);
  }
  
  private static class NamedImpl implements Named {
    private final String name;
    
    private final int hashCode;
    
    private NamedImpl(String name) {
      this.name = name;
      this.hashCode = 127 * "value".hashCode() ^ name.hashCode();
    }
    
    public Class<? extends Annotation> annotationType() {
      return (Class)Named.class;
    }
    
    public String value() {
      return this.name;
    }
    
    public int hashCode() {
      return this.hashCode;
    }
    
    public boolean equals(Object obj) {
      if (this == obj)
        return true; 
      if (!(obj instanceof Named))
        return false; 
      return this.name.equals(((Named)obj).value());
    }
    
    public String toString() {
      return "@Named(\"" + this.name + "\")";
    }
  }
}
