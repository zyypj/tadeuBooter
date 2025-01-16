package me.syncwrld.booter.libs.unnamed.inject.key;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Objects;
import me.syncwrld.booter.libs.unnamed.inject.util.ElementFormatter;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

public final class Key<T> implements Types.CompositeType, Serializable {
  private static final long serialVersionUID = 987654321L;
  
  private final TypeReference<T> type;
  
  private final Class<? extends Annotation> qualifierType;
  
  private final Annotation qualifier;
  
  private final int hashCode;
  
  public Key(TypeReference<T> type, Class<? extends Annotation> qualifierType, Annotation qualifier) {
    Validate.notNull(type, "type", new Object[0]);
    Validate.argument((qualifierType == null || qualifier == null), "Cannot use both qualifierType and qualifier qualifiers!", new Object[0]);
    this.type = type.canonicalize();
    this.qualifierType = qualifierType;
    this.qualifier = qualifier;
    this.hashCode = computeHashCode();
  }
  
  public static <T> Key<T> of(Class<T> type) {
    return of(TypeReference.of(type));
  }
  
  public static <T> Key<T> of(TypeReference<T> type) {
    return new Key<>(type, null, null);
  }
  
  public static <T> Key<T> of(TypeReference<T> type, Class<? extends Annotation> qualifierType, Annotation qualifier) {
    return new Key<>(type, qualifierType, qualifier);
  }
  
  public boolean isPureRawType() {
    return this.type.isPureRawType();
  }
  
  public boolean requiresContext() {
    return this.type.requiresContext();
  }
  
  public TypeReference<T> getType() {
    return this.type;
  }
  
  public Annotation getQualifier() {
    return this.qualifier;
  }
  
  public Class<? extends Annotation> getQualifierType() {
    return this.qualifierType;
  }
  
  public <R> Key<R> withType(TypeReference<R> type) {
    return new Key(type, this.qualifierType, this.qualifier);
  }
  
  public Key<T> withQualifier(Annotation qualifier) {
    return new Key(this.type, null, qualifier);
  }
  
  public Key<T> withQualifier(Class<? extends Annotation> qualifierType) {
    return new Key(this.type, qualifierType, null);
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (!(o instanceof Key))
      return false; 
    Key<?> key = (Key)o;
    return (this.hashCode == key.hashCode && this.type
      .equals(key.type) && 
      Objects.equals(this.qualifier, key.qualifier) && 
      Objects.equals(this.qualifierType, key.qualifierType));
  }
  
  private int computeHashCode() {
    return Objects.hash(new Object[] { this.type, this.qualifier, this.qualifierType });
  }
  
  public int hashCode() {
    return this.hashCode;
  }
  
  public String toString() {
    StringBuilder builder = new StringBuilder(this.type.toString());
    if (this.qualifierType != null) {
      builder.append(" marked with @")
        .append(this.qualifierType.getSimpleName());
    } else if (this.qualifier != null) {
      builder.append(" annotated with ")
        .append(ElementFormatter.annotationToString(this.qualifier));
    } 
    return builder.toString();
  }
}
