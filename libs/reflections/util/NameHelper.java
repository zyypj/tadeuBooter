package me.syncwrld.booter.libs.reflections.util;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import me.syncwrld.booter.libs.javax.annotation.Nullable;
import me.syncwrld.booter.libs.reflections.ReflectionsException;

public interface NameHelper {
  public static final List<String> primitiveNames = Arrays.asList(new String[] { "boolean", "char", "byte", "short", "int", "long", "float", "double", "void" });
  
  public static final List<Class<?>> primitiveTypes = Arrays.asList(new Class[] { boolean.class, char.class, byte.class, short.class, int.class, long.class, float.class, double.class, void.class });
  
  public static final List<String> primitiveDescriptors = Arrays.asList(new String[] { "Z", "C", "B", "S", "I", "J", "F", "D", "V" });
  
  default String toName(AnnotatedElement element) {
    return element.getClass().equals(Class.class) ? toName((Class)element) : (
      element.getClass().equals(Constructor.class) ? toName((Constructor)element) : (
      element.getClass().equals(Method.class) ? toName((Method)element) : (
      element.getClass().equals(Field.class) ? toName((Field)element) : null)));
  }
  
  default String toName(Class<?> type) {
    int dim = 0;
    while (type.isArray()) {
      dim++;
      type = type.getComponentType();
    } 
    return type.getName() + String.join("", Collections.nCopies(dim, "[]"));
  }
  
  default String toName(Constructor<?> constructor) {
    return String.format("%s.<init>(%s)", new Object[] { constructor.getName(), String.join(", ", (Iterable)toNames((AnnotatedElement[])constructor.getParameterTypes())) });
  }
  
  default String toName(Method method) {
    return String.format("%s.%s(%s)", new Object[] { method.getDeclaringClass().getName(), method.getName(), String.join(", ", (Iterable)toNames((AnnotatedElement[])method.getParameterTypes())) });
  }
  
  default String toName(Field field) {
    return String.format("%s.%s", new Object[] { field.getDeclaringClass().getName(), field.getName() });
  }
  
  default Collection<String> toNames(Collection<? extends AnnotatedElement> elements) {
    return (Collection<String>)elements.stream().map(this::toName).filter(Objects::nonNull).collect(Collectors.toList());
  }
  
  Collection<String> toNames(AnnotatedElement... elements) {
    return toNames(Arrays.asList(elements));
  }
  
  <T> T forName(String name, Class<T> resultType, ClassLoader... loaders) {
    return resultType.equals(Class.class) ? (T)forClass(name, loaders) : (
      resultType.equals(Constructor.class) ? (T)forConstructor(name, loaders) : (
      resultType.equals(Method.class) ? (T)forMethod(name, loaders) : (
      resultType.equals(Field.class) ? (T)forField(name, loaders) : (
      resultType.equals(Member.class) ? (T)forMember(name, loaders) : null))));
  }
  
  Class<?> forClass(String typeName, ClassLoader... loaders) {
    String type;
    if (primitiveNames.contains(typeName))
      return primitiveTypes.get(primitiveNames.indexOf(typeName)); 
    if (typeName.contains("[")) {
      int i = typeName.indexOf("[");
      type = typeName.substring(0, i);
      String array = typeName.substring(i).replace("]", "");
      if (primitiveNames.contains(type)) {
        type = primitiveDescriptors.get(primitiveNames.indexOf(type));
      } else {
        type = "L" + type + ";";
      } 
      type = array + type;
    } else {
      type = typeName;
    } 
    for (ClassLoader classLoader : ClasspathHelper.classLoaders(loaders)) {
      if (type.contains("["))
        try {
          return Class.forName(type, false, classLoader);
        } catch (Throwable throwable) {} 
      try {
        return classLoader.loadClass(type);
      } catch (Throwable throwable) {}
    } 
    return null;
  }
  
  Member forMember(String descriptor, ClassLoader... loaders) throws ReflectionsException {
    Class<?> aClass;
    int p0 = descriptor.lastIndexOf('(');
    String memberKey = (p0 != -1) ? descriptor.substring(0, p0) : descriptor;
    String methodParameters = (p0 != -1) ? descriptor.substring(p0 + 1, descriptor.lastIndexOf(')')) : "";
    int p1 = memberKey.lastIndexOf('.');
    String className = memberKey.substring(0, p1);
    String memberName = memberKey.substring(p1 + 1);
    Class<?>[] parameterTypes = null;
    if (!methodParameters.isEmpty()) {
      String[] parameterNames = methodParameters.split(",");
      parameterTypes = (Class[])Arrays.<String>stream(parameterNames).map(name -> forClass(name.trim(), loaders)).toArray(x$0 -> new Class[x$0]);
    } 
    try {
      aClass = forClass(className, loaders);
    } catch (Exception e) {
      return null;
    } 
    while (aClass != null) {
      try {
        if (!descriptor.contains("("))
          return aClass.isInterface() ? aClass.getField(memberName) : aClass.getDeclaredField(memberName); 
        if (descriptor.contains("init>"))
          return aClass.isInterface() ? aClass.getConstructor(parameterTypes) : aClass.getDeclaredConstructor(parameterTypes); 
        return aClass.isInterface() ? aClass.getMethod(memberName, parameterTypes) : aClass.getDeclaredMethod(memberName, parameterTypes);
      } catch (Exception e) {
        aClass = aClass.getSuperclass();
      } 
    } 
    return null;
  }
  
  @Nullable
  default <T extends AnnotatedElement> T forElement(String descriptor, Class<T> resultType, ClassLoader[] loaders) {
    Member member = forMember(descriptor, loaders);
    return (member != null && member.getClass().equals(resultType)) ? (T)member : null;
  }
  
  @Nullable
  Method forMethod(String descriptor, ClassLoader... loaders) throws ReflectionsException {
    return forElement(descriptor, Method.class, loaders);
  }
  
  Constructor<?> forConstructor(String descriptor, ClassLoader... loaders) throws ReflectionsException {
    return forElement(descriptor, Constructor.class, loaders);
  }
  
  @Nullable
  Field forField(String descriptor, ClassLoader... loaders) {
    return forElement(descriptor, Field.class, loaders);
  }
  
  <T> Collection<T> forNames(Collection<String> names, Class<T> resultType, ClassLoader... loaders) {
    return (Collection<T>)names.stream().map(name -> forName(name, resultType, loaders)).filter(Objects::nonNull).collect(Collectors.toCollection(java.util.LinkedHashSet::new));
  }
  
  Collection<Class<?>> forNames(Collection<String> names, ClassLoader... loaders) {
    return forNames(names, Class.class, loaders);
  }
}
