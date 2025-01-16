package me.syncwrld.booter.libs.javassist.convert;

import me.syncwrld.booter.libs.javassist.CannotCompileException;
import me.syncwrld.booter.libs.javassist.CtClass;
import me.syncwrld.booter.libs.javassist.bytecode.BadBytecode;
import me.syncwrld.booter.libs.javassist.bytecode.CodeAttribute;
import me.syncwrld.booter.libs.javassist.bytecode.CodeIterator;
import me.syncwrld.booter.libs.javassist.bytecode.ConstPool;
import me.syncwrld.booter.libs.javassist.bytecode.MethodInfo;
import me.syncwrld.booter.libs.javassist.bytecode.Opcode;

public abstract class Transformer implements Opcode {
  private Transformer next;
  
  public Transformer(Transformer t) {
    this.next = t;
  }
  
  public Transformer getNext() {
    return this.next;
  }
  
  public void initialize(ConstPool cp, CodeAttribute attr) {}
  
  public void initialize(ConstPool cp, CtClass clazz, MethodInfo minfo) throws CannotCompileException {
    initialize(cp, minfo.getCodeAttribute());
  }
  
  public void clean() {}
  
  public abstract int transform(CtClass paramCtClass, int paramInt, CodeIterator paramCodeIterator, ConstPool paramConstPool) throws CannotCompileException, BadBytecode;
  
  public int extraLocals() {
    return 0;
  }
  
  public int extraStack() {
    return 0;
  }
}
