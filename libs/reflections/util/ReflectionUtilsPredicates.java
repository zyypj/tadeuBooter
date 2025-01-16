package me.syncwrld.booter.libs.reflections.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import me.syncwrld.booter.libs.reflections.ReflectionUtils;

public class ReflectionUtilsPredicates {
  public static <T extends Member> Predicate<T> withName(String name) {
    return input -> (input != null && input.getName().equals(name));
  }
  
  public static <T extends Member> Predicate<T> withPrefix(String prefix) {
    return input -> (input != null && input.getName().startsWith(prefix));
  }
  
  public static <T> Predicate<T> withNamePrefix(String prefix) {
    return input -> toName(input).startsWith(prefix);
  }
  
  public static <T extends AnnotatedElement> Predicate<T> withPattern(String regex) {
    return input -> Pattern.matches(regex, input.toString());
  }
  
  public static <T extends AnnotatedElement> Predicate<T> withAnnotation(Class<? extends Annotation> annotation) {
    return input -> (input != null && input.isAnnotationPresent(annotation));
  }
  
  public static <T extends AnnotatedElement> Predicate<T> withAnnotations(Class<? extends Annotation>... annotations) {
    return input -> (input != null && Arrays.equals((Object[])annotations, (Object[])annotationTypes(input.getAnnotations())));
  }
  
  public static <T extends AnnotatedElement> Predicate<T> withAnnotation(Annotation annotation) {
    return input -> (input != null && input.isAnnotationPresent(annotation.annotationType()) && areAnnotationMembersMatching(input.getAnnotation((Class)annotation.annotationType()), annotation));
  }
  
  public static <T extends AnnotatedElement> Predicate<T> withAnnotations(Annotation... annotations) {
    return input -> {
        if (input != null) {
          Annotation[] inputAnnotations = input.getAnnotations();
          if (inputAnnotations.length == annotations.length)
            return IntStream.range(0, inputAnnotations.length).allMatch(()); 
        } 
        return true;
      };
  }
  
  public static Predicate<Member> withParameters(Class<?>... types) {
    return input -> Arrays.equals((Object[])parameterTypes(input), (Object[])types);
  }
  
  public static Predicate<Member> withParametersAssignableTo(Class... types) {
    return input -> isAssignable(types, parameterTypes(input));
  }
  
  public static Predicate<Member> withParametersAssignableFrom(Class... types) {
    return input -> isAssignable(parameterTypes(input), types);
  }
  
  public static Predicate<Member> withParametersCount(int count) {
    return input -> (input != null && (parameterTypes(input)).length == count);
  }
  
  public static Predicate<Member> withAnyParameterAnnotation(Class<? extends Annotation> annotationClass) {
    return input -> (input != null && annotationTypes(parameterAnnotations(input)).stream().anyMatch(()));
  }
  
  public static Predicate<Member> withAnyParameterAnnotation(Annotation annotation) {
    return input -> (input != null && parameterAnnotations(input).stream().anyMatch(()));
  }
  
  public static <T> Predicate<Field> withType(Class<T> type) {
    return input -> (input != null && input.getType().equals(type));
  }
  
  public static <T> Predicate<Field> withTypeAssignableTo(Class<T> type) {
    return input -> (input != null && type.isAssignableFrom(input.getType()));
  }
  
  public static <T> Predicate<Method> withReturnType(Class<T> type) {
    return input -> (input != null && input.getReturnType().equals(type));
  }
  
  public static <T> Predicate<Method> withReturnTypeAssignableFrom(Class<T> type) {
    return input -> (input != null && type.isAssignableFrom(input.getReturnType()));
  }
  
  public static <T extends Member> Predicate<T> withModifier(int mod) {
    return input -> (input != null && (input.getModifiers() & mod) != 0);
  }
  
  public static <T extends Member> Predicate<T> withPublic() {
    return withModifier(1);
  }
  
  public static <T extends Member> Predicate<T> withStatic() {
    return withModifier(8);
  }
  
  public static <T extends Member> Predicate<T> withInterface() {
    return withModifier(512);
  }
  
  public static Predicate<Class<?>> withClassModifier(int mod) {
    return input -> (input != null && (input.getModifiers() & mod) != 0);
  }
  
  public static boolean isAssignable(Class[] childClasses, Class[] parentClasses) {
    if (childClasses == null || childClasses.length == 0)
      return (parentClasses == null || parentClasses.length == 0); 
    if (childClasses.length != parentClasses.length)
      return false; 
    return IntStream.range(0, childClasses.length)
      .noneMatch(i -> (!parentClasses[i].isAssignableFrom(childClasses[i]) || (parentClasses[i] == Object.class && childClasses[i] != Object.class)));
  }
  
  private static String toName(Object input) {
    return (input == null) ? "" : (
      input.getClass().equals(Class.class) ? ((Class)input).getName() : ((input instanceof Member) ? ((Member)input)
      .getName() : ((input instanceof Annotation) ? ((Annotation)input)
      .annotationType().getName() : input
      .toString())));
  }
  
  private static Class[] parameterTypes(Member member) {
    return (member != null) ? (
      (member.getClass() == Method.class) ? ((Method)member).getParameterTypes() : (
      (member.getClass() == Constructor.class) ? ((Constructor)member).getParameterTypes() : null)) : null;
  }
  
  private static Set<Annotation> parameterAnnotations(Member member) {
    Annotation[][] annotations = (member instanceof Method) ? ((Method)member).getParameterAnnotations() : ((member instanceof Constructor) ? ((Constructor)member).getParameterAnnotations() : (Annotation[][])null);
    return (Set<Annotation>)Arrays.<Annotation[]>stream((annotations != null) ? annotations : new Annotation[0][]).flatMap(Arrays::stream).collect(Collectors.toSet());
  }
  
  private static Set<Class<? extends Annotation>> annotationTypes(Collection<Annotation> annotations) {
    return (Set<Class<? extends Annotation>>)annotations.stream().map(Annotation::annotationType).collect(Collectors.toSet());
  }
  
  private static Class<? extends Annotation>[] annotationTypes(Annotation[] annotations) {
    return (Class<? extends Annotation>[])Arrays.<Annotation>stream(annotations).map(Annotation::annotationType).toArray(x$0 -> new Class[x$0]);
  }
  
  private static boolean areAnnotationMembersMatching(Annotation annotation1, Annotation annotation2) {
    if (annotation2 != null && annotation1.annotationType() == annotation2.annotationType()) {
      for (Method method : annotation1.annotationType().getDeclaredMethods()) {
        if (!ReflectionUtils.invoke(method, annotation1, new Object[0]).equals(ReflectionUtils.invoke(method, annotation2, new Object[0])))
          return false; 
      } 
      return true;
    } 
    return false;
  }
}
