package me.syncwrld.booter.libs.javassist.tools.reflect;

import me.syncwrld.booter.libs.javassist.CannotCompileException;
import me.syncwrld.booter.libs.javassist.ClassPool;
import me.syncwrld.booter.libs.javassist.Loader;
import me.syncwrld.booter.libs.javassist.NotFoundException;

public class Loader extends Loader {
  protected Reflection reflection;
  
  public static void main(String[] args) throws Throwable {
    Loader cl = new Loader();
    cl.run(args);
  }
  
  public Loader() throws CannotCompileException, NotFoundException {
    delegateLoadingOf("me.syncwrld.booter.libs.javassist.tools.reflect.Loader");
    this.reflection = new Reflection();
    ClassPool pool = ClassPool.getDefault();
    addTranslator(pool, this.reflection);
  }
  
  public boolean makeReflective(String clazz, String metaobject, String metaclass) throws CannotCompileException, NotFoundException {
    return this.reflection.makeReflective(clazz, metaobject, metaclass);
  }
}
