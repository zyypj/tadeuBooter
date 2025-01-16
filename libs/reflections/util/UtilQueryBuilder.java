package me.syncwrld.booter.libs.reflections.util;

import java.lang.reflect.AnnotatedElement;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.reflections.ReflectionUtils;
import me.syncwrld.booter.libs.reflections.Store;

public interface UtilQueryBuilder<F, E> {
  QueryFunction<Store, E> get(F paramF);
  
  default QueryFunction<Store, E> of(F element) {
    return of(ReflectionUtils.extendType().get((AnnotatedElement)element));
  }
  
  default QueryFunction<Store, E> of(F element, Predicate<? super E> predicate) {
    return of(element).filter(predicate);
  }
  
  default <T> QueryFunction<Store, E> of(QueryFunction<Store, T> function) {
    return store -> (LinkedHashSet)function.apply(store).stream().flatMap(()).collect(Collectors.toCollection(LinkedHashSet::new));
  }
}
