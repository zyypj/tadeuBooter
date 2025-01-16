package me.syncwrld.booter.libs.reflections.util;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.reflections.Store;

public interface QueryFunction<C, T> extends Function<C, Set<T>>, NameHelper {
  static <C, T> QueryFunction<Store, T> empty() {
    return ctx -> Collections.emptySet();
  }
  
  static <C, T> QueryFunction<Store, T> single(T element) {
    return ctx -> Collections.singleton(element);
  }
  
  static <C, T> QueryFunction<Store, T> set(Collection<T> elements) {
    return ctx -> new LinkedHashSet(elements);
  }
  
  default QueryFunction<C, T> filter(Predicate<? super T> predicate) {
    return ctx -> (LinkedHashSet)apply((C)ctx).stream().filter(predicate).collect(Collectors.toCollection(LinkedHashSet::new));
  }
  
  default <R> QueryFunction<C, R> map(Function<? super T, ? extends R> function) {
    return ctx -> (LinkedHashSet)apply((C)ctx).stream().map(function).collect(Collectors.toCollection(LinkedHashSet::new));
  }
  
  default <R> QueryFunction<C, R> flatMap(Function<T, ? extends Function<C, Set<R>>> function) {
    return ctx -> (LinkedHashSet)apply((C)ctx).stream().flatMap(()).collect(Collectors.toCollection(LinkedHashSet::new));
  }
  
  default QueryFunction<C, T> getAll(Function<T, QueryFunction<C, T>> builder) {
    return getAll(builder, t -> t);
  }
  
  default <R> QueryFunction<C, R> getAll(Function<T, QueryFunction<C, R>> builder, Function<R, T> traverse) {
    return ctx -> {
        List<T> workKeys = new ArrayList<>(apply((C)ctx));
        Set<R> result = new LinkedHashSet<>();
        for (int i = 0; i < workKeys.size(); i++) {
          T key = workKeys.get(i);
          Set<R> apply = ((QueryFunction<Object, R>)builder.apply(key)).apply(ctx);
          for (R r : apply) {
            if (result.add(r))
              workKeys.add(traverse.apply(r)); 
          } 
        } 
        return result;
      };
  }
  
  default <R> QueryFunction<C, T> add(QueryFunction<C, T> function) {
    return ctx -> (LinkedHashSet)Stream.<Set>of(new Set[] { apply((C)ctx), function.apply(ctx) }).flatMap(Collection::stream).collect(Collectors.toCollection(LinkedHashSet::new));
  }
  
  <R> QueryFunction<C, R> as(Class<? extends R> type, ClassLoader... loaders) {
    return ctx -> {
        Set<T> apply = apply((C)ctx);
        return apply.stream().findFirst().map(()).orElse(apply);
      };
  }
  
  <R> QueryFunction<C, Class<?>> asClass(ClassLoader... loaders) {
    return ctx -> (Set)forNames((Collection)apply((C)ctx), Class.class, loaders);
  }
  
  default QueryFunction<C, String> asString() {
    return ctx -> new LinkedHashSet<>(toNames(new AnnotatedElement[] { (AnnotatedElement)apply((C)ctx) }));
  }
  
  default <R> QueryFunction<C, Class<? extends R>> as() {
    return ctx -> new LinkedHashSet<>(apply((C)ctx));
  }
  
  Set<T> apply(C paramC);
}
