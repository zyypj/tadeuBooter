package me.syncwrld.booter.libs.unnamed.inject.resolve.solution;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.util.Collections;
import java.util.List;
import me.syncwrld.booter.libs.unnamed.inject.impl.InjectorImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.ProvisionStack;
import me.syncwrld.booter.libs.unnamed.inject.key.InjectedKey;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;
import me.syncwrld.booter.libs.unnamed.inject.util.ElementFormatter;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

public class InjectableConstructor implements InjectableMember {
  private final List<InjectedKey<?>> keys;
  
  private final TypeReference<?> declaringType;
  
  private final Constructor<?> constructor;
  
  public InjectableConstructor(List<InjectedKey<?>> keys, Constructor<?> constructor) {
    this.keys = Collections.unmodifiableList(keys);
    this.constructor = constructor;
    for (InjectedKey<?> key : keys)
      Validate.doesntRequiresContext(key.getKey()); 
    if (constructor != null) {
      this.constructor.setAccessible(true);
      this.declaringType = TypeReference.of(constructor.getDeclaringClass());
    } else {
      this.declaringType = null;
    } 
  }
  
  public TypeReference<?> getDeclaringType() {
    return this.declaringType;
  }
  
  public Constructor<?> getMember() {
    return this.constructor;
  }
  
  public List<InjectedKey<?>> getKeys() {
    return this.keys;
  }
  
  public Object inject(InjectorImpl injector, ProvisionStack stack, Object target) {
    Object[] values = new Object[this.keys.size()];
    for (int i = 0; i < this.keys.size(); i++) {
      InjectedKey<?> key = this.keys.get(i);
      Object value = injector.getValue(key, stack);
      if (value == InjectorImpl.ABSENT_INSTANCE) {
        stack.attach(new String[] { "Cannot instantiate class\n\tClass: " + this.constructor
              
              .getName() + "\n\tReason: Cannot get value for required parameter (index " + i + ") \n\tRequired Key: " + key
              
              .getKey() });
        return null;
      } 
      values[i] = value;
    } 
    try {
      return this.constructor.newInstance(values);
    } catch (InstantiationException|IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
      stack.attach("Errors while constructing " + 
          
          ElementFormatter.formatConstructor(this.constructor, this.keys), e);
      return null;
    } 
  }
}
