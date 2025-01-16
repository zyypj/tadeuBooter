package me.syncwrld.booter.libs.unnamed.inject.resolve;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import me.syncwrld.booter.libs.unnamed.inject.Inject;
import me.syncwrld.booter.libs.unnamed.inject.error.ErrorAttachable;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;
import me.syncwrld.booter.libs.unnamed.inject.resolve.solution.InjectableConstructor;

public final class ConstructorResolver {
  static final Object CONSTRUCTOR_NOT_DEFINED = new Object();
  
  public InjectableConstructor get(ErrorAttachable errors, TypeReference<?> type) {
    Solution solution = ComponentResolver.SOLUTIONS.get(type);
    if (solution == null || solution.constructor == CONSTRUCTOR_NOT_DEFINED) {
      if (solution == null) {
        solution = new Solution();
        ComponentResolver.SOLUTIONS.put(type, solution);
      } 
      solution.constructor = resolve(errors, type, (Class)Inject.class);
    } 
    return (InjectableConstructor)solution.constructor;
  }
  
  public InjectableConstructor resolve(ErrorAttachable errors, TypeReference<?> type, Class<? extends Annotation> annotation) {
    Class<?> rawType = type.getRawType();
    Constructor<?> injectableConstructor = null;
    for (Constructor<?> constructor : rawType.getDeclaredConstructors()) {
      if (constructor.isAnnotationPresent(annotation)) {
        injectableConstructor = constructor;
        break;
      } 
    } 
    if (injectableConstructor == null)
      try {
        injectableConstructor = rawType.getDeclaredConstructor(new Class[0]);
      } catch (NoSuchMethodException noSuchMethodException) {} 
    if (injectableConstructor == null) {
      errors.attach(new String[] { "No constructor found for type '" + type + "'" });
      return null;
    } 
    return new InjectableConstructor(
        ComponentResolver.keys().keysOf(type, injectableConstructor
          
          .getParameters()), injectableConstructor);
  }
}
