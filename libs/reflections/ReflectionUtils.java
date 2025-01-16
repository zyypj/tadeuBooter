package me.syncwrld.booter.libs.reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.reflections.util.ClasspathHelper;
import me.syncwrld.booter.libs.reflections.util.QueryFunction;
import me.syncwrld.booter.libs.reflections.util.ReflectionUtilsPredicates;
import me.syncwrld.booter.libs.reflections.util.UtilQueryBuilder;

public abstract class ReflectionUtils extends ReflectionUtilsPredicates {
  public static <C, T> Set<T> get(QueryFunction<C, T> function) {
    return function.apply(null);
  }
  
  public static <T> Set<T> get(QueryFunction<Store, T> queryFunction, Predicate<? super T>... predicates) {
    return get(queryFunction.filter(Arrays.<Predicate>stream((Predicate[])predicates).reduce(t -> true, Predicate::and)));
  }
  
  private static final List<String> objectMethodNames = Arrays.asList(new String[] { "equals", "hashCode", "toString", "wait", "notify", "notifyAll" });
  
  public static final Predicate<Method> notObjectMethod;
  
  static {
    notObjectMethod = (m -> !objectMethodNames.contains(m.getName()));
  }
  
  public static final UtilQueryBuilder<Class<?>, Class<?>> SuperClass = element -> ();
  
  public static final UtilQueryBuilder<Class<?>, Class<?>> Interfaces = element -> ();
  
  public static final UtilQueryBuilder<Class<?>, Class<?>> SuperTypes = new UtilQueryBuilder<Class<?>, Class<?>>() {
      public QueryFunction<Store, Class<?>> get(Class<?> element) {
        return ReflectionUtils.SuperClass.get(element).add(ReflectionUtils.Interfaces.get(element));
      }
      
      public QueryFunction<Store, Class<?>> of(Class<?> element) {
        return QueryFunction.single(element).getAll(ReflectionUtils.SuperTypes::get);
      }
    };
  
  public static final UtilQueryBuilder<AnnotatedElement, Annotation> Annotations = new UtilQueryBuilder<AnnotatedElement, Annotation>() {
      public QueryFunction<Store, Annotation> get(AnnotatedElement element) {
        return ctx -> (LinkedHashSet)Arrays.<Annotation>stream(element.getAnnotations()).collect(Collectors.toCollection(LinkedHashSet::new));
      }
      
      public QueryFunction<Store, Annotation> of(AnnotatedElement element) {
        return ReflectionUtils.<AnnotatedElement>extendType().get(element).getAll(ReflectionUtils.Annotations::get, Annotation::annotationType);
      }
    };
  
  public static final UtilQueryBuilder<AnnotatedElement, Class<? extends Annotation>> AnnotationTypes = new UtilQueryBuilder<AnnotatedElement, Class<? extends Annotation>>() {
      public QueryFunction<Store, Class<? extends Annotation>> get(AnnotatedElement element) {
        return ReflectionUtils.Annotations.get(element).map(Annotation::annotationType);
      }
      
      public QueryFunction<Store, Class<? extends Annotation>> of(AnnotatedElement element) {
        return ReflectionUtils.<AnnotatedElement>extendType().get(element).getAll(ReflectionUtils.AnnotationTypes::get, a -> a);
      }
    };
  
  public static final UtilQueryBuilder<Class<?>, Method> Methods = element -> ();
  
  public static final UtilQueryBuilder<Class<?>, Constructor> Constructors = element -> ();
  
  public static final UtilQueryBuilder<Class<?>, Field> Fields = element -> ();
  
  public static final UtilQueryBuilder<String, URL> Resources = element -> ();
  
  public static <T extends AnnotatedElement> UtilQueryBuilder<AnnotatedElement, T> extendType() {
    return element -> {
        if (element instanceof Class && !((Class)element).isAnnotation()) {
          QueryFunction<Store, Class<?>> single = QueryFunction.single(element);
          return single.add(single.getAll(SuperTypes::get));
        } 
        return QueryFunction.single(element);
      };
  }
  
  public static <T extends AnnotatedElement> Set<Annotation> getAllAnnotations(T type, Predicate<Annotation>... predicates) {
    return get(Annotations.of(type), (Predicate<? super Annotation>[])predicates);
  }
  
  public static Set<Class<?>> getAllSuperTypes(Class<?> type, Predicate<? super Class<?>>... predicates) {
    (new Predicate[1])[0] = (t -> !Object.class.equals(t));
    Predicate<? super Class<?>>[] filter = (predicates == null || predicates.length == 0) ? (Predicate<? super Class<?>>[])new Predicate[1] : predicates;
    return get(SuperTypes.of(type), filter);
  }
  
  public static Set<Class<?>> getSuperTypes(Class<?> type) {
    return get(SuperTypes.get(type));
  }
  
  public static Set<Method> getAllMethods(Class<?> type, Predicate<? super Method>... predicates) {
    return get(Methods.of(type), predicates);
  }
  
  public static Set<Method> getMethods(Class<?> t, Predicate<? super Method>... predicates) {
    return get(Methods.get(t), predicates);
  }
  
  public static Set<Constructor> getAllConstructors(Class<?> type, Predicate<? super Constructor>... predicates) {
    return get(Constructors.of(type), predicates);
  }
  
  public static Set<Constructor> getConstructors(Class<?> t, Predicate<? super Constructor>... predicates) {
    return get(Constructors.get(t), predicates);
  }
  
  public static Set<Field> getAllFields(Class<?> type, Predicate<? super Field>... predicates) {
    return get(Fields.of(type), predicates);
  }
  
  public static Set<Field> getFields(Class<?> type, Predicate<? super Field>... predicates) {
    return get(Fields.get(type), predicates);
  }
  
  public static <T extends AnnotatedElement> Set<Annotation> getAnnotations(T type, Predicate<Annotation>... predicates) {
    return get(Annotations.get(type), (Predicate<? super Annotation>[])predicates);
  }
  
  public static Map<String, Object> toMap(Annotation annotation) {
    return (Map<String, Object>)get(Methods.of(annotation.annotationType())
        .filter(notObjectMethod.and(withParametersCount(0))))
      .stream()
      .collect(Collectors.toMap(Method::getName, m -> {
            Object v1 = invoke(m, annotation, new Object[0]);
            return (v1.getClass().isArray() && v1.getClass().getComponentType().isAnnotation()) ? Stream.<Annotation>of((Annotation[])v1).map(ReflectionUtils::toMap).collect(Collectors.toList()) : v1;
          }));
  }
  
  public static Map<String, Object> toMap(Annotation annotation, AnnotatedElement element) {
    Map<String, Object> map = toMap(annotation);
    if (element != null)
      map.put("annotatedElement", element); 
    return map;
  }
  
  public static Annotation toAnnotation(Map<String, Object> map) {
    return toAnnotation(map, (Class<Annotation>)map.get("annotationType"));
  }
  
  public static <T extends Annotation> T toAnnotation(Map<String, Object> map, Class<T> annotationType) {
    return (T)Proxy.newProxyInstance(annotationType.getClassLoader(), new Class[] { annotationType }, (proxy, method, args) -> notObjectMethod.test(method) ? map.get(method.getName()) : method.invoke(map, new Object[0]));
  }
  
  public static Object invoke(Method method, Object obj, Object... args) {
    try {
      return method.invoke(obj, args);
    } catch (Exception e) {
      return e;
    } 
  }
}
