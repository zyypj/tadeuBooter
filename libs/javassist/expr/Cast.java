package me.syncwrld.booter.libs.javassist.expr;

import me.syncwrld.booter.libs.javassist.CannotCompileException;
import me.syncwrld.booter.libs.javassist.ClassPool;
import me.syncwrld.booter.libs.javassist.CtBehavior;
import me.syncwrld.booter.libs.javassist.CtClass;
import me.syncwrld.booter.libs.javassist.NotFoundException;
import me.syncwrld.booter.libs.javassist.bytecode.BadBytecode;
import me.syncwrld.booter.libs.javassist.bytecode.Bytecode;
import me.syncwrld.booter.libs.javassist.bytecode.CodeAttribute;
import me.syncwrld.booter.libs.javassist.bytecode.CodeIterator;
import me.syncwrld.booter.libs.javassist.bytecode.ConstPool;
import me.syncwrld.booter.libs.javassist.bytecode.MethodInfo;
import me.syncwrld.booter.libs.javassist.compiler.CompileError;
import me.syncwrld.booter.libs.javassist.compiler.Javac;
import me.syncwrld.booter.libs.javassist.compiler.JvstCodeGen;
import me.syncwrld.booter.libs.javassist.compiler.JvstTypeChecker;
import me.syncwrld.booter.libs.javassist.compiler.ProceedHandler;
import me.syncwrld.booter.libs.javassist.compiler.ast.ASTList;

public class Cast extends Expr {
  protected Cast(int pos, CodeIterator i, CtClass declaring, MethodInfo m) {
    super(pos, i, declaring, m);
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
  
  public CtClass getType() throws NotFoundException {
    ConstPool cp = getConstPool();
    int pos = this.currentPos;
    int index = this.iterator.u16bitAt(pos + 1);
    String name = cp.getClassInfo(index);
    return this.thisClass.getClassPool().getCtClass(name);
  }
  
  public CtClass[] mayThrow() {
    return super.mayThrow();
  }
  
  public void replace(String statement) throws CannotCompileException {
    this.thisClass.getClassFile();
    ConstPool constPool = getConstPool();
    int pos = this.currentPos;
    int index = this.iterator.u16bitAt(pos + 1);
    Javac jc = new Javac(this.thisClass);
    ClassPool cp = this.thisClass.getClassPool();
    CodeAttribute ca = this.iterator.get();
    try {
      CtClass[] params = { cp.get("java.lang.Object") };
      CtClass retType = getType();
      int paramVar = ca.getMaxLocals();
      jc.recordParams("java.lang.Object", params, true, paramVar, 
          withinStatic());
      int retVar = jc.recordReturnType(retType, true);
      jc.recordProceed(new ProceedForCast(index, retType));
      checkResultValue(retType, statement);
      Bytecode bytecode = jc.getBytecode();
      storeStack(params, true, paramVar, bytecode);
      jc.recordLocalVariables(ca, pos);
      bytecode.addConstZero(retType);
      bytecode.addStore(retVar, retType);
      jc.compileStmnt(statement);
      bytecode.addLoad(retVar, retType);
      replace0(pos, bytecode, 3);
    } catch (CompileError e) {
      throw new CannotCompileException(e);
    } catch (NotFoundException e) {
      throw new CannotCompileException(e);
    } catch (BadBytecode e) {
      throw new CannotCompileException("broken method");
    } 
  }
  
  static class ProceedForCast implements ProceedHandler {
    int index;
    
    CtClass retType;
    
    ProceedForCast(int i, CtClass t) {
      this.index = i;
      this.retType = t;
    }
    
    public void doit(JvstCodeGen gen, Bytecode bytecode, ASTList args) throws CompileError {
      if (gen.getMethodArgsLength(args) != 1)
        throw new CompileError("$proceed() cannot take more than one parameter for cast"); 
      gen.atMethodArgs(args, new int[1], new int[1], new String[1]);
      bytecode.addOpcode(192);
      bytecode.addIndex(this.index);
      gen.setType(this.retType);
    }
    
    public void setReturnType(JvstTypeChecker c, ASTList args) throws CompileError {
      c.atMethodArgs(args, new int[1], new int[1], new String[1]);
      c.setType(this.retType);
    }
  }
}
