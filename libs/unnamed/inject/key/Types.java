package me.syncwrld.booter.libs.unnamed.inject.key;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

public final class Types {
  private static final Map<Class<?>, Class<?>> WRAPPER_TYPES = new HashMap<>();
  
  private static final List<String> OMITTED_PACKAGES = new ArrayList<>();
  
  private static final Type[] EMPTY_TYPE_ARRAY = new Type[0];
  
  static {
    OMITTED_PACKAGES.add("java.lang.");
    OMITTED_PACKAGES.add("java.util.");
    WRAPPER_TYPES.put(int.class, Integer.class);
    WRAPPER_TYPES.put(double.class, Double.class);
    WRAPPER_TYPES.put(float.class, Float.class);
    WRAPPER_TYPES.put(short.class, Short.class);
    WRAPPER_TYPES.put(long.class, Long.class);
    WRAPPER_TYPES.put(char.class, Character.class);
    WRAPPER_TYPES.put(byte.class, Byte.class);
    WRAPPER_TYPES.put(boolean.class, Boolean.class);
  }
  
  public static void omitPackage(String packageName) {
    Validate.notEmpty(packageName, "packageName", new Object[0]);
    OMITTED_PACKAGES.add(packageName);
  }
  
  private static Class<?> toWrapperIfPrimitive(Class<?> clazz) {
    Class<?> wrapper = WRAPPER_TYPES.get(clazz);
    return (wrapper == null) ? clazz : wrapper;
  }
  
  static Type compose(Type type) {
    if (type instanceof TypeReference)
      return ((TypeReference)type).getType(); 
    if (type instanceof Class) {
      Class<?> clazz = (Class)type;
      if (clazz.isArray())
        return genericArrayTypeOf(clazz.getComponentType()); 
      return toWrapperIfPrimitive(clazz);
    } 
    if (type instanceof CompositeType)
      return type; 
    if (type instanceof ParameterizedType) {
      ParameterizedType prototype = (ParameterizedType)type;
      Type rawType = prototype.getRawType();
      Validate.state(rawType instanceof Class, "Raw type isn't a class!", new Object[0]);
      return parameterizedTypeOf(prototype
          .getOwnerType(), (Class)rawType, prototype
          
          .getActualTypeArguments());
    } 
    if (type instanceof GenericArrayType) {
      GenericArrayType prototype = (GenericArrayType)type;
      return genericArrayTypeOf(prototype
          .getGenericComponentType());
    } 
    if (type instanceof WildcardType) {
      WildcardType prototype = (WildcardType)type;
      return new WildcardTypeWrapper(prototype
          .getUpperBounds(), prototype
          .getLowerBounds());
    } 
    return type;
  }
  
  static Class<?> getRawType(Type type) {
    Class<?> rawType = null;
    if (type instanceof Class) {
      rawType = (Class)type;
    } else if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType)type;
      Type typeRaw = parameterizedType.getRawType();
      Validate.state(typeRaw instanceof Class, "Raw type isn't a Class!", new Object[0]);
      rawType = (Class)typeRaw;
    } else if (type instanceof GenericArrayType) {
      Type componentType = ((GenericArrayType)type).getGenericComponentType();
      Class<?> componentRawType = getRawType(componentType);
      Object emptyArray = Array.newInstance(componentRawType, 0);
      rawType = emptyArray.getClass();
    } else {
      if (type instanceof java.lang.reflect.TypeVariable)
        return Object.class; 
      if (type instanceof WildcardType) {
        Type upperBound = ((WildcardType)type).getUpperBounds()[0];
        rawType = getRawType(upperBound);
      } 
    } 
    Validate.argument((rawType != null), "Cannot get raw type of '%s'", new Object[] { type });
    return toWrapperIfPrimitive(rawType);
  }
  
  static String getTypeName(Type type) {
    if (type instanceof Class) {
      Class<?> clazz = (Class)type;
      String className = clazz.getName();
      for (String packageName : OMITTED_PACKAGES) {
        if (className.startsWith(packageName)) {
          className = className.substring(packageName.length());
          break;
        } 
      } 
      return className;
    } 
    return type.toString();
  }
  
  static GenericArrayType genericArrayTypeOf(Type type) {
    type = compose(type);
    return new GenericArrayTypeWrapper(type);
  }
  
  public static ParameterizedType parameterizedTypeOf(Type ownerType, Class<?> rawType, Type... parameterTypes) {
    ownerType = compose(ownerType);
    parameterTypes = (Type[])parameterTypes.clone();
    for (int i = 0; i < parameterTypes.length; i++)
      parameterTypes[i] = compose(parameterTypes[i]); 
    return new ParameterizedTypeWrapper(rawType, parameterTypes, ownerType);
  }
  
  static WildcardType wildcardSuperTypeOf(Type type) {
    type = compose(type);
    return new WildcardTypeWrapper(new Type[] { Object.class }, new Type[] { type });
  }
  
  static WildcardType wildcardSubTypeOf(Type type) {
    type = compose(type);
    return new WildcardTypeWrapper(new Type[] { type }, EMPTY_TYPE_ARRAY);
  }
  
  static interface CompositeType {
    boolean requiresContext();
  }
  
  static abstract class AbstractTypeWrapper implements Type, CompositeType {
    protected final Set<Type> components = new HashSet<>();
    
    public boolean requiresContext() {
      for (Type component : this.components) {
        if (component instanceof Types.CompositeType) {
          if (((Types.CompositeType)component)
            .requiresContext())
            return true; 
          continue;
        } 
        if (component instanceof java.lang.reflect.TypeVariable)
          return true; 
        if (!(component instanceof Class))
          if (((Types.CompositeType)Types.compose(component))
            .requiresContext())
            return true;  
      } 
      return false;
    }
    
    public int hashCode() {
      int result = 1;
      for (Type component : this.components)
        result = 31 * result + component.hashCode(); 
      return result;
    }
    
    public abstract boolean equals(Object param1Object);
  }
  
  static class GenericArrayTypeWrapper extends AbstractTypeWrapper implements GenericArrayType {
    private final Type componentType;
    
    private GenericArrayTypeWrapper(Type componentType) {
      Validate.notNull(componentType, "componentType", new Object[0]);
      this.componentType = componentType;
      this.components.add(this.componentType);
    }
    
    public Type getGenericComponentType() {
      return this.componentType;
    }
    
    public boolean equals(Object o) {
      if (this == o)
        return true; 
      if (!(o instanceof GenericArrayType))
        return false; 
      GenericArrayType that = (GenericArrayType)o;
      return this.componentType.equals(that.getGenericComponentType());
    }
    
    public String toString() {
      return Types.getTypeName(this.componentType) + "[]";
    }
  }
  
  static class ParameterizedTypeWrapper extends AbstractTypeWrapper implements ParameterizedType {
    private final Class<?> rawType;
    
    private final Type[] typeArguments;
    
    private final Type ownerType;
    
    private ParameterizedTypeWrapper(Class<?> rawType, Type[] typeArguments, Type ownerType) {
      this.rawType = rawType;
      this.ownerType = ownerType;
      this.typeArguments = typeArguments;
      Collections.addAll(this.components, typeArguments);
      if (ownerType != null)
        this.components.add(ownerType); 
    }
    
    public Type getRawType() {
      return this.rawType;
    }
    
    public Type[] getActualTypeArguments() {
      return this.typeArguments;
    }
    
    public Type getOwnerType() {
      return this.ownerType;
    }
    
    public boolean equals(Object o) {
      if (this == o)
        return true; 
      if (!(o instanceof ParameterizedType))
        return false; 
      ParameterizedType that = (ParameterizedType)o;
      return (this.rawType.equals(that.getRawType()) && 
        Arrays.equals((Object[])this.typeArguments, (Object[])that.getActualTypeArguments()) && ((this.ownerType == null) ? (that
        
        .getOwnerType() == null) : this.ownerType
        .equals(that.getOwnerType())));
    }
    
    public String toString() {
      StringBuilder builder = new StringBuilder();
      String clazz = this.rawType.getName();
      if (this.ownerType != null) {
        builder.append(Types.getTypeName(this.ownerType));
        builder.append('.');
        String prefix = (this.ownerType instanceof ParameterizedType) ? (((Class)((ParameterizedType)this.ownerType).getRawType()).getName() + '$') : (((Class)this.ownerType).getName() + '$');
        if (clazz.startsWith(prefix))
          clazz = clazz.substring(prefix.length()); 
      } 
      builder.append(clazz);
      if (this.typeArguments.length != 0) {
        builder.append('<');
        for (int i = 0; i < this.typeArguments.length; i++) {
          builder.append(Types.getTypeName(this.typeArguments[i]));
          if (i != this.typeArguments.length - 1)
            builder.append(", "); 
        } 
        builder.append('>');
      } 
      return builder.toString();
    }
  }
  
  static class WildcardTypeWrapper extends AbstractTypeWrapper implements WildcardType {
    private final Type[] upperBounds;
    
    private final Type[] lowerBounds;
    
    private WildcardTypeWrapper(Type[] upperBounds, Type[] lowerBounds) {
      Validate.argument((upperBounds.length == 1), "The wildcard must have 1 upper bound. For unbound wildcards, just use Object", new Object[0]);
      Validate.argument((lowerBounds.length < 2), "The wildcard must have at most 1 lower bound", new Object[0]);
      if (lowerBounds.length == 1) {
        this.lowerBounds = new Type[] { Types.compose(lowerBounds[0]) };
        this.upperBounds = new Type[] { Object.class };
      } else {
        this.lowerBounds = Types.EMPTY_TYPE_ARRAY;
        this.upperBounds = new Type[] { Types.compose(upperBounds[0]) };
      } 
      Collections.addAll(this.components, this.upperBounds);
      Collections.addAll(this.components, this.lowerBounds);
    }
    
    public Type[] getUpperBounds() {
      return this.upperBounds;
    }
    
    public Type[] getLowerBounds() {
      return this.lowerBounds;
    }
    
    public boolean equals(Object o) {
      if (this == o)
        return true; 
      if (!(o instanceof WildcardType))
        return false; 
      WildcardType other = (WildcardType)o;
      return (Arrays.equals((Object[])this.upperBounds, (Object[])other.getUpperBounds()) && 
        Arrays.equals((Object[])this.lowerBounds, (Object[])other.getLowerBounds()));
    }
    
    public String toString() {
      if (this.lowerBounds.length == 1)
        return "? super " + Types.getTypeName(this.lowerBounds[0]); 
      if (this.upperBounds[0] == Object.class)
        return "?"; 
      return "? extends " + Types.getTypeName(this.upperBounds[0]);
    }
  }
}
