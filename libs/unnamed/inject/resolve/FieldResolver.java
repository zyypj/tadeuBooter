package me.syncwrld.booter.libs.unnamed.inject.resolve;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import me.syncwrld.booter.libs.unnamed.inject.Inject;
import me.syncwrld.booter.libs.unnamed.inject.InjectAll;
import me.syncwrld.booter.libs.unnamed.inject.InjectIgnore;
import me.syncwrld.booter.libs.unnamed.inject.key.InjectedKey;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;
import me.syncwrld.booter.libs.unnamed.inject.resolve.solution.InjectableField;

public final class FieldResolver {
  public List<InjectableField> get(TypeReference<?> type) {
    Solution solution = ComponentResolver.SOLUTIONS.get(type);
    if (solution == null || solution.fields == null) {
      if (solution == null) {
        solution = new Solution();
        ComponentResolver.SOLUTIONS.put(type, solution);
      } 
      if (solution.fields == null)
        solution.fields = resolve(type); 
    } 
    return solution.fields;
  }
  
  public List<InjectableField> resolve(TypeReference<?> type) {
    List<InjectableField> fields = new ArrayList<>();
    Class<?> clazz = type.getRawType();
    Class<?> checking = clazz;
    for (; checking != null && checking != Object.class; 
      checking = checking.getSuperclass()) {
      boolean injectAll = checking.isAnnotationPresent((Class)InjectAll.class);
      for (Field field : checking.getDeclaredFields()) {
        if (injectAll ? (
          field
          .isSynthetic() || field
          .isAnnotationPresent((Class)InjectIgnore.class)) : 
          
          !field.isAnnotationPresent((Class)Inject.class)) {
          TypeReference<?> fieldType = type.getFieldType(field);
          InjectedKey<?> key = ComponentResolver.KEY_RESOLVER.keyOf(fieldType, field.getAnnotations());
          fields.add(new InjectableField(type, key, field));
        } 
      } 
    } 
    return fields;
  }
}
