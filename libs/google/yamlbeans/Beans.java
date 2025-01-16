package me.syncwrld.booter.libs.google.yamlbeans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

class Beans {
  public static boolean isScalar(Class<String> c) {
    return (c.isPrimitive() || c == String.class || c == Integer.class || c == Boolean.class || c == Float.class || c == Long.class || c == Double.class || c == Short.class || c == Byte.class || c == Character.class);
  }
  
  public static DeferredConstruction getDeferredConstruction(Class type, YamlConfig config) {
    YamlConfig.ConstructorParameters parameters = config.readConfig.constructorParameters.get(type);
    if (parameters != null)
      return new DeferredConstruction(parameters.constructor, parameters.parameterNames); 
    try {
      Class<?> constructorProperties = Class.forName("java.beans.ConstructorProperties");
      Constructor[] arrayOfConstructor;
      int i;
      byte b;
      for (arrayOfConstructor = (Constructor[])type.getConstructors(), i = arrayOfConstructor.length, b = 0; b < i; ) {
        Constructor<?> typeConstructor = arrayOfConstructor[b];
        Annotation annotation = (Annotation)typeConstructor.getAnnotation(constructorProperties);
        if (annotation == null) {
          b++;
          continue;
        } 
        String[] parameterNames = (String[])constructorProperties.getMethod("value", new Class[0]).invoke(annotation, (Object[])null);
        return new DeferredConstruction(typeConstructor, parameterNames);
      } 
    } catch (Exception exception) {}
    return null;
  }
  
  public static Object createObject(Class<?> type, boolean privateConstructors) throws InvocationTargetException {
    Constructor<?> constructor = null;
    for (Constructor<?> typeConstructor : type.getConstructors()) {
      if ((typeConstructor.getParameterTypes()).length == 0) {
        constructor = typeConstructor;
        break;
      } 
    } 
    if (constructor == null && privateConstructors)
      try {
        constructor = type.getDeclaredConstructor(new Class[0]);
        constructor.setAccessible(true);
      } catch (SecurityException securityException) {
      
      } catch (NoSuchMethodException noSuchMethodException) {} 
    if (constructor == null)
      try {
        if (List.class.isAssignableFrom(type)) {
          constructor = ArrayList.class.getConstructor(new Class[0]);
        } else if (Set.class.isAssignableFrom(type)) {
          constructor = HashSet.class.getConstructor(new Class[0]);
        } else if (Map.class.isAssignableFrom(type)) {
          constructor = HashMap.class.getConstructor(new Class[0]);
        } 
      } catch (Exception ex) {
        throw new InvocationTargetException(ex, "Error getting constructor for class: " + type.getName());
      }  
    if (constructor == null)
      throw new InvocationTargetException(null, "Unable to find a no-arg constructor for class: " + type.getName()); 
    try {
      return constructor.newInstance(new Object[0]);
    } catch (Exception ex) {
      throw new InvocationTargetException(ex, "Error constructing instance of class: " + type.getName());
    } 
  }
  
  public static Set<Property> getProperties(Class<?> type, boolean beanProperties, boolean privateFields, YamlConfig config) {
    if (type == null)
      throw new IllegalArgumentException("type cannot be null."); 
    Set<Property> properties = config.writeConfig.keepBeanPropertyOrder ? new LinkedHashSet<Property>() : new TreeSet<Property>();
    for (Field field : getAllFields(type)) {
      Property property = getProperty(type, beanProperties, privateFields, config, field);
      if (property != null)
        properties.add(property); 
    } 
    return properties;
  }
  
  private static String toJavaIdentifier(String name) {
    StringBuilder buffer = new StringBuilder();
    for (int i = 0, n = name.length(); i < n; i++) {
      char c = name.charAt(i);
      if (Character.isJavaIdentifierPart(c))
        buffer.append(c); 
    } 
    return buffer.toString();
  }
  
  public static Property getProperty(Class<?> type, String name, boolean beanProperties, boolean privateFields, YamlConfig config) {
    if (type == null)
      throw new IllegalArgumentException("type cannot be null."); 
    if (name == null || name.length() == 0)
      throw new IllegalArgumentException("name cannot be null or empty."); 
    name = toJavaIdentifier(name);
    for (Field field : getAllFields(type)) {
      if (field.getName().equals(name))
        return getProperty(type, beanProperties, privateFields, config, field); 
    } 
    if (!name.startsWith("_"))
      return getProperty(type, "_" + name, beanProperties, privateFields, config); 
    return null;
  }
  
  private static Property getProperty(Class<?> type, boolean beanProperties, boolean privateFields, YamlConfig config, Field field) {
    Property property = null;
    if (beanProperties) {
      String name = field.getName();
      DeferredConstruction deferredConstruction = getDeferredConstruction(type, config);
      boolean constructorProperty = (deferredConstruction != null && deferredConstruction.hasParameter(name));
      String nameUpper = Character.toUpperCase(name.charAt(0)) + name.substring(1);
      String setMethodName = "set" + nameUpper;
      String getMethodName = "get" + nameUpper;
      if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
        setMethodName = name.startsWith("is") ? ("set" + Character.toUpperCase(name.charAt(2)) + name.substring(3)) : setMethodName;
        getMethodName = name.startsWith("is") ? name : ("is" + nameUpper);
      } 
      Method getMethod = null;
      Method setMethod = null;
      try {
        setMethod = type.getMethod(setMethodName, new Class[] { field.getType() });
      } catch (Exception exception) {}
      try {
        getMethod = type.getMethod(getMethodName, new Class[0]);
      } catch (Exception exception) {}
      if (getMethod == null && (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)))
        try {
          getMethod = type.getMethod("get" + nameUpper, new Class[0]);
        } catch (Exception exception) {} 
      if (getMethod != null && (setMethod != null || constructorProperty))
        return new MethodProperty(name, setMethod, getMethod); 
    } 
    int modifiers = field.getModifiers();
    if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers) && (
      Modifier.isPublic(modifiers) || privateFields)) {
      field.setAccessible(true);
      property = new FieldProperty(field);
    } 
    return property;
  }
  
  private static ArrayList<Field> getAllFields(Class<Object> type) {
    ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
    Class<Object> nextClass = type;
    while (nextClass != null && nextClass != Object.class) {
      classes.add(nextClass);
      nextClass = (Class)nextClass.getSuperclass();
    } 
    ArrayList<Field> allFields = new ArrayList<Field>();
    for (int i = classes.size() - 1; i >= 0; i--)
      Collections.addAll(allFields, ((Class)classes.get(i)).getDeclaredFields()); 
    return allFields;
  }
  
  public static class MethodProperty extends Property {
    private final Method setMethod;
    
    private final Method getMethod;
    
    public MethodProperty(String name, Method setMethod, Method getMethod) {
      super(getMethod.getDeclaringClass(), name, getMethod.getReturnType(), getMethod.getGenericReturnType());
      this.setMethod = setMethod;
      this.getMethod = getMethod;
    }
    
    public void set(Object object, Object value) throws Exception {
      if (object instanceof DeferredConstruction) {
        ((DeferredConstruction)object).storeProperty(this, value);
        return;
      } 
      this.setMethod.invoke(object, new Object[] { value });
    }
    
    public Object get(Object object) throws Exception {
      return this.getMethod.invoke(object, new Object[0]);
    }
  }
  
  public static class FieldProperty extends Property {
    private final Field field;
    
    public FieldProperty(Field field) {
      super(field.getDeclaringClass(), field.getName(), field.getType(), field.getGenericType());
      this.field = field;
    }
    
    public void set(Object object, Object value) throws Exception {
      if (object instanceof DeferredConstruction) {
        ((DeferredConstruction)object).storeProperty(this, value);
        return;
      } 
      this.field.set(object, value);
    }
    
    public Object get(Object object) throws Exception {
      return this.field.get(object);
    }
  }
  
  public static abstract class Property implements Comparable<Property> {
    private final Class declaringClass;
    
    private final String name;
    
    private final Class type;
    
    private final Class elementType;
    
    Property(Class declaringClass, String name, Class type, Type genericType) {
      this.declaringClass = declaringClass;
      this.name = name;
      this.type = type;
      this.elementType = getElementTypeFromGenerics(genericType);
    }
    
    private Class getElementTypeFromGenerics(Type type) {
      if (type instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType)type;
        Type rawType = parameterizedType.getRawType();
        if (isCollection(rawType) || isMap(rawType)) {
          Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
          if (actualTypeArguments.length > 0) {
            Type cType = actualTypeArguments[actualTypeArguments.length - 1];
            if (cType instanceof Class)
              return (Class)cType; 
            if (cType instanceof WildcardType) {
              WildcardType t = (WildcardType)cType;
              Type bound = t.getUpperBounds()[0];
              return (bound instanceof Class) ? (Class)bound : null;
            } 
            if (cType instanceof ParameterizedType) {
              ParameterizedType t = (ParameterizedType)cType;
              Type rt = t.getRawType();
              return (rt instanceof Class) ? (Class)rt : null;
            } 
          } 
        } 
      } 
      return null;
    }
    
    private boolean isMap(Type type) {
      return Map.class.isAssignableFrom((Class)type);
    }
    
    private boolean isCollection(Type type) {
      return Collection.class.isAssignableFrom((Class)type);
    }
    
    public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + ((this.declaringClass == null) ? 0 : this.declaringClass.hashCode());
      result = 31 * result + ((this.name == null) ? 0 : this.name.hashCode());
      result = 31 * result + ((this.type == null) ? 0 : this.type.hashCode());
      result = 31 * result + ((this.elementType == null) ? 0 : this.elementType.hashCode());
      return result;
    }
    
    public boolean equals(Object obj) {
      if (this == obj)
        return true; 
      if (obj == null)
        return false; 
      if (getClass() != obj.getClass())
        return false; 
      Property other = (Property)obj;
      if (this.declaringClass == null) {
        if (other.declaringClass != null)
          return false; 
      } else if (!this.declaringClass.equals(other.declaringClass)) {
        return false;
      } 
      if (this.name == null) {
        if (other.name != null)
          return false; 
      } else if (!this.name.equals(other.name)) {
        return false;
      } 
      if (this.type == null) {
        if (other.type != null)
          return false; 
      } else if (!this.type.equals(other.type)) {
        return false;
      } 
      if (this.elementType == null) {
        if (other.elementType != null)
          return false; 
      } else if (!this.elementType.equals(other.elementType)) {
        return false;
      } 
      return true;
    }
    
    public Class getDeclaringClass() {
      return this.declaringClass;
    }
    
    public Class getElementType() {
      return this.elementType;
    }
    
    public Class getType() {
      return this.type;
    }
    
    public String getName() {
      return this.name;
    }
    
    public String toString() {
      return this.name;
    }
    
    public int compareTo(Property o) {
      int comparison = this.name.compareTo(o.name);
      if (comparison != 0) {
        if (this.name.equals("id"))
          return -1; 
        if (o.name.equals("id"))
          return 1; 
        if (this.name.equals("name"))
          return -1; 
        if (o.name.equals("name"))
          return 1; 
      } 
      return comparison;
    }
    
    public abstract void set(Object param1Object1, Object param1Object2) throws Exception;
    
    public abstract Object get(Object param1Object) throws Exception;
  }
}
