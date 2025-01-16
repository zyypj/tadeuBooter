package me.syncwrld.booter.libs.reflections.util;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import me.syncwrld.booter.libs.reflections.Store;

public interface QueryBuilder extends NameHelper {
  default String index() {
    return getClass().getSimpleName();
  }
  
  default QueryFunction<Store, String> get(String key) {
    return store -> new LinkedHashSet((Collection)((Map)store.getOrDefault(index(), Collections.emptyMap())).getOrDefault(key, Collections.emptySet()));
  }
  
  default QueryFunction<Store, String> get(AnnotatedElement element) {
    return get(toName(element));
  }
  
  default QueryFunction<Store, String> get(Collection<String> keys) {
    return keys.stream().map(this::get).reduce(QueryFunction::add).get();
  }
  
  default QueryFunction<Store, String> getAll(Collection<String> keys) {
    return QueryFunction.set(keys).getAll(this::get);
  }
  
  default QueryFunction<Store, String> getAllIncluding(String key) {
    return QueryFunction.single(key).add(QueryFunction.single(key).getAll(this::get));
  }
  
  default QueryFunction<Store, String> getAllIncluding(Collection<String> keys) {
    return QueryFunction.set(keys).add(QueryFunction.set(keys).getAll(this::get));
  }
  
  default QueryFunction<Store, String> of(Collection<String> keys) {
    return getAll(keys);
  }
  
  default QueryFunction<Store, String> of(String key) {
    return getAll(Collections.singletonList(key));
  }
  
  QueryFunction<Store, String> of(AnnotatedElement... elements) {
    return getAll(toNames(elements));
  }
  
  default QueryFunction<Store, String> of(Set<? extends AnnotatedElement> elements) {
    return getAll(toNames(elements));
  }
  
  default QueryFunction<Store, String> with(Collection<String> keys) {
    return of(keys);
  }
  
  default QueryFunction<Store, String> with(String key) {
    return of(key);
  }
  
  QueryFunction<Store, String> with(AnnotatedElement... keys) {
    return of(keys);
  }
  
  default QueryFunction<Store, String> with(Set<? extends AnnotatedElement> keys) {
    return of(keys);
  }
  
  default <T> QueryFunction<Store, T> of(QueryFunction queryFunction) {
    return queryFunction.add(queryFunction.getAll(this::get));
  }
}
