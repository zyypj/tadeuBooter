package me.syncwrld.booter.libs.unnamed.inject.resolve;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import me.syncwrld.booter.libs.unnamed.inject.Inject;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;
import me.syncwrld.booter.libs.unnamed.inject.resolve.solution.InjectableMethod;

public final class MethodResolver {
  public List<InjectableMethod> get(TypeReference<?> type) {
    Solution solution = ComponentResolver.SOLUTIONS.get(type);
    if (solution == null || solution.methods == null) {
      if (solution == null) {
        solution = new Solution();
        ComponentResolver.SOLUTIONS.put(type, solution);
      } 
      if (solution.methods == null)
        solution.methods = resolve(type, (Class)Inject.class); 
    } 
    return solution.methods;
  }
  
  public List<InjectableMethod> resolve(TypeReference<?> type, Class<? extends Annotation> annotation) {
    List<InjectableMethod> methods = new ArrayList<>();
    Class<?> clazz = type.getRawType();
    Class<?> checking = clazz;
    for (; checking != null && checking != Object.class; 
      checking = checking.getSuperclass()) {
      for (Method method : checking.getDeclaredMethods()) {
        if (method.isAnnotationPresent(annotation))
          methods.add(new InjectableMethod(type, ComponentResolver.KEY_RESOLVER
                
                .keysOf(type, method
                  
                  .getParameters()), method)); 
      } 
    } 
    return methods;
  }
}
