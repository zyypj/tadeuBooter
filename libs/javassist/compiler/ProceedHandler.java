package me.syncwrld.booter.libs.javassist.compiler;

import me.syncwrld.booter.libs.javassist.bytecode.Bytecode;
import me.syncwrld.booter.libs.javassist.compiler.ast.ASTList;

public interface ProceedHandler {
  void doit(JvstCodeGen paramJvstCodeGen, Bytecode paramBytecode, ASTList paramASTList) throws CompileError;
  
  void setReturnType(JvstTypeChecker paramJvstTypeChecker, ASTList paramASTList) throws CompileError;
}
