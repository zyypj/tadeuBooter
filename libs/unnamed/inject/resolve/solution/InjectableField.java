package me.syncwrld.booter.libs.unnamed.inject.resolve.solution;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Objects;
import me.syncwrld.booter.libs.unnamed.inject.impl.InjectorImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.ProvisionStack;
import me.syncwrld.booter.libs.unnamed.inject.key.InjectedKey;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;
import me.syncwrld.booter.libs.unnamed.inject.util.ElementFormatter;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

public class InjectableField implements InjectableMember {
  private final TypeReference<?> declaringType;
  
  private final InjectedKey<?> key;
  
  private final Field field;
  
  public InjectableField(TypeReference<?> declaringType, InjectedKey<?> key, Field field) {
    this.declaringType = (TypeReference)Validate.notNull(declaringType, "declaringType", new Object[0]);
    this.key = (InjectedKey)Validate.notNull(key, "key", new Object[0]);
    this.field = (Field)Validate.notNull(field, "field", new Object[0]);
    Validate.doesntRequiresContext(key.getKey());
    this.field.setAccessible(true);
  }
  
  public TypeReference<?> getDeclaringType() {
    return this.declaringType;
  }
  
  public Field getMember() {
    return this.field;
  }
  
  public Object inject(InjectorImpl injector, ProvisionStack stack, Object target) {
    if ((((target == null) ? 1 : 0) ^ Modifier.isStatic(this.field.getModifiers())) != 0)
      return null; 
    Object value = injector.getValue(this.key, stack);
    if (value == InjectorImpl.ABSENT_INSTANCE) {
      stack.attach(new String[] { "Cannot inject '" + this.field
            .getName() + "' field.\n\tAt:" + this.declaringType + "\n\tReason: Cannot get value for required key \n\tRequired Key: " + this.key
            
            .getKey() });
      return null;
    } 
    try {
      this.field.set(target, value);
    } catch (IllegalAccessException e) {
      stack.attach("Cannot inject field " + 
          
          ElementFormatter.formatField(this.field, this.key), e);
    } 
    return null;
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (o == null || getClass() != o.getClass())
      return false; 
    InjectableField that = (InjectableField)o;
    return (this.declaringType.equals(that.declaringType) && this.key
      .equals(that.key) && this.field
      .equals(that.field));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.declaringType, this.key, this.field });
  }
}
