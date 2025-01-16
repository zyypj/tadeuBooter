package me.syncwrld.booter.libs.unnamed.inject.resolve;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import me.syncwrld.booter.libs.unnamed.inject.Qualifier;
import me.syncwrld.booter.libs.unnamed.inject.assisted.Assist;
import me.syncwrld.booter.libs.unnamed.inject.impl.Annotations;
import me.syncwrld.booter.libs.unnamed.inject.key.InjectedKey;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;

public final class KeyResolver {
  public List<InjectedKey<?>> keysOf(TypeReference<?> declaringType, Parameter[] parameters) {
    List<InjectedKey<?>> keys = new ArrayList<>(parameters.length);
    for (Parameter parameter : parameters) {
      Type type = parameter.getParameterizedType();
      Annotation[] annotations = parameter.getAnnotations();
      TypeReference<?> parameterType = declaringType.resolve(type);
      keys.add(keyOf(parameterType, annotations));
    } 
    return keys;
  }
  
  public <T> InjectedKey<T> keyOf(TypeReference<T> type, Annotation[] annotations) {
    boolean optional = false;
    boolean assisted = false;
    Class<? extends Annotation> qualifierType = null;
    Annotation qualifier = null;
    for (Annotation annotation : annotations) {
      Class<? extends Annotation> annotationType = annotation.annotationType();
      if (!optional) {
        String simpleName = annotationType.getSimpleName();
        if (simpleName.equalsIgnoreCase("Nullable")) {
          optional = true;
          continue;
        } 
      } 
      if (!assisted && annotationType == Assist.class)
        assisted = true; 
      if (qualifierType == null && qualifier == null && annotationType
        
        .isAnnotationPresent((Class)Qualifier.class))
        if (Annotations.containsOnlyDefaultValues(annotation)) {
          qualifierType = annotationType;
        } else {
          qualifier = annotation;
        }  
      continue;
    } 
    Key<T> key = Key.of(type, qualifierType, qualifier);
    return new InjectedKey(key, optional, assisted);
  }
}
