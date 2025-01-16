package me.syncwrld.booter.libs.reflections;

import java.net.URL;
import java.util.Set;
import java.util.function.Predicate;
import me.syncwrld.booter.libs.reflections.scanners.Scanner;

public interface Configuration {
  Set<Scanner> getScanners();
  
  Set<URL> getUrls();
  
  Predicate<String> getInputsFilter();
  
  boolean isParallel();
  
  ClassLoader[] getClassLoaders();
  
  boolean shouldExpandSuperTypes();
}
