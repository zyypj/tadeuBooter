package me.syncwrld.booter.libs.javassist.bytecode;

import me.syncwrld.booter.libs.javassist.CannotCompileException;

public class DuplicateMemberException extends CannotCompileException {
  private static final long serialVersionUID = 1L;
  
  public DuplicateMemberException(String msg) {
    super(msg);
  }
}
