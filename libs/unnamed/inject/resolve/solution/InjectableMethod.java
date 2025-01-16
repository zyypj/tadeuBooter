package me.syncwrld.booter.libs.unnamed.inject.resolve.solution;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import me.syncwrld.booter.libs.unnamed.inject.impl.InjectorImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.ProvisionStack;
import me.syncwrld.booter.libs.unnamed.inject.key.InjectedKey;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;
import me.syncwrld.booter.libs.unnamed.inject.util.ElementFormatter;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

public class InjectableMethod implements InjectableMember {
  private final TypeReference<?> declaringType;
  
  private final List<InjectedKey<?>> keys;
  
  private final Method method;
  
  public InjectableMethod(TypeReference<?> declaringType, List<InjectedKey<?>> keys, Method method) {
    this.declaringType = (TypeReference)Validate.notNull(declaringType);
    this.keys = Collections.unmodifiableList(keys);
    this.method = (Method)Validate.notNull(method);
    for (InjectedKey<?> key : keys)
      Validate.doesntRequiresContext(key.getKey()); 
    this.method.setAccessible(true);
  }
  
  public TypeReference<?> getDeclaringType() {
    return this.declaringType;
  }
  
  public Method getMember() {
    return this.method;
  }
  
  public Object inject(InjectorImpl injector, ProvisionStack stack, Object target) {
    if ((((target == null) ? 1 : 0) ^ Modifier.isStatic(this.method.getModifiers())) != 0)
      return null; 
    Object[] values = new Object[this.keys.size()];
    for (int i = 0; i < this.keys.size(); i++) {
      InjectedKey<?> key = this.keys.get(i);
      Object value = injector.getValue(key, stack);
      if (value == InjectorImpl.ABSENT_INSTANCE) {
        stack.attach(new String[] { "Cannot inject '" + this.method
              .getName() + "' method.\n\tAt:" + this.declaringType + "\n\tReason: Cannot get value for required parameter (index " + i + ") \n\tRequired Key: " + key
              
              .getKey() });
        return null;
      } 
      values[i] = value;
    } 
    try {
      return this.method.invoke(target, values);
    } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
      stack.attach("Cannot inject method " + 
          
          ElementFormatter.formatMethod(this.method, this.keys), e);
      return null;
    } 
  }
}
