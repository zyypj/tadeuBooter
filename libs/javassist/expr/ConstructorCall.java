package me.syncwrld.booter.libs.javassist.expr;

import me.syncwrld.booter.libs.javassist.CtClass;
import me.syncwrld.booter.libs.javassist.CtConstructor;
import me.syncwrld.booter.libs.javassist.CtMethod;
import me.syncwrld.booter.libs.javassist.NotFoundException;
import me.syncwrld.booter.libs.javassist.bytecode.CodeIterator;
import me.syncwrld.booter.libs.javassist.bytecode.MethodInfo;

public class ConstructorCall extends MethodCall {
  protected ConstructorCall(int pos, CodeIterator i, CtClass decl, MethodInfo m) {
    super(pos, i, decl, m);
  }
  
  public String getMethodName() {
    return isSuper() ? "super" : "this";
  }
  
  public CtMethod getMethod() throws NotFoundException {
    throw new NotFoundException("this is a constructor call.  Call getConstructor().");
  }
  
  public CtConstructor getConstructor() throws NotFoundException {
    return getCtClass().getConstructor(getSignature());
  }
  
  public boolean isSuper() {
    return super.isSuper();
  }
}
