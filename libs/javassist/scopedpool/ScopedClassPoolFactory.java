package me.syncwrld.booter.libs.javassist.scopedpool;

import me.syncwrld.booter.libs.javassist.ClassPool;

public interface ScopedClassPoolFactory {
  ScopedClassPool create(ClassLoader paramClassLoader, ClassPool paramClassPool, ScopedClassPoolRepository paramScopedClassPoolRepository);
  
  ScopedClassPool create(ClassPool paramClassPool, ScopedClassPoolRepository paramScopedClassPoolRepository);
}
