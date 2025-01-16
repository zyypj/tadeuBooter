package me.syncwrld.booter.libs.unnamed.inject.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import me.syncwrld.booter.libs.unnamed.inject.key.InjectedKey;

public final class ElementFormatter {
  public static String formatField(Field field, InjectedKey<?> key) {
    StringBuilder builder = new StringBuilder();
    if (key.isOptional())
      builder.append("@Nullable "); 
    builder.append(key.getKey().getType());
    builder.append(' ');
    builder.append(field.getName());
    return builder.toString();
  }
  
  public static String formatConstructor(Constructor<?> constructor, List<InjectedKey<?>> keys) {
    Validate.notNull(constructor, "constructor", new Object[0]);
    return constructor.getDeclaringClass().getName() + '(' + 
      formatParameters(constructor.getParameters(), keys) + ')';
  }
  
  public static String formatMethod(Method method, List<InjectedKey<?>> keys) {
    return method.getDeclaringClass().getName() + '#' + method.getName() + '(' + 
      formatParameters(method.getParameters(), keys) + ')';
  }
  
  public static String annotationToString(Annotation annotation) {
    StringBuilder builder = new StringBuilder("@");
    builder.append(annotation.annotationType().getSimpleName());
    builder.append("(");
    Method[] methods = annotation.annotationType().getDeclaredMethods();
    for (int i = 0; i < methods.length; i++) {
      Method method = methods[i];
      String methodName = method.getName();
      Object value = "<non accessible>";
      try {
        value = method.invoke(annotation, new Object[0]);
      } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException illegalAccessException) {}
      if (!methodName.equals("value") || methods.length != 1) {
        builder.append(methodName);
        builder.append(" = ");
      } 
      if (value instanceof String) {
        builder.append("\"");
        builder.append(value);
        builder.append("\"");
      } else {
        builder.append(value);
      } 
      if (i != methods.length - 1)
        builder.append(", "); 
    } 
    builder.append(")");
    return builder.toString();
  }
  
  private static String formatParameters(Parameter[] parameters, List<InjectedKey<?>> keys) {
    Validate.notNull(parameters, "parameters", new Object[0]);
    Validate.notNull(keys, "keys", new Object[0]);
    Validate.argument((parameters.length == keys.size()), "Parameters length and keys length must be the same", new Object[0]);
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < parameters.length; i++) {
      Parameter parameter = parameters[i];
      InjectedKey<?> key = keys.get(i);
      if (key.isOptional())
        builder.append("@Nullable "); 
      builder.append(key.getKey().getType());
      builder.append(' ');
      builder.append(parameter.getName());
      if (i < parameters.length - 1)
        builder.append(", "); 
    } 
    return builder.toString();
  }
}
