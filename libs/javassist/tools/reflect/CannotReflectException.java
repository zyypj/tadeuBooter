package me.syncwrld.booter.libs.javassist.tools.reflect;

import me.syncwrld.booter.libs.javassist.CannotCompileException;

public class CannotReflectException extends CannotCompileException {
  private static final long serialVersionUID = 1L;
  
  public CannotReflectException(String msg) {
    super(msg);
  }
}
