package me.syncwrld.booter.libs.reflections.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.javassist.bytecode.AccessFlag;
import me.syncwrld.booter.libs.javassist.bytecode.AnnotationsAttribute;
import me.syncwrld.booter.libs.javassist.bytecode.AttributeInfo;
import me.syncwrld.booter.libs.javassist.bytecode.ClassFile;
import me.syncwrld.booter.libs.javassist.bytecode.Descriptor;
import me.syncwrld.booter.libs.javassist.bytecode.FieldInfo;
import me.syncwrld.booter.libs.javassist.bytecode.MethodInfo;
import me.syncwrld.booter.libs.javassist.bytecode.ParameterAnnotationsAttribute;
import me.syncwrld.booter.libs.javassist.bytecode.annotation.Annotation;

public class JavassistHelper {
  public static boolean includeInvisibleTag = true;
  
  public static String fieldName(ClassFile classFile, FieldInfo object) {
    return String.format("%s.%s", new Object[] { classFile.getName(), object.getName() });
  }
  
  public static String methodName(ClassFile classFile, MethodInfo object) {
    return String.format("%s.%s(%s)", new Object[] { classFile.getName(), object.getName(), String.join(", ", (Iterable)getParameters(object)) });
  }
  
  public static boolean isPublic(Object object) {
    if (object instanceof ClassFile)
      return AccessFlag.isPublic(((ClassFile)object).getAccessFlags()); 
    if (object instanceof FieldInfo)
      return AccessFlag.isPublic(((FieldInfo)object).getAccessFlags()); 
    if (object instanceof MethodInfo)
      return AccessFlag.isPublic(((MethodInfo)object).getAccessFlags()); 
    return false;
  }
  
  public static Stream<MethodInfo> getMethods(ClassFile classFile) {
    return classFile.getMethods().stream().filter(MethodInfo::isMethod);
  }
  
  public static Stream<MethodInfo> getConstructors(ClassFile classFile) {
    return classFile.getMethods().stream().filter(methodInfo -> !methodInfo.isMethod());
  }
  
  public static List<String> getParameters(MethodInfo method) {
    List<String> result = new ArrayList<>();
    String descriptor = method.getDescriptor().substring(1);
    Descriptor.Iterator iterator = new Descriptor.Iterator(descriptor);
    Integer prev = null;
    while (iterator.hasNext()) {
      int cur = iterator.next();
      if (prev != null)
        result.add(Descriptor.toString(descriptor.substring(prev.intValue(), cur))); 
      prev = Integer.valueOf(cur);
    } 
    return result;
  }
  
  public static String getReturnType(MethodInfo method) {
    String descriptor = method.getDescriptor();
    descriptor = descriptor.substring(descriptor.lastIndexOf(")") + 1);
    return Descriptor.toString(descriptor);
  }
  
  public static List<String> getAnnotations(Function<String, AttributeInfo> function) {
    Function<String, List<String>> names = function.andThen(attribute -> (attribute != null) ? ((AnnotationsAttribute)attribute).getAnnotations() : null).andThen(JavassistHelper::annotationNames);
    List<String> result = new ArrayList<>(names.apply("RuntimeVisibleAnnotations"));
    if (includeInvisibleTag)
      result.addAll(names.apply("RuntimeInvisibleAnnotations")); 
    return result;
  }
  
  public static List<List<String>> getParametersAnnotations(MethodInfo method) {
    Function<String, List<List<String>>> names = method::getAttribute.andThen(attribute -> (attribute != null) ? ((ParameterAnnotationsAttribute)attribute).getAnnotations() : (Annotation[][])null).andThen(aa -> (aa != null) ? (List)Stream.<Annotation[]>of(aa).map(JavassistHelper::annotationNames).collect(Collectors.toList()) : Collections.emptyList());
    List<List<String>> visibleAnnotations = names.apply("RuntimeVisibleParameterAnnotations");
    if (!includeInvisibleTag)
      return new ArrayList<>(visibleAnnotations); 
    List<List<String>> invisibleAnnotations = names.apply("RuntimeInvisibleParameterAnnotations");
    if (invisibleAnnotations.isEmpty())
      return new ArrayList<>(visibleAnnotations); 
    List<List<String>> result = new ArrayList<>();
    for (int i = 0; i < Math.max(visibleAnnotations.size(), invisibleAnnotations.size()); i++) {
      List<String> concat = new ArrayList<>();
      if (i < visibleAnnotations.size())
        concat.addAll(visibleAnnotations.get(i)); 
      if (i < invisibleAnnotations.size())
        concat.addAll(invisibleAnnotations.get(i)); 
      result.add(concat);
    } 
    return result;
  }
  
  private static List<String> annotationNames(Annotation[] annotations) {
    return (annotations != null) ? (List<String>)Stream.<Annotation>of(annotations).map(Annotation::getTypeName).collect(Collectors.toList()) : Collections.<String>emptyList();
  }
}
