package me.syncwrld.booter.libs.javassist.runtime;

public class DotClass {
  public static NoClassDefFoundError fail(ClassNotFoundException e) {
    return new NoClassDefFoundError(e.getMessage());
  }
}
