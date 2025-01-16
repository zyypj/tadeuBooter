package me.syncwrld.booter.libs.javassist.expr;

import me.syncwrld.booter.libs.javassist.CannotCompileException;
import me.syncwrld.booter.libs.javassist.CtBehavior;
import me.syncwrld.booter.libs.javassist.CtClass;
import me.syncwrld.booter.libs.javassist.NotFoundException;
import me.syncwrld.booter.libs.javassist.bytecode.Bytecode;
import me.syncwrld.booter.libs.javassist.bytecode.CodeAttribute;
import me.syncwrld.booter.libs.javassist.bytecode.CodeIterator;
import me.syncwrld.booter.libs.javassist.bytecode.ConstPool;
import me.syncwrld.booter.libs.javassist.bytecode.ExceptionTable;
import me.syncwrld.booter.libs.javassist.bytecode.MethodInfo;
import me.syncwrld.booter.libs.javassist.compiler.CompileError;
import me.syncwrld.booter.libs.javassist.compiler.Javac;

public class Handler extends Expr {
  private static String EXCEPTION_NAME = "$1";
  
  private ExceptionTable etable;
  
  private int index;
  
  protected Handler(ExceptionTable et, int nth, CodeIterator it, CtClass declaring, MethodInfo m) {
    super(et.handlerPc(nth), it, declaring, m);
    this.etable = et;
    this.index = nth;
  }
  
  public CtBehavior where() {
    return super.where();
  }
  
  public int getLineNumber() {
    return super.getLineNumber();
  }
  
  public String getFileName() {
    return super.getFileName();
  }
  
  public CtClass[] mayThrow() {
    return super.mayThrow();
  }
  
  public CtClass getType() throws NotFoundException {
    int type = this.etable.catchType(this.index);
    if (type == 0)
      return null; 
    ConstPool cp = getConstPool();
    String name = cp.getClassInfo(type);
    return this.thisClass.getClassPool().getCtClass(name);
  }
  
  public boolean isFinally() {
    return (this.etable.catchType(this.index) == 0);
  }
  
  public void replace(String statement) throws CannotCompileException {
    throw new RuntimeException("not implemented yet");
  }
  
  public void insertBefore(String src) throws CannotCompileException {
    this.edited = true;
    ConstPool cp = getConstPool();
    CodeAttribute ca = this.iterator.get();
    Javac jv = new Javac(this.thisClass);
    Bytecode b = jv.getBytecode();
    b.setStackDepth(1);
    b.setMaxLocals(ca.getMaxLocals());
    try {
      CtClass type = getType();
      int var = jv.recordVariable(type, EXCEPTION_NAME);
      jv.recordReturnType(type, false);
      b.addAstore(var);
      jv.compileStmnt(src);
      b.addAload(var);
      int oldHandler = this.etable.handlerPc(this.index);
      b.addOpcode(167);
      b.addIndex(oldHandler - this.iterator.getCodeLength() - b
          .currentPc() + 1);
      this.maxStack = b.getMaxStack();
      this.maxLocals = b.getMaxLocals();
      int pos = this.iterator.append(b.get());
      this.iterator.append(b.getExceptionTable(), pos);
      this.etable.setHandlerPc(this.index, pos);
    } catch (NotFoundException e) {
      throw new CannotCompileException(e);
    } catch (CompileError e) {
      throw new CannotCompileException(e);
    } 
  }
}
