package me.syncwrld.booter.libs.unnamed.inject.key;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

public class TypeReference<T> extends Types.AbstractTypeWrapper implements Types.CompositeType {
  private final Class<T> rawType;
  
  private final Type type;
  
  protected TypeReference() {
    Type superClass = getClass().getGenericSuperclass();
    Validate.state(superClass instanceof ParameterizedType, "Invalid TypeReference creation.", new Object[0]);
    ParameterizedType parameterized = (ParameterizedType)superClass;
    this.type = Types.compose(parameterized.getActualTypeArguments()[0]);
    this.rawType = (Class)Types.getRawType(this.type);
    this.components.add(this.type);
  }
  
  TypeReference(Type type) {
    Validate.notNull(type);
    this.type = Types.compose(type);
    this.rawType = (Class)Types.getRawType(this.type);
    this.components.add(this.type);
  }
  
  TypeReference(Type type, Class<? super T> rawType) {
    Validate.notNull(type, "type", new Object[0]);
    Validate.notNull(rawType, "rawType", new Object[0]);
    this.type = Types.compose(type);
    this.rawType = (Class)Types.getRawType(rawType);
    this.components.add(this.type);
  }
  
  public static <T> TypeReference<T> of(Type type) {
    return new TypeReference<>(type);
  }
  
  public static <T> TypeReference<T> of(Class<?> rawType, Type... typeArguments) {
    Validate.notNull(rawType);
    return of(Types.parameterizedTypeOf(null, rawType, typeArguments));
  }
  
  public static <K, V> TypeReference<Map<K, V>> mapTypeOf(TypeReference<K> key, TypeReference<V> value) {
    return of(Map.class, new Type[] { key.getType(), value.getType() });
  }
  
  public final boolean isPureRawType() {
    return (this.type == this.rawType);
  }
  
  public final TypeReference<?> getFieldType(Field field) {
    Validate.notNull(field, "field", new Object[0]);
    Validate.argument(field
        .getDeclaringClass().isAssignableFrom(this.rawType), "Field '%s' isn't present in any super-type of '%s'", new Object[] { field
          
          .getName(), this.rawType });
    Type resolvedType = CompositeTypeReflector.resolveContextually(this, field
        .getGenericType());
    TypeReference<?> fieldType = new TypeReference(resolvedType, (Class)field.getType());
    return fieldType;
  }
  
  public final TypeReference<?> resolve(Type type) {
    Validate.notNull(type, "type", new Object[0]);
    type = CompositeTypeReflector.resolveContextually(this, type);
    return new TypeReference(type);
  }
  
  public final Class<T> getRawType() {
    return this.rawType;
  }
  
  public final Type getType() {
    return this.type;
  }
  
  public final TypeReference<T> canonicalize() {
    if (getClass() == TypeReference.class)
      return this; 
    return new TypeReference(this.type, this.rawType);
  }
  
  public final int hashCode() {
    return this.type.hashCode();
  }
  
  public final boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof TypeReference))
      return false; 
    TypeReference<?> other = (TypeReference)o;
    return this.type.equals(other.type);
  }
  
  public final String toString() {
    return Types.getTypeName(this.type);
  }
  
  protected final Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
  
  protected final void finalize() throws Throwable {
    super.finalize();
  }
}
