package me.syncwrld.booter.libs.javassist;

public interface Translator {
  void start(ClassPool paramClassPool) throws NotFoundException, CannotCompileException;
  
  void onLoad(ClassPool paramClassPool, String paramString) throws NotFoundException, CannotCompileException;
}
