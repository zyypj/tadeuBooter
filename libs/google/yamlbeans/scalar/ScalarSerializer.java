package me.syncwrld.booter.libs.google.yamlbeans.scalar;

import me.syncwrld.booter.libs.google.yamlbeans.YamlException;

public interface ScalarSerializer<T> {
  String write(T paramT) throws YamlException;
  
  T read(String paramString) throws YamlException;
}
