package me.syncwrld.booter.libs.reflections.scanners;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.syncwrld.booter.libs.javassist.bytecode.ClassFile;
import me.syncwrld.booter.libs.javax.annotation.Nullable;
import me.syncwrld.booter.libs.reflections.vfs.Vfs;

public interface Scanner {
  @Nullable
  default List<Map.Entry<String, String>> scan(Vfs.File file) {
    return null;
  }
  
  default String index() {
    return getClass().getSimpleName();
  }
  
  default boolean acceptsInput(String file) {
    return file.endsWith(".class");
  }
  
  default Map.Entry<String, String> entry(String key, String value) {
    return new AbstractMap.SimpleEntry<>(key, value);
  }
  
  default List<Map.Entry<String, String>> entries(Collection<String> keys, String value) {
    return (List<Map.Entry<String, String>>)keys.stream().map(key -> entry(key, value)).collect(Collectors.toList());
  }
  
  default List<Map.Entry<String, String>> entries(String key, String value) {
    return Collections.singletonList(entry(key, value));
  }
  
  default List<Map.Entry<String, String>> entries(String key, Collection<String> values) {
    return (List<Map.Entry<String, String>>)values.stream().map(value -> entry(key, value)).collect(Collectors.toList());
  }
  
  List<Map.Entry<String, String>> scan(ClassFile paramClassFile);
}
