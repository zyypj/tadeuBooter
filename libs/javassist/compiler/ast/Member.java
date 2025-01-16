package me.syncwrld.booter.libs.javassist.compiler.ast;

import me.syncwrld.booter.libs.javassist.CtField;
import me.syncwrld.booter.libs.javassist.compiler.CompileError;

public class Member extends Symbol {
  private static final long serialVersionUID = 1L;
  
  private CtField field;
  
  public Member(String name) {
    super(name);
    this.field = null;
  }
  
  public void setField(CtField f) {
    this.field = f;
  }
  
  public CtField getField() {
    return this.field;
  }
  
  public void accept(Visitor v) throws CompileError {
    v.atMember(this);
  }
}
